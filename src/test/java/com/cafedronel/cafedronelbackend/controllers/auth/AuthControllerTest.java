package com.cafedronel.cafedronelbackend.controllers.auth;

import com.cafedronel.cafedronelbackend.data.dto.auth.AuthResponse;
import com.cafedronel.cafedronelbackend.data.dto.auth.LoginRequest;
import com.cafedronel.cafedronelbackend.data.dto.auth.RegisterRequest;
import com.cafedronel.cafedronelbackend.data.dto.auth.VerifyRequest;
import com.cafedronel.cafedronelbackend.data.model.Rol;
import com.cafedronel.cafedronelbackend.exceptions.BusinessException;
import com.cafedronel.cafedronelbackend.services.auth.AuthService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void login_ConCredencialesValidas_DeberiaRetornarAuthResponse() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("test@example.com", "password123");
        AuthResponse authResponse = new AuthResponse("jwt-token", "test@example.com", "CLIENTE", 1);

        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.rol").value("CLIENTE"));

        verify(authService).login(any(LoginRequest.class));
    }

    @Test
    void login_ConCredencialesInvalidas_DeberiaRetornarError() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("test@example.com", "wrongpassword");

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Credenciales inválidas"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isInternalServerError());

        verify(authService).login(any(LoginRequest.class));
    }

    @Test
    void register_ConUsuarioNuevo_DeberiaRegistrarUsuario() throws Exception {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest(
                "Test User",
                "test@example.com",
                "password123",
                "123456789",
                "Test Address",
                Rol.CLIENTE
        );

        when(authService.register(any(RegisterRequest.class))).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").value(true))
                .andExpect(jsonPath("$.message").value("Operación exitosa"));

        verify(authService).register(any(RegisterRequest.class));
    }

    @Test
    void register_ConUsuarioExistente_DeberiaRetornarError() throws Exception {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest(
                "Test User",
                "test@example.com",
                "password123",
                "123456789",
                "Test Address",
                Rol.CLIENTE
        );

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new BusinessException("El usuario ya esta registrado"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isInternalServerError());

        verify(authService).register(any(RegisterRequest.class));
    }

    @Test
    void verify_ConTokenValido_DeberiaRetornarTrue() throws Exception {
        // Arrange
        VerifyRequest verifyRequest = new VerifyRequest("Bearer valid-token");

        when(authService.verify(any(VerifyRequest.class))).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").value(true))
                .andExpect(jsonPath("$.message").value("Operación exitosa"));

        verify(authService).verify(any(VerifyRequest.class));
    }

    @Test
    void verify_ConTokenInvalido_DeberiaRetornarError() throws Exception {
        // Arrange
        VerifyRequest verifyRequest = new VerifyRequest("Bearer invalid-token");

        when(authService.verify(any(VerifyRequest.class)))
                .thenThrow(new BusinessException("El token no es valido"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyRequest)))
                .andExpect(status().isInternalServerError());

        verify(authService).verify(any(VerifyRequest.class));
    }

    @Test
    void login_ConRequestInvalido_DeberiaRetornarBadRequest() throws Exception {
        // Arrange - Request sin campos requeridos
        String invalidJson = "{\"correo\":\"test@example.com\"}";

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(authService, never()).login(any(LoginRequest.class));
    }

    @Test
    void register_ConRequestInvalido_DeberiaRetornarBadRequest() throws Exception {
        // Arrange - Request sin campos requeridos
        String invalidJson = "{\"correo\":\"test@example.com\",\"nombre\":\"Test User\"}";

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any(RegisterRequest.class));
    }
}
