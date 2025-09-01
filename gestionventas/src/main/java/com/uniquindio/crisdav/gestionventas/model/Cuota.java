package com.uniquindio.crisdav.gestionventas.model;

import java.time.LocalDate;

/**
 * Entidad Cuota: representa la tabla CUOTA en la base de datos.
 */
public class Cuota {
    private int idCuota;
    private int idVenta;
    private int numeroCuota;
    private double valorCuota;
    private LocalDate fechaPago;
    private String estado; // "Pagada" o "Pendiente"

    public Cuota() {}

    public Cuota(int idCuota, int idVenta, int numeroCuota, double valorCuota, LocalDate fechaPago, String estado) {
        this.idCuota = idCuota;
        this.idVenta = idVenta;
        this.numeroCuota = numeroCuota;
        this.valorCuota = valorCuota;
        this.fechaPago = fechaPago;
        this.estado = estado;
    }

    public int getIdCuota() {
        return idCuota;
    }

    public void setIdCuota(int idCuota) {
        this.idCuota = idCuota;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public int getNumeroCuota() {
        return numeroCuota;
    }

    public void setNumeroCuota(int numeroCuota) {
        this.numeroCuota = numeroCuota;
    }

    public double getValorCuota() {
        return valorCuota;
    }

    public void setValorCuota(double valorCuota) {
        this.valorCuota = valorCuota;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
