module com.uniquindio.crisdav.gestionventas {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.uniquindio.crisdav.gestionventas to javafx.fxml;
    exports com.uniquindio.crisdav.gestionventas;
}