package com.uniquindio.crisdav.gestionventas.models.enums;

public enum TipoVenta {
    CONTADO("Contado"),
    CREDITO("Credito");

    private final String valor;

    TipoVenta(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public static TipoVenta fromString(String text) {
        for (TipoVenta tipo : TipoVenta.values()) {
            if (tipo.valor.equalsIgnoreCase(text)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("No existe el tipo de venta: " + text);
    }

    @Override
    public String toString() {
        return valor;
    }
}