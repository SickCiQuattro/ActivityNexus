package model;

import java.io.*;
import java.util.Objects;

/**
 * Rappresenta una categoria all'interno di una struttura gerarchica.
 * <p>
 * Una categoria è un nodo nell'albero della gerarchia, identificata da un nome
 * univoco,
 * con un possibile campo caratteristico che determina le specializzazioni:
 * <ul>
 * <li><b>Categorie intermedie:</b> hanno un campo caratteristico che definisce
 * i valori per le sottocategorie (es. "Sport")</li>
 * <li><b>Categorie foglia:</b> senza campo caratteristico (campo = null),
 * rappresentano attività concrete (es. "Nuoto agonistico")</li>
 * </ul>
 * <p>
 * Implementa {@link Serializable} per la persistenza dei dati.
 * 
 * @see Campo
 * @see Gerarchia
 * @see ValoreCampo
 */
public class Categoria implements Serializable {
    // Identificatore di versione per la serializzazione
    private static final long serialVersionUID = 1L;

    // Costante per i messaggi di errore, riutilizzata in più punti
    public static final String NOME_NON_NULL = "Il nome della categoria non può essere null";

    // Nome univoco che identifica la categoria
    private String nome;

    // Campo caratteristico associato alla categoria (null per categorie foglia)
    private Campo campo = null;

    // -----------------------------------------------------------------------------------
    // COSTRUTTORI
    // -----------------------------------------------------------------------------------

    /**
     * Costruisce una categoria foglia con il nome specificato.
     * <p>
     * Crea una categoria terminale che rappresenta un'attività concreta
     * senza possibilità di ulteriore suddivisione.
     *
     * @param nome il nome della categoria (non deve essere null)
     * @throws NullPointerException se il nome è null
     */
    public Categoria(String nome) {
        // Utilizzo di Objects.requireNonNull per validare il parametro e lanciare
        // eccezione
        // se il nome è null, usando il messaggio di errore definito nella costante
        // NOME_NON_NULL
        this.nome = Objects.requireNonNull(nome, NOME_NON_NULL);
    }

    /**
     * Costruisce una categoria con nome e campo specificati.
     * <p>
     * Se campo != null, crea una categoria intermedia con possibili sottocategorie.
     * Se campo == null, equivale al costruttore con un solo parametro.
     *
     * @param nome  il nome della categoria (non deve essere null)
     * @param campo il campo associato alla categoria (null per categorie foglia)
     * @throws NullPointerException se il nome è null
     */
    public Categoria(String nome, Campo campo) {
        // Valida il nome come nel costruttore precedente
        this.nome = Objects.requireNonNull(nome, NOME_NON_NULL);
        // Il campo può essere null (categoria foglia), quindi non c'è validazione
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
        // Restituisce il nome della categoria (sempre non-null)
        return this.nome;
    }

    /**
     * Imposta il nome della categoria.
     * <p>
     * Attenzione: se la categoria è già inserita in una gerarchia, cambiare il nome
     * potrebbe influenzare i metodi di ricerca e confronto.
     *
     * @param nome il nuovo nome della categoria (non deve essere null)
     * @throws NullPointerException se il nome è null
     */
    public void setNome(String nome) {
        // Valida il nuovo nome e lo assegna
        this.nome = Objects.requireNonNull(nome, NOME_NON_NULL);
    }

    /**
     * Restituisce il campo caratteristico della categoria.
     * <p>
     * Il campo definisce la proprietà su cui si basa la classificazione
     * delle sottocategorie (es. "Disciplina" per la categoria "Sport").
     *
     * @return il campo associato alla categoria, o null se la categoria è una
     *         foglia
     * @see #hasCampo()
     */
    public Campo getCampo() {
        // Restituisce il campo (può essere null per categorie foglia)
        return this.campo;
    }

    /**
     * Imposta il campo caratteristico della categoria.
     * <p>
     * Attenzione: modifica la natura della categoria (intermedia o foglia)
     * e potrebbe richiedere aggiornamenti alla struttura gerarchica.
     * 
     * @param campo il nuovo campo associato alla categoria (null per categorie
     *              foglia)
     */
    public void setCampo(Campo campo) {
        // Assegna il campo (può essere null)
        this.campo = campo;
    }

    /**
     * Verifica se la categoria ha un campo caratteristico associato.
     * <p>
     * Distingue tra categorie intermedie (true) e categorie foglia (false).
     *
     * @return true se categoria intermedia, false se categoria foglia
     */
    public boolean hasCampo() {
        // Verifica se il campo è diverso da null
        return this.campo != null;
    }

    // -----------------------------------------------------------------------------------
    // METODI OVERRIDE DI OBJECT
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce una rappresentazione in stringa della categoria.
     * <p>
     * Include il nome e, se presente, il campo con tutti i suoi valori di dominio.
     *
     * @return una rappresentazione in stringa della categoria
     */
    @Override
    public String toString() {
        // Costruisce la rappresentazione in stringa della categoria
        StringBuilder sb = new StringBuilder();
        sb.append("Nome: ").append(this.nome).append("\n");

        // Se la categoria ha un campo, aggiunge anche la sua rappresentazione
        if (hasCampo()) {
            sb.append("Campo: ").append(this.campo).append("\n");
        }

        return sb.toString();
    }

    /**
     * Confronta questa categoria con l'oggetto specificato.
     * <p>
     * Due categorie sono uguali se hanno lo stesso nome (case-insensitive).
     * Il campo caratteristico non viene considerato nel confronto.
     *
     * @param obj l'oggetto da confrontare
     * @return true se l'oggetto è una Categoria con lo stesso nome, false
     *         altrimenti
     */
    @Override
    public boolean equals(Object obj) {
        // Controllo di identità (stesso riferimento) per efficienza
        if (this == obj) {
            return true;
        }
        // Se obj è null o di classe diversa da Categoria, non sono uguali
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        // Cast sicuro (verificato sopra) e confronto dei nomi ignorando
        // maiuscole/minuscole
        Categoria categoria = (Categoria) obj;
        return nome.equalsIgnoreCase(categoria.getNome());
    }

    /**
     * Calcola l'hash code per questa categoria.
     * <p>
     * Coerente con {@link #equals(Object)}, basato sul nome in minuscolo.
     * Garantisce che categorie uguali abbiano lo stesso hashcode.
     *
     * @return l'hash code basato sul nome in minuscolo
     */
    @Override
    public int hashCode() {
        // Calcola l'hash code basato sul nome convertito in minuscolo
        // per mantenere coerenza con la definizione di equals
        return nome.toLowerCase().hashCode();
    }
}