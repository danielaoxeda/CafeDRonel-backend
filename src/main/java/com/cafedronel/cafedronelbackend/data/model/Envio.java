package com.cafedronel.cafedronelbackend.data.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "envio")
@Getter
@Setter
@ToString(exclude = {"pedido"})
@EqualsAndHashCode(exclude = {"pedido"})
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEnvio;

    @OneToOne
    @JoinColumn(name = "id_pedido")
    @JsonBackReference("pedido-envio")
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
