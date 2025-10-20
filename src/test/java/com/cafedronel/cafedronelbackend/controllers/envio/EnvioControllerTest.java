package com.cafedronel.cafedronelbackend.controllers.envio;

import com.cafedronel.cafedronelbackend.data.dto.envio.EnvioDTO;
import com.cafedronel.cafedronelbackend.data.model.Envio;
import com.cafedronel.cafedronelbackend.data.model.Pedido;
import com.cafedronel.cafedronelbackend.repository.PedidoRepository;
import com.cafedronel.cafedronelbackend.services.envio.EnvioService;
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
class EnvioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EnvioService envioService;

    @MockBean
    private PedidoRepository pedidoRepository;

    private Envio envio;
    private EnvioDTO envioDTO;
    private Pedido pedido;

    @BeforeEach
    void setUp() {
        pedido = new Pedido();
        pedido.setIdPedido(1);

        envio = new Envio();
        envio.setIdEnvio(1);
        envio.setPedido(pedido);
        envio.setMetodoEnvio("ESTANDAR");
        envio.setEstado("PENDIENTE");
        envio.setFechaEnvio(new Date());
        envio.setNumeroSeguimiento("TRACK123456");

        envioDTO = new EnvioDTO();
        envioDTO.setIdPedido(1);
        envioDTO.setMetodoEnvio("ESTANDAR");
        envioDTO.setNumeroSeguimiento("TRACK123456");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllEnvios() throws Exception {
        when(envioService.findAll()).thenReturn(Arrays.asList(envio));

        mockMvc.perform(get("/api/envios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idEnvio").value(1))
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getEnvioById() throws Exception {
        when(envioService.findById(1)).thenReturn(Optional.of(envio));

        mockMvc.perform(get("/api/envios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEnvio").value(1))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getEnvioById_notFound() throws Exception {
        when(envioService.findById(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/envios/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getEnvioByPedidoId() throws Exception {
        when(envioService.findByPedidoId(1)).thenReturn(Optional.of(envio));

        mockMvc.perform(get("/api/envios/pedido/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEnvio").value(1))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createEnvio() throws Exception {
        when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedido));
        when(envioService.save(any(Envio.class))).thenReturn(envio);

        mockMvc.perform(post("/api/envios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(envioDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idEnvio").value(1))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateEnvio() throws Exception {
        when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedido));
        when(envioService.update(eq(1), any(Envio.class))).thenReturn(envio);

        mockMvc.perform(put("/api/envios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(envioDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEnvio").value(1))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void actualizarEstadoEnvio() throws Exception {
        envio.setEstado("ENVIADO");
        when(envioService.actualizarEstado(1, "ENVIADO")).thenReturn(envio);

        mockMvc.perform(patch("/api/envios/1/estado")
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"ENVIADO\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEnvio").value(1))
                .andExpect(jsonPath("$.estado").value("ENVIADO"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteEnvio() throws Exception {
        mockMvc.perform(delete("/api/envios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Envío eliminado correctamente"));
    }
}