package model;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Classe che rappresenta un campo di una categoria all'interno del sistema
 * gerarchico.
 * <p>
 * Un campo caratteristico definisce una proprietà di una categoria che può
 * assumere
 * diversi valori predefiniti nel suo dominio. Il dominio è l'insieme dei
 * possibili valori
 * che il campo può assumere, ognuno rappresentato da un oggetto
 * {@link ValoreCampo}.
 * <p>
 * I campi sono elementi fondamentali per definire la struttura gerarchica:
 * <ul>
 * <li>Le categorie non-foglia hanno un campo caratteristico che determina le
 * possibili
 * sottocategorie derivabili</li>
 * <li>Ogni valore nel dominio del campo rappresenta una possibile
 * specializzazione della categoria</li>
 * <li>Le categorie foglia (attività concrete) non hanno campi
 * caratteristici</li>
 * </ul>
 * <p>
 * Questa classe garantisce l'integrità del dominio impedendo valori nulli e
 * fornendo
 * metodi per manipolare i valori in sicurezza.
 * <p>
 * Implementa {@link Serializable} per consentire la persistenza dei dati.
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

    // Insieme di valori che costituiscono il dominio del campo (es. per
    // "Disciplina": Calcio, Nuoto, ecc.)
    private final Set<ValoreCampo> dominio;

    // -----------------------------------------------------------------------------------
    // COSTRUTTORI
    // -----------------------------------------------------------------------------------

    /**
     * Costruisce un Campo con il nome specificato.
     * Inizializza il dominio come un insieme vuoto, che potrà essere popolato
     * successivamente
     * tramite il metodo {@link #addValore(ValoreCampo)}.
     * <p>
     * Questo costruttore è tipicamente usato quando i valori del dominio non sono
     * noti
     * al momento della creazione del campo, ma verranno aggiunti in seguito.
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
     * <p>
     * Questo costruttore è utile quando si conosce già l'insieme completo di valori
     * che il campo può assumere. Il dominio fornito viene copiato internamente per
     * garantire l'incapsulamento.
     * <p>
     * I valori null nel dominio vengono ignorati silenziosamente per mantenere
     * l'integrità del dominio stesso.
     *
     * @param nome    il nome del campo (non deve essere null)
     * @param dominio l'insieme iniziale di valori per il campo
     * @throws NullPointerException se il nome è null
     * 
     * @see ValoreCampo
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
     * <p>
     * Il nome del campo rappresenta la proprietà che il campo descrive, ad esempio
     * "Disciplina" per un campo che distingue diverse tipologie di sport, o "Area
     * geografica"
     * per un campo che distingue diverse regioni.
     *
     * @return il nome del campo, mai null
     */
    public String getNome() {
        return this.nome;
    }

    /**
     * Imposta il nome del campo.
     * <p>
     * Permette di modificare il nome del campo mantenendo lo stesso dominio di
     * valori.
     * Questo può essere utile quando si desidera rinominare un campo senza
     * alterarne
     * la struttura.
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
     * <p>
     * Questo metodo fornisce accesso in sola lettura al dominio, impedendo
     * modifiche
     * dirette che potrebbero compromettere l'integrità dei dati. Per modificare il
     * dominio,
     * è necessario utilizzare i metodi dedicati {@code addValore} e
     * {@code removeValore}.
     * <p>
     * Il set restituito è una vista immutabile dell'insieme interno, quindi
     * qualsiasi
     * tentativo di modifica genererà un'eccezione.
     *
     * @return una vista immutabile dell'insieme dei valori del dominio
     * 
     * @see Collections#unmodifiableSet(Set)
     * @see #addValore(ValoreCampo)
     * @see #removeValore(ValoreCampo)
     */
    public Set<ValoreCampo> getDominio() {
        // Restituisce una vista immutabile per proteggere l'incapsulamento
        return Collections.unmodifiableSet(this.dominio);
    }

    /**
     * Restituisce il numero di valori nel dominio del campo.
     * <p>
     * Questo metodo è utile per verificare rapidamente quante opzioni sono
     * disponibili
     * per questo campo, senza dover ottenere e contare l'intero insieme di valori.
     *
     * @return la dimensione del dominio, 0 se il dominio è vuoto
     */
    public int getDominioDimensione() {
        return this.dominio.size();
    }

    // -----------------------------------------------------------------------------------
    // METODI DI MANIPOLAZIONE DEL DOMINIO
    // -----------------------------------------------------------------------------------

    /**
     * Aggiunge un valore al dominio del campo.
     * <p>
     * Se un valore con lo stesso nome è già presente nel dominio, questo metodo
     * restituirà false senza modificare il dominio, in quanto i valori sono
     * identificati
     * univocamente dal loro nome all'interno del campo.
     * <p>
     * L'aggiunta di valori al dominio è essenziale per definire le possibili
     * specializzazioni della categoria associata al campo.
     *
     * @param valore il valore da aggiungere (non deve essere null)
     * @return true se il valore è stato aggiunto correttamente, false se era già
     *         presente
     * @throws NullPointerException se il valore è null
     * 
     * @see ValoreCampo
     * @see #hasValore(ValoreCampo)
     */
    public boolean addValore(ValoreCampo valore) {
        // Verifica che il valore non sia null e lancia eccezione in caso contrario
        Objects.requireNonNull(valore, "Il valore non può essere null");

        // Tenta di aggiungere il valore al dominio
        return this.dominio.add(valore);
    }

    /**
     * Rimuove un valore dal dominio del campo.
     * <p>
     * Questo metodo rimuove il valore specificato se è presente nel dominio.
     * La rimozione è basata sul metodo {@code equals} della classe
     * {@link ValoreCampo},
     * quindi due valori sono considerati uguali se hanno lo stesso nome.
     * <p>
     * Se il valore specificato è null, il metodo restituisce false senza modificare
     * il dominio.
     *
     * @param valore il valore da rimuovere
     * @return true se il valore è stato rimosso, false se non era presente o era
     *         null
     * 
     * @see ValoreCampo#equals(Object)
     */
    public boolean removeValore(ValoreCampo valore) {
        // Se il valore è null, non è possibile rimuoverlo
        return valore != null && this.dominio.remove(valore);
    }

    /**
     * Rimuove un valore dal dominio del campo specificandone il nome.
     * <p>
     * Questo metodo è una variante di {@link #removeValore(ValoreCampo)} che
     * permette
     * di rimuovere un valore conoscendone solo il nome, senza dover creare un
     * oggetto
     * ValoreCampo completo.
     * <p>
     * Utilizza un predicato per trovare e rimuovere il valore con il nome
     * specificato.
     *
     * @param nomeValore il nome del valore da rimuovere
     * @return true se il valore è stato trovato e rimosso, false altrimenti
     * 
     * @see #removeValore(ValoreCampo)
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
     * Controlla se un dato valore è presente nel dominio del campo.
     * <p>
     * La verifica è basata sul metodo {@code equals} della classe
     * {@link ValoreCampo},
     * quindi due valori sono considerati uguali se hanno lo stesso nome.
     * <p>
     * Questo metodo è utile per evitare duplicati nel dominio prima di tentare di
     * aggiungere un nuovo valore.
     *
     * @param valore il valore da cercare
     * @return true se il valore è presente, false altrimenti o se il valore è null
     * 
     * @see ValoreCampo#equals(Object)
     * @see #addValore(ValoreCampo)
     */
    public boolean hasValore(ValoreCampo valore) {
        // Se il valore è null, non può essere presente nel dominio
        return valore != null && this.dominio.contains(valore);
    }

    /**
     * Controlla se un valore con il nome specificato è presente nel dominio del
     * campo.
     * <p>
     * Questo metodo è una variante di {@link #hasValore(ValoreCampo)} che permette
     * di verificare la presenza di un valore conoscendone solo il nome, senza dover
     * creare un oggetto ValoreCampo completo.
     * <p>
     * Utilizza un ciclo per iterare su tutti i valori del dominio finché non trova
     * un valore con il nome corrispondente.
     *
     * @param nomeValore il nome del valore da cercare
     * @return true se un valore con quel nome è presente, false altrimenti
     * 
     * @see #hasValore(ValoreCampo)
     * @see ValoreCampo#getNome()
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
     * <p>
     * Questo metodo è utile quando si desidera accedere all'oggetto ValoreCampo
     * completo a partire dal suo nome, ad esempio per ottenere la descrizione
     * associata al valore.
     * <p>
     * Se nessun valore con il nome specificato è presente nel dominio, il metodo
     * restituisce null.
     *
     * @param nomeValore il nome del valore da cercare
     * @return il ValoreCampo corrispondente, o null se non trovato o se il nome è
     *         null
     * 
     * @see ValoreCampo#getNome()
     * @see ValoreCampo#getDescrizione()
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
     * <p>
     * Questo metodo è particolarmente utile durante la validazione,
     * poiché un campo senza valori nel dominio non può essere utilizzato
     * per definire sottocategorie.
     *
     * @return true se il dominio contiene almeno un valore, false se è vuoto
     * 
     * @see #getDominioDimensione()
     */
    public boolean hasDominio() {
        return !this.dominio.isEmpty();
    }

    // -----------------------------------------------------------------------------------
    // METODI OVERRIDE DI OBJECT
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce una rappresentazione in stringa del campo.
     * <p>
     * La stringa generata include il nome del campo seguito dalla lista di tutti
     * i valori nel suo dominio, ciascuno su una nuova riga e indentato.
     * <p>
     * Se il dominio è vuoto, viene indicato con un messaggio specifico.
     * Questo formato è utile per la visualizzazione gerarchica delle informazioni.
     *
     * @return una rappresentazione in stringa del campo formattata su più righe
     * 
     * @see ValoreCampo#toString()
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
     * <p>
     * Due campi sono considerati uguali se hanno lo stesso nome e lo stesso
     * dominio di valori (stessi elementi, indipendentemente dall'ordine).
     * <p>
     * Questo metodo segue il contratto generale di {@code equals}: è riflessivo,
     * simmetrico, transitivo e consistente.
     *
     * @param obj l'oggetto da confrontare con questo campo
     * @return true se l'oggetto è un Campo uguale a questo, false altrimenti
     * 
     * @see Object#equals(Object)
     * @see #hashCode()
     * @see ValoreCampo#equals(Object)
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
     * Calcola l'hash code per questo campo.
     * <p>
     * L'hash code è derivato dal nome del campo e dal suo dominio di valori.
     * Questo metodo è coerente con {@link #equals(Object)}, garantendo che
     * campi uguali abbiano lo stesso hash code.
     * <p>
     * Questo è importante quando i campi vengono utilizzati come chiavi in
     * strutture dati basate su hash come HashMap o HashSet.
     *
     * @return l'hash code del campo
     * 
     * @see Object#hashCode()
     * @see #equals(Object)
     * @see java.util.HashMap
     * @see java.util.HashSet
     */
    @Override
    public int hashCode() {
        // Calcola l'hash basato su nome e dominio
        return Objects.hash(nome, dominio);
    }
}