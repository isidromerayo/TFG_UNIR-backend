package eu.estilolibre.tfgunir.backend.controller;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private long id;
    private String username;
    private String token;
    private String fullname;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public User() {
        // Constructor vacío requerido para serialización/deserialización JSON
    }

}
