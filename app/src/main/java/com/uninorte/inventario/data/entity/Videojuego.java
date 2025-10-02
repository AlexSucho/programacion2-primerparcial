package com.uninorte.inventario.data.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "videojuegos",
        foreignKeys = @ForeignKey(
                entity = Categoria.class,
                parentColumns = "categoriaId",
                childColumns = "categoriaId",
                onDelete = ForeignKey.RESTRICT
        ),
        indices = {@Index("categoriaId")}
)
public class Videojuego {
    @PrimaryKey(autoGenerate = true)
    public long videojuegoId;

    public String titulo;
    public double precio;
    public int stock;

    public long categoriaId;
}
