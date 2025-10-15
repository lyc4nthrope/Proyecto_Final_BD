package com.uniquindio.crisdav.gestionventas.models.dto;

import java.math.BigDecimal;

public class CategoriaFormResult {
    String nombre;
    BigDecimal iva;
    BigDecimal utilidad;

    public CategoriaFormResult(String nombre, BigDecimal iva, BigDecimal utilidad) {
        this.nombre = nombre;
        this.iva = iva;
        this.utilidad = utilidad;
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
    
}
