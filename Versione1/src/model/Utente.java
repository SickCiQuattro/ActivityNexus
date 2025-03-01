package model;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

/**
 * Classe che rappresenta un utente nel sistema di gestione delle attività.
 * <p>
 * Questa classe implementa le funzionalità di base per la gestione degli
 * account utente,
 * inclusi meccanismi di sicurezza per la protezione delle credenziali. Ogni
 * utente è
 * identificato da un username univoco e ha una password che viene memorizzata
 * in modo
 * sicuro utilizzando tecniche di hashing con salt.
 * <p>
 * Il sistema supporta due tipologie di utenti:
 * <ul>
 * <li><b>Fruitori:</b> utenti standard che possono accedere e utilizzare le
 * funzionalità di base</li>
 * <li><b>Configuratori:</b> utenti con privilegi amministrativi che possono
 * modificare
 * le impostazioni del sistema e gestire le gerarchie di categorie</li>
 * </ul>
 * <p>
 * Caratteristiche di sicurezza implementate:
 * <ul>
 * <li>Hashing delle password con algoritmo SHA-256</li>
 * <li>Utilizzo di salt casuali per proteggere contro attacchi rainbow
 * table</li>
 * <li>Validazione delle credenziali senza memorizzare password in chiaro</li>
 * <li>Verifiche di non nullità per i campi critici</li>
 * </ul>
 * <p>
 * La classe implementa {@link Serializable} per consentire la persistenza degli
 * utenti,
 * assicurandosi che le credenziali siano memorizzate in modo sicuro anche
 * quando
 * serializzate.
 *
 * @see Configuratore
 * @see Fruitore
 * @see java.io.Serializable
 */
public class Utente implements Serializable {
    // Identificatore di versione per la serializzazione
    private static final long serialVersionUID = 1L;

    // Costanti per messaggi di errore predefiniti, migliora la manutenibilità
    public static final String USERNAME_NON_NULL = "Username non può essere null";
    public static final String HASHING_NON_DISPONIBILE = "Algoritmo di hashing non disponibile";

    /**
     * Nome utente univoco che identifica l'utente nel sistema.
     * <p>
     * Lo username è l'identificatore principale dell'utente, utilizzato per
     * il login e per distinguere gli utenti tra loro. Due utenti con lo stesso
     * username sono considerati lo stesso utente dal sistema (vedi
     * {@link #equals(Object)}).
     */
    private String username;

    /**
     * Password dell'utente salvata come hash per sicurezza.
     * <p>
     * La password non viene mai memorizzata in chiaro, ma viene salvato solo il suo
     * hash ottenuto con l'algoritmo SHA-256 e combinato con un salt casuale.
     * Questo campo è dichiarato come final perché una volta impostato al momento
     * della
     * creazione dell'utente, non può essere modificato direttamente.
     * <p>
     * L'hash è memorizzato in formato Base64 per una rappresentazione compatta.
     * 
     * @see #hashPassword(String, byte[])
     */
    private final String hashedPassword;

    /**
     * Salt casuale usato per proteggere la password da attacchi rainbow table.
     * <p>
     * Il salt è un valore binario casuale che viene combinato con la password
     * prima di applicare la funzione di hash. L'uso del salt garantisce che:
     * <ul>
     * <li>Due utenti con la stessa password avranno hash diversi</li>
     * <li>Gli attacchi basati su tabelle pre-calcolate (rainbow tables) sono
     * inefficaci</li>
     * </ul>
     * <p>
     * Il salt viene generato al momento della creazione dell'utente e salvato
     * per essere riutilizzato durante la verifica delle credenziali.
     * 
     * @see #generateSalt()
     */
    private final byte[] salt;

    /**
     * Flag che indica se l'utente è un configuratore (true) o un fruitore (false).
     * <p>
     * Questo flag determina i privilegi dell'utente all'interno del sistema:
     * <ul>
     * <li>Se true, l'utente è un configuratore con privilegi amministrativi</li>
     * <li>Se false, l'utente è un fruitore con privilegi standard</li>
     * </ul>
     * <p>
     * Il valore predefinito è false, rappresentando un fruitore standard.
     */
    private boolean isConfig = false;

    // -----------------------------------------------------------------------------------
    // COSTRUTTORI
    // -----------------------------------------------------------------------------------

