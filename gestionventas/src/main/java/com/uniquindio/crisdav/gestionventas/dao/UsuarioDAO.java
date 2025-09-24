package com.uniquindio.crisdav.gestionventas.dao;

import com.uniquindio.crisdav.gestionventas.models.entity.Usuario;
import java.time.LocalDateTime;

public interface UsuarioDAO {
    Usuario findByUsername(String username) throws Exception;
    Usuario findById(Long id) throws Exception;
    Long insert(Usuario usuario) throws Exception;
    void update(Usuario usuario) throws Exception;
    void updateLastLogin(Long usuarioId, LocalDateTime lastLogin) throws Exception;
    void incrementFailedAttempts(Long usuarioId) throws Exception;
    void resetFailedAttempts(Long usuarioId) throws Exception;
    void setActive(Long usuarioId, boolean activo) throws Exception;
}