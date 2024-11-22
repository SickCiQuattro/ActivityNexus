package model;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Classe che rappresenta un campo di una categoria.
 * Un campo è costituito da un nome e da un dominio di valori.
 * I valori possono essere associati a una descrizione.
 */
public class Campo implements Serializable {
    private String nome;
    private HashMap<String, String> dominio; // <valore, descrizione>

    /**
     * Costruisce un Campo con il nome specificato.
     * Inizializza il dominio come un HashMap vuoto.
     *
     * @param nome il nome del campo
     */
    public Campo(String nome) {
        this.nome = nome;
        this.dominio = new HashMap<>();
    }

    /**
     * Costruisce un Campo con il nome e il dominio specificati.
     *
     * @param nome il nome del campo
     * @param dominio il dominio dei valori associati alle descrizioni
     */
    public Campo(String nome, HashMap<String, String> dominio) {
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
    public HashMap<String, String> getDominio() {
        return this.dominio;
    }

    /**
     * Aggiunge un valore al dominio del campo se non è già presente.
     * Al valore può essere associata anche una descrizione.
     *
     * @param valore il valore da aggiungere
     * @param descrizione la descrizione associata al valore
     * @return true se il valore è stato aggiunto, false altrimenti
     */
    public boolean addValore(String valore, String descrizione) {
        if (this.dominio.containsKey(valore)) {
            return false;
        }
        this.dominio.put(valore, descrizione);
        return true;
    }

    /**
     * Rimuove un valore dal dominio del campo se è presente.
     *
     * @param valore il valore da rimuovere
     * @return true se il valore è stato rimosso, false altrimenti
     */
    public boolean removeValore(String valore) {
        if (this.dominio.containsKey(valore)) {
            this.dominio.remove(valore);
            return true;
        }
        return false;
    }

    /**
     * Restituisce una rappresentazione in stringa del campo.
     * La stringa include il nome del campo e i valori nel dominio.
     *
     * @return una rappresentazione in stringa del campo
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Nome: ").append(this.nome).append("\n");
        sb.append("Valori: \n");
        for (String valore : this.dominio.keySet()) {
            sb.append(valore).append(" - ").append(this.dominio.get(valore)).append("\n");
        }
        return sb.toString();
    }

    /**
    * Confronta questo campo con l'oggetto specificato.
    * Il risultato è true se e solo se l'argomento non è null ed è un oggetto Campo che ha lo stesso nome di questo oggetto.
    *
    * @param obj l'oggetto da confrontare con questo Campo
    * @return true se l'oggetto è uguale a questo Campo, false altrimenti
    **/
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Campo campo = (Campo) obj;
        return nome.equals(campo.nome);
    }
}