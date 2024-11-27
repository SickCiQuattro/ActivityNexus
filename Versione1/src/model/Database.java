package model;

import java.util.ArrayList;
import java.util.List;

public class Database {
    private List<Utente> utenti;
    private List<Comprensorio> comprensori;
    private List<Gerarchia> gerarchie;

    public Database() {
        this.utenti = new ArrayList<>();
        this.comprensori = new ArrayList<>();
        this.gerarchie = new ArrayList<>();
    }

    public boolean addUtente(Utente utente) {
        if(!hasUtente(utente.getUsername())) {
            this.utenti.add(utente);
            return true;
        }
        return false;
    }

    public List<Utente> getUtenti() {
        return this.utenti;
    }

    public boolean addComprensorio(Comprensorio comprensorio) {
        for (Comprensorio c : this.comprensori) {
            if (c.getNome().equals(comprensorio.getNome())) {
                return false;
            }
        }
        this.comprensori.add(comprensorio);
        return true;
    }

    public List<Comprensorio> getComprensori() {
        return this.comprensori;
    }

    public boolean addGerarchia(Gerarchia gerarchia) {
        for (Gerarchia g : this.gerarchie) {
            if (g.equals(gerarchia)) {
                return false;
            }
        }
        this.gerarchie.add(gerarchia);
        return true;
    }

    public List<Gerarchia> getGerarchie() {
        return this.gerarchie;
    }

    /**
     * Registra un utente nel database. L'utente può essere di tipo Utente (in futuro Fruitore) o Configuratore.
     * @param username  lo username dell'Utente
     * @param password la password dell'Utente
     * @param isConfig true se l'utente è di tipo Configuratore, false altrimenti
     * @return true se l'utente è stato registrato correttamente, false altrimenti
     */
    public boolean registerUtente(String username, String password, boolean isConfig) {
        if (isConfig) {
            return addUtente(new Configuratore(username, password));
        }
        return addUtente(new Utente(username, password));
    }

    /**
     * Effettua il login di un utente nel sistema.
     * @param username lo username dell'Utente
     * @param password la password dell'Utente
     * @return true se l'utente è stato loggato corretamente ed è di tipo Configuratore,
     * false se l'utente è stato loggato correttamente ed è di tipo Utente (in futuro Fruitore),
     * null se l'utente non è presente nel database.
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

    public boolean hasUtente(String username) {
        for (Utente utente : this.utenti) {
            if (utente.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }
}