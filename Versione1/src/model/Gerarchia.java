package model;

import com.scalified.tree.TraversalAction;
import com.scalified.tree.TreeNode;
import com.scalified.tree.multinode.LinkedMultiTreeNode;
import java.io.Serial;
import java.io.Serializable;
import java.util.*;

/**
 * Rappresenta una gerarchia di categorie organizzata in struttura ad albero.
 * <p>
 * Gestisce categorie in relazione gerarchica, con supporto per conversioni
 * tra diversi livelli e visualizzazione formattata della struttura.
 * <p>
 * Funzionalità principali:
 * <ul>
 * <li>Gestione della struttura ad albero di categorie</li>
 * <li>Conversioni tra livelli gerarchici diversi</li>
 * <li>Visualizzazione formattata con diverse modalità</li>
 * <li>Recupero di foglie e nodi a diversi livelli</li>
 * </ul>
 *
 * <p>
 * <b>Invarianti:</b>
 * <ul>
 * <li>La radice dell'albero non è mai null</li>
 * <li>Il fattore di conversione non è mai null</li>
 * <li>I nodi dell'albero contengono sempre oggetti di tipo Categoria non
 * null</li>
 * <li>Due gerarchie sono considerate uguali se hanno la stessa categoria
 * radice</li>
 * </ul>
 *
 * @see Categoria
 * @see FattoreConversione
 * @see LinkedMultiTreeNode
 * @see Serializable
 */
public class Gerarchia extends LinkedMultiTreeNode<Categoria> implements Serializable {
    // -----------------------------------------------------------------------------------
    // CAMPI E COSTANTI
    // -----------------------------------------------------------------------------------

    /** Identificatore per la serializzazione */
    @Serial
    private static final long serialVersionUID = 1L;

    /** Fattore di conversione associato alla gerarchia */
    private final FattoreConversione fattoreConversione = new FattoreConversione();

    // -----------------------------------------------------------------------------------
    // COSTRUTTORI
    // -----------------------------------------------------------------------------------

    /**
     * Costruisce una Gerarchia con la categoria radice specificata.
     * <p>
     * Crea un albero con un singolo nodo radice contenente la categoria
     * specificata.
     * Inizializza anche il sistema di fattori di conversione associato alla
     * gerarchia.
     *
     * @param categoriaRadice la categoria radice della gerarchia
     * @throws NullPointerException se categoriaRadice è null
     *
     * @pre categoriaRadice != null
     * @post root().data().equals(categoriaRadice)
     * @post getFattoreConversione() != null
     */
    public Gerarchia(Categoria categoriaRadice) {
        super(categoriaRadice); // Inizializza il nodo radice con la categoria specificata

        // Non è necessario inizializzare fattoreConversione poiché viene fatto
        // implicitamente alla dichiarazione del campo
    }

    // -----------------------------------------------------------------------------------
    // METODI DI ACCESSO AI DATI (GETTERS)
    // -----------------------------------------------------------------------------------

    /**
     * Ottiene il gestore dei fattori di conversione per questa gerarchia.
     * <p>
     * Permette di accedere all'oggetto che gestisce le conversioni tra
     * diversi livelli della gerarchia.
     *
     * @return il gestore dei fattori di conversione
     *
     * @pre true (nessuna precondizione)
     * @post result != null
     * @post result è l'istanza di FattoreConversione associata a questa gerarchia
     */
    public FattoreConversione getFattoreConversione() {
        return fattoreConversione;
    }

