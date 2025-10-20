package com.cafedronel.cafedronelbackend.data.dto.producto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoResponseDTO {
    private Integer idProducto;
    private String nombre;
    private String categoria;
    private String subtipo;
    private String descripcion;
    private Double precio;
    private Integer stock;
    private Boolean activo;
}