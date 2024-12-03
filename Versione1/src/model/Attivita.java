package model;

import java.io.*;

/**
 * Classe che rappresenta un'attività.
 * Un'attività è costituita da un nome e da una descrizione opzionale.
 */
public class Attivita implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nome;
    private String descrizione = "";

    /**
     * Costruisce un'Attivita con il nome specificato.
     *
     * @param nome il nome dell'attività
     */
    public Attivita(String nome) {
        this.nome = nome;
    }

    /**
     * Costruisce un'Attivita con il nome e la descrizione specificati.
     *
     * @param nome        il nome dell'attività
     * @param descrizione la descrizione dell'attività
     */
    public Attivita(String nome, String descrizione) {
        this.nome = nome;
        this.descrizione = descrizione;
    }

    /**
     * Restituisce il nome dell'attività.
     *
     * @return il nome dell'attività
     */
    public String getNome() {
        return this.nome;
    }

    /**
     * Restituisce la descrizione dell'attività.
     *
     * @return la descrizione dell'attività
     */
    public String getDescrizione() {
        return this.descrizione;
    }

    /**
     * Scrive l'oggetto Attivita in un flusso di output.
     *
     * @param out il flusso di output
     * @throws IOException se si verifica un errore di I/O
     */
    @Serial
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    /**
     * Legge l'oggetto Attivita da un flusso di input.
     *
     * @param in il flusso di input
     * @throws IOException se si verifica un errore di I/O
     * @throws ClassNotFoundException se la classe non viene trovata
     */
    @Serial
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }

    /**
     * Restituisce una rappresentazione in stringa dell'attività.
     * La stringa include il nome dell'attività e, se presente, la descrizione.
     *
     * @return una rappresentazione in stringa dell'attività
     */
    @Override
    public String toString() {
        return this.nome + (this.descrizione.isEmpty() ? "" : " - " + this.descrizione);
    }

    /**
     * Confronta questa attività con l'oggetto specificato.
     * Il risultato è true se e solo se l'argomento non è null ed è un oggetto Attivita che ha lo stesso nome di questo oggetto.
     *
     * @param obj l'oggetto da confrontare con questa Attivita
     * @return true se l'oggetto è uguale a questa Attivita, false altrimenti
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Attivita attivita = (Attivita) obj;
        return this.nome.equals(attivita.nome);
    }
}