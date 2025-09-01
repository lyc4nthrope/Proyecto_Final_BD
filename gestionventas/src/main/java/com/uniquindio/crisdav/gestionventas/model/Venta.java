package com.uniquindio.crisdav.gestionventas.model;

import java.time.LocalDate;

/**
 * Entidad Venta: representa la tabla VENTA en la base de datos.
 */
public class Venta {
    private int idVenta;
    private LocalDate fecha;
    private String tipoVenta; // "Contado" o "Crédito"
    private int idCliente;

    public Venta() {}

    public Venta(int idVenta, LocalDate fecha, String tipoVenta, int idCliente) {
        this.idVenta = idVenta;
        this.fecha = fecha;
        this.tipoVenta = tipoVenta;
        this.idCliente = idCliente;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getTipoVenta() {
        return tipoVenta;
    }

    public void setTipoVenta(String tipoVenta) {
        this.tipoVenta = tipoVenta;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }
}
