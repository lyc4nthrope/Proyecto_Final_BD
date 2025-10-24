package com.uniquindio.crisdav.gestionventas.controllers.views;

import com.uniquindio.crisdav.gestionventas.controllers.ClienteController;
import com.uniquindio.crisdav.gestionventas.controllers.CuotaController;
import com.uniquindio.crisdav.gestionventas.controllers.VentaController;
import com.uniquindio.crisdav.gestionventas.models.dto.CreditoInfo;
import com.uniquindio.crisdav.gestionventas.models.entity.*;
import com.uniquindio.crisdav.gestionventas.models.enums.EstadoCuota;
import com.uniquindio.crisdav.gestionventas.utils.FormatoUtil;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GestionCuotasViewController {

    // Búsqueda y filtros
    @FXML private TextField txtBuscarCliente;
    @FXML private ComboBox<String> comboEstado;

    // Tabla de créditos
    @FXML private TableView<CreditoInfo> tablaCreditosActivos;
    @FXML private TableColumn<CreditoInfo, Integer> colCreditoId;
    @FXML private TableColumn<CreditoInfo, String> colCliente;
    @FXML private TableColumn<CreditoInfo, String> colFechaVenta;
    @FXML private TableColumn<CreditoInfo, String> colTotal;
    @FXML private TableColumn<CreditoInfo, String> colSaldo;
    @FXML private TableColumn<CreditoInfo, String> colNumCuotas;

    // Info del crédito
    @FXML private Label lblInfoCliente;
    @FXML private Label lblCuotaInicial;
    @FXML private Label lblTotalFinanciado;
    @FXML private Label lblSaldoPendiente;

    // Tabla de cuotas
    @FXML private TableView<Cuota> tablaCuotas;
    @FXML private TableColumn<Cuota, Integer> colNumCuota;
    @FXML private TableColumn<Cuota, String> colFechaVenc;
    @FXML private TableColumn<Cuota, String> colFechaPago;
    @FXML private TableColumn<Cuota, String> colValorCuota;
    @FXML private TableColumn<Cuota, String> colEstadoCuota;
    @FXML private TableColumn<Cuota, Void> colAccionesCuota;

    // Resumen cuotas
    @FXML private Label lblCuotasPendientes;
    @FXML private Label lblCuotasPagadas;
    @FXML private Label lblCuotasVencidas;
    @FXML private Label lblTotalAPagar;

    // Estadísticas generales
    @FXML private Label lblTotalCreditos;
    @FXML private Label lblMontoTotal;
    @FXML private Label lblSaldoTotalPendiente;
    @FXML private Label lblTotalCuotasVencidas;

    private CuotaController cuotaController;
    private VentaController ventaController;
    private ClienteController clienteController;
    
    private ObservableList<CreditoInfo> listaCreditos;
    private ObservableList<CreditoInfo> listaCreditosFiltrada;
    private ObservableList<Cuota> listaCuotas;
    
    private CreditoInfo creditoSeleccionado;

    @FXML
    public void initialize() {
        cuotaController = new CuotaController();
        ventaController = new VentaController();
        clienteController = new ClienteController();
        
        listaCreditos = FXCollections.observableArrayList();
        listaCreditosFiltrada = FXCollections.observableArrayList();
        listaCuotas = FXCollections.observableArrayList();

        configurarTablasCreditos();
        configurarTablaCuotas();
        configurarSeleccion();
        cargarCreditosActivos();
        configurarCombobox();
        
        
    }

    private void configurarCombobox(){
        // ComboBox estado
        comboEstado.setItems(FXCollections.observableArrayList(
            "Todos",
            "Con saldo pendiente",
            "Cancelados"
        ));
        comboEstado.setValue("Todos");
    }

    private void configurarTablasCreditos() {
        colCreditoId.setCellValueFactory(new PropertyValueFactory<>("idVentaCredito"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
        colFechaVenta.setCellValueFactory(cellData -> 
            new SimpleStringProperty(FormatoUtil.formatearFecha(cellData.getValue().getFechaVenta())));
        colTotal.setCellValueFactory(cellData -> 
            new SimpleStringProperty(FormatoUtil.formatearMoneda(cellData.getValue().getTotalVenta())));
        colSaldo.setCellValueFactory(cellData -> 
            new SimpleStringProperty(FormatoUtil.formatearMoneda(cellData.getValue().getSaldoPendiente())));
        colNumCuotas.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNumCuotas() + " meses"));

        // Resaltar créditos con cuotas vencidas
        tablaCreditosActivos.setRowFactory(tv -> new TableRow<CreditoInfo>() {
            @Override
            protected void updateItem(CreditoInfo item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else if (item.getTieneCuotasVencidas()) {
                    setStyle("-fx-background-color: #f8d7da;");
                } else if (item.getSaldoPendiente().compareTo(BigDecimal.ZERO) == 0) {
                    setStyle("-fx-background-color: #d4edda;");
                }
            }
        });

        tablaCreditosActivos.setItems(listaCreditosFiltrada);
    }

    private void configurarTablaCuotas() {
        colNumCuota.setCellValueFactory(new PropertyValueFactory<>("numCuota"));
        colFechaVenc.setCellValueFactory(cellData -> 
            new SimpleStringProperty(FormatoUtil.formatearFecha(cellData.getValue().getFechaVencimiento())));
        colFechaPago.setCellValueFactory(cellData -> {
            LocalDate fecha = cellData.getValue().getFechaPago();
            return new SimpleStringProperty(fecha != null ? FormatoUtil.formatearFecha(fecha) : "-");
        });
        colValorCuota.setCellValueFactory(cellData -> 
            new SimpleStringProperty(FormatoUtil.formatearMoneda(cellData.getValue().getValorCuota())));
        
        // Columna de estado con colores
        colEstadoCuota.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getEstado().getValor()));
        
        colEstadoCuota.setCellFactory(column -> new TableCell<Cuota, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("Pendiente")) {
                        setStyle("-fx-background-color: #fff3cd; -fx-text-fill: #856404; -fx-font-weight: bold;");
                    } else if (item.equals("Pagada")) {
                        setStyle("-fx-background-color: #d4edda; -fx-text-fill: #155724; -fx-font-weight: bold;");
                    } else if (item.equals("Vencida")) {
                        setStyle("-fx-background-color: #f8d7da; -fx-text-fill: #721c24; -fx-font-weight: bold;");
                    }
                }
            }
        });

        // Botón pagar
        colAccionesCuota.setCellFactory(param -> new TableCell<>() {
            private final Button btnPagar = new Button("💰 Pagar");
            {
                btnPagar.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 11;");
                btnPagar.setOnAction(event -> {
                    Cuota cuota = getTableView().getItems().get(getIndex());
                    pagarCuota(cuota);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Cuota cuota = getTableView().getItems().get(getIndex());
                    // Solo mostrar botón si está pendiente o vencida
                    if (cuota.getEstado() != EstadoCuota.PAGADA) {
                        setGraphic(btnPagar);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });

        tablaCuotas.setItems(listaCuotas);
    }

    private void configurarSeleccion() {
        tablaCreditosActivos.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    creditoSeleccionado = newValue;
                    cargarCuotasDelCredito(newValue);
                    mostrarInfoCredito(newValue);
                }
            }
        );
    }

    @FXML
    private void cargarCreditosActivos() {
        try {
            // Obtener todos los créditos con saldo pendiente
            List<VentaCredito> creditosActivos = new CuotaController().obtenerVentasCredito();
            
            listaCreditos.clear();
            
            // Por cada crédito, obtener información adicional
            for (VentaCredito vc : creditosActivos) {
                try {
                    // Obtener venta
                    Venta venta = ventaController.buscarVenta(vc.getIdVenta());
                    if (venta == null) continue;
                    
                    // Obtener cliente
                    Cliente cliente = clienteController.buscarCliente(venta.getIdCliente());
                    if (cliente == null) continue;
                    
                    // Verificar si tiene cuotas vencidas
                    List<Cuota> cuotas = cuotaController.listarCuotasPorVentaCredito(vc.getIdVentaCredito());
                    boolean tieneCuotasVencidas = cuotas.stream()
                        .anyMatch(c -> c.getEstado() == EstadoCuota.VENCIDA);
                    
                    CreditoInfo info = new CreditoInfo(
                        vc.getIdVentaCredito(),
                        vc.getIdVenta(),
                        cliente.getNombre(),
                        cliente.getCedula(),
                        venta.getFecha(),
                        venta.getTotal(),
                        vc.getCuotaInicial(),
                        vc.getSaldoFinanciado(),
                        vc.getSaldoPendiente(),
                        vc.getNumCuotas(),
                        tieneCuotasVencidas
                    );
                    
                    listaCreditos.add(info);
                    
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            
            listaCreditosFiltrada.setAll(listaCreditos);
            actualizarEstadisticasGenerales();
            
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al cargar créditos:\n" + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void cargarCuotasDelCredito(CreditoInfo credito) {
        try {
            List<Cuota> cuotas = cuotaController.listarCuotasPorVentaCredito(credito.getIdVentaCredito());
            listaCuotas.setAll(cuotas);
            actualizarResumenCuotas(cuotas);
            
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar cuotas:\n" + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void mostrarInfoCredito(CreditoInfo credito) {
        lblInfoCliente.setText(credito.getNombreCliente() + " (CC: " + credito.getCedulaCliente() + ")");
        lblCuotaInicial.setText(FormatoUtil.formatearMoneda(credito.getCuotaInicial()));
        lblTotalFinanciado.setText(FormatoUtil.formatearMoneda(credito.getSaldoFinanciado()));
        lblSaldoPendiente.setText(FormatoUtil.formatearMoneda(credito.getSaldoPendiente()));
    }

    private void actualizarResumenCuotas(List<Cuota> cuotas) {
        long pendientes = cuotas.stream().filter(c -> c.getEstado() == EstadoCuota.PENDIENTE).count();
        long pagadas = cuotas.stream().filter(c -> c.getEstado() == EstadoCuota.PAGADA).count();
        long vencidas = cuotas.stream().filter(c -> c.getEstado() == EstadoCuota.VENCIDA).count();
        
        BigDecimal totalAPagar = cuotas.stream()
            .filter(c -> c.getEstado() != EstadoCuota.PAGADA)
            .map(Cuota::getValorCuota)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        lblCuotasPendientes.setText(String.valueOf(pendientes));
        lblCuotasPagadas.setText(String.valueOf(pagadas));
        lblCuotasVencidas.setText(String.valueOf(vencidas));
        lblTotalAPagar.setText(FormatoUtil.formatearMoneda(totalAPagar));
    }

    private void actualizarEstadisticasGenerales() {
        lblTotalCreditos.setText(String.valueOf(listaCreditos.size()));
        
        BigDecimal montoTotal = listaCreditos.stream()
            .map(CreditoInfo::getTotalVenta)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        lblMontoTotal.setText(FormatoUtil.formatearMoneda(montoTotal));
        
        BigDecimal saldoTotal = listaCreditos.stream()
            .map(CreditoInfo::getSaldoPendiente)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        lblSaldoTotalPendiente.setText(FormatoUtil.formatearMoneda(saldoTotal));
        
        long cuotasVencidas = listaCreditos.stream()
            .filter(CreditoInfo::getTieneCuotasVencidas)
            .count();
        lblTotalCuotasVencidas.setText(String.valueOf(cuotasVencidas));
    }

    @FXML
    private void buscarCliente(ActionEvent event) {
        aplicarFiltros(null);
    }

    @FXML
    private void aplicarFiltros(ActionEvent event) {
        String criterio = txtBuscarCliente.getText();
        String estado = comboEstado.getValue();
        
        List<CreditoInfo> filtrados = listaCreditos.stream()
            .filter(c -> {
                // Filtro de búsqueda
                boolean coincideBusqueda = true;
                if (criterio != null && !criterio.trim().isEmpty()) {
                    String criterioLower = criterio.toLowerCase();
                    coincideBusqueda = c.getNombreCliente().toLowerCase().contains(criterioLower) ||
                                      c.getCedulaCliente().contains(criterio);
                }
                
                // Filtro de estado
                boolean coincideEstado = true;
                if (estado != null) {
                    if (estado.equals("Con saldo pendiente")) {
                        coincideEstado = c.getSaldoPendiente().compareTo(BigDecimal.ZERO) > 0;
                    } else if (estado.equals("Cancelados")) {
                        coincideEstado = c.getSaldoPendiente().compareTo(BigDecimal.ZERO) == 0;
                    }
                }
                
                return coincideBusqueda && coincideEstado;
            })
            .collect(Collectors.toList());
        
        listaCreditosFiltrada.setAll(filtrados);
    }

    @FXML
    private void actualizarVencidas(ActionEvent event) {
        try {
            cuotaController.actualizarCuotasVencidas();
            mostrarAlerta("Éxito", "Cuotas vencidas actualizadas correctamente", Alert.AlertType.INFORMATION);
            cargarCreditosActivos();
            
            if (creditoSeleccionado != null) {
                cargarCuotasDelCredito(creditoSeleccionado);
            }
            
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al actualizar cuotas vencidas:\n" + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void pagarCuota(Cuota cuota) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Pago de Cuota");
        confirmacion.setHeaderText("¿Confirmar el pago de la cuota?");
        
        VBox contenido = new VBox(10);
        contenido.getChildren().addAll(
            new Label("Número de cuota: " + cuota.getNumCuota()),
            new Label("Valor: " + FormatoUtil.formatearMoneda(cuota.getValorCuota())),
            new Label("Fecha vencimiento: " + FormatoUtil.formatearFecha(cuota.getFechaVencimiento())),
            new Label("Estado actual: " + cuota.getEstado().getValor())
        );
        
        if (cuota.getEstado() == EstadoCuota.VENCIDA) {
            Label advertencia = new Label("⚠️ Esta cuota está VENCIDA");
            advertencia.setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;");
            contenido.getChildren().add(advertencia);
        }
        
        confirmacion.getDialogPane().setContent(contenido);
        
        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                boolean exito = cuotaController.pagarCuota(cuota.getIdCuota());
                
                if (exito) {
                    // Obtener info actualizada
                    VentaCredito vc = cuotaController.obtenerVentaCredito(cuota.getIdVentaCredito());
                    
                    Alert exitoAlert = new Alert(Alert.AlertType.INFORMATION);
                    exitoAlert.setTitle("Pago Exitoso");
                    exitoAlert.setHeaderText("Cuota pagada correctamente");
                    
                    String mensaje = "Cuota #" + cuota.getNumCuota() + " pagada\n" +
                                   "Valor: " + FormatoUtil.formatearMoneda(cuota.getValorCuota()) + "\n" +
                                   "Saldo pendiente: " + FormatoUtil.formatearMoneda(vc.getSaldoPendiente());
                    
                    if (vc.getSaldoPendiente().compareTo(BigDecimal.ZERO) == 0) {
                        mensaje += "\n\n✅ ¡Crédito CANCELADO completamente!";
                        exitoAlert.setHeaderText("¡Crédito Cancelado!");
                    }
                    
                    exitoAlert.setContentText(mensaje);
                    exitoAlert.showAndWait();
                    
                    // Recargar datos
                    cargarCreditosActivos();
                    if (creditoSeleccionado != null) {
                        cargarCuotasDelCredito(creditoSeleccionado);
                    }
                    
                } else {
                    mostrarAlerta("Error", "No se pudo procesar el pago", Alert.AlertType.ERROR);
                }
                
            } catch (SQLException e) {
                mostrarAlerta("Error", "Error al pagar cuota:\n" + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }


}