package com.uniquindio.crisdav.gestionventas.models.entity;

import com.uniquindio.crisdav.gestionventas.models.enums.TipoVenta;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Venta {
    private Integer idVenta;
    private TipoVenta tipoVenta;
    private LocalDate fecha;
    private Integer idCliente;
    private Integer idVendedor;
    private Integer idUsuario;
    private BigDecimal subtotal;
    private BigDecimal totalIva;
    private BigDecimal total;

    public Venta() {
        this.fecha = LocalDate.now();
        this.subtotal = BigDecimal.ZERO;
        this.totalIva = BigDecimal.ZERO;
        this.total = BigDecimal.ZERO;
    }

    public Venta(TipoVenta tipoVenta, Integer idCliente, Integer idVendedor, Integer idUsuario) {
        this();
        this.tipoVenta = tipoVenta;
        this.idCliente = idCliente;
        this.idVendedor = idVendedor;
        this.idUsuario = idUsuario;
    }

    public Integer getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(Integer idVenta) {
        this.idVenta = idVenta;
    }

    public TipoVenta getTipoVenta() {
        return tipoVenta;
    }

    public void setTipoVenta(TipoVenta tipoVenta) {
        this.tipoVenta = tipoVenta;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public Integer getIdVendedor() {
        return idVendedor;
    }

    public void setIdVendedor(Integer idVendedor) {
        this.idVendedor = idVendedor;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getTotalIva() {
        return totalIva;
    }

    public void setTotalIva(BigDecimal totalIva) {
        this.totalIva = totalIva;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "Venta{" +
                "idVenta=" + idVenta +
                ", tipoVenta=" + tipoVenta +
                ", fecha=" + fecha +
                ", total=" + total +
                '}';
    }
}