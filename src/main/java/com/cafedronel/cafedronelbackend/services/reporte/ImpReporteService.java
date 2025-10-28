package com.cafedronel.cafedronelbackend.services.reporte;

import com.cafedronel.cafedronelbackend.data.dto.reporte.ReporteClientesDTO;
import com.cafedronel.cafedronelbackend.data.dto.reporte.ReportePedidosDTO;
import com.cafedronel.cafedronelbackend.data.dto.reporte.ReporteProductosDTO;
import com.cafedronel.cafedronelbackend.data.dto.reporte.ReporteVentasDTO;
import com.cafedronel.cafedronelbackend.data.model.Rol;
import com.cafedronel.cafedronelbackend.data.model.Usuario;
import com.cafedronel.cafedronelbackend.repository.PedidoRepository;
import com.cafedronel.cafedronelbackend.repository.ProductoRepository;
import com.cafedronel.cafedronelbackend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        // Por ahora retornamos una lista vacía, se puede implementar con consultas
        // específicas
        return new ArrayList<>();
    }

    @Override
    public List<ReporteProductosDTO> generarReporteProductos() {
        // Por ahora retornamos una lista vacía, se puede implementar con consultas
        // específicas
        return new ArrayList<>();
    }

    @Override
    public List<ReporteVentasDTO> generarReporteVentas(LocalDate fechaInicio, LocalDate fechaFin) {
        // Por ahora retornamos una lista vacía, se puede implementar con consultas
        // específicas
        return new ArrayList<>();
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
        List<ReportePedidosDTO> pedidos = generarReportePedidos(fechaInicio, fechaFin);

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
                row.createCell(4).setCellValue(pedido.getEstado());
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
            log.error("Error generando Excel de pedidos", e);
            throw new RuntimeException("Error generando reporte Excel", e);
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

    private ReporteClientesDTO convertirAReporteCliente(Usuario usuario) {
        return ReporteClientesDTO.builder()
                .idUsuario(usuario.getIdUsuario())
                .nombre(usuario.getNombre())
                .correo(usuario.getCorreo())
                .telefono(usuario.getTelefono())
                .direccion(usuario.getDireccion())
                .totalPedidos(usuario.getPedidos() != null ? usuario.getPedidos().size() : 0)
                .totalGastado(0.0) // Se puede calcular sumando los totales de pedidos
                .fechaRegistro(LocalDateTime.now()) // Se puede agregar campo de fecha de registro
                .estado("Activo") // Se puede determinar basado en actividad reciente
                .build();
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

        // Ajustar ancho de columnas
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}