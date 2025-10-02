package com.uninorte.inventario.ui.ventas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.uninorte.inventario.data.dao.ClienteDao;
import com.uninorte.inventario.data.dao.DetalleVentaDao;
import com.uninorte.inventario.data.dao.VentaDao;
import com.uninorte.inventario.data.dao.VideojuegoDao;
import com.uninorte.inventario.data.db.AppDatabase;
import com.uninorte.inventario.data.entity.Cliente;
import com.uninorte.inventario.data.entity.DetalleVenta;
import com.uninorte.inventario.data.entity.Venta;
import com.uninorte.inventario.data.entity.VentaConDetallesYCliente;
import com.uninorte.inventario.data.entity.Videojuego;
import com.uninorte.inventario.databinding.DialogVentaBinding;
import com.uninorte.inventario.databinding.FragmentVentasBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VentasFragment extends Fragment implements VentasAdapter.Listener {

    private FragmentVentasBinding binding;
    private AppDatabase db;
    private VentaDao ventaDao;
    private DetalleVentaDao detalleDao;
    private ClienteDao clienteDao;
    private VideojuegoDao videojuegoDao;
    private VentasAdapter adapter;

    // Executor recreado en onViewCreated y apagado en onDestroyView
    private ExecutorService io;

    // ===== Helpers ciclo de vida / UI segura =====
    private boolean isUiActive() {
        return isAdded() && getActivity() != null && binding != null && binding.getRoot() != null;
    }
    private void runOnUiThreadSafe(Runnable r) {
        if (!isUiActive()) return;
        getActivity().runOnUiThread(() -> {
            if (isUiActive()) r.run();
        });
    }
    // =============================================

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentVentasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        io = Executors.newSingleThreadExecutor();

        db = AppDatabase.getInstance(requireContext());
        ventaDao     = db.ventaDao();
        detalleDao   = db.detalleVentaDao();
        clienteDao   = db.clienteDao();
        videojuegoDao= db.videojuegoDao();

        adapter = new VentasAdapter(this);
        binding.rvVentas.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvVentas.setAdapter(adapter);

        binding.fabAddVenta.setOnClickListener(v -> openVentaDialog(null));

        loadVentas();
    }

    private void loadVentas(){
        if (io == null || io.isShutdown()) return;
        io.execute(() -> {
            List<VentaConDetallesYCliente> lista = ventaDao.findAllConTodo();
            runOnUiThreadSafe(() -> adapter.submitList(lista));
        });
    }

    /** Diálogo de creación/edición (versión simple: 1 ítem por venta) */
    private void openVentaDialog(@Nullable VentaConDetallesYCliente toEdit){
        if (!isUiActive()) return;

        DialogVentaBinding d = DialogVentaBinding.inflate(getLayoutInflater());

        if (io == null || io.isShutdown()) return;
        io.execute(() -> {
            List<Cliente> clientes = clienteDao.findAll();
            List<Videojuego> juegos = videojuegoDao.findAll();

            List<String> nombresClientes = new ArrayList<>();
            for (Cliente c: clientes) nombresClientes.add(c.nombre);

            List<String> nombresJuegos = new ArrayList<>();
            for (Videojuego v: juegos) nombresJuegos.add(v.titulo + " (Stock: " + v.stock + ")");

            runOnUiThreadSafe(() -> {
                ArrayAdapter<String> ac = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, nombresClientes);
                ArrayAdapter<String> aj = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, nombresJuegos);
                d.spCliente.setAdapter(ac);
                d.spVideojuego.setAdapter(aj);

                if (toEdit != null) {
                    // seleccionar cliente actual
                    int iCli = 0;
                    for (int i=0;i<clientes.size();i++){
                        if (clientes.get(i).clienteId == toEdit.cliente.clienteId) { iCli = i; break; }
                    }
                    d.spCliente.setSelection(iCli);

                    // precargar primer detalle (versión simple)
                    int cantidad = 1;
                    long juegoIdSel = -1;
                    if (toEdit.detalles != null && !toEdit.detalles.isEmpty()){
                        DetalleVenta d0 = toEdit.detalles.get(0);
                        cantidad = d0.cantidad;
                        juegoIdSel = d0.videojuegoId;
                    }
                    d.edtCantidad.setText(String.valueOf(cantidad));

                    int iJ = 0;
                    if (juegoIdSel != -1) {
                        for (int i=0;i<juegos.size();i++){
                            if (juegos.get(i).videojuegoId == juegoIdSel) { iJ = i; break; }
                        }
                    }
                    d.spVideojuego.setSelection(iJ);
                }

                new AlertDialog.Builder(requireContext())
                        .setTitle(toEdit == null ? "Nueva venta" : "Editar venta")
                        .setView(d.getRoot())
                        .setPositiveButton("Guardar", (dialog, which) -> {
                            if (clientes.isEmpty() || juegos.isEmpty()){
                                Toast.makeText(requireContext(),"Necesitas al menos 1 cliente y 1 videojuego", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            String cantStr = String.valueOf(d.edtCantidad.getText()).trim();
                            int cant;
                            try { cant = Integer.parseInt(cantStr); }
                            catch (Exception e){
                                Toast.makeText(requireContext(),"Cantidad inválida", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (cant <= 0){
                                Toast.makeText(requireContext(),"Cantidad debe ser > 0", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            int iCliente = d.spCliente.getSelectedItemPosition();
                            int iJuego   = d.spVideojuego.getSelectedItemPosition();
                            Cliente cli = clientes.get(iCliente);
                            Videojuego juego = juegos.get(iJuego);

                            if (juego.stock < cant){
                                Toast.makeText(requireContext(),"Stock insuficiente", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (io == null || io.isShutdown()) return;
                            io.execute(() -> {
                                if (toEdit == null) {
                                    // CREAR
                                    Venta vta = new Venta();
                                    vta.clienteId   = cli.clienteId;
                                    vta.fechaMillis = System.currentTimeMillis();
                                    long ventaId = ventaDao.insert(vta);

                                    DetalleVenta det = new DetalleVenta();
                                    det.ventaId = ventaId;
                                    det.videojuegoId = juego.videojuegoId;
                                    det.cantidad = cant;
                                    detalleDao.insert(det);

                                    videojuegoDao.actualizarStock(juego.videojuegoId, -cant);
                                } else {
                                    // EDITAR (simple: 1 línea de detalle)
                                    // 1) reponer stock de detalles actuales y borrarlos
                                    List<DetalleVenta> existentes = detalleDao.findByVenta(toEdit.venta.ventaId);
                                    for (DetalleVenta dOld : existentes) {
                                        videojuegoDao.actualizarStock(dOld.videojuegoId, dOld.cantidad);
                                        detalleDao.delete(dOld);
                                    }
                                    // 2) actualizar venta (cliente)
                                    toEdit.venta.clienteId = cli.clienteId;
                                    ventaDao.update(toEdit.venta);
                                    // 3) insertar nuevo detalle y descontar stock
                                    DetalleVenta det = new DetalleVenta();
                                    det.ventaId = toEdit.venta.ventaId;
                                    det.videojuegoId = juego.videojuegoId;
                                    det.cantidad = cant;
                                    detalleDao.insert(det);

                                    videojuegoDao.actualizarStock(juego.videojuegoId, -cant);
                                }

                                runOnUiThreadSafe(this::loadVentas);
                            });
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            });
        });
    }

    // Listener del adapter (tap para editar)
    @Override public void onEdit(VentaConDetallesYCliente venta) {
        openVentaDialog(venta);
    }

    @Override public void onDelete(VentaConDetallesYCliente venta) {
        if (!isUiActive()) return;
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar venta")
                .setMessage("¿Eliminar esta venta y sus detalles?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    if (io == null || io.isShutdown()) return;
                    io.execute(() -> {
                        // Devolver stock por cada detalle antes de borrar
                        List<DetalleVenta> dets = detalleDao.findByVenta(venta.venta.ventaId);
                        for (DetalleVenta d : dets) {
                            videojuegoDao.actualizarStock(d.videojuegoId, d.cantidad);
                        }
                        // Borrar detalles y venta
                        for (DetalleVenta d : dets) detalleDao.delete(d);
                        ventaDao.delete(venta.venta);

                        runOnUiThreadSafe(this::loadVentas);
                    });
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        if (io != null && !io.isShutdown()) io.shutdownNow();
        io = null;
    }
}
