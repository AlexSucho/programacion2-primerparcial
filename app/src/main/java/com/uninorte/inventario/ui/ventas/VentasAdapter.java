package com.uninorte.inventario.ui.ventas;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uninorte.inventario.data.entity.DetalleVenta;
import com.uninorte.inventario.data.entity.VentaConDetallesYCliente;
import com.uninorte.inventario.databinding.ItemVentaBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VentasAdapter extends RecyclerView.Adapter<VentasAdapter.VH> {

    public interface Listener {
        void onEdit(VentaConDetallesYCliente v);
        void onDelete(VentaConDetallesYCliente v);
    }

    private final List<VentaConDetallesYCliente> data = new ArrayList<>();
    private final Listener listener;
    private final SimpleDateFormat sdf =
            new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public VentasAdapter(Listener l) { this.listener = l; }

    public void submitList(List<VentaConDetallesYCliente> items) {
        data.clear();
        if (items != null) data.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int v) {
        return new VH(ItemVentaBinding.inflate(LayoutInflater.from(p.getContext()), p, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        VentaConDetallesYCliente x = data.get(pos);

        String cliente = (x.cliente != null ? x.cliente.nombre : "Sin cliente");
        String fecha   = sdf.format(new Date(x.venta.fechaMillis));
        h.b.txtClienteFecha.setText(cliente + " • " + fecha);

        int totalUnidades = 0;
        if (x.detalles != null) {
            for (DetalleVenta d : x.detalles) totalUnidades += (d != null ? d.cantidad : 0);
        }
        h.b.txtResumen.setText("Ítems: " + totalUnidades);

        // Clicks con posición segura
        h.b.btnEdit.setOnClickListener(v -> {
            int p = h.getBindingAdapterPosition();
            if (p != RecyclerView.NO_POSITION) listener.onEdit(data.get(p));
        });

        h.b.btnDelete.setOnClickListener(v -> {
            int p = h.getBindingAdapterPosition();
            if (p != RecyclerView.NO_POSITION) listener.onDelete(data.get(p));
        });

        // (Opcional) tap en la tarjeta para editar
        h.itemView.setOnClickListener(v -> {
            int p = h.getBindingAdapterPosition();
            if (p != RecyclerView.NO_POSITION) listener.onEdit(data.get(p));
        });
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ItemVentaBinding b;
        VH(ItemVentaBinding b) { super(b.getRoot()); this.b = b; }
    }
}
