package model;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Classe che rappresenta il database del sistema.
 * Contiene le liste di utenti, comprensori e gerarchie.
 */
public class Database {
    private Set<Utente> utenti;
    private Set<Comprensorio> comprensori;
    private Set<Gerarchia> gerarchie;

    /**
     * Costruttore della classe Database.
     * Inizializza le liste di utenti, comprensori e gerarchie.
     */
    public Database() {
        this.utenti = new HashSet<>();
        this.comprensori = new HashSet<>();
        this.gerarchie = new HashSet<>();
    }

    /**
     * Aggiunge un utente al database.
     *
     * @param utente l'utente da aggiungere
     * @return true se l'utente è stato aggiunto correttamente, false se l'utente è già presente
     */
    public boolean addUtente(Utente utente) {
        if (utente == null) {
            return false;
        }
        return this.utenti.add(utente);
    }

    /**
     * Restituisce la lista degli utenti nel database.
     *
     * @return la lista degli utenti
     */
    public Set<Utente> getUtenti() {
        return this.utenti;
    }

    /**
     * Imposta la lista degli utenti nel database.
     *
     * @param utenti
     */
    public void setUtenti(Set<Utente> utenti) {
        this.utenti = utenti;
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
        return this.comprensori.add(comprensorio);
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
    public Set<Comprensorio> getComprensori() {
        return this.comprensori;
    }

    /**
     * Imposta la lista dei comprensori nel database.
     *
     * @param comprensori
     */
    public void setComprensori(Set<Comprensorio> comprensori) {
        this.comprensori = comprensori;
    }

    /**
     * Aggiunge una gerarchia al database.
     *
     * @param gerarchia la gerarchia da aggiungere
     * @return true se la gerarchia è stata aggiunta correttamente, false se la gerarchia è già presente
     */
    public boolean addGerarchia(Gerarchia gerarchia) {
        if (gerarchia == null) {
            return false;
        }
        return this.gerarchie.add(gerarchia);
    }

    /**
     * Restituisce la lista delle gerarchie nel database.
     *
     * @return la lista delle gerarchie
     */
    public Set<Gerarchia> getGerarchie() {
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
        return this.addUtente(utente);
    }

    /**
     * Imposta la lista delle gerarchie nel database.
     *
     * @param gerarchie
     */
    public void setGerarchie(Set<Gerarchia> gerarchie) {
        this.gerarchie = gerarchie;
    }

    public boolean hasGerarchia (String nome) {
        for (Gerarchia g : this.gerarchie) {
            Categoria c = (Categoria) g.root().data();
            if (c.getNome().equals(nome)) {
                return true;
            }
        }
        return false;
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

    /**
     * Restituisce una rappresentazione in stringa delle gerarchie nel database.
     *
     * @return una stringa che rappresenta le gerarchie
     */
    public String toStringGerarchie() {
        StringBuilder sb = new StringBuilder();
        for (Gerarchia c : this.gerarchie) {
            sb.append(c.toString()).append("\n\n");
        }
        return sb.toString();
    }


    public void serializeUtenti(String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(this.utenti);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deserializeUtenti(String filePath) {
        try {
            FileInputStream fis = new FileInputStream(filePath);
            if (fis.available() == 0) {
                return;
            }
            ObjectInputStream ois = new ObjectInputStream(fis);
            this.utenti = (Set<Utente>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void serializeComprensori(String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(this.comprensori);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deserializeComprensori(String filePath) {
        try  {
            FileInputStream fis = new FileInputStream(filePath);
            if (fis.available() == 0) {
                return;
            }
            ObjectInputStream ois = new ObjectInputStream(fis);
            this.comprensori = (Set<Comprensorio>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}