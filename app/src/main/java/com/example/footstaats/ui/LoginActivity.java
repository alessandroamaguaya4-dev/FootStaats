package com.example.footstaats.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.footstaats.R;
import com.example.footstaats.data.db.AppDatabase;
import com.example.footstaats.data.model.Usuario;

import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private EditText etCorreo, etContrasena;
    private TextView tvErrorCorreo, tvErrorContrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etCorreo = findViewById(R.id.etCorreo);
        etContrasena = findViewById(R.id.etContrasena);
        tvErrorCorreo = findViewById(R.id.tvErrorCorreo);
        tvErrorContrasena = findViewById(R.id.tvErrorContrasena);

        Button btnEntrar = findViewById(R.id.btnEntrar);
        btnEntrar.setOnClickListener(v -> login());
    }

    private void login() {
        boolean valido = true;

        String correo = etCorreo.getText().toString().trim();
        String contrasena = etContrasena.getText().toString().trim();

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
            Usuario encontrado = db.usuarioDao().login(correo, contrasena);

            runOnUiThread(() -> {
                if (encontrado != null) {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("usuarioId", encontrado.id);
                    intent.putExtra("nombre", encontrado.nombre);
                    intent.putExtra("posicion", encontrado.posicion);
                    intent.putExtra("foto", encontrado.foto);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}