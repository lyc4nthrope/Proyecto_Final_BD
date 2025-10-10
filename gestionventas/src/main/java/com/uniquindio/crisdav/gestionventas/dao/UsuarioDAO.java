package com.uniquindio.crisdav.gestionventas.dao;

import com.uniquindio.crisdav.gestionventas.models.entity.Usuario;
import com.uniquindio.crisdav.gestionventas.models.enums.EstadoUsuario;
import com.uniquindio.crisdav.gestionventas.models.enums.NivelUsuario;
import com.uniquindio.crisdav.gestionventas.utils.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public Usuario insertar(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO USUARIO (username, password, nivel, estado) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, new String[]{"id_usuario"})) {
            
            pstmt.setString(1, usuario.getUsername());
            pstmt.setString(2, usuario.getPassword());
            pstmt.setString(3, usuario.getNivel().getValor());
            pstmt.setString(4, usuario.getEstado().getValor());
            
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        usuario.setIdUsuario(rs.getInt(1));
                    }
                }
            }
            
            return usuario;
        }
    }

    public Usuario buscarPorId(Integer id) throws SQLException {
        String sql = "SELECT * FROM USUARIO WHERE id_usuario = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }
        }
        
        return null;
    }

    public Usuario buscarPorUsername(String username) throws SQLException {
        String sql = "SELECT * FROM USUARIO WHERE username = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }
        }
        
        return null;
    }

    public List<Usuario> listarTodos() throws SQLException {
        String sql = "SELECT * FROM USUARIO ORDER BY username";
        List<Usuario> usuarios = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
        }
        
        return usuarios;
    }

    public List<Usuario> listarActivos() throws SQLException {
        String sql = "SELECT * FROM USUARIO WHERE estado = 'Activo' ORDER BY username";
        List<Usuario> usuarios = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
        }
        
        return usuarios;
    }

    public boolean actualizar(Usuario usuario) throws SQLException {
        String sql = "UPDATE USUARIO SET username = ?, password = ?, nivel = ?, estado = ? WHERE id_usuario = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, usuario.getUsername());
            pstmt.setString(2, usuario.getPassword());
            pstmt.setString(3, usuario.getNivel().getValor());
            pstmt.setString(4, usuario.getEstado().getValor());
            pstmt.setInt(5, usuario.getIdUsuario());
            
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean actualizarPassword(Integer idUsuario, String nuevoPasswordHash) throws SQLException {
        String sql = "UPDATE USUARIO SET password = ? WHERE id_usuario = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nuevoPasswordHash);
            pstmt.setInt(2, idUsuario);
            
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean cambiarEstado(Integer idUsuario, EstadoUsuario nuevoEstado) throws SQLException {
        String sql = "UPDATE USUARIO SET estado = ? WHERE id_usuario = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nuevoEstado.getValor());
            pstmt.setInt(2, idUsuario);
            
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean eliminar(Integer id) throws SQLException {
        String sql = "DELETE FROM USUARIO WHERE id_usuario = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean existeUsername(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM USUARIO WHERE username = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(rs.getInt("id_usuario"));
        usuario.setUsername(rs.getString("username"));
        usuario.setPassword(rs.getString("password"));
        usuario.setNivel(NivelUsuario.fromString(rs.getString("nivel")));
        usuario.setEstado(EstadoUsuario.fromString(rs.getString("estado")));
        return usuario;
    }
}