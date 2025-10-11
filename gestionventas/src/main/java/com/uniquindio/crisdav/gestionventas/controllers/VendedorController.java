package com.uniquindio.crisdav.gestionventas.controllers;

import com.uniquindio.crisdav.gestionventas.dao.VendedorDAO;
import com.uniquindio.crisdav.gestionventas.models.entity.Vendedor;

import java.sql.SQLException;
import java.util.List;

public class VendedorController {
    
    private final VendedorDAO vendedorDAO;

    public VendedorController() {
        this.vendedorDAO = new VendedorDAO();
    }

    /**
     * Crea un nuevo vendedor
     */
    public Vendedor crearVendedor(String nombre, String telefono, String correo) throws SQLException {
        // Validaciones
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }

        Vendedor vendedor = new Vendedor(nombre, telefono, correo);
        return vendedorDAO.insertar(vendedor);
    }

    /**
     * Actualiza un vendedor existente
     */
    public boolean actualizarVendedor(Vendedor vendedor) throws SQLException {
        if (vendedor.getIdVendedor() == null) {
            throw new IllegalArgumentException("ID de vendedor no puede ser null");
        }

        return vendedorDAO.actualizar(vendedor);
    }

    /**
     * Busca un vendedor por ID
     */
    public Vendedor buscarVendedor(Integer id) throws SQLException {
        return vendedorDAO.buscarPorId(id);
    }

    /**
     * Lista todos los vendedores
     */
    public List<Vendedor> listarVendedores() throws SQLException {
        return vendedorDAO.listarTodos();
    }

    /**
     * Elimina un vendedor
     */
    public boolean eliminarVendedor(Integer id) throws SQLException {
        return vendedorDAO.eliminar(id);
    }
}