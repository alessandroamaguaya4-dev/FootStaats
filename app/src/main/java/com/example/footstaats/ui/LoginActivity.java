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
import com.example.footstaats.data.model.Usuario;
import com.example.footstaats.repository.UsuarioRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText etCorreo, etContrasena;
    private TextView tvErrorCorreo, tvErrorContrasena;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private UsuarioRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etCorreo = findViewById(R.id.etCorreo);
        etContrasena = findViewById(R.id.etContrasena);
        tvErrorCorreo = findViewById(R.id.tvErrorCorreo);
        tvErrorContrasena = findViewById(R.id.tvErrorContrasena);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        repository = new UsuarioRepository(getApplication());

        Button btnEntrar = findViewById(R.id.btnEntrar);
        btnEntrar.setOnClickListener(v -> login());
    }

    private void login() {
        boolean valido = true;

        String correo = etCorreo.getText().toString().trim().toLowerCase();
        String contrasena = etContrasena.getText().toString().trim();

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

        firebaseAuth.signInWithEmailAndPassword(correo, contrasena)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser == null) {
                        Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    cargarPerfilYEntrar(firebaseUser.getUid());
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show());
    }

    private void cargarPerfilYEntrar(String uid) {
        firestore.collection("usuarios").document(uid).get()
                .addOnSuccessListener(this::procesarPerfil)
                .addOnFailureListener(e ->
                        Toast.makeText(this, "No se pudo cargar el perfil: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void procesarPerfil(DocumentSnapshot doc) {
        if (!doc.exists()) {
            Toast.makeText(this, "Perfil no encontrado", Toast.LENGTH_SHORT).show();
            return;
        }

        Usuario usuario = new Usuario();
        usuario.firebaseUid = doc.getId();
        usuario.nombre = doc.getString("nombre");
        usuario.correo = doc.getString("correo");
        usuario.posicion = doc.getString("posicion");
        usuario.edad = doc.getString("edad");
        usuario.foto = doc.getString("foto");

        repository.guardarOActualizar(usuario, guardado -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("usuarioId", guardado.id);
            intent.putExtra("nombre", guardado.nombre);
            intent.putExtra("posicion", guardado.posicion);
            intent.putExtra("foto", guardado.foto);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}