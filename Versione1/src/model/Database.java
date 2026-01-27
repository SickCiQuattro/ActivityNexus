package model;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Repository centrale per tutti i dati dell'applicazione "ActivityNexus".
 * <p>
 * Questa classe implementa il pattern Repository e funge da contenitore
 * principale
 * per tutte le entità del dominio, mantenendo la loro coerenza e fornendo
 * metodi
 * per l'accesso e la manipolazione sicura. Gestisce:
 * <ul>
 * <li>Utenti (Configuratori e Fruitori)</li>
 * <li>Comprensori geografici e relativi comuni</li>
 * <li>Gerarchie di categorie e relative tassonomie</li>
 * <li>Fattori di conversione tra categorie di livelli diversi</li>
 * <li>Proposte di scambio e loro stati</li>
 * <li>Insiemi chiusi di scambi finalizzati</li>
 * </ul>
 * <p>
 * Ogni tipo di entità è gestito attraverso metodi specifici che garantiscono
 * l'integrità e la coerenza dei dati. Il database fornisce anche funzionalità
 * complete di persistenza tramite serializzazione Java, permettendo di salvare
 * e
 * ricaricare lo stato dell'applicazione tra sessioni diverse.
 *
 * <p>
 * <b>Invarianti del sistema:</b>
 * <ul>
 * <li>Tutte le collezioni interne (utenti, comprensori, gerarchie, proposte,
 * insiemiChiusi)
 * non sono mai null (vengono inizializzate nel costruttore)</li>
 * <li>L'oggetto conversioneGerarchie non è mai null</li>
 * <li>Non vengono mai memorizzati elementi null nelle collezioni</li>
 * <li>Gli identificatori univoci (username per utenti, nome per comprensori,
 * radice per gerarchie)
 * mantengono l'unicità all'interno delle rispettive collezioni</li>
 * <li>La relazione tra entità è sempre mantenuta coerente (es. proposte
 * riferiscono
 * a fruitori esistenti, insiemi chiusi contengono proposte valide)</li>
 * </ul>
 *
 * @see Utente
 * @see Configuratore
 * @see Fruitore
 * @see Comprensorio
 * @see Gerarchia
 * @see ConversioneGerarchie
 * @see Proposta
 * @see InsiemeChiuso
 */
public class Database {
    /** Collezione di tutti gli utenti registrati nel sistema */
    private Set<Utente> utenti;

    /** Collezione di tutti i comprensori geografici nel sistema */
    private Set<Comprensorio> comprensori;

    /** Collezione di tutte le gerarchie di categorie nel sistema */
    private Set<Gerarchia> gerarchie;

    /** Gestore delle conversioni tra categorie di gerarchie diverse */
    private ConversioneGerarchie conversioneGerarchie;

    /** Collezione di tutte le proposte di scambio nel sistema */
    private Set<Proposta> proposte;

    /** Collezione di tutti gli insiemi chiusi finalizzati nel sistema */
    private List<InsiemeChiuso> insiemiChiusi;

    // -----------------------------------------------------------------------------------
    // COSTRUTTORE
    // -----------------------------------------------------------------------------------

    /**
     * Inizializza un database vuoto con tutte le collezioni pronte per l'uso.
     * <p>
     * Questo costruttore crea un'istanza del database con tutte le collezioni
     * vuote ma inizializzate, garantendo che tutte le operazioni successive
     * possano essere effettuate senza verifiche di null.
     *
     * @post utenti != null (inizializzato come HashSet vuoto)
     * @post comprensori != null (inizializzato come HashSet vuoto)
     * @post gerarchie != null (inizializzato come HashSet vuoto)
     * @post conversioneGerarchie != null (nuova istanza)
     * @post proposte != null (inizializzato come HashSet vuoto)
     * @post insiemiChiusi != null (inizializzato come ArrayList vuoto)
     */
    public Database() {
        this.utenti = new HashSet<>();
        this.comprensori = new HashSet<>();
        this.gerarchie = new HashSet<>();
        this.conversioneGerarchie = new ConversioneGerarchie();
        this.proposte = new HashSet<>();
        this.insiemiChiusi = new ArrayList<>();
    }

