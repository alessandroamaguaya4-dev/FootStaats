package com.example.footstaats.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.footstaats.R;
import com.example.footstaats.data.model.Entrenamiento;
import com.example.footstaats.repository.EstadisticasRepository;
import com.google.android.material.button.MaterialButton;

import java.util.Calendar;

public class RegistroEntrenamientoActivity extends AppCompatActivity {

    private AutoCompleteTextView etTipo, etIntensidad;
    private EditText etFecha, etDuracion, etGoles, etDescripcion;
    private ImageButton btnCalendario;
    private MaterialButton btnGuardar;
    private ScrollView scrollView;

    private int usuarioId;
    private int entrenamientoId = -1;
    private EstadisticasRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_entrenamiento);

        repository = new EstadisticasRepository(getApplication());

        usuarioId = getIntent().getIntExtra("usuarioId", -1);

        if (getIntent().hasExtra("entrenamientoId")) {
            entrenamientoId = getIntent().getIntExtra("entrenamientoId", -1);
            if (usuarioId == -1) {
                usuarioId = getIntent().getIntExtra("entrenamientoUsuarioId", -1);
            }
        }

        scrollView = findViewById(R.id.scrollView);
        etFecha = findViewById(R.id.etFecha);
        btnCalendario = findViewById(R.id.btnCalendario);
        etTipo = findViewById(R.id.etTipo);
        etDuracion = findViewById(R.id.etDuracion);
        etGoles = findViewById(R.id.etGoles);
        etIntensidad = findViewById(R.id.etIntensidad);
        etDescripcion = findViewById(R.id.etDescripcion);
        btnGuardar = findViewById(R.id.btnGuardar);

        String[] tiposEntreno = {"Físico", "Táctico", "Técnico"};
        ArrayAdapter<String> adapterTipo = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, tiposEntreno);
        etTipo.setAdapter(adapterTipo);

        String[] intensidades = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        ArrayAdapter<String> adapterIntensidad = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, intensidades);
        etIntensidad.setAdapter(adapterIntensidad);

        btnCalendario.setOnClickListener(v -> mostrarDatePicker());
        etFecha.setOnClickListener(v -> mostrarDatePicker());

        // Fix real para Android 15+ (edge-to-edge): el sistema ya no redimensiona
        // la ventana con adjustResize, así que agregamos padding manual igual
        // a la altura del teclado (IME) cuando aparece, y hacemos scroll al campo con foco.
        ViewCompat.setOnApplyWindowInsetsListener(scrollView, (v, insets) -> {
            int alturaTeclado = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom;
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), alturaTeclado);

            if (alturaTeclado > 0) {
                v.postDelayed(() -> {
                    android.view.View focoActual = getCurrentFocus();
                    if (focoActual != null) {
                        v.scrollTo(0, focoActual.getBottom());
                    }
                }, 100);
            }
            return insets;
        });

        if (entrenamientoId != -1) {
            btnGuardar.setText("Actualizar Entrenamiento");
            etFecha.setText(getIntent().getStringExtra("fecha"));
            etTipo.setText(getIntent().getStringExtra("tipo"), false);
            etDuracion.setText(String.valueOf(getIntent().getIntExtra("duracion", 0)));
            etGoles.setText(String.valueOf(getIntent().getIntExtra("goles", 0)));
            etIntensidad.setText(String.valueOf(getIntent().getIntExtra("intensidad", 1)), false);
            etDescripcion.setText(getIntent().getStringExtra("descripcion"));
        }

        btnGuardar.setOnClickListener(v -> guardar());
    }

    private void mostrarDatePicker() {
        Calendar calendario = Calendar.getInstance();
        int anio = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String fecha = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year);
            etFecha.setText(fecha);
        }, anio, mes, dia).show();
    }

    private void guardar() {
        String fecha = etFecha.getText().toString().trim();
        String tipo = etTipo.getText().toString().trim();

        if (fecha.isEmpty() || tipo.isEmpty()) {
            Toast.makeText(this, "Fecha y tipo son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (usuarioId == -1) {
            Toast.makeText(this, "Error: No se detectó un usuario válido", Toast.LENGTH_SHORT).show();
            return;
        }

        Entrenamiento e = new Entrenamiento();
        e.usuarioId = usuarioId;
        e.fecha = fecha;
        e.tipo = tipo;
        e.descripcion = etDescripcion.getText().toString().trim();

        try {
            e.duracionMinutos = etDuracion.getText().toString().isEmpty() ? 0 : Integer.parseInt(etDuracion.getText().toString());
            e.golesEntrenamiento = etGoles.getText().toString().isEmpty() ? 0 : Integer.parseInt(etGoles.getText().toString());
            e.intensidad = etIntensidad.getText().toString().isEmpty() ? 1 : Integer.parseInt(etIntensidad.getText().toString());
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }

        if (entrenamientoId == -1) {
            repository.insertarEntrenamiento(e);
            Toast.makeText(this, "Entrenamiento guardado", Toast.LENGTH_SHORT).show();
        } else {
            e.id = entrenamientoId;
            repository.actualizarEntrenamiento(e);
            Toast.makeText(this, "Entrenamiento actualizado", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}