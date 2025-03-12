package model;

import java.io.*;
import java.util.*;

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
 *
 * <p>
 * <b>Invarianti:</b>
 * <ul>
 * <li>Le collezioni di utenti, comprensori e gerarchie non sono mai null
 * (possono essere vuote)</li>
 * <li>L'oggetto conversioneGerarchie non è mai null</li>
 * <li>Tutti gli elementi contenuti nelle collezioni non sono null</li>
 * <li>Non ci sono utenti duplicati (stesso username)</li>
 * <li>Non ci sono comprensori duplicati (stesso nome)</li>
 * <li>Non ci sono gerarchie duplicate (stessa radice)</li>
 * </ul>
 *
 * @see Utente
 * @see Configuratore
 * @see Comprensorio
 * @see Gerarchia
 * @see ConversioneGerarchie
 */
public class Database {
    // Collezioni principali di entità
    private Set<Utente> utenti;
    private Set<Comprensorio> comprensori;
    private Set<Gerarchia> gerarchie;
    private ConversioneGerarchie conversioneGerarchie;
    private Set<Proposta> proposte;

    /**
     * Inizializza un database vuoto con tutte le collezioni pronte per l'uso.
     *
     * @post utenti è un nuovo HashSet vuoto non-null
     * @post comprensori è un nuovo HashSet vuoto non-null
     * @post gerarchie è un nuovo HashSet vuoto non-null
     * @post conversioneGerarchie è una nuova istanza non-null
     */
    public Database() {
        this.utenti = new HashSet<>();
        this.comprensori = new HashSet<>();
        this.gerarchie = new HashSet<>();
        this.conversioneGerarchie = new ConversioneGerarchie();
        this.proposte = new HashSet<>();
    }

    // -----------------------------------------------------------------------------------
    // METODI PER LA GESTIONE DEGLI UTENTI
    // -----------------------------------------------------------------------------------

    /**
     * Aggiunge un utente al database.
     *
     * @param utente l'utente da aggiungere
     * @return true se l'utente è stato aggiunto, false se già presente o null
     *
     * @post se utente == null, restituisce false e lo stato non cambia
     * @post se un utente con lo stesso username esiste già, restituisce false e lo
     *       stato non cambia
     * @post se restituisce true, l'utente è stato aggiunto alla collezione
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
     * Registra un nuovo utente configuratore nel sistema.
     * <p>
     * Crea un nuovo utente con ruolo Configuratore, verificando prima che lo
     * username
     * richiesto non sia già utilizzato. Il processo include:
     * <ol>
     * <li>Validazione dei parametri obbligatori</li>
     * <li>Verifica dell'unicità dello username nel sistema</li>
     * <li>Creazione di una nuova istanza Configuratore con le credenziali
     * specificate</li>
     * <li>Aggiunta dell'utente alla collezione del database</li>
     * </ol>
     * <p>
     * La password sarà sottoposta ad hashing per garantire la sicurezza.
     *
     * @param username lo username univoco per il nuovo configuratore
     * @param password la password in chiaro (sarà oggetto di hashing)
     * @return true se la registrazione è avvenuta con successo, false se lo
     *         username esiste già
     *
     * @pre username != null
     * @pre password != null
     * @pre this.utenti != null
     * @post se restituisce true, hasUtente(username) == true
     * @post se restituisce true, un nuovo Configuratore è stato aggiunto alla
     *       collezione utenti
     * @post se hasUtente(username) == true prima dell'invocazione, restituisce
     *       false e lo stato non cambia
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
     * Crea un nuovo utente con ruolo Fruitore, verificando prima che lo username
     * richiesto non sia già utilizzato. Il processo include:
     * <ol>
     * <li>Validazione dei parametri obbligatori (username, password, email)</li>
     * <li>Verifica dell'unicità dello username nel sistema</li>
     * <li>Creazione di una nuova istanza Fruitore con le credenziali e informazioni
     * specificate</li>
     * <li>Aggiunta dell'utente alla collezione del database</li>
     * </ol>
     * <p>
     * Il comprensorio di appartenenza è opzionale e può essere specificato null.
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
     * @pre this.utenti != null
     * @post se restituisce true, hasUtente(username) == true
     * @post se restituisce true, un nuovo Fruitore è stato aggiunto alla collezione
     *       utenti
     * @post se hasUtente(username) == true prima dell'invocazione, restituisce
     *       false e lo stato non cambia
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

    /**
     * Verifica se uno username è disponibile per la registrazione.
     * <p>
     * Controlla se non esiste già un utente con lo username specificato nel
     * sistema.
     * Il confronto è case-sensitive (distingue tra maiuscole e minuscole).
     * <p>
     * Nota: Il nome del metodo può essere fuorviante - restituisce true se lo
     * username
     * è DISPONIBILE (non esiste), false se è già utilizzato.
     *
     * @param username lo username da verificare
     * @return true se lo username è disponibile (non esiste), false se è già
     *         utilizzato
     *
     * @pre this.utenti != null
     * @post se username == null, restituisce true
     * @post se esiste un utente u tale che u.getUsername().equals(username),
     *       restituisce false
     * @post se non esiste un utente u tale che u.getUsername().equals(username),
     *       restituisce true
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
     * Effettua il login verificando le credenziali fornite.
     *
     * @param username lo username dell'utente
     * @param password la password dell'utente
     * @return l'utente autenticato, o null se credenziali errate
     *
     * @post se username == null o password == null o utenti è vuoto, restituisce
     *       null
     * @post restituisce null se nessun utente ha corrispondenza di username e
     *       password
     * @post se trovato, restituisce l'istanza utente corrispondente alle
     *       credenziali
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
     * Verifica se esiste un utente con lo username specificato.
     *
     * @param username lo username da verificare (case-sensitive)
     * @return true se lo username esiste, false altrimenti
     *
     * @post se username == null, restituisce false
     * @post restituisce true se e solo se esiste un utente con lo username
     *       specificato
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
     * Restituisce una vista non modificabile della collezione degli utenti.
     *
     * @return vista non modificabile degli utenti
     *
     * @post il valore restituito non è null
     * @post il valore restituito è una vista non modificabile della collezione
     *       interna
     * @post lo stato del database non viene modificato
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
     *
     * @post se utenti == null, viene creata una collezione vuota
     * @post se utenti != null, viene creata una copia difensiva della collezione
     * @post this.utenti contiene tutti e soli gli elementi della collezione fornita
     *       (o nessuno se era null)
     */
    public void setUtenti(Set<Utente> utenti) {
        // Crea una copia difensiva della collezione o una nuova collezione vuota se
        // null
        this.utenti = utenti != null ? new HashSet<>(utenti) : new HashSet<>();
    }