    // -----------------------------------------------------------------------------------
    // METODI PER LA GESTIONE DEGLI UTENTI
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce una vista non modificabile della collezione degli utenti.
     * <p>
     * Questo metodo fornisce accesso in sola lettura alla collezione degli utenti,
     * proteggendo l'integrità interna del database impedendo modifiche non
     * controllate.
     *
     * @return vista non modificabile degli utenti registrati nel sistema
     *
     * @post result != null
     * @post result è una vista non modificabile di this.utenti
     * @post lo stato del database non viene modificato
     */
    public Set<Utente> getUtenti() {
        // Restituisce una vista non modificabile della collezione per proteggere
        // l'incapsulamento
        return Collections.unmodifiableSet(this.utenti);
    }

    /**
     * Aggiunge un utente al database se non è già presente.
     * <p>
     * L'unicità di un utente è determinata dal suo username attraverso il metodo
     * {@link Utente#equals(Object)}. Se un utente con lo stesso username esiste
     * già,
     * l'operazione fallisce.
     *
     * @param utente l'utente da aggiungere
     * @return true se l'utente è stato aggiunto con successo, false se era già
     *         presente o null
     *
     * @post se utente == null, result == false && database non modificato
     * @post se utente != null && !this.utenti.contains(utente),
     *       result == true && this.utenti.contains(utente)
     * @post se utente != null && this.utenti.contains(utente),
     *       result == false && database non modificato
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
     * Verifica se esiste un utente con lo username specificato.
     * <p>
     * Il confronto tra username è case-sensitive, quindi "User" e "user"
     * sono considerati username diversi.
     *
     * @param username lo username da verificare
     * @return true se esiste un utente con lo username specificato, false
     *         altrimenti
     *
     * @pre true (username può essere null)
     * @post se username == null, result == false
     * @post se esiste un utente u in this.utenti tale che
     *       u.getUsername().equals(username),
     *       result == true, altrimenti result == false
     * @post lo stato del database non viene modificato
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
     * Verifica se uno username è disponibile per la registrazione.
     * <p>
     * ATTENZIONE: Questo metodo ha un nome che può generare confusione. Restituisce
     * true
     * se lo username è DISPONIBILE (non esiste già), false se è già utilizzato.
     *
     * @param username lo username da verificare
     * @return true se lo username è disponibile (non esiste), false se è già
     *         utilizzato
     *
     * @pre true (username può essere null)
     * @post se username == null, result == true
     * @post se esiste un utente u in this.utenti tale che
     *       u.getUsername().equals(username),
     *       result == false, altrimenti result == true
     * @post lo stato del database non viene modificato
     */
    public boolean esisteUtente(String username) {
        // Verifica che lo username non sia null
        if (username == null) {
            return true;
        }
        // Cerca tra tutti gli utenti uno con lo username corrispondente
        for (Utente utente : this.utenti) {
            if (utente.getUsername().equals(username)) { // Confronto case-sensitive
                return false;
            }
        }
        // Nessuna corrispondenza trovata
        return true;
    }

