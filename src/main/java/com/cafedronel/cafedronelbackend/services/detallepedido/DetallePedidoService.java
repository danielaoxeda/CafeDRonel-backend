package com.cafedronel.cafedronelbackend.services.detallepedido;

import com.cafedronel.cafedronelbackend.data.model.DetallePedido;
import java.util.List;
import java.util.Optional;

public interface DetallePedidoService {
    List<DetallePedido> findByPedidoId(Integer pedidoId);
    Optional<DetallePedido> findById(Integer id);
    DetallePedido save(DetallePedido detallePedido);
    void delete(Integer id);
}