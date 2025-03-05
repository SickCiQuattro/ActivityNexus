package model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Rappresenta un utente fruitore del sistema, con diritti di accesso standard.
 * <p>
 * Il fruitore è caratterizzato da:
 * <ul>
 * <li>Un indirizzo email per le comunicazioni</li>
 * <li>Un comprensorio geografico di appartenenza</li>
 * </ul>
 * <p>
 * A differenza dei configuratori, i fruitori hanno accesso limitato
 * alle funzionalità del sistema.
 * 
 * @see Utente
 * @see Comprensorio
 */
public class Fruitore extends Utente implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // Costanti per messaggi di errore
    private static final String MAIL_NON_NULL = "L'indirizzo email non può essere null";

    /** Indirizzo email del fruitore per comunicazioni */
    private String mail;

    /** Comprensorio geografico di appartenenza del fruitore */
    private Comprensorio comprensorio;

    /** Proposte di attività inviate dal fruitore */
    private ArrayList<Proposta> proposte;

    /**
     * Costruisce un nuovo fruitore con credenziali e dati personali specificati.
     *
     * @param username     nome utente univoco per l'autenticazione
     * @param password     password in chiaro per l'autenticazione
     * @param mail         indirizzo email del fruitore
     * @param comprensorio comprensorio geografico di appartenenza
     * @throws NullPointerException se username, password o mail sono null
     */
    public Fruitore(String username, String password, String mail, Comprensorio comprensorio) {
        // false indica che non è un configuratore
        super(username, password, false);
        this.mail = Objects.requireNonNull(mail, MAIL_NON_NULL);
        this.comprensorio = comprensorio; // può essere null se non specificato
        this.proposte = new ArrayList<>();
    }

    /**
     * Costruisce un nuovo fruitore senza comprensorio specificato.
     *
     * @param username nome utente univoco
     * @param password password in chiaro
     * @param mail     indirizzo email del fruitore
     * @throws NullPointerException se username, password o mail sono null
     */
    public Fruitore(String username, String password, String mail) {
        this(username, password, mail, null);
    }

    /**
     * Restituisce l'indirizzo email del fruitore.
     *
     * @return l'indirizzo email (mai null)
     */
    public String getMail() {
        return mail;
    }

    /**
     * Imposta un nuovo indirizzo email per il fruitore.
     *
     * @param mail il nuovo indirizzo email
     * @throws NullPointerException se mail è null
     */
    public void setMail(String mail) {
        this.mail = Objects.requireNonNull(mail, MAIL_NON_NULL);
    }

    /**
     * Restituisce il comprensorio di appartenenza del fruitore.
     *
     * @return il comprensorio (può essere null)
     */
    public Comprensorio getComprensorio() {
        return comprensorio;
    }

    /**
     * Imposta un nuovo comprensorio di appartenenza per il fruitore.
     *
     * @param comprensorio il nuovo comprensorio
     */
    public void setComprensorio(Comprensorio comprensorio) {
        this.comprensorio = comprensorio;
    }

    /**
     * Verifica se il fruitore ha un comprensorio assegnato.
     *
     * @return true se il fruitore ha un comprensorio, false altrimenti
     */
    public boolean haComprensorio() {
        return this.comprensorio != null;
    }

    /**
     * Restituisce la lista delle proposte di attività inviate dal fruitore.
     *
     * @return lista delle proposte inviate
     */
    public ArrayList<Proposta> getProposte() {
        return proposte;
    }

    /**
     * Aggiunge una proposta di attività alla lista del fruitore.
     *
     * @param proposta la proposta da aggiungere
     */
    public void aggiungiProposta (Proposta proposta) {
        proposte.add(proposta);
    }

    /**
     * Restituisce una rappresentazione testuale del fruitore.
     *
     * @return stringa con dettagli del fruitore
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Fruitore: ");
        sb.append(super.toString());
        sb.append("\nEmail: ").append(mail);

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
     * @return true se l'oggetto è un fruitore con lo stesso username
     */
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    /**
     * Calcola l'hashcode basato sullo username (ereditato da Utente).
     *
     * @return hashcode basato sullo username
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}