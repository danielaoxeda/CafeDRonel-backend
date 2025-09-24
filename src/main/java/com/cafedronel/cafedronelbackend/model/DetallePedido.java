package com.cafedronel.cafedronelbackend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "detalle_pedido")
@Data   
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDetalle;

    @ManyToOne
    @JoinColumn(name = "id_pedido")
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "id_producto")
    private Producto producto;

    private Integer cantidad;
    private Double subtotal;
}
