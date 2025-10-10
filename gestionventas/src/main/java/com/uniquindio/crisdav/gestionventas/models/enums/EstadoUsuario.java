package com.uniquindio.crisdav.gestionventas.models.enums;

public enum EstadoUsuario {
    ACTIVO("Activo"),
    INACTIVO("Inactivo");

    private final String valor;

    EstadoUsuario(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public static EstadoUsuario fromString(String text) {
        for (EstadoUsuario estado : EstadoUsuario.values()) {
            if (estado.valor.equalsIgnoreCase(text)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("No existe el estado: " + text);
    }

    @Override
    public String toString() {
        return valor;
    }
}