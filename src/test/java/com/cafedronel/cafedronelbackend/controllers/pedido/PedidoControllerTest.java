package com.cafedronel.cafedronelbackend.controllers.pedido;

import com.cafedronel.cafedronelbackend.data.dto.pedido.PedidoDTO;
import com.cafedronel.cafedronelbackend.data.enums.EstadoPedido;
import com.cafedronel.cafedronelbackend.data.model.Pedido;
import com.cafedronel.cafedronelbackend.data.model.Usuario;
import com.cafedronel.cafedronelbackend.repository.PedidoRepository;
import com.cafedronel.cafedronelbackend.repository.UsuarioRepository;
import com.cafedronel.cafedronelbackend.services.pedido.PedidoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PedidoService pedidoService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private PedidoRepository pedidoRepository;

    private Pedido pedido;
    private PedidoDTO pedidoDTO;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setIdUsuario(1);
        usuario.setNombre("Usuario Test");
        usuario.setCorreo("test@example.com");

        pedido = new Pedido();
        pedido.setIdPedido(1);
        pedido.setUsuario(usuario);
        pedido.setFecha(new Date());
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setDireccion("Calle Test 123");
        pedido.setTelefono("123456789");

        pedidoDTO = new PedidoDTO();
        pedidoDTO.setIdUsuario(1);
        pedidoDTO.setDireccion("Calle Test 123");
        pedidoDTO.setTelefono("123456789");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllPedidos() throws Exception {
        when(pedidoService.findAll()).thenReturn(Arrays.asList(pedido));

        mockMvc.perform(get("/api/v1/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idPedido").value(1))
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getPedidoById() throws Exception {
        when(pedidoService.findById(1)).thenReturn(Optional.of(pedido));

        mockMvc.perform(get("/api/v1/pedidos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPedido").value(1))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getPedidoById_notFound() throws Exception {
        when(pedidoService.findById(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/pedidos/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getPedidosByUsuarioId() throws Exception {
        when(pedidoService.findByUsuarioId(1)).thenReturn(Arrays.asList(pedido));

        mockMvc.perform(get("/api/v1/pedidos/usuario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idPedido").value(1))
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createPedido() throws Exception {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(pedidoService.save(any(Pedido.class))).thenReturn(pedido);

        mockMvc.perform(post("/api/v1/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedidoDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idPedido").value(1))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updatePedido() throws Exception {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(pedidoService.update(eq(1), any(Pedido.class))).thenReturn(pedido);

        mockMvc.perform(put("/api/v1/pedidos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedidoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPedido").value(1))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void cambiarEstadoPedido() throws Exception {
        pedido.setEstado(EstadoPedido.ENTREGADO);
        when(pedidoService.cambiarEstado(1, EstadoPedido.ENTREGADO)).thenReturn(pedido);

        mockMvc.perform(patch("/api/v1/pedidos/1/estado")
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"ENTREGADO\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPedido").value(1))
                .andExpect(jsonPath("$.estado").value("ENTREGADO"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletePedido() throws Exception {
        mockMvc.perform(delete("/api/v1/pedidos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Pedido eliminado correctamente"));
    }
}
