package model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Classe che rappresenta un comprensorio geografico.
 * Un comprensorio geografico è costituito da un nome e da una lista di comuni.
 * I comuni possono essere aggiunti o rimossi dalla lista.
 */
public class Comprensorio implements Serializable {
    private String nome;
    private ArrayList<String> comuni;

    /**
     * Costruisce un Comprensorio con il nome specificato.
     * Inizializza la lista dei comuni come una ArrayList vuota.
     *
     * @param nome il nome del comprensorio
     */
    public Comprensorio(String nome) {
        this.nome = nome;
        this.comuni = new ArrayList<>();
    }

    /**
     * Aggiunge un comune al comprensorio se non è già presente nella lista dei comuni.
     *
     * @param comune il nome del comune da aggiungere
     * @return true se il comune è stato aggiunto, false altrimenti
     */
    public boolean addComune(String comune) {
        for (String c : this.comuni) {
            if (c.equals(comune)) {
                return false;
            }
        }
        this.comuni.add(comune);
        return true;
    }

    /**
     * Rimuove un comune dal comprensorio se è presente nella lista dei comuni.
     *
     * @param comune il nome del comune da rimuovere
     * @return true se il comune è stato rimosso, false altrimenti
     */
    public boolean removeComune(String comune) {
        for (String c : this.comuni) {
            if (c.equals(comune)) {
                this.comuni.remove(c);
                return true;
            }
        }
        return false;
    }

    /**
     * Restituisce il nome del comprensorio.
     *
     * @return il nome del comprensorio
     */
    public String getNome() {
        return this.nome;
    }

    /**
     * Restituisce la lista dei comuni del comprensorio.
     *
     * @return la lista dei comuni
     */
    public ArrayList<String> getComuni() {
        return this.comuni;
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
     * Restituisce una rappresentazione in stringa del comprensorio.
     * La stringa include il nome del comprensorio e la lista dei comuni.
     *
     * @return una rappresentazione in stringa del comprensorio
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Nome: ").append(this.nome).append(" Comuni: ");
        for (String c : this.comuni) {
            sb.append(c).append(" ");
        }
        return sb.toString();
    }

    /**
     * Confronta questo comprensorio con l'oggetto specificato.
     * Il risultato è true se e solo se l'argomento non è null ed è un oggetto Comprensorio che ha lo stesso nome di questo oggetto.
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
        return nome.equals(comprensorio.nome);
    }
}