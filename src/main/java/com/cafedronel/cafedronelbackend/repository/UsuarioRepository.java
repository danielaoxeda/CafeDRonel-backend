package com.cafedronel.cafedronelbackend.repository;

import com.cafedronel.cafedronelbackend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
}
