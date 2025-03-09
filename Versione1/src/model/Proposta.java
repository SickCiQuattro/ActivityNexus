package model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Rappresenta una proposta di scambio di prestazioni d'opera.
 * <p>
 * Una proposta è composta da:
 * <ul>
 * <li>Una prestazione richiesta che si desidera ricevere</li>
 * <li>Una prestazione offerta che si è disposti a fornire in cambio</li>
 * <li>Uno stato che indica la fase corrente del ciclo di vita della
 * proposta</li>
 * </ul>
 * <p>
 * La proposta può attraversare diversi stati (aperta, chiusa, ritirata) con
 * specifiche regole di transizione.
 *
 * @see Prestazione
 * @see StatoProposta
 */
public class Proposta implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /** Prestazione richiesta */
    private final Prestazione richiesta;

    /** Prestazione offerta in cambio */
    private final Prestazione offerta;

    /** Stato corrente della proposta */
    private StatoProposta stato;

    /**
     * Crea una nuova proposta di scambio con i dettagli specificati.
     *
     * @param richiesta la prestazione richiesta (non può essere null)
     * @param offerta   la prestazione offerta in cambio (non può essere null)
     * @param stato     lo stato iniziale della proposta (non può essere null)
     * @throws NullPointerException se uno dei parametri è null
     */
    public Proposta(Prestazione richiesta, Prestazione offerta, StatoProposta stato) {
        this.richiesta = Objects.requireNonNull(richiesta, "La prestazione richiesta non può essere null");
        this.offerta = Objects.requireNonNull(offerta, "La prestazione offerta non può essere null");
        this.stato = Objects.requireNonNull(stato, "Lo stato della proposta non può essere null");
    }

    /**
     * Crea una nuova proposta di scambio con stato iniziale APERTA.
     *
     * @param richiesta la prestazione richiesta
     * @param offerta   la prestazione offerta in cambio
     * @throws NullPointerException se uno dei parametri è null
     */
    public Proposta(Prestazione richiesta, Prestazione offerta) {
        this(richiesta, offerta, StatoProposta.APERTA);
    }

    /**
     * Tenta di chiudere questa proposta.
     * <p>
     * Una proposta può essere chiusa solo se è attualmente aperta.
     *
     * @return true se la proposta è stata chiusa, false se era in uno stato
     *         che non permette la chiusura
     */
    public boolean chiudiProposta() {
        if (stato == StatoProposta.APERTA) {
            stato = StatoProposta.CHIUSA;
            return true;
        }
        return false;
    }

    /**
     * Tenta di ritirare questa proposta.
     * <p>
     * Una proposta può essere ritirata solo se è attualmente aperta.
     *
     * @return true se la proposta è stata ritirata, false se era in uno stato
     *         che non permette il ritiro
     */
    public boolean ritiraProposta() {
        if (stato == StatoProposta.APERTA) {
            stato = StatoProposta.RITIRATA;
            return true;
        }
        return false;
    }

    /**
     * Verifica se questa proposta è ancora aperta per possibili accettazioni.
     *
     * @return true se la proposta è nello stato APERTA, false altrimenti
     */
    public boolean isAperta() {
        return stato == StatoProposta.APERTA;
    }

    /**
     * Restituisce la prestazione richiesta.
     *
     * @return la prestazione richiesta (mai null)
     */
    public Prestazione getRichiesta() {
        return richiesta;
    }

    /**
     * Restituisce la prestazione offerta.
     *
     * @return la prestazione offerta (mai null)
     */
    public Prestazione getOfferta() {
        return offerta;
    }

    /**
     * Restituisce lo stato corrente della proposta.
     *
     * @return lo stato della proposta (mai null)
     */
    public StatoProposta getStato() {
        return stato;
    }


    /**
     * Genera una rappresentazione testuale della proposta.
     *
     * @return una stringa contenente i dettagli della proposta
     */
    @Override
    public String toString() {
        return "PROPOSTA:\n" +
                "Richiesta: " + richiesta.toString() +
                "\nOfferta: " + offerta.toString() +
                "\nStato: " + stato.toString();
    }

    /**
     * Verifica se questa proposta è uguale a un altro oggetto.
     * <p>
     * Due proposte sono considerate uguali se hanno gli stessi valori per
     * richiesta, offerta e stato.
     *
     * @param obj l'oggetto da confrontare con questa proposta
     * @return true se l'oggetto rappresenta una proposta uguale a questa
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        Proposta other = (Proposta) obj;
        return Objects.equals(richiesta, other.richiesta) &&
                Objects.equals(offerta, other.offerta) &&
                stato == other.stato;
    }

    /**
     * Calcola il codice hash per questa proposta.
     *
     * @return il codice hash calcolato
     */
    @Override
    public int hashCode() {
        return Objects.hash(richiesta, offerta, stato);
    }
}