    /**
     * Ottiene tutti i fruitori che appartengono allo stesso comprensorio
     * dell'utente specificato.
     * <p>
     * Questo metodo è utile per operazioni che richiedono l'identificazione di
     * potenziali
     * partner di scambio all'interno dello stesso comprensorio geografico, come
     * richiesto
     * dalle regole di business per la realizzazione degli scambi.
     * 
     * @param utente        l'utente di riferimento di cui trovare i fruitori nello
     *                      stesso comprensorio
     * @param includiUtente se true include l'utente di riferimento nel risultato,
     *                      altrimenti lo esclude
     * @return lista di fruitori che appartengono allo stesso comprensorio
     *         dell'utente
     * 
     * @pre utente != null
     * @pre utente deve essere un Fruitore (non un Configuratore)
     * @post La lista restituita contiene solo Fruitori appartenenti allo stesso
     *       comprensorio dell'utente
     * @post Se includiUtente è false, l'utente di riferimento non è incluso nella
     *       lista restituita
     * @post Se non ci sono altri fruitori nello stesso comprensorio, viene
     *       restituita una lista vuota
     */
    public List<Fruitore> getFruitoriStessoComprensorio(Utente utente, boolean includiUtente) {
        // Cast a Fruitore e recupero del comprensorio
        Fruitore fruitoreRiferimento = (Fruitore) utente;
        Comprensorio comprensorioRiferimento = fruitoreRiferimento.getComprensorio();

        // Lista dove verranno inseriti i fruitori dello stesso comprensorio
        List<Fruitore> fruitoriStessoComprensorio = new ArrayList<>();

        // Itera su tutti gli utenti nel database
        for (Utente altroUtente : this.getUtenti()) {
            // Salta i configuratori
            if (altroUtente.isConfig()) {
                continue;
            }

            Fruitore altroFruitore = (Fruitore) altroUtente;

            // Verifica se è lo stesso utente e se deve essere incluso
            if (altroFruitore.equals(fruitoreRiferimento) && !includiUtente) {
                continue;
            }

            // Verifica se il fruitore appartiene allo stesso comprensorio
            if (altroFruitore.getComprensorio().equals(comprensorioRiferimento)) {
                fruitoriStessoComprensorio.add(altroFruitore);
            }
        }

        return fruitoriStessoComprensorio;
    }

