package com.uniquindio.crisdav.gestionventas.controllers.views;

import com.uniquindio.crisdav.gestionventas.controllers.ReporteController;
import com.uniquindio.crisdav.gestionventas.controllers.VentaController;
import com.uniquindio.crisdav.gestionventas.models.entity.Venta;
import com.uniquindio.crisdav.gestionventas.models.vo.FacturaVO;
import com.uniquindio.crisdav.gestionventas.utils.FormatoUtil;
import com.uniquindio.crisdav.gestionventas.utils.JasperReportUtil;

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
import java.awt.Desktop;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class HistorialVentasViewController {

    @FXML private DatePicker dpFechaInicio;
    @FXML private DatePicker dpFechaFin;
    @FXML private ComboBox<String> comboTipoVenta;
    @FXML private TextField txtBuscar;
    
    @FXML private TableView<Venta> tablaVentas;
    @FXML private TableColumn<Venta, Integer> colId;
    @FXML private TableColumn<Venta, String> colFecha;
    @FXML private TableColumn<Venta, String> colTipo;
    @FXML private TableColumn<Venta, String> colSubtotal;
    @FXML private TableColumn<Venta, String> colIva;
    @FXML private TableColumn<Venta, String> colTotal;
    @FXML private TableColumn<Venta, Void> colAcciones;
    
    @FXML private Label lblTotalVentas;
    @FXML private Label lblVentasContado;
    @FXML private Label lblVentasCredito;
    @FXML private Label lblMontoTotal;
    @FXML private Label lblIvaTotal;

    private VentaController ventaController;
    private ReporteController reporteController;
    private ObservableList<Venta> listaVentas;
    private ObservableList<Venta> listaVentasFiltrada;

    @FXML
    public void initialize() {
        ventaController = new VentaController();
        reporteController = new ReporteController();
        listaVentas = FXCollections.observableArrayList();
        listaVentasFiltrada = FXCollections.observableArrayList();

        configurarTabla();
        configurarFiltros();
        cargarVentas();
        configurarCombobox();
        
    }

    private void configurarCombobox(){
        // ComboBox Tipo de Venta
        comboTipoVenta.setItems(FXCollections.observableArrayList(
            "Todas",
            "Contado",
            "Credito"
        ));
       comboTipoVenta.setValue("Todas");
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idVenta"));
        colFecha.setCellValueFactory(cellData -> 
            new SimpleStringProperty(FormatoUtil.formatearFecha(cellData.getValue().getFecha())));
        colTipo.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getTipoVenta().getValor()));
        colSubtotal.setCellValueFactory(cellData -> 
            new SimpleStringProperty(FormatoUtil.formatearMoneda(cellData.getValue().getSubtotal())));
        colIva.setCellValueFactory(cellData -> 
            new SimpleStringProperty(FormatoUtil.formatearMoneda(cellData.getValue().getTotalIva())));
        colTotal.setCellValueFactory(cellData -> 
            new SimpleStringProperty(FormatoUtil.formatearMoneda(cellData.getValue().getTotal())));

        // Columna de acciones
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnVer = new Button("👁️ Ver Factura");
            private final HBox hbox = new HBox(5, btnVer);

            {
                btnVer.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-size: 11;");
                
                btnVer.setOnAction(event -> {
                    Venta venta = getTableView().getItems().get(getIndex());
                    verFactura(venta);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });

        // Colorear filas según tipo
        tablaVentas.setRowFactory(tv -> new TableRow<Venta>() {
            @Override
            protected void updateItem(Venta item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else if (item.getTipoVenta().getValor().equals("Credito")) {
                    setStyle("-fx-background-color: #fff3cd;");
                } else {
                    setStyle("-fx-background-color: #d4edda;");
                }
            }
        });

        tablaVentas.setItems(listaVentasFiltrada);
    }

    private void configurarFiltros() {
        // Fechas por defecto: último mes
        dpFechaFin.setValue(LocalDate.now());
        dpFechaInicio.setValue(LocalDate.now().minusMonths(1));
        
        comboTipoVenta.setValue("Todas");
        
        // Listener para búsqueda
        txtBuscar.textProperty().addListener((obs, old, newVal) -> aplicarFiltros(null));
    }

    @FXML
    private void cargarVentas() {
        try {
            LocalDate inicio = dpFechaInicio.getValue();
            LocalDate fin = dpFechaFin.getValue();
            
            if (inicio == null || fin == null) {
                mostrarAlerta("Error", "Debe seleccionar las fechas de inicio y fin", Alert.AlertType.ERROR);
                return;
            }
            
            if (inicio.isAfter(fin)) {
                mostrarAlerta("Error", "La fecha de inicio debe ser anterior a la fecha fin", Alert.AlertType.ERROR);
                return;
            }
            
            List<Venta> ventas = ventaController.listarVentasPorFecha(inicio, fin);
            listaVentas.setAll(ventas);
            listaVentasFiltrada.setAll(ventas);
            
            aplicarFiltros(null);
            actualizarEstadisticas();
            
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar ventas:\n" + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void aplicarFiltros(ActionEvent event) {
        String tipoVenta = comboTipoVenta.getValue();
        String busqueda = txtBuscar.getText().toLowerCase().trim();
        
        List<Venta> filtradas = listaVentas.stream()
            .filter(v -> {
                // Filtro por tipo
                boolean coincideTipo = tipoVenta.equals("Todas") || 
                                      v.getTipoVenta().getValor().equals(tipoVenta);
                
                // Filtro por búsqueda (ID)
                boolean coincideBusqueda = busqueda.isEmpty() || 
                                          String.valueOf(v.getIdVenta()).contains(busqueda);
                
                return coincideTipo && coincideBusqueda;
            })
            .collect(Collectors.toList());
        
        listaVentasFiltrada.setAll(filtradas);
        actualizarEstadisticas();
    }

    private void actualizarEstadisticas() {
        lblTotalVentas.setText(String.valueOf(listaVentasFiltrada.size()));
        
        long contado = listaVentasFiltrada.stream()
            .filter(v -> v.getTipoVenta().getValor().equals("Contado"))
            .count();
        lblVentasContado.setText(String.valueOf(contado));
        
        long credito = listaVentasFiltrada.stream()
            .filter(v -> v.getTipoVenta().getValor().equals("Credito"))
            .count();
        lblVentasCredito.setText(String.valueOf(credito));
        
        BigDecimal montoTotal = listaVentasFiltrada.stream()
            .map(Venta::getTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        lblMontoTotal.setText(FormatoUtil.formatearMoneda(montoTotal));
        
        BigDecimal ivaTotal = listaVentasFiltrada.stream()
            .map(Venta::getTotalIva)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        lblIvaTotal.setText(FormatoUtil.formatearMoneda(ivaTotal));
    }

    private void verFactura(Venta venta) {
        try {
            FacturaVO factura = reporteController.generarFactura(venta.getIdVenta());
            
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Factura de Venta #" + venta.getIdVenta());
            dialog.setHeaderText("Detalle de la Venta");
            
            VBox contenido = new VBox(15);
            contenido.setPadding(new Insets(20));
            contenido.setStyle("-fx-background-color: white;");
            
            // Encabezado
            VBox encabezado = new VBox(5);
            encabezado.setStyle("-fx-border-color: #007bff; -fx-border-width: 0 0 2 0; -fx-padding: 0 0 10 0;");
            Label lblEmpresa = new Label("ELECTRODOMÉSTICOS S.A.");
            lblEmpresa.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
            Label lblNit = new Label("NIT: 900.123.456-7");
            Label lblDireccion = new Label("Armenia, Quindío");
            encabezado.getChildren().addAll(lblEmpresa, lblNit, lblDireccion);
            
            // Info factura
            GridPane gridFactura = new GridPane();
            gridFactura.setHgap(15);
            gridFactura.setVgap(8);
            
            gridFactura.add(new Label("Factura #:"), 0, 0);
            gridFactura.add(new Label(FormatoUtil.formatearCodigoFactura(factura.getIdVenta())), 1, 0);
            gridFactura.add(new Label("Fecha:"), 0, 1);
            gridFactura.add(new Label(FormatoUtil.formatearFecha(factura.getFecha())), 1, 1);
            gridFactura.add(new Label("Tipo:"), 0, 2);
            Label lblTipo = new Label(factura.getTipoVenta());
            if (factura.getTipoVenta().equals("Credito")) {
                lblTipo.setStyle("-fx-text-fill: #ffc107; -fx-font-weight: bold;");
            } else {
                lblTipo.setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold;");
            }
            gridFactura.add(lblTipo, 1, 2);
            
            // Info cliente
            VBox infoCliente = new VBox(5);
            infoCliente.setStyle("-fx-border-color: #dee2e6; -fx-border-width: 1; -fx-padding: 10; -fx-background-color: #f8f9fa;");
            Label lblClienteTitulo = new Label("CLIENTE");
            lblClienteTitulo.setStyle("-fx-font-weight: bold;");
            infoCliente.getChildren().addAll(
                lblClienteTitulo,
                new Label("Nombre: " + factura.getNombreCliente()),
                new Label("Cédula: " + factura.getCedulaCliente())
            );
            
            // Detalles (items)
            TableView<com.uniquindio.crisdav.gestionventas.models.vo.ItemFacturaVO> tablaItems = new TableView<>();
            tablaItems.setPrefHeight(200);
            
            TableColumn<com.uniquindio.crisdav.gestionventas.models.vo.ItemFacturaVO, String> colCod = new TableColumn<>("Código");
            colCod.setCellValueFactory(new PropertyValueFactory<>("codigoProducto"));
            colCod.setPrefWidth(100);
            
            TableColumn<com.uniquindio.crisdav.gestionventas.models.vo.ItemFacturaVO, String> colProd = new TableColumn<>("Producto");
            colProd.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
            colProd.setPrefWidth(250);
            
            TableColumn<com.uniquindio.crisdav.gestionventas.models.vo.ItemFacturaVO, Integer> colCant = new TableColumn<>("Cant.");
            colCant.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
            colCant.setPrefWidth(60);
            
            TableColumn<com.uniquindio.crisdav.gestionventas.models.vo.ItemFacturaVO, String> colPrecio = new TableColumn<>("Precio");
            colPrecio.setCellValueFactory(cellData -> 
                new SimpleStringProperty(FormatoUtil.formatearMoneda(cellData.getValue().getPrecioUnitario())));
            colPrecio.setPrefWidth(100);
            
            TableColumn<com.uniquindio.crisdav.gestionventas.models.vo.ItemFacturaVO, String> colSubt = new TableColumn<>("Subtotal");
            colSubt.setCellValueFactory(cellData -> 
                new SimpleStringProperty(FormatoUtil.formatearMoneda(cellData.getValue().getSubtotal())));
            colSubt.setPrefWidth(100);
            
            tablaItems.getColumns().addAll(colCod, colProd, colCant, colPrecio, colSubt);
            tablaItems.setItems(FXCollections.observableArrayList(factura.getItems()));
            
            // Totales
            VBox totales = new VBox(5);
            totales.setStyle("-fx-border-color: #28a745; -fx-border-width: 2; -fx-padding: 15; -fx-background-color: #f8f9fa;");
            
            HBox hSubtotal = new HBox(10);
            hSubtotal.getChildren().addAll(
                createLabel("Subtotal:", true),
                createLabel(FormatoUtil.formatearMoneda(factura.getSubtotal()), false)
            );
            
            HBox hIva = new HBox(10);
            hIva.getChildren().addAll(
                createLabel("IVA:", true),
                createLabel(FormatoUtil.formatearMoneda(factura.getTotalIva()), false)
            );
            
            HBox hTotal = new HBox(10);
            hTotal.getChildren().addAll(
                createLabel("TOTAL:", true, 18),
                createLabel(FormatoUtil.formatearMoneda(factura.getTotal()), false, 18)
            );
            hTotal.setStyle("-fx-padding: 5 0 0 0;");
            
            totales.getChildren().addAll(hSubtotal, hIva, new Separator(), hTotal);
            
            contenido.getChildren().addAll(
                encabezado,
                gridFactura,
                infoCliente,
                new Label("DETALLE DE PRODUCTOS"),
                tablaItems,
                totales
            );
            
            ScrollPane scroll = new ScrollPane(contenido);
            scroll.setFitToWidth(true);
            scroll.setPrefHeight(600);
            scroll.setPrefWidth(700);
            
            dialog.getDialogPane().setContent(scroll);

            ButtonType generatePdfButtonType = new ButtonType("Generar PDF", ButtonBar.ButtonData.OK_DONE);
            ButtonType closeButtonType = new ButtonType("Cerrar", ButtonBar.ButtonData.CANCEL_CLOSE);

            dialog.getDialogPane().getButtonTypes().addAll(generatePdfButtonType, closeButtonType);

            // Manejar la acción del botón
            Button generatePdfButton = (Button) dialog.getDialogPane().lookupButton(generatePdfButtonType);
            generatePdfButton.setOnAction(e -> {
                generarPDFFactura(factura);
            });
            dialog.showAndWait();
            
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al generar factura:\n" + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void generarPDFFactura(FacturaVO factura) {
        try {
            // Preparar parámetros
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("ID_VENTA", factura.getIdVenta());
            parametros.put("FECHA", FormatoUtil.formatearFecha(factura.getFecha()));
            parametros.put("CLIENTE", factura.getNombreCliente());
            parametros.put("CEDULA", factura.getCedulaCliente());
            parametros.put("VENDEDOR", factura.getNombreVendedor());
            parametros.put("TIPO_VENTA", factura.getTipoVenta());
            parametros.put("SUBTOTAL", factura.getSubtotal());
            parametros.put("TOTAL_IVA", factura.getTotalIva());
            parametros.put("TOTAL", factura.getTotal());
            
            // Ruta del reporte
            String jrxmlPath = "/com/uniquindio/crisdav/gestionventas/reportes/Factura.jrxml";
            
            // Ruta de salida
            JasperReportUtil.crearDirectorioReportes();
            String nombreArchivo = JasperReportUtil.generarNombreArchivo("Factura_" + factura.getIdVenta());
            String rutaSalida = JasperReportUtil.getRutaDocumentos() + File.separator + nombreArchivo;
            
            // Generar PDF con los items de la factura
            boolean exito = JasperReportUtil.generarPDFDesdeColeccion(
                jrxmlPath,
                parametros,
                factura.getItems(),
                rutaSalida
            );
            
            if (exito) {
                // Preguntar si desea abrir el PDF
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("PDF Generado");
                alert.setHeaderText("Factura generada exitosamente");
                alert.setContentText("Archivo: " + rutaSalida + "\n\n¿Desea abrir el PDF?");
                
                ButtonType btnAbrir = new ButtonType("Abrir");
                ButtonType btnCerrar = new ButtonType("Cerrar", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert.getButtonTypes().setAll(btnAbrir, btnCerrar);
                
                Optional<ButtonType> resultado = alert.showAndWait();
                if (resultado.isPresent() && resultado.get() == btnAbrir) {
                    abrirArchivo(rutaSalida);
                }
            } else {
                mostrarAlerta("Error", "No se pudo generar el PDF", Alert.AlertType.ERROR);
            }
            
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al generar PDF:\n" + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void abrirArchivo(String ruta) {
        try {
            File archivo = new File(ruta);
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(archivo);
            }
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo abrir el archivo:\n" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private Label createLabel(String texto, boolean bold) {
        return createLabel(texto, bold, 14);
    }

    private Label createLabel(String texto, boolean bold, int fontSize) {
        Label label = new Label(texto);
        String style = "-fx-font-size: " + fontSize + ";";
        if (bold) style += " -fx-font-weight: bold;";
        label.setStyle(style);
        return label;
    }

    @FXML
    private void exportarReporte(ActionEvent event) {
        if (listaVentasFiltrada.isEmpty()) {
            mostrarAlerta("Error", "No hay ventas para exportar", Alert.AlertType.WARNING);
            return;
        }
        try {
            // Preparar parámetros
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("FECHA_INICIO", FormatoUtil.formatearFecha(dpFechaInicio.getValue()));
            parametros.put("FECHA_FIN", FormatoUtil.formatearFecha(dpFechaFin.getValue()));
            parametros.put("TOTAL_VENTAS", Integer.parseInt(lblTotalVentas.getText()));
            parametros.put("VENTAS_CONTADO", Integer.parseInt(lblVentasContado.getText()));
            parametros.put("VENTAS_CREDITO", Integer.parseInt(lblVentasCredito.getText()));
            parametros.put("MONTO_TOTAL", FormatoUtil.parsearMoneda(lblMontoTotal.getText()));
            parametros.put("IVA_TOTAL", FormatoUtil.parsearMoneda(lblIvaTotal.getText()));
            
            // Ruta del reporte
            String jrxmlPath = "/com/uniquindio/crisdav/gestionventas/reportes/ReporteVentas.jrxml";

            // Ruta de salida
            JasperReportUtil.crearDirectorioReportes();
            String nombreArchivo = JasperReportUtil.generarNombreArchivo("Ventas" + FormatoUtil.formatearFechaSinEspacios(LocalDate.now()) + ".pdf");
            String rutaSalida = JasperReportUtil.getRutaDocumentos() + File.separator + nombreArchivo;
                
            // Generar PDF con los items de la factura
            boolean exito = JasperReportUtil.generarPDFDesdeColeccion(
                jrxmlPath,
                parametros,
                listaVentasFiltrada,
                rutaSalida
            );

            if (exito) {
                // Preguntar si desea abrir el PDF
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("PDF Generado");
                alert.setHeaderText("Reporte exportado exitosamente");
                alert.setContentText("Archivo: " + rutaSalida + "\n\n¿Desea abrir el PDF?");
                    
                ButtonType btnAbrir = new ButtonType("Abrir");
                ButtonType btnCerrar = new ButtonType("Cerrar", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert.getButtonTypes().setAll(btnAbrir, btnCerrar);
                    
                Optional<ButtonType> resultado = alert.showAndWait();
                if (resultado.isPresent() && resultado.get() == btnAbrir) {
                    abrirArchivo(rutaSalida);
                }
            } else {
                    mostrarAlerta("Error", "No se pudo generar el PDF", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al generar PDF:\n" + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
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
