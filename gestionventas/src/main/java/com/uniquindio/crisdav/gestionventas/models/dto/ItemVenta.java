package com.uniquindio.crisdav.gestionventas.models.dto;

import java.math.BigDecimal;
/**
 * Clase auxiliar para items del carrito de compra
 */
public class ItemVenta {
    private Integer idProducto;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal iva;

    public ItemVenta(Integer idProducto, Integer cantidad, BigDecimal precioUnitario, BigDecimal iva) {
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.iva = iva;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public BigDecimal getIva() {
        return iva;
    }
}
