package com.cafedronel.cafedronelbackend.controllers.producto;

import com.cafedronel.cafedronelbackend.data.dto.MessageResponse;
import com.cafedronel.cafedronelbackend.data.dto.producto.ProductoRequestDTO;
import com.cafedronel.cafedronelbackend.data.dto.producto.ProductoResponseDTO;
import com.cafedronel.cafedronelbackend.services.producto.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<ProductoResponseDTO>> getAllProductos() {
        return ResponseEntity.ok(productoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> getProductoById(@PathVariable Integer id) {
        return ResponseEntity.ok(productoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ProductoResponseDTO> createProducto(@Valid @RequestBody ProductoRequestDTO productoRequestDTO) {
        ProductoResponseDTO createdProducto = productoService.create(productoRequestDTO);
        return new ResponseEntity<>(createdProducto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> updateProducto(
            @PathVariable Integer id,
            @Valid @RequestBody ProductoRequestDTO productoRequestDTO) {
        return ResponseEntity.ok(productoService.update(id, productoRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteProducto(@PathVariable Integer id) {
        productoService.delete(id);
        return ResponseEntity.ok(new MessageResponse("Producto eliminado correctamente"));
    }
}