    // -----------------------------------------------------------------------------------
    // METODI DI RICERCA FOGLIE
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce tutte le foglie della gerarchia.
     * <p>
     * Una foglia è un nodo che non ha figli. La lista restituita include tutte
     * le foglie trovate durante l'attraversamento in pre-ordine dell'albero,
     * quindi l'ordine riflette la sequenza di visita.
     *
     * @return lista di nodi foglia
     *
     * @pre true (nessuna precondizione)
     * @post result != null
     * @post per ogni nodo in result, nodo.isLeaf() == true
     * @post result contiene tutti e soli i nodi foglia della gerarchia
     */
    public ArrayList<TreeNode<Categoria>> getFoglie() {
        ArrayList<TreeNode<Categoria>> foglie = new ArrayList<>();

        // Crea un'azione da eseguire su ogni nodo durante l'attraversamento
        TraversalAction<TreeNode<Categoria>> action = new TraversalAction<>() {
            @Override
            public void perform(TreeNode<Categoria> node) {
                // Aggiunge alla lista solo i nodi foglia (senza figli)
                if (node.isLeaf()) {
                    foglie.add(node);
                }
            }

            @Override
            public boolean isCompleted() {
                // Continua l'attraversamento fino alla fine
                return false;
            }
        };

        // Esegue l'attraversamento pre-ordine dell'albero
        this.traversePreOrder(action);
        return foglie;
    }

    /**
     * Restituisce una foglia per ogni livello distinto presente nella gerarchia.
     * <p>
     * Per ogni livello trovato nell'albero, viene aggiunta alla lista la prima
     * foglia incontrata a quel livello durante l'attraversamento in pre-ordine.
     * <p>
     * Questo metodo è utile quando si desidera avere un rappresentante di ogni
     * livello
     * per operazioni come visualizzazione o configurazione di conversioni.
     *
     * @return lista di nodi foglia, uno per ogni livello presente
     *
     * @pre true (nessuna precondizione)
     * @post result != null
     * @post per ogni nodo in result, nodo.isLeaf() == true
     * @post per ogni livello L che ha almeno una foglia, esiste esattamente un nodo
     *       in result con level() == L
     */
    public ArrayList<TreeNode<Categoria>> getFogliePerLivello() {
        ArrayList<TreeNode<Categoria>> foglie = new ArrayList<>();
        HashSet<Integer> livelloFoglie = new HashSet<>(); // Set per tenere traccia dei livelli già trovati

        TraversalAction<TreeNode<Categoria>> action = new TraversalAction<>() {
            @Override
            public void perform(TreeNode<Categoria> node) {
                // Aggiunge il nodo solo se:
                // 1. È una foglia (non ha figli)
                // 2. Il suo livello non è già stato trovato (add() restituisce true se
                // l'elemento è nuovo)
                if (node.isLeaf() && livelloFoglie.add(node.level())) {
                    foglie.add(node);
                }
            }

            @Override
            public boolean isCompleted() {
                // Continua l'attraversamento fino alla fine
                return false;
            }
        };

        this.traversePreOrder(action);
        return foglie;
    }

    // -----------------------------------------------------------------------------------
    // METODI DI VISUALIZZAZIONE PUBBLICI
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce una rappresentazione in stringa della gerarchia.
     * <p>
     * Genera una visualizzazione ad albero con indentazione, includendo i nomi
     * delle
     * categorie e i valori del campo associato a ogni categoria.
     * <p>
     * Questa versione utilizza i parametri di visualizzazione predefiniti,
     * senza numerazione di foglie o fattori di conversione.
     *
     * @return una rappresentazione in stringa della gerarchia
     *
     * @pre true (nessuna precondizione)
     * @post result != null
     * @post result contiene una rappresentazione testuale di tutti i nodi
     *       dell'albero
     */
    @Override
    public String toString() {
        // Usa la visualizzazione standard senza opzioni speciali
        return visualizzaGerarchia(new ParametriVisualizzazione());
    }

