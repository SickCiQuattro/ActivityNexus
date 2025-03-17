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
 * Questa classe implementa:
 * <ul>
 * <li>Gestione sicura delle credenziali con hashing SHA-256 e salt casuale</li>
 * <li>Differenziazione tra fruitori e configuratori tramite flag di
 * autorizzazione</li>
 * <li>Meccanismi di verifica delle credenziali per l'autenticazione</li>
 * </ul>
 * <p>
 * Supporta due tipi di utente:
 * <ul>
 * <li><b>Fruitori:</b> utenti standard con privilegi limitati che possono
 * proporre e accettare scambi</li>
 * <li><b>Configuratori:</b> utenti con privilegi amministrativi che possono
 * configurare il sistema e gestire categorie</li>
 * </ul>
 * <p>
 * La classe è base per le sottoclassi specifiche {@link Fruitore} e
 * {@link Configuratore}.
 *
 * <p>
 * <b>Invarianti della classe:</b>
 * <ul>
 * <li>Lo username non è mai null</li>
 * <li>La password hashed non è mai null</li>
 * <li>Il salt non è mai null ed ha sempre una dimensione di DIMENSIONE_SALT
 * byte</li>
 * <li>Due utenti sono uguali se e solo se hanno lo stesso username
 * (case-sensitive)</li>
 * </ul>
 *
 * @see Fruitore
 * @see Configuratore
 * @see java.io.Serializable
 */
public class Utente implements Serializable {
    // -----------------------------------------------------------------------------------
    // FIELDS E COSTANTI
    // -----------------------------------------------------------------------------------

    /** Identificatore di versione per la serializzazione */
    @Serial
    private static final long serialVersionUID = 1L;

    /** Algoritmo di hashing utilizzato per proteggere le password */
    private static final String ALGORITMO_HASH = "SHA-256";

    /** Dimensione del salt in byte per aumentare la sicurezza delle password */
    private static final int DIMENSIONE_SALT = 20;

    /** Messaggi di errore per validazione e gestione eccezioni */
    private static final String USERNAME_NON_NULL = "Username non può essere null";
    private static final String HASHING_NON_DISPONIBILE = "Algoritmo di hashing non disponibile";
    private static final String PASSWORD_NON_NULL = "Password non può essere null";
    private static final String SALT_NON_NULL = "Salt non può essere null";

    /** Username univoco che identifica l'utente */
    private final String username;

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
     * Crea un nuovo utente specificando il tipo.
     * <p>
     * Genera automaticamente un salt casuale e applica l'algoritmo di hashing
     * SHA-256
     * alla password fornita per memorizzarla in modo sicuro.
     *
     * @param username lo username scelto per l'utente
     * @param password la password in chiaro (sarà hashata internamente)
     * @param isConfig true per configuratore, false per fruitore
     * @throws NullPointerException se username o password sono null
     *
     * @pre username != null
     * @pre password != null
     * @post getUsername().equals(username)
     * @post isConfig() == isConfig
     * @post salt.length == DIMENSIONE_SALT
     * @post hashedPassword contiene l'hash SHA-256 della password con il salt
     *       generato
     */
    public Utente(String username, String password, boolean isConfig) {
        // Verifica che l'username non sia null, lanciando un'eccezione in caso
        // contrario
        this.username = Objects.requireNonNull(username, USERNAME_NON_NULL);

        // Verifica che anche la password non sia null
        Objects.requireNonNull(password, PASSWORD_NON_NULL);

        // Genera un salt casuale per questa password
        this.salt = generateSalt();

        // Applica l'algoritmo di hashing alla password combinata con il salt
        this.hashedPassword = hashPassword(password, this.salt);

        // Imposta il flag che determina il tipo di utente
        this.isConfig = isConfig;
    }

    // -----------------------------------------------------------------------------------
    // GETTERS E SETTERS
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce lo username dell'utente.
     *
     * @return lo username (mai null)
     *
     * @post result != null
     */
    public String getUsername() {
        return username;
    }

    /**
     * Verifica se l'utente è un configuratore con privilegi amministrativi.
     *
     * @return true se configuratore, false se fruitore
     *
     * @post result == this.isConfig
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
     * Confronta username e password con i valori memorizzati. L'username
     * viene confrontato case-sensitive mentre la password viene hashata con
     * il salt dell'utente e confrontata con l'hash memorizzato.
     *
     * @param username nome utente da verificare
     * @param password password da verificare
     * @return true se le credenziali sono valide, false altrimenti
     *
     * @pre true (username e password possono essere null)
     * @post se username == null o password == null, result == false
     * @post result == true se e solo se username.equals(this.username) &&
     *       hashPassword(password, salt).equals(hashedPassword)
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
     * <p>
     * Combina il salt con la password, applica l'algoritmo SHA-256 e restituisce
     * il risultato in formato Base64 per una rappresentazione leggibile.
     *
     * @param password la password in chiaro
     * @param salt     il salt da aggiungere per maggiore sicurezza
     * @return hash in formato Base64
     * @throws NullPointerException se password o salt sono null
     * @throws RuntimeException     se l'algoritmo SHA-256 non è disponibile
     *
     * @pre password != null
     * @pre salt != null
     * @post result != null
     * @post result è una stringa Base64 dell'hash SHA-256 di password+salt
     */
    private String hashPassword(String password, byte[] salt) {
        // Verifica che i parametri non siano null
        Objects.requireNonNull(password, PASSWORD_NON_NULL);
        Objects.requireNonNull(salt, SALT_NON_NULL);

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
     * Genera un salt casuale per migliorare la sicurezza dell'hash.
     * <p>
     * Il salt è un valore casuale che viene combinato con la password prima di
     * applicare
     * l'hashing per proteggere contro attacchi con tabelle precalcolate (rainbow
     * table).
     *
     * @return array di byte casuali della dimensione specificata da DIMENSIONE_SALT
     *
     * @post result != null
     * @post result.length == DIMENSIONE_SALT
     * @post result contiene byte generati con SecureRandom
     */
    private byte[] generateSalt() {
        // Crea un'istanza di SecureRandom per generare numeri pseudocasuali sicuri
        SecureRandom random = new SecureRandom();

        // Crea un array di byte per contenere il salt
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
     * <p>
     * Il formato è "Username: [username]" seguito dal tipo di utente
     * (Configuratore o Fruitore).
     *
     * @return stringa con username e tipo di utente
     *
     * @post result != null
     * @post result contiene getUsername() e un'indicazione se isConfig() è true o
     *       false
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
     * da password, salt o tipo di utente. Questo permette di utilizzare gli utenti
     * come chiavi in mappe e set basandosi solo sull'username.
     *
     * @param obj l'oggetto da confrontare
     * @return true se l'oggetto è un Utente con lo stesso username
     *
     * @pre true (obj può essere null)
     * @post result == true se e solo se obj è un Utente e
     *       obj.getUsername().equals(this.username)
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
     * Calcola l'hash code per questo utente.
     * <p>
     * L'hash è basato solo sullo username in accordo con equals().
     * Questo garantisce la coerenza per l'uso dell'utente in collezioni hash.
     *
     * @return l'hash code calcolato in base allo username
     *
     * @post se x.equals(y) == true, allora x.hashCode() == y.hashCode()
     */
    @Override
    public int hashCode() {
        // L'hash code è basato solo sullo username
        return this.username.hashCode();
    }
}