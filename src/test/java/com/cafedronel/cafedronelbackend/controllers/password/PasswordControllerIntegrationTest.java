package com.cafedronel.cafedronelbackend.controllers.password;

import com.cafedronel.cafedronelbackend.data.dto.password.ForgotRequest;
import com.cafedronel.cafedronelbackend.data.dto.password.ResetPasswordRequest;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class PasswordControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private com.cafedronel.cafedronelbackend.services.email.EmailService emailService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
        usuarioRepository.deleteAll(); // Limpiar datos antes de cada test
    }

    @Test
    void forgotPassword_ConUsuarioExistente_DeberiaEnviarCodigoRecuperacion() throws Exception {
        // Arrange - Crear usuario en la base de datos
        Usuario usuario = new Usuario();
        usuario.setCorreo("test@example.com");
        usuario.setNombre("Test User");
        usuario.setContrasena(passwordEncoder.encode("password123"));
        usuario.setTelefono("123456789");
        usuario.setDireccion("Test Address");
        usuario.setRol(Rol.CLIENTE);
        usuarioRepository.save(usuario);

        ForgotRequest forgotRequest = new ForgotRequest("test@example.com");

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/forgot")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(forgotRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").value(true))
                .andExpect(jsonPath("$.message").value("Operación exitosa"));

        // Verificar que se generó un código de recuperación
        Usuario usuarioActualizado = usuarioRepository.findByCorreo("test@example.com").get();
        assertNotNull(usuarioActualizado.getRecoveryCode());
        assertEquals(6, usuarioActualizado.getRecoveryCode().length());
    }

    @Test
    void forgotPassword_ConUsuarioNoExistente_DeberiaRetornarError() throws Exception {
        // Arrange
        ForgotRequest forgotRequest = new ForgotRequest("nonexistent@example.com");

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/forgot")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(forgotRequest)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void resetPassword_ConCodigoValido_DeberiaCambiarContrasena() throws Exception {
        // Arrange - Crear usuario con código de recuperación
        Usuario usuario = new Usuario();
        usuario.setCorreo("test@example.com");
        usuario.setNombre("Test User");
        usuario.setContrasena(passwordEncoder.encode("oldPassword"));
        usuario.setTelefono("123456789");
        usuario.setDireccion("Test Address");
        usuario.setRol(Rol.CLIENTE);
        usuario.setRecoveryCode("123456");
        usuarioRepository.save(usuario);

        ResetPasswordRequest resetRequest = new ResetPasswordRequest(
                "test@example.com",
                "123456",
                "newPassword123"
        );

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").value(true))
                .andExpect(jsonPath("$.message").value("Operación exitosa"));

        // Verificar que la contraseña cambió
        Usuario usuarioActualizado = usuarioRepository.findByCorreo("test@example.com").get();
        assertTrue(passwordEncoder.matches("newPassword123", usuarioActualizado.getContrasena()));
    }

    @Test
    void resetPassword_ConCodigoInvalido_DeberiaRetornarError() throws Exception {
        // Arrange - Crear usuario con código de recuperación
        Usuario usuario = new Usuario();
        usuario.setCorreo("test@example.com");
        usuario.setNombre("Test User");
        usuario.setContrasena(passwordEncoder.encode("oldPassword"));
        usuario.setTelefono("123456789");
        usuario.setDireccion("Test Address");
        usuario.setRol(Rol.CLIENTE);
        usuario.setRecoveryCode("123456");
        usuarioRepository.save(usuario);

        ResetPasswordRequest resetRequest = new ResetPasswordRequest(
                "test@example.com",
                "999999", // Código incorrecto
                "newPassword123"
        );

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetRequest)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void resetPassword_ConUsuarioNoExistente_DeberiaRetornarError() throws Exception {
        // Arrange
        ResetPasswordRequest resetRequest = new ResetPasswordRequest(
                "nonexistent@example.com",
                "123456",
                "newPassword123"
        );

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetRequest)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void forgotPassword_ConDatosInvalidos_DeberiaRetornarBadRequest() throws Exception {
        // Arrange - Request con datos faltantes
        String invalidJson = "{}";

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/forgot")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void resetPassword_ConDatosInvalidos_DeberiaRetornarBadRequest() throws Exception {
        // Arrange - Request con datos faltantes
        String invalidJson = "{\"email\":\"test@example.com\"}";

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
}
