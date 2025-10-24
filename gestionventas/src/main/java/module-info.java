open module com.uniquindio.crisdav.gestionventas {
    requires javafx.controls;
    requires javafx.fxml;
    requires jasperreports;
    requires java.desktop;

    requires transitive java.sql;
    requires transitive javafx.graphics;


    exports com.uniquindio.crisdav.gestionventas;
    exports com.uniquindio.crisdav.gestionventas.controllers.views;
    exports com.uniquindio.crisdav.gestionventas.controllers;
    exports com.uniquindio.crisdav.gestionventas.models.entity;
    exports com.uniquindio.crisdav.gestionventas.models.enums;
    exports com.uniquindio.crisdav.gestionventas.utils;
    exports com.uniquindio.crisdav.gestionventas.dao;
    exports com.uniquindio.crisdav.gestionventas.models.vo;
    exports com.uniquindio.crisdav.gestionventas.models.dto;
    
}
