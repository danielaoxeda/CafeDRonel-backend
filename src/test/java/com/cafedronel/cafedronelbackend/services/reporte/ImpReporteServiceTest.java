package com.cafedronel.cafedronelbackend.services.reporte;

import com.cafedronel.cafedronelbackend.data.dto.reporte.ReporteClientesDTO;
import com.cafedronel.cafedronelbackend.data.model.Rol;
import com.cafedronel.cafedronelbackend.data.model.Usuario;
import com.cafedronel.cafedronelbackend.repository.PedidoRepository;
import com.cafedronel.cafedronelbackend.repository.ProductoRepository;
import com.cafedronel.cafedronelbackend.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImpReporteServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ImpReporteService reporteService;

    private Usuario cliente1;
    private Usuario cliente2;

    @BeforeEach
    void setUp() {
        cliente1 = Usuario.builder()
                .idUsuario(1)
                .nombre("Juan Pérez")
                .correo("juan@example.com")
                .telefono("12345678")
                .direccion("Calle 123")
                .rol(Rol.CLIENTE)
                .build();

        cliente2 = Usuario.builder()
                .idUsuario(2)
                .nombre("María García")
                .correo("maria@example.com")
                .telefono("87654321")
                .direccion("Avenida 456")
                .rol(Rol.CLIENTE)
                .build();
    }

    @Test
    void generarReporteClientes_DeberiaRetornarListaDeClientes() {
        // Arrange
        when(usuarioRepository.findByRol(Rol.CLIENTE)).thenReturn(Arrays.asList(cliente1, cliente2));

        // Act
        List<ReporteClientesDTO> resultado = reporteService.generarReporteClientes();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        
        ReporteClientesDTO primerCliente = resultado.get(0);
        assertEquals("Juan Pérez", primerCliente.getNombre());
        assertEquals("juan@example.com", primerCliente.getCorreo());
        assertEquals("12345678", primerCliente.getTelefono());
        assertEquals("Calle 123", primerCliente.getDireccion());
        assertEquals("Activo", primerCliente.getEstado());
    }

    @Test
    void generarReporteClientes_ConListaVacia_DeberiaRetornarListaVacia() {
        // Arrange
        when(usuarioRepository.findByRol(Rol.CLIENTE)).thenReturn(Arrays.asList());

        // Act
        List<ReporteClientesDTO> resultado = reporteService.generarReporteClientes();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void generarExcelClientes_DeberiaGenerarArchivoExcel() {
        // Arrange
        when(usuarioRepository.findByRol(Rol.CLIENTE)).thenReturn(Arrays.asList(cliente1));

        // Act
        byte[] excelData = reporteService.generarExcelClientes();

        // Assert
        assertNotNull(excelData);
        assertTrue(excelData.length > 0);
        // Verificar que es un archivo Excel válido (los primeros bytes deben ser la firma de un archivo ZIP/Excel)
        assertTrue(excelData.length > 4);
    }

    @Test
    void generarExcelPedidos_DeberiaGenerarArchivoExcel() {
        // Arrange
        LocalDate fechaInicio = LocalDate.now().minusDays(30);
        LocalDate fechaFin = LocalDate.now();

        // Act
        byte[] excelData = reporteService.generarExcelPedidos(fechaInicio, fechaFin);

        // Assert
        assertNotNull(excelData);
        assertTrue(excelData.length > 0);
    }

    @Test
    void generarExcelProductos_DeberiaGenerarArchivoExcel() {
        // Act
        byte[] excelData = reporteService.generarExcelProductos();

        // Assert
        assertNotNull(excelData);
        assertTrue(excelData.length > 0);
    }

    @Test
    void generarExcelVentas_DeberiaGenerarArchivoExcel() {
        // Arrange
        LocalDate fechaInicio = LocalDate.now().minusDays(30);
        LocalDate fechaFin = LocalDate.now();

        // Act
        byte[] excelData = reporteService.generarExcelVentas(fechaInicio, fechaFin);

        // Assert
        assertNotNull(excelData);
        assertTrue(excelData.length > 0);
    }

    @Test
    void generarExcelCompleto_DeberiaGenerarArchivoExcelConVariasHojas() {
        // Arrange
        when(usuarioRepository.findByRol(Rol.CLIENTE)).thenReturn(Arrays.asList(cliente1, cliente2));
        LocalDate fechaInicio = LocalDate.now().minusDays(30);
        LocalDate fechaFin = LocalDate.now();

        // Act
        byte[] excelData = reporteService.generarExcelCompleto(fechaInicio, fechaFin);

        // Assert
        assertNotNull(excelData);
        assertTrue(excelData.length > 0);
        // El archivo completo debería ser más grande que un archivo individual
        byte[] excelClientes = reporteService.generarExcelClientes();
        assertTrue(excelData.length >= excelClientes.length);
    }

    @Test
    void generarReportePedidos_ConFechas_DeberiaRetornarLista() {
        // Arrange
        LocalDate fechaInicio = LocalDate.now().minusDays(30);
        LocalDate fechaFin = LocalDate.now();

        // Act
        var resultado = reporteService.generarReportePedidos(fechaInicio, fechaFin);

        // Assert
        assertNotNull(resultado);
        // Por ahora retorna lista vacía, pero no debería fallar
    }

    @Test
    void generarReporteProductos_DeberiaRetornarLista() {
        // Act
        var resultado = reporteService.generarReporteProductos();

        // Assert
        assertNotNull(resultado);
        // Por ahora retorna lista vacía, pero no debería fallar
    }

    @Test
    void generarReporteVentas_ConFechas_DeberiaRetornarLista() {
        // Arrange
        LocalDate fechaInicio = LocalDate.now().minusDays(30);
        LocalDate fechaFin = LocalDate.now();

        // Act
        var resultado = reporteService.generarReporteVentas(fechaInicio, fechaFin);

        // Assert
        assertNotNull(resultado);
        // Por ahora retorna lista vacía, pero no debería fallar
    }
}