package com.example.footstaats.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.footstaats.R;
import com.example.footstaats.data.db.AppDatabase;
import com.example.footstaats.data.model.Entrenamiento;

import java.util.concurrent.Executors;

public class RegistroEntrenamientoActivity extends AppCompatActivity {

    private EditText etFecha, etTipo, etDuracion, etGoles, etIntensidad, etDescripcion;
    private int usuarioId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_entrenamiento);

        usuarioId = getIntent().getIntExtra("usuarioId", -1);

        etFecha = findViewById(R.id.etFecha);
        etTipo = findViewById(R.id.etTipo);
        etDuracion = findViewById(R.id.etDuracion);
        etGoles = findViewById(R.id.etGoles);
        etIntensidad = findViewById(R.id.etIntensidad);
        etDescripcion = findViewById(R.id.etDescripcion);

        Button btnGuardar = findViewById(R.id.btnGuardar);
        btnGuardar.setOnClickListener(v -> guardar());
    }

    private void guardar() {
        String fecha = etFecha.getText().toString().trim();
        String tipo = etTipo.getText().toString().trim();

        if (fecha.isEmpty() || tipo.isEmpty()) {
            Toast.makeText(this, "Fecha y tipo son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        Entrenamiento e = new Entrenamiento();
        e.fecha = fecha;
        e.tipo = tipo;
        e.duracionMinutos = etDuracion.getText().toString().isEmpty() ? 0 : Integer.parseInt(etDuracion.getText().toString());
        e.golesEntrenamiento = etGoles.getText().toString().isEmpty() ? 0 : Integer.parseInt(etGoles.getText().toString());
        e.intensidad = etIntensidad.getText().toString().isEmpty() ? 1 : Integer.parseInt(etIntensidad.getText().toString());
        e.descripcion = etDescripcion.getText().toString().trim();

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase.getInstance(this).entrenamientoDao().insertar(e);
            runOnUiThread(() -> {
                Toast.makeText(this, "Entrenamiento guardado", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }
}