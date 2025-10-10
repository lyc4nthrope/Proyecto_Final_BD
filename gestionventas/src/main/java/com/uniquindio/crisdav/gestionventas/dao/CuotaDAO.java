package com.uniquindio.crisdav.gestionventas.dao;

import com.uniquindio.crisdav.gestionventas.models.entity.Cuota;
import com.uniquindio.crisdav.gestionventas.models.enums.EstadoCuota;
import com.uniquindio.crisdav.gestionventas.utils.DBUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CuotaDAO {

    public Cuota insertar(Cuota cuota) throws SQLException {
        String sql = "INSERT INTO CUOTA (id_venta_credito, num_cuota, fecha_vencimiento, fecha_pago, valor_cuota, estado) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, new String[]{"id_cuota"})) {
            
            pstmt.setInt(1, cuota.getIdVentaCredito());
            pstmt.setInt(2, cuota.getNumCuota());
            pstmt.setDate(3, Date.valueOf(cuota.getFechaVencimiento()));
            
            if (cuota.getFechaPago() != null) {
                pstmt.setDate(4, Date.valueOf(cuota.getFechaPago()));
            } else {
                pstmt.setNull(4, Types.DATE);
            }
            
            pstmt.setBigDecimal(5, cuota.getValorCuota());
            pstmt.setString(6, cuota.getEstado().getValor());
            
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        cuota.setIdCuota(rs.getInt(1));
                    }
                }
            }
            
            return cuota;
        }
    }

    public Cuota buscarPorId(Integer id) throws SQLException {
        String sql = "SELECT * FROM CUOTA WHERE id_cuota = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearCuota(rs);
                }
            }
        }
        
        return null;
    }

    public List<Cuota> listarPorVentaCredito(Integer idVentaCredito) throws SQLException {
        String sql = "SELECT * FROM CUOTA WHERE id_venta_credito = ? ORDER BY num_cuota";
        List<Cuota> cuotas = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idVentaCredito);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    cuotas.add(mapearCuota(rs));
                }
            }
        }
        
        return cuotas;
    }

    public List<Cuota> listarPendientesPorVentaCredito(Integer idVentaCredito) throws SQLException {
        String sql = "SELECT * FROM CUOTA WHERE id_venta_credito = ? AND estado = 'Pendiente' ORDER BY num_cuota";
        List<Cuota> cuotas = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idVentaCredito);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    cuotas.add(mapearCuota(rs));
                }
            }
        }
        
        return cuotas;
    }

    public List<Cuota> listarVencidas() throws SQLException {
        String sql = "SELECT * FROM CUOTA WHERE estado = 'Vencida' ORDER BY fecha_vencimiento";
        List<Cuota> cuotas = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                cuotas.add(mapearCuota(rs));
            }
        }
        
        return cuotas;
    }

    public boolean actualizarEstado(Integer idCuota, EstadoCuota nuevoEstado, LocalDate fechaPago) throws SQLException {
        String sql = "UPDATE CUOTA SET estado = ?, fecha_pago = ? WHERE id_cuota = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nuevoEstado.getValor());
            
            if (fechaPago != null) {
                pstmt.setDate(2, Date.valueOf(fechaPago));
            } else {
                pstmt.setNull(2, Types.DATE);
            }
            
            pstmt.setInt(3, idCuota);
            
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean marcarVencidas() throws SQLException {
        String sql = "UPDATE CUOTA SET estado = 'Vencida' WHERE estado = 'Pendiente' AND fecha_vencimiento < CURRENT_DATE";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            return pstmt.executeUpdate() > 0;
        }
    }

    private Cuota mapearCuota(ResultSet rs) throws SQLException {
        Cuota cuota = new Cuota();
        cuota.setIdCuota(rs.getInt("id_cuota"));
        cuota.setIdVentaCredito(rs.getInt("id_venta_credito"));
        cuota.setNumCuota(rs.getInt("num_cuota"));
        cuota.setFechaVencimiento(rs.getDate("fecha_vencimiento").toLocalDate());
        
        Date fechaPago = rs.getDate("fecha_pago");
        if (fechaPago != null) {
            cuota.setFechaPago(fechaPago.toLocalDate());
        }
        
        cuota.setValorCuota(rs.getBigDecimal("valor_cuota"));
        cuota.setEstado(EstadoCuota.fromString(rs.getString("estado")));
        return cuota;
    }
}