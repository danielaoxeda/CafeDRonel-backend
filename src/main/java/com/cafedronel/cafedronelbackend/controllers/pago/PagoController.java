package com.cafedronel.cafedronelbackend.controllers.pago;

import com.cafedronel.cafedronelbackend.data.dto.MessageResponse;
import com.cafedronel.cafedronelbackend.data.dto.pago.PagoDTO;
import com.cafedronel.cafedronelbackend.data.model.Pago;
import com.cafedronel.cafedronelbackend.data.model.Pedido;
import com.cafedronel.cafedronelbackend.repository.PedidoRepository;
import com.cafedronel.cafedronelbackend.services.pago.PagoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pagos")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @Autowired
    private PedidoRepository pedidoRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Pago>> getAllPagos() {
        return ResponseEntity.ok(pagoService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @authService.esPagoDeUsuarioAutenticado(#id)")
    public ResponseEntity<Pago> getPagoById(@PathVariable Integer id) {
        return pagoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pedido/{pedidoId}")
    @PreAuthorize("hasRole('ADMIN') or @authService.esPedidoDeUsuarioAutenticado(#pedidoId)")
    public ResponseEntity<Pago> getPagoByPedidoId(@PathVariable Integer pedidoId) {
        return pagoService.findByPedidoId(pedidoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Pago> createPago(@Valid @RequestBody PagoDTO pagoDTO) {
        Pedido pedido = pedidoRepository.findById(pagoDTO.getIdPedido())
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        Pago pago = new Pago();
        pago.setPedido(pedido);
        pago.setMetodoPago(pagoDTO.getMetodoPago());
        pago.setMonto(pagoDTO.getMonto());
        pago.setFechaPago(new Date());
        pago.setEstado("COMPLETADO");
        pago.setReferencia(pagoDTO.getReferencia());

        return new ResponseEntity<>(pagoService.save(pago), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Pago> updatePago(@PathVariable Integer id, @Valid @RequestBody PagoDTO pagoDTO) {
        Pedido pedido = pedidoRepository.findById(pagoDTO.getIdPedido())
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        Pago pago = new Pago();
        pago.setIdPago(id);
        pago.setPedido(pedido);
        pago.setMetodoPago(pagoDTO.getMetodoPago());
        pago.setMonto(pagoDTO.getMonto());
        pago.setFechaPago(pagoDTO.getFechaPago());
        pago.setEstado(pagoDTO.getEstado());
        pago.setReferencia(pagoDTO.getReferencia());

        return ResponseEntity.ok(pagoService.update(id, pago));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deletePago(@PathVariable Integer id) {
        pagoService.delete(id);
        return ResponseEntity.ok(new MessageResponse("Pago eliminado correctamente"));
    }
}