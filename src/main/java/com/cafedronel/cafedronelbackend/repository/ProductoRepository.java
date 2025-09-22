package com.cafedronel.cafedronelbackend.repository;

import com.cafedronel.cafedronelbackend.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
}