    /**
     * Effettua l'autenticazione di un utente verificandone le credenziali.
     * <p>
     * Cerca tra gli utenti registrati quello con le credenziali corrispondenti
     * e lo restituisce se trovato. Le credenziali sono verificate in modo sicuro
     * utilizzando l'algoritmo di hashing implementato nella classe Utente.
     *
     * @param username lo username dell'utente
     * @param password la password in chiaro dell'utente
     * @return l'utente autenticato, o null se le credenziali sono errate o l'utente
     *         non esiste
     *
     * @pre true (parametri possono essere null)
     * @post se username == null || password == null || this.utenti.isEmpty(),
     *       result == null
     * @post se esiste un utente u in this.utenti tale che
     *       u.verificaCredenziali(username, password),
     *       result == u
     * @post se non esiste un utente con credenziali valide, result == null
     * @post lo stato del database non viene modificato
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
     * Registra un nuovo utente configuratore nel sistema.
     * <p>
     * Crea un nuovo utente con ruolo Configuratore e lo aggiunge al database,
     * verificando
     * preventivamente che lo username richiesto non sia già in uso. Il processo
     * include
     * la validazione dei parametri e la creazione sicura delle credenziali.
     *
     * @param username lo username univoco per il nuovo configuratore
     * @param password la password in chiaro (sarà oggetto di hashing)
     * @return true se la registrazione è avvenuta con successo, false se lo
     *         username esiste già
     *
     * @pre username != null
     * @pre password != null
     * @post se hasUtente(username) == true prima dell'invocazione, result == false
     *       && database non modificato
     * @post se hasUtente(username) == false prima dell'invocazione, result == true
     *       &&
     *       hasUtente(username) == true dopo l'invocazione
     * @post se result == true, è stato aggiunto un nuovo Configuratore con lo
     *       username specificato
     * @throws NullPointerException se username o password sono null
     */
    public boolean registraConfiguratore(String username, String password) {
        // Valida i parametri obbligatori
        Objects.requireNonNull(username, "Username non può essere null");
        Objects.requireNonNull(password, "Password non può essere null");
        // Verifica che lo username non sia già utilizzato
        if (hasUtente(username)) {
            return false;
        }
        // Crea e registra il nuovo utente configuratore
        Configuratore configuratore = new Configuratore(username, password);
        return this.addUtente(configuratore);
    }

    /**
     * Registra un nuovo utente fruitore nel sistema.
     * <p>
     * Crea un nuovo utente con ruolo Fruitore, associandolo opzionalmente a un
     * comprensorio geografico, e lo aggiunge al database. Verifica preventivamente
     * che lo username richiesto non sia già in uso.
     *
     * @param username     lo username univoco per il nuovo fruitore
     * @param password     la password in chiaro (sarà oggetto di hashing)
     * @param email        l'indirizzo email del fruitore
     * @param comprensorio il comprensorio geografico di appartenenza (può essere
     *                     null)
     * @return true se la registrazione è avvenuta con successo, false se lo
     *         username esiste già
     *
     * @pre username != null
     * @pre password != null
     * @pre email != null
     * @post se hasUtente(username) == true prima dell'invocazione, result == false
     *       && database non modificato
     * @post se hasUtente(username) == false prima dell'invocazione, result == true
     *       &&
     *       hasUtente(username) == true dopo l'invocazione
     * @post se result == true, è stato aggiunto un nuovo Fruitore con lo username
     *       specificato
     *       e il comprensorio fornito
     * @throws NullPointerException se username, password o email sono null
     */
    public boolean registraFruitore(String username, String password, String email, Comprensorio comprensorio) {
        // Valida i parametri obbligatori
        Objects.requireNonNull(username, "Username non può essere null");
        Objects.requireNonNull(password, "Password non può essere null");
        Objects.requireNonNull(email, "Email non può essere null");
        // Verifica che lo username non sia già utilizzato
        if (hasUtente(username)) {
            return false;
        }
        // Crea e registra il nuovo utente fruitore
        Fruitore fruitore = new Fruitore(username, password, email, comprensorio);
        return this.addUtente(fruitore);
    }

    // -----------------------------------------------------------------------------------
    // METODI PER LA GESTIONE DEI COMPRENSORI
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce una vista non modificabile dei comprensori geografici.
     * <p>
     * Fornisce accesso in sola lettura alla collezione dei comprensori registrati
     * nel sistema, proteggendo l'integrità del database.
     *
     * @return vista non modificabile dei comprensori
     *
     * @pre true (nessuna precondizione)
     * @post result != null
     * @post result è una vista non modificabile di this.comprensori
     * @post lo stato del database non viene modificato
     */
    public Set<Comprensorio> getComprensori() {
        // Restituisce una vista non modificabile della collezione
        return Collections.unmodifiableSet(this.comprensori);
    }

