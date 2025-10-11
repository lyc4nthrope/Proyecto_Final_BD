package com.uniquindio.crisdav.gestionventas.dao;

import com.uniquindio.crisdav.gestionventas.models.entity.Vendedor;
import com.uniquindio.crisdav.gestionventas.utils.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VendedorDAO {

    public Vendedor insertar(Vendedor vendedor) throws SQLException {
        String sql = "INSERT INTO VENDEDOR (nombre, telefono, correo) VALUES (?, ?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, new String[]{"id_vendedor"})) {
            
            pstmt.setString(1, vendedor.getNombre());
            pstmt.setString(2, vendedor.getTelefono());
            pstmt.setString(3, vendedor.getCorreo());
            
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        vendedor.setIdVendedor(rs.getInt(1));
                    }
                }
            }
            
            return vendedor;
        }
    }

    public Vendedor buscarPorId(Integer id) throws SQLException {
        String sql = "SELECT * FROM VENDEDOR WHERE id_vendedor = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearVendedor(rs);
                }
            }
        }
        
        return null;
    }

    public List<Vendedor> listarTodos() throws SQLException {
        String sql = "SELECT * FROM VENDEDOR ORDER BY nombre";
        List<Vendedor> vendedores = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                vendedores.add(mapearVendedor(rs));
            }
        }
        
        return vendedores;
    }

    public boolean actualizar(Vendedor vendedor) throws SQLException {
        String sql = "UPDATE VENDEDOR SET nombre = ?, telefono = ?, correo = ? WHERE id_vendedor = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, vendedor.getNombre());
            pstmt.setString(2, vendedor.getTelefono());
            pstmt.setString(3, vendedor.getCorreo());
            pstmt.setInt(4, vendedor.getIdVendedor());
            
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean eliminar(Integer id) throws SQLException {
        String sql = "DELETE FROM VENDEDOR WHERE id_vendedor = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    private Vendedor mapearVendedor(ResultSet rs) throws SQLException {
        Vendedor vendedor = new Vendedor();
        vendedor.setIdVendedor(rs.getInt("id_vendedor"));
        vendedor.setNombre(rs.getString("nombre"));
        vendedor.setTelefono(rs.getString("telefono"));
        vendedor.setCorreo(rs.getString("correo"));
        return vendedor;
    }
}