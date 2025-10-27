package com.cafedronel.cafedronelbackend.services.auth;

import com.cafedronel.cafedronelbackend.data.dto.auth.AuthResponse;
import com.cafedronel.cafedronelbackend.data.dto.auth.LoginRequest;
import com.cafedronel.cafedronelbackend.data.dto.auth.RegisterRequest;
import com.cafedronel.cafedronelbackend.data.dto.auth.VerifyRequest;
import com.cafedronel.cafedronelbackend.data.model.Rol;
import com.cafedronel.cafedronelbackend.data.model.Usuario;
import com.cafedronel.cafedronelbackend.exceptions.BusinessException;
import com.cafedronel.cafedronelbackend.repository.UsuarioRepository;
import com.cafedronel.cafedronelbackend.util.jwt.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImpAuthServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ImpAuthService authService;

    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;
    private VerifyRequest verifyRequest;
    private Usuario usuario;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest("test@example.com", "password123");
        registerRequest = new RegisterRequest(
                "Test User",
                "test@example.com",
                "password123",
                "123456789",
                "Test Address",
                Rol.CLIENTE
        );
        verifyRequest = new VerifyRequest("Bearer valid-token");

        usuario = new Usuario();
        usuario.setIdUsuario(1);
        usuario.setCorreo("test@example.com");
        usuario.setNombre("Test User");
        usuario.setContrasena("encodedPassword");
        usuario.setTelefono("123456789");
        usuario.setDireccion("Test Address");
        usuario.setRol(Rol.CLIENTE);


    }

    @Test
    void login_ConCredencialesValidas_DeberiaRetornarAuthResponse() {
        // Arrange
        authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("test@example.com");
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("CLIENTE")))
                .when(authentication).getAuthorities();
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtil.generateToken("test@example.com")).thenReturn("jwt-token");

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jwt-token", response.token());
        assertEquals("test@example.com", response.email());
        assertEquals("CLIENTE", response.rol());
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil).generateToken("test@example.com");
    }

    @Test
    void login_ConCredencialesInvalidas_DeberiaLanzarExcepcion() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciales inválidas"));

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void register_ConUsuarioNuevo_DeberiaRegistrarUsuario() {
        // Arrange
        when(usuarioRepository.findByCorreo("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        Boolean result = authService.register(registerRequest);

        // Assert
        assertTrue(result);
        verify(usuarioRepository).findByCorreo("test@example.com");
        verify(passwordEncoder).encode("password123");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void register_ConUsuarioExistente_DeberiaLanzarBusinessException() {
        // Arrange
        when(usuarioRepository.findByCorreo("test@example.com")).thenReturn(Optional.of(usuario));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, 
                () -> authService.register(registerRequest));
        
        assertEquals("El usuario ya esta registrado", exception.getMessage());
        verify(usuarioRepository).findByCorreo("test@example.com");
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void verify_ConTokenValido_DeberiaRetornarTrue() {
        // Arrange
        when(jwtUtil.extractEmail("valid-token")).thenReturn("test@example.com");
        when(jwtUtil.validateToken("valid-token", "test@example.com")).thenReturn(true);
        when(usuarioRepository.findByCorreo("test@example.com")).thenReturn(Optional.of(usuario));

        // Act
        Boolean result = authService.verify(verifyRequest);

        // Assert
        assertTrue(result);
        verify(jwtUtil).extractEmail("valid-token");
        verify(jwtUtil).validateToken("valid-token", "test@example.com");
        verify(usuarioRepository).findByCorreo("test@example.com");
    }

    @Test
    void verify_ConTokenInvalido_DeberiaLanzarBusinessException() {
        // Arrange
        VerifyRequest invalidTokenRequest = new VerifyRequest("Bearer invalid-token");
        when(jwtUtil.extractEmail("invalid-token")).thenReturn("test@example.com");
        when(jwtUtil.validateToken("invalid-token", "test@example.com")).thenReturn(false);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, 
                () -> authService.verify(invalidTokenRequest));
        
        assertEquals("El token no es valido", exception.getMessage());
        verify(jwtUtil).extractEmail("invalid-token");
        verify(jwtUtil).validateToken("invalid-token", "test@example.com");
    }

    @Test
    void verify_ConTokenSinBearer_DeberiaProcesarCorrectamente() {
        // Arrange
        VerifyRequest requestSinBearer = new VerifyRequest("valid-token");
        when(jwtUtil.extractEmail("valid-token")).thenReturn("test@example.com");
        when(jwtUtil.validateToken("valid-token", "test@example.com")).thenReturn(true);
        when(usuarioRepository.findByCorreo("test@example.com")).thenReturn(Optional.of(usuario));

        // Act
        Boolean result = authService.verify(requestSinBearer);

        // Assert
        assertTrue(result);
        verify(jwtUtil).extractEmail("valid-token");
        verify(jwtUtil).validateToken("valid-token", "test@example.com");
    }

    @Test
    void verify_ConExcepcionEnJwtUtil_DeberiaLanzarBusinessException() {
        // Arrange
        when(jwtUtil.extractEmail("valid-token")).thenThrow(new RuntimeException("Error JWT"));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, 
                () -> authService.verify(verifyRequest));
        
        assertEquals("El token no es valido", exception.getMessage());
        verify(jwtUtil).extractEmail("valid-token");
    }
}
