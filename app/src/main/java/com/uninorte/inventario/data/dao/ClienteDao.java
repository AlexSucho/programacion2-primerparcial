package com.uninorte.inventario.data.dao;

import androidx.room.*;
import com.uninorte.inventario.data.entity.Cliente;
import java.util.List;

@Dao
public interface ClienteDao {
    @Insert long insert(Cliente c);
    @Update int update(Cliente c);
    @Delete int delete(Cliente c);

    @Query("SELECT * FROM clientes ORDER BY nombre")
    List<Cliente> findAll();

    @Query("SELECT * FROM clientes WHERE clienteId = :id")
    Cliente findById(long id);
}
