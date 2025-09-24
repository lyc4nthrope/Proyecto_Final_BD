package com.uniquindio.crisdav.gestionventas.controllers;

import com.uniquindio.crisdav.gestionventas.models.entity.Usuario;

public class AppSession {
    private static Usuario currentUser;

    private AppSession() {}

    public static Usuario getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(Usuario user) {
        currentUser = user;
    }

    public static void clear() {
        currentUser = null;
    }
}

