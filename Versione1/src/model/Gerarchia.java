package model;

import com.scalified.tree.TreeNode;
import com.scalified.tree.multinode.LinkedMultiTreeNode;

import java.io.Serializable;

public class Gerarchia implements Serializable {
    private static final long serialVersionUID = 1L;
    private final TreeNode<Categoria> root;

    /**
     * Costruisce una Gerarchia con la categoria radice specificata.
     *
     * @param rootCategoria la categoria radice della gerarchia
     */
    public Gerarchia(Categoria rootCategoria) {

        this.root = new LinkedMultiTreeNode<>(rootCategoria);
    }

    /**
     * Aggiunge una categoria come figlio di una categoria padre specificata.
     *
     * @param parentCategoria la categoria padre
     * @param childCategoria la categoria figlio da aggiungere
     * @return true se la categoria figlio è stata aggiunta correttamente, false altrimenti
     */
    public boolean addCategoria(Categoria parentCategoria, Categoria childCategoria) {
        TreeNode<Categoria> parentNode = root.find(parentCategoria);

        return parentNode != null && parentNode.add(new LinkedMultiTreeNode<>(childCategoria));
    }
}