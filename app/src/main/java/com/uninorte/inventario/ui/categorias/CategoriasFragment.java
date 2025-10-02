package com.uninorte.inventario.ui.categorias;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.*;
import android.widget.Toast;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.uninorte.inventario.data.db.AppDatabase;
import com.uninorte.inventario.data.dao.CategoriaDao;
import com.uninorte.inventario.data.entity.Categoria;
import com.uninorte.inventario.databinding.DialogCategoriaBinding;
import com.uninorte.inventario.databinding.FragmentCategoriasBinding;
import java.util.List;
import java.util.concurrent.*;

public class CategoriasFragment extends Fragment implements CategoriaAdapter.Listener {

    private FragmentCategoriasBinding binding;
    private CategoriaDao categoriaDao;
    private final ExecutorService io = Executors.newSingleThreadExecutor();
    private CategoriaAdapter adapter;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCategoriasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        categoriaDao = AppDatabase.getInstance(requireContext()).categoriaDao();
        adapter = new CategoriaAdapter(this);
        binding.rvCategorias.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvCategorias.setAdapter(adapter);
        binding.fabAddCategoria.setOnClickListener(v -> openDialog(null));
        load();
    }

    private void load(){
        io.execute(() -> {
            List<Categoria> list = categoriaDao.findAll();
            requireActivity().runOnUiThread(() -> adapter.submitList(list));
        });
    }

    private void openDialog(@Nullable Categoria toEdit){
        DialogCategoriaBinding d = DialogCategoriaBinding.inflate(getLayoutInflater());
        if (toEdit != null) d.edtNombre.setText(toEdit.nombre);

        new AlertDialog.Builder(requireContext())
                .setTitle(toEdit==null? "Agregar categoría":"Editar categoría")
                .setView(d.getRoot())
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nombre = String.valueOf(d.edtNombre.getText()).trim();
                    if (nombre.isEmpty()){ Toast.makeText(requireContext(),"Nombre requerido",Toast.LENGTH_SHORT).show(); return; }
                    io.execute(() -> {
                        if (toEdit==null){ Categoria c = new Categoria(); c.nombre = nombre; categoriaDao.insert(c); }
                        else { toEdit.nombre = nombre; categoriaDao.update(toEdit); }
                        requireActivity().runOnUiThread(this::load);
                    });
                })
                .setNegativeButton("Cancelar", null).show();
    }

    @Override public void onEdit(Categoria c){ openDialog(c); }
    @Override public void onDelete(Categoria c){
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar").setMessage("¿Eliminar \""+c.nombre+"\"?")
                .setPositiveButton("Sí", (d,w)-> io.execute(()->{ categoriaDao.delete(c); requireActivity().runOnUiThread(this::load);} ))
                .setNegativeButton("No", null).show();
    }

    @Override public void onDestroyView(){ super.onDestroyView(); binding=null; io.shutdown(); }
}
