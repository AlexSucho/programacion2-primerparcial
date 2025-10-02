package com.uninorte.inventario.data.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "detalles",
        foreignKeys = {
                @ForeignKey(
                        entity = Venta.class,
                        parentColumns = "ventaId",
                        childColumns = "ventaId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Videojuego.class,
                        parentColumns = "videojuegoId",
                        childColumns = "videojuegoId",
                        onDelete = ForeignKey.RESTRICT
                )
        },
        indices = {@Index("ventaId"), @Index("videojuegoId")}
)
public class DetalleVenta {
    @PrimaryKey(autoGenerate = true)
    public long detalleId;

    public long ventaId;
    public long videojuegoId;
    public int cantidad;
}
