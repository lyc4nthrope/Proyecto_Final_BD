package com.uniquindio.crisdav.gestionventas.controllers;

import com.uniquindio.crisdav.gestionventas.controllers.UsuarioController;
import com.uniquindio.crisdav.gestionventas.models.entity.Usuario;
import com.uniquindio.crisdav.gestionventas.models.enums.NivelUsuario;
import com.uniquindio.crisdav.gestionventas.utils.SessionManager;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {

    @FXML private AnchorPane loginPane;
    @FXML private AnchorPane registroPane;
    @FXML private AnchorPane opcionPane;
    
    // Campos de Login
    @FXML private TextField nombreLoginLabel;
    @FXML private PasswordField contraseñaLoginLabel;
    @FXML private Button loginButton;
    
    // Campos de Registro
    @FXML private TextField nombreRegistroLabel;
    @FXML private PasswordField contrasenaRegistroLabel;
    @FXML private PasswordField contrasenaConfirmarLabel;
    @FXML private ComboBox<String> nivelUsuarioCombo;
    @FXML private Button registrarButton;
    
    // Botones de cambio
    @FXML private Button crearUsuarioButton;
    @FXML private Button tengoCuentaButton;
    
    private UsuarioController usuarioController;
    private boolean mostrandoRegistro = false;

    @FXML
    public void initialize() {
        usuarioController = new UsuarioController();
        
        // Configurar posición inicial
        registroPane.setTranslateX(-300);
        
        // Llenar ComboBox de niveles
        nivelUsuarioCombo.getItems().addAll(
            "Administrador",
            "Parametrico",
            "Esporadico"
        );
        nivelUsuarioCombo.setValue("Esporadico"); // Por defecto
        
        // Permitir Enter para login
        nombreLoginLabel.setOnAction(e -> contraseñaLoginLabel.requestFocus());
        contraseñaLoginLabel.setOnAction(e -> manejarLogin(null));
    }

    @FXML
    private void manejarLogin(ActionEvent event) {
        String username = nombreLoginLabel.getText().trim();
        String password = contraseñaLoginLabel.getText();
        
        // Validaciones
        if (username.isEmpty()) {
            mostrarAlerta("Error", "Por favor ingrese su nombre de usuario", Alert.AlertType.ERROR);
            return;
        }
        
        if (password.isEmpty()) {
            mostrarAlerta("Error", "Por favor ingrese su contraseña", Alert.AlertType.ERROR);
            return;
        }
        
        try {
            // Intentar login
            Usuario usuario = usuarioController.login(username, password);
            
            if (usuario != null) {
                // Guardar usuario en sesión
                SessionManager.getInstance().setUsuarioActual(usuario);
                
                // Mostrar mensaje de bienvenida
                mostrarAlerta("Bienvenido", 
                    "Bienvenido " + usuario.getUsername() + "\nNivel: " + usuario.getNivel(), 
                    Alert.AlertType.INFORMATION);
                
                // Abrir vista principal según el nivel del usuario
                abrirVistaPrincipal(usuario);
                
            } else {
                mostrarAlerta("Error de Autenticación", 
                    "Usuario o contraseña incorrectos", 
                    Alert.AlertType.ERROR);
                contraseñaLoginLabel.clear();
                contraseñaLoginLabel.requestFocus();
            }
            
        } catch (IllegalStateException e) {
            mostrarAlerta("Usuario Inactivo", 
                "Su cuenta está inactiva. Contacte al administrador.", 
                Alert.AlertType.WARNING);
        } catch (SQLException e) {
            mostrarAlerta("Error de Conexión", 
                "No se pudo conectar a la base de datos:\n" + e.getMessage(), 
                Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void manejarRegistro(ActionEvent event) {
        String username = nombreRegistroLabel.getText().trim();
        String password = contrasenaRegistroLabel.getText();
        String passwordConfirmar = contrasenaConfirmarLabel.getText();
        String nivelSeleccionado = nivelUsuarioCombo.getValue();
        
        // Validaciones
        if (username.isEmpty() || password.isEmpty() || nivelSeleccionado == null) {
            mostrarAlerta("Campos Vacíos", "Por favor complete todos los campos", Alert.AlertType.ERROR);
            return;
        }
        
        if (!password.equals(passwordConfirmar)) {
            mostrarAlerta("Error", "Las contraseñas no coinciden", Alert.AlertType.ERROR);
            contrasenaConfirmarLabel.clear();
            contrasenaConfirmarLabel.requestFocus();
            return;
        }
        
        if (password.length() < 4) {
            mostrarAlerta("Contraseña Débil", 
                "La contraseña debe tener al menos 4 caracteres", 
                Alert.AlertType.ERROR);
            return;
        }
        
        try {
            // Convertir string a enum
            NivelUsuario nivel = NivelUsuario.fromString(nivelSeleccionado);
            
            // Crear usuario
            Usuario usuario = usuarioController.crearUsuario(username, password, nivel);
            
            if (usuario != null) {
                mostrarAlerta("Registro Exitoso", 
                    "Usuario '" + username + "' creado exitosamente con nivel " + nivel + ".\n" +
                    "Puede iniciar sesión ahora.", 
                    Alert.AlertType.INFORMATION);
                
                // Volver a la vista de login
                registrarseBoton(null);
                
                // Limpiar campos
                limpiarCamposRegistro();
            }
            
        } catch (IllegalArgumentException e) {
            mostrarAlerta("Error de Validación", e.getMessage(), Alert.AlertType.ERROR);
        } catch (SQLException e) {
            mostrarAlerta("Error de Base de Datos", 
                "No se pudo crear el usuario:\n" + e.getMessage(), 
                Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void registrarseBoton(ActionEvent event) {
        if (!mostrandoRegistro) {
            // Mostrar panel de registro
            TranslateTransition slideLogin = new TranslateTransition(Duration.seconds(0.5), loginPane);
            slideLogin.setToX(300);
            
            TranslateTransition slideRegistro = new TranslateTransition(Duration.seconds(0.5), registroPane);
            slideRegistro.setToX(0);
            
            TranslateTransition slideOpcion = new TranslateTransition(Duration.seconds(0.5), opcionPane);
            slideOpcion.setToX(300);
            
            slideLogin.play();
            slideRegistro.play();
            slideOpcion.play();
            
            crearUsuarioButton.setVisible(false);
            tengoCuentaButton.setVisible(true);
            
            mostrandoRegistro = true;
            
        } else {
            // Mostrar panel de login
            TranslateTransition slideLogin = new TranslateTransition(Duration.seconds(0.5), loginPane);
            slideLogin.setToX(0);
            
            TranslateTransition slideRegistro = new TranslateTransition(Duration.seconds(0.5), registroPane);
            slideRegistro.setToX(-300);
            
            TranslateTransition slideOpcion = new TranslateTransition(Duration.seconds(0.5), opcionPane);
            slideOpcion.setToX(0);
            
            slideLogin.play();
            slideRegistro.play();
            slideOpcion.play();
            
            crearUsuarioButton.setVisible(true);
            tengoCuentaButton.setVisible(false);
            
            mostrandoRegistro = false;
        }
    }

    private void abrirVistaPrincipal(Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/uniquindio/crisdav/gestionventas/views/MainView.fxml"));
            Scene scene = new Scene(loader.load());
            
            // Obtener el stage actual
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Sistema de Gestión de Ventas - " + usuario.getUsername());
            stage.setMaximized(true);
            stage.show();
            
        } catch (IOException e) {
            mostrarAlerta("Error", 
                "No se pudo abrir la vista principal:\n" + e.getMessage(), 
                Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void limpiarCamposRegistro() {
        nombreRegistroLabel.clear();
        contrasenaRegistroLabel.clear();
        contrasenaConfirmarLabel.clear();
        nivelUsuarioCombo.setValue("Esporadico");
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}