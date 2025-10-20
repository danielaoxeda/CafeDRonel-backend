package com.cafedronel.cafedronelbackend.services.envio;

import com.cafedronel.cafedronelbackend.data.model.Envio;
import com.cafedronel.cafedronelbackend.exceptions.BusinessException;
import com.cafedronel.cafedronelbackend.repository.EnvioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ImpEnvioServiceTest {

    @Mock
    private EnvioRepository envioRepository;

    @InjectMocks
    private ImpEnvioService envioService;

    private Envio envio;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        envio = new Envio();
        envio.setIdEnvio(1);
        envio.setMetodoEnvio("ESTANDAR");
        envio.setEstado("PENDIENTE");
        envio.setFechaEnvio(new Date());
    }

    @Test
    void findAll() {
        when(envioRepository.findAll()).thenReturn(Arrays.asList(envio));
        
        List<Envio> result = envioService.findAll();
        
        assertEquals(1, result.size());
        verify(envioRepository, times(1)).findAll();
    }

    @Test
    void findById() {
        when(envioRepository.findById(1)).thenReturn(Optional.of(envio));
        
        Optional<Envio> result = envioService.findById(1);
        
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getIdEnvio());
        verify(envioRepository, times(1)).findById(1);
    }

    @Test
    void findByPedidoId() {
        when(envioRepository.findByPedidoIdPedido(1)).thenReturn(Optional.of(envio));
        
        Optional<Envio> result = envioService.findByPedidoId(1);
        
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getIdEnvio());
        verify(envioRepository, times(1)).findByPedidoIdPedido(1);
    }

    @Test
    void save() {
        when(envioRepository.save(any(Envio.class))).thenReturn(envio);
        
        Envio result = envioService.save(envio);
        
        assertNotNull(result);
        assertEquals(1, result.getIdEnvio());
        verify(envioRepository, times(1)).save(envio);
    }

    @Test
    void update() {
        when(envioRepository.existsById(1)).thenReturn(true);
        when(envioRepository.save(any(Envio.class))).thenReturn(envio);
        
        Envio result = envioService.update(1, envio);
        
        assertNotNull(result);
        assertEquals(1, result.getIdEnvio());
        verify(envioRepository, times(1)).existsById(1);
        verify(envioRepository, times(1)).save(envio);
    }

    @Test
    void update_notFound() {
        when(envioRepository.existsById(1)).thenReturn(false);
        
        assertThrows(BusinessException.class, () -> envioService.update(1, envio));
        
        verify(envioRepository, times(1)).existsById(1);
        verify(envioRepository, never()).save(any(Envio.class));
    }

    @Test
    void delete() {
        when(envioRepository.existsById(1)).thenReturn(true);
        
        envioService.delete(1);
        
        verify(envioRepository, times(1)).existsById(1);
        verify(envioRepository, times(1)).deleteById(1);
    }

    @Test
    void delete_notFound() {
        when(envioRepository.existsById(1)).thenReturn(false);
        
        assertThrows(BusinessException.class, () -> envioService.delete(1));
        
        verify(envioRepository, times(1)).existsById(1);
        verify(envioRepository, never()).deleteById(any());
    }

    @Test
    void actualizarEstado() {
        when(envioRepository.findById(1)).thenReturn(Optional.of(envio));
        when(envioRepository.save(any(Envio.class))).thenReturn(envio);
        
        Envio result = envioService.actualizarEstado(1, "ENVIADO");
        
        assertEquals("ENVIADO", result.getEstado());
        verify(envioRepository, times(1)).findById(1);
        verify(envioRepository, times(1)).save(envio);
    }

    @Test
    void actualizarEstado_notFound() {
        when(envioRepository.findById(1)).thenReturn(Optional.empty());
        
        assertThrows(BusinessException.class, () -> envioService.actualizarEstado(1, "ENVIADO"));
        
        verify(envioRepository, times(1)).findById(1);
        verify(envioRepository, never()).save(any(Envio.class));
    }
}