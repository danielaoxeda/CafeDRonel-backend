package com.cafedronel.cafedronelbackend.services.detallepedido;

import com.cafedronel.cafedronelbackend.data.model.DetallePedido;
import com.cafedronel.cafedronelbackend.exceptions.BusinessException;
import com.cafedronel.cafedronelbackend.repository.DetallePedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ImpDetallePedidoService implements DetallePedidoService {

    @Autowired
    private DetallePedidoRepository detallePedidoRepository;

    @Override
    public List<DetallePedido> findByPedidoId(Integer pedidoId) {
        return detallePedidoRepository.findByPedidoIdPedido(pedidoId);
    }

    @Override
    public Optional<DetallePedido> findById(Integer id) {
        return detallePedidoRepository.findById(id);
    }

    @Override
    public DetallePedido save(DetallePedido detallePedido) {
        return detallePedidoRepository.save(detallePedido);
    }

    @Override
    public void delete(Integer id) {
        if (!detallePedidoRepository.existsById(id)) {
            throw new BusinessException("Detalle de pedido no encontrado con ID: " + id);
        }
        detallePedidoRepository.deleteById(id);
    }
}