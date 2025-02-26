package model;

import com.scalified.tree.TraversalAction;
import com.scalified.tree.TreeNode;
import com.scalified.tree.multinode.LinkedMultiTreeNode;
import java.io.*;
import java.util.*;

/**
 * Classe che rappresenta una gerarchia di categorie.
 * Una gerarchia è costituita da una categoria radice e da una struttura ad albero di categorie figlie.
 */
public class Gerarchia extends LinkedMultiTreeNode implements Serializable {
    private static final long serialVersionUID = 1L;
    private FattoreConversione fattoreConversione = new FattoreConversione();

    /**
     * Costruisce una Gerarchia con la categoria radice specificata.
     *
     * @param rootCategoria la categoria radice della gerarchia
     */
    public Gerarchia(Categoria rootCategoria) {
        super(rootCategoria);
    }

    /**
     * Aggiunge una categoria come figlio di una categoria padre specificata.
     *
     * @param parentCategoria la categoria padre
     * @param childCategoria  la categoria figlio da aggiungere
     * @return true se la categoria figlio è stata aggiunta correttamente, false altrimenti
     */
    public boolean addCategoria(Categoria parentCategoria, Categoria childCategoria) {
        TreeNode<Categoria> parentNode = this.find(parentCategoria);
        return parentNode != null && parentNode.add(new LinkedMultiTreeNode<>(childCategoria));
    }

    /**
     * Stampa la struttura dell'albero.
     *
     * @param node      il nodo corrente
     * @param nodeLevel il livello del nodo
     * @return una stringa che rappresenta la struttura dell'albero
     */
    private String printTree(TreeNode<Categoria> node, int nodeLevel) {
        StringBuilder builder = new StringBuilder();
        TreeNode<Categoria> currentNode = node;

        for (int i = 1; i < nodeLevel; i++) {
            currentNode = currentNode.parent();
            builder.append("\t");
            if (!isLastChild(currentNode)) {
                builder.append("|");
            }
        }
        return builder.reverse().toString();
    }

    private boolean isLastChild(TreeNode<Categoria> node) {
        TreeNode<Categoria> parent = node.parent();
        if (parent == null) {
            return true;
        }
        Collection<TreeNode<Categoria>> siblings = (Collection<TreeNode<Categoria>>) parent.subtrees();
        return node.equals(siblings.toArray()[siblings.size() - 1]);
    }

    public FattoreConversione getFattoreConversione() {
        return fattoreConversione;
    }

    public ArrayList<TreeNode<Categoria>> getFogliePerLivello() {
        ArrayList<TreeNode<Categoria>> foglie = new ArrayList<>();
        HashSet<Integer> livelloFoglie = new HashSet<>();

        TraversalAction<TreeNode<Categoria>> action = new TraversalAction<>() {
            @Override
            public void perform(TreeNode<Categoria> node) {
                if (node.isLeaf())
                    if (livelloFoglie.add(node.level()))
                        foglie.add(node);
            }

            @Override
            public boolean isCompleted() {
                return false;
            }
        };
        this.traversePreOrder(action);
        return foglie;
    }

    public ArrayList<TreeNode<Categoria>> getFoglie() {
        ArrayList<TreeNode<Categoria>> leafNodes = new ArrayList<>();
        TraversalAction<TreeNode<Categoria>> action = new TraversalAction<>() {
            @Override
            public void perform(TreeNode<Categoria> node) {
                if (node.isLeaf()) {
                    leafNodes.add(node);
                }
            }

            @Override
            public boolean isCompleted() {
                return false;
            }
        };
        this.traversePreOrder(action);
        return leafNodes;
    }

