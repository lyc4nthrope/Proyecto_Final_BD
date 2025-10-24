package com.uniquindio.crisdav.gestionventas.models.dto;

// Clase auxiliar para el ComboBox
public class VendedorComboItem {
    private Integer id;
    private String nombre;
    
    public VendedorComboItem(Integer id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
    
    public Integer getId() {
        return id;
    }
    
    @Override
    public String toString() {
        return nombre;
    }
}
