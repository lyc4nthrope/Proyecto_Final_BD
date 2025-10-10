package com.uniquindio.crisdav.gestionventas.models.vo;

import java.math.BigDecimal;

public class EstadisticaVentasVO {
    private String periodo;
    private Integer cantidadVentasContado;
    private Integer cantidadVentasCredito;
    private Integer cantidadTotalVentas;
    private BigDecimal montoTotalContado;
    private BigDecimal montoTotalCredito;
    private BigDecimal montoTotal;

    public EstadisticaVentasVO() {}

    public EstadisticaVentasVO(String periodo, Integer cantidadVentasContado,
                               Integer cantidadVentasCredito, BigDecimal montoTotalContado,
                               BigDecimal montoTotalCredito) {
        this.periodo = periodo;
        this.cantidadVentasContado = cantidadVentasContado;
        this.cantidadVentasCredito = cantidadVentasCredito;
        this.cantidadTotalVentas = cantidadVentasContado + cantidadVentasCredito;
        this.montoTotalContado = montoTotalContado;
        this.montoTotalCredito = montoTotalCredito;
        this.montoTotal = montoTotalContado.add(montoTotalCredito);
    }

    // Getters y Setters
    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public Integer getCantidadVentasContado() {
        return cantidadVentasContado;
    }

    public void setCantidadVentasContado(Integer cantidadVentasContado) {
        this.cantidadVentasContado = cantidadVentasContado;
    }

    public Integer getCantidadVentasCredito() {
        return cantidadVentasCredito;
    }

    public void setCantidadVentasCredito(Integer cantidadVentasCredito) {
        this.cantidadVentasCredito = cantidadVentasCredito;
    }

    public Integer getCantidadTotalVentas() {
        return cantidadTotalVentas;
    }

    public void setCantidadTotalVentas(Integer cantidadTotalVentas) {
        this.cantidadTotalVentas = cantidadTotalVentas;
    }

    public BigDecimal getMontoTotalContado() {
        return montoTotalContado;
    }

    public void setMontoTotalContado(BigDecimal montoTotalContado) {
        this.montoTotalContado = montoTotalContado;
    }

    public BigDecimal getMontoTotalCredito() {
        return montoTotalCredito;
    }

    public void setMontoTotalCredito(BigDecimal montoTotalCredito) {
        this.montoTotalCredito = montoTotalCredito;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }
}