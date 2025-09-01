package com.uniquindio.crisdav.gestionventas.controller;

import com.uniquindio.crisdav.gestionventas.dao.DetalleVentaDAO;
import com.uniquindio.crisdav.gestionventas.model.DetalleVenta;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.List;

/**
 * Controlador para la gestión de detalles de venta en la interfaz JavaFX.
 */
public class DetalleVentaController {

    @FXML
    private TableView<DetalleVenta> tablaDetalleVenta;
    @FXML
    private TableColumn<DetalleVenta, Integer> colIdVenta;
    @FXML
    private TableColumn<DetalleVenta, Integer> colIdProducto;
    @FXML
    private TableColumn<DetalleVenta, Integer> colCantidad;
    @FXML
    private TableColumn<DetalleVenta, Double> colSubtotal;

    private final DetalleVentaDAO detalleVentaDAO = new DetalleVentaDAO();
    private final ObservableList<DetalleVenta> detalles = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configurar columnas de la tabla
        colIdVenta.setCellValueFactory(new PropertyValueFactory<>("idVenta"));
        colIdProducto.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        // Cargar datos (por defecto, lista vacía)
        tablaDetalleVenta.setItems(detalles);
    }

    public void cargarDetallesPorVenta(int idVenta) {
        try {
            List<DetalleVenta> lista = detalleVentaDAO.listarPorVenta(idVenta);
            detalles.setAll(lista);
        } catch (SQLException e) {
            e.printStackTrace();
            // Aquí puedes mostrar un mensaje de error en la UI
        }
    }

    // Métodos para agregar, editar y eliminar detalles de venta se agregarán aquí
}
