package com.example.footstaats.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "lesiones")
public class Lesion {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int usuarioId;
    public String fecha;
    public String tipo;
    public String parteCuerpo;
    public int diasRecuperacion;
    public String descripcion;
}