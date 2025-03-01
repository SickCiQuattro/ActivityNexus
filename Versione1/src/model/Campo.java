package model;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Classe che rappresenta un campo di una categoria.
 * Un campo è costituito da un nome e da un dominio di valori possibili.
 * Ogni valore nel dominio può avere una propria descrizione.
 */
public class Campo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nome;
    private final Set<ValoreCampo> dominio;

    // -----------------------------------------------------------------------------------
    // COSTRUTTORI
    // -----------------------------------------------------------------------------------

    /**
     * Costruisce un Campo con il nome specificato.
     * Inizializza il dominio come un insieme vuoto.
     *
     * @param nome il nome del campo (non deve essere null)
     * @throws NullPointerException se il nome è null
     */
    public Campo(String nome) {
        this(nome, new HashSet<>());
    }

    /**
     * Costruisce un Campo con il nome e il dominio specificati.
     *
     * @param nome    il nome del campo (non deve essere null)
     * @param dominio l'insieme iniziale di valori per il campo
     * @throws NullPointerException se il nome o il dominio sono null
     */
    public Campo(String nome, Set<ValoreCampo> dominio) {
        this.nome = Objects.requireNonNull(nome, "Il nome del campo non può essere null");
        this.dominio = new HashSet<>();

        if (dominio != null) {
            // Aggiungiamo i valori uno alla volta per evitare di inserire null
            dominio.forEach(valore -> {
                if (valore != null) {
                    this.dominio.add(valore);
                }
            });
        }
    }

    // -----------------------------------------------------------------------------------
    // GETTER E SETTER
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce il nome del campo.
     *
     * @return il nome del campo
     */
    public String getNome() {
        return this.nome;
    }

    /**
     * Imposta il nome del campo.
     *
     * @param nome il nuovo nome del campo (non deve essere null)
     * @throws NullPointerException se il nome è null
     */
    public void setNome(String nome) {
        this.nome = Objects.requireNonNull(nome, "Il nome del campo non può essere null");
    }

    /**
     * Restituisce una vista non modificabile del dominio dei valori.
     * Per modificare il dominio, utilizzare i metodi {@code addValore} e
     * {@code removeValore}.
     *
     * @return una vista immutabile dell'insieme dei valori del dominio
     */
    public Set<ValoreCampo> getDominio() {
        return Collections.unmodifiableSet(this.dominio);
    }

    /**
     * Restituisce il numero di valori nel dominio del campo.
     *
     * @return la dimensione del dominio
     */
    public int getDominioDimensione() {
        return this.dominio.size();
    }

    // -----------------------------------------------------------------------------------
    // METODI DI MANIPOLAZIONE DEL DOMINIO
    // -----------------------------------------------------------------------------------

    /**
     * Aggiunge un valore al dominio del campo.
     *
     * @param valore il valore da aggiungere (non deve essere null)
     * @return true se il valore è stato aggiunto correttamente, false se era già
     *         presente
     * @throws NullPointerException se il valore è null
     */
    public boolean addValore(ValoreCampo valore) {
        Objects.requireNonNull(valore, "Il valore non può essere null");
        return this.dominio.add(valore);
    }

    /**
     * Rimuove un valore dal dominio del campo.
     *
     * @param valore il valore da rimuovere
     * @return true se il valore è stato rimosso, false se non era presente
     */
    public boolean removeValore(ValoreCampo valore) {
        return valore != null && this.dominio.remove(valore);
    }

    /**
     * Rimuove un valore dal dominio del campo specificandone il nome.
     *
     * @param nomeValore il nome del valore da rimuovere
     * @return true se il valore è stato trovato e rimosso, false altrimenti
     */
    public boolean removeValore(String nomeValore) {
        if (nomeValore == null) {
            return false;
        }

        return dominio.removeIf(valore -> nomeValore.equals(valore.getNome()));
    }

    /**
     * Controlla se un dato valore è presente nel dominio del campo.
     *
     * @param valore il valore da cercare
     * @return true se il valore è presente, false altrimenti
     */
    public boolean hasValore(ValoreCampo valore) {
        return valore != null && this.dominio.contains(valore);
    }

    /**
     * Controlla se un valore con il nome specificato è presente nel dominio del
     * campo.
     *
     * @param nomeValore il nome del valore da cercare
     * @return true se un valore con quel nome è presente, false altrimenti
     */
    public boolean hasValore(String nomeValore) {
        if (nomeValore == null) {
            return false;
        }

        for (ValoreCampo valore : this.dominio) {
            if (nomeValore.equals(valore.getNome())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Cerca e restituisce un valore nel dominio dato il suo nome.
     *
     * @param nomeValore il nome del valore da cercare
     * @return il ValoreCampo corrispondente, o null se non trovato
     */
    public ValoreCampo getValoreByNome(String nomeValore) {
        if (nomeValore == null) {
            return null;
        }

        for (ValoreCampo valore : this.dominio) {
            if (nomeValore.equals(valore.getNome())) {
                return valore;
            }
        }

        return null;
    }

    /**
     * Verifica se il campo ha almeno un valore nel suo dominio.
     *
     * @return true se il dominio non è vuoto, false altrimenti
     */
    public boolean hasDominio() {
        return !this.dominio.isEmpty();
    }

    // -----------------------------------------------------------------------------------
    // METODI OVERRIDE DI OBJECT
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce una rappresentazione in stringa del campo.
     * La stringa include il nome del campo e la lista dei valori nel dominio.
     *
     * @return una rappresentazione in stringa del campo
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.nome).append(":\n");

        if (dominio.isEmpty()) {
            sb.append("\t(dominio vuoto)\n");
        } else {
            for (ValoreCampo valore : this.dominio) {
                sb.append("\t").append(valore).append("\n");
            }
        }

        return sb.toString();
    }

    /**
     * Confronta questo campo con l'oggetto specificato.
     * Due campi sono considerati uguali se hanno lo stesso nome e lo stesso
     * dominio.
     *
     * @param obj l'oggetto da confrontare con questo campo
     * @return true se l'oggetto è uguale a questo campo, false altrimenti
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Campo other = (Campo) obj;
        return Objects.equals(this.nome, other.nome) &&
                Objects.equals(this.dominio, other.dominio);
    }

    /**
     * Calcola l'hash code per questo campo.
     *
     * @return l'hash code del campo
     */
    @Override
    public int hashCode() {
        return Objects.hash(nome, dominio);
    }
}