    /**
     * Versione semplificata che esclude sempre l'utente di riferimento.
     * 
     * @param utente l'utente di riferimento
     * @return lista di fruitori che appartengono allo stesso comprensorio, escluso
     *         l'utente specificato
     */
    public List<Fruitore> getFruitoriStessoComprensorio(Utente utente) {
        return getFruitoriStessoComprensorio(utente, false);
    }

    /**
     * Ottiene tutte le proposte di scambio aperte formulate dai fruitori dello
     * stesso comprensorio.
     *
     * @param utente        l'utente di riferimento di cui trovare le proposte nel
     *                      comprensorio
     * @param includiUtente se includere anche le proposte dell'utente di
     *                      riferimento
     * @return lista di proposte aperte nel comprensorio dell'utente
     *
     * @pre utente != null
     * @pre utente deve essere un Fruitore (non un Configuratore)
     *
     * @post La lista restituita contiene solo proposte aperte formulate da Fruitori
     *       appartenenti allo stesso comprensorio dell'utente
     * @post Se includiUtente è false, l'utente di riferimento non è incluso
     *       nella lista restituita
     */
    public List<Proposta> getProposteAperteStessoComprensorio(Utente utente, boolean includiUtente) {
        // Cast a Fruitore e recupero del comprensorio
        Fruitore fruitoreRiferimento = (Fruitore) utente;
        Comprensorio comprensorioRiferimento = fruitoreRiferimento.getComprensorio();

        // Lista per le proposte aperte dello stesso comprensorio
        List<Proposta> proposteAperte = new ArrayList<>();

        // Filtra tutte le proposte aperte
        List<Proposta> tutteProposteAperte = getPropostePerStato(StatoProposta.APERTA);

        for (Proposta proposta : tutteProposteAperte) {
            Fruitore proponente = proposta.getPropostitore();

            // Controlla che il proponente abbia lo stesso comprensorio
            if (proponente.getComprensorio().equals(comprensorioRiferimento)) {
                // Se non dobbiamo includere l'utente di riferimento e questo è la proposta dell'utente, la saltiamo
                if (!includiUtente && proponente.equals(fruitoreRiferimento)) {
                    continue;
                }

                proposteAperte.add(proposta);
            }
        }

        return proposteAperte;
    }

    // -----------------------------------------------------------------------------------
    // METODI PER LA GESTIONE DEI COMPRENSORI
    // -----------------------------------------------------------------------------------

    /**
     * Aggiunge un comprensorio al database.
     *
     * @param comprensorio il comprensorio da aggiungere
     * @return true se aggiunto con successo, false se già presente o null
     *
     * @post se comprensorio == null, restituisce false e lo stato non cambia
     * @post se un comprensorio con lo stesso nome esiste già, restituisce false e
     *       lo stato non cambia
     * @post se restituisce true, il comprensorio è stato aggiunto alla collezione
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
     *
     * @post se nome == null, restituisce false
     * @post restituisce true se e solo se esiste un comprensorio con il nome
     *       specificato (case-insensitive)
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
     * Restituisce una vista non modificabile dei comprensori.
     *
     * @return vista non modificabile dei comprensori
     *
     * @post il valore restituito non è null
     * @post il valore restituito è una vista non modificabile della collezione
     *       interna
     * @post lo stato del database non viene modificato
     */
    public Set<Comprensorio> getComprensori() {
        // Restituisce una vista non modificabile della collezione
        return Collections.unmodifiableSet(this.comprensori);
    }

