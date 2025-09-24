package com.uniquindio.crisdav.gestionventas.models.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Producto implements Serializable {
    private Long id;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private Long createdByUserId;
    private Long updatedByUserId;

    private String codigo;
    private String nombre;
    private Long categoriaId;      // FK
    private Categoria categoria;   // referencia opcional
    private BigDecimal costoAdquisicion;
    private BigDecimal precioVenta;
    private Integer stock;
    private String descripcion;

    public Producto() {
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

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public BigDecimal getCostoAdquisicion() { return costoAdquisicion; }
    public void setCostoAdquisicion(BigDecimal costoAdquisicion) { this.costoAdquisicion = costoAdquisicion; }

    public BigDecimal getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(BigDecimal precioVenta) { this.precioVenta = precioVenta; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    @Override
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", categoriaId=" + categoriaId +
                ", costoAdquisicion=" + costoAdquisicion +
                ", precioVenta=" + precioVenta +
                ", stock=" + stock +
                '}';
    }
}
