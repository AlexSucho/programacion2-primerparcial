package com.uninorte.inventario.data.dao;

import androidx.room.*;
import com.uninorte.inventario.data.entity.DetalleVenta;
import java.util.List;

@Dao
public interface DetalleVentaDao {
    @Insert long insert(DetalleVenta d);
    @Update int update(DetalleVenta d);
    @Delete int delete(DetalleVenta d);

    @Query("SELECT * FROM detalles WHERE ventaId = :ventaId")
    List<DetalleVenta> findByVenta(long ventaId);
}
