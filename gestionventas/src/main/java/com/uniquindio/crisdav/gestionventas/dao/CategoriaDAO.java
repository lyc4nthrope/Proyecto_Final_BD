package com.uniquindio.crisdav.gestionventas.dao;

import com.uniquindio.crisdav.gestionventas.models.entity.Categoria;
import com.uniquindio.crisdav.gestionventas.utils.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    public Categoria insertar(Categoria categoria) throws SQLException {
        String sql = "INSERT INTO CATEGORIA (nombre, iva, utilidad) VALUES (?, ?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, new String[]{"id_categoria"})) {
            
            pstmt.setString(1, categoria.getNombre());
            pstmt.setBigDecimal(2, categoria.getIva());
            pstmt.setBigDecimal(3, categoria.getUtilidad());
            
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        categoria.setIdCategoria(rs.getInt(1));
                    }
                }
            }
            
            return categoria;
        }
    }

    public Categoria buscarPorId(Integer id) throws SQLException {
        String sql = "SELECT * FROM CATEGORIA WHERE id_categoria = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearCategoria(rs);
                }
            }
        }
        
        return null;
    }

    public Categoria buscarPorNombre(String nombre) throws SQLException {
        String sql = "SELECT * FROM CATEGORIA WHERE nombre = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nombre);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearCategoria(rs);
                }
            }
        }
        
        return null;
    }

    public List<Categoria> listarTodas() throws SQLException {
        String sql = "SELECT * FROM CATEGORIA ORDER BY nombre";
        List<Categoria> categorias = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                categorias.add(mapearCategoria(rs));
            }
        }
        
        return categorias;
    }

    public boolean actualizar(Categoria categoria) throws SQLException {
        String sql = "UPDATE CATEGORIA SET nombre = ?, iva = ?, utilidad = ? WHERE id_categoria = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, categoria.getNombre());
            pstmt.setBigDecimal(2, categoria.getIva());
            pstmt.setBigDecimal(3, categoria.getUtilidad());
            pstmt.setInt(4, categoria.getIdCategoria());
            
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean eliminar(Integer id) throws SQLException {
        String sql = "DELETE FROM CATEGORIA WHERE id_categoria = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    private Categoria mapearCategoria(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria();
        categoria.setIdCategoria(rs.getInt("id_categoria"));
        categoria.setNombre(rs.getString("nombre"));
        categoria.setIva(rs.getBigDecimal("iva"));
        categoria.setUtilidad(rs.getBigDecimal("utilidad"));
        return categoria;
    }
}