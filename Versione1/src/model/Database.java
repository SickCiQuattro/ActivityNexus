package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe che rappresenta il database del sistema.
 * Contiene le liste di utenti, comprensori e gerarchie.
 */
public class Database {
    private List<Utente> utenti;
    private List<Comprensorio> comprensori;
    private List<Gerarchia> gerarchie;

    /**
     * Costruttore della classe Database.
     * Inizializza le liste di utenti, comprensori e gerarchie.
     */
    public Database() {
        this.utenti = new ArrayList<>();
        this.comprensori = new ArrayList<>();
        this.gerarchie = new ArrayList<>();
    }

    /**
     * Aggiunge un utente al database.
     *
     * @param utente l'utente da aggiungere
     * @return true se l'utente è stato aggiunto correttamente, false se l'utente è già presente
     */
    public boolean addUtente(Utente utente) {
        if(!hasUtente(utente.getUsername())) {
            this.utenti.add(utente);
            return true;
        }
        return false;
    }

    /**
     * Restituisce la lista degli utenti nel database.
     *
     * @return la lista degli utenti
     */
    public List<Utente> getUtenti() {
        return this.utenti;
    }

    /**
     * Aggiunge un comprensorio al database.
     *
     * @param comprensorio il comprensorio da aggiungere
     * @return true se il comprensorio è stato aggiunto correttamente, false se il comprensorio è già presente o è null
     */
    public boolean addComprensorio(Comprensorio comprensorio) {
        if (comprensorio == null) {
            return false;
        }

        if (hasComprensorio(comprensorio.getNome())) {
            return false;
        }

        this.comprensori.add(comprensorio);
        return true;
    }

    /**
     * Verifica se un comprensorio è presente nel database.
     *
     * @param nome il nome del comprensorio
     * @return true se il comprensorio è presente, false altrimenti
     */
    public boolean hasComprensorio(String nome) {
        for (Comprensorio c : this.comprensori) {
            if (c.getNome().equals(nome)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Restituisce la lista dei comprensori nel database.
     *
     * @return la lista dei comprensori
     */
    public List<Comprensorio> getComprensori() {
        return this.comprensori;
    }

    /**
     * Aggiunge una gerarchia al database.
     *
     * @param gerarchia la gerarchia da aggiungere
     * @return true se la gerarchia è stata aggiunta correttamente, false se la gerarchia è già presente
     */
    public boolean addGerarchia(Gerarchia gerarchia) {
        for (Gerarchia g : this.gerarchie) {
            if (g.equals(gerarchia)) {
                return false;
            }
        }
        this.gerarchie.add(gerarchia);
        return true;
    }

    /**
     * Restituisce la lista delle gerarchie nel database.
     *
     * @return la lista delle gerarchie
     */
    public List<Gerarchia> getGerarchie() {
        return this.gerarchie;
    }

    /**
     * Registra un utente nel database. L'utente può essere di tipo Utente (in futuro Fruitore) o Configuratore.
     *
     * @param username lo username dell'Utente
     * @param password la password dell'Utente
     * @param isConfig true se l'utente è di tipo Configuratore, false altrimenti
     * @return true se l'utente è stato registrato correttamente, false altrimenti
     */
    public boolean registerUtente(String username, String password, boolean isConfig) {
        Utente utente = isConfig ? new Configuratore(username, password) : new Utente(username, password);
        return addUtente(utente);
    }

    /**
     * Effettua il login di un utente nel sistema.
     *
     * @param username lo username dell'Utente
     * @param password la password dell'Utente
     * @return l'utente loggato se le credenziali sono corrette, null altrimenti
     */
    public Utente loginUtente(String username, String password) {
        if (this.utenti.isEmpty()) {
            return null;
        }

        for (Utente utente : this.utenti) {
            if (utente.verificaCredenziali(username, password)) {
                return utente;
            }
        }
        return null;
    }

    /**
     * Verifica se un utente è presente nel database.
     *
     * @param username lo username dell'utente
     * @return true se l'utente è presente, false altrimenti
     */
    public boolean hasUtente(String username) {
        for (Utente utente : this.utenti) {
            if (utente.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Restituisce una rappresentazione in stringa dei comprensori nel database.
     *
     * @return una stringa che rappresenta i comprensori
     */
    public String toStringComprensori() {
        StringBuilder sb = new StringBuilder();
        for (Comprensorio c : this.comprensori) {
            sb.append(c.toString()).append("\n\n");
        }
        return sb.toString();
    }
}