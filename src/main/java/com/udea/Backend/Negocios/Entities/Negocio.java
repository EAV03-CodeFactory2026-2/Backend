package com.udea.Backend.Negocios.Entities;

import com.udea.Backend.Plataforma.Entities.Moneda;
import com.udea.Backend.Usuarios.Entities.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "negocio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Negocio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_negocio")
    private Integer idNegocio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propietario_id", nullable = false)
    private Usuario propietario;

    @Column(name = "nombre_negocio", nullable = false, length = 250)
    private String nombreNegocio;

    @Column(name = "direccion_negocio", nullable = false, length = 250)
    private String direccionNegocio;

    @Column(length = 20)
    private String telefono;

    @Column(name = "identificacion_fiscal", nullable = false, length = 20, unique = true)
    private String identificacionFiscal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moneda_codigo_iso", nullable = false)
    private Moneda moneda;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String estado = "Activo";
}
