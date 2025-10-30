package com.cafedronel.cafedronelbackend.controllers.pedido;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cafedronel.cafedronelbackend.data.dto.MessageResponse;
import com.cafedronel.cafedronelbackend.data.dto.pedido.CambiarEstadoPedidoDTO;
import com.cafedronel.cafedronelbackend.data.dto.pedido.PedidoDTO;
import com.cafedronel.cafedronelbackend.data.enums.EstadoPedido;
import com.cafedronel.cafedronelbackend.data.model.DetallePedido;
import com.cafedronel.cafedronelbackend.data.model.Pedido;
import com.cafedronel.cafedronelbackend.data.model.Producto;
import com.cafedronel.cafedronelbackend.data.model.Usuario;
import com.cafedronel.cafedronelbackend.repository.ProductoRepository;
import com.cafedronel.cafedronelbackend.repository.UsuarioRepository;
import com.cafedronel.cafedronelbackend.services.pedido.PedidoService;
import com.cafedronel.cafedronelbackend.services.producto.ProductoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;
    
    @Autowired
    private ProductoService productoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Pedido>> getAllPedidos() {
        return ResponseEntity.ok(pedidoService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @authService.esUsuarioAutenticado(#id)")
    public ResponseEntity<Pedido> getPedidoById(@PathVariable Integer id) {
        return pedidoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasRole('ADMIN') or @authService.esUsuarioAutenticado(#usuarioId)")
    public ResponseEntity<List<Pedido>> getPedidosByUsuario(@PathVariable Integer usuarioId) {
        return ResponseEntity.ok(pedidoService.findByUsuarioId(usuarioId));
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<Pedido> createPedido(@Valid @RequestBody PedidoDTO pedidoDTO) {
        Usuario usuario = usuarioRepository.findById(pedidoDTO.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setFecha(new Date());
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setTelefono(pedidoDTO.getTelefono());
        pedido.setDireccion(pedidoDTO.getDireccion());
        
        List<DetallePedido> detalles = new ArrayList<>();
        
        if (pedidoDTO.getDetalles() != null) {
            // Primero verificar que hay stock suficiente para todos los productos
            for (var detalleDTO : pedidoDTO.getDetalles()) {
                if (!productoService.verificarStock(detalleDTO.getIdProducto(), detalleDTO.getCantidad())) {
                    throw new RuntimeException("Stock insuficiente para el producto con ID: " + detalleDTO.getIdProducto());
                }
            }
            
            // Si hay stock suficiente, crear los detalles y disminuir el stock
            detalles = pedidoDTO.getDetalles().stream().map(detalleDTO -> {
                Producto producto = productoRepository.findById(detalleDTO.getIdProducto())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
                
                // Disminuir el stock del producto
                productoService.disminuirStock(detalleDTO.getIdProducto(), detalleDTO.getCantidad());
                
                DetallePedido detalle = new DetallePedido();
                detalle.setPedido(pedido);
                detalle.setProducto(producto);
                detalle.setCantidad(detalleDTO.getCantidad());
                detalle.setPrecioUnitario(producto.getPrecio());
                return detalle;
            }).collect(Collectors.toList());
        }
        
        pedido.setDetalles(detalles);
        
        return new ResponseEntity<>(pedidoService.save(pedido), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Pedido> updatePedido(@PathVariable Integer id, @Valid @RequestBody PedidoDTO pedidoDTO) {
        return ResponseEntity.ok(pedidoService.update(id, convertToEntity(pedidoDTO)));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Pedido> cambiarEstadoPedido(@PathVariable Integer id, @Valid @RequestBody CambiarEstadoPedidoDTO cambiarEstadoDTO) {
        return ResponseEntity.ok(pedidoService.cambiarEstado(id, cambiarEstadoDTO.getEstado()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deletePedido(@PathVariable Integer id) {
        pedidoService.delete(id);
        return ResponseEntity.ok(new MessageResponse("Pedido eliminado correctamente"));
    }

    private Pedido convertToEntity(PedidoDTO pedidoDTO) {
        Usuario usuario = usuarioRepository.findById(pedidoDTO.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Pedido pedido = new Pedido();
        pedido.setIdPedido(pedidoDTO.getIdPedido());
        pedido.setUsuario(usuario);
        pedido.setFecha(pedidoDTO.getFecha() != null ? pedidoDTO.getFecha() : new Date());
        pedido.setEstado(pedidoDTO.getEstado() != null ? pedidoDTO.getEstado() : EstadoPedido.PENDIENTE);
        pedido.setTelefono(pedidoDTO.getTelefono());
        pedido.setDireccion(pedidoDTO.getDireccion());
        
        return pedido;
    }
}