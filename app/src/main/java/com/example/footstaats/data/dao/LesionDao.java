package com.example.footstaats.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.footstaats.data.model.Lesion;

import java.util.List;

@Dao
public interface LesionDao {

    @Insert
    void insertar(Lesion lesion);

    @Update
    void actualizar(Lesion lesion);

    @Query("SELECT * FROM lesiones WHERE usuarioId = :usuarioId ORDER BY fecha DESC")
    LiveData<List<Lesion>> obtenerTodos(int usuarioId);

    @Query("SELECT * FROM lesiones WHERE usuarioId = :usuarioId ORDER BY fecha DESC")
    List<Lesion> obtenerTodosSync(int usuarioId);

    @Query("DELETE FROM lesiones WHERE id = :id")
    void eliminar(int id);
}