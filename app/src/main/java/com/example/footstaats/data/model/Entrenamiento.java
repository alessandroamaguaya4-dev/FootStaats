package com.example.footstaats.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "entrenamientos")
public class Entrenamiento {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int usuarioId;
    public String fecha;
    public String tipo;
    public int duracionMinutos;
    public int golesEntrenamiento;
    public String descripcion;
    public int intensidad;
}