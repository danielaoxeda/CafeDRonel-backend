package com.cafedronel.cafedronelbackend.controllers.envio;

import com.cafedronel.cafedronelbackend.data.dto.MessageResponse;
import com.cafedronel.cafedronelbackend.data.dto.envio.EnvioDTO;
import com.cafedronel.cafedronelbackend.data.model.Envio;
import com.cafedronel.cafedronelbackend.data.model.Pedido;
import com.cafedronel.cafedronelbackend.repository.PedidoRepository;
import com.cafedronel.cafedronelbackend.services.envio.EnvioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/envios")
public class EnvioController {

    @Autowired
    private EnvioService envioService;

    @Autowired
    private PedidoRepository pedidoRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Envio>> getAllEnvios() {
        return ResponseEntity.ok(envioService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @authService.esEnvioDeUsuarioAutenticado(#id)")
    public ResponseEntity<Envio> getEnvioById(@PathVariable Integer id) {
        return envioService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pedido/{pedidoId}")
    @PreAuthorize("hasRole('ADMIN') or @authService.esPedidoDeUsuarioAutenticado(#pedidoId)")
    public ResponseEntity<Envio> getEnvioByPedidoId(@PathVariable Integer pedidoId) {
        return envioService.findByPedidoId(pedidoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Envio> createEnvio(@Valid @RequestBody EnvioDTO envioDTO) {
        Pedido pedido = pedidoRepository.findById(envioDTO.getIdPedido())
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        Envio envio = new Envio();
        envio.setPedido(pedido);
        envio.setMetodoEnvio(envioDTO.getMetodoEnvio());
        envio.setEstado("PREPARANDO");
        envio.setFechaEnvio(new Date());
        envio.setNumeroSeguimiento(envioDTO.getNumeroSeguimiento());

        return new ResponseEntity<>(envioService.save(envio), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Envio> updateEnvio(@PathVariable Integer id, @Valid @RequestBody EnvioDTO envioDTO) {
        Pedido pedido = pedidoRepository.findById(envioDTO.getIdPedido())
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        Envio envio = new Envio();
        envio.setIdEnvio(id);
        envio.setPedido(pedido);
        envio.setMetodoEnvio(envioDTO.getMetodoEnvio());
        envio.setEstado(envioDTO.getEstado());
        envio.setFechaEnvio(envioDTO.getFechaEnvio());
        envio.setFechaEntrega(envioDTO.getFechaEntrega());
        envio.setNumeroSeguimiento(envioDTO.getNumeroSeguimiento());

        return ResponseEntity.ok(envioService.update(id, envio));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Envio> actualizarEstadoEnvio(@PathVariable Integer id, @RequestBody String nuevoEstado) {
        return ResponseEntity.ok(envioService.actualizarEstado(id, nuevoEstado));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteEnvio(@PathVariable Integer id) {
        envioService.delete(id);
        return ResponseEntity.ok(new MessageResponse("Envío eliminado correctamente"));
    }
}