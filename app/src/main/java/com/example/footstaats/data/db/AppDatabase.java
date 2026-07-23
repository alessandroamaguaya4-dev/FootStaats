package com.example.footstaats.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.footstaats.data.dao.EntrenamientoDao;
import com.example.footstaats.data.dao.LesionDao;
import com.example.footstaats.data.dao.PartidoDao;
import com.example.footstaats.data.dao.UsuarioDao;
import com.example.footstaats.data.model.Entrenamiento;
import com.example.footstaats.data.model.Lesion;
import com.example.footstaats.data.model.Partido;
import com.example.footstaats.data.model.Usuario;

@Database(entities = {Partido.class, Entrenamiento.class, Lesion.class, Usuario.class}, version = 5)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instancia;

    public abstract PartidoDao partidoDao();
    public abstract EntrenamientoDao entrenamientoDao();
    public abstract LesionDao lesionDao();
    public abstract UsuarioDao usuarioDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instancia == null) {
            instancia = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "footstaats_db"
                    )
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instancia;
    }
}