    /**
     * Aggiunge un comprensorio al database se non è già presente.
     * <p>
     * L'unicità di un comprensorio è determinata dal suo nome attraverso il metodo
     * {@link Comprensorio#equals(Object)}. Se un comprensorio con lo stesso nome
     * esiste già,
     * l'operazione fallisce.
     *
     * @param comprensorio il comprensorio da aggiungere
     * @return true se il comprensorio è stato aggiunto con successo, false se era
     *         già presente o null
     *
     * @pre true (comprensorio può essere null)
     * @post se comprensorio == null, result == false && database non modificato
     * @post se comprensorio != null && !this.comprensori.contains(comprensorio),
     *       result == true && this.comprensori.contains(comprensorio)
     * @post se comprensorio != null && this.comprensori.contains(comprensorio),
     *       result == false && database non modificato
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
     * <p>
     * Il confronto tra nomi è case-insensitive, quindi "Milano" e "milano"
     * sono considerati lo stesso comprensorio.
     *
     * @param nome il nome del comprensorio da verificare
     * @return true se esiste un comprensorio con il nome specificato, false
     *         altrimenti
     *
     * @pre true (nome può essere null)
     * @post se nome == null, result == false
     * @post se esiste un comprensorio c in this.comprensori tale che c.getNome() !=
     *       null
     *       && c.getNome().equalsIgnoreCase(nome), result == true, altrimenti
     *       result == false
     * @post lo stato del database non viene modificato
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
     * Genera una rappresentazione testuale di tutti i comprensori registrati.
     * <p>
     * Crea una stringa formattata che rappresenta tutti i comprensori presenti nel
     * sistema,
     * includendo i dettagli dei comuni appartenenti a ciascun comprensorio.
     *
     * @return stringa formattata contenente tutti i comprensori
     *
     * @pre true (nessuna precondizione)
     * @post result != null
     * @post se this.comprensori.isEmpty(), result è un messaggio che indica
     *       l'assenza di comprensori
     * @post se !this.comprensori.isEmpty(), result contiene la rappresentazione di
     *       tutti i comprensori
     * @post lo stato del database non viene modificato
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
     * Restituisce una vista non modificabile delle gerarchie di categorie.
     * <p>
     * Fornisce accesso in sola lettura alla collezione delle gerarchie registrate
     * nel sistema, proteggendo l'integrità del database.
     *
     * @return vista non modificabile delle gerarchie
     *
     * @post result != null
     * @post result è una vista non modificabile di this.gerarchie
     * @post lo stato del database non viene modificato
     */
    public Set<Gerarchia> getGerarchie() {
        // Restituisce una vista non modificabile della collezione
        return Collections.unmodifiableSet(this.gerarchie);
    }

