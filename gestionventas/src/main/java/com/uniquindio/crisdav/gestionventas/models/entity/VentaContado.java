package com.uniquindio.crisdav.gestionventas.models.entity;

import java.math.BigDecimal;

public class VentaContado {
    private Integer idVentaContado;
    private Integer idVenta;
    private BigDecimal montoPagado;

    public VentaContado() {}

    public VentaContado(Integer idVenta, BigDecimal montoPagado) {
        this.idVenta = idVenta;
        this.montoPagado = montoPagado;
    }

    public Integer getIdVentaContado() {
        return idVentaContado;
    }

    public void setIdVentaContado(Integer idVentaContado) {
        this.idVentaContado = idVentaContado;
    }

    public Integer getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(Integer idVenta) {
        this.idVenta = idVenta;
    }

    public BigDecimal getMontoPagado() {
        return montoPagado;
    }

    public void setMontoPagado(BigDecimal montoPagado) {
        this.montoPagado = montoPagado;
    }

    @Override
    public String toString() {
        return "VentaContado{" +
                "idVentaContado=" + idVentaContado +
                ", idVenta=" + idVenta +
                ", montoPagado=" + montoPagado +
                '}';
    }
}