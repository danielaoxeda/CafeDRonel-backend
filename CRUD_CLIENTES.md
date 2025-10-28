# CRUD de Clientes - API REST

Este documento describe el CRUD completo para la gestión de clientes basado en el modelo Usuario con rol CLIENTE.

## 📋 Características

- ✅ Operaciones CRUD completas (Create, Read, Update, Delete)
- ✅ Validaciones de datos con Bean Validation
- ✅ Seguridad basada en roles (ADMINISTRADOR/CLIENTE)
- ✅ Paginación y ordenamiento
- ✅ Búsqueda por nombre y correo
- ✅ Encriptación de contraseñas
- ✅ Pruebas unitarias completas
- ✅ Documentación con Swagger/OpenAPI

## 🏗️ Arquitectura

### Componentes Creados

1. **DTOs**
   - `ClienteDTO` - Para respuestas
   - `ClienteRequestDTO` - Para creación (con validaciones)
   - `ClienteUpdateDTO` - Para actualizaciones (campos opcionales)

2. **Servicios**
   - `ClienteService` - Interfaz del servicio
   - `ImpClienteService` - Implementación del servicio

3. **Controlador**
   - `ClienteController` - API REST con endpoints completos

4. **Repositorio**
   - Métodos agregados a `UsuarioRepository` para trabajar con roles

5. **Pruebas**
   - `ImpClienteServiceTest` - Pruebas unitarias del servicio
   - `ClienteControllerTest` - Pruebas de integración del controlador

## 🔐 Seguridad

### Permisos por Rol

- **ADMINISTRADOR**: Acceso completo a todos los endpoints
- **CLIENTE**: Solo puede ver y actualizar su propio perfil

### Endpoints y Permisos

| Endpoint | Método | Permiso | Descripción |
|----------|--------|---------|-------------|
| `/api/v1/clientes` | GET | ADMIN | Listar todos los clientes (paginado) |
| `/api/v1/clientes/all` | GET | ADMIN | Listar todos los clientes (sin paginación) |
| `/api/v1/clientes/{id}` | GET | ADMIN o CLIENTE propio | Obtener cliente por ID |
| `/api/v1/clientes/correo/{correo}` | GET | ADMIN | Buscar cliente por correo |
| `/api/v1/clientes/buscar` | GET | ADMIN | Buscar clientes por nombre |
| `/api/v1/clientes` | POST | ADMIN | Crear nuevo cliente |
| `/api/v1/clientes/{id}` | PUT | ADMIN o CLIENTE propio | Actualizar cliente |
| `/api/v1/clientes/{id}` | DELETE | ADMIN | Eliminar cliente |
| `/api/v1/clientes/existe/{correo}` | GET | ADMIN | Verificar si existe correo |
| `/api/v1/clientes/estadisticas` | GET | ADMIN | Obtener estadísticas |

## 📝 Ejemplos de Uso

### 1. Crear Cliente

```bash
POST /api/v1/clientes
Content-Type: application/json
Authorization: Bearer {admin-token}

{
  "nombre": "Juan Pérez",
  "correo": "juan@example.com",
  "contrasena": "password123",
  "telefono": "12345678",
  "direccion": "Calle 123, Ciudad"
}
```

**Respuesta:**
```json
{
  "message": "Cliente creado exitosamente",
  "data": {
    "idUsuario": 1,
    "nombre": "Juan Pérez",
    "correo": "juan@example.com",
    "telefono": "12345678",
    "direccion": "Calle 123, Ciudad"
  }
}
```

### 2. Listar Clientes (Paginado)

```bash
GET /api/v1/clientes?page=0&size=10&sortBy=nombre&sortDir=asc
Authorization: Bearer {admin-token}
```

**Respuesta:**
```json
{
  "content": [
    {
      "idUsuario": 1,
      "nombre": "Juan Pérez",
      "correo": "juan@example.com",
      "telefono": "12345678",
      "direccion": "Calle 123, Ciudad"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 1,
  "totalPages": 1
}
```

### 3. Obtener Cliente por ID

```bash
GET /api/v1/clientes/1
Authorization: Bearer {admin-token}
```

**Respuesta:**
```json
{
  "idUsuario": 1,
  "nombre": "Juan Pérez",
  "correo": "juan@example.com",
  "telefono": "12345678",
  "direccion": "Calle 123, Ciudad"
}
```

