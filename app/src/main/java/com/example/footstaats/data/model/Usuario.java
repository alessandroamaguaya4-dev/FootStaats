package com.example.footstaats.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "usuarios")
public class Usuario {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String firebaseUid;
    public String nombre;
    public String correo;
    public String posicion;
    public String edad;
    public String foto;
}