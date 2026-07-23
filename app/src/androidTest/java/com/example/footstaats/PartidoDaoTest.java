package com.example.footstaats;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.footstaats.data.dao.PartidoDao;
import com.example.footstaats.data.db.AppDatabase;
import com.example.footstaats.data.model.Partido;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class PartidoDaoTest {

    private AppDatabase db;
    private PartidoDao partidoDao;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        partidoDao = db.partidoDao();
    }

    @After
    public void tearDown() {
        db.close();
    }

    @Test
    public void insertar_partido_y_verificar() {
        Partido p = new Partido();
        p.usuarioId = 1;
        p.fecha = "15/07/2026";
        p.rival = "Equipo Rival";
        p.goles = 2;
        p.asistencias = 1;
        p.minutosJugados = 90;
        p.tarjetas = "ninguna";
        p.posicion = "Delantero";
        p.notas = "Buen partido";

        partidoDao.insertar(p);

        List<Partido> partidos = partidoDao.obtenerTodosSync(1);
        assertNotNull(partidos);
        assertEquals(1, partidos.size());
        assertEquals("Equipo Rival", partidos.get(0).rival);
        assertEquals(2, partidos.get(0).goles);
    }

    @Test
    public void filtrar_por_usuarioId() {
        // Usuario 1
        Partido p1 = new Partido();
        p1.usuarioId = 1;
        p1.fecha = "15/07/2026";
        p1.rival = "Equipo A";
        p1.goles = 1;
        partidoDao.insertar(p1);

        // Usuario 2
        Partido p2 = new Partido();
        p2.usuarioId = 2;
        p2.fecha = "16/07/2026";
        p2.rival = "Equipo B";
        p2.goles = 3;
        partidoDao.insertar(p2);

        List<Partido> partidosUsuario1 = partidoDao.obtenerTodosSync(1);
        assertEquals(1, partidosUsuario1.size());
        assertEquals("Equipo A", partidosUsuario1.get(0).rival);

        List<Partido> partidosUsuario2 = partidoDao.obtenerTodosSync(2);
        assertEquals(1, partidosUsuario2.size());
        assertEquals("Equipo B", partidosUsuario2.get(0).rival);
    }

    @Test
    public void eliminar_partido_verifica_lista_vacia() {
        Partido p = new Partido();
        p.usuarioId = 1;
        p.fecha = "15/07/2026";
        p.rival = "Equipo C";
        p.goles = 0;
        partidoDao.insertar(p);

        List<Partido> antes = partidoDao.obtenerTodosSync(1);
        assertEquals(1, antes.size());

        partidoDao.eliminar(antes.get(0).id);

        List<Partido> despues = partidoDao.obtenerTodosSync(1);
        assertEquals(0, despues.size());
    }
}