package com.example.footstaats.data.model;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "entrenamientos")
public class Entrenamiento {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String fecha;
    public String tipo;        // "físico", "técnico", "táctico"
    public int duracionMinutos;
    public int golesEntrenamiento;
    public String descripcion;
    public int intensidad;     // 1 al 5
}
