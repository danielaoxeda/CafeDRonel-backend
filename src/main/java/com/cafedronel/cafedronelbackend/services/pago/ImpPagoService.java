package com.cafedronel.cafedronelbackend.services.pago;

import com.cafedronel.cafedronelbackend.data.model.Pago;
import com.cafedronel.cafedronelbackend.exceptions.BusinessException;
import com.cafedronel.cafedronelbackend.repository.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ImpPagoService implements PagoService {

    @Autowired
    private PagoRepository pagoRepository;

    @Override
    public List<Pago> findAll() {
        return pagoRepository.findAll();
    }

    @Override
    public Optional<Pago> findById(Integer id) {
        return pagoRepository.findById(id);
    }

    @Override
    public Optional<Pago> findByPedidoId(Integer pedidoId) {
        return pagoRepository.findByPedidoIdPedido(pedidoId);
    }

    @Override
    public Pago save(Pago pago) {
        return pagoRepository.save(pago);
    }

    @Override
    public Pago update(Integer id, Pago pago) {
        if (!pagoRepository.existsById(id)) {
            throw new BusinessException("Pago no encontrado con ID: " + id);
        }
        pago.setIdPago(id);
        return pagoRepository.save(pago);
    }

    @Override
    public void delete(Integer id) {
        if (!pagoRepository.existsById(id)) {
            throw new BusinessException("Pago no encontrado con ID: " + id);
        }
        pagoRepository.deleteById(id);
    }
}