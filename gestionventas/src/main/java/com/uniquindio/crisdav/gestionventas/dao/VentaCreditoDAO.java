package com.uniquindio.crisdav.gestionventas.dao;

import com.uniquindio.crisdav.gestionventas.models.entity.VentaCredito;
import com.uniquindio.crisdav.gestionventas.utils.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VentaCreditoDAO {

    public VentaCredito insertar(VentaCredito ventaCredito) throws SQLException {
        String sql = "INSERT INTO VENTA_CREDITO (id_venta, cuota_inicial, saldo_financiado, interes_aplicado, num_cuotas, valor_cuota, saldo_pendiente, fecha_limite_pago) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, new String[]{"id_venta_credito"})) {
            
            pstmt.setInt(1, ventaCredito.getIdVenta());
            pstmt.setBigDecimal(2, ventaCredito.getCuotaInicial());
            pstmt.setBigDecimal(3, ventaCredito.getSaldoFinanciado());
            pstmt.setBigDecimal(4, ventaCredito.getInteresAplicado());
            pstmt.setInt(5, ventaCredito.getNumCuotas());
            pstmt.setBigDecimal(6, ventaCredito.getValorCuota());
            pstmt.setBigDecimal(7, ventaCredito.getSaldoPendiente());
            
            if (ventaCredito.getFechaLimitePago() != null) {
                pstmt.setDate(8, Date.valueOf(ventaCredito.getFechaLimitePago()));
            } else {
                pstmt.setNull(8, Types.DATE);
            }
            
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        ventaCredito.setIdVentaCredito(rs.getInt(1));
                    }
                }
            }
            
            return ventaCredito;
        }
    }

    public VentaCredito buscarPorId(Integer id) throws SQLException {
        String sql = "SELECT * FROM VENTA_CREDITO WHERE id_venta_credito = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearVentaCredito(rs);
                }
            }
        }
        
        return null;
    }

    public VentaCredito buscarPorIdVenta(Integer idVenta) throws SQLException {
        String sql = "SELECT * FROM VENTA_CREDITO WHERE id_venta = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idVenta);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearVentaCredito(rs);
                }
            }
        }
        
        return null;
    }

    public List<VentaCredito> listarTodas() throws SQLException {
        String sql = "SELECT * FROM VENTA_CREDITO ORDER BY fecha_limite_pago";
        List<VentaCredito> ventasCredito = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                ventasCredito.add(mapearVentaCredito(rs));
            }
        }
        
        return ventasCredito;
    }

    public List<VentaCredito> listarConSaldoPendiente() throws SQLException {
        String sql = "SELECT * FROM VENTA_CREDITO WHERE saldo_pendiente > 0 ORDER BY fecha_limite_pago";
        List<VentaCredito> ventasCredito = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                ventasCredito.add(mapearVentaCredito(rs));
            }
        }
        
        return ventasCredito;
    }

    public boolean actualizarSaldoPendiente(Integer idVentaCredito, java.math.BigDecimal nuevoSaldo) throws SQLException {
        String sql = "UPDATE VENTA_CREDITO SET saldo_pendiente = ? WHERE id_venta_credito = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBigDecimal(1, nuevoSaldo);
            pstmt.setInt(2, idVentaCredito);
            
            return pstmt.executeUpdate() > 0;
        }
    }

    private VentaCredito mapearVentaCredito(ResultSet rs) throws SQLException {
        VentaCredito ventaCredito = new VentaCredito();
        ventaCredito.setIdVentaCredito(rs.getInt("id_venta_credito"));
        ventaCredito.setIdVenta(rs.getInt("id_venta"));
        ventaCredito.setCuotaInicial(rs.getBigDecimal("cuota_inicial"));
        ventaCredito.setSaldoFinanciado(rs.getBigDecimal("saldo_financiado"));
        ventaCredito.setInteresAplicado(rs.getBigDecimal("interes_aplicado"));
        ventaCredito.setNumCuotas(rs.getInt("num_cuotas"));
        ventaCredito.setValorCuota(rs.getBigDecimal("valor_cuota"));
        ventaCredito.setSaldoPendiente(rs.getBigDecimal("saldo_pendiente"));
        
        Date fechaLimite = rs.getDate("fecha_limite_pago");
        if (fechaLimite != null) {
            ventaCredito.setFechaLimitePago(fechaLimite.toLocalDate());
        }
        
        return ventaCredito;
    }
}