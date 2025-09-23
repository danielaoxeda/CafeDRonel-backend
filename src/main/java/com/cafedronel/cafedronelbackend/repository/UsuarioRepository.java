package com.cafedronel.cafedronelbackend.repository;

import com.cafedronel.cafedronelbackend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; // para buscar usuarios por email

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByCorreo(String correo);

}
