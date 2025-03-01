package model;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

/**
 * La classe Utente rappresenta un utente del sistema.
 * Ogni utente ha un username, una password protetta con hash e un salt casuale.
 * L'utente può essere un fruitore o un configuratore.
 * Implementa misure di sicurezza per la gestione delle password con hash
 * SHA-256 e salt.
 */
public class Utente implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String USERNAME_NON_NULL = "Username non può essere null";
    public static final String HASHING_NON_DISPONIBILE = "Algoritmo di hashing non disponibile";

    /** Nome utente univoco che identifica l'utente nel sistema. */
    private String username;

    /** Password dell'utente salvata come hash per sicurezza. */
    private String hashedPassword;

    /** Salt casuale usato per proteggere la password da attacchi rainbow table. */
    private byte[] salt;

    /**
     * Flag che indica se l'utente è un configuratore (true) o un fruitore (false).
     */
    private boolean isConfig = false;

    // -----------------------------------------------------------------------------------
    // COSTRUTTORI
    // -----------------------------------------------------------------------------------

    /**
     * Costruisce un utente fruitore con username e password specificati.
     * La password viene protetta con hash e salt.
     *
     * @param username nome utente univoco
     * @param password password in chiaro (sarà salvata come hash)
     */
    public Utente(String username, String password) {
        this(username, password, false);
    }

    /**
     * Costruisce un utente con username, password e tipo specificati.
     * La password viene protetta con hash e salt.
     *
     * @param username nome utente univoco
     * @param password password in chiaro (sarà salvata come hash)
     * @param isConfig true se l'utente è un configuratore, false se è un fruitore
     */
    public Utente(String username, String password, boolean isConfig) {
        this.username = Objects.requireNonNull(username, USERNAME_NON_NULL);
        this.salt = generateSalt();
        this.hashedPassword = hashPassword(password, this.salt);
        this.isConfig = isConfig;
    }

    // -----------------------------------------------------------------------------------
    // GETTER E SETTER
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce il nome utente.
     *
     * @return il nome utente
     */
    public String getUsername() {
        return username;
    }

    /**
     * Imposta un nuovo nome utente.
     *
     * @param username il nuovo nome utente
     */
    public void setUsername(String username) {
        this.username = Objects.requireNonNull(username, USERNAME_NON_NULL);
    }

    /**
     * Verifica se l'utente è un configuratore.
     *
     * @return true se l'utente è un configuratore, false se è un fruitore
     */
    public boolean isConfig() {
        return isConfig;
    }

    // -----------------------------------------------------------------------------------
    // METODI DI AUTENTICAZIONE
    // -----------------------------------------------------------------------------------

    /**
     * Verifica le credenziali dell'utente.
     * Confronta lo username fornito con quello dell'utente e
     * applica l'hashing alla password fornita per confrontarla con quella salvata.
     *
     * @param username nome utente da verificare
     * @param password password da verificare
     * @return true se le credenziali sono valide, false altrimenti
     */
    public boolean verificaCredenziali(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        return this.username.equals(username) &&
                this.hashedPassword.equals(hashPassword(password, this.salt));
    }

    // -----------------------------------------------------------------------------------
    // METODI DI SICUREZZA PER LA PASSWORD
    // -----------------------------------------------------------------------------------

    /**
     * Protegge la password dell'utente con un hash SHA-256 combinato con il salt.
     * Questo metodo applica una funzione di hash unidirezionale per la sicurezza.
     *
     * @param password la password in chiaro da proteggere
     * @param salt     il salt da utilizzare per migliorare la sicurezza dell'hash
     * @return password protetta con hash in formato stringa Base64
     * @throws RuntimeException se l'algoritmo di hashing non è disponibile
     */
    private String hashPassword(String password, byte[] salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hash = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(HASHING_NON_DISPONIBILE, e);
        }
    }

    /**
     * Genera un salt casuale per proteggere la password da attacchi con tabelle
     * rainbow.
     * Il salt è un valore casuale che viene combinato con la password prima di
     * applicare la funzione di hash.
     *
     * @return salt casuale di 20 byte
     */
    private byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[20];
        random.nextBytes(salt);
        return salt;
    }

    // -----------------------------------------------------------------------------------
    // METODI OVERRIDE DI OBJECT
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce una rappresentazione testuale dell'utente.
     *
     * @return stringa con username e tipo di utente
     */
    @Override
    public String toString() {
        return "Username: " + username + "\n" + "Permessi: " + (isConfig ? "Configuratore" : "Fruitore");
    }

    /**
     * Confronta questo utente con un altro oggetto.
     * Due utenti sono considerati uguali se hanno lo stesso username.
     *
     * @param obj l'oggetto da confrontare con questo utente
     * @return true se l'oggetto è uguale a questo utente, false altrimenti
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
        return this.username.equals(utente.getUsername());
    }

    /**
     * Calcola l'hash code per questo utente basato sullo username.
     *
     * @return hash code dell'utente
     */
    @Override
    public int hashCode() {
        return this.username.hashCode();
    }
}