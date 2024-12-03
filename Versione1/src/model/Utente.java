package model;

import java.io.*;

/**
 * Classe che rappresenta un utente del sistema.
 * Un utente è costituito da un username e da una password.
 * Un utente può essere un utente normale o un configuratore.
 */
public class Utente implements Serializable {
    private static final long serialVersionUID = 1L;
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
     * Verifica se l'utente è un Configuratore.
     *
     * @return true se l'utente è un Configuratore, false altrimenti
     */
    public boolean isConfig() {
        return isConfig;
    }

    /**
     * Verifica se lo username e la password specificati corrispondono a quelli dell'utente.
     *
     * @param username lo username da verificare
     * @param password la password da verificare
     * @return true se lo username e la password corrispondono a quelli dell'utente, false altrimenti
     */
    public boolean verificaCredenziali(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    /**
     * Scrive l'oggetto Utente in un flusso di output.
     *
     * @param out il flusso di output
     * @throws IOException se si verifica un errore di I/O
     */
    @Serial
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    /**
     * Legge l'oggetto Utente da un flusso di input.
     *
     * @param in il flusso di input
     * @throws IOException se si verifica un errore di I/O
     * @throws ClassNotFoundException se la classe non viene trovata
     */
    @Serial
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }

    /**
     * Restituisce una rappresentazione in stringa dell'utente.
     * La stringa include lo username e i permessi dell'utente.
     *
     * @return una rappresentazione in stringa dell'utente
     */
    @Override
    public String toString() {
        return "Username: " + username + "\n" + "Permessi: " + (isConfig ? "Configuratore" : "Fruitore");
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
        return this.username.equalsIgnoreCase(utente.getUsername());
    }

    /**
     * Restituisce un codice hash per questo utente.
     *
     * @return un codice hash per questo utente
     */
    @Override
    public int hashCode() {
        return this.username.hashCode();
    }
}