    /**
     * Aggiunge una gerarchia al database se non è già presente.
     * <p>
     * L'unicità di una gerarchia è determinata dalla sua categoria radice
     * attraverso il metodo
     * {@link Gerarchia#equals(Object)}. Se una gerarchia con la stessa radice
     * esiste già,
     * l'operazione fallisce.
     *
     * @param gerarchia la gerarchia da aggiungere
     * @return true se la gerarchia è stata aggiunta con successo, false se era già
     *         presente o null
     *
     * @pre true (gerarchia può essere null)
     * @post se gerarchia == null, result == false && database non modificato
     * @post se gerarchia != null && !this.gerarchie.contains(gerarchia),
     *       result == true && this.gerarchie.contains(gerarchia)
     * @post se gerarchia != null && this.gerarchie.contains(gerarchia),
     *       result == false && database non modificato
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
     * Verifica se esiste una gerarchia con la categoria radice specificata.
     * <p>
     * Il confronto avviene sul nome della categoria radice della gerarchia in
     * modo case-sensitive.
     *
     * @param nome il nome della categoria radice da verificare
     * @return true se esiste una gerarchia con la radice specificata, false
     *         altrimenti
     *
     * @pre true (nome può essere null)
     * @post se nome == null, result == false
     * @post se esiste una gerarchia g in this.gerarchie tale che g.root().data() !=
     *       null &&
     *       g.root().data().getNome().equals(nome), result == true, altrimenti
     *       result == false
     * @post lo stato del database non viene modificato
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
     * Recupera una gerarchia specifica in base alla posizione nella collezione.
     * <p>
     * Attenzione: poiché le gerarchie sono memorizzate in un Set, l'ordine non è
     * garantito essere stabile tra diverse esecuzioni del programma.
     *
     * @param index l'indice della gerarchia (0-based)
     * @return la gerarchia all'indice specificato
     *
     * @pre 0 <= index < this.gerarchie.size()
     * @post result è la gerarchia all'indice specificato nella collezione
     *       convertita in lista
     * @post lo stato del database non viene modificato
     * @throws IndexOutOfBoundsException se index è negativo o maggiore o uguale a
     *                                   gerarchie.size()
     */
    public Gerarchia getGerarchia(int index) {
        // Converte il set in una lista per accedere per indice
        ArrayList<Gerarchia> gerarchieList = new ArrayList<>(this.gerarchie);
        // Restituisce l'elemento all'indice specifico
        // get() lancerà IndexOutOfBoundsException se l'indice non è valido
        return gerarchieList.get(index);
    }

    /**
     * Genera una rappresentazione testuale di tutte le gerarchie registrate.
     * <p>
     * Crea una stringa formattata che rappresenta tutte le gerarchie presenti nel
     * sistema,
     * includendo i dettagli della struttura ad albero di ciascuna gerarchia.
     *
     * @return stringa formattata contenente tutte le gerarchie
     *
     * @post result != null
     * @post se this.gerarchie.isEmpty(), result è un messaggio che indica l'assenza
     *       di gerarchie
     * @post se !this.gerarchie.isEmpty(), result contiene la rappresentazione di
     *       tutte le gerarchie
     * @post lo stato del database non viene modificato
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
     * Restituisce il gestore delle conversioni tra gerarchie.
     * <p>
     * Fornisce accesso all'oggetto che gestisce i fattori di conversione tra
     * categorie di gerarchie diverse. Questo oggetto permette di definire e
     * applicare conversioni tra unità di scambio in diverse categorie.
     *
     * @return il gestore delle conversioni tra gerarchie (mai null)
     *
     * @post result == this.conversioneGerarchie
     * @post result != null
     * @post lo stato del database non viene modificato
     */
    public ConversioneGerarchie getConversioneGerarchie() {
        return this.conversioneGerarchie;
    }

    // -----------------------------------------------------------------------------------
    // METODI PER LA GESTIONE DELLE PROPOSTE
    // -----------------------------------------------------------------------------------

    /**
     * Aggiunge una proposta di scambio al database.
     * <p>
     * A differenza di altri metodi "add", questo metodo non restituisce un valore
     * booleano e non verifica preventivamente se la proposta è già presente.
     * Se la proposta è null, l'operazione viene ignorata silenziosamente.
     *
     * @param proposta la proposta di scambio da aggiungere
     *
     * @pre true (proposta può essere null)
     * @post se proposta == null, database non modificato
     * @post se proposta != null, this.proposte contiene la proposta specificata
     */
    public void aggiungiProposta(Proposta proposta) {
        // Verifica che la proposta non sia null
        if (proposta == null) {
            return;
        }

        // Tenta di aggiungere la proposta alla collezione
        this.proposte.add(proposta);
    }

