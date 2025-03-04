package model;

import java.io.*;
import java.util.*;

/**
 * Rappresenta un comprensorio geografico che raggruppa più comuni.
 * <p>
 * Caratteristiche principali:
 * <ul>
 * <li>Identificato da un nome univoco (case-insensitive)</li>
 * <li>Contiene una lista gestita internamente di comuni</li>
 * <li>Impedisce duplicazioni di comuni (case-insensitive)</li>
 * <li>Implementa {@link Serializable} per la persistenza</li>
 * </ul>
 * <p>
 * Due comprensori sono considerati uguali se hanno lo stesso nome,
 * indipendentemente dai comuni contenuti.
 * 
 * @see java.io.Serializable
 */
public class Comprensorio implements Serializable {
    // Identificatore di versione per la serializzazione
    @Serial
    private static final long serialVersionUID = 1L;

    // Nome del comprensorio, utilizzato come identificatore univoco
    private String nome;

    // Lista dei comuni appartenenti al comprensorio
    // Dichiarata final per garantire che il riferimento non cambi,
    // ma i contenuti possono essere modificati
    private final List<String> comuni;

    // -----------------------------------------------------------------------------------
    // COSTRUTTORI
    // -----------------------------------------------------------------------------------

    /**
     * Costruisce un Comprensorio con il nome specificato e lista vuota di comuni.
     *
     * @param nome il nome del comprensorio
     */
    public Comprensorio(String nome) {
        this.nome = nome; // Accetta anche null, anche se non consigliato
        this.comuni = new ArrayList<>(); // Inizializza una lista vuota
    }

    /**
     * Costruisce un Comprensorio con nome e lista di comuni specificati.
     * <p>
     * I comuni duplicati o null vengono automaticamente filtrati.
     *
     * @param nome   il nome del comprensorio
     * @param comuni la collezione iniziale dei comuni (se null, viene creata una
     *               lista vuota)
     */
    public Comprensorio(String nome, Collection<String> comuni) {
        this.nome = nome;
        this.comuni = new ArrayList<>(); // Inizializza una lista vuota

        // Se la collezione di comuni non è null, aggiunge ogni comune valido
        // (non null e non già presente) alla lista interna
        if (comuni != null) {
            for (String comune : comuni) {
                if (comune != null && !hasComune(comune)) {
                    this.comuni.add(comune);
                }
            }
        }
    }

    // -----------------------------------------------------------------------------------
    // GETTER E SETTER
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce il nome del comprensorio.
     *
     * @return il nome del comprensorio
     */
    public String getNome() {
        return this.nome;
    }

    /**
     * Imposta il nome del comprensorio.
     * <p>
     * Attenzione: modificare il nome di un comprensorio già in una collezione
     * potrebbe causare inconsistenze (hashCode cambierà).
     *
     * @param nome il nuovo nome del comprensorio
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Restituisce una copia non modificabile della lista dei comuni.
     * <p>
     * Per modificare la lista, usare i metodi dedicati.
     *
     * @return una lista non modificabile dei comuni
     * @see #addComune(String)
     * @see #removeComune(String)
     */
    public List<String> getComuni() {
        // Restituisce una vista non modificabile della lista interna
        // per proteggere l'incapsulamento
        return Collections.unmodifiableList(this.comuni);
    }

    /**
     * Restituisce il numero di comuni nel comprensorio.
     *
     * @return il numero di comuni
     */
    public int getNumeroComuniCount() {
        return this.comuni.size();
    }

    /**
     * Verifica se il comprensorio non contiene alcun comune.
     *
     * @return true se il comprensorio è vuoto, false altrimenti
     */
    public boolean isEmpty() {
        return this.comuni.isEmpty();
    }

    // -----------------------------------------------------------------------------------
    // METODI DI GESTIONE DEI COMUNI
    // -----------------------------------------------------------------------------------

    /**
     * Aggiunge un comune al comprensorio se non già presente.
     * <p>
     * Il confronto è case-insensitive ("Milano" e "milano" sono considerati
     * uguali).
     *
     * @param comune il nome del comune da aggiungere
     * @return true se il comune è stato aggiunto, false se era null o già presente
     */
    public boolean addComune(String comune) {
        // Verifica se il comune è null o già presente (case-insensitive)
        if (comune == null || hasComune(comune)) {
            return false;
        }
        // Aggiunge il comune alla lista interna
        return this.comuni.add(comune);
    }

