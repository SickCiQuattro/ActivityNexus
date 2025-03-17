package model;

import java.io.*;
import java.util.*;

/**
 * Rappresenta un comprensorio geografico che raggruppa più comuni.
 * <p>
 * Un comprensorio è un'entità territoriale che identifica un'area geografica
 * composta
 * da più comuni, utilizzata nel sistema per organizzare e classificare le
 * attività
 * di scambio in base alla collocazione geografica.
 * <p>
 * <b>Caratteristiche principali:</b>
 * <ul>
 * <li>Identificato da un nome univoco (case-insensitive)</li>
 * <li>Contiene una lista ordinata di comuni appartenenti al comprensorio</li>
 * <li>Implementa meccanismi per prevenire duplicazioni di comuni</li>
 * <li>Supporta la serializzazione per persistenza dei dati</li>
 * </ul>
 *
 * <p>
 * <b>Invarianti della classe:</b>
 * <ul>
 * <li>Il nome del comprensorio può essere null, ma è sconsigliato</li>
 * <li>La lista dei comuni non è mai null (può essere vuota)</li>
 * <li>La lista dei comuni non contiene mai valori null</li>
 * <li>La lista dei comuni non contiene mai duplicati (case-insensitive)</li>
 * <li>Due comprensori sono considerati uguali se hanno lo stesso nome
 * (case-insensitive)</li>
 * </ul>
 *
 * @see java.io.Serializable
 */
public class Comprensorio implements Serializable {
    // -----------------------------------------------------------------------------------
    // FIELDS E COSTANTI
    // -----------------------------------------------------------------------------------

    /** Versione per la serializzazione */
    @Serial
    private static final long serialVersionUID = 1L;

    /** Nome del comprensorio, identificativo univoco */
    private String nome;

    /** Lista dei comuni appartenenti al comprensorio */
    private final List<String> comuni;

    // -----------------------------------------------------------------------------------
    // COSTRUTTORI
    // -----------------------------------------------------------------------------------

    /**
     * Costruisce un Comprensorio con il nome specificato e lista vuota di comuni.
     *
     * @param nome il nome del comprensorio
     *
     * @pre true (nessuna precondizione particolare)
     * @post getNome() == nome
     * @post getComuni().isEmpty() == true
     */
    public Comprensorio(String nome) {
        // Accetta anche nome null, anche se sconsigliato per la logica applicativa
        this.nome = nome;

        // Inizializza una lista vuota di comuni
        this.comuni = new ArrayList<>();
    }

    // -----------------------------------------------------------------------------------
    // GETTER E SETTER
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce il nome del comprensorio.
     *
     * @return il nome del comprensorio (può essere null)
     *
     * @post result == nome
     */
    public String getNome() {
        return this.nome;
    }

    /**
     * Imposta il nome del comprensorio.
     * <p>
     * <b>Attenzione:</b> Modificare il nome di un comprensorio già in una
     * collezione
     * potrebbe causare inconsistenze nell'hashCode e nell'uguaglianza.
     *
     * @param nome il nuovo nome del comprensorio (può essere null)
     *
     * @post getNome() == nome
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Restituisce una copia non modificabile della lista dei comuni.
     * <p>
     * Per modificare la lista, usare i metodi dedicati come
     * {@link #addComune(String)}.
     *
     * @return una lista non modificabile dei comuni
     *
     * @post result != null
     * @post !result.contains(null)
     * @post result non è modificabile direttamente
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
     * @post result == getComuni().isEmpty()
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
     * @post se comune == null, result == false
     * @post se hasComune(comune) era true prima dell'invocazione, result == false e
     *       lo stato non è cambiato
     * @post se result == true, hasComune(comune) == true e getNumeroComuniCount() è
     *       aumentato di 1
     */
    public boolean addComune(String comune) {
        // Verifica che il comune non sia null e non sia già presente (case-insensitive)
        if (comune == null || hasComune(comune)) {
            return false;
        }

        // Aggiunge il comune alla lista interna e restituisce il risultato
        // dell'operazione
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
     * @post se comune == null, result == false
     * @post se comune != null, result == true se e solo se esiste un comune c in
     *       getComuni() tale che c.equalsIgnoreCase(comune)
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
     * Include il nome e l'elenco dei comuni con formattazione gerarchica.
     *
     * @return rappresentazione testuale del comprensorio
     *
     * @post result != null
     * @post result contiene il nome del comprensorio
     * @post result contiene la rappresentazione di tutti i comuni
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
     * @post se this == obj, result == true
     * @post se obj == null o obj non è un Comprensorio, result == false
     * @post se getNome() == null, result == false
     * @post se obj è un Comprensorio con nome non null, result == true se e solo se
     *       getNome().equalsIgnoreCase(((Comprensorio)obj).getNome())
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
     * @return hash code del comprensorio
     *
     * @post se getNome() == null, result == 0
     * @post se equals(altro) == true, allora hashCode() == altro.hashCode()
     */
    @Override
    public int hashCode() {
        // Calcola l'hash code basato sul nome convertito in minuscolo, o 0 se il nome è
        // null
        return nome != null ? nome.toLowerCase().hashCode() : 0;
    }
}