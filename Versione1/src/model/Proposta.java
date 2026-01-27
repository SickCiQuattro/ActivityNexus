package model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import model.observers.PropostaObserver;

/**
 * Rappresenta una proposta di scambio di prestazioni d'opera nel sistema.
 * <p>
 * Una proposta è una richiesta formale di scambio che mette in relazione:
 * <ul>
 * <li>Una prestazione richiesta che il propostitore desidera ricevere</li>
 * <li>Una prestazione offerta che il propostitore è disposto a fornire in
 * cambio</li>
 * <li>Il fruitore che formula la proposta (propostitore)</li>
 * </ul>
 * <p>
 * <b>Ciclo di vita della proposta:</b>
 * <ol>
 * <li><b>APERTA</b>: stato iniziale quando la proposta viene inserita nel
 * sistema</li>
 * <li><b>CHIUSA</b>: quando la proposta viene accettata in un insieme
 * chiuso</li>
 * <li><b>RITIRATA</b>: quando il propostitore decide di ritirare la
 * proposta</li>
 * </ol>
 * <p>
 * Le transizioni di stato possibili sono solo da APERTA a CHIUSA o RITIRATA.
 * Una volta chiusa o ritirata, una proposta non può tornare allo stato aperto.
 *
 * <p>
 * <b>Invarianti della classe:</b>
 * <ul>
 * <li>propostitore != null (ogni proposta ha un fruitore associato)</li>
 * <li>richiesta != null (ogni proposta ha una prestazione richiesta)</li>
 * <li>offerta != null (ogni proposta ha una prestazione offerta)</li>
 * <li>stato != null (ogni proposta ha uno stato valido)</li>
 * <li>dataCreazione != null (ogni proposta ha una data di creazione)</li>
 * </ul>
 *
 * @see Prestazione
 * @see StatoProposta
 * @see Fruitore
 * @see InsiemeChiuso
 */
public class Proposta implements Serializable {
    // -----------------------------------------------------------------------------------
    // FIELDS E COSTANTI
    // -----------------------------------------------------------------------------------

    /** Identificatore di versione per la serializzazione */
    @Serial
    private static final long serialVersionUID = 1L;

    /** Fruitore che ha inviato la proposta */
    private final Fruitore propostitore;

    /** Prestazione richiesta dal propostitore */
    private final Prestazione richiesta;

    /** Prestazione offerta in cambio dal propostitore */
    private final Prestazione offerta;

    /** Stato corrente della proposta (APERTA, CHIUSA, RITIRATA) */
    private StatoProposta stato;

    /** Data e ora di creazione della proposta */
    private final LocalDateTime dataCreazione;

    /** Data e ora dell'ultima modifica di stato (null se ancora non modificata) */
    private LocalDateTime dataModifica;

    /**
     * Lista degli observer registrati per questa proposta.
     * <p>
     * PATTERN: Observer (GoF) + Open/Closed Principle (SOLID)
     * <p>
     * Gli observer vengono notificati quando la proposta cambia stato.
     * La lista è transient perché gli observer non devono essere serializzati.
     */
    private transient List<PropostaObserver> observers = new ArrayList<>();

    /** Messaggi di errore per validazione */
    private static final String ERRORE_FRUITORE_NULL = "Il fruitore non può essere null";
    private static final String ERRORE_RICHIESTA_NULL = "La prestazione richiesta non può essere null";
    private static final String ERRORE_OFFERTA_NULL = "La prestazione offerta non può essere null";
    private static final String ERRORE_STATO_NULL = "Lo stato della proposta non può essere null";

    // -----------------------------------------------------------------------------------
    // COSTRUTTORI
    // -----------------------------------------------------------------------------------

