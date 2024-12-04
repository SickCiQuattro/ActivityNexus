package model;

import com.scalified.tree.TraversalAction;
import com.scalified.tree.TreeNode;
import com.scalified.tree.multinode.LinkedMultiTreeNode;
import java.io.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Stack;

/**
 * Classe che rappresenta una gerarchia di categorie.
 * Una gerarchia è costituita da una categoria radice e da una struttura ad albero di categorie figlie.
 */
public class Gerarchia extends LinkedMultiTreeNode implements Serializable {
    private static final long serialVersionUID = 1L;

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
     * @param childCategoria la categoria figlio da aggiungere
     * @return true se la categoria figlio è stata aggiunta correttamente, false altrimenti
     */
    public boolean addCategoria(Categoria parentCategoria, Categoria childCategoria) {
        TreeNode<Categoria> parentNode = this.find(parentCategoria);
        return parentNode != null && parentNode.add(new LinkedMultiTreeNode<>(childCategoria));
    }

    /**
     * Stampa la struttura dell'albero.
     *
     * @param node il nodo corrente
     * @param nodeLevel il livello del nodo
     * @return una stringa che rappresenta la struttura dell'albero
     */
    private String printTree(TreeNode<Categoria> node, int nodeLevel) {
        StringBuilder builder = new StringBuilder();
        Gerarchia parent, figlio, lastElement = null;
        Collection<Gerarchia> figli;

        for(int i = 1; i < nodeLevel; i++) { // dal livello 2 in poi
            parent = (Gerarchia) node.parent(); //parent = (Gerarchia) this.find(node.parent().data());
            figlio = (Gerarchia) node;
            for(int j = 0; j < i; j++) { // scorre i livelli verso la radice
                parent = (Gerarchia) parent.parent();
                figlio = (Gerarchia) figlio.parent();
            }
            figli = parent.subtrees(); // ricerca ultimo figlio del nodo padre
            for (Gerarchia f : figli) {
                lastElement = f;
            }
            builder.append("\t");
            if (!lastElement.data().equals(figlio.data())) // se non è l'ultimo figlio si aggiunge il collegamento
                builder.append("|");
        }
        return builder.reverse().toString(); // inverte la stringa
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
     * @throws IOException se si verifica un errore di I/O
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
                }
                else if (!lastParent.contains(node.parent())) { // se il padre non è già stato preso in considerazione
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
                }
                else
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

    /**
     * Restituisce un hash code per questa gerarchia.
     *
     * @return un hash code per questa gerarchia
     */
    //@Override
    //public int hashCode() {
    //    return ((Categoria) this.root().data()).getNome().hashCode();
        //return this.root().data().hashCode();
    //}
}