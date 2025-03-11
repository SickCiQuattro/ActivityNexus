package model;

import java.io.Serializable;
import com.scalified.tree.TreeNode;

/**
 * Rappresenta uno scambio di prestazione tra due fruitori.
 * <p>
 * Contiene informazioni sul fornitore, sul ricevente e sulla prestazione
 * scambiata.
 */
public class Scambio implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Fruitore fornitore;
    private final Fruitore ricevente;
    private final Prestazione prestazione;

    /**
     * Crea un nuovo scambio.
     * 
     * @param fornitore   il fruitore che fornisce la prestazione
     * @param ricevente   il fruitore che riceve la prestazione
     * @param prestazione la prestazione scambiata
     */
    public Scambio(Fruitore fornitore, Fruitore ricevente, Prestazione prestazione) {
        this.fornitore = fornitore;
        this.ricevente = ricevente;
        this.prestazione = prestazione;
    }

    /**
     * Restituisce il fruitore che fornisce la prestazione.
     * 
     * @return il fornitore
     */
    public Fruitore getFornitore() {
        return fornitore;
    }

    /**
     * Restituisce il fruitore che riceve la prestazione.
     * 
     * @return il ricevente
     */
    public Fruitore getRicevente() {
        return ricevente;
    }

    /**
     * Restituisce la prestazione scambiata.
     * 
     * @return la prestazione
     */
    public Prestazione getPrestazione() {
        return prestazione;
    }

    @Override
    public String toString() {
        return String.format("%s fornisce %s ore di %s a %s",
                fornitore.getUsername(),
                prestazione.getOre(),
                prestazione.getCategoria().data().getNome(),
                ricevente.getUsername());
    }
}