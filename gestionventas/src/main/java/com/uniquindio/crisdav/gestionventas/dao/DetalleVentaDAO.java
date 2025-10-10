package com.uniquindio.crisdav.gestionventas.dao;

import com.uniquindio.crisdav.gestionventas.models.entity.DetalleVenta;
import com.uniquindio.crisdav.gestionventas.utils.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetalleVentaDAO {

    public DetalleVenta insertar(DetalleVenta detalle) throws SQLException {
        String sql = "INSERT INTO DETALLE_VENTA (id_venta, id_producto, cantidad, precio_unitario, iva, subtotal) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, new String[]{"id_detalle_venta"})) {
            
            pstmt.setInt(1, detalle.getIdVenta());
            pstmt.setInt(2, detalle.getIdProducto());
            pstmt.setInt(3, detalle.getCantidad());
            pstmt.setBigDecimal(4, detalle.getPrecioUnitario());
            pstmt.setBigDecimal(5, detalle.getIva());
            pstmt.setBigDecimal(6, detalle.getSubtotal());
            
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        detalle.setIdDetalleVenta(rs.getInt(1));
                    }
                }
            }
            
            return detalle;
        }
    }

    public List<DetalleVenta> listarPorVenta(Integer idVenta) throws SQLException {
        String sql = "SELECT * FROM DETALLE_VENTA WHERE id_venta = ?";
        List<DetalleVenta> detalles = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idVenta);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    detalles.add(mapearDetalle(rs));
                }
            }
        }
        
        return detalles;
    }

    private DetalleVenta mapearDetalle(ResultSet rs) throws SQLException {
        DetalleVenta detalle = new DetalleVenta();
        detalle.setIdDetalleVenta(rs.getInt("id_detalle_venta"));
        detalle.setIdVenta(rs.getInt("id_venta"));
        detalle.setIdProducto(rs.getInt("id_producto"));
        detalle.setCantidad(rs.getInt("cantidad"));
        detalle.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
        detalle.setIva(rs.getBigDecimal("iva"));
        detalle.setSubtotal(rs.getBigDecimal("subtotal"));
        return detalle;
    }
}