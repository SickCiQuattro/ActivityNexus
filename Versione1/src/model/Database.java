package model;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Repository centrale per tutti i dati dell'applicazione.
 * <p>
 * Gestisce:
 * <ul>
 * <li>Utenti (Configuratori e Fruitori)</li>
 * <li>Comprensori geografici e relativi comuni</li>
 * <li>Gerarchie di categorie</li>
 * <li>Fattori di conversione tra categorie</li>
 * </ul>
 * <p>
 * Fornisce funzionalità CRUD complete e persistenza dei dati tramite
 * serializzazione.
 */
public class Database {
    // Collezioni principali di entità
    private Set<Utente> utenti;
    private Set<Comprensorio> comprensori;
    private Set<Gerarchia> gerarchie;
    private ConversioneGerarchie conversioneGerarchie;

    /**
     * Inizializza un database vuoto con tutte le collezioni pronte per l'uso.
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
     * @return true se l'utente è stato aggiunto, false se già presente o null
     */
    public boolean addUtente(Utente utente) {
        // Verifica che l'utente non sia null prima di tentare di aggiungerlo
        if (utente == null) {
            return false;
        }
        // Tenta di aggiungere l'utente alla collezione
        // HashSet.add() restituisce false se l'elemento esiste già
        return this.utenti.add(utente);
    }

    /**
     * Registra un nuovo utente nel database.
     *
     * @param username lo username dell'utente (deve essere univoco)
     * @param password la password dell'utente
     * @param isConfig true per creare un Configuratore, false per un Utente
     *                 standard
     * @return true se registrato con successo, false altrimenti
     */
    public boolean registerUtente(String username, String password, boolean isConfig) {
        // Verifica dei parametri e unicità dello username
        if (username == null || password == null || hasUtente(username)) {
            return false;
        }

        // Crea il tipo appropriato di utente in base al flag isConfig
        Utente utente = isConfig ? new Configuratore(username, password) : new Utente(username, password);

        // Tenta di aggiungere l'utente al database
        return this.addUtente(utente);
    }

    /**
     * Effettua il login verificando le credenziali fornite.
     *
     * @param username lo username dell'utente
     * @param password la password dell'utente
     * @return l'utente autenticato, o null se credenziali errate
     */
    public Utente loginUtente(String username, String password) {
        // Verifica parametri di base e se ci sono utenti
        if (username == null || password == null || this.utenti.isEmpty()) {
            return null;
        }

        // Itera su tutti gli utenti per trovare corrispondenza con le credenziali
        for (Utente utente : this.utenti) {
            if (utente.verificaCredenziali(username, password)) {
                return utente; // Restituisce l'utente se le credenziali corrispondono
            }
        }

        // Nessuna corrispondenza trovata
        return null;
    }

    /**
     * Verifica se esiste un utente con lo username specificato.
     *
     * @param username lo username da verificare (case-sensitive)
     * @return true se lo username esiste, false altrimenti
     */
    public boolean hasUtente(String username) {
        // Verifica che lo username non sia null
        if (username == null) {
            return false;
        }

        // Cerca tra tutti gli utenti uno con lo username corrispondente
        for (Utente utente : this.utenti) {
            if (utente.getUsername().equals(username)) { // Confronto case-sensitive
                return true;
            }
        }

        // Nessuna corrispondenza trovata
        return false;
    }

    /**
     * Restituisce una vista non modificabile della collezione degli utenti.
     *
     * @return vista non modificabile degli utenti
     */
    public Set<Utente> getUtenti() {
        // Restituisce una vista non modificabile della collezione per proteggere
        // l'incapsulamento
        return Collections.unmodifiableSet(this.utenti);
    }

    /**
     * Imposta la collezione degli utenti, creando una copia difensiva.
     *
     * @param utenti la nuova collezione di utenti
     */
    public void setUtenti(Set<Utente> utenti) {
        // Crea una copia difensiva della collezione o una nuova collezione vuota se
        // null
        this.utenti = utenti != null ? new HashSet<>(utenti) : new HashSet<>();
    }

    // -----------------------------------------------------------------------------------
    // METODI PER LA GESTIONE DEI COMPRENSORI
    // -----------------------------------------------------------------------------------

