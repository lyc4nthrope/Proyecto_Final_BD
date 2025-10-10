package com.uniquindio.crisdav.gestionventas.models.entity;

import com.uniquindio.crisdav.gestionventas.models.enums.EstadoCuota;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Cuota {
    private Integer idCuota;
    private Integer idVentaCredito;
    private Integer numCuota;
    private LocalDate fechaVencimiento;
    private LocalDate fechaPago;
    private BigDecimal valorCuota;
    private EstadoCuota estado;

    public Cuota() {
        this.estado = EstadoCuota.PENDIENTE;
    }

    public Cuota(Integer idVentaCredito, Integer numCuota, LocalDate fechaVencimiento, BigDecimal valorCuota) {
        this();
        this.idVentaCredito = idVentaCredito;
        this.numCuota = numCuota;
        this.fechaVencimiento = fechaVencimiento;
        this.valorCuota = valorCuota;
    }

    public Integer getIdCuota() {
        return idCuota;
    }

    public void setIdCuota(Integer idCuota) {
        this.idCuota = idCuota;
    }

    public Integer getIdVentaCredito() {
        return idVentaCredito;
    }

    public void setIdVentaCredito(Integer idVentaCredito) {
        this.idVentaCredito = idVentaCredito;
    }

    public Integer getNumCuota() {
        return numCuota;
    }

    public void setNumCuota(Integer numCuota) {
        this.numCuota = numCuota;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }

    public BigDecimal getValorCuota() {
        return valorCuota;
    }

    public void setValorCuota(BigDecimal valorCuota) {
        this.valorCuota = valorCuota;
    }

    public EstadoCuota getEstado() {
        return estado;
    }

    public void setEstado(EstadoCuota estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Cuota{" +
                "idCuota=" + idCuota +
                ", numCuota=" + numCuota +
                ", fechaVencimiento=" + fechaVencimiento +
                ", estado=" + estado +
                '}';
    }
}