package model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Rappresenta uno scambio di prestazione tra due fruitori.
 * <p>
 * Questa classe modella il trasferimento di una prestazione da un fruitore
 * (fornitore)
 * a un altro (ricevente) all'interno dello stesso comprensorio geografico. Gli
 * scambi
 * sono elementi fondamentali degli insiemi chiusi e rappresentano il punto
 * finale
 * del processo di matching delle proposte.
 * <p>
 * Ogni scambio può essere collegato alla proposta originale che ha contribuito
 * alla sua creazione, mantenendo così la tracciabilità del processo di scambio.
 * <p>
 * <b>Invarianti della classe:</b>
 * <ul>
 * <li>fornitore != null (ogni scambio ha un fruitore che offre la
 * prestazione)</li>
 * <li>ricevente != null (ogni scambio ha un fruitore che riceve la
 * prestazione)</li>
 * <li>prestazione != null (ogni scambio coinvolge una prestazione
 * concreta)</li>
 * <li>fornitore e ricevente sono fruitori distinti (non è ammesso
 * l'auto-scambio)</li>
 * <li>se entrambi hanno un comprensorio, fornitore e ricevente appartengono
 * allo stesso comprensorio</li>
 * </ul>
 *
 * @see Fruitore
 * @see Prestazione
 * @see Proposta
 * @see InsiemeChiuso
 */
public class Scambio implements Serializable {
    // -----------------------------------------------------------------------------------
    // FIELDS E COSTANTI
    // -----------------------------------------------------------------------------------

    /** Identificatore di versione per la serializzazione */
    @Serial
    private static final long serialVersionUID = 1L;

    /** Messaggi di errore per validazione */
    private static final String ERRORE_FORNITORE_NULL = "Il fornitore non può essere null";
    private static final String ERRORE_RICEVENTE_NULL = "Il ricevente non può essere null";
    private static final String ERRORE_PRESTAZIONE_NULL = "La prestazione non può essere null";
    private static final String ERRORE_COMPRENSORI_DIVERSI = "Fornitore e ricevente devono appartenere allo stesso comprensorio. ";
    private static final String ERRORE_AUTO_SCAMBIO = "Il fornitore e il ricevente non possono essere lo stesso fruitore";

    /** Il fruitore che fornisce la prestazione */
    private final Fruitore fornitore;

    /** Il fruitore che riceve la prestazione */
    private final Fruitore ricevente;

    /** La prestazione scambiata */
    private final Prestazione prestazione;

    // -----------------------------------------------------------------------------------
    // COSTRUTTORI
    // -----------------------------------------------------------------------------------

    /**
     * Crea un nuovo scambio collegandolo alla proposta di origine.
     * <p>
     * Verifica che i fruitori appartengano allo stesso comprensorio e che i
     * parametri
     * essenziali non siano null. La proposta di origine può essere null.
     * 
     * @param fornitore       il fruitore che fornisce la prestazione
     * @param ricevente       il fruitore che riceve la prestazione
     * @param prestazione     la prestazione scambiata
     * @throws NullPointerException     se fornitore, ricevente o prestazione sono
     *                                  null
     * @throws IllegalArgumentException se i fruitori appartengono a comprensori
     *                                  diversi
     *                                  o se fornitore e ricevente sono lo stesso
     *                                  fruitore
     *
     * @pre fornitore != null
     * @pre ricevente != null
     * @pre prestazione != null
     * @pre fornitore != ricevente
     * @pre se fornitore.getComprensorio() != null e ricevente.getComprensorio() !=
     *      null,
     *      allora fornitore.getComprensorio().equals(ricevente.getComprensorio())
     * @post getFornitore() == fornitore
     * @post getRicevente() == ricevente
     * @post getPrestazione() == prestazione
     * @post getPropostaOrigine() == propostaOrigine
     */
    public Scambio(Fruitore fornitore, Fruitore ricevente, Prestazione prestazione) {
        // Verifica che i parametri essenziali non siano null
        this.fornitore = Objects.requireNonNull(fornitore, ERRORE_FORNITORE_NULL);
        this.ricevente = Objects.requireNonNull(ricevente, ERRORE_RICEVENTE_NULL);
        this.prestazione = Objects.requireNonNull(prestazione, ERRORE_PRESTAZIONE_NULL);

        // La proposta di origine può essere null (scambio creato manualmente)

        // Verifica che i fruitori non siano lo stesso fruitore
        if (fornitore.equals(ricevente)) {
            throw new IllegalArgumentException(ERRORE_AUTO_SCAMBIO);
        }

        // Verifica che i fruitori appartengano allo stesso comprensorio
        verificaStessoComprensorio();
    }

    // -----------------------------------------------------------------------------------
    // METODI DI VERIFICA (PRIVATI)
    // -----------------------------------------------------------------------------------

    /**
     * Verifica che fornitore e ricevente appartengano allo stesso comprensorio.
     * <p>
     * Il controllo viene eseguito solo se entrambi i fruitori hanno un comprensorio
     * assegnato. Se uno o entrambi non hanno comprensorio, il vincolo è considerato
     * soddisfatto.
     * 
     * @throws IllegalArgumentException se i fruitori appartengono a comprensori
     *                                  diversi
     *
     * @pre fornitore != null
     * @pre ricevente != null
     */
    private void verificaStessoComprensorio() {
        // Verifica se entrambi i fruitori hanno un comprensorio assegnato
        if (fornitore.getComprensorio() != null && ricevente.getComprensorio() != null) {
            // Verifica che i comprensori siano uguali
            if (!fornitore.getComprensorio().equals(ricevente.getComprensorio())) {
                // Costruisce un messaggio di errore dettagliato
                String errore = ERRORE_COMPRENSORI_DIVERSI +
                        "Fornitore: " + fornitore.getUsername() + " (" +
                        fornitore.getComprensorio().getNome() + "), " +
                        "Ricevente: " + ricevente.getUsername() + " (" +
                        ricevente.getComprensorio().getNome() + ")";

                throw new IllegalArgumentException(errore);
            }
        }
        // Se uno o entrambi non hanno comprensorio, non facciamo alcuna verifica
    }

    // -----------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce il fruitore che fornisce la prestazione.
     * 
     * @return il fornitore della prestazione (mai null)
     *
     * @post result != null
     * @post result == this.fornitore
     */
    public Fruitore getFornitore() {
        return fornitore;
    }

    /**
     * Restituisce il fruitore che riceve la prestazione.
     * 
     * @return il ricevente della prestazione (mai null)
     *
     * @post result != null
     * @post result == this.ricevente
     */
    public Fruitore getRicevente() {
        return ricevente;
    }

    /**
     * Restituisce la prestazione scambiata.
     * 
     * @return la prestazione oggetto dello scambio (mai null)
     *
     * @post result != null
     * @post result == this.prestazione
     */
    public Prestazione getPrestazione() {
        return prestazione;
    }

    /**
     * Restituisce il comprensorio a cui appartengono i fruitori coinvolti.
     * <p>
     * Se i fruitori hanno un comprensorio assegnato, questo è garantito essere
     * lo stesso per entrambi a seguito dei controlli nel costruttore.
     * 
     * @return il comprensorio comune dei fruitori, o null se nessuno dei fruitori
     *         ha un comprensorio
     *
     * @post se fornitore.getComprensorio() != null, result ==
     *       fornitore.getComprensorio()
     * @post se fornitore.getComprensorio() == null, result ==
     *       ricevente.getComprensorio()
     */
    public Comprensorio getComprensorio() {
        // Restituisce il comprensorio del fornitore, che è garantito essere
        // lo stesso del ricevente se entrambi ne hanno uno
        return fornitore.getComprensorio();
    }

    // -----------------------------------------------------------------------------------
    // METODI DI UTILITÀ
    // -----------------------------------------------------------------------------------

    // -----------------------------------------------------------------------------------
    // METODI DI VISUALIZZAZIONE
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce una rappresentazione testuale standard dello scambio.
     * <p>
     * Formato: "[fornitore] fornisce [ore] ore di [categoria] a [ricevente]"
     *
     * @return una stringa che rappresenta lo scambio
     *
     * @post result != null
     * @post result contiene username del fornitore e ricevente, ore e nome della
     *       categoria
     */
    @Override
    public String toString() {
        return String.format("%s fornisce %s ore di %s a %s",
                fornitore.getUsername(),
                prestazione.getOre(),
                prestazione.getCategoria().data().getNome(),
                ricevente.getUsername());
    }

    // -----------------------------------------------------------------------------------
    // OVERRIDE DI OBJECT
    // -----------------------------------------------------------------------------------

    /**
     * Confronta questo scambio con un altro oggetto.
     * <p>
     * Due scambi sono considerati uguali se coinvolgono gli stessi fruitori
     * (fornitore e ricevente) e la stessa prestazione. La proposta di origine
     * non influisce sull'uguaglianza.
     * 
     * @param obj l'oggetto da confrontare
     * @return true se l'oggetto è uno Scambio equivalente a questo
     *
     * @post result == true se e solo se obj instanceof Scambio &&
     *       Objects.equals(fornitore, ((Scambio)obj).fornitore) &&
     *       Objects.equals(ricevente, ((Scambio)obj).ricevente) &&
     *       Objects.equals(prestazione, ((Scambio)obj).prestazione)
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
        Scambio other = (Scambio) obj;
        return Objects.equals(fornitore, other.fornitore) &&
                Objects.equals(ricevente, other.ricevente) &&
                Objects.equals(prestazione, other.prestazione);
    }

    /**
     * Calcola l'hash code per questo scambio.
     * <p>
     * Usa gli stessi campi considerati nel metodo equals() per garantire coerenza.
     * 
     * @return il codice hash calcolato
     *
     * @post se x.equals(y) == true, allora x.hashCode() == y.hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(fornitore, ricevente, prestazione);
    }
}