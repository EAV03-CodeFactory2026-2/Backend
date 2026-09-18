package com.udea.Backend.CatalogoServicios.Entities;

/**
 * Enum que define las modalidades de servicio predefinidas por el sistema.
 */
public enum TipoModalidad {
    PRESENCIAL("Presencial"),
    VIRTUAL("Virtual");

    private final String nombre;

    TipoModalidad(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
