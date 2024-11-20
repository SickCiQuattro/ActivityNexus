package model;

import java.util.HashMap;

/**
 * Classe che rappresenta un campo di una categoria
 * Un campo è costituito da un nome e da un dominio di valori
 * I valori possono essere associati a una descrizione
 */
public class Campo {
    private String nome;
    private HashMap<String, String> dominio;

    public Campo(String nome){
        this.nome = nome;
        this.dominio = new HashMap<String, String>();
    }

    public String getNome(){
        return this.nome;
    }

    public HashMap<String, String> getDominio(){
        return this.dominio;
    }

    /**
     * Aggiunge un valore al dominio del campo se non è già presente. Al valore può essere associata anche una descrizione
     * @param valore
     * @param descrizione
     * @return true se il valore è stato aggiunto, false altrimenti
     */
    public boolean addValore(String valore, String descrizione){
        if(this.dominio.containsKey(valore)){
            return false;
        }
        this.dominio.put(valore, descrizione);
        return true;
    }

    /**
     * Rimuove un valore dal dominio del campo se è presente
     * @param valore
     * @return true se il valore è stato rimosso, false altrimenti
     */
    public boolean removeValore(String valore){
        if(this.dominio.containsKey(valore)){
            this.dominio.remove(valore);
            return true;
        }
        return false;
    }
    @Override
    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append("Nome: " + this.nome + "\n");
        sb.append("Valori: \n");
        for (String valore : this.dominio.keySet()){
            sb.append(valore + " - " + this.dominio.get(valore) + "\n");
        }
        return sb.toString();
    }
}
