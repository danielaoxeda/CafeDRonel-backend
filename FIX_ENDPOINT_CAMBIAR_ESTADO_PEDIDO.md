# Fix: Endpoint Cambiar Estado de Pedido - JSON en lugar de String

## Problema Identificado

El endpoint `PATCH /api/v1/pedidos/{id}/estado` estaba recibiendo un String directamente en lugar de un objeto JSON estructurado, lo que causaba problemas de serialización y no seguía las mejores prácticas de API REST.

## Solución Implementada

### 1. Creación de DTO Específico

**Archivo:** `src/main/java/com/cafedronel/cafedronelbackend/data/dto/pedido/CambiarEstadoPedidoDTO.java`

```java
@Data
public class CambiarEstadoPedidoDTO {
    
    @NotNull(message = "El estado es obligatorio")
    private EstadoPedido estado;
}
```

### 2. Actualización del Controlador

**Antes:**
```java
@PatchMapping("/{id}/estado")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<Pedido> cambiarEstadoPedido(@PathVariable Integer id, @RequestBody EstadoPedido nuevoEstado) {
    return ResponseEntity.ok(pedidoService.cambiarEstado(id, nuevoEstado));
}
```

**Después:**
```java
@PatchMapping("/{id}/estado")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<Pedido> cambiarEstadoPedido(@PathVariable Integer id, @Valid @RequestBody CambiarEstadoPedidoDTO cambiarEstadoDTO) {
    return ResponseEntity.ok(pedidoService.cambiarEstado(id, cambiarEstadoDTO.getEstado()));
}
```

### 3. Actualización de Tests

**Antes:**
```java
mockMvc.perform(patch("/api/v1/pedidos/1/estado")
        .contentType(MediaType.APPLICATION_JSON)
        .content("\"ENTREGADO\""))
```

**Después:**
```java
CambiarEstadoPedidoDTO cambiarEstadoDTO = new CambiarEstadoPedidoDTO();
cambiarEstadoDTO.setEstado(EstadoPedido.ENTREGADO);

mockMvc.perform(patch("/api/v1/pedidos/1/estado")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(cambiarEstadoDTO)))
```

## Uso del Endpoint

### Request

```http
PATCH /api/v1/pedidos/123/estado
Content-Type: application/json
Authorization: Bearer <token>

{
  "estado": "ENTREGADO"
}
```

### Response

```json
{
  "idPedido": 123,
  "usuario": {
    "idUsuario": 1,
    "nombre": "Juan Pérez",
    "correo": "juan@example.com"
  },
  "fecha": "2024-10-30",
  "estado": "ENTREGADO",
  "telefono": "987654321",
  "direccion": "Calle Principal 123",
  "detalles": [...],
  "pago": {...},
  "envio": {...}
}
```

### Estados Disponibles

Para consultar todos los estados disponibles, usar:

```http
GET /api/v1/estados-pedido
```

```json
[
  "PENDIENTE",
  "CONFIRMADO", 
  "EN_PREPARACION",
  "LISTO",
  "EN_CAMINO",
  "ENTREGADO",
  "CANCELADO"
]
```

## Beneficios de la Solución

### ✅ **Estructura JSON Consistente**
- El endpoint ahora recibe un objeto JSON estructurado
- Sigue las mejores prácticas de API REST
- Facilita la extensión futura del DTO

### ✅ **Validación Mejorada**
- Validación automática con `@Valid`
- Mensaje de error claro con `@NotNull`
- Type safety con el enum `EstadoPedido`

### ✅ **Mejor Experiencia de Desarrollo**
- Documentación automática más clara
- Fácil integración desde el frontend
- Serialización/deserialización automática

### ✅ **Extensibilidad**
- Fácil agregar campos adicionales al DTO
- Posibilidad de agregar validaciones específicas
- Mantenimiento simplificado

## Archivos Modificados

1. **`CambiarEstadoPedidoDTO.java`** (NUEVO) - DTO para cambio de estado
2. **`PedidoController.java`** - Actualizado endpoint y importaciones
3. **`PedidoControllerTest.java`** - Actualizado test con nuevo DTO

## Verificación

- ✅ Compilación exitosa
- ✅ Tests pasando (12/12)
- ✅ Endpoint funcional con JSON estructurado
- ✅ Validación automática funcionando
- ✅ Compatibilidad con enum `EstadoPedido`

## Ejemplo de Integración Frontend

```javascript
// JavaScript/TypeScript
const cambiarEstadoPedido = async (pedidoId, nuevoEstado) => {
  const response = await fetch(`/api/v1/pedidos/${pedidoId}/estado`, {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({
      estado: nuevoEstado
    })
  });
  
  return response.json();
};

// Uso
await cambiarEstadoPedido(123, 'ENTREGADO');
```