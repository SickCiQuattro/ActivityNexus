package model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Rappresenta un valore possibile per un {@link Campo} nel sistema di
 * categorizzazione.
 * <p>
 * Un ValoreCampo è costituito da:
 * <ul>
 * <li><b>Nome</b>: identificatore univoco obbligatorio (ad es. "Basso",
 * "Medio", "Alto")</li>
 * <li><b>Descrizione</b>: informazione opzionale con dettagli aggiuntivi</li>
 * </ul>
 * <p>
 * I valori di campo sono utilizzati per popolare i campi di una categoria e
 * definiscono
 * le opzioni valide tra cui gli utenti possono scegliere. Ad esempio, per un
 * campo "Livello"
 * potrebbero esistere i valori "Principiante", "Intermedio" e "Avanzato".
 * <p>
 * Questa classe implementa {@link Serializable} per consentire la persistenza
 * dei dati
 * quando necessario salvare o trasmettere categorie e le loro proprietà.
 *
 * <p>
 * <b>Invarianti della classe:</b>
 * <ul>
 * <li>Il nome non è mai null</li>
 * <li>La descrizione non è mai null (può essere una stringa vuota)</li>
 * <li>Due ValoreCampo sono considerati uguali se hanno lo stesso nome,
 * indipendentemente dalla descrizione</li>
 * </ul>
 *
 * @see Campo
 * @see Categoria
 */
public class ValoreCampo implements Serializable {
    // -----------------------------------------------------------------------------------
    // FIELDS E COSTANTI
    // -----------------------------------------------------------------------------------

    /** Identificatore di versione per la serializzazione */
    @Serial
    private static final long serialVersionUID = 1L;

    /** Messaggio di errore per nome null */
    public static final String NOME_NON_NULL = "Il nome del valore non può essere null";

    /** Nome univoco che identifica questo valore all'interno di un campo */
    private String nome;

    /**
     * Descrizione opzionale con informazioni aggiuntive sul valore.
     * Può essere una stringa vuota ma mai null.
     */
    private final String descrizione;

    // -----------------------------------------------------------------------------------
    // COSTRUTTORI
    // -----------------------------------------------------------------------------------

    /**
     * Costruisce un valore del campo con il nome specificato e senza descrizione.
     * <p>
     * Questo costruttore è comodo quando non è necessaria una descrizione
     * aggiuntiva
     * per il valore del campo.
     *
     * @param nome il nome del valore del campo (non deve essere null)
     * @throws NullPointerException se il nome è null
     *
     * @pre nome != null
     * @post getNome().equals(nome)
     * @post getDescrizione().isEmpty() == true
     */
    public ValoreCampo(String nome) {
        // Delega al costruttore completo con descrizione vuota
        this(nome, "");
    }

    /**
     * Costruisce un valore del campo con il nome e la descrizione specificati.
     * <p>
     * Questo è il costruttore principale che inizializza completamente l'oggetto.
     *
     * @param nome        il nome del valore del campo (non deve essere null)
     * @param descrizione la descrizione del valore (se null, viene trattata come
     *                    stringa vuota)
     * @throws NullPointerException se il nome è null
     *
     * @pre nome != null
     * @post getNome().equals(nome)
     * @post getDescrizione().equals(descrizione != null ? descrizione : "")
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
    // GETTERS E SETTERS
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce il nome del valore del campo.
     * <p>
     * Il nome è l'identificatore univoco di questo valore all'interno del campo.
     *
     * @return il nome del valore del campo (mai null)
     *
     * @post result != null
     */
    public String getNome() {
        return this.nome;
    }

    /**
     * Imposta il nome del valore del campo.
     * <p>
     * Da usare con cautela se il valore fa già parte di un Campo o struttura dati
     * ordinata, poiché potrebbe alterare l'ordinamento o l'unicità.
     *
     * @param nome il nuovo nome del valore (non deve essere null)
     * @throws NullPointerException se il nome è null
     *
     * @pre nome != null
     * @post getNome().equals(nome)
     */
    public void setNome(String nome) {
        // Verifica che il nuovo nome non sia null
        this.nome = Objects.requireNonNull(nome, NOME_NON_NULL);
    }

    /**
     * Restituisce la descrizione del valore del campo.
     * <p>
     * La descrizione fornisce dettagli aggiuntivi sul significato di questo valore.
     *
     * @return la descrizione (mai null, può essere stringa vuota)
     *
     * @post result != null
     */
    public String getDescrizione() {
        return this.descrizione;
    }

    // -----------------------------------------------------------------------------------
    // METODI DI UTILITÀ
    // -----------------------------------------------------------------------------------

    // -----------------------------------------------------------------------------------
    // OVERRIDE DI OBJECT
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce una rappresentazione testuale del valore del campo.
     * <p>
     * Formato: "nome" o "nome - descrizione" se la descrizione è presente.
     *
     * @return una rappresentazione in stringa del valore (mai null)
     *
     * @post result != null
     * @post se descrizione.isEmpty(), result.equals(nome)
     * @post se !descrizione.isEmpty(), result.equals(nome + " - " + descrizione)
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
     * indipendentemente dalla descrizione. Questo permette di utilizzare
     * i ValoreCampo come chiavi in mappe e set dove l'identificazione avviene
     * per nome.
     *
     * @param obj l'oggetto da confrontare
     * @return true se l'oggetto è un ValoreCampo con lo stesso nome
     *
     * @pre true (obj può essere null)
     * @post result == true se e solo se obj è un ValoreCampo con lo stesso nome
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
     * Poiché l'uguaglianza è determinata solo dal nome, l'hash code è calcolato
     * solo in base ad esso.
     *
     * @return l'hash code basato sul nome
     *
     * @post se due ValoreCampo hanno lo stesso nome, restituiranno lo stesso
     *       hashCode
     */
    @Override
    public int hashCode() {
        // L'hash code è basato solo sul nome per coerenza con equals()
        return Objects.hash(nome);
    }
}