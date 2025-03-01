package model;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Classe che rappresenta il database centrale del sistema.
 * <p>
 * Questa classe implementa il pattern Repository e agisce come punto
 * centralizzato di
 * accesso e gestione di tutti i dati dell'applicazione. Gestisce le seguenti
 * entità:
 * <ul>
 * <li>Utenti: gestione degli account (Configuratori e Fruitori)</li>
 * <li>Comprensori: aree geografiche e relativi comuni</li>
 * <li>Gerarchie: strutture organizzative di categorie e attività</li>
 * <li>Conversioni: fattori di equivalenza tra categorie di gerarchie
 * diverse</li>
 * </ul>
 * <p>
 * Il Database fornisce funzionalità complete per:
 * <ul>
 * <li>Aggiungere, verificare e recuperare entità</li>
 * <li>Persistere i dati tramite serializzazione Java</li>
 * <li>Caricare dati precedentemente salvati</li>
 * <li>Ottenere rappresentazioni testuali delle collezioni</li>
 * </ul>
 * <p>
 * Tutte le collezioni sono implementate come {@link Set}, garantendo l'unicità
 * degli
 * elementi in base ai loro metodi equals() e hashCode().
 *
 * @see Utente
 * @see Configuratore
 * @see Comprensorio
 * @see Gerarchia
 * @see ConversioneGerarchie
 */
public class Database {
    // -----------------------------------------------------------------------------------
    // ATTRIBUTI
    // -----------------------------------------------------------------------------------

    /**
     * Collezione degli utenti registrati nel sistema
     * Implementata come HashSet per garantire l'unicità degli utenti in base allo
     * username
     */
    private Set<Utente> utenti;

    /**
     * Collezione dei comprensori geografici
     * Implementata come HashSet per garantire l'unicità dei comprensori in base al
     * nome
     */
    private Set<Comprensorio> comprensori;

    /**
     * Collezione delle gerarchie di categorie
     * Implementata come HashSet per garantire l'unicità delle gerarchie in base al
     * nome della radice
     */
    private Set<Gerarchia> gerarchie;

    /**
     * Oggetto di gestione dei fattori di conversione tra gerarchie
     * Conserva i rapporti di equivalenza tra categorie di gerarchie diverse
     */
    private ConversioneGerarchie conversioneGerarchie;

    // -----------------------------------------------------------------------------------
    // COSTRUTTORE
    // -----------------------------------------------------------------------------------

    /**
     * Costruttore della classe Database.
     * <p>
     * Inizializza tutte le collezioni come HashSet vuoti, garantendo l'unicità
     * degli elementi.
     * Crea anche un nuovo oggetto ConversioneGerarchie per gestire i fattori di
     * conversione.
     * <p>
     * Il database è pronto per l'uso immediatamente dopo la creazione, ma sarà
     * vuoto
     * finché non vengono aggiunti elementi o caricati dati persistenti.
     */
    public Database() {
        // Inizializza tutte le collezioni come insiemi vuoti
        this.utenti = new HashSet<>();
        this.comprensori = new HashSet<>();
        this.gerarchie = new HashSet<>();
        // Crea un nuovo oggetto per gestire le conversioni tra gerarchie
        this.conversioneGerarchie = new ConversioneGerarchie();
    }

    // -----------------------------------------------------------------------------------
    // METODI PER LA GESTIONE DEGLI UTENTI
    // -----------------------------------------------------------------------------------

