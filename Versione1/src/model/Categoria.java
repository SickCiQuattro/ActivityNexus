package model;

import java.io.*;
import java.util.Objects;

/**
 * Classe che rappresenta una categoria all'interno di una struttura gerarchica.
 * <p>
 * Una categoria è un elemento fondamentale nel sistema di classificazione
 * gerarchica
 * e rappresenta un nodo nell'albero della gerarchia. Ogni categoria è
 * identificata
 * da un nome univoco e può avere un campo caratteristico associato che
 * determina
 * le possibili specializzazioni.
 * <p>
 * Le categorie si dividono in due tipi principali:
 * <ul>
 * <li><b>Categorie intermedie:</b> hanno un campo caratteristico associato che
 * definisce
 * i possibili valori per le sottocategorie derivate. Rappresentano gruppi o
 * classi
 * di attività (es. "Sport", "Attività artistiche").</li>
 * <li><b>Categorie foglia:</b> non hanno un campo caratteristico (campo = null)
 * e
 * rappresentano attività concrete che non possono essere ulteriormente
 * suddivise
 * (es. "Nuoto agonistico", "Pittura ad olio").</li>
 * </ul>
 * <p>
 * Le categorie sono organizzate in una struttura gerarchica attraverso la
 * classe
 * {@link Gerarchia}, che utilizza un albero per rappresentare le relazioni di
 * specializzazione.
 * <p>
 * Questa classe implementa {@link Serializable} per consentire la persistenza
 * dei dati.
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
     * Costruisce una Categoria con il nome specificato e senza campo associato.
     * <p>
     * Questo costruttore crea una categoria foglia, che rappresenta un'attività
     * concreta
     * che non può essere ulteriormente suddivisa in sottocategorie. Le categorie
     * foglia
     * sono i nodi terminali dell'albero gerarchico.
     * <p>
     * Esempio di utilizzo:
     * 
     * <pre>
     * Categoria attivitaConcreta = new Categoria("Nuoto agonistico");
     * </pre>
     *
     * @param nome il nome della categoria (non deve essere null)
     * @throws NullPointerException se il nome è null
     * 
     * @see #hasCampo()
     */
    public Categoria(String nome) {
        // Utilizzo di Objects.requireNonNull per validare il parametro e lanciare
        // eccezione
        // se il nome è null, usando il messaggio di errore definito nella costante
        // NOME_NON_NULL
        this.nome = Objects.requireNonNull(nome, NOME_NON_NULL);
    }

    /**
     * Costruisce una Categoria con il nome e il campo specificati.
     * <p>
     * Questo costruttore può creare sia categorie intermedie (se il campo non è
     * null)
     * che categorie foglia (se il campo è null). Le categorie intermedie possono
     * avere
     * sottocategorie basate sui valori del dominio del campo.
     * <p>
     * Esempi di utilizzo:
     * 
     * <pre>
     * // Categoria intermedia con campo caratteristico
     * Campo campoDisciplina = new Campo("Disciplina");
     * Categoria categoriaIntermedia = new Categoria("Sport", campoDisciplina);
     * 
     * // Categoria foglia (equivalente a usare il costruttore con un solo
     * // parametro)
     * Categoria categoriaFoglia = new Categoria("Calcio", null);
     * </pre>
     *
     * @param nome  il nome della categoria (non deve essere null)
     * @param campo il campo associato alla categoria (può essere null per categorie
     *              foglia)
     * @throws NullPointerException se il nome è null
     * 
     * @see Campo
     * @see #hasCampo()
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
     * <p>
     * Il nome è l'identificativo principale della categoria all'interno della sua
     * gerarchia
     * e viene utilizzato nei confronti per determinare l'uguaglianza tra categorie.
     * <p>
     * Poiché il nome non può mai essere null (come garantito dai costruttori),
     * questo
     * metodo restituirà sempre un valore non null.
     *
     * @return il nome della categoria (mai null)
     * 
     * @see #setNome(String)
     */
    public String getNome() {
        // Restituisce il nome della categoria (sempre non-null)
        return this.nome;
    }

    /**
     * Imposta il nome della categoria.
     * <p>
     * Consente di modificare l'identificativo della categoria. Questo può essere
     * utile
     * in fase di modifica della struttura gerarchica, ma va usato con cautela
     * poiché
     * potrebbe influire su riferimenti esistenti a questa categoria.
     * <p>
     * Attenzione: se la categoria è già inserita in una gerarchia, cambiare il nome
     * potrebbe influenzare il funzionamento dei metodi di ricerca e confronto.
     *
     * @param nome il nuovo nome della categoria (non deve essere null)
     * @throws NullPointerException se il nome è null
     * 
     * @see #getNome()
     */
    public void setNome(String nome) {
        // Valida il nuovo nome e lo assegna
        this.nome = Objects.requireNonNull(nome, NOME_NON_NULL);
    }

    /**
     * Restituisce il campo caratteristico associato alla categoria.
     * <p>
     * Il campo caratteristico definisce la proprietà su cui si basa la
     * classificazione
     * delle sottocategorie derivate. Ad esempio, una categoria "Sport" potrebbe
     * avere
     * un campo "Disciplina" con valori come "Calcio", "Nuoto", ecc.
     * <p>
     * Se la categoria è una foglia (un'attività concreta), questo metodo restituirà
     * null.
     * Usare {@link #hasCampo()} per verificare preliminarmente se il campo esiste.
     *
     * @return il campo associato alla categoria, o null se la categoria è una
     *         foglia
     * 
     * @see Campo
     * @see #setCampo(Campo)
     * @see #hasCampo()
     */
    public Campo getCampo() {
        // Restituisce il campo (può essere null per categorie foglia)
        return this.campo;
    }

    /**
     * Imposta il campo caratteristico associato alla categoria.
     * <p>
     * Questo metodo permette di modificare il campo associato alla categoria,
     * trasformandola da categoria intermedia a foglia (se campo = null) o
     * viceversa.
     * <p>
     * Attenzione: se la categoria è già inserita in una gerarchia, cambiare il
     * campo
     * potrebbe richiedere un aggiornamento della struttura gerarchica per mantenere
     * la coerenza dei dati.
     * 
     * @param campo il nuovo campo associato alla categoria (può essere null)
     * 
     * @see Campo
     * @see #getCampo()
     * @see #hasCampo()
     */
    public void setCampo(Campo campo) {
        // Assegna il campo (può essere null)
        this.campo = campo;
    }

    /**
     * Verifica se la categoria ha un campo caratteristico associato.
     * <p>
     * Questo metodo è utile per determinare se una categoria è una categoria
     * intermedia
     * (ha un campo caratteristico, quindi potrebbe avere sottocategorie) o una
     * foglia
     * (categoria terminale che rappresenta un'attività concreta).
     * <p>
     * Esempio di utilizzo:
     * 
     * <pre>
     * if (categoria.hasCampo()) {
     *     // È una categoria intermedia, potrebbe avere sottocategorie
     *     Campo campo = categoria.getCampo();
     *     // Elaborazione del campo...
     * } else {
     *     // È una categoria foglia, rappresenta un'attività concreta
     *     // Nessuna ulteriore specializzazione possibile
     * }
     * </pre>
     *
     * @return true se la categoria ha un campo associato (categoria intermedia),
     *         false se è una categoria foglia
     * 
     * @see #getCampo()
     * @see #setCampo(Campo)
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
     * La stringa generata include il nome della categoria e, se presente, il campo
     * associato con tutti i suoi valori di dominio. Il formato è strutturato per
     * facilitare la visualizzazione gerarchica:
     * <ul>
     * <li>Prima riga: "Nome: [nome della categoria]"</li>
     * <li>Se presente un campo, righe successive: "Campo: [rappresentazione del
     * campo]"</li>
     * </ul>
     * <p>
     * La rappresentazione del campo include il suo nome e l'elenco dei valori di
     * dominio,
     * ciascuno indentato su una nuova riga. Questa formattazione è utile per
     * visualizzare
     * la struttura completa della categoria in modo leggibile.
     *
     * @return una rappresentazione in stringa della categoria formattata su più
     *         righe
     * 
     * @see Campo#toString()
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
     * Due categorie sono considerate uguali se hanno lo stesso nome, ignorando la
     * differenza tra maiuscole e minuscole. Il campo caratteristico non viene
     * considerato
     * nel confronto, quindi due categorie con lo stesso nome ma campi diversi
     * saranno
     * considerate uguali.
     * <p>
     * Questa implementazione supporta l'unicità dei nomi delle categorie
     * all'interno di
     * una gerarchia, permettendo di identificare univocamente le categorie per nome
     * in
     * modo case-insensitive.
     * <p>
     * Note implementative:
     * <ul>
     * <li>Controllo di identità per efficienza: se sono lo stesso oggetto, sono
     * uguali</li>
     * <li>Controlli di tipo e null: se l'oggetto è null o di tipo diverso, non sono
     * uguali</li>
     * <li>Confronto case-insensitive dei nomi utilizzando equalsIgnoreCase</li>
     * </ul>
     *
     * @param obj l'oggetto da confrontare con questa Categoria
     * @return true se l'oggetto è uguale a questa Categoria, false altrimenti
     * 
     * @see #hashCode()
     * @see String#equalsIgnoreCase(String)
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
     * Coerentemente con {@link #equals(Object)}, l'hash code è basato solo sul nome
     * della
     * categoria, convertito in minuscolo per ignorare differenze tra maiuscole e
     * minuscole.
     * <p>
     * Questo garantisce che due categorie considerate uguali da {@code equals()}
     * avranno
     * lo stesso hash code, requisito fondamentale quando le categorie vengono
     * utilizzate
     * come chiavi in strutture dati basate su hash come HashMap o HashSet.
     * <p>
     * Note implementative:
     * <ul>
     * <li>Conversione del nome in minuscolo prima di calcolare l'hash</li>
     * <li>Utilizzo del metodo hashCode() della classe String</li>
     * </ul>
     *
     * @return l'hash code della categoria basato sul nome in minuscolo
     * 
     * @see #equals(Object)
     * @see String#toLowerCase()
     * @see String#hashCode()
     * @see java.util.HashMap
     * @see java.util.HashSet
     */
    @Override
    public int hashCode() {
        // Calcola l'hash code basato sul nome convertito in minuscolo
        // per mantenere coerenza con la definizione di equals
        return nome.toLowerCase().hashCode();
    }
}