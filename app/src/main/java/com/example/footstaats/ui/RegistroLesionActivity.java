package com.example.footstaats.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.footstaats.R;
import com.example.footstaats.data.db.AppDatabase;
import com.example.footstaats.data.model.Lesion;

import java.util.concurrent.Executors;

public class RegistroLesionActivity extends AppCompatActivity {

    private EditText etFecha, etTipo, etParteCuerpo, etDias, etDescripcion;
    private int usuarioId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_lesion);

        usuarioId = getIntent().getIntExtra("usuarioId", -1);

        etFecha = findViewById(R.id.etFecha);
        etTipo = findViewById(R.id.etTipo);
        etParteCuerpo = findViewById(R.id.etParteCuerpo);
        etDias = findViewById(R.id.etDias);
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

        Lesion l = new Lesion();
        l.fecha = fecha;
        l.tipo = tipo;
        l.parteCuerpo = etParteCuerpo.getText().toString().trim();
        l.diasRecuperacion = etDias.getText().toString().isEmpty() ? 0 : Integer.parseInt(etDias.getText().toString());
        l.descripcion = etDescripcion.getText().toString().trim();

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase.getInstance(this).lesionDao().insertar(l);
            runOnUiThread(() -> {
                Toast.makeText(this, "Lesión guardada", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }
}