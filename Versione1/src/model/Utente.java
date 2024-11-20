package model;

/**
 * Classe che rappresenta un utente del sistema
 * Un utente è costituito da un username e da una password
 * Un utente può essere un utente normale o un configuratore
 */
public class Utente {
    private String username;
    private String password;
    private boolean isConfig=false;

    public Utente(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Utente(String username, String password, boolean isConfig) {
        this.username = username;
        this.password = password;
        this.isConfig = isConfig;
    }

    public boolean isConfig() {
        return isConfig;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "username: " + username + " " + "permessi: " + (isConfig ? "configuratore" : "utente");
    }
}