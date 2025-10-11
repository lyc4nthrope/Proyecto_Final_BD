package com.uniquindio.crisdav.gestionventas.controllers;

import com.uniquindio.crisdav.gestionventas.dao.ClienteDAO;
import com.uniquindio.crisdav.gestionventas.models.entity.Cliente;
import java.sql.SQLException;
import java.util.List;

public class ClienteController {
    
    private final ClienteDAO clienteDAO;

    public ClienteController() {
        this.clienteDAO = new ClienteDAO();
    }

    /**
     * Crea un nuevo cliente
     */
    public Cliente crearCliente(String cedula, String nombre, String direccion, 
                               String telefono, String correo) throws SQLException {
        
        // Validaciones
        if (cedula == null || cedula.trim().isEmpty()) {
            throw new IllegalArgumentException("La cédula no puede estar vacía");
        }
        
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }

        // Verificar que la cédula no exista
        if (clienteDAO.buscarPorCedula(cedula) != null) {
            throw new IllegalArgumentException("Ya existe un cliente con esa cédula");
        }

        Cliente cliente = new Cliente(cedula, nombre, direccion, telefono, correo);
        return clienteDAO.insertar(cliente);
    }

    /**
     * Actualiza un cliente existente
     */
    public boolean actualizarCliente(Cliente cliente) throws SQLException {
        if (cliente.getIdCliente() == null) {
            throw new IllegalArgumentException("ID de cliente no puede ser null");
        }

        // Verificar que existe
        Cliente existente = clienteDAO.buscarPorId(cliente.getIdCliente());
        if (existente == null) {
            throw new IllegalArgumentException("Cliente no encontrado");
        }

        return clienteDAO.actualizar(cliente);
    }

    /**
     * Busca un cliente por ID
     */
    public Cliente buscarCliente(Integer id) throws SQLException {
        return clienteDAO.buscarPorId(id);
    }

    /**
     * Busca un cliente por cédula
     */
    public Cliente buscarClientePorCedula(String cedula) throws SQLException {
        return clienteDAO.buscarPorCedula(cedula);
    }

    /**
     * Lista todos los clientes
     */
    public List<Cliente> listarClientes() throws SQLException {
        return clienteDAO.listarTodos();
    }

    /**
     * Lista clientes con crédito activo
     */
    public List<Cliente> listarClientesConCredito() throws SQLException {
        return clienteDAO.listarConCreditoActivo();
    }

    /**
     * Verifica si un cliente puede solicitar crédito
     */
    public boolean puedeObtenerCredito(Integer idCliente) throws SQLException {
        Cliente cliente = clienteDAO.buscarPorId(idCliente);
        if (cliente == null) {
            return false;
        }
        return !cliente.getTieneCreditoActivo();
    }

    /**
     * Elimina un cliente
     */
    public boolean eliminarCliente(Integer id) throws SQLException {
        return clienteDAO.eliminar(id);
    }
}