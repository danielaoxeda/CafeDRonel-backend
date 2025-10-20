package com.cafedronel.cafedronelbackend.services.producto;

import com.cafedronel.cafedronelbackend.data.dto.producto.ProductoRequestDTO;
import com.cafedronel.cafedronelbackend.data.dto.producto.ProductoResponseDTO;
import com.cafedronel.cafedronelbackend.data.model.Producto;
import com.cafedronel.cafedronelbackend.exceptions.BusinessException;
import com.cafedronel.cafedronelbackend.repository.ProductoRepository;
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
}