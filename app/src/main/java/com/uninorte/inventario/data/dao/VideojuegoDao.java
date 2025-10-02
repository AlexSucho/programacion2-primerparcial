package com.uninorte.inventario.data.dao;

import androidx.room.*;
import com.uninorte.inventario.data.entity.Videojuego;
import java.util.List;

@Dao
public interface VideojuegoDao {
    @Insert long insert(Videojuego v);
    @Update int update(Videojuego v);
    @Delete int delete(Videojuego v);

    @Query("SELECT * FROM videojuegos ORDER BY titulo")
    List<Videojuego> findAll();

    @Query("SELECT * FROM videojuegos WHERE videojuegoId = :id")
    Videojuego findById(long id);

    @Query("UPDATE videojuegos SET stock = stock + :delta WHERE videojuegoId = :id")
    int actualizarStock(long id, int delta);
}
