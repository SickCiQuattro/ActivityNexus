package model;

import com.scalified.tree.TreeNode;

import java.util.HashMap;
import java.util.Map;

public class FattoreConversione<T> {
    private Map<CoppiaOrdinata<T>, Double> fattoriConversione = new HashMap<>();

    public void addFattoreConversione(T primo, T secondo, double value) {
        fattoriConversione.put(new CoppiaOrdinata<>(primo, secondo), value);
    }

    public Double getFattoreConversione(T primo, T secondo) {
        Double value = fattoriConversione.get(new CoppiaOrdinata<>(primo, secondo));
        if (value != null) {
            return value;
        }
        value = fattoriConversione.get(new CoppiaOrdinata<>(secondo, primo));
        if (value != null && value != 0) {
            return 1 / value;
        }
        return null;
    }
}