    /**
     * Costruisce un utente fruitore con username e password specificati.
     * <p>
     * Questo costruttore crea un utente standard (fruitore) con i privilegi base.
     * La password fornita viene elaborata con un salt casuale e algoritmo SHA-256
     * per garantire la sicurezza delle credenziali.
     * <p>
     * Esempio di utilizzo:
     * 
     * <pre>
     * Utente fruitore = new Utente("mario_rossi", "password123");
     * </pre>
     *
     * @param username nome utente univoco per identificare l'utente
     * @param password password in chiaro che verrà trasformata in hash per la
     *                 memorizzazione
     * @throws NullPointerException se username è null
     * 
     * @see #Utente(String, String, boolean)
     */
    public Utente(String username, String password) {
        // Delega al costruttore completo, specificando isConfig=false (utente fruitore)
        this(username, password, false);
    }

    /**
     * Costruisce un utente con username, password e tipo specificati.
     * <p>
     * Questo costruttore permette di specificare se l'utente è un configuratore
     * (con privilegi amministrativi) o un fruitore (con privilegi standard).
     * <p>
     * Il processo di creazione include:
     * <ol>
     * <li>Validazione dello username (non può essere null)</li>
     * <li>Generazione di un salt casuale</li>
     * <li>Hashing della password con il salt generato</li>
     * <li>Impostazione del flag che determina il tipo di utente</li>
     * </ol>
     * <p>
     * Esempio di utilizzo:
     * 
     * <pre>
     * // Creazione di un configuratore
     * Utente admin = new Utente("admin", "securePassword", true);
     * 
     * // Creazione di un fruitore
     * Utente user = new Utente("johndoe", "userPass", false);
     * </pre>
     *
     * @param username nome utente univoco per identificare l'utente
     * @param password password in chiaro che verrà trasformata in hash per la
     *                 memorizzazione
     * @param isConfig true se l'utente è un configuratore, false se è un fruitore
     * @throws NullPointerException se username è null
     * 
     * @see #hashPassword(String, byte[])
     * @see #generateSalt()
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
     * Restituisce il nome utente.
     * <p>
     * Lo username è l'identificatore univoco dell'utente nel sistema,
     * utilizzato per l'autenticazione e per distinguere gli utenti tra loro.
     *
     * @return il nome utente (mai null dopo la creazione dell'utente)
     * 
     * @see #setUsername(String)
     */
    public String getUsername() {
        return username;
    }

    /**
     * Imposta un nuovo nome utente.
     * <p>
     * Questo metodo consente di modificare l'identificativo dell'utente. Va usato
     * con cautela
     * se l'utente fa già parte di una collezione (come un Set) basata sullo
     * username,
     * poiché ciò potrebbe rendere l'utente irraggiungibile nella collezione.
     * <p>
     * Per garantire l'integrità del sistema, il nuovo username non può essere null.
     *
     * @param username il nuovo nome utente da assegnare
     * @throws NullPointerException se il nuovo username è null
     * 
     * @see #getUsername()
     * @see #equals(Object)
     * @see #hashCode()
     */
    public void setUsername(String username) {
        // Verifica che il nuovo username non sia null
        this.username = Objects.requireNonNull(username, USERNAME_NON_NULL);
    }

