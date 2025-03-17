package model;

import com.scalified.tree.TreeNode;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Rappresenta una prestazione d'opera associata a una categoria e
 * caratterizzata da una durata.
 * <p>
 * Una prestazione è definita da:
 * <ul>
 * <li>Una categoria foglia che ne identifica il tipo specifico di attività</li>
 * <li>Una durata in ore che ne quantifica l'impegno temporale</li>
 * </ul>
 * <p>
 * Questa classe è immutabile e quindi thread-safe: una volta creata una
 * prestazione,
 * non è possibile modificarne né la categoria né la durata. Ciò garantisce che
 * le istanze
 * di questa classe possano essere condivise in modo sicuro tra diversi thread e
 * componenti.
 *
 * <p>
 * <b>Invarianti della classe:</b>
 * <ul>
 * <li>categoria != null (una prestazione ha sempre una categoria
 * associata)</li>
 * <li>ore > 0 (una prestazione ha sempre una durata positiva)</li>
 * </ul>
 *
 * @see Categoria
 * @see Proposta
 * @see Scambio
 */
public class Prestazione implements Serializable {
    // -----------------------------------------------------------------------------------
    // FIELDS E COSTANTI
    // -----------------------------------------------------------------------------------

    /** Identificatore di versione per la serializzazione */
    @Serial
    private static final long serialVersionUID = 1L;

    /** Messaggi di errore per validazione */
    private static final String CATEGORIA_NON_NULL = "La categoria non può essere null";
    private static final String ORE_POSITIVE = "Le ore devono essere un valore positivo";

    /** Categoria foglia che identifica il tipo di prestazione */
    private final TreeNode<Categoria> categoria;

    /** Durata in ore della prestazione (valore positivo) */
    private final int ore;

    // -----------------------------------------------------------------------------------
    // COSTRUTTORI
    // -----------------------------------------------------------------------------------

    /**
     * Crea una nuova prestazione con la categoria e la durata specificate.
     * <p>
     * La categoria deve rappresentare un'attività concreta (categoria foglia)
     * e la durata deve essere un valore positivo.
     *
     * @param categoria la categoria foglia della prestazione
     * @param ore       la durata in ore della prestazione
     * @throws IllegalArgumentException se ore è un valore non positivo (≤ 0)
     * @throws NullPointerException     se categoria è null
     *
     * @pre categoria != null
     * @pre ore > 0
     * @post getCategoria() == categoria
     * @post getOre() == ore
     */
    public Prestazione(TreeNode<Categoria> categoria, int ore) {
        // Verifica che la categoria non sia null (lancia NullPointerException con
        // messaggio)
        this.categoria = Objects.requireNonNull(categoria, CATEGORIA_NON_NULL);

        // Verifica che le ore siano un valore positivo
        if (ore <= 0) {
            throw new IllegalArgumentException(ORE_POSITIVE);
        }

        this.ore = ore;
    }

    // -----------------------------------------------------------------------------------
    // METODI DI ACCESSO (GETTERS)
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce la categoria foglia associata a questa prestazione.
     * <p>
     * La categoria identifica il tipo specifico di attività offerta o richiesta.
     *
     * @return la categoria della prestazione (mai null)
     *
     * @post result != null
     * @post result == this.categoria
     */
    public TreeNode<Categoria> getCategoria() {
        return categoria;
    }

    /**
     * Restituisce la durata in ore della prestazione.
     * <p>
     * La durata rappresenta l'impegno temporale richiesto per completare
     * la prestazione.
     *
     * @return il numero intero di ore (sempre positivo)
     *
     * @post result > 0
     * @post result == this.ore
     */
    public int getOre() {
        return ore;
    }

    // -----------------------------------------------------------------------------------
    // METODI OVERRIDE DI OBJECT
    // -----------------------------------------------------------------------------------

    /**
     * Confronta questa prestazione con un altro oggetto.
     * <p>
     * Due prestazioni sono considerate uguali se hanno la stessa categoria e lo
     * stesso numero di ore.
     *
     * @param o l'oggetto da confrontare
     * @return true se l'oggetto è una Prestazione con stessa categoria e ore
     *
     * @post result == true se e solo se o instanceof Prestazione &&
     *       this.ore == ((Prestazione)o).ore &&
     *       this.categoria.equals(((Prestazione)o).categoria)
     */
    @Override
    public boolean equals(Object o) {
        // Verifica identità di riferimento (ottimizzazione)
        if (this == o)
            return true;

        // Verifica tipo e non-nullità
        if (o == null || getClass() != o.getClass())
            return false;

        // Cast sicuro (verificato sopra) e confronto dei campi
        Prestazione that = (Prestazione) o;
        return ore == that.ore &&
                Objects.equals(categoria, that.categoria);
    }

    /**
     * Calcola l'hash code di questa prestazione.
     * <p>
     * Combina gli hash code di categoria e ore per mantenere la coerenza
     * con il metodo equals().
     *
     * @return hash code basato su categoria e ore
     *
     * @post se x.equals(y) == true, allora x.hashCode() == y.hashCode()
     */
    @Override
    public int hashCode() {
        // Usa Objects.hash per calcolare un hash combinato dei due campi
        // garantendo la coerenza con equals()
        return Objects.hash(categoria, ore);
    }

    /**
     * Restituisce una rappresentazione testuale di questa prestazione.
     * <p>
     * Formato: "Nome categoria, X ore"
     * Esempio: "Ripetizioni matematica, 2 ore"
     *
     * @return una stringa che rappresenta questa prestazione
     *
     * @post result != null
     * @post result contiene il nome della categoria e il numero di ore
     */
    @Override
    public String toString() {
        // Formato conciso per una visualizzazione leggibile della prestazione
        // che mostra il nome della categoria e le ore richieste
        return categoria.data().getNome() + ", " + ore + " ore";
    }
}