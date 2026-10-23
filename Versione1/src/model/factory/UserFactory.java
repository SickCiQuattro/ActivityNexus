package model.factory;

import model.Comprensorio;
import model.Configuratore;
import model.Fruitore;
import model.Utente;

import java.util.Objects;

/**
 * Factory per la creazione centralizzata di utenti nel sistema.
 * <p>
 * Questa classe implementa il pattern <b>Factory Method</b> (GoF) per
 * centralizzare
 * e standardizzare la creazione di oggetti {@link Utente} e delle sue
 * sottoclassi.
 * <p>
 * <b>Vantaggi del pattern Factory Method:</b>
 * <ul>
 * <li><b>Centralizzazione:</b> tutta la logica di creazione utenti è in un
 * unico punto</li>
 * <li><b>Flessibilità:</b> facilita l'aggiunta di nuovi tipi di utente senza
 * modificare il codice client</li>
 * <li><b>Disaccoppiamento:</b> il client non conosce i dettagli di costruzione
 * delle sottoclassi</li>
 * <li><b>Consistenza:</b> garantisce che tutti gli utenti siano creati seguendo
 * le stesse regole</li>
 * <li><b>Testabilità:</b> facilita il mocking e il testing isolato</li>
 * </ul>
 * <p>
 * <b>Tipi di utente supportati:</b>
 * <ul>
 * <li>{@link UserType#CONFIGURATORE}: utente amministratore con privilegi
 * avanzati</li>
 * <li>{@link UserType#FRUITORE}: utente standard per scambi di prestazioni</li>
 * </ul>
 *
 * @see Utente
 * @see Configuratore
 * @see Fruitore
 * @see UserType
 */
public class UserFactory {

    /**
     * Enumerazione dei tipi di utente supportati dal sistema.
     * <p>
     * Permette di specificare in modo type-safe quale tipo di utente creare,
     * evitando errori dovuti a stringhe o magic numbers.
     */
    public enum UserType {
        /** Utente amministratore con privilegi di configurazione */
        CONFIGURATORE,
        /** Utente standard che partecipa agli scambi */
        FRUITORE
    }

    /** Istanza singleton della factory */
    private static final UserFactory INSTANCE = new UserFactory();

    /**
     * Restituisce l'istanza singleton della factory.
     * <p>
     * Utilizza il pattern Singleton per fornire un punto di accesso globale
     * alla factory, mantenendo un'unica istanza in tutto il sistema.
     *
     * @return l'istanza singleton di UserFactory
     */
    public static UserFactory getInstance() {
        return INSTANCE;
    }

    /**
     * Costruttore privato per il pattern Singleton.
     * <p>
     * Può essere reso public se si preferisce permettere istanze multiple.
     */
    private UserFactory() {
        // Costruttore vuoto - factory stateless
    }

    /**
     * Crea un nuovo Configuratore con le credenziali specificate.
     * <p>
     * Questo è il metodo factory dedicato per la creazione di utenti
     * amministratori.
     * Valida i parametri in ingresso e crea un'istanza di {@link Configuratore}.
     *
     * @param username lo username univoco per il configuratore
     * @param password la password in chiaro (sarà hashata internamente)
     * @return una nuova istanza di Configuratore
     * @throws NullPointerException     se username o password sono null
     * @throws IllegalArgumentException se username o password sono vuoti
     *
     * @pre username != null && !username.isBlank()
     * @pre password != null && !password.isBlank()
     * @post result != null
     * @post result.isConfig() == true
     * @post result.getUsername().equals(username)
     */
    public Configuratore createConfiguratore(String username, String password) {
        validateCredentials(username, password);
        return new Configuratore(username, password);
    }

