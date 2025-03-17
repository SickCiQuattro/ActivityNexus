package util;

import java.util.Random;

/**
 * Utilità per la gestione delle credenziali utente nel sistema.
 * <p>
 * Questa classe fornisce funzionalità per:
 * <ul>
 * <li>Generazione di username e password casuali conformi ai requisiti di
 * sicurezza</li>
 * <li>Validazione di credenziali secondo criteri specifici di sicurezza e
 * sintassi</li>
 * </ul>
 * <p>
 * Gli username generati sono alfanumerici e di lunghezza fissa, mentre le
 * password
 * includono una combinazione di lettere maiuscole, minuscole, numeri e
 * caratteri speciali
 * per garantire un adeguato livello di sicurezza.
 * 
 * @see model.Utente
 * @see model.Configuratore
 * @see model.Fruitore
 */
public class UserUtil {
    // -----------------------------------------------------------------------------------
    // COSTANTI
    // -----------------------------------------------------------------------------------

    /** Caratteri consentiti per username (alfanumerici) */
    private static final String USERNAME_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    /** Caratteri consentiti per password (alfanumerici e speciali) */
    private static final String PASSWORD_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*";

    /** Caratteri maiuscoli per garantire la presenza di almeno uno in password */
    private static final String UPPERCASE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /** Caratteri minuscoli per garantire la presenza di almeno uno in password */
    private static final String LOWERCASE_CHARS = "abcdefghijklmnopqrstuvwxyz";

    /** Caratteri numerici per garantire la presenza di almeno uno in password */
    private static final String DIGIT_CHARS = "0123456789";

    /** Caratteri speciali per garantire la presenza di almeno uno in password */
    private static final String SPECIAL_CHARS = "!@#$%^&*";

    /** Lunghezza massima per gli username */
    public static final int USERNAME_LENGTH = 20;

    /** Lunghezza minima per le password */
    public static final int PASSWORD_LENGTH = 8;

    /** Lunghezza effettiva dello username generato */
    private static final int GENERATED_USERNAME_LENGTH = 10;

    /** Numero di caratteri aggiuntivi obbligatori nelle password generate */
    private static final int MANDATORY_CHARS_COUNT = 4; // una maiuscola, minuscola, cifra, speciale

    // -----------------------------------------------------------------------------------
    // METODI DI VALIDAZIONE
    // -----------------------------------------------------------------------------------

    /**
     * Verifica la validità di uno username secondo i requisiti del sistema.
     * <p>
     * Requisiti:
     * <ul>
     * <li>Non vuoto</li>
     * <li>Senza spazi</li>
     * <li>Max USERNAME_LENGTH caratteri</li>
     * <li>Solo caratteri alfanumerici (a-z, A-Z, 0-9)</li>
     * </ul>
     *
     * @param username lo username da verificare
     * @return true se lo username è valido, false altrimenti
     *
     * @pre true (username può essere null)
     * @post se username == null, result == false
     * @post se username.isEmpty(), result == false
     * @post se username.contains(" "), result == false
     * @post se username.length() > USERNAME_LENGTH, result == false
     * @post se username contiene caratteri non alfanumerici, result == false
     * @post se tutte le condizioni sopra sono false, result == true
     */
    public static boolean checkUsername(String username) {
        // Gestione caso null
        if (username == null) {
            return false;
        }

        // Verifica tutti i requisiti:
        // 1. Non vuoto
        // 2. Non contiene spazi
        // 3. Non supera la lunghezza massima
        // 4. Contiene solo caratteri alfanumerici (la regex verifica l'assenza di
        // caratteri non alfanumerici)
        return !username.isEmpty() &&
                !username.contains(" ") &&
                username.length() <= USERNAME_LENGTH &&
                !username.matches(".*[^a-zA-Z0-9].*");
    }

    /**
     * Verifica la validità di una password secondo i requisiti di sicurezza.
     * <p>
     * Requisiti:
     * <ul>
     * <li>Almeno PASSWORD_LENGTH caratteri</li>
     * <li>Almeno una lettera maiuscola</li>
     * <li>Almeno una lettera minuscola</li>
     * <li>Almeno un numero</li>
     * <li>Almeno un carattere speciale (!@#$%^&*)</li>
     * </ul>
     *
     * @param password la password da verificare
     * @return true se la password soddisfa tutti i requisiti, false altrimenti
     *
     * @pre true (password può essere null)
     * @post se password == null, result == false
     * @post se password.length() < PASSWORD_LENGTH, result == false
     * @post se password non contiene lettere maiuscole, result == false
     * @post se password non contiene lettere minuscole, result == false
     * @post se password non contiene numeri, result == false
     * @post se password non contiene caratteri speciali, result == false
     * @post se tutte le condizioni sopra sono false, result == true
     */
    public static boolean checkPassword(String password) {
        // Gestione caso null
        if (password == null) {
            return false;
        }

        // Verifica tutti i requisiti:
        // 1. Lunghezza minima
        // 2. Presenza di almeno una lettera maiuscola
        // 3. Presenza di almeno una lettera minuscola
        // 4. Presenza di almeno un numero
        // 5. Presenza di almeno un carattere speciale
        return password.length() >= PASSWORD_LENGTH &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[a-z].*") &&
                password.matches(".*\\d.*") &&
                password.matches(".*[!@#$%^&*].*");
    }

