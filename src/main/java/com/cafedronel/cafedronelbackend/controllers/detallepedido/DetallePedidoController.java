package com.cafedronel.cafedronelbackend.controllers.detallepedido;

import com.cafedronel.cafedronelbackend.data.dto.MessageResponse;
import com.cafedronel.cafedronelbackend.data.model.DetallePedido;
import com.cafedronel.cafedronelbackend.services.detallepedido.DetallePedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/detalles-pedido")
public class DetallePedidoController {

    @Autowired
    private DetallePedidoService detallePedidoService;

    @GetMapping("/pedido/{pedidoId}")
    @PreAuthorize("hasRole('ADMIN') or @authService.esPedidoDeUsuarioAutenticado(#pedidoId)")
    public ResponseEntity<List<DetallePedido>> getDetallesByPedido(@PathVariable Integer pedidoId) {
        return ResponseEntity.ok(detallePedidoService.findByPedidoId(pedidoId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @authService.esDetalleDeUsuarioAutenticado(#id)")
    public ResponseEntity<DetallePedido> getDetalleById(@PathVariable Integer id) {
        return detallePedidoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteDetalle(@PathVariable Integer id) {
        detallePedidoService.delete(id);
        return ResponseEntity.ok(new MessageResponse("Detalle de pedido eliminado correctamente"));
    }
}