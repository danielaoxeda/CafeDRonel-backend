package com.cafedronel.cafedronelbackend.services.producto;

import com.cafedronel.cafedronelbackend.data.dto.producto.ProductoRequestDTO;
import com.cafedronel.cafedronelbackend.data.dto.producto.ProductoResponseDTO;

import java.util.List;

public interface ProductoService {
    List<ProductoResponseDTO> findAll();
    ProductoResponseDTO findById(Integer id);
    ProductoResponseDTO create(ProductoRequestDTO productoRequestDTO);
    ProductoResponseDTO update(Integer id, ProductoRequestDTO productoRequestDTO);
    void delete(Integer id);
}