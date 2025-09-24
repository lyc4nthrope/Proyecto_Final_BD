package com.uniquindio.crisdav.gestionventas.dao.impl;

import com.uniquindio.crisdav.gestionventas.models.entity.Auditoria;
import com.uniquindio.crisdav.gestionventas.utils.DBUtil;
import com.uniquindio.crisdav.gestionventas.dao.AuditoriaDAO;

import java.sql.*;

public class AuditoriaDAOImpl implements AuditoriaDAO {

    private static final String SEQ = "SEQ_AUDITORIA";

    private Long getNextVal(Connection conn) throws SQLException {
        String sql = "SELECT " + SEQ + ".NEXTVAL FROM DUAL";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getLong(1);
            throw new SQLException("No NEXTVAL para " + SEQ);
        }
    }

    @Override
    public Long insert(Auditoria a) throws Exception {
        String sql = "INSERT INTO auditoria (id, timestamp, user_id, username, accion, exito, ip_address, detalle) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection()) {
            Long id = getNextVal(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, id);
                ps.setTimestamp(2, a.getTimestamp() != null ? Timestamp.valueOf(a.getTimestamp()) : new Timestamp(System.currentTimeMillis()));
                if (a.getUserId() != null) ps.setLong(3, a.getUserId()); else ps.setNull(3, Types.NUMERIC);
                ps.setString(4, a.getUsername());
                ps.setString(5, a.getAccion() != null ? a.getAccion().name() : null);
                ps.setBoolean(6, a.isExito());
                ps.setString(7, a.getIpAddress());
                ps.setString(8, a.getDetalle());
                ps.executeUpdate();
                return id;
            }
        }
    }
}
