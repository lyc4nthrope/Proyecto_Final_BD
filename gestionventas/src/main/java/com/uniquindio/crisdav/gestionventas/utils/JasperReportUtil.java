package com.uniquindio.crisdav.gestionventas.utils;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.engine.util.JRLoader;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Collection;
import java.util.Map;

/**
 * Utilidad para generar reportes PDF con JasperReports
 */
public final class JasperReportUtil {
    
    private JasperReportUtil() {}
    
    /**
     * Genera un PDF desde un archivo .jrxml usando una conexión SQL
     * 
     * @param jrxmlPath Ruta al archivo .jrxml (en resources)
     * @param parameters Parámetros del reporte
     * @param connection Conexión a la base de datos
     * @param outputPath Ruta donde guardar el PDF
     * @return true si se generó exitosamente
     */
    public static boolean generarPDFDesdeSQL(String jrxmlPath, Map<String, Object> parameters, 
                                            Connection connection, String outputPath) {
        try {
            // Cargar el diseño del reporte
            InputStream inputStream = JasperReportUtil.class.getResourceAsStream(jrxmlPath);
            if (inputStream == null) {
                throw new RuntimeException("No se encontró el archivo: " + jrxmlPath);
            }
            
            JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
            
            // Compilar el reporte
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
            
            // Llenar el reporte con datos de la BD
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                jasperReport, 
                parameters, 
                connection
            );
            
            // Exportar a PDF
            JasperExportManager.exportReportToPdfFile(jasperPrint, outputPath);
            
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Genera un PDF desde un archivo .jrxml usando una colección de objetos Java
     * 
     * @param jrxmlPath Ruta al archivo .jrxml (en resources)
     * @param parameters Parámetros del reporte
     * @param dataSource Colección de objetos para el reporte
     * @param outputPath Ruta donde guardar el PDF
     * @return true si se generó exitosamente
     */
    public static boolean generarPDFDesdeColeccion(String jrxmlPath, Map<String, Object> parameters, 
                                                   Collection<?> dataSource, String outputPath) {
        try {
            // Cargar el diseño del reporte
            InputStream inputStream = JasperReportUtil.class.getResourceAsStream(jrxmlPath);
            if (inputStream == null) {
                throw new RuntimeException("No se encontró el archivo: " + jrxmlPath);
            }
            
            JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
            
            // Compilar el reporte
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
            
            // Crear data source desde la colección
            JRBeanCollectionDataSource jrDataSource = new JRBeanCollectionDataSource(dataSource);
            
            // Llenar el reporte
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                jasperReport, 
                parameters, 
                jrDataSource
            );
            
            // Exportar a PDF
            JasperExportManager.exportReportToPdfFile(jasperPrint, outputPath);
            
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Genera un PDF desde un archivo .jasper ya compilado
     * Útil cuando ya tienes el .jasper precompilado
     */
    public static boolean generarPDFDesdeJasper(String jasperPath, Map<String, Object> parameters, 
                                                Connection connection, String outputPath) {
        try {
            InputStream inputStream = JasperReportUtil.class.getResourceAsStream(jasperPath);
            if (inputStream == null) {
                throw new RuntimeException("No se encontró el archivo: " + jasperPath);
            }
            
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(inputStream);
            
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                jasperReport, 
                parameters, 
                connection
            );
            
            JasperExportManager.exportReportToPdfFile(jasperPrint, outputPath);
            
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Obtiene la ruta de Documentos del usuario
     */
    public static String getRutaDocumentos() {
        String userHome = System.getProperty("user.home");
        return userHome + File.separator + "Documents" + File.separator + "GestionVentas";
    }
    
    /**
     * Crea el directorio de reportes si no existe
     */
    public static void crearDirectorioReportes() {
        File directorio = new File(getRutaDocumentos());
        if (!directorio.exists()) {
            directorio.mkdirs();
        }
    }
    
    /**
     * Genera un nombre de archivo único para el PDF
     */
    public static String generarNombreArchivo(String prefijo) {
        String timestamp = java.time.LocalDateTime.now()
            .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return prefijo + "_" + timestamp + ".pdf";
    }
}