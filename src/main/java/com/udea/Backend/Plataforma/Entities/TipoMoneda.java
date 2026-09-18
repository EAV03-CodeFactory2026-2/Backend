package com.udea.Backend.Plataforma.Entities;

/**
 * Enum que define las monedas estándar precargadas en la plataforma.
 * El nombre de la constante (ej. COP) sirve como el código ISO.
 */
public enum TipoMoneda {
    COP("Peso Colombiano"),
    USD("Dólar Estadounidense"),
    EUR("Euro"),
    MXN("Peso Mexicano"),
    ARS("Peso Argentino"),
    PEN("Sol Peruano"),
    CLP("Peso Chileno");

    private final String nombre;

    TipoMoneda(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
