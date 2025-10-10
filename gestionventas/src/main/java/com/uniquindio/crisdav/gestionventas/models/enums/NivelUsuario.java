package com.uniquindio.crisdav.gestionventas.models.enums;

public enum NivelUsuario {
    ADMINISTRADOR("Administrador"),
    PARAMETRICO("Parametrico"),
    ESPORADICO("Esporadico");

    private final String valor;

    NivelUsuario(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public static NivelUsuario fromString(String text) {
        for (NivelUsuario nivel : NivelUsuario.values()) {
            if (nivel.valor.equalsIgnoreCase(text)) {
                return nivel;
            }
        }
        throw new IllegalArgumentException("No existe el nivel: " + text);
    }

    @Override
    public String toString() {
        return valor;
    }
}