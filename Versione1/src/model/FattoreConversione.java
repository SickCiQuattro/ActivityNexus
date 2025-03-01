package model;

import com.scalified.tree.TreeNode;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Classe che gestisce i fattori di conversione tra diversi livelli di una
 * gerarchia.
 * <p>
 * Permette di definire un nodo di riferimento e di impostare fattori di
 * conversione
 * tra il nodo di riferimento e altri nodi della gerarchia. Fornisce inoltre
 * metodi
 * per calcolare fattori di conversione tra qualsiasi coppia di nodi.
 * <p>
 * ATTENZIONE: i metodi che accettano parametri numerici si aspettano che venga
 * passato
 * il livello del nodo (nodo.level()) e non il nodo stesso.
 */
public class FattoreConversione implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String NODO_RIFERIMENTO_NON_NULL = "Il nodo di riferimento non può essere null";
    public static final String LIVELLO_RIFERIMENTO_MINIMO = "Il livello di riferimento deve essere almeno 1";
    public static final String FATTORE_CONVERSIONE_POSITIVO = "Il fattore di conversione deve essere positivo";
    public static final String LIVELLO_RIFERIMENTO_NON_AGGIUNGIBILE = "Non è possibile aggiungere un fattore per il livello di riferimento";
    public static final String LIVELLO_RIFERIMENTO_NON_RIMOVIBILE = "Non è possibile rimuovere il fattore per il livello di riferimento";

    /** Il nodo foglia che serve come riferimento per le conversioni */
    private TreeNode<Categoria> fogliaRiferimento;

    /** Il livello nella gerarchia del nodo di riferimento */
    private int livelloRiferimento = 1;

    /**
     * Mappa che associa i livelli ai fattori di conversione rispetto al nodo di
     * riferimento
     */
    private final Map<Integer, Double> fattoriConversione = new HashMap<>();

    // -----------------------------------------------------------------------------------
    // METODI DI GESTIONE DEL NODO DI RIFERIMENTO
    // -----------------------------------------------------------------------------------

    /**
     * Imposta il nodo di riferimento per le conversioni.
     * Aggiorna sia il nodo che il suo livello.
     *
     * @param fogliaRiferimento il nodo da usare come riferimento per le conversioni
     * @throws NullPointerException se fogliaRiferimento è null
     */
    public void setNodoRiferimento(TreeNode<Categoria> fogliaRiferimento) {
        this.fogliaRiferimento = Objects.requireNonNull(fogliaRiferimento,
                NODO_RIFERIMENTO_NON_NULL);
        this.setLivelloRiferimento(fogliaRiferimento.level());
    }

    /**
     * Imposta il livello di riferimento per le conversioni.
     *
     * @param livelloRiferimento il livello da usare come riferimento
     * @throws IllegalArgumentException se il livello è inferiore a 1
     */
    public void setLivelloRiferimento(int livelloRiferimento) {
        if (livelloRiferimento < 1) {
            throw new IllegalArgumentException(LIVELLO_RIFERIMENTO_MINIMO);
        }
        this.livelloRiferimento = livelloRiferimento;
    }

    /**
     * Restituisce il nodo foglia utilizzato come riferimento per le conversioni.
     *
     * @return il nodo foglia di riferimento, può essere null se non è stato
     *         impostato
     */
    public TreeNode<Categoria> getFogliaRiferimento() {
        return fogliaRiferimento;
    }

    /**
     * Restituisce il livello del nodo di riferimento.
     *
     * @return il livello del nodo di riferimento
     */
    public int getLivelloRiferimento() {
        return livelloRiferimento;
    }

    // -----------------------------------------------------------------------------------
    // METODI DI GESTIONE DEI FATTORI DI CONVERSIONE
    // -----------------------------------------------------------------------------------

    /**
     * Verifica se un fattore di conversione può essere aggiunto per un dato
     * livello.
     * Un fattore può essere aggiunto se il livello è diverso dal livello di
     * riferimento
     * e se non esiste già un fattore per quel livello.
     *
     * @param livello il livello per cui verificare la possibilità di aggiungere un
     *                fattore
     * @return true se è possibile aggiungere un fattore, false altrimenti
     */
    public boolean isAggiungibileFattoreConversione(int livello) {
        return livello != livelloRiferimento && !fattoriConversione.containsKey(livello);
    }

    /**
     * Verifica se esiste già un fattore di conversione per un dato livello.
     *
     * @param livello il livello da verificare
     * @return true se esiste un fattore per il livello specificato, false
     *         altrimenti
     */
    public boolean hasFattoreConversione(int livello) {
        return livello == livelloRiferimento || fattoriConversione.containsKey(livello);
    }

    /**
     * Aggiunge o aggiorna un fattore di conversione per un livello specifico.
     * Il fattore rappresenta la conversione dal livello di riferimento al livello
     * specificato.
     *
     * @param livello il livello per cui impostare il fattore di conversione
     * @param fattore il valore del fattore di conversione
     * @throws IllegalArgumentException se il fattore è minore o uguale a zero o se
     *                                  il livello è
     *                                  uguale al livello di riferimento
     */
    public void addFattoreConversione(int livello, double fattore) {
        if (fattore <= 0) {
            throw new IllegalArgumentException(FATTORE_CONVERSIONE_POSITIVO);
        }
        if (livello == livelloRiferimento) {
            throw new IllegalArgumentException(LIVELLO_RIFERIMENTO_NON_AGGIUNGIBILE);
        }

        fattoriConversione.put(livello, fattore);
    }

    /**
     * Rimuove un fattore di conversione per un livello specifico.
     *
     * @param livello il livello per cui rimuovere il fattore
     * @return true se il fattore è stato rimosso, false se non esisteva
     * @throws IllegalArgumentException se il livello è uguale al livello di
     *                                  riferimento
     */
    public boolean removeFattoreConversione(int livello) {
        if (livello == livelloRiferimento) {
            throw new IllegalArgumentException(LIVELLO_RIFERIMENTO_NON_RIMOVIBILE);
        }

        return fattoriConversione.remove(livello) != null;
    }

    /**
     * Restituisce il fattore di conversione diretto per un livello specifico.
     * Il fattore restituito è relativo al livello di riferimento.
     *
     * @param livello il livello per cui ottenere il fattore
     * @return il fattore di conversione o 1.0 se è il livello di riferimento, null
     *         se non esiste
     */
    public Double getFattoreDiretto(int livello) {
        if (livello == livelloRiferimento) {
            return 1.0;
        }
        return fattoriConversione.get(livello);
    }

    /**
     * Calcola il fattore di conversione tra due livelli qualsiasi.
     * <p>
     * La conversione viene calcolata come:
     * - Se il primo livello è il riferimento: restituisce il fattore diretto del
     * secondo
     * - Se il secondo livello è il riferimento: restituisce l'inverso del fattore
     * del primo
     * - Altrimenti: restituisce il rapporto tra i due fattori
     *
     * @param primoLivello   il livello di partenza
     * @param secondoLivello il livello di arrivo
     * @return il fattore di conversione calcolato, null se almeno uno dei livelli
     *         non ha un fattore definito
     */
    public Double getFattoreConversione(int primoLivello, int secondoLivello) {
        // Se i livelli sono uguali, il fattore è 1
        if (primoLivello == secondoLivello) {
            return 1.0;
        }

        // Caso 1: Dal livello di riferimento a un altro livello
        if (primoLivello == livelloRiferimento) {
            return getFattoreDiretto(secondoLivello);
        }
        // Caso 2: Da un livello al livello di riferimento
        else if (secondoLivello == livelloRiferimento) {
            Double fattorePrimo = getFattoreDiretto(primoLivello);
            return fattorePrimo != null ? 1.0 / fattorePrimo : null;
        }
        // Caso 3: Da un livello a un altro livello (via livello di riferimento)
        else {
            Double fattorePrimo = getFattoreDiretto(primoLivello);
            Double fattoreSecondo = getFattoreDiretto(secondoLivello);

            if (fattorePrimo != null && fattoreSecondo != null) {
                return fattoreSecondo / fattorePrimo;
            } else {
                return null;
            }
        }
    }

    /**
     * Rimuove tutti i fattori di conversione.
     * Il livello di riferimento viene mantenuto.
     */
    public void clearFattori() {
        fattoriConversione.clear();
    }

    /**
     * Restituisce il numero di fattori di conversione memorizzati.
     * Il livello di riferimento non viene contato.
     *
     * @return il numero di fattori di conversione
     */
    public int getNumeroFattori() {
        return fattoriConversione.size();
    }
}