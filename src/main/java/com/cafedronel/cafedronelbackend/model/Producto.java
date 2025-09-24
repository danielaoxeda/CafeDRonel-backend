package com.cafedronel.cafedronelbackend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "producto")
@Data  
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProducto;

    private String nombre;
    private String categoria;
    private String subtipo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    private Double precio;
    private Integer stock;
    private Boolean activo;
}
