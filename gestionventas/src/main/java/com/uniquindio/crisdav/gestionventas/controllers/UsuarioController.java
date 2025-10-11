package com.uniquindio.crisdav.gestionventas.controllers;

import com.uniquindio.crisdav.gestionventas.dao.AuditoriaDAO;
import com.uniquindio.crisdav.gestionventas.dao.UsuarioDAO;
import com.uniquindio.crisdav.gestionventas.models.entity.Auditoria;
import com.uniquindio.crisdav.gestionventas.models.entity.Usuario;
import com.uniquindio.crisdav.gestionventas.models.enums.AccionAuditoria;
import com.uniquindio.crisdav.gestionventas.models.enums.EstadoUsuario;
import com.uniquindio.crisdav.gestionventas.models.enums.NivelUsuario;
import com.uniquindio.crisdav.gestionventas.utils.PasswordUtil;

import java.sql.SQLException;
import java.util.List;

public class UsuarioController {
    
    private final UsuarioDAO usuarioDAO;
    private final AuditoriaDAO auditoriaDAO;
    private Usuario usuarioActual;

    public UsuarioController() {
        this.usuarioDAO = new UsuarioDAO();
        this.auditoriaDAO = new AuditoriaDAO();
    }

    /**
     * Intenta autenticar un usuario
     * @return Usuario si las credenciales son correctas, null en caso contrario
     */
    public Usuario login(String username, String password) throws SQLException {
        Usuario usuario = usuarioDAO.buscarPorUsername(username);
        
        if (usuario == null) {
            return null;
        }
        
        // Verificar que el usuario esté activo
        if (usuario.getEstado() != EstadoUsuario.ACTIVO) {
            throw new IllegalStateException("Usuario inactivo");
        }
        
        // Verificar password
        if (!PasswordUtil.verifyPassword(password, usuario.getPassword())) {
            return null;
        }
        
        // Registrar auditoría de login
        Auditoria auditoria = new Auditoria(usuario.getIdUsuario(), AccionAuditoria.LOGIN);
        auditoriaDAO.insertar(auditoria);
        
        this.usuarioActual = usuario;
        return usuario;
    }

    /**
     * Cierra la sesión del usuario actual
     */
    public void logout() throws SQLException {
        if (usuarioActual != null) {
            Auditoria auditoria = new Auditoria(usuarioActual.getIdUsuario(), AccionAuditoria.LOGOUT);
            auditoriaDAO.insertar(auditoria);
            usuarioActual = null;
        }
    }

    /**
     * Crea un nuevo usuario
     */
    public Usuario crearUsuario(String username, String password, NivelUsuario nivel) throws SQLException {
        // Verificar que el username no exista
        if (usuarioDAO.existeUsername(username)) {
            throw new IllegalArgumentException("El username ya existe");
        }
        
        // Validar datos
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El username no puede estar vacío");
        }
        
        if (password == null || password.length() < 4) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 4 caracteres");
        }
        
        // Hashear password
        String hashedPassword = PasswordUtil.hashPassword(password);
        
        // Crear usuario
        Usuario usuario = new Usuario(username, hashedPassword, nivel);
        return usuarioDAO.insertar(usuario);
    }

    /**
     * Actualiza la información de un usuario
     */
    public boolean actualizarUsuario(Usuario usuario) throws SQLException {
        if (usuario.getIdUsuario() == null) {
            throw new IllegalArgumentException("ID de usuario no puede ser null");
        }
        
        return usuarioDAO.actualizar(usuario);
    }

    /**
     * Cambia la contraseña de un usuario
     */
    public boolean cambiarPassword(Integer idUsuario, String passwordActual, String passwordNuevo) throws SQLException {
        Usuario usuario = usuarioDAO.buscarPorId(idUsuario);
        
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        
        // Verificar password actual
        if (!PasswordUtil.verifyPassword(passwordActual, usuario.getPassword())) {
            throw new IllegalArgumentException("Contraseña actual incorrecta");
        }
        
        // Validar nueva password
        if (passwordNuevo == null || passwordNuevo.length() < 4) {
            throw new IllegalArgumentException("La nueva contraseña debe tener al menos 4 caracteres");
        }
        
        // Hashear y actualizar
        String hashedPassword = PasswordUtil.hashPassword(passwordNuevo);
        return usuarioDAO.actualizarPassword(idUsuario, hashedPassword);
    }

    /**
     * Cambia el estado de un usuario (Activo/Inactivo)
     */
    public boolean cambiarEstadoUsuario(Integer idUsuario, EstadoUsuario nuevoEstado) throws SQLException {
        return usuarioDAO.cambiarEstado(idUsuario, nuevoEstado);
    }

    /**
     * Busca un usuario por ID
     */
    public Usuario buscarUsuario(Integer id) throws SQLException {
        return usuarioDAO.buscarPorId(id);
    }

    /**
     * Lista todos los usuarios
     */
    public List<Usuario> listarUsuarios() throws SQLException {
        return usuarioDAO.listarTodos();
    }

    /**
     * Lista solo usuarios activos
     */
    public List<Usuario> listarUsuariosActivos() throws SQLException {
        return usuarioDAO.listarActivos();
    }

    /**
     * Elimina un usuario (usar con precaución)
     */
    public boolean eliminarUsuario(Integer id) throws SQLException {
        return usuarioDAO.eliminar(id);
    }

    /**
     * Verifica si el usuario actual tiene un nivel específico
     */
    public boolean tieneNivel(NivelUsuario nivel) {
        return usuarioActual != null && usuarioActual.getNivel() == nivel;
    }

    /**
     * Verifica si el usuario actual es administrador
     */
    public boolean esAdministrador() {
        return tieneNivel(NivelUsuario.ADMINISTRADOR);
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public void setUsuarioActual(Usuario usuario) {
        this.usuarioActual = usuario;
    }
}