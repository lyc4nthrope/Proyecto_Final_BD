package com.uniquindio.crisdav.gestionventas.models.vo;

import java.math.BigDecimal;

public class ReporteIvaVO {
    private String categoria;
    private BigDecimal totalVentasCategoria;
    private BigDecimal ivaCategoria;
    private BigDecimal totalIvaCategoria;

    public ReporteIvaVO() {}

    public ReporteIvaVO(String categoria, BigDecimal totalVentasCategoria, 
                        BigDecimal ivaCategoria, BigDecimal totalIvaCategoria) {
        this.categoria = categoria;
        this.totalVentasCategoria = totalVentasCategoria;
        this.ivaCategoria = ivaCategoria;
        this.totalIvaCategoria = totalIvaCategoria;
    }

    // Getters y Setters
    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getTotalVentasCategoria() {
        return totalVentasCategoria;
    }

    public void setTotalVentasCategoria(BigDecimal totalVentasCategoria) {
        this.totalVentasCategoria = totalVentasCategoria;
    }

    public BigDecimal getIvaCategoria() {
        return ivaCategoria;
    }

    public void setIvaCategoria(BigDecimal ivaCategoria) {
        this.ivaCategoria = ivaCategoria;
    }

    public BigDecimal getTotalIvaCategoria() {
        return totalIvaCategoria;
    }

    public void setTotalIvaCategoria(BigDecimal totalIvaCategoria) {
        this.totalIvaCategoria = totalIvaCategoria;
    }
}