package com.cafedronel.cafedronelbackend.services.envio;

import com.cafedronel.cafedronelbackend.data.model.Envio;
import com.cafedronel.cafedronelbackend.exceptions.BusinessException;
import com.cafedronel.cafedronelbackend.repository.EnvioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ImpEnvioService implements EnvioService {

    @Autowired
    private EnvioRepository envioRepository;

    @Override
    public List<Envio> findAll() {
        return envioRepository.findAll();
    }

    @Override
    public Optional<Envio> findById(Integer id) {
        return envioRepository.findById(id);
    }

    @Override
    public Optional<Envio> findByPedidoId(Integer pedidoId) {
        return envioRepository.findByPedidoIdPedido(pedidoId);
    }

    @Override
    public Envio save(Envio envio) {
        return envioRepository.save(envio);
    }

    @Override
    public Envio update(Integer id, Envio envio) {
        if (!envioRepository.existsById(id)) {
            throw new BusinessException("Envío no encontrado con ID: " + id);
        }
        envio.setIdEnvio(id);
        return envioRepository.save(envio);
    }

    @Override
    public void delete(Integer id) {
        if (!envioRepository.existsById(id)) {
            throw new BusinessException("Envío no encontrado con ID: " + id);
        }
        envioRepository.deleteById(id);
    }

    @Override
    public Envio actualizarEstado(Integer id, String nuevoEstado) {
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Envío no encontrado con ID: " + id));
        envio.setEstado(nuevoEstado);
        return envioRepository.save(envio);
    }
}