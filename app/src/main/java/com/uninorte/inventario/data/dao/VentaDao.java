package com.uninorte.inventario.data.dao;

import androidx.room.*;
import com.uninorte.inventario.data.entity.Venta;
import com.uninorte.inventario.data.entity.VentaConDetallesYCliente;
import java.util.List;

@Dao
public interface VentaDao {
    @Insert long insert(Venta v);
    @Update int update(Venta v);
    @Delete int delete(Venta v);

    @Query("SELECT * FROM ventas ORDER BY ventaId DESC")
    List<Venta> findAll();

    // Para mostrar en lista con cliente y detalles
    @Transaction
    @Query("SELECT * FROM ventas ORDER BY ventaId DESC")
    List<VentaConDetallesYCliente> findAllConTodo();
}
