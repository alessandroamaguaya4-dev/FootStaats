package com.example.footstaats.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.footstaats.R;
import com.example.footstaats.data.model.Lesion;
import com.example.footstaats.repository.EstadisticasRepository;
import com.example.footstaats.util.NotificationHelper;
import com.example.footstaats.util.RecordatorioRecuperacionWorker;
import com.google.android.material.button.MaterialButton;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class RegistroLesionActivity extends AppCompatActivity {

    private AutoCompleteTextView etTipo, etParteCuerpo;
    private EditText etFecha, etDias, etDescripcion;
    private ImageButton btnCalendario;
    private MaterialButton btnGuardar;

    private int usuarioId;
    private int lesionId = -1;
    private EstadisticasRepository repository;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_lesion);

        repository = new EstadisticasRepository(getApplication());
        scrollView = findViewById(R.id.scrollView);

        // Obtención inicial del usuarioId
        usuarioId = getIntent().getIntExtra("usuarioId", -1);

        if (getIntent().hasExtra("lesionId")) {
            lesionId = getIntent().getIntExtra("lesionId", -1);
            // SOLUCIÓN AL BUG: Si venimos de la lista para editar, aseguramos el usuarioId
            // mapeándolo desde el extra alternativo para evitar que se pise con -1 o 0
            if (usuarioId == -1) {
                usuarioId = getIntent().getIntExtra("lesionUsuarioId", -1);
            }
        }

        etFecha = findViewById(R.id.etFecha);
        btnCalendario = findViewById(R.id.btnCalendario);
        etTipo = findViewById(R.id.etTipo);
        etParteCuerpo = findViewById(R.id.etParteCuerpo);
        etDias = findViewById(R.id.etDias);
        etDescripcion = findViewById(R.id.etDescripcion);
        btnGuardar = findViewById(R.id.btnGuardar);

        // Configuración de adaptadores
        String[] tiposLesiones = {"Esguince", "Desgarro Muscular", "Contusión", "Tendinitis", "Fractura", "Sobrecarga", "Otro"};
        ArrayAdapter<String> adapterTipoL = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, tiposLesiones);
        etTipo.setAdapter(adapterTipoL);

        String[] partesCuerpo = {"Tobillo", "Rodilla", "Muslo (Isquiotibiales)", "Muslo (Cuádriceps)", "Gemelo", "Pie", "Cadera", "Ingle", "Otro"};
        ArrayAdapter<String> adapterParte = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, partesCuerpo);
        etParteCuerpo.setAdapter(adapterParte);

        // Control de comportamiento al seleccionar "Otro" en el Tipo de Lesión
        etTipo.setOnItemClickListener((parent, view, position, id) -> {
            String seleccion = (String) parent.getItemAtPosition(position);
            if ("Otro".equals(seleccion)) {
                etTipo.setText("", false); // Evita filtrar la lista
                etTipo.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null) imm.showSoftInput(etTipo, InputMethodManager.SHOW_IMPLICIT);
                Toast.makeText(this, "Escribe el tipo de lesión manualmente", Toast.LENGTH_SHORT).show();
            }
        });

        // Control de comportamiento al seleccionar "Otro" en la Parte del Cuerpo
        etParteCuerpo.setOnItemClickListener((parent, view, position, id) -> {
            String seleccion = (String) parent.getItemAtPosition(position);
            if ("Otro".equals(seleccion)) {
                etParteCuerpo.setText("", false); // Evita filtrar la lista
                etParteCuerpo.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null) imm.showSoftInput(etParteCuerpo, InputMethodManager.SHOW_IMPLICIT);
                Toast.makeText(this, "Escribe la parte del cuerpo manualmente", Toast.LENGTH_SHORT).show();
            }
        });

        etDescripcion.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                scrollView.postDelayed(() -> scrollView.smoothScrollTo(0, etDescripcion.getBottom()), 200);
            }
        });
        btnCalendario.setOnClickListener(v -> mostrarDatePicker());
        etFecha.setOnClickListener(v -> mostrarDatePicker());

        if (lesionId != -1) {
            btnGuardar.setText("Actualizar Lesión");
            etFecha.setText(getIntent().getStringExtra("fecha"));
            etTipo.setText(getIntent().getStringExtra("tipo"), false);
            etParteCuerpo.setText(getIntent().getStringExtra("parteCuerpo"), false);
            etDias.setText(String.valueOf(getIntent().getIntExtra("diasRecuperacion", 0)));
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
        String parteCuerpo = etParteCuerpo.getText().toString().trim();

        if (fecha.isEmpty() || tipo.isEmpty() || parteCuerpo.isEmpty()) {
            Toast.makeText(this, "Fecha, tipo y parte del cuerpo son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        // VALIDACIÓN DE SEGURIDAD: Evita guardar con dueños corruptos (-1)
        if (usuarioId == -1) {
            Toast.makeText(this, "Error: No se detectó un usuario válido", Toast.LENGTH_SHORT).show();
            return;
        }

        Lesion l = new Lesion();
        l.usuarioId = usuarioId;
        l.fecha = fecha;
        l.tipo = tipo;
        l.parteCuerpo = parteCuerpo;
        l.descripcion = etDescripcion.getText().toString().trim();

        try {
            l.diasRecuperacion = etDias.getText().toString().isEmpty() ? 0 : Integer.parseInt(etDias.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        boolean esNueva = (lesionId == -1);

        if (esNueva) {
            repository.insertarLesion(l);
            Toast.makeText(this, "Lesión guardada", Toast.LENGTH_SHORT).show();
        } else {
            l.id = lesionId;
            repository.actualizarLesion(l);
            Toast.makeText(this, "Lesión actualizada", Toast.LENGTH_SHORT).show();
        }

        // Notificación de confirmación inmediata
        NotificationHelper.mostrarNotificacion(
                this,
                (int) System.currentTimeMillis(),
                esNueva ? "Lesión guardada" : "Lesión actualizada",
                "Registraste una lesión de " + parteCuerpo + " (" + tipo + ")"
        );

        // Recordatorio programado: solo si hay días de recuperación válidos
        if (esNueva && l.diasRecuperacion > 0) {
            programarRecordatorioRecuperacion(parteCuerpo, l.diasRecuperacion);
        }

        finish();
    }

    private void programarRecordatorioRecuperacion(String parteCuerpo, int diasRecuperacion) {
        Data datosEntrada = new Data.Builder()
                .putString(RecordatorioRecuperacionWorker.KEY_PARTE_CUERPO, parteCuerpo)
                .build();

        OneTimeWorkRequest solicitud = new OneTimeWorkRequest.Builder(RecordatorioRecuperacionWorker.class)
                .setInitialDelay(10, TimeUnit.SECONDS)
                .setInputData(datosEntrada)
                .build();

        WorkManager.getInstance(this).enqueue(solicitud);
    }
}