package model;

import com.scalified.tree.TreeNode;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Gestisce i fattori di conversione tra diversi livelli di una gerarchia.
 * <p>
 * Definisce equivalenze quantitative tra categorie a diversi livelli gerarchici
 * basandosi su un "nodo di riferimento" rispetto al quale vengono calcolate
 * tutte le conversioni.
 * <p>
 * Concetti chiave:
 * <ul>
 * <li><b>Nodo di riferimento</b>: Punto base per le conversioni</li>
 * <li><b>Fattore di conversione</b>: Rapporto tra unità di diversi livelli</li>
 * </ul>
 *
 * @see TreeNode
 * @see Categoria
 * @see ConversioneGerarchie
 */
public class FattoreConversione implements Serializable {
    // Identificatore di versione per la serializzazione
    private static final long serialVersionUID = 1L;

    // Costanti per i messaggi di errore, migliora la manutenibilità del codice
    public static final String NODO_RIFERIMENTO_NON_NULL = "Il nodo di riferimento non può essere null";
    public static final String LIVELLO_RIFERIMENTO_MINIMO = "Il livello di riferimento deve essere almeno 1";
    public static final String FATTORE_CONVERSIONE_POSITIVO = "Il fattore di conversione deve essere positivo";
    public static final String LIVELLO_RIFERIMENTO_NON_AGGIUNGIBILE = "Non è possibile aggiungere un fattore per il livello di riferimento";
    public static final String LIVELLO_RIFERIMENTO_NON_RIMOVIBILE = "Non è possibile rimuovere il fattore per il livello di riferimento";

    /** Nodo foglia che serve come riferimento per le conversioni */
    private TreeNode<Categoria> fogliaRiferimento;

    /** Livello nella gerarchia del nodo di riferimento (numerato da 1) */
    private int livelloRiferimento = 1;

    /**
     * Mappa che associa livelli a fattori di conversione.
     * <p>
     * Chiave: livello nella gerarchia.
     * Valore: quante unità del livello equivalgono a un'unità del riferimento.
     */
    private final Map<Integer, Double> fattoriConversione = new HashMap<>();

    // -----------------------------------------------------------------------------------
    // METODI DI GESTIONE DEL NODO DI RIFERIMENTO
    // -----------------------------------------------------------------------------------

    /**
     * Imposta il nodo di riferimento per le conversioni.
     * <p>
     * Aggiorna automaticamente anche il livello di riferimento.
     *
     * @param fogliaRiferimento il nodo da usare come riferimento
     * @throws NullPointerException se fogliaRiferimento è null
     */
    public void setNodoRiferimento(TreeNode<Categoria> fogliaRiferimento) {
        // Verifica che il nodo non sia null, lanciando un'eccezione in caso contrario
        this.fogliaRiferimento = Objects.requireNonNull(fogliaRiferimento,
                NODO_RIFERIMENTO_NON_NULL);

        // Imposta automaticamente il livello di riferimento in base al livello del nodo
        this.setLivelloRiferimento(fogliaRiferimento.level());
    }

    /**
     * Imposta il livello di riferimento per le conversioni.
     *
     * @param livelloRiferimento il livello da usare come riferimento
     * @throws IllegalArgumentException se il livello è inferiore a 1
     */
    public void setLivelloRiferimento(int livelloRiferimento) {
        // Verifica che il livello sia valido (almeno 1, poiché i livelli partono da 1)
        if (livelloRiferimento < 1) {
            throw new IllegalArgumentException(LIVELLO_RIFERIMENTO_MINIMO);
        }

        // Imposta il nuovo livello di riferimento
        this.livelloRiferimento = livelloRiferimento;
    }

    /**
     * Restituisce il nodo foglia utilizzato come riferimento.
     *
     * @return il nodo foglia di riferimento, può essere null
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
     * Verifica se un fattore di conversione può essere aggiunto.
     * <p>
     * Un fattore può essere aggiunto se il livello è diverso dal riferimento
     * e non esiste già un fattore per quel livello.
     *
     * @param livello il livello da verificare
     * @return true se è possibile aggiungere un fattore
     */
    public boolean isAggiungibileFattoreConversione(int livello) {
        // Un fattore può essere aggiunto solo se:
        // 1. Il livello è diverso dal livello di riferimento (che ha sempre fattore
        // 1.0)
        // 2. Non esiste già un fattore per questo livello
        return livello != livelloRiferimento && !fattoriConversione.containsKey(livello);
    }

    /**
     * Verifica se esiste un fattore di conversione per un livello.
     * <p>
     * Restituisce true per il livello di riferimento o se esiste un fattore
     * definito.
     *
     * @param livello il livello da verificare
     * @return true se esiste un fattore per il livello
     */
    public boolean hasFattoreConversione(int livello) {
        // Un fattore esiste se:
        // 1. È il livello di riferimento (ha sempre fattore 1.0 implicito)
        // 2. Oppure è presente nella mappa dei fattori
        return livello == livelloRiferimento || fattoriConversione.containsKey(livello);
    }

