package model;

import java.io.*;
import java.util.Objects;

/**
 * Classe che rappresenta un valore possibile per un {@link Campo}.
 * Un ValoreCampo è costituito da un nome obbligatorio e da una descrizione
 * opzionale.
 * Il nome è utilizzato come identificatore univoco del valore all'interno di un
 * campo.
 */
public class ValoreCampo implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String NOME_NON_NULL = "Il nome del valore non può essere null";

    private String nome;
    private String descrizione;

    // -----------------------------------------------------------------------------------
    // COSTRUTTORI
    // -----------------------------------------------------------------------------------

    /**
     * Costruisce un valore del campo con il nome specificato e senza descrizione.
     *
     * @param nome il nome del valore del campo (non deve essere null)
     * @throws NullPointerException se il nome è null
     */
    public ValoreCampo(String nome) {
        this(nome, "");
    }

    /**
     * Costruisce un valore del campo con il nome e la descrizione specificati.
     *
     * @param nome        il nome del valore del campo (non deve essere null)
     * @param descrizione la descrizione del valore del campo (può essere null)
     * @throws NullPointerException se il nome è null
     */
    public ValoreCampo(String nome, String descrizione) {
        this.nome = Objects.requireNonNull(nome, NOME_NON_NULL);
        this.descrizione = descrizione != null ? descrizione : "";
    }

    // -----------------------------------------------------------------------------------
    // GETTER E SETTER
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce il nome del valore del campo.
     *
     * @return il nome del valore del campo (mai null)
     */
    public String getNome() {
        return this.nome;
    }

    /**
     * Imposta il nome del valore del campo.
     *
     * @param nome il nuovo nome del valore (non deve essere null)
     * @throws NullPointerException se il nome è null
     */
    public void setNome(String nome) {
        this.nome = Objects.requireNonNull(nome, NOME_NON_NULL);
    }

    /**
     * Restituisce la descrizione del valore del campo.
     *
     * @return la descrizione del valore del campo (mai null, può essere stringa
     *         vuota)
     */
    public String getDescrizione() {
        return this.descrizione;
    }

    /**
     * Imposta la descrizione del valore del campo.
     *
     * @param descrizione la nuova descrizione (se null, viene impostata come
     *                    stringa vuota)
     */
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione != null ? descrizione : "";
    }

    /**
     * Verifica se questo valore ha una descrizione non vuota.
     *
     * @return true se la descrizione non è vuota, false altrimenti
     */
    public boolean hasDescrizione() {
        return !this.descrizione.isEmpty();
    }

    // -----------------------------------------------------------------------------------
    // OVERRIDE DI OBJECT
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce una rappresentazione testuale del valore del campo.
     * La stringa include il nome del valore e, se presente, la descrizione.
     *
     * @return una rappresentazione in stringa del valore del campo
     */
    @Override
    public String toString() {
        if (descrizione.isEmpty()) {
            return nome;
        } else {
            return nome + " - " + descrizione;
        }
    }

    /**
     * Confronta questo valore del campo con l'oggetto specificato.
     * Due valori di campo sono considerati uguali se hanno lo stesso nome,
     * indipendentemente dalla descrizione.
     *
     * @param obj l'oggetto da confrontare con questo ValoreCampo
     * @return true se l'oggetto è uguale a questo ValoreCampo, false altrimenti
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ValoreCampo valore = (ValoreCampo) obj;
        return nome.equals(valore.nome);
    }

    /**
     * Calcola l'hash code per questo valore del campo.
     * Coerentemente con equals(), l'hash code è basato solo sul nome.
     *
     * @return l'hash code del valore del campo
     */
    @Override
    public int hashCode() {
        return Objects.hash(nome);
    }
}