package com.uniquindio.crisdav.gestionventas.dao;

import com.uniquindio.crisdav.gestionventas.models.entity.Producto;
import com.uniquindio.crisdav.gestionventas.models.vo.ProductoConCategoriaVO;
import com.uniquindio.crisdav.gestionventas.utils.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    public Producto insertar(Producto producto) throws SQLException {
        String sql = "INSERT INTO PRODUCTO (codigo, nombre, id_categoria, valor_adquisicion, valor_venta, stock) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, new String[]{"id_producto"})) {
            
            pstmt.setString(1, producto.getCodigo());
            pstmt.setString(2, producto.getNombre());
            pstmt.setInt(3, producto.getIdCategoria());
            pstmt.setBigDecimal(4, producto.getValorAdquisicion());
            pstmt.setBigDecimal(5, producto.getValorVenta());
            pstmt.setInt(6, producto.getStock());
            
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        producto.setIdProducto(rs.getInt(1));
                    }
                }
            }
            
            return producto;
        }
    }

    public Producto buscarPorId(Integer id) throws SQLException {
        String sql = "SELECT * FROM PRODUCTO WHERE id_producto = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearProducto(rs);
                }
            }
        }
        
        return null;
    }

    public Producto buscarPorCodigo(String codigo) throws SQLException {
        String sql = "SELECT * FROM PRODUCTO WHERE codigo = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, codigo);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearProducto(rs);
                }
            }
        }
        
        return null;
    }

    public List<Producto> listarTodos() throws SQLException {
        String sql = "SELECT * FROM PRODUCTO ORDER BY nombre";
        List<Producto> productos = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }
        }
        
        return productos;
    }

    public List<ProductoConCategoriaVO> listarConCategoria() throws SQLException {
        String sql = "SELECT p.*, c.nombre as nombre_categoria, c.iva, c.utilidad " +
                    "FROM PRODUCTO p " +
                    "INNER JOIN CATEGORIA c ON p.id_categoria = c.id_categoria " +
                    "ORDER BY p.nombre";
        List<ProductoConCategoriaVO> productos = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                ProductoConCategoriaVO vo = new ProductoConCategoriaVO();
                vo.setIdProducto(rs.getInt("id_producto"));
                vo.setCodigo(rs.getString("codigo"));
                vo.setNombre(rs.getString("nombre"));
                vo.setNombreCategoria(rs.getString("nombre_categoria"));
                vo.setIva(rs.getBigDecimal("iva"));
                vo.setUtilidad(rs.getBigDecimal("utilidad"));
                vo.setValorAdquisicion(rs.getBigDecimal("valor_adquisicion"));
                vo.setValorVenta(rs.getBigDecimal("valor_venta"));
                vo.setStock(rs.getInt("stock"));
                productos.add(vo);
            }
        }
        
        return productos;
    }

    public List<Producto> listarPorCategoria(Integer idCategoria) throws SQLException {
        String sql = "SELECT * FROM PRODUCTO WHERE id_categoria = ? ORDER BY nombre";
        List<Producto> productos = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idCategoria);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapearProducto(rs));
                }
            }
        }
        
        return productos;
    }

    public boolean actualizar(Producto producto) throws SQLException {
        String sql = "UPDATE PRODUCTO SET codigo = ?, nombre = ?, id_categoria = ?, valor_adquisicion = ?, valor_venta = ?, stock = ? WHERE id_producto = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, producto.getCodigo());
            pstmt.setString(2, producto.getNombre());
            pstmt.setInt(3, producto.getIdCategoria());
            pstmt.setBigDecimal(4, producto.getValorAdquisicion());
            pstmt.setBigDecimal(5, producto.getValorVenta());
            pstmt.setInt(6, producto.getStock());
            pstmt.setInt(7, producto.getIdProducto());
            
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean actualizarStock(Integer idProducto, Integer nuevoStock) throws SQLException {
        String sql = "UPDATE PRODUCTO SET stock = ? WHERE id_producto = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, nuevoStock);
            pstmt.setInt(2, idProducto);
            
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean disminuirStock(Integer idProducto, Integer cantidad) throws SQLException {
        String sql = "UPDATE PRODUCTO SET stock = stock - ? WHERE id_producto = ? AND stock >= ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, cantidad);
            pstmt.setInt(2, idProducto);
            pstmt.setInt(3, cantidad);
            
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean eliminar(Integer id) throws SQLException {
        String sql = "DELETE FROM PRODUCTO WHERE id_producto = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    private Producto mapearProducto(ResultSet rs) throws SQLException {
        Producto producto = new Producto();
        producto.setIdProducto(rs.getInt("id_producto"));
        producto.setCodigo(rs.getString("codigo"));
        producto.setNombre(rs.getString("nombre"));
        producto.setIdCategoria(rs.getInt("id_categoria"));
        producto.setValorAdquisicion(rs.getBigDecimal("valor_adquisicion"));
        producto.setValorVenta(rs.getBigDecimal("valor_venta"));
        producto.setStock(rs.getInt("stock"));
        return producto;
    }
}