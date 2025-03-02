package model;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Campo caratteristico di una categoria nel sistema gerarchico.
 * <p>
 * Rappresenta una proprietà di una categoria che può assumere valori da un
 * dominio
 * predefinito. Ogni valore nel dominio, rappresentato da un
 * {@link ValoreCampo},
 * definisce una possibile specializzazione della categoria.
 * <p>
 * All'interno della struttura gerarchica:
 * <ul>
 * <li>Le categorie non-foglia hanno un campo che determina le possibili
 * sottocategorie</li>
 * <li>Le categorie foglia (attività concrete) non hanno campi
 * caratteristici</li>
 * </ul>
 *
 * @see ValoreCampo
 * @see Categoria
 * @see Gerarchia
 */
public class Campo implements Serializable {
    // Identificatore di versione per la serializzazione
    private static final long serialVersionUID = 1L;

    // Nome del campo (es. "Disciplina", "Area geografica")
    private String nome;

    // Insieme di valori che costituiscono il dominio del campo
    private final Set<ValoreCampo> dominio;

    // -----------------------------------------------------------------------------------
    // COSTRUTTORI
    // -----------------------------------------------------------------------------------

    /**
     * Costruisce un Campo con il nome specificato e dominio vuoto.
     *
     * @param nome il nome del campo (non deve essere null)
     * @throws NullPointerException se il nome è null
     * 
     * @see #addValore(ValoreCampo)
     */
    public Campo(String nome) {
        // Delega al costruttore più completo con un HashSet vuoto come dominio iniziale
        this(nome, new HashSet<>());
    }

    /**
     * Costruisce un Campo con il nome e il dominio specificati.
     *
     * @param nome    il nome del campo (non deve essere null)
     * @param dominio l'insieme iniziale di valori per il campo
     * @throws NullPointerException se il nome è null
     */
    public Campo(String nome, Set<ValoreCampo> dominio) {
        // Verifica che il nome non sia null e lancia eccezione in caso contrario
        this.nome = Objects.requireNonNull(nome, "Il nome del campo non può essere null");

        // Inizializza un nuovo insieme per memorizzare i valori del dominio
        this.dominio = new HashSet<>();

        // Se il dominio fornito non è null, copia i valori non-null nel dominio interno
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
     * @return il nome del campo, mai null
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
        // Verifica che il nuovo nome non sia null prima di assegnarlo
        this.nome = Objects.requireNonNull(nome, "Il nome del campo non può essere null");
    }

    /**
     * Restituisce una vista non modificabile del dominio dei valori.
     *
     * @return una vista immutabile dell'insieme dei valori del dominio
     * @see #addValore(ValoreCampo)
     * @see #removeValore(ValoreCampo)
     */
    public Set<ValoreCampo> getDominio() {
        // Restituisce una vista immutabile per proteggere l'incapsulamento
        return Collections.unmodifiableSet(this.dominio);
    }

    /**
     * Restituisce il numero di valori nel dominio del campo.
     *
     * @return la dimensione del dominio, 0 se vuoto
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
     * @return true se il valore è stato aggiunto, false se era già presente
     * @throws NullPointerException se il valore è null
     */
    public boolean addValore(ValoreCampo valore) {
        // Verifica che il valore non sia null e lancia eccezione in caso contrario
        Objects.requireNonNull(valore, "Il valore non può essere null");

        // Tenta di aggiungere il valore al dominio
        return this.dominio.add(valore);
    }

    /**
     * Rimuove un valore dal dominio del campo.
     *
     * @param valore il valore da rimuovere
     * @return true se il valore è stato rimosso, false se non era presente o era
     *         null
     */
    public boolean removeValore(ValoreCampo valore) {
        // Se il valore è null, non è possibile rimuoverlo
        return valore != null && this.dominio.remove(valore);
    }

