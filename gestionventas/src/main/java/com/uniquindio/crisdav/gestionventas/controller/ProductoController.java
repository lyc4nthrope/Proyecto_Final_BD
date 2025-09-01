package com.uniquindio.crisdav.gestionventas.controller;

import com.uniquindio.crisdav.gestionventas.dao.ProductoDAO;
import com.uniquindio.crisdav.gestionventas.model.Producto;
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
 * Controlador para la gestión de productos en la interfaz JavaFX.
 */
public class ProductoController {

    @FXML
    private TextField txtNombre, txtCategoria, txtValorAdquisicion, txtValorVenta, txtIva, txtUtilidad;
    @FXML
    private TableView<Producto> tablaProductos;
    @FXML
    private TableColumn<Producto, Integer> colId;
    @FXML
    private TableColumn<Producto, String> colNombre;
    @FXML
    private TableColumn<Producto, String> colCategoria;
    @FXML
    private TableColumn<Producto, Double> colValorAdquisicion;
    @FXML
    private TableColumn<Producto, Double> colValorVenta;
    @FXML
    private TableColumn<Producto, Double> colIva;
    @FXML
    private TableColumn<Producto, Double> colUtilidad;

    private final ProductoDAO productoDAO = new ProductoDAO();
    private final ObservableList<Producto> productos = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configurar columnas de la tabla
        colId.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colValorAdquisicion.setCellValueFactory(new PropertyValueFactory<>("valorAdquisicion"));
        colValorVenta.setCellValueFactory(new PropertyValueFactory<>("valorVenta"));
        colIva.setCellValueFactory(new PropertyValueFactory<>("iva"));
        colUtilidad.setCellValueFactory(new PropertyValueFactory<>("utilidad"));
        // Cargar datos
        cargarProductos();
    }

    private void cargarProductos() {
        try {
            List<Producto> lista = productoDAO.listarTodos();
            productos.setAll(lista);
            tablaProductos.setItems(productos);
        } catch (SQLException e) {
            e.printStackTrace();
            // Aquí puedes mostrar un mensaje de error en la UI
        }
    }

    /**
     * Método para agregar un producto desde la interfaz.
     */
    @FXML
    private void agregarProducto() {
        try {
            Producto producto = new Producto(
                0, // El ID puede ser autogenerado por la BD si corresponde
                txtNombre.getText(),
                txtCategoria.getText(),
                Double.parseDouble(txtValorAdquisicion.getText()),
                Double.parseDouble(txtValorVenta.getText()),
                Double.parseDouble(txtIva.getText()),
                Double.parseDouble(txtUtilidad.getText())
            );
            productoDAO.insertar(producto);
            cargarProductos();
            limpiarCampos();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Método para editar un producto seleccionado en la tabla.
     */
    @FXML
    private void editarProducto() {
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            try {
                Producto productoEditado = new Producto(
                    seleccionado.getIdProducto(),
                    txtNombre.getText(),
                    txtCategoria.getText(),
                    Double.parseDouble(txtValorAdquisicion.getText()),
                    Double.parseDouble(txtValorVenta.getText()),
                    Double.parseDouble(txtIva.getText()),
                    Double.parseDouble(txtUtilidad.getText())
                );
                productoDAO.actualizar(productoEditado);
                cargarProductos();
                limpiarCampos();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Método para eliminar un producto seleccionado en la tabla.
     */
    @FXML
    private void eliminarProducto() {
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            try {
                productoDAO.eliminar(seleccionado.getIdProducto());
                cargarProductos();
                limpiarCampos();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Método para limpiar los campos de entrada.
     */
    private void limpiarCampos() {
        txtNombre.clear();
        txtCategoria.clear();
        txtValorAdquisicion.clear();
        txtValorVenta.clear();
        txtIva.clear();
        txtUtilidad.clear();
    }

    /**
     * Método para cargar los datos del producto seleccionado en los campos de entrada.
     */
    @FXML
    private void seleccionarProducto() {
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            txtNombre.setText(seleccionado.getNombre());
            txtCategoria.setText(seleccionado.getCategoria());
            txtValorAdquisicion.setText(String.valueOf(seleccionado.getValorAdquisicion()));
            txtValorVenta.setText(String.valueOf(seleccionado.getValorVenta()));
            txtIva.setText(String.valueOf(seleccionado.getIva()));
            txtUtilidad.setText(String.valueOf(seleccionado.getUtilidad()));
        }
    }
}
