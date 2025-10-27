package com.cafedronel.cafedronelbackend.controllers.detallepedido;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cafedronel.cafedronelbackend.data.model.DetallePedido;
import com.cafedronel.cafedronelbackend.data.model.Pedido;
import com.cafedronel.cafedronelbackend.data.model.Producto;
import com.cafedronel.cafedronelbackend.services.detallepedido.DetallePedidoService;

@SpringBootTest
@AutoConfigureMockMvc
class DetallePedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DetallePedidoService detallePedidoService;

    private DetallePedido detallePedido;
    private Pedido pedido;
    private Producto producto;

    @BeforeEach
    void setUp() {
        pedido = new Pedido();
        pedido.setIdPedido(1);

        producto = new Producto();
        producto.setIdProducto(1);
        producto.setNombre("Café Espresso");
        producto.setPrecio(5.0);

        detallePedido = new DetallePedido();
        detallePedido.setIdDetalle(1);
        detallePedido.setPedido(pedido);
        detallePedido.setProducto(producto);
        detallePedido.setCantidad(2);
        detallePedido.setPrecioUnitario(5.0);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getDetallesByPedidoId() throws Exception {
        when(detallePedidoService.findByPedidoId(1)).thenReturn(Arrays.asList(detallePedido));

        mockMvc.perform(get("/api/v1/detalles-pedido/pedido/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idDetalle").value(1))
                .andExpect(jsonPath("$[0].cantidad").value(2))
                .andExpect(jsonPath("$[0].precioUnitario").value(5.0));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getDetalleById() throws Exception {
        when(detallePedidoService.findById(1)).thenReturn(Optional.of(detallePedido));

        mockMvc.perform(get("/api/v1/detalles-pedido/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idDetalle").value(1))
                .andExpect(jsonPath("$.cantidad").value(2))
                .andExpect(jsonPath("$.precioUnitario").value(5.0));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getDetalleById_notFound() throws Exception {
        when(detallePedidoService.findById(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/detalles-pedido/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteDetalle() throws Exception {
        mockMvc.perform(delete("/api/v1/detalles-pedido/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Detalle de pedido eliminado correctamente"));
    }
}