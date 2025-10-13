package com.uniquindio.crisdav.gestionventas.controllers.views;

import com.uniquindio.crisdav.gestionventas.controllers.CategoriaController;
import com.uniquindio.crisdav.gestionventas.controllers.ProductoController;
import com.uniquindio.crisdav.gestionventas.models.entity.Categoria;
import com.uniquindio.crisdav.gestionventas.models.vo.ProductoConCategoriaVO;
import com.uniquindio.crisdav.gestionventas.utils.FormatoUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProductosViewController {

    @FXML private TextField txtBuscar;
    @FXML private TableView<ProductoConCategoriaVO> tablaProductos;
    @FXML private TableColumn<ProductoConCategoriaVO, Integer> colId;
    @FXML private TableColumn<ProductoConCategoriaVO, String> colCodigo;
    @FXML private TableColumn<ProductoConCategoriaVO, String> colNombre;
    @FXML private TableColumn<ProductoConCategoriaVO, String> colCategoria;
    @FXML private TableColumn<ProductoConCategoriaVO, String> colValorAdq;
    @FXML private TableColumn<ProductoConCategoriaVO, String> colValorVenta;
    @FXML private TableColumn<ProductoConCategoriaVO, Integer> colStock;
    @FXML private TableColumn<ProductoConCategoriaVO, String> colIva;
    @FXML private TableColumn<ProductoConCategoriaVO, Void> colAcciones;
    @FXML private Label lblTotalProductos;
    @FXML private Label lblStockBajo;

    private ProductoController productoController;
    private CategoriaController categoriaController;
    private ObservableList<ProductoConCategoriaVO> listaProductos;
    private ObservableList<ProductoConCategoriaVO> listaProductosFiltrada;

    @FXML
    public void initialize() {
        productoController = new ProductoController();
        categoriaController = new CategoriaController();
        listaProductos = FXCollections.observableArrayList();
        listaProductosFiltrada = FXCollections.observableArrayList();

        configurarTabla();
        cargarProductos();
        configurarBusqueda();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("nombreCategoria"));
        
        colValorAdq.setCellValueFactory(cellData -> 
            new SimpleStringProperty(FormatoUtil.formatearMoneda(cellData.getValue().getValorAdquisicion())));
        
        colValorVenta.setCellValueFactory(cellData -> 
            new SimpleStringProperty(FormatoUtil.formatearMoneda(cellData.getValue().getValorVenta())));
        
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        
        colIva.setCellValueFactory(cellData -> 
            new SimpleStringProperty(FormatoUtil.formatearPorcentaje(cellData.getValue().getIva())));

        // Configurar columna de acciones con botones
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnEditar = new Button("✏️ Editar");
            private final Button btnEliminar = new Button("🗑️");
            private final HBox hbox = new HBox(5, btnEditar, btnEliminar);

            {
                btnEditar.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-size: 11;");
                btnEliminar.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 11;");
                
                btnEditar.setOnAction(event -> {
                    ProductoConCategoriaVO producto = getTableView().getItems().get(getIndex());
                    editarProducto(producto);
                });

                btnEliminar.setOnAction(event -> {
                    ProductoConCategoriaVO producto = getTableView().getItems().get(getIndex());
                    eliminarProducto(producto);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });

        tablaProductos.setItems(listaProductosFiltrada);
    }

    private void configurarBusqueda() {
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrarProductos(newValue);
        });
    }

    private void filtrarProductos(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            listaProductosFiltrada.setAll(listaProductos);
        } else {
            String criterioLower = criterio.toLowerCase();
            List<ProductoConCategoriaVO> filtrados = listaProductos.stream()
                .filter(p -> 
                    p.getCodigo().toLowerCase().contains(criterioLower) ||
                    p.getNombre().toLowerCase().contains(criterioLower) ||
                    p.getNombreCategoria().toLowerCase().contains(criterioLower)
                )
                .collect(Collectors.toList());
            listaProductosFiltrada.setAll(filtrados);
        }
    }

    @FXML
    private void cargarProductos() {
        try {
            List<ProductoConCategoriaVO> productos = productoController.listarProductosConCategoria();
            listaProductos.setAll(productos);
            listaProductosFiltrada.setAll(productos);
            
            actualizarEstadisticas();
            
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar productos:\n" + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void actualizarEstadisticas() {
        lblTotalProductos.setText(String.valueOf(listaProductos.size()));
        
        long stockBajo = listaProductos.stream()
            .filter(p -> p.getStock() < 10)
            .count();
        lblStockBajo.setText(String.valueOf(stockBajo));
    }

    @FXML
    private void buscar(ActionEvent event) {
        filtrarProductos(txtBuscar.getText());
    }

    @FXML
    private void nuevoProducto(ActionEvent event) {
        Dialog<ProductoFormResult> dialog = crearDialogoProducto(null);
        Optional<ProductoFormResult> resultado = dialog.showAndWait();
        
        resultado.ifPresent(form -> {
            try {
                productoController.crearProducto(
                    form.codigo,
                    form.nombre,
                    form.idCategoria,
                    form.valorAdquisicion,
                    form.valorVenta,
                    form.stock
                );
                
                mostrarAlerta("Éxito", "Producto creado correctamente", Alert.AlertType.INFORMATION);
                cargarProductos();
                
            } catch (SQLException e) {
                mostrarAlerta("Error", "Error al crear producto:\n" + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                mostrarAlerta("Error de Validación", e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    private void editarProducto(ProductoConCategoriaVO productoVO) {
        Dialog<ProductoFormResult> dialog = crearDialogoProducto(productoVO);
        Optional<ProductoFormResult> resultado = dialog.showAndWait();
        
        resultado.ifPresent(form -> {
            try {
                com.uniquindio.crisdav.gestionventas.models.entity.Producto producto = 
                    productoController.buscarProducto(productoVO.getIdProducto());
                
                if (producto != null) {
                    producto.setCodigo(form.codigo);
                    producto.setNombre(form.nombre);
                    producto.setIdCategoria(form.idCategoria);
                    producto.setValorAdquisicion(form.valorAdquisicion);
                    producto.setValorVenta(form.valorVenta);
                    producto.setStock(form.stock);
                    
                    productoController.actualizarProducto(producto);
                    
                    mostrarAlerta("Éxito", "Producto actualizado correctamente", Alert.AlertType.INFORMATION);
                    cargarProductos();
                }
                
            } catch (SQLException e) {
                mostrarAlerta("Error", "Error al actualizar producto:\n" + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                mostrarAlerta("Error de Validación", e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    private void eliminarProducto(ProductoConCategoriaVO producto) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Eliminar el producto?");
        confirmacion.setContentText(
            "Producto: " + producto.getNombre() + "\n" +
            "Código: " + producto.getCodigo() + "\n\n" +
            "Esta acción no se puede deshacer."
        );
        
        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                boolean eliminado = productoController.eliminarProducto(producto.getIdProducto());
                
                if (eliminado) {
                    mostrarAlerta("Éxito", "Producto eliminado correctamente", Alert.AlertType.INFORMATION);
                    cargarProductos();
                } else {
                    mostrarAlerta("Error", "No se pudo eliminar el producto", Alert.AlertType.ERROR);
                }
                
            } catch (SQLException e) {
                mostrarAlerta("Error", 
                    "Error al eliminar producto:\n" + e.getMessage() + 
                    "\n\nEs posible que el producto tenga ventas asociadas.", 
                    Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    private Dialog<ProductoFormResult> crearDialogoProducto(ProductoConCategoriaVO productoExistente) {
        Dialog<ProductoFormResult> dialog = new Dialog<>();
        dialog.setTitle(productoExistente == null ? "Nuevo Producto" : "Editar Producto");
        dialog.setHeaderText(productoExistente == null ? 
            "Ingrese los datos del nuevo producto" : 
            "Modifique los datos del producto");

        // Botones
        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);

        // Crear formulario
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField txtCodigo = new TextField();
        TextField txtNombre = new TextField();
        ComboBox<CategoriaComboItem> cmbCategoria = new ComboBox<>();
        TextField txtValorAdq = new TextField();
        TextField txtValorVenta = new TextField();
        Spinner<Integer> spinStock = new Spinner<>(0, 10000, 0);
        spinStock.setEditable(true);

        // Cargar categorías
        try {
            List<Categoria> categorias = categoriaController.listarCategorias();
            for (Categoria cat : categorias) {
                cmbCategoria.getItems().add(new CategoriaComboItem(cat.getIdCategoria(), cat.getNombre()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Si es edición, llenar datos
        if (productoExistente != null) {
            txtCodigo.setText(productoExistente.getCodigo());
            txtNombre.setText(productoExistente.getNombre());
            txtValorAdq.setText(productoExistente.getValorAdquisicion().toString());
            txtValorVenta.setText(productoExistente.getValorVenta().toString());
            spinStock.getValueFactory().setValue(productoExistente.getStock());
            
            // Seleccionar categoría
            for (CategoriaComboItem item : cmbCategoria.getItems()) {
                if (item.id.equals(productoExistente.getIdProducto())) {
                    cmbCategoria.setValue(item);
                    break;
                }
            }
        }

        // Agregar campos al grid
        grid.add(new Label("Código:*"), 0, 0);
        grid.add(txtCodigo, 1, 0);
        grid.add(new Label("Nombre:*"), 0, 1);
        grid.add(txtNombre, 1, 1);
        grid.add(new Label("Categoría:*"), 0, 2);
        grid.add(cmbCategoria, 1, 2);
        grid.add(new Label("Valor Adquisición:*"), 0, 3);
        grid.add(txtValorAdq, 1, 3);
        grid.add(new Label("Valor Venta:*"), 0, 4);
        grid.add(txtValorVenta, 1, 4);
        grid.add(new Label("Stock Inicial:*"), 0, 5);
        grid.add(spinStock, 1, 5);

        dialog.getDialogPane().setContent(grid);

        // Convertir resultado
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnGuardar) {
                try {
                    String codigo = txtCodigo.getText().trim();
                    String nombre = txtNombre.getText().trim();
                    CategoriaComboItem catSeleccionada = cmbCategoria.getValue();
                    BigDecimal valorAdq = new BigDecimal(txtValorAdq.getText().trim());
                    BigDecimal valorVenta = new BigDecimal(txtValorVenta.getText().trim());
                    Integer stock = spinStock.getValue();

                    if (codigo.isEmpty() || nombre.isEmpty() || catSeleccionada == null) {
                        mostrarAlerta("Error", "Todos los campos son obligatorios", Alert.AlertType.ERROR);
                        return null;
                    }

                    return new ProductoFormResult(
                        codigo, nombre, catSeleccionada.id, 
                        valorAdq, valorVenta, stock
                    );
                } catch (NumberFormatException e) {
                    mostrarAlerta("Error", "Valores numéricos inválidos", Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });

        return dialog;
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Clase auxiliar para resultado del formulario
    private static class ProductoFormResult {
        String codigo;
        String nombre;
        Integer idCategoria;
        BigDecimal valorAdquisicion;
        BigDecimal valorVenta;
        Integer stock;

        ProductoFormResult(String codigo, String nombre, Integer idCategoria, 
                          BigDecimal valorAdquisicion, BigDecimal valorVenta, Integer stock) {
            this.codigo = codigo;
            this.nombre = nombre;
            this.idCategoria = idCategoria;
            this.valorAdquisicion = valorAdquisicion;
            this.valorVenta = valorVenta;
            this.stock = stock;
        }
    }

    // Clase auxiliar para ComboBox de categorías
    private static class CategoriaComboItem {
        Integer id;
        String nombre;

        CategoriaComboItem(Integer id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }

        @Override
        public String toString() {
            return nombre;
        }
    }
}