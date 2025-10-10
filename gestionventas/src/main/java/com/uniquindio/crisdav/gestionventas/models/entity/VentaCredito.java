package com.uniquindio.crisdav.gestionventas.models.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

public class VentaCredito {
    private Integer idVentaCredito;
    private Integer idVenta;
    private BigDecimal cuotaInicial;
    private BigDecimal saldoFinanciado;
    private BigDecimal interesAplicado;
    private Integer numCuotas;
    private BigDecimal valorCuota;
    private BigDecimal saldoPendiente;
    private LocalDate fechaLimitePago;

    public VentaCredito() {
        this.interesAplicado = new BigDecimal("0.05");
    }

    public VentaCredito(Integer idVenta, BigDecimal cuotaInicial, BigDecimal saldoFinanciado,
                        Integer numCuotas, BigDecimal valorCuota) {
        this();
        this.idVenta = idVenta;
        this.cuotaInicial = cuotaInicial;
        this.saldoFinanciado = saldoFinanciado;
        this.numCuotas = numCuotas;
        this.valorCuota = valorCuota;
        this.saldoPendiente = saldoFinanciado;
    }

    public Integer getIdVentaCredito() {
        return idVentaCredito;
    }

    public void setIdVentaCredito(Integer idVentaCredito) {
        this.idVentaCredito = idVentaCredito;
    }

    public Integer getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(Integer idVenta) {
        this.idVenta = idVenta;
    }

    public BigDecimal getCuotaInicial() {
        return cuotaInicial;
    }

    public void setCuotaInicial(BigDecimal cuotaInicial) {
        this.cuotaInicial = cuotaInicial;
    }

    public BigDecimal getSaldoFinanciado() {
        return saldoFinanciado;
    }

    public void setSaldoFinanciado(BigDecimal saldoFinanciado) {
        this.saldoFinanciado = saldoFinanciado;
    }

    public BigDecimal getInteresAplicado() {
        return interesAplicado;
    }

    public void setInteresAplicado(BigDecimal interesAplicado) {
        this.interesAplicado = interesAplicado;
    }

    public Integer getNumCuotas() {
        return numCuotas;
    }

    public void setNumCuotas(Integer numCuotas) {
        this.numCuotas = numCuotas;
    }

    public BigDecimal getValorCuota() {
        return valorCuota;
    }

    public void setValorCuota(BigDecimal valorCuota) {
        this.valorCuota = valorCuota;
    }

    public BigDecimal getSaldoPendiente() {
        return saldoPendiente;
    }

    public void setSaldoPendiente(BigDecimal saldoPendiente) {
        this.saldoPendiente = saldoPendiente;
    }

    public LocalDate getFechaLimitePago() {
        return fechaLimitePago;
    }

    public void setFechaLimitePago(LocalDate fechaLimitePago) {
        this.fechaLimitePago = fechaLimitePago;
    }

    @Override
    public String toString() {
        return "VentaCredito{" +
                "idVentaCredito=" + idVentaCredito +
                ", idVenta=" + idVenta +
                ", numCuotas=" + numCuotas +
                ", saldoPendiente=" + saldoPendiente +
                '}';
    }
}