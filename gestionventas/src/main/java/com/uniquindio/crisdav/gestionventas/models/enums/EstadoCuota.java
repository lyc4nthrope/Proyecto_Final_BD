package com.uniquindio.crisdav.gestionventas.models.enums;

public enum EstadoCuota {
    PENDIENTE("Pendiente"),
    PAGADA("Pagada"),
    VENCIDA("Vencida");

    private final String valor;

    EstadoCuota(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public static EstadoCuota fromString(String text) {
        for (EstadoCuota estado : EstadoCuota.values()) {
            if (estado.valor.equalsIgnoreCase(text)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("No existe el estado de cuota: " + text);
    }

    @Override
    public String toString() {
        return valor;
    }
}