package model;

import java.io.*;
import java.util.Objects;

/**
 * Rappresenta una categoria all'interno di una struttura gerarchica.
 * <p>
 * Una categoria è un nodo nell'albero della gerarchia di classificazione, con
 * due possibili ruoli:
 * <ul>
 * <li><b>Categoria intermedia:</b> possiede un campo caratteristico che
 * definisce
 * i criteri di suddivisione in sottocategorie (es. "Sport" con campo
 * "Disciplina")</li>
 * <li><b>Categoria foglia:</b> rappresenta un'attività concreta senza ulteriori
 * suddivisioni
 * (es. "Nuoto agonistico"), identificata dall'assenza di campo caratteristico
 * (campo = null)</li>
 * </ul>
 * <p>
 * Le categorie sono identificate dal loro nome e organizzate secondo relazioni
 * gerarchiche
 * padre-figlio all'interno di una {@link Gerarchia}.
 *
 * <p>
 * <b>Invarianti della classe:</b>
 * <ul>
 * <li>Il nome della categoria non è mai null</li>
 * <li>Il campo caratteristico può essere null (per categorie foglia)</li>
 * <li>Due categorie con lo stesso nome (ignorando maiuscole/minuscole) sono
 * considerate equivalenti</li>
 * </ul>
 *
 * @see Campo
 * @see Gerarchia
 * @see ValoreCampo
 */
public class Categoria implements Serializable {
    // -----------------------------------------------------------------------------------
    // FIELDS E COSTANTI
    // -----------------------------------------------------------------------------------

    @Serial
    private static final long serialVersionUID = 1L;

    /** Messaggio di errore per quando il nome è null */
    public static final String NOME_NON_NULL = "Il nome della categoria non può essere null";

    /** Nome identificativo della categoria */
    private String nome;

    /**
     * Campo caratteristico associato alla categoria
     * (null per categorie foglia che rappresentano attività concrete)
     */
    private Campo campo = null;

    // -----------------------------------------------------------------------------------
    // COSTRUTTORI
    // -----------------------------------------------------------------------------------

    /**
     * Costruisce una categoria foglia che rappresenta un'attività concreta.
     *
     * @param nome il nome della categoria
     * @throws NullPointerException se il nome è null
     *
     * @pre nome != null
     * @post getNome().equals(nome)
     * @post hasCampo() == false
     * @post getCampo() == null
     */
    public Categoria(String nome) {
        // Verifica che il nome non sia null, altrimenti lancia NullPointerException
        this.nome = Objects.requireNonNull(nome, NOME_NON_NULL);
        // Per impostazione predefinita, campo è null (categoria foglia)
    }

    /**
     * Costruisce una categoria con nome e campo specificati.
     * <p>
     * Se campo != null, crea una categoria intermedia con possibili sottocategorie.
     * Se campo == null, crea una categoria foglia equivalente al costruttore con un
     * solo parametro.
     *
     * @param nome  il nome della categoria
     * @param campo il campo associato alla categoria (null per categorie foglia)
     * @throws NullPointerException se il nome è null
     *
     * @pre nome != null
     * @post getNome().equals(nome)
     * @post getCampo() == campo
     * @post hasCampo() == (campo != null)
     */
    public Categoria(String nome, Campo campo) {
        // Verifica che il nome non sia null
        this.nome = Objects.requireNonNull(nome, NOME_NON_NULL);
        // Campo può essere null (categoria foglia)
        this.campo = campo;
    }

    // -----------------------------------------------------------------------------------
    // GETTER E SETTER
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce il nome della categoria.
     *
     * @return il nome della categoria, mai null
     *
     * @post result != null
     */
    public String getNome() {
        return this.nome;
    }