    /**
     * Aggiunge un utente al database.
     * <p>
     * L'unicità degli utenti è garantita dal metodo equals() della classe Utente,
     * che considera uguali due utenti con lo stesso username (case-sensitive).
     * <p>
     * Esempio di utilizzo:
     * 
     * <pre>
     * Utente nuovo = new Configuratore("admin", "password");
     * boolean aggiunto = database.addUtente(nuovo); // true se aggiunto con successo
     * </pre>
     *
     * @param utente l'utente da aggiungere al database
     * @return true se l'utente è stato aggiunto correttamente, false se l'utente è
     *         già presente (stesso username) o null
     * 
     * @see Utente#equals(Object)
     * @see #registerUtente(String, String, boolean)
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
     * Registra un nuovo utente nel database creando l'istanza appropriata.
     * <p>
     * Questo metodo semplifica la creazione di utenti, determinando automaticamente
     * se creare un Configuratore o un Utente standard in base al flag isConfig.
     * Verifica anche se esiste già un utente con lo stesso username.
     * <p>
     * Esempio di utilizzo:
     * 
     * <pre>
     * // Registra un nuovo configuratore
     * boolean registrato = database.registerUtente("admin", "password", true);
     * 
     * // Registra un fruitore (futuro, attualmente Utente standard)
     * boolean registrato = database.registerUtente("user", "password", false);
     * </pre>
     *
     * @param username lo username dell'Utente (deve essere univoco)
     * @param password la password dell'Utente
     * @param isConfig true se l'utente è di tipo Configuratore, false per un Utente
     *                 standard
     * @return true se l'utente è stato registrato correttamente, false se i
     *         parametri
     *         sono null o esiste già un utente con lo stesso username
     * 
     * @see #addUtente(Utente)
     * @see #hasUtente(String)
     * @see Configuratore
     * @see Utente
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
     * Effettua il login di un utente verificando le credenziali fornite.
     * <p>
     * Cerca tra tutti gli utenti registrati quello con le credenziali
     * corrispondenti.
     * Il controllo è case-sensitive sia per username che per password.
     * <p>
     * Esempio di utilizzo:
     * 
     * <pre>
     * Utente utenteLoggato = database.loginUtente("admin", "password");
     * if (utenteLoggato != null) {
     *     // Login riuscito, verifica il tipo di utente
     *     boolean isConfiguratore = utenteLoggato.isConfig();
     * }
     * </pre>
     *
     * @param username lo username dell'Utente
     * @param password la password dell'Utente
     * @return l'utente autenticato se le credenziali sono corrette, null se le
     *         credenziali
     *         sono errate, i parametri sono null, o non ci sono utenti nel database
     * 
     * @see Utente#verificaCredenziali(String, String)
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
     * Verifica se un utente con lo username specificato è presente nel database.
     * <p>
     * Il confronto degli username è case-sensitive.
     * <p>
     * Esempio di utilizzo:
     * 
     * <pre>
     * // Prima di registrare un nuovo utente, verifica che lo username sia
     * // disponibile
     * if (!database.hasUtente("nuovoutente")) {
     *     database.registerUtente("nuovoutente", "password", false);
     * }
     * </pre>
     *
     * @param username lo username dell'utente da cercare
     * @return true se esiste un utente con lo username specificato, false
     *         altrimenti
     *         o se lo username è null
     * 
     * @see #registerUtente(String, String, boolean)
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
     * <p>
     * Per ragioni di sicurezza e incapsulamento, la collezione restituita è di sola
     * lettura.
     * Qualsiasi tentativo di modificarla genererà un'eccezione
     * UnsupportedOperationException.
     * <p>
     * Per aggiungere utenti, utilizzare i metodi {@link #addUtente(Utente)} o
     * {@link #registerUtente(String, String, boolean)}.
     *
     * @return una vista non modificabile della collezione degli utenti
     * 
     * @see Collections#unmodifiableSet(Set)
     * @see #addUtente(Utente)
     * @see #registerUtente(String, String, boolean)
     */
    public Set<Utente> getUtenti() {
        // Restituisce una vista non modificabile della collezione per proteggere
        // l'incapsulamento
        return Collections.unmodifiableSet(this.utenti);
    }

