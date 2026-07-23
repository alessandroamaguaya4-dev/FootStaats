package com.example.footstaats.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.footstaats.R;
import com.example.footstaats.data.db.AppDatabase;
import com.example.footstaats.data.model.Entrenamiento;
import com.example.footstaats.data.model.Lesion;
import com.example.footstaats.data.model.Partido;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private RadarChart radarChart;
    private TextView tvTotalGoles, tvTotalAsistencias, tvTotalPartidos;
    private TextView tvNombre, tvPosicion;
    private ImageView imgPerfil;
    private int usuarioId;

    private ActivityResultLauncher<Intent> perfilLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usuarioId = getIntent().getIntExtra("usuarioId", -1);
        String nombre = getIntent().getStringExtra("nombre");
        String posicion = getIntent().getStringExtra("posicion");
        String foto = getIntent().getStringExtra("foto");

        tvNombre = findViewById(R.id.tvNombre);
        tvPosicion = findViewById(R.id.tvPosicion);
        imgPerfil = findViewById(R.id.imgPerfil);
        tvTotalGoles = findViewById(R.id.tvTotalGoles);
        tvTotalAsistencias = findViewById(R.id.tvTotalAsistencias);
        tvTotalPartidos = findViewById(R.id.tvTotalPartidos);
        radarChart = findViewById(R.id.radarChart);

        tvNombre.setText(nombre);
        tvPosicion.setText(posicion);

        if (foto != null) {
            Glide.with(this).load(new File(foto)).circleCrop().into(imgPerfil);
        }

        registrarPerfilLauncher();

        LinearLayout headerPerfil = findViewById(R.id.headerPerfil);
        headerPerfil.setOnClickListener(v -> abrirPerfil());

        // Botones
        Button btnPartido = findViewById(R.id.btnRegistrarPartido);
        Button btnEntrenamiento = findViewById(R.id.btnRegistrarEntrenamiento);
        Button btnLesion = findViewById(R.id.btnRegistrarLesion);
        Button btnHistorial = findViewById(R.id.btnHistorial);

        btnPartido.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegistroPartidoActivity.class);
            intent.putExtra("usuarioId", usuarioId);
            startActivity(intent);
        });

        btnEntrenamiento.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegistroEntrenamientoActivity.class);
            intent.putExtra("usuarioId", usuarioId);
            startActivity(intent);
        });

        btnLesion.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegistroLesionActivity.class);
            intent.putExtra("usuarioId", usuarioId);
            startActivity(intent);
        });

        btnHistorial.setOnClickListener(v -> {
            Intent intent = new Intent(this, HistorialActivity.class);
            intent.putExtra("usuarioId", usuarioId);
            startActivity(intent);
        });

        cargarEstadisticas();
    }

    private void registrarPerfilLauncher() {
        perfilLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                resultado -> {
                    if (resultado.getResultCode() == RESULT_OK && resultado.getData() != null) {
                        String nuevoNombre = resultado.getData().getStringExtra("nombre");
                        String nuevaPosicion = resultado.getData().getStringExtra("posicion");
                        String nuevaFoto = resultado.getData().getStringExtra("foto");

                        tvNombre.setText(nuevoNombre);
                        tvPosicion.setText(nuevaPosicion);
                        if (nuevaFoto != null) {
                            Glide.with(this).load(new File(nuevaFoto)).circleCrop().into(imgPerfil);
                        }
                    }
                }
        );
    }

    private void abrirPerfil() {
        Intent intent = new Intent(this, PerfilActivity.class);
        intent.putExtra("usuarioId", usuarioId);
        perfilLauncher.launch(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarEstadisticas();
    }

    private void cargarEstadisticas() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            List<Partido> partidos = db.partidoDao().obtenerTodosSync(usuarioId);
            List<Entrenamiento> entrenamientos = db.entrenamientoDao().obtenerTodosSync(usuarioId);
            List<Lesion> lesiones = db.lesionDao().obtenerTodosSync(usuarioId);

            int totalGoles = 0, totalAsistencias = 0, totalPartidos = 0;
            int totalMinutos = 0, totalEntrenamientos = 0;

            if (partidos != null) {
                totalPartidos = partidos.size();
                for (Partido p : partidos) {
                    totalGoles += p.goles;
                    totalAsistencias += p.asistencias;
                    totalMinutos += p.minutosJugados;
                }
            }

            if (entrenamientos != null) {
                totalEntrenamientos = entrenamientos.size();
            }

            float golesN = Math.min(totalGoles, 10);
            float asistenciasN = Math.min(totalAsistencias, 10);
            float partidosN = Math.min(totalPartidos, 10);
            float minutosN = Math.min(totalMinutos / 90f, 10);
            float entrenamientosN = Math.min(totalEntrenamientos, 10);

            final int fg = totalGoles, fa = totalAsistencias, fp = totalPartidos;

            runOnUiThread(() -> {
                tvTotalGoles.setText(String.valueOf(fg));
                tvTotalAsistencias.setText(String.valueOf(fa));
                tvTotalPartidos.setText(String.valueOf(fp));
                configurarRadar(golesN, asistenciasN, partidosN, minutosN, entrenamientosN);
            });
        });
    }

    private void configurarRadar(float goles, float asistencias, float partidos, float minutos, float entrenamientos) {
        List<RadarEntry> entries = new ArrayList<>();
        entries.add(new RadarEntry(goles));
        entries.add(new RadarEntry(asistencias));
        entries.add(new RadarEntry(partidos));
        entries.add(new RadarEntry(minutos));
        entries.add(new RadarEntry(entrenamientos));

        RadarDataSet dataSet = new RadarDataSet(entries, "Rendimiento");
        dataSet.setColor(0xFF2E86C1);
        dataSet.setFillColor(0xFF2E86C1);
        dataSet.setDrawFilled(true);
        dataSet.setFillAlpha(180);
        dataSet.setLineWidth(2f);
        dataSet.setValueTextColor(0xFFFFFFFF);
        dataSet.setValueTextSize(10f);

        RadarData data = new RadarData(dataSet);
        radarChart.setData(data);

        String[] labels = {"Goles", "Asistencias", "Partidos", "Minutos", "Entrenos"};
        XAxis xAxis = radarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setTextColor(0xFFFFFFFF);
        xAxis.setTextSize(12f);

        radarChart.getYAxis().setTextColor(0xFFFFFFFF);
        radarChart.getYAxis().setAxisMinimum(0f);
        radarChart.getYAxis().setAxisMaximum(10f);
        radarChart.getLegend().setTextColor(0xFFFFFFFF);
        radarChart.setBackgroundColor(0x00000000);
        radarChart.getDescription().setEnabled(false);
        radarChart.setWebColor(0xFFFFFFFF);
        radarChart.setWebColorInner(0xFFAAAAAA);
        radarChart.animateXY(1000, 1000);
        radarChart.invalidate();
    }
}