    /**
     * Recupera le proposte di scambio filtrate in base allo stato.
     * <p>
     * Se lo stato specificato è null, vengono restituite tutte le proposte
     * presenti nel sistema senza filtraggio.
     *
     * @param stato lo stato delle proposte da filtrare, o null per tutte le
     *              proposte
     * @return lista contenente le proposte filtrate per stato (mai null)
     *
     * @pre true (stato può essere null)
     * @post result != null
     * @post se stato == null, result contiene tutte le proposte in this.proposte
     * @post se stato != null, result contiene solo le proposte p in this.proposte
     *       tali che p.getStato() == stato
     * @post lo stato del database non viene modificato
     */
    public List<Proposta> getPropostePerStato(StatoProposta stato) {
        // Se lo stato è null, restituisce tutte le proposte
        if (stato == null) {
            return new ArrayList<>(this.proposte);
        }

        // Altrimenti filtra le proposte per lo stato specificato
        List<Proposta> proposteFiltrate = new ArrayList<>();

        for (Proposta proposta : this.proposte) {
            if (proposta.getStato() == stato) {
                proposteFiltrate.add(proposta);
            }
        }
        return proposteFiltrate;
    }

    /**
     * Recupera tutte le proposte di scambio formulate da un fruitore specifico.
     * <p>
     * Restituisce tutte le proposte create dal fruitore specificato,
     * indipendentemente dal loro stato attuale.
     *
     * @param fruitore il fruitore di cui recuperare le proposte
     * @return lista contenente le proposte del fruitore specificato (mai null)
     *
     * @pre fruitore != null
     * @post result != null
     * @post result contiene solo le proposte p in this.proposte
     *       tali che p.getPropostitore().equals(fruitore)
     * @post lo stato del database non viene modificato
     * @throws NullPointerException se fruitore è null
     */
    public List<Proposta> getPropostePerFruitore(Fruitore fruitore) {
        // Filtra le proposte per il fruitore specificato
        List<Proposta> proposteFiltrate = new ArrayList<>();

        for (Proposta proposta : this.proposte) {
            if (proposta.getPropostitore().equals(fruitore)) {
                proposteFiltrate.add(proposta);
            }
        }
        return proposteFiltrate;
    }

    /**
     * Recupera le proposte di un fruitore filtrate per stato.
     * <p>
     * Se lo stato specificato è null, vengono restituite tutte le proposte
     * del fruitore senza filtraggio per stato.
     *
     * @param fruitore il fruitore di cui recuperare le proposte
     * @param stato    lo stato delle proposte da filtrare, o null per tutte le
     *                 proposte del fruitore
     * @return lista contenente le proposte del fruitore filtrate per stato (mai
     *         null)
     *
     * @pre fruitore != null
     * @pre true (stato può essere null)
     * @post result != null
     * @post result contiene solo proposte p tali che
     *       p.getPropostitore().equals(fruitore)
     * @post se stato != null, result contiene solo proposte p tali che p.getStato()
     *       == stato
     * @post se stato == null, result contiene tutte le proposte del fruitore
     * @post lo stato del database non viene modificato
     * @throws NullPointerException se fruitore è null
     */
    public List<Proposta> getPropostePerFruitore(Fruitore fruitore, StatoProposta stato) {
        List<Proposta> proposteFiltrate = new ArrayList<>();

        for (Proposta proposta : this.proposte) {
            if (proposta.getPropostitore().equals(fruitore)) {
                // Se stato è null o corrisponde allo stato della proposta, aggiungi la proposta
                if (stato == null || proposta.getStato() == stato) {
                    proposteFiltrate.add(proposta);
                }
            }
        }

        return proposteFiltrate;
    }

