package model;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Sistema di conversione tra categorie appartenenti a gerarchie diverse.
 * <p>
 * Questa classe gestisce i fattori di conversione che rappresentano il rapporto
 * di
 * equivalenza tra attività di categorie diverse, permettendo di confrontare e
 * scambiare prestazioni eterogenee. Il fattore di conversione quantifica quanto
 * "vale" un'attività rispetto ad un'altra.
 * <p>
 * <b>Funzionalità principali:</b>
 * <ul>
 * <li>Memorizzazione di fattori diretti tra coppie di categorie</li>
 * <li>Calcolo automatico di fattori inversi (reciproci)</li>
 * <li>Validazione della consistenza matematica dei fattori</li>
 * <li>Supporto alla serializzazione per persistenza</li>
 * </ul>
 *
 * <p>
 * <b>Invarianti della classe:</b>
 * <ul>
 * <li>La mappa dei fattori non è mai null</li>
 * <li>Tutti i fattori di conversione memorizzati sono positivi (> 0)</li>
 * <li>Non esistono fattori di conversione per categorie identiche
 * (auto-riferimenti)</li>
 * <li>Le chiavi della mappa sono sempre istanze di CoppiaOrdinata non null</li>
 * </ul>
 *
 * @see Categoria
 * @see Gerarchia
 */
public class ConversioneGerarchie implements Serializable {
    // -----------------------------------------------------------------------------------
    // FIELDS E COSTANTI
    // -----------------------------------------------------------------------------------

    /** Identificatore di versione per la serializzazione */
    @Serial
    private static final long serialVersionUID = 1L;

    /** Messaggio di errore per fattori non positivi */
    private static final String ERRORE_FATTORE_NON_POSITIVO = "Il fattore di conversione deve essere positivo";

    /** Mappa che associa coppie di categorie ai relativi fattori di conversione */
    private final Map<CoppiaOrdinata, Double> fattori = new HashMap<>();

    // -----------------------------------------------------------------------------------
    // METODI DI ACCESSO AI FATTORI DI CONVERSIONE
    // -----------------------------------------------------------------------------------

    /**
     * Ottiene il fattore di conversione tra due categorie.
     * <p>
     * Il metodo ricerca un fattore di conversione diretto o, se non disponibile,
     * calcola l'inverso del fattore nella direzione opposta. Se le categorie sono
     * identiche, restituisce il fattore identità (1.0).
     *
     * @param prima   la categoria di origine (da cui si converte)
     * @param seconda la categoria di destinazione (verso cui si converte)
     * @return il fattore di conversione o null se non esiste relazione
     *
     * @post se prima == null o seconda == null, result == null
     * @post se prima.equals(seconda), result == 1.0 (fattore identità)
     * @post se esiste un fattore diretto, result == tale valore
     * @post se esiste un fattore inverso, result == 1/fattore_inverso
     * @post se non esiste né un fattore diretto né uno inverso, result == null
     */
    public Double getFattoreConversione(Categoria prima, Categoria seconda) {
        // Verifica parametri null: se almeno uno è null, non può esistere conversione
        if (prima == null || seconda == null) {
            return null;
        }

        // Se le categorie sono uguali, il fattore è 1.0 (identità)
        if (prima.equals(seconda)) {
            return 1.0;
        }

        // Cerca prima il fattore diretto (da prima a seconda)
        Double fattore = getFattore(prima, seconda);
        if (fattore != null) {
            // Se trovato, restituisce direttamente
            return fattore;
        }

        // Se non trova il fattore diretto, cerca quello inverso
        fattore = getFattore(seconda, prima);
        if (fattore != null && fattore != 0) {
            // Calcola l'inverso (1/fattore) per ottenere la conversione opposta
            return 1.0 / fattore;
        }

        // Se non esiste né il fattore diretto né l'inverso, non c'è relazione
        return null;
    }

    /**
     * Aggiunge un fattore di conversione tra due categorie.
     * <p>
     * Il fattore rappresenta quante unità della seconda categoria equivalgono
     * a una unità della prima. Ad esempio, se prima=Nuoto e seconda=Corsa e
     * fattore=1.5,
     * significa che 1 ora di nuoto equivale a 1.5 ore di corsa.
     *
     * @param prima   la categoria di origine
     * @param seconda la categoria di destinazione
     * @param fattore il fattore di conversione (deve essere positivo)
     * @throws IllegalArgumentException se il fattore è minore o uguale a zero
     *
     * @pre fattore > 0
     * @post se prima == null o seconda == null, non viene memorizzato alcun fattore
     * @post se prima.equals(seconda), non viene memorizzato alcun fattore
     * @post se prima != null e seconda != null e !prima.equals(seconda),
     *       getFattoreConversione(prima, seconda) restituisce il fattore
     *       specificato
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
    // METODI DI SUPPORTO PRIVATI
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce il fattore di conversione diretto per una coppia di categorie.
     * <p>
     * Questo è un metodo di supporto interno che cerca solo il fattore diretto
     * nella mappa, senza calcolare l'inverso.
     *
     * @param prima   la prima categoria
     * @param seconda la seconda categoria
     * @return il fattore diretto memorizzato, o null se non esiste
     *
     * @post se prima == null o seconda == null, result == null
     * @post se esiste un fattore diretto per la coppia (prima, seconda), result ==
     *       tale valore
     * @post se non esiste un fattore diretto, result == null
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
     * rilevante: (A,B) è diverso da (B,A). Questa distinzione è fondamentale
     * per rappresentare correttamente i fattori di conversione, che dipendono
     * dalla direzione.
     *
     * <p>
     * <b>Invarianti:</b>
     * <ul>
     * <li>Le categorie prima e seconda possono essere null</li>
     * <li>Due coppie sono uguali se e solo se prima e seconda sono uguali
     * nell'ordine</li>
     * </ul>
     */
    private static class CoppiaOrdinata implements Serializable {
        /** Identificatore di versione per la serializzazione */
        @Serial
        private static final long serialVersionUID = 1L;

        /** Prima categoria della coppia */
        private final Categoria prima;

        /** Seconda categoria della coppia */
        private final Categoria seconda;

        /**
         * Costruisce una coppia ordinata di categorie.
         * <p>
         * L'ordine delle categorie è significativo per l'identità della coppia.
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
         * <p>
         * Due coppie sono considerate uguali se contengono le stesse categorie
         * nello stesso ordine.
         *
         * @param obj l'oggetto da confrontare con questa coppia
         * @return true se obj è una CoppiaOrdinata con le stesse categorie nello stesso
         *         ordine
         *
         * @post se obj == this, result == true
         * @post se obj == null o obj è di classe diversa, result == false
         * @post result == true se e solo se obj è una CoppiaOrdinata con prima e
         *       seconda uguali
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
         * Il codice hash è calcolato combinando gli hash code delle due categorie,
         * mantenendo la coerenza con il metodo equals().
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
         * <p>
         * Il formato è: "(nome_prima, nome_seconda)" o "(null, nome_seconda)"
         * se la prima categoria è null e analogamente per la seconda.
         *
         * @return una rappresentazione testuale della coppia ordinata
         *
         * @post result != null
         * @post result contiene una rappresentazione di entrambe le categorie
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