    /**
     * Imposta la collezione dei comprensori, creando una copia difensiva.
     *
     * @param comprensori la nuova collezione di comprensori
     *
     * @post se comprensori == null, viene creata una collezione vuota
     * @post se comprensori != null, viene creata una copia difensiva della
     *       collezione
     * @post this.comprensori contiene tutti e soli gli elementi della collezione
     *       fornita (o nessuno se era null)
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
     *
     * @post il valore restituito non è null
     * @post se comprensori è vuoto, restituisce un messaggio appropriato
     * @post se comprensori non è vuoto, restituisce una rappresentazione di tutti i
     *       comprensori
     * @post lo stato del database non viene modificato
     */
    public String toStringComprensori() {
        // Gestisce il caso in cui non ci sono comprensori
        if (comprensori.isEmpty()) {
            return "Nessun comprensorio presente.";
        }

        int count = 1;

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
     *
     * @post se gerarchia == null, restituisce false e lo stato non cambia
     * @post se una gerarchia con la stessa radice esiste già, restituisce false e
     *       lo stato non cambia
     * @post se restituisce true, la gerarchia è stata aggiunta alla collezione
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
     *
     * @post se nome == null, restituisce false
     * @post restituisce true se e solo se esiste una gerarchia con radice avente il
     *       nome specificato
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
     * Restituisce una vista non modificabile delle gerarchie.
     *
     * @return vista non modificabile delle gerarchie
     *
     * @post il valore restituito non è null
     * @post il valore restituito è una vista non modificabile della collezione
     *       interna
     * @post lo stato del database non viene modificato
     */
    public Set<Gerarchia> getGerarchie() {
        // Restituisce una vista non modificabile della collezione
        return Collections.unmodifiableSet(this.gerarchie);
    }

    /**
     * Imposta la collezione delle gerarchie, creando una copia difensiva.
     *
     * @param gerarchie la nuova collezione di gerarchie
     *
     * @post se gerarchie == null, viene creata una collezione vuota
     * @post se gerarchie != null, viene creata una copia difensiva della collezione
     * @post this.gerarchie contiene tutti e soli gli elementi della collezione
     *       fornita (o nessuno se era null)
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
     *
     * @pre 0 <= index < gerarchie.size()
     * @post il valore restituito è la gerarchia corrispondente all'indice
     *       specificato
     * @post lo stato del database non viene modificato
     * @throws IndexOutOfBoundsException se index < 0 o index >= gerarchie.size()
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
     *
     * @post il valore restituito non è null
     * @post se gerarchie è vuoto, restituisce un messaggio appropriato
     * @post se gerarchie non è vuoto, restituisce una rappresentazione di tutte le
     *       gerarchie
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
     * Restituisce l'oggetto di gestione delle conversioni tra gerarchie.
     *
     * @return l'oggetto di conversione tra categorie (mai null)
     *
     * @post il valore restituito non è null
     * @post il valore restituito è l'istanza conversioneGerarchie del database
     * @post lo stato del database non viene modificato
     */
    public ConversioneGerarchie getConversioneGerarchie() {
        return this.conversioneGerarchie;
    }


    // -----------------------------------------------------------------------------------
    // METODI PER LA GESTIONE DELLE PROPOSTE
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce una lista di proposte filtrate in base allo stato specificato.
     * Se lo stato specificato è null, restituisce tutte le proposte.
     *
     * @param stato lo stato delle proposte da filtrare, o null per tutte le proposte
     * @return lista contenente le proposte filtrate per stato, o tutte le proposte se stato è null
     *
     * @post il valore restituito non è null
     * @post se stato è null, il valore restituito contiene tutte le proposte
     * @post se stato non è null, il valore restituito contiene solo proposte con stato uguale al parametro
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
     * Restituisce una lista di proposte filtrate in base al fruitore specificato.
     *
     * @param fruitore il fruitore di riferimento per il filtro, o null per tutte le proposte
     * @return lista contenente le proposte filtrate per fruitore, o tutte le proposte se fruitore è null
     *
     * @pre fruitore != null
     *
     * @post il valore restituito non è null
     * @post se fruitore non è null, il valore restituito contiene solo proposte del fruitore specificato
     * @post lo stato del database non viene modificato
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
     * Restituisce una lista di proposte filtrate per fruitore e stato.
     * Se lo stato è null, restituisce tutte le proposte del fruitore specificato.
     *
     * @param fruitore il fruitore di cui recuperare le proposte
     * @param stato lo stato delle proposte da filtrare, o null per tutte le proposte del fruitore
     * @return lista contenente le proposte del fruitore filtrate per stato, o tutte le sue proposte se stato è null
     *
     * @pre fruitore != null
     * @post il valore restituito non è null
     * @post il valore restituito contiene solo proposte del fruitore specificato
     * @post se stato non è null, il valore restituito contiene solo proposte con stato uguale al parametro
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
     * Aggiunge una proposta al database.
     *
     * @param proposta la proposta da aggiungere
     * @return true se aggiunta con successo, false se già presente o null
     *
     * @pre proposta != null
     * @post se proposta == null, restituisce false e lo stato non cambia
     * @post se una proposta con lo stesso ID esiste già, restituisce false e lo
     *       stato non cambia
     * @post se restituisce true, la proposta è stata aggiunta alla collezione
     */
    public boolean aggiungiProposta(Proposta proposta) {
        // Verifica che la proposta non sia null
        if (proposta == null) {
            return false;
        }

        // Tenta di aggiungere la proposta alla collezione
        return this.proposte.add(proposta);
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
}