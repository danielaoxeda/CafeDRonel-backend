package com.cafedronel.cafedronelbackend.controllers.reporte;

import com.cafedronel.cafedronelbackend.data.dto.reporte.ReporteClientesDTO;
import com.cafedronel.cafedronelbackend.data.dto.reporte.ReportePedidosDTO;
import com.cafedronel.cafedronelbackend.data.dto.reporte.ReporteProductosDTO;
import com.cafedronel.cafedronelbackend.data.dto.reporte.ReporteVentasDTO;
import com.cafedronel.cafedronelbackend.services.reporte.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
@Tag(name = "Reportes", description = "API para generación de reportes y exportación a Excel")
public class ReporteController {

    private final ReporteService reporteService;

    // ========== REPORTES JSON ==========

    @GetMapping("/clientes")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Obtener reporte de clientes", description = "Genera un reporte con estadísticas de todos los clientes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reporte generado exitosamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<List<ReporteClientesDTO>> obtenerReporteClientes() {
        List<ReporteClientesDTO> reporte = reporteService.generarReporteClientes();
        return ResponseEntity.ok(reporte);
    }

    @GetMapping("/pedidos")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Obtener reporte de pedidos", description = "Genera un reporte de pedidos en un rango de fechas")
    public ResponseEntity<List<ReportePedidosDTO>> obtenerReportePedidos(
            @Parameter(description = "Fecha de inicio (formato: yyyy-MM-dd)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @Parameter(description = "Fecha de fin (formato: yyyy-MM-dd)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        
        List<ReportePedidosDTO> reporte = reporteService.generarReportePedidos(fechaInicio, fechaFin);
        return ResponseEntity.ok(reporte);
    }

    @GetMapping("/productos")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Obtener reporte de productos", description = "Genera un reporte con estadísticas de productos y ventas")
    public ResponseEntity<List<ReporteProductosDTO>> obtenerReporteProductos() {
        List<ReporteProductosDTO> reporte = reporteService.generarReporteProductos();
        return ResponseEntity.ok(reporte);
    }

    @GetMapping("/ventas")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Obtener reporte de ventas", description = "Genera un reporte de ventas diarias en un rango de fechas")
    public ResponseEntity<List<ReporteVentasDTO>> obtenerReporteVentas(
            @Parameter(description = "Fecha de inicio (formato: yyyy-MM-dd)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @Parameter(description = "Fecha de fin (formato: yyyy-MM-dd)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        
        List<ReporteVentasDTO> reporte = reporteService.generarReporteVentas(fechaInicio, fechaFin);
        return ResponseEntity.ok(reporte);
    }

    // ========== EXPORTACIÓN A EXCEL ==========

    @GetMapping("/excel/clientes")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Descargar reporte de clientes en Excel", description = "Genera y descarga un archivo Excel con el reporte de clientes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Archivo Excel generado exitosamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "500", description = "Error generando el archivo")
    })
    public ResponseEntity<byte[]> descargarExcelClientes() {
        byte[] excelData = reporteService.generarExcelClientes();
        
        String fileName = "reporte_clientes_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName);
        headers.setContentLength(excelData.length);
        
        return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
    }

    @GetMapping("/excel/pedidos")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Descargar reporte de pedidos en Excel", description = "Genera y descarga un archivo Excel con el reporte de pedidos")
    public ResponseEntity<byte[]> descargarExcelPedidos(
            @Parameter(description = "Fecha de inicio (formato: yyyy-MM-dd)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @Parameter(description = "Fecha de fin (formato: yyyy-MM-dd)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        
        byte[] excelData = reporteService.generarExcelPedidos(fechaInicio, fechaFin);
        
        String fileName = "reporte_pedidos_" + fechaInicio.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + 
                         "_" + fechaFin.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName);
        headers.setContentLength(excelData.length);
        
        return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
    }

    @GetMapping("/excel/productos")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Descargar reporte de productos en Excel", description = "Genera y descarga un archivo Excel con el reporte de productos")
    public ResponseEntity<byte[]> descargarExcelProductos() {
        byte[] excelData = reporteService.generarExcelProductos();
        
        String fileName = "reporte_productos_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName);
        headers.setContentLength(excelData.length);
        
        return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
    }

    @GetMapping("/excel/ventas")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Descargar reporte de ventas en Excel", description = "Genera y descarga un archivo Excel con el reporte de ventas")
    public ResponseEntity<byte[]> descargarExcelVentas(
            @Parameter(description = "Fecha de inicio (formato: yyyy-MM-dd)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @Parameter(description = "Fecha de fin (formato: yyyy-MM-dd)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        
        byte[] excelData = reporteService.generarExcelVentas(fechaInicio, fechaFin);
        
        String fileName = "reporte_ventas_" + fechaInicio.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + 
                         "_" + fechaFin.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName);
        headers.setContentLength(excelData.length);
        
        return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
    }

    @GetMapping("/excel/completo")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Descargar reporte completo en Excel", 
               description = "Genera y descarga un archivo Excel con todos los reportes en diferentes hojas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Archivo Excel completo generado exitosamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "500", description = "Error generando el archivo")
    })
    public ResponseEntity<byte[]> descargarExcelCompleto(
            @Parameter(description = "Fecha de inicio para reportes con fechas (formato: yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @Parameter(description = "Fecha de fin para reportes con fechas (formato: yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        
        // Si no se proporcionan fechas, usar el último mes
        if (fechaInicio == null) {
            fechaInicio = LocalDate.now().minusMonths(1);
        }
        if (fechaFin == null) {
            fechaFin = LocalDate.now();
        }
        
        byte[] excelData = reporteService.generarExcelCompleto(fechaInicio, fechaFin);
        
        String fileName = "reporte_completo_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName);
        headers.setContentLength(excelData.length);
        
        return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
    }

    // ========== ENDPOINTS DE UTILIDAD ==========

    @GetMapping("/disponibles")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Listar reportes disponibles", description = "Obtiene una lista de todos los reportes disponibles")
    public ResponseEntity<List<String>> listarReportesDisponibles() {
        List<String> reportes = List.of(
                "Clientes - Estadísticas generales de clientes",
                "Pedidos - Reporte de pedidos por rango de fechas",
                "Productos - Estadísticas de productos y ventas",
                "Ventas - Reporte de ventas diarias",
                "Completo - Todos los reportes en un solo archivo Excel"
        );
        
        return ResponseEntity.ok(reportes);
    }

    @GetMapping("/formatos")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Listar formatos disponibles", description = "Obtiene una lista de formatos de exportación disponibles")
    public ResponseEntity<List<String>> listarFormatosDisponibles() {
        List<String> formatos = List.of(
                "JSON - Respuesta JSON para integración con APIs",
                "Excel (.xlsx) - Archivo Excel para análisis y presentación"
        );
        
        return ResponseEntity.ok(formatos);
    }
}