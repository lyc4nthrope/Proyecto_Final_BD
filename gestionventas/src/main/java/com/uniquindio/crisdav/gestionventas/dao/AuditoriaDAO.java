package com.uniquindio.crisdav.gestionventas.dao;

import com.uniquindio.crisdav.gestionventas.models.entity.Auditoria;
import com.uniquindio.crisdav.gestionventas.models.enums.AccionAuditoria;
import com.uniquindio.crisdav.gestionventas.utils.DBUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AuditoriaDAO {

    public Auditoria insertar(Auditoria auditoria) throws SQLException {
        String sql = "INSERT INTO AUDITORIA (id_usuario, accion, fecha_hora) VALUES (?, ?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, new String[]{"id_auditoria"})) {
            
            pstmt.setInt(1, auditoria.getIdUsuario());
            pstmt.setString(2, auditoria.getAccion().getValor());
            pstmt.setTimestamp(3, Timestamp.valueOf(auditoria.getFechaHora()));
            
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        auditoria.setIdAuditoria(rs.getInt(1));
                    }
                }
            }
            
            return auditoria;
        }
    }

    public List<Auditoria> listarPorUsuario(Integer idUsuario) throws SQLException {
        String sql = "SELECT * FROM AUDITORIA WHERE id_usuario = ? ORDER BY fecha_hora DESC";
        List<Auditoria> auditorias = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idUsuario);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    auditorias.add(mapearAuditoria(rs));
                }
            }
        }
        
        return auditorias;
    }

    public List<Auditoria> listarPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) throws SQLException {
        String sql = "SELECT * FROM AUDITORIA WHERE fecha_hora BETWEEN ? AND ? ORDER BY fecha_hora DESC";
        List<Auditoria> auditorias = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setTimestamp(1, Timestamp.valueOf(fechaInicio));
            pstmt.setTimestamp(2, Timestamp.valueOf(fechaFin));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    auditorias.add(mapearAuditoria(rs));
                }
            }
        }
        
        return auditorias;
    }

    private Auditoria mapearAuditoria(ResultSet rs) throws SQLException {
        Auditoria auditoria = new Auditoria();
        auditoria.setIdAuditoria(rs.getInt("id_auditoria"));
        auditoria.setIdUsuario(rs.getInt("id_usuario"));
        auditoria.setAccion(AccionAuditoria.fromString(rs.getString("accion")));
        auditoria.setFechaHora(rs.getTimestamp("fecha_hora").toLocalDateTime());
        return auditoria;
    }
}