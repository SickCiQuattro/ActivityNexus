package model;

import com.scalified.tree.TraversalAction;
import com.scalified.tree.TreeNode;
import com.scalified.tree.multinode.LinkedMultiTreeNode;

import java.io.*;
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
        Stack<ValoreCampo> valore = new Stack<>();
        final HashSet<TreeNode<Categoria>> lastParent = new HashSet<>();
        final StringBuilder builder = new StringBuilder();
        builder.append("\n");
        final int topNodeLevel = level();

        TraversalAction<TreeNode<Categoria>> action = new TraversalAction<>() {
            @Override
            public void perform(TreeNode<Categoria> node) {
                if (node.isRoot()) {
                    lastParent.add(node);
                    Stack<ValoreCampo> reverse = new Stack<>();
                    node.data().getCampo().getDominio().forEach(reverse::push);
                    while (!reverse.isEmpty()) {
                        valore.push(reverse.pop());
                    }
                }
                if (!node.isRoot() && !lastParent.contains(node.parent())) {
                    lastParent.add(node.parent());
                    Stack<ValoreCampo> reverse = new Stack<>();
                    node.parent().data().getCampo().getDominio().forEach(reverse::push);
                    while (!reverse.isEmpty()) {
                        valore.push(reverse.pop());
                    }
                }
                if (!node.isRoot())
                    builder.append("Valore': ").append(valore.pop()).append("\n");

                int nodeLevel = node.level() - topNodeLevel;
                for (int i = 0; i < nodeLevel; i++) {
                    builder.append("|  ");
                }
                builder.append("+- ").append("Nome: ").append(node.data().getNome()).append("\n");

                if (!node.isLeaf())
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

    /**
     * Confronta questa gerarchia con l'oggetto specificato.
     * Il risultato è true se e solo se l'argomento non è null ed è un oggetto Gerarchia che ha la stessa categoria radice di questo oggetto.
     *
     * @param obj l'oggetto da confrontare con questa Gerarchia
     * @return true se l'oggetto è uguale a questa Gerarchia, false altrimenti
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Gerarchia)) {
            return false;
        }
        Gerarchia other = (Gerarchia) obj;
        Categoria otherRoot = (Categoria) other.root().data();
        Categoria root = (Categoria) this.root().data();
        return root.equals(otherRoot);
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