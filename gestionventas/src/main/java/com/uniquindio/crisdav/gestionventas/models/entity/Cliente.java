package com.uniquindio.crisdav.gestionventas.models.entity;

public class Cliente {
    private Integer idCliente;
    private String cedula;
    private String nombre;
    private String direccion;
    private String telefono;
    private String correo;
    private Boolean tieneCreditoActivo;

    public Cliente() {
        this.tieneCreditoActivo = false;
    }

    public Cliente(String cedula, String nombre, String direccion, String telefono, String correo) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.correo = correo;
        this.tieneCreditoActivo = false;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Boolean getTieneCreditoActivo() {
        return tieneCreditoActivo;
    }

    public void setTieneCreditoActivo(Boolean tieneCreditoActivo) {
        this.tieneCreditoActivo = tieneCreditoActivo;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "idCliente=" + idCliente +
                ", cedula='" + cedula + '\'' +
                ", nombre='" + nombre + '\'' +
                ", tieneCreditoActivo=" + tieneCreditoActivo +
                '}';
    }
}