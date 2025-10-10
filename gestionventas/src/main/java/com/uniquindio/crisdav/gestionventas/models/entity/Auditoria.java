package com.uniquindio.crisdav.gestionventas.models.entity;

import com.uniquindio.crisdav.gestionventas.models.enums.AccionAuditoria;
import java.time.LocalDateTime;

public class Auditoria {
    private Integer idAuditoria;
    private Integer idUsuario;
    private AccionAuditoria accion;
    private LocalDateTime fechaHora;

    public Auditoria() {
        this.fechaHora = LocalDateTime.now();
    }

    public Auditoria(Integer idUsuario, AccionAuditoria accion) {
        this.idUsuario = idUsuario;
        this.accion = accion;
        this.fechaHora = LocalDateTime.now();
    }

    public Integer getIdAuditoria() {
        return idAuditoria;
    }

    public void setIdAuditoria(Integer idAuditoria) {
        this.idAuditoria = idAuditoria;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public AccionAuditoria getAccion() {
        return accion;
    }

    public void setAccion(AccionAuditoria accion) {
        this.accion = accion;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    @Override
    public String toString() {
        return "Auditoria{" +
                "idAuditoria=" + idAuditoria +
                ", idUsuario=" + idUsuario +
                ", accion=" + accion +
                ", fechaHora=" + fechaHora +
                '}';
    }
}