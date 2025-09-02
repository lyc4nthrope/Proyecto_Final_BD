package com.uniquindio.crisdav.gestionventas.dao;

import com.uniquindio.crisdav.gestionventas.models.DetalleVenta;
import com.uniquindio.crisdav.gestionventas.utils.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la entidad DetalleVenta. Permite realizar operaciones CRUD básicas sobre la tabla DETALLEVENTA.
 */
public class DetalleVentaDAO {

    public void insertar(DetalleVenta detalle) throws SQLException {
        String sql = "INSERT INTO DETALLEVENTA (id_venta, id_producto, cantidad, subtotal) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, detalle.getIdVenta());
            ps.setInt(2, detalle.getIdProducto());
            ps.setInt(3, detalle.getCantidad());
            ps.setDouble(4, detalle.getSubtotal());
            ps.executeUpdate();
        }
    }

    public void actualizar(DetalleVenta detalle) throws SQLException {
        String sql = "UPDATE DETALLEVENTA SET cantidad=?, subtotal=? WHERE id_venta=? AND id_producto=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, detalle.getCantidad());
            ps.setDouble(2, detalle.getSubtotal());
            ps.setInt(3, detalle.getIdVenta());
            ps.setInt(4, detalle.getIdProducto());
            ps.executeUpdate();
        }
    }

    public void eliminar(int idVenta, int idProducto) throws SQLException {
        String sql = "DELETE FROM DETALLEVENTA WHERE id_venta=? AND id_producto=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idVenta);
            ps.setInt(2, idProducto);
            ps.executeUpdate();
        }
    }

    public DetalleVenta buscarPorId(int idVenta, int idProducto) throws SQLException {
        String sql = "SELECT * FROM DETALLEVENTA WHERE id_venta=? AND id_producto=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idVenta);
            ps.setInt(2, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new DetalleVenta(
                        rs.getInt("id_venta"),
                        rs.getInt("id_producto"),
                        rs.getInt("cantidad"),
                        rs.getDouble("subtotal")
                    );
                }
            }
        }
        return null;
    }

    public List<DetalleVenta> listarPorVenta(int idVenta) throws SQLException {
        List<DetalleVenta> lista = new ArrayList<>();
        String sql = "SELECT * FROM DETALLEVENTA WHERE id_venta=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idVenta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DetalleVenta detalle = new DetalleVenta(
                        rs.getInt("id_venta"),
                        rs.getInt("id_producto"),
                        rs.getInt("cantidad"),
                        rs.getDouble("subtotal")
                    );
                    lista.add(detalle);
                }
            }
        }
        return lista;
    }
}
