package com.example.footstaats.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "partidos")
public class Partido {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int usuarioId;
    public String fecha;
    public String rival;
    public int goles;
    public int asistencias;
    public int minutosJugados;
    public String tarjetas;
    public String posicion;
    public String notas;
}