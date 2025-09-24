module com.uniquindio.crisdav.gestionventas {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires transitive javafx.graphics;

    opens com.uniquindio.crisdav.gestionventas to javafx.fxml;
    exports com.uniquindio.crisdav.gestionventas;
    exports com.uniquindio.crisdav.gestionventas.controllers to javafx.fxml;
    opens com.uniquindio.crisdav.gestionventas.controllers to javafx.fxml;
    exports com.uniquindio.crisdav.gestionventas.models.entity;
    exports com.uniquindio.crisdav.gestionventas.models.enums;
    opens com.uniquindio.crisdav.gestionventas.models.entity to javafx.fxml;
    opens com.uniquindio.crisdav.gestionventas.models.enums to javafx.fxml;
}
