package model;

/**
 * Classe che rappresenta un'attività.
 * Un'attività è costituita da un nome e da una descrizione opzionale.
 */
public class Attivita {
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