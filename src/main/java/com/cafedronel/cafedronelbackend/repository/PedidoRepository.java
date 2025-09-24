package com.cafedronel.cafedronelbackend.repository;

import com.cafedronel.cafedronelbackend.data.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
}
