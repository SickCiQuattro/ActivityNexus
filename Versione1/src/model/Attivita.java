package model;

public class Attivita {
    private String nome;
    private String descrizione = "";

    public Attivita(String nome) {
        this.nome = nome;
    }

    public Attivita(String nome, String descrizione) {
        this.nome = nome;
        this.descrizione = descrizione;
    }

    /**
     * Restituisce il nome dell'attività.
     * @return il nome dell'attività
     */
    public String getNome() {
        return this.nome;
    }

    /**
     * Restituisce la descrizione dell'attività.
     * @return la descrizione dell'attività
     */
    public String getDescrizione() {
        return this.descrizione;
    }

    @Override
    public String toString() {
        return this.nome + (this.descrizione.isEmpty() ? "" : " - " + this.descrizione);
    }

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