    /**
     * Verifica se l'utente è un configuratore.
     * <p>
     * I configuratori hanno privilegi amministrativi che permettono loro di:
     * <ul>
     * <li>Modificare la struttura del sistema</li>
     * <li>Creare e gestire gerarchie di categorie</li>
     * <li>Definire fattori di conversione</li>
     * <li>Configurare comprensori geografici</li>
     * </ul>
     * <p>
     * I fruitori (non configuratori) hanno invece accesso limitato alle
     * funzionalità
     * di base dell'applicazione.
     *
     * @return true se l'utente è un configuratore con privilegi amministrativi,
     *         false se è un fruitore con privilegi standard
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
     * Questo metodo confronta lo username fornito con quello dell'utente e
     * applica l'hashing alla password fornita per confrontarla con quella salvata.
     * La verifica avviene senza mai memorizzare o esporre la password in chiaro.
     * <p>
     * Processo di verifica:
     * <ol>
     * <li>Controllo preliminare sui parametri (null safety)</li>
     * <li>Confronto degli username (case-sensitive)</li>
     * <li>Hashing della password fornita con lo stesso salt dell'utente</li>
     * <li>Confronto degli hash delle password</li>
     * </ol>
     * <p>
     * Esempio di utilizzo:
     * 
     * <pre>
     * if (utente.verificaCredenziali("mario_rossi", "password123")) {
     *     System.out.println("Accesso consentito");
     * } else {
     *     System.out.println("Credenziali non valide");
     * }
     * </pre>
     *
     * @param username nome utente da verificare
     * @param password password da verificare
     * @return true se le credenziali sono valide (username corrisponde e la
     *         password
     *         genera lo stesso hash), false altrimenti o se uno dei parametri è
     *         null
     * 
     * @see #hashPassword(String, byte[])
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
     * Protegge la password dell'utente con un hash SHA-256 combinato con il salt.
     * <p>
     * Questo metodo implementa una funzione di hashing unidirezionale che:
     * <ol>
     * <li>Inizializza l'algoritmo di hashing SHA-256</li>
     * <li>Aggiunge il salt ai dati da processare</li>
     * <li>Calcola l'hash della password</li>
     * <li>Codifica l'hash binario in formato Base64 per una rappresentazione
     * compatta</li>
     * </ol>
     * <p>
     * La funzione di hash SHA-256 è unidirezionale e resistente alle collisioni,
     * rendendo quasi impossibile risalire alla password originale a partire
     * dall'hash.
     *
     * @param password la password in chiaro da proteggere
     * @param salt     il salt da utilizzare per migliorare la sicurezza dell'hash
     * @return password protetta con hash in formato stringa Base64
     * @throws RuntimeException se l'algoritmo di hashing SHA-256 non è disponibile
     */
    private String hashPassword(String password, byte[] salt) {
        try {
            // Inizializza l'algoritmo di hashing SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");

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
     * Genera un salt casuale per proteggere la password da attacchi con tabelle
     * rainbow.
     * <p>
     * Il salt è un valore casuale che viene combinato con la password prima di
     * applicare la funzione di hash. L'uso di un salt casuale garantisce che:
     * <ul>
     * <li>Due utenti con la stessa password avranno hash diversi</li>
     * <li>Gli attacchi basati su tabelle pre-calcolate sono inefficaci</li>
     * </ul>
     * <p>
     * Il metodo utilizza {@link SecureRandom} che fornisce un generatore di numeri
     * pseudocasuali crittograficamente forte, adatto per applicazioni di sicurezza.
     * <p>
     * La dimensione del salt (20 byte) è sufficiente per garantire un'entropia
     * adeguata
     * e proteggere efficacemente contro attacchi di tipo dictionary o rainbow
     * table.
     *
     * @return salt casuale di 20 byte
     * 
     * @see java.security.SecureRandom
     */
    private byte[] generateSalt() {
        // Crea un'istanza di SecureRandom per generare numeri pseudocasuali sicuri
        SecureRandom random = new SecureRandom();

        // Crea un array di 20 byte per contenere il salt
        byte[] salt = new byte[20];

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
     * La stringa generata include:
     * <ul>
     * <li>Lo username dell'utente</li>
     * <li>Il tipo di utente (Configuratore o Fruitore)</li>
     * </ul>
     * <p>
     * Questo metodo è utile per debugging, logging e visualizzazione
     * nell'interfaccia utente.
     * Non include informazioni sensibili come password o salt.
     *
     * @return stringa formattata con username e tipo di utente
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
     * Due utenti sono considerati uguali se hanno lo stesso username, ignorando
     * qualsiasi altra proprietà come la password o il tipo di utente
     * (configuratore/fruitore).
     * <p>
     * Questo comportamento è coerente con l'idea che lo username sia
     * l'identificatore
     * univoco dell'utente nel sistema.
     * <p>
     * Implementazione:
     * <ol>
     * <li>Confronto di identità (stesso oggetto)</li>
     * <li>Controllo di null e di tipo</li>
     * <li>Confronto degli username</li>
     * </ol>
     *
     * @param obj l'oggetto da confrontare con questo utente
     * @return true se l'oggetto è un Utente con lo stesso username,
     *         false altrimenti
     * 
     * @see #hashCode()
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
     * Calcola l'hash code per questo utente basato sullo username.
     * <p>
     * Coerentemente con {@link #equals(Object)}, l'hash code è basato solo sullo
     * username,
     * che è l'identificatore univoco dell'utente nel sistema.
     * <p>
     * Questo garantisce che due utenti considerati uguali da {@code equals()}
     * avranno
     * lo stesso hash code, requisito fondamentale quando gli utenti vengono
     * utilizzati
     * come chiavi in strutture dati come HashMap o HashSet.
     *
     * @return hash code dell'utente basato sullo username
     * 
     * @see #equals(Object)
     * @see String#hashCode()
     */
    @Override
    public int hashCode() {
        // L'hash code è basato solo sullo username
        return this.username.hashCode();
    }
}