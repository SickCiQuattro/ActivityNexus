package model;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Sistema di conversione tra categorie appartenenti a gerarchie diverse.
 * <p>
 * Gestisce fattori di conversione che rappresentano il rapporto di equivalenza
 * tra categorie, permettendo di confrontare attività eterogenee. Ad esempio, un
 * fattore di 1.5 tra "Nuoto" e "Corsa" significa che 1 ora di nuoto equivale
 * a 1.5 ore di corsa.
 * <p>
 * Funzionalità principali:
 * <ul>
 * <li>Gestione di fattori diretti tra coppie di categorie</li>
 * <li>Calcolo automatico di fattori inversi</li>
 * <li>Validazione della consistenza dei dati</li>
 * </ul>
 * 
 * @see Categoria
 * @see Gerarchia
 */
public class ConversioneGerarchie implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Messaggio di errore per fattori non positivi */
    private static final String ERRORE_FATTORE_NON_POSITIVO = "Il fattore di conversione deve essere positivo";

    /** Mappa delle conversioni tra categorie */
    private final Map<CoppiaDiCategorie, Double> fattori = new HashMap<>();

    // -----------------------------------------------------------------------------------
    // METODI DI ACCESSO AI FATTORI DI CONVERSIONE
    // -----------------------------------------------------------------------------------

    /**
     * Ottiene il fattore di conversione tra due categorie.
     * <p>
     * Cerca il fattore diretto o calcola l'inverso se necessario.
     *
     * @param prima   la categoria di origine
     * @param seconda la categoria di destinazione
     * @return il fattore arrotondato a due decimali, o null se non esiste relazione
     */
    public Double getFattoreConversione(Categoria prima, Categoria seconda) {
        // Verifica parametri null: se almeno uno è null, non può esistere conversione
        if (prima == null || seconda == null) {
            return null;
        }

        // Cerca prima il fattore diretto (da prima a seconda)
        Double fattore = getFattore(prima, seconda);
        if (fattore != null) {
            // Se trovato, arrotonda a due decimali e restituisce
            return fattore;
        }

        // Se non trova il fattore diretto, cerca quello inverso
        fattore = getFattore(seconda, prima);
        if (fattore != null && fattore != 0) {
            // Calcola l'inverso (1/fattore) per ottenere la conversione opposta
            fattore = 1.0 / fattore;
            // Arrotonda a due decimali e restituisce
            return fattore;
        }

        // Se non esiste né il fattore diretto né l'inverso, non c'è relazione
        return null;
    }

    /**
     * Aggiunge un fattore di conversione tra due categorie.
     * <p>
     * Il fattore rappresenta quante unità della seconda categoria equivalgono
     * a una unità della prima.
     *
     * @param prima   la categoria di origine
     * @param seconda la categoria di destinazione
     * @param fattore il fattore (deve essere positivo)
     * @throws IllegalArgumentException se il fattore è minore o uguale a zero
     */
    public void addFattoreConversione(Categoria prima, Categoria seconda, double fattore) {
        // Verifica parametri null: se uno dei parametri è null, termina senza effetti
        if (prima == null || seconda == null) {
            return;
        }

        // Verifica che il fattore sia positivo
        if (fattore <= 0) {
            throw new IllegalArgumentException(ERRORE_FATTORE_NON_POSITIVO);
        }

        // Non memorizzare fattori per categorie uguali (auto-riferimento)
        // poiché il fattore sarebbe sempre 1.0 per definizione
        if (prima.equals(seconda)) {
            return;
        }

        // Crea la coppia ordinata e memorizza il fattore nella mappa
        CoppiaOrdinata coppia = new CoppiaOrdinata(prima, seconda);
        fattori.put(coppia, fattore);
    }

    /**
     * Verifica se esiste un fattore di conversione tra due categorie.
     * <p>
     * Controlla l'esistenza di un fattore diretto o inverso.
     *
     * @param prima   la prima categoria
     * @param seconda la seconda categoria
     * @return true se esiste un fattore, false altrimenti
     */
    public boolean hasFattoreConversione(Categoria prima, Categoria seconda) {
        // Verifica parametri null: se almeno uno è null, non può esistere conversione
        if (prima == null || seconda == null) {
            return false;
        }

        // Verifica se esiste un fattore diretto o inverso
        return getFattore(prima, seconda) != null || getFattore(seconda, prima) != null;
    }

    /**
     * Rimuove il fattore di conversione tra due categorie.
     * <p>
     * Elimina solo la conversione diretta, non quella inversa.
     *
     * @param prima   la prima categoria
     * @param seconda la seconda categoria
     * @return true se il fattore è stato rimosso, false altrimenti
     */
    public boolean removeFattoreConversione(Categoria prima, Categoria seconda) {
        // Verifica parametri null: se almeno uno è null, non ci sono fattori da
        // rimuovere
        if (prima == null || seconda == null) {
            return false;
        }

        // Crea la coppia ordinata e tenta di rimuoverla dalla mappa
        // Il metodo remove() restituisce il valore associato alla chiave rimossa, o
        // null
        CoppiaOrdinata coppia = new CoppiaOrdinata(prima, seconda);
        return fattori.remove(coppia) != null;
    }

    /**
     * Rimuove tutti i fattori di conversione memorizzati.
     */
    public void clearFattori() {
        // Rimuove tutti gli elementi dalla mappa
        fattori.clear();
    }

    /**
     * Restituisce il numero di fattori di conversione diretti memorizzati.
     * <p>
     * Conta solo i fattori esplicitamente memorizzati, non quelli calcolabili
     * tramite inversione.
     *
     * @return il numero di coppie di conversione esplicitamente memorizzate
     */
    public int getNumeroFattori() {
        // Restituisce la dimensione della mappa (numero di coppie memorizzate)
        return fattori.size();
    }

    // -----------------------------------------------------------------------------------
    // METODI DI SUPPORTO
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce il fattore di conversione diretto per una coppia di categorie.
     * <p>
     * Metodo di supporto interno che non calcola fattori inversi.
     *
     * @param prima   la prima categoria
     * @param seconda la seconda categoria
     * @return il fattore diretto, o null se non esiste
     */
    private Double getFattore(Categoria prima, Categoria seconda) {
        // Verifica parametri null
        if (prima == null || seconda == null) {
            return null;
        }

        // Crea la coppia ordinata e cerca il fattore associato nella mappa
        CoppiaOrdinata coppia = new CoppiaOrdinata(prima, seconda);
        return fattori.get(coppia);
    }

    // -----------------------------------------------------------------------------------
    // CLASSE INTERNA
    // -----------------------------------------------------------------------------------

    /**
     * Coppia ordinata di categorie usata come chiave nella mappa dei fattori.
     * <p>
     * Rappresenta una relazione direzionale tra categorie, dove l'ordine è
     * rilevante:
     * (A,B) è diverso da (B,A).
     */
    private static class CoppiaOrdinata implements Serializable {
        // Identificatore di versione per la serializzazione
        private static final long serialVersionUID = 1L;

        // Le due categorie che formano la coppia ordinata
        private final Categoria prima;
        private final Categoria seconda;

        /**
         * Costruisce una coppia ordinata di categorie.
         *
         * @param prima   la prima categoria
         * @param seconda la seconda categoria
         */
        public CoppiaOrdinata(Categoria prima, Categoria seconda) {
            // Memorizza i riferimenti alle categorie nell'ordine specificato
            this.prima = prima;
            this.seconda = seconda;
        }

        /**
         * Confronta questa coppia ordinata con un altro oggetto.
         * <p>
         * 
         * @param obj l'oggetto da confrontare con questa coppia
         * @return true se obj è una CoppiaOrdinata con le stesse categorie nello stesso
         *         ordine
         */
        @Override
        public boolean equals(Object obj) {
            // Verifica identità di riferimento (ottimizzazione)
            if (this == obj)
                return true;
            // Verifica tipo e non-nullità
            if (obj == null || getClass() != obj.getClass())
                return false;

            // Cast sicuro (verificato sopra) e confronto delle categorie
            CoppiaOrdinata other = (CoppiaOrdinata) obj;
            // Usa Objects.equals per gestire correttamente anche i casi null
            return Objects.equals(this.prima, other.prima) &&
                    Objects.equals(this.seconda, other.seconda);
        }

        /**
         * Calcola l'hash code per questa coppia ordinata.
         * <p>
         * Il codice hash è calcolato combinando gli hash code delle due categorie.
         *
         * @return il codice hash della coppia ordinata
         */
        @Override
        public int hashCode() {
            // Usa Objects.hash per calcolare un hash combinato delle due categorie
            // Questo metodo gestisce correttamente i riferimenti null
            return Objects.hash(prima, seconda);
        }

        /**
         * Restituisce una rappresentazione in stringa della coppia ordinata.
         * <p>
         * 
         * @return una rappresentazione testuale della coppia ordinata
         */
        @Override
        public String toString() {
            // Crea una rappresentazione leggibile della coppia
            // Gestisce il caso in cui una delle categorie sia null
            return "(" + (prima != null ? prima.getNome() : "null") +
                    ", " + (seconda != null ? seconda.getNome() : "null") + ")";
        }
    }
}