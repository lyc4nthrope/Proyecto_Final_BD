package com.uniquindio.crisdav.gestionventas.dao.impl;

import com.uniquindio.crisdav.gestionventas.models.entity.Usuario;
import com.uniquindio.crisdav.gestionventas.utils.DBUtil;
import com.uniquindio.crisdav.gestionventas.dao.UsuarioDAO;
import java.sql.*;
import java.time.LocalDateTime;

public class UsuarioDAOImpl implements UsuarioDAO {

    private static final String SEQ = "SEQ_USUARIO";

    @Override
    public Usuario findByUsername(String username) throws Exception {
        String sql = "SELECT id, username, password_hash, role, activo, last_login, intentos_fallidos, created_at, updated_at " +
                     "FROM usuario WHERE username = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getLong("id"));
                    u.setUsername(rs.getString("username"));
                    u.setPasswordHash(rs.getString("password_hash"));
                    String role = rs.getString("role");
                    if (role != null) u.setRole(com.uniquindio.crisdav.gestionventas.models.enums.UserRole.valueOf(role));
                    u.setActivo(rs.getBoolean("activo"));
                    Timestamp ts = rs.getTimestamp("last_login");
                    if (ts != null) u.setLastLogin(ts.toLocalDateTime());
                    u.setIntentosFallidos(rs.getInt("intentos_fallidos"));
                    // createdAt/updatedAt mapping (assume DATE -> java.sql.Date)
                    Date ca = rs.getDate("created_at");
                    if (ca != null) u.setCreatedAt(ca.toLocalDate());
                    Date ua = rs.getDate("updated_at");
                    if (ua != null) u.setUpdatedAt(ua.toLocalDate());
                    return u;
                }
                return null;
            }
        }
    }

    @Override
    public Usuario findById(Long id) throws Exception {
        String sql = "SELECT id, username, password_hash, role, activo, last_login, intentos_fallidos, created_at, updated_at " +
                     "FROM usuario WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getLong("id"));
                    u.setUsername(rs.getString("username"));
                    u.setPasswordHash(rs.getString("password_hash"));
                    String role = rs.getString("role");
                    if (role != null) u.setRole(com.uniquindio.crisdav.gestionventas.models.enums.UserRole.valueOf(role));
                    u.setActivo(rs.getBoolean("activo"));
                    Timestamp ts = rs.getTimestamp("last_login");
                    if (ts != null) u.setLastLogin(ts.toLocalDateTime());
                    u.setIntentosFallidos(rs.getInt("intentos_fallidos"));
                    Date ca = rs.getDate("created_at");
                    if (ca != null) u.setCreatedAt(ca.toLocalDate());
                    Date ua = rs.getDate("updated_at");
                    if (ua != null) u.setUpdatedAt(ua.toLocalDate());
                    return u;
                }
                return null;
            }
        }
    }

    private Long getNextVal(Connection conn) throws SQLException {
        String sql = "SELECT " + SEQ + ".NEXTVAL FROM DUAL";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            throw new SQLException("No se pudo obtener NEXTVAL de " + SEQ);
        }
    }

    @Override
    public Long insert(Usuario usuario) throws Exception {
        String insert = "INSERT INTO usuario (id, username, password_hash, role, activo, last_login, intentos_fallidos, created_at, updated_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection()) {
            Long id = getNextVal(conn);
            try (PreparedStatement ps = conn.prepareStatement(insert)) {
                ps.setLong(1, id);
                ps.setString(2, usuario.getUsername());
                ps.setString(3, usuario.getPasswordHash());
                ps.setString(4, usuario.getRole() != null ? usuario.getRole().name() : null);
                ps.setBoolean(5, usuario.isActivo());
                if (usuario.getLastLogin() != null) ps.setTimestamp(6, Timestamp.valueOf(usuario.getLastLogin()));
                else ps.setNull(6, Types.TIMESTAMP);
                ps.setInt(7, usuario.getIntentosFallidos() != null ? usuario.getIntentosFallidos() : 0);
                ps.setDate(8, usuario.getCreatedAt() != null ? Date.valueOf(usuario.getCreatedAt()) : Date.valueOf(java.time.LocalDate.now()));
                ps.setDate(9, usuario.getUpdatedAt() != null ? Date.valueOf(usuario.getUpdatedAt()) : Date.valueOf(java.time.LocalDate.now()));
                ps.executeUpdate();
                return id;
            }
        }
    }

    @Override
    public void update(Usuario usuario) throws Exception {
        String sql = "UPDATE usuario SET password_hash = ?, role = ?, activo = ?, last_login = ?, intentos_fallidos = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario.getPasswordHash());
            ps.setString(2, usuario.getRole() != null ? usuario.getRole().name() : null);
            ps.setBoolean(3, usuario.isActivo());
            if (usuario.getLastLogin() != null) ps.setTimestamp(4, Timestamp.valueOf(usuario.getLastLogin()));
            else ps.setNull(4, Types.TIMESTAMP);
            ps.setInt(5, usuario.getIntentosFallidos() != null ? usuario.getIntentosFallidos() : 0);
            ps.setDate(6, usuario.getUpdatedAt() != null ? Date.valueOf(usuario.getUpdatedAt()) : Date.valueOf(java.time.LocalDate.now()));
            ps.setLong(7, usuario.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void updateLastLogin(Long usuarioId, LocalDateTime lastLogin) throws Exception {
        String sql = "UPDATE usuario SET last_login = ?, intentos_fallidos = 0, updated_at = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(lastLogin));
            ps.setDate(2, Date.valueOf(java.time.LocalDate.now()));
            ps.setLong(3, usuarioId);
            ps.executeUpdate();
        }
    }

    @Override
    public void incrementFailedAttempts(Long usuarioId) throws Exception {
        String sql = "UPDATE usuario SET intentos_fallidos = NVL(intentos_fallidos,0) + 1, updated_at = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(java.time.LocalDate.now()));
            ps.setLong(2, usuarioId);
            ps.executeUpdate();
        }
    }

    @Override
    public void resetFailedAttempts(Long usuarioId) throws Exception {
        String sql = "UPDATE usuario SET intentos_fallidos = 0, updated_at = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(java.time.LocalDate.now()));
            ps.setLong(2, usuarioId);
            ps.executeUpdate();
        }
    }

    @Override
    public void setActive(Long usuarioId, boolean activo) throws Exception {
        String sql = "UPDATE usuario SET activo = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, activo);
            ps.setDate(2, Date.valueOf(java.time.LocalDate.now()));
            ps.setLong(3, usuarioId);
            ps.executeUpdate();
        }
    }
}

