package com.uninorte.inventario.data.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.uninorte.inventario.data.dao.CategoriaDao;
import com.uninorte.inventario.data.dao.ClienteDao;
import com.uninorte.inventario.data.dao.VideojuegoDao;
// 👇 NUEVOS IMPORTS
import com.uninorte.inventario.data.dao.VentaDao;
import com.uninorte.inventario.data.dao.DetalleVentaDao;

import com.uninorte.inventario.data.entity.Categoria;
import com.uninorte.inventario.data.entity.Cliente;
import com.uninorte.inventario.data.entity.DetalleVenta;
import com.uninorte.inventario.data.entity.Venta;
import com.uninorte.inventario.data.entity.Videojuego;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {
                Categoria.class,
                Videojuego.class,
                Cliente.class,
                Venta.class,
                DetalleVenta.class
        },
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract CategoriaDao categoriaDao();
    public abstract VideojuegoDao videojuegoDao();
    public abstract ClienteDao clienteDao();
    // 👇 NUEVOS GETTERS
    public abstract VentaDao ventaDao();
    public abstract DetalleVentaDao detalleVentaDao();

    private static final ExecutorService dbExecutor = Executors.newSingleThreadExecutor();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "inventario_db"
                            )
                            .fallbackToDestructiveMigration()
                            .addCallback(prepopulate())
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // Semillas mínimas para que tengas datos al probar
    private static Callback prepopulate() {
        return new Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);
                dbExecutor.execute(() -> {
                    if (INSTANCE == null) return;
                    CategoriaDao catDao = INSTANCE.categoriaDao();
                    if (catDao.count() == 0) {
                        Categoria c1 = new Categoria(); c1.nombre = "Acción";
                        Categoria c2 = new Categoria(); c2.nombre = "Aventura";
                        Categoria c3 = new Categoria(); c3.nombre = "Deportes";
                        catDao.insert(c1);
                        catDao.insert(c2);
                        catDao.insert(c3);
                    }
                });
            }
        };
    }
}
