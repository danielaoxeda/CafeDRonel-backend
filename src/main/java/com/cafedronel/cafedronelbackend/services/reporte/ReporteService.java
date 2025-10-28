package com.cafedronel.cafedronelbackend.services.reporte;

import com.cafedronel.cafedronelbackend.data.dto.reporte.ReporteClientesDTO;
import com.cafedronel.cafedronelbackend.data.dto.reporte.ReportePedidosDTO;
import com.cafedronel.cafedronelbackend.data.dto.reporte.ReporteProductosDTO;
import com.cafedronel.cafedronelbackend.data.dto.reporte.ReporteVentasDTO;

import java.time.LocalDate;
import java.util.List;

public interface ReporteService {
    
    /**
     * Genera reporte de clientes con estadísticas
     */
    List<ReporteClientesDTO> generarReporteClientes();
    
    /**
     * Genera reporte de pedidos en un rango de fechas
     */
    List<ReportePedidosDTO> generarReportePedidos(LocalDate fechaInicio, LocalDate fechaFin);
    
    /**
     * Genera reporte de productos con estadísticas de ventas
     */
    List<ReporteProductosDTO> generarReporteProductos();
    
    /**
     * Genera reporte de ventas diarias en un rango de fechas
     */
    List<ReporteVentasDTO> generarReporteVentas(LocalDate fechaInicio, LocalDate fechaFin);
    
    /**
     * Genera archivo Excel con reporte de clientes
     */
    byte[] generarExcelClientes();
    
    /**
     * Genera archivo Excel con reporte de pedidos
     */
    byte[] generarExcelPedidos(LocalDate fechaInicio, LocalDate fechaFin);
    
    /**
     * Genera archivo Excel con reporte de productos
     */
    byte[] generarExcelProductos();
    
    /**
     * Genera archivo Excel con reporte de ventas
     */
    byte[] generarExcelVentas(LocalDate fechaInicio, LocalDate fechaFin);
    
    /**
     * Genera archivo Excel con reporte completo (todas las hojas)
     */
    byte[] generarExcelCompleto(LocalDate fechaInicio, LocalDate fechaFin);
}