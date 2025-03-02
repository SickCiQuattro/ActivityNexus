package model;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

/**
 * Rappresenta un utente nel sistema con meccanismi di autenticazione sicura.
 * <p>
 * Supporta due tipi di utente:
 * <ul>
 * <li><b>Fruitori:</b> utenti standard con privilegi limitati</li>
 * <li><b>Configuratori:</b> utenti con privilegi amministrativi</li>
 * </ul>
 * <p>
 * Implementa sicurezza con hashing SHA-256 e salt casuale.
 *
 * @see Configuratore
 * @see Fruitore
 * @see java.io.Serializable
 */
public class Utente implements Serializable {
    private static final long serialVersionUID = 1L;

    // Costanti di sicurezza
    private static final String ALGORITMO_HASH = "SHA-256";
    private static final int DIMENSIONE_SALT = 20; // In byte

    // Costanti per messaggi di errore
    private static final String USERNAME_NON_NULL = "Username non può essere null";
    private static final String HASHING_NON_DISPONIBILE = "Algoritmo di hashing non disponibile";

    /** Username univoco che identifica l'utente */
    private String username;

    /** Hash della password combinato con salt (SHA-256 in formato Base64) */
    private final String hashedPassword;

    /** Salt casuale per proteggere la password da attacchi rainbow table */
    private final byte[] salt;

    /** Flag che indica se l'utente è un configuratore (true) o fruitore (false) */
    private boolean isConfig = false;

    // -----------------------------------------------------------------------------------
    // COSTRUTTORI
    // -----------------------------------------------------------------------------------

    /**
     * Costruisce un utente fruitore con username e password specificati.
     *
     * @param username nome utente univoco per identificare l'utente
     * @param password password in chiaro che verrà trasformata in hash
     * @throws NullPointerException se username è null
     */
    public Utente(String username, String password) {
        // Delega al costruttore completo, specificando isConfig=false (utente fruitore)
        this(username, password, false);
    }

    /**
     * Costruisce un utente con username, password e tipo specificati.
     *
     * @param username nome utente univoco
     * @param password password in chiaro
     * @param isConfig true per configuratore, false per fruitore
     * @throws NullPointerException se username è null
     */
    public Utente(String username, String password, boolean isConfig) {
        // Verifica che l'username non sia null, lanciando un'eccezione in caso
        // contrario
        this.username = Objects.requireNonNull(username, USERNAME_NON_NULL);

        // Genera un salt casuale per questa password
        this.salt = generateSalt();

        // Applica l'algoritmo di hashing alla password combinata con il salt
        this.hashedPassword = hashPassword(password, this.salt);

        // Imposta il flag che determina il tipo di utente
        this.isConfig = isConfig;
    }

    // -----------------------------------------------------------------------------------
    // GETTER E SETTER
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce lo username dell'utente.
     *
     * @return lo username (mai null)
     */
    public String getUsername() {
        return username;
    }

    /**
     * Imposta un nuovo username per l'utente.
     * <p>
     * Usare con cautela se l'utente è già in una collezione basata su username.
     *
     * @param username il nuovo username
     * @throws NullPointerException se username è null
     */
    public void setUsername(String username) {
        // Verifica che il nuovo username non sia null
        this.username = Objects.requireNonNull(username, USERNAME_NON_NULL);
    }

    /**
     * Verifica se l'utente è un configuratore con privilegi amministrativi.
     *
     * @return true se configuratore, false se fruitore
     */
    public boolean isConfig() {
        return isConfig;
    }

    // -----------------------------------------------------------------------------------
    // METODI DI AUTENTICAZIONE
    // -----------------------------------------------------------------------------------

    /**
     * Verifica le credenziali dell'utente.
     * <p>
     * Confronta username e password con i valori memorizzati.
     *
     * @param username nome utente da verificare
     * @param password password da verificare
     * @return true se le credenziali sono valide, false altrimenti
     */
    public boolean verificaCredenziali(String username, String password) {
        // Controllo preventivo: se username o password sono null, le credenziali non
        // sono valide
        if (username == null || password == null) {
            return false;
        }

        // Verifica due condizioni:
        // 1. L'username deve corrispondere esattamente (case-sensitive)
        // 2. La password fornita, dopo l'hashing con il salt dell'utente,
        // deve generare lo stesso hash memorizzato
        return this.username.equals(username) &&
                this.hashedPassword.equals(hashPassword(password, this.salt));
    }

    // -----------------------------------------------------------------------------------
    // METODI DI SICUREZZA PER LA PASSWORD
    // -----------------------------------------------------------------------------------

    /**
     * Calcola l'hash SHA-256 di una password usando il salt specificato.
     *
     * @param password la password in chiaro
     * @param salt     il salt da aggiungere per maggiore sicurezza
     * @return hash in formato Base64
     * @throws RuntimeException se l'algoritmo SHA-256 non è disponibile
     */
    private String hashPassword(String password, byte[] salt) {
        try {
            // Inizializza l'algoritmo di hashing SHA-256
            MessageDigest md = MessageDigest.getInstance(ALGORITMO_HASH);

            // Aggiunge il salt ai dati da processare
            md.update(salt);

            // Calcola l'hash della password (convertita in byte array)
            byte[] hash = md.digest(password.getBytes());

            // Codifica l'hash binario in formato Base64 e lo restituisce come stringa
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            // Se l'algoritmo SHA-256 non è disponibile (molto improbabile nelle JVM
            // moderne)
            throw new RuntimeException(HASHING_NON_DISPONIBILE, e);
        }
    }

    /**
     * Genera un salt casuale di 20 byte per migliorare la sicurezza dell'hash.
     *
     * @return array di 20 byte casuali
     */
    private byte[] generateSalt() {
        // Crea un'istanza di SecureRandom per generare numeri pseudocasuali sicuri
        SecureRandom random = new SecureRandom();

        // Crea un array di 20 byte per contenere il salt
        byte[] salt = new byte[DIMENSIONE_SALT];

        // Riempie l'array con valori casuali
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
        // Crea una rappresentazione leggibile dell'utente
        return "Username: " + username + "\n" +
                "Permessi: " + (isConfig ? "Configuratore" : "Fruitore");
    }

    /**
     * Confronta questo utente con un altro oggetto.
     * <p>
     * Due utenti sono uguali se hanno lo stesso username, indipendentemente
     * da password o tipo.
     *
     * @param obj l'oggetto da confrontare
     * @return true se l'oggetto è un Utente con lo stesso username
     */
    @Override
    public boolean equals(Object obj) {
        // Confronto di identità (ottimizzazione)
        if (this == obj) {
            return true;
        }

        // Verifica che l'oggetto non sia null e sia della stessa classe
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        // Cast sicuro dopo la verifica del tipo
        Utente utente = (Utente) obj;

        // Gli utenti sono uguali se hanno lo stesso username
        return this.username.equals(utente.getUsername());
    }

    /**
     * Calcola l'hash code basato solo sullo username.
     *
     * @return hash code dello username
     */
    @Override
    public int hashCode() {
        // L'hash code è basato solo sullo username
        return this.username.hashCode();
    }
}