    /**
     * Crea una nuova proposta di scambio con i dettagli specificati.
     * <p>
     * La proposta viene inizializzata con la data di creazione corrente e
     * la data di modifica a null (verrà impostata al primo cambiamento di stato).
     *
     * @param propostitore il fruitore che ha inviato la proposta
     * @param richiesta    la prestazione richiesta
     * @param offerta      la prestazione offerta in cambio
     * @param stato        lo stato iniziale della proposta (solitamente APERTA)
     * @throws NullPointerException se uno dei parametri è null
     *
     * @pre propostitore != null
     * @pre richiesta != null
     * @pre offerta != null
     * @pre stato != null
     * @post getPropostitore() == propostitore
     * @post getRichiesta() == richiesta
     * @post getOfferta() == offerta
     * @post getStato() == stato
     * @post getDataCreazione() != null
     * @post getDataModifica() == null
     */
    public Proposta(Fruitore propostitore, Prestazione richiesta, Prestazione offerta, StatoProposta stato) {
        // Verifica che nessun parametro sia null, lancia NullPointerException con
        // messaggio descrittivo
        this.propostitore = Objects.requireNonNull(propostitore, ERRORE_FRUITORE_NULL);
        this.richiesta = Objects.requireNonNull(richiesta, ERRORE_RICHIESTA_NULL);
        this.offerta = Objects.requireNonNull(offerta, ERRORE_OFFERTA_NULL);
        this.stato = Objects.requireNonNull(stato, ERRORE_STATO_NULL);

        // Inizializza la data di creazione con il timestamp corrente
        this.dataCreazione = LocalDateTime.now();

        // La data di modifica è inizialmente null (non ci sono state modifiche)
        this.dataModifica = null;
    }

    // -----------------------------------------------------------------------------------
    // METODI DI ACCESSO (GETTERS)
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce il fruitore che ha inviato la proposta.
     *
     * @return il fruitore che ha inviato la proposta (mai null)
     *
     * @post result != null
     * @post result == this.propostitore
     */
    public Fruitore getPropostitore() {
        return this.propostitore;
    }

    /**
     * Restituisce la prestazione richiesta dal propostitore.
     *
     * @return la prestazione richiesta (mai null)
     *
     * @post result != null
     * @post result == this.richiesta
     */
    public Prestazione getRichiesta() {
        return richiesta;
    }

    /**
     * Restituisce la prestazione offerta dal propostitore.
     *
     * @return la prestazione offerta (mai null)
     *
     * @post result != null
     * @post result == this.offerta
     */
    public Prestazione getOfferta() {
        return offerta;
    }

    /**
     * Restituisce lo stato corrente della proposta.
     *
     * @return lo stato della proposta (mai null)
     *
     * @post result != null
     * @post result == this.stato
     */
    public StatoProposta getStato() {
        return stato;
    }

    // -----------------------------------------------------------------------------------
    // METODI INFORMATION EXPERT
    // -----------------------------------------------------------------------------------

    /**
     * Verifica se questa proposta è attualmente aperta.
     * <p>
     * PATTERN: Information Expert - La classe Proposta è l'esperto delle sue
     * informazioni
     * e quindi è responsabile di rispondere a domande sul proprio stato.
     *
     * @return true se la proposta è nello stato APERTA, false altrimenti
     *
     * @post result == (this.stato == StatoProposta.APERTA)
     */
    public boolean isAperta() {
        return this.stato == StatoProposta.APERTA;
    }

    /**
     * Verifica se questa proposta appartiene allo stesso comprensorio specificato.
     * <p>
     * PATTERN: Information Expert - La classe Proposta ha accesso diretto al
     * propostitore
     * e quindi può rispondere a domande sul comprensorio di appartenenza.
     *
     * @param comprensorio il comprensorio da confrontare
     * @return true se il propostitore appartiene al comprensorio specificato
     *
     * @pre comprensorio != null
     * @post result == this.propostitore.getComprensorio().equals(comprensorio)
     */
    public boolean appartieneStoComprensorio(Comprensorio comprensorio) {
        return this.propostitore.getComprensorio().equals(comprensorio);
    }

