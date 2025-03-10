package model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
 * <p><b>Invarianti:</b>
 * <ul>
 * <li>richiesta != null</li>
 * <li>offerta != null</li>
 * <li>stato != null</li>
 * </ul>
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

    /** Data di creazione della proposta */
    private LocalDateTime dataCreazione;

    private LocalDateTime dataModifica;


    /**
     * Crea una nuova proposta di scambio con i dettagli specificati.
     *
     * @param richiesta la prestazione richiesta (non può essere null)
     * @param offerta   la prestazione offerta in cambio (non può essere null)
     * @param stato     lo stato iniziale della proposta (non può essere null)
     * @throws NullPointerException se uno dei parametri è null
     *
     * @pre richiesta != null
     * @pre offerta != null
     * @pre stato != null
     * @post getRichiesta() == richiesta
     * @post getOfferta() == offerta
     * @post getStato() == stato
     */
    public Proposta(Prestazione richiesta, Prestazione offerta, StatoProposta stato) {
        this.richiesta = Objects.requireNonNull(richiesta, "La prestazione richiesta non può essere null");
        this.offerta = Objects.requireNonNull(offerta, "La prestazione offerta non può essere null");
        this.stato = Objects.requireNonNull(stato, "Lo stato della proposta non può essere null");
        this.dataCreazione = LocalDateTime.now();
        this.dataModifica = null;
    }


    /**
     * Tenta di chiudere questa proposta.
     * <p>
     * Una proposta può essere chiusa solo se è attualmente aperta.
     *
     * @return true se la proposta è stata chiusa, false se era in uno stato
     *         che non permette la chiusura
     *
     * @pre true (può essere invocato in qualsiasi stato)
     * @post risultato == true ⟹ this.stato == StatoProposta.CHIUSA && old(this.stato) == StatoProposta.APERTA
     * @post risultato == false ⟹ this.stato == old(this.stato) && old(this.stato) != StatoProposta.APERTA
     */
    public boolean chiudiProposta() {
        if (stato == StatoProposta.APERTA) {
            stato = StatoProposta.CHIUSA;
            this.dataModifica = LocalDateTime.now();
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
     *
     * @pre true (può essere invocato in qualsiasi stato)
     * @post risultato == true ⟹ this.stato == StatoProposta.RITIRATA && old(this.stato) == StatoProposta.APERTA
     * @post risultato == false ⟹ this.stato == old(this.stato) && old(this.stato) != StatoProposta.APERTA
     */
    public boolean ritiraProposta() {
        if (stato == StatoProposta.APERTA) {
            stato = StatoProposta.RITIRATA;
            this.dataModifica = LocalDateTime.now();
            return true;
        }
        return false;
    }

    /**
     * Verifica se questa proposta è ancora aperta per possibili accettazioni.
     *
     * @return true se la proposta è nello stato APERTA, false altrimenti
     *
     * @pre true
     * @post risultato == (this.stato == StatoProposta.APERTA)
     * @post this.stato == old(this.stato) (non modifica lo stato)
     */
    public boolean isAperta() {
        return stato == StatoProposta.APERTA;
    }

    /**
     * Restituisce la prestazione richiesta.
     *
     * @return la prestazione richiesta (mai null)
     *
     * @pre true
     * @post risultato != null
     * @post risultato == this.richiesta
     */
    public Prestazione getRichiesta() {
        return richiesta;
    }

    /**
     * Restituisce la prestazione offerta.
     *
     * @return la prestazione offerta (mai null)
     *
     * @pre true
     * @post risultato != null
     * @post risultato == this.offerta
     */
    public Prestazione getOfferta() {
        return offerta;
    }

    /**
     * Restituisce lo stato corrente della proposta.
     *
     * @return lo stato della proposta (mai null)
     *
     * @pre true
     * @post risultato != null
     * @post risultato == this.stato
     */
    public StatoProposta getStato() {
        return stato;
    }


    /**
     * Genera una rappresentazione testuale della proposta.
     *
     * @return una stringa contenente i dettagli della proposta
     *
     * @pre true
     * @post risultato != null
     * @post risultato contiene informazioni su richiesta, offerta e stato
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PROPOSTA:\n" +
                "Richiesta: " + richiesta.toString() +
                "\nOfferta: " + offerta.toString() +
                "\nStato: " + stato.toString() +
                "\nData creazione: "
                + dataCreazione.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        if (dataModifica != null) {
            sb.append("\nLa proposta è passata da " + StatoProposta.APERTA + " a " + stato + " il " +
                    dataModifica.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        }
        sb.append("\n");
        return sb.toString();
    }

    /**
     * Verifica se questa proposta è uguale a un altro oggetto.
     * <p>
     * Due proposte sono considerate uguali se hanno gli stessi valori per
     * richiesta, offerta e stato.
     *
     * @param obj l'oggetto da confrontare con questa proposta
     * @return true se l'oggetto rappresenta una proposta uguale a questa
     *
     * @pre true
     * @post risultato == true se e solo se obj instanceof Proposta &&
     *       Objects.equals(richiesta, ((Proposta)obj).richiesta) &&
     *       Objects.equals(offerta, ((Proposta)obj).offerta) &&
     *       stato == ((Proposta)obj).stato
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
     *
     * @pre true
     * @post se x.equals(y) == true, allora x.hashCode() == y.hashCode()
     * @post risultato == Objects.hash(richiesta, offerta, stato)
     */
    @Override
    public int hashCode() {
        return Objects.hash(richiesta, offerta, stato);
    }
}