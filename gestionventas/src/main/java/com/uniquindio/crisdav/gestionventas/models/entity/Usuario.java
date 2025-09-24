package com.uniquindio.crisdav.gestionventas.models.entity;

import com.uniquindio.crisdav.gestionventas.models.enums.UserRole;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Representa un usuario del sistema (login y clave).
 * Nota: la contraseña debe almacenarse hasheada (ej. bcrypt) desde la capa de servicio.
 */
public class Usuario implements Serializable {
    private Long id;
    private java.time.LocalDate createdAt;
    private java.time.LocalDate updatedAt;

    private String username;
    private String passwordHash; // almacenar hash, nunca la clave en texto plano
    private UserRole role;
    private boolean activo;
    private LocalDateTime lastLogin;
    private Integer intentosFallidos;

    public Usuario() {
        this.createdAt = java.time.LocalDate.now();
        this.updatedAt = java.time.LocalDate.now();
        this.activo = true;
        this.intentosFallidos = 0;
    }

    // getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public java.time.LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.time.LocalDate createdAt) { this.createdAt = createdAt; }

    public java.time.LocalDate getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(java.time.LocalDate updatedAt) { this.updatedAt = updatedAt; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }

    public Integer getIntentosFallidos() { return intentosFallidos; }
    public void setIntentosFallidos(Integer intentosFallidos) { this.intentosFallidos = intentosFallidos; }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role=" + role +
                ", activo=" + activo +
                '}';
    }
}
