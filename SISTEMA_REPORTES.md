# Sistema de Reportes con Apache POI

Este documento describe el sistema completo de reportes implementado con Apache POI para generar archivos Excel profesionales.

## 📊 Características

- ✅ Generación de reportes en formato JSON y Excel (.xlsx)
- ✅ Múltiples tipos de reportes (Clientes, Pedidos, Productos, Ventas)
- ✅ Filtrado por rangos de fechas
- ✅ Estilos profesionales en Excel (colores, bordes, fuentes)
- ✅ Ajuste automático de columnas
- ✅ Reporte completo con múltiples hojas
- ✅ Seguridad basada en roles (solo ADMINISTRADOR)
- ✅ Documentación Swagger completa
- ✅ Pruebas unitarias y de integración

## 🏗️ Arquitectura

### Componentes Creados

1. **📋 DTOs de Reportes**
   - `ReporteClientesDTO` - Estadísticas de clientes
   - `ReportePedidosDTO` - Información de pedidos
   - `ReporteProductosDTO` - Estadísticas de productos
   - `ReporteVentasDTO` - Datos de ventas diarias

2. **⚙️ Servicios**
   - `ReporteService` - Interfaz del servicio
   - `ImpReporteService` - Implementación con Apache POI

3. **🌐 Controlador REST**
   - `ReporteController` - API REST con 12 endpoints

4. **🧪 Pruebas Completas**
   - `ImpReporteServiceTest` - 10 pruebas unitarias ✅
   - `ReporteControllerTest` - 15 pruebas de integración ✅

## 📝 Tipos de Reportes

### 1. Reporte de Clientes
**Información incluida:**
- ID del cliente
- Nombre completo
- Correo electrónico
- Teléfono
- Dirección
- Total de pedidos realizados
- Total gastado
- Estado (Activo/Inactivo)

### 2. Reporte de Pedidos
**Información incluida:**
- ID del pedido
- Nombre del cliente
- Correo del cliente
- Fecha del pedido
- Estado del pedido
- Total del pedido
- Cantidad de productos
- Método de pago
- Dirección de envío

### 3. Reporte de Productos
**Información incluida:**
- ID del producto
- Nombre del producto
- Descripción
- Precio
- Stock disponible
- Categoría
- Total vendido
- Ingresos totales generados
- Estado (Disponible/Agotado)

### 4. Reporte de Ventas
**Información incluida:**
- Fecha
- Total de pedidos del día
- Total de ventas del día
- Promedio de venta
- Clientes únicos
- Producto más vendido

## 🔐 Seguridad

### Permisos
- **Solo ADMINISTRADOR** puede acceder a todos los endpoints de reportes
- Autenticación JWT requerida
- Validación de roles en cada endpoint

## 🌐 Endpoints Disponibles

### Reportes JSON

| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/api/v1/reportes/clientes` | GET | Reporte de clientes en JSON |
| `/api/v1/reportes/pedidos` | GET | Reporte de pedidos (requiere fechas) |
| `/api/v1/reportes/productos` | GET | Reporte de productos en JSON |
| `/api/v1/reportes/ventas` | GET | Reporte de ventas (requiere fechas) |

### Exportación a Excel

| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/api/v1/reportes/excel/clientes` | GET | Descarga Excel de clientes |
| `/api/v1/reportes/excel/pedidos` | GET | Descarga Excel de pedidos |
| `/api/v1/reportes/excel/productos` | GET | Descarga Excel de productos |
| `/api/v1/reportes/excel/ventas` | GET | Descarga Excel de ventas |
| `/api/v1/reportes/excel/completo` | GET | Descarga Excel con todas las hojas |

### Utilidades

| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/api/v1/reportes/disponibles` | GET | Lista de reportes disponibles |
| `/api/v1/reportes/formatos` | GET | Formatos de exportación disponibles |

## 📚 Ejemplos de Uso

### 1. Obtener Reporte de Clientes (JSON)

```bash
GET /api/v1/reportes/clientes
Authorization: Bearer {admin-token}
```

**Respuesta:**
```json
[
  {
    "idUsuario": 1,
    "nombre": "Juan Pérez",
    "correo": "juan@example.com",
    "telefono": "12345678",
    "direccion": "Calle 123, Ciudad",
    "totalPedidos": 5,
    "totalGastado": 250.50,
    "fechaRegistro": "2024-01-15T10:30:00",
    "estado": "Activo"
  }
]
```

### 2. Obtener Reporte de Pedidos con Fechas

```bash
GET /api/v1/reportes/pedidos?fechaInicio=2024-01-01&fechaFin=2024-01-31
Authorization: Bearer {admin-token}
```

### 3. Descargar Excel de Clientes

```bash
GET /api/v1/reportes/excel/clientes
Authorization: Bearer {admin-token}
```

**Respuesta:** Archivo Excel descargable con nombre `reporte_clientes_20241027.xlsx`

### 4. Descargar Reporte Completo

```bash
GET /api/v1/reportes/excel/completo?fechaInicio=2024-01-01&fechaFin=2024-01-31
Authorization: Bearer {admin-token}
```

**Respuesta:** Archivo Excel con 4 hojas:
- Hoja 1: Clientes
- Hoja 2: Pedidos
- Hoja 3: Productos  
- Hoja 4: Ventas

### 5. Listar Reportes Disponibles

```bash
GET /api/v1/reportes/disponibles
Authorization: Bearer {admin-token}
```

**Respuesta:**
```json
[
  "Clientes - Estadísticas generales de clientes",
  "Pedidos - Reporte de pedidos por rango de fechas",
  "Productos - Estadísticas de productos y ventas",
  "Ventas - Reporte de ventas diarias",
  "Completo - Todos los reportes en un solo archivo Excel"
]
```

## 🎨 Características de los Archivos Excel

### Estilos Aplicados
- **Encabezados:** Fondo azul oscuro, texto blanco, fuente en negrita
- **Datos:** Bordes en todas las celdas para mejor legibilidad
- **Columnas:** Ajuste automático de ancho según contenido
- **Formato:** Archivos .xlsx compatibles con Excel 2007+

### Nombres de Archivos
- `reporte_clientes_YYYYMMDD.xlsx`
- `reporte_pedidos_YYYYMMDD_YYYYMMDD.xlsx`
- `reporte_productos_YYYYMMDD.xlsx`
- `reporte_ventas_YYYYMMDD_YYYYMMDD.xlsx`
- `reporte_completo_YYYYMMDD.xlsx`

## 🔧 Configuración Técnica

### Dependencias Agregadas al pom.xml

```xml
<!-- Apache POI for Excel reports -->
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi</artifactId>
    <version>5.2.5</version>
