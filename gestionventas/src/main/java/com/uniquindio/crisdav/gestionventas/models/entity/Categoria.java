package com.uniquindio.crisdav.gestionventas.models.entity;

import java.math.BigDecimal;

public class Categoria {
    private Integer idCategoria;
    private String nombre;
    private BigDecimal iva;
    private BigDecimal utilidad;

    public Categoria() {}

    public Categoria(String nombre, BigDecimal iva, BigDecimal utilidad) {
        this.nombre = nombre;
        this.iva = iva;
        this.utilidad = utilidad;
    }

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getIva() {
        return iva;
    }

    public void setIva(BigDecimal iva) {
        this.iva = iva;
    }

    public BigDecimal getUtilidad() {
        return utilidad;
    }

    public void setUtilidad(BigDecimal utilidad) {
        this.utilidad = utilidad;
    }

    @Override
    public String toString() {
        return "Categoria{" +
                "idCategoria=" + idCategoria +
                ", nombre='" + nombre + '\'' +
                ", iva=" + iva +
                ", utilidad=" + utilidad +
                '}';
    }
}