package com.uniquindio.crisdav.gestionventas.dao;

import com.uniquindio.crisdav.gestionventas.models.vo.*;
import com.uniquindio.crisdav.gestionventas.utils.DBUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReporteDAO {

    /**
     * Genera el reporte de factura completo para una venta
     */
    public FacturaVO generarFactura(Integer idVenta) throws SQLException {
        String sqlVenta = "SELECT v.*, c.nombre as nombre_cliente, c.cedula, ve.nombre as nombre_vendedor " +
                         "FROM VENTA v " +
                         "INNER JOIN CLIENTE c ON v.id_cliente = c.id_cliente " +
                         "INNER JOIN VENDEDOR ve ON v.id_vendedor = ve.id_vendedor " +
                         "WHERE v.id_venta = ?";
        
        String sqlDetalles = "SELECT dv.*, p.codigo, p.nombre as nombre_producto " +
                            "FROM DETALLE_VENTA dv " +
                            "INNER JOIN PRODUCTO p ON dv.id_producto = p.id_producto " +
                            "WHERE dv.id_venta = ?";
        
        FacturaVO factura = new FacturaVO();
        
        try (Connection conn = DBUtil.getConnection()) {
            // Obtener información de la venta
            try (PreparedStatement pstmt = conn.prepareStatement(sqlVenta)) {
                pstmt.setInt(1, idVenta);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        factura.setIdVenta(rs.getInt("id_venta"));
                        factura.setFecha(rs.getDate("fecha").toLocalDate());
                        factura.setNombreCliente(rs.getString("nombre_cliente"));
                        factura.setCedulaCliente(rs.getString("cedula"));
                        factura.setNombreVendedor(rs.getString("nombre_vendedor"));
                        factura.setTipoVenta(rs.getString("tipo_venta"));
                        factura.setSubtotal(rs.getBigDecimal("subtotal"));
                        factura.setTotalIva(rs.getBigDecimal("total_iva"));
                        factura.setTotal(rs.getBigDecimal("total"));
                    }
                }
            }
            
            // Obtener detalles de la venta
            try (PreparedStatement pstmt = conn.prepareStatement(sqlDetalles)) {
                pstmt.setInt(1, idVenta);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        ItemFacturaVO item = new ItemFacturaVO(
                            rs.getString("codigo"),
                            rs.getString("nombre_producto"),
                            rs.getInt("cantidad"),
                            rs.getBigDecimal("precio_unitario"),
                            rs.getBigDecimal("iva"),
                            rs.getBigDecimal("subtotal")
                        );
                        factura.addItem(item);
                    }
                }
            }
        }
        
        return factura;
    }

    /**
     * Obtiene el total de ventas durante un mes específico
     */
    public BigDecimal obtenerTotalVentasMes(int anio, int mes) throws SQLException {
        String sql = "SELECT COALESCE(SUM(total), 0) as total_mes " +
                    "FROM VENTA " +
                    "WHERE EXTRACT(YEAR FROM fecha) = ? AND EXTRACT(MONTH FROM fecha) = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, anio);
            pstmt.setInt(2, mes);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("total_mes");
                }
            }
        }
        
        return BigDecimal.ZERO;
    }

    /**
     * Genera reporte de IVA por categoría durante un trimestre
     */
    public List<ReporteIvaVO> generarReporteIvaTrimestral(int anio, int trimestre) throws SQLException {
        // Calcular meses del trimestre
        int mesInicio = (trimestre - 1) * 3 + 1;
        int mesFin = mesInicio + 2;
        
        String sql = "SELECT cat.nombre as categoria, " +
                    "       cat.iva, " +
                    "       COALESCE(SUM(dv.subtotal), 0) as total_ventas, " +
                    "       COALESCE(SUM(dv.subtotal * cat.iva), 0) as total_iva " +
                    "FROM CATEGORIA cat " +
                    "LEFT JOIN PRODUCTO p ON cat.id_categoria = p.id_categoria " +
                    "LEFT JOIN DETALLE_VENTA dv ON p.id_producto = dv.id_producto " +
                    "LEFT JOIN VENTA v ON dv.id_venta = v.id_venta " +
                    "WHERE v.fecha IS NULL OR " +
                    "      (EXTRACT(YEAR FROM v.fecha) = ? AND " +
                    "       EXTRACT(MONTH FROM v.fecha) BETWEEN ? AND ?) " +
                    "GROUP BY cat.nombre, cat.iva " +
                    "ORDER BY cat.nombre";
        
        List<ReporteIvaVO> reportes = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, anio);
            pstmt.setInt(2, mesInicio);
            pstmt.setInt(3, mesFin);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ReporteIvaVO reporte = new ReporteIvaVO(
                        rs.getString("categoria"),
                        rs.getBigDecimal("total_ventas"),
                        rs.getBigDecimal("iva"),
                        rs.getBigDecimal("total_iva")
                    );
                    reportes.add(reporte);
                }
            }
        }
        
        return reportes;
    }

    /**
     * Determina cantidad de ventas por tipo durante un periodo
     */
    public EstadisticaVentasVO obtenerEstadisticasVentas(LocalDate fechaInicio, LocalDate fechaFin) throws SQLException {
        String sql = "SELECT tipo_venta, " +
                    "       COUNT(*) as cantidad, " +
                    "       COALESCE(SUM(total), 0) as monto_total " +
                    "FROM VENTA " +
                    "WHERE fecha BETWEEN ? AND ? " +
                    "GROUP BY tipo_venta";
        
        int cantidadContado = 0;
        int cantidadCredito = 0;
        BigDecimal montoContado = BigDecimal.ZERO;
        BigDecimal montoCredito = BigDecimal.ZERO;
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(fechaInicio));
            pstmt.setDate(2, Date.valueOf(fechaFin));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String tipo = rs.getString("tipo_venta");
                    int cantidad = rs.getInt("cantidad");
                    BigDecimal monto = rs.getBigDecimal("monto_total");
                    
                    if ("Contado".equalsIgnoreCase(tipo)) {
                        cantidadContado = cantidad;
                        montoContado = monto;
                    } else if ("Credito".equalsIgnoreCase(tipo)) {
                        cantidadCredito = cantidad;
                        montoCredito = monto;
                    }
                }
            }
        }
        
        String periodo = fechaInicio + " a " + fechaFin;
        return new EstadisticaVentasVO(periodo, cantidadContado, cantidadCredito, montoContado, montoCredito);
    }

    /**
     * Obtiene inventario de productos por categoría con costo asociado
     */
    public List<InventarioVO> obtenerInventarioPorCategoria() throws SQLException {
        String sql = "SELECT cat.nombre as categoria, " +
                    "       p.codigo, " +
                    "       p.nombre as nombre_producto, " +
                    "       p.stock, " +
                    "       p.valor_adquisicion " +
                    "FROM PRODUCTO p " +
                    "INNER JOIN CATEGORIA cat ON p.id_categoria = cat.id_categoria " +
                    "ORDER BY cat.nombre, p.nombre";
        
        List<InventarioVO> inventario = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                InventarioVO item = new InventarioVO(
                    rs.getString("categoria"),
                    rs.getString("codigo"),
                    rs.getString("nombre_producto"),
                    rs.getInt("stock"),
                    rs.getBigDecimal("valor_adquisicion")
                );
                inventario.add(item);
            }
        }
        
        return inventario;
    }

    /**
     * Genera reporte de clientes morosos (con cuotas vencidas)
     */
    public List<ClienteMorosoVO> obtenerClientesMorosos() throws SQLException {
        String sql = "SELECT DISTINCT c.cedula, " +
                    "       c.nombre, " +
                    "       c.telefono, " +
                    "       vc.id_venta_credito, " +
                    "       v.fecha as fecha_venta, " +
                    "       COUNT(cu.id_cuota) as cuotas_vencidas, " +
                    "       COALESCE(SUM(cu.valor_cuota), 0) as total_vencido, " +
                    "       vc.saldo_pendiente, " +
                    "       MAX(cu.fecha_vencimiento) as fecha_ultima_vencida " +
                    "FROM CLIENTE c " +
                    "INNER JOIN VENTA v ON c.id_cliente = v.id_cliente " +
                    "INNER JOIN VENTA_CREDITO vc ON v.id_venta = vc.id_venta " +
                    "INNER JOIN CUOTA cu ON vc.id_venta_credito = cu.id_venta_credito " +
                    "WHERE cu.estado = 'Vencida' " +
                    "GROUP BY c.cedula, c.nombre, c.telefono, vc.id_venta_credito, v.fecha, vc.saldo_pendiente " +
                    "ORDER BY fecha_ultima_vencida DESC";
        
        List<ClienteMorosoVO> morosos = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                ClienteMorosoVO moroso = new ClienteMorosoVO(
                    rs.getString("cedula"),
                    rs.getString("nombre"),
                    rs.getString("telefono"),
                    rs.getInt("id_venta_credito"),
                    rs.getDate("fecha_venta").toLocalDate(),
                    rs.getInt("cuotas_vencidas"),
                    rs.getBigDecimal("total_vencido"),
                    rs.getBigDecimal("saldo_pendiente"),
                    rs.getDate("fecha_ultima_vencida").toLocalDate()
                );
                morosos.add(moroso);
            }
        }
        
        return morosos;
    }

    /**
     * Obtiene el total de IVA general en un periodo
     */
    public BigDecimal obtenerTotalIvaPeriodo(LocalDate fechaInicio, LocalDate fechaFin) throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_iva), 0) as total_iva " +
                    "FROM VENTA " +
                    "WHERE fecha BETWEEN ? AND ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(fechaInicio));
            pstmt.setDate(2, Date.valueOf(fechaFin));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("total_iva");
                }
            }
        }
        
        return BigDecimal.ZERO;
    }
}