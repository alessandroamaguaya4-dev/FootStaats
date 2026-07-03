package com.example.footstaats.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.footstaats.data.model.Entrenamiento;

import java.util.List;

@Dao
public interface EntrenamientoDao {
    @Insert
    void insertar(Entrenamiento entrenamiento);

    @Query("SELECT * FROM entrenamientos ORDER BY fecha DESC")
    LiveData<List<Entrenamiento>> obtenerTodos();

    @Query("DELETE FROM entrenamientos WHERE id = :id")
    void eliminar(int id);

    @Query("SELECT * FROM entrenamientos ORDER BY fecha DESC")
    List<Entrenamiento> obtenerTodosSync();
}
