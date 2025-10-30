package com.cafedronel.cafedronelbackend.services.pedido;

import com.cafedronel.cafedronelbackend.data.enums.EstadoPedido;
import com.cafedronel.cafedronelbackend.data.model.Pedido;
import com.cafedronel.cafedronelbackend.exceptions.BusinessException;
import com.cafedronel.cafedronelbackend.repository.PedidoRepository;
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

class ImpPedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private ImpPedidoService pedidoService;

    private Pedido pedido;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        pedido = new Pedido();
        pedido.setIdPedido(1);
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setDireccion("Calle Principal 123");
        pedido.setTelefono("987654321");
    }

    @Test
    void findAll() {
        when(pedidoRepository.findAll()).thenReturn(Arrays.asList(pedido));
        
        List<Pedido> result = pedidoService.findAll();
        
        assertEquals(1, result.size());
        verify(pedidoRepository, times(1)).findAll();
    }

    @Test
    void findById() {
        when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedido));
        
        Optional<Pedido> result = pedidoService.findById(1);
        
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getIdPedido());
        verify(pedidoRepository, times(1)).findById(1);
    }

    @Test
    void findByUsuarioId() {
        when(pedidoRepository.findByUsuarioIdUsuario(1)).thenReturn(Arrays.asList(pedido));
        
        List<Pedido> result = pedidoService.findByUsuarioId(1);
        
        assertEquals(1, result.size());
        verify(pedidoRepository, times(1)).findByUsuarioIdUsuario(1);
    }

    @Test
    void save() {
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        
        Pedido result = pedidoService.save(pedido);
        
        assertNotNull(result);
        assertEquals(1, result.getIdPedido());
        verify(pedidoRepository, times(1)).save(pedido);
    }

    @Test
    void update() {
        when(pedidoRepository.existsById(1)).thenReturn(true);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        
        Pedido result = pedidoService.update(1, pedido);
        
        assertNotNull(result);
        assertEquals(1, result.getIdPedido());
        verify(pedidoRepository, times(1)).existsById(1);
        verify(pedidoRepository, times(1)).save(pedido);
    }

    @Test
    void update_notFound() {
        when(pedidoRepository.existsById(1)).thenReturn(false);
        
        assertThrows(BusinessException.class, () -> pedidoService.update(1, pedido));
        
        verify(pedidoRepository, times(1)).existsById(1);
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void delete() {
        when(pedidoRepository.existsById(1)).thenReturn(true);
        
        pedidoService.delete(1);
        
        verify(pedidoRepository, times(1)).existsById(1);
        verify(pedidoRepository, times(1)).deleteById(1);
    }

    @Test
    void delete_notFound() {
        when(pedidoRepository.existsById(1)).thenReturn(false);
        
        assertThrows(BusinessException.class, () -> pedidoService.delete(1));
        
        verify(pedidoRepository, times(1)).existsById(1);
        verify(pedidoRepository, never()).deleteById(any());
    }

    @Test
    void cambiarEstado() {
        when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        
        Pedido result = pedidoService.cambiarEstado(1, EstadoPedido.ENTREGADO);
        
        assertEquals(EstadoPedido.ENTREGADO, result.getEstado());
        verify(pedidoRepository, times(1)).findById(1);
        verify(pedidoRepository, times(1)).save(pedido);
    }

    @Test
    void cambiarEstado_notFound() {
        when(pedidoRepository.findById(1)).thenReturn(Optional.empty());
        
        assertThrows(BusinessException.class, () -> pedidoService.cambiarEstado(1, EstadoPedido.ENTREGADO));
        
        verify(pedidoRepository, times(1)).findById(1);
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }
}