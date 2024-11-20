package model;

import java.util.ArrayList;

/**
 * Classe che rappresenta un comprensorio geografico
 * Un comprensorio geografico è costituito da un nome e da una lista di comuni
 * I comuni possono essere aggiunti o rimossi dalla lista
 */
public class Comprensorio {
    private String nome;
    private ArrayList<String> comuni;

    public Comprensorio(String nome){
        this.nome = nome;
        comuni = new ArrayList<String>();
    }

    /**
     * Aggiunge un comune al comprensorio se non è già presente nella lista dei comuni
     * @param comune
     * @return true se il comune è stato aggiunto, false altrimenti
     */
    public boolean addComune(String comune){
        for (String c : this.comuni){
            if (c.equals(comune)){
                return false;
            }
        }
        this.comuni.add(comune);
        return true;
    }

    /**
     * Rimuove un comune dal comprensorio se è presente nella lista dei comuni
     * @param comune
     * @return true se il comune è stato rimosso, false altrimenti
     */
    public boolean removeComune(String comune){
        for (String c : this.comuni){
            if (c.equals(comune)){
                this.comuni.remove(c);
                return true;
            }
        }
        return false;
    }

    public String getNome(){
        return this.nome;
    }

    public ArrayList<String> getComuni(){
        return this.comuni;
    }

    public void setNome(String nome){
        this.nome = nome;
    }

    @Override
    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append("Nome: " + this.nome + " Comuni: ");
        for (String c : this.comuni){
            sb.append(c + " ");
        }
        return sb.toString();
    }
}