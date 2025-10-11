package com.uniquindio.crisdav.gestionventas.controllers;

import com.uniquindio.crisdav.gestionventas.dao.CategoriaDAO;
import com.uniquindio.crisdav.gestionventas.dao.ProductoDAO;
import com.uniquindio.crisdav.gestionventas.models.entity.Categoria;
import com.uniquindio.crisdav.gestionventas.models.entity.Producto;
import com.uniquindio.crisdav.gestionventas.models.vo.ProductoConCategoriaVO;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ProductoController {
    
    private final ProductoDAO productoDAO;
    private final CategoriaDAO categoriaDAO;

    public ProductoController() {
        this.productoDAO = new ProductoDAO();
        this.categoriaDAO = new CategoriaDAO();
    }

    /**
     * Crea un nuevo producto
     */
    public Producto crearProducto(String codigo, String nombre, Integer idCategoria,
                                 BigDecimal valorAdquisicion, BigDecimal valorVenta, Integer stock) throws SQLException {
        
        // Validaciones
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El código no puede estar vacío");
        }
        
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        
        if (valorAdquisicion == null || valorAdquisicion.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El valor de adquisición debe ser mayor a cero");
        }
        
        if (valorVenta == null || valorVenta.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El valor de venta debe ser mayor a cero");
        }
        
        if (stock == null || stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }

        // Verificar que la categoría existe
        Categoria categoria = categoriaDAO.buscarPorId(idCategoria);
        if (categoria == null) {
            throw new IllegalArgumentException("Categoría no encontrada");
        }

        // Verificar que el código no exista
        if (productoDAO.buscarPorCodigo(codigo) != null) {
            throw new IllegalArgumentException("Ya existe un producto con ese código");
        }

        Producto producto = new Producto(codigo, nombre, idCategoria, valorAdquisicion, valorVenta, stock);
        return productoDAO.insertar(producto);
    }

    /**
     * Actualiza un producto existente
     */
    public boolean actualizarProducto(Producto producto) throws SQLException {
        if (producto.getIdProducto() == null) {
            throw new IllegalArgumentException("ID de producto no puede ser null");
        }

        // Verificar que existe
        Producto existente = productoDAO.buscarPorId(producto.getIdProducto());
        if (existente == null) {
            throw new IllegalArgumentException("Producto no encontrado");
        }

        return productoDAO.actualizar(producto);
    }

    /**
     * Actualiza el stock de un producto
     */
    public boolean actualizarStock(Integer idProducto, Integer nuevoStock) throws SQLException {
        if (nuevoStock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }

        return productoDAO.actualizarStock(idProducto, nuevoStock);
    }

    /**
     * Busca un producto por ID
     */
    public Producto buscarProducto(Integer id) throws SQLException {
        return productoDAO.buscarPorId(id);
    }

    /**
     * Busca un producto por código
     */
    public Producto buscarProductoPorCodigo(String codigo) throws SQLException {
        return productoDAO.buscarPorCodigo(codigo);
    }

    /**
     * Lista todos los productos
     */
    public List<Producto> listarProductos() throws SQLException {
        return productoDAO.listarTodos();
    }

    /**
     * Lista productos con información de categoría
     */
    public List<ProductoConCategoriaVO> listarProductosConCategoria() throws SQLException {
        return productoDAO.listarConCategoria();
    }

    /**
     * Lista productos por categoría
     */
    public List<Producto> listarProductosPorCategoria(Integer idCategoria) throws SQLException {
        return productoDAO.listarPorCategoria(idCategoria);
    }

    /**
     * Elimina un producto
     */
    public boolean eliminarProducto(Integer id) throws SQLException {
        return productoDAO.eliminar(id);
    }

    /**
     * Valida si hay suficiente stock
     */
    public boolean validarStock(Integer idProducto, Integer cantidadRequerida) throws SQLException {
        Producto producto = productoDAO.buscarPorId(idProducto);
        if (producto == null) {
            return false;
        }
        return producto.getStock() >= cantidadRequerida;
    }
}