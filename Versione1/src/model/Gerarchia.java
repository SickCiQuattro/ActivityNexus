package model;

import com.scalified.tree.TraversalAction;
import com.scalified.tree.TreeNode;
import com.scalified.tree.multinode.LinkedMultiTreeNode;

import java.io.Serializable;
import java.util.*;

/**
 * Classe che rappresenta una gerarchia di categorie.
 * Una gerarchia è costituita da una categoria radice e da una struttura ad
 * albero di categorie figlie.
 * Permette di organizzare le categorie in modo gerarchico e di gestire le
 * conversioni tra diversi livelli.
 */
public class Gerarchia extends LinkedMultiTreeNode<Categoria> implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Fattore di conversione associato alla gerarchia */
    private final FattoreConversione fattoreConversione = new FattoreConversione();

    // -----------------------------------------------------------------------------------
    // COSTRUTTORI
    // -----------------------------------------------------------------------------------

    /**
     * Costruisce una Gerarchia con la categoria radice specificata.
     *
     * @param categoriaRadice la categoria radice della gerarchia
     */
    public Gerarchia(Categoria categoriaRadice) {
        super(categoriaRadice);
    }

    // -----------------------------------------------------------------------------------
    // METODI DI GESTIONE DELLA STRUTTURA
    // -----------------------------------------------------------------------------------

    /**
     * Aggiunge una categoria come figlio di una categoria padre specificata.
     *
     * @param categoriaGenitore la categoria padre
     * @param categoriaFiglio   la categoria figlio da aggiungere
     * @return true se la categoria figlio è stata aggiunta correttamente, false
     *         altrimenti
     */
    public boolean addCategoria(Categoria categoriaGenitore, Categoria categoriaFiglio) {
        TreeNode<Categoria> nodoGenitore = this.find(categoriaGenitore);
        return nodoGenitore != null && nodoGenitore.add(new LinkedMultiTreeNode<>(categoriaFiglio));
    }

    /**
     * Ottiene il gestore dei fattori di conversione per questa gerarchia.
     *
     * @return il gestore dei fattori di conversione
     */
    public FattoreConversione getFattoreConversione() {
        return fattoreConversione;
    }

    // -----------------------------------------------------------------------------------
    // METODI DI RICERCA FOGLIE
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce tutte le foglie della gerarchia.
     *
     * @return lista di nodi foglia
     */
    public ArrayList<TreeNode<Categoria>> getFoglie() {
        ArrayList<TreeNode<Categoria>> foglie = new ArrayList<>();

        TraversalAction<TreeNode<Categoria>> action = new TraversalAction<>() {
            @Override
            public void perform(TreeNode<Categoria> node) {
                if (node.isLeaf()) {
                    foglie.add(node);
                }
            }

            @Override
            public boolean isCompleted() {
                return false;
            }
        };

        this.traversePreOrder(action);
        return foglie;
    }

    /**
     * Restituisce una foglia per ogni livello distinto presente nella gerarchia.
     *
     * @return lista di nodi foglia, uno per ogni livello presente
     */
    public ArrayList<TreeNode<Categoria>> getFogliePerLivello() {
        ArrayList<TreeNode<Categoria>> foglie = new ArrayList<>();
        HashSet<Integer> livelloFoglie = new HashSet<>();

        TraversalAction<TreeNode<Categoria>> action = new TraversalAction<>() {
            @Override
            public void perform(TreeNode<Categoria> node) {
                if (node.isLeaf() && livelloFoglie.add(node.level())) {
                    foglie.add(node);
                }
            }

            @Override
            public boolean isCompleted() {
                return false;
            }
        };

        this.traversePreOrder(action);
        return foglie;
    }

    // -----------------------------------------------------------------------------------
    // METODI DI VISUALIZZAZIONE
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce una rappresentazione in stringa della gerarchia.
     * La stringa include i nomi delle categorie e i valori del campo associato alla
     * categoria.
     *
     * @return una rappresentazione in stringa della gerarchia
     */
    @Override
    public String toString() {
        return visualizzaGerarchia(new ParametriVisualizzazione());
    }

    /**
     * Visualizza la gerarchia con numeri progressivi accanto alle foglie.
     * Utile per permettere all'utente di selezionare una foglia.
     *
     * @return stringa con rappresentazione della gerarchia e numeri di selezione
     */
    public String toStringSelezioneFoglia() {
        ParametriVisualizzazione parametri = new ParametriVisualizzazione();
        parametri.numeraFoglie = true;
        return visualizzaGerarchia(parametri);
    }

    /**
     * Visualizza la gerarchia con i valori di conversione calcolati per ogni
     * foglia.
     *
     * @param fattoreEsterno valore esterno da applicare alla conversione
     * @return stringa con rappresentazione della gerarchia e valori di conversione
     */
    public String toStringFattoreConversione(double fattoreEsterno) {
        ParametriVisualizzazione parametri = new ParametriVisualizzazione();
        parametri.mostraFattoriConversione = true;
        parametri.fattoreEsterno = fattoreEsterno;
        return visualizzaGerarchia(parametri);
    }

    /**
     * Classe di supporto per configurare la visualizzazione della gerarchia.
     */
    private static class ParametriVisualizzazione {
        boolean numeraFoglie = false;
        boolean mostraFattoriConversione = false;
        double fattoreEsterno = 1.0;
    }

    /**
     * Metodo centrale per la visualizzazione della gerarchia.
     * Implementa tutte le varianti di visualizzazione in base ai parametri
     * specificati.
     * 
     * @param parametri i parametri di visualizzazione
     * @return stringa formattata rappresentante la gerarchia
     */
    private String visualizzaGerarchia(ParametriVisualizzazione parametri) {
        final StringBuilder builder = new StringBuilder();
        builder.append("\n");

        final int livelloRadice = level();
        final Set<TreeNode<Categoria>> genitoriProcessati = new HashSet<>();
        final Map<TreeNode<Categoria>, Queue<ValoreCampo>> valoriPerNodo = new HashMap<>();
        final int[] contatoreFoglie = parametri.numeraFoglie ? new int[] { 1 } : null;

        TraversalAction<TreeNode<Categoria>> action = new TraversalAction<>() {
            @Override
            public void perform(TreeNode<Categoria> nodo) {
                // Preparazione dell'ambiente per il nodo corrente
                int livelloRelativo = nodo.level() - livelloRadice;
                String prefissoLinea = creaIndentazione(nodo, livelloRelativo);
                preparaValoriNodo(nodo, genitoriProcessati, valoriPerNodo);

                // Visualizzazione delle informazioni del nodo
                if (nodo.isRoot()) {
                    // Visualizzazione radice
                    builder.append("Gerarchia: ").append(nodo.data().getNome()).append("\n");
                } else {
                    // Visualizzazione nodo standard
                    ValoreCampo valoreDaMostrare = ottieniValoreNodo(nodo, valoriPerNodo);

                    // Linea di collegamento
                    builder.append(prefissoLinea).append("|\t\n");

                    // Linea valore
                    builder.append(prefissoLinea).append("|\tValore: ")
                            .append(valoreDaMostrare != null ? valoreDaMostrare : "N/A")
                            .append("\n");

                    // Linea nome categoria
                    builder.append(prefissoLinea).append("|---");
                    if (parametri.numeraFoglie && nodo.isLeaf()) {
                        builder.append(" [").append(contatoreFoglie[0]++).append("] ");
                    }
                    builder.append("Nome: ").append(nodo.data().getNome());

                    // Eventuale informazione di conversione
                    if (parametri.mostraFattoriConversione && nodo.isLeaf()) {
                        Double valoreConversione = calcolaFattoreConversione(nodo, parametri.fattoreEsterno);
                        builder.append(" [Conversione: ")
                                .append(valoreConversione != null ? valoreConversione : "N/A")
                                .append("]");
                    }

                    builder.append("\n");
                }

                // Visualizzazione campo per nodi non-foglia
                if (!nodo.isLeaf()) {
                    visualizzaCampoNodo(nodo, prefissoLinea, builder);
                }
            }

            @Override
            public boolean isCompleted() {
                return false;
            }
        };

        traversePreOrder(action);
        return builder.toString();
    }

    /**
     * Calcola il fattore di conversione per un nodo foglia.
     * 
     * @param nodo           il nodo foglia
     * @param fattoreEsterno fattore di conversione esterno da applicare
     * @return il valore di conversione calcolato, o null se non disponibile
     */
    private Double calcolaFattoreConversione(TreeNode<Categoria> nodo, double fattoreEsterno) {
        try {
            TreeNode<Categoria> nodoRiferimento = fattoreConversione.getFogliaRiferimento();
            if (nodoRiferimento == null)
                return null;

            Double fattore = fattoreConversione.getFattoreConversione(
                    nodoRiferimento.level(), nodo.level());

            return fattore != null ? fattoreEsterno * fattore : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Visualizza il campo associato a un nodo non-foglia.
     * 
     * @param nodo          il nodo di cui visualizzare il campo
     * @param prefissoLinea il prefisso da usare per l'indentazione
     * @param builder       il builder su cui accumulare il risultato
     */
    private void visualizzaCampoNodo(TreeNode<Categoria> nodo, String prefissoLinea, StringBuilder builder) {
        if (!nodo.isRoot()) {
            builder.append(prefissoLinea).append("|\t");
        }

        String nomeCampo = "N/A";
        Categoria categoria = nodo.data();
        if (categoria != null && categoria.getCampo() != null) {
            nomeCampo = categoria.getCampo().getNome();
        }

        builder.append("Campo: ").append(nomeCampo).append("\n");
    }

    /**
     * Ottiene il valore da visualizzare per un nodo.
     * 
     * @param nodo          il nodo attuale
     * @param valoriPerNodo mappa dei valori assegnati per nodo
     * @return il valore da visualizzare o null se non disponibile
     */
    private ValoreCampo ottieniValoreNodo(TreeNode<Categoria> nodo,
            Map<TreeNode<Categoria>, Queue<ValoreCampo>> valoriPerNodo) {
        TreeNode<Categoria> genitore = nodo.parent();
        if (genitore == null)
            return null;

        Queue<ValoreCampo> valoriDisponibili = valoriPerNodo.get(genitore);
        return valoriDisponibili != null && !valoriDisponibili.isEmpty() ? valoriDisponibili.poll() : null;
    }

    /**
     * Prepara i valori da assegnare ai figli di un nodo.
     * 
     * @param nodo               il nodo corrente
     * @param genitoriProcessati set dei genitori già elaborati
     * @param valoriPerNodo      mappa che associa nodi genitori alle code di valori
     *                           per i loro figli
     */
    private void preparaValoriNodo(TreeNode<Categoria> nodo, Set<TreeNode<Categoria>> genitoriProcessati,
            Map<TreeNode<Categoria>, Queue<ValoreCampo>> valoriPerNodo) {
        // Determiniamo quale nodo processare
        TreeNode<Categoria> nodoProcessare = nodo.isRoot() ? nodo : nodo.parent();

        // Se abbiamo già processato questo nodo, non facciamo nulla
        if (genitoriProcessati.contains(nodoProcessare)) {
            return;
        }

        // Marchiamo il nodo come processato
        genitoriProcessati.add(nodoProcessare);

        // Prepariamo la coda dei valori
        Queue<ValoreCampo> codaValori = new LinkedList<>();

        // Otteniamo la categoria associata al nodo
        Categoria categoria = nodoProcessare.data();
        if (categoria != null && categoria.getCampo() != null) {
            Set<ValoreCampo> dominio = categoria.getCampo().getDominio();

            if (dominio != null && !dominio.isEmpty()) {
                // Contiamo i figli
                int numeroFigli = nodoProcessare.subtrees().size();

                // Otteniamo un array di valori dal dominio
                ValoreCampo[] arrayValori = dominio.toArray(new ValoreCampo[0]);

                // Assegniamo i valori ai figli
                for (int i = 0; i < numeroFigli; i++) {
                    codaValori.add(arrayValori[i % arrayValori.length]);
                }
            }
        }

        // Memorizziamo la coda per questo nodo
        valoriPerNodo.put(nodoProcessare, codaValori);
    }

    /**
     * Crea la stringa di indentazione per la visualizzazione dell'albero.
     * 
     * @param nodo    il nodo corrente
     * @param livello il livello del nodo
     * @return la stringa di indentazione
     */
    private String creaIndentazione(TreeNode<Categoria> nodo, int livello) {
        StringBuilder builder = new StringBuilder();
        TreeNode<Categoria> nodoCorrente = nodo;

        for (int i = 1; i < livello; i++) {
            nodoCorrente = nodoCorrente.parent();
            builder.append("\t");
            if (!isUltimoFiglio(nodoCorrente)) {
                builder.append("|");
            }
        }

        return builder.reverse().toString();
    }

    /**
     * Verifica se un nodo è l'ultimo figlio del suo genitore.
     * Utilizza confronto di riferimenti invece di equals per evitare
     * conflitti con l'implementazione di equals nella classe.
     *
     * @param nodo il nodo da verificare
     * @return true se il nodo è l'ultimo figlio, false altrimenti
     */
    private boolean isUltimoFiglio(TreeNode<Categoria> nodo) {
        TreeNode<Categoria> genitore = nodo.parent();
        if (genitore == null) {
            return true;
        }

        Collection<TreeNode<Categoria>> fratelli = (Collection<TreeNode<Categoria>>) genitore.subtrees();
        Object[] arrayFratelli = fratelli.toArray();

        return nodo == arrayFratelli[fratelli.size() - 1];
    }

    // -----------------------------------------------------------------------------------
    // METODI OVERRIDE DI OBJECT
    // -----------------------------------------------------------------------------------

    /**
     * Confronta questa gerarchia con l'oggetto specificato.
     * Due gerarchie sono considerate uguali se hanno la stessa categoria radice.
     *
     * @param obj l'oggetto da confrontare con questa gerarchia
     * @return true se l'oggetto è uguale a questa gerarchia, false altrimenti
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Gerarchia other = (Gerarchia) obj;
        return Objects.equals(this.root().data(), other.root().data());
    }

    /**
     * Calcola l'hash code per questa gerarchia.
     * Questa implementazione si basa sull'identità dell'oggetto anziché sui suoi
     * contenuti
     * per evitare interferenze con i metodi di visualizzazione.
     *
     * @return l'hash code basato sull'identità dell'oggetto
     */
    //@Override // override funzionante per il toString
    //public int hashCode() {
    //    return System.identityHashCode(this);
    //}
}