package com.example.footstaats;

import org.junit.Test;
import static org.junit.Assert.*;

public class ValidacionesTest {

    // ── validarCorreo() ──
    private boolean validarCorreo(String correo) {
        if (correo == null || correo.isEmpty()) return false;
        return correo.contains("@") && correo.contains(".")
                && !correo.contains(" ")
                && correo.indexOf("@") < correo.lastIndexOf(".")
                && correo.lastIndexOf(".") < correo.length() - 1;
    }

    // ── validarContrasena() ──
    private boolean validarContrasena(String contrasena) {
        if (contrasena == null || contrasena.isEmpty()) return false;
        return contrasena.length() >= 6;
    }

    // ── calcularRadar() ──
    private float calcularRadar(int valor, int maximo) {
        return Math.min(valor, maximo);
    }

    // ════ TESTS validarCorreo ════
    @Test
    public void correoValido_retornaTrue() {
        assertTrue(validarCorreo("aless@gmail.com"));
    }

    @Test
    public void correoSinArroba_retornaFalse() {
        assertFalse(validarCorreo("alessfootstaats"));
    }

    @Test
    public void correoSinExtension_retornaFalse() {
        assertFalse(validarCorreo("aless@gmail"));
    }

    @Test
    public void correoConEspacios_retornaFalse() {
        assertFalse(validarCorreo("aless Ama@gmail.com"));
    }

    @Test
    public void correoVacio_retornaFalse() {
        assertFalse(validarCorreo(""));
    }

    // ════ TESTS validarContrasena ════
    @Test
    public void contrasenaVacia_retornaFalse() {
        assertFalse(validarContrasena(""));
    }

    @Test
    public void contrasenaMenorA6_retornaFalse() {
        assertFalse(validarContrasena("abc1"));
    }

    @Test
    public void contrasenaExactamente6_retornaTrue() {
        assertTrue(validarContrasena("foot12"));
    }

    @Test
    public void contrasenaMayorA6_retornaTrue() {
        assertTrue(validarContrasena("footstaats123"));
    }

    // ════ TESTS calcularRadar ════
    @Test
    public void radar_conCeroRegistros_retornaCero() {
        assertEquals(0f, calcularRadar(0, 10), 0.01f);
    }

    @Test
    public void radar_con5Goles_retorna5() {
        assertEquals(5f, calcularRadar(5, 10), 0.01f);
    }

    @Test
    public void radar_superaMaximo_retornaMaximo() {
        assertEquals(10f, calcularRadar(15, 10), 0.01f);
    }
}