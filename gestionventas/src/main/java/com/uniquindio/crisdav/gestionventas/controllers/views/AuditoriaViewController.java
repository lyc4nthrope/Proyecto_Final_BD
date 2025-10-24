package com.uniquindio.crisdav.gestionventas.controllers.views;

import com.uniquindio.crisdav.gestionventas.dao.AuditoriaDAO;
import com.uniquindio.crisdav.gestionventas.dao.UsuarioDAO;
import com.uniquindio.crisdav.gestionventas.models.entity.Auditoria;
import com.uniquindio.crisdav.gestionventas.models.entity.Usuario;
import com.uniquindio.crisdav.gestionventas.utils.FormatoUtil;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AuditoriaViewController {

    @FXML private DatePicker dpFechaInicio;
    @FXML private DatePicker dpFechaFin;
    @FXML private ComboBox<String> comboUsuario;
    @FXML private ComboBox<String> comboAccion;
    
    @FXML private TableView<Auditoria> tablaAuditoria;
    @FXML private TableColumn<Auditoria, Integer> colId;
    @FXML private TableColumn<Auditoria, String> colUsuario;
    @FXML private TableColumn<Auditoria, String> colAccion;
    @FXML private TableColumn<Auditoria, String> colFechaHora;
    
    @FXML private Label lblTotalRegistros;
    @FXML private Label lblLogins;
    @FXML private Label lblLogouts;
    @FXML private Label lblUsuarioMasActivo;

    private AuditoriaDAO auditoriaDAO;
    private UsuarioDAO usuarioDAO;
    private ObservableList<Auditoria> listaAuditoria;
    private ObservableList<Auditoria> listaAuditoriaFiltrada;
    private Map<Integer, String> mapaUsuarios;

    @FXML
    public void initialize() {
        auditoriaDAO = new AuditoriaDAO();
        usuarioDAO = new UsuarioDAO();
        listaAuditoria = FXCollections.observableArrayList();
        listaAuditoriaFiltrada = FXCollections.observableArrayList();
        mapaUsuarios = new HashMap<>();

        configurarTabla();
        cargarUsuarios();
        configurarFiltros();
        cargarAuditoria();
        configurarCombobox();
    }

    private void configurarCombobox(){
        // ComboBox Accion Filtro
        comboAccion.setItems(FXCollections.observableArrayList(
            "Todas las acciones",
            "Login",
            "Logout"
        ));
       comboAccion.setValue("Todas las acciones");
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idAuditoria"));
        
        colUsuario.setCellValueFactory(cellData -> {
            Integer idUsuario = cellData.getValue().getIdUsuario();
            String nombreUsuario = mapaUsuarios.getOrDefault(idUsuario, "Usuario #" + idUsuario);
            return new SimpleStringProperty(nombreUsuario);
        });
        
        colAccion.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getAccion().getValor()));
        
        colFechaHora.setCellValueFactory(cellData -> 
            new SimpleStringProperty(FormatoUtil.formatearFechaHora(cellData.getValue().getFechaHora())));

        // Colorear según acción
        colAccion.setCellFactory(column -> new TableCell<Auditoria, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("Login")) {
                        setStyle("-fx-background-color: #d4edda; -fx-text-fill: #155724; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-background-color: #fff3cd; -fx-text-fill: #856404; -fx-font-weight: bold;");
                    }
                }
            }
        });

        tablaAuditoria.setItems(listaAuditoriaFiltrada);
    }

    private void cargarUsuarios() {
        try {
            List<Usuario> usuarios = usuarioDAO.listarTodos();
            
            comboUsuario.getItems().add("Todos los usuarios");
            for (Usuario u : usuarios) {
                comboUsuario.getItems().add(u.getUsername());
                mapaUsuarios.put(u.getIdUsuario(), u.getUsername());
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void configurarFiltros() {
        // Fechas por defecto: última semana
        dpFechaFin.setValue(LocalDate.now());
        dpFechaInicio.setValue(LocalDate.now().minusWeeks(1));
        
        comboUsuario.setValue("Todos los usuarios");
        comboAccion.setValue("Todas las acciones");
    }

    @FXML
    private void cargarAuditoria() {
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
            
            LocalDateTime inicioDateTime = inicio.atStartOfDay();
            LocalDateTime finDateTime = fin.atTime(LocalTime.MAX);
            
            List<Auditoria> registros = auditoriaDAO.listarPorFecha(inicioDateTime, finDateTime);
            listaAuditoria.setAll(registros);
            listaAuditoriaFiltrada.setAll(registros);
            
            aplicarFiltros(null);
            actualizarEstadisticas();
            
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar auditoría:\n" + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void aplicarFiltros(ActionEvent event) {
        String usuario = comboUsuario.getValue();
        String accion = comboAccion.getValue();
        
        List<Auditoria> filtrados = listaAuditoria.stream()
            .filter(a -> {
                // Filtro por usuario
                boolean coincideUsuario = usuario.equals("Todos los usuarios") ||
                    mapaUsuarios.get(a.getIdUsuario()).equals(usuario);
                
                // Filtro por acción
                boolean coincideAccion = accion.equals("Todas las acciones") ||
                    a.getAccion().getValor().equals(accion);
                
                return coincideUsuario && coincideAccion;
            })
            .collect(Collectors.toList());
        
        listaAuditoriaFiltrada.setAll(filtrados);
        actualizarEstadisticas();
    }

    private void actualizarEstadisticas() {
        lblTotalRegistros.setText(String.valueOf(listaAuditoriaFiltrada.size()));
        
        long logins = listaAuditoriaFiltrada.stream()
            .filter(a -> a.getAccion().getValor().equals("Login"))
            .count();
        lblLogins.setText(String.valueOf(logins));
        
        long logouts = listaAuditoriaFiltrada.stream()
            .filter(a -> a.getAccion().getValor().equals("Logout"))
            .count();
        lblLogouts.setText(String.valueOf(logouts));
        
        // Usuario más activo
        Map<Integer, Long> actividadPorUsuario = listaAuditoriaFiltrada.stream()
            .collect(Collectors.groupingBy(Auditoria::getIdUsuario, Collectors.counting()));
        
        if (!actividadPorUsuario.isEmpty()) {
            Integer idMasActivo = actividadPorUsuario.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
            
            if (idMasActivo != null) {
                String nombreMasActivo = mapaUsuarios.getOrDefault(idMasActivo, "Desconocido");
                long actividad = actividadPorUsuario.get(idMasActivo);
                lblUsuarioMasActivo.setText(nombreMasActivo + " (" + actividad + " acciones)");
            }
        } else {
            lblUsuarioMasActivo.setText("-");
        }
    }

    @FXML
    private void exportarReporte(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Exportar Auditoría");
        alert.setHeaderText("Generar Reporte de Auditoría");
        alert.setContentText(
            "Período: " + FormatoUtil.formatearFecha(dpFechaInicio.getValue()) + 
            " al " + FormatoUtil.formatearFecha(dpFechaFin.getValue()) + "\n\n" +
            "Total registros: " + lblTotalRegistros.getText() + "\n" +
            "Logins: " + lblLogins.getText() + "\n" +
            "Logouts: " + lblLogouts.getText() + "\n\n" +
            "Nota: La exportación requiere librerías adicionales.\n" +
            "Implementación pendiente."
        );
        alert.showAndWait();
    }

    @FXML
    private void limpiarAuditoriaAntigua(ActionEvent event) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Limpiar Auditoría Antigua");
        confirmacion.setHeaderText("¿Eliminar registros antiguos?");
        confirmacion.setContentText(
            "Esta acción eliminará registros de auditoría\n" +
            "anteriores a 6 meses.\n\n" +
            "¿Desea continuar?"
        );
        
        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Alert resultado = new Alert(Alert.AlertType.INFORMATION);
                resultado.setTitle("Limpieza Simulada");
                resultado.setHeaderText("Funcionalidad Pendiente");
                resultado.setContentText(
                    "En producción, aquí se eliminarían\n" +
                    "los registros antiguos de la base de datos.\n\n" +
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