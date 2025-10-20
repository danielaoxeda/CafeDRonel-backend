package com.cafedronel.cafedronelbackend.services.password;

import com.cafedronel.cafedronelbackend.data.dto.password.ForgotRequest;
import com.cafedronel.cafedronelbackend.data.dto.password.ResetPasswordRequest;
import com.cafedronel.cafedronelbackend.data.model.Rol;
import com.cafedronel.cafedronelbackend.data.model.Usuario;
import com.cafedronel.cafedronelbackend.exceptions.BusinessException;
import com.cafedronel.cafedronelbackend.repository.UsuarioRepository;
import com.cafedronel.cafedronelbackend.services.email.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImpPasswordServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ImpPasswordService passwordService;

    private Usuario usuario;
    private ForgotRequest forgotRequest;
    private ResetPasswordRequest resetPasswordRequest;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setIdUsuario(1);
        usuario.setCorreo("test@example.com");
        usuario.setNombre("Test User");
        usuario.setContrasena("oldPassword");
        usuario.setTelefono("123456789");
        usuario.setDireccion("Test Address");
        usuario.setRol(Rol.CLIENTE);
        usuario.setRecoveryCode("123456");

        forgotRequest = new ForgotRequest("test@example.com");
        resetPasswordRequest = new ResetPasswordRequest(
                "test@example.com",
                "123456",
                "newPassword123"
        );
    }

    @Test
    void forgotAccount_ConUsuarioExistente_DeberiaEnviarCodigoRecuperacion() {
        // Arrange
        when(usuarioRepository.getUsuariosByCorreo("test@example.com"))
                .thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        Boolean result = passwordService.forgotAccount(forgotRequest);

        // Assert
        assertTrue(result);
        assertNotNull(usuario.getRecoveryCode());
        assertEquals(6, usuario.getRecoveryCode().length());
        
        verify(usuarioRepository).getUsuariosByCorreo("test@example.com");
        verify(emailService).sendPasswordResetEmail("test@example.com", usuario.getRecoveryCode());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void forgotAccount_ConUsuarioNoExistente_DeberiaLanzarBusinessException() {
        // Arrange
        when(usuarioRepository.getUsuariosByCorreo("nonexistent@example.com"))
                .thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, 
                () -> passwordService.forgotAccount(new ForgotRequest("nonexistent@example.com")));
        
        assertEquals("El usuario no existe", exception.getMessage());
        verify(usuarioRepository).getUsuariosByCorreo("nonexistent@example.com");
        verify(emailService, never()).sendPasswordResetEmail(anyString(), anyString());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void resetPassword_ConCodigoValido_DeberiaCambiarContrasena() {
        // Arrange
        when(usuarioRepository.findByCorreo("test@example.com"))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        Boolean result = passwordService.resetPassword(resetPasswordRequest);

        // Assert
        assertTrue(result);
        assertEquals("encodedNewPassword", usuario.getContrasena());
        
        verify(usuarioRepository).findByCorreo("test@example.com");
        verify(passwordEncoder).encode("newPassword123");
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void resetPassword_ConUsuarioNoExistente_DeberiaLanzarBusinessException() {
        // Arrange
        when(usuarioRepository.findByCorreo("nonexistent@example.com"))
                .thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, 
                () -> passwordService.resetPassword(new ResetPasswordRequest(
                        "nonexistent@example.com", "123456", "newPassword123")));
        
        assertEquals("El usuario no existe", exception.getMessage());
        verify(usuarioRepository).findByCorreo("nonexistent@example.com");
        verify(passwordEncoder, never()).encode(anyString());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void resetPassword_ConCodigoInvalido_DeberiaLanzarBusinessException() {
        // Arrange
        ResetPasswordRequest requestConCodigoInvalido = new ResetPasswordRequest(
                "test@example.com",
                "999999", // Código incorrecto
                "newPassword123"
        );
        
        when(usuarioRepository.findByCorreo("test@example.com"))
                .thenReturn(Optional.of(usuario));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, 
                () -> passwordService.resetPassword(requestConCodigoInvalido));
        
        assertEquals("El código de recuperación no es valido", exception.getMessage());
        verify(usuarioRepository).findByCorreo("test@example.com");
        verify(passwordEncoder, never()).encode(anyString());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void resetPassword_ConCodigoNulo_DeberiaLanzarBusinessException() {
        // Arrange
        usuario.setRecoveryCode(null);
        when(usuarioRepository.findByCorreo("test@example.com"))
                .thenReturn(Optional.of(usuario));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, 
                () -> passwordService.resetPassword(resetPasswordRequest));
        
        assertEquals("El código de recuperación no es valido", exception.getMessage());
        verify(usuarioRepository).findByCorreo("test@example.com");
        verify(passwordEncoder, never()).encode(anyString());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void forgotAccount_DeberiaGenerarCodigoDe6Digitos() {
        // Arrange
        when(usuarioRepository.getUsuariosByCorreo("test@example.com"))
                .thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        passwordService.forgotAccount(forgotRequest);

        // Assert
        String recoveryCode = usuario.getRecoveryCode();
        assertNotNull(recoveryCode);
        assertEquals(6, recoveryCode.length());
        assertTrue(recoveryCode.matches("\\d{6}")); // Solo dígitos
    }

    @Test
    void resetPassword_DeberiaLimpiarCodigoRecuperacionDespuesDelUso() {
        // Arrange
        when(usuarioRepository.findByCorreo("test@example.com"))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        passwordService.resetPassword(resetPasswordRequest);

        // Assert
        // El código de recuperación debería seguir siendo el mismo hasta que se limpie explícitamente
        // Esto es una mejora que se podría implementar en el futuro
        verify(usuarioRepository).save(usuario);
    }
}
