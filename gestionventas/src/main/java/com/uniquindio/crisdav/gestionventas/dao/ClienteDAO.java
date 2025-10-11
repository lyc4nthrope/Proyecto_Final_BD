package com.uniquindio.crisdav.gestionventas.dao;

import com.uniquindio.crisdav.gestionventas.models.entity.Cliente;
import com.uniquindio.crisdav.gestionventas.utils.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public Cliente insertar(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO CLIENTE (cedula, nombre, direccion, telefono, correo, tiene_credito_activo) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, new String[]{"id_cliente"})) {
            
            pstmt.setString(1, cliente.getCedula());
            pstmt.setString(2, cliente.getNombre());
            pstmt.setString(3, cliente.getDireccion());
            pstmt.setString(4, cliente.getTelefono());
            pstmt.setString(5, cliente.getCorreo());
            pstmt.setInt(6, cliente.getTieneCreditoActivo() ? 1 : 0);
            
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        cliente.setIdCliente(rs.getInt(1));
                    }
                }
            }
            
            return cliente;
        }
    }

    public Cliente buscarPorId(Integer id) throws SQLException {
        String sql = "SELECT * FROM CLIENTE WHERE id_cliente = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearCliente(rs);
                }
            }
        }
        
        return null;
    }

    public Cliente buscarPorCedula(String cedula) throws SQLException {
        String sql = "SELECT * FROM CLIENTE WHERE cedula = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, cedula);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearCliente(rs);
                }
            }
        }
        
        return null;
    }

    public List<Cliente> listarTodos() throws SQLException {
        String sql = "SELECT * FROM CLIENTE ORDER BY nombre";
        List<Cliente> clientes = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                clientes.add(mapearCliente(rs));
            }
        }
        
        return clientes;
    }

    public List<Cliente> listarConCreditoActivo() throws SQLException {
        String sql = "SELECT * FROM CLIENTE WHERE tiene_credito_activo = 1 ORDER BY nombre";
        List<Cliente> clientes = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                clientes.add(mapearCliente(rs));
            }
        }
        
        return clientes;
    }

    public boolean actualizar(Cliente cliente) throws SQLException {
        String sql = "UPDATE CLIENTE SET cedula = ?, nombre = ?, direccion = ?, telefono = ?, correo = ?, tiene_credito_activo = ? WHERE id_cliente = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, cliente.getCedula());
            pstmt.setString(2, cliente.getNombre());
            pstmt.setString(3, cliente.getDireccion());
            pstmt.setString(4, cliente.getTelefono());
            pstmt.setString(5, cliente.getCorreo());
            pstmt.setInt(6, cliente.getTieneCreditoActivo() ? 1 : 0);
            pstmt.setInt(7, cliente.getIdCliente());
            
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean actualizarEstadoCredito(Integer idCliente, boolean tieneCredito) throws SQLException {
        String sql = "UPDATE CLIENTE SET tiene_credito_activo = ? WHERE id_cliente = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tieneCredito ? 1 : 0);
            pstmt.setInt(2, idCliente);
            
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean eliminar(Integer id) throws SQLException {
        String sql = "DELETE FROM CLIENTE WHERE id_cliente = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(rs.getInt("id_cliente"));
        cliente.setCedula(rs.getString("cedula"));
        cliente.setNombre(rs.getString("nombre"));
        cliente.setDireccion(rs.getString("direccion"));
        cliente.setTelefono(rs.getString("telefono"));
        cliente.setCorreo(rs.getString("correo"));
        cliente.setTieneCreditoActivo(rs.getInt("tiene_credito_activo") == 1);
        return cliente;
    }
}