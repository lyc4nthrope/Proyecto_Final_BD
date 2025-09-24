package com.uniquindio.crisdav.gestionventas.models.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Categoria implements Serializable {
    private Long id;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private Long createdByUserId;
    private Long updatedByUserId;

    private String nombre;
    private BigDecimal iva;      // ejemplo 0.16
    private BigDecimal utilidad; // ejemplo 0.35 = 35%

    public Categoria() {
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
    }

    public Categoria(Long id, String nombre, BigDecimal iva, BigDecimal utilidad) {
        this.id = id;
        this.nombre = nombre;
        this.iva = iva;
        this.utilidad = utilidad;
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
    }

    // getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    public LocalDate getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDate updatedAt) { this.updatedAt = updatedAt; }

    public Long getCreatedByUserId() { return createdByUserId; }
    public void setCreatedByUserId(Long createdByUserId) { this.createdByUserId = createdByUserId; }

    public Long getUpdatedByUserId() { return updatedByUserId; }
    public void setUpdatedByUserId(Long updatedByUserId) { this.updatedByUserId = updatedByUserId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public BigDecimal getIva() { return iva; }
    public void setIva(BigDecimal iva) { this.iva = iva; }

    public BigDecimal getUtilidad() { return utilidad; }
    public void setUtilidad(BigDecimal utilidad) { this.utilidad = utilidad; }

    @Override
    public String toString() {
        return "Categoria{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", iva=" + iva +
                ", utilidad=" + utilidad +
                '}';
    }
}
