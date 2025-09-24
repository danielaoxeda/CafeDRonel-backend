package com.cafedronel.cafedronelbackend.repository;

import com.cafedronel.cafedronelbackend.data.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
}
