package com.uninorte.inventario.ui.clientes;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.uninorte.inventario.data.entity.Cliente;
import com.uninorte.inventario.databinding.ItemClienteBinding;
import java.util.ArrayList;
import java.util.List;

public class ClienteAdapter extends RecyclerView.Adapter<ClienteAdapter.VH> {

    public interface Listener {
        void onEdit(Cliente c);
        void onDelete(Cliente c);
    }

    private final List<Cliente> data = new ArrayList<>();
    private final Listener listener;

    public ClienteAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submitList(List<Cliente> items) {
        data.clear();
        if (items != null) data.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemClienteBinding b = ItemClienteBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new VH(b);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Cliente c = data.get(position);
        h.b.txtNombre.setText(c.nombre);
        h.b.txtEmail.setText(c.email);
        h.b.btnEdit.setOnClickListener(v -> listener.onEdit(c));
        h.b.btnDelete.setOnClickListener(v -> listener.onDelete(c));
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ItemClienteBinding b;
        VH(ItemClienteBinding binding) {
            super(binding.getRoot());
            b = binding;
        }
    }
}
