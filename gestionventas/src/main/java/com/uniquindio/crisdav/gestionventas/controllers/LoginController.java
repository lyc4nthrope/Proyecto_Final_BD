package com.uniquindio.crisdav.gestionventas.controllers;

import com.uniquindio.crisdav.gestionventas.dao.AuditoriaDAO;
import com.uniquindio.crisdav.gestionventas.dao.impl.AuditoriaDAOImpl;
import com.uniquindio.crisdav.gestionventas.dao.UsuarioDAO;
import com.uniquindio.crisdav.gestionventas.dao.impl.UsuarioDAOImpl;
import com.uniquindio.crisdav.gestionventas.models.entity.Auditoria;
import com.uniquindio.crisdav.gestionventas.models.entity.Usuario;
import com.uniquindio.crisdav.gestionventas.models.enums.AuditAction;
import com.uniquindio.crisdav.gestionventas.models.enums.UserRole;
import com.uniquindio.crisdav.gestionventas.utils.PasswordUtil;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.time.LocalDateTime;

public class LoginController {

    @FXML private TextField cedulaRegistroLabel;
    @FXML private PasswordField contrasenaConfirmarLabel;
    @FXML private PasswordField contrasenaRegistroLabel;
    @FXML private PasswordField contraseñaLoginLabel;
    @FXML private Button crearUsuarioButton;
    @FXML private Button loginButton;
    @FXML private AnchorPane loginPane;
    @FXML private TextField nombreLoginLabel;
    @FXML private TextField nombreRegistroLabel;
    @FXML private AnchorPane opcionPane;
    @FXML private Button registrarButton;
    @FXML private AnchorPane registroPane;
    @FXML private Button tengoCuentaButton;

    // DAOs (implementaciones JDBC que ya te entregué)
    private UsuarioDAO usuarioDAO = new UsuarioDAOImpl();
    private AuditoriaDAO auditoriaDAO = new AuditoriaDAOImpl();

    // política simple de bloqueo
    private static final int MAX_INTENTOS = 5;

    @FXML
    public void initialize() {
        // cualquier inicialización (e.g., setear tooltips)
    }

