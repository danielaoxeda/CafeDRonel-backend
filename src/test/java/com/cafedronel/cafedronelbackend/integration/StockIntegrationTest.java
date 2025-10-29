package com.cafedronel.cafedronelbackend.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.cafedronel.cafedronelbackend.data.model.Producto;
import com.cafedronel.cafedronelbackend.data.model.Rol;
import com.cafedronel.cafedronelbackend.data.model.Usuario;
import com.cafedronel.cafedronelbackend.repository.ProductoRepository;
import com.cafedronel.cafedronelbackend.repository.UsuarioRepository;
import com.cafedronel.cafedronelbackend.services.producto.ProductoService;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class StockIntegrationTest {

    @Autowired
    private ProductoService productoService;
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario usuario;
    private Producto producto;

    @BeforeEach
    void setUp() {
        // Crear usuario de prueba
        usuario = new Usuario();
        usuario.setNombre("Test User");
        usuario.setCorreo("test@example.com");
        usuario.setContrasena("password");
        usuario.setTelefono("123456789");
        usuario.setDireccion("Test Address");
        usuario.setRol(Rol.CLIENTE);
        usuario = usuarioRepository.save(usuario);

        // Crear producto de prueba con stock inicial
        producto = new Producto();
        producto.setNombre("Café Test");
        producto.setCategoria("Bebidas");
        producto.setSubtipo("Café");
        producto.setDescripcion("Café de prueba");
        producto.setPrecio(5.0);
        producto.setStock(10); // Stock inicial: 10
        producto.setActivo(true);
        producto = productoRepository.save(producto);
    }

    @Test
    void cuandoSeDisminuyeStock_DeberiaActualizarCorrectamente() {
        // Arrange
        Integer stockInicial = producto.getStock();
        Integer cantidadADisminuir = 3;

        // Act
        productoService.disminuirStock(producto.getIdProducto(), cantidadADisminuir);

        // Assert
        Producto productoActualizado = productoRepository.findById(producto.getIdProducto()).orElseThrow();
        assertEquals(stockInicial - cantidadADisminuir, productoActualizado.getStock());
        assertEquals(7, productoActualizado.getStock());
    }

    @Test
    void cuandoNoHayStockSuficiente_DeberiaLanzarExcepcion() {
        // Arrange
        Integer cantidadExcesiva = 15; // Más que el stock disponible (10)

        // Act & Assert
        assertThrows(Exception.class, () -> 
            productoService.disminuirStock(producto.getIdProducto(), cantidadExcesiva));
        
        // Verificar que el stock no cambió
        Producto productoSinCambios = productoRepository.findById(producto.getIdProducto()).orElseThrow();
        assertEquals(10, productoSinCambios.getStock());
    }

    @Test
    void verificarStock_ConStockSuficiente_DeberiaRetornarTrue() {
        // Act & Assert
        assertTrue(productoService.verificarStock(producto.getIdProducto(), 5));
        assertTrue(productoService.verificarStock(producto.getIdProducto(), 10));
    }

    @Test
    void verificarStock_ConStockInsuficiente_DeberiaRetornarFalse() {
        // Act & Assert
        assertFalse(productoService.verificarStock(producto.getIdProducto(), 15));
    }
}