    /**
     * Aggiunge un fattore di conversione per un livello.
     * <p>
     * Il fattore rappresenta quante unità del livello equivalgono a un'unità del
     * riferimento.
     *
     * @param livello il livello per il fattore
     * @param fattore il valore del fattore di conversione
     * @throws IllegalArgumentException se fattore ≤ 0 o livello =
     *                                  livelloRiferimento
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
     * Rimuove un fattore di conversione.
     *
     * @param livello il livello per cui rimuovere il fattore
     * @return true se il fattore è stato rimosso, false se non esisteva
     * @throws IllegalArgumentException se il livello è uguale al riferimento
     */
    public boolean removeFattoreConversione(int livello) {
        // Non è possibile rimuovere il fattore per il livello di riferimento
        // (che è sempre 1.0 per definizione)
        if (livello == livelloRiferimento) {
            throw new IllegalArgumentException(LIVELLO_RIFERIMENTO_NON_RIMOVIBILE);
        }

        // Tenta di rimuovere il fattore dalla mappa e restituisce true se era presente
        return fattoriConversione.remove(livello) != null;
    }

    /**
     * Restituisce il fattore di conversione diretto rispetto al riferimento.
     * <p>
     * Per il livello di riferimento restituisce sempre 1.0.
     *
     * @param livello il livello per cui ottenere il fattore
     * @return il fattore diretto, 1.0 per il riferimento, null se non definito
     */
    public Double getFattoreDiretto(int livello) {
        // Per il livello di riferimento, il fattore è sempre 1.0 per definizione
        if (livello == livelloRiferimento) {
            return 1.0;
        }

        // Per gli altri livelli, cerca il fattore nella mappa (può essere null se non
        // definito)
        return fattoriConversione.get(livello);
    }

    /**
     * Calcola il fattore di conversione tra due livelli qualsiasi.
     * <p>
     * Casi gestiti:
     * <ol>
     * <li>Livelli uguali: fattore = 1.0</li>
     * <li>Dal riferimento a un altro: restituisce il fattore diretto</li>
     * <li>Da altro a riferimento: restituisce l'inverso del fattore</li>
     * <li>Tra due livelli diversi dal riferimento: calcola il rapporto tra
     * fattori</li>
     * </ol>
     *
     * @param primoLivello   il livello di partenza
     * @param secondoLivello il livello di arrivo
     * @return il fattore calcolato, null se mancano i fattori necessari
     */
    public Double getFattoreConversione(int primoLivello, int secondoLivello) {
        // CASO 0: Se i livelli sono uguali, il fattore è 1 (identità)
        if (primoLivello == secondoLivello) {
            return 1.0;
        }

        // CASO 1: Dal livello di riferimento a un altro livello
        // Esempio: Da "Nuoto agonistico" (rif) a "Sport acquatici"
        if (primoLivello == livelloRiferimento) {
            // Restituisce direttamente il fattore per il secondo livello
            // Ad es. se fattore=0.8, significa che 1 unità di riferimento = 0.8 unità del
            // secondo livello
            return getFattoreDiretto(secondoLivello);
        }

        // CASO 2: Da un livello al livello di riferimento
        // Esempio: Da "Sport acquatici" a "Nuoto agonistico" (rif)
        else if (secondoLivello == livelloRiferimento) {
            // Ottiene il fattore per il primo livello
            Double fattorePrimo = getFattoreDiretto(primoLivello);

            // Se il fattore esiste, calcola l'inverso
            // Ad es. se fattore=0.8 (1 unità primo = 0.8 unità riferimento)
            // allora 1/0.8 = 1.25 (1 unità riferimento = 1.25 unità primo)
            return fattorePrimo != null ? 1.0 / fattorePrimo : null;
        }

        // CASO 3: Da un livello a un altro livello (entrambi diversi dal riferimento)
        // Esempio: Da "Sport acquatici" a "Nuoto"
        else {
            // Ottiene i fattori diretti rispetto al livello di riferimento
            Double fattorePrimo = getFattoreDiretto(primoLivello);
            Double fattoreSecondo = getFattoreDiretto(secondoLivello);

            // Se entrambi i fattori esistono, calcola il rapporto
            if (fattorePrimo != null && fattoreSecondo != null) {
                // La conversione indiretta è il rapporto tra i due fattori
                // Ad es. se fattorePrimo=0.8 e fattoreSecondo=0.9
                // allora 0.9/0.8 = 1.125 (1 unità primo = 1.125 unità secondo)
                return fattoreSecondo / fattorePrimo;
            } else {
                // Se manca almeno uno dei fattori, non è possibile calcolare la conversione
                return null;
            }
        }
    }

    /**
     * Rimuove tutti i fattori di conversione memorizzati.
     * <p>
     * Il livello di riferimento viene mantenuto.
     */
    public void clearFattori() {
        // Rimuove tutti i fattori di conversione dalla mappa
        fattoriConversione.clear();
    }

    /**
     * Restituisce il numero di fattori di conversione memorizzati.
     * <p>
     * Non include il livello di riferimento (implicito).
     *
     * @return il numero di fattori definiti
     */
    public int getNumeroFattori() {
        // Restituisce il numero di fattori memorizzati nella mappa
        return fattoriConversione.size();
    }
}