    /**
     * Recupera le proposte aperte formulate dai fruitori dello stesso comprensorio.
     * <p>
     * Questo metodo è utile per mostrare a un fruitore tutte le proposte
     * disponibili
     * nel suo comprensorio geografico, con la possibilità di escludere le proprie
     * proposte.
     * <p>
     * <b>PATTERN: Information Expert</b> - Il metodo delega la logica di filtraggio
     * alla classe Proposta che è l'esperto delle proprie informazioni. Questo
     * riduce
     * l'accoppiamento e migliora la manutenibilità.
     * <p>
     * Il filtro applica tre criteri usando i metodi della classe Proposta:
     * <ol>
     * <li>{@link Proposta#isAperta()} - Le proposte devono essere nello stato
     * APERTA</li>
     * <li>{@link Proposta#appartieneStoComprensorio(Comprensorio)} - Le proposte
     * devono
     * provenire da fruitori dello stesso comprensorio</li>
     * <li>{@link Proposta#isDiPropostitore(Fruitore)} - Opzionalmente esclude le
     * proposte
     * dell'utente di riferimento</li>
     * </ol>
     *
     * @param utente        l'utente di riferimento per identificare il comprensorio
     * @param includiUtente se includere anche le proposte formulate dall'utente di
     *                      riferimento
     * @return lista di proposte aperte nel comprensorio dell'utente (mai null)
     *
     * @pre utente != null
     * @pre utente è istanza di Fruitore
     * @pre ((Fruitore)utente).getComprensorio() != null
     * @post result != null
     * @post tutti gli elementi in result hanno isAperta() == true
     * @post tutti gli elementi in result hanno
     *       appartieneStoComprensorio(((Fruitore)utente).getComprensorio()) == true
     * @post se includiUtente == false, nessun elemento in result ha
     *       isDiPropostitore((Fruitore)utente) == true
     * @post lo stato del database non viene modificato
     * 
     * @throws ClassCastException   se utente non è un'istanza di Fruitore
     * @throws NullPointerException se utente è null o il suo comprensorio è null
     */
    public List<Proposta> getProposteAperteStessoComprensorio(Utente utente, boolean includiUtente) {
        // Cast a Fruitore e recupero del comprensorio
        Fruitore fruitore = (Fruitore) utente;
        Comprensorio comprensorio = fruitore.getComprensorio();

        // PATTERN: Information Expert - Usa metodi della classe Proposta per il
        // filtraggio
        // La logica di verifica è delegata a Proposta che è l'esperto delle sue
        // informazioni
        return proposte.stream()
                .filter(Proposta::isAperta) // Proposta sa se è aperta
                .filter(p -> p.appartieneStoComprensorio(comprensorio)) // Proposta sa il suo comprensorio
                .filter(p -> includiUtente || !p.isDiPropostitore(fruitore)) // Proposta sa chi l'ha creata
                .collect(Collectors.toList());
    }

    // -----------------------------------------------------------------------------------
    // METODI PER LA GESTIONE DEGLI INSIEMI CHIUSI
    // -----------------------------------------------------------------------------------

    /**
     * Aggiunge un insieme chiuso al database.
     * <p>
     * Un insieme chiuso rappresenta un ciclo di scambi finalizzato tra più
     * fruitori.
     * Se l'insieme chiuso specificato è null, l'operazione viene ignorata
     * silenziosamente.
     *
     * @param insiemeChiuso l'insieme chiuso da aggiungere
     *
     * @pre true (insiemeChiuso può essere null)
     * @post se insiemeChiuso == null, database non modificato
     * @post se insiemeChiuso != null, this.insiemiChiusi contiene l'insieme chiuso
     *       specificato
     */
    public void aggiungiInsiemeChiuso(InsiemeChiuso insiemeChiuso) {
        if (insiemeChiuso == null) {
            return;
        }
        this.insiemiChiusi.add(insiemeChiuso);
    }