    /**
     * Verifica se questa proposta è stata creata dal fruitore specificato.
     * <p>
     * PATTERN: Information Expert - La classe Proposta conosce il proprio
     * propostitore
     * e può rispondere direttamente a questa domanda.
     *
     * @param fruitore il fruitore da confrontare
     * @return true se il fruitore specificato è il propostitore di questa proposta
     *
     * @pre fruitore != null
     * @post result == this.propostitore.equals(fruitore)
     */
    public boolean isDiPropostitore(Fruitore fruitore) {
        return this.propostitore.equals(fruitore);
    }

    // -----------------------------------------------------------------------------------
    // METODI DI CAMBIO STATO
    // -----------------------------------------------------------------------------------

    /**
     * Tenta di chiudere questa proposta.
     * <p>
     * Una proposta può essere chiusa solo se è attualmente aperta. Quando una
     * proposta
     * viene chiusa, la data di modifica viene aggiornata al timestamp corrente.
     * <p>
     * Il passaggio allo stato CHIUSA indica che la proposta è stata accettata e
     * inserita in un insieme chiuso di scambio.
     *
     * @pre true (può essere invocato in qualsiasi stato)
     * @post result == true ⟹ this.stato == StatoProposta.CHIUSA && la data di
     *       modifica è aggiornata
     * @post result == false ⟹ this.stato == old(this.stato) (nessun cambiamento)
     */
    public void chiudiProposta() {
        // Verifica che la proposta sia nello stato APERTA
        // Solo le proposte aperte possono essere chiuse
        if (stato == StatoProposta.APERTA) {
            // Modifica lo stato a CHIUSA
            stato = StatoProposta.CHIUSA;

            // Aggiorna la data di modifica con il timestamp corrente
            this.dataModifica = LocalDateTime.now();

            // PATTERN: Observer - Notifica tutti gli observer della chiusura
            notifyPropostaChiusa();
        }

        // Se la proposta non era aperta, l'operazione fallisce
    }

    /**
     * Tenta di ritirare questa proposta.
     * <p>
     * Una proposta può essere ritirata solo se è attualmente aperta. Quando una
     * proposta
     * viene ritirata, la data di modifica viene aggiornata al timestamp corrente.
     * <p>
     * Il passaggio allo stato RITIRATA indica che il propostitore ha deciso
     * di rimuovere la sua offerta dal sistema.
     *
     * @return true se la proposta è stata ritirata, false se era in uno stato
     *         che non permette il ritiro
     *
     * @pre true (può essere invocato in qualsiasi stato)
     * @post result == true ⟹ this.stato == StatoProposta.RITIRATA && la data di
     *       modifica è aggiornata
     * @post result == false ⟹ this.stato == old(this.stato) (nessun cambiamento)
     */
    public boolean ritiraProposta() {
        // Verifica che la proposta sia nello stato APERTA
        // Solo le proposte aperte possono essere ritirate
        if (stato == StatoProposta.APERTA) {
            // Modifica lo stato a RITIRATA
            stato = StatoProposta.RITIRATA;

            // Aggiorna la data di modifica con il timestamp corrente
            this.dataModifica = LocalDateTime.now();

            // PATTERN: Observer - Notifica tutti gli observer del ritiro
            notifyPropostaRitirata();

            return true; // Operazione riuscita
        }

        // Se la proposta non era aperta, l'operazione fallisce
        return false;
    }

    // -----------------------------------------------------------------------------------
    // METODI OBSERVER PATTERN
    // -----------------------------------------------------------------------------------

    /**
     * Registra un observer per ricevere notifiche sui cambiamenti di stato.
     * <p>
     * PATTERN: Observer (GoF) + Open/Closed Principle (SOLID)
     * <p>
     * Questo metodo permette di estendere il comportamento della proposta
     * senza modificare il codice esistente, rispettando il principio OCP.
     *
     * @param observer l'observer da registrare
     * @throws NullPointerException se observer è null
     */
    public void addObserver(PropostaObserver observer) {
        Objects.requireNonNull(observer, "Observer non può essere null");
        if (observers == null) {
            observers = new ArrayList<>();
        }
        observers.add(observer);
    }

