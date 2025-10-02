package com.uninorte.inventario.data.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "ventas",
        foreignKeys = @ForeignKey(
                entity = Cliente.class,
                parentColumns = "clienteId",
                childColumns = "clienteId",
                onDelete = ForeignKey.RESTRICT
        ),
        indices = {@Index("clienteId")}
)
public class Venta {
    @PrimaryKey(autoGenerate = true)
    public long ventaId;

    public long clienteId;
    public long fechaMillis; // Epoch time
}
