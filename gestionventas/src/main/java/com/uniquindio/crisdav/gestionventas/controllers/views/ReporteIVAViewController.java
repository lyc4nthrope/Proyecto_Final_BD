package com.uniquindio.crisdav.gestionventas.controllers.views;

import com.uniquindio.crisdav.gestionventas.controllers.ReporteController;
import com.uniquindio.crisdav.gestionventas.models.vo.ReporteIvaVO;
import com.uniquindio.crisdav.gestionventas.utils.FormatoUtil;
import com.uniquindio.crisdav.gestionventas.utils.JasperReportUtil;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.io.File;
import java.awt.Desktop;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ReporteIVAViewController {

    @FXML private Spinner<Integer> spinAnio;
    @FXML private ComboBox<String> comboTrimestre;
    @FXML private TableView<ReporteIvaVO> tablaReporteIva;
    @FXML private TableColumn<ReporteIvaVO, String> colCategoria;
    @FXML private TableColumn<ReporteIvaVO, String> colVentas;
    @FXML private TableColumn<ReporteIvaVO, String> colIva;
    @FXML private TableColumn<ReporteIvaVO, String> colTotalIva;
    
    @FXML private Label lblPeriodo;
    @FXML private Label lblTotalVentas;
    @FXML private Label lblTotalIva;
    @FXML private Label lblFechaGeneracion;

    private ReporteController reporteController;
    private ObservableList<ReporteIvaVO> listaReporte;

    @FXML
    public void initialize() {
        reporteController = new ReporteController();
        listaReporte = FXCollections.observableArrayList();

        configurarTabla();
        configurarControles();
        lblFechaGeneracion.setText("Generado: " + FormatoUtil.formatearFecha(LocalDate.now()));
    }

    private void configurarTabla() {
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colVentas.setCellValueFactory(cellData -> 
            new SimpleStringProperty(FormatoUtil.formatearMoneda(cellData.getValue().getTotalVentasCategoria())));
        colIva.setCellValueFactory(cellData -> 
            new SimpleStringProperty(FormatoUtil.formatearPorcentaje(cellData.getValue().getIvaCategoria())));
        colTotalIva.setCellValueFactory(cellData -> 
            new SimpleStringProperty(FormatoUtil.formatearMoneda(cellData.getValue().getTotalIvaCategoria())));

        // Colorear fila de totales
        tablaReporteIva.setRowFactory(tv -> new TableRow<ReporteIvaVO>() {
            @Override
            protected void updateItem(ReporteIvaVO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else if (item.getTotalVentasCategoria().compareTo(BigDecimal.ZERO) == 0) {
                    setStyle("-fx-background-color: #e9ecef;");
                } else {
                    setStyle("");
                }
            }
        });

        tablaReporteIva.setItems(listaReporte);
    }

    private void configurarControles() {
        // Spinner de año
        int anioActual = LocalDate.now().getYear();
        SpinnerValueFactory<Integer> valueFactory = 
            new SpinnerValueFactory.IntegerSpinnerValueFactory(2020, anioActual + 5, anioActual);
        spinAnio.setValueFactory(valueFactory);
        
        // ComboBox trimestre
        comboTrimestre.setItems(FXCollections.observableArrayList(
            "T1 - Enero a Marzo",
            "T2 - Abril a Junio",
            "T3 - Julio a Septiembre",
            "T4 - Octubre a Diciembre"
        ));
        comboTrimestre.setValue("T1 - Enero a Marzo");
    }

    @FXML
    private void generarReporte(ActionEvent event) {
        try {
            int anio = spinAnio.getValue();
            String trimestreStr = comboTrimestre.getValue();
            int trimestre = Integer.parseInt(trimestreStr.substring(1, 2));
            
            List<ReporteIvaVO> reportes = reporteController.generarReporteIvaTrimestral(anio, trimestre);
            listaReporte.setAll(reportes);
            
            // Actualizar totales
            BigDecimal totalVentas = reportes.stream()
                .map(ReporteIvaVO::getTotalVentasCategoria)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal totalIva = reporteController.calcularTotalIvaTrimestre(anio, trimestre);
            
            lblPeriodo.setText("Año " + anio + " - " + FormatoUtil.nombreTrimestre(trimestre));
            lblTotalVentas.setText(FormatoUtil.formatearMoneda(totalVentas));
            lblTotalIva.setText(FormatoUtil.formatearMoneda(totalIva));
            
            if (listaReporte.isEmpty()) {
                mostrarAlerta("Sin Datos", 
                    "No hay ventas registradas para el período seleccionado", 
                    Alert.AlertType.INFORMATION);
            }
            
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al generar reporte:\n" + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void exportarPDF(ActionEvent event) {
        if (listaReporte.isEmpty()) {
            mostrarAlerta("Error", "No hay un reporte para exportar", Alert.AlertType.WARNING);
            return;
        }
        try {
            String trimestreStr = comboTrimestre.getValue();
            int trimestre = Integer.parseInt(trimestreStr.substring(1, 2));
            // Preparar parámetros
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("ANIO", spinAnio.getValue());
            parametros.put("TRIMESTRE", trimestre);
            parametros.put("PERIODO_TEXTO", trimestreStr);
            parametros.put("TOTAL_VENTAS", FormatoUtil.parsearMoneda(lblTotalVentas.getText()));
            parametros.put("TOTAL_IVA", FormatoUtil.parsearMoneda(lblTotalIva.getText()));
            parametros.put("FECHA_GENERACIÓN", FormatoUtil.formatearFecha(LocalDate.now()));
            
            // Ruta del reporte
            String jrxmlPath = "/com/uniquindio/crisdav/gestionventas/reportes/ReporteIVATrimestral.jrxml";

            // Ruta de salida
            JasperReportUtil.crearDirectorioReportes();
            String nombreArchivo = JasperReportUtil.generarNombreArchivo("Ventas" + FormatoUtil.formatearFechaSinEspacios(LocalDate.now()) + ".pdf");
            String rutaSalida = JasperReportUtil.getRutaDocumentos() + File.separator + nombreArchivo;
                
            // Generar PDF con los items de la factura
            boolean exito = JasperReportUtil.generarPDFDesdeColeccion(
                jrxmlPath,
                parametros,
                listaReporte,
                rutaSalida
            );

            if (exito) {
                // Preguntar si desea abrir el PDF
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("PDF Generado");
                alert.setHeaderText("Reporte generado exitosamente");
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

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}