package com.uninorte.inventario.ui.videojuegos;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uninorte.inventario.data.entity.Videojuego;
import com.uninorte.inventario.databinding.ItemVideojuegoBinding;

import java.util.ArrayList;
import java.util.List;

public class VideojuegoAdapter extends RecyclerView.Adapter<VideojuegoAdapter.VH> {

    public interface Listener {
        void onEdit(Videojuego v);
        void onDelete(Videojuego v);
    }

    private final List<Videojuego> data = new ArrayList<>();
    private final Listener listener;

    public VideojuegoAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submitList(List<Videojuego> items) {
        data.clear();
        if (items != null) data.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemVideojuegoBinding b = ItemVideojuegoBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new VH(b);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Videojuego v = data.get(position);
        h.b.txtTitulo.setText(v.titulo);
        h.b.txtPrecioStock.setText("Gs. " + v.precio + "  •  Stock: " + v.stock);

        h.b.btnEdit.setOnClickListener(_v -> listener.onEdit(v));
        h.b.btnDelete.setOnClickListener(_v -> listener.onDelete(v));
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ItemVideojuegoBinding b;
        VH(ItemVideojuegoBinding binding) {
            super(binding.getRoot());
            this.b = binding;
        }
    }
}
