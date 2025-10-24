package com.uniquindio.crisdav.gestionventas.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Utilidad para formatear datos para mostrar en la UI
 */
public final class FormatoUtil {
    
    private FormatoUtil() {}
    
    // Formateadores
    private static final NumberFormat FORMATO_MONEDA = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
    private static final DecimalFormat FORMATO_DECIMAL = new DecimalFormat("#,##0.00");
    private static final DecimalFormat FORMATO_PORCENTAJE = new DecimalFormat("#0.00%");
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter FORMATO_FECHA_CORTA = DateTimeFormatter.ofPattern("dd/MM/yy");
    
    /**
     * Formatea un BigDecimal como moneda colombiana
     * Ejemplo: $1,500,000.00
     */
    public static String formatearMoneda(BigDecimal monto) {
        if (monto == null) {
            return "$0.00";
        }
        return FORMATO_MONEDA.format(monto);
    }
    
    /**
     * Formatea un BigDecimal como decimal con separadores de miles
     * Ejemplo: 1,500.50
     */
    public static String formatearDecimal(BigDecimal numero) {
        if (numero == null) {
            return "0.00";
        }
        return FORMATO_DECIMAL.format(numero);
    }
    
    /**
     * Formatea un BigDecimal como porcentaje
     * Ejemplo: 0.16 -> 16.00%
     */
    public static String formatearPorcentaje(BigDecimal porcentaje) {
        if (porcentaje == null) {
            return "0.00%";
        }
        return FORMATO_PORCENTAJE.format(porcentaje);
    }
    
    /**
     * Formatea un Integer con separadores de miles
     * Ejemplo: 1500 -> 1,500
     */
    public static String formatearNumero(Integer numero) {
        if (numero == null) {
            return "0";
        }
        return String.format("%,d", numero);
    }
    
    /**
     * Formatea una fecha en formato DD/MM/YYYY
     * Ejemplo: 15/10/2024
     */
    public static String formatearFecha(LocalDate fecha) {
        if (fecha == null) {
            return "";
        }
        return fecha.format(FORMATO_FECHA);
    }
    
    /**
     * Formatea una fecha y hora en formato DD/MM/YYYY HH:MM:SS
     * Ejemplo: 15/10/2024 14:30:45
     */
    public static String formatearFechaHora(LocalDateTime fechaHora) {
        if (fechaHora == null) {
            return "";
        }
        return fechaHora.format(FORMATO_FECHA_HORA);
    }
    
    /**
     * Formatea una fecha en formato corto DD/MM/YY
     * Ejemplo: 15/10/24
     */
    public static String formatearFechaCorta(LocalDate fecha) {
        if (fecha == null) {
            return "";
        }
        return fecha.format(FORMATO_FECHA_CORTA);
    }

    /**
     * Formatea una fecha en formato sin espacios DDMMYY
     * Ejemplo: 151024
     */
    public static String formatearFechaSinEspacios(LocalDate fecha) {
        if (fecha == null) {
            return "";
        }
        DateTimeFormatter formatoSinEspacios = DateTimeFormatter.ofPattern("ddMMyy");
        return fecha.format(formatoSinEspacios);
    }

    /**
     * Parsea una fecha desde String en formato DD/MM/YYYY
     */
    public static LocalDate parsearFecha(String fecha) {
        if (fecha == null || fecha.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(fecha, FORMATO_FECHA);
        } catch (Exception e) {
            throw new IllegalArgumentException("Formato de fecha inválido. Use DD/MM/YYYY");
        }
    }
    
    /**
     * Parsea un monto desde String (elimina símbolos de moneda y separadores)
     */
    public static BigDecimal parsearMoneda(String monto) {
        if (monto == null || monto.isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            // Eliminar símbolo de moneda y espacios
            String limpio = monto.replaceAll("[^0-9.,-]", "").trim();

            // Si el formato usa punto como separador de miles y coma como decimal,
            // eliminamos los puntos y reemplazamos la coma por punto.
            if (limpio.contains(",") && limpio.contains(".")) {
                // Asumir formato "1.234.567,89"
                limpio = limpio.replace(".", "").replace(",", ".");
            } else if (limpio.contains(",")) {
                // Asumir formato "1234,56"
                limpio = limpio.replace(",", ".");
            }

            return new BigDecimal(limpio);
        } catch (Exception e) {
            throw new IllegalArgumentException("Formato de monto inválido: " + monto, e);
        }
    }

    
    /**
     * Formatea un número de teléfono
     * Ejemplo: 3001234567 -> 300-123-4567
     */
    public static String formatearTelefono(String telefono) {
        if (telefono == null || telefono.isEmpty()) {
            return "";
        }
        
        String numeros = telefono.replaceAll("[^0-9]", "");
        
        if (numeros.length() == 10) {
            return numeros.substring(0, 3) + "-" + 
                   numeros.substring(3, 6) + "-" + 
                   numeros.substring(6);
        } else if (numeros.length() == 7) {
            return numeros.substring(0, 3) + "-" + 
                   numeros.substring(3);
        }
        
        return telefono;
    }
    
