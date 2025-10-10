package com.uniquindio.crisdav.gestionventas.models.vo;

import java.math.BigDecimal;

public class InventarioVO {
    private String categoria;
    private String codigoProducto;
    private String nombreProducto;
    private Integer stock;
    private BigDecimal valorAdquisicion;
    private BigDecimal costoTotalCategoria;

    public InventarioVO() {}

    public InventarioVO(String categoria, String codigoProducto, String nombreProducto, 
                        Integer stock, BigDecimal valorAdquisicion) {
        this.categoria = categoria;
        this.codigoProducto = codigoProducto;
        this.nombreProducto = nombreProducto;
        this.stock = stock;
        this.valorAdquisicion = valorAdquisicion;
        this.costoTotalCategoria = valorAdquisicion.multiply(BigDecimal.valueOf(stock));
    }

    // Getters y Setters
    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public BigDecimal getValorAdquisicion() {
        return valorAdquisicion;
    }

    public void setValorAdquisicion(BigDecimal valorAdquisicion) {
        this.valorAdquisicion = valorAdquisicion;
    }

    public BigDecimal getCostoTotalCategoria() {
        return costoTotalCategoria;
    }

    public void setCostoTotalCategoria(BigDecimal costoTotalCategoria) {
        this.costoTotalCategoria = costoTotalCategoria;
    }
}