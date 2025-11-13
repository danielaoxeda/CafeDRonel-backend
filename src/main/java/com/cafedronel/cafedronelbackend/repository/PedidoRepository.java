package com.cafedronel.cafedronelbackend.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cafedronel.cafedronelbackend.data.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
    List<Pedido> findByUsuarioIdUsuario(Integer idUsuario);
    List<Pedido> findByFechaBetween(Date fechaInicio, Date fechaFin);
}
