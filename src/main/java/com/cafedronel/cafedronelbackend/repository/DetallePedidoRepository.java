package com.cafedronel.cafedronelbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cafedronel.cafedronelbackend.data.model.DetallePedido;
import java.util.List;

public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Integer> {
    List<DetallePedido> findByPedidoIdPedido(Integer pedidoId);
}
