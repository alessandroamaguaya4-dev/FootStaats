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
import com.example.footstaats.data.model.Partido;
import com.example.footstaats.repository.EstadisticasRepository;
import com.google.android.material.button.MaterialButton;

import java.util.Calendar;

public class RegistroPartidoActivity extends AppCompatActivity {

    private AutoCompleteTextView etTarjetas, etPosicion;
    private EditText etFecha, etRival, etGoles, etAsistencias, etMinutos, etNotas;
    private ImageButton btnCalendario;
    private MaterialButton btnGuardar;
    private ScrollView scrollView;

    private int usuarioId;
    private int partidoId = -1;
    private EstadisticasRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_partido);

        repository = new EstadisticasRepository(getApplication());

        usuarioId = getIntent().getIntExtra("usuarioId", -1);

        if (getIntent().hasExtra("partidoId")) {
            partidoId = getIntent().getIntExtra("partidoId", -1);
            if (usuarioId == -1) {
                usuarioId = getIntent().getIntExtra("partidoUsuarioId", -1);
            }
        }

        scrollView = findViewById(R.id.scrollView);
        etFecha = findViewById(R.id.etFecha);
        btnCalendario = findViewById(R.id.btnCalendario);
        etRival = findViewById(R.id.etRival);
        etGoles = findViewById(R.id.etGoles);
        etAsistencias = findViewById(R.id.etAsistencias);
        etMinutos = findViewById(R.id.etMinutos);
        etTarjetas = findViewById(R.id.etTarjetas);
        etPosicion = findViewById(R.id.etPosicion);
        etNotas = findViewById(R.id.etNotas);
        btnGuardar = findViewById(R.id.btnGuardar);

        String[] opcionesTarjetas = {"Ninguna", "Amarilla", "Doble Amarilla", "Roja"};
        ArrayAdapter<String> adapterTarjetas = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, opcionesTarjetas);
        etTarjetas.setAdapter(adapterTarjetas);

        String[] posiciones = {"Portero", "Defensa", "Mediocampista", "Delantero"};
        ArrayAdapter<String> adapterPosicion = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, posiciones);
        etPosicion.setAdapter(adapterPosicion);

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
                        int[] posicionEnPantalla = new int[2];
                        focoActual.getLocationInWindow(posicionEnPantalla);
                        v.scrollTo(0, focoActual.getBottom());
                    }
                }, 100);
            }
            return insets;
        });

        if (partidoId != -1) {
            btnGuardar.setText("Actualizar Partido");
            etFecha.setText(getIntent().getStringExtra("fecha"));
            etRival.setText(getIntent().getStringExtra("rival"));
            etGoles.setText(String.valueOf(getIntent().getIntExtra("goles", 0)));
            etAsistencias.setText(String.valueOf(getIntent().getIntExtra("asistencias", 0)));
            etMinutos.setText(String.valueOf(getIntent().getIntExtra("minutosJugados", 0)));
            etTarjetas.setText(getIntent().getStringExtra("tarjetas"), false);
            etPosicion.setText(getIntent().getStringExtra("posicion"), false);
            etNotas.setText(getIntent().getStringExtra("notas"));
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
        String rival = etRival.getText().toString().trim();

        if (fecha.isEmpty() || rival.isEmpty()) {
            Toast.makeText(this, "Fecha y rival son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (usuarioId == -1) {
            Toast.makeText(this, "Error: No se detectó un usuario válido", Toast.LENGTH_SHORT).show();
            return;
        }

        Partido p = new Partido();
        p.usuarioId = usuarioId;
        p.fecha = fecha;
        p.rival = rival;
        p.tarjetas = etTarjetas.getText().toString().trim();
        p.posicion = etPosicion.getText().toString().trim();
        p.notas = etNotas.getText().toString().trim();

        try {
            p.goles = etGoles.getText().toString().isEmpty() ? 0 : Integer.parseInt(etGoles.getText().toString());
            p.asistencias = etAsistencias.getText().toString().isEmpty() ? 0 : Integer.parseInt(etAsistencias.getText().toString());
            p.minutosJugados = etMinutos.getText().toString().isEmpty() ? 0 : Integer.parseInt(etMinutos.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (partidoId == -1) {
            repository.insertarPartido(p);
            Toast.makeText(this, "Partido guardado", Toast.LENGTH_SHORT).show();
        } else {
            p.id = partidoId;
            repository.actualizarPartido(p);
            Toast.makeText(this, "Partido actualizado", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}