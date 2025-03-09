package model;

import java.io.*;
import java.util.Objects;

/**
 * Rappresenta un valore possibile per un {@link Campo} nel sistema di
 * categorizzazione.
 * <p>
 * Un ValoreCampo è costituito da:
 * <ul>
 * <li><b>Nome</b>: identificatore univoco obbligatorio</li>
 * <li><b>Descrizione</b>: informazione opzionale con dettagli aggiuntivi</li>
 * </ul>
 * <p>
 * Implementa {@link Serializable} per la persistenza dei dati.
 *
 * <p><b>Invarianti:</b>
 * <ul>
 *   <li>Il nome non è mai null</li>
 *   <li>La descrizione non è mai null (può essere una stringa vuota)</li>
 *   <li>Due ValoreCampo sono considerati uguali se hanno lo stesso nome, indipendentemente dalla descrizione</li>
 * </ul>
 *
 * @see Campo
 * @see Categoria
 */
public class ValoreCampo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // Messaggio di errore costante
    public static final String NOME_NON_NULL = "Il nome del valore non può essere null";

    /** Nome univoco che identifica questo valore all'interno di un campo */
    private String nome;

    /** Descrizione opzionale con informazioni aggiuntive sul valore */
    private String descrizione;

    // -----------------------------------------------------------------------------------
    // COSTRUTTORI
    // -----------------------------------------------------------------------------------

    /**
     * Costruisce un valore del campo con il nome specificato e senza descrizione.
     *
     * @param nome il nome del valore del campo (non deve essere null)
     * @throws NullPointerException se il nome è null
     *
     * @pre nome != null
     * @post getNome() restituisce un valore uguale a nome
     * @post getDescrizione() restituisce una stringa vuota
     */
    public ValoreCampo(String nome) {
        // Delega al costruttore completo con descrizione vuota
        this(nome, "");
    }

    /**
     * Costruisce un valore del campo con il nome e la descrizione specificati.
     *
     * @param nome        il nome del valore del campo (non deve essere null)
     * @param descrizione la descrizione del valore (se null, viene trattata come
     *                    stringa vuota)
     * @throws NullPointerException se il nome è null
     *
     * @pre nome != null
     * @post getNome() restituisce un valore uguale a nome
     * @post getDescrizione() restituisce descrizione se non null, altrimenti stringa vuota
     */
    public ValoreCampo(String nome, String descrizione) {
        // Verifica che il nome non sia null, lanciando un'eccezione in caso contrario
        this.nome = Objects.requireNonNull(nome, NOME_NON_NULL);

        // Gestisce il caso in cui la descrizione sia null, convertendola in stringa
        // vuota
        // Questo garantisce che getDescrizione() non restituisca mai null
        this.descrizione = descrizione != null ? descrizione : "";
    }

    // -----------------------------------------------------------------------------------
    // GETTER E SETTER
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce il nome del valore del campo.
     *
     * @return il nome del valore del campo (mai null)
     *
     * @post il valore restituito non è null
     * @post il valore restituito corrisponde al nome attualmente impostato
     */
    public String getNome() {
        return this.nome;
    }

    /**
     * Imposta il nome del valore del campo.
     * <p>
     * Da usare con cautela se il valore fa già parte di un Campo o struttura dati
     * ordinata.
     *
     * @param nome il nuovo nome del valore (non deve essere null)
     * @throws NullPointerException se il nome è null
     *
     * @pre nome != null
     * @post getNome() restituisce un valore uguale al nome fornito
     */
    public void setNome(String nome) {
        // Verifica che il nuovo nome non sia null
        this.nome = Objects.requireNonNull(nome, NOME_NON_NULL);
    }

    /**
     * Restituisce la descrizione del valore del campo.
     *
     * @return la descrizione (mai null, può essere stringa vuota)
     *
     * @post il valore restituito non è null
     * @post il valore restituito corrisponde alla descrizione attualmente impostata
     */
    public String getDescrizione() {
        return this.descrizione;
    }

    /**
     * Imposta la descrizione del valore del campo.
     *
     * @param descrizione la nuova descrizione (se null, viene impostata come
     *                    stringa vuota)
     *
     * @post getDescrizione() restituisce descrizione se non null, altrimenti stringa vuota
     * @post getDescrizione() non restituisce mai null
     */
    public void setDescrizione(String descrizione) {
        // Gestisce il caso in cui la nuova descrizione sia null, convertendola in
        // stringa vuota
        this.descrizione = descrizione != null ? descrizione : "";
    }


    // -----------------------------------------------------------------------------------
    // OVERRIDE DI OBJECT
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce una rappresentazione testuale del valore del campo.
     * <p>
     * Formato: "nome" o "nome - descrizione" se la descrizione è presente.
     *
     * @return una rappresentazione in stringa del valore
     *
     * @post il valore restituito non è null
     * @post se la descrizione è vuota, restituisce solo il nome
     * @post se la descrizione non è vuota, restituisce "nome - descrizione"
     */
    @Override
    public String toString() {
        // Se la descrizione è vuota, restituisce solo il nome
        if (descrizione.isEmpty()) {
            return nome;
        } else {
            // Altrimenti combina nome e descrizione con un separatore
            return nome + " - " + descrizione;
        }
    }

    /**
     * Confronta questo valore del campo con l'oggetto specificato.
     * <p>
     * Due valori sono considerati uguali se hanno lo stesso nome,
     * indipendentemente dalla descrizione.
     *
     * @param obj l'oggetto da confrontare
     * @return true se l'oggetto è un ValoreCampo con lo stesso nome
     *
     * @post se obj == this, restituisce true
     * @post se obj == null o obj non è di tipo ValoreCampo, restituisce false
     * @post restituisce true se e solo se obj è un ValoreCampo con lo stesso nome
     */
    @Override
    public boolean equals(Object obj) {
        // Confronto di identità (ottimizzazione)
        if (this == obj) {
            return true;
        }

        // Verifica che l'oggetto non sia null e sia della stessa classe
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        // Cast sicuro dopo la verifica del tipo
        ValoreCampo valore = (ValoreCampo) obj;

        // I valori sono uguali se hanno lo stesso nome (indipendentemente dalla
        // descrizione)
        return nome.equals(valore.nome);
    }

    /**
     * Calcola l'hash code basato solo sul nome del valore.
     * <p>
     * Coerente con {@link #equals(Object)} per l'uso nelle collezioni hash.
     *
     * @return l'hash code basato sul nome
     *
     * @post se due ValoreCampo sono uguali secondo equals(), hanno lo stesso hashCode()
     */
    @Override
    public int hashCode() {
        // L'hash code è basato solo sul nome
        return Objects.hash(nome);
    }
}