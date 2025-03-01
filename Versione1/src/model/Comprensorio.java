package model;

import java.io.*;
import java.util.*;

/**
 * Classe che rappresenta un comprensorio geografico nel sistema.
 * <p>
 * Un comprensorio geografico è un'unità amministrativa che raggruppa più comuni
 * sotto un'unica entità. Questa classe fornisce funzionalità per creare,
 * gestire
 * e manipolare comprensori, mantenendo l'elenco dei comuni contenuti in
 * ciascuno di essi.
 * <p>
 * Caratteristiche principali:
 * <ul>
 * <li>Ogni comprensorio è identificato da un nome univoco</li>
 * <li>Contiene una lista di nomi di comuni gestita internamente</li>
 * <li>Offre metodi per aggiungere, rimuovere e verificare la presenza di
 * comuni</li>
 * <li>Impedisce duplicazioni di comuni (case-insensitive)</li>
 * <li>Implementa l'interfaccia {@link Serializable} per consentire la
 * persistenza</li>
 * </ul>
 * <p>
 * Un comprensorio è univocamente identificato dal suo nome, senza considerare
 * maiuscole/minuscole. Due comprensori con lo stesso nome sono considerati
 * uguali,
 * indipendentemente dai comuni contenuti.
 * 
 * @see java.io.Serializable
 */
public class Comprensorio implements Serializable {
    // Identificatore di versione per la serializzazione
    private static final long serialVersionUID = 1L;

    // Nome del comprensorio, utilizzato come identificatore univoco
    private String nome;

    // Lista dei comuni appartenenti al comprensorio
    // Dichiarata final per garantire che il riferimento non cambi,
    // ma i contenuti possono essere modificati
    private final List<String> comuni;

    // -----------------------------------------------------------------------------------
    // COSTRUTTORI
    // -----------------------------------------------------------------------------------

    /**
     * Costruisce un Comprensorio con il nome specificato.
     * <p>
     * Inizializza la lista dei comuni come una ArrayList vuota. I comuni possono
     * essere aggiunti successivamente tramite i metodi {@code addComune} o
     * {@code addComuni}.
     * <p>
     * Esempio di utilizzo:
     * 
     * <pre>
     * Comprensorio valtellina = new Comprensorio("Valtellina");
     * valtellina.addComune("Sondrio");
     * valtellina.addComune("Tirano");
     * </pre>
     *
     * @param nome il nome del comprensorio (può essere null, ma non consigliato per
     *             garantire il corretto funzionamento dei metodi equals e hashCode)
     * 
     * @see #addComune(String)
     * @see #addComuni(Collection)
     */
    public Comprensorio(String nome) {
        this.nome = nome; // Accetta anche null, anche se non consigliato
        this.comuni = new ArrayList<>(); // Inizializza una lista vuota
    }

    /**
     * Costruisce un Comprensorio con il nome e la lista di comuni specificati.
     * <p>
     * Questo costruttore è utile quando si dispone già di una collezione di comuni
     * che devono essere inclusi nel comprensorio. I comuni duplicati o null vengono
     * automaticamente filtrati durante la costruzione.
     * <p>
     * Esempio di utilizzo:
     * 
     * <pre>
     * List&lt;String&gt; comuniLombardi = Arrays.asList("Milano", "Brescia", "Bergamo");
     * Comprensorio lombardia = new Comprensorio("Lombardia", comuniLombardi);
     * </pre>
     *
     * @param nome   il nome del comprensorio
     * @param comuni la collezione iniziale dei comuni (se null, viene creata una
     *               lista vuota)
     * 
     * @see #hasComune(String)
     */
    public Comprensorio(String nome, Collection<String> comuni) {
        this.nome = nome;
        this.comuni = new ArrayList<>(); // Inizializza una lista vuota

        // Se la collezione di comuni non è null, aggiunge ogni comune valido
        // (non null e non già presente) alla lista interna
        if (comuni != null) {
            for (String comune : comuni) {
                if (comune != null && !hasComune(comune)) {
                    this.comuni.add(comune);
                }
            }
        }
    }

    // -----------------------------------------------------------------------------------
    // GETTER E SETTER
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce il nome del comprensorio.
     * <p>
     * Il nome è l'identificatore principale del comprensorio e viene utilizzato
     * per determinare l'uguaglianza tra comprensori.
     *
     * @return il nome del comprensorio (può essere null)
     * 
     * @see #setNome(String)
     */
    public String getNome() {
        return this.nome;
    }

