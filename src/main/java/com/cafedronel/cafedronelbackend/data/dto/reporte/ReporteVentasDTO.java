package com.cafedronel.cafedronelbackend.data.dto.reporte;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteVentasDTO {
    private LocalDate fecha;
    private Integer totalPedidos;
    private Double totalVentas;
    private Double promedioVenta;
    private Integer clientesUnicos;
    private String productoMasVendido;
}