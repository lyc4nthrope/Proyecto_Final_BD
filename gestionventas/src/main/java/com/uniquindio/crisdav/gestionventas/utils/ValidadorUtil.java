package com.uniquindio.crisdav.gestionventas.utils;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Utilidad para validaciones comunes en la aplicación
 */
public final class ValidadorUtil {
    
    private ValidadorUtil() {}
    
    // Patrones de validación
    private static final Pattern PATTERN_EMAIL = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    private static final Pattern PATTERN_TELEFONO = Pattern.compile(
        "^\\d{7,10}$"
    );
    
    private static final Pattern PATTERN_CEDULA = Pattern.compile(
        "^\\d{6,10}$"
    );
    
    /**
     * Valida que una cadena no sea null ni esté vacía
     */
    public static boolean noEsVacio(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }
    
    /**
     * Valida que una cadena no sea null ni esté vacía (lanza excepción)
     */
    public static void validarNoVacio(String texto, String nombreCampo) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("El campo '" + nombreCampo + "' no puede estar vacío");
        }
    }
    
    /**
     * Valida formato de email
     */
    public static boolean esEmailValido(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return PATTERN_EMAIL.matcher(email).matches();
    }
    
    /**
     * Valida formato de email (lanza excepción)
     */
    public static void validarEmail(String email) {
        if (!esEmailValido(email)) {
            throw new IllegalArgumentException("El email no tiene un formato válido");
        }
    }
    
    /**
     * Valida formato de teléfono (7-10 dígitos)
     */
    public static boolean esTelefonoValido(String telefono) {
        if (telefono == null || telefono.isEmpty()) {
            return false;
        }
        return PATTERN_TELEFONO.matcher(telefono).matches();
    }
    
    /**
     * Valida formato de teléfono (lanza excepción)
     */
    public static void validarTelefono(String telefono) {
        if (!esTelefonoValido(telefono)) {
            throw new IllegalArgumentException("El teléfono debe tener entre 7 y 10 dígitos");
        }
    }
    
    /**
     * Valida formato de cédula (6-10 dígitos)
     */
    public static boolean esCedulaValida(String cedula) {
        if (cedula == null || cedula.isEmpty()) {
            return false;
        }
        return PATTERN_CEDULA.matcher(cedula).matches();
    }
    
    /**
     * Valida formato de cédula (lanza excepción)
     */
    public static void validarCedula(String cedula) {
        if (!esCedulaValida(cedula)) {
            throw new IllegalArgumentException("La cédula debe tener entre 6 y 10 dígitos");
        }
    }
    
    /**
     * Valida que un BigDecimal sea positivo
     */
    public static boolean esPositivo(BigDecimal valor) {
        return valor != null && valor.compareTo(BigDecimal.ZERO) > 0;
    }
    
    /**
     * Valida que un BigDecimal sea positivo (lanza excepción)
     */
    public static void validarPositivo(BigDecimal valor, String nombreCampo) {
        if (valor == null) {
            throw new IllegalArgumentException("El campo '" + nombreCampo + "' no puede ser null");
        }
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El campo '" + nombreCampo + "' debe ser mayor a cero");
        }
    }
    
    /**
     * Valida que un BigDecimal sea no negativo
     */
    public static boolean esNoNegativo(BigDecimal valor) {
        return valor != null && valor.compareTo(BigDecimal.ZERO) >= 0;
    }
    
    /**
     * Valida que un BigDecimal sea no negativo (lanza excepción)
     */
    public static void validarNoNegativo(BigDecimal valor, String nombreCampo) {
        if (valor == null) {
            throw new IllegalArgumentException("El campo '" + nombreCampo + "' no puede ser null");
        }
        if (valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El campo '" + nombreCampo + "' no puede ser negativo");
        }
    }
    
    /**
     * Valida que un Integer sea positivo
     */
    public static boolean esPositivo(Integer valor) {
        return valor != null && valor > 0;
    }
    
    /**
     * Valida que un Integer sea positivo (lanza excepción)
     */
    public static void validarPositivo(Integer valor, String nombreCampo) {
        if (valor == null) {
            throw new IllegalArgumentException("El campo '" + nombreCampo + "' no puede ser null");
        }
        if (valor <= 0) {
            throw new IllegalArgumentException("El campo '" + nombreCampo + "' debe ser mayor a cero");
        }
    }
    
    /**
     * Valida que un Integer sea no negativo
     */
    public static boolean esNoNegativo(Integer valor) {
        return valor != null && valor >= 0;
    }
    
    /**
     * Valida que un Integer sea no negativo (lanza excepción)
     */
    public static void validarNoNegativo(Integer valor, String nombreCampo) {
        if (valor == null) {
            throw new IllegalArgumentException("El campo '" + nombreCampo + "' no puede ser null");
        }
        if (valor < 0) {
            throw new IllegalArgumentException("El campo '" + nombreCampo + "' no puede ser negativo");
        }
    }
    
    /**
     * Valida longitud mínima de una cadena
     */
    public static boolean tieneLongitudMinima(String texto, int longitudMinima) {
        return texto != null && texto.length() >= longitudMinima;
    }
    
    /**
     * Valida longitud mínima de una cadena (lanza excepción)
     */
    public static void validarLongitudMinima(String texto, int longitudMinima, String nombreCampo) {
        if (texto == null || texto.length() < longitudMinima) {
            throw new IllegalArgumentException(
                "El campo '" + nombreCampo + "' debe tener al menos " + longitudMinima + " caracteres"
            );
        }
    }
    
    /**
     * Valida longitud máxima de una cadena
     */
    public static boolean tieneLongitudMaxima(String texto, int longitudMaxima) {
        return texto != null && texto.length() <= longitudMaxima;
    }
    
    /**
     * Valida longitud máxima de una cadena (lanza excepción)
     */
    public static void validarLongitudMaxima(String texto, int longitudMaxima, String nombreCampo) {
        if (texto != null && texto.length() > longitudMaxima) {
            throw new IllegalArgumentException(
                "El campo '" + nombreCampo + "' no puede tener más de " + longitudMaxima + " caracteres"
            );
        }
    }
    
    /**
     * Valida que una cadena solo contenga letras y espacios
     */
    public static boolean soloLetrasYEspacios(String texto) {
        if (texto == null || texto.isEmpty()) {
            return false;
        }
        return texto.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$");
    }
    
    /**
     * Valida que una cadena solo contenga dígitos
     */
    public static boolean soloDigitos(String texto) {
        if (texto == null || texto.isEmpty()) {
            return false;
        }
        return texto.matches("^\\d+$");
    }
    
    /**
     * Valida que una cadena sea alfanumérica
     */
    public static boolean esAlfanumerico(String texto) {
        if (texto == null || texto.isEmpty()) {
            return false;
        }
        return texto.matches("^[a-zA-Z0-9]+$");
    }
    
    /**
     * Valida rango de valores para BigDecimal
     */
    public static boolean estaEnRango(BigDecimal valor, BigDecimal minimo, BigDecimal maximo) {
        if (valor == null) {
            return false;
        }
        return valor.compareTo(minimo) >= 0 && valor.compareTo(maximo) <= 0;
    }
    
    /**
     * Valida rango de valores para Integer
     */
    public static boolean estaEnRango(Integer valor, int minimo, int maximo) {
        if (valor == null) {
            return false;
        }
        return valor >= minimo && valor <= maximo;
    }
    
    /**
     * Valida rango de valores para Integer (lanza excepción)
     */
    public static void validarRango(Integer valor, int minimo, int maximo, String nombreCampo) {
        if (valor == null) {
            throw new IllegalArgumentException("El campo '" + nombreCampo + "' no puede ser null");
        }
        if (valor < minimo || valor > maximo) {
            throw new IllegalArgumentException(
                "El campo '" + nombreCampo + "' debe estar entre " + minimo + " y " + maximo
            );
        }
    }
    
    /**
     * Valida que un objeto no sea null
     */
    public static void validarNoNull(Object objeto, String nombreCampo) {
        if (objeto == null) {
            throw new IllegalArgumentException("El campo '" + nombreCampo + "' no puede ser null");
        }
    }
    
    /**
     * Valida password con requisitos mínimos
     */
    public static boolean esPasswordValida(String password) {
        if (password == null || password.length() < 4) {
            return false;
        }
        // Puedes agregar más requisitos aquí si lo deseas
        return true;
    }
    
    /**
     * Valida password con requisitos mínimos (lanza excepción)
     */
    public static void validarPassword(String password) {
        if (password == null || password.length() < 4) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 4 caracteres");
        }
    }
    
    /**
     * Limpia y normaliza una cadena
     */
    public static String limpiar(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.trim();
    }
    
    /**
     * Capitaliza la primera letra de cada palabra
     */
    public static String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) {
            return texto;
        }
        
        String[] palabras = texto.toLowerCase().split("\\s+");
        StringBuilder resultado = new StringBuilder();
        
        for (String palabra : palabras) {
            if (!palabra.isEmpty()) {
                resultado.append(Character.toUpperCase(palabra.charAt(0)))
                         .append(palabra.substring(1))
                         .append(" ");
            }
        }
        
        return resultado.toString().trim();
    }
}