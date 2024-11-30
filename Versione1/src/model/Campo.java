package model;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Classe che rappresenta un campo di una categoria.
 * Un campo è costituito da un nome e da un dominio di valori.
 * I valori possono essere associati a una descrizione.
 */
public class Campo {
    private String nome;
    private Set<Attivita> dominio; // <valore, descrizione>

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
    public Campo(String nome, HashSet<Attivita> dominio) {
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
     * Restituisce il dominio dei valori associati alle descrizioni.
     *
     * @return il dominio dei valori
     */
    public Set<Attivita> getDominio() {
        return this.dominio;
    }

    /**
     * Aggiunge un valore al dominio del campo.
     *
     * @param attivita il valore da aggiungere
     * @return true se il valore è stato aggiunto correttamente, false altrimenti
     */
    public boolean addAttivita(Attivita attivita) {
        if (attivita == null) {
            return false;
        }
        return this.dominio.add(attivita);
    }

    /**
     * Controlla se una attività è presente nel dominio del campo.
     * @param attivita l'attività da cercare
     * @return true se l'attività è presente, false altrimenti
     */
    public boolean hasAttivita(Attivita attivita) {
        return this.dominio.contains(attivita);
    }

    public boolean hasAttivita(String nome) {
        for (Attivita attivita : this.dominio) {
            if (attivita.getNome().equals(nome)) {
                return true;
            }
        }
        return false;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.nome).append(":\n");
        for (Attivita attivita : this.dominio) {
            sb.append("\t").append(attivita.toString()).append("\n");
        }
        return sb.toString();
    }

}