    /**
     * Rimuove un observer dalla lista delle notifiche.
     *
     * @param observer l'observer da rimuovere
     */
    public void removeObserver(PropostaObserver observer) {
        if (observers != null) {
            observers.remove(observer);
        }
    }

    /**
     * Notifica tutti gli observer che la proposta è stata chiusa.
     * <p>
     * PATTERN: Observer - Metodo privato per la notifica
     */
    private void notifyPropostaChiusa() {
        if (observers != null) {
            for (PropostaObserver observer : observers) {
                observer.onPropostaChiusa(this);
            }
        }
    }

    /**
     * Notifica tutti gli observer che la proposta è stata ritirata.
     * <p>
     * PATTERN: Observer - Metodo privato per la notifica
     */
    private void notifyPropostaRitirata() {
        if (observers != null) {
            for (PropostaObserver observer : observers) {
                observer.onPropostaRitirata(this);
            }
        }
    }

    // -----------------------------------------------------------------------------------
    // METODI OVERRIDE DI OBJECT
    // -----------------------------------------------------------------------------------

    /**
     * Genera una rappresentazione testuale dettagliata della proposta.
     * <p>
     * Include informazioni su propostitore, prestazioni richieste/offerte,
     * stato attuale e storia delle modifiche.
     *
     * @return una stringa contenente i dettagli della proposta
     *
     * @post result != null
     * @post result contiene informazioni su propostitore, richiesta, offerta, stato
     *       e date
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // Intestazione e dettagli principali
        sb.append("PROPOSTA:\n")
                .append("Propositore: ").append(propostitore.getMail())
                .append("\nRichiesta: ").append(richiesta.toString())
                .append("\nOfferta: ").append(offerta.toString())
                .append("\nStato: ").append(stato.toString())
                .append("\nData creazione: ")
                .append(dataCreazione.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

        // Aggiunge informazioni sulla modifica di stato se presente
        if (dataModifica != null) {
            sb.append("\nLa proposta è passata da ").append(StatoProposta.APERTA)
                    .append(" a ").append(stato).append(" il ")
                    .append(dataModifica.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        }

        sb.append("\n");
        return sb.toString();
    }

    /**
     * Verifica se questa proposta è uguale a un altro oggetto.
     * <p>
     * Due proposte sono considerate uguali se hanno lo stesso propostitore,
     * prestazioni richieste/offerte identiche e lo stesso stato.
     *
     * @param obj l'oggetto da confrontare con questa proposta
     * @return true se l'oggetto rappresenta una proposta uguale a questa
     *
     * @post result == true se e solo se obj instanceof Proposta &&
     *       Objects.equals(propostitore, ((Proposta)obj).propostitore) &&
     *       Objects.equals(richiesta, ((Proposta)obj).richiesta) &&
     *       Objects.equals(offerta, ((Proposta)obj).offerta) &&
     *       stato == ((Proposta)obj).stato
     */
    @Override
    public boolean equals(Object obj) {
        // Verifica identità di riferimento (ottimizzazione)
        if (this == obj)
            return true;

        // Verifica tipo e non-nullità
        if (obj == null || getClass() != obj.getClass())
            return false;

        // Cast sicuro (verificato sopra) e confronto dei campi significativi
        Proposta other = (Proposta) obj;
        return Objects.equals(propostitore, other.propostitore) &&
                Objects.equals(richiesta, other.richiesta) &&
                Objects.equals(offerta, other.offerta) &&
                stato == other.stato;
    }

    /**
     * Calcola il codice hash per questa proposta.
     * <p>
     * Garantisce la coerenza con il metodo equals().
     *
     * @return il codice hash calcolato
     *
     * @post se x.equals(y) == true, allora x.hashCode() == y.hashCode()
     */
    @Override
    public int hashCode() {
        // Usa gli stessi campi considerati in equals()
        return Objects.hash(propostitore, richiesta, offerta, stato);
    }
}