package com.example.footstaats.data.dao;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.footstaats.data.model.Lesion;

import java.util.List;

@Dao
public interface LesionDao {
    @Insert
    void insertar(Lesion lesion);

    @Query("SELECT * FROM lesiones ORDER BY fecha DESC")
    LiveData<List<Lesion>> obtenerTodos();

    @Query("DELETE FROM lesiones WHERE id = :id")
    void eliminar(int id);

    @Query("SELECT * FROM lesiones ORDER BY fecha DESC")
    List<Lesion> obtenerTodosSync();
}