    /**
     * Visualizza la gerarchia con numeri progressivi accanto alle foglie.
     * <p>
     * Questo formato è particolarmente utile quando si desidera permettere
     * all'utente
     * di selezionare una foglia specifica tramite numero. Le foglie sono numerate
     * in ordine di attraversamento pre-ordine dell'albero.
     *
     * @return stringa con rappresentazione della gerarchia e numeri di selezione
     *
     * @pre true (nessuna precondizione)
     * @post result != null
     * @post result contiene una rappresentazione testuale con foglie numerate
     *       progressivamente
     */
    public String toStringSelezioneFoglia() {
        // Configura la visualizzazione per numerare le foglie
        ParametriVisualizzazione parametri = new ParametriVisualizzazione();
        parametri.numeraFoglie = true;
        return visualizzaGerarchia(parametri);
    }

    /**
     * Visualizza la gerarchia con i valori di conversione calcolati per ogni
     * foglia.
     * <p>
     * Mostra accanto a ogni foglia il fattore di conversione calcolato,
     * moltiplicato per il fattore esterno specificato. Utile per visualizzare
     * come i valori si trasformano tra diversi livelli.
     *
     * @param fattoreEsterno valore esterno da applicare alla conversione
     * @return stringa con rappresentazione della gerarchia e valori di conversione
     *
     * @pre true (fattoreEsterno può essere qualsiasi numero)
     * @post result != null
     * @post result contiene una rappresentazione testuale con fattori di
     *       conversione
     *       calcolati per ogni foglia
     */
    public String toStringFattoreConversione(double fattoreEsterno) {
        // Configura la visualizzazione per mostrare i fattori di conversione
        ParametriVisualizzazione parametri = new ParametriVisualizzazione();
        parametri.mostraFattoriConversione = true;
        parametri.fattoreEsterno = fattoreEsterno;
        return visualizzaGerarchia(parametri);
    }

    // -----------------------------------------------------------------------------------
    // METODI DI SUPPORTO ALLA VISUALIZZAZIONE (PRIVATI)
    // -----------------------------------------------------------------------------------

    /**
     * Classe di supporto per configurare la visualizzazione della gerarchia.
     * <p>
     * Raccoglie tutti i parametri che influenzano la formattazione della
     * visualizzazione
     * in un singolo oggetto per semplificare il passaggio al metodo di
     * visualizzazione.
     */
    private static class ParametriVisualizzazione {
        /** Flag che indica se numerare le foglie per facilitare la selezione */
        boolean numeraFoglie = false;

        /** Flag che indica se mostrare i fattori di conversione per le foglie */
        boolean mostraFattoriConversione = false;

        /** Fattore esterno da applicare ai calcoli di conversione */
        double fattoreEsterno = 1.0;
    }