### 4. Actualizar Cliente

```bash
PUT /api/v1/clientes/1
Content-Type: application/json
Authorization: Bearer {admin-token}

{
  "nombre": "Juan Carlos Pérez",
  "telefono": "87654321"
}
```

**Respuesta:**
```json
{
  "message": "Cliente actualizado exitosamente",
  "data": {
    "idUsuario": 1,
    "nombre": "Juan Carlos Pérez",
    "correo": "juan@example.com",
    "telefono": "87654321",
    "direccion": "Calle 123, Ciudad"
  }
}
```

### 5. Buscar Clientes por Nombre

```bash
GET /api/v1/clientes/buscar?nombre=Juan
Authorization: Bearer {admin-token}
```

### 6. Eliminar Cliente

```bash
DELETE /api/v1/clientes/1
Authorization: Bearer {admin-token}
```

**Respuesta:**
```json
{
  "message": "Cliente eliminado exitosamente"
}
```

## ✅ Validaciones

### ClienteRequestDTO (Creación)
- `nombre`: Obligatorio, 2-100 caracteres
- `correo`: Obligatorio, formato email válido
- `contrasena`: Obligatoria, mínimo 6 caracteres
- `telefono`: Obligatorio, 8-15 caracteres
- `direccion`: Obligatoria, 10-200 caracteres

### ClienteUpdateDTO (Actualización)
- Todos los campos son opcionales
- Mismas validaciones que ClienteRequestDTO cuando se proporcionan

## 🧪 Pruebas

### Ejecutar Pruebas del Servicio
```bash
./mvnw test -Dtest=ImpClienteServiceTest
```

### Ejecutar Pruebas del Controlador
```bash
./mvnw test -Dtest=ClienteControllerTest
```

### Cobertura de Pruebas
- ✅ Creación de clientes
- ✅ Listado con paginación
- ✅ Búsqueda por ID, correo y nombre
- ✅ Actualización de datos
- ✅ Eliminación
- ✅ Validación de datos
- ✅ Manejo de errores
- ✅ Seguridad por roles

## 🔧 Configuración

### Métodos Agregados al UsuarioRepository

```java
// Métodos para trabajar con roles específicos
List<Usuario> findByRol(Rol rol);
Page<Usuario> findByRol(Rol rol, Pageable pageable);
Optional<Usuario> findByIdUsuarioAndRol(Integer id, Rol rol);
Optional<Usuario> findByCorreoAndRol(String correo, Rol rol);
List<Usuario> findByNombreContainingIgnoreCaseAndRol(String nombre, Rol rol);
boolean existsByCorreoAndRol(String correo, Rol rol);
long countByRol(Rol rol);
boolean existsByCorreo(String correo);
```

## 📊 Estadísticas

El endpoint `/api/v1/clientes/estadisticas` proporciona:
- Total de clientes registrados

## 🚀 Próximas Mejoras

- [ ] Filtros avanzados de búsqueda
- [ ] Exportación de datos (CSV, Excel)
- [ ] Historial de cambios
- [ ] Validación de teléfonos por país
- [ ] Integración con servicios de geolocalización para direcciones
- [ ] Notificaciones por email al crear/actualizar clientes

## 📚 Documentación API

La documentación completa de la API está disponible en Swagger UI:
```
http://localhost:8080/swagger-ui.html
```

## 🐛 Manejo de Errores

### Códigos de Estado HTTP
- `200` - Operación exitosa
- `201` - Cliente creado exitosamente
- `400` - Datos inválidos o faltantes
- `403` - Acceso denegado
- `404` - Cliente no encontrado
- `409` - Conflicto (correo ya existe)
- `500` - Error interno del servidor

### Ejemplos de Errores

**Correo duplicado:**
```json
{
  "message": "Ya existe un usuario con el correo: juan@example.com"
}
```

**Cliente no encontrado:**
```json
{
  "message": "Cliente no encontrado con ID: 999"
}
```

**Datos inválidos:**
```json
{
  "message": "Validation failed",
  "errors": {
    "nombre": "El nombre es obligatorio",
    "correo": "El correo debe tener un formato válido"
  }
}
```