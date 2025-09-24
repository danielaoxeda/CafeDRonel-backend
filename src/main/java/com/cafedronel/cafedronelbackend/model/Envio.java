package com.cafedronel.cafedronelbackend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "envio")
@Data   
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEnvio;

    @OneToOne
    @JoinColumn(name = "id_pedido")
    private Pedido pedido;

    private String direccion;
    private String departamento;
    private String provincia;
    private String distrito;
    private Double costoEnvio;
    private String estado;
}
