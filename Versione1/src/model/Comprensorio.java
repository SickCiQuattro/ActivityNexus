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
 * <p><b>Invarianti:</b>
 * <ul>
 *   <li>La lista dei comuni non è mai null (può essere vuota)</li>
 *   <li>La lista dei comuni non contiene mai valori null</li>
 *   <li>La lista dei comuni non contiene mai duplicati (case-insensitive)</li>
 * </ul>
 *
 * @see java.io.Serializable
 */
public class Comprensorio implements Serializable {
    // Identificatore di versione per la serializzazione
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
     *
     * @post getNome() restituisce il nome fornito
     * @post getComuni() restituisce una lista vuota
     */
    public Comprensorio(String nome) {
        this.nome = nome; // Accetta anche null, anche se non consigliato
        this.comuni = new ArrayList<>(); // Inizializza una lista vuota
    }

    // -----------------------------------------------------------------------------------
    // GETTER E SETTER
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce il nome del comprensorio.
     *
     * @return il nome del comprensorio
     *
     * @post il valore restituito è uguale al nome assegnato al comprensorio
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
     *
     * @post getNome() restituisce il valore fornito
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
     *
     * @post il valore restituito non è null
     * @post il valore restituito non contiene elementi null
     * @post il valore restituito non contiene duplicati (case-insensitive)
     * @post il valore restituito è una vista non modificabile della lista interna
     */
    public List<String> getComuni() {
        // Restituisce una vista non modificabile della lista interna
        // per proteggere l'incapsulamento
        return Collections.unmodifiableList(this.comuni);
    }

    /**
     * Verifica se il comprensorio non contiene alcun comune.
     *
     * @return true se il comprensorio è vuoto, false altrimenti
     *
     * @post il valore restituito è true se e solo se getNumeroComuniCount() == 0
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
     *
     * @post se restituisce true, hasComune(comune) == true
     * @post se restituisce true, getNumeroComuniCount() è aumentato di 1
     * @post se comune == null o hasComune(comune) == true, restituisce false e lo stato non è modificato
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
     * Verifica se un comune è presente nel comprensorio.
     * <p>
     * Il confronto è case-insensitive.
     *
     * @param comune il nome del comune da verificare
     * @return true se presente, false altrimenti o se null
     *
     * @post se comune == null, restituisce false
     * @post il valore restituito riflette correttamente la presenza o assenza del comune nel comprensorio
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

    // -----------------------------------------------------------------------------------
    // METODI OVERRIDE DI OBJECT
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce una rappresentazione in stringa del comprensorio.
     * <p>
     * Include il nome e l'elenco dei comuni con formattazione.
     *
     * @return rappresentazione in stringa del comprensorio
     *
     * @post il valore restituito non è null
     * @post il valore restituito contiene il nome del comprensorio
     * @post il valore restituito contiene una rappresentazione di tutti i comuni
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
     *
     * @post se this e obj sono lo stesso oggetto (riferimento identico), restituisce true
     * @post se obj è null o non è un'istanza di Comprensorio, restituisce false
     * @post se il nome di questo comprensorio è null, restituisce false
     * @post se obj è un Comprensorio e entrambi i nomi non sono null, restituisce true se e solo se i nomi sono uguali ignorando case
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
     *
     * @post se this.nome == null, restituisce 0
     * @post se due comprensori sono uguali secondo equals(), hanno lo stesso hashCode()
     */
    @Override
    public int hashCode() {
        // Calcola l'hash code basato sul nome convertito in minuscolo, o 0 se nome è
        // null
        return nome != null ? nome.toLowerCase().hashCode() : 0;
    }
}