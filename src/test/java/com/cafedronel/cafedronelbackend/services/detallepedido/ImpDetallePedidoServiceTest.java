package com.cafedronel.cafedronelbackend.services.detallepedido;

import com.cafedronel.cafedronelbackend.data.model.DetallePedido;
import com.cafedronel.cafedronelbackend.exceptions.BusinessException;
import com.cafedronel.cafedronelbackend.repository.DetallePedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ImpDetallePedidoServiceTest {

    @Mock
    private DetallePedidoRepository detallePedidoRepository;

    @InjectMocks
    private ImpDetallePedidoService detallePedidoService;

    private DetallePedido detallePedido;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        detallePedido = new DetallePedido();
        detallePedido.setIdDetalle(1);
        detallePedido.setCantidad(2);
        detallePedido.setPrecioUnitario(10.0);
    }

    @Test
    void findByPedidoId() {
        when(detallePedidoRepository.findByPedidoIdPedido(1)).thenReturn(Arrays.asList(detallePedido));
        
        List<DetallePedido> result = detallePedidoService.findByPedidoId(1);
        
        assertEquals(1, result.size());
        verify(detallePedidoRepository, times(1)).findByPedidoIdPedido(1);
    }

    @Test
    void findById() {
        when(detallePedidoRepository.findById(1)).thenReturn(Optional.of(detallePedido));
        
        Optional<DetallePedido> result = detallePedidoService.findById(1);
        
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getIdDetalle());
        verify(detallePedidoRepository, times(1)).findById(1);
    }

    @Test
    void save() {
        when(detallePedidoRepository.save(any(DetallePedido.class))).thenReturn(detallePedido);
        
        DetallePedido result = detallePedidoService.save(detallePedido);
        
        assertNotNull(result);
        assertEquals(1, result.getIdDetalle());
        verify(detallePedidoRepository, times(1)).save(detallePedido);
    }

    @Test
    void delete() {
        when(detallePedidoRepository.existsById(1)).thenReturn(true);
        
        detallePedidoService.delete(1);
        
        verify(detallePedidoRepository, times(1)).existsById(1);
        verify(detallePedidoRepository, times(1)).deleteById(1);
    }

    @Test
    void delete_notFound() {
        when(detallePedidoRepository.existsById(1)).thenReturn(false);
        
        assertThrows(BusinessException.class, () -> detallePedidoService.delete(1));
        
        verify(detallePedidoRepository, times(1)).existsById(1);
        verify(detallePedidoRepository, never()).deleteById(any());
    }
}