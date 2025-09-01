package com.uniquindio.crisdav.gestionventas.model;

/**
 * Entidad Producto: representa la tabla PRODUCTO en la base de datos.
 */
public class Producto {
    private int idProducto;
    private String nombre;
    private String categoria;
    private double valorAdquisicion;
    private double valorVenta;
    private double iva;
    private double utilidad;

    public Producto() {}

    public Producto(int idProducto, String nombre, String categoria, double valorAdquisicion, double valorVenta, double iva, double utilidad) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.categoria = categoria;
        this.valorAdquisicion = valorAdquisicion;
        this.valorVenta = valorVenta;
        this.iva = iva;
        this.utilidad = utilidad;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getValorAdquisicion() {
        return valorAdquisicion;
    }

    public void setValorAdquisicion(double valorAdquisicion) {
        this.valorAdquisicion = valorAdquisicion;
    }

    public double getValorVenta() {
        return valorVenta;
    }

    public void setValorVenta(double valorVenta) {
        this.valorVenta = valorVenta;
    }

    public double getIva() {
        return iva;
    }

    public void setIva(double iva) {
        this.iva = iva;
    }

    public double getUtilidad() {
        return utilidad;
    }

    public void setUtilidad(double utilidad) {
        this.utilidad = utilidad;
    }

    @Override
    public String toString() {
        return nombre + " (" + categoria + ")";
    }
}