    /**
     * Imposta il nome del comprensorio.
     * <p>
     * Attenzione: modificare il nome di un comprensorio già inserito in una
     * collezione
     * (come un Set o utilizzato come chiave in una Map) potrebbe causare
     * inconsistenze
     * poiché l'hashCode cambierà.
     *
     * @param nome il nuovo nome del comprensorio (può essere null, ma non
     *             consigliato)
     * 
     * @see #getNome()
     * @see #equals(Object)
     * @see #hashCode()
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Restituisce una copia non modificabile della lista dei comuni del
     * comprensorio.
     * <p>
     * Questo metodo garantisce l'incapsulamento dei dati impedendo modifiche
     * dirette alla
     * lista interna. Per modificare la lista dei comuni, è necessario utilizzare i
     * metodi
     * dedicati come {@code addComune}, {@code removeComune}, ecc.
     * <p>
     * Esempio di utilizzo:
     * 
     * <pre>
     * Comprensorio lombardia = new Comprensorio("Lombardia");
     * lombardia.addComune("Milano");
     * 
     * // Modo corretto per iterare sui comuni
     * for (String comune : lombardia.getComuni()) {
     *     System.out.println(comune);
     * }
     * 
     * // Questo causerà un'eccezione UnsupportedOperationException
     * // lombardia.getComuni().add("Como");
     * </pre>
     *
     * @return una lista non modificabile dei comuni del comprensorio
     * 
     * @see Collections#unmodifiableList(List)
     * @see #addComune(String)
     * @see #removeComune(String)
     */
    public List<String> getComuni() {
        // Restituisce una vista non modificabile della lista interna
        // per proteggere l'incapsulamento
        return Collections.unmodifiableList(this.comuni);
    }

    /**
     * Restituisce il numero di comuni presenti nel comprensorio.
     * <p>
     * Questo metodo è utile per verificare rapidamente quanti comuni appartengono
     * al comprensorio senza dover ottenere e contare l'intera lista.
     *
     * @return il numero di comuni nel comprensorio
     * 
     * @see #isEmpty()
     * @see #getComuni()
     */
    public int getNumeroComuniCount() {
        return this.comuni.size();
    }

    /**
     * Verifica se il comprensorio non contiene alcun comune.
     * <p>
     * Un comprensorio vuoto può rappresentare un'entità appena creata o che ha
     * subito un'operazione di {@code clearComuni()}.
     *
     * @return true se il comprensorio non contiene comuni, false altrimenti
     * 
     * @see #getNumeroComuniCount()
     * @see #clearComuni()
     */
    public boolean isEmpty() {
        return this.comuni.isEmpty();
    }

    // -----------------------------------------------------------------------------------
    // METODI DI GESTIONE DEI COMUNI
    // -----------------------------------------------------------------------------------

    /**
     * Aggiunge un comune al comprensorio se non è già presente nella lista dei
     * comuni.
     * <p>
     * Il confronto tra nomi di comuni ignora la differenza tra maiuscole e
     * minuscole
     * (case-insensitive), quindi "Milano" e "milano" sono considerati lo stesso
     * comune.
     * <p>
     * Esempi di utilizzo:
     * 
     * <pre>
     * Comprensorio c = new Comprensorio("Valtellina");
     * c.addComune("Sondrio"); // Restituisce true
     * c.addComune("sondrio"); // Restituisce false (già presente)
     * c.addComune(null); // Restituisce false (non valido)
     * </pre>
     *
     * @param comune il nome del comune da aggiungere (se null, non viene aggiunto)
     * @return true se il comune è stato aggiunto con successo, false altrimenti
     *         (nome null o comune già presente)
     * 
     * @see #hasComune(String)
     * @see #addComuni(Collection)
     */
    public boolean addComune(String comune) {
        // Verifica se il comune è null o già presente (case-insensitive)
        if (comune == null || hasComune(comune)) {
            return false;
        }
        // Aggiunge il comune alla lista interna
        return this.comuni.add(comune);
    }

    /**
     * Aggiunge tutti i comuni della collezione specificata al comprensorio.
     * <p>
     * Vengono aggiunti solo i comuni non nulli e non già presenti nel comprensorio.
     * Il confronto tra nomi di comuni ignora la differenza tra maiuscole e
     * minuscole.
     * <p>
     * Questo metodo è utile per aggiungere più comuni in una sola operazione, ad
     * esempio
     * quando si uniscono due comprensori o si importano dati da una fonte esterna.
     * <p>
     * Esempio di utilizzo:
     * 
     * <pre>
     * Comprensorio c = new Comprensorio("Lombardia");
     * List&lt;String&gt; nuoviComuni = Arrays.asList("Milano", "Brescia", null, "MILANO");
     * int aggiunti = c.addComuni(nuoviComuni); // Restituisce 2
     * </pre>
     *
     * @param comuni la collezione di comuni da aggiungere
     * @return il numero di comuni effettivamente aggiunti (escludendo null e
     *         duplicati)
     * 
     * @see #addComune(String)
     */
    public int addComuni(Collection<String> comuni) {
        // Se la collezione è null, non viene aggiunto nessun comune
        if (comuni == null) {
            return 0;
        }

        // Tiene traccia del numero di comuni effettivamente aggiunti
        int aggiunti = 0;

        // Iterazione su tutti i comuni della collezione
        for (String comune : comuni) {
            // addComune gestirà la validazione (non null, non duplicato)
            if (addComune(comune)) {
                aggiunti++;
            }
        }

        return aggiunti;
    }

