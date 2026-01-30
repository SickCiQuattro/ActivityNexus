package service;

import util.UserUtil;

/**
 * Servizio centralizzato per la validazione e gestione delle credenziali
 * utente.
 * <p>
 * Questa classe implementa il pattern <b>Low Coupling</b> (GRASP), separando la
 * logica
 * di validazione delle credenziali dal Controller per ridurre l'accoppiamento
 * e migliorare la manutenibilità del codice.
 * <p>
 * <b>Vantaggi del Low Coupling applicato:</b>
 * <ul>
 * <li><b>Riduzione delle dipendenze:</b> il Controller non dipende più
 * direttamente
 * da UserUtil per la validazione</li>
 * <li><b>Single Responsibility:</b> tutta la logica di validazione credenziali
 * è centralizzata in un unico punto</li>
 * <li><b>Testabilità:</b> il servizio può essere testato in isolamento e
 * mockato
 * facilmente nei test del Controller</li>
 * <li><b>Estendibilità:</b> nuove validazioni possono essere aggiunte senza
 * modificare il Controller</li>
 * <li><b>Riuso:</b> la logica di validazione può essere riutilizzata da altri
 * componenti del sistema</li>
 * </ul>
 * <p>
 * <b>Pattern correlati:</b>
 * <ul>
 * <li>Service Layer - fornisce un'interfaccia di servizio tra Controller e
 * utility</li>
 * <li>Facade - nasconde la complessità di UserUtil dietro un'interfaccia
 * semplificata</li>
 * <li>Information Expert - contiene la conoscenza necessaria per validare
 * credenziali</li>
 * </ul>
 *
 * @see UserUtil
 * @see controller.Controller
 */
public class CredentialService {

    // -----------------------------------------------------------------------------------
    // SINGLETON
    // -----------------------------------------------------------------------------------

    /** Istanza singleton del servizio */
    private static final CredentialService INSTANCE = new CredentialService();

    /**
     * Restituisce l'istanza singleton del servizio credenziali.
     *
     * @return l'unica istanza di CredentialService
     */
    public static CredentialService getInstance() {
        return INSTANCE;
    }

    /** Costruttore privato per Singleton */
    private CredentialService() {
        // Costruttore vuoto - servizio stateless
    }

    /** Lunghezza massima consentita per gli username */
    public static final int MAX_USERNAME_LENGTH = UserUtil.USERNAME_LENGTH;

    /** Lunghezza minima richiesta per le password */
    public static final int MIN_PASSWORD_LENGTH = UserUtil.PASSWORD_LENGTH;

    // -----------------------------------------------------------------------------------
    // METODI DI VALIDAZIONE
    // -----------------------------------------------------------------------------------

    /**
     * Valida uno username secondo i requisiti del sistema.
     * <p>
     * Requisiti verificati:
     * <ul>
     * <li>Non nullo e non vuoto</li>
     * <li>Nessuno spazio</li>
     * <li>Massimo {@value #MAX_USERNAME_LENGTH} caratteri</li>
     * <li>Solo caratteri alfanumerici (a-z, A-Z, 0-9)</li>
     * </ul>
     *
     * @param username lo username da validare
     * @return true se lo username è valido, false altrimenti
     */
    public boolean validateUsername(String username) {
        return UserUtil.checkUsername(username);
    }

    /**
     * Valida una password secondo i requisiti di sicurezza.
     * <p>
     * Requisiti verificati:
     * <ul>
     * <li>Almeno {@value #MIN_PASSWORD_LENGTH} caratteri</li>
     * <li>Almeno una lettera maiuscola</li>
     * <li>Almeno una lettera minuscola</li>
     * <li>Almeno un numero</li>
     * <li>Almeno un carattere speciale (!@#$%^&*)</li>
     * </ul>
     *
     * @param password la password da validare
     * @return true se la password soddisfa i requisiti, false altrimenti
     */
    public boolean validatePassword(String password) {
        return UserUtil.checkPassword(password);
    }