    /**
     * Metodo centrale per la visualizzazione della gerarchia.
     * <p>
     * Implementa tutte le varianti di visualizzazione in base ai parametri
     * specificati,
     * traversando l'albero in pre-ordine e costruendo progressivamente la
     * rappresentazione
     * testuale con indentazione appropriata.
     * 
     * @param parametri i parametri di visualizzazione
     * @return stringa formattata rappresentante la gerarchia
     *
     * @pre parametri != null
     * @post result != null
     */
    private String visualizzaGerarchia(ParametriVisualizzazione parametri) {
        final StringBuilder builder = new StringBuilder();
        builder.append("\n");

        final int livelloRadice = level(); // Ottiene il livello della radice
        final Set<TreeNode<Categoria>> genitoriProcessati = new HashSet<>(); // Tiene traccia dei genitori già
                                                                             // processati
        final Map<TreeNode<Categoria>, Queue<ValoreCampo>> valoriPerNodo = new HashMap<>(); // Associa nodi a valori di
                                                                                            // campo
        final int[] contatoreFoglie = parametri.numeraFoglie ? new int[] { 1 } : null; // Contatore per numerazione
                                                                                       // foglie

        TraversalAction<TreeNode<Categoria>> action = new TraversalAction<>() {
            @Override
            public void perform(TreeNode<Categoria> nodo) {
                // Preparazione dell'ambiente per il nodo corrente
                int livelloRelativo = nodo.level() - livelloRadice; // Calcolo del livello relativo alla radice
                String prefissoLinea = creaIndentazione(nodo, livelloRelativo); // Crea l'indentazione grafica
                preparaValoriNodo(nodo, genitoriProcessati, valoriPerNodo); // Prepara i valori da mostrare

                // Visualizzazione delle informazioni del nodo
                if (nodo.isRoot()) {
                    // Visualizzazione speciale per il nodo radice
                    builder.append("GERARCHIA: ").append(nodo.data().getNome()).append("\n");
                } else {
                    // Visualizzazione per nodi non-radice
                    ValoreCampo valoreDaMostrare = ottieniValoreNodo(nodo, valoriPerNodo);

                    // Linea di collegamento verticale
                    builder.append(prefissoLinea).append("|\t\n");

                    // Linea con il valore del campo caratteristico
                    builder.append(prefissoLinea).append("|\tValore: ")
                            .append(valoreDaMostrare != null ? valoreDaMostrare : "N/A")
                            .append("\n");

                    // Linea con il nome della categoria
                    builder.append(prefissoLinea).append("|---");

                    // Aggiunge numerazione progressiva se richiesto e se è foglia
                    if (parametri.numeraFoglie && nodo.isLeaf()) {
                        assert contatoreFoglie != null;
                        builder.append(" [").append(contatoreFoglie[0]++).append("] ");
                    }
                    builder.append("Nome: ").append(nodo.data().getNome());

                    // Aggiunge informazione di conversione se richiesto e se è foglia
                    if (parametri.mostraFattoriConversione && nodo.isLeaf()) {
                        Double valoreConversione = calcolaFattoreConversione(nodo, parametri.fattoreEsterno);
                        builder.append(" [Conversione: ")
                                .append(valoreConversione != null ? String.format("%.2f", valoreConversione) : "N/A")
                                .append("]");
                    }

                    builder.append("\n");
                }

                // Visualizza campo caratteristico per nodi non-foglia
                if (!nodo.isLeaf()) {
                    visualizzaCampoNodo(nodo, prefissoLinea, builder);
                }
            }

            @Override
            public boolean isCompleted() {
                return false; // Continua l'attraversamento fino alla fine
            }
        };

        // Esegue l'attraversamento in pre-ordine dell'albero
        traversePreOrder(action);
        return builder.toString();
    }

    /**
     * Calcola il fattore di conversione per un nodo foglia.
     * <p>
     * Utilizza il sistema di fattori di conversione della gerarchia per calcolare
     * il fattore appropriato per il livello del nodo specificato, moltiplicato
     * per un fattore esterno opzionale.
     * 
     * @param nodo           il nodo foglia di cui calcolare il fattore
     * @param fattoreEsterno fattore di conversione esterno da applicare
     * @return il valore di conversione calcolato, o null se non disponibile
     *
     * @pre nodo != null
     */
    private Double calcolaFattoreConversione(TreeNode<Categoria> nodo, double fattoreEsterno) {
        try {
            // Ottiene il nodo di riferimento per le conversioni
            TreeNode<Categoria> nodoRiferimento = fattoreConversione.getFogliaRiferimento();
            if (nodoRiferimento == null)
                return null; // Se non esiste un nodo di riferimento, non è possibile calcolare il fattore

            // Ottiene il fattore di conversione tra il livello del riferimento e quello del
            // nodo
            Double fattore = fattoreConversione.getFattoreConversione(
                    nodoRiferimento.level(), nodo.level());

            // Applica il fattore esterno e lo restituisce
            return fattore != null ? fattoreEsterno * fattore : null;
        } catch (Exception e) {
            // In caso di errore durante il calcolo, restituisce null
            return null;
        }
    }

