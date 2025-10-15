package com.uniquindio.crisdav.gestionventas.models.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

// Clase auxiliar para información del crédito
public class CreditoInfo {
    private Integer idVentaCredito;
    private Integer idVenta;
    private String nombreCliente;
    private String cedulaCliente;
    private LocalDate fechaVenta;
    private BigDecimal totalVenta;
    private BigDecimal cuotaInicial;
    private BigDecimal saldoFinanciado;
    private BigDecimal saldoPendiente;
    private Integer numCuotas;
    private Boolean tieneCuotasVencidas;

    public CreditoInfo(Integer idVentaCredito, Integer idVenta, String nombreCliente, String cedulaCliente, LocalDate fechaVenta, BigDecimal totalVenta, BigDecimal cuotaInicial, BigDecimal saldoFinanciado, BigDecimal saldoPendiente, Integer numCuotas, Boolean tieneCuotasVencidas) {
        this.idVentaCredito = idVentaCredito;
        this.idVenta = idVenta;
        this.nombreCliente = nombreCliente;
        this.cedulaCliente = cedulaCliente;
        this.fechaVenta = fechaVenta;
        this.totalVenta = totalVenta;
        this.cuotaInicial = cuotaInicial;
        this.saldoFinanciado = saldoFinanciado;
        this.saldoPendiente = saldoPendiente;
        this.numCuotas = numCuotas;
        this.tieneCuotasVencidas = tieneCuotasVencidas;
    }

    // Getters
    public Integer getIdVentaCredito() {
        return idVentaCredito;
    }

    public Integer getIdVenta() {
        return idVenta;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public String getCedulaCliente() {
        return cedulaCliente;
    }

    public LocalDate getFechaVenta() {
        return fechaVenta;
    }

    public BigDecimal getTotalVenta() {
        return totalVenta;
    }

    public BigDecimal getCuotaInicial() {
        return cuotaInicial;
    }

    public BigDecimal getSaldoFinanciado() {
        return saldoFinanciado;
    }

    public BigDecimal getSaldoPendiente() {
        return saldoPendiente;
    }

    public Integer getNumCuotas() {
        return numCuotas;
    }

    public Boolean getTieneCuotasVencidas() {
        return tieneCuotasVencidas;
    }

}
