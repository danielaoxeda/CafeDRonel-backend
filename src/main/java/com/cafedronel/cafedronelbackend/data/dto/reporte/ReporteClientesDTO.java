package com.cafedronel.cafedronelbackend.data.dto.reporte;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteClientesDTO {
    private Integer idUsuario;
    private String nombre;
    private String correo;
    private String telefono;
    private String direccion;
    private Integer totalPedidos;
    private Double totalGastado;
    private LocalDateTime fechaRegistro;
    private String estado; // Activo/Inactivo basado en pedidos recientes
}