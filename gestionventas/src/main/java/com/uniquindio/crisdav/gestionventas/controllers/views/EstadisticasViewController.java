package com.uniquindio.crisdav.gestionventas.controllers.views;

import com.uniquindio.crisdav.gestionventas.controllers.ReporteController;
import com.uniquindio.crisdav.gestionventas.models.vo.EstadisticaVentasVO;
import com.uniquindio.crisdav.gestionventas.utils.FormatoUtil;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;

public class EstadisticasViewController {

    @FXML private DatePicker dpFechaInicio;
    @FXML private DatePicker dpFechaFin;
    
    // Estadísticas generales
    @FXML private Label lblTotalVentas;
    @FXML private Label lblVentasContado;
    @FXML private Label lblVentasCredito;
    @FXML private Label lblMontoTotal;
    @FXML private Label lblMontoContado;
    @FXML private Label lblMontoCredito;
    @FXML private Label lblPromedioVenta;
    
    // Gráficos
    @FXML private PieChart graficoPorTipo;
    @FXML private BarChart<String, Number> graficoMontos;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;

    private ReporteController reporteController;

    @FXML
    public void initialize() {
        reporteController = new ReporteController();
        
        // Fechas por defecto: último mes
        dpFechaFin.setValue(LocalDate.now());
        dpFechaInicio.setValue(LocalDate.now().minusMonths(1));
        
        generarEstadisticas(null);
    }

    @FXML
    private void generarEstadisticas(ActionEvent event) {
        try {
            LocalDate inicio = dpFechaInicio.getValue();
            LocalDate fin = dpFechaFin.getValue();
            
            if (inicio == null || fin == null) {
                mostrarAlerta("Error", "Debe seleccionar las fechas", Alert.AlertType.ERROR);
                return;
            }
            
            if (inicio.isAfter(fin)) {
                mostrarAlerta("Error", "La fecha de inicio debe ser anterior a la fecha fin", Alert.AlertType.ERROR);
                return;
            }
            
            EstadisticaVentasVO stats = reporteController.obtenerEstadisticasVentas(inicio, fin);
            
            // Actualizar labels
            lblTotalVentas.setText(String.valueOf(stats.getCantidadTotalVentas()));
            lblVentasContado.setText(String.valueOf(stats.getCantidadVentasContado()));
            lblVentasCredito.setText(String.valueOf(stats.getCantidadVentasCredito()));
            
            lblMontoTotal.setText(FormatoUtil.formatearMoneda(stats.getMontoTotal()));
            lblMontoContado.setText(FormatoUtil.formatearMoneda(stats.getMontoTotalContado()));
            lblMontoCredito.setText(FormatoUtil.formatearMoneda(stats.getMontoTotalCredito()));
            
            // Promedio
            BigDecimal promedio = BigDecimal.ZERO;
            if (stats.getCantidadTotalVentas() > 0) {
                promedio = stats.getMontoTotal().divide(
                    BigDecimal.valueOf(stats.getCantidadTotalVentas()), 
                    2, 
                    java.math.RoundingMode.HALF_UP
                );
            }
            lblPromedioVenta.setText(FormatoUtil.formatearMoneda(promedio));
            
            // Actualizar gráficos
            actualizarGraficoPorTipo(stats);
            actualizarGraficoMontos(stats);
            
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al generar estadísticas:\n" + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void actualizarGraficoPorTipo(EstadisticaVentasVO stats) {
        graficoPorTipo.getData().clear();
        
        if (stats.getCantidadTotalVentas() > 0) {
            PieChart.Data dataContado = new PieChart.Data(
                "Contado (" + stats.getCantidadVentasContado() + ")", 
                stats.getCantidadVentasContado()
            );
            
            PieChart.Data dataCredito = new PieChart.Data(
                "Crédito (" + stats.getCantidadVentasCredito() + ")", 
                stats.getCantidadVentasCredito()
            );
            
            graficoPorTipo.getData().addAll(dataContado, dataCredito);
        }
    }

    private void actualizarGraficoMontos(EstadisticaVentasVO stats) {
        graficoMontos.getData().clear();
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Montos de Venta");
        
        series.getData().add(new XYChart.Data<>("Contado", stats.getMontoTotalContado()));
        series.getData().add(new XYChart.Data<>("Crédito", stats.getMontoTotalCredito()));
        series.getData().add(new XYChart.Data<>("Total", stats.getMontoTotal()));
        
        graficoMontos.getData().add(series);
    }

    @FXML
    private void exportarReporte(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Exportar Estadísticas");
        alert.setHeaderText("Generar Reporte de Estadísticas");
        alert.setContentText(
            "Período: " + FormatoUtil.formatearFecha(dpFechaInicio.getValue()) + 
            " al " + FormatoUtil.formatearFecha(dpFechaFin.getValue()) + "\n\n" +
            "Total ventas: " + lblTotalVentas.getText() + "\n" +
            "Monto total: " + lblMontoTotal.getText() + "\n\n" +
            "Nota: La exportación requiere librerías adicionales.\n" +
            "Implementación pendiente."
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
