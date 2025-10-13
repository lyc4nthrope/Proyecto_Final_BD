module com.uniquindio.crisdav.gestionventas {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires transitive javafx.graphics;

    opens com.uniquindio.crisdav.gestionventas to javafx.fxml;
    exports com.uniquindio.crisdav.gestionventas;
    exports com.uniquindio.crisdav.gestionventas.controllers.views;
    opens com.uniquindio.crisdav.gestionventas.controllers.views to javafx.fxml;
    opens com.uniquindio.crisdav.gestionventas.models.entity to javafx.base, javafx.fxml;
    opens com.uniquindio.crisdav.gestionventas.models.enums to javafx.base, javafx.fxml;
    opens com.uniquindio.crisdav.gestionventas.utils to javafx.base, javafx.fxml;
    opens com.uniquindio.crisdav.gestionventas.dao to javafx.base, javafx.fxml;
    opens com.uniquindio.crisdav.gestionventas.controllers to javafx.base, javafx.fxml;
    opens com.uniquindio.crisdav.gestionventas.models.vo to javafx.base, javafx.fxml;

}
