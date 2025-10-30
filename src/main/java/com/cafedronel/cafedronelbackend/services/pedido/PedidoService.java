package com.cafedronel.cafedronelbackend.services.pedido;

import com.cafedronel.cafedronelbackend.data.enums.EstadoPedido;
import com.cafedronel.cafedronelbackend.data.model.Pedido;
import java.util.List;
import java.util.Optional;

public interface PedidoService {
    List<Pedido> findAll();
    Optional<Pedido> findById(Integer id);
    List<Pedido> findByUsuarioId(Integer usuarioId);
    Pedido save(Pedido pedido);
    Pedido update(Integer id, Pedido pedido);
    void delete(Integer id);
    Pedido cambiarEstado(Integer id, EstadoPedido nuevoEstado);
}