package com.uniquindio.crisdav.gestionventas.dao;

import com.uniquindio.crisdav.gestionventas.models.entity.VentaContado;
import com.uniquindio.crisdav.gestionventas.utils.DBUtil;

import java.sql.*;

public class VentaContadoDAO {

    public VentaContado insertar(VentaContado ventaContado) throws SQLException {
        String sql = "INSERT INTO VENTA_CONTADO (id_venta, monto_pagado) VALUES (?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, new String[]{"id_venta_contado"})) {
            
            pstmt.setInt(1, ventaContado.getIdVenta());
            pstmt.setBigDecimal(2, ventaContado.getMontoPagado());
            
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        ventaContado.setIdVentaContado(rs.getInt(1));
                    }
                }
            }
            
            return ventaContado;
        }
    }

    public VentaContado buscarPorIdVenta(Integer idVenta) throws SQLException {
        String sql = "SELECT * FROM VENTA_CONTADO WHERE id_venta = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idVenta);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearVentaContado(rs);
                }
            }
        }
        
        return null;
    }

    private VentaContado mapearVentaContado(ResultSet rs) throws SQLException {
        VentaContado ventaContado = new VentaContado();
        ventaContado.setIdVentaContado(rs.getInt("id_venta_contado"));
        ventaContado.setIdVenta(rs.getInt("id_venta"));
        ventaContado.setMontoPagado(rs.getBigDecimal("monto_pagado"));
        return ventaContado;
    }
}