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
public class ReportePedidosDTO {
    private Integer idPedido;
    private String nombreCliente;
    private String correoCliente;
    private LocalDateTime fechaPedido;
    private String estado;
    private Double total;
    private Integer cantidadProductos;
    private String metodoPago;
    private String direccionEnvio;
}