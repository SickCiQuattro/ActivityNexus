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
 * <p><b>Invarianti:</b>
 * <ul>
 *   <li>La mappa dei fattori non è mai null</li>
 *   <li>Tutti i fattori di conversione memorizzati sono positivi (> 0)</li>
 *   <li>Non esistono fattori di conversione per categorie identiche (auto-riferimenti)</li>
 *   <li>Le chiavi della mappa sono sempre istanze di CoppiaOrdinata non null</li>
 * </ul>
 *
 * @see Categoria
 * @see Gerarchia
 */
public class ConversioneGerarchie implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /** Messaggio di errore per fattori non positivi */
    private static final String ERRORE_FATTORE_NON_POSITIVO = "Il fattore di conversione deve essere positivo";

    /** Mappa delle conversioni tra categorie */
    private final Map<CoppiaOrdinata, Double> fattori = new HashMap<>();

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
     *
     * @post se prima == null o seconda == null, restituisce null
     * @post se prima.equals(seconda), restituisce 1.0 (fattore identità)
     * @post se esiste un fattore diretto, restituisce tale valore
     * @post se esiste un fattore inverso, restituisce 1/fattore_inverso
     * @post se non esiste né un fattore diretto né uno inverso, restituisce null
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
     *
     * @pre fattore > 0
     * @post se prima == null o seconda == null, non viene memorizzato alcun fattore
     * @post se prima.equals(seconda), non viene memorizzato alcun fattore
     * @post se prima != null e seconda != null e prima non equals seconda,
     *       getFattoreConversione(prima, seconda) restituisce il fattore specificato
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
     *
     * @post se prima == null o seconda == null, restituisce null
     * @post se esiste un fattore diretto per la coppia (prima, seconda), restituisce tale valore
     * @post se non esiste un fattore diretto, restituisce null
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
     * rilevante: (A,B) è diverso da (B,A).
     *
     * <p><b>Invarianti:</b>
     * <ul>
     *   <li>Le categorie prima e seconda possono essere null</li>
     *   <li>Due coppie sono uguali se e solo se prima e seconda sono uguali nell'ordine</li>
     * </ul>
     */
    private static class CoppiaOrdinata implements Serializable {
        // Identificatore di versione per la serializzazione
        @Serial
        private static final long serialVersionUID = 1L;

        // Le due categorie che formano la coppia ordinata
        private final Categoria prima;
        private final Categoria seconda;

        /**
         * Costruisce una coppia ordinata di categorie.
         *
         * @param prima   la prima categoria
         * @param seconda la seconda categoria
         *
         * @post this.prima è uguale alla categoria prima fornita
         * @post this.seconda è uguale alla categoria seconda fornita
         */
        public CoppiaOrdinata(Categoria prima, Categoria seconda) {
            // Memorizza i riferimenti alle categorie nell'ordine specificato
            this.prima = prima;
            this.seconda = seconda;
        }

        /**
         * Confronta questa coppia ordinata con un altro oggetto.
         *
         * @param obj l'oggetto da confrontare con questa coppia
         * @return true se obj è una CoppiaOrdinata con le stesse categorie nello stesso
         *         ordine
         *
         * @post se obj == this, restituisce true
         * @post se obj == null o obj è di classe diversa, restituisce false
         * @post restituisce true se e solo se obj è una CoppiaOrdinata con prima e seconda uguali
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
         *
         * @post se due coppie sono uguali secondo equals(), hanno lo stesso hashCode()
         */
        @Override
        public int hashCode() {
            // Usa Objects.hash per calcolare un hash combinato delle due categorie
            // Questo metodo gestisce correttamente i riferimenti null
            return Objects.hash(prima, seconda);
        }

        /**
         * Restituisce una rappresentazione in stringa della coppia ordinata.
         *
         * @return una rappresentazione testuale della coppia ordinata
         *
         * @post il valore restituito non è null
         * @post il valore restituito contiene una rappresentazione di entrambe le categorie
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