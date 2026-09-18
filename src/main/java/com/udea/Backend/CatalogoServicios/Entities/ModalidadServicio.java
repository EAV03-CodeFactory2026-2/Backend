package com.udea.Backend.CatalogoServicios.Entities;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que representa la tabla 'modalidad_servicio' en la base de datos.
 * Define las modalidades disponibles para los servicios (Presencial, Virtual).
 */
@Entity
@Table(name = "modalidad_servicio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModalidadServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_modalidad")
    private Integer idModalidad;

    @Column(name = "nombre_modalidad", nullable = false, unique = true, length = 20)
    private String nombreModalidad;
}
