package com.example.footstaats.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.footstaats.R;
import com.example.footstaats.data.db.AppDatabase;
import com.example.footstaats.data.model.Partido;

import java.util.concurrent.Executors;

public class RegistroPartidoActivity extends AppCompatActivity {

    private EditText etFecha, etRival, etGoles, etAsistencias, etMinutos, etTarjetas, etPosicion, etNotas;
    private int usuarioId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_partido);

        usuarioId = getIntent().getIntExtra("usuarioId", -1);

        etFecha = findViewById(R.id.etFecha);
        etRival = findViewById(R.id.etRival);
        etGoles = findViewById(R.id.etGoles);
        etAsistencias = findViewById(R.id.etAsistencias);
        etMinutos = findViewById(R.id.etMinutos);
        etTarjetas = findViewById(R.id.etTarjetas);
        etPosicion = findViewById(R.id.etPosicion);
        etNotas = findViewById(R.id.etNotas);

        Button btnGuardar = findViewById(R.id.btnGuardar);
        btnGuardar.setOnClickListener(v -> guardar());
    }

    private void guardar() {
        String fecha = etFecha.getText().toString().trim();
        String rival = etRival.getText().toString().trim();

        if (fecha.isEmpty() || rival.isEmpty()) {
            Toast.makeText(this, "Fecha y rival son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        Partido p = new Partido();
        p.fecha = fecha;
        p.rival = rival;
        p.goles = etGoles.getText().toString().isEmpty() ? 0 : Integer.parseInt(etGoles.getText().toString());
        p.asistencias = etAsistencias.getText().toString().isEmpty() ? 0 : Integer.parseInt(etAsistencias.getText().toString());
        p.minutosJugados = etMinutos.getText().toString().isEmpty() ? 0 : Integer.parseInt(etMinutos.getText().toString());
        p.tarjetas = etTarjetas.getText().toString().trim();
        p.posicion = etPosicion.getText().toString().trim();
        p.notas = etNotas.getText().toString().trim();

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase.getInstance(this).partidoDao().insertar(p);
            runOnUiThread(() -> {
                Toast.makeText(this, "Partido guardado", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }
}