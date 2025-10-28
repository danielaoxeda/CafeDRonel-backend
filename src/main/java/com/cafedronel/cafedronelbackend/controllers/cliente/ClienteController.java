package com.cafedronel.cafedronelbackend.controllers.cliente;

import com.cafedronel.cafedronelbackend.data.dto.cliente.ClienteDTO;
import com.cafedronel.cafedronelbackend.data.dto.cliente.ClienteRequestDTO;
import com.cafedronel.cafedronelbackend.data.dto.cliente.ClienteUpdateDTO;
import com.cafedronel.cafedronelbackend.services.cliente.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "API para gestión de clientes")
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Obtener todos los clientes", description = "Obtiene una lista paginada de todos los clientes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de clientes obtenida exitosamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<Page<ClienteDTO>> getAllClientes(
            @Parameter(description = "Número de página (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo por el cual ordenar") @RequestParam(defaultValue = "nombre") String sortBy,
            @Parameter(description = "Dirección del ordenamiento (asc/desc)") @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ClienteDTO> clientes = clienteService.findAll(pageable);

        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Obtener todos los clientes sin paginación", description = "Obtiene una lista completa de todos los clientes")
    public ResponseEntity<List<ClienteDTO>> getAllClientesSinPaginacion() {
        List<ClienteDTO> clientes = clienteService.findAll();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or (hasRole('CLIENTE') and #id == authentication.principal.idUsuario)")
    @Operation(summary = "Obtener cliente por ID", description = "Obtiene un cliente específico por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<ClienteDTO> getClienteById(
            @Parameter(description = "ID del cliente") @PathVariable Integer id) {

        return clienteService.findById(id)
                .map(cliente -> ResponseEntity.ok(cliente))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/correo/{correo}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Buscar cliente por correo", description = "Busca un cliente por su correo electrónico")
    public ResponseEntity<ClienteDTO> getClienteByCorreo(
            @Parameter(description = "Correo electrónico del cliente") @PathVariable String correo) {

        return clienteService.findByCorreo(correo)
                .map(cliente -> ResponseEntity.ok(cliente))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Buscar clientes por nombre", description = "Busca clientes que contengan el texto especificado en su nombre")
    public ResponseEntity<List<ClienteDTO>> buscarClientesPorNombre(
            @Parameter(description = "Texto a buscar en el nombre") @RequestParam String nombre) {

        List<ClienteDTO> clientes = clienteService.findByNombreContaining(nombre);
        return ResponseEntity.ok(clientes);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Crear nuevo cliente", description = "Crea un nuevo cliente en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "El correo ya existe"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<Map<String, Object>> crearCliente(
            @Parameter(description = "Datos del nuevo cliente") @Valid @RequestBody ClienteRequestDTO clienteRequest) {

        ClienteDTO nuevoCliente = clienteService.save(clienteRequest);

        Map<String, Object> response = Map.of(
                "message", "Cliente creado exitosamente",
                "data", nuevoCliente);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or (hasRole('CLIENTE') and #id == authentication.principal.idUsuario)")
    @Operation(summary = "Actualizar cliente", description = "Actualiza los datos de un cliente existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            @ApiResponse(responseCode = "409", description = "El correo ya existe"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<Map<String, Object>> actualizarCliente(
            @Parameter(description = "ID del cliente") @PathVariable Integer id,
            @Parameter(description = "Datos actualizados del cliente") @Valid @RequestBody ClienteUpdateDTO clienteUpdate) {

        ClienteDTO clienteActualizado = clienteService.update(id, clienteUpdate);

        Map<String, Object> response = Map.of(
                "message", "Cliente actualizado exitosamente",
                "data", clienteActualizado);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Eliminar cliente", description = "Elimina un cliente del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<Map<String, String>> eliminarCliente(
            @Parameter(description = "ID del cliente") @PathVariable Integer id) {

        clienteService.deleteById(id);

        Map<String, String> response = Map.of(
                "message", "Cliente eliminado exitosamente");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/existe/{correo}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Verificar si existe cliente", description = "Verifica si existe un cliente con el correo especificado")
    public ResponseEntity<Map<String, Boolean>> existeClientePorCorreo(
            @Parameter(description = "Correo electrónico a verificar") @PathVariable String correo) {

        boolean existe = clienteService.existsByCorreo(correo);

        Map<String, Boolean> response = Map.of("existe", existe);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/estadisticas")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Obtener estadísticas de clientes", description = "Obtiene estadísticas básicas sobre los clientes")
    public ResponseEntity<Map<String, Object>> getEstadisticasClientes() {
        long totalClientes = clienteService.count();

        Map<String, Object> estadisticas = Map.of(
                "totalClientes", totalClientes);

        return ResponseEntity.ok(estadisticas);
    }
}