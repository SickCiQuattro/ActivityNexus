package model;

import java.io.*;
import java.util.*;

/**
 * Classe che rappresenta un comprensorio geografico.
 * Un comprensorio geografico è costituito da un nome e da una lista di comuni.
 * I comuni possono essere aggiunti o rimossi dalla lista.
 */
public class Comprensorio implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nome;
    private final List<String> comuni;

    // -----------------------------------------------------------------------------------
    // COSTRUTTORI
    // -----------------------------------------------------------------------------------

    /**
     * Costruisce un Comprensorio con il nome specificato.
     * Inizializza la lista dei comuni come una ArrayList vuota.
     *
     * @param nome il nome del comprensorio (può essere null, ma non consigliato)
     */
    public Comprensorio(String nome) {
        this.nome = nome;
        this.comuni = new ArrayList<>();
    }

    /**
     * Costruisce un Comprensorio con il nome e la lista di comuni specificati.
     *
     * @param nome   il nome del comprensorio
     * @param comuni la lista iniziale dei comuni (se null, viene creata una lista
     *               vuota)
     */
    public Comprensorio(String nome, Collection<String> comuni) {
        this.nome = nome;
        this.comuni = new ArrayList<>();

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
     *
     * @return il nome del comprensorio
     */
    public String getNome() {
        return this.nome;
    }

    /**
     * Imposta il nome del comprensorio.
     *
     * @param nome il nuovo nome del comprensorio
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Restituisce una copia non modificabile della lista dei comuni del
     * comprensorio.
     * Per modificare la lista dei comuni, utilizzare i metodi {@code addComune} e
     * {@code removeComune}.
     *
     * @return una lista non modificabile dei comuni
     */
    public List<String> getComuni() {
        return Collections.unmodifiableList(this.comuni);
    }

    /**
     * Restituisce il numero di comuni presenti nel comprensorio.
     *
     * @return il numero di comuni
     */
    public int getNumeroComuniCount() {
        return this.comuni.size();
    }

    /**
     * Verifica se il comprensorio non contiene alcun comune.
     *
     * @return true se il comprensorio non contiene comuni, false altrimenti
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
     * Il confronto tra nomi di comuni ignora la differenza tra maiuscole e
     * minuscole.
     *
     * @param comune il nome del comune da aggiungere (se null, non viene aggiunto)
     * @return true se il comune è stato aggiunto, false altrimenti
     */
    public boolean addComune(String comune) {
        if (comune == null || hasComune(comune)) {
            return false;
        }
        return this.comuni.add(comune);
    }

    /**
     * Aggiunge tutti i comuni della collezione specificata al comprensorio.
     * Vengono aggiunti solo i comuni non nulli e non già presenti.
     *
     * @param comuni la collezione di comuni da aggiungere
     * @return il numero di comuni effettivamente aggiunti
     */
    public int addComuni(Collection<String> comuni) {
        if (comuni == null) {
            return 0;
        }

        int aggiunti = 0;
        for (String comune : comuni) {
            if (addComune(comune)) {
                aggiunti++;
            }
        }
        return aggiunti;
    }

    /**
     * Verifica se un comune è presente nella lista dei comuni.
     * Il confronto tra nomi di comuni ignora la differenza tra maiuscole e
     * minuscole.
     *
     * @param comune il nome del comune da verificare
     * @return true se il comune è presente, false altrimenti o se comune è null
     */
    public boolean hasComune(String comune) {
        if (comune == null) {
            return false;
        }

        for (String c : this.comuni) {
            if (c.equalsIgnoreCase(comune)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Rimuove un comune dal comprensorio se è presente nella lista dei comuni.
     * Il confronto tra nomi di comuni ignora la differenza tra maiuscole e
     * minuscole.
     *
     * @param comune il nome del comune da rimuovere
     * @return true se il comune è stato rimosso, false altrimenti o se comune è
     *         null
     */
    public boolean removeComune(String comune) {
        if (comune == null) {
            return false;
        }

        Iterator<String> it = this.comuni.iterator();
        while (it.hasNext()) {
            String c = it.next();
            if (c.equalsIgnoreCase(comune)) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    /**
     * Rimuove tutti i comuni dal comprensorio.
     */
    public void clearComuni() {
        this.comuni.clear();
    }

    /**
     * Restituisce un array contenente tutti i comuni del comprensorio.
     *
     * @return un array di stringhe contenente i nomi dei comuni
     */
    public String[] getComuniAsArray() {
        return this.comuni.toArray(new String[0]);
    }

    // -----------------------------------------------------------------------------------
    // METODI OVERRIDE DI OBJECT
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce una rappresentazione in stringa del comprensorio.
     * La stringa include il nome del comprensorio e la lista dei comuni.
     *
     * @return una rappresentazione in stringa del comprensorio
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Comprensorio: ").append(this.nome).append("\nComuni:\n");

        if (this.comuni.isEmpty()) {
            sb.append("\t(nessun comune)\n");
        } else {
            for (String c : this.comuni) {
                sb.append("\t").append(c).append("\n");
            }
        }

        return sb.toString();
    }

    /**
     * Confronta questo comprensorio con l'oggetto specificato.
     * Due comprensori sono considerati uguali se hanno lo stesso nome, ignorando
     * la differenza tra maiuscole e minuscole.
     *
     * @param obj l'oggetto da confrontare con questo Comprensorio
     * @return true se l'oggetto è uguale a questo Comprensorio, false altrimenti
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Comprensorio comprensorio = (Comprensorio) obj;
        return nome != null && nome.equalsIgnoreCase(comprensorio.getNome());
    }

    /**
     * Calcola l'hash code per questo comprensorio.
     * Coerentemente con equals(), l'hash code è basato sul nome del comprensorio
     * convertito in minuscolo per ignorare differenze tra maiuscole e minuscole.
     *
     * @return l'hash code del comprensorio
     */
    @Override
    public int hashCode() {
        return nome != null ? nome.toLowerCase().hashCode() : 0;
    }
}