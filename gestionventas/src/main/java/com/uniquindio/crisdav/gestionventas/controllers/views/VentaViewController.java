package com.uniquindio.crisdav.gestionventas.controllers.views;

import com.uniquindio.crisdav.gestionventas.controllers.*;
import com.uniquindio.crisdav.gestionventas.models.dto.ClienteFormResult;
import com.uniquindio.crisdav.gestionventas.models.entity.*;
import com.uniquindio.crisdav.gestionventas.utils.ValidadorUtil;
import com.uniquindio.crisdav.gestionventas.models.vo.ProductoConCategoriaVO;
import com.uniquindio.crisdav.gestionventas.utils.FormatoUtil;
import com.uniquindio.crisdav.gestionventas.utils.SessionManager;
import com.uniquindio.crisdav.gestionventas.models.dto.ItemVentaUI;

import javafx.geometry.Insets;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VentaViewController {

    // Información del cliente
    @FXML private TextField txtCedulaCliente;
    @FXML private Label lblNombreCliente;
    @FXML private Label lblTelefonoCliente;
    @FXML private Label lblCreditoActivo;

    // Tipo de venta
    @FXML private RadioButton rbContado;
    @FXML private RadioButton rbCredito;
    @FXML private Label lblCuotas;
    @FXML private ComboBox<String> comboCuotas;

    // Productos
    @FXML private TextField txtCodigoProducto;
    @FXML private Spinner<Integer> spinCantidad;
    @FXML private Label lblProductoSeleccionado;
    @FXML private Label lblPrecioProducto;
    @FXML private Label lblStockProducto;

    // Tabla
    @FXML private TableView<ItemVentaUI> tablaProductos;
    @FXML private TableColumn<ItemVentaUI, String> colCodigo;
    @FXML private TableColumn<ItemVentaUI, String> colNombre;
    @FXML private TableColumn<ItemVentaUI, Integer> colCantidad;
    @FXML private TableColumn<ItemVentaUI, String> colPrecio;
    @FXML private TableColumn<ItemVentaUI, String> colIva;
    @FXML private TableColumn<ItemVentaUI, String> colSubtotal;
    @FXML private TableColumn<ItemVentaUI, Void> colAcciones;

    // Totales
    @FXML private Label lblSubtotal;
    @FXML private Label lblIva;
    @FXML private Label lblTotal;
    @FXML private VBox infoCreditoBox;
    @FXML private Label lblCuotaInicial;
    @FXML private Label lblSaldoFinanciar;
    @FXML private Label lblValorCuota;

    // Controllers
    private ClienteController clienteController;
    private ProductoController productoController;
    private VentaController ventaController;
    private VendedorController vendedorController;

    // Datos
    private Cliente clienteSeleccionado;
    private ProductoConCategoriaVO productoSeleccionado;
    private ObservableList<ItemVentaUI> itemsVenta;

    @FXML
    public void initialize() {
        clienteController = new ClienteController();
        productoController = new ProductoController();
        ventaController = new VentaController();
        vendedorController = new VendedorController();

        itemsVenta = FXCollections.observableArrayList();

        configurarTabla();
        configurarSpinner();
        configurarTipoVenta();
        configurarCombobox();
        
        comboCuotas.setValue("12");
    }

    private void configurarCombobox(){
        // ComboBox Cuotas
        comboCuotas.setItems(FXCollections.observableArrayList(
            "12",
            "18",
            "24"
        ));
        comboCuotas.setValue("12");
    }

    private void configurarTabla() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPrecio.setCellValueFactory(cellData -> 
            new SimpleStringProperty(FormatoUtil.formatearMoneda(cellData.getValue().getPrecioUnitario())));
        colIva.setCellValueFactory(cellData -> 
            new SimpleStringProperty(FormatoUtil.formatearPorcentaje(cellData.getValue().getIva())));
        colSubtotal.setCellValueFactory(cellData -> 
            new SimpleStringProperty(FormatoUtil.formatearMoneda(cellData.getValue().getSubtotal())));

        // Botón eliminar en cada fila
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnEliminar = new Button("🗑️");
            {
                btnEliminar.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
                btnEliminar.setOnAction(event -> {
                    ItemVentaUI item = getTableView().getItems().get(getIndex());
                    eliminarItem(item);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnEliminar);
            }
        });

        tablaProductos.setItems(itemsVenta);
    }

    private void configurarSpinner() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 1);
        spinCantidad.setValueFactory(valueFactory);
    }

    private void configurarTipoVenta() {
        rbCredito.selectedProperty().addListener((obs, oldVal, newVal) -> {
            boolean esCredito = newVal;
            lblCuotas.setVisible(esCredito);
            lblCuotas.setManaged(esCredito);
            comboCuotas.setVisible(esCredito);
            comboCuotas.setManaged(esCredito);
            infoCreditoBox.setVisible(esCredito);
            infoCreditoBox.setManaged(esCredito);
            
            if (esCredito) {
                calcularTotales();
            }
        });
    }

    @FXML
    private void buscarCliente(ActionEvent event) {
        String cedula = txtCedulaCliente.getText().trim();
        
        if (cedula.isEmpty()) {
            mostrarAlerta("Error", "Ingrese una cédula", Alert.AlertType.ERROR);
            return;
        }

        try {
            clienteSeleccionado = clienteController.buscarClientePorCedula(cedula);
            
            if (clienteSeleccionado != null) {
                lblNombreCliente.setText(clienteSeleccionado.getNombre());
                lblTelefonoCliente.setText(clienteSeleccionado.getTelefono());
                lblCreditoActivo.setText(clienteSeleccionado.getTieneCreditoActivo() ? "Sí" : "No");
                lblCreditoActivo.setStyle(clienteSeleccionado.getTieneCreditoActivo() ? 
                    "-fx-text-fill: red; -fx-font-weight: bold;" : 
                    "-fx-text-fill: green; -fx-font-weight: bold;");
            } else {
                mostrarAlerta("No Encontrado", "Cliente no encontrado", Alert.AlertType.WARNING);
                limpiarCliente();
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al buscar cliente: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
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
                
            } catch (SQLException e) {
                mostrarAlerta("Error", "Error al crear cliente:\n" + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                mostrarAlerta("Error de Validación", e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    @FXML
    private void buscarProducto(ActionEvent event) {
        String codigo = txtCodigoProducto.getText().trim();
        
        if (codigo.isEmpty()) {
            mostrarAlerta("Error", "Ingrese un código de producto", Alert.AlertType.ERROR);
            return;
        }

        try {
            Producto producto = productoController.buscarProductoPorCodigo(codigo);
            
            if (producto != null) {
                // Buscar con categoría para obtener IVA
                List<ProductoConCategoriaVO> productos = productoController.listarProductosConCategoria();
                productoSeleccionado = productos.stream()
                    .filter(p -> p.getCodigo().equals(codigo))
                    .findFirst()
                    .orElse(null);
                
                if (productoSeleccionado != null) {
                    lblProductoSeleccionado.setText(productoSeleccionado.getNombre());
                    lblPrecioProducto.setText(FormatoUtil.formatearMoneda(productoSeleccionado.getValorVenta()));
                    lblStockProducto.setText(String.valueOf(productoSeleccionado.getStock()));
                }
            } else {
                mostrarAlerta("No Encontrado", "Producto no encontrado", Alert.AlertType.WARNING);
                limpiarProducto();
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al buscar producto: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void agregarProducto(ActionEvent event) {
        if (productoSeleccionado == null) {
            mostrarAlerta("Error", "Seleccione un producto primero", Alert.AlertType.ERROR);
            return;
        }

        int cantidad = spinCantidad.getValue();
        
        // Validar stock
        if (cantidad > productoSeleccionado.getStock()) {
            mostrarAlerta("Stock Insuficiente", 
                "Solo hay " + productoSeleccionado.getStock() + " unidades disponibles", 
                Alert.AlertType.WARNING);
            return;
        }

        // Verificar si ya existe en la lista
        Optional<ItemVentaUI> itemExistente = itemsVenta.stream()
            .filter(i -> i.getIdProducto().equals(productoSeleccionado.getIdProducto()))
            .findFirst();

        if (itemExistente.isPresent()) {
            // Actualizar cantidad
            ItemVentaUI item = itemExistente.get();
            int nuevaCantidad = item.getCantidad() + cantidad;
            
            if (nuevaCantidad > productoSeleccionado.getStock()) {
                mostrarAlerta("Stock Insuficiente", 
                    "La cantidad total excede el stock disponible", 
                    Alert.AlertType.WARNING);
                return;
            }
            
            item.setCantidad(nuevaCantidad);
            item.calcularSubtotal();
        } else {
            // Agregar nuevo item
            ItemVentaUI nuevoItem = new ItemVentaUI(
                productoSeleccionado.getIdProducto(),
                productoSeleccionado.getCodigo(),
                productoSeleccionado.getNombre(),
                cantidad,
                productoSeleccionado.getValorVenta(),
                productoSeleccionado.getIva()
            );
            itemsVenta.add(nuevoItem);
        }

        tablaProductos.refresh();
        calcularTotales();
        limpiarProducto();
        txtCodigoProducto.clear();
        txtCodigoProducto.requestFocus();
        spinCantidad.getValueFactory().setValue(1);
    }

    @FXML
    private void eliminarProducto(ActionEvent event) {
        ItemVentaUI seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        
        if (seleccionado != null) {
            eliminarItem(seleccionado);
        } else {
            mostrarAlerta("Error", "Seleccione un producto para eliminar", Alert.AlertType.ERROR);
        }
    }

    private void eliminarItem(ItemVentaUI item) {
        itemsVenta.remove(item);
        calcularTotales();
    }

    private void calcularTotales() {
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalIva = BigDecimal.ZERO;

        for (ItemVentaUI item : itemsVenta) {
            subtotal = subtotal.add(item.getSubtotal());
            BigDecimal ivaItem = item.getSubtotal().multiply(item.getIva());
            totalIva = totalIva.add(ivaItem);
        }

        BigDecimal total = subtotal.add(totalIva);

        lblSubtotal.setText(FormatoUtil.formatearMoneda(subtotal));
        lblIva.setText(FormatoUtil.formatearMoneda(totalIva));
        lblTotal.setText(FormatoUtil.formatearMoneda(total));

        // Calcular info de crédito si es necesario
        if (rbCredito.isSelected()) {
            BigDecimal cuotaInicial = total.multiply(new BigDecimal("0.30")).setScale(2, RoundingMode.HALF_UP);
            BigDecimal saldoSinInteres = total.multiply(new BigDecimal("0.70")).setScale(2, RoundingMode.HALF_UP);
            BigDecimal saldoFinanciado = saldoSinInteres.multiply(new BigDecimal("1.05")).setScale(2, RoundingMode.HALF_UP);
            
            int numCuotas = Integer.parseInt(comboCuotas.getValue());
            BigDecimal valorCuota = saldoFinanciado.divide(BigDecimal.valueOf(numCuotas), 2, RoundingMode.HALF_UP);

            lblCuotaInicial.setText(FormatoUtil.formatearMoneda(cuotaInicial));
            lblSaldoFinanciar.setText(FormatoUtil.formatearMoneda(saldoFinanciado));
            lblValorCuota.setText(FormatoUtil.formatearMoneda(valorCuota));
        }
    }

    @FXML
    private void procesarVenta(ActionEvent event) {
        // Validaciones
        if (clienteSeleccionado == null) {
            mostrarAlerta("Error", "Debe seleccionar un cliente", Alert.AlertType.ERROR);
            return;
        }

        if (itemsVenta.isEmpty()) {
            mostrarAlerta("Error", "Debe agregar al menos un producto", Alert.AlertType.ERROR);
            return;
        }

        // Validar crédito
        if (rbCredito.isSelected() && clienteSeleccionado.getTieneCreditoActivo()) {
            mostrarAlerta("Error", 
                "El cliente ya tiene un crédito activo. No puede solicitar otro.", 
                Alert.AlertType.ERROR);
            return;
        }

        // Confirmación
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Venta");
        confirmacion.setHeaderText("¿Confirmar la venta?");
        confirmacion.setContentText(
            "Cliente: " + clienteSeleccionado.getNombre() + "\n" +
            "Tipo: " + (rbContado.isSelected() ? "Contado" : "Crédito") + "\n" +
            "Total: " + lblTotal.getText()
        );

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            realizarVenta();
        }
    }

    private void realizarVenta() {
        try {
            // Convertir items UI a items de venta
            List<VentaController.ItemVenta> items = new ArrayList<>();
            for (ItemVentaUI itemUI : itemsVenta) {
                VentaController.ItemVenta item = ventaController.crearItem(
                    itemUI.getIdProducto(), 
                    itemUI.getCantidad()
                );
                items.add(item);
            }

            // Obtener primer vendedor (simplificado, podrías tener un selector)
            List<Vendedor> vendedores = vendedorController.listarVendedores();
            if (vendedores.isEmpty()) {
                mostrarAlerta("Error", "No hay vendedores registrados", Alert.AlertType.ERROR);
                return;
            }
            Integer idVendedor = vendedores.get(0).getIdVendedor();

            Integer idVenta;
            
            if (rbContado.isSelected()) {
                // Venta de contado
                idVenta = ventaController.procesarVentaContado(
                    clienteSeleccionado.getIdCliente(),
                    idVendedor,
                    SessionManager.getInstance().getIdUsuarioActual(),
                    items
                );
            } else {
                // Venta a crédito
                int numCuotas = Integer.parseInt(comboCuotas.getValue());
                idVenta = ventaController.procesarVentaCredito(
                    clienteSeleccionado.getIdCliente(),
                    idVendedor,
                    SessionManager.getInstance().getIdUsuarioActual(),
                    items,
                    numCuotas
                );
            }

            // Mostrar mensaje de éxito
            Alert exitoAlert = new Alert(Alert.AlertType.INFORMATION);
            exitoAlert.setTitle("Venta Exitosa");
            exitoAlert.setHeaderText("La venta se registró correctamente");
            exitoAlert.setContentText("Número de venta: " + idVenta);
            
            ButtonType btnImprimir = new ButtonType("Imprimir Factura");
            ButtonType btnCerrar = new ButtonType("Cerrar", ButtonBar.ButtonData.CANCEL_CLOSE);
            exitoAlert.getButtonTypes().setAll(btnImprimir, btnCerrar);
            
            Optional<ButtonType> resultado = exitoAlert.showAndWait();
            if (resultado.isPresent() && resultado.get() == btnImprimir) {
                // Aquí podrías generar e imprimir la factura
                mostrarAlerta("Información", "Funcionalidad de impresión por implementar", Alert.AlertType.INFORMATION);
            }

            limpiarFormulario(null);

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al procesar la venta:\n" + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            mostrarAlerta("Error de Validación", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void limpiarFormulario(ActionEvent event) {
        limpiarCliente();
        limpiarProducto();
        itemsVenta.clear();
        calcularTotales();
        txtCedulaCliente.clear();
        txtCodigoProducto.clear();
        rbContado.setSelected(true);
        spinCantidad.getValueFactory().setValue(1);
    }

    @FXML
    private void cancelar(ActionEvent event) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Cancelar");
        confirmacion.setHeaderText("¿Cancelar la venta actual?");
        confirmacion.setContentText("Se perderán todos los datos ingresados.");
        
        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            limpiarFormulario(null);
        }
    }

    private void limpiarCliente() {
        clienteSeleccionado = null;
        lblNombreCliente.setText("-");
        lblTelefonoCliente.setText("-");
        lblCreditoActivo.setText("-");
        lblCreditoActivo.setStyle("");
    }

    private void limpiarProducto() {
        productoSeleccionado = null;
        lblProductoSeleccionado.setText("Ninguno");
        lblPrecioProducto.setText("$0.00");
        lblStockProducto.setText("0");
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
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
}