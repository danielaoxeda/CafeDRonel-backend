package com.cafedronel.cafedronelbackend.services.cliente;

import com.cafedronel.cafedronelbackend.data.dto.cliente.ClienteDTO;
import com.cafedronel.cafedronelbackend.data.dto.cliente.ClienteRequestDTO;
import com.cafedronel.cafedronelbackend.data.dto.cliente.ClienteUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ClienteService {
    
    /**
     * Obtiene todos los clientes con paginación
     */
    Page<ClienteDTO> findAll(Pageable pageable);
    
    /**
     * Obtiene todos los clientes sin paginación
     */
    List<ClienteDTO> findAll();
    
    /**
     * Busca un cliente por ID
     */
    Optional<ClienteDTO> findById(Integer id);
    
    /**
     * Busca un cliente por correo electrónico
     */
    Optional<ClienteDTO> findByCorreo(String correo);
    
    /**
     * Busca clientes por nombre (búsqueda parcial)
     */
    List<ClienteDTO> findByNombreContaining(String nombre);
    
    /**
     * Crea un nuevo cliente
     */
    ClienteDTO save(ClienteRequestDTO clienteRequest);
    
    /**
     * Actualiza un cliente existente
     */
    ClienteDTO update(Integer id, ClienteUpdateDTO clienteUpdate);
    
    /**
     * Elimina un cliente por ID
     */
    void deleteById(Integer id);
    
    /**
     * Verifica si existe un cliente con el correo dado
     */
    boolean existsByCorreo(String correo);
    
    /**
     * Cuenta el total de clientes
     */
    long count();
}