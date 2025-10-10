package com.uniquindio.crisdav.gestionventas.dao;

import com.uniquindio.crisdav.gestionventas.models.entity.Venta;
import com.uniquindio.crisdav.gestionventas.models.enums.TipoVenta;
import com.uniquindio.crisdav.gestionventas.utils.DBUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO {

    public Venta insertar(Venta venta) throws SQLException {
        String sql = "INSERT INTO VENTA (tipo_venta, fecha, id_cliente, id_vendedor, id_usuario, subtotal, total_iva, total) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, new String[]{"id_venta"})) {
            
            pstmt.setString(1, venta.getTipoVenta().getValor());
            pstmt.setDate(2, Date.valueOf(venta.getFecha()));
            pstmt.setInt(3, venta.getIdCliente());
            pstmt.setInt(4, venta.getIdVendedor());
            pstmt.setInt(5, venta.getIdUsuario());
            pstmt.setBigDecimal(6, venta.getSubtotal());
            pstmt.setBigDecimal(7, venta.getTotalIva());
            pstmt.setBigDecimal(8, venta.getTotal());
            
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        venta.setIdVenta(rs.getInt(1));
                    }
                }
            }
            
            return venta;
        }
    }

    public Venta buscarPorId(Integer id) throws SQLException {
        String sql = "SELECT * FROM VENTA WHERE id_venta = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearVenta(rs);
                }
            }
        }
        
        return null;
    }

    public List<Venta> listarTodas() throws SQLException {
        String sql = "SELECT * FROM VENTA ORDER BY fecha DESC";
        List<Venta> ventas = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                ventas.add(mapearVenta(rs));
            }
        }
        
        return ventas;
    }

    public List<Venta> listarPorCliente(Integer idCliente) throws SQLException {
        String sql = "SELECT * FROM VENTA WHERE id_cliente = ? ORDER BY fecha DESC";
        List<Venta> ventas = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idCliente);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ventas.add(mapearVenta(rs));
                }
            }
        }
        
        return ventas;
    }

    public List<Venta> listarPorFecha(LocalDate fechaInicio, LocalDate fechaFin) throws SQLException {
        String sql = "SELECT * FROM VENTA WHERE fecha BETWEEN ? AND ? ORDER BY fecha DESC";
        List<Venta> ventas = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(fechaInicio));
            pstmt.setDate(2, Date.valueOf(fechaFin));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ventas.add(mapearVenta(rs));
                }
            }
        }
        
        return ventas;
    }

    public List<Venta> listarPorTipo(TipoVenta tipo, LocalDate fechaInicio, LocalDate fechaFin) throws SQLException {
        String sql = "SELECT * FROM VENTA WHERE tipo_venta = ? AND fecha BETWEEN ? AND ? ORDER BY fecha DESC";
        List<Venta> ventas = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, tipo.getValor());
            pstmt.setDate(2, Date.valueOf(fechaInicio));
            pstmt.setDate(3, Date.valueOf(fechaFin));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ventas.add(mapearVenta(rs));
                }
            }
        }
        
        return ventas;
    }

    private Venta mapearVenta(ResultSet rs) throws SQLException {
        Venta venta = new Venta();
        venta.setIdVenta(rs.getInt("id_venta"));
        venta.setTipoVenta(TipoVenta.fromString(rs.getString("tipo_venta")));
        venta.setFecha(rs.getDate("fecha").toLocalDate());
        venta.setIdCliente(rs.getInt("id_cliente"));
        venta.setIdVendedor(rs.getInt("id_vendedor"));
        venta.setIdUsuario(rs.getInt("id_usuario"));
        venta.setSubtotal(rs.getBigDecimal("subtotal"));
        venta.setTotalIva(rs.getBigDecimal("total_iva"));
        venta.setTotal(rs.getBigDecimal("total"));
        return venta;
    }
}