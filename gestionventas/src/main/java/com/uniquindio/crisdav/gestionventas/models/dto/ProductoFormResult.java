package com.uniquindio.crisdav.gestionventas.models.dto;

import java.math.BigDecimal;

public class ProductoFormResult {
    String codigo;
    String nombre;
    Integer idCategoria;
    BigDecimal valorAdquisicion;
    BigDecimal valorVenta;
    Integer stock;

    public ProductoFormResult(String codigo, String nombre, Integer idCategoria,
            BigDecimal valorAdquisicion, BigDecimal valorVenta, Integer stock) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.idCategoria = idCategoria;
        this.valorAdquisicion = valorAdquisicion;
        this.valorVenta = valorVenta;
        this.stock = stock;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    public BigDecimal getValorAdquisicion() {
        return valorAdquisicion;
    }

    public void setValorAdquisicion(BigDecimal valorAdquisicion) {
        this.valorAdquisicion = valorAdquisicion;
    }

    public BigDecimal getValorVenta() {
        return valorVenta;
    }

    public void setValorVenta(BigDecimal valorVenta) {
        this.valorVenta = valorVenta;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
    
}
