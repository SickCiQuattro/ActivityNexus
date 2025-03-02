package util;

import java.util.Random;

/**
 * Utilità per la gestione delle credenziali utente.
 * <p>
 * Fornisce metodi per:
 * <ul>
 * <li>Generazione di username e password casuali e sicuri</li>
 * <li>Validazione di credenziali secondo i requisiti di sicurezza</li>
 * </ul>
 * 
 * @see model.Utente
 * @see model.Configuratore
 * @see model.Fruitore
 */
public class UserUtil {
    /** Caratteri consentiti per username (alfanumerici) */
    private static final String USERNAME_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    /** Caratteri consentiti per password (alfanumerici e speciali) */
    private static final String PASSWORD_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*";

    /** Lunghezza massima per gli username */
    public static final int USERNAME_LENGTH = 20;

    /** Lunghezza minima per le password */
    public static final int PASSWORD_LENGTH = 8;

    /**
     * Genera uno username casuale alfanumerico di 10 caratteri.
     * <p>
     * Nota: Nonostante USERNAME_LENGTH sia 20, questa implementazione
     * genera username di 10 caratteri.
     *
     * @return uno username casuale alfanumerico
     * @see #checkUsername(String)
     */
    public static String generateRandomUsername() {
        // Crea un generatore di numeri casuali
        Random random = new Random();

        // Inizializza un StringBuilder con la capacità desiderata
        StringBuilder username = new StringBuilder(USERNAME_LENGTH);

        // Genera 10 caratteri casuali (nota: non usa USERNAME_LENGTH)
        for (int i = 0; i < 10; i++) {
            // Seleziona un carattere casuale dall'insieme dei caratteri consentiti
            username.append(USERNAME_CHARS.charAt(random.nextInt(USERNAME_CHARS.length())));
        }

        return username.toString();
    }

    /**
     * Genera una password casuale sicura.
     * <p>
     * La password includerà almeno: una maiuscola, una minuscola,
     * un numero e un carattere speciale.
     * <p>
     * La lunghezza totale sarà PASSWORD_LENGTH + 4 caratteri.
     *
     * @return una password casuale sicura
     * @see #checkPassword(String)
     */
    public static String generateRandomPassword() {
        // Crea un generatore di numeri casuali
        Random random = new Random();

        // Inizializza un StringBuilder con la capacità desiderata
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH + 4); // +4 per i caratteri obbligatori

        // Aggiunge almeno una lettera maiuscola
        password.append(getRandomChar("ABCDEFGHIJKLMNOPQRSTUVWXYZ", random));

        // Aggiunge almeno una lettera minuscola
        password.append(getRandomChar("abcdefghijklmnopqrstuvwxyz", random));

        // Aggiunge almeno un numero
        password.append(getRandomChar("0123456789", random));

        // Aggiunge almeno un carattere speciale
        password.append(getRandomChar("!@#$%^&*", random));

        // Aggiunge ulteriori PASSWORD_LENGTH caratteri casuali
        // Nota: questo produrrà una password più lunga di PASSWORD_LENGTH
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            // Seleziona un carattere casuale dall'insieme dei caratteri consentiti per
            // password
            password.append(PASSWORD_CHARS.charAt(random.nextInt(PASSWORD_CHARS.length())));
        }

        return password.toString();
    }

    /**
     * Estrae un carattere casuale da una stringa specificata.
     *
     * @param chars  la stringa da cui estrarre un carattere
     * @param random generatore di numeri casuali
     * @return un carattere casuale dalla stringa
     */
    private static char getRandomChar(String chars, Random random) {
        // Seleziona e restituisce un carattere casuale dalla stringa fornita
        return chars.charAt(random.nextInt(chars.length()));
    }

    /**
     * Verifica la validità di uno username.
     * <p>
     * Requisiti:
     * <ul>
     * <li>Non vuoto</li>
     * <li>Senza spazi</li>
     * <li>Max USERNAME_LENGTH caratteri</li>
     * <li>Solo caratteri alfanumerici</li>
     * </ul>
     *
     * @param username lo username da verificare
     * @return true se lo username è valido
     */
    public static boolean checkUsername(String username) {
        // Verifica che lo username:
        // 1. Non sia vuoto
        // 2. Non contenga spazi
        // 3. Non superi la lunghezza massima consentita
        // 4. Non contenga caratteri speciali (nota: la regex attuale verifica
        // l'opposto)
        return !username.isEmpty() &&
                !username.contains(" ") &&
                username.length() <= USERNAME_LENGTH &&
                !username.matches(".*[^a-zA-Z0-9].*");
    }

    /**
     * Verifica la validità di una password.
     * <p>
     * Requisiti:
     * <ul>
     * <li>Almeno PASSWORD_LENGTH caratteri</li>
     * <li>Almeno una maiuscola</li>
     * <li>Almeno una minuscola</li>
     * <li>Almeno un numero</li>
     * <li>Almeno un carattere speciale (!@#$%^&*)</li>
     * </ul>
     *
     * @param password la password da verificare
     * @return true se la password è valida
     */
    public static boolean checkPassword(String password) {
        // Verifica che la password:
        // 1. Abbia almeno la lunghezza minima richiesta
        // 2. Contenga almeno una lettera maiuscola
        // 3. Contenga almeno una lettera minuscola
        // 4. Contenga almeno un numero
        // 5. Contenga almeno un carattere speciale
        return password.length() >= PASSWORD_LENGTH &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[a-z].*") &&
                password.matches(".*\\d.*") &&
                password.matches(".*[!@#$%^&*].*");
    }
}