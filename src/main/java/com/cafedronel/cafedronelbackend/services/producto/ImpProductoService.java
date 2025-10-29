package com.cafedronel.cafedronelbackend.services.producto;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cafedronel.cafedronelbackend.data.dto.producto.ProductoRequestDTO;
import com.cafedronel.cafedronelbackend.data.dto.producto.ProductoResponseDTO;
import com.cafedronel.cafedronelbackend.data.model.Producto;
import com.cafedronel.cafedronelbackend.exceptions.BusinessException;
import com.cafedronel.cafedronelbackend.repository.ProductoRepository;

@Service
public class ImpProductoService implements ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public List<ProductoResponseDTO> findAll() {
        return productoRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductoResponseDTO findById(Integer id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Producto no encontrado"));
        return mapToDTO(producto);
    }

    @Override
    public ProductoResponseDTO create(ProductoRequestDTO productoRequestDTO) {
        Producto producto = mapToEntity(productoRequestDTO);
        Producto savedProducto = productoRepository.save(producto);
        return mapToDTO(savedProducto);
    }

    @Override
    public ProductoResponseDTO update(Integer id, ProductoRequestDTO productoRequestDTO) {
        Producto existingProducto = productoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Producto no encontrado"));
        
        updateProductoFromDTO(existingProducto, productoRequestDTO);
        Producto updatedProducto = productoRepository.save(existingProducto);
        return mapToDTO(updatedProducto);
    }

    @Override
    public void delete(Integer id) {
        if (!productoRepository.existsById(id)) {
            throw new BusinessException("Producto no encontrado");
        }
        productoRepository.deleteById(id);
    }

    private ProductoResponseDTO mapToDTO(Producto producto) {
        return ProductoResponseDTO.builder()
                .idProducto(producto.getIdProducto())
                .nombre(producto.getNombre())
                .categoria(producto.getCategoria())
                .subtipo(producto.getSubtipo())
                .descripcion(producto.getDescripcion())
                .precio(producto.getPrecio())
                .stock(producto.getStock())
                .activo(producto.getActivo())
                .build();
    }

    private Producto mapToEntity(ProductoRequestDTO dto) {
        Producto producto = new Producto();
        updateProductoFromDTO(producto, dto);
        return producto;
    }

    private void updateProductoFromDTO(Producto producto, ProductoRequestDTO dto) {
        producto.setNombre(dto.getNombre());
        producto.setCategoria(dto.getCategoria());
        producto.setSubtipo(dto.getSubtipo());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        producto.setActivo(dto.getActivo() != null ? dto.getActivo() : true);
    }

    @Override
    public void disminuirStock(Integer productoId, Integer cantidad) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new BusinessException("Producto no encontrado con ID: " + productoId));
        
        if (producto.getStock() < cantidad) {
            throw new BusinessException("Stock insuficiente para el producto: " + producto.getNombre() + 
                    ". Stock disponible: " + producto.getStock() + ", cantidad solicitada: " + cantidad);
        }
        
        producto.setStock(producto.getStock() - cantidad);
        productoRepository.save(producto);
    }

    @Override
    public void aumentarStock(Integer productoId, Integer cantidad) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new BusinessException("Producto no encontrado con ID: " + productoId));
        
        producto.setStock(producto.getStock() + cantidad);
        productoRepository.save(producto);
    }

    @Override
    public boolean verificarStock(Integer productoId, Integer cantidad) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new BusinessException("Producto no encontrado con ID: " + productoId));
        
        return producto.getStock() >= cantidad;
    }
}