    /**
     * Recupera tutti gli insiemi chiusi presenti nel database.
     * <p>
     * Restituisce una vista non modificabile della lista di insiemi chiusi,
     * garantendo che non possano essere alterati attraverso questo riferimento.
     *
     * @return lista non modificabile di tutti gli insiemi chiusi (mai null)
     *
     * @post result != null
     * @post result è una vista non modificabile di this.insiemiChiusi
     * @post lo stato del database non viene modificato
     */
    public List<InsiemeChiuso> getInsiemiChiusi() {
        return Collections.unmodifiableList(insiemiChiusi);
    }

    // -----------------------------------------------------------------------------------
    // METODI DI SERIALIZZAZIONE E DESERIALIZZAZIONE
    // -----------------------------------------------------------------------------------

    /**
     * Serializza la collezione degli utenti nel file specificato.
     * <p>
     * Salva in modo persistente tutti gli utenti attualmente presenti nel database,
     * consentendo di ricaricarli in sessioni future dell'applicazione.
     *
     * @param filePath il percorso completo del file in cui salvare gli utenti
     *
     * @pre filePath != null
     * @post se non si verificano eccezioni, il file specificato contiene una
     *       rappresentazione
     *       serializzata della collezione utenti
     * @post se si verifica un'eccezione durante la serializzazione, viene
     *       registrato un errore
     * @post lo stato del database non viene modificato
     * @throws NullPointerException se filePath è null
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

    /**
     * Serializza la collezione delle proposte nel file specificato.
     *
     * @param filePath il percorso del file in cui salvare le proposte
     *
     */
    public void serializeProposte(String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(this.proposte);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deserializza la collezione delle proposte dal file specificato.
     *
     * @param filePath il percorso del file da cui caricare le proposte
     *
     */
    public void deserializeProposte(String filePath) {
        try {
            FileInputStream fis = new FileInputStream(filePath);

            // Verifica se il file è vuoto
            if (fis.available() == 0) {
                return; // File vuoto, niente da deserializzare
            }

            ObjectInputStream ois = new ObjectInputStream(fis);

            // Legge il Set di proposte dal file e lo assegna all'attributo
            this.proposte = (Set<Proposta>) ois.readObject();

        } catch (IOException | ClassNotFoundException e) {
            // Gestisce errori di I/O o di classe non trovata
            e.printStackTrace();
        }
    }

    /**
     * Serializza la collezione degli insiemi chiusi nel file specificato.
     * <p>
     * Salva in modo persistente tutti gli insiemi chiusi presenti nel database,
     * permettendo di ricaricarli in sessioni future dell'applicazione.
     *
     * @param filePath il percorso del file in cui salvare gli insiemi chiusi
     *
     * @pre this.insiemiChiusi != null
     * @pre filePath != null
     * @post Il file specificato contiene una rappresentazione serializzata della
     *       collezione insiemiChiusi
     * @post Se si verifica un'eccezione durante la serializzazione, viene stampato
     *       lo stack trace
     * @post Lo stato del database non viene modificato
     */
    public void serializeInsiemiChiusi(String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(this.insiemiChiusi);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deserializza la collezione degli insiemi chiusi dal file specificato.
     * <p>
     * Carica gli insiemi chiusi precedentemente salvati, ripristinando lo stato
     * della collezione come era al momento della serializzazione.
     *
     * @param filePath il percorso del file da cui caricare gli insiemi chiusi
     *
     * @pre filePath != null
     * @post Se il file esiste ed è valido, this.insiemiChiusi contiene gli insiemi
     *       chiusi deserializzati
     * @post Se il file è vuoto, this.insiemiChiusi rimane invariato
     * @post Se si verifica un'eccezione durante la deserializzazione, viene
     *       stampato lo stack trace
     */
    public void deserializeInsiemiChiusi(String filePath) {
        try {
            FileInputStream fis = new FileInputStream(filePath);
            if (fis.available() == 0) {
                return;
            }
            ObjectInputStream ois = new ObjectInputStream(fis);
            this.insiemiChiusi = (List<InsiemeChiuso>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}