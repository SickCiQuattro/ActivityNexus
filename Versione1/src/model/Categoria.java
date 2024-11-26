package model;

import java.io.*;

/**
 * Classe che rappresenta una categoria di una gerarchia.
 * Una categoria è costituita da un nome e da un campo.
 */
public class Categoria implements Serializable {
    private String nome;
    private Campo campo = null;

    /**
     * Costruisce una Categoria con il nome specificato.
     *
     * @param nome il nome della categoria
     */
    public Categoria(String nome) {
        this.nome = nome;
    }

    /**
     * Costruisce una Categoria con il nome e il campo specificati.
     *
     * @param nome il nome della categoria
     * @param campo il campo associato alla categoria
     */
    public Categoria(String nome, Campo campo) {
        this.nome = nome;
        this.campo = campo;
    }

    /**
     * Restituisce il nome della categoria.
     *
     * @return il nome della categoria
     */
    public String getNome() {
        return this.nome;
    }

    /**
     * Restituisce il campo associato alla categoria.
     *
     * @return il campo associato alla categoria
     */
    public Campo getCampo() {
        return this.campo;
    }

    /**
     * Imposta il nome della categoria.
     *
     * @param nome il nuovo nome della categoria
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Imposta il campo associato alla categoria.
     *
     * @param campo il nuovo campo associato alla categoria
     */
    public void setCampo(Campo campo) {
        this.campo = campo;
    }

    /**
     * Scrive l'oggetto Categoria in un flusso di output.
     *
     * @param out il flusso di output
     * @throws IOException se si verifica un errore di I/O
     */
    @Serial
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    /**
     * Legge l'oggetto Categoria da un flusso di input.
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
     * Restituisce una rappresentazione in stringa della categoria.
     * La stringa include il nome della categoria e, se presente, il campo associato.
     *
     * @return una rappresentazione in stringa della categoria
     */
    @Override
    public String toString() {
        return "Nome: " + this.nome + "\n" + (this.campo != null ? "Campo: " + this.campo + "\n" : "");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Categoria categoria = (Categoria) obj;
        return nome.equals(categoria.nome);
    }
}