    /**
     * Valida entrambe le credenziali (username e password) contemporaneamente.
     * <p>
     * Questo metodo fornisce una validazione completa delle credenziali
     * in un'unica chiamata, utile per scenari di registrazione.
     *
     * @param username lo username da validare
     * @param password la password da validare
     * @return un oggetto CredentialValidationResult con i dettagli della
     *         validazione
     */
    public CredentialValidationResult validateCredentials(String username, String password) {
        boolean usernameValid = validateUsername(username);
        boolean passwordValid = validatePassword(password);

        return new CredentialValidationResult(usernameValid, passwordValid);
    }

    /**
     * Genera uno username casuale valido.
     * <p>
     * Lo username generato soddisfa automaticamente tutti i requisiti
     * di validazione del sistema.
     *
     * @return uno username casuale valido
     */
    public String generateUsername() {
        return UserUtil.generateRandomUsername();
    }

    /**
     * Genera una password casuale sicura.
     * <p>
     * La password generata soddisfa automaticamente tutti i requisiti
     * di sicurezza del sistema.
     *
     * @return una password casuale sicura
     */
    public String generatePassword() {
        return UserUtil.generateRandomPassword();
    }

    /**
     * Valida il formato di un indirizzo email.
     * <p>
     * PATTERN: Low Coupling - Centralizza anche la validazione email
     * che prima era nel Controller.
     * <p>
     * Requisiti verificati:
     * <ul>
     * <li>Non nullo e non vuoto</li>
     * <li>Contiene esattamente un carattere '@'</li>
     * <li>Il dominio contiene almeno un punto</li>
     * <li>Il dominio non inizia con un punto</li>
     * </ul>
     *
     * @param email l'indirizzo email da validare
     * @return true se il formato è valido, false altrimenti
     */
    public boolean validateEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }

        // Validazione di base: deve contenere @ e almeno un punto dopo @
        int chiocciola = email.indexOf('@');
        if (chiocciola < 1 || chiocciola == email.length() - 1) {
            return false;
        }

        String dominio = email.substring(chiocciola + 1);
        return dominio.contains(".") && !dominio.startsWith(".");
    }

    // -----------------------------------------------------------------------------------
    // CLASSI INTERNE
    // -----------------------------------------------------------------------------------

    /**
     * Risultato della validazione delle credenziali.
     * <p>
     * Oggetto immutabile che incapsula il risultato della validazione
     * di username e password, fornendo metodi per interrogare lo stato.
     */
    public static class CredentialValidationResult {
        private final boolean usernameValid;
        private final boolean passwordValid;

        /**
         * Costruisce un nuovo risultato di validazione.
         *
         * @param usernameValid true se lo username è valido
         * @param passwordValid true se la password è valida
         */
        public CredentialValidationResult(boolean usernameValid, boolean passwordValid) {
            this.usernameValid = usernameValid;
            this.passwordValid = passwordValid;
        }

        /**
         * Verifica se lo username è valido.
         *
         * @return true se lo username è valido
         */
        public boolean isUsernameValid() {
            return usernameValid;
        }

        /**
         * Verifica se la password è valida.
         *
         * @return true se la password è valida
         */
        public boolean isPasswordValid() {
            return passwordValid;
        }

        /**
         * Verifica se entrambe le credenziali sono valide.
         *
         * @return true se sia username che password sono validi
         */
        public boolean isValid() {
            return usernameValid && passwordValid;
        }

        /**
         * Genera un messaggio di errore descrittivo se le credenziali non sono valide.
         *
         * @return il messaggio di errore, o stringa vuota se tutto è valido
         */
        public String getErrorMessage() {
            if (isValid()) {
                return "";
            }

            StringBuilder sb = new StringBuilder();
            if (!usernameValid) {
                sb.append("Username non valido. ");
            }
            if (!passwordValid) {
                sb.append("Password non valida.");
            }
            return sb.toString().trim();
        }
    }
}
