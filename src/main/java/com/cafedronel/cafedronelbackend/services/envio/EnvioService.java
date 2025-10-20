package com.cafedronel.cafedronelbackend.services.envio;

import com.cafedronel.cafedronelbackend.data.model.Envio;
import java.util.List;
import java.util.Optional;

public interface EnvioService {
    List<Envio> findAll();
    Optional<Envio> findById(Integer id);
    Optional<Envio> findByPedidoId(Integer pedidoId);
    Envio save(Envio envio);
    Envio update(Integer id, Envio envio);
    void delete(Integer id);
    Envio actualizarEstado(Integer id, String nuevoEstado);
}