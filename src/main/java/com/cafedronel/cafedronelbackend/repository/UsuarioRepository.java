package com.cafedronel.cafedronelbackend.repository;

import com.cafedronel.cafedronelbackend.data.model.Rol;
import com.cafedronel.cafedronelbackend.data.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> getUsuarioByCorreo(String correo);

    Optional<Usuario> findByCorreo(String correo);

    Optional<Usuario> getUsuariosByCorreo(String correo);
    
    // Métodos para trabajar con roles específicos
    List<Usuario> findByRol(Rol rol);
    
    Page<Usuario> findByRol(Rol rol, Pageable pageable);
    
    Optional<Usuario> findByIdUsuarioAndRol(Integer id, Rol rol);
    
    Optional<Usuario> findByCorreoAndRol(String correo, Rol rol);
    
    List<Usuario> findByNombreContainingIgnoreCaseAndRol(String nombre, Rol rol);
    
    boolean existsByCorreoAndRol(String correo, Rol rol);
    
    long countByRol(Rol rol);
    
    boolean existsByCorreo(String correo);
}
