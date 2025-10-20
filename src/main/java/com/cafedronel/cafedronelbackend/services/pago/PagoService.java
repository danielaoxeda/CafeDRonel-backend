package com.cafedronel.cafedronelbackend.services.pago;

import com.cafedronel.cafedronelbackend.data.model.Pago;
import java.util.List;
import java.util.Optional;

public interface PagoService {
    List<Pago> findAll();
    Optional<Pago> findById(Integer id);
    Optional<Pago> findByPedidoId(Integer pedidoId);
    Pago save(Pago pago);
    Pago update(Integer id, Pago pago);
    void delete(Integer id);
}