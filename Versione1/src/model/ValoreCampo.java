package model;

import java.io.*;
import java.util.Objects;

/**
 * Classe che rappresenta un valore possibile per un {@link Campo}.
 * <p>
 * In un sistema di categorizzazione gerarchica, i {@link Campo} possono avere
 * valori
 * predefiniti che l'utente può scegliere. Questa classe modella ciascuno di
 * questi valori,
 * fornendo un modo strutturato per rappresentarli e gestirli.
 * <p>
 * Un ValoreCampo è costituito da:
 * <ul>
 * <li><b>Nome</b>: identificatore univoco obbligatorio, utilizzato per
 * distinguere
 * questo valore dagli altri all'interno dello stesso campo</li>
 * <li><b>Descrizione</b>: informazione opzionale che fornisce dettagli
 * aggiuntivi
 * sul significato del valore</li>
 * </ul>
 * <p>
 * Esempi di ValoreCampo in contesti diversi:
 * <ul>
 * <li>In un campo "Difficoltà": ValoreCampo("Facile", "Adatto a
 * principianti")</li>
 * <li>In un campo "Stagione": ValoreCampo("Estate", "Giugno-Settembre")</li>
 * <li>In un campo "Priorità": ValoreCampo("Alta", "Richiede attenzione
 * immediata")</li>
 * </ul>
 * <p>
 * La classe implementa {@link Serializable} per consentire la persistenza dei
 * valori
 * all'interno della struttura del sistema.
 * 
 * @see Campo
 * @see Categoria
 * @see Serializable
 */
public class ValoreCampo implements Serializable {
    // Identificatore di versione per la serializzazione
    private static final long serialVersionUID = 1L;

    // Messaggio di errore costante per migliorare la manutenibilità del codice
    public static final String NOME_NON_NULL = "Il nome del valore non può essere null";

    /**
     * Nome univoco che identifica questo valore all'interno di un campo.
     * <p>
     * Il nome è l'attributo primario e obbligatorio di un ValoreCampo, utilizzato
     * per identificarlo in modo univoco tra i valori di uno stesso campo. Non può
     * essere null e viene utilizzato nei confronti di uguaglianza.
     * <p>
     * Esempi: "Facile", "Media", "Difficile" in un campo di difficoltà.
     */
    private String nome;

    /**
     * Descrizione opzionale che fornisce informazioni aggiuntive sul valore.
     * <p>
     * La descrizione permette di aggiungere dettagli esplicativi sul significato
     * del valore, utili per gli utenti del sistema. Non viene utilizzata nei
     * confronti di uguaglianza e può essere una stringa vuota ma non null.
     * <p>
     * Esempi: "Adatto a principianti" per il valore "Facile".
     */
    private String descrizione;

    // -----------------------------------------------------------------------------------
    // COSTRUTTORI
    // -----------------------------------------------------------------------------------

    /**
     * Costruisce un valore del campo con il nome specificato e senza descrizione.
     * <p>
     * Questo costruttore crea un ValoreCampo con il nome fornito e imposta
     * automaticamente una descrizione vuota. È utile quando non è necessario
     * fornire una descrizione dettagliata del valore.
     * <p>
     * Esempio di utilizzo:
     * 
     * <pre>
     * ValoreCampo difficoltaMedia = new ValoreCampo("Media");
     * </pre>
     *
     * @param nome il nome del valore del campo (non deve essere null)
     * @throws NullPointerException se il nome è null
     * 
     * @see #ValoreCampo(String, String)
     */
    public ValoreCampo(String nome) {
        // Delega al costruttore completo con descrizione vuota
        this(nome, "");
    }

    /**
     * Costruisce un valore del campo con il nome e la descrizione specificati.
     * <p>
     * Questo costruttore crea un ValoreCampo completo, specificando sia il nome
     * (obbligatorio) che la descrizione (opzionale). Se la descrizione passata è
     * null,
     * viene automaticamente convertita in una stringa vuota per garantire che
     * {@link #getDescrizione()} non restituisca mai null.
     * <p>
     * Esempio di utilizzo:
     * 
     * <pre>
     * ValoreCampo difficoltaFacile = new ValoreCampo("Facile", "Adatto a principianti");
     * </pre>
     *
     * @param nome        il nome del valore del campo (non deve essere null)
     * @param descrizione la descrizione del valore del campo (se null, viene
     *                    trattata come stringa vuota)
     * @throws NullPointerException se il nome è null
     * 
     * @see #ValoreCampo(String)
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
    // GETTER E SETTER
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce il nome del valore del campo.
     * <p>
     * Il nome è l'identificatore univoco del valore all'interno di un campo
     * e viene utilizzato per i confronti di uguaglianza.
     * <p>
     * Questo metodo garantisce di non restituire mai null, poiché il nome
     * è un attributo obbligatorio.
     *
     * @return il nome del valore del campo (mai null)
     * 
     * @see #setNome(String)
     */
    public String getNome() {
        return this.nome;
    }

