package util;

import java.util.Random;

/**
 * Classe di utilità per la gestione degli utenti nel sistema.
 * <p>
 * Questa classe fornisce funzionalità statiche per:
 * <ul>
 * <li>Generazione di username e password casuali ma sicuri</li>
 * <li>Validazione di credenziali secondo i requisiti di sicurezza definiti</li>
 * </ul>
 * <p>
 * I metodi di generazione producono:
 * <ul>
 * <li>Username: stringhe alfanumeriche di lunghezza definita, facili da
 * leggere</li>
 * <li>Password: stringhe complesse con caratteri misti che rispettano i
 * requisiti
 * minimi di sicurezza (maiuscole, minuscole, numeri, caratteri speciali)</li>
 * </ul>
 * <p>
 * I metodi di validazione verificano che username e password rispettino i
 * criteri
 * stabiliti per il sistema, garantendo consistenza nei dati degli utenti.
 * <p>
 * Questa classe è pensata per essere usata sia durante la registrazione di
 * nuovi utenti
 * che durante il recupero di credenziali o la generazione di credenziali
 * temporanee.
 * 
 * @see model.Utente
 * @see model.Configuratore
 * @see model.Fruitore
 */
public class UserUtil {
    /**
     * Insieme dei caratteri consentiti per gli username.
     * <p>
     * Include solo caratteri alfanumerici (lettere maiuscole, minuscole e numeri)
     * per garantire username facilmente digitabili e privi di caratteri ambigui.
     */
    private static final String USERNAME_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    /**
     * Insieme dei caratteri consentiti per le password.
     * <p>
     * Include caratteri alfanumerici e caratteri speciali comuni per garantire
     * password sufficientemente complesse.
     */
    private static final String PASSWORD_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*";

    /**
     * Lunghezza massima consentita per gli username.
     * <p>
     * Questo valore definisce sia la lunghezza degli username generati
     * automaticamente
     * che il limite massimo per gli username inseriti manualmente dagli utenti.
     * 
     * @see #generateRandomUsername()
     * @see #checkUsername(String)
     */
    public static final int USERNAME_LENGTH = 20;

    /**
     * Lunghezza minima richiesta per le password.
     * <p>
     * Questo valore definisce la lunghezza minima delle password per soddisfare
     * i requisiti di sicurezza. Le password dovrebbero avere almeno questa
     * lunghezza
     * per essere considerate valide.
     * 
     * @see #generateRandomPassword()
     * @see #checkPassword(String)
     */
    public static final int PASSWORD_LENGTH = 8;

    /**
     * Genera uno username casuale alfanumerico.
     * <p>
     * Lo username generato è composto da caratteri alfanumerici (lettere maiuscole,
     * minuscole e numeri) e ha una lunghezza di 10 caratteri.
     * <p>
     * Nota: Sebbene la costante {@code USERNAME_LENGTH} sia definita come 20,
     * questo metodo genera username di 10 caratteri. Questo potrebbe essere un
     * aspetto da rivedere per garantire coerenza.
     * <p>
     * Esempio di output: "a7bF9xZ2pQ"
     *
     * @return uno username casuale alfanumerico di 10 caratteri
     * 
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
     * La password generata soddisfa i seguenti requisiti di sicurezza:
     * <ul>
     * <li>Contiene almeno una lettera maiuscola</li>
     * <li>Contiene almeno una lettera minuscola</li>
     * <li>Contiene almeno un numero</li>
     * <li>Contiene almeno un carattere speciale</li>
     * </ul>
     * <p>
     * La password ha una lunghezza totale di {@code PASSWORD_LENGTH + 4} caratteri,
     * poiché aggiunge 4 caratteri obbligatori (uno per ogni categoria) oltre ai
     * caratteri
     * casuali generati secondo la lunghezza standard.
     * <p>
     * Esempio di output: "aB1@5r7Z!"
     *
     * @return una password casuale che soddisfa i requisiti di sicurezza
     * 
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
     * <p>
     * Questo metodo di supporto facilita la selezione di un carattere casuale
     * da un insieme definito di caratteri. Viene utilizzato principalmente dal
     * metodo
     * {@link #generateRandomPassword()} per garantire la presenza di specifici
     * tipi di caratteri nella password generata.
     *
     * @param chars  la stringa da cui estrarre un carattere casuale
     * @param random l'istanza di Random da utilizzare per la generazione del numero
     *               casuale
     * @return un carattere casuale selezionato dalla stringa specificata
     * 
     * @see #generateRandomPassword()
     */
    private static char getRandomChar(String chars, Random random) {
        // Seleziona e restituisce un carattere casuale dalla stringa fornita
        return chars.charAt(random.nextInt(chars.length()));
    }

    /**
     * Verifica la validità di uno username secondo i requisiti del sistema.
     * <p>
     * Lo username deve rispettare i seguenti criteri:
     * <ul>
     * <li>Non essere vuoto</li>
     * <li>Non contenere spazi</li>
     * <li>Non superare la lunghezza massima {@code USERNAME_LENGTH}</li>
     * <li>Contenere solo caratteri alfanumerici (lettere e numeri)</li>
     * </ul>
     * <p>
     * Nota: c'è un possibile errore nell'espressione regolare usata per verificare
     * l'assenza di caratteri speciali. Il pattern {@code "[^a-zA-Z0-9]"} cerca
     * caratteri
     * che NON sono alfanumerici, quindi la condizione dovrebbe essere modificata
     * per
     * verificare che non ci siano corrispondenze con questo pattern.
     *
     * @param username lo username da verificare
     * @return true se lo username è valido e rispetta tutti i criteri, false
     *         altrimenti
     * 
     * @see #generateRandomUsername()
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
     * Verifica la validità di una password secondo i requisiti di sicurezza.
     * <p>
     * La password deve rispettare i seguenti criteri:
     * <ul>
     * <li>Avere almeno {@code PASSWORD_LENGTH} caratteri</li>
     * <li>Contenere almeno una lettera maiuscola</li>
     * <li>Contenere almeno una lettera minuscola</li>
     * <li>Contenere almeno un numero</li>
     * <li>Contenere almeno un carattere speciale tra: !@#$%^&*</li>
     * </ul>
     * <p>
     * Questi requisiti garantiscono un livello minimo di complessità per le
     * password,
     * aumentando la sicurezza degli account utente nel sistema.
     *
     * @param password la password da verificare
     * @return true se la password è valida e rispetta tutti i criteri di sicurezza,
     *         false altrimenti
     * 
     * @see #generateRandomPassword()
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