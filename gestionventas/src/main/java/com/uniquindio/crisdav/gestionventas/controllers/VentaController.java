package com.uniquindio.crisdav.gestionventas.controllers;

import com.uniquindio.crisdav.gestionventas.dao.VentaDAO;
import com.uniquindio.crisdav.gestionventas.models.Cliente;
import com.uniquindio.crisdav.gestionventas.models.DetalleVenta;
import com.uniquindio.crisdav.gestionventas.models.Producto;
import com.uniquindio.crisdav.gestionventas.models.Venta;
import com.uniquindio.crisdav.gestionventas.dao.ClienteDAO;
import com.uniquindio.crisdav.gestionventas.dao.ProductoDAO;
import com.uniquindio.crisdav.gestionventas.dao.DetalleVentaDAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Controlador para la gestión de ventas en la interfaz JavaFX.
 */
public class VentaController {

    @FXML
    private ComboBox<Cliente> cbCliente;
    @FXML
    private ComboBox<String> cbTipoVenta;
    @FXML
    private DatePicker dpFecha;
    @FXML
    private ComboBox<Producto> cbProducto;
    @FXML
    private TextField txtCantidad;
    @FXML
    private TableView<DetalleVenta> tablaDetalleVenta;
    @FXML
    private TableColumn<DetalleVenta, Integer> colIdProducto;
    @FXML
    private TableColumn<DetalleVenta, String> colNombreProducto;
    @FXML
    private TableColumn<DetalleVenta, Integer> colCantidad;
    @FXML
    private TableColumn<DetalleVenta, Double> colSubtotal;
    @FXML
    private Label lblTotal;

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();
    // Eliminar campo duplicado de ventaDAO
    private final DetalleVentaDAO detalleVentaDAO = new DetalleVentaDAO();
    private final ObservableList<DetalleVenta> detalles = FXCollections.observableArrayList();

    @FXML
    private TableView<Venta> tablaVentas;
    @FXML
    private TableColumn<Venta, Integer> colIdVenta;
    @FXML
    private TableColumn<Venta, LocalDate> colFecha;
    @FXML
    private TableColumn<Venta, String> colTipoVenta;
    @FXML
    private TableColumn<Venta, Integer> colIdCliente;

    private final VentaDAO ventaDAO = new VentaDAO();
    private final ObservableList<Venta> ventas = FXCollections.observableArrayList();


    @FXML
    public void initialize() {
        // Inicializar combos
        try {
            if (cbCliente != null) cbCliente.setItems(FXCollections.observableArrayList(clienteDAO.listarTodos()));
            if (cbProducto != null) cbProducto.setItems(FXCollections.observableArrayList(productoDAO.listarTodos()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (cbTipoVenta != null) cbTipoVenta.setItems(FXCollections.observableArrayList("Contado", "Crédito"));
        if (dpFecha != null) dpFecha.setValue(LocalDate.now());

        // Configurar columnas de la tabla de detalle
        if (colIdProducto != null) colIdProducto.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        if (colNombreProducto != null) colNombreProducto.setCellValueFactory(cellData -> {
            int idProd = cellData.getValue().getIdProducto();
            Producto prod = null;
            try {
                prod = productoDAO.buscarPorId(idProd);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return new javafx.beans.property.SimpleStringProperty(prod != null ? prod.getNombre() : "");
        });
        if (colCantidad != null) colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        if (colSubtotal != null) colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        if (tablaDetalleVenta != null) tablaDetalleVenta.setItems(detalles);
        actualizarTotal();
    }


    @FXML
    private void agregarDetalle() {
        Producto producto = cbProducto.getValue();
        int cantidad;
        try {
            cantidad = Integer.parseInt(txtCantidad.getText());
        } catch (NumberFormatException e) {
            return;
        }
        if (producto != null && cantidad > 0) {
            double subtotal = cantidad * producto.getValorVenta();
            DetalleVenta detalle = new DetalleVenta(0, producto.getIdProducto(), cantidad, subtotal);
            detalles.add(detalle);
            actualizarTotal();
        }
    }

    @FXML
    private void eliminarDetalle() {
        DetalleVenta seleccionado = tablaDetalleVenta.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            detalles.remove(seleccionado);
            actualizarTotal();
        }
    }

    @FXML
    private void guardarVenta() {
        // Aquí iría la lógica para guardar la venta y los detalles en la BD
        // Puedes usar ventaDAO y detalleVentaDAO
    }

    @FXML
    private void limpiarFormulario() {
        if (cbCliente != null) cbCliente.getSelectionModel().clearSelection();
        if (cbTipoVenta != null) cbTipoVenta.getSelectionModel().clearSelection();
        if (dpFecha != null) dpFecha.setValue(LocalDate.now());
        if (cbProducto != null) cbProducto.getSelectionModel().clearSelection();
        if (txtCantidad != null) txtCantidad.clear();
        detalles.clear();
        actualizarTotal();
    }

    private void actualizarTotal() {
        double total = detalles.stream().mapToDouble(DetalleVenta::getSubtotal).sum();
        if (lblTotal != null) lblTotal.setText(String.valueOf(total));
    }

    // Métodos para agregar, editar y eliminar ventas se agregarán aquí
}
