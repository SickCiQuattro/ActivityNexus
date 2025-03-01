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
 * Questa classe permette di definire un sistema di equivalenze quantitative tra
 * categorie posizionate a diversi livelli di una gerarchia. Il funzionamento si
 * basa
 * su un "nodo di riferimento" (tipicamente una foglia) rispetto al quale
 * vengono
 * definiti tutti i fattori di conversione.
 * <p>
 * Concetti chiave:
 * <ul>
 * <li><b>Nodo di riferimento</b>: Un nodo foglia che funge da base per tutte le
 * conversioni</li>
 * <li><b>Livello di riferimento</b>: Il livello gerarchico del nodo di
 * riferimento</li>
 * <li><b>Fattore di conversione</b>: Valore che indica quante unità di un
 * livello
 * equivalgono a un'unità del livello di riferimento</li>
 * </ul>
 * <p>
 * Esempio pratico: in una gerarchia di attività sportive, se prendiamo come
 * riferimento
 * "Nuoto agonistico" (livello 3), potremmo definire che:
 * <ul>
 * <li>1 unità di "Sport acquatici" (livello 1) = 0.8 unità di "Nuoto
 * agonistico"</li>
 * <li>1 unità di "Nuoto" (livello 2) = 0.9 unità di "Nuoto agonistico"</li>
 * </ul>
 * <p>
 * La classe calcola automaticamente le conversioni tra livelli diversi dal
 * riferimento,
 * ad esempio da "Sport acquatici" a "Nuoto".
 * <p>
 * ATTENZIONE: i metodi che accettano parametri numerici si aspettano che venga
 * passato
 * il livello del nodo (nodo.level()) e non il nodo stesso.
 * <p>
 * La classe implementa {@link Serializable} per consentire la persistenza dei
 * fattori di conversione.
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

    /**
     * Il nodo foglia che serve come riferimento per le conversioni.
     * Tutti i fattori di conversione sono definiti rispetto a questo nodo.
     */
    private TreeNode<Categoria> fogliaRiferimento;

    /**
     * Il livello nella gerarchia del nodo di riferimento.
     * I livelli sono numerati a partire da 1 (radice).
     */
    private int livelloRiferimento = 1;

    /**
     * Mappa che associa i livelli ai fattori di conversione rispetto al nodo di
     * riferimento.
     * <p>
     * Chiave: il livello nella gerarchia (int)
     * <p>
     * Valore: il fattore di conversione che rappresenta quante unità di quel
     * livello
     * equivalgono a un'unità del livello di riferimento.
     * <p>
     * Esempio: se livelloRiferimento=3 e fattoriConversione.get(1)=0.8, significa
     * che
     * 1 unità di livello 1 equivale a 0.8 unità del livello di riferimento (3).
     */
    private final Map<Integer, Double> fattoriConversione = new HashMap<>();

    // -----------------------------------------------------------------------------------
    // METODI DI GESTIONE DEL NODO DI RIFERIMENTO
    // -----------------------------------------------------------------------------------

    /**
     * Imposta il nodo di riferimento per le conversioni e aggiorna il suo livello.
     * <p>
     * Il nodo di riferimento è il punto rispetto al quale vengono definiti tutti
     * i fattori di conversione. Tipicamente è un nodo foglia (es. "Nuoto
     * agonistico").
     * <p>
     * Questo metodo imposta sia il nodo che aggiorna automaticamente il livello di
     * riferimento basandosi sul livello del nodo nell'albero.
     * <p>
     * Esempio di utilizzo:
     * 
     * <pre>
     * TreeNode&lt;Categoria&gt; nodoFoglia = gerarchia.findNode("Nuoto agonistico");
     * fattoreConversione.setNodoRiferimento(nodoFoglia);
     * </pre>
     *
     * @param fogliaRiferimento il nodo da usare come riferimento per le conversioni
     * @throws NullPointerException se fogliaRiferimento è null
     * 
     * @see #setLivelloRiferimento(int)
     * @see #getFogliaRiferimento()
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
     * <p>
     * Il livello di riferimento determina il punto base rispetto al quale
     * vengono calcolate tutte le conversioni. Ogni livello nella gerarchia
     * ha un valore numerico che parte da 1 (radice) e aumenta con la profondità.
     * <p>
     * Questo metodo è tipicamente chiamato automaticamente da
     * {@link #setNodoRiferimento(TreeNode)},
     * ma può essere utilizzato separatamente quando si conosce solo il livello
     * senza
     * avere il nodo specifico.
     *
     * @param livelloRiferimento il livello da usare come riferimento
     * @throws IllegalArgumentException se il livello è inferiore a 1 (i livelli
     *                                  partono da 1)
     * 
     * @see #getLivelloRiferimento()
     * @see #setNodoRiferimento(TreeNode)
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
     * Restituisce il nodo foglia utilizzato come riferimento per le conversioni.
     * <p>
     * Questo nodo è il punto centrale del sistema di conversione, rispetto al quale
     * vengono definiti tutti i fattori. Il fattore di conversione per questo nodo
     * è sempre implicitamente 1.0.
     *
     * @return il nodo foglia di riferimento, può essere null se non è stato
     *         impostato
     * 
     * @see #setNodoRiferimento(TreeNode)
     * @see #getLivelloRiferimento()
     */
    public TreeNode<Categoria> getFogliaRiferimento() {
        return fogliaRiferimento;
    }

    /**
     * Restituisce il livello del nodo di riferimento.
     * <p>
     * Il livello è un valore numerico che indica la profondità nella gerarchia,
     * partendo da 1 per il nodo radice e aumentando con la profondità.
     * <p>
     * Conoscere questo livello è fondamentale per capire come funzionano le
     * conversioni,
     * poiché tutti i fattori sono definiti in relazione a questo punto di
     * riferimento.
     *
     * @return il livello del nodo di riferimento
     * 
     * @see #setLivelloRiferimento(int)
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
     * <p>
     * Un fattore può essere aggiunto se sono soddisfatte entrambe queste
     * condizioni:
     * <ol>
     * <li>Il livello è diverso dal livello di riferimento (per il quale il fattore
     * è sempre 1.0)</li>
     * <li>Non esiste già un fattore per quel livello (per evitare sovrascritture
     * accidentali)</li>
     * </ol>
     * <p>
     * Questo metodo è utile per verificare preventivamente se un'operazione di
     * aggiunta
     * avrà successo, prima di effettuarla.
     *
     * @param livello il livello per cui verificare la possibilità di aggiungere un
     *                fattore
     * @return true se è possibile aggiungere un fattore, false altrimenti
     * 
     * @see #addFattoreConversione(int, double)
     * @see #hasFattoreConversione(int)
     */
    public boolean isAggiungibileFattoreConversione(int livello) {
        // Un fattore può essere aggiunto solo se:
        // 1. Il livello è diverso dal livello di riferimento (che ha sempre fattore
        // 1.0)
        // 2. Non esiste già un fattore per questo livello
        return livello != livelloRiferimento && !fattoriConversione.containsKey(livello);
    }

    /**
     * Verifica se esiste già un fattore di conversione per un dato livello.
     * <p>
     * Il metodo restituisce true se:
     * <ul>
     * <li>Il livello è il livello di riferimento (che ha sempre fattore 1.0
     * implicito)</li>
     * <li>Oppure esiste un fattore specificamente definito per quel livello</li>
     * </ul>
     * <p>
     * È utile prima di calcolare conversioni per verificare che i fattori necessari
     * siano disponibili.
     *
     * @param livello il livello da verificare
     * @return true se esiste un fattore per il livello specificato, false
     *         altrimenti
     * 
     * @see #getFattoreDiretto(int)
     * @see #addFattoreConversione(int, double)
     */
    public boolean hasFattoreConversione(int livello) {
        // Un fattore esiste se:
        // 1. È il livello di riferimento (ha sempre fattore 1.0 implicito)
        // 2. Oppure è presente nella mappa dei fattori
        return livello == livelloRiferimento || fattoriConversione.containsKey(livello);
    }

    /**
     * Aggiunge o aggiorna un fattore di conversione per un livello specifico.
     * <p>
     * Il fattore rappresenta quante unità del livello specificato equivalgono a
     * un'unità del livello di riferimento. Ad esempio:
     * <ul>
     * <li>Se livelloRiferimento=3 (es. "Nuoto agonistico")</li>
     * <li>E si imposta fattore=0.8 per livello=1 (es. "Sport acquatici")</li>
     * <li>Significa che 1 unità di "Sport acquatici" equivale a 0.8 unità di "Nuoto
     * agonistico"</li>
     * </ul>
     * <p>
     * Il metodo impedisce di impostare fattori non validi (negativi o zero) o di
     * impostare un fattore per il livello di riferimento (che è sempre 1.0 per
     * definizione).
     *
     * @param livello il livello per cui impostare il fattore di conversione
     * @param fattore il valore del fattore di conversione
     * @throws IllegalArgumentException se il fattore è minore o uguale a zero o se
     *                                  il livello è uguale al livello di
     *                                  riferimento
     * 
     * @see #getFattoreDiretto(int)
     * @see #removeFattoreConversione(int)
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
     * Rimuove un fattore di conversione per un livello specifico.
     * <p>
     * Una volta rimosso, il fattore non sarà più disponibile per calcolare
     * conversioni
     * che coinvolgono quel livello. È possibile rimuovere solo i fattori per
     * livelli
     * diversi dal livello di riferimento.
     * <p>
     * Esempio di utilizzo:
     * 
     * <pre>
     * // Se non abbiamo più bisogno della conversione per il livello 2
     * boolean rimosso = fattoreConversione.removeFattoreConversione(2);
     * if (rimosso) {
     *     System.out.println("Fattore rimosso con successo");
     * } else {
     *     System.out.println("Il fattore non esisteva");
     * }
     * </pre>
     *
     * @param livello il livello per cui rimuovere il fattore
     * @return true se il fattore è stato rimosso, false se non esisteva
     * @throws IllegalArgumentException se il livello è uguale al livello di
     *                                  riferimento
     * 
     * @see #addFattoreConversione(int, double)
     * @see #clearFattori()
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
     * Restituisce il fattore di conversione diretto per un livello specifico.
     * <p>
     * Il fattore diretto è quello definito esplicitamente tra il livello
     * specificato
     * e il livello di riferimento. Non calcola conversioni tra livelli diversi dal
     * riferimento (per questo uso c'è {@link #getFattoreConversione(int, int)}).
     * <p>
     * Per il livello di riferimento, il fattore è sempre 1.0 (identità).
     *
     * @param livello il livello per cui ottenere il fattore
     * @return il fattore di conversione o 1.0 se è il livello di riferimento, null
     *         se non esiste un fattore definito per quel livello
     * 
     * @see #getFattoreConversione(int, int)
     * @see #addFattoreConversione(int, double)
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
     * Calcola il fattore di conversione tra due livelli qualsiasi della gerarchia.
     * <p>
     * Questo è il metodo principale per ottenere conversioni tra livelli diversi.
     * La conversione viene calcolata utilizzando il livello di riferimento come
     * punto intermedio, seguendo questa logica:
     * <p>
     * <b>Casi possibili:</b>
     * <ol>
     * <li>Se i livelli sono uguali: il fattore è 1.0 (stessa categoria)</li>
     * <li>Dal livello di riferimento a un altro: restituisce direttamente il
     * fattore definito</li>
     * <li>Da un altro livello al riferimento: restituisce l'inverso del fattore
     * definito</li>
     * <li>Da un livello a un altro (entrambi diversi dal riferimento): calcola il
     * rapporto
     * tra i due fattori per ottenere la conversione indiretta</li>
     * </ol>
     * <p>
     * Esempio di calcolo:
     * 
     * <pre>
     * // Se abbiamo:
     * // - Livello riferimento = 3 (es. "Nuoto agonistico")
     * // - Fattore livello 1 = 0.8 (1 unità "Sport acquatici" = 0.8 unità
     * // "Nuoto agonistico")
     * // - Fattore livello 2 = 0.9 (1 unità "Nuoto" = 0.9 unità "Nuoto
     * // agonistico")
     * 
     * // Per convertire da livello 1 a livello 2:
     * double fattore = fattoreConversione.getFattoreConversione(1, 2); // = 0.9/0.8 = 1.125
     * // Significa: 1 unità di "Sport acquatici" equivale a 1.125 unità di
     * // "Nuoto"
     * </pre>
     *
     * @param primoLivello   il livello di partenza della conversione
     * @param secondoLivello il livello di arrivo della conversione
     * @return il fattore di conversione calcolato, null se almeno uno dei livelli
     *         non ha un fattore definito
     * 
     * @see #getFattoreDiretto(int)
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
     * Questo metodo azzera il sistema di conversione, eliminando tutti i fattori
     * precedentemente definiti. Il livello di riferimento viene mantenuto, quindi
     * è possibile poi ridefinire nuovi fattori.
     * <p>
     * È utile quando si desidera riconfigurare completamente il sistema di
     * conversione,
     * ad esempio dopo aver cambiato il nodo di riferimento.
     * 
     * @see #removeFattoreConversione(int)
     * @see #getNumeroFattori()
     */
    public void clearFattori() {
        // Rimuove tutti i fattori di conversione dalla mappa
        fattoriConversione.clear();
    }

    /**
     * Restituisce il numero di fattori di conversione esplicitamente memorizzati.
     * <p>
     * Questo conteggio non include il livello di riferimento, per il quale
     * il fattore è sempre implicitamente 1.0 e non viene memorizzato nella mappa.
     * <p>
     * È utile per diagnostica e per verificare la completezza del sistema di
     * conversione.
     *
     * @return il numero di fattori di conversione definiti
     * 
     * @see #clearFattori()
     * @see #hasFattoreConversione(int)
     */
    public int getNumeroFattori() {
        // Restituisce il numero di fattori memorizzati nella mappa
        return fattoriConversione.size();
    }
}