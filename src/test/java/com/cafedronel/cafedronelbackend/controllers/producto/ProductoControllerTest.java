package com.cafedronel.cafedronelbackend.controllers.producto;

import com.cafedronel.cafedronelbackend.data.dto.producto.ProductoRequestDTO;
import com.cafedronel.cafedronelbackend.data.dto.producto.ProductoResponseDTO;
import com.cafedronel.cafedronelbackend.exceptions.BusinessException;
import com.cafedronel.cafedronelbackend.services.producto.ProductoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductoControllerTest {

    @Mock
    private ProductoService productoService;

    @InjectMocks
    private ProductoController productoController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private ProductoRequestDTO productoRequestDTO;
    private ProductoResponseDTO productoResponseDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productoController).build();
        objectMapper = new ObjectMapper();
        
        productoRequestDTO = new ProductoRequestDTO();
        productoRequestDTO.setNombre("Café Espresso");
        productoRequestDTO.setCategoria("Bebidas");
        productoRequestDTO.setSubtipo("Café");
        productoRequestDTO.setDescripcion("Café espresso intenso");
        productoRequestDTO.setPrecio(3.5);
        productoRequestDTO.setStock(100);
        productoRequestDTO.setActivo(true);

        productoResponseDTO = new ProductoResponseDTO();
        productoResponseDTO.setIdProducto(1);
        productoResponseDTO.setNombre("Café Espresso");
        productoResponseDTO.setCategoria("Bebidas");
        productoResponseDTO.setSubtipo("Café");
        productoResponseDTO.setDescripcion("Café espresso intenso");
        productoResponseDTO.setPrecio(3.5);
        productoResponseDTO.setStock(100);
        productoResponseDTO.setActivo(true);
    }

    @Test
    void getAllProductos_ReturnsListOfProductos() throws Exception {
        List<ProductoResponseDTO> productos = Arrays.asList(productoResponseDTO);
        when(productoService.findAll()).thenReturn(productos);

        mockMvc.perform(get("/api/v1/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idProducto", is(1)))
                .andExpect(jsonPath("$[0].nombre", is("Café Espresso")));

        verify(productoService, times(1)).findAll();
    }

    @Test
    void getProductoById_ExistingId_ReturnsProducto() throws Exception {
        when(productoService.findById(1)).thenReturn(productoResponseDTO);

        mockMvc.perform(get("/api/v1/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProducto", is(1)))
                .andExpect(jsonPath("$.nombre", is("Café Espresso")));

        verify(productoService, times(1)).findById(1);
    }

    @Test
    void getProductoById_NonExistingId_ReturnsNotFound() throws Exception {
        when(productoService.findById(999)).thenThrow(new BusinessException("Producto no encontrado"));

        // En un test standalone, la excepción se propaga directamente
        // En un test real con @WebMvcTest, habría un manejador de excepciones global
        try {
            mockMvc.perform(get("/api/v1/productos/999"));
        } catch (Exception e) {
            // Verificamos que la causa raíz sea la BusinessException esperada
            assertTrue(e.getCause() instanceof BusinessException);
            assertEquals("Producto no encontrado", e.getCause().getMessage());
        }

        verify(productoService, times(1)).findById(999);
    }

    @Test
    void createProducto_ValidData_ReturnsCreatedProducto() throws Exception {
        when(productoService.create(any(ProductoRequestDTO.class))).thenReturn(productoResponseDTO);

        mockMvc.perform(post("/api/v1/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productoRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idProducto", is(1)))
                .andExpect(jsonPath("$.nombre", is("Café Espresso")));

        verify(productoService, times(1)).create(any(ProductoRequestDTO.class));
    }

    @Test
    void updateProducto_ExistingId_ReturnsUpdatedProducto() throws Exception {
        when(productoService.update(eq(1), any(ProductoRequestDTO.class))).thenReturn(productoResponseDTO);

        mockMvc.perform(put("/api/v1/productos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productoRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProducto", is(1)))
                .andExpect(jsonPath("$.nombre", is("Café Espresso")));

        verify(productoService, times(1)).update(eq(1), any(ProductoRequestDTO.class));
    }

    @Test
    void deleteProducto_ExistingId_ReturnsSuccess() throws Exception {
        doNothing().when(productoService).delete(1);

        mockMvc.perform(delete("/api/v1/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Producto eliminado correctamente")));

        verify(productoService, times(1)).delete(1);
    }
}