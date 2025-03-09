package model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Rappresenta una prestazione d'opera associata a una categoria e
 * caratterizzata da una durata.
 * <p>
 * Una prestazione è definita da:
 * <ul>
 * <li>Una categoria foglia che ne identifica il tipo</li>
 * <li>Una durata in ore che ne quantifica l'impegno</li>
 * </ul>
 * <p>
 * Questa classe è immutabile e quindi thread-safe.
 * 
 * @see Categoria
 * @see Proposta
 */
public class Prestazione implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /** Categoria foglia che identifica il tipo di prestazione */
    private final Categoria categoria;

    /** Durata in ore della prestazione (valore positivo) */
    private final int ore;

    /**
     * Crea una nuova prestazione con la categoria e la durata specificate.
     *
     * @param categoria la categoria foglia della prestazione (non deve essere null)
     * @param ore       la durata in ore della prestazione (deve essere positiva)
     * @throws IllegalArgumentException se ore è un valore non positivo (≤ 0)
     * @throws NullPointerException     se categoria è null
     */
    public Prestazione(Categoria categoria, int ore) {
        this.categoria = Objects.requireNonNull(categoria, "La categoria non può essere null");
        if (ore <= 0) {
            throw new IllegalArgumentException("Le ore devono essere un valore positivo");
        }
        this.ore = ore;
    }

    /**
     * Restituisce la categoria foglia associata a questa prestazione.
     *
     * @return la categoria della prestazione (mai null)
     */
    public Categoria getCategoria() {
        return categoria;
    }

    /**
     * Restituisce la durata in ore della prestazione.
     *
     * @return il numero intero di ore (sempre positivo)
     */
    public int getOre() {
        return ore;
    }

    /**
     * Confronta questa prestazione con un altro oggetto.
     * <p>
     * Due prestazioni sono considerate uguali se hanno la stessa categoria e lo
     * stesso numero di ore.
     *
     * @param o l'oggetto da confrontare
     * @return true se l'oggetto è una Prestazione con stessa categoria e ore
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Prestazione that = (Prestazione) o;
        return ore == that.ore &&
                Objects.equals(categoria, that.categoria);
    }

    /**
     * Calcola l'hash code di questa prestazione.
     *
     * @return hash code basato su categoria e ore
     */
    @Override
    public int hashCode() {
        return Objects.hash(categoria, ore);
    }

    /**
     * Restituisce una rappresentazione testuale di questa prestazione.
     * <p>
     * Formato: "Nome categoria, X ore"
     *
     * @return una stringa che rappresenta questa prestazione
     */
    @Override
    public String toString() {
        return categoria.getNome() + ", " + ore + " ore";
    }
}