package com.example.footstaats.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.footstaats.R;
import com.example.footstaats.data.db.AppDatabase;
import com.example.footstaats.data.model.Usuario;

import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {

    private ImageView imgFotoPerfil;
    private EditText etNombre, etCorreo, etConfirmarCorreo, etContrasena, etPosicion, etEdad;
    private TextView tvErrorNombre, tvErrorCorreo, tvErrorConfirmarCorreo, tvErrorContrasena;
    private Uri fotoUri;
    private ActivityResultLauncher<String> seleccionarFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        imgFotoPerfil = findViewById(R.id.imgFotoPerfil);
        etNombre = findViewById(R.id.etNombre);
        etCorreo = findViewById(R.id.etCorreo);
        etConfirmarCorreo = findViewById(R.id.etConfirmarCorreo);
        etContrasena = findViewById(R.id.etContrasena);
        etPosicion = findViewById(R.id.etPosicion);
        etEdad = findViewById(R.id.etEdad);
        tvErrorNombre = findViewById(R.id.tvErrorNombre);
        tvErrorCorreo = findViewById(R.id.tvErrorCorreo);
        tvErrorConfirmarCorreo = findViewById(R.id.tvErrorConfirmarCorreo);
        tvErrorContrasena = findViewById(R.id.tvErrorContrasena);

        seleccionarFoto = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        fotoUri = uri;
                        imgFotoPerfil.setImageURI(uri);
                    }
                });

        imgFotoPerfil.setOnClickListener(v -> seleccionarFoto.launch("image/*"));
        findViewById(R.id.btnGuardar).setOnClickListener(v -> guardarUsuario());
    }

    private void guardarUsuario() {
        boolean valido = true;

        String nombre = etNombre.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String confirmarCorreo = etConfirmarCorreo.getText().toString().trim();
        String contrasena = etContrasena.getText().toString().trim();
        String posicion = etPosicion.getText().toString().trim();
        String edad = etEdad.getText().toString().trim();

        // Validar nombre
        if (nombre.isEmpty()) {
            tvErrorNombre.setText("Ingresa tu nombre");
            tvErrorNombre.setVisibility(View.VISIBLE);
            valido = false;
        } else {
            tvErrorNombre.setVisibility(View.GONE);
        }

        // Validar correo
        if (correo.isEmpty()) {
            tvErrorCorreo.setText("Ingresa tu correo");
            tvErrorCorreo.setVisibility(View.VISIBLE);
            valido = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            tvErrorCorreo.setText("Ingresa un correo válido");
            tvErrorCorreo.setVisibility(View.VISIBLE);
            valido = false;
        } else {
            tvErrorCorreo.setVisibility(View.GONE);
        }

        // Validar confirmar correo
        if (!correo.equals(confirmarCorreo)) {
            tvErrorConfirmarCorreo.setText("Los correos no coinciden");
            tvErrorConfirmarCorreo.setVisibility(View.VISIBLE);
            valido = false;
        } else {
            tvErrorConfirmarCorreo.setVisibility(View.GONE);
        }

        // Validar contraseña
        if (contrasena.isEmpty()) {
            tvErrorContrasena.setText("Ingresa tu contraseña");
            tvErrorContrasena.setVisibility(View.VISIBLE);
            valido = false;
        } else if (contrasena.length() < 6) {
            tvErrorContrasena.setText("Mínimo 6 caracteres");
            tvErrorContrasena.setVisibility(View.VISIBLE);
            valido = false;
        } else {
            tvErrorContrasena.setVisibility(View.GONE);
        }

        if (!valido) return;

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            Usuario existente = db.usuarioDao().buscarPorCorreo(correo);

            if (existente != null) {
                runOnUiThread(() -> {
                    tvErrorCorreo.setText("Este correo ya está registrado");
                    tvErrorCorreo.setVisibility(View.VISIBLE);
                });
                return;
            }

            Usuario nuevo = new Usuario();
            nuevo.nombre = nombre;
            nuevo.correo = correo;
            nuevo.contrasena = contrasena;
            nuevo.posicion = posicion;
            nuevo.edad = edad;
            nuevo.foto = fotoUri != null ? fotoUri.toString() : null;

            db.usuarioDao().insertar(nuevo);
            Usuario creado = db.usuarioDao().buscarPorCorreo(correo);

            runOnUiThread(() -> {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });
        });
    }
}