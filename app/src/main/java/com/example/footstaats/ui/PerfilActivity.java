package com.example.footstaats.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.example.footstaats.R;
import com.example.footstaats.data.model.Usuario;
import com.example.footstaats.repository.UsuarioRepository;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class PerfilActivity extends AppCompatActivity {

    private ImageButton btnAtras;
    private ImageView imgFotoPerfil;
    private EditText etNombre, etEdad;
    private AutoCompleteTextView etPosicion;
    private MaterialButton btnGuardar, btnCerrarSesion;

    private int usuarioId;
    private Usuario usuarioActual;

    private UsuarioRepository repository;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    private Uri fotoUriTemp;
    private String fotoGuardadaPath; // null hasta que el usuario elija una nueva foto

    private ActivityResultLauncher<Uri> cameraLauncher;
    private ActivityResultLauncher<String> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        usuarioId = getIntent().getIntExtra("usuarioId", -1);

        btnAtras = findViewById(R.id.btnAtras);
        imgFotoPerfil = findViewById(R.id.imgFotoPerfil);
        etNombre = findViewById(R.id.etNombre);
        etPosicion = findViewById(R.id.etPosicion);
        etEdad = findViewById(R.id.etEdad);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        repository = new UsuarioRepository(getApplication());
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        String[] posiciones = {"Portero", "Defensa", "Mediocampista", "Delantero"};
        ArrayAdapter<String> adapterPosicion = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, posiciones);
        etPosicion.setAdapter(adapterPosicion);

        registrarLaunchers();

        btnAtras.setOnClickListener(v -> finish());
        imgFotoPerfil.setOnClickListener(v -> mostrarSelectorFoto());
        btnGuardar.setOnClickListener(v -> guardarCambios());
        btnCerrarSesion.setOnClickListener(v -> confirmarCerrarSesion());

        cargarDatosActuales();
    }

    private void cargarDatosActuales() {
        if (usuarioId == -1) {
            Toast.makeText(this, "Error: usuario no válido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        repository.obtenerPorId(usuarioId, usuario -> {
            if (usuario == null) {
                Toast.makeText(this, "No se encontró el perfil", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            usuarioActual = usuario;
            etNombre.setText(usuario.nombre);
            etPosicion.setText(usuario.posicion, false);
            etEdad.setText(usuario.edad);

            if (usuario.foto != null) {
                Glide.with(this).load(new File(usuario.foto)).circleCrop().into(imgFotoPerfil);
            }
        });
    }

    private void registrarLaunchers() {
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), exito -> {
            if (exito) {
                imgFotoPerfil.setImageURI(fotoUriTemp);
                fotoGuardadaPath = fotoUriTemp.getPath();
            } else {
                Toast.makeText(this, "No se pudo tomar la foto", Toast.LENGTH_SHORT).show();
            }
        });

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                String rutaInterna = copiarImagenAInterno(uri);
                if (rutaInterna != null) {
                    fotoGuardadaPath = rutaInterna;
                    imgFotoPerfil.setImageURI(Uri.fromFile(new File(rutaInterna)));
                } else {
                    Toast.makeText(this, "No se pudo procesar la imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void mostrarSelectorFoto() {
        String[] opciones = {"Cámara", "Galería"};
        new AlertDialog.Builder(this)
                .setTitle("Foto de perfil")
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) {
                        abrirCamara();
                    } else {
                        galleryLauncher.launch("image/*");
                    }
                })
                .show();
    }

    private void abrirCamara() {
        try {
            File archivo = new File(getFilesDir(), "perfil_" + System.currentTimeMillis() + ".jpg");
            fotoUriTemp = FileProvider.getUriForFile(this, "com.example.footstaats.fileprovider", archivo);
            cameraLauncher.launch(fotoUriTemp);
        } catch (Exception e) {
            Toast.makeText(this, "No se pudo abrir la cámara", Toast.LENGTH_SHORT).show();
        }
    }

    private String copiarImagenAInterno(Uri origen) {
        try {
            InputStream input = getContentResolver().openInputStream(origen);
            File destino = new File(getFilesDir(), "perfil_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream output = new FileOutputStream(destino);

            byte[] buffer = new byte[4096];
            int bytesLeidos;
            while (input != null && (bytesLeidos = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesLeidos);
            }
            output.close();
            if (input != null) input.close();

            return destino.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void guardarCambios() {
        if (usuarioActual == null) return;

        String nombre = etNombre.getText().toString().trim();
        String posicion = etPosicion.getText().toString().trim();
        String edad = etEdad.getText().toString().trim();

        if (nombre.isEmpty() || posicion.isEmpty()) {
            Toast.makeText(this, "Nombre y posición son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = firebaseAuth.getCurrentUser() != null ? firebaseAuth.getCurrentUser().getUid() : usuarioActual.firebaseUid;
        String fotoFinal = fotoGuardadaPath != null ? fotoGuardadaPath : usuarioActual.foto;

        btnGuardar.setEnabled(false);

        Map<String, Object> cambios = new HashMap<>();
        cambios.put("nombre", nombre);
        cambios.put("posicion", posicion);
        cambios.put("edad", edad);
        cambios.put("foto", fotoFinal);

        firestore.collection("usuarios").document(uid)
                .update(cambios)
                .addOnSuccessListener(unused -> {
                    Usuario actualizado = new Usuario();
                    actualizado.firebaseUid = uid;
                    actualizado.nombre = nombre;
                    actualizado.correo = usuarioActual.correo;
                    actualizado.posicion = posicion;
                    actualizado.edad = edad;
                    actualizado.foto = fotoFinal;

                    repository.guardarOActualizar(actualizado, guardado -> {
                        Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show();

                        Intent resultado = new Intent();
                        resultado.putExtra("nombre", guardado.nombre);
                        resultado.putExtra("posicion", guardado.posicion);
                        resultado.putExtra("foto", guardado.foto);
                        setResult(RESULT_OK, resultado);
                        finish();
                    });
                })
                .addOnFailureListener(e -> {
                    btnGuardar.setEnabled(true);
                    Toast.makeText(this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void confirmarCerrarSesion() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Deseas cerrar sesión?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    firebaseAuth.signOut();
                    Intent intent = new Intent(this, SplashActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}