</dependency>
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.5</version>
</dependency>
```

### Estructura de Clases

```
src/main/java/com/cafedronel/cafedronelbackend/
├── data/dto/reporte/
│   ├── ReporteClientesDTO.java
│   ├── ReportePedidosDTO.java
│   ├── ReporteProductosDTO.java
│   └── ReporteVentasDTO.java
├── services/reporte/
│   ├── ReporteService.java
│   └── ImpReporteService.java
└── controllers/reporte/
    └── ReporteController.java
```

## 🧪 Pruebas

### Ejecutar Pruebas del Servicio
```bash
./mvnw test -Dtest=ImpReporteServiceTest
```

### Ejecutar Pruebas del Controlador
```bash
./mvnw test -Dtest=ReporteControllerTest
```

### Cobertura de Pruebas
- ✅ Generación de reportes JSON
- ✅ Generación de archivos Excel
- ✅ Manejo de fechas
- ✅ Estilos y formato de Excel
- ✅ Seguridad por roles
- ✅ Manejo de errores
- ✅ Descarga de archivos

## 🚀 Funcionalidades Avanzadas

### Reporte Completo Multi-Hoja
El endpoint `/excel/completo` genera un archivo Excel con múltiples hojas:

```java
// Ejemplo de uso interno
byte[] excelCompleto = reporteService.generarExcelCompleto(fechaInicio, fechaFin);
```

### Estilos Personalizados
```java
// Estilo para encabezados
CellStyle headerStyle = crearEstiloHeader(workbook);
- Fondo azul oscuro
- Texto blanco en negrita
- Bordes en todas las celdas
- Alineación centrada

// Estilo para datos
CellStyle dataStyle = crearEstiloData(workbook);
- Bordes en todas las celdas
- Formato estándar
```

### Ajuste Automático de Columnas
```java
// Se aplica automáticamente a todas las hojas
for (int i = 0; i < headers.length; i++) {
    sheet.autoSizeColumn(i);
}
```

## 📊 Casos de Uso

### 1. Análisis de Clientes
- Identificar clientes más activos
- Analizar patrones de gasto
- Segmentar clientes por actividad

### 2. Seguimiento de Pedidos
- Monitorear estados de pedidos
- Analizar métodos de pago preferidos
- Identificar picos de demanda

### 3. Gestión de Inventario
- Productos más vendidos
- Control de stock
- Análisis de rentabilidad

### 4. Análisis de Ventas
- Tendencias diarias/mensuales
- Productos estrella
- Rendimiento por período

## 🔮 Próximas Mejoras

- [ ] Reportes con gráficos (Apache POI Charts)
- [ ] Filtros avanzados por categorías
- [ ] Exportación a PDF
- [ ] Reportes programados automáticos
- [ ] Dashboard interactivo
- [ ] Comparativas entre períodos
- [ ] Reportes personalizables por usuario
- [ ] Integración con servicios de email para envío automático

## 📚 Documentación API

La documentación completa está disponible en Swagger UI:
```
http://localhost:8080/swagger-ui.html
```

Buscar la sección "Reportes" para ver todos los endpoints disponibles.

## 🐛 Manejo de Errores

### Códigos de Estado HTTP
- `200` - Reporte generado exitosamente
- `400` - Parámetros inválidos (fechas incorrectas)
- `403` - Acceso denegado (rol insuficiente)
- `500` - Error interno generando el reporte

### Ejemplos de Errores

**Fechas inválidas:**
```json
{
  "message": "La fecha de inicio debe ser anterior a la fecha de fin"
}
```

**Error generando Excel:**
```json
{
  "message": "Error generando reporte Excel",
  "details": "IOException al escribir archivo"
}
```

## 💡 Consejos de Uso

1. **Rendimiento:** Los reportes grandes pueden tardar varios segundos
2. **Memoria:** Los archivos Excel se generan en memoria, considerar límites
3. **Fechas:** Usar formato ISO (YYYY-MM-DD) para parámetros de fecha
4. **Seguridad:** Solo administradores pueden generar reportes
5. **Nombres:** Los archivos incluyen fecha para evitar conflictos

## 🎯 Beneficios del Sistema

- **Profesional:** Archivos Excel con formato empresarial
- **Flexible:** Múltiples formatos de salida (JSON/Excel)
- **Seguro:** Control de acceso basado en roles
- **Escalable:** Fácil agregar nuevos tipos de reportes
- **Mantenible:** Código bien estructurado y probado
- **Documentado:** API completamente documentada