package model;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Classe che rappresenta i fattori di conversione tra categorie di una
 * gerarchia.
 * Permette di memorizzare e recuperare i fattori di conversione tra diverse
 * categorie,
 * gestendo automaticamente anche le conversioni inverse.
 */
public class ConversioneGerarchie implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Mappa che associa coppie ordinate di categorie ai rispettivi fattori di
     * conversione
     */
    private final Map<CoppiaOrdinata, Double> fattori = new HashMap<>();

    // -----------------------------------------------------------------------------------
    // METODI DI ACCESSO AI FATTORI DI CONVERSIONE
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce il fattore di conversione tra due categorie.
     * Se il fattore di conversione non esiste nella direzione richiesta,
     * cerca la coppia invertita e restituisce l'inverso del fattore di conversione.
     *
     * @param prima   la prima categoria
     * @param seconda la seconda categoria
     * @return il fattore di conversione, o null se non esiste nessuna relazione tra
     *         le categorie
     */
    public Double getFattoreConversione(Categoria prima, Categoria seconda) {
        if (prima == null || seconda == null) {
            return null;
        }

        Double fattore = getFattore(prima, seconda);
        if (fattore != null) {
            return fattore;
        }

        // Se non si trova il fattore diretto, si cerca quello inverso
        fattore = getFattore(seconda, prima);
        if (fattore != null && fattore != 0) {
            return 1.0 / fattore;
        }

        return null;
    }

    /**
     * Aggiunge un fattore di conversione tra due categorie.
     * Se le categorie sono uguali, non viene memorizzato alcun fattore.
     *
     * @param prima   la prima categoria
     * @param seconda la seconda categoria
     * @param fattore il fattore di conversione
     * @throws IllegalArgumentException se il fattore è minore o uguale a zero
     */
    public void addFattoreConversione(Categoria prima, Categoria seconda, double fattore) {
        if (prima == null || seconda == null) {
            return;
        }

        if (fattore <= 0) {
            throw new IllegalArgumentException("Il fattore di conversione deve essere positivo");
        }

        // Non memorizzare fattori per categorie uguali
        if (prima.equals(seconda)) {
            return;
        }

        CoppiaOrdinata coppia = new CoppiaOrdinata(prima, seconda);
        fattori.put(coppia, fattore);
    }

    /**
     * Verifica se esiste un fattore di conversione tra due categorie.
     *
     * @param prima   la prima categoria
     * @param seconda la seconda categoria
     * @return true se esiste un fattore di conversione diretto o inverso, false
     *         altrimenti
     */
    public boolean hasFattoreConversione(Categoria prima, Categoria seconda) {
        if (prima == null || seconda == null) {
            return false;
        }

        return getFattore(prima, seconda) != null || getFattore(seconda, prima) != null;
    }

    /**
     * Rimuove il fattore di conversione tra due categorie.
     *
     * @param prima   la prima categoria
     * @param seconda la seconda categoria
     * @return true se il fattore è stato rimosso, false se non esisteva
     */
    public boolean removeFattoreConversione(Categoria prima, Categoria seconda) {
        if (prima == null || seconda == null) {
            return false;
        }

        CoppiaOrdinata coppia = new CoppiaOrdinata(prima, seconda);
        return fattori.remove(coppia) != null;
    }

    /**
     * Rimuove tutti i fattori di conversione.
     */
    public void clearFattori() {
        fattori.clear();
    }

    /**
     * Restituisce il numero di fattori di conversione memorizzati.
     *
     * @return il numero di fattori di conversione
     */
    public int getNumeroFattori() {
        return fattori.size();
    }

    // -----------------------------------------------------------------------------------
    // METODI DI SUPPORTO
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce il fattore di conversione per una coppia ordinata di categorie.
     * Questo metodo non considera la coppia inversa.
     *
     * @param prima   la prima categoria
     * @param seconda la seconda categoria
     * @return il fattore di conversione, o null se non esiste
     */
    private Double getFattore(Categoria prima, Categoria seconda) {
        if (prima == null || seconda == null) {
            return null;
        }

        CoppiaOrdinata coppia = new CoppiaOrdinata(prima, seconda);
        return fattori.get(coppia);
    }

    // -----------------------------------------------------------------------------------
    // CLASSE INTERNA
    // -----------------------------------------------------------------------------------

    /**
     * Classe interna che rappresenta una coppia ordinata di categorie.
     * Utilizzata come chiave nella mappa dei fattori di conversione.
     * L'ordine delle categorie è importante: (A,B) è diverso da (B,A).
     */
    private static class CoppiaOrdinata implements Serializable {
        private static final long serialVersionUID = 1L;

        private final Categoria prima;
        private final Categoria seconda;

        /**
         * Costruisce una coppia ordinata di categorie.
         *
         * @param prima   la prima categoria
         * @param seconda la seconda categoria
         */
        public CoppiaOrdinata(Categoria prima, Categoria seconda) {
            this.prima = prima;
            this.seconda = seconda;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null || getClass() != obj.getClass())
                return false;
            CoppiaOrdinata other = (CoppiaOrdinata) obj;
            return Objects.equals(this.prima, other.prima) &&
                    Objects.equals(this.seconda, other.seconda);
        }

        @Override
        public int hashCode() {
            return Objects.hash(prima, seconda);
        }

        @Override
        public String toString() {
            return "(" + (prima != null ? prima.getNome() : "null") +
                    ", " + (seconda != null ? seconda.getNome() : "null") + ")";
        }
    }
}