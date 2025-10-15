package com.uniquindio.crisdav.gestionventas.controllers.views;

import com.uniquindio.crisdav.gestionventas.controllers.ReporteController;
import com.uniquindio.crisdav.gestionventas.models.vo.ClienteMorosoVO;
import com.uniquindio.crisdav.gestionventas.utils.FormatoUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ClientesMorososViewController {

    // Filtros y búsqueda
    @FXML private TextField txtBuscarCliente;
    @FXML private ComboBox<String> comboOrdenamiento;
    @FXML private Label lblFechaReporte;
    @FXML private Label lblHoraReporte;

    // Tabla
    @FXML private TableView<ClienteMorosoVO> tablaClientesMorosos;
    @FXML private TableColumn<ClienteMorosoVO, Void> colPrioridad;
    @FXML private TableColumn<ClienteMorosoVO, String> colCedula;
    @FXML private TableColumn<ClienteMorosoVO, String> colNombre;
    @FXML private TableColumn<ClienteMorosoVO, String> colTelefono;
    @FXML private TableColumn<ClienteMorosoVO, Integer> colCuotasVencidas;
    @FXML private TableColumn<ClienteMorosoVO, String> colTotalVencido;
    @FXML private TableColumn<ClienteMorosoVO, String> colSaldoPendiente;
    @FXML private TableColumn<ClienteMorosoVO, String> colDiasAtraso;
    @FXML private TableColumn<ClienteMorosoVO, String> colFechaVencida;
    @FXML private TableColumn<ClienteMorosoVO, Void> colAcciones;

    // Estadísticas
    @FXML private Label lblTotalMorosos;
    @FXML private Label lblTotalCuotasVencidas;
    @FXML private Label lblTotalDineroVencido;
    @FXML private Label lblTotalSaldoPendiente;

    // Niveles de morosidad
    @FXML private Label lblMorosidadLeve;
    @FXML private Label lblMorosidadModerada;
    @FXML private Label lblMorosidadGrave;
    @FXML private Label lblMorosidadCritica;

    private ReporteController reporteController;
    private ObservableList<ClienteMorosoVO> listaClientesMorosos;
    private ObservableList<ClienteMorosoVO> listaClientesFiltrada;

    @FXML
    public void initialize() {
        reporteController = new ReporteController();
        listaClientesMorosos = FXCollections.observableArrayList();
        listaClientesFiltrada = FXCollections.observableArrayList();

        configurarTabla();
        configurarFechaHora();
        cargarClientesMorosos();
        
        comboOrdenamiento.setValue("Más días atrasado");
    }

    private void configurarTabla() {
        // Columna de prioridad con colores
        colPrioridad.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    ClienteMorosoVO moroso = getTableView().getItems().get(getIndex());
                    long diasAtraso = ChronoUnit.DAYS.between(moroso.getFechaUltimaVencida(), LocalDate.now());
                    
                    Label lblPrioridad = new Label();
                    lblPrioridad.setStyle("-fx-font-weight: bold; -fx-padding: 5; -fx-background-radius: 3;");
                    
                    if (diasAtraso > 60) {
                        lblPrioridad.setText("🚨 CRÍTICA");
                        lblPrioridad.setStyle(lblPrioridad.getStyle() + "-fx-background-color: #d63031; -fx-text-fill: white;");
                    } else if (diasAtraso > 30) {
                        lblPrioridad.setText("🔴 GRAVE");
                        lblPrioridad.setStyle(lblPrioridad.getStyle() + "-fx-background-color: #f8d7da; -fx-text-fill: #721c24;");
                    } else if (diasAtraso > 15) {
                        lblPrioridad.setText("⚠️ MODERADA");
                        lblPrioridad.setStyle(lblPrioridad.getStyle() + "-fx-background-color: #ffeaa7; -fx-text-fill: #856404;");
                    } else {
                        lblPrioridad.setText("⚠️ LEVE");
                        lblPrioridad.setStyle(lblPrioridad.getStyle() + "-fx-background-color: #fff3cd; -fx-text-fill: #856404;");
                    }
                    
                    setGraphic(lblPrioridad);
                }
            }
        });

        colCedula.setCellValueFactory(new PropertyValueFactory<>("cedula"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
        colTelefono.setCellValueFactory(cellData -> 
            new SimpleStringProperty(FormatoUtil.formatearTelefono(cellData.getValue().getTelefono())));
        colCuotasVencidas.setCellValueFactory(new PropertyValueFactory<>("numCuotasVencidas"));
        
        colTotalVencido.setCellValueFactory(cellData -> 
            new SimpleStringProperty(FormatoUtil.formatearMoneda(cellData.getValue().getTotalVencido())));
        
        colSaldoPendiente.setCellValueFactory(cellData -> 
            new SimpleStringProperty(FormatoUtil.formatearMoneda(cellData.getValue().getSaldoPendiente())));
        
        colDiasAtraso.setCellValueFactory(cellData -> {
            long dias = ChronoUnit.DAYS.between(cellData.getValue().getFechaUltimaVencida(), LocalDate.now());
            return new SimpleStringProperty(dias + " días");
        });
        
        colFechaVencida.setCellValueFactory(cellData -> 
            new SimpleStringProperty(FormatoUtil.formatearFecha(cellData.getValue().getFechaUltimaVencida())));

        // Columna de acciones
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnVer = new Button("👁️ Ver");
            private final Button btnLlamar = new Button("📞");
            private final Button btnEmail = new Button("📧");
            private final HBox hbox = new HBox(5, btnVer, btnLlamar, btnEmail);

            {
                btnVer.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-size: 10;");
                btnLlamar.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 10;");
                btnEmail.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white; -fx-font-size: 10;");
                
                btnVer.setOnAction(event -> {
                    ClienteMorosoVO moroso = getTableView().getItems().get(getIndex());
                    verDetalleCliente(moroso);
                });

                btnLlamar.setOnAction(event -> {
                    ClienteMorosoVO moroso = getTableView().getItems().get(getIndex());
                    contactarCliente(moroso, "teléfono");
                });

                btnEmail.setOnAction(event -> {
                    ClienteMorosoVO moroso = getTableView().getItems().get(getIndex());
                    contactarCliente(moroso, "email");
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });

        // Resaltar filas según gravedad
        tablaClientesMorosos.setRowFactory(tv -> new TableRow<ClienteMorosoVO>() {
            @Override
            protected void updateItem(ClienteMorosoVO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else {
                    long diasAtraso = ChronoUnit.DAYS.between(item.getFechaUltimaVencida(), LocalDate.now());
                    if (diasAtraso > 60) {
                        setStyle("-fx-background-color: #f8d7da;");
                    } else if (diasAtraso > 30) {
                        setStyle("-fx-background-color: #fff3cd;");
                    }
                }
            }
        });

        tablaClientesMorosos.setItems(listaClientesFiltrada);
    }

    private void configurarFechaHora() {
        lblFechaReporte.setText("Fecha: " + FormatoUtil.formatearFecha(LocalDate.now()));
        lblHoraReporte.setText("Hora: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm a")));
    }

    @FXML
    private void cargarClientesMorosos() {
        try {
            List<ClienteMorosoVO> morosos = reporteController.obtenerClientesMorosos();
            listaClientesMorosos.setAll(morosos);
            listaClientesFiltrada.setAll(morosos);
            
            aplicarOrdenamiento(null);
            actualizarEstadisticas();
            actualizarNivelesMorosidad();
            
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar clientes morosos:\n" + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void buscarCliente(ActionEvent event) {
        String criterio = txtBuscarCliente.getText();
        
        if (criterio == null || criterio.trim().isEmpty()) {
            listaClientesFiltrada.setAll(listaClientesMorosos);
        } else {
            String criterioLower = criterio.toLowerCase();
            List<ClienteMorosoVO> filtrados = listaClientesMorosos.stream()
                .filter(c -> 
                    c.getCedula().toLowerCase().contains(criterioLower) ||
                    c.getNombreCliente().toLowerCase().contains(criterioLower)
                )
                .collect(Collectors.toList());
            listaClientesFiltrada.setAll(filtrados);
        }
        
        actualizarEstadisticas();
    }

    @FXML
    private void aplicarOrdenamiento(ActionEvent event) {
        String ordenamiento = comboOrdenamiento.getValue();
        
        List<ClienteMorosoVO> ordenados = listaClientesFiltrada.stream()
            .sorted((c1, c2) -> {
                switch (ordenamiento) {
                    case "Mayor deuda":
                        return c2.getTotalVencido().compareTo(c1.getTotalVencido());
                    case "Más cuotas vencidas":
                        return c2.getNumCuotasVencidas().compareTo(c1.getNumCuotasVencidas());
                    case "Más días atrasado":
                        long dias1 = ChronoUnit.DAYS.between(c1.getFechaUltimaVencida(), LocalDate.now());
                        long dias2 = ChronoUnit.DAYS.between(c2.getFechaUltimaVencida(), LocalDate.now());
                        return Long.compare(dias2, dias1);
                    case "Nombre (A-Z)":
                        return c1.getNombreCliente().compareTo(c2.getNombreCliente());
                    default:
                        return 0;
                }
            })
            .collect(Collectors.toList());
        
        listaClientesFiltrada.setAll(ordenados);
    }

    private void actualizarEstadisticas() {
        lblTotalMorosos.setText(String.valueOf(listaClientesFiltrada.size()));
        
        int totalCuotas = listaClientesFiltrada.stream()
            .mapToInt(ClienteMorosoVO::getNumCuotasVencidas)
            .sum();
        lblTotalCuotasVencidas.setText(String.valueOf(totalCuotas));
        
        BigDecimal totalVencido = listaClientesFiltrada.stream()
            .map(ClienteMorosoVO::getTotalVencido)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        lblTotalDineroVencido.setText(FormatoUtil.formatearMoneda(totalVencido));
        
        BigDecimal totalSaldo = listaClientesFiltrada.stream()
            .map(ClienteMorosoVO::getSaldoPendiente)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        lblTotalSaldoPendiente.setText(FormatoUtil.formatearMoneda(totalSaldo));
    }

    private void actualizarNivelesMorosidad() {
        long leve = listaClientesMorosos.stream()
            .filter(c -> {
                long dias = ChronoUnit.DAYS.between(c.getFechaUltimaVencida(), LocalDate.now());
                return dias >= 1 && dias <= 15;
            })
            .count();
        
        long moderada = listaClientesMorosos.stream()
            .filter(c -> {
                long dias = ChronoUnit.DAYS.between(c.getFechaUltimaVencida(), LocalDate.now());
                return dias >= 16 && dias <= 30;
            })
            .count();
        
        long grave = listaClientesMorosos.stream()
            .filter(c -> {
                long dias = ChronoUnit.DAYS.between(c.getFechaUltimaVencida(), LocalDate.now());
                return dias >= 31 && dias <= 60;
            })
            .count();
        
        long critica = listaClientesMorosos.stream()
            .filter(c -> {
                long dias = ChronoUnit.DAYS.between(c.getFechaUltimaVencida(), LocalDate.now());
                return dias > 60;
            })
            .count();
        
        lblMorosidadLeve.setText(String.valueOf(leve));
        lblMorosidadModerada.setText(String.valueOf(moderada));
        lblMorosidadGrave.setText(String.valueOf(grave));
        lblMorosidadCritica.setText(String.valueOf(critica));
    }

    private void verDetalleCliente(ClienteMorosoVO moroso) {
        Alert detalle = new Alert(Alert.AlertType.INFORMATION);
        detalle.setTitle("Detalle del Cliente Moroso");
        detalle.setHeaderText("Información Completa");
        
        VBox contenido = new VBox(15);
        contenido.setPadding(new Insets(10));
        
        // Información del cliente
        VBox infoCliente = new VBox(5);
        infoCliente.setStyle("-fx-border-color: #007bff; -fx-border-width: 2; -fx-padding: 10; -fx-background-radius: 5; -fx-border-radius: 5;");
        infoCliente.getChildren().addAll(
            new Label("📋 INFORMACIÓN DEL CLIENTE"),
            createInfoRow("Nombre:", moroso.getNombreCliente()),
            createInfoRow("Cédula:", moroso.getCedula()),
            createInfoRow("Teléfono:", FormatoUtil.formatearTelefono(moroso.getTelefono()))
        );
        
        // Información del crédito
        VBox infoCredito = new VBox(5);
        infoCredito.setStyle("-fx-border-color: #dc3545; -fx-border-width: 2; -fx-padding: 10; -fx-background-radius: 5; -fx-border-radius: 5;");
        infoCredito.getChildren().addAll(
            new Label("⚠️ INFORMACIÓN DE MOROSIDAD"),
            createInfoRow("Cuotas vencidas:", String.valueOf(moroso.getNumCuotasVencidas())),
            createInfoRow("Total vencido:", FormatoUtil.formatearMoneda(moroso.getTotalVencido())),
            createInfoRow("Saldo pendiente:", FormatoUtil.formatearMoneda(moroso.getSaldoPendiente())),
            createInfoRow("Fecha última vencida:", FormatoUtil.formatearFecha(moroso.getFechaUltimaVencida())),
            createInfoRow("Días de atraso:", ChronoUnit.DAYS.between(moroso.getFechaUltimaVencida(), LocalDate.now()) + " días")
        );
        
        contenido.getChildren().addAll(infoCliente, infoCredito);
        
        detalle.getDialogPane().setContent(contenido);
        detalle.getDialogPane().setMinWidth(500);
        detalle.showAndWait();
    }

    private HBox createInfoRow(String label, String value) {
        HBox row = new HBox(10);
        Label lblLabel = new Label(label);
        lblLabel.setStyle("-fx-font-weight: bold; -fx-min-width: 150;");
        Label lblValue = new Label(value);
        row.getChildren().addAll(lblLabel, lblValue);
        return row;
    }

    private void contactarCliente(ClienteMorosoVO moroso, String medio) {
        Alert contacto = new Alert(Alert.AlertType.INFORMATION);
        contacto.setTitle("Contactar Cliente");
        
        if (medio.equals("teléfono")) {
            contacto.setHeaderText("Llamar al Cliente");
            contacto.setContentText(
                "Cliente: " + moroso.getNombreCliente() + "\n" +
                "Teléfono: " + FormatoUtil.formatearTelefono(moroso.getTelefono()) + "\n\n" +
                "Deuda vencida: " + FormatoUtil.formatearMoneda(moroso.getTotalVencido()) + "\n" +
                "Cuotas vencidas: " + moroso.getNumCuotasVencidas() + "\n\n" +
                "Nota: Esta funcionalidad abrirá el marcador telefónico\n" +
                "en la implementación completa."
            );
        } else {
            contacto.setHeaderText("Enviar Email de Recordatorio");
            contacto.setContentText(
                "Se enviará un recordatorio de pago a:\n" +
                "Cliente: " + moroso.getNombreCliente() + "\n\n" +
                "Contenido del email:\n" +
                "- Cuotas vencidas: " + moroso.getNumCuotasVencidas() + "\n" +
                "- Monto vencido: " + FormatoUtil.formatearMoneda(moroso.getTotalVencido()) + "\n" +
                "- Días de atraso: " + ChronoUnit.DAYS.between(moroso.getFechaUltimaVencida(), LocalDate.now()) + "\n\n" +
                "Nota: La integración de email requiere configuración adicional."
            );
        }
        
        contacto.showAndWait();
    }

    @FXML
    private void exportarPDF(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Exportar Reporte a PDF");
        alert.setHeaderText("Generar Reporte de Clientes Morosos");
        alert.setContentText(
            "Resumen del reporte:\n\n" +
            "Total morosos: " + lblTotalMorosos.getText() + "\n" +
            "Total cuotas vencidas: " + lblTotalCuotasVencidas.getText() + "\n" +
            "Total dinero vencido: " + lblTotalDineroVencido.getText() + "\n" +
            "Total saldo pendiente: " + lblTotalSaldoPendiente.getText() + "\n\n" +
            "Niveles de morosidad:\n" +
            "- Leve: " + lblMorosidadLeve.getText() + "\n" +
            "- Moderada: " + lblMorosidadModerada.getText() + "\n" +
            "- Grave: " + lblMorosidadGrave.getText() + "\n" +
            "- Crítica: " + lblMorosidadCritica.getText() + "\n\n" +
            "Nota: La generación de PDF requiere librerías adicionales\n" +
            "(iText o Apache PDFBox). Implementación pendiente."
        );
        alert.showAndWait();
    }

    @FXML
    private void enviarRecordatorios(ActionEvent event) {
        if (listaClientesMorosos.isEmpty()) {
            mostrarAlerta("Sin Morosos", 
                "No hay clientes morosos para enviar recordatorios", 
                Alert.AlertType.INFORMATION);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Enviar Recordatorios Masivos");
        confirmacion.setHeaderText("¿Enviar recordatorios a todos los clientes morosos?");
        confirmacion.setContentText(
            "Se enviarán recordatorios de pago a:\n" +
            "- " + listaClientesMorosos.size() + " clientes morosos\n" +
            "- Por email y/o SMS\n\n" +
            "¿Desea continuar?"
        );
        
        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Alert resultado = new Alert(Alert.AlertType.INFORMATION);
                resultado.setTitle("Recordatorios Enviados");
                resultado.setHeaderText("Proceso Completado");
                resultado.setContentText(
                    "Se han enviado " + listaClientesMorosos.size() + " recordatorios.\n\n" +
                    "Nota: La funcionalidad de envío automático de emails/SMS\n" +
                    "requiere configuración de servicios externos.\n" +
                    "Implementación pendiente."
                );
                resultado.showAndWait();
            }
        });
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}