    /**
     * Formatea una cédula con separadores
     * Ejemplo: 1234567890 -> 1.234.567.890
     */
    public static String formatearCedula(String cedula) {
        if (cedula == null || cedula.isEmpty()) {
            return "";
        }
        
        String numeros = cedula.replaceAll("[^0-9]", "");
        
        if (numeros.length() <= 3) {
            return numeros;
        }
        
        StringBuilder resultado = new StringBuilder();
        int contador = 0;
        
        for (int i = numeros.length() - 1; i >= 0; i--) {
            if (contador > 0 && contador % 3 == 0) {
                resultado.insert(0, ".");
            }
            resultado.insert(0, numeros.charAt(i));
            contador++;
        }
        
        return resultado.toString();
    }
    
    /**
     * Formatea texto para título (Primera Letra Mayúscula)
     */
    public static String formatearTitulo(String texto) {
        if (texto == null || texto.isEmpty()) {
            return "";
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
    
    /**
     * Formatea texto para mayúsculas
     */
    public static String formatearMayusculas(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.toUpperCase();
    }
    
    /**
     * Formatea texto para minúsculas
     */
    public static String formatearMinusculas(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.toLowerCase();
    }
    
    /**
     * Trunca un texto a una longitud máxima
     * Ejemplo: "Texto largo" -> "Texto lar..."
     */
    public static String truncar(String texto, int longitudMaxima) {
        if (texto == null || texto.isEmpty()) {
            return "";
        }
        
        if (texto.length() <= longitudMaxima) {
            return texto;
        }
        
        return texto.substring(0, longitudMaxima - 3) + "...";
    }
    
    /**
     * Formatea un booleano como Sí/No
     */
    public static String formatearBoolean(Boolean valor) {
        if (valor == null) {
            return "No";
        }
        return valor ? "Sí" : "No";
    }
    
    /**
     * Formatea un booleano como Activo/Inactivo
     */
    public static String formatearEstado(Boolean valor) {
        if (valor == null) {
            return "Inactivo";
        }
        return valor ? "Activo" : "Inactivo";
    }
    
    /**
     * Calcula y formatea el tiempo transcurrido desde una fecha
     * Ejemplo: "Hace 3 días", "Hace 2 meses"
     */
    public static String tiempoTranscurrido(LocalDate fechaPasada) {
        if (fechaPasada == null) {
            return "";
        }
        
        LocalDate hoy = LocalDate.now();
        long dias = java.time.temporal.ChronoUnit.DAYS.between(fechaPasada, hoy);
        
        if (dias == 0) {
            return "Hoy";
        } else if (dias == 1) {
            return "Ayer";
        } else if (dias < 7) {
            return "Hace " + dias + " días";
        } else if (dias < 30) {
            long semanas = dias / 7;
            return "Hace " + semanas + (semanas == 1 ? " semana" : " semanas");
        } else if (dias < 365) {
            long meses = dias / 30;
            return "Hace " + meses + (meses == 1 ? " mes" : " meses");
        } else {
            long años = dias / 365;
            return "Hace " + años + (años == 1 ? " año" : " años");
        }
    }
    
    /**
     * Calcula y formatea el tiempo restante hasta una fecha futura
     * Ejemplo: "En 3 días", "En 2 meses"
     */
    public static String tiempoRestante(LocalDate fechaFutura) {
        if (fechaFutura == null) {
            return "";
        }
        
        LocalDate hoy = LocalDate.now();
        
        if (fechaFutura.isBefore(hoy)) {
            return "Vencido";
        }
        
        long dias = java.time.temporal.ChronoUnit.DAYS.between(hoy, fechaFutura);
        
        if (dias == 0) {
            return "Hoy";
        } else if (dias == 1) {
            return "Mañana";
        } else if (dias < 7) {
            return "En " + dias + " días";
        } else if (dias < 30) {
            long semanas = dias / 7;
            return "En " + semanas + (semanas == 1 ? " semana" : " semanas");
        } else if (dias < 365) {
            long meses = dias / 30;
            return "En " + meses + (meses == 1 ? " mes" : " meses");
        } else {
            long años = dias / 365;
            return "En " + años + (años == 1 ? " año" : " años");
        }
    }
    
    /**
     * Formatea un ID con ceros a la izquierda
     * Ejemplo: 5 -> 00005 (ancho 5)
     */
    public static String formatearId(Integer id, int ancho) {
        if (id == null) {
            return "0".repeat(ancho);
        }
        return String.format("%0" + ancho + "d", id);
    }
    
    /**
     * Formatea un código de factura
     * Ejemplo: FAC-00001234
     */
    public static String formatearCodigoFactura(Integer idVenta) {
        if (idVenta == null) {
            return "FAC-00000000";
        }
        return "FAC-" + formatearId(idVenta, 8);
    }
    
    /**
     * Formatea el nombre de un mes
     */
    public static String nombreMes(int mes) {
        String[] meses = {
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        };
        
        if (mes < 1 || mes > 12) {
            return "";
        }
        
        return meses[mes - 1];
    }
    
    /**
     * Formatea el nombre de un trimestre
     */
    public static String nombreTrimestre(int trimestre) {
        if (trimestre < 1 || trimestre > 4) {
            return "";
        }
        return "T" + trimestre + " - " + nombreMes((trimestre - 1) * 3 + 1) + 
               " a " + nombreMes(trimestre * 3);
    }
}