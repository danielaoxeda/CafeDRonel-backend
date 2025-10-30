package com.cafedronel.cafedronelbackend.data.enums;

public enum EstadoPedido {
    PENDIENTE("Pendiente"),
    CONFIRMADO("Confirmado"),
    EN_PREPARACION("En Preparación"),
    LISTO("Listo"),
    EN_CAMINO("En Camino"),
    ENTREGADO("Entregado"),
    CANCELADO("Cancelado");

    private final String descripcion;

    EstadoPedido(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}