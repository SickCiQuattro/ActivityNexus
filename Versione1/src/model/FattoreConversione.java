package model;

import com.scalified.tree.TreeNode;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

// ATTENZIONE: passare nodo.level() e non il nodo stesso
// oggetto di ogni Gerarchia
public class FattoreConversione implements Serializable {
    private static final long serialVersionUID = 1L;

    private TreeNode<Categoria> fogliaRiferimento;
    private int livelloRiferimento = 1;
    private final Map<Integer, Double> fattoriConversione = new HashMap<>();

    /**
     * Verifica se un fattore di conversione è già presente
     * @param nodo
     * @return
     */
    public boolean checkFattoreConversione(int nodo) {
        return livelloRiferimento != nodo && !fattoriConversione.containsKey(nodo);
    }

    public void addFattoreConversione(int nodo, double value) {
        fattoriConversione.put(nodo, value);
    }

    public Double getFattoreConversione(int primo, int secondo) {
        if (primo == livelloRiferimento) {
            if (secondo == livelloRiferimento) {
                return 1.0;
            }
            return fattoriConversione.get(secondo);
        } else if (secondo == livelloRiferimento) {
            return 1 / fattoriConversione.get(primo);
        } else {
            return fattoriConversione.get(secondo) / fattoriConversione.get(primo);
            /*  da primo a secondo significa primo -> nodoRiferimento -> secondo
            => fattore = 1 / fattore(primo) * fattore(secondo)
            => fattore = fattore(secondo) / fattore(primo)
            */
        }
    }

    public void setNodoRiferimento(TreeNode<Categoria> fogliaRiferimento){
        this.fogliaRiferimento = fogliaRiferimento;
        this.setNodoRiferimento(fogliaRiferimento.level());
    }

    public void setNodoRiferimento(int nodoRiferimento) {
        this.livelloRiferimento = nodoRiferimento;
    }
    public TreeNode<Categoria> getFogliaRiferimento() {
        return fogliaRiferimento;
    }

    /**
     * Scrive l'oggetto FattoreConversione in un flusso di output.
     *
     * @param out il flusso di output
     * @throws IOException se si verifica un errore di I/O
     */
    @Serial
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    /**
     * Legge l'oggetto FattoreConversione da un flusso di input.
     *
     * @param in il flusso di input
     * @throws IOException se si verifica un errore di I/O
     * @throws ClassNotFoundException se la classe non viene trovata
     */
    @Serial
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }
}