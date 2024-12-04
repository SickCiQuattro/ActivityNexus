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
        Stack<ValoreCampo> valore = new Stack<>(); // stack per i valori dei campi
        final HashSet<TreeNode<Categoria>> lastParent = new HashSet<>(); // controllo sui padri per evitare di ripetere i valori nello stack
        final StringBuilder builder = new StringBuilder();
        builder.append("\n");
        final int topNodeLevel = level(); // livello della radice (sempre 0 con un albero completo)
        TraversalAction<TreeNode<Categoria>> action = new TraversalAction<>() { // itera su ogni nodo dell'albero da sinistra a destra
            @Override
            public void perform(TreeNode<Categoria> node) {
                Stack<ValoreCampo> reverse = new Stack<>(); // stack per invertire l'ordine dei valori
                int nodeLevel = node.level() - topNodeLevel; // livello del nodo rispetto alla radice

                if (node.isRoot()) {
                    lastParent.add(node);
                    node.data().getCampo().getDominio().forEach(reverse::push);
                }
                else if (!lastParent.contains(node.parent())) {
                    lastParent.add(node.parent());
                    node.parent().data().getCampo().getDominio().forEach(reverse::push);
                }
                while (!reverse.isEmpty()) {
                    valore.push(reverse.pop()); // inserisce i valori nello stack
                }
                if (!node.isRoot()) {
                    builder.append(printTree(node, nodeLevel)).append("|\t\n"); // collegamento padre-figlio
                    builder.append(printTree(node, nodeLevel)).append("|\t"); // valore
                    builder.append("Valore: ").append(valore.pop()).append("\n");
                    builder.append(printTree(node, nodeLevel)); // nome
                    builder.append("|---").append("Nome: ").append(node.data().getNome()).append("\n");
                }
                else
                    builder.append("Gerarchia: ").append(node.data().getNome()).append("\n");

                if (!node.isLeaf()) {
                    if (!node.isRoot())
                        builder.append(printTree(node, nodeLevel)).append("|\t"); // campo
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
            figli = parent.subtrees(); // ricerca ultimo figlo del nodo padre
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