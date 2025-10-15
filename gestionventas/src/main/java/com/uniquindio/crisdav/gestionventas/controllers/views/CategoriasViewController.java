package com.uniquindio.crisdav.gestionventas.controllers.views;

import com.uniquindio.crisdav.gestionventas.controllers.CategoriaController;
import com.uniquindio.crisdav.gestionventas.models.entity.Categoria;
import com.uniquindio.crisdav.gestionventas.utils.FormatoUtil;
import com.uniquindio.crisdav.gestionventas.models.dto.CategoriaFormResult;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CategoriasViewController {

    @FXML private TableView<Categoria> tablaCategorias;
    @FXML private TableColumn<Categoria, Integer> colId;
    @FXML private TableColumn<Categoria, String> colNombre;
    @FXML private TableColumn<Categoria, String> colIva;
    @FXML private TableColumn<Categoria, String> colUtilidad;
    @FXML private TableColumn<Categoria, Void> colAcciones;
    @FXML private Label lblTotalCategorias;

    private CategoriaController categoriaController;
    private ObservableList<Categoria> listaCategorias;

    @FXML
    public void initialize() {
        categoriaController = new CategoriaController();
        listaCategorias = FXCollections.observableArrayList();

        configurarTabla();
        cargarCategorias();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idCategoria"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colIva.setCellValueFactory(cellData -> 
            new SimpleStringProperty(FormatoUtil.formatearPorcentaje(cellData.getValue().getIva())));
        colUtilidad.setCellValueFactory(cellData -> 
            new SimpleStringProperty(FormatoUtil.formatearPorcentaje(cellData.getValue().getUtilidad())));

        // Columna de acciones
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnEditar = new Button("✏️ Editar");
            private final Button btnVer = new Button("👁️ Ver");
            private final Button btnEliminar = new Button("🗑️");
            private final HBox hbox = new HBox(5, btnEditar, btnVer, btnEliminar);

            {
                btnEditar.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-size: 10;");
                btnVer.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white; -fx-font-size: 10;");
                btnEliminar.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 10;");
                
                btnEditar.setOnAction(event -> {
                    Categoria categoria = getTableView().getItems().get(getIndex());
                    editarCategoria(categoria);
                });

                btnVer.setOnAction(event -> {
                    Categoria categoria = getTableView().getItems().get(getIndex());
                    verDetalleCategoria(categoria);
                });

                btnEliminar.setOnAction(event -> {
                    Categoria categoria = getTableView().getItems().get(getIndex());
                    eliminarCategoria(categoria);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });

        tablaCategorias.setItems(listaCategorias);
    }

    @FXML
    private void cargarCategorias() {
        try {
            List<Categoria> categorias = categoriaController.listarCategorias();
            listaCategorias.setAll(categorias);
            
            lblTotalCategorias.setText(String.valueOf(categorias.size()));
            
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar categorías:\n" + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void nuevaCategoria(ActionEvent event) {
        Dialog<CategoriaFormResult> dialog = crearDialogoCategoria(null);
        Optional<CategoriaFormResult> resultado = dialog.showAndWait();
        
        resultado.ifPresent(form -> {
            try {
                categoriaController.crearCategoria(
                    form.getNombre(),
                    form.getIva(),
                    form.getUtilidad()
                );
                
                mostrarAlerta("Éxito", "Categoría creada correctamente", Alert.AlertType.INFORMATION);
                cargarCategorias();
                
            } catch (SQLException e) {
                mostrarAlerta("Error", "Error al crear categoría:\n" + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                mostrarAlerta("Error de Validación", e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    private void editarCategoria(Categoria categoria) {
        Dialog<CategoriaFormResult> dialog = crearDialogoCategoria(categoria);
        Optional<CategoriaFormResult> resultado = dialog.showAndWait();
        
        resultado.ifPresent(form -> {
            try {
                categoria.setNombre(form.getNombre());
                categoria.setIva(form.getIva());
                categoria.setUtilidad(form.getUtilidad());
                
                categoriaController.actualizarCategoria(categoria);
                
                mostrarAlerta("Éxito", "Categoría actualizada correctamente", Alert.AlertType.INFORMATION);
                cargarCategorias();
                
            } catch (SQLException e) {
                mostrarAlerta("Error", "Error al actualizar categoría:\n" + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                mostrarAlerta("Error de Validación", e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    private void verDetalleCategoria(Categoria categoria) {
        Alert detalle = new Alert(Alert.AlertType.INFORMATION);
        detalle.setTitle("Detalle de Categoría");
        detalle.setHeaderText("Información Completa");
        
        VBox contenido = new VBox(10);
        contenido.setPadding(new Insets(10));
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        
        grid.add(new Label("ID:"), 0, 0);
        grid.add(new Label(categoria.getIdCategoria().toString()), 1, 0);
        
        grid.add(new Label("Nombre:"), 0, 1);
        grid.add(new Label(categoria.getNombre()), 1, 1);
        
        grid.add(new Label("IVA:"), 0, 2);
        grid.add(new Label(FormatoUtil.formatearPorcentaje(categoria.getIva())), 1, 2);
        
        grid.add(new Label("Utilidad:"), 0, 3);
        grid.add(new Label(FormatoUtil.formatearPorcentaje(categoria.getUtilidad())), 1, 3);
        
        // Calcular precio ejemplo
        BigDecimal precioBase = new BigDecimal("100000");
        BigDecimal precioConUtilidad = precioBase.multiply(categoria.getUtilidad().add(BigDecimal.ONE));
        BigDecimal ivaTotal = precioConUtilidad.multiply(categoria.getIva());
        BigDecimal precioFinal = precioConUtilidad.add(ivaTotal);
        
        VBox ejemplo = new VBox(5);
        ejemplo.setStyle("-fx-border-color: #007bff; -fx-border-width: 1; -fx-padding: 10; -fx-background-color: #e7f3ff; -fx-border-radius: 5;");
        Label lblEjemplo = new Label("📊 Ejemplo de Precio");
        lblEjemplo.setStyle("-fx-font-weight: bold;");
        ejemplo.getChildren().addAll(
            lblEjemplo,
            new Label("Costo base: " + FormatoUtil.formatearMoneda(precioBase)),
            new Label("+ Utilidad (" + FormatoUtil.formatearPorcentaje(categoria.getUtilidad()) + "): " + 
                     FormatoUtil.formatearMoneda(precioBase.multiply(categoria.getUtilidad()))),
            new Label("= Precio sin IVA: " + FormatoUtil.formatearMoneda(precioConUtilidad)),
            new Label("+ IVA (" + FormatoUtil.formatearPorcentaje(categoria.getIva()) + "): " + 
                     FormatoUtil.formatearMoneda(ivaTotal)),
            new Label("= Precio Final: " + FormatoUtil.formatearMoneda(precioFinal))
        );
        
        contenido.getChildren().addAll(grid, new Separator(), ejemplo);
        
        detalle.getDialogPane().setContent(contenido);
        detalle.getDialogPane().setMinWidth(450);
        detalle.showAndWait();
    }

    private void eliminarCategoria(Categoria categoria) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Eliminar la categoría?");
        confirmacion.setContentText(
            "Categoría: " + categoria.getNombre() + "\n\n" +
            "Esta acción no se puede deshacer.\n" +
            "No podrá eliminar si tiene productos asociados."
        );
        
        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                boolean eliminado = categoriaController.eliminarCategoria(categoria.getIdCategoria());
                
                if (eliminado) {
                    mostrarAlerta("Éxito", "Categoría eliminada correctamente", Alert.AlertType.INFORMATION);
                    cargarCategorias();
                } else {
                    mostrarAlerta("Error", "No se pudo eliminar la categoría", Alert.AlertType.ERROR);
                }
                
            } catch (SQLException e) {
                mostrarAlerta("Error", 
                    "Error al eliminar categoría:\n" + e.getMessage() + 
                    "\n\nLa categoría tiene productos asociados.", 
                    Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    private Dialog<CategoriaFormResult> crearDialogoCategoria(Categoria categoriaExistente) {
        Dialog<CategoriaFormResult> dialog = new Dialog<>();
        dialog.setTitle(categoriaExistente == null ? "Nueva Categoría" : "Editar Categoría");
        dialog.setHeaderText(categoriaExistente == null ? 
            "Ingrese los datos de la nueva categoría" : 
            "Modifique los datos de la categoría");

        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField txtNombre = new TextField();
        TextField txtIva = new TextField();
        TextField txtUtilidad = new TextField();

        if (categoriaExistente != null) {
            txtNombre.setText(categoriaExistente.getNombre());
            txtIva.setText(categoriaExistente.getIva().toString());
            txtUtilidad.setText(categoriaExistente.getUtilidad().toString());
        }

        grid.add(new Label("Nombre:*"), 0, 0);
        grid.add(txtNombre, 1, 0);
        grid.add(new Label("IVA (decimal):*"), 0, 1);
        grid.add(txtIva, 1, 1);
        grid.add(new Label("Ej: 0.16 para 16%"), 1, 2);
        grid.add(new Label("Utilidad (decimal):*"), 0, 3);
        grid.add(txtUtilidad, 1, 3);
        grid.add(new Label("Ej: 0.35 para 35%"), 1, 4);
        
        Label lblInfo = new Label("* Campos obligatorios");
        lblInfo.setStyle("-fx-font-size: 10; -fx-text-fill: #666;");
        grid.add(lblInfo, 0, 5, 2, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnGuardar) {
                try {
                    String nombre = txtNombre.getText().trim();
                    BigDecimal iva = new BigDecimal(txtIva.getText().trim());
                    BigDecimal utilidad = new BigDecimal(txtUtilidad.getText().trim());

                    if (nombre.isEmpty()) {
                        mostrarAlerta("Error", "El nombre es obligatorio", Alert.AlertType.ERROR);
                        return null;
                    }

                    if (iva.compareTo(BigDecimal.ZERO) < 0 || iva.compareTo(BigDecimal.ONE) > 0) {
                        mostrarAlerta("Error", "El IVA debe estar entre 0 y 1", Alert.AlertType.ERROR);
                        return null;
                    }

                    if (utilidad.compareTo(BigDecimal.ZERO) < 0 || utilidad.compareTo(BigDecimal.ONE) > 0) {
                        mostrarAlerta("Error", "La utilidad debe estar entre 0 y 1", Alert.AlertType.ERROR);
                        return null;
                    }

                    return new CategoriaFormResult(nombre, iva, utilidad);
                } catch (NumberFormatException e) {
                    mostrarAlerta("Error", "IVA y Utilidad deben ser valores numéricos válidos", Alert.AlertType.ERROR);
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

}
