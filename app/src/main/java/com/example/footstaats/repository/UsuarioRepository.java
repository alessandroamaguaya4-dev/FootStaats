package com.example.footstaats.repository;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import com.example.footstaats.data.dao.UsuarioDao;
import com.example.footstaats.data.db.AppDatabase;
import com.example.footstaats.data.model.Usuario;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UsuarioRepository {

    private final UsuarioDao usuarioDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public UsuarioRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        usuarioDao = db.usuarioDao();
    }

    public interface OnUsuarioListo {
        void listo(Usuario usuario);
    }

    public void guardarOActualizar(Usuario usuario, OnUsuarioListo callback) {
        executor.execute(() -> {
            Usuario existente = usuarioDao.buscarPorUid(usuario.firebaseUid);
            if (existente == null) {
                long nuevoId = usuarioDao.insertar(usuario);
                usuario.id = (int) nuevoId;
            } else {
                usuario.id = existente.id;
                usuarioDao.actualizar(usuario);
            }
            mainHandler.post(() -> callback.listo(usuario));
        });
    }

    public void obtenerPorId(int id, OnUsuarioListo callback) {
        executor.execute(() -> {
            Usuario usuario = usuarioDao.buscarPorId(id);
            mainHandler.post(() -> callback.listo(usuario));
        });
    }
}