package com.udea.Backend.Usuarios.Entities;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que representa la tabla 'rol' en la base de datos.
 */
@Entity
@Table(name = "rol")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Integer idRol;

    @Column(name = "nombre_rol", nullable = false, unique = true, length = 30)
    private String nombreRol;

}
