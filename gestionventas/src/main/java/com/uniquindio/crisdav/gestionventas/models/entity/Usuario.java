package com.uniquindio.crisdav.gestionventas.models.entity;

import com.uniquindio.crisdav.gestionventas.models.enums.NivelUsuario;
import com.uniquindio.crisdav.gestionventas.models.enums.EstadoUsuario;

public class Usuario {
    private Integer idUsuario;
    private String username;
    private String password;
    private NivelUsuario nivel;
    private EstadoUsuario estado;

    public Usuario() {
        this.estado = EstadoUsuario.ACTIVO;
    }

    public Usuario(String username, String password, NivelUsuario nivel) {
        this.username = username;
        this.password = password;
        this.nivel = nivel;
        this.estado = EstadoUsuario.ACTIVO;
    }

    // Getters y Setters
    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public NivelUsuario getNivel() {
        return nivel;
    }

    public void setNivel(NivelUsuario nivel) {
        this.nivel = nivel;
    }

    public EstadoUsuario getEstado() {
        return estado;
    }

    public void setEstado(EstadoUsuario estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", username='" + username + '\'' +
                ", nivel=" + nivel +
                ", estado=" + estado +
                '}';
    }
}