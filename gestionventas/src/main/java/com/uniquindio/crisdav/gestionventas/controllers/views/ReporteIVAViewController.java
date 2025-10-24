package com.uniquindio.crisdav.gestionventas.controllers.views;

import com.uniquindio.crisdav.gestionventas.controllers.ReporteController;
import com.uniquindio.crisdav.gestionventas.models.vo.ReporteIvaVO;
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
            mostrarAlerta("Error", "Debe generar un reporte primero", Alert.AlertType.WARNING);
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Exportar a PDF");
        alert.setHeaderText("Reporte de IVA");
        alert.setContentText(
            "Período: " + lblPeriodo.getText() + "\n" +
            "Total Ventas: " + lblTotalVentas.getText() + "\n" +
            "Total IVA a Pagar: " + lblTotalIva.getText() + "\n\n" +
            "Nota: La generación de PDF requiere librerías adicionales\n" +
            "(iText o Apache PDFBox). Implementación pendiente."
        );
        alert.showAndWait();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}