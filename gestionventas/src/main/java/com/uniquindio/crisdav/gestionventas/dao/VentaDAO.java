package com.uniquindio.crisdav.gestionventas.dao;

import com.uniquindio.crisdav.gestionventas.models.Venta;
import com.uniquindio.crisdav.gestionventas.utils.DBUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la entidad Venta. Permite realizar operaciones CRUD básicas sobre la tabla VENTA.
 */
public class VentaDAO {

    public void insertar(Venta venta) throws SQLException {
        String sql = "INSERT INTO VENTA (id_venta, fecha, tipo_venta, id_cliente) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, venta.getIdVenta());
            ps.setDate(2, Date.valueOf(venta.getFecha()));
            ps.setString(3, venta.getTipoVenta());
            ps.setInt(4, venta.getIdCliente());
            ps.executeUpdate();
        }
    }

    public void actualizar(Venta venta) throws SQLException {
        String sql = "UPDATE VENTA SET fecha=?, tipo_venta=?, id_cliente=? WHERE id_venta=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(venta.getFecha()));
            ps.setString(2, venta.getTipoVenta());
            ps.setInt(3, venta.getIdCliente());
            ps.setInt(4, venta.getIdVenta());
            ps.executeUpdate();
        }
    }

    public void eliminar(int idVenta) throws SQLException {
        String sql = "DELETE FROM VENTA WHERE id_venta=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idVenta);
            ps.executeUpdate();
        }
    }

    public Venta buscarPorId(int idVenta) throws SQLException {
        String sql = "SELECT * FROM VENTA WHERE id_venta=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idVenta);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Venta(
                        rs.getInt("id_venta"),
                        rs.getDate("fecha").toLocalDate(),
                        rs.getString("tipo_venta"),
                        rs.getInt("id_cliente")
                    );
                }
            }
        }
        return null;
    }

    public List<Venta> listarTodos() throws SQLException {
        List<Venta> lista = new ArrayList<>();
        String sql = "SELECT * FROM VENTA";
        try (Connection conn = DBUtil.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Venta venta = new Venta(
                    rs.getInt("id_venta"),
                    rs.getDate("fecha").toLocalDate(),
                    rs.getString("tipo_venta"),
                    rs.getInt("id_cliente")
                );
                lista.add(venta);
            }
        }
        return lista;
    }
}
