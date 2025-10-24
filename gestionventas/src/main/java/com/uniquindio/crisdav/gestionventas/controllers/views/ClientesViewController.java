package com.uniquindio.crisdav.gestionventas.controllers.views;

import com.uniquindio.crisdav.gestionventas.controllers.ClienteController;
import com.uniquindio.crisdav.gestionventas.controllers.VentaController;
import com.uniquindio.crisdav.gestionventas.models.dto.ClienteFormResult;
import com.uniquindio.crisdav.gestionventas.models.entity.Cliente;
import com.uniquindio.crisdav.gestionventas.models.entity.Venta;
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
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ClientesViewController {

    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> comboFiltroCredito;
    @FXML private TableView<Cliente> tablaClientes;
    @FXML private TableColumn<Cliente, Integer> colId;
    @FXML private TableColumn<Cliente, String> colCedula;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colDireccion;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, String> colCorreo;
    @FXML private TableColumn<Cliente, String> colCreditoActivo;
    @FXML private TableColumn<Cliente, Void> colAcciones;
    @FXML private Label lblTotalClientes;
    @FXML private Label lblClientesConCredito;
    @FXML private Label lblClientesSinCredito;

    private ClienteController clienteController;
    private VentaController ventaController;
    private ObservableList<Cliente> listaClientes;
    private ObservableList<Cliente> listaClientesFiltrada;

    @FXML
    public void initialize() {
        clienteController = new ClienteController();
        ventaController = new VentaController();
        listaClientes = FXCollections.observableArrayList();
        listaClientesFiltrada = FXCollections.observableArrayList();

        configurarTabla();
        cargarClientes();
        configurarBusqueda();
        configurarCombobox();
        
        
        comboFiltroCredito.setValue("Todos los clientes");
    }

    private void configurarCombobox(){
        // ComboBox Filtro Crédito
        comboFiltroCredito.setItems(FXCollections.observableArrayList(
            "Todos los clientes",
            "Con crédito activo",
            "Sin crédito activo"
        ));
        comboFiltroCredito.setValue("12");
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idCliente"));
        colCedula.setCellValueFactory(new PropertyValueFactory<>("cedula"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colTelefono.setCellValueFactory(cellData -> 
            new SimpleStringProperty(FormatoUtil.formatearTelefono(cellData.getValue().getTelefono())));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        
        // Columna de crédito activo con estilos
        colCreditoActivo.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getTieneCreditoActivo() ? "Sí" : "No"));
        
        colCreditoActivo.setCellFactory(column -> new TableCell<Cliente, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("Sí")) {
                        setStyle("-fx-background-color: #fff3cd; -fx-text-fill: #856404; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-background-color: #d4edda; -fx-text-fill: #155724; -fx-font-weight: bold;");
                    }
                }
            }
        });

        // Configurar columna de acciones con botones
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
                    Cliente cliente = getTableView().getItems().get(getIndex());
                    editarCliente(cliente);
                });

                btnVer.setOnAction(event -> {
                    Cliente cliente = getTableView().getItems().get(getIndex());
                    verDetalleCliente(cliente);
                });

                btnEliminar.setOnAction(event -> {
                    Cliente cliente = getTableView().getItems().get(getIndex());
                    eliminarCliente(cliente);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });

        tablaClientes.setItems(listaClientesFiltrada);
    }

    private void configurarBusqueda() {
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrarClientes();
        });
    }

    @FXML
    private void aplicarFiltros(ActionEvent event) {
        filtrarClientes();
    }

    private void filtrarClientes() {
        String criterio = txtBuscar.getText();
        String filtroCredito = comboFiltroCredito.getValue();
        
        List<Cliente> filtrados = listaClientes.stream()
            .filter(c -> {
                // Filtro de búsqueda
                boolean coincideBusqueda = true;
                if (criterio != null && !criterio.trim().isEmpty()) {
                    String criterioLower = criterio.toLowerCase();
                    coincideBusqueda = c.getCedula().toLowerCase().contains(criterioLower) ||
                                      c.getNombre().toLowerCase().contains(criterioLower) ||
                                      (c.getTelefono() != null && c.getTelefono().contains(criterioLower));
                }
                
                // Filtro de crédito
                boolean coincideCredito = true;
                if (filtroCredito != null) {
                    if (filtroCredito.equals("Con crédito activo")) {
                        coincideCredito = c.getTieneCreditoActivo();
                    } else if (filtroCredito.equals("Sin crédito activo")) {
                        coincideCredito = !c.getTieneCreditoActivo();
                    }
                }
                
                return coincideBusqueda && coincideCredito;
            })
            .collect(Collectors.toList());
        
        listaClientesFiltrada.setAll(filtrados);
    }

    @FXML
    private void cargarClientes() {
        try {
            List<Cliente> clientes = clienteController.listarClientes();
            listaClientes.setAll(clientes);
            listaClientesFiltrada.setAll(clientes);
            
            actualizarEstadisticas();
            
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar clientes:\n" + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void actualizarEstadisticas() {
        lblTotalClientes.setText(String.valueOf(listaClientes.size()));
        
        long conCredito = listaClientes.stream()
            .filter(Cliente::getTieneCreditoActivo)
            .count();
        lblClientesConCredito.setText(String.valueOf(conCredito));
        
        long sinCredito = listaClientes.size() - conCredito;
        lblClientesSinCredito.setText(String.valueOf(sinCredito));
    }

    @FXML
    private void nuevoCliente(ActionEvent event) {
        Dialog<ClienteFormResult> dialog = crearDialogoCliente(null);
        Optional<ClienteFormResult> resultado = dialog.showAndWait();
        
        resultado.ifPresent(form -> {
            try {
                clienteController.crearCliente(
                    form.getCedula(),
                    form.getNombre(),
                    form.getDireccion(),
                    form.getTelefono(),
                    form.getCorreo()
                );
                
                mostrarAlerta("Éxito", "Cliente creado correctamente", Alert.AlertType.INFORMATION);
                cargarClientes();
                
            } catch (SQLException e) {
                mostrarAlerta("Error", "Error al crear cliente:\n" + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                mostrarAlerta("Error de Validación", e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    private void editarCliente(Cliente cliente) {
        Dialog<ClienteFormResult> dialog = crearDialogoCliente(cliente);
        Optional<ClienteFormResult> resultado = dialog.showAndWait();
        
        resultado.ifPresent(form -> {
            try {
                cliente.setCedula(form.getCedula());
                cliente.setNombre(form.getNombre());
                cliente.setDireccion(form.getDireccion());
                cliente.setTelefono(form.getTelefono());
                cliente.setCorreo(form.getCorreo());
                
                clienteController.actualizarCliente(cliente);
                
                mostrarAlerta("Éxito", "Cliente actualizado correctamente", Alert.AlertType.INFORMATION);
                cargarClientes();
                
            } catch (SQLException e) {
                mostrarAlerta("Error", "Error al actualizar cliente:\n" + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                mostrarAlerta("Error de Validación", e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    private void verDetalleCliente(Cliente cliente) {
        Alert detalleDialog = new Alert(Alert.AlertType.INFORMATION);
        detalleDialog.setTitle("Detalle del Cliente");
        detalleDialog.setHeaderText("Información Completa");
        
        VBox contenido = new VBox(10);
        contenido.setPadding(new Insets(10));
        
        // Información básica
        GridPane gridInfo = new GridPane();
        gridInfo.setHgap(10);
        gridInfo.setVgap(8);
        
        gridInfo.add(new Label("ID:"), 0, 0);
        gridInfo.add(new Label(cliente.getIdCliente().toString()), 1, 0);
        
        gridInfo.add(new Label("Cédula:"), 0, 1);
        gridInfo.add(new Label(cliente.getCedula()), 1, 1);
        
        gridInfo.add(new Label("Nombre:"), 0, 2);
        gridInfo.add(new Label(cliente.getNombre()), 1, 2);
        
        gridInfo.add(new Label("Dirección:"), 0, 3);
        gridInfo.add(new Label(cliente.getDireccion() != null ? cliente.getDireccion() : "-"), 1, 3);
        
        gridInfo.add(new Label("Teléfono:"), 0, 4);
        gridInfo.add(new Label(FormatoUtil.formatearTelefono(cliente.getTelefono())), 1, 4);
        
        gridInfo.add(new Label("Correo:"), 0, 5);
        gridInfo.add(new Label(cliente.getCorreo() != null ? cliente.getCorreo() : "-"), 1, 5);
        
        gridInfo.add(new Label("Crédito Activo:"), 0, 6);
        Label lblCredito = new Label(cliente.getTieneCreditoActivo() ? "Sí" : "No");
        lblCredito.setStyle(cliente.getTieneCreditoActivo() ? 
            "-fx-text-fill: #dc3545; -fx-font-weight: bold;" : 
            "-fx-text-fill: #28a745; -fx-font-weight: bold;");
        gridInfo.add(lblCredito, 1, 6);
        
        contenido.getChildren().add(gridInfo);
        
        // Historial de compras
        try {
            List<Venta> ventas = ventaController.listarVentasPorCliente(cliente.getIdCliente());
            
            if (!ventas.isEmpty()) {
                contenido.getChildren().add(new Separator());
                Label lblHistorial = new Label("Historial de Compras (" + ventas.size() + " ventas)");
                lblHistorial.setStyle("-fx-font-weight: bold; -fx-font-size: 13;");
                contenido.getChildren().add(lblHistorial);
                
                VBox listaVentas = new VBox(5);
                for (int i = 0; i < Math.min(ventas.size(), 5); i++) {
                    Venta v = ventas.get(i);
                    HBox ventaBox = new HBox(10);
                    ventaBox.getChildren().addAll(
                        new Label("Venta #" + v.getIdVenta()),
                        new Label(FormatoUtil.formatearFecha(v.getFecha())),
                        new Label(v.getTipoVenta().getValor()),
                        new Label(FormatoUtil.formatearMoneda(v.getTotal()))
                    );
                    listaVentas.getChildren().add(ventaBox);
                }
                
                if (ventas.size() > 5) {
                    listaVentas.getChildren().add(new Label("... y " + (ventas.size() - 5) + " más"));
                }
                
                contenido.getChildren().add(listaVentas);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        detalleDialog.getDialogPane().setContent(contenido);
        detalleDialog.showAndWait();
    }

    private void eliminarCliente(Cliente cliente) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Eliminar el cliente?");
        confirmacion.setContentText(
            "Cliente: " + cliente.getNombre() + "\n" +
            "Cédula: " + cliente.getCedula() + "\n\n" +
            "Esta acción no se puede deshacer."
        );
        
        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                boolean eliminado = clienteController.eliminarCliente(cliente.getIdCliente());
                
                if (eliminado) {
                    mostrarAlerta("Éxito", "Cliente eliminado correctamente", Alert.AlertType.INFORMATION);
                    cargarClientes();
                } else {
                    mostrarAlerta("Error", "No se pudo eliminar el cliente", Alert.AlertType.ERROR);
                }
                
            } catch (SQLException e) {
                mostrarAlerta("Error", 
                    "Error al eliminar cliente:\n" + e.getMessage() + 
                    "\n\nEs posible que el cliente tenga ventas asociadas.", 
                    Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    private Dialog<ClienteFormResult> crearDialogoCliente(Cliente clienteExistente) {
        Dialog<ClienteFormResult> dialog = new Dialog<>();
        dialog.setTitle(clienteExistente == null ? "Nuevo Cliente" : "Editar Cliente");
        dialog.setHeaderText(clienteExistente == null ? 
            "Ingrese los datos del nuevo cliente" : 
            "Modifique los datos del cliente");

        // Botones
        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);

        // Crear formulario
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField txtCedula = new TextField();
        TextField txtNombre = new TextField();
        TextField txtDireccion = new TextField();
        TextField txtTelefono = new TextField();
        TextField txtCorreo = new TextField();

        // Si es edición, llenar datos
        if (clienteExistente != null) {
            txtCedula.setText(clienteExistente.getCedula());
            txtNombre.setText(clienteExistente.getNombre());
            txtDireccion.setText(clienteExistente.getDireccion());
            txtTelefono.setText(clienteExistente.getTelefono());
            txtCorreo.setText(clienteExistente.getCorreo());
        }

        // Agregar campos al grid
        grid.add(new Label("Cédula:*"), 0, 0);
        grid.add(txtCedula, 1, 0);
        grid.add(new Label("Nombre Completo:*"), 0, 1);
        grid.add(txtNombre, 1, 1);
        grid.add(new Label("Dirección:"), 0, 2);
        grid.add(txtDireccion, 1, 2);
        grid.add(new Label("Teléfono:*"), 0, 3);
        grid.add(txtTelefono, 1, 3);
        grid.add(new Label("Correo Electrónico:"), 0, 4);
        grid.add(txtCorreo, 1, 4);
        
        Label lblInfo = new Label("* Campos obligatorios");
        lblInfo.setStyle("-fx-font-size: 10; -fx-text-fill: #666;");
        grid.add(lblInfo, 0, 5, 2, 1);

        dialog.getDialogPane().setContent(grid);

        // Convertir resultado
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnGuardar) {
                String cedula = txtCedula.getText().trim();
                String nombre = txtNombre.getText().trim();
                String direccion = txtDireccion.getText().trim();
                String telefono = txtTelefono.getText().trim();
                String correo = txtCorreo.getText().trim();

                // Validaciones
                if (cedula.isEmpty() || nombre.isEmpty() || telefono.isEmpty()) {
                    mostrarAlerta("Error", "Cédula, nombre y teléfono son obligatorios", Alert.AlertType.ERROR);
                    return null;
                }

                if (!ValidadorUtil.esCedulaValida(cedula)) {
                    mostrarAlerta("Error", "Cédula inválida (debe tener 6-10 dígitos)", Alert.AlertType.ERROR);
                    return null;
                }

                if (!ValidadorUtil.esTelefonoValido(telefono)) {
                    mostrarAlerta("Error", "Teléfono inválido (debe tener 7-10 dígitos)", Alert.AlertType.ERROR);
                    return null;
                }

                if (!correo.isEmpty() && !ValidadorUtil.esEmailValido(correo)) {
                    mostrarAlerta("Error", "Correo electrónico inválido", Alert.AlertType.ERROR);
                    return null;
                }

                return new ClienteFormResult(cedula, nombre, direccion, telefono, correo);
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