    // -----------------------------------------------------------------------------------
    // METODI DI GENERAZIONE
    // -----------------------------------------------------------------------------------

    /**
     * Genera uno username casuale alfanumerico.
     * <p>
     * Lo username generato avrà esattamente GENERATED_USERNAME_LENGTH caratteri
     * (10)
     * e sarà composto solo da caratteri alfanumerici.
     *
     * @return uno username casuale valido (mai null)
     *
     * @post result != null
     * @post result.length() == GENERATED_USERNAME_LENGTH (10)
     * @post checkUsername(result) == true
     * @post result contiene solo caratteri da USERNAME_CHARS
     */
    public static String generateRandomUsername() {
        // Crea un generatore di numeri casuali
        Random random = new Random();

        // Inizializza un StringBuilder con la capacità desiderata
        StringBuilder username = new StringBuilder(GENERATED_USERNAME_LENGTH);

        // Genera GENERATED_USERNAME_LENGTH caratteri casuali
        for (int i = 0; i < GENERATED_USERNAME_LENGTH; i++) {
            // Seleziona un carattere casuale dall'insieme dei caratteri consentiti
            username.append(USERNAME_CHARS.charAt(random.nextInt(USERNAME_CHARS.length())));
        }

        return username.toString();
    }

    /**
     * Genera una password casuale sicura conforme ai requisiti di sicurezza.
     * <p>
     * La password generata includerà obbligatoriamente:
     * <ul>
     * <li>Esattamente una lettera maiuscola</li>
     * <li>Esattamente una lettera minuscola</li>
     * <li>Esattamente un numero</li>
     * <li>Esattamente un carattere speciale</li>
     * <li>Altri PASSWORD_LENGTH caratteri casuali</li>
     * </ul>
     * <p>
     * La lunghezza totale sarà PASSWORD_LENGTH + MANDATORY_CHARS_COUNT caratteri.
     *
     * @return una password casuale sicura (mai null)
     *
     * @post result != null
     * @post result.length() == PASSWORD_LENGTH + MANDATORY_CHARS_COUNT
     * @post checkPassword(result) == true
     * @post result contiene almeno un carattere da ciascuno dei set:
     *       UPPERCASE_CHARS, LOWERCASE_CHARS, DIGIT_CHARS, SPECIAL_CHARS
     */
    public static String generateRandomPassword() {
        // Crea un generatore di numeri casuali
        Random random = new Random();

        // Inizializza un StringBuilder con la capacità desiderata
        // +MANDATORY_CHARS_COUNT (4) per i caratteri obbligatori
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH + MANDATORY_CHARS_COUNT);

        // Aggiunge almeno una lettera maiuscola
        password.append(getRandomChar(UPPERCASE_CHARS, random));

        // Aggiunge almeno una lettera minuscola
        password.append(getRandomChar(LOWERCASE_CHARS, random));

        // Aggiunge almeno un numero
        password.append(getRandomChar(DIGIT_CHARS, random));

        // Aggiunge almeno un carattere speciale
        password.append(getRandomChar(SPECIAL_CHARS, random));

        // Aggiunge ulteriori PASSWORD_LENGTH caratteri casuali
        // per raggiungere la lunghezza totale di PASSWORD_LENGTH +
        // MANDATORY_CHARS_COUNT
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            password.append(PASSWORD_CHARS.charAt(random.nextInt(PASSWORD_CHARS.length())));
        }

        return password.toString();
    }

    // -----------------------------------------------------------------------------------
    // METODI DI UTILITÀ (PRIVATI)
    // -----------------------------------------------------------------------------------

    /**
     * Estrae un carattere casuale da una stringa specificata.
     * <p>
     * Questo metodo di utilità supporta la generazione di credenziali casuali
     * permettendo di selezionare un carattere da un insieme specifico.
     *
     * @param chars  la stringa da cui estrarre un carattere
     * @param random generatore di numeri casuali
     * @return un carattere casuale dalla stringa
     *
     * @pre chars != null && chars.length() > 0
     * @pre random != null
     * @post result è un carattere presente in chars
     */
    private static char getRandomChar(String chars, Random random) {
        return chars.charAt(random.nextInt(chars.length()));
    }
}