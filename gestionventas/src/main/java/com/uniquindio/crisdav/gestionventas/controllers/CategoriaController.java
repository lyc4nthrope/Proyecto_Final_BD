package com.uniquindio.crisdav.gestionventas.controllers;

import com.uniquindio.crisdav.gestionventas.dao.CategoriaDAO;
import com.uniquindio.crisdav.gestionventas.models.entity.Categoria;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class CategoriaController {
    
    private final CategoriaDAO categoriaDAO;

    public CategoriaController() {
        this.categoriaDAO = new CategoriaDAO();
    }

    /**
     * Crea una nueva categoría
     */
    public Categoria crearCategoria(String nombre, BigDecimal iva, BigDecimal utilidad) throws SQLException {
        // Validaciones
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        
        if (iva == null || iva.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El IVA debe ser mayor o igual a cero");
        }
        
        if (utilidad == null || utilidad.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("La utilidad debe ser mayor o igual a cero");
        }

        // Verificar que el nombre no exista
        if (categoriaDAO.buscarPorNombre(nombre) != null) {
            throw new IllegalArgumentException("Ya existe una categoría con ese nombre");
        }

        Categoria categoria = new Categoria(nombre, iva, utilidad);
        return categoriaDAO.insertar(categoria);
    }

    /**
     * Actualiza una categoría existente
     */
    public boolean actualizarCategoria(Categoria categoria) throws SQLException {
        if (categoria.getIdCategoria() == null) {
            throw new IllegalArgumentException("ID de categoría no puede ser null");
        }

        return categoriaDAO.actualizar(categoria);
    }

    /**
     * Busca una categoría por ID
     */
    public Categoria buscarCategoria(Integer id) throws SQLException {
        return categoriaDAO.buscarPorId(id);
    }

    /**
     * Busca una categoría por nombre
     */
    public Categoria buscarCategoriaPorNombre(String nombre) throws SQLException {
        return categoriaDAO.buscarPorNombre(nombre);
    }

    /**
     * Lista todas las categorías
     */
    public List<Categoria> listarCategorias() throws SQLException {
        return categoriaDAO.listarTodas();
    }

    /**
     * Elimina una categoría
     */
    public boolean eliminarCategoria(Integer id) throws SQLException {
        return categoriaDAO.eliminar(id);
    }
}