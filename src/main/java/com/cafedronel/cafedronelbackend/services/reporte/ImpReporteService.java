package com.cafedronel.cafedronelbackend.services.reporte;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cafedronel.cafedronelbackend.data.dto.reporte.ReporteClientesDTO;
import com.cafedronel.cafedronelbackend.data.dto.reporte.ReportePedidosDTO;
import com.cafedronel.cafedronelbackend.data.dto.reporte.ReporteProductosDTO;
import com.cafedronel.cafedronelbackend.data.dto.reporte.ReporteVentasDTO;
import com.cafedronel.cafedronelbackend.data.model.DetallePedido;
import com.cafedronel.cafedronelbackend.data.model.Rol;
import com.cafedronel.cafedronelbackend.data.model.Usuario;
import com.cafedronel.cafedronelbackend.repository.PedidoRepository;
import com.cafedronel.cafedronelbackend.repository.ProductoRepository;
import com.cafedronel.cafedronelbackend.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ImpReporteService implements ReporteService {

    private final UsuarioRepository usuarioRepository;
    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;

    @Override
    public List<ReporteClientesDTO> generarReporteClientes() {
        List<Usuario> clientes = usuarioRepository.findByRol(Rol.CLIENTE);

        return clientes.stream()
                .map(this::convertirAReporteCliente)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReportePedidosDTO> generarReportePedidos(LocalDate fechaInicio, LocalDate fechaFin) {
        List<com.cafedronel.cafedronelbackend.data.model.Pedido> pedidos;

        if (fechaInicio != null && fechaFin != null) {
            // Convertir LocalDate a Date para la consulta
            java.util.Date fechaInicioDate = java.sql.Date.valueOf(fechaInicio);
            java.util.Date fechaFinDate = java.sql.Date.valueOf(fechaFin);
            pedidos = pedidoRepository.findByFechaBetween(fechaInicioDate, fechaFinDate);
        } else {
            pedidos = pedidoRepository.findAll();
        }

        return pedidos.stream()
                .map(this::convertirAReportePedido)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReporteProductosDTO> generarReporteProductos() {
        List<com.cafedronel.cafedronelbackend.data.model.Producto> productos = productoRepository.findAll();

        return productos.stream()
                .map(this::convertirAReporteProducto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReporteVentasDTO> generarReporteVentas(LocalDate fechaInicio, LocalDate fechaFin) {
        List<com.cafedronel.cafedronelbackend.data.model.Pedido> pedidos;

        if (fechaInicio != null && fechaFin != null) {
            java.util.Date fechaInicioDate = java.sql.Date.valueOf(fechaInicio);
            java.util.Date fechaFinDate = java.sql.Date.valueOf(fechaFin);
            pedidos = pedidoRepository.findByFechaBetween(fechaInicioDate, fechaFinDate);
        } else {
            pedidos = pedidoRepository.findAll();
        }

        return generarReporteVentasPorFecha(pedidos, fechaInicio, fechaFin);
    }

    @Override
    public byte[] generarExcelClientes() {
        List<ReporteClientesDTO> clientes = generarReporteClientes();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Reporte de Clientes");

            // Crear estilos
            CellStyle headerStyle = crearEstiloHeader(workbook);
            CellStyle dataStyle = crearEstiloData(workbook);

            // Crear encabezados
            Row headerRow = sheet.createRow(0);
            String[] headers = { "ID", "Nombre", "Correo", "Teléfono", "Dirección", "Total Pedidos", "Total Gastado",
                    "Estado" };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Llenar datos
            int rowNum = 1;
            for (ReporteClientesDTO cliente : clientes) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(cliente.getIdUsuario());
                row.createCell(1).setCellValue(cliente.getNombre());
                row.createCell(2).setCellValue(cliente.getCorreo());
                row.createCell(3).setCellValue(cliente.getTelefono());
                row.createCell(4).setCellValue(cliente.getDireccion());
                row.createCell(5).setCellValue(cliente.getTotalPedidos());
                row.createCell(6).setCellValue(cliente.getTotalGastado());
                row.createCell(7).setCellValue(cliente.getEstado());

                // Aplicar estilo a las celdas de datos
                for (int i = 0; i < headers.length; i++) {
                    row.getCell(i).setCellStyle(dataStyle);
                }
            }

            // Ajustar ancho de columnas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Convertir a bytes
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            log.error("Error generando Excel de clientes", e);
            throw new RuntimeException("Error generando reporte Excel", e);
        }
    }

    @Override
    public byte[] generarExcelPedidos(LocalDate fechaInicio, LocalDate fechaFin) {
        try {
            log.info("Generando reporte Excel de pedidos desde {} hasta {}", fechaInicio, fechaFin);
            List<ReportePedidosDTO> pedidos = generarReportePedidos(fechaInicio, fechaFin);
            log.info("Se encontraron {} pedidos para el reporte", pedidos.size());
            
            if (pedidos.isEmpty()) {
                log.warn("No se encontraron pedidos en el rango de fechas especificado");
            }

            try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Reporte de Pedidos");

            // Crear estilos
            CellStyle headerStyle = crearEstiloHeader(workbook);
            CellStyle dataStyle = crearEstiloData(workbook);

            // Crear encabezados
            Row headerRow = sheet.createRow(0);
            String[] headers = { "ID Pedido", "Cliente", "Correo", "Fecha", "Estado", "Total", "Productos",
                    "Método Pago", "Dirección" };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Llenar datos
            int rowNum = 1;
            for (ReportePedidosDTO pedido : pedidos) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(pedido.getIdPedido());
                row.createCell(1).setCellValue(pedido.getNombreCliente());
                row.createCell(2).setCellValue(pedido.getCorreoCliente());
                row.createCell(3)
                        .setCellValue(pedido.getFechaPedido().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                row.createCell(4).setCellValue(pedido.getEstado().getDescripcion());
                row.createCell(5).setCellValue(pedido.getTotal());
                row.createCell(6).setCellValue(pedido.getCantidadProductos());
                row.createCell(7).setCellValue(pedido.getMetodoPago());
                row.createCell(8).setCellValue(pedido.getDireccionEnvio());

                // Aplicar estilo a las celdas de datos
                for (int i = 0; i < headers.length; i++) {
                    row.getCell(i).setCellStyle(dataStyle);
                }
            }

            // Ajustar ancho de columnas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Convertir a bytes
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();

            } catch (IOException e) {
                log.error("Error de IO generando Excel de pedidos", e);
                throw new RuntimeException("Error generando reporte Excel: " + e.getMessage(), e);
            }
        } catch (Exception e) {
            log.error("Error inesperado generando Excel de pedidos desde {} hasta {}", fechaInicio, fechaFin, e);
            throw new RuntimeException("Error inesperado generando reporte Excel: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] generarExcelProductos() {
        List<ReporteProductosDTO> productos = generarReporteProductos();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Reporte de Productos");

            // Crear estilos
            CellStyle headerStyle = crearEstiloHeader(workbook);
            CellStyle dataStyle = crearEstiloData(workbook);

            // Crear encabezados
            Row headerRow = sheet.createRow(0);
            String[] headers = { "ID", "Nombre", "Descripción", "Precio", "Stock", "Categoría", "Vendido", "Ingresos",
                    "Estado" };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Llenar datos
            int rowNum = 1;
            for (ReporteProductosDTO producto : productos) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(producto.getIdProducto());
                row.createCell(1).setCellValue(producto.getNombre());
                row.createCell(2).setCellValue(producto.getDescripcion());
                row.createCell(3).setCellValue(producto.getPrecio());
                row.createCell(4).setCellValue(producto.getStock());
                row.createCell(5).setCellValue(producto.getCategoria());
                row.createCell(6).setCellValue(producto.getTotalVendido());
                row.createCell(7).setCellValue(producto.getIngresosTotales());
                row.createCell(8).setCellValue(producto.getEstado());

                // Aplicar estilo a las celdas de datos
                for (int i = 0; i < headers.length; i++) {
                    row.getCell(i).setCellStyle(dataStyle);
                }
            }

            // Ajustar ancho de columnas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Convertir a bytes
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            log.error("Error generando Excel de productos", e);
            throw new RuntimeException("Error generando reporte Excel", e);
        }
    }

    @Override
    public byte[] generarExcelVentas(LocalDate fechaInicio, LocalDate fechaFin) {
        List<ReporteVentasDTO> ventas = generarReporteVentas(fechaInicio, fechaFin);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Reporte de Ventas");

            // Crear estilos
            CellStyle headerStyle = crearEstiloHeader(workbook);
            CellStyle dataStyle = crearEstiloData(workbook);

            // Crear encabezados
            Row headerRow = sheet.createRow(0);
            String[] headers = { "Fecha", "Total Pedidos", "Total Ventas", "Promedio Venta", "Clientes Únicos",
                    "Producto Más Vendido" };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Llenar datos
            int rowNum = 1;
            for (ReporteVentasDTO venta : ventas) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(venta.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                row.createCell(1).setCellValue(venta.getTotalPedidos());
                row.createCell(2).setCellValue(venta.getTotalVentas());
                row.createCell(3).setCellValue(venta.getPromedioVenta());
                row.createCell(4).setCellValue(venta.getClientesUnicos());
                row.createCell(5).setCellValue(venta.getProductoMasVendido());

                // Aplicar estilo a las celdas de datos
                for (int i = 0; i < headers.length; i++) {
                    row.getCell(i).setCellStyle(dataStyle);
                }
            }

            // Ajustar ancho de columnas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Convertir a bytes
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            log.error("Error generando Excel de ventas", e);
            throw new RuntimeException("Error generando reporte Excel", e);
        }
    }

    @Override
    public byte[] generarExcelCompleto(LocalDate fechaInicio, LocalDate fechaFin) {
        try (Workbook workbook = new XSSFWorkbook()) {

            // Crear estilos
            CellStyle headerStyle = crearEstiloHeader(workbook);
            CellStyle dataStyle = crearEstiloData(workbook);

            // Hoja 1: Clientes
            crearHojaClientes(workbook, headerStyle, dataStyle);

            // Hoja 2: Pedidos
            crearHojaPedidos(workbook, headerStyle, dataStyle, fechaInicio, fechaFin);

            // Hoja 3: Productos
            crearHojaProductos(workbook, headerStyle, dataStyle);

            // Hoja 4: Ventas
            crearHojaVentas(workbook, headerStyle, dataStyle, fechaInicio, fechaFin);

            // Convertir a bytes
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            log.error("Error generando Excel completo", e);
            throw new RuntimeException("Error generando reporte Excel completo", e);
        }
    }

    // Métodos auxiliares

    /**
     * Convierte java.util.Date o java.sql.Date a LocalDateTime de forma segura
     */
    private LocalDateTime convertirDateALocalDateTime(java.util.Date fecha) {
        if (fecha == null) {
            return LocalDateTime.now();
        }
        
        if (fecha instanceof java.sql.Date) {
            // Para java.sql.Date, convertir a LocalDate primero y luego a LocalDateTime
            return ((java.sql.Date) fecha).toLocalDate().atStartOfDay();
        } else {
            // Para java.util.Date normal
            return fecha.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        }
    }

    /**
     * Convierte java.util.Date o java.sql.Date a LocalDate de forma segura
     */
    private LocalDate convertirDateALocalDate(java.util.Date fecha) {
        if (fecha == null) {
            return LocalDate.now();
        }
        
        if (fecha instanceof java.sql.Date) {
            // Para java.sql.Date
            return ((java.sql.Date) fecha).toLocalDate();
        } else {
            // Para java.util.Date normal
            return fecha.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        }
    }

    private ReporteClientesDTO convertirAReporteCliente(Usuario usuario) {
        // Calcular total gastado sumando los totales de pedidos
        double totalGastado = 0.0;
        int totalPedidos = 0;

        if (usuario.getPedidos() != null) {
            totalPedidos = usuario.getPedidos().size();
            totalGastado = usuario.getPedidos().stream()
                    .mapToDouble(this::calcularTotalPedido)
                    .sum();
        }

        return ReporteClientesDTO.builder()
                .idUsuario(usuario.getIdUsuario())
                .nombre(usuario.getNombre() + (usuario.getApellido() != null ? " " + usuario.getApellido() : ""))
                .correo(usuario.getCorreo())
                .telefono(usuario.getTelefono())
                .direccion(usuario.getDireccion())
                .totalPedidos(totalPedidos)
                .totalGastado(totalGastado)
                .fechaRegistro(LocalDateTime.now()) // Se puede agregar campo de fecha de registro
                .estado(usuario.getActivo() != null && usuario.getActivo() ? "Activo" : "Inactivo")
                .build();
    }

    private ReportePedidosDTO convertirAReportePedido(com.cafedronel.cafedronelbackend.data.model.Pedido pedido) {
        // Convertir Date a LocalDateTime de forma segura
        LocalDateTime fechaPedido = convertirDateALocalDateTime(pedido.getFecha());

        // Calcular total del pedido
        double total = calcularTotalPedido(pedido);

        // Contar productos
        int cantidadProductos = pedido.getDetalles() != null
                ? pedido.getDetalles().stream().mapToInt(DetallePedido::getCantidad).sum()
                : 0;

        // Obtener método de pago
        String metodoPago = "No especificado";
        if (pedido.getPago() != null && pedido.getPago().getMetodoPago() != null) {
            metodoPago = pedido.getPago().getMetodoPago();
        }

        // Obtener dirección de envío
        String direccionEnvio = "No especificada";
        if (pedido.getEnvio() != null && pedido.getEnvio().getDireccion() != null) {
            direccionEnvio = pedido.getEnvio().getDireccion();
        } else if (pedido.getDireccion() != null) {
            direccionEnvio = pedido.getDireccion();
        }

        // Obtener información del usuario de forma segura
        String nombreCliente = "Cliente no disponible";
        String correoCliente = "No disponible";
        
        if (pedido.getUsuario() != null) {
            nombreCliente = pedido.getUsuario().getNombre();
            if (pedido.getUsuario().getApellido() != null) {
                nombreCliente += " " + pedido.getUsuario().getApellido();
            }
            correoCliente = pedido.getUsuario().getCorreo() != null ? pedido.getUsuario().getCorreo() : "No disponible";
        }

        return ReportePedidosDTO.builder()
                .idPedido(pedido.getIdPedido())
                .nombreCliente(nombreCliente)
                .correoCliente(correoCliente)
                .fechaPedido(fechaPedido)
                .estado(pedido.getEstado())
                .total(total)
                .cantidadProductos(cantidadProductos)
                .metodoPago(metodoPago)
                .direccionEnvio(direccionEnvio)
                .build();
    }

    private ReporteProductosDTO convertirAReporteProducto(
            com.cafedronel.cafedronelbackend.data.model.Producto producto) {
        // Calcular total vendido y ingresos (esto requeriría una consulta más compleja
        // en un caso real)
        int totalVendido = calcularTotalVendidoProducto(producto.getIdProducto());
        double ingresosTotales = totalVendido * producto.getPrecio();

        String estado = producto.getStock() > 0 ? "Disponible" : "Agotado";
        if (producto.getActivo() != null && !producto.getActivo()) {
            estado = "Inactivo";
        }

        return ReporteProductosDTO.builder()
                .idProducto(producto.getIdProducto())
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .precio(producto.getPrecio())
                .stock(producto.getStock())
                .categoria(producto.getCategoria())
                .totalVendido(totalVendido)
                .ingresosTotales(ingresosTotales)
                .estado(estado)
                .build();
    }

    private double calcularTotalPedido(com.cafedronel.cafedronelbackend.data.model.Pedido pedido) {
        if (pedido.getDetalles() == null || pedido.getDetalles().isEmpty()) {
            return 0.0;
        }

        return pedido.getDetalles().stream()
                .mapToDouble(detalle -> detalle.getCantidad() * detalle.getPrecioUnitario())
                .sum();
    }

    private int calcularTotalVendidoProducto(Integer idProducto) {
        // En un caso real, esto sería una consulta a la base de datos
        // Por ahora retornamos un valor simulado
        return pedidoRepository.findAll().stream()
                .flatMap(pedido -> pedido.getDetalles().stream())
                .filter(detalle -> detalle.getProducto().getIdProducto().equals(idProducto))
                .mapToInt(DetallePedido::getCantidad)
                .sum();
    }

    private List<ReporteVentasDTO> generarReporteVentasPorFecha(
            List<com.cafedronel.cafedronelbackend.data.model.Pedido> pedidos, LocalDate fechaInicio,
            LocalDate fechaFin) {
        // Agrupar pedidos por fecha de forma segura
        Map<LocalDate, List<com.cafedronel.cafedronelbackend.data.model.Pedido>> pedidosPorFecha = pedidos.stream()
                .collect(Collectors.groupingBy(pedido -> convertirDateALocalDate(pedido.getFecha())));

        return pedidosPorFecha.entrySet().stream()
                .map(entry -> {
                    LocalDate fecha = entry.getKey();
                    List<com.cafedronel.cafedronelbackend.data.model.Pedido> pedidosDia = entry.getValue();

                    int totalPedidos = pedidosDia.size();
                    double totalVentas = pedidosDia.stream().mapToDouble(this::calcularTotalPedido).sum();
                    double promedioVenta = totalPedidos > 0 ? totalVentas / totalPedidos : 0.0;

                    int clientesUnicos = (int) pedidosDia.stream()
                            .map(pedido -> pedido.getUsuario().getIdUsuario())
                            .distinct()
                            .count();

                    String productoMasVendido = obtenerProductoMasVendidoDelDia(pedidosDia);

                    return ReporteVentasDTO.builder()
                            .fecha(fecha)
                            .totalPedidos(totalPedidos)
                            .totalVentas(totalVentas)
                            .promedioVenta(promedioVenta)
                            .clientesUnicos(clientesUnicos)
                            .productoMasVendido(productoMasVendido)
                            .build();
                })
                .sorted((a, b) -> a.getFecha().compareTo(b.getFecha()))
                .collect(Collectors.toList());
    }

    private String obtenerProductoMasVendidoDelDia(List<com.cafedronel.cafedronelbackend.data.model.Pedido> pedidos) {
        Map<String, Integer> ventasPorProducto = new HashMap<>();

        pedidos.stream()
                .flatMap(pedido -> pedido.getDetalles().stream())
                .forEach(detalle -> {
                    String nombreProducto = detalle.getProducto().getNombre();
                    ventasPorProducto.merge(nombreProducto, detalle.getCantidad(), Integer::sum);
                });

        return ventasPorProducto.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
    }

    private CellStyle crearEstiloHeader(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle crearEstiloData(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }

    private void crearHojaClientes(Workbook workbook, CellStyle headerStyle, CellStyle dataStyle) {
        Sheet sheet = workbook.createSheet("Clientes");
        List<ReporteClientesDTO> clientes = generarReporteClientes();

        // Crear encabezados
        Row headerRow = sheet.createRow(0);
        String[] headers = { "ID", "Nombre", "Correo", "Teléfono", "Dirección", "Total Pedidos", "Total Gastado",
                "Estado" };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Llenar datos
        int rowNum = 1;
        for (ReporteClientesDTO cliente : clientes) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(cliente.getIdUsuario());
            row.createCell(1).setCellValue(cliente.getNombre());
            row.createCell(2).setCellValue(cliente.getCorreo());
            row.createCell(3).setCellValue(cliente.getTelefono());
            row.createCell(4).setCellValue(cliente.getDireccion());
            row.createCell(5).setCellValue(cliente.getTotalPedidos());
            row.createCell(6).setCellValue(cliente.getTotalGastado());
            row.createCell(7).setCellValue(cliente.getEstado());

            // Aplicar estilo a las celdas de datos
            for (int i = 0; i < headers.length; i++) {
                row.getCell(i).setCellStyle(dataStyle);
            }
        }

        // Ajustar ancho de columnas
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void crearHojaPedidos(Workbook workbook, CellStyle headerStyle, CellStyle dataStyle, LocalDate fechaInicio,
            LocalDate fechaFin) {
        Sheet sheet = workbook.createSheet("Pedidos");
        List<ReportePedidosDTO> pedidos = generarReportePedidos(fechaInicio, fechaFin);

        // Crear encabezados
        Row headerRow = sheet.createRow(0);
        String[] headers = { "ID Pedido", "Cliente", "Correo", "Fecha", "Estado", "Total", "Productos", "Método Pago",
                "Dirección" };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Llenar datos
        int rowNum = 1;
        for (ReportePedidosDTO pedido : pedidos) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(pedido.getIdPedido());
            row.createCell(1).setCellValue(pedido.getNombreCliente());
            row.createCell(2).setCellValue(pedido.getCorreoCliente());
            row.createCell(3)
                    .setCellValue(pedido.getFechaPedido().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            row.createCell(4).setCellValue(pedido.getEstado().getDescripcion());
            row.createCell(5).setCellValue(pedido.getTotal());
            row.createCell(6).setCellValue(pedido.getCantidadProductos());
            row.createCell(7).setCellValue(pedido.getMetodoPago());
            row.createCell(8).setCellValue(pedido.getDireccionEnvio());

            // Aplicar estilo a las celdas de datos
            for (int i = 0; i < headers.length; i++) {
                row.getCell(i).setCellStyle(dataStyle);
            }
        }

        // Ajustar ancho de columnas
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void crearHojaProductos(Workbook workbook, CellStyle headerStyle, CellStyle dataStyle) {
        Sheet sheet = workbook.createSheet("Productos");
        List<ReporteProductosDTO> productos = generarReporteProductos();

        // Crear encabezados
        Row headerRow = sheet.createRow(0);
        String[] headers = { "ID", "Nombre", "Descripción", "Precio", "Stock", "Categoría", "Vendido", "Ingresos",
                "Estado" };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Llenar datos
        int rowNum = 1;
        for (ReporteProductosDTO producto : productos) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(producto.getIdProducto());
            row.createCell(1).setCellValue(producto.getNombre());
            row.createCell(2).setCellValue(producto.getDescripcion());
            row.createCell(3).setCellValue(producto.getPrecio());
            row.createCell(4).setCellValue(producto.getStock());
            row.createCell(5).setCellValue(producto.getCategoria());
            row.createCell(6).setCellValue(producto.getTotalVendido());
            row.createCell(7).setCellValue(producto.getIngresosTotales());
            row.createCell(8).setCellValue(producto.getEstado());

            // Aplicar estilo a las celdas de datos
            for (int i = 0; i < headers.length; i++) {
                row.getCell(i).setCellStyle(dataStyle);
            }
        }

        // Ajustar ancho de columnas
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void crearHojaVentas(Workbook workbook, CellStyle headerStyle, CellStyle dataStyle, LocalDate fechaInicio,
            LocalDate fechaFin) {
        Sheet sheet = workbook.createSheet("Ventas");
        List<ReporteVentasDTO> ventas = generarReporteVentas(fechaInicio, fechaFin);

        // Crear encabezados
        Row headerRow = sheet.createRow(0);
        String[] headers = { "Fecha", "Total Pedidos", "Total Ventas", "Promedio Venta", "Clientes Únicos",
                "Producto Más Vendido" };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Llenar datos
        int rowNum = 1;
        for (ReporteVentasDTO venta : ventas) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(venta.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            row.createCell(1).setCellValue(venta.getTotalPedidos());
            row.createCell(2).setCellValue(venta.getTotalVentas());
            row.createCell(3).setCellValue(venta.getPromedioVenta());
            row.createCell(4).setCellValue(venta.getClientesUnicos());
            row.createCell(5).setCellValue(venta.getProductoMasVendido());

            // Aplicar estilo a las celdas de datos
            for (int i = 0; i < headers.length; i++) {
                row.getCell(i).setCellStyle(dataStyle);
            }
        }

        // Ajustar ancho de columnas
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}