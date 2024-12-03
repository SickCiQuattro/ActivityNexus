package model;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Classe che rappresenta un campo di una categoria.
 * Un campo è costituito da un nome e da un dominio di valori.
 * I valori possono essere associati a una descrizione.
 */
public class Campo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nome;
    private Set<ValoreCampo> dominio;

    /**
     * Costruisce un Campo con il nome specificato.
     * Inizializza il dominio come un HashSet vuoto.
     *
     * @param nome il nome del campo
     */
    public Campo(String nome) {
        this.nome = nome;
        this.dominio = new HashSet<>();
    }

    /**
     * Costruisce un Campo con il nome e il dominio specificati.
     *
     * @param nome    il nome del campo
     * @param dominio il dominio dei valori associati alle descrizioni
     */
    public Campo(String nome, HashSet<ValoreCampo> dominio) {
        this.nome = nome;
        this.dominio = dominio;
    }

    /**
     * Restituisce il nome del campo.
     *
     * @return il nome del campo
     */
    public String getNome() {
        return this.nome;
    }

    /**
     * Imposta il nome del campo.
     *
     * @param nome il nuovo nome del campo
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Restituisce il dominio dei valori associati alle descrizioni.
     *
     * @return il dominio dei valori
     */
    public Set<ValoreCampo> getDominio() {
        return this.dominio;
    }

    /**
     * Aggiunge un valore al dominio del campo.
     *
     * @param valore il valore da aggiungere
     * @return true se il valore è stato aggiunto correttamente, false altrimenti
     */
    public boolean addValore(ValoreCampo valore) {
        if (valore == null) {
            return false;
        }
        return this.dominio.add(valore);
    }

    /**
     * Controlla se un dato valore è presente nel dominio del campo.
     *
     * @param valore il valore da cercare
     * @return true se il valore è presente, false altrimenti
     */
    public boolean hasValore(ValoreCampo valore) {
        return this.dominio.contains(valore);
    }

    /**
     * Controlla se un valore con il nome specificato è presente nel dominio del campo.
     *
     * @param nome il nome del valore da cercare
     * @return true se il valore è presente, false altrimenti
     */
    public boolean hasValore(String nome) {
        for (ValoreCampo valore : this.dominio) {
            if (valore.getNome().equals(nome)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Scrive l'oggetto Campo in un flusso di output.
     *
     * @param out il flusso di output
     * @throws IOException se si verifica un errore di I/O
     */
    @Serial
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    /**
     * Legge l'oggetto Campo da un flusso di input.
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
     * Restituisce una rappresentazione in stringa del campo.
     * La stringa include il nome del campo e la lista dei valori nel dominio.
     *
     * @return una rappresentazione in stringa del campo
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.nome).append(":\n");
        for (ValoreCampo valore : this.dominio) {
            sb.append("\t").append(valore.toString()).append("\n");
        }
        return sb.toString();
    }
}