package com.uniquindio.crisdav.gestionventas.model;

/**
 * Entidad DetalleVenta: representa la tabla DETALLEVENTA en la base de datos.
 */
public class DetalleVenta {
    private int idVenta;
    private int idProducto;
    private int cantidad;
    private double subtotal;

    public DetalleVenta() {}

    public DetalleVenta(int idVenta, int idProducto, int cantidad, double subtotal) {
        this.idVenta = idVenta;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
}