    /**
     * Visualizza il campo associato a un nodo non-foglia.
     * <p>
     * Aggiunge alla rappresentazione testuale il nome del campo caratteristico
     * associato alla categoria del nodo.
     * 
     * @param nodo          il nodo di cui visualizzare il campo
     * @param prefissoLinea il prefisso da usare per l'indentazione
     * @param builder       il builder su cui accumulare il risultato
     *
     * @pre nodo != null
     * @pre prefissoLinea != null
     * @pre builder != null
     */
    private void visualizzaCampoNodo(TreeNode<Categoria> nodo, String prefissoLinea, StringBuilder builder) {
        // Aggiunge indentazione solo se non è il nodo radice
        if (!nodo.isRoot()) {
            builder.append(prefissoLinea).append("|\t");
        }

        // Determina il nome del campo (o N/A se non disponibile)
        String nomeCampo = "N/A";
        Categoria categoria = nodo.data();
        if (categoria != null && categoria.getCampo() != null) {
            nomeCampo = categoria.getCampo().getNome();
        }

        // Aggiunge il campo al builder
        builder.append("Campo: ").append(nomeCampo).append("\n");
    }

    /**
     * Ottiene il valore da visualizzare per un nodo.
     * <p>
     * Recupera il valore di campo appropriato per il nodo corrente,
     * prelevandolo dalla coda di valori preparati per il genitore.
     * 
     * @param nodo          il nodo attuale
     * @param valoriPerNodo mappa dei valori assegnati per nodo
     * @return il valore da visualizzare o null se non disponibile
     *
     * @pre nodo != null
     * @pre valoriPerNodo != null
     */
    private ValoreCampo ottieniValoreNodo(TreeNode<Categoria> nodo,
            Map<TreeNode<Categoria>, Queue<ValoreCampo>> valoriPerNodo) {
        // Ottiene il genitore del nodo
        TreeNode<Categoria> genitore = nodo.parent();
        if (genitore == null)
            return null; // Se è la radice, non ha un valore associato

        // Recupera la coda dei valori per il genitore
        Queue<ValoreCampo> valoriDisponibili = valoriPerNodo.get(genitore);

        // Prende il prossimo valore dalla coda, se disponibile
        return valoriDisponibili != null && !valoriDisponibili.isEmpty() ? valoriDisponibili.poll() : null;
    }

    /**
     * Prepara i valori da assegnare ai figli di un nodo.
     * <p>
     * Crea e memorizza una coda di valori di campo che verranno successivamente
     * assegnati ai figli del nodo durante la visualizzazione.
     * 
     * @param nodo               il nodo corrente
     * @param genitoriProcessati set dei genitori già elaborati
     * @param valoriPerNodo      mappa che associa nodi genitori alle code di valori
     *                           per i loro figli
     *
     * @pre nodo != null
     * @pre genitoriProcessati != null
     * @pre valoriPerNodo != null
     */
    private void preparaValoriNodo(TreeNode<Categoria> nodo, Set<TreeNode<Categoria>> genitoriProcessati,
            Map<TreeNode<Categoria>, Queue<ValoreCampo>> valoriPerNodo) {
        // Determina quale nodo processare (la radice stessa o il genitore del nodo
        // corrente)
        TreeNode<Categoria> nodoProcessare = nodo.isRoot() ? nodo : nodo.parent();

        // Se abbiamo già processato questo nodo, non facciamo nulla
        if (genitoriProcessati.contains(nodoProcessare)) {
            return;
        }

        // Marchiamo il nodo come processato per evitare duplicazioni
        genitoriProcessati.add(nodoProcessare);

        // Prepariamo la coda dei valori per i figli
        Queue<ValoreCampo> codaValori = new LinkedList<>();

        // Otteniamo la categoria associata al nodo
        Categoria categoria = nodoProcessare.data();
        if (categoria != null && categoria.getCampo() != null) {
            // Recupera il dominio dei valori possibili dal campo
            List<ValoreCampo> dominio = categoria.getCampo().getDominio();

            if (dominio != null && !dominio.isEmpty()) {
                // Conta quanti figli ha il nodo
                int numeroFigli = nodoProcessare.subtrees().size();

                // Converte il set di valori in array per accesso indicizzato
                ValoreCampo[] arrayValori = dominio.toArray(new ValoreCampo[0]);

                // Assegna valori ciclicamente ai figli (se ci sono più figli che valori, i
                // valori si ripetono)
                for (int i = 0; i < numeroFigli; i++) {
                    codaValori.add(arrayValori[i % arrayValori.length]);
                }
            }
        }

        // Memorizza la coda per questo nodo nella mappa
        valoriPerNodo.put(nodoProcessare, codaValori);
    }