    /**
     * Scrive l'oggetto Gerarchia in un flusso di output.
     *
     * @param out il flusso di output
     * @throws IOException se si verifica un errore di I/O
     */
    @Serial
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    /**
     * Legge l'oggetto Gerarchia da un flusso di input.
     *
     * @param in il flusso di input
     * @throws IOException            se si verifica un errore di I/O
     * @throws ClassNotFoundException se la classe non viene trovata
     */
    @Serial
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }

    /**
     * Restituisce una rappresentazione in stringa della gerarchia.
     * La stringa include i nomi delle categorie e i valori del campo associato alla categoria.
     *
     * @return una rappresentazione in stringa della gerarchia
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("\n");
        final int topNodeLevel = level(); // livello della radice (sempre 0 con un albero completo)
        final HashSet<TreeNode<Categoria>> lastParent = new HashSet<>(); // controllo sui padri già presi in considerazione per evitare di ripetere i valori nello stack
        Stack<ValoreCampo> valore = new Stack<>(); // stack per i valori dei campi

        TraversalAction<TreeNode<Categoria>> action = new TraversalAction<>() { // itera su ogni nodo dell'albero da sinistra a destra
            @Override
            public void perform(TreeNode<Categoria> node) {
                int nodeLevel = node.level() - topNodeLevel; // livello del nodo rispetto alla radice
                String stampa = printTree(node, nodeLevel);
                Stack<ValoreCampo> reverse = new Stack<>(); // stack per invertire l'ordine dei valori da inserire in valore

                if (node.isRoot()) {
                    lastParent.add(node); // aggiunge la radice alla lista dei padri
                    node.data().getCampo().getDominio().forEach(reverse::push); // inserisce i valori del campo nello stack
                } else if (!lastParent.contains(node.parent())) { // se il padre non è già stato preso in considerazione
                    lastParent.add(node.parent()); // aggiunge il padre alla lista dei padri
                    node.parent().data().getCampo().getDominio().forEach(reverse::push); // inserisce i valori del campo nello stack
                }
                while (!reverse.isEmpty()) { // inverte l'ordine dei valori
                    valore.push(reverse.pop()); // inserisce i valori del campo nello stack nell'ordine corretto
                }
                // stampa i valori
                if (!node.isRoot()) { // se non è la radice
                    builder.append(stampa).append("|\t\n") // riga collegamento padre-figlio
                            .append(stampa).append("|\tValore: ").append(valore.pop()).append("\n") // riga valore
                            .append(stampa).append("|---Nome: ").append(node.data().getNome()).append("\n"); // riga nome
                } else
                    builder.append("Gerarchia: ").append(node.data().getNome()).append("\n"); // stampa il nome della gerarchia

                if (!node.isLeaf()) { // se non è una foglia si aggiunge il campo
                    if (!node.isRoot())
                        builder.append(stampa).append("|\t"); // riga campo
                    builder.append("Campo: ").append(node.data().getCampo().getNome()).append("\n");
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



    public String toStringSelezioneFoglia() {
        final StringBuilder builder = new StringBuilder();
        builder.append("\n");
        final int topNodeLevel = level();
        final HashSet<TreeNode<Categoria>> lastParent = new HashSet<>();
        Stack<ValoreCampo> valore = new Stack<>();
        final int[] leafCounter = {1};

        TraversalAction<TreeNode<Categoria>> action = new TraversalAction<>() {
            @Override
            public void perform(TreeNode<Categoria> node) {
                int nodeLevel = node.level() - topNodeLevel;
                String stampa = printTree(node, nodeLevel);
                Stack<ValoreCampo> reverse = new Stack<>();

                if (node.isRoot()) {
                    lastParent.add(node);
                    node.data().getCampo().getDominio().forEach(reverse::push);
                } else if (!lastParent.contains(node.parent())) {
                    lastParent.add(node.parent());
                    node.parent().data().getCampo().getDominio().forEach(reverse::push);
                }
                while (!reverse.isEmpty()) {
                    valore.push(reverse.pop());
                }
                if (!node.isRoot()) {
                    builder.append(stampa).append("|\t\n")
                            .append(stampa).append("|\tValore: ").append(valore.pop()).append("\n")
                            .append(stampa).append("|---");
                    if (node.isLeaf()) {
                        builder.append(" [").append(leafCounter[0]++).append("] ");
                    }
                    builder.append("Nome: ").append(node.data().getNome());
                    builder.append("\n");
                } else {
                    builder.append("Gerarchia: ").append(node.data().getNome()).append("\n");
                }
                if (!node.isLeaf()) {
                    if (!node.isRoot())
                        builder.append(stampa).append("|\t");
                    builder.append("Campo: ").append(node.data().getCampo().getNome()).append("\n");
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

    public String toStringFattoreConversione(double externalValue) {
        final StringBuilder builder = new StringBuilder();
        builder.append("\n");
        final int topNodeLevel = level();
        final HashSet<TreeNode<Categoria>> lastParent = new HashSet<>();
        Stack<ValoreCampo> valore = new Stack<>();

        TraversalAction<TreeNode<Categoria>> action = new TraversalAction<>() {
            @Override
            public void perform(TreeNode<Categoria> node) {
                int nodeLevel = node.level() - topNodeLevel;
                String stampa = printTree(node, nodeLevel);
                Stack<ValoreCampo> reverse = new Stack<>();

                if (node.isRoot()) {
                    lastParent.add(node);
                    node.data().getCampo().getDominio().forEach(reverse::push);
                } else if (!lastParent.contains(node.parent())) {
                    lastParent.add(node.parent());
                    node.parent().data().getCampo().getDominio().forEach(reverse::push);
                }
                while (!reverse.isEmpty()) {
                    valore.push(reverse.pop());
                }
                if (!node.isRoot()) {
                    builder.append(stampa).append("|\t\n")
                            .append(stampa).append("|\tValore: ").append(valore.pop()).append("\n")
                            .append(stampa).append("|---Nome: ").append(node.data().getNome());
                    if (node.isLeaf()) {
                        Double conversionValue = externalValue * fattoreConversione.getFattoreConversione(fattoreConversione.getFogliaRiferimento().level(), node.level()); // vedi se invertire (sono troppo fuso per capire)
                        builder.append(" [Conversione: ").append(conversionValue != null ? conversionValue : "N/A").append("]");
                    }
                    builder.append("\n");
                } else {
                    builder.append("Gerarchia: ").append(node.data().getNome()).append("\n");
                }
                if (!node.isLeaf()) {
                    if (!node.isRoot())
                        builder.append(stampa).append("|\t");
                    builder.append("Campo: ").append(node.data().getCampo().getNome()).append("\n");
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
}