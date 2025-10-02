package com.uninorte.inventario.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "clientes")
public class Cliente {
    @PrimaryKey(autoGenerate = true)
    public long clienteId;

    public String nombre;
    public String email;
}
