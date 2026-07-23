package com.example.footstaats.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.footstaats.R;
import com.example.footstaats.data.model.Entrenamiento;
import com.example.footstaats.data.model.Lesion;
import com.example.footstaats.data.model.Partido;
import com.example.footstaats.repository.EstadisticasRepository;

import java.util.List;

public class HistorialActivity extends AppCompatActivity {

    private TextView tvHistorial;
    private EstadisticasRepository repository;
    private List<Partido> listaPartidos;
    private List<Entrenamiento> listaEntrenamientos;
    private List<Lesion> listaLesiones;
    private int usuarioId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        usuarioId = getIntent().getIntExtra("usuarioId", -1);
        repository = new EstadisticasRepository(getApplication());
        tvHistorial = findViewById(R.id.tvHistorial);
        tvHistorial.setText("Cargando...");

        tvHistorial.setOnLongClickListener(v -> {
            mostrarMenuPrincipal();
            return true;
        });

        observarDatos();
    }

    private void observarDatos() {
        repository.obtenerPartidos(usuarioId).observe(this, partidos -> {
            listaPartidos = partidos;
            renderizar();
        });

        repository.obtenerEntrenamientos(usuarioId).observe(this, entrenamientos -> {
            listaEntrenamientos = entrenamientos;
            renderizar();
        });

        repository.obtenerLesiones(usuarioId).observe(this, lesiones -> {
            listaLesiones = lesiones;
            renderizar();
        });
    }

    private void renderizar() {
        StringBuilder sb = new StringBuilder();
        sb.append("💡 [MANTÉN PRESIONADA LA PANTALLA PARA EDITAR O ELIMINAR]\n\n");

        sb.append("═══ PARTIDOS ═══\n\n");
        if (listaPartidos != null && !listaPartidos.isEmpty()) {
            for (int i = 0; i < listaPartidos.size(); i++) {
                Partido p = listaPartidos.get(i);
                sb.append("Nº [").append(i).append("] 📅 ").append(p.fecha).append("\n");
                sb.append("⚽ vs ").append(p.rival).append("\n");
                sb.append("Goles: ").append(p.goles).append(" | Asistencias: ").append(p.asistencias).append("\n");
                sb.append("Minutos: ").append(p.minutosJugados).append("\n\n");
            }
        } else {
            sb.append("Sin partidos registrados\n\n");
        }

        sb.append("═══ ENTRENAMIENTOS ═══\n\n");
        if (listaEntrenamientos != null && !listaEntrenamientos.isEmpty()) {
            for (int i = 0; i < listaEntrenamientos.size(); i++) {
                Entrenamiento e = listaEntrenamientos.get(i);
                sb.append("Nº [").append(i).append("] 📅 ").append(e.fecha).append("\n");
                sb.append("Tipo: ").append(e.tipo).append("\n");
                sb.append("Duración: ").append(e.duracionMinutos).append(" min | Intensidad: ").append(e.intensidad).append("/10\n\n");
            }
        } else {
            sb.append("Sin entrenamientos registrados\n\n");
        }

        sb.append("═══ LESIONES ═══\n\n");
        if (listaLesiones != null && !listaLesiones.isEmpty()) {
            for (int i = 0; i < listaLesiones.size(); i++) {
                Lesion l = listaLesiones.get(i);
                sb.append("Nº [").append(i).append("] 📅 ").append(l.fecha).append("\n");
                sb.append("Tipo: ").append(l.tipo).append("\n");
                sb.append("Parte: ").append(l.parteCuerpo).append("\n");
                sb.append("Recuperación: ").append(l.diasRecuperacion).append(" días\n\n");
            }
        } else {
            sb.append("Sin lesiones registradas\n\n");
        }

        tvHistorial.setText(sb.toString());
    }

    private void mostrarMenuPrincipal() {
        String[] categorias = {"Partidos", "Entrenamientos", "Lesiones"};
        new AlertDialog.Builder(this)
                .setTitle("¿Qué deseas gestionar?")
                .setItems(categorias, (dialog, which) -> {
                    switch (which) {
                        case 0: mostrarOpcionesCRUD("partido"); break;
                        case 1: mostrarOpcionesCRUD("entrenamiento"); break;
                        case 2: mostrarOpcionesCRUD("lesion"); break;
                    }
                })
                .show();
    }

    private void mostrarOpcionesCRUD(String tipo) {
        List<?> lista = tipo.equals("partido") ? listaPartidos :
                tipo.equals("entrenamiento") ? listaEntrenamientos : listaLesiones;

        if (lista == null || lista.isEmpty()) {
            Toast.makeText(this, "No hay registros de " + tipo, Toast.LENGTH_SHORT).show();
            return;
        }

        String[] opciones = {"Modificar", "Eliminar"};
        new AlertDialog.Builder(this)
                .setTitle("Gestión de " + tipo)
                .setItems(opciones, (dialog, which) -> solicitarIndice(tipo, which == 0))
                .show();
    }

    private void solicitarIndice(String tipo, boolean esEditar) {
        final EditText input = new EditText(this);
        input.setHint("Ej. 0");

        new AlertDialog.Builder(this)
                .setTitle("Ingrese el número Nº del registro")
                .setView(input)
                .setPositiveButton("Confirmar", (dialog, which) -> {
                    String txt = input.getText().toString().trim();
                    if (txt.isEmpty()) return;
                    try {
                        int index = Integer.parseInt(txt);
                        procesarAccion(tipo, index, esEditar);
                    } catch (NumberFormatException ex) {
                        Toast.makeText(this, "Formato numérico inválido", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void procesarAccion(String tipo, int index, boolean esEditar) {
        switch (tipo) {
            case "partido":
                if (listaPartidos == null || index < 0 || index >= listaPartidos.size()) {
                    Toast.makeText(this, "Índice inválido", Toast.LENGTH_SHORT).show();
                    return;
                }
                Partido p = listaPartidos.get(index);
                if (esEditar) {
                    Intent ip = new Intent(this, RegistroPartidoActivity.class);
                    ip.putExtra("partidoId", p.id);
                    ip.putExtra("partidoUsuarioId", p.usuarioId);
                    ip.putExtra("usuarioId", usuarioId);
                    ip.putExtra("fecha", p.fecha);
                    ip.putExtra("rival", p.rival);
                    ip.putExtra("goles", p.goles);
                    ip.putExtra("asistencias", p.asistencias);
                    ip.putExtra("minutosJugados", p.minutosJugados);
                    ip.putExtra("tarjetas", p.tarjetas);
                    ip.putExtra("posicion", p.posicion);
                    ip.putExtra("notas", p.notas);
                    startActivity(ip);
                } else {
                    confirmarEliminacion("partido del " + p.fecha, () -> repository.eliminarPartido(p));
                }
                break;

            case "entrenamiento":
                if (listaEntrenamientos == null || index < 0 || index >= listaEntrenamientos.size()) {
                    Toast.makeText(this, "Índice inválido", Toast.LENGTH_SHORT).show();
                    return;
                }
                Entrenamiento e = listaEntrenamientos.get(index);
                if (esEditar) {
                    Intent ie = new Intent(this, RegistroEntrenamientoActivity.class);
                    ie.putExtra("entrenamientoId", e.id);
                    ie.putExtra("entrenamientoUsuarioId", e.usuarioId);
                    ie.putExtra("usuarioId", usuarioId);
                    ie.putExtra("fecha", e.fecha);
                    ie.putExtra("tipo", e.tipo);
                    ie.putExtra("duracion", e.duracionMinutos);
                    ie.putExtra("goles", e.golesEntrenamiento);
                    ie.putExtra("intensidad", e.intensidad);
                    ie.putExtra("descripcion", e.descripcion);
                    startActivity(ie);
                } else {
                    confirmarEliminacion("entrenamiento del " + e.fecha, () -> repository.eliminarEntrenamiento(e));
                }
                break;

            case "lesion":
                if (listaLesiones == null || index < 0 || index >= listaLesiones.size()) {
                    Toast.makeText(this, "Índice inválido", Toast.LENGTH_SHORT).show();
                    return;
                }
                Lesion l = listaLesiones.get(index);
                if (esEditar) {
                    Intent il = new Intent(this, RegistroLesionActivity.class);
                    il.putExtra("lesionId", l.id);
                    il.putExtra("lesionUsuarioId", l.usuarioId);
                    il.putExtra("usuarioId", usuarioId);
                    il.putExtra("fecha", l.fecha);
                    il.putExtra("tipo", l.tipo);
                    il.putExtra("parteCuerpo", l.parteCuerpo);
                    il.putExtra("diasRecuperacion", l.diasRecuperacion);
                    il.putExtra("descripcion", l.descripcion);
                    startActivity(il);
                } else {
                    confirmarEliminacion("lesión del " + l.fecha, () -> repository.eliminarLesion(l));
                }
                break;
        }
    }

    private void confirmarEliminacion(String descripcion, Runnable accion) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Eliminar permanentemente el " + descripcion + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    accion.run();
                    Toast.makeText(this, "Registro eliminado", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}