    /**
     * Imposta la collezione degli utenti del database.
     * <p>
     * Questo metodo crea una copia difensiva della collezione fornita per garantire
     * l'incapsulamento. Se il parametro è null, viene creata una nuova collezione
     * vuota.
     * <p>
     * Attenzione: questo metodo sovrascrive completamente la collezione degli
     * utenti esistente.
     * Utilizzarlo con cautela, preferibilmente solo per inizializzazioni o
     * ripristini completi.
     *
     * @param utenti la nuova collezione di utenti da utilizzare
     * 
     * @see #getUtenti()
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
     * <p>
     * L'unicità dei comprensori è garantita dal metodo equals() della classe
     * Comprensorio,
     * che considera uguali due comprensori con lo stesso nome (case-insensitive).
     * <p>
     * Esempio di utilizzo:
     * 
     * <pre>
     * Comprensorio valtellina = new Comprensorio("Valtellina");
     * valtellina.addComune("Sondrio");
     * valtellina.addComune("Tirano");
     * database.addComprensorio(valtellina);
     * </pre>
     *
     * @param comprensorio il comprensorio da aggiungere
     * @return true se il comprensorio è stato aggiunto correttamente, false se il
     *         comprensorio è già presente (stesso nome) o null
     * 
     * @see Comprensorio#equals(Object)
     * @see #hasComprensorio(String)
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
     * Verifica se un comprensorio con il nome specificato è presente nel database.
     * <p>
     * Il confronto dei nomi è case-insensitive, quindi "Valtellina" e "valtellina"
     * sono considerati lo stesso comprensorio.
     * <p>
     * Esempio di utilizzo:
     * 
     * <pre>
     * if (!database.hasComprensorio("Lombardia")) {
     *     Comprensorio lombardia = new Comprensorio("Lombardia");
     *     database.addComprensorio(lombardia);
     * }
     * </pre>
     *
     * @param nome il nome del comprensorio da cercare (case-insensitive)
     * @return true se esiste un comprensorio con il nome specificato, false
     *         altrimenti
     *         o se il nome è null
     * 
     * @see #addComprensorio(Comprensorio)
     * @see Comprensorio#getNome()
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
     * Restituisce una vista non modificabile della collezione dei comprensori.
     * <p>
     * Per ragioni di sicurezza e incapsulamento, la collezione restituita è di sola
     * lettura.
     * Qualsiasi tentativo di modificarla genererà un'eccezione
     * UnsupportedOperationException.
     * <p>
     * Per aggiungere comprensori, utilizzare il metodo
     * {@link #addComprensorio(Comprensorio)}.
     *
     * @return una vista non modificabile della collezione dei comprensori
     * 
     * @see Collections#unmodifiableSet(Set)
     * @see #addComprensorio(Comprensorio)
     */
    public Set<Comprensorio> getComprensori() {
        // Restituisce una vista non modificabile della collezione
        return Collections.unmodifiableSet(this.comprensori);
    }

    /**
     * Imposta la collezione dei comprensori del database.
     * <p>
     * Questo metodo crea una copia difensiva della collezione fornita per garantire
     * l'incapsulamento. Se il parametro è null, viene creata una nuova collezione
     * vuota.
     * <p>
     * Attenzione: questo metodo sovrascrive completamente la collezione dei
     * comprensori esistente.
     * Utilizzarlo con cautela, preferibilmente solo per inizializzazioni o
     * ripristini completi.
     *
     * @param comprensori la nuova collezione di comprensori da utilizzare
     * 
     * @see #getComprensori()
     */
    public void setComprensori(Set<Comprensorio> comprensori) {
        // Crea una copia difensiva della collezione o una nuova collezione vuota se
        // null
        this.comprensori = comprensori != null ? new HashSet<>(comprensori) : new HashSet<>();
    }

