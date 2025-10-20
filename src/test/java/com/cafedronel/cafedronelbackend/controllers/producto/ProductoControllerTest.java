package com.cafedronel.cafedronelbackend.controllers.producto;

import com.cafedronel.cafedronelbackend.data.dto.producto.ProductoRequestDTO;
import com.cafedronel.cafedronelbackend.data.dto.producto.ProductoResponseDTO;
import com.cafedronel.cafedronelbackend.exceptions.BusinessException;
import com.cafedronel.cafedronelbackend.services.producto.ProductoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductoController.class)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService productoService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductoRequestDTO productoRequestDTO;
    private ProductoResponseDTO productoResponseDTO;

    @BeforeEach
    void setUp() {
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

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idProducto", is(1)))
                .andExpect(jsonPath("$[0].nombre", is("Café Espresso")));

        verify(productoService, times(1)).findAll();
    }

    @Test
    void getProductoById_ExistingId_ReturnsProducto() throws Exception {
        when(productoService.findById(1)).thenReturn(productoResponseDTO);

        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProducto", is(1)))
                .andExpect(jsonPath("$.nombre", is("Café Espresso")));

        verify(productoService, times(1)).findById(1);
    }

    @Test
    void getProductoById_NonExistingId_ReturnsNotFound() throws Exception {
        when(productoService.findById(999)).thenThrow(new BusinessException("Producto no encontrado"));

        mockMvc.perform(get("/api/productos/999"))
                .andExpect(status().isInternalServerError());

        verify(productoService, times(1)).findById(999);
    }

    @Test
    void createProducto_ValidData_ReturnsCreatedProducto() throws Exception {
        when(productoService.create(any(ProductoRequestDTO.class))).thenReturn(productoResponseDTO);

        mockMvc.perform(post("/api/productos")
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

        mockMvc.perform(put("/api/productos/1")
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

        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Producto eliminado correctamente")));

        verify(productoService, times(1)).delete(1);
    }
}