package com.uninorte.inventario.ui.categorias;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.uninorte.inventario.data.entity.Categoria;
import com.uninorte.inventario.databinding.ItemCategoriaBinding;
import java.util.*;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.VH> {
    public interface Listener { void onEdit(Categoria c); void onDelete(Categoria c); }
    private final List<Categoria> data = new ArrayList<>();
    private final Listener listener;
    public CategoriaAdapter(Listener l){ this.listener = l; }
    public void submitList(List<Categoria> items){ data.clear(); if(items!=null) data.addAll(items); notifyDataSetChanged(); }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int v) {
        return new VH(ItemCategoriaBinding.inflate(LayoutInflater.from(p.getContext()), p, false));
    }
    @Override public void onBindViewHolder(@NonNull VH h, int pos){
        Categoria c = data.get(pos);
        h.b.txtNombre.setText(c.nombre);
        h.b.btnEdit.setOnClickListener(v -> listener.onEdit(c));
        h.b.btnDelete.setOnClickListener(v -> listener.onDelete(c));
    }
    @Override public int getItemCount(){ return data.size(); }
    static class VH extends RecyclerView.ViewHolder{
        ItemCategoriaBinding b; VH(ItemCategoriaBinding b){ super(b.getRoot()); this.b=b; }
    }
}
