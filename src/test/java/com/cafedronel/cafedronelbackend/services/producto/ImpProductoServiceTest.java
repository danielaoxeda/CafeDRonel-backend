package com.cafedronel.cafedronelbackend.services.producto;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.cafedronel.cafedronelbackend.data.dto.producto.ProductoRequestDTO;
import com.cafedronel.cafedronelbackend.data.dto.producto.ProductoResponseDTO;
import com.cafedronel.cafedronelbackend.data.model.Producto;
import com.cafedronel.cafedronelbackend.exceptions.BusinessException;
import com.cafedronel.cafedronelbackend.repository.ProductoRepository;

class ImpProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ImpProductoService productoService;

    private Producto producto;
    private ProductoRequestDTO productoRequestDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        producto = new Producto();
        producto.setIdProducto(1);
        producto.setNombre("Café Espresso");
        producto.setCategoria("Bebidas");
        producto.setSubtipo("Café");
        producto.setDescripcion("Café espresso intenso");
        producto.setPrecio(3.5);
        producto.setStock(100);
        producto.setActivo(true);

        productoRequestDTO = new ProductoRequestDTO();
        productoRequestDTO.setNombre("Café Espresso");
        productoRequestDTO.setCategoria("Bebidas");
        productoRequestDTO.setSubtipo("Café");
        productoRequestDTO.setDescripcion("Café espresso intenso");
        productoRequestDTO.setPrecio(3.5);
        productoRequestDTO.setStock(100);
        productoRequestDTO.setActivo(true);
    }

    @Test
    void findAll_ReturnsAllProductos() {
        // Arrange
        when(productoRepository.findAll()).thenReturn(Arrays.asList(producto));

        // Act
        List<ProductoResponseDTO> result = productoService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(producto.getNombre(), result.get(0).getNombre());
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    void findById_ExistingId_ReturnsProducto() {
        // Arrange
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));

        // Act
        ProductoResponseDTO result = productoService.findById(1);

        // Assert
        assertNotNull(result);
        assertEquals(producto.getIdProducto(), result.getIdProducto());
        assertEquals(producto.getNombre(), result.getNombre());
        verify(productoRepository, times(1)).findById(1);
    }

    @Test
    void findById_NonExistingId_ThrowsBusinessException() {
        // Arrange
        when(productoRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BusinessException.class, () -> productoService.findById(999));
        verify(productoRepository, times(1)).findById(999);
    }

    @Test
    void create_ValidProducto_ReturnsCreatedProducto() {
        // Arrange
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        // Act
        ProductoResponseDTO result = productoService.create(productoRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(producto.getNombre(), result.getNombre());
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void update_ExistingId_ReturnsUpdatedProducto() {
        // Arrange
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        // Act
        ProductoResponseDTO result = productoService.update(1, productoRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(productoRequestDTO.getNombre(), result.getNombre());
        verify(productoRepository, times(1)).findById(1);
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void delete_ExistingId_DeletesProducto() {
        // Arrange
        when(productoRepository.existsById(1)).thenReturn(true);
        doNothing().when(productoRepository).deleteById(1);

        // Act
        productoService.delete(1);

        // Assert
        verify(productoRepository, times(1)).existsById(1);
        verify(productoRepository, times(1)).deleteById(1);
    }

    @Test
    void delete_NonExistingId_ThrowsBusinessException() {
        // Arrange
        when(productoRepository.existsById(999)).thenReturn(false);

        // Act & Assert
        assertThrows(BusinessException.class, () -> productoService.delete(999));
        verify(productoRepository, times(1)).existsById(999);
        verify(productoRepository, never()).deleteById(anyInt());
    }

    @Test
    void disminuirStock_StockSuficiente_DisminuyeCorrectamente() {
        // Arrange
        producto.setStock(10);
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        // Act
        productoService.disminuirStock(1, 3);

        // Assert
        assertEquals(7, producto.getStock());
        verify(productoRepository, times(1)).findById(1);
        verify(productoRepository, times(1)).save(producto);
    }

    @Test
    void disminuirStock_StockInsuficiente_LanzaExcepcion() {
        // Arrange
        producto.setStock(2);
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, 
                () -> productoService.disminuirStock(1, 5));
        
        assertTrue(exception.getMessage().contains("Stock insuficiente"));
        verify(productoRepository, times(1)).findById(1);
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void aumentarStock_ProductoExistente_AumentaCorrectamente() {
        // Arrange
        producto.setStock(5);
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        // Act
        productoService.aumentarStock(1, 3);

        // Assert
        assertEquals(8, producto.getStock());
        verify(productoRepository, times(1)).findById(1);
        verify(productoRepository, times(1)).save(producto);
    }

    @Test
    void verificarStock_StockSuficiente_RetornaTrue() {
        // Arrange
        producto.setStock(10);
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));

        // Act
        boolean resultado = productoService.verificarStock(1, 5);

        // Assert
        assertTrue(resultado);
        verify(productoRepository, times(1)).findById(1);
    }

    @Test
    void verificarStock_StockInsuficiente_RetornaFalse() {
        // Arrange
        producto.setStock(3);
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));

        // Act
        boolean resultado = productoService.verificarStock(1, 5);

        // Assert
        assertFalse(resultado);
        verify(productoRepository, times(1)).findById(1);
    }

    @Test
    void verificarStock_ProductoNoExistente_LanzaExcepcion() {
        // Arrange
        when(productoRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BusinessException.class, () -> productoService.verificarStock(999, 1));
        verify(productoRepository, times(1)).findById(999);
    }
}