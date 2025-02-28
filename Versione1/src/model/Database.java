package model;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Classe che rappresenta il database del sistema.
 * Contiene e gestisce le collezioni di utenti, comprensori e gerarchie.
 * Fornisce funzionalità di persistenza dei dati attraverso la serializzazione.
 */
public class Database {
    // -----------------------------------------------------------------------------------
    // ATTRIBUTI
    // -----------------------------------------------------------------------------------

    /** Collezione degli utenti registrati nel sistema */
    private Set<Utente> utenti;

    /** Collezione dei comprensori geografici */
    private Set<Comprensorio> comprensori;

    /** Collezione delle gerarchie di categorie */
    private Set<Gerarchia> gerarchie;

    /** Oggetto di gestione dei fattori di conversione tra gerarchie */
    private ConversioneGerarchie conversioneGerarchie;

    // -----------------------------------------------------------------------------------
    // COSTRUTTORE
    // -----------------------------------------------------------------------------------

    /**
     * Costruttore della classe Database.
     * Inizializza le collezioni di utenti, comprensori e gerarchie.
     */
    public Database() {
        this.utenti = new HashSet<>();
        this.comprensori = new HashSet<>();
        this.gerarchie = new HashSet<>();
        this.conversioneGerarchie = new ConversioneGerarchie();
    }

    // -----------------------------------------------------------------------------------
    // METODI PER LA GESTIONE DEGLI UTENTI
    // -----------------------------------------------------------------------------------

    /**
     * Aggiunge un utente al database.
     *
     * @param utente l'utente da aggiungere
     * @return true se l'utente è stato aggiunto correttamente, false se l'utente è
     *         già presente o null
     */
    public boolean addUtente(Utente utente) {
        if (utente == null) {
            return false;
        }
        return this.utenti.add(utente);
    }

    /**
     * Registra un utente nel database. L'utente può essere di tipo Utente (in
     * futuro Fruitore) o Configuratore.
     *
     * @param username lo username dell'Utente
     * @param password la password dell'Utente
     * @param isConfig true se l'utente è di tipo Configuratore, false altrimenti
     * @return true se l'utente è stato registrato correttamente, false altrimenti
     */
    public boolean registerUtente(String username, String password, boolean isConfig) {
        if (username == null || password == null || hasUtente(username)) {
            return false;
        }
        Utente utente = isConfig ? new Configuratore(username, password) : new Utente(username, password);
        return this.addUtente(utente);
    }