    /**
     * Crea la stringa di indentazione per la visualizzazione dell'albero.
     * <p>
     * Genera un prefisso di indentazione appropriato per visualizzare
     * la struttura gerarchica dell'albero in modo leggibile.
     * 
     * @param nodo    il nodo corrente
     * @param livello il livello del nodo
     * @return la stringa di indentazione
     *
     * @pre nodo != null
     * @pre livello >= 0
     * @post result != null
     */
    private String creaIndentazione(TreeNode<Categoria> nodo, int livello) {
        StringBuilder builder = new StringBuilder();
        TreeNode<Categoria> nodoCorrente = nodo;

        // Costruisce la stringa di indentazione risalendo l'albero
        for (int i = 1; i < livello; i++) {
            nodoCorrente = nodoCorrente.parent();
            builder.append("\t"); // Aggiunge tab per indentazione

            // Aggiunge una linea verticale se non è l'ultimo figlio
            if (!isUltimoFiglio(nodoCorrente)) {
                builder.append("|");
            }
        }

        // Inverte la stringa per ottenere l'ordine corretto di indentazione
        return builder.reverse().toString();
    }

    /**
     * Verifica se un nodo è l'ultimo figlio del suo genitore.
     * <p>
     * Utilizza confronto di riferimenti invece di equals per evitare
     * conflitti con l'implementazione di equals nella classe.
     *
     * @param nodo il nodo da verificare
     * @return true se il nodo è l'ultimo figlio, false altrimenti
     *
     * @pre nodo != null
     */
    private boolean isUltimoFiglio(TreeNode<Categoria> nodo) {
        TreeNode<Categoria> genitore = nodo.parent();
        if (genitore == null) {
            return true; // Se è la radice, è considerato l'ultimo figlio
        }

        // Ottiene tutti i figli (fratelli) del genitore come array
        Collection<TreeNode<Categoria>> fratelli = (Collection<TreeNode<Categoria>>) genitore.subtrees();
        Object[] arrayFratelli = fratelli.toArray();

        // Verifica se il nodo è l'ultimo elemento dell'array (confronto per
        // riferimento)
        return nodo == arrayFratelli[fratelli.size() - 1];
    }

    // -----------------------------------------------------------------------------------
    // METODI OVERRIDE DI OBJECT
    // -----------------------------------------------------------------------------------

    /**
     * Confronta questa gerarchia con l'oggetto specificato.
     * <p>
     * Due gerarchie sono considerate uguali se hanno la stessa categoria radice.
     * Questo è coerente con l'invariante che identifica una gerarchia tramite
     * la sua categoria principale.
     *
     * @param obj l'oggetto da confrontare con questa gerarchia
     * @return true se l'oggetto è uguale a questa gerarchia, false altrimenti
     *
     * @post se obj instanceof Gerarchia, result ==
     *       (this.root().data().equals(((Gerarchia)obj).root().data()))
     * @post se !(obj instanceof Gerarchia), result == false
     */
    @Override
    public boolean equals(Object obj) {
        // Verifica di identità veloce
        if (this == obj) {
            return true;
        }

        // Verifiche standard null e tipo
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        // Confronta le categorie radice delle due gerarchie
        Gerarchia other = (Gerarchia) obj;
        return Objects.equals(this.root().data(), other.root().data());
    }
}