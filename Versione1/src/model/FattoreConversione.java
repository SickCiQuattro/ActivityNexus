package model;

import java.util.HashMap;
import java.util.Map;

// ATTENZIONE: passare nodo.level() e non il nodo stesso
// oggetto di ogni Gerarchia
public class FattoreConversione {
    private int nodoRiferimento = 1;
    private final Map<Integer, Double> fattoriConversione = new HashMap<>();

    /**
     * Verifica se un fattore di conversione è già presente
     * @param nodo
     * @return
     */
    public boolean checkFattoreConversione(int nodo) {
        return nodoRiferimento != nodo && !fattoriConversione.containsKey(nodo);
    }

    public void addFattoreConversione(int nodo, double value) {
        fattoriConversione.put(nodo, value);
    }

    public Double getFattoreConversione(int primo, int secondo) {
        if (primo == nodoRiferimento) {
            if (secondo == nodoRiferimento) {
                return 1.0;
            }
            return fattoriConversione.get(secondo);
        } else if (secondo == nodoRiferimento) {
            return 1 / fattoriConversione.get(primo);
        } else {
            return fattoriConversione.get(secondo) / fattoriConversione.get(primo);
            /*  da primo a secondo significa primo -> nodoRiferimento -> secondo
            => fattore = 1 / fattore(primo) * fattore(secondo)
            => fattore = fattore(secondo) / fattore(primo)
            */
        }
    }

    public void setNodoRiferimento(int nodoRiferimento) {
        this.nodoRiferimento = nodoRiferimento;
    }
    public int getNodoRiferimento() {
        return nodoRiferimento;
    }
}