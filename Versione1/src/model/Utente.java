package model;

import java.io.Serializable;

/**
 * Classe che rappresenta un utente del sistema.
 * Un utente è costituito da un username e da una password.
 * Un utente può essere un utente normale o un configuratore.
 */
public class Utente implements Serializable {
    private String username;
    private String password;
    private boolean isConfig = false;

    /**
     * Costruisce un Utente con lo username e la password specificati.
     *
     * @param username lo username dell'utente
     * @param password la password dell'utente
     */
    public Utente(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Costruisce un Utente con lo username, la password e il flag di configuratore specificati.
     *
     * @param username lo username dell'utente
     * @param password la password dell'utente
     * @param isConfig true se l'utente è un configuratore, false altrimenti
     */
    public Utente(String username, String password, boolean isConfig) {
        this.username = username;
        this.password = password;
        this.isConfig = isConfig;
    }

    /**
     * Verifica se l'utente è un configuratore.
     *
     * @return true se l'utente è un configuratore, false altrimenti
     */
    public boolean isConfig() {
        return isConfig;
    }

    /**
     * Restituisce lo username dell'utente.
     *
     * @return lo username dell'utente
     */
    public String getUsername() {
        return username;
    }

    /**
     * Imposta lo username dell'utente.
     *
     * @param username il nuovo username dell'utente
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Restituisce una rappresentazione in stringa dell'utente.
     * La stringa include lo username e i permessi dell'utente.
     *
     * @return una rappresentazione in stringa dell'utente
     */
    @Override
    public String toString() {
        return "username: " + username + " " + "permessi: " + (isConfig ? "configuratore" : "utente");
    }

    /**
     * Confronta questo utente con l'oggetto specificato.
     * Il risultato è true se e solo se l'argomento non è null ed è un oggetto Utente che ha lo stesso username di questo oggetto.
     *
     * @param obj l'oggetto da confrontare con questo Utente
     * @return true se l'oggetto è uguale a questo Utente, false altrimenti
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Utente utente = (Utente) obj;
        return username.equals(utente.username);
    }
}