package com.cafedronel.cafedronelbackend.repository;

import com.cafedronel.cafedronelbackend.data.model.Envio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EnvioRepository extends JpaRepository<Envio, Integer> {
    Optional<Envio> findByPedidoIdPedido(Integer pedidoId);
}
