package com.uniquindio.crisdav.gestionventas.utils;

import com.uniquindio.crisdav.gestionventas.models.entity.Usuario;
import com.uniquindio.crisdav.gestionventas.models.enums.NivelUsuario;

/**
 * Singleton para gestionar la sesión del usuario actual en toda la aplicación
 */
public class SessionManager {
    
    private static SessionManager instance;
    private Usuario usuarioActual;
    
    private SessionManager() {}
    
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    /**
     * Establece el usuario actual de la sesión
     */
    public void setUsuarioActual(Usuario usuario) {
        this.usuarioActual = usuario;
    }
    
    /**
     * Obtiene el usuario actual de la sesión
     */
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }
    
    /**
     * Verifica si hay un usuario logueado
     */
    public boolean isLoggedIn() {
        return usuarioActual != null;
    }
    
    /**
     * Cierra la sesión actual
     */
    public void logout() {
        this.usuarioActual = null;
    }
    
    /**
     * Obtiene el ID del usuario actual
     */
    public Integer getIdUsuarioActual() {
        return usuarioActual != null ? usuarioActual.getIdUsuario() : null;
    }
    
    /**
     * Obtiene el username del usuario actual
     */
    public String getUsernameActual() {
        return usuarioActual != null ? usuarioActual.getUsername() : null;
    }
    
    /**
     * Obtiene el nivel del usuario actual
     */
    public NivelUsuario getNivelActual() {
        return usuarioActual != null ? usuarioActual.getNivel() : null;
    }
    
    /**
     * Verifica si el usuario actual es administrador
     */
    public boolean esAdministrador() {
        return usuarioActual != null && usuarioActual.getNivel() == NivelUsuario.ADMINISTRADOR;
    }
    
    /**
     * Verifica si el usuario actual es paramétrico
     */
    public boolean esParametrico() {
        return usuarioActual != null && usuarioActual.getNivel() == NivelUsuario.PARAMETRICO;
    }
    
    /**
     * Verifica si el usuario actual es esporádico
     */
    public boolean esEsporadico() {
        return usuarioActual != null && usuarioActual.getNivel() == NivelUsuario.ESPORADICO;
    }
    
    /**
     * Verifica si el usuario actual tiene un nivel específico
     */
    public boolean tieneNivel(NivelUsuario nivel) {
        return usuarioActual != null && usuarioActual.getNivel() == nivel;
    }
    
    /**
     * Verifica si el usuario actual tiene al menos el nivel especificado
     * Orden: ADMINISTRADOR > PARAMETRICO > ESPORADICO
     */
    public boolean tieneNivelMinimo(NivelUsuario nivelMinimo) {
        if (usuarioActual == null) {
            return false;
        }
        
        NivelUsuario nivelActual = usuarioActual.getNivel();
        
        if (nivelActual == NivelUsuario.ADMINISTRADOR) {
            return true;
        }
        
        if (nivelActual == NivelUsuario.PARAMETRICO) {
            return nivelMinimo != NivelUsuario.ADMINISTRADOR;
        }
        
        if (nivelActual == NivelUsuario.ESPORADICO) {
            return nivelMinimo == NivelUsuario.ESPORADICO;
        }
        
        return false;
    }
    
    @Override
    public String toString() {
        if (usuarioActual == null) {
            return "Sesión: No hay usuario logueado";
        }
        return "Sesión: " + usuarioActual.getUsername() + " (" + usuarioActual.getNivel() + ")";
    }
}