    /**
     * Rimuove un valore dal dominio specificandone il nome.
     *
     * @param nomeValore il nome del valore da rimuovere
     * @return true se il valore è stato trovato e rimosso, false altrimenti
     */
    public boolean removeValore(String nomeValore) {
        // Se il nome è null, non è possibile identificare un valore da rimuovere
        if (nomeValore == null) {
            return false;
        }

        // Utilizza removeIf con un predicato per rimuovere il valore con il nome
        // specificato
        return dominio.removeIf(valore -> nomeValore.equals(valore.getNome()));
    }

    /**
     * Verifica se un valore è presente nel dominio del campo.
     *
     * @param valore il valore da cercare
     * @return true se il valore è presente, false altrimenti o se null
     */
    public boolean hasValore(ValoreCampo valore) {
        // Se il valore è null, non può essere presente nel dominio
        return valore != null && this.dominio.contains(valore);
    }

    /**
     * Verifica se un valore con il nome specificato è presente nel dominio.
     *
     * @param nomeValore il nome del valore da cercare
     * @return true se un valore con quel nome è presente, false altrimenti
     */
    public boolean hasValore(String nomeValore) {
        // Se il nome è null, non è possibile identificare un valore da cercare
        if (nomeValore == null) {
            return false;
        }

        // Itera su tutti i valori per trovare uno con il nome specificato
        for (ValoreCampo valore : this.dominio) {
            if (nomeValore.equals(valore.getNome())) {
                return true;
            }
        }

        // Nessun valore con il nome specificato trovato
        return false;
    }

    /**
     * Cerca e restituisce un valore nel dominio dato il suo nome.
     *
     * @param nomeValore il nome del valore da cercare
     * @return il ValoreCampo corrispondente, o null se non trovato o se il nome è
     *         null
     */
    public ValoreCampo getValoreByNome(String nomeValore) {
        // Se il nome è null, non è possibile identificare un valore da cercare
        if (nomeValore == null) {
            return null;
        }

        // Itera su tutti i valori per trovare uno con il nome specificato
        for (ValoreCampo valore : this.dominio) {
            if (nomeValore.equals(valore.getNome())) {
                return valore;
            }
        }

        // Nessun valore con il nome specificato trovato
        return null;
    }

    /**
     * Verifica se il campo ha almeno un valore nel suo dominio.
     *
     * @return true se il dominio contiene almeno un valore, false se è vuoto
     */
    public boolean hasDominio() {
        return !this.dominio.isEmpty();
    }

    // -----------------------------------------------------------------------------------
    // METODI OVERRIDE DI OBJECT
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce una rappresentazione in stringa del campo.
     * Include il nome del campo seguito dalla lista dei valori nel dominio.
     *
     * @return una rappresentazione in stringa del campo formattata su più righe
     */
    @Override
    public String toString() {
        // Costruisce una stringa che rappresenta il campo e i suoi valori
        StringBuilder sb = new StringBuilder();
        sb.append(this.nome).append(":\n");

        // Gestisce il caso di dominio vuoto
        if (dominio.isEmpty()) {
            sb.append("\t(dominio vuoto)\n");
        } else {
            // Aggiunge ogni valore indentato su una nuova riga
            for (ValoreCampo valore : this.dominio) {
                sb.append("\t").append(valore).append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * Confronta questo campo con l'oggetto specificato.
     * Due campi sono uguali se hanno lo stesso nome e dominio.
     *
     * @param obj l'oggetto da confrontare con questo campo
     * @return true se l'oggetto è un Campo uguale a questo, false altrimenti
     */
    @Override
    public boolean equals(Object obj) {
        // Controllo di identità per efficienza
        if (this == obj) {
            return true;
        }

        // Controlli di base (null e tipo corretto)
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        // Cast sicuro (verificato sopra) e confronto dei campi
        Campo other = (Campo) obj;
        return Objects.equals(this.nome, other.nome) &&
                Objects.equals(this.dominio, other.dominio);
    }

    /**
     * Calcola l'hash code del campo basato su nome e dominio.
     *
     * @return l'hash code del campo
     */
    @Override
    public int hashCode() {
        // Calcola l'hash basato su nome e dominio
        return Objects.hash(nome, dominio);
    }
}