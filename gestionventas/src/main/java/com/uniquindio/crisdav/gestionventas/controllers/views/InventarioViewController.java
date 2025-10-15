package com.uniquindio.crisdav.gestionventas.controllers.views;

import com.uniquindio.crisdav.gestionventas.controllers.ReporteController;
import com.uniquindio.crisdav.gestionventas.models.vo.InventarioVO;
import com.uniquindio.crisdav.gestionventas.utils.FormatoUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class InventarioViewController {

    // Filtros
    @FXML private ComboBox<String> comboCategoriaFiltro;
    @FXML private CheckBox chkStockBajo;
    @FXML private Label lblFechaReporte;

    // Tabla
    @FXML private TableView<InventarioVO> tablaInventario;
    @FXML private TableColumn<InventarioVO, String> colCategoria;
    @FXML private TableColumn<InventarioVO, String> colCodigo;
    @FXML private TableColumn<InventarioVO, String> colNombre;
    @FXML private TableColumn<InventarioVO, Integer> colStock;
    @FXML private TableColumn<InventarioVO, String> colValorAdq;
    @FXML private TableColumn<InventarioVO, String> colCostoTotal;

    // Resumen y gráficos
    @FXML private GridPane gridResumenCategorias;
    @FXML private PieChart graficoInventario;
    @FXML private VBox vboxAlertasStock;
    @FXML private VBox vboxTop5;

    // Estadísticas
    @FXML private Label lblTotalProductos;
    @FXML private Label lblTotalUnidades;
    @FXML private Label lblValorTotal;
    @FXML private Label lblStockBajo;

    private ReporteController reporteController;
    private ObservableList<InventarioVO> listaInventario;
    private ObservableList<InventarioVO> listaInventarioFiltrada;

    @FXML
    public void initialize() {
        reporteController = new ReporteController();
        listaInventario = FXCollections.observableArrayList();
        listaInventarioFiltrada = FXCollections.observableArrayList();

        configurarTabla();
        configurarFechaReporte();
        cargarInventario();
        
        comboCategoriaFiltro.setValue("Todas las categorías");
    }

    private void configurarTabla() {
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoProducto"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        
        colValorAdq.setCellValueFactory(cellData -> 
            new SimpleStringProperty(FormatoUtil.formatearMoneda(cellData.getValue().getValorAdquisicion())));
        
        colCostoTotal.setCellValueFactory(cellData -> 
            new SimpleStringProperty(FormatoUtil.formatearMoneda(cellData.getValue().getCostoTotalCategoria())));

        // Resaltar productos con stock bajo
        tablaInventario.setRowFactory(tv -> new TableRow<InventarioVO>() {
            @Override
            protected void updateItem(InventarioVO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else if (item.getStock() < 10) {
                    setStyle("-fx-background-color: #fff3cd;");
                } else if (item.getStock() == 0) {
                    setStyle("-fx-background-color: #f8d7da;");
                }
            }
        });

        tablaInventario.setItems(listaInventarioFiltrada);
    }

    private void configurarFechaReporte() {
        lblFechaReporte.setText("Fecha: " + FormatoUtil.formatearFecha(LocalDate.now()));
    }

    @FXML
    private void cargarInventario() {
        try {
            List<InventarioVO> inventario = reporteController.obtenerInventarioPorCategoria();
            listaInventario.setAll(inventario);
            listaInventarioFiltrada.setAll(inventario);
            
            actualizarEstadisticas();
            generarResumenPorCategoria();
            generarGrafico();
            generarAlertasStock();
            generarTop5();
            
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar inventario:\n" + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void aplicarFiltro(ActionEvent event) {
        String categoriaSeleccionada = comboCategoriaFiltro.getValue();
        boolean soloStockBajo = chkStockBajo.isSelected();
        
        List<InventarioVO> filtrados = listaInventario.stream()
            .filter(item -> {
                // Filtro por categoría
                boolean coincideCategoria = categoriaSeleccionada.equals("Todas las categorías") || 
                                           item.getCategoria().equals(categoriaSeleccionada);
                
                // Filtro por stock bajo
                boolean coincideStock = !soloStockBajo || item.getStock() < 10;
                
                return coincideCategoria && coincideStock;
            })
            .collect(Collectors.toList());
        
        listaInventarioFiltrada.setAll(filtrados);
        actualizarEstadisticas();
    }

    private void actualizarEstadisticas() {
        // Total productos (distintos)
        long totalProductos = listaInventarioFiltrada.stream()
            .map(InventarioVO::getCodigoProducto)
            .distinct()
            .count();
        lblTotalProductos.setText(String.valueOf(totalProductos));
        
        // Total unidades
        int totalUnidades = listaInventarioFiltrada.stream()
            .mapToInt(InventarioVO::getStock)
            .sum();
        lblTotalUnidades.setText(FormatoUtil.formatearNumero(totalUnidades));
        
        // Valor total
        BigDecimal valorTotal = listaInventarioFiltrada.stream()
            .map(InventarioVO::getCostoTotalCategoria)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        lblValorTotal.setText(FormatoUtil.formatearMoneda(valorTotal));
        
        // Stock bajo
        long stockBajo = listaInventarioFiltrada.stream()
            .filter(item -> item.getStock() < 10)
            .count();
        lblStockBajo.setText(String.valueOf(stockBajo));
    }

    private void generarResumenPorCategoria() {
        gridResumenCategorias.getChildren().clear();
        
        // Agrupar por categoría
        Map<String, BigDecimal> resumenCategorias = listaInventario.stream()
            .collect(Collectors.groupingBy(
                InventarioVO::getCategoria,
                Collectors.reducing(BigDecimal.ZERO, 
                    InventarioVO::getCostoTotalCategoria, 
                    BigDecimal::add)
            ));
        
        int row = 0;
        for (Map.Entry<String, BigDecimal> entry : resumenCategorias.entrySet()) {
            Label lblCategoria = new Label(entry.getKey() + ":");
            lblCategoria.setStyle("-fx-font-weight: bold;");
            
            Label lblValor = new Label(FormatoUtil.formatearMoneda(entry.getValue()));
            lblValor.setStyle("-fx-text-fill: #007bff; -fx-font-weight: bold;");
            
            gridResumenCategorias.add(lblCategoria, 0, row);
            gridResumenCategorias.add(lblValor, 1, row);
            row++;
        }
    }

    private void generarGrafico() {
        // Agrupar por categoría para el gráfico
        Map<String, Integer> stockPorCategoria = listaInventario.stream()
            .collect(Collectors.groupingBy(
                InventarioVO::getCategoria,
                Collectors.summingInt(InventarioVO::getStock)
            ));
        
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        
        for (Map.Entry<String, Integer> entry : stockPorCategoria.entrySet()) {
            pieChartData.add(new PieChart.Data(
                entry.getKey() + " (" + entry.getValue() + ")", 
                entry.getValue()
            ));
        }
        
        graficoInventario.setData(pieChartData);
        graficoInventario.setTitle("Distribución de Stock por Categoría");
    }

    private void generarAlertasStock() {
        vboxAlertasStock.getChildren().clear();
        
        List<InventarioVO> productosStockBajo = listaInventario.stream()
            .filter(item -> item.getStock() < 10)
            .sorted(Comparator.comparingInt(InventarioVO::getStock))
            .limit(10)
            .collect(Collectors.toList());
        
        if (productosStockBajo.isEmpty()) {
            Label lblSinAlertas = new Label("✅ No hay productos con stock bajo");
            lblSinAlertas.setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold;");
            vboxAlertasStock.getChildren().add(lblSinAlertas);
        } else {
            for (InventarioVO item : productosStockBajo) {
                HBox alerta = new HBox(10);
                alerta.setStyle("-fx-padding: 5; -fx-background-color: white; -fx-background-radius: 3;");
                
                Label lblIcono = new Label(item.getStock() == 0 ? "🔴" : "⚠️");
                Label lblProducto = new Label(item.getNombreProducto());
                lblProducto.setStyle("-fx-font-size: 11;");
                Label lblStock = new Label("Stock: " + item.getStock());
                lblStock.setStyle("-fx-font-weight: bold; -fx-text-fill: #dc3545; -fx-font-size: 11;");
                
                alerta.getChildren().addAll(lblIcono, lblProducto, lblStock);
                vboxAlertasStock.getChildren().add(alerta);
            }
        }
    }

    private void generarTop5() {
        vboxTop5.getChildren().clear();
        
        List<InventarioVO> top5 = listaInventario.stream()
            .sorted(Comparator.comparing(InventarioVO::getCostoTotalCategoria).reversed())
            .limit(5)
            .collect(Collectors.toList());
        
        int posicion = 1;
        for (InventarioVO item : top5) {
            HBox itemBox = new HBox(10);
            itemBox.setStyle("-fx-padding: 5; -fx-background-color: white; -fx-background-radius: 3;");
            
            Label lblPosicion = new Label("#" + posicion);
            lblPosicion.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-text-fill: #007bff;");
            lblPosicion.setMinWidth(30);
            
            VBox infoBox = new VBox(2);
            Label lblNombre = new Label(item.getNombreProducto());
            lblNombre.setStyle("-fx-font-weight: bold; -fx-font-size: 11;");
            Label lblDetalle = new Label(item.getStock() + " und × " + 
                FormatoUtil.formatearMoneda(item.getValorAdquisicion()));
            lblDetalle.setStyle("-fx-font-size: 10; -fx-text-fill: #666;");
            infoBox.getChildren().addAll(lblNombre, lblDetalle);
            
            Label lblTotal = new Label(FormatoUtil.formatearMoneda(item.getCostoTotalCategoria()));
            lblTotal.setStyle("-fx-font-weight: bold; -fx-text-fill: #28a745; -fx-font-size: 12;");
            
            itemBox.getChildren().addAll(lblPosicion, infoBox, lblTotal);
            HBox.setHgrow(infoBox, javafx.scene.layout.Priority.ALWAYS);
            
            vboxTop5.getChildren().add(itemBox);
            posicion++;
        }
    }

    @FXML
    private void exportarExcel(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Exportar a Excel");
        alert.setHeaderText("Funcionalidad de exportación");
        alert.setContentText(
            "Datos a exportar:\n" +
            "- " + listaInventario.size() + " productos\n" +
            "- Valor total: " + lblValorTotal.getText() + "\n\n" +
            "Nota: La exportación a Excel requiere librerías adicionales\n" +
            "(Apache POI). Implementación pendiente."
        );
        alert.showAndWait();
    }

    @FXML
    private void imprimirReporte(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Imprimir Reporte");
        alert.setHeaderText("Funcionalidad de impresión");
        alert.setContentText(
            "Reporte de Inventario\n" +
            "Fecha: " + lblFechaReporte.getText() + "\n\n" +
            "Total Productos: " + lblTotalProductos.getText() + "\n" +
            "Total Unidades: " + lblTotalUnidades.getText() + "\n" +
            "Valor Total: " + lblValorTotal.getText() + "\n\n" +
            "Nota: La impresión requiere configuración adicional.\n" +
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