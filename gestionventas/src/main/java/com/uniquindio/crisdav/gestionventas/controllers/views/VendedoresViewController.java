package com.uniquindio.crisdav.gestionventas.controllers.views;

import com.uniquindio.crisdav.gestionventas.controllers.VendedorController;
import com.uniquindio.crisdav.gestionventas.models.entity.Vendedor;
import com.uniquindio.crisdav.gestionventas.utils.FormatoUtil;
import com.uniquindio.crisdav.gestionventas.utils.ValidadorUtil;

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

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.uniquindio.crisdav.gestionventas.models.dto.VendedorFormResult;

public class VendedoresViewController {

    @FXML private TextField txtBuscar;
    @FXML private TableView<Vendedor> tablaVendedores;
    @FXML private TableColumn<Vendedor, Integer> colId;
    @FXML private TableColumn<Vendedor, String> colNombre;
    @FXML private TableColumn<Vendedor, String> colTelefono;
    @FXML private TableColumn<Vendedor, String> colCorreo;
    @FXML private TableColumn<Vendedor, Void> colAcciones;
    @FXML private Label lblTotalVendedores;

    private VendedorController vendedorController;
    private ObservableList<Vendedor> listaVendedores;
    private ObservableList<Vendedor> listaVendedoresFiltrada;

