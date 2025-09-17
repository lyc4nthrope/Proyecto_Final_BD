package com.uniquindio.crisdav.gestionventas.controllers;

import java.io.IOException;



import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class LoginController {
    
    @FXML
    private PasswordField cedulaRegistroLabel;

    @FXML
    private TextField contrasenaConfirmarLabel;

    @FXML
    private PasswordField contrasenaRegistroLabel;

    @FXML
    private PasswordField contraseñaLoginLabel;

    @FXML
    private Button crearUsuarioButton;

    @FXML
    private Button loginButton;

    @FXML
    private AnchorPane loginPane;

    @FXML
    private TextField nombreLoginLabel;

    @FXML
    private TextField nombreRegistroLabel;

    @FXML
    private AnchorPane opcionPane;

    @FXML
    private Button registrarButton;

    @FXML
    private AnchorPane registroPane;

    @FXML
    private Button tengoCuentaButton;


    @FXML
    public void initialize() {
        
    }
    
    public void manejarLogin() throws ClassNotFoundException, IOException {

    }

    public void manejarRegistro() throws IOException, ClassNotFoundException {

    }

    private void mostrarAlerta(String titulo, String encabezado, String contenido) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(encabezado);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }

    private void mostrarInformacion(String titulo, String encabezado, String contenido) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION); // Cambiar a tipo de alerta de información
        alerta.setTitle(titulo);
        alerta.setHeaderText(encabezado);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }

    public void registrarseBoton(ActionEvent event){

        TranslateTransition slider = new TranslateTransition();

        if(event.getSource() == crearUsuarioButton){
            slider.setNode(opcionPane);
            slider.setToX(300);
            slider.setDuration(Duration.seconds(.5));

            slider.setOnFinished((ActionEvent e) ->{
                tengoCuentaButton.setVisible(true);
                crearUsuarioButton.setVisible(false);
            });

            slider.play();
        }else if(event.getSource() == tengoCuentaButton){
            slider.setNode(opcionPane);
            slider.setToX(0);
            slider.setDuration(Duration.seconds(.5));

            slider.setOnFinished((ActionEvent e) ->{
                tengoCuentaButton.setVisible(false);
                crearUsuarioButton.setVisible(true);
            });

            slider.play();
        }
    }
}

