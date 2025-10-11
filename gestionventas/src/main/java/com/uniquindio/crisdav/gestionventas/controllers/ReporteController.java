package com.uniquindio.crisdav.gestionventas.controllers;

import com.uniquindio.crisdav.gestionventas.dao.ReporteDAO;
import com.uniquindio.crisdav.gestionventas.models.vo.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ReporteController {
    
    private final ReporteDAO reporteDAO;

    public ReporteController() {
        this.reporteDAO = new ReporteDAO();
    }

    /**
     * Genera la factura completa de una venta
     */
    public FacturaVO generarFactura(Integer idVenta) throws SQLException {
        if (idVenta == null) {
            throw new IllegalArgumentException("ID de venta no puede ser null");
        }
        
        return reporteDAO.generarFactura(idVenta);
    }

    /**
     * Obtiene el total de ventas de un mes específico
     */
    public BigDecimal obtenerTotalVentasMes(int anio, int mes) throws SQLException {
        if (mes < 1 || mes > 12) {
            throw new IllegalArgumentException("El mes debe estar entre 1 y 12");
        }
        
        return reporteDAO.obtenerTotalVentasMes(anio, mes);
    }

    /**
     * Genera reporte de IVA trimestral
     */
    public List<ReporteIvaVO> generarReporteIvaTrimestral(int anio, int trimestre) throws SQLException {
        if (trimestre < 1 || trimestre > 4) {
            throw new IllegalArgumentException("El trimestre debe estar entre 1 y 4");
        }
        
        return reporteDAO.generarReporteIvaTrimestral(anio, trimestre);
    }

    /**
     * Obtiene el total de IVA a pagar en un periodo
     */
    public BigDecimal obtenerTotalIvaPeriodo(LocalDate fechaInicio, LocalDate fechaFin) throws SQLException {
        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio debe ser anterior a la fecha fin");
        }
        
        return reporteDAO.obtenerTotalIvaPeriodo(fechaInicio, fechaFin);
    }

    /**
     * Genera estadísticas de ventas por tipo (contado vs crédito)
     */
    public EstadisticaVentasVO obtenerEstadisticasVentas(LocalDate fechaInicio, LocalDate fechaFin) throws SQLException {
        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio debe ser anterior a la fecha fin");
        }
        
        return reporteDAO.obtenerEstadisticasVentas(fechaInicio, fechaFin);
    }

    /**
     * Obtiene el inventario de productos por categoría
     */
    public List<InventarioVO> obtenerInventarioPorCategoria() throws SQLException {
        return reporteDAO.obtenerInventarioPorCategoria();
    }

    /**
     * Genera reporte de clientes morosos
     */
    public List<ClienteMorosoVO> obtenerClientesMorosos() throws SQLException {
        return reporteDAO.obtenerClientesMorosos();
    }

    /**
     * Calcula el total de IVA de un trimestre (suma de todas las categorías)
     */
    public BigDecimal calcularTotalIvaTrimestre(int anio, int trimestre) throws SQLException {
        List<ReporteIvaVO> reportes = generarReporteIvaTrimestral(anio, trimestre);
        
        BigDecimal total = BigDecimal.ZERO;
        for (ReporteIvaVO reporte : reportes) {
            total = total.add(reporte.getTotalIvaCategoria());
        }
        
        return total;
    }
}