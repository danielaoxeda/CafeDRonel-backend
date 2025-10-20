package com.cafedronel.cafedronelbackend.data.dto.pago;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.util.Date;

@Data
public class PagoDTO {
    private Integer idPago;
    
    @NotNull(message = "El ID del pedido es obligatorio")
    private Integer idPedido;
    
    @NotBlank(message = "El método de pago es obligatorio")
    private String metodoPago;
    
    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser positivo")
    private Double monto;
    
    private Date fechaPago;
    private String estado;
    private String referencia;
}