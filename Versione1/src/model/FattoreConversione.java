package model;

import com.scalified.tree.TreeNode;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Gestisce i fattori di conversione tra diversi livelli di una gerarchia di
 * categorie.
 * <p>
 * Questa classe implementa un sistema di equivalenze quantitative tra categorie
 * posizionate a diversi livelli di una gerarchia. Il modello si basa su un
 * "nodo foglia
 * di riferimento" rispetto al quale vengono calcolate tutte le conversioni.
 * <p>
 * <b>Concetti chiave:</b>
 * <ul>
 * <li><b>Nodo foglia di riferimento</b>: Categoria foglia che rappresenta
 * l'unità base
 * rispetto alla quale vengono definite tutte le conversioni</li>
 * <li><b>Livello di riferimento</b>: Profondità del nodo di riferimento nella
 * gerarchia
 * (numerata a partire da 1 per la radice)</li>
 * <li><b>Fattore di conversione</b>: Quante unità di una categoria a un certo
 * livello
 * equivalgono a una singola unità della categoria di riferimento</li>
 * </ul>
 *
 * <p>
 * <b>Invarianti della classe:</b>
 * <ul>
 * <li>La mappa dei fattori di conversione non è mai null</li>
 * <li>Tutti i fattori di conversione memorizzati sono positivi (> 0)</li>
 * <li>Il livello di riferimento è sempre maggiore o uguale a 1</li>
 * </ul>
 *
 * @see TreeNode
 * @see Categoria
 * @see ConversioneGerarchie
 */
public class FattoreConversione implements Serializable {
    // -----------------------------------------------------------------------------------
    // FIELDS E COSTANTI
    // -----------------------------------------------------------------------------------

    /** Identificatore di versione per la serializzazione */
    @Serial
    private static final long serialVersionUID = 1L;

    /** Messaggi di errore per validazione */
    public static final String NODO_RIFERIMENTO_NON_NULL = "Il nodo di riferimento non può essere null";
    public static final String LIVELLO_RIFERIMENTO_MINIMO = "Il livello di riferimento deve essere almeno 1";
    public static final String FATTORE_CONVERSIONE_POSITIVO = "Il fattore di conversione deve essere positivo";
    public static final String LIVELLO_RIFERIMENTO_NON_AGGIUNGIBILE = "Non è possibile aggiungere un fattore per il livello di riferimento";

    /** Nodo foglia che serve come punto di riferimento per tutte le conversioni */
    private TreeNode<Categoria> fogliaRiferimento;

    /**
     * Livello nella gerarchia del nodo di riferimento (numerato a partire da 1)
     * Il livello 1 corrisponde alla radice
     */
    private int livelloRiferimento = 1;

    /**
     * Mappa che associa livelli a fattori di conversione.
     * <p>
     * Chiave: livello nella gerarchia.
     * Valore: quante unità del livello equivalgono a un'unità del riferimento.
     */
    private final Map<Integer, Double> fattoriConversione = new HashMap<>();

    // -----------------------------------------------------------------------------------
    // METODI DI ACCESSO AL NODO E LIVELLO DI RIFERIMENTO
    // -----------------------------------------------------------------------------------

    /**
     * Imposta il nodo foglia di riferimento per il sistema di conversione.
     * <p>
     * Il metodo aggiorna automaticamente anche il livello di riferimento al livello
     * del nodo fornito.
     *
     * @param fogliaRiferimento il nodo da usare come riferimento
     * @throws NullPointerException se fogliaRiferimento è null
     *
     * @pre fogliaRiferimento != null
     * @post getFogliaRiferimento() == fogliaRiferimento
     * @post il livelloRiferimento è impostato al livello del nodo fornito
     */
    public void setNodoRiferimento(TreeNode<Categoria> fogliaRiferimento) {
        // Verifica che il nodo non sia null, utilizzando Objects.requireNonNull
        // che lancia un'eccezione NullPointerException con il messaggio specificato
        this.fogliaRiferimento = Objects.requireNonNull(fogliaRiferimento,
                NODO_RIFERIMENTO_NON_NULL);

        // Imposta automaticamente il livello di riferimento in base al livello del nodo
        // Questo garantisce coerenza tra nodo e livello di riferimento
        this.setLivelloRiferimento(fogliaRiferimento.level());
    }

    /**
     * Restituisce il nodo foglia utilizzato come riferimento per le conversioni.
     *
     * @return il nodo foglia di riferimento, può essere null
     *
     * @post result può essere null se non è stato ancora impostato un nodo di
     *       riferimento
     */
    public TreeNode<Categoria> getFogliaRiferimento() {
        return fogliaRiferimento;
    }

    /**
     * Imposta il livello di riferimento per le conversioni.
     * <p>
     * Il livello di riferimento rappresenta la profondità nella gerarchia
     * rispetto alla quale verranno calcolati i fattori di conversione.
     *
     * @param livelloRiferimento il livello da usare come riferimento
     * @throws IllegalArgumentException se il livello è inferiore a 1
     *
     * @pre livelloRiferimento >= 1
     * @post this.livelloRiferimento == livelloRiferimento
     */
    public void setLivelloRiferimento(int livelloRiferimento) {
        // Verifica che il livello sia valido (almeno 1, poiché i livelli partono da 1)
        if (livelloRiferimento < 1) {
            throw new IllegalArgumentException(LIVELLO_RIFERIMENTO_MINIMO);
        }

        // Imposta il nuovo livello di riferimento
        this.livelloRiferimento = livelloRiferimento;
    }

