package com.uniquindio.crisdav.gestionventas.dao;

import com.uniquindio.crisdav.gestionventas.model.Cuota;
import com.uniquindio.crisdav.gestionventas.util.DBUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la entidad Cuota. Permite realizar operaciones CRUD básicas sobre la tabla CUOTA.
 */
public class CuotaDAO {

    public void insertar(Cuota cuota) throws SQLException {
        String sql = "INSERT INTO CUOTA (id_cuota, id_venta, numero_cuota, valor_cuota, fecha_pago, estado) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cuota.getIdCuota());
            ps.setInt(2, cuota.getIdVenta());
            ps.setInt(3, cuota.getNumeroCuota());
            ps.setDouble(4, cuota.getValorCuota());
            if (cuota.getFechaPago() != null) {
                ps.setDate(5, Date.valueOf(cuota.getFechaPago()));
            } else {
                ps.setNull(5, Types.DATE);
            }
            ps.setString(6, cuota.getEstado());
            ps.executeUpdate();
        }
    }

    public void actualizar(Cuota cuota) throws SQLException {
        String sql = "UPDATE CUOTA SET id_venta=?, numero_cuota=?, valor_cuota=?, fecha_pago=?, estado=? WHERE id_cuota=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cuota.getIdVenta());
            ps.setInt(2, cuota.getNumeroCuota());
            ps.setDouble(3, cuota.getValorCuota());
            if (cuota.getFechaPago() != null) {
                ps.setDate(4, Date.valueOf(cuota.getFechaPago()));
            } else {
                ps.setNull(4, Types.DATE);
            }
            ps.setString(5, cuota.getEstado());
            ps.setInt(6, cuota.getIdCuota());
            ps.executeUpdate();
        }
    }

    public void eliminar(int idCuota) throws SQLException {
        String sql = "DELETE FROM CUOTA WHERE id_cuota=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCuota);
            ps.executeUpdate();
        }
    }

    public Cuota buscarPorId(int idCuota) throws SQLException {
        String sql = "SELECT * FROM CUOTA WHERE id_cuota=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCuota);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LocalDate fechaPago = null;
                    Date fecha = rs.getDate("fecha_pago");
                    if (fecha != null) {
                        fechaPago = fecha.toLocalDate();
                    }
                    return new Cuota(
                        rs.getInt("id_cuota"),
                        rs.getInt("id_venta"),
                        rs.getInt("numero_cuota"),
                        rs.getDouble("valor_cuota"),
                        fechaPago,
                        rs.getString("estado")
                    );
                }
            }
        }
        return null;
    }

    public List<Cuota> listarPorVenta(int idVenta) throws SQLException {
        List<Cuota> lista = new ArrayList<>();
        String sql = "SELECT * FROM CUOTA WHERE id_venta=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idVenta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LocalDate fechaPago = null;
                    Date fecha = rs.getDate("fecha_pago");
                    if (fecha != null) {
                        fechaPago = fecha.toLocalDate();
                    }
                    Cuota cuota = new Cuota(
                        rs.getInt("id_cuota"),
                        rs.getInt("id_venta"),
                        rs.getInt("numero_cuota"),
                        rs.getDouble("valor_cuota"),
                        fechaPago,
                        rs.getString("estado")
                    );
                    lista.add(cuota);
                }
            }
        }
        return lista;
    }
}
