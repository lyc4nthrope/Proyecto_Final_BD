package com.uniquindio.crisdav.gestionventas.controllers;

import com.uniquindio.crisdav.gestionventas.dao.CuotaDAO;
import com.uniquindio.crisdav.gestionventas.models.Cuota;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Controlador para la gestión de cuotas en la interfaz JavaFX.
 */
public class CuotaController {

    @FXML
    private TableView<Cuota> tablaCuotas;
    @FXML
    private TableColumn<Cuota, Integer> colIdCuota;
    @FXML
    private TableColumn<Cuota, Integer> colIdVenta;
    @FXML
    private TableColumn<Cuota, Integer> colNumeroCuota;
    @FXML
    private TableColumn<Cuota, Double> colValorCuota;
    @FXML
    private TableColumn<Cuota, LocalDate> colFechaPago;
    @FXML
    private TableColumn<Cuota, String> colEstado;

    private final CuotaDAO cuotaDAO = new CuotaDAO();
    private final ObservableList<Cuota> cuotas = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configurar columnas de la tabla
        colIdCuota.setCellValueFactory(new PropertyValueFactory<>("idCuota"));
        colIdVenta.setCellValueFactory(new PropertyValueFactory<>("idVenta"));
        colNumeroCuota.setCellValueFactory(new PropertyValueFactory<>("numeroCuota"));
        colValorCuota.setCellValueFactory(new PropertyValueFactory<>("valorCuota"));
        colFechaPago.setCellValueFactory(new PropertyValueFactory<>("fechaPago"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        // Por defecto, la tabla está vacía
        tablaCuotas.setItems(cuotas);
    }

    public void cargarCuotasPorVenta(int idVenta) {
        try {
            List<Cuota> lista = cuotaDAO.listarPorVenta(idVenta);
            cuotas.setAll(lista);
        } catch (SQLException e) {
            e.printStackTrace();
            // Aquí puedes mostrar un mensaje de error en la UI
        }
    }

    // Métodos para agregar, editar y eliminar cuotas se agregarán aquí
}
