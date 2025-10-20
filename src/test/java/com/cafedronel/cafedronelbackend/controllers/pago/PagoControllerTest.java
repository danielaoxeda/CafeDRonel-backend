package com.cafedronel.cafedronelbackend.controllers.pago;

import com.cafedronel.cafedronelbackend.data.dto.pago.PagoDTO;
import com.cafedronel.cafedronelbackend.data.model.Pago;
import com.cafedronel.cafedronelbackend.data.model.Pedido;
import com.cafedronel.cafedronelbackend.repository.PedidoRepository;
import com.cafedronel.cafedronelbackend.services.pago.PagoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PagoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PagoService pagoService;

    @MockBean
    private PedidoRepository pedidoRepository;

    private Pago pago;
    private PagoDTO pagoDTO;
    private Pedido pedido;

    @BeforeEach
    void setUp() {
        pedido = new Pedido();
        pedido.setIdPedido(1);

        pago = new Pago();
        pago.setIdPago(1);
        pago.setPedido(pedido);
        pago.setMetodoPago("TARJETA");
        pago.setMonto(100.0);
        pago.setFechaPago(new Date());
        pago.setEstado("COMPLETADO");
        pago.setReferencia("REF123456");

        pagoDTO = new PagoDTO();
        pagoDTO.setIdPedido(1);
        pagoDTO.setMetodoPago("TARJETA");
        pagoDTO.setMonto(100.0);
        pagoDTO.setReferencia("REF123456");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllPagos() throws Exception {
        when(pagoService.findAll()).thenReturn(Arrays.asList(pago));

        mockMvc.perform(get("/api/pagos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idPago").value(1))
                .andExpect(jsonPath("$[0].estado").value("COMPLETADO"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getPagoById() throws Exception {
        when(pagoService.findById(1)).thenReturn(Optional.of(pago));

        mockMvc.perform(get("/api/pagos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPago").value(1))
                .andExpect(jsonPath("$.estado").value("COMPLETADO"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getPagoByPedidoId() throws Exception {
        when(pagoService.findByPedidoId(1)).thenReturn(Optional.of(pago));

        mockMvc.perform(get("/api/pagos/pedido/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPago").value(1))
                .andExpect(jsonPath("$.estado").value("COMPLETADO"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createPago() throws Exception {
        when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedido));
        when(pagoService.save(any(Pago.class))).thenReturn(pago);

        mockMvc.perform(post("/api/pagos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pagoDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idPago").value(1))
                .andExpect(jsonPath("$.estado").value("COMPLETADO"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updatePago() throws Exception {
        when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedido));
        when(pagoService.update(eq(1), any(Pago.class))).thenReturn(pago);

        mockMvc.perform(put("/api/pagos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pagoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPago").value(1))
                .andExpect(jsonPath("$.estado").value("COMPLETADO"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletePago() throws Exception {
        mockMvc.perform(delete("/api/pagos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Pago eliminado correctamente"));
    }
}