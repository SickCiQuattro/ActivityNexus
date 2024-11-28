package model;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Classe che rappresenta il database del sistema.
 * Contiene le liste di utenti, comprensori e gerarchie.
 */
public class Database implements Serializable {
    private static final long serialVersionUID = 1L;
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
        return utente != null && this.utenti.add(utente);
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
     * Aggiunge un comprensorio al database.
     *
     * @param comprensorio il comprensorio da aggiungere
     * @return true se il comprensorio è stato aggiunto correttamente, false se il comprensorio è già presente o è null
     */
    public boolean addComprensorio(Comprensorio comprensorio) {
        return comprensorio != null && this.comprensori.add(comprensorio);
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
     * Aggiunge una gerarchia al database.
     *
     * @param gerarchia la gerarchia da aggiungere
     * @return true se la gerarchia è stata aggiunta correttamente, false se la gerarchia è già presente
     */
    public boolean addGerarchia(Gerarchia gerarchia) {
        return gerarchia != null && this.gerarchie.add(gerarchia);
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

    public void serialize(String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(this.utenti);
            oos.writeObject(this.comprensori);
            oos.writeObject(this.gerarchie);
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error during serialization: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static Database deserialize(String filePath) {
        Database db = new Database();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            db.utenti = (Set<Utente>) ois.readObject();
            db.comprensori = (Set<Comprensorio>) ois.readObject();
            db.gerarchie = (Set<Gerarchia>) ois.readObject();
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error during deserialization: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Class not found: " + e.getMessage());
        }
        return db;
    }
}