package com.example.footstaats.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.footstaats.data.model.Partido;

import java.util.List;

@Dao
public interface PartidoDao {

    @Insert
    void insertar(Partido partido);

    @Update
    void actualizar(Partido partido);

    @Query("SELECT * FROM partidos WHERE usuarioId = :usuarioId ORDER BY fecha DESC")
    LiveData<List<Partido>> obtenerTodos(int usuarioId);

    @Query("SELECT * FROM partidos WHERE usuarioId = :usuarioId ORDER BY fecha DESC")
    List<Partido> obtenerTodosSync(int usuarioId);

    @Query("DELETE FROM partidos WHERE id = :id")
    void eliminar(int id);
}