    @FXML
    public void initialize() {
        vendedorController = new VendedorController();
        listaVendedores = FXCollections.observableArrayList();
        listaVendedoresFiltrada = FXCollections.observableArrayList();

        configurarTabla();
        cargarVendedores();
        configurarBusqueda();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idVendedor"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colTelefono.setCellValueFactory(cellData -> 
            new SimpleStringProperty(FormatoUtil.formatearTelefono(cellData.getValue().getTelefono())));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));

        // Columna de acciones
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnEditar = new Button("✏️ Editar");
            private final Button btnEliminar = new Button("🗑️");
            private final HBox hbox = new HBox(5, btnEditar, btnEliminar);

            {
                btnEditar.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-size: 11;");
                btnEliminar.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 11;");
                
                btnEditar.setOnAction(event -> {
                    Vendedor vendedor = getTableView().getItems().get(getIndex());
                    editarVendedor(vendedor);
                });

                btnEliminar.setOnAction(event -> {
                    Vendedor vendedor = getTableView().getItems().get(getIndex());
                    eliminarVendedor(vendedor);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });

        tablaVendedores.setItems(listaVendedoresFiltrada);
    }

    private void configurarBusqueda() {
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrarVendedores(newValue);
        });
    }

    private void filtrarVendedores(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            listaVendedoresFiltrada.setAll(listaVendedores);
        } else {
            String criterioLower = criterio.toLowerCase();
            List<Vendedor> filtrados = listaVendedores.stream()
                .filter(v -> 
                    v.getNombre().toLowerCase().contains(criterioLower) ||
                    (v.getTelefono() != null && v.getTelefono().contains(criterio)) ||
                    (v.getCorreo() != null && v.getCorreo().toLowerCase().contains(criterioLower))
                )
                .collect(Collectors.toList());
            listaVendedoresFiltrada.setAll(filtrados);
        }
    }

    @FXML
    private void cargarVendedores() {
        try {
            List<Vendedor> vendedores = vendedorController.listarVendedores();
            listaVendedores.setAll(vendedores);
            listaVendedoresFiltrada.setAll(vendedores);
            
            lblTotalVendedores.setText(String.valueOf(vendedores.size()));
            
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar vendedores:\n" + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void nuevoVendedor(ActionEvent event) {
        Dialog<VendedorFormResult> dialog = crearDialogoVendedor(null);
        Optional<VendedorFormResult> resultado = dialog.showAndWait();
        
        resultado.ifPresent(form -> {
            try {
                vendedorController.crearVendedor(
                    form.getNombre(),
                    form.getTelefono(),
                    form.getCorreo()
                );
                
                mostrarAlerta("Éxito", "Vendedor creado correctamente", Alert.AlertType.INFORMATION);
                cargarVendedores();
                
            } catch (SQLException e) {
                mostrarAlerta("Error", "Error al crear vendedor:\n" + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                mostrarAlerta("Error de Validación", e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    private void editarVendedor(Vendedor vendedor) {
        Dialog<VendedorFormResult> dialog = crearDialogoVendedor(vendedor);
        Optional<VendedorFormResult> resultado = dialog.showAndWait();
        
        resultado.ifPresent(form -> {
            try {
                vendedor.setNombre(form.getNombre());
                vendedor.setTelefono(form.getTelefono());
                vendedor.setCorreo(form.getCorreo());
                
                vendedorController.actualizarVendedor(vendedor);
                
                mostrarAlerta("Éxito", "Vendedor actualizado correctamente", Alert.AlertType.INFORMATION);
                cargarVendedores();
                
            } catch (SQLException e) {
                mostrarAlerta("Error", "Error al actualizar vendedor:\n" + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                mostrarAlerta("Error de Validación", e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    private void eliminarVendedor(Vendedor vendedor) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Eliminar el vendedor?");
        confirmacion.setContentText(
            "Vendedor: " + vendedor.getNombre() + "\n\n" +
            "Esta acción no se puede deshacer."
        );
        
        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                boolean eliminado = vendedorController.eliminarVendedor(vendedor.getIdVendedor());
                
                if (eliminado) {
                    mostrarAlerta("Éxito", "Vendedor eliminado correctamente", Alert.AlertType.INFORMATION);
                    cargarVendedores();
                } else {
                    mostrarAlerta("Error", "No se pudo eliminar el vendedor", Alert.AlertType.ERROR);
                }
                
            } catch (SQLException e) {
                mostrarAlerta("Error", 
                    "Error al eliminar vendedor:\n" + e.getMessage() + 
                    "\n\nEs posible que el vendedor tenga ventas asociadas.", 
                    Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    private Dialog<VendedorFormResult> crearDialogoVendedor(Vendedor vendedorExistente) {
        Dialog<VendedorFormResult> dialog = new Dialog<>();
        dialog.setTitle(vendedorExistente == null ? "Nuevo Vendedor" : "Editar Vendedor");
        dialog.setHeaderText(vendedorExistente == null ? 
            "Ingrese los datos del nuevo vendedor" : 
            "Modifique los datos del vendedor");

        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField txtNombre = new TextField();
        TextField txtTelefono = new TextField();
        TextField txtCorreo = new TextField();

        if (vendedorExistente != null) {
            txtNombre.setText(vendedorExistente.getNombre());
            txtTelefono.setText(vendedorExistente.getTelefono());
            txtCorreo.setText(vendedorExistente.getCorreo());
        }

        grid.add(new Label("Nombre Completo:*"), 0, 0);
        grid.add(txtNombre, 1, 0);
        grid.add(new Label("Teléfono:"), 0, 1);
        grid.add(txtTelefono, 1, 1);
        grid.add(new Label("Correo Electrónico:"), 0, 2);
        grid.add(txtCorreo, 1, 2);
        
        Label lblInfo = new Label("* Campos obligatorios");
        lblInfo.setStyle("-fx-font-size: 10; -fx-text-fill: #666;");
        grid.add(lblInfo, 0, 3, 2, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnGuardar) {
                String nombre = txtNombre.getText().trim();
                String telefono = txtTelefono.getText().trim();
                String correo = txtCorreo.getText().trim();

                if (nombre.isEmpty()) {
                    mostrarAlerta("Error", "El nombre es obligatorio", Alert.AlertType.ERROR);
                    return null;
                }

                if (!telefono.isEmpty() && !ValidadorUtil.esTelefonoValido(telefono)) {
                    mostrarAlerta("Error", "Teléfono inválido (debe tener 7-10 dígitos)", Alert.AlertType.ERROR);
                    return null;
                }

                if (!correo.isEmpty() && !ValidadorUtil.esEmailValido(correo)) {
                    mostrarAlerta("Error", "Correo electrónico inválido", Alert.AlertType.ERROR);
                    return null;
                }

                return new VendedorFormResult(nombre, telefono, correo);
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
