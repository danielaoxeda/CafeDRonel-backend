package com.cafedronel.cafedronelbackend.services.cliente;

import com.cafedronel.cafedronelbackend.data.dto.cliente.ClienteDTO;
import com.cafedronel.cafedronelbackend.data.dto.cliente.ClienteRequestDTO;
import com.cafedronel.cafedronelbackend.data.dto.cliente.ClienteUpdateDTO;
import com.cafedronel.cafedronelbackend.data.model.Rol;
import com.cafedronel.cafedronelbackend.data.model.Usuario;
import com.cafedronel.cafedronelbackend.exceptions.BusinessException;
import com.cafedronel.cafedronelbackend.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImpClienteServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ImpClienteService clienteService;

    private Usuario cliente;
    private ClienteRequestDTO clienteRequest;
    private ClienteUpdateDTO clienteUpdate;

    @BeforeEach
    void setUp() {
        cliente = new Usuario();
        cliente.setIdUsuario(1);
        cliente.setNombre("Juan Pérez");
        cliente.setCorreo("juan@example.com");
        cliente.setContrasena("encodedPassword");
        cliente.setTelefono("12345678");
        cliente.setDireccion("Calle 123, Ciudad");
        cliente.setRol(Rol.CLIENTE);

        clienteRequest = ClienteRequestDTO.builder()
                .nombre("Juan Pérez")
                .correo("juan@example.com")
                .contrasena("password123")
                .telefono("12345678")
                .direccion("Calle 123, Ciudad")
                .build();

        clienteUpdate = ClienteUpdateDTO.builder()
                .nombre("Juan Carlos Pérez")
                .telefono("87654321")
                .build();
    }

    @Test
    void findAll_ConPaginacion_DeberiaRetornarPaginaDeClientes() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Usuario> usuariosPage = new PageImpl<>(Arrays.asList(cliente));
        when(usuarioRepository.findByRol(Rol.CLIENTE, pageable)).thenReturn(usuariosPage);

        // Act
        Page<ClienteDTO> result = clienteService.findAll(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Juan Pérez", result.getContent().get(0).getNombre());
        verify(usuarioRepository).findByRol(Rol.CLIENTE, pageable);
    }

    @Test
    void findAll_SinPaginacion_DeberiaRetornarListaDeClientes() {
        // Arrange
        when(usuarioRepository.findByRol(Rol.CLIENTE)).thenReturn(Arrays.asList(cliente));

        // Act
        List<ClienteDTO> result = clienteService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Juan Pérez", result.get(0).getNombre());
        verify(usuarioRepository).findByRol(Rol.CLIENTE);
    }

    @Test
    void findById_ConIdExistente_DeberiaRetornarCliente() {
        // Arrange
        when(usuarioRepository.findByIdUsuarioAndRol(1, Rol.CLIENTE)).thenReturn(Optional.of(cliente));

        // Act
        Optional<ClienteDTO> result = clienteService.findById(1);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Juan Pérez", result.get().getNombre());
        verify(usuarioRepository).findByIdUsuarioAndRol(1, Rol.CLIENTE);
    }

    @Test
    void findById_ConIdNoExistente_DeberiaRetornarVacio() {
        // Arrange
        when(usuarioRepository.findByIdUsuarioAndRol(999, Rol.CLIENTE)).thenReturn(Optional.empty());

        // Act
        Optional<ClienteDTO> result = clienteService.findById(999);

        // Assert
        assertFalse(result.isPresent());
        verify(usuarioRepository).findByIdUsuarioAndRol(999, Rol.CLIENTE);
    }

    @Test
    void save_ConDatosValidos_DeberiaCrearCliente() {
        // Arrange
        when(usuarioRepository.existsByCorreo(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(cliente);

        // Act
        ClienteDTO result = clienteService.save(clienteRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Juan Pérez", result.getNombre());
        assertEquals("juan@example.com", result.getCorreo());
        verify(usuarioRepository).existsByCorreo("juan@example.com");
        verify(passwordEncoder).encode("password123");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void save_ConCorreoExistente_DeberiaLanzarExcepcion() {
        // Arrange
        when(usuarioRepository.existsByCorreo(anyString())).thenReturn(true);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, 
                () -> clienteService.save(clienteRequest));
        
        assertEquals("Ya existe un usuario con el correo: juan@example.com", exception.getMessage());
        verify(usuarioRepository).existsByCorreo("juan@example.com");
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void update_ConDatosValidos_DeberiaActualizarCliente() {
        // Arrange
        when(usuarioRepository.findByIdUsuarioAndRol(1, Rol.CLIENTE)).thenReturn(Optional.of(cliente));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(cliente);

        // Act
        ClienteDTO result = clienteService.update(1, clienteUpdate);

        // Assert
        assertNotNull(result);
        verify(usuarioRepository).findByIdUsuarioAndRol(1, Rol.CLIENTE);
        verify(usuarioRepository).save(cliente);
    }

    @Test
    void update_ConIdNoExistente_DeberiaLanzarExcepcion() {
        // Arrange
        when(usuarioRepository.findByIdUsuarioAndRol(999, Rol.CLIENTE)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, 
                () -> clienteService.update(999, clienteUpdate));
        
        assertEquals("Cliente no encontrado con ID: 999", exception.getMessage());
        verify(usuarioRepository).findByIdUsuarioAndRol(999, Rol.CLIENTE);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void deleteById_ConIdExistente_DeberiaEliminarCliente() {
        // Arrange
        when(usuarioRepository.findByIdUsuarioAndRol(1, Rol.CLIENTE)).thenReturn(Optional.of(cliente));

        // Act
        clienteService.deleteById(1);

        // Assert
        verify(usuarioRepository).findByIdUsuarioAndRol(1, Rol.CLIENTE);
        verify(usuarioRepository).delete(cliente);
    }

    @Test
    void deleteById_ConIdNoExistente_DeberiaLanzarExcepcion() {
        // Arrange
        when(usuarioRepository.findByIdUsuarioAndRol(999, Rol.CLIENTE)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, 
                () -> clienteService.deleteById(999));
        
        assertEquals("Cliente no encontrado con ID: 999", exception.getMessage());
        verify(usuarioRepository).findByIdUsuarioAndRol(999, Rol.CLIENTE);
        verify(usuarioRepository, never()).delete(any(Usuario.class));
    }

    @Test
    void existsByCorreo_ConCorreoExistente_DeberiaRetornarTrue() {
        // Arrange
        when(usuarioRepository.existsByCorreoAndRol("juan@example.com", Rol.CLIENTE)).thenReturn(true);

        // Act
        boolean result = clienteService.existsByCorreo("juan@example.com");

        // Assert
        assertTrue(result);
        verify(usuarioRepository).existsByCorreoAndRol("juan@example.com", Rol.CLIENTE);
    }

    @Test
    void count_DeberiaRetornarCantidadDeClientes() {
        // Arrange
        when(usuarioRepository.countByRol(Rol.CLIENTE)).thenReturn(5L);

        // Act
        long result = clienteService.count();

        // Assert
        assertEquals(5L, result);
        verify(usuarioRepository).countByRol(Rol.CLIENTE);
    }
}