package model;

import java.io.*;

/**
 * Classe che rappresenta un valore assunto dal {@link Campo}.
 * Un ValoreCampo è costituito da un nome e da una descrizione opzionale.
 */
public class ValoreCampo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nome;
    private String descrizione = "";

    /**
     * Costruisce un valore del campo con il nome specificato.
     *
     * @param nome il nome del valore del campo
     */
    public ValoreCampo(String nome) {
        this.nome = nome;
    }

    /**
     * Costruisce un valore del campo con il nome e la descrizione specificati.
     *
     * @param nome        il nome del valore del campo
     * @param descrizione la descrizione del valore del campo
     */
    public ValoreCampo(String nome, String descrizione) {
        this.nome = nome;
        this.descrizione = descrizione;
    }

    /**
     * Restituisce il nome del valore del campo.
     *
     * @return il nome del valore del campo
     */
    public String getNome() {
        return this.nome;
    }

    /**
     * Restituisce la descrizione del valore del campo.
     *
     * @return la descrizione del valore del campo
     */
    public String getDescrizione() {
        return this.descrizione;
    }

    /**
     * Scrive l'oggetto ValoreCampo in un flusso di output.
     *
     * @param out il flusso di output
     * @throws IOException se si verifica un errore di I/O
     */
    @Serial
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    /**
     * Legge l'oggetto ValoreCampo da un flusso di input.
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
     * Restituisce una rappresentazione in stringa del valore del campo.
     * La stringa include il nome del valore del campo e, se presente, la descrizione.
     *
     * @return una rappresentazione in stringa del valore del campo
     */
    @Override
    public String toString() {
        return this.nome + (this.descrizione.isEmpty() ? "" : " - " + this.descrizione);
    }

    /**
     * Confronta questo valore del campo con l'oggetto specificato.
     * Il risultato è true se e solo se l'argomento non è null ed è un oggetto ValoreCampo che ha lo stesso nome di questo oggetto.
     *
     * @param obj l'oggetto da confrontare con questo ValoreCampo
     * @return true se l'oggetto è uguale a questo ValoreCampo, false altrimenti
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ValoreCampo valore = (ValoreCampo) obj;
        return this.nome.equals(valore.nome);
    }
}