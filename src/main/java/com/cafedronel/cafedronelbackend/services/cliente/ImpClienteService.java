package com.cafedronel.cafedronelbackend.services.cliente;

import com.cafedronel.cafedronelbackend.data.dto.cliente.ClienteDTO;
import com.cafedronel.cafedronelbackend.data.dto.cliente.ClienteRequestDTO;
import com.cafedronel.cafedronelbackend.data.dto.cliente.ClienteUpdateDTO;
import com.cafedronel.cafedronelbackend.data.model.Rol;
import com.cafedronel.cafedronelbackend.data.model.Usuario;
import com.cafedronel.cafedronelbackend.exceptions.BusinessException;
import com.cafedronel.cafedronelbackend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ImpClienteService implements ClienteService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public Page<ClienteDTO> findAll(Pageable pageable) {
        return usuarioRepository.findByRol(Rol.CLIENTE, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteDTO> findAll() {
        return usuarioRepository.findByRol(Rol.CLIENTE)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClienteDTO> findById(Integer id) {
        return usuarioRepository.findByIdUsuarioAndRol(id, Rol.CLIENTE)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClienteDTO> findByCorreo(String correo) {
        return usuarioRepository.findByCorreoAndRol(correo, Rol.CLIENTE)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteDTO> findByNombreContaining(String nombre) {
        return usuarioRepository.findByNombreContainingIgnoreCaseAndRol(nombre, Rol.CLIENTE)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ClienteDTO save(ClienteRequestDTO clienteRequest) {
        // Verificar si ya existe un usuario con ese correo
        if (usuarioRepository.existsByCorreo(clienteRequest.getCorreo())) {
            throw new BusinessException("Ya existe un usuario con el correo: " + clienteRequest.getCorreo());
        }

        Usuario usuario = Usuario.builder()
                .nombre(clienteRequest.getNombre())
                .correo(clienteRequest.getCorreo())
                .contrasena(passwordEncoder.encode(clienteRequest.getContrasena()))
                .telefono(clienteRequest.getTelefono())
                .direccion(clienteRequest.getDireccion())
                .rol(Rol.CLIENTE)
                .build();

        Usuario savedUsuario = usuarioRepository.save(usuario);
        return convertToDTO(savedUsuario);
    }

    @Override
    public ClienteDTO update(Integer id, ClienteUpdateDTO clienteUpdate) {
        Usuario usuario = usuarioRepository.findByIdUsuarioAndRol(id, Rol.CLIENTE)
                .orElseThrow(() -> new BusinessException("Cliente no encontrado con ID: " + id));

        // Verificar si el correo ya existe (si se está actualizando)
        if (clienteUpdate.getCorreo() != null && 
            !clienteUpdate.getCorreo().equals(usuario.getCorreo()) &&
            usuarioRepository.existsByCorreo(clienteUpdate.getCorreo())) {
            throw new BusinessException("Ya existe un usuario con el correo: " + clienteUpdate.getCorreo());
        }

        // Actualizar solo los campos que no son nulos
        if (clienteUpdate.getNombre() != null) {
            usuario.setNombre(clienteUpdate.getNombre());
        }
        if (clienteUpdate.getCorreo() != null) {
            usuario.setCorreo(clienteUpdate.getCorreo());
        }
        if (clienteUpdate.getTelefono() != null) {
            usuario.setTelefono(clienteUpdate.getTelefono());
        }
        if (clienteUpdate.getDireccion() != null) {
            usuario.setDireccion(clienteUpdate.getDireccion());
        }

        Usuario updatedUsuario = usuarioRepository.save(usuario);
        return convertToDTO(updatedUsuario);
    }

    @Override
    public void deleteById(Integer id) {
        Usuario usuario = usuarioRepository.findByIdUsuarioAndRol(id, Rol.CLIENTE)
                .orElseThrow(() -> new BusinessException("Cliente no encontrado con ID: " + id));
        
        usuarioRepository.delete(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCorreo(String correo) {
        return usuarioRepository.existsByCorreoAndRol(correo, Rol.CLIENTE);
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return usuarioRepository.countByRol(Rol.CLIENTE);
    }

    private ClienteDTO convertToDTO(Usuario usuario) {
        return ClienteDTO.builder()
                .idUsuario(usuario.getIdUsuario())
                .nombre(usuario.getNombre())
                .correo(usuario.getCorreo())
                .telefono(usuario.getTelefono())
                .direccion(usuario.getDireccion())
                .build();
    }
}