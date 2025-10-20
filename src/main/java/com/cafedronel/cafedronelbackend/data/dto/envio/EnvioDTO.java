package com.cafedronel.cafedronelbackend.data.dto.envio;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.Date;

@Data
public class EnvioDTO {
    private Integer idEnvio;
    
    @NotNull(message = "El ID del pedido es obligatorio")
    private Integer idPedido;
    
    @NotBlank(message = "El método de envío es obligatorio")
    private String metodoEnvio;
    
    private String estado;
    private Date fechaEnvio;
    private Date fechaEntrega;
    
    @NotBlank(message = "El número de seguimiento es obligatorio")
    private String numeroSeguimiento;
}