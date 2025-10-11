package com.uniquindio.crisdav.gestionventas.controllers;

import com.uniquindio.crisdav.gestionventas.controllers.CuotaController;
import com.uniquindio.crisdav.gestionventas.controllers.ReporteController;
import com.uniquindio.crisdav.gestionventas.controllers.UsuarioController;
import com.uniquindio.crisdav.gestionventas.controllers.VentaController;
import com.uniquindio.crisdav.gestionventas.models.entity.Cuota;
import com.uniquindio.crisdav.gestionventas.models.entity.Usuario;
import com.uniquindio.crisdav.gestionventas.utils.FormatoUtil;
import com.uniquindio.crisdav.gestionventas.utils.SessionManager;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class MainViewController {

    @FXML private Menu menuGestion;
    @FXML private Menu menuAdmin;
    @FXML private Label lblUsuario;
    @FXML private Label lblNivel;
    @FXML private Label lblFechaHora;
    @FXML private Label lblStatus;
    @FXML private StackPane contentArea;
    
    // Dashboard labels
    @FXML private Label lblVentasHoy;
    @FXML private Label lblTotalMes;
    @FXML private Label lblCuotasVencidas;
    
    private Usuario usuarioActual;
    private UsuarioController usuarioController;
    private ReporteController reporteController;
    private CuotaController cuotaController;
    private Timeline relojTimeline;

    @FXML
    public void initialize() {
        usuarioController = new UsuarioController();
        reporteController = new ReporteController();
        cuotaController = new CuotaController();
        
        // Obtener usuario de la sesión
        usuarioActual = SessionManager.getInstance().getUsuarioActual();
        
        if (usuarioActual != null) {
            lblUsuario.setText(usuarioActual.getUsername());
            lblNivel.setText(usuarioActual.getNivel().getValor());
            
            // Configurar permisos según nivel
            configurarPermisos();
        }
        
        // Iniciar reloj
        iniciarReloj();
        
        // Cargar estadísticas del dashboard
        cargarEstadisticas();
        
        // Actualizar cuotas vencidas
        actualizarCuotasVencidas();
    }

    private void configurarPermisos() {
        // Solo administradores pueden ver el menú de administración
        menuAdmin.setVisible(SessionManager.getInstance().esAdministrador());
        
        // Los esporádicos tienen acceso limitado
        if (SessionManager.getInstance().esEsporadico()) {
            menuGestion.setDisable(true);
        }
    }

    private void iniciarReloj() {
        relojTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            String fechaHora = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
            );
            lblFechaHora.setText(fechaHora);
        }));
        relojTimeline.setCycleCount(Animation.INDEFINITE);
        relojTimeline.play();
    }

    private void cargarEstadisticas() {
        Platform.runLater(() -> {
            try {
                // Ventas de hoy (simplificado - contar ventas de hoy)
                LocalDate hoy = LocalDate.now();
                VentaController ventaCtrl = new VentaController();
                List<?> ventasHoy = ventaCtrl.listarVentasPorFecha(hoy, hoy);
                lblVentasHoy.setText(String.valueOf(ventasHoy.size()));
                
                // Total del mes actual
                int mesActual = hoy.getMonthValue();
                int anioActual = hoy.getYear();
                BigDecimal totalMes = reporteController.obtenerTotalVentasMes(anioActual, mesActual);
                lblTotalMes.setText(FormatoUtil.formatearMoneda(totalMes));
                
                // Cuotas vencidas
                cuotaController.actualizarCuotasVencidas();
                List<Cuota> cuotasVencidas = cuotaController.listarCuotasVencidas();
                lblCuotasVencidas.setText(String.valueOf(cuotasVencidas.size()));
                
            } catch (SQLException e) {
                lblStatus.setText("Error al cargar estadísticas");
                e.printStackTrace();
            }
        });
    }

    private void actualizarCuotasVencidas() {
        try {
            cuotaController.actualizarCuotasVencidas();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void mostrarInicio(ActionEvent event) {
        cargarEstadisticas();
        lblStatus.setText("Inicio cargado");
    }

    @FXML
    private void mostrarNuevaVenta(ActionEvent event) {
        cargarVista("/com/uniquindio/crisdav/gestionventas/views/VentaView.fxml", "Nueva Venta");
    }

    @FXML
    private void mostrarHistorialVentas(ActionEvent event) {
        cargarVista("/com/uniquindio/crisdav/gestionventas/views/HistorialVentasView.fxml", "Historial de Ventas");
    }

    @FXML
    private void mostrarGestionCuotas(ActionEvent event) {
        cargarVista("/com/uniquindio/crisdav/gestionventas/views/GestionCuotasView.fxml", "Gestión de Cuotas");
    }

    @FXML
    private void mostrarProductos(ActionEvent event) {
        cargarVista("/com/uniquindio/crisdav/gestionventas/views/ProductosView.fxml", "Gestión de Productos");
    }

    @FXML
    private void mostrarClientes(ActionEvent event) {
        cargarVista("/com/uniquindio/crisdav/gestionventas/views/ClientesView.fxml", "Gestión de Clientes");
    }

    @FXML
    private void mostrarVendedores(ActionEvent event) {
        cargarVista("/com/uniquindio/crisdav/gestionventas/views/VendedoresView.fxml", "Gestión de Vendedores");
    }

    @FXML
    private void mostrarCategorias(ActionEvent event) {
        cargarVista("/com/uniquindio/crisdav/gestionventas/views/CategoriasView.fxml", "Gestión de Categorías");
    }

    @FXML
    private void mostrarReporteIVA(ActionEvent event) {
        cargarVista("/com/uniquindio/crisdav/gestionventas/views/ReporteIVAView.fxml", "Reporte de IVA");
    }

    @FXML
    private void mostrarInventario(ActionEvent event) {
        cargarVista("/com/uniquindio/crisdav/gestionventas/views/InventarioView.fxml", "Inventario");
    }

    @FXML
    private void mostrarClientesMorosos(ActionEvent event) {
        cargarVista("/com/uniquindio/crisdav/gestionventas/views/ClientesMorososView.fxml", "Clientes Morosos");
    }

    @FXML
    private void mostrarEstadisticas(ActionEvent event) {
        cargarVista("/com/uniquindio/crisdav/gestionventas/views/EstadisticasView.fxml", "Estadísticas de Ventas");
    }

    @FXML
    private void mostrarUsuarios(ActionEvent event) {
        if (!SessionManager.getInstance().esAdministrador()) {
            mostrarAlerta("Acceso Denegado", "No tiene permisos para acceder a esta sección", Alert.AlertType.WARNING);
            return;
        }
        cargarVista("/com/uniquindio/crisdav/gestionventas/views/UsuariosView.fxml", "Gestión de Usuarios");
    }

    @FXML
    private void mostrarAuditoria(ActionEvent event) {
        if (!SessionManager.getInstance().esAdministrador()) {
            mostrarAlerta("Acceso Denegado", "No tiene permisos para acceder a esta sección", Alert.AlertType.WARNING);
            return;
        }
        cargarVista("/com/uniquindio/crisdav/gestionventas/views/AuditoriaView.fxml", "Auditoría del Sistema");
    }

    @FXML
    private void mostrarAcercaDe(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Acerca de");
        alert.setHeaderText("Sistema de Gestión de Ventas");
        alert.setContentText(
            "Versión: 1.0\n" +
            "Desarrollado para: Universidad del Quindío\n" +
            "Materia: Bases de Datos I\n" +
            "Año: 2025\n\n" +
            "Sistema de gestión de ventas de electrodomésticos\n" +
            "con soporte para ventas de contado y crédito."
        );
        alert.showAndWait();
    }

    @FXML
    private void cerrarSesion(ActionEvent event) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Cerrar Sesión");
        confirmacion.setHeaderText("¿Está seguro que desea cerrar sesión?");
        confirmacion.setContentText("Deberá iniciar sesión nuevamente para acceder al sistema.");
        
        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                // Registrar logout en auditoría
                usuarioController.logout();
                
                // Limpiar sesión
                SessionManager.getInstance().logout();
                
                // Detener reloj
                if (relojTimeline != null) {
                    relojTimeline.stop();
                }
                
                // Volver a login
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/uniquindio/crisdav/gestionventas/views/LoginView.fxml"));
                Scene scene = new Scene(loader.load());
                Stage stage = (Stage) contentArea.getScene().getWindow();
                stage.setScene(scene);
                stage.setMaximized(false);
                stage.centerOnScreen();
                
            } catch (IOException | SQLException e) {
                mostrarAlerta("Error", "Error al cerrar sesión: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void salir(ActionEvent event) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Salir");
        confirmacion.setHeaderText("¿Está seguro que desea salir de la aplicación?");
        
        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                // Registrar logout si hay usuario
                if (usuarioActual != null) {
                    usuarioController.logout();
                }
                
                // Cerrar aplicación
                Platform.exit();
                System.exit(0);
            } catch (SQLException e) {
                e.printStackTrace();
                Platform.exit();
            }
        }
    }

    private void cargarVista(String rutaFXML, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Parent vista = loader.load();
            
            // Limpiar contenido actual
            contentArea.getChildren().clear();
            
            // Agregar nueva vista
            contentArea.getChildren().add(vista);
            
            lblStatus.setText(titulo + " cargado");
            
        } catch (IOException e) {
            // Si la vista no existe, mostrar mensaje
            lblStatus.setText("Vista no disponible: " + titulo);
            mostrarAlerta("Vista no disponible", 
                "La vista '" + titulo + "' aún no ha sido implementada.\n" +
                "Ruta: " + rutaFXML, 
                Alert.AlertType.INFORMATION);
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