package com.cafedronel.cafedronelbackend.services.pago;

import com.cafedronel.cafedronelbackend.data.model.Pago;
import com.cafedronel.cafedronelbackend.exceptions.BusinessException;
import com.cafedronel.cafedronelbackend.repository.PagoRepository;
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

class ImpPagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @InjectMocks
    private ImpPagoService pagoService;

    private Pago pago;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        pago = new Pago();
        pago.setIdPago(1);
        pago.setMetodoPago("TARJETA");
        pago.setMonto(100.0);
        pago.setFechaPago(new Date());
        pago.setEstado("COMPLETADO");
        pago.setReferencia("REF123456");
    }

    @Test
    void findAll() {
        when(pagoRepository.findAll()).thenReturn(Arrays.asList(pago));
        
        List<Pago> result = pagoService.findAll();
        
        assertEquals(1, result.size());
        verify(pagoRepository, times(1)).findAll();
    }

    @Test
    void findById() {
        when(pagoRepository.findById(1)).thenReturn(Optional.of(pago));
        
        Optional<Pago> result = pagoService.findById(1);
        
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getIdPago());
        verify(pagoRepository, times(1)).findById(1);
    }

    @Test
    void findByPedidoId() {
        when(pagoRepository.findByPedidoIdPedido(1)).thenReturn(Optional.of(pago));
        
        Optional<Pago> result = pagoService.findByPedidoId(1);
        
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getIdPago());
        verify(pagoRepository, times(1)).findByPedidoIdPedido(1);
    }

    @Test
    void save() {
        when(pagoRepository.save(any(Pago.class))).thenReturn(pago);
        
        Pago result = pagoService.save(pago);
        
        assertNotNull(result);
        assertEquals(1, result.getIdPago());
        verify(pagoRepository, times(1)).save(pago);
    }

    @Test
    void update() {
        when(pagoRepository.existsById(1)).thenReturn(true);
        when(pagoRepository.save(any(Pago.class))).thenReturn(pago);
        
        Pago result = pagoService.update(1, pago);
        
        assertNotNull(result);
        assertEquals(1, result.getIdPago());
        verify(pagoRepository, times(1)).existsById(1);
        verify(pagoRepository, times(1)).save(pago);
    }

    @Test
    void update_notFound() {
        when(pagoRepository.existsById(1)).thenReturn(false);
        
        assertThrows(BusinessException.class, () -> pagoService.update(1, pago));
        
        verify(pagoRepository, times(1)).existsById(1);
        verify(pagoRepository, never()).save(any(Pago.class));
    }

    @Test
    void delete() {
        when(pagoRepository.existsById(1)).thenReturn(true);
        
        pagoService.delete(1);
        
        verify(pagoRepository, times(1)).existsById(1);
        verify(pagoRepository, times(1)).deleteById(1);
    }

    @Test
    void delete_notFound() {
        when(pagoRepository.existsById(1)).thenReturn(false);
        
        assertThrows(BusinessException.class, () -> pagoService.delete(1));
        
        verify(pagoRepository, times(1)).existsById(1);
        verify(pagoRepository, never()).deleteById(any());
    }
}