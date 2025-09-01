package com.uniquindio.crisdav.gestionventas.dao;

import com.uniquindio.crisdav.gestionventas.model.Usuario;
import com.uniquindio.crisdav.gestionventas.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la entidad Usuario. Permite realizar operaciones CRUD básicas sobre la tabla USUARIO.
 */
public class UsuarioDAO {

    public void insertar(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO USUARIO (id_usuario, username, password, rol) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuario.getIdUsuario());
            ps.setString(2, usuario.getUsername());
            ps.setString(3, usuario.getPassword());
            ps.setString(4, usuario.getRol());
            ps.executeUpdate();
        }
    }

    public void actualizar(Usuario usuario) throws SQLException {
        String sql = "UPDATE USUARIO SET username=?, password=?, rol=? WHERE id_usuario=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario.getUsername());
            ps.setString(2, usuario.getPassword());
            ps.setString(3, usuario.getRol());
            ps.setInt(4, usuario.getIdUsuario());
            ps.executeUpdate();
        }
    }

    public void eliminar(int idUsuario) throws SQLException {
        String sql = "DELETE FROM USUARIO WHERE id_usuario=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
        }
    }

    public Usuario buscarPorId(int idUsuario) throws SQLException {
        String sql = "SELECT * FROM USUARIO WHERE id_usuario=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("rol")
                    );
                }
            }
        }
        return null;
    }

    public List<Usuario> listarTodos() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM USUARIO";
        try (Connection conn = DBUtil.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Usuario usuario = new Usuario(
                    rs.getInt("id_usuario"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("rol")
                );
                lista.add(usuario);
            }
        }
        return lista;
    }
}
