package com.example.footstaats.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.footstaats.data.model.Entrenamiento;

import java.util.List;

@Dao
public interface EntrenamientoDao {

    @Insert
    void insertar(Entrenamiento entrenamiento);

    @Update
    void actualizar(Entrenamiento entrenamiento);

    @Query("SELECT * FROM entrenamientos WHERE usuarioId = :usuarioId ORDER BY fecha DESC")
    LiveData<List<Entrenamiento>> obtenerTodos(int usuarioId);

    @Query("SELECT * FROM entrenamientos WHERE usuarioId = :usuarioId ORDER BY fecha DESC")
    List<Entrenamiento> obtenerTodosSync(int usuarioId);

    @Query("DELETE FROM entrenamientos WHERE id = :id")
    void eliminar(int id);
}