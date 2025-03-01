package model;

import java.io.*;
import java.util.Objects;

/**
 * Classe che rappresenta una categoria di una gerarchia.
 * Una categoria è costituita da un nome e può avere un campo associato.
 * Il campo associato determina i valori possibili per le sottocategorie.
 */
public class Categoria implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nome;
    private Campo campo = null;

    // -----------------------------------------------------------------------------------
    // COSTRUTTORI
    // -----------------------------------------------------------------------------------

    /**
     * Costruisce una Categoria con il nome specificato e senza campo associato.
     *
     * @param nome il nome della categoria (non deve essere null)
     * @throws NullPointerException se il nome è null
     */
    public Categoria(String nome) {
        this.nome = Objects.requireNonNull(nome, "Il nome della categoria non può essere null");
    }

    /**
     * Costruisce una Categoria con il nome e il campo specificati.
     *
     * @param nome  il nome della categoria (non deve essere null)
     * @param campo il campo associato alla categoria (può essere null)
     * @throws NullPointerException se il nome è null
     */
    public Categoria(String nome, Campo campo) {
        this.nome = Objects.requireNonNull(nome, "Il nome della categoria non può essere null");
        this.campo = campo;
    }

    // -----------------------------------------------------------------------------------
    // GETTER E SETTER
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce il nome della categoria.
     *
     * @return il nome della categoria (mai null)
     */
    public String getNome() {
        return this.nome;
    }

    /**
     * Imposta il nome della categoria.
     *
     * @param nome il nuovo nome della categoria (non deve essere null)
     * @throws NullPointerException se il nome è null
     */
    public void setNome(String nome) {
        this.nome = Objects.requireNonNull(nome, "Il nome della categoria non può essere null");
    }

    /**
     * Restituisce il campo associato alla categoria.
     *
     * @return il campo associato alla categoria, o null se non è stato definito
     */
    public Campo getCampo() {
        return this.campo;
    }

    /**
     * Imposta il campo associato alla categoria.
     *
     * @param campo il nuovo campo associato alla categoria (può essere null)
     */
    public void setCampo(Campo campo) {
        this.campo = campo;
    }

    /**
     * Verifica se la categoria ha un campo associato.
     *
     * @return true se la categoria ha un campo associato, false altrimenti
     */
    public boolean hasCampo() {
        return this.campo != null;
    }

    // -----------------------------------------------------------------------------------
    // METODI OVERRIDE DI OBJECT
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce una rappresentazione in stringa della categoria.
     * La stringa include il nome della categoria e, se presente, il campo
     * associato.
     *
     * @return una rappresentazione in stringa della categoria
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Nome: ").append(this.nome).append("\n");

        if (hasCampo()) {
            sb.append("Campo: ").append(this.campo).append("\n");
        }

        return sb.toString();
    }

    /**
     * Confronta questa categoria con l'oggetto specificato.
     * Due categorie sono considerate uguali se hanno lo stesso nome, ignorando la
     * differenza tra maiuscole e minuscole.
     * Il campo non viene considerato nel confronto.
     *
     * @param obj l'oggetto da confrontare con questa Categoria
     * @return true se l'oggetto è uguale a questa Categoria, false altrimenti
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Categoria categoria = (Categoria) obj;
        return nome.equalsIgnoreCase(categoria.getNome());
    }

    /**
     * Calcola l'hash code per questa categoria.
     * Coerentemente con equals(), l'hash code è basato solo sul nome della
     * categoria,
     * convertito in minuscolo per ignorare differenze tra maiuscole e minuscole.
     *
     * @return l'hash code della categoria
     */
    @Override
    public int hashCode() {
        return nome.toLowerCase().hashCode();
    }
}