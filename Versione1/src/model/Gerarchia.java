package model;

import com.scalified.tree.TraversalAction;
import com.scalified.tree.TreeNode;
import com.scalified.tree.multinode.LinkedMultiTreeNode;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Stack;

public class Gerarchia extends LinkedMultiTreeNode implements Serializable {
    private static final long serialVersionUID = 1L;
    //private final TreeNode<Categoria> root;

    /**
     * Costruisce una Gerarchia con la categoria radice specificata.
     *
     * @param rootCategoria la categoria radice della gerarchia
     */
    public Gerarchia(Categoria rootCategoria) {
        super(rootCategoria);

        //this.root = new LinkedMultiTreeNode<>(rootCategoria);
    }

    /**
     * Aggiunge una categoria come figlio di una categoria padre specificata.
     *
     * @param parentCategoria la categoria padre
     * @param childCategoria la categoria figlio da aggiungere
     * @return true se la categoria figlio è stata aggiunta correttamente, false altrimenti
     */
    public boolean addCategoria(Categoria parentCategoria, Categoria childCategoria) { // metodo di merda. usare add
        TreeNode<Categoria> parentNode = this.find(parentCategoria);

        return parentNode != null && parentNode.add(new LinkedMultiTreeNode<>(childCategoria));
    }

    /**
     * Returns the string representation of this object
     *
     * @return string representation of this object
     */
    @Override
    public String toString() {
        Stack<Attivita>attivita = new Stack<>(); // stack per le attivita', vengono aggiunte in cima per una questione di ordine del TraveralAction
        //final TreeNode<Categoria>[] lastParent = new TreeNode[]{null}; // per tenere traccia del nodo padre e non aggiungere attivita' doppie
        final HashSet<TreeNode<Categoria>> lastParent = new HashSet<>(); // per tenere traccia dei nodi padre e non aggiungere attivita' doppie
        final StringBuilder builder = new StringBuilder();
        builder.append("\n");
        final int topNodeLevel = level(); // livello del nodo di partenza

        TraversalAction<TreeNode<Categoria>> action = new TraversalAction<>() {
            @Override
            public void perform(TreeNode<Categoria> node) {
                if(node.isRoot()) {
                    //lastParent[0] = node;
                    lastParent.add(node);
                    Stack<Attivita> reverse = new Stack<>();
                    node.data().getCampo().getDominio().forEach(reverse::push);
                    while(!reverse.isEmpty()){
                        attivita.push(reverse.pop());
                    }
                }
                //if(!node.isRoot() && !node.parent().equals(lastParent[0])){
                if(!node.isRoot() && !lastParent.contains(node.parent())){
                    //lastParent[0] = node.parent();
                    lastParent.add(node.parent());
                    Stack<Attivita> reverse = new Stack<>();
                    node.parent().data().getCampo().getDominio().forEach(reverse::push);
                    while(!reverse.isEmpty()){
                        attivita.push(reverse.pop());
                    }
                }
                if(!node.isRoot())
                    builder.append("Attivita': ").append(attivita.pop()).append("\n");

                int nodeLevel = node.level() - topNodeLevel;
                for (int i = 0; i < nodeLevel; i++) { // TODO: sarebbe interessante distingurere i rami con simboli diversi
                    builder.append("|  "); // livello profondita
                }
                //builder.append("|  ".repeat(Math.max(0, nodeLevel))); // modo alternativo per ripetere una stringa

                builder.append("+- ").append("Nome: ").append(node.data().getNome()).append("\n");

                if(!node.isLeaf())
                    builder.append("Campo: ").append(node.data().getCampo().getNome());
                builder.append("\n\n");

            }

            @Override
            public boolean isCompleted() {
                return false;
            }
        };
        traversePreOrder(action);
        return builder.toString();
    }
}