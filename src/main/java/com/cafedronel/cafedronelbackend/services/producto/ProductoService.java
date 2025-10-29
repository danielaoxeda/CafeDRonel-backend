package com.cafedronel.cafedronelbackend.services.producto;

import java.util.List;

import com.cafedronel.cafedronelbackend.data.dto.producto.ProductoRequestDTO;
import com.cafedronel.cafedronelbackend.data.dto.producto.ProductoResponseDTO;

public interface ProductoService {
    List<ProductoResponseDTO> findAll();
    ProductoResponseDTO findById(Integer id);
    ProductoResponseDTO create(ProductoRequestDTO productoRequestDTO);
    ProductoResponseDTO update(Integer id, ProductoRequestDTO productoRequestDTO);
    void delete(Integer id);
    
    // Métodos para manejo de stock
    void disminuirStock(Integer productoId, Integer cantidad);
    void aumentarStock(Integer productoId, Integer cantidad);
    boolean verificarStock(Integer productoId, Integer cantidad);
}