    /**
     * Imposta il nome della categoria.
     * <p>
     * <b>Attenzione:</b> Se la categoria è già inserita in una gerarchia, cambiare
     * il nome
     * potrebbe compromettere la coerenza della struttura.
     *
     * @param nome il nuovo nome della categoria
     * @throws NullPointerException se il nome è null
     *
     * @pre nome != null
     * @post getNome().equals(nome)
     */
    public void setNome(String nome) {
        this.nome = Objects.requireNonNull(nome, NOME_NON_NULL);
    }

    /**
     * Restituisce il campo caratteristico della categoria.
     * <p>
     * Il campo determina la proprietà su cui si basano le sottocategorie
     * (es. "Disciplina" per la categoria "Sport").
     *
     * @return il campo associato alla categoria, o null se la categoria è una
     *         foglia
     *
     * @post result == null se e solo se hasCampo() == false
     */
    public Campo getCampo() {
        return this.campo;
    }

    /**
     * Imposta il campo caratteristico della categoria.
     * <p>
     * <b>Attenzione:</b> Questo metodo cambia la natura della categoria (da
     * intermedia a foglia
     * o viceversa) e potrebbe richiedere aggiornamenti nella struttura gerarchica.
     *
     * @param campo il nuovo campo associato alla categoria (null per categorie
     *              foglia)
     *
     * @post getCampo() == campo
     * @post hasCampo() == (campo != null)
     */
    public void setCampo(Campo campo) {
        this.campo = campo;
    }

    /**
     * Verifica se la categoria ha un campo caratteristico associato.
     * <p>
     * Distingue tra categorie intermedie (con campo) e categorie foglia (senza
     * campo).
     *
     * @return true se la categoria è intermedia, false se è una foglia
     *
     * @post result == (getCampo() != null)
     */
    public boolean hasCampo() {
        return this.campo != null;
    }

    // -----------------------------------------------------------------------------------
    // METODI OVERRIDE DI OBJECT
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce una rappresentazione testuale della categoria.
     * <p>
     * Include il nome e, se presente, il campo caratteristico con i suoi valori di
     * dominio.
     *
     * @return una rappresentazione in stringa della categoria
     *
     * @post result != null
     * @post result contiene il nome della categoria
     * @post se hasCampo() == true, result contiene anche la rappresentazione del
     *       campo
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Nome: ").append(this.nome).append("\n");

        // Aggiunge la rappresentazione del campo solo se presente
        if (hasCampo()) {
            sb.append("Campo: ").append(this.campo).append("\n");
        }

        return sb.toString();
    }

    /**
     * Confronta questa categoria con l'oggetto specificato.
     * <p>
     * Due categorie sono considerate uguali se hanno lo stesso nome, ignorando
     * maiuscole e minuscole.
     * Il campo caratteristico non viene considerato per determinare l'uguaglianza.
     *
     * @param obj l'oggetto da confrontare
     * @return true se l'oggetto è una Categoria con lo stesso nome, false
     *         altrimenti
     *
     * @post se this == obj, result è true
     * @post se obj == null o obj non è una Categoria, result è false
     * @post se obj è una Categoria, result è true se e solo se
     *       getNome().equalsIgnoreCase(((Categoria)obj).getNome())
     */
    @Override
    public boolean equals(Object obj) {
        // Ottimizzazione: controllo di identità
        if (this == obj) {
            return true;
        }

        // Controllo di tipo e null
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        // Confronto dei nomi ignorando maiuscole/minuscole
        Categoria categoria = (Categoria) obj;
        return nome.equalsIgnoreCase(categoria.getNome());
    }

    /**
     * Calcola l'hash code della categoria basato sul nome in minuscolo.
     * <p>
     * Garantisce la coerenza con il metodo {@link #equals(Object)}.
     *
     * @return l'hash code basato sul nome in minuscolo
     *
     * @post se this.equals(altro), allora this.hashCode() == altro.hashCode()
     */
    @Override
    public int hashCode() {
        // Usa il nome in minuscolo per essere coerente con equals (case-insensitive)
        return nome.toLowerCase().hashCode();
    }
}