    /**
     * Verifica se un comune è presente nella lista dei comuni del comprensorio.
     * <p>
     * Il confronto tra nomi di comuni ignora la differenza tra maiuscole e
     * minuscole,
     * quindi "Milano" e "milano" sono considerati lo stesso comune.
     * <p>
     * Esempi di utilizzo:
     * 
     * <pre>
     * Comprensorio c = new Comprensorio("Valtellina");
     * c.addComune("Sondrio");
     * c.hasComune("Sondrio"); // Restituisce true
     * c.hasComune("sondrio"); // Restituisce true (case-insensitive)
     * c.hasComune("Tirano"); // Restituisce false
     * c.hasComune(null); // Restituisce false
     * </pre>
     *
     * @param comune il nome del comune da verificare
     * @return true se il comune è presente, false altrimenti o se il parametro è
     *         null
     * 
     * @see #addComune(String)
     * @see #removeComune(String)
     */
    public boolean hasComune(String comune) {
        // Se il comune è null, non può essere presente nel comprensorio
        if (comune == null) {
            return false;
        }

        // Itera su tutti i comuni cercando una corrispondenza case-insensitive
        for (String c : this.comuni) {
            if (c.equalsIgnoreCase(comune)) {
                return true;
            }
        }

        // Se non è stata trovata corrispondenza, restituisce false
        return false;
    }

    /**
     * Rimuove un comune dal comprensorio se è presente nella lista dei comuni.
     * <p>
     * Il confronto tra nomi di comuni ignora la differenza tra maiuscole e
     * minuscole,
     * quindi "Milano" e "milano" sono considerati lo stesso comune.
     * <p>
     * Questo metodo utilizza un iteratore per evitare
     * ConcurrentModificationException
     * durante la rimozione di elementi dalla lista.
     * <p>
     * Esempi di utilizzo:
     * 
     * <pre>
     * Comprensorio c = new Comprensorio("Lombardia");
     * c.addComune("Milano");
     * c.removeComune("MILANO"); // Restituisce true (case-insensitive)
     * c.removeComune("Roma"); // Restituisce false (non presente)
     * c.removeComune(null); // Restituisce false (parametro non valido)
     * </pre>
     *
     * @param comune il nome del comune da rimuovere
     * @return true se il comune è stato trovato e rimosso, false altrimenti o se il
     *         parametro è null
     * 
     * @see #hasComune(String)
     * @see #clearComuni()
     */
    public boolean removeComune(String comune) {
        // Se il comune è null, non può essere rimosso
        if (comune == null) {
            return false;
        }

        // Utilizza un iteratore per rimuovere in modo sicuro durante l'iterazione
        Iterator<String> it = this.comuni.iterator();
        while (it.hasNext()) {
            String c = it.next();
            if (c.equalsIgnoreCase(comune)) {
                it.remove(); // Rimuove il comune corrente dall'iteratore
                return true; // Comune trovato e rimosso
            }
        }

        // Se non è stata trovata corrispondenza, restituisce false
        return false;
    }

    /**
     * Rimuove tutti i comuni dal comprensorio.
     * <p>
     * Questo metodo svuota completamente la lista dei comuni, ma mantiene
     * le altre proprietà del comprensorio come il nome.
     * <p>
     * Esempio di utilizzo:
     * 
     * <pre>
     * Comprensorio c = new Comprensorio("Lombardia");
     * c.addComune("Milano");
     * c.addComune("Brescia");
     * c.clearComuni(); // Rimuove tutti i comuni
     * c.isEmpty(); // Restituisce true
     * </pre>
     * 
     * @see #removeComune(String)
     * @see #isEmpty()
     */
    public void clearComuni() {
        this.comuni.clear();
    }

