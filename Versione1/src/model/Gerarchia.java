package model;

import com.scalified.tree.TreeNode;
import com.scalified.tree.multinode.ArrayMultiTreeNode;

public class Gerarchia {
    private final TreeNode<Categoria> root;

    /**
     * Costruisce una Gerarchia con la categoria radice specificata.
     *
     * @param rootCategoria la categoria radice della gerarchia
     */
    public Gerarchia(Categoria rootCategoria) {
        this.root = new ArrayMultiTreeNode<>(rootCategoria);
    }

    /**
     * Aggiunge una categoria come figlio di una categoria padre specificata.
     *
     * @param parentCategoria la categoria padre
     * @param childCategoria la categoria figlio da aggiungere
     * @return true se la categoria figlio è stata aggiunta correttamente, false altrimenti
     */
    public boolean addCategoria(Categoria parentCategoria, Categoria childCategoria) {
        TreeNode<Categoria> parentNode = findNode(root, parentCategoria);
        if (parentNode != null) {
            parentNode.add(new ArrayMultiTreeNode<>(childCategoria));
            return true;
        }
        return false;
    }

    /**
     * Trova un nodo nella gerarchia.
     *
     * @param currentNode il nodo corrente
     * @param categoria la categoria da trovare
     * @return il nodo trovato, o null se non trovato
     */
    private TreeNode<Categoria> findNode(TreeNode<Categoria> currentNode, Categoria categoria) {
        if (currentNode.data().equals(categoria)) {
            return currentNode;
        }
        for (TreeNode<Categoria> child : currentNode.subtrees()) {
            TreeNode<Categoria> result = findNode(child, categoria);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    /**
     * Restituisce una rappresentazione in stringa della gerarchia.
     *
     * @return una stringa che rappresenta la gerarchia
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        buildString(root, sb, 0);
        return sb.toString();
    }

    /**
     * Costruisce una rappresentazione in stringa della gerarchia.
     *
     * @param node il nodo corrente
     * @param sb il StringBuilder per costruire la stringa
     * @param level il livello corrente nella gerarchia
     */
    private void buildString(TreeNode<Categoria> node, StringBuilder sb, int level) {
        for (int i = 0; i < level; i++) {
            sb.append("\t");
        }
        sb.append(node.data().toString()).append("\n");
        for (TreeNode<Categoria> child : node.subtrees()) {
            buildString(child, sb, level + 1);
        }
    }
}