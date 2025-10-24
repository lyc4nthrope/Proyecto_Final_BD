package com.uniquindio.crisdav.gestionventas.controllers.views;

import com.uniquindio.crisdav.gestionventas.controllers.UsuarioController;
import com.uniquindio.crisdav.gestionventas.models.entity.Usuario;
import com.uniquindio.crisdav.gestionventas.models.enums.EstadoUsuario;
import com.uniquindio.crisdav.gestionventas.models.enums.NivelUsuario;
import com.uniquindio.crisdav.gestionventas.utils.SessionManager;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UsuariosViewController {

    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> comboFiltroNivel;
    @FXML private ComboBox<String> comboFiltroEstado;
    
    @FXML private TableView<Usuario> tablaUsuarios;
    @FXML private TableColumn<Usuario, Integer> colId;
    @FXML private TableColumn<Usuario, String> colUsername;
    @FXML private TableColumn<Usuario, String> colNivel;
    @FXML private TableColumn<Usuario, String> colEstado;
    @FXML private TableColumn<Usuario, Void> colAcciones;
    
    @FXML private Label lblTotalUsuarios;
    @FXML private Label lblAdministradores;
    @FXML private Label lblParametricos;
    @FXML private Label lblEsporadicos;
    @FXML private Label lblActivos;
    @FXML private Label lblInactivos;

    private UsuarioController usuarioController;
    private ObservableList<Usuario> listaUsuarios;
    private ObservableList<Usuario> listaUsuariosFiltrada;

    @FXML
    public void initialize() {
        usuarioController = new UsuarioController();
        listaUsuarios = FXCollections.observableArrayList();
        listaUsuariosFiltrada = FXCollections.observableArrayList();

        configurarTabla();
        cargarUsuarios();
        configurarBusqueda();
        configurarCombobox();
        
    }

    private void configurarCombobox(){
        // ComboBox Nivel Filtro
        comboFiltroNivel.setItems(FXCollections.observableArrayList(
            "Todos los niveles",
            "Administrador",
            "Parametrico",
            "Esporadico"
        ));
       comboFiltroNivel.setValue("Todos los niveles");

       // ComboBox Estado Filtro
        comboFiltroEstado.setItems(FXCollections.observableArrayList(
            "Todos los estados",
            "Activo",
            "Inactivo"
        ));
        comboFiltroEstado.setValue("Todos los estados");

    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idUsuario"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colNivel.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNivel().getValor()));
        colEstado.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getEstado().getValor()));

        // Colorear estado
        colEstado.setCellFactory(column -> new TableCell<Usuario, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("Activo")) {
                        setStyle("-fx-background-color: #d4edda; -fx-text-fill: #155724; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-background-color: #f8d7da; -fx-text-fill: #721c24; -fx-font-weight: bold;");
                    }
                }
            }
        });

        // Columna de acciones
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnEditar = new Button("✏️");
            private final Button btnCambiarEstado = new Button("🔄");
            private final Button btnResetPassword = new Button("🔑");
            private final Button btnEliminar = new Button("🗑️");
            private final HBox hbox = new HBox(5, btnEditar, btnCambiarEstado, btnResetPassword, btnEliminar);

            {
                btnEditar.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-size: 10;");
                btnCambiarEstado.setStyle("-fx-background-color: #ffc107; -fx-text-fill: white; -fx-font-size: 10;");
                btnResetPassword.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white; -fx-font-size: 10;");
                btnEliminar.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 10;");
                
                btnEditar.setOnAction(event -> {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    editarUsuario(usuario);
                });

                btnCambiarEstado.setOnAction(event -> {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    cambiarEstado(usuario);
                });

                btnResetPassword.setOnAction(event -> {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    resetearPassword(usuario);
                });

                btnEliminar.setOnAction(event -> {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    eliminarUsuario(usuario);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    // No permitir editar/eliminar el usuario actual
                    if (usuario.getIdUsuario().equals(SessionManager.getInstance().getIdUsuarioActual())) {
                        btnEliminar.setDisable(true);
                    }
                    setGraphic(hbox);
                }
            }
        });

        tablaUsuarios.setItems(listaUsuariosFiltrada);
    }

    private void configurarBusqueda() {
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            aplicarFiltros(null);
        });
    }

    @FXML
    private void cargarUsuarios() {
        try {
            List<Usuario> usuarios = usuarioController.listarUsuarios();
            listaUsuarios.setAll(usuarios);
            listaUsuariosFiltrada.setAll(usuarios);
            
            actualizarEstadisticas();
            
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar usuarios:\n" + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void aplicarFiltros(ActionEvent event) {
        String criterio = txtBuscar.getText();
        String nivel = comboFiltroNivel.getValue();
        String estado = comboFiltroEstado.getValue();
        
        List<Usuario> filtrados = listaUsuarios.stream()
            .filter(u -> {
                // Filtro de búsqueda
                boolean coincideBusqueda = true;
                if (criterio != null && !criterio.trim().isEmpty()) {
                    String criterioLower = criterio.toLowerCase();
                    coincideBusqueda = u.getUsername().toLowerCase().contains(criterioLower);
                }
                
                // Filtro de nivel
                boolean coincideNivel = true;
                if (nivel != null && !nivel.equals("Todos los niveles")) {
                    coincideNivel = u.getNivel().getValor().equals(nivel);
                }
                
                // Filtro de estado
                boolean coincideEstado = true;
                if (estado != null && !estado.equals("Todos los estados")) {
                    coincideEstado = u.getEstado().getValor().equals(estado);
                }
                
                return coincideBusqueda && coincideNivel && coincideEstado;
            })
            .collect(Collectors.toList());
        
        listaUsuariosFiltrada.setAll(filtrados);
    }

    private void actualizarEstadisticas() {
        lblTotalUsuarios.setText(String.valueOf(listaUsuarios.size()));
        
        long admins = listaUsuarios.stream()
            .filter(u -> u.getNivel() == NivelUsuario.ADMINISTRADOR)
            .count();
        lblAdministradores.setText(String.valueOf(admins));
        
        long parametricos = listaUsuarios.stream()
            .filter(u -> u.getNivel() == NivelUsuario.PARAMETRICO)
            .count();
        lblParametricos.setText(String.valueOf(parametricos));
        
        long esporadicos = listaUsuarios.stream()
            .filter(u -> u.getNivel() == NivelUsuario.ESPORADICO)
            .count();
        lblEsporadicos.setText(String.valueOf(esporadicos));
        
        long activos = listaUsuarios.stream()
            .filter(u -> u.getEstado() == EstadoUsuario.ACTIVO)
            .count();
        lblActivos.setText(String.valueOf(activos));
        
        long inactivos = listaUsuarios.stream()
            .filter(u -> u.getEstado() == EstadoUsuario.INACTIVO)
            .count();
        lblInactivos.setText(String.valueOf(inactivos));
    }

    @FXML
    private void nuevoUsuario(ActionEvent event) {
        Dialog<UsuarioFormResult> dialog = crearDialogoUsuario(null);
        Optional<UsuarioFormResult> resultado = dialog.showAndWait();
        
        resultado.ifPresent(form -> {
            try {
                usuarioController.crearUsuario(
                    form.username,
                    form.password,
                    form.nivel
                );
                
                mostrarAlerta("Éxito", "Usuario creado correctamente", Alert.AlertType.INFORMATION);
                cargarUsuarios();
                
            } catch (SQLException e) {
                mostrarAlerta("Error", "Error al crear usuario:\n" + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                mostrarAlerta("Error de Validación", e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    private void editarUsuario(Usuario usuario) {
        Dialog<UsuarioFormResult> dialog = crearDialogoUsuario(usuario);
        Optional<UsuarioFormResult> resultado = dialog.showAndWait();
        
        resultado.ifPresent(form -> {
            try {
                usuario.setUsername(form.username);
                usuario.setNivel(form.nivel);
                
                // Si se proporcionó nueva contraseña
                if (form.password != null && !form.password.isEmpty()) {
                    usuario.setPassword(com.uniquindio.crisdav.gestionventas.utils.PasswordUtil.hashPassword(form.password));
                }
                
                usuarioController.actualizarUsuario(usuario);
                
                mostrarAlerta("Éxito", "Usuario actualizado correctamente", Alert.AlertType.INFORMATION);
                cargarUsuarios();
                
            } catch (SQLException e) {
                mostrarAlerta("Error", "Error al actualizar usuario:\n" + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                mostrarAlerta("Error de Validación", e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    private void cambiarEstado(Usuario usuario) {
        EstadoUsuario nuevoEstado = usuario.getEstado() == EstadoUsuario.ACTIVO ? 
            EstadoUsuario.INACTIVO : EstadoUsuario.ACTIVO;
        
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Cambiar Estado");
        confirmacion.setHeaderText("¿Cambiar estado del usuario?");
        confirmacion.setContentText(
            "Usuario: " + usuario.getUsername() + "\n" +
            "Estado actual: " + usuario.getEstado().getValor() + "\n" +
            "Nuevo estado: " + nuevoEstado.getValor()
        );
        
        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                usuarioController.cambiarEstadoUsuario(usuario.getIdUsuario(), nuevoEstado);
                mostrarAlerta("Éxito", "Estado actualizado correctamente", Alert.AlertType.INFORMATION);
                cargarUsuarios();
                
            } catch (SQLException e) {
                mostrarAlerta("Error", "Error al cambiar estado:\n" + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    private void resetearPassword(Usuario usuario) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Resetear Contraseña");
        dialog.setHeaderText("Resetear contraseña para: " + usuario.getUsername());
        dialog.setContentText("Nueva contraseña:");
        
        Optional<String> resultado = dialog.showAndWait();
        resultado.ifPresent(password -> {
            if (password.length() < 4) {
                mostrarAlerta("Error", "La contraseña debe tener al menos 4 caracteres", Alert.AlertType.ERROR);
                return;
            }
            
            try {
                String hashedPassword = com.uniquindio.crisdav.gestionventas.utils.PasswordUtil.hashPassword(password);
                usuarioController.cambiarPassword(usuario.getIdUsuario(), usuario.getPassword(), hashedPassword);
                
                mostrarAlerta("Éxito", "Contraseña actualizada correctamente", Alert.AlertType.INFORMATION);
                
            } catch (SQLException e) {
                mostrarAlerta("Error", "Error al actualizar contraseña:\n" + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        });
    }

    private void eliminarUsuario(Usuario usuario) {
        if (usuario.getIdUsuario().equals(SessionManager.getInstance().getIdUsuarioActual())) {
            mostrarAlerta("Error", "No puede eliminar su propio usuario", Alert.AlertType.ERROR);
            return;
        }
        
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Eliminar el usuario?");
        confirmacion.setContentText(
            "Usuario: " + usuario.getUsername() + "\n" +
            "Nivel: " + usuario.getNivel().getValor() + "\n\n" +
            "Esta acción no se puede deshacer."
        );
        
        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                boolean eliminado = usuarioController.eliminarUsuario(usuario.getIdUsuario());
                
                if (eliminado) {
                    mostrarAlerta("Éxito", "Usuario eliminado correctamente", Alert.AlertType.INFORMATION);
                    cargarUsuarios();
                } else {
                    mostrarAlerta("Error", "No se pudo eliminar el usuario", Alert.AlertType.ERROR);
                }
                
            } catch (SQLException e) {
                mostrarAlerta("Error", 
                    "Error al eliminar usuario:\n" + e.getMessage() + 
                    "\n\nEl usuario puede tener datos asociados.", 
                    Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    private Dialog<UsuarioFormResult> crearDialogoUsuario(Usuario usuarioExistente) {
        Dialog<UsuarioFormResult> dialog = new Dialog<>();
        dialog.setTitle(usuarioExistente == null ? "Nuevo Usuario" : "Editar Usuario");
        dialog.setHeaderText(usuarioExistente == null ? 
            "Crear nuevo usuario del sistema" : 
            "Modificar datos del usuario");

        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField txtUsername = new TextField();
        PasswordField txtPassword = new PasswordField();
        ComboBox<String> cmbNivel = new ComboBox<>();
        cmbNivel.getItems().addAll("Administrador", "Parametrico", "Esporadico");

        if (usuarioExistente != null) {
            txtUsername.setText(usuarioExistente.getUsername());
            cmbNivel.setValue(usuarioExistente.getNivel().getValor());
            txtPassword.setPromptText("Dejar vacío para no cambiar");
        } else {
            cmbNivel.setValue("Esporadico");
        }

        grid.add(new Label("Username:*"), 0, 0);
        grid.add(txtUsername, 1, 0);
        grid.add(new Label("Contraseña:*"), 0, 1);
        grid.add(txtPassword, 1, 1);
        grid.add(new Label("Nivel:*"), 0, 2);
        grid.add(cmbNivel, 1, 2);
        
        if (usuarioExistente != null) {
            Label lblInfo = new Label("* Dejar contraseña vacía para no cambiarla");
            lblInfo.setStyle("-fx-font-size: 10; -fx-text-fill: #666;");
            grid.add(lblInfo, 0, 3, 2, 1);
        }

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnGuardar) {
                String username = txtUsername.getText().trim();
                String password = txtPassword.getText();
                String nivelStr = cmbNivel.getValue();

                if (username.isEmpty()) {
                    mostrarAlerta("Error", "El username es obligatorio", Alert.AlertType.ERROR);
                    return null;
                }

                // Si es nuevo usuario, password es obligatorio
                if (usuarioExistente == null && password.isEmpty()) {
                    mostrarAlerta("Error", "La contraseña es obligatoria", Alert.AlertType.ERROR);
                    return null;
                }

                // Si no es nuevo y password no está vacío, validar longitud
                if (usuarioExistente != null && !password.isEmpty() && password.length() < 4) {
                    mostrarAlerta("Error", "La contraseña debe tener al menos 4 caracteres", Alert.AlertType.ERROR);
                    return null;
                }

                NivelUsuario nivel = NivelUsuario.fromString(nivelStr);
                
                // Si es edición y password está vacío, enviar null
                String passwordFinal = (usuarioExistente != null && password.isEmpty()) ? null : password;

                return new UsuarioFormResult(username, passwordFinal, nivel);
            }
            return null;
        });

        return dialog;
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private static class UsuarioFormResult {
        String username;
        String password;
        NivelUsuario nivel;

        UsuarioFormResult(String username, String password, NivelUsuario nivel) {
            this.username = username;
            this.password = password;
            this.nivel = nivel;
        }
    }
}