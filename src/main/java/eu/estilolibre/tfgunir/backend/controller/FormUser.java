package eu.estilolibre.tfgunir.backend.controller;

public class FormUser {
    private String email;
    private String password;
    

    public FormUser() {
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "FormUser [email=" + email + ", password=" + password + "]";
    }
    

}