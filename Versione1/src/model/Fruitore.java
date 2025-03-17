package model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Rappresenta un utente fruitore del sistema di scambio di prestazioni.
 * <p>
 * Il fruitore è l'utente standard che può proporre e accettare scambi di
 * prestazioni.
 * A differenza dei configuratori, i fruitori hanno accesso limitato al sistema
 * e sono caratterizzati da:
 * <ul>
 * <li>Un indirizzo email per le comunicazioni e le notifiche</li>
 * <li>Un comprensorio geografico di appartenenza che determina l'area di
 * scambio</li>
 * </ul>
 * <p>
 * <b>Responsabilità principali:</b>
 * <ul>
 * <li>Gestione delle proprie proposte di scambio</li>
 * <li>Partecipazione a scambi d'opera</li>
 * <li>Ricevere notifiche su insiemi chiusi che lo coinvolgono</li>
 * </ul>
 *
 * <p>
 * <b>Invarianti della classe:</b>
 * <ul>
 * <li>mail != null (l'indirizzo email è sempre definito)</li>
 * <li>isConfig() == false (un fruitore non ha mai privilegi di
 * amministrazione)</li>
 * <li>Il comprensorio può essere null (non impostato) ma è consigliato
 * assegnarlo</li>
 * </ul>
 *
 * @see Utente
 * @see Comprensorio
 * @see Proposta
 */
public class Fruitore extends Utente implements Serializable {
    // -----------------------------------------------------------------------------------
    // FIELDS E COSTANTI
    // -----------------------------------------------------------------------------------

    /** Identificatore di versione per la serializzazione */
    @Serial
    private static final long serialVersionUID = 1L;

    /** Messaggio di errore per email null */
    private static final String MAIL_NON_NULL = "L'indirizzo email non può essere null";

    /** Indirizzo email del fruitore per comunicazioni e notifiche */
    private final String mail;

    /**
     * Comprensorio geografico di appartenenza del fruitore.
     * Determina l'area geografica in cui il fruitore può operare.
     */
    private Comprensorio comprensorio;

    // -----------------------------------------------------------------------------------
    // COSTRUTTORI
    // -----------------------------------------------------------------------------------

    /**
     * Costruisce un nuovo fruitore con credenziali e dati personali specificati.
     * <p>
     * Inizializza un utente di tipo fruitore con le credenziali di accesso e
     * le informazioni necessarie per operare nel sistema.
     *
     * @param username     nome utente univoco per l'autenticazione
     * @param password     password in chiaro per l'autenticazione
     * @param mail         indirizzo email del fruitore
     * @param comprensorio comprensorio geografico di appartenenza
     * @throws NullPointerException se username, password o mail sono null
     *
     * @pre username != null
     * @pre password != null
     * @pre mail != null
     * @post getMail().equals(mail)
     * @post getComprensorio() == comprensorio
     * @post isConfig() == false
     */
    public Fruitore(String username, String password, String mail, Comprensorio comprensorio) {
        // Chiama il costruttore della classe padre con isConfig=false
        // per garantire che il fruitore non abbia privilegi di amministrazione
        super(username, password, false);

        // Verifica che mail non sia null e la memorizza
        this.mail = Objects.requireNonNull(mail, MAIL_NON_NULL);

        // Il comprensorio può essere null (non impostato)
        this.comprensorio = comprensorio;
    }

    // -----------------------------------------------------------------------------------
    // GETTERS E SETTERS
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce l'indirizzo email del fruitore.
     *
     * @return l'indirizzo email (mai null)
     *
     * @post result != null
     */
    public String getMail() {
        return mail;
    }

    /**
     * Restituisce il comprensorio di appartenenza del fruitore.
     * <p>
     * Il comprensorio definisce l'ambito geografico in cui il fruitore può operare
     * per gli scambi di prestazioni.
     *
     * @return il comprensorio (può essere null se non impostato)
     *
     * @post result == this.comprensorio
     */
    public Comprensorio getComprensorio() {
        return comprensorio;
    }

    /**
     * Imposta un nuovo comprensorio di appartenenza per il fruitore.
     * <p>
     * Modificare il comprensorio può influenzare la visibilità delle proposte
     * e la partecipazione a scambi in corso.
     *
     * @param comprensorio il nuovo comprensorio
     *
     * @pre true (nessuna precondizione, comprensorio può essere null)
     * @post getComprensorio() == comprensorio
     */
    public void setComprensorio(Comprensorio comprensorio) {
        this.comprensorio = comprensorio;
    }

    // -----------------------------------------------------------------------------------
    // OVERRIDE DI OBJECT
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce una rappresentazione testuale del fruitore.
     * <p>
     * Include username, email e comprensorio (se disponibile).
     *
     * @return stringa con dettagli del fruitore
     *
     * @post result != null
     * @post result contiene this.getUsername() e this.getMail()
     * @post se this.comprensorio != null, result contiene
     *       this.comprensorio.getNome()
     */
    @Override
    public String toString() {
        // Inizia con il prefisso "Fruitore: " seguito dalle informazioni base
        // dell'utente
        StringBuilder sb = new StringBuilder("Fruitore: ");
        sb.append(super.toString());

        // Aggiunge l'email, sempre presente
        sb.append("\nEmail: ").append(mail);

        // Aggiunge il comprensorio se presente, altrimenti indica che non è specificato
        if (comprensorio != null) {
            sb.append("\nComprensorio: ").append(comprensorio.getNome());
        } else {
            sb.append("\nComprensorio: non specificato");
        }

        return sb.toString();
    }

    /**
     * Confronta questo fruitore con un altro oggetto.
     * <p>
     * Due fruitori sono considerati uguali se hanno lo stesso username
     * (ereditato da Utente), indipendentemente da email e comprensorio.
     *
     * @param obj l'oggetto da confrontare
     * @return true se l'oggetto è un utente con lo stesso username
     *
     * @post result == super.equals(obj)
     * @post se result == true, obj è un Utente con lo stesso username di this
     */
    @Override
    public boolean equals(Object obj) {
        // Delega alla classe padre Utente che confronta gli username
        return super.equals(obj);
    }

    /**
     * Calcola l'hashcode basato sullo username (ereditato da Utente).
     * <p>
     * Mantiene la coerenza con il metodo equals.
     *
     * @return hashcode basato sullo username
     *
     * @post result == super.hashCode()
     * @post se x.equals(y) == true, allora x.hashCode() == y.hashCode()
     */
    @Override
    public int hashCode() {
        // Delega alla classe padre Utente che usa lo username per l'hashcode
        return super.hashCode();
    }
}