    /**
     * Restituisce un array contenente tutti i comuni del comprensorio.
     * <p>
     * Questo metodo è utile quando è necessario lavorare con un array di
     * comuni anziché con una lista o una collezione. L'array restituito è
     * una copia dei dati, quindi modificarlo non influisce sul comprensorio.
     * <p>
     * Esempio di utilizzo:
     * 
     * <pre>
     * Comprensorio c = new Comprensorio("Lombardia");
     * c.addComune("Milano");
     * c.addComune("Brescia");
     * 
     * String[] comuni = c.getComuniAsArray();
     * // ora 'comuni' è un array {"Milano", "Brescia"}
     * </pre>
     *
     * @return un array di stringhe contenente i nomi dei comuni
     * 
     * @see #getComuni()
     */
    public String[] getComuniAsArray() {
        // Converte la lista in un array di stringhe
        return this.comuni.toArray(new String[0]);
    }

    // -----------------------------------------------------------------------------------
    // METODI OVERRIDE DI OBJECT
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce una rappresentazione in stringa del comprensorio.
     * <p>
     * Il formato della stringa include:
     * <ul>
     * <li>Il nome del comprensorio in maiuscolo</li>
     * <li>L'elenco dei comuni, uno per riga con prefisso "|---"</li>
     * <li>Un messaggio speciale se il comprensorio non contiene comuni</li>
     * </ul>
     * <p>
     * Esempio di output:
     * 
     * <pre>
     * COMPRENSORIO: Lombardia
     * ELENCO COMUNI:
     * |---Milano
     * |---Brescia
     * </pre>
     *
     * @return una rappresentazione in stringa del comprensorio formattata su più
     *         righe
     */
    @Override
    public String toString() {
        // Costruisce la rappresentazione in stringa del comprensorio
        StringBuilder sb = new StringBuilder();
        sb.append("COMPRENSORIO: ").append(this.nome).append("\nELENCO COMUNI:\n");

        // Gestisce il caso di comprensorio senza comuni
        if (this.comuni.isEmpty()) {
            sb.append("\t(nessun comune)\n");
        } else {
            // Aggiunge ogni comune con un prefisso grafico per maggiore leggibilità
            for (String c : this.comuni) {
                sb.append("|---").append(c).append("\n");
            }
        }

        return sb.toString();
    }

    /**
     * Confronta questo comprensorio con l'oggetto specificato.
     * <p>
     * Due comprensori sono considerati uguali se hanno lo stesso nome, ignorando
     * la differenza tra maiuscole e minuscole. Il contenuto delle liste dei comuni
     * non viene considerato per il confronto.
     * <p>
     * Note implementative:
     * <ul>
     * <li>Se i due oggetti sono lo stesso riferimento, sono considerati uguali</li>
     * <li>Se l'oggetto passato è null o di tipo diverso, sono considerati
     * diversi</li>
     * <li>Se il nome di questo comprensorio è null, i comprensori sono considerati
     * diversi</li>
     * <li>Altrimenti, il confronto è basato sull'uguaglianza case-insensitive dei
     * nomi</li>
     * </ul>
     *
     * @param obj l'oggetto da confrontare con questo Comprensorio
     * @return true se l'oggetto è uguale a questo Comprensorio, false altrimenti
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

        // Verifiche base: oggetto non null e della stessa classe
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        // Cast sicuro (verificato sopra) e confronto dei nomi
        Comprensorio comprensorio = (Comprensorio) obj;

        // Se il nome è null, il comprensorio non può essere uguale ad altri
        // Altrimenti confronta i nomi ignorando maiuscole/minuscole
        return nome != null && nome.equalsIgnoreCase(comprensorio.getNome());
    }

    /**
     * Calcola l'hash code per questo comprensorio.
     * <p>
     * Coerentemente con {@link #equals(Object)}, l'hash code è basato solo sul nome
     * del comprensorio, convertito in minuscolo per ignorare differenze tra
     * maiuscole
     * e minuscole.
     * <p>
     * Note implementative:
     * <ul>
     * <li>Se il nome è null, restituisce 0</li>
     * <li>Altrimenti, restituisce l'hash code del nome convertito in minuscolo</li>
     * </ul>
     * <p>
     * Questo garantisce che due comprensori considerati uguali da equals() abbiano
     * lo stesso hash code, requisito fondamentale quando i comprensori vengono
     * utilizzati
     * come chiavi in strutture dati basate su hash.
     *
     * @return l'hash code del comprensorio basato sul nome in minuscolo, o 0 se il
     *         nome è null
     * 
     * @see #equals(Object)
     * @see String#toLowerCase()
     * @see String#hashCode()
     */
    @Override
    public int hashCode() {
        // Calcola l'hash code basato sul nome convertito in minuscolo, o 0 se nome è
        // null
        return nome != null ? nome.toLowerCase().hashCode() : 0;
    }
}