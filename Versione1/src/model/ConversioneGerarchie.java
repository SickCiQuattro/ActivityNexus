package model;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Classe che rappresenta il sistema di conversione tra categorie appartenenti a
 * gerarchie diverse.
 * <p>
 * Questa classe implementa un meccanismo fondamentale per il confronto e la
 * conversione di attività
 * tra diverse gerarchie di classificazione, consentendo di stabilire rapporti
 * quantitativi tra
 * categorie anche quando appartengono a sistemi di classificazione
 * completamente diversi.
 * <p>
 * Il sistema di conversione memorizza "fattori di conversione" che
 * rappresentano il rapporto di
 * equivalenza tra due categorie. Ad esempio, se il fattore di conversione tra
 * "Nuoto" e "Corsa"
 * è 1.5, significa che un'unità di tempo dedicata al nuoto equivale a 1.5 unità
 * di tempo dedicate
 * alla corsa, in termini di valore o impegno.
 * <p>
 * Caratteristiche principali:
 * <ul>
 * <li>Memorizza i fattori di conversione diretti tra coppie di categorie</li>
 * <li>Calcola automaticamente i fattori di conversione inversi (se A→B = 2.0,
 * allora B→A = 0.5)</li>
 * <li>Fornisce metodi per aggiungere, recuperare e rimuovere fattori di
 * conversione</li>
 * <li>Garantisce la consistenza dei dati (fattori sempre positivi, no
 * auto-riferimenti)</li>
 * </ul>
 * <p>
 * I fattori di conversione sono essenziali per confrontare e aggregare attività
 * eterogenee
 * in un sistema che necessita di valutare l'equivalenza tra diverse tipologie
 * di impegno.
 * <p>
 * La classe implementa {@link Serializable} per consentire la persistenza della
 * rete di conversioni.
 * 
 * @see Categoria
 * @see Gerarchia
 * @see FattoreConversione
 */
public class ConversioneGerarchie implements Serializable {
    // Identificatore di versione per la serializzazione
    private static final long serialVersionUID = 1L;

    /**
     * Mappa che associa coppie ordinate di categorie ai rispettivi fattori di
     * conversione.
     * <p>
     * La mappa è dichiarata final per garantire che il riferimento rimanga
     * immutabile,
     * ma il suo contenuto può essere modificato tramite i metodi della classe.
     * <p>
     * Ogni coppia ordinata di categorie (A,B) è associata a un fattore che indica
     * quante unità di B equivalgono a una unità di A. Ad esempio, se la coppia è
     * (Nuoto, Corsa) con fattore 1.5, significa che 1 ora di nuoto equivale a 1.5
     * ore di corsa.
     */
    private final Map<CoppiaOrdinata, Double> fattori = new HashMap<>();

