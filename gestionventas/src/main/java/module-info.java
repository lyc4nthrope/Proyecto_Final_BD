module com.uniquindio.crisdav.gestionventas {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires transitive javafx.graphics;

    opens com.uniquindio.crisdav.gestionventas to javafx.fxml;
    exports com.uniquindio.crisdav.gestionventas;
}