    /**
     * Aggiunge un comprensorio al database.
     *
     * @param comprensorio il comprensorio da aggiungere
     * @return true se aggiunto con successo, false se già presente o null
     */
    public boolean addComprensorio(Comprensorio comprensorio) {
        // Verifica che il comprensorio non sia null
        if (comprensorio == null) {
            return false;
        }

        // Tenta di aggiungere il comprensorio alla collezione
        return this.comprensori.add(comprensorio);
    }

    /**
     * Verifica se esiste un comprensorio con il nome specificato.
     *
     * @param nome il nome del comprensorio (case-insensitive)
     * @return true se esiste, false altrimenti
     */
    public boolean hasComprensorio(String nome) {
        // Verifica che il nome non sia null
        if (nome == null) {
            return false;
        }

        // Cerca tra tutti i comprensori uno con il nome corrispondente
        for (Comprensorio c : this.comprensori) {
            // Confronto case-insensitive, con gestione dei nomi null
            if (c.getNome() != null && c.getNome().equalsIgnoreCase(nome)) {
                return true;
            }
        }

        // Nessuna corrispondenza trovata
        return false;
    }

    /**
     * Restituisce una vista non modificabile dei comprensori.
     *
     * @return vista non modificabile dei comprensori
     */
    public Set<Comprensorio> getComprensori() {
        // Restituisce una vista non modificabile della collezione
        return Collections.unmodifiableSet(this.comprensori);
    }

    /**
     * Imposta la collezione dei comprensori, creando una copia difensiva.
     *
     * @param comprensori la nuova collezione di comprensori
     */
    public void setComprensori(Set<Comprensorio> comprensori) {
        // Crea una copia difensiva della collezione o una nuova collezione vuota se
        // null
        this.comprensori = comprensori != null ? new HashSet<>(comprensori) : new HashSet<>();
    }

