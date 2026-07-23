package com.example.footstaats.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.footstaats.data.dao.EntrenamientoDao;
import com.example.footstaats.data.dao.LesionDao;
import com.example.footstaats.data.dao.PartidoDao;
import com.example.footstaats.data.db.AppDatabase;
import com.example.footstaats.data.model.Entrenamiento;
import com.example.footstaats.data.model.Lesion;
import com.example.footstaats.data.model.Partido;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EstadisticasRepository {

    private final PartidoDao partidoDao;
    private final EntrenamientoDao entrenamientoDao;
    private final LesionDao lesionDao;
    private final ExecutorService executor;

    public EstadisticasRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        partidoDao = db.partidoDao();
        entrenamientoDao = db.entrenamientoDao();
        lesionDao = db.lesionDao();
        executor = Executors.newSingleThreadExecutor();
    }

    // ── PARTIDOS ──
    public void insertarPartido(Partido partido) {
        executor.execute(() -> partidoDao.insertar(partido));
    }

    public void actualizarPartido(Partido partido) {
        executor.execute(() -> partidoDao.actualizar(partido));
    }

    public void eliminarPartido(Partido partido) {
        executor.execute(() -> partidoDao.eliminar(partido.id));
    }

    public LiveData<List<Partido>> obtenerPartidos(int usuarioId) {
        return partidoDao.obtenerTodos(usuarioId);
    }

    // ── ENTRENAMIENTOS ──
    public void insertarEntrenamiento(Entrenamiento entrenamiento) {
        executor.execute(() -> entrenamientoDao.insertar(entrenamiento));
    }

    public void actualizarEntrenamiento(Entrenamiento entrenamiento) {
        executor.execute(() -> entrenamientoDao.actualizar(entrenamiento));
    }

    public void eliminarEntrenamiento(Entrenamiento entrenamiento) {
        executor.execute(() -> entrenamientoDao.eliminar(entrenamiento.id));
    }

    public LiveData<List<Entrenamiento>> obtenerEntrenamientos(int usuarioId) {
        return entrenamientoDao.obtenerTodos(usuarioId);
    }

    // ── LESIONES ──
    public void insertarLesion(Lesion lesion) {
        executor.execute(() -> lesionDao.insertar(lesion));
    }

    public void actualizarLesion(Lesion lesion) {
        executor.execute(() -> lesionDao.actualizar(lesion));
    }

    public void eliminarLesion(Lesion lesion) {
        executor.execute(() -> lesionDao.eliminar(lesion.id));
    }

    public LiveData<List<Lesion>> obtenerLesiones(int usuarioId) {
        return lesionDao.obtenerTodos(usuarioId);
    }
}