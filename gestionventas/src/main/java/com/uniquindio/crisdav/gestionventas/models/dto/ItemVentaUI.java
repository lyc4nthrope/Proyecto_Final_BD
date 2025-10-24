package com.uniquindio.crisdav.gestionventas.models.dto;
import java.math.BigDecimal;

public class ItemVentaUI {
    private Integer idProducto;
    private String codigo;
    private String nombre;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal iva;
    private BigDecimal subtotal;

    public ItemVentaUI(Integer idProducto, String codigo, String nombre, Integer cantidad,
            BigDecimal precioUnitario, BigDecimal iva) {
        this.idProducto = idProducto;
        this.codigo = codigo;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.iva = iva;
        calcularSubtotal();
    }

    public void calcularSubtotal() {
        this.subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }

    // Getters y Setters
    public Integer getIdProducto() {
        return idProducto;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public BigDecimal getIva() {
        return iva;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }
}
