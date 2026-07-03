package com.example.footstaats.data.model;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "lesiones")
public class Lesion {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String fecha;
    public String tipo;        // "muscular", "ligamento", "hueso", "otra"
    public String parteCuerpo; // "rodilla", "tobillo", "muslo", etc.
    public int diasRecuperacion;
    public String descripcion;
}
