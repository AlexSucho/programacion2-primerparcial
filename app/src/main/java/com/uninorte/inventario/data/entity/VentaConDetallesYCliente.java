package com.uninorte.inventario.data.entity;

import androidx.room.Embedded;
import androidx.room.Relation;
import java.util.List;

public class VentaConDetallesYCliente {
    @Embedded
    public Venta venta;

    @Relation(
            parentColumn = "clienteId",
            entityColumn = "clienteId"
    )
    public Cliente cliente; // uno-a-uno

    @Relation(
            parentColumn = "ventaId",
            entityColumn = "ventaId"
    )
    public List<DetalleVenta> detalles;
}
