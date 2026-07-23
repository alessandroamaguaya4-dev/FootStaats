package com.example.footstaats.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class RecordatorioRecuperacionWorker extends Worker {

    public static final String KEY_PARTE_CUERPO = "parte_cuerpo";

    public RecordatorioRecuperacionWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        String parteCuerpo = getInputData().getString(KEY_PARTE_CUERPO);
        String mensaje = "Tu recuperación de " + (parteCuerpo != null ? parteCuerpo : "la lesión") + " debería estar completa. ¡Ya puedes volver a entrenar!";

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS)
                        == PackageManager.PERMISSION_GRANTED) {
            NotificationHelper.mostrarNotificacion(
                    getApplicationContext(),
                    (int) System.currentTimeMillis(), // id único por notificación
                    "Recuperación completa",
                    mensaje
            );
        }

        return Result.success();
    }
}