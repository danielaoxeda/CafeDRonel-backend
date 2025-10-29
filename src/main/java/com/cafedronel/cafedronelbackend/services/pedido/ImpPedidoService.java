package com.cafedronel.cafedronelbackend.services.pedido;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cafedronel.cafedronelbackend.data.model.Pedido;
import com.cafedronel.cafedronelbackend.exceptions.BusinessException;
import com.cafedronel.cafedronelbackend.repository.PedidoRepository;

@Service
public class ImpPedidoService implements PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Override
    public List<Pedido> findAll() {
        return pedidoRepository.findAll();
    }

    @Override
    public Optional<Pedido> findById(Integer id) {
        return pedidoRepository.findById(id);
    }

    @Override
    public List<Pedido> findByUsuarioId(Integer usuarioId) {
        return pedidoRepository.findByUsuarioIdUsuario(usuarioId);
    }

    @Override
    public Pedido save(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    @Override
    public Pedido update(Integer id, Pedido pedido) {
        if (!pedidoRepository.existsById(id)) {
            throw new BusinessException("Pedido no encontrado con ID: " + id);
        }
        pedido.setIdPedido(id);
        return pedidoRepository.save(pedido);
    }

    @Override
    public void delete(Integer id) {
        if (!pedidoRepository.existsById(id)) {
            throw new BusinessException("Pedido no encontrado con ID: " + id);
        }
        pedidoRepository.deleteById(id);
    }

    @Override
    public Pedido cambiarEstado(Integer id, String nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Pedido no encontrado con ID: " + id));
        pedido.setEstado(nuevoEstado);
        return pedidoRepository.save(pedido);
    }
}