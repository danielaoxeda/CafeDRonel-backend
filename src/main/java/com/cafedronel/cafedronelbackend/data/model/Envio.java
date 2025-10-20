package com.cafedronel.cafedronelbackend.data.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

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

    private String metodoEnvio;
    private String estado;
    
    @Temporal(TemporalType.DATE)
    private Date fechaEnvio;
    
    @Temporal(TemporalType.DATE)
    private Date fechaEntrega;
    
    private String numeroSeguimiento;
    private String direccion;
    private String departamento;
    private String provincia;
    private String distrito;
    private Double costoEnvio;
}
