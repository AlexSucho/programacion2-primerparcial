package com.uninorte.inventario.ui.clientes;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.*;
import android.widget.Toast;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.uninorte.inventario.data.db.AppDatabase;
import com.uninorte.inventario.data.dao.ClienteDao;
import com.uninorte.inventario.data.entity.Cliente;
import com.uninorte.inventario.databinding.DialogClienteBinding;
import com.uninorte.inventario.databinding.FragmentClientesBinding;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientesFragment extends Fragment implements ClienteAdapter.Listener {

    private FragmentClientesBinding binding;
    private AppDatabase db;
    private ClienteDao clienteDao;
    private ClienteAdapter adapter;
    private final ExecutorService io = Executors.newSingleThreadExecutor();

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentClientesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = AppDatabase.getInstance(requireContext());
        clienteDao = db.clienteDao();

        adapter = new ClienteAdapter(this);
        binding.rvClientes.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvClientes.setAdapter(adapter);

        binding.fabAddCliente.setOnClickListener(v -> openDialog(null));

        loadClientes();
    }

    private void loadClientes() {
        io.execute(() -> {
            List<Cliente> lista = clienteDao.findAll();
            requireActivity().runOnUiThread(() -> adapter.submitList(lista));
        });
    }

    private void openDialog(@Nullable Cliente toEdit) {
        DialogClienteBinding d = DialogClienteBinding.inflate(getLayoutInflater());

        if (toEdit != null) {
            d.edtNombre.setText(toEdit.nombre);
            d.edtEmail.setText(toEdit.email);
        }

        new AlertDialog.Builder(requireContext())
                .setTitle(toEdit == null ? "Agregar cliente" : "Editar cliente")
                .setView(d.getRoot())
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nombre = String.valueOf(d.edtNombre.getText()).trim();
                    String email  = String.valueOf(d.edtEmail.getText()).trim();

                    if (nombre.isEmpty() || email.isEmpty()) {
                        Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    io.execute(() -> {
                        if (toEdit == null) {
                            Cliente c = new Cliente();
                            c.nombre = nombre; c.email = email;
                            clienteDao.insert(c);
                        } else {
                            toEdit.nombre = nombre; toEdit.email = email;
                            clienteDao.update(toEdit);
                        }
                        requireActivity().runOnUiThread(this::loadClientes);
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override public void onEdit(Cliente c) { openDialog(c); }

    @Override public void onDelete(Cliente c) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar")
                .setMessage("¿Eliminar \"" + c.nombre + "\"?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    io.execute(() -> {
                        clienteDao.delete(c);
                        requireActivity().runOnUiThread(this::loadClientes);
                    });
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        io.shutdown();
    }
}