    /**
     * Restituisce una rappresentazione testuale di tutti i comprensori nel
     * database.
     * <p>
     * Il formato include:
     * <ul>
     * <li>Un messaggio speciale se non ci sono comprensori</li>
     * <li>Altrimenti, la rappresentazione toString() di ogni comprensorio su più
     * righe</li>
     * </ul>
     * <p>
     * Questa stringa è pensata per visualizzazioni a console o rapporti testuali.
     *
     * @return una stringa formattata con la rappresentazione di tutti i comprensori
     * 
     * @see Comprensorio#toString()
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
     * <p>
     * L'unicità delle gerarchie è garantita dal metodo equals() della classe
     * Gerarchia,
     * che considera uguali due gerarchie con la stessa categoria radice.
     * <p>
     * Esempio di utilizzo:
     * 
     * <pre>
     * Categoria sportRoot = new Categoria("Sport", new Campo("Tipo"));
     * Gerarchia sportGerarchia = new Gerarchia(sportRoot);
     * database.addGerarchia(sportGerarchia);
     * </pre>
     *
     * @param gerarchia la gerarchia da aggiungere
     * @return true se la gerarchia è stata aggiunta correttamente, false se la
     *         gerarchia è già presente (stessa radice) o null
     * 
     * @see Gerarchia#equals(Object)
     * @see #hasGerarchia(String)
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
     * Verifica se una gerarchia con il nome specificato per la categoria radice è
     * presente nel database.
     * <p>
     * Il confronto è case-sensitive, quindi "Sport" e "sport" sono considerati
     * gerarchie diverse.
     * <p>
     * Esempio di utilizzo:
     * 
     * <pre>
     * if (!database.hasGerarchia("Sport")) {
     *     // Crea e aggiungi una nuova gerarchia "Sport"
     * }
     * </pre>
     *
     * @param nome il nome della categoria radice della gerarchia da cercare
     * @return true se esiste una gerarchia con la radice specificata, false
     *         altrimenti
     *         o se il nome è null
     * 
     * @see #addGerarchia(Gerarchia)
     * @see Gerarchia#root()
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
     * Restituisce una vista non modificabile della collezione delle gerarchie.
     * <p>
     * Per ragioni di sicurezza e incapsulamento, la collezione restituita è di sola
     * lettura.
     * Qualsiasi tentativo di modificarla genererà un'eccezione
     * UnsupportedOperationException.
     * <p>
     * Per aggiungere gerarchie, utilizzare il metodo
     * {@link #addGerarchia(Gerarchia)}.
     *
     * @return una vista non modificabile della collezione delle gerarchie
     * 
     * @see Collections#unmodifiableSet(Set)
     * @see #addGerarchia(Gerarchia)
     */
    public Set<Gerarchia> getGerarchie() {
        // Restituisce una vista non modificabile della collezione
        return Collections.unmodifiableSet(this.gerarchie);
    }

    /**
     * Imposta la collezione delle gerarchie del database.
     * <p>
     * Questo metodo crea una copia difensiva della collezione fornita per garantire
     * l'incapsulamento. Se il parametro è null, viene creata una nuova collezione
     * vuota.
     * <p>
     * Attenzione: questo metodo sovrascrive completamente la collezione delle
     * gerarchie esistente.
     * Utilizzarlo con cautela, preferibilmente solo per inizializzazioni o
     * ripristini completi.
     *
     * @param gerarchie la nuova collezione di gerarchie da utilizzare
     * 
     * @see #getGerarchie()
     */
    public void setGerarchie(Set<Gerarchia> gerarchie) {
        // Crea una copia difensiva della collezione o una nuova collezione vuota se
        // null
        this.gerarchie = gerarchie != null ? new HashSet<>(gerarchie) : new HashSet<>();
    }

