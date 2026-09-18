package com.udea.Backend.Usuarios.Entities;

/**
 * Enum que define los roles estándar manejados por la plataforma.
 */
public enum TipoRol {
    CLIENTE("Cliente"),
    PROVEEDOR("Proveedor"),
    PROPIETARIO("Propietario");

    private final String nombre;

    TipoRol(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
