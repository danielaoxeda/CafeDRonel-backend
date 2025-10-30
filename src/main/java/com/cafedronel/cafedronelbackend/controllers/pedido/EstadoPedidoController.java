package com.cafedronel.cafedronelbackend.controllers.pedido;

import com.cafedronel.cafedronelbackend.data.enums.EstadoPedido;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/estados-pedido")
public class EstadoPedidoController {

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<EstadoPedido>> getAllEstados() {
        return ResponseEntity.ok(Arrays.asList(EstadoPedido.values()));
    }

    @GetMapping("/descripciones")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Map<String, String>> getEstadosConDescripciones() {
        Map<String, String> estadosMap = Arrays.stream(EstadoPedido.values())
                .collect(Collectors.toMap(
                    Enum::name,
                    EstadoPedido::getDescripcion
                ));
        return ResponseEntity.ok(estadosMap);
    }
}