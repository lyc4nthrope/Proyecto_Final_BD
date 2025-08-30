package com.uniquindio.crisdav.gestionventas.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {

    // URL basada en tu configuración
    private static final String URL = "jdbc:oracle:thin:@//localhost:1521/XEPDB1";
    private static final String USER = "CRISTHIAN"; 
    private static final String PASSWORD = "osorio"; 

    public static Connection getConnection() throws SQLException {
        try {
            // Cargar el driver
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontró el driver JDBC de Oracle", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