    // Método llamado por el botón de login (con ActionEvent si tu FXML lo usa)
    @FXML
    public void manejarLogin(ActionEvent event) {
        String username = nombreLoginLabel.getText().trim();
        String password = contraseñaLoginLabel.getText();

        if (username.isEmpty() || password.isEmpty()) {
            mostrarAlerta("Error", "Campos vacíos", "Por favor, ingrese su usuario y contraseña.");
            return;
        }

        try {
            Usuario usuario = usuarioDAO.findByUsername(username);

            if (usuario == null) {
                // registro de intento fallido (usuario inexistente)
                Auditoria a = new Auditoria();
                a.setUsername(username);
                a.setAccion(null); // opcional, puedes dejar null o agregar una acción específica
                a.setExito(false);
                a.setDetalle("Login: usuario no encontrado");
                auditoriaDAO.insert(a);

                mostrarAlerta("Error", "Login fallido", "Usuario o contraseña incorrectos.");
                return;
            }

            if (!usuario.isActivo()) {
                mostrarAlerta("Cuenta desactivada", "Acceso denegado", "Su cuenta está desactivada. Contacte al administrador.");
                return;
            }

            // Verificar contraseña (PasswordUtils abstrae el hash)
            boolean ok = PasswordUtil.verifyPassword(password, usuario.getPasswordHash());

            if (ok) {
                // éxito: actualizar lastLogin, resetear intentos
                usuarioDAO.updateLastLogin(usuario.getId(), LocalDateTime.now());
                usuarioDAO.resetFailedAttempts(usuario.getId());

                // registrar auditoría de login exitoso
                Auditoria a = new Auditoria();
                a.setUserId(usuario.getId());
                a.setUsername(usuario.getUsername());
                a.setAccion(AuditAction.LOGIN);
                a.setExito(true);
                a.setDetalle("Login exitoso");
                auditoriaDAO.insert(a);

                // guardar usuario en sesión
                com.uniquindio.crisdav.gestionventas.controllers.AppSession.setCurrentUser(usuario);

                mostrarInformacion("Login exitoso", null, "Bienvenido, " + usuario.getUsername());
                // TODO: navegar al Dashboard principal (depende de tu gestor de escenas)
                // e.g.: AppNavigator.loadScene("dashboard.fxml");
            } else {
                // contraseña incorrecta -> incrementar intentos y registrar auditoría
                usuarioDAO.incrementFailedAttempts(usuario.getId());

                // obtener intentos actuales (releer usuario)
                Usuario refreshed = usuarioDAO.findById(usuario.getId());
                int intentos = refreshed != null && refreshed.getIntentosFallidos() != null ? refreshed.getIntentosFallidos() : 0;

                // Auditar intento fallido
                Auditoria a = new Auditoria();
                a.setUserId(usuario.getId());
                a.setUsername(usuario.getUsername());
                a.setAccion(null);
                a.setExito(false);
                a.setDetalle("Login fallido: contraseña incorrecta. Intentos=" + intentos);
                auditoriaDAO.insert(a);

                if (intentos >= MAX_INTENTOS) {
                    usuarioDAO.setActive(usuario.getId(), false); // bloquear
                    mostrarAlerta("Cuenta bloqueada", "Se alcanzó el número máximo de intentos", "Su cuenta ha sido bloqueada. Contacte al administrador.");
                } else {
                    mostrarAlerta("Error", "Login fallido", "Usuario o contraseña incorrectos. Intentos: " + intentos + "/" + MAX_INTENTOS);
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // en dev; en prod usar logger
            mostrarAlerta("Error", "Login fallido", "Ocurrió un error al intentar iniciar sesión.");
        }
    }

    @FXML
    public void manejarRegistro(ActionEvent event) {
        String username = nombreRegistroLabel.getText().trim();
        String password = contrasenaRegistroLabel.getText();
        String passwordConfirm = contrasenaConfirmarLabel.getText();

        if (username.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
            mostrarAlerta("Error", "Campos vacíos", "Por favor, complete todos los campos.");
            return;
        }

        if (!password.equals(passwordConfirm)) {
            mostrarAlerta("Error", "Contraseña", "Las contraseñas no coinciden.");
            return;
        }

        try {
            // Evitar duplicados
            Usuario existente = usuarioDAO.findByUsername(username);
            if (existente != null) {
                mostrarAlerta("Registro", "Usuario existente", "Ya existe un usuario con ese username.");
                return;
            }

            // Crear usuario (por defecto VENDEDOR)
            Usuario usuario = new Usuario();
            usuario.setUsername(username);
            usuario.setPasswordHash(PasswordUtil.hashPassword(password));
            // si tu enum tiene los roles actualizados usa: UserRole.VENDEDOR
            usuario.setRole(UserRole.VENDEDOR);
            usuario.setActivo(true);
            usuario.setIntentosFallidos(0);

            Long newId = usuarioDAO.insert(usuario); // devuelve id
            usuario.setId(newId);

            // Auditar registro (accion = null o usa un enum nuevo si lo agregas)
            Auditoria a = new Auditoria();
            a.setUserId(newId);
            a.setUsername(username);
            a.setAccion(null);
            a.setExito(true);
            a.setDetalle("Registro de usuario (vendedor)");
            auditoriaDAO.insert(a);

            mostrarInformacion("Registro exitoso", null, "Usuario registrado correctamente.");
            // opcional: auto-login o navegar al login
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Registro fallido", "No se pudo registrar el usuario.");
        }
    }

    private void mostrarAlerta(String titulo, String encabezado, String contenido) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(encabezado);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }

    private void mostrarInformacion(String titulo, String encabezado, String contenido) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(encabezado);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }

    public void registrarseBoton(ActionEvent event) {
        TranslateTransition slider = new TranslateTransition();

        if (event.getSource() == crearUsuarioButton) {
            slider.setNode(opcionPane);
            slider.setToX(300);
            slider.setDuration(Duration.seconds(.5));

            slider.setOnFinished((ActionEvent e) -> {
                tengoCuentaButton.setVisible(true);
                crearUsuarioButton.setVisible(false);
            });

            slider.play();
        } else if (event.getSource() == tengoCuentaButton) {
            slider.setNode(opcionPane);
            slider.setToX(0);
            slider.setDuration(Duration.seconds(.5));

            slider.setOnFinished((ActionEvent e) -> {
                tengoCuentaButton.setVisible(false);
                crearUsuarioButton.setVisible(true);
            });

            slider.play();
        }
    }
}
