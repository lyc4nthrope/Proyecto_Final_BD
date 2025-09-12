package com.uniquindio.crisdav.gestionventas;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import com.uniquindio.crisdav.gestionventas.utils.DBUtil;

public class Launcher extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Intentar conectar a la base de datos antes de cargar la vista
        try (Connection conn = DBUtil.getConnection()) {
            if (conn != null) {
                System.out.println("✅ Conexión exitosa a Oracle!");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al conectar: " + e.getMessage());
            e.printStackTrace();
        }

        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("/com/uniquindio/crisdav/gestionventas/views/ClientesView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Gestion BD");
        stage.setScene(scene);
        stage.show();
    }
}