    /**
     * Imposta il nome del valore del campo.
     * <p>
     * Modifica l'identificatore univoco del valore. Questo metodo va usato con
     * cautela se il valore fa già parte di un Campo o di una struttura dati
     * ordinata, poiché potrebbe alterare l'unicità e la corretta indicizzazione.
     * <p>
     * Esempio di utilizzo:
     * 
     * <pre>
     * ValoreCampo difficolta = new ValoreCampo("Media");
     * difficolta.setNome("Intermedia"); // Rinomina il valore
     * </pre>
     *
     * @param nome il nuovo nome del valore (non deve essere null)
     * @throws NullPointerException se il nome è null
     * 
     * @see #getNome()
     */
    public void setNome(String nome) {
        // Verifica che il nuovo nome non sia null
        this.nome = Objects.requireNonNull(nome, NOME_NON_NULL);
    }

    /**
     * Restituisce la descrizione del valore del campo.
     * <p>
     * La descrizione fornisce informazioni aggiuntive sul significato del valore.
     * Questo metodo garantisce di non restituire mai null, anche se il valore
     * non ha una descrizione (in tal caso restituisce una stringa vuota).
     * <p>
     * Per verificare se esiste una descrizione non vuota, utilizzare
     * {@link #hasDescrizione()}.
     *
     * @return la descrizione del valore del campo (mai null, può essere stringa
     *         vuota)
     * 
     * @see #setDescrizione(String)
     * @see #hasDescrizione()
     */
    public String getDescrizione() {
        return this.descrizione;
    }

    /**
     * Imposta la descrizione del valore del campo.
     * <p>
     * Aggiorna le informazioni aggiuntive associate a questo valore.
     * Se il parametro è null, la descrizione viene impostata come stringa vuota,
     * garantendo che {@link #getDescrizione()} non restituisca mai null.
     * <p>
     * Esempio di utilizzo:
     * 
     * <pre>
     * ValoreCampo priorita = new ValoreCampo("Alta");
     * priorita.setDescrizione("Richiede attenzione immediata");
     * </pre>
     *
     * @param descrizione la nuova descrizione (se null, viene impostata come
     *                    stringa vuota)
     * 
     * @see #getDescrizione()
     */
    public void setDescrizione(String descrizione) {
        // Gestisce il caso in cui la nuova descrizione sia null, convertendola in
        // stringa vuota
        this.descrizione = descrizione != null ? descrizione : "";
    }

    /**
     * Verifica se questo valore ha una descrizione non vuota.
     * <p>
     * Questo metodo è utile per determinare se il valore ha informazioni
     * aggiuntive associate, senza dover verificare manualmente se la
     * stringa restituita da {@link #getDescrizione()} è vuota.
     * <p>
     * Esempio di utilizzo:
     * 
     * <pre>
     * ValoreCampo valore = new ValoreCampo("Alta", "");
     * if (!valore.hasDescrizione()) {
     *     System.out.println("Nessuna descrizione disponibile");
     * }
     * </pre>
     *
     * @return true se la descrizione contiene almeno un carattere, false altrimenti
     * 
     * @see #getDescrizione()
     */
    public boolean hasDescrizione() {
        // Verifica se la descrizione contiene almeno un carattere
        return !this.descrizione.isEmpty();
    }

    // -----------------------------------------------------------------------------------
    // OVERRIDE DI OBJECT
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce una rappresentazione testuale del valore del campo.
     * <p>
     * La stringa generata include:
     * <ul>
     * <li>Il nome del valore (sempre)</li>
     * <li>La descrizione, se presente, separata dal nome con " - "</li>
     * </ul>
     * <p>
     * Esempi di output:
     * <ul>
     * <li>"Alta" (solo nome, senza descrizione)</li>
     * <li>"Alta - Richiede attenzione immediata" (nome e descrizione)</li>
     * </ul>
     * <p>
     * Questo metodo è utile per visualizzare il valore in interfacce utente
     * o per debugging.
     *
     * @return una rappresentazione in stringa del valore del campo
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
     * Due valori di campo sono considerati uguali se hanno lo stesso nome,
     * indipendentemente dalla descrizione. Questo comportamento è coerente con
     * l'idea che il nome sia l'identificatore univoco del valore all'interno
     * di un campo.
     * <p>
     * Implementazione:
     * <ol>
     * <li>Confronto di identità (stesso oggetto)</li>
     * <li>Controllo di null e di tipo</li>
     * <li>Confronto dei nomi (case-sensitive)</li>
     * </ol>
     * <p>
     * Nota: la descrizione non è considerata per l'uguaglianza.
     *
     * @param obj l'oggetto da confrontare con questo ValoreCampo
     * @return true se l'oggetto è un ValoreCampo con lo stesso nome,
     *         false altrimenti
     * 
     * @see #hashCode()
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
     * Calcola l'hash code per questo valore del campo.
     * <p>
     * Coerentemente con {@link #equals(Object)}, l'hash code è basato solo sul
     * nome,
     * che è l'identificatore univoco del valore all'interno di un campo.
     * <p>
     * Questo garantisce che due valori considerati uguali da {@code equals()}
     * avranno
     * lo stesso hash code, requisito fondamentale quando i valori vengono
     * utilizzati
     * come chiavi in strutture dati come HashMap o HashSet.
     *
     * @return l'hash code del valore del campo basato sul nome
     * 
     * @see #equals(Object)
     * @see Objects#hash(Object...)
     */
    @Override
    public int hashCode() {
        // L'hash code è basato solo sul nome
        return Objects.hash(nome);
    }
}