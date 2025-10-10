package com.uniquindio.crisdav.gestionventas.models.vo;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ClienteMorosoVO {
    private String cedula;
    private String nombreCliente;
    private String telefono;
    private Integer idVentaCredito;
    private LocalDate fechaVenta;
    private Integer numCuotasVencidas;
    private BigDecimal totalVencido;
    private BigDecimal saldoPendiente;
    private LocalDate fechaUltimaVencida;

    public ClienteMorosoVO() {}

    public ClienteMorosoVO(String cedula, String nombreCliente, String telefono,
                           Integer idVentaCredito, LocalDate fechaVenta,
                           Integer numCuotasVencidas, BigDecimal totalVencido,
                           BigDecimal saldoPendiente, LocalDate fechaUltimaVencida) {
        this.cedula = cedula;
        this.nombreCliente = nombreCliente;
        this.telefono = telefono;
        this.idVentaCredito = idVentaCredito;
        this.fechaVenta = fechaVenta;
        this.numCuotasVencidas = numCuotasVencidas;
        this.totalVencido = totalVencido;
        this.saldoPendiente = saldoPendiente;
        this.fechaUltimaVencida = fechaUltimaVencida;
    }

    // Getters y Setters
    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Integer getIdVentaCredito() {
        return idVentaCredito;
    }

    public void setIdVentaCredito(Integer idVentaCredito) {
        this.idVentaCredito = idVentaCredito;
    }

    public LocalDate getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(LocalDate fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public Integer getNumCuotasVencidas() {
        return numCuotasVencidas;
    }

    public void setNumCuotasVencidas(Integer numCuotasVencidas) {
        this.numCuotasVencidas = numCuotasVencidas;
    }

    public BigDecimal getTotalVencido() {
        return totalVencido;
    }

    public void setTotalVencido(BigDecimal totalVencido) {
        this.totalVencido = totalVencido;
    }

    public BigDecimal getSaldoPendiente() {
        return saldoPendiente;
    }

    public void setSaldoPendiente(BigDecimal saldoPendiente) {
        this.saldoPendiente = saldoPendiente;
    }

    public LocalDate getFechaUltimaVencida() {
        return fechaUltimaVencida;
    }

    public void setFechaUltimaVencida(LocalDate fechaUltimaVencida) {
        this.fechaUltimaVencida = fechaUltimaVencida;
    }
}