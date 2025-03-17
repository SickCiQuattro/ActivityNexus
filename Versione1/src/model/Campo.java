package model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Campo caratteristico di una categoria nel sistema gerarchico.
 * <p>
 * Un campo rappresenta una proprietà distintiva che caratterizza una categoria
 * e
 * determina le sue possibili specializzazioni. Il dominio del campo (insieme di
 * {@link ValoreCampo}) definisce i valori che la proprietà può assumere,
 * ciascuno
 * dei quali corrisponde a una potenziale sottocategoria.
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
 * <li>L'insieme dominio non è mai null (può essere vuoto)</li>
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

    /** Nome identificativo del campo */
    private String nome;

    /** Insieme dei valori possibili per questo campo */
    private final Set<ValoreCampo> dominio;

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
     * @post getDominio() restituisce un insieme vuoto
     */
    public Campo(String nome) {
        // Delega al costruttore più completo con un HashSet vuoto come dominio iniziale
        this(nome, new HashSet<>());
    }

    /**
     * Costruisce un Campo con il nome e il dominio specificati.
     *
     * @param nome    il nome del campo
     * @param dominio l'insieme iniziale di valori per il campo
     * @throws NullPointerException se il nome è null
     *
     * @pre nome != null
     * @post getNome() restituisce il nome fornito
     * @post getDominio() contiene tutti i valori non-null di dominio
     */
    public Campo(String nome, Set<ValoreCampo> dominio) {
        // Verifica che il nome non sia null e lancia eccezione in caso contrario
        this.nome = Objects.requireNonNull(nome, ERRORE_NOME_NULLO);

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
     *
     * @return una vista immutabile dell'insieme dei valori del dominio
     *
     * @post il valore restituito non è null
     * @post il valore restituito è un insieme non modificabile
     */
    public Set<ValoreCampo> getDominio() {
        // Restituisce una vista immutabile per proteggere l'incapsulamento
        return Collections.unmodifiableSet(this.dominio);
    }

    // -----------------------------------------------------------------------------------
    // METODI DI MANIPOLAZIONE DEL DOMINIO
    // -----------------------------------------------------------------------------------

    /**
     * Aggiunge un valore al dominio del campo.
     *
     * @param valore il valore da aggiungere
     * @throws NullPointerException se il valore è null
     *
     * @pre valore != null
     * @post getDominio().contains(valore) == true
     */
    public void addValore(ValoreCampo valore) {
        // Verifica che il valore non sia null e lancia eccezione in caso contrario
        Objects.requireNonNull(valore, ERRORE_VALORE_NULLO);

        // Aggiunge il valore al dominio
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
     * Include il nome del campo seguito dalla lista dei valori nel dominio.
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
     * Due campi sono uguali se hanno lo stesso nome e dominio.
     *
     * @param obj l'oggetto da confrontare con questo campo
     * @return true se l'oggetto è un Campo uguale a questo, false altrimenti
     *
     * @post il valore restituito è true solo se obj è un Campo con nome e dominio
     *       uguali a questo
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
     *
     * @post se due campi sono uguali secondo equals(), hanno lo stesso hashCode()
     */
    @Override
    public int hashCode() {
        // Calcola l'hash basato su nome e dominio
        return Objects.hash(nome, dominio);
    }
}