    /**
     * Crea un nuovo Fruitore con le credenziali e i dati specificati.
     * <p>
     * Questo è il metodo factory dedicato per la creazione di utenti fruitori.
     * Valida i parametri in ingresso e crea un'istanza di {@link Fruitore}.
     *
     * @param username     lo username univoco per il fruitore
     * @param password     la password in chiaro (sarà hashata internamente)
     * @param email        l'indirizzo email del fruitore
     * @param comprensorio il comprensorio geografico di appartenenza (può essere
     *                     null)
     * @return una nuova istanza di Fruitore
     * @throws NullPointerException     se username, password o email sono null
     * @throws IllegalArgumentException se username, password o email sono vuoti
     *
     * @pre username != null && !username.isBlank()
     * @pre password != null && !password.isBlank()
     * @pre email != null && !email.isBlank()
     * @post result != null
     * @post result.isConfig() == false
     * @post result.getUsername().equals(username)
     * @post result.getMail().equals(email)
     */
    public Fruitore createFruitore(String username, String password, String email, Comprensorio comprensorio) {
        validateCredentials(username, password);
        Objects.requireNonNull(email, "Email non può essere null");
        if (email.isBlank()) {
            throw new IllegalArgumentException("Email non può essere vuota");
        }
        return new Fruitore(username, password, email, comprensorio);
    }

    /**
     * Metodo factory generico che crea un utente in base al tipo specificato.
     * <p>
     * Questo metodo implementa il pattern Factory Method classico, permettendo
     * di creare utenti di tipo diverso usando un'unica interfaccia.
     * <p>
     * <b>Nota:</b> Per creare un Fruitore con questo metodo, i parametri aggiuntivi
     * (email e comprensorio) devono essere passati tramite {@code extraParams}.
     *
     * @param type        il tipo di utente da creare
     * @param username    lo username univoco
     * @param password    la password in chiaro
     * @param extraParams parametri aggiuntivi specifici per il tipo:
     *                    <ul>
     *                    <li>FRUITORE: extraParams[0] = email (String),
     *                    extraParams[1] = comprensorio (Comprensorio,
     *                    opzionale)</li>
     *                    <li>CONFIGURATORE: nessun parametro extra richiesto</li>
     *                    </ul>
     * @return una nuova istanza di Utente del tipo specificato
     * @throws NullPointerException     se type, username o password sono null
     * @throws IllegalArgumentException se i parametri non sono validi per il tipo
     *                                  richiesto
     *
     * @pre type != null
     * @pre username != null && !username.isBlank()
     * @pre password != null && !password.isBlank()
     * @post result != null
     * @post (type == CONFIGURATORE) implies result instanceof Configuratore
     * @post (type == FRUITORE) implies result instanceof Fruitore
     */
    public Utente createUser(UserType type, String username, String password, Object... extraParams) {
        Objects.requireNonNull(type, "Il tipo utente non può essere null");

        return switch (type) {
            case CONFIGURATORE -> createConfiguratore(username, password);
            case FRUITORE -> createFruitoreFromParams(username, password, extraParams);
        };
    }

    /**
     * Valida le credenziali di accesso.
     *
     * @param username lo username da validare
     * @param password la password da validare
     * @throws NullPointerException     se username o password sono null
     * @throws IllegalArgumentException se username o password sono vuoti
     */
    private void validateCredentials(String username, String password) {
        Objects.requireNonNull(username, "Username non può essere null");
        Objects.requireNonNull(password, "Password non può essere null");

        if (username.isBlank()) {
            throw new IllegalArgumentException("Username non può essere vuoto");
        }
        if (password.isBlank()) {
            throw new IllegalArgumentException("Password non può essere vuota");
        }
    }

    /**
     * Crea un Fruitore estraendo i parametri dall'array varargs.
     *
     * @param username    lo username
     * @param password    la password
     * @param extraParams array contenente email e opzionalmente comprensorio
     * @return una nuova istanza di Fruitore
     * @throws IllegalArgumentException se i parametri non sono validi
     */
    private Fruitore createFruitoreFromParams(String username, String password, Object[] extraParams) {
        if (extraParams == null || extraParams.length < 1) {
            throw new IllegalArgumentException("Per creare un Fruitore è necessario specificare almeno l'email");
        }

        if (!(extraParams[0] instanceof String email)) {
            throw new IllegalArgumentException("Il primo parametro extra deve essere l'email (String)");
        }

        Comprensorio comprensorio = null;
        if (extraParams.length >= 2 && extraParams[1] != null) {
            if (!(extraParams[1] instanceof Comprensorio)) {
                throw new IllegalArgumentException("Il secondo parametro extra deve essere un Comprensorio");
            }
            comprensorio = (Comprensorio) extraParams[1];
        }

        return createFruitore(username, password, email, comprensorio);
    }
}