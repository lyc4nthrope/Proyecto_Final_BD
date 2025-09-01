package com.uniquindio.crisdav.gestionventas.dao;

import com.uniquindio.crisdav.gestionventas.model.Producto;
import com.uniquindio.crisdav.gestionventas.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la entidad Producto. Permite realizar operaciones CRUD básicas sobre la tabla PRODUCTO.
 */
public class ProductoDAO {

    public void insertar(Producto producto) throws SQLException {
        String sql = "INSERT INTO PRODUCTO (id_producto, nombre, categoria, valor_adquisicion, valor_venta, iva, utilidad) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, producto.getIdProducto());
            ps.setString(2, producto.getNombre());
            ps.setString(3, producto.getCategoria());
            ps.setDouble(4, producto.getValorAdquisicion());
            ps.setDouble(5, producto.getValorVenta());
            ps.setDouble(6, producto.getIva());
            ps.setDouble(7, producto.getUtilidad());
            ps.executeUpdate();
        }
    }

    public void actualizar(Producto producto) throws SQLException {
        String sql = "UPDATE PRODUCTO SET nombre=?, categoria=?, valor_adquisicion=?, valor_venta=?, iva=?, utilidad=? WHERE id_producto=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, producto.getNombre());
            ps.setString(2, producto.getCategoria());
            ps.setDouble(3, producto.getValorAdquisicion());
            ps.setDouble(4, producto.getValorVenta());
            ps.setDouble(5, producto.getIva());
            ps.setDouble(6, producto.getUtilidad());
            ps.setInt(7, producto.getIdProducto());
            ps.executeUpdate();
        }
    }

    public void eliminar(int idProducto) throws SQLException {
        String sql = "DELETE FROM PRODUCTO WHERE id_producto=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            ps.executeUpdate();
        }
    }

    public Producto buscarPorId(int idProducto) throws SQLException {
        String sql = "SELECT * FROM PRODUCTO WHERE id_producto=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Producto(
                        rs.getInt("id_producto"),
                        rs.getString("nombre"),
                        rs.getString("categoria"),
                        rs.getDouble("valor_adquisicion"),
                        rs.getDouble("valor_venta"),
                        rs.getDouble("iva"),
                        rs.getDouble("utilidad")
                    );
                }
            }
        }
        return null;
    }

    public List<Producto> listarTodos() throws SQLException {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM PRODUCTO";
        try (Connection conn = DBUtil.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Producto producto = new Producto(
                    rs.getInt("id_producto"),
                    rs.getString("nombre"),
                    rs.getString("categoria"),
                    rs.getDouble("valor_adquisicion"),
                    rs.getDouble("valor_venta"),
                    rs.getDouble("iva"),
                    rs.getDouble("utilidad")
                );
                lista.add(producto);
            }
        }
        return lista;
    }
}