    /**
     * Effettua il login di un utente nel sistema.
     *
     * @param username lo username dell'Utente
     * @param password la password dell'Utente
     * @return l'utente autenticato se le credenziali sono corrette, null altrimenti
     */
    public Utente loginUtente(String username, String password) {
        if (username == null || password == null || this.utenti.isEmpty()) {
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
        if (username == null) {
            return false;
        }
        for (Utente utente : this.utenti) {
            if (utente.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Restituisce la collezione degli utenti nel database.
     * Per sicurezza, viene restituita una vista non modificabile della collezione.
     *
     * @return una vista non modificabile della collezione degli utenti
     */
    public Set<Utente> getUtenti() {
        return Collections.unmodifiableSet(this.utenti);
    }

    /**
     * Imposta la collezione degli utenti nel database.
     *
     * @param utenti la nuova collezione di utenti
     */
    public void setUtenti(Set<Utente> utenti) {
        this.utenti = utenti != null ? new HashSet<>(utenti) : new HashSet<>();
    }

    // -----------------------------------------------------------------------------------
    // METODI PER LA GESTIONE DEI COMPRENSORI
    // -----------------------------------------------------------------------------------

    /**
     * Aggiunge un comprensorio al database.
     *
     * @param comprensorio il comprensorio da aggiungere
     * @return true se il comprensorio è stato aggiunto correttamente, false se il
     *         comprensorio è già presente o è null
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
     * @param nome il nome del comprensorio (case insensitive)
     * @return true se il comprensorio è presente, false altrimenti
     */
    public boolean hasComprensorio(String nome) {
        if (nome == null) {
            return false;
        }
        for (Comprensorio c : this.comprensori) {
            if (c.getNome() != null && c.getNome().equalsIgnoreCase(nome)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Restituisce la collezione dei comprensori nel database.
     * Per sicurezza, viene restituita una vista non modificabile della collezione.
     *
     * @return una vista non modificabile della collezione dei comprensori
     */
    public Set<Comprensorio> getComprensori() {
        return Collections.unmodifiableSet(this.comprensori);
    }

    /**
     * Imposta la collezione dei comprensori nel database.
     *
     * @param comprensori la nuova collezione di comprensori
     */
    public void setComprensori(Set<Comprensorio> comprensori) {
        this.comprensori = comprensori != null ? new HashSet<>(comprensori) : new HashSet<>();
    }

    /**
     * Restituisce una rappresentazione in stringa dei comprensori nel database.
     *
     * @return una stringa che rappresenta i comprensori
     */
    public String toStringComprensori() {
        if (comprensori.isEmpty()) {
            return "Nessun comprensorio presente.";
        }

        StringBuilder sb = new StringBuilder();
        for (Comprensorio c : this.comprensori) {
            sb.append(c.toString()).append("\n\n");
        }
        return sb.toString();
    }

    // -----------------------------------------------------------------------------------
    // METODI PER LA GESTIONE DELLE GERARCHIE
    // -----------------------------------------------------------------------------------

    /**
     * Aggiunge una gerarchia al database.
     *
     * @param gerarchia la gerarchia da aggiungere
     * @return true se la gerarchia è stata aggiunta correttamente, false se la
     *         gerarchia è già presente o null
     */
    public boolean addGerarchia(Gerarchia gerarchia) {
        if (gerarchia == null) {
            return false;
        }
        return this.gerarchie.add(gerarchia);
    }

    /**
     * Verifica se una gerarchia è presente nel database.
     *
     * @param nome il nome della categoria radice della gerarchia
     * @return true se la gerarchia è presente, false altrimenti
     */
    public boolean hasGerarchia(String nome) {
        if (nome == null) {
            return false;
        }
        for (Gerarchia g : this.gerarchie) {
            Categoria c = g.root().data();
            if (c != null && c.getNome().equals(nome)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Restituisce la collezione delle gerarchie nel database.
     * Per sicurezza, viene restituita una vista non modificabile della collezione.
     *
     * @return una vista non modificabile della collezione delle gerarchie
     */
    public Set<Gerarchia> getGerarchie() {
        return Collections.unmodifiableSet(this.gerarchie);
    }

    /**
     * Imposta la collezione delle gerarchie nel database.
     *
     * @param gerarchie la nuova collezione di gerarchie
     */
    public void setGerarchie(Set<Gerarchia> gerarchie) {
        this.gerarchie = gerarchie != null ? new HashSet<>(gerarchie) : new HashSet<>();
    }

    /**
     * Restituisce una gerarchia specifica in base alla posizione nella lista.
     *
     * @param index l'indice della gerarchia da recuperare
     * @return la gerarchia corrispondente all'indice specificato
     * @throws IndexOutOfBoundsException se l'indice è fuori dai limiti
     */
    public Gerarchia getGerarchia(int index) {
        ArrayList<Gerarchia> gerarchieList = new ArrayList<>(this.gerarchie);
        return gerarchieList.get(index);
    }

    /**
     * Restituisce una rappresentazione in stringa delle gerarchie nel database.
     *
     * @return una stringa che rappresenta le gerarchie
     */
    public String toStringGerarchie() {
        if (gerarchie.isEmpty()) {
            return "Nessuna gerarchia presente.";
        }

        StringBuilder sb = new StringBuilder();
        for (Gerarchia g : this.gerarchie) {
            sb.append(g).append("\n\n");
        }
        return sb.toString();
    }

    // -----------------------------------------------------------------------------------
    // METODI PER LA GESTIONE DELLE CONVERSIONI TRA GERARCHIE
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce l'oggetto di gestione delle conversioni tra gerarchie.
     *
     * @return l'oggetto ConversioneGerarchie
     */
    public ConversioneGerarchie getConversioneGerarchie() {
        return this.conversioneGerarchie;
    }

    // -----------------------------------------------------------------------------------
    // METODI DI SERIALIZZAZIONE E DESERIALIZZAZIONE
    // -----------------------------------------------------------------------------------

    /**
     * Serializza la collezione degli utenti nel file specificato.
     *
     * @param filePath il percorso del file in cui salvare gli utenti
     */
    public void serializeUtenti(String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(this.utenti);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deserializza la collezione degli utenti dal file specificato.
     *
     * @param filePath il percorso del file da cui caricare gli utenti
     */
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

    /**
     * Serializza la collezione dei comprensori nel file specificato.
     *
     * @param filePath il percorso del file in cui salvare i comprensori
     */
    public void serializeComprensori(String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(this.comprensori);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deserializza la collezione dei comprensori dal file specificato.
     *
     * @param filePath il percorso del file da cui caricare i comprensori
     */
    public void deserializeComprensori(String filePath) {
        try {
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

    /**
     * Serializza la collezione delle gerarchie nel file specificato.
     *
     * @param filePath il percorso del file in cui salvare le gerarchie
     */
    public void serializeGerarchie(String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(this.gerarchie);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deserializza la collezione delle gerarchie dal file specificato.
     *
     * @param filePath il percorso del file da cui caricare le gerarchie
     */
    public void deserializeGerarchie(String filePath) {
        try {
            FileInputStream fis = new FileInputStream(filePath);
            if (fis.available() == 0) {
                return;
            }
            ObjectInputStream ois = new ObjectInputStream(fis);
            this.gerarchie = (Set<Gerarchia>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Serializza l'oggetto di gestione delle conversioni tra gerarchie nel file
     * specificato.
     *
     * @param filePath il percorso del file in cui salvare le conversioni
     */
    public void serializeConversioneGerarchie(String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(this.conversioneGerarchie);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deserializza l'oggetto di gestione delle conversioni tra gerarchie dal file
     * specificato.
     *
     * @param filePath il percorso del file da cui caricare le conversioni
     */
    public void deserializeConversioneGerarchie(String filePath) {
        try {
            FileInputStream fis = new FileInputStream(filePath);
            if (fis.available() == 0) {
                return;
            }
            ObjectInputStream ois = new ObjectInputStream(fis);
            this.conversioneGerarchie = (ConversioneGerarchie) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}