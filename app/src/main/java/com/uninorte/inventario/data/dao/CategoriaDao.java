package com.uninorte.inventario.data.dao;

import androidx.room.*;
import com.uninorte.inventario.data.entity.Categoria;
import java.util.List;

@Dao
public interface CategoriaDao {
    @Insert long insert(Categoria c);
    @Update int update(Categoria c);
    @Delete int delete(Categoria c);

    @Query("SELECT * FROM categorias ORDER BY nombre")
    List<Categoria> findAll();

    @Query("SELECT COUNT(*) FROM categorias")
    int count();
}
