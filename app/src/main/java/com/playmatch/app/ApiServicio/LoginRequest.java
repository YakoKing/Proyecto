package com.playmatch.app.ApiServicio;

public class LoginRequest {

    private String nombre;
    private String password;

    public LoginRequest(String nombre, String password) {
        this.nombre = nombre;
        this.password = password;
    }
}
