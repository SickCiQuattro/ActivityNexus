package model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Campo caratteristico di una categoria nel sistema gerarchico.
 * <p>
 * Un campo rappresenta una proprietà distintiva che caratterizza una categoria
 * e
 * determina le sue possibili specializzazioni. Il dominio del campo (lista di
 * {@link ValoreCampo}) definisce i valori che la proprietà può assumere,
 * ciascuno dei quali corrisponde a una potenziale sottocategoria.
 * <p>
 * <b>Ruolo nella struttura gerarchica:</b>
 * <ul>
 * <li>Le categorie <b>non-foglia</b> hanno sempre un campo che determina le
 * possibili sottocategorie</li>
 * <li>Le categorie <b>foglia</b> (attività concrete) non hanno campi
 * caratteristici</li>
 * </ul>
 *
 * <p>
 * <b>Invarianti della classe:</b>
 * <ul>
 * <li>Il nome del campo non è mai null</li>
 * <li>Il dominio non contiene mai valori null</li>
 * <li>La lista dominio non è mai null (può essere vuota)</li>
 * </ul>
 *
 * @see ValoreCampo
 * @see Categoria
 * @see Gerarchia
 */
public class Campo implements Serializable {
    // -----------------------------------------------------------------------------------
    // FIELDS E COSTANTI
    // -----------------------------------------------------------------------------------
    @Serial
    private static final long serialVersionUID = 1L;

    /** Messaggi di errore per validazione */
    private static final String ERRORE_NOME_NULLO = "Il nome del campo non può essere null";
    private static final String ERRORE_VALORE_NULLO = "Il valore non può essere null";
    private static final String ERRORE_VALORE_DUPLICATO = "Il valore è già presente nel dominio";

    /** Nome identificativo del campo */
    private String nome;

    /** Lista dei valori possibili per questo campo, nell'ordine di inserimento */
    private final List<ValoreCampo> dominio;

    // -----------------------------------------------------------------------------------
    // COSTRUTTORI
    // -----------------------------------------------------------------------------------

    /**
     * Costruisce un Campo con il nome specificato e dominio vuoto.
     *
     * @param nome il nome del campo
     * @throws NullPointerException se il nome è null
     *
     * @pre nome != null
     * @post getNome() restituisce il nome fornito
     * @post getDominio() restituisce una lista vuota
     */
    public Campo(String nome) {
        // Delega al costruttore più completo con una ArrayList vuota come dominio
        // iniziale
        this(nome, new ArrayList<>());
    }

    /**
     * Costruisce un Campo con il nome e il dominio specificati.
     *
     * @param nome    il nome del campo
     * @param dominio la lista iniziale di valori per il campo
     * @throws NullPointerException se il nome è null
     *
     * @pre nome != null
     * @post getNome() restituisce il nome fornito
     * @post getDominio() contiene tutti i valori non-null di dominio nell'ordine
     *       originale
     */
    public Campo(String nome, List<ValoreCampo> dominio) {
        // Verifica che il nome non sia null e lancia eccezione in caso contrario
        this.nome = Objects.requireNonNull(nome, ERRORE_NOME_NULLO);

        // Inizializza una nuova lista per memorizzare i valori del dominio
        this.dominio = new ArrayList<>();

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
     *
     * @post il valore restituito non è null
     */
    public String getNome() {
        return this.nome;
    }

    /**
     * Imposta il nome del campo.
     *
     * @param nome il nuovo nome del campo
     * @throws NullPointerException se il nome è null
     *
     * @pre nome != null
     * @post getNome() restituisce il valore fornito
     */
    public void setNome(String nome) {
        // Verifica che il nuovo nome non sia null prima di assegnarlo
        this.nome = Objects.requireNonNull(nome, ERRORE_NOME_NULLO);
    }

    /**
     * Restituisce una vista non modificabile del dominio dei valori.
     * L'ordine degli elementi nella lista restituita corrisponde all'ordine di
     * inserimento.
     *
     * @return una vista immutabile della lista dei valori del dominio
     *
     * @post il valore restituito non è null
     * @post il valore restituito è una lista non modificabile
     */
    public List<ValoreCampo> getDominio() {
        // Restituisce una vista immutabile per proteggere l'incapsulamento
        return Collections.unmodifiableList(this.dominio);
    }

    // -----------------------------------------------------------------------------------
    // METODI DI MANIPOLAZIONE DEL DOMINIO
    // -----------------------------------------------------------------------------------

    /**
     * Aggiunge un valore al dominio del campo.
     * Il valore viene aggiunto alla fine della lista, preservando l'ordine di
     * inserimento.
     *
     * @param valore il valore da aggiungere
     * @throws NullPointerException se il valore è null
     *
     * @pre valore != null
     * @post getDominio().contains(valore) == true
     * @post getDominio().get(getDominio().size()-1) == valore
     */
    public void addValore(ValoreCampo valore) {
        // Verifica che il valore non sia null e lancia eccezione in caso contrario
        Objects.requireNonNull(valore, ERRORE_VALORE_NULLO);

        // Aggiunge il valore al dominio alla fine della lista
        this.dominio.add(valore);
    }

    /**
     * Verifica se un valore con il nome specificato è presente nel dominio.
     *
     * @param nomeValore il nome del valore da cercare
     * @return true se un valore con quel nome è presente, false altrimenti
     *
     * @post il valore restituito è true se e solo se esiste v nel getDominio() tale
     *       che v.getNome().equals(nomeValore)
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

    // -----------------------------------------------------------------------------------
    // METODI OVERRIDE DI OBJECT
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce una rappresentazione in stringa del campo.
     * Include il nome del campo seguito dalla lista dei valori nel dominio
     * nell'ordine originale.
     *
     * @return una rappresentazione in stringa del campo formattata su più righe
     *
     * @post il valore restituito non è null
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
     * Due campi sono uguali se hanno lo stesso nome e gli stessi valori di dominio
     * (indipendentemente dall'ordine).
     *
     * @param obj l'oggetto da confrontare con questo campo
     * @return true se l'oggetto è un Campo uguale a questo, false altrimenti
     *
     * @post il valore restituito è true solo se obj è un Campo con nome uguale e
     *       dominio contenente gli stessi elementi di questo
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
        if (!Objects.equals(this.nome, other.nome)) {
            return false;
        }

        // Verifica che i domini contengano gli stessi elementi (indipendentemente
        // dall'ordine)
        if (this.dominio.size() != other.dominio.size()) {
            return false;
        }

        // Verifica che tutti gli elementi di this.dominio siano in other.dominio
        return this.dominio.containsAll(other.dominio);
    }

    /**
     * Calcola l'hash code del campo basato su nome e dominio.
     *
     * @return l'hash code del campo
     *
     * @post se due campi sono uguali secondo equals(), hanno lo stesso hashCode()
     */
    @Override
    public int hashCode() {
        // Calcola l'hash basato su nome e sul contenuto del dominio
        // (indipendentemente dall'ordine)
        return Objects.hash(nome, new ArrayList<>(dominio));
    }
}