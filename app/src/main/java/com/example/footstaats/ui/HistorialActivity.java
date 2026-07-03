package com.example.footstaats.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.footstaats.R;
import com.example.footstaats.data.db.AppDatabase;
import com.example.footstaats.data.model.Entrenamiento;
import com.example.footstaats.data.model.Lesion;
import com.example.footstaats.data.model.Partido;

import java.util.List;
import java.util.concurrent.Executors;

public class HistorialActivity extends AppCompatActivity {

    private TextView tvHistorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        tvHistorial = findViewById(R.id.tvHistorial);
        tvHistorial.setText("Cargando...");

        cargarHistorial();
    }

    private void cargarHistorial() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);

            List<Partido> partidos = db.partidoDao().obtenerTodosSync();
            List<Entrenamiento> entrenamientos = db.entrenamientoDao().obtenerTodosSync();
            List<Lesion> lesiones = db.lesionDao().obtenerTodosSync();

            StringBuilder sb = new StringBuilder();

            sb.append("═══ PARTIDOS ═══\n\n");
            if (partidos != null && !partidos.isEmpty()) {
                for (Partido p : partidos) {
                    sb.append("📅 ").append(p.fecha).append("\n");
                    sb.append("⚽ vs ").append(p.rival).append("\n");
                    sb.append("Goles: ").append(p.goles).append(" | Asistencias: ").append(p.asistencias).append("\n");
                    sb.append("Minutos: ").append(p.minutosJugados).append("\n\n");
                }
            } else {
                sb.append("Sin partidos registrados\n\n");
            }

            sb.append("═══ ENTRENAMIENTOS ═══\n\n");
            if (entrenamientos != null && !entrenamientos.isEmpty()) {
                for (Entrenamiento e : entrenamientos) {
                    sb.append("📅 ").append(e.fecha).append("\n");
                    sb.append("Tipo: ").append(e.tipo).append("\n");
                    sb.append("Duración: ").append(e.duracionMinutos).append(" min | Intensidad: ").append(e.intensidad).append("/5\n\n");
                }
            } else {
                sb.append("Sin entrenamientos registrados\n\n");
            }

            sb.append("═══ LESIONES ═══\n\n");
            if (lesiones != null && !lesiones.isEmpty()) {
                for (Lesion l : lesiones) {
                    sb.append("📅 ").append(l.fecha).append("\n");
                    sb.append("Tipo: ").append(l.tipo).append("\n");
                    sb.append("Parte: ").append(l.parteCuerpo).append("\n");
                    sb.append("Recuperación: ").append(l.diasRecuperacion).append(" días\n\n");
                }
            } else {
                sb.append("Sin lesiones registradas\n\n");
            }

            String resultado = sb.toString();
            runOnUiThread(() -> tvHistorial.setText(resultado));
        });
    }
}