    // -----------------------------------------------------------------------------------
    // METODI DI GESTIONE DEI FATTORI DI CONVERSIONE
    // -----------------------------------------------------------------------------------

    /**
     * Aggiunge un fattore di conversione per un livello specifico.
     * <p>
     * Il fattore rappresenta quante unità del livello specificato equivalgono
     * a un'unità del livello di riferimento.
     *
     * @param livello il livello per cui definire il fattore
     * @param fattore il valore del fattore di conversione
     * @throws IllegalArgumentException se fattore ≤ 0 o livello =
     *                                  livelloRiferimento
     *
     * @pre fattore > 0
     * @pre livello != livelloRiferimento
     * @post getFattoreDiretto(livello) == fattore
     */
    public void addFattoreConversione(int livello, double fattore) {
        // Il fattore deve essere positivo (una conversione non può essere nulla o
        // negativa)
        if (fattore <= 0) {
            throw new IllegalArgumentException(FATTORE_CONVERSIONE_POSITIVO);
        }

        // Non è possibile impostare un fattore per il livello di riferimento
        // (per definizione, il fattore di conversione è sempre 1.0 per il livello di
        // riferimento)
        if (livello == livelloRiferimento) {
            throw new IllegalArgumentException(LIVELLO_RIFERIMENTO_NON_AGGIUNGIBILE);
        }

        // Memorizza il fattore nella mappa, sovrascrivendo eventuali valori precedenti
        fattoriConversione.put(livello, fattore);
    }

    /**
     * Restituisce il fattore di conversione diretto di un livello rispetto al
     * riferimento.
     * <p>
     * Per il livello di riferimento restituisce sempre 1.0 per definizione.
     *
     * @param livello il livello per cui ottenere il fattore
     * @return il fattore diretto, 1.0 per il riferimento, null se non definito
     *
     * @post se livello == livelloRiferimento, result == 1.0
     * @post se livello != livelloRiferimento e esiste un fattore per livello,
     *       result == tale fattore
     * @post se livello != livelloRiferimento e non esiste un fattore per livello,
     *       result == null
     */
    public Double getFattoreDiretto(int livello) {
        // Per il livello di riferimento, il fattore è sempre 1.0 per definizione
        // indipendentemente da eventuali valori memorizzati nella mappa
        if (livello == livelloRiferimento) {
            return 1.0;
        }

        // Per gli altri livelli, cerca il fattore nella mappa
        // (restituisce null se il livello non ha un fattore definito)
        return fattoriConversione.get(livello);
    }

    /**
     * Calcola il fattore di conversione tra due livelli qualsiasi.
     * <p>
     * Questo metodo è il cuore del sistema di conversione e gestisce tutti i
     * possibili
     * casi di conversione:
     *
     * <ul>
     * <li><b>Livelli identici</b>: Restituisce 1.0 (fattore identità)</li>
     * <li><b>Dal riferimento ad altro livello</b>: Usa il fattore diretto</li>
     * <li><b>Da altro livello al riferimento</b>: Calcola l'inverso del fattore
     * diretto</li>
     * <li><b>Tra due livelli diversi dal riferimento</b>: Calcola il rapporto tra i
     * loro
     * fattori diretti rispetto al riferimento</li>
     * </ul>
     *
     * @param livelloPartenza il livello di partenza per la conversione
     * @param livelloArrivo   il livello di destinazione per la conversione
     * @return il fattore calcolato, null se mancano i fattori necessari
     *
     * @post se livelloPartenza == livelloArrivo, result == 1.0
     * @post se livelloPartenza == livelloRiferimento, result ==
     *       getFattoreDiretto(livelloArrivo)
     * @post se livelloArrivo == livelloRiferimento e esiste fattore per
     *       livelloPartenza,
     *       result == 1/getFattoreDiretto(livelloPartenza)
     * @post se livelloPartenza e livelloArrivo sono diversi dal riferimento,
     *       result ==
     *       getFattoreDiretto(livelloArrivo)/getFattoreDiretto(livelloPartenza)
     *       se entrambi i fattori esistono, altrimenti null
     */
    public Double getFattoreConversione(int livelloPartenza, int livelloArrivo) {
        // CASO 1: Se i livelli sono uguali, il fattore è 1.0 (identità)
        if (livelloPartenza == livelloArrivo) {
            return 1.0;
        }

        // CASO 2: Dal livello di riferimento a un altro livello
        if (livelloPartenza == livelloRiferimento) {
            // Restituisce direttamente il fattore per il livello di arrivo
            return getFattoreDiretto(livelloArrivo);
        }

        // CASO 3: Da un livello al livello di riferimento
        else if (livelloArrivo == livelloRiferimento) {
            // Ottiene il fattore diretto per il livello di partenza
            Double fattorePrimo = getFattoreDiretto(livelloPartenza);
            if (fattorePrimo == null) {
                return null; // Manca il fattore necessario
            }

            // Calcola l'inverso del fattore
            return 1.0 / fattorePrimo;
        }

        // CASO 4: Tra due livelli diversi dal riferimento
        else {
            // Ottiene i fattori diretti di entrambi i livelli rispetto al riferimento
            Double fattorePartenza = getFattoreDiretto(livelloPartenza);
            Double fattoreArrivo = getFattoreDiretto(livelloArrivo);

            // Se entrambi i fattori esistono, calcola il rapporto tra di essi
            if (fattorePartenza != null && fattoreArrivo != null) {
                // Il fattore di conversione è il rapporto tra i due fattori diretti
                return fattoreArrivo / fattorePartenza;
            }

            // Se manca almeno un fattore, la conversione non è possibile
            return null;
        }
    }
}