    /**
     * Restituisce una gerarchia specifica in base alla posizione in una lista
     * ordinata.
     * <p>
     * Poiché le gerarchie sono memorizzate in un Set (non ordinato), questo metodo
     * converte temporaneamente il set in una lista per accedere all'elemento
     * all'indice specificato.
     * L'ordine della lista non è garantito essere sempre lo stesso in diverse
     * chiamate.
     * <p>
     * Esempio di utilizzo:
     * 
     * <pre>
     * // Ottenere la prima gerarchia (se esiste)
     * if (!database.getGerarchie().isEmpty()) {
     *     Gerarchia prima = database.getGerarchia(0);
     * }
     * </pre>
     *
     * @param index l'indice della gerarchia da recuperare (0-based)
     * @return la gerarchia corrispondente all'indice specificato
     * @throws IndexOutOfBoundsException se l'indice è negativo o maggiore/uguale al
     *                                   numero di gerarchie
     * 
     * @see #getGerarchie()
     */
    public Gerarchia getGerarchia(int index) {
        // Converte il set in una lista per accedere per indice
        ArrayList<Gerarchia> gerarchieList = new ArrayList<>(this.gerarchie);
        // Restituisce l'elemento all'indice specifico
        // get() lancerà IndexOutOfBoundsException se l'indice non è valido
        return gerarchieList.get(index);
    }

    /**
     * Restituisce una rappresentazione testuale di tutte le gerarchie nel database.
     * <p>
     * Il formato include:
     * <ul>
     * <li>Un messaggio speciale se non ci sono gerarchie</li>
     * <li>Altrimenti, la rappresentazione toString() di ogni gerarchia su più
     * righe</li>
     * </ul>
     * <p>
     * Questa stringa è pensata per visualizzazioni a console o rapporti testuali.
     *
     * @return una stringa formattata con la rappresentazione di tutte le gerarchie
     * 
     * @see Gerarchia#toString()
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
     * Restituisce l'oggetto che gestisce le conversioni tra categorie di gerarchie
     * diverse.
     * <p>
     * L'oggetto ConversioneGerarchie permette di definire e recuperare fattori di
     * conversione
     * tra categorie appartenenti a gerarchie diverse, consentendo di confrontare
     * attività
     * eterogenee in base alla loro equivalenza.
     * <p>
     * Esempio di utilizzo:
     * 
     * <pre>
     * ConversioneGerarchie conversione = database.getConversioneGerarchie();
     * // Definire un fattore di conversione: 1 ora di nuoto = 1.5 ore di corsa
     * conversione.addFattoreConversione(nuoto, corsa, 1.5);
     * </pre>
     *
     * @return l'oggetto ConversioneGerarchie del database
     * 
     * @see ConversioneGerarchie
     * @see ConversioneGerarchie#addFattoreConversione(Categoria, Categoria, double)
     * @see ConversioneGerarchie#getFattoreConversione(Categoria, Categoria)
     */
    public ConversioneGerarchie getConversioneGerarchie() {
        return this.conversioneGerarchie;
    }

    // -----------------------------------------------------------------------------------
    // METODI DI SERIALIZZAZIONE E DESERIALIZZAZIONE
    // -----------------------------------------------------------------------------------

    /**
     * Serializza la collezione degli utenti nel file specificato.
     * <p>
     * Utilizza ObjectOutputStream per salvare l'intero Set di utenti come un
     * oggetto
     * serializzato. In caso di errore durante il salvataggio, stampa lo stack trace
     * dell'eccezione ma non la propaga.
     * <p>
     * Questo metodo utilizza try-with-resources per gestire automaticamente la
     * chiusura
     * dello stream di output.
     *
     * @param filePath il percorso completo del file in cui salvare gli utenti
     * 
     * @see #deserializeUtenti(String)
     * @see java.io.ObjectOutputStream
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
     * Deserializza la collezione degli utenti dal file specificato.
     * <p>
     * Carica un Set di utenti precedentemente serializzato. Se il file è vuoto,
     * termina senza effetti. In caso di errori durante il caricamento, stampa lo
     * stack trace dell'eccezione ma non la propaga.
     * <p>
     * A differenza dei metodi di serializzazione, questo metodo non utilizza
     * try-with-resources poiché è necessario verificare se il file è vuoto prima
     * di creare l'ObjectInputStream.
     *
     * @param filePath il percorso completo del file da cui caricare gli utenti
     * 
     * @see #serializeUtenti(String)
     * @see java.io.ObjectInputStream
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