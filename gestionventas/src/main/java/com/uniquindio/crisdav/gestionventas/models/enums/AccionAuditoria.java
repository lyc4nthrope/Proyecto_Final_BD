package com.uniquindio.crisdav.gestionventas.models.enums;

public enum AccionAuditoria {
    LOGIN("Login"),
    LOGOUT("Logout");

    private final String valor;

    AccionAuditoria(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public static AccionAuditoria fromString(String text) {
        for (AccionAuditoria accion : AccionAuditoria.values()) {
            if (accion.valor.equalsIgnoreCase(text)) {
                return accion;
            }
        }
        throw new IllegalArgumentException("No existe la acción: " + text);
    }

    @Override
    public String toString() {
        return valor;
    }
}