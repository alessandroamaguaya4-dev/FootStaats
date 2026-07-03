package com.example.footstaats.data.dao;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.footstaats.data.model.Partido;

import java.util.List;

@Dao
public interface PartidoDao {
    @Insert
    void insertar(Partido partido);

    @Query("SELECT * FROM partidos ORDER BY fecha DESC")
    LiveData<List<Partido>> obtenerTodos();

    @Query("DELETE FROM partidos WHERE id = :id")
    void eliminar(int id);
    @Query("SELECT * FROM partidos ORDER BY fecha DESC")
    List<Partido> obtenerTodosSync();
}
