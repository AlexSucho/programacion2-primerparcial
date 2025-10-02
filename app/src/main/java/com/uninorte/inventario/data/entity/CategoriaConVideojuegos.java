package com.uninorte.inventario.data.entity;

import androidx.room.Embedded;
import androidx.room.Relation;
import java.util.List;

public class CategoriaConVideojuegos {
    @Embedded
    public Categoria categoria;

    @Relation(
            parentColumn = "categoriaId",
            entityColumn = "categoriaId"
    )
    public List<Videojuego> videojuegos;
}