    /**
     * Aggiunge tutti i comuni della collezione specificata.
     * <p>
     * Vengono aggiunti solo i comuni non nulli e non già presenti.
     *
     * @param comuni la collezione di comuni da aggiungere
     * @return il numero di comuni effettivamente aggiunti
     */
    public int addComuni(Collection<String> comuni) {
        // Se la collezione è null, non viene aggiunto nessun comune
        if (comuni == null) {
            return 0;
        }

        // Tiene traccia del numero di comuni effettivamente aggiunti
        int aggiunti = 0;

        // Iterazione su tutti i comuni della collezione
        for (String comune : comuni) {
            // addComune gestirà la validazione (non null, non duplicato)
            if (addComune(comune)) {
                aggiunti++;
            }
        }

        return aggiunti;
    }

    /**
     * Verifica se un comune è presente nel comprensorio.
     * <p>
     * Il confronto è case-insensitive.
     *
     * @param comune il nome del comune da verificare
     * @return true se presente, false altrimenti o se null
     */
    public boolean hasComune(String comune) {
        // Se il comune è null, non può essere presente nel comprensorio
        if (comune == null) {
            return false;
        }

        // Itera su tutti i comuni cercando una corrispondenza case-insensitive
        for (String c : this.comuni) {
            if (c.equalsIgnoreCase(comune)) {
                return true;
            }
        }

        // Se non è stata trovata corrispondenza, restituisce false
        return false;
    }

    /**
     * Rimuove un comune dal comprensorio.
     * <p>
     * Il confronto è case-insensitive.
     *
     * @param comune il nome del comune da rimuovere
     * @return true se il comune è stato trovato e rimosso, false altrimenti
     */
    public boolean removeComune(String comune) {
        // Se il comune è null, non può essere rimosso
        if (comune == null) {
            return false;
        }

        // Utilizza un iteratore per rimuovere in modo sicuro durante l'iterazione
        Iterator<String> it = this.comuni.iterator();
        while (it.hasNext()) {
            String c = it.next();
            if (c.equalsIgnoreCase(comune)) {
                it.remove(); // Rimuove il comune corrente dall'iteratore
                return true; // Comune trovato e rimosso
            }
        }

        // Se non è stata trovata corrispondenza, restituisce false
        return false;
    }

    /**
     * Rimuove tutti i comuni dal comprensorio.
     */
    public void clearComuni() {
        this.comuni.clear();
    }

    /**
     * Restituisce un array contenente tutti i comuni del comprensorio.
     *
     * @return un array di stringhe contenente i nomi dei comuni
     */
    public String[] getComuniAsArray() {
        // Converte la lista in un array di stringhe
        return this.comuni.toArray(new String[0]);
    }

    // -----------------------------------------------------------------------------------
    // METODI OVERRIDE DI OBJECT
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce una rappresentazione in stringa del comprensorio.
     * <p>
     * Include il nome e l'elenco dei comuni con formattazione.
     *
     * @return rappresentazione in stringa del comprensorio
     */
    @Override
    public String toString() {
        // Costruisce la rappresentazione in stringa del comprensorio
        StringBuilder sb = new StringBuilder();
        sb.append("COMPRENSORIO: ").append(this.nome).append("\nELENCO COMUNI:\n");

        // Gestisce il caso di comprensorio senza comuni
        if (this.comuni.isEmpty()) {
            sb.append("\t(nessun comune)\n");
        } else {
            // Aggiunge ogni comune con un prefisso grafico per maggiore leggibilità
            for (String c : this.comuni) {
                sb.append("|---").append(c).append("\n");
            }
        }

        return sb.toString();
    }

    /**
     * Confronta questo comprensorio con l'oggetto specificato.
     * <p>
     * Due comprensori sono uguali se hanno lo stesso nome (case-insensitive).
     * I comuni contenuti non influenzano l'uguaglianza.
     *
     * @param obj l'oggetto da confrontare
     * @return true se l'oggetto è un Comprensorio con lo stesso nome, false
     *         altrimenti
     */
    @Override
    public boolean equals(Object obj) {
        // Controllo di identità (stesso riferimento) per efficienza
        if (this == obj) {
            return true;
        }

        // Verifiche base: oggetto non null e della stessa classe
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        // Cast sicuro (verificato sopra) e confronto dei nomi
        Comprensorio comprensorio = (Comprensorio) obj;

        // Se il nome è null, il comprensorio non può essere uguale ad altri
        // Altrimenti confronta i nomi ignorando maiuscole/minuscole
        return nome != null && nome.equalsIgnoreCase(comprensorio.getNome());
    }

    /**
     * Calcola l'hash code per questo comprensorio.
     * <p>
     * Basato sul nome convertito in minuscolo per coerenza con equals().
     *
     * @return hash code del nome in minuscolo, o 0 se nome è null
     */
    @Override
    public int hashCode() {
        // Calcola l'hash code basato sul nome convertito in minuscolo, o 0 se nome è
        // null
        return nome != null ? nome.toLowerCase().hashCode() : 0;
    }
}