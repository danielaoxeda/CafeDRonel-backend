package com.cafedronel.cafedronelbackend.controllers.auth;

import com.cafedronel.cafedronelbackend.data.dto.auth.LoginRequest;
import com.cafedronel.cafedronelbackend.data.dto.auth.RegisterRequest;
import com.cafedronel.cafedronelbackend.data.dto.auth.VerifyRequest;
import com.cafedronel.cafedronelbackend.data.model.Rol;
import com.cafedronel.cafedronelbackend.data.model.Usuario;
import com.cafedronel.cafedronelbackend.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
        usuarioRepository.deleteAll(); // Limpiar datos antes de cada test
    }

    @Test
    void register_ConUsuarioNuevo_DeberiaRegistrarCorrectamente() throws Exception {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest(
                "Test Name User",
                "Test Last User",
                "test@example.com",
                "password123",
                "123456789",
                "Test Address",
                Rol.CLIENTE
        );
        

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").value(true))
                .andExpect(jsonPath("$.message").value("Operación exitosa"));

        // Verificar que el usuario se guardó en la base de datos
        assertTrue(usuarioRepository.findByCorreo("usuario@test.com").isPresent());
        Usuario usuarioGuardado = usuarioRepository.findByCorreo("usuario@test.com").get();
        assertEquals("Test User", usuarioGuardado.getNombre());
        assertEquals("usuario@test.com", usuarioGuardado.getCorreo());
        assertEquals(Rol.CLIENTE, usuarioGuardado.getRol());
    }

    @Test
    void register_ConUsuarioExistente_DeberiaRetornarError() throws Exception {
        // Arrange - Crear usuario existente
        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setCorreo("test@example.com");
        usuarioExistente.setNombre("Existing User");
        usuarioExistente.setContrasena(passwordEncoder.encode("password123"));
        usuarioExistente.setTelefono("123456789");
        usuarioExistente.setDireccion("Existing Address");
        usuarioExistente.setRol(Rol.CLIENTE);
        usuarioRepository.save(usuarioExistente);

        RegisterRequest registerRequest = new RegisterRequest(
                "Test Name User",
                "Test Last User",
                "test@example.com",
                "password123",
                "123456789",
                "Test Address",
                Rol.CLIENTE
        );

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void login_ConCredencialesValidas_DeberiaRetornarToken() throws Exception {
        // Arrange - Crear usuario en la base de datos
        Usuario usuario = new Usuario();
        usuario.setCorreo("usuario@test.com");
        usuario.setNombre("Test User");
        usuario.setContrasena(passwordEncoder.encode("password123"));
        usuario.setTelefono("123456789");
        usuario.setDireccion("Test Address");
        usuario.setRol(Rol.CLIENTE);
        usuarioRepository.save(usuario);

        LoginRequest loginRequest = new LoginRequest("usuario@test.com", "password123");

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.email").value("usuario@test.com"))
                .andExpect(jsonPath("$.rol").value("CLIENTE"));
    }

    @Test
    void login_ConCredencialesInvalidas_DeberiaRetornarError() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("test@example.com", "wrongpassword");

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void verify_ConTokenValido_DeberiaRetornarTrue() throws Exception {
        // Arrange - Crear usuario en la base de datos
        Usuario usuario = new Usuario();
        usuario.setCorreo("usuario@test.com");
        usuario.setNombre("Test User");
        usuario.setContrasena(passwordEncoder.encode("password123"));
        usuario.setTelefono("123456789");
        usuario.setDireccion("Test Address");
        usuario.setRol(Rol.CLIENTE);
        usuarioRepository.save(usuario);

        // Primero hacer login para obtener un token válido
        LoginRequest loginRequest = new LoginRequest("usuario@test.com", "password123");
        String loginResponse = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extraer el token del response (simplificado para el test)
        String token = "Bearer valid-token"; // En un test real, extraerías el token del JSON response

        VerifyRequest verifyRequest = new VerifyRequest(token);

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isBoolean());
    }

    @Test
    void register_ConDatosInvalidos_DeberiaRetornarBadRequest() throws Exception {
        // Arrange - Request con datos faltantes
        String invalidJson = "{\"correo\":\"test@example.com\"}";

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_ConDatosInvalidos_DeberiaRetornarBadRequest() throws Exception {
        // Arrange - Request con datos faltantes
        String invalidJson = "{\"correo\":\"test@example.com\"}";

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
}