    /**
     * Restituisce una rappresentazione testuale di tutti i comprensori.
     *
     * @return stringa formattata con tutti i comprensori
     */
    public String toStringComprensori() {
        // Gestisce il caso in cui non ci sono comprensori
        if (comprensori.isEmpty()) {
            return "Nessun comprensorio presente.";
        }

        // Costruisce la rappresentazione in stringa di tutti i comprensori
        StringBuilder sb = new StringBuilder();
        for (Comprensorio c : this.comprensori) {
            sb.append(c.toString()).append("\n");
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
     * @return true se aggiunta con successo, false se già presente o null
     */
    public boolean addGerarchia(Gerarchia gerarchia) {
        // Verifica che la gerarchia non sia null
        if (gerarchia == null) {
            return false;
        }

        // Tenta di aggiungere la gerarchia alla collezione
        return this.gerarchie.add(gerarchia);
    }

    /**
     * Verifica se esiste una gerarchia con la radice specificata.
     *
     * @param nome il nome della categoria radice (case-sensitive)
     * @return true se esiste, false altrimenti
     */
    public boolean hasGerarchia(String nome) {
        // Verifica che il nome non sia null
        if (nome == null) {
            return false;
        }

        // Cerca tra tutte le gerarchie una con la radice corrispondente
        for (Gerarchia g : this.gerarchie) {
            Categoria c = g.root().data();
            if (c != null && c.getNome().equals(nome)) { // Confronto case-sensitive
                return true;
            }
        }

        // Nessuna corrispondenza trovata
        return false;
    }

    /**
     * Restituisce una vista non modificabile delle gerarchie.
     *
     * @return vista non modificabile delle gerarchie
     */
    public Set<Gerarchia> getGerarchie() {
        // Restituisce una vista non modificabile della collezione
        return Collections.unmodifiableSet(this.gerarchie);
    }

    /**
     * Imposta la collezione delle gerarchie, creando una copia difensiva.
     *
     * @param gerarchie la nuova collezione di gerarchie
     */
    public void setGerarchie(Set<Gerarchia> gerarchie) {
        // Crea una copia difensiva della collezione o una nuova collezione vuota se
        // null
        this.gerarchie = gerarchie != null ? new HashSet<>(gerarchie) : new HashSet<>();
    }

    /**
     * Restituisce una gerarchia specifica in base alla posizione.
     *
     * @param index l'indice della gerarchia (0-based)
     * @return la gerarchia all'indice specificato
     * @throws IndexOutOfBoundsException se l'indice non è valido
     */
    public Gerarchia getGerarchia(int index) {
        // Converte il set in una lista per accedere per indice
        ArrayList<Gerarchia> gerarchieList = new ArrayList<>(this.gerarchie);
        // Restituisce l'elemento all'indice specifico
        // get() lancerà IndexOutOfBoundsException se l'indice non è valido
        return gerarchieList.get(index);
    }

    /**
     * Restituisce una rappresentazione testuale di tutte le gerarchie.
     *
     * @return stringa formattata con tutte le gerarchie
     */
    public String toStringGerarchie() {
        // Gestisce il caso in cui non ci sono gerarchie
        if (gerarchie.isEmpty()) {
            return "Nessuna gerarchia presente.";
        }

        // Costruisce la rappresentazione in stringa di tutte le gerarchie
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
     * @return l'oggetto di conversione tra categorie
     */
    public ConversioneGerarchie getConversioneGerarchie() {
        return this.conversioneGerarchie;
    }

    // -----------------------------------------------------------------------------------
    // METODI DI SERIALIZZAZIONE E DESERIALIZZAZIONE
    // -----------------------------------------------------------------------------------

    /**
     * Serializza gli utenti nel file specificato.
     *
     * @param filePath il percorso del file
     */
    public void serializeUtenti(String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            // Scrive l'intero Set di utenti come un oggetto serializzato
            oos.writeObject(this.utenti);
        } catch (IOException e) {
            // Stampa lo stack trace in caso di errore di I/O
            e.printStackTrace();
        }
    }

    /**
     * Carica gli utenti dal file specificato.
     *
     * @param filePath il percorso del file
     */
    public void deserializeUtenti(String filePath) {
        try {
            // Apre un FileInputStream per il file specificato
            FileInputStream fis = new FileInputStream(filePath);

            // Verifica se il file è vuoto
            if (fis.available() == 0) {
                return; // File vuoto, niente da deserializzare
            }

            // Crea un ObjectInputStream per leggere l'oggetto serializzato
            ObjectInputStream ois = new ObjectInputStream(fis);

            // Legge il Set di utenti dal file e lo assegna all'attributo
            this.utenti = (Set<Utente>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // Stampa lo stack trace in caso di errori di I/O o di deserializzazione
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
            // Apre un FileInputStream per il file specificato
            FileInputStream fis = new FileInputStream(filePath);

            // Verifica se il file è vuoto (dimensione zero)
            if (fis.available() == 0) {
                return; // File vuoto, niente da deserializzare
            }

            // Crea un ObjectInputStream per leggere l'oggetto serializzato
            ObjectInputStream ois = new ObjectInputStream(fis);

            // Legge il Set di comprensori dal file e lo assegna all'attributo
            // Il cast è necessario poiché readObject() restituisce Object generico
            this.comprensori = (Set<Comprensorio>) ois.readObject();

            // Nota: ois e fis non vengono chiusi esplicitamente, ma
            // verranno chiusi automaticamente dal garbage collector
        } catch (IOException | ClassNotFoundException e) {
            // Stampa lo stack trace in caso di errori di I/O o di deserializzazione
            // Errori tipici: file non trovato, formato non valido, classe non trovata
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
            // Apre un FileInputStream per il file specificato
            FileInputStream fis = new FileInputStream(filePath);

            // Verifica se il file è vuoto - se non contiene dati, esce dal metodo
            // senza modificare la collezione delle gerarchie esistente
            if (fis.available() == 0) {
                return;
            }

            // Crea un ObjectInputStream per leggere l'oggetto serializzato
            ObjectInputStream ois = new ObjectInputStream(fis);

            // Legge il Set di gerarchie dal file, eseguendo un cast al tipo corretto
            // Attenzione: readObject() può lanciare ClassNotFoundException se la classe
            // Gerarchia è cambiata in modo incompatibile rispetto alla versione
            // serializzata
            this.gerarchie = (Set<Gerarchia>) ois.readObject();

            // Gli stream vengono gestiti internamente nel blocco catch
            // e verranno chiusi automaticamente dal garbage collector
        } catch (IOException | ClassNotFoundException e) {
            // Gestione unificata delle eccezioni:
            // - IOException: errore di I/O (file non trovato, accesso negato, ecc.)
            // - ClassNotFoundException: la classe serializzata non corrisponde alla classe
            // attuale
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