    // -----------------------------------------------------------------------------------
    // METODI DI ACCESSO AI FATTORI DI CONVERSIONE
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce il fattore di conversione tra due categorie.
     * <p>
     * Questo metodo ricerca il fattore di conversione tra le categorie specificate,
     * gestendo automaticamente la direzione della conversione. Se esiste un fattore
     * diretto
     * da prima a seconda, viene restituito. Se non esiste un fattore diretto ma
     * esiste
     * quello inverso, viene calcolato e restituito l'inverso del fattore.
     * <p>
     * Il fattore restituito rappresenta quante unità della seconda categoria
     * equivalgono
     * a una unità della prima categoria.
     * <p>
     * Esempio:
     * 
     * <pre>
     * // Se "Nuoto" → "Corsa" ha fattore 1.5 (1h di nuoto = 1.5h di corsa)
     * double fattore = conversione.getFattoreConversione(nuoto, corsa); // Ritorna 1.5
     * 
     * // La conversione inversa viene calcolata automaticamente
     * double fattoreInverso = conversione.getFattoreConversione(corsa, nuoto); // Ritorna 0.67
     * </pre>
     * <p>
     * Il valore restituito è arrotondato a due decimali per migliorare la
     * leggibilità
     * e ridurre errori di arrotondamento.
     *
     * @param prima   la categoria di origine della conversione
     * @param seconda la categoria di destinazione della conversione
     * @return il fattore di conversione arrotondato a due decimali, o null se non
     *         esiste
     *         nessuna relazione tra le categorie specificate
     * 
     * @see #addFattoreConversione(Categoria, Categoria, double)
     * @see #hasFattoreConversione(Categoria, Categoria)
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
            return Math.round(fattore * 100) / 100.0;
        }

        // Se non trova il fattore diretto, cerca quello inverso
        fattore = getFattore(seconda, prima);
        if (fattore != null && fattore != 0) {
            // Calcola l'inverso (1/fattore) per ottenere la conversione opposta
            fattore = 1.0 / fattore;
            // Arrotonda a due decimali e restituisce
            return Math.round(fattore * 100) / 100.0;
        }

        // Se non esiste né il fattore diretto né l'inverso, non c'è relazione
        return null;
    }

    /**
     * Aggiunge un fattore di conversione tra due categorie.
     * <p>
     * Il fattore di conversione rappresenta quante unità della seconda categoria
     * equivalgono a una unità della prima. Ad esempio, se prima=Nuoto,
     * seconda=Corsa
     * e fattore=1.5, significa che 1 ora di nuoto equivale a 1.5 ore di corsa.
     * <p>
     * Il metodo implementa controlli di validità:
     * <ul>
     * <li>Se una delle categorie è null, il metodo termina senza effetti</li>
     * <li>Se il fattore è zero o negativo, viene lanciata un'eccezione</li>
     * <li>Se le due categorie sono identiche, il fattore non viene memorizzato</li>
     * </ul>
     * <p>
     * Nota: non è necessario aggiungere esplicitamente la conversione inversa.
     * Il metodo {@link #getFattoreConversione(Categoria, Categoria)} calcolerà
     * automaticamente l'inverso quando necessario.
     *
     * @param prima   la categoria di origine della conversione
     * @param seconda la categoria di destinazione della conversione
     * @param fattore il fattore di conversione (deve essere positivo)
     * @throws IllegalArgumentException se il fattore è minore o uguale a zero
     * 
     * @see #removeFattoreConversione(Categoria, Categoria)
     * @see #getFattoreConversione(Categoria, Categoria)
     */
    public void addFattoreConversione(Categoria prima, Categoria seconda, double fattore) {
        // Verifica parametri null: se uno dei parametri è null, termina senza effetti
        if (prima == null || seconda == null) {
            return;
        }

        // Verifica che il fattore sia positivo
        if (fattore <= 0) {
            throw new IllegalArgumentException("Il fattore di conversione deve essere positivo");
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
     * Questo metodo controlla se è possibile convertire valori tra le categorie
     * specificate,
     * in qualsiasi direzione. Restituisce true se esiste un fattore di conversione
     * diretto
     * da prima a seconda o da seconda a prima.
     * <p>
     * Esempio di utilizzo:
     * 
     * <pre>
     * if (conversione.hasFattoreConversione(categoriaA, categoriaB)) {
     *     // È possibile convertire valori tra le due categorie
     *     double fattore = conversione.getFattoreConversione(categoriaA, categoriaB);
     *     // Usa il fattore...
     * }
     * </pre>
     *
     * @param prima   la prima categoria
     * @param seconda la seconda categoria
     * @return true se esiste un fattore di conversione diretto o inverso, false
     *         altrimenti
     *         o se almeno una delle categorie è null
     * 
     * @see #getFattoreConversione(Categoria, Categoria)
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
     * Questo metodo elimina solo la conversione diretta specificata, non quella
     * inversa.
     * Se si desidera rimuovere completamente la relazione tra due categorie,
     * potrebbe
     * essere necessario chiamare questo metodo due volte, invertendo l'ordine delle
     * categorie.
     * <p>
     * Esempio:
     * 
     * <pre>
     * // Per rimuovere completamente la relazione tra A e B:
     * conversione.removeFattoreConversione(A, B);
     * conversione.removeFattoreConversione(B, A);
     * </pre>
     *
     * @param prima   la prima categoria della coppia di conversione da rimuovere
     * @param seconda la seconda categoria della coppia di conversione da rimuovere
     * @return true se il fattore è stato trovato e rimosso, false se non esisteva
     *         o se almeno una delle categorie è null
     * 
     * @see #addFattoreConversione(Categoria, Categoria, double)
     * @see #clearFattori()
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
     * <p>
     * Questo metodo azzera la mappa dei fattori di conversione, eliminando tutte
     * le relazioni precedentemente definite tra categorie. Dopo questa chiamata,
     * {@link #getNumeroFattori()} restituirà 0 e nessuna conversione sarà
     * disponibile.
     * <p>
     * È utile quando si desidera riconfigurare completamente il sistema di
     * conversione,
     * ad esempio dopo modifiche strutturali significative alle gerarchie o alle
     * categorie.
     * 
     * @see #removeFattoreConversione(Categoria, Categoria)
     * @see #getNumeroFattori()
     */
    public void clearFattori() {
        // Rimuove tutti gli elementi dalla mappa
        fattori.clear();
    }

    /**
     * Restituisce il numero di fattori di conversione diretti memorizzati.
     * <p>
     * Questo metodo conta solo i fattori esplicitamente memorizzati, non quelli
     * calcolabili tramite inversione. Ad esempio, se è stato definito solo il
     * fattore
     * A→B, ma non B→A, questo metodo restituirà 1, anche se è possibile ottenere
     * entrambe le conversioni tramite
     * {@link #getFattoreConversione(Categoria, Categoria)}.
     * <p>
     * È utile per scopi diagnostici e per verificare la completezza del sistema di
     * conversione.
     *
     * @return il numero di coppie di conversione esplicitamente memorizzate
     * 
     * @see #clearFattori()
     */
    public int getNumeroFattori() {
        // Restituisce la dimensione della mappa (numero di coppie memorizzate)
        return fattori.size();
    }

    // -----------------------------------------------------------------------------------
    // METODI DI SUPPORTO
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce il fattore di conversione per una coppia ordinata di categorie.
     * <p>
     * Questo è un metodo di supporto interno che cerca direttamente nella mappa
     * il fattore associato alla coppia (prima, seconda), senza considerare la
     * direzione inversa o calcolare l'inverso del fattore.
     * <p>
     * A differenza di {@link #getFattoreConversione(Categoria, Categoria)}, questo
     * metodo:
     * <ul>
     * <li>Restituisce il valore grezzo, non arrotondato</li>
     * <li>Non cerca il fattore inverso se quello diretto non esiste</li>
     * <li>È pensato solo per uso interno della classe</li>
     * </ul>
     *
     * @param prima   la prima categoria della coppia
     * @param seconda la seconda categoria della coppia
     * @return il fattore di conversione diretto, o null se non esiste
     * 
     * @see #getFattoreConversione(Categoria, Categoria)
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
     * Classe interna che rappresenta una coppia ordinata di categorie.
     * <p>
     * Questa classe implementa il concetto di coppia ordinata, dove l'ordine degli
     * elementi
     * è rilevante: (A,B) è diverso da (B,A). È utilizzata come chiave nella mappa
     * dei
     * fattori di conversione, permettendo di associare fattori diversi a direzioni
     * diverse.
     * <p>
     * Ad esempio:
     * <ul>
     * <li>La coppia (Nuoto, Corsa) potrebbe avere fattore 1.5</li>
     * <li>La coppia (Corsa, Nuoto) potrebbe avere fattore 0.67</li>
     * </ul>
     * <p>
     * La classe implementa correttamente {@code equals()} e {@code hashCode()} per
     * funzionare come chiave nella mappa, e implementa {@link Serializable} per
     * consentire la persistenza insieme alla classe contenitore.
     */
    private static class CoppiaOrdinata implements Serializable {
        // Identificatore di versione per la serializzazione
        private static final long serialVersionUID = 1L;

        // Le due categorie che formano la coppia ordinata
        private final Categoria prima;
        private final Categoria seconda;

        /**
         * Costruisce una coppia ordinata di categorie.
         * <p>
         * Le categorie vengono memorizzate nell'ordine specificato, che è rilevante
         * per l'identità della coppia: (A,B) è diversa da (B,A).
         * <p>
         * Entrambe le categorie possono essere null, ma in tal caso la coppia
         * non sarà utile per le conversioni (sarà usata principalmente in casi di
         * gestione errori).
         *
         * @param prima   la prima categoria della coppia
         * @param seconda la seconda categoria della coppia
         */
        public CoppiaOrdinata(Categoria prima, Categoria seconda) {
            // Memorizza i riferimenti alle categorie nell'ordine specificato
            this.prima = prima;
            this.seconda = seconda;
        }

        /**
         * Confronta questa coppia ordinata con un altro oggetto.
         * <p>
         * Due coppie ordinate sono uguali se e solo se entrambi gli elementi
         * corrispondenti
         * sono uguali (o entrambi null). L'ordine è rilevante: (A,B) è diverso da
         * (B,A).
         * <p>
         * L'implementazione delega al metodo {@link Objects#equals(Object, Object)} per
         * gestire correttamente il caso in cui una delle categorie sia null.
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
         * Il codice hash è calcolato combinando gli hash code delle due categorie
         * in modo da garantire che:
         * <ul>
         * <li>Coppie uguali abbiano lo stesso hash code</li>
         * <li>Coppie con ordine diverso abbiano hash code diversi</li>
         * </ul>
         * <p>
         * L'implementazione utilizza {@link Objects#hash(Object...)} che gestisce
         * correttamente il caso in cui una delle categorie sia null.
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
         * Il formato della stringa è "(nome1, nome2)" dove nome1 e nome2 sono i nomi
         * delle categorie. Se una categoria è null, viene visualizzato "null" al suo
         * posto.
         * <p>
         * Questo metodo è utile per debugging e logging, consentendo di identificare
         * facilmente quali categorie sono coinvolte in una relazione di conversione.
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