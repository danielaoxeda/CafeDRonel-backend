package com.cafedronel.cafedronelbackend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Entity
@Table(name = "pago")
@Data  
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPago;

    @OneToOne
    @JoinColumn(name = "id_pedido")
    private Pedido pedido;

    private String metodo;
    private Double monto;
    private String codigoOperacion;

    @Temporal(TemporalType.DATE)
    private Date fecha;
}
