package com.uniquindio.crisdav.gestionventas.models.entity;

import com.uniquindio.crisdav.gestionventas.models.enums.AuditAction;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Registra entrada/salida (login/logout) de usuarios.
 */
public class Auditoria implements Serializable {
    private Long id;
    private LocalDateTime timestamp;
    private Long userId;        // nullable si login fallido con username no existente
    private String username;
    private AuditAction accion; // LOGIN / LOGOUT
    private boolean exito;
    private String ipAddress;
    private String detalle;

    public Auditoria() {
        this.timestamp = LocalDateTime.now();
    }

    // getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public AuditAction getAccion() { return accion; }
    public void setAccion(AuditAction accion) { this.accion = accion; }

    public boolean isExito() { return exito; }
    public void setExito(boolean exito) { this.exito = exito; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }

    @Override
    public String toString() {
        return "Auditoria{" +
                "timestamp=" + timestamp +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", accion=" + accion +
                ", exito=" + exito +
                '}';
    }
}
