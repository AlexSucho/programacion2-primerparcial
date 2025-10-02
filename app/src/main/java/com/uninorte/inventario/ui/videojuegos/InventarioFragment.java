package com.uninorte.inventario.ui.videojuegos;

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

import com.uninorte.inventario.data.dao.CategoriaDao;
import com.uninorte.inventario.data.dao.VideojuegoDao;
import com.uninorte.inventario.data.db.AppDatabase;
import com.uninorte.inventario.data.entity.Categoria;
import com.uninorte.inventario.data.entity.Videojuego;
import com.uninorte.inventario.databinding.DialogVideojuegoBinding;
import com.uninorte.inventario.databinding.FragmentInventarioBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InventarioFragment extends Fragment implements VideojuegoAdapter.Listener {

    private FragmentInventarioBinding binding;
    private AppDatabase db;
    private VideojuegoDao videojuegoDao;
    private CategoriaDao categoriaDao;
    private VideojuegoAdapter adapter;

    // Ya no final: lo creamos en onViewCreated y lo apagamos en onDestroyView.
    private ExecutorService io;

    // -------- Helpers de seguridad UI --------
    private boolean isUiActive() {
        return isAdded() && getActivity() != null && binding != null && binding.getRoot() != null;
    }

    private void runOnUiThreadSafe(Runnable r) {
        if (!isUiActive()) return;
        getActivity().runOnUiThread(() -> {
            if (isUiActive()) r.run();
        });
    }
    // -----------------------------------------

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentInventarioBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // (Re)crear executor cada vez que se crea la vista
        io = Executors.newSingleThreadExecutor();

        db = AppDatabase.getInstance(requireContext());
        videojuegoDao = db.videojuegoDao();
        categoriaDao  = db.categoriaDao();

        adapter = new VideojuegoAdapter(this);
        binding.rvVideojuegos.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvVideojuegos.setAdapter(adapter);

        binding.fabAdd.setOnClickListener(v -> openDialog(null));

        loadVideojuegos();
    }

    private void loadVideojuegos() {
        if (io == null || io.isShutdown()) return;
        io.execute(() -> {
            List<Videojuego> lista = videojuegoDao.findAll();
            runOnUiThreadSafe(() -> {
                adapter.submitList(lista);
                // Si añadiste emptyView, descomenta:
                // boolean vacio = (lista == null || lista.isEmpty());
                // binding.emptyView.setVisibility(vacio ? View.VISIBLE : View.GONE);
                // binding.rvVideojuegos.setVisibility(vacio ? View.GONE : View.VISIBLE);
            });
        });
    }

    private void openDialog(@Nullable Videojuego toEdit) {
        if (!isUiActive()) return;

        DialogVideojuegoBinding d = DialogVideojuegoBinding.inflate(getLayoutInflater());

        // Cargar categorías al Spinner (IO)
        if (io == null || io.isShutdown()) return;
        io.execute(() -> {
            List<Categoria> cats = categoriaDao.findAll();
            List<String> nombres = new ArrayList<>();
            for (Categoria c : cats) nombres.add(c.nombre);

            runOnUiThreadSafe(() -> {
                if (getContext() == null) return;

                ArrayAdapter<String> adp = new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        nombres
                );
                d.spCategoria.setAdapter(adp);

                if (toEdit != null) {
                    d.edtTitulo.setText(toEdit.titulo);
                    d.edtPrecio.setText(String.valueOf(toEdit.precio));
                    d.edtStock.setText(String.valueOf(toEdit.stock));
                    // Seleccionar categoría actual si se puede
                    int idx = 0;
                    for (int i = 0; i < cats.size(); i++) {
                        if (cats.get(i).categoriaId == toEdit.categoriaId) { idx = i; break; }
                    }
                    d.spCategoria.setSelection(idx);
                }

                if (!isUiActive()) return;
                new AlertDialog.Builder(requireContext())
                        .setTitle(toEdit == null ? "Agregar videojuego" : "Editar videojuego")
                        .setView(d.getRoot())
                        .setPositiveButton("Guardar", (dialog, which) -> {
                            if (!isUiActive()) return;
                            // Validaciones simples
                            String titulo = String.valueOf(d.edtTitulo.getText()).trim();
                            String precioStr = String.valueOf(d.edtPrecio.getText()).trim();
                            String stockStr  = String.valueOf(d.edtStock.getText()).trim();
                            if (titulo.isEmpty() || precioStr.isEmpty() || stockStr.isEmpty()) {
                                if (getContext() != null)
                                    Toast.makeText(getContext(),"Completa todos los campos", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            double precio; int stock;
                            try {
                                precio = Double.parseDouble(precioStr);
                                stock  = Integer.parseInt(stockStr);
                            } catch (Exception e) {
                                if (getContext() != null)
                                    Toast.makeText(getContext(),"Valores inválidos", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            final int pos = d.spCategoria.getSelectedItemPosition();
                            if (pos < 0 || pos >= cats.size()) {
                                if (getContext() != null)
                                    Toast.makeText(getContext(),"Selecciona una categoría", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            long categoriaId = cats.get(pos).categoriaId;

                            if (io == null || io.isShutdown()) return;
                            io.execute(() -> {
                                if (toEdit == null) {
                                    Videojuego v = new Videojuego();
                                    v.titulo = titulo; v.precio = precio; v.stock = stock; v.categoriaId = categoriaId;
                                    videojuegoDao.insert(v);
                                } else {
                                    toEdit.titulo = titulo; toEdit.precio = precio; toEdit.stock = stock; toEdit.categoriaId = categoriaId;
                                    videojuegoDao.update(toEdit);
                                }
                                runOnUiThreadSafe(this::loadVideojuegos);
                            });
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            });
        });
    }

    @Override public void onEdit(Videojuego v) { openDialog(v); }

    @Override public void onDelete(Videojuego v) {
        if (!isUiActive()) return;
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar")
                .setMessage("¿Eliminar \"" + v.titulo + "\"?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    if (io == null || io.isShutdown()) return;
                    io.execute(() -> {
                        videojuegoDao.delete(v);
                        runOnUiThreadSafe(this::loadVideojuegos);
                    });
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        if (io != null && !io.isShutdown()) {
            io.shutdownNow(); // cancelamos callbacks pendientes
        }
        io = null; // evita RejectedExecutionException al volver
    }
}
