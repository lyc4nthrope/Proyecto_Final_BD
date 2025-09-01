package com.uniquindio.crisdav.gestionventas.controller;

import com.uniquindio.crisdav.gestionventas.dao.ClienteDAO;
import com.uniquindio.crisdav.gestionventas.model.Cliente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.List;

/**
 * Controlador para la gestión de clientes en la interfaz JavaFX.
 */
public class ClienteController {

    @FXML
    private TextField txtNombre, txtCedula, txtDireccion, txtTelefono, txtCorreo;
    @FXML
    private TableView<Cliente> tablaClientes;
    @FXML
    private TableColumn<Cliente, Integer> colId;
    @FXML
    private TableColumn<Cliente, String> colNombre;
    @FXML
    private TableColumn<Cliente, String> colCedula;
    @FXML
    private TableColumn<Cliente, String> colDireccion;
    @FXML
    private TableColumn<Cliente, String> colTelefono;
    @FXML
    private TableColumn<Cliente, String> colCorreo;

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ObservableList<Cliente> clientes = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configurar columnas de la tabla
        colId.setCellValueFactory(new PropertyValueFactory<>("idCliente"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCedula.setCellValueFactory(new PropertyValueFactory<>("cedula"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        // Cargar datos
        cargarClientes();
    }

    private void cargarClientes() {
        try {
            List<Cliente> lista = clienteDAO.listarTodos();
            clientes.setAll(lista);
            tablaClientes.setItems(clientes);
        } catch (SQLException e) {
            e.printStackTrace();
            // Aquí puedes mostrar un mensaje de error en la UI
        }
    }

    /**
     * Método para agregar un cliente desde la interfaz.
     */
    @FXML
    private void agregarCliente() {
        try {
            Cliente cliente = new Cliente(
                0, // El ID puede ser autogenerado por la BD si corresponde
                txtNombre.getText(),
                txtCedula.getText(),
                txtDireccion.getText(),
                txtTelefono.getText(),
                txtCorreo.getText()
            );
            clienteDAO.insertar(cliente);
            cargarClientes(); // Refresca la tabla
            limpiarCampos();
        } catch (SQLException e) {
            e.printStackTrace();
            // Aquí puedes mostrar un mensaje de error en la UI
        }
    }

    /**
     * Método para editar un cliente seleccionado en la tabla.
     */
    @FXML
    private void editarCliente() {
        Cliente seleccionado = tablaClientes.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            try {
                Cliente clienteEditado = new Cliente(
                    seleccionado.getIdCliente(),
                    txtNombre.getText(),
                    txtCedula.getText(),
                    txtDireccion.getText(),
                    txtTelefono.getText(),
                    txtCorreo.getText()
                );
                clienteDAO.actualizar(clienteEditado);
                cargarClientes();
                limpiarCampos();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Método para eliminar un cliente seleccionado en la tabla.
     */
    @FXML
    private void eliminarCliente() {
        Cliente seleccionado = tablaClientes.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            try {
                clienteDAO.eliminar(seleccionado.getIdCliente());
                cargarClientes();
                limpiarCampos();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Método para limpiar los campos de entrada.
     */
    private void limpiarCampos() {
        txtNombre.clear();
        txtCedula.clear();
        txtDireccion.clear();
        txtTelefono.clear();
        txtCorreo.clear();
    }

    /**
     * Método para cargar los datos del cliente seleccionado en los campos de entrada.
     */
    @FXML
    private void seleccionarCliente() {
        Cliente seleccionado = tablaClientes.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            txtNombre.setText(seleccionado.getNombre());
            txtCedula.setText(seleccionado.getCedula());
            txtDireccion.setText(seleccionado.getDireccion());
            txtTelefono.setText(seleccionado.getTelefono());
            txtCorreo.setText(seleccionado.getCorreo());
        }
    }
}
