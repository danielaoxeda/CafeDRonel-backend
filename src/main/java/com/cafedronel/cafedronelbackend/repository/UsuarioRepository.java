package com.cafedronel.cafedronelbackend.repository;

import com.cafedronel.cafedronelbackend.data.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> getUsuarioByCorreo(String correo);

    Optional<Usuario> findByCorreo(String correo);

    Optional<Usuario> getUsuariosByCorreo(String correo);
}
