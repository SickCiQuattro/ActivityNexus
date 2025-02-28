package controller;

import com.scalified.tree.TreeNode;
import model.*;
import util.UserUtil;
import view.ConsoleView;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Classe Controller che gestisce la logica dell'applicazione.
 * Si occupa di gestire le interazioni tra la vista e il modello,
 * implementando tutte le funzionalità del sistema.
 */
public class Controller {
    private static final String INVALID_OPTION = "Opzione non valida. Riprova.";
    private static final String NO_USERS_REGISTERED = "Nessun utente registrato. Effettua la registrazione.";
    private static final String LOGIN_SUCCESS = "Login effettuato con successo! Bentornato, ";
    private static final String LOGIN_FAILED = "Credenziali non valide. Riprova.";
    private static final String REGISTRATION_SUCCESS = "Registrazione effettuata con successo!";
    private static final String REGISTRATION_FAILED = "Registrazione fallita. Utente già esistente. Riprova.";
    private static final String EXIT_MESSAGE = "Grazie per aver utilizzato il programma. Arrivederci!";
    private static final String LOGOUT_MESSAGE = "Logout effettuato con successo.";
    private static final String NO_COMPRENSORI = "Nessun comprensorio presente.";
    private static final String COMPRENSORIO_CREATED = "Comprensorio creato con successo!";
    private static final String OPERATION_CANCELLED = "Operazione annullata.";
    private static final String USERS_FILE_PATH = "src/data/utenti.ser";
    private static final String COMPRESORI_FILE_PATH = "src/data/comprensori.ser";
    private static final String GERARCHIE_FILE_PATH = "src/data/gerarchie.ser";
    private static final String CONVERSIONE_FILE_PATH = "src/data/conversione.ser";
    public static final String MSG_SCELTA_ERRATA = "La scelta deve essere un valore intero compreso tra 1 e %d.";
    public static final String MSG_LISTA_GERARCHIE = "\nEcco una lista delle categorie radice delle gerarchie presenti. Le radici sono contrassegnate da un numero:";
    public static final String MSG_SCELTA_GERARCHIA = "Inserisci il numero corrispondente alla gerarchia scelta: ";
    public static final String MSG_LISTA_FOGLIE = "\nEcco la gerarchia scelta. Le foglie sono contrassegnate da un numero:";
    public static final String MSG_SCELTA_FOGLIA = "Inserisci il numero corrispondente alla categoria foglia scelta: ";

    public static final String GERARCHIA_CREATED = "Gerarchia creata con successo!";
    public static final String GERARCHIA_ADDED = "Gerarchia aggiunta al database con successo!";
    public static final String NO_GERARCHIE = "Nessuna gerarchia presente nel sistema.";
    public static final String CONVERSIONE_TITLE = "Generazione dei fattori di conversione...";

    private Database database;
    private final ConsoleView view;

    // -----------------------------------------------------------------------------------
    // METODI PRINCIPALI
    // -----------------------------------------------------------------------------------

    /**
     * Costruttore della classe Controller.
     * Inizializza il database e collega la vista.
     *
     * @param view la vista console utilizzata per l'interazione con l'utente
     */
    public Controller(ConsoleView view) {
        this.database = new Database();
        this.view = view;
    }

    /**
     * Avvia il ciclo principale del controller.
     * Carica i dati salvati, mostra il menu principale e gestisce le scelte
     * dell'utente.
     */
    public void start() {
        // Carica i dati prima di avviare l'applicazione
        loadData();

        // Ciclo principale dell'applicazione
        while (true) {
            try {
                // Mostra il menu principale e ottieni la scelta dell'utente
                view.showMenu();
                String choice = view.getInput("Cosa vuoi fare? ");

                switch (choice) {
                    case "1": // Login
                        handleLogin();
                        break;

                    case "2": // Registrazione
                        if (handleRegistration()) {
                            salvaUtenti();
                            view.showSuccessMessage("Dati utente salvati con successo");
                        }
                        break;

                    case "0": // Uscita
                        salvatutti();
                        view.showMessage(EXIT_MESSAGE);
                        return;

                    default:
                        view.showMessage(INVALID_OPTION);
                }

                // Pausa per dare il tempo all'utente di leggere i messaggi (opzionale)
                // view.pressEnterToContinue();

            } catch (Exception e) {
                // Gestione errori imprevisti per evitare che l'applicazione si arresti
                view.showErrorMessage("Si è verificato un errore: " + e.getMessage());
                e.printStackTrace();
                view.pressEnterToContinue();
            }
        }
    }

    /**
     * Carica tutti i dati dal disco.
     * Gestisce automaticamente i file mancanti o vuoti.
     */
    private void loadData() {
        view.showMessage("Caricamento dati in corso...");

        try {
            // Carica i dati utenti
            if (fileEsistente(USERS_FILE_PATH)) {
                this.database.deserializeUtenti(USERS_FILE_PATH);
                view.showMessage("✓ Dati utenti caricati");
            } else {
                view.showMessage("✓ Nessun dato utente trovato, si parte da zero");
            }

            // Carica i dati comprensori
            if (fileEsistente(COMPRESORI_FILE_PATH)) {
                this.database.deserializeComprensori(COMPRESORI_FILE_PATH);
                view.showMessage("✓ Dati comprensori caricati");
            } else {
                view.showMessage("✓ Nessun comprensorio trovato, si parte da zero");
            }

            // Carica i dati gerarchie
            if (fileEsistente(GERARCHIE_FILE_PATH)) {
                this.database.deserializeGerarchie(GERARCHIE_FILE_PATH);
                view.showMessage("✓ Dati gerarchie caricati");
            } else {
                view.showMessage("✓ Nessuna gerarchia trovata, si parte da zero");
            }

            // Carica i dati conversione gerarchie
            if (fileEsistente(CONVERSIONE_FILE_PATH)) {
                this.database.deserializeConversioneGerarchie(CONVERSIONE_FILE_PATH);
                view.showMessage("✓ Dati conversione gerarchie caricati");
            } else {
                view.showMessage("✓ Nessun dato di conversione trovato, si parte da zero");
            }

            view.showSuccessMessage("Caricamento dati completato con successo");
            // view.pressEnterToContinue();

        } catch (Exception e) {
            view.showErrorMessage("Errore durante il caricamento dei dati: " + e.getMessage());
            view.showMessage("L'applicazione continuerà con un database vuoto");
            this.database = new Database();
            view.pressEnterToContinue();
        }
    }

    /**
     * Verifica se un file esiste ed è non vuoto.
     * 
     * @param path percorso del file da verificare
     * @return true se il file esiste ed è non vuoto, false altrimenti
     */
    private boolean fileEsistente(String path) {
        java.io.File file = new java.io.File(path);
        return file.exists() && file.length() > 0;
    }

    /**
     * Salva i dati degli utenti su file.
     */
    private void salvaUtenti() {
        try {
            this.database.serializeUtenti(USERS_FILE_PATH);
        } catch (Exception e) {
            view.showErrorMessage("Errore durante il salvataggio degli utenti: " + e.getMessage());
        }
    }

    /**
     * Salva tutti i dati su file prima di uscire dall'applicazione.
     */
    private void salvatutti() {
        view.showMessage("Salvataggio dati in corso...");

        try {
            this.database.serializeUtenti(USERS_FILE_PATH);
            this.database.serializeComprensori(COMPRESORI_FILE_PATH);
            this.database.serializeGerarchie(GERARCHIE_FILE_PATH);
            this.database.serializeConversioneGerarchie(CONVERSIONE_FILE_PATH);
            view.showSuccessMessage("Dati salvati con successo");
        } catch (Exception e) {
            view.showErrorMessage("Errore durante il salvataggio dei dati: " + e.getMessage());
        }
    }

    /**
     * Restituisce il database gestito dal controller.
     *
     * @return il database contenente tutti i dati del sistema
     */
    public Database getDatabase() {
        return this.database;
    }

    // -----------------------------------------------------------------------------------
    // METODI DI GESTIONE MENU E SESSIONE
    // -----------------------------------------------------------------------------------

    /**
     * Gestisce il login dell'utente.
     * Verifica le credenziali e indirizza l'utente al menu appropriato in base al
     * tipo.
     */
    private void handleLogin() {
        if (database.getUtenti().isEmpty()) {
            view.showMessage(NO_USERS_REGISTERED);
            return;
        }

        Utente utente = loginUtente();
        if (utente == null) {
            view.showMessage(LOGIN_FAILED);
            return;
        }

        view.showMessage(LOGIN_SUCCESS + utente.getUsername() + ".");
        if (utente.isConfig()) {
            manageConfiguratore();
        } else {
            view.showMessage("TODO menu Fruitore");
        }
    }

    /**
     * Gestisce la registrazione dell'utente.
     * Raccoglie i dati necessari e registra l'utente nel database.
     *
     * @return true se l'utente è registrato correttamente, false altrimenti
     */
    private boolean handleRegistration() {
        boolean checkRegistration = registerUtente();
        view.showMessage(checkRegistration ? REGISTRATION_SUCCESS : REGISTRATION_FAILED);
        return checkRegistration;
    }

    /**
     * Gestisce il menu del configuratore.
     * Mostra il menu specifico e gestisce le varie funzionalità disponibili per
     * l'utente configuratore.
     * Implementa operazioni di visualizzazione e creazione di comprensori,
     * gerarchie e fattori di conversione.
     */
    private void manageConfiguratore() {
        while (true) {
            try {
                // Mostra il menu e ottiene la scelta dell'utente
                view.showMenuConfiguratore();
                String choice = view.getInput("Cosa vuoi fare? ");

                switch (choice) {
                    case "1": // Visualizza comprensori
                        visualizzaComprensori();
                        break;

                    case "2": // Visualizza gerarchie
                        visualizzaGerarchie();
                        break;

                    case "3": // Visualizza fattori di conversione
                        if (verificaPresenzaGerarchie()) {
                            visualizzaFattoriConversione();
                        }
                        break;

                    case "4": // Crea comprensorio
                        creaComprensorio();
                        break;

                    case "5": // Crea gerarchia
                        creaGerarchia(GERARCHIA_CREATED, GERARCHIA_ADDED, CONVERSIONE_TITLE);
                        break;

                    case "0": // Uscita
                        view.showMessage(LOGOUT_MESSAGE);
                        return;

                    default:
                        view.showMessage(INVALID_OPTION);
                }

                // Pausa per permettere all'utente di leggere i risultati
                view.pressEnterToContinue();

            } catch (Exception e) {
                // Gestione delle eccezioni generiche per garantire robustezza
                view.showErrorMessage("Si è verificato un errore: " + e.getMessage());
                view.pressEnterToContinue();
            }
        }
    }

    /**
     * Visualizza i comprensori presenti nel sistema.
     */
    private void visualizzaComprensori() {
        if (this.database.getComprensori().isEmpty()) {
            view.showMessage(NO_COMPRENSORI);
        } else {
            view.showMessage("\n=== COMPRENSORI DISPONIBILI ===");
            view.showMessage(database.toStringComprensori());
        }
    }

    /**
     * Visualizza le gerarchie presenti nel sistema.
     */
    private void visualizzaGerarchie() {
        if (this.database.getGerarchie().isEmpty()) {
            view.showMessage("Nessuna gerarchia presente nel sistema.");
        } else {
            view.showMessage("\n=== GERARCHIE DISPONIBILI ===");
            view.showMessage(this.database.toStringGerarchie());
        }
    }

    /**
     * Verifica la presenza di gerarchie nel sistema.
     * 
     * @return true se ci sono gerarchie, false altrimenti
     */
    private boolean verificaPresenzaGerarchie() {
        if (this.database.getGerarchie().isEmpty()) {
            this.view.showErrorMessage(
                    "Nessuna gerarchia presente nel sistema. Impossibile visualizzare fattori di conversione.");
            return false;
        }
        return true;
    }

    /**
     * Gestisce la creazione di un nuovo comprensorio.
     */
    private void creaComprensorio() {
        view.showMessage("\n=== CREAZIONE NUOVO COMPRENSORIO ===");

        if (createComprensorio()) {
            this.database.serializeComprensori(COMPRESORI_FILE_PATH);
            view.showSuccessMessage(COMPRENSORIO_CREATED);
        } else {
            view.showMessage(OPERATION_CANCELLED);
        }
    }

    /**
     * Gestisce la creazione di una nuova gerarchia e i relativi fattori di
     * conversione.
     * 
     * @param messaggioCreazione messaggio da mostrare alla creazione della
     *                           gerarchia
     * @param messaggioAggiunta  messaggio da mostrare all'aggiunta della gerarchia
     * @param titoloConversione  titolo da mostrare prima della generazione dei
     *                           fattori
     */
    private void creaGerarchia(String messaggioCreazione, String messaggioAggiunta, String titoloConversione) {
        view.showMessage("\n=== CREAZIONE NUOVA GERARCHIA ===");

        // Creazione della gerarchia
        Gerarchia gerarchia = createGerarchia();
        view.showSuccessMessage(messaggioCreazione);

        // Visualizzazione della struttura
        view.showMessage("\n" + gerarchia.toString());

        // Generazione dei fattori di conversione
        view.showMessage("\n" + titoloConversione);
        creaFattoriConversione(gerarchia);

        // Generazione delle conversioni tra gerarchie (se esistono altre gerarchie)
        if (!database.getGerarchie().isEmpty()) {
            view.showMessage("\n=== CONVERSIONI TRA GERARCHIE ===");
            creaConversioneGerarchie(gerarchia);
        }

        // Aggiunta della gerarchia al database
        if (this.database.addGerarchia(gerarchia)) {
            view.showSuccessMessage(messaggioAggiunta);
        }

        // Salvataggio dati
        this.database.serializeGerarchie(GERARCHIE_FILE_PATH);
        this.database.serializeConversioneGerarchie(CONVERSIONE_FILE_PATH);
    }

    // -----------------------------------------------------------------------------------
    // METODI DI AUTENTICAZIONE E REGISTRAZIONE
    // -----------------------------------------------------------------------------------

    /**
     * Effettua il login di un utente nel sistema.
     * L'utente può essere di tipo Utente (in futuro Fruitore) o Configuratore.
     * Viene richiesto all'utente di inserire username e password.
     *
     * @return l'utente autenticato se le credenziali sono corrette, null altrimenti
     */
    public Utente loginUtente() {
        String username = view.getInput("Inserisci username: ");
        String password = view.getInput("Inserisci password: ");
        return database.loginUtente(username, password);
    }

    /**
     * Registra un nuovo utente nel sistema.
     * Richiede all'utente di inserire username, password e tipo di account.
     *
     * @return true se l'utente è registrato correttamente, false altrimenti
     */
    public boolean registerUtente() {
        if (!defaultUtente())
            return false;

        String username = getUsername();
        if (username == null)
            return false;

        String password = getPassword();
        if (password == null)
            return false;

        boolean isConfig = getUserChoice("Sei un configuratore? (s/n): ");
        return database.registerUtente(username, password, isConfig);
    }

    /**
     * Effettua il login di un utente con credenziali predefinite generate
     * casualmente.
     * Utilizzato come misura di sicurezza durante la registrazione.
     *
     * @return true se il login va a buon fine, false altrimenti
     */
    private boolean defaultUtente() {
        String usernameDefault = UserUtil.generateRandomUsername();
        String passwordDefault = UserUtil.generateRandomPassword();

        view.showMessage("Effettua il login con le seguenti credenziali:");
        view.showMessage("Username: " + usernameDefault);
        view.showMessage("Password: " + passwordDefault);

        for (int i = 2; i >= 0; i--) {
            String username = view.getInput("Inserisci username: ");
            String password = view.getInput("Inserisci password: ");
            if (usernameDefault.equals(username) && passwordDefault.equals(password))
                return true;
            view.showMessage("Credenziali non valide. Tentativi rimasti: " + i);
        }
        view.showMessage("Login non riuscito. Riprova più tardi.");
        return false;
    }

    // -----------------------------------------------------------------------------------
    // METODI PER LA GESTIONE DEI COMPRENSORI
    // -----------------------------------------------------------------------------------

    /**
     * Crea un nuovo comprensorio nel sistema.
     * Richiede all'utente di inserire nome e comuni.
     *
     * @return true se il comprensorio è stato creato con successo, false altrimenti
     */
    public boolean createComprensorio() {
        while (true) {
            String nome = view.getInput("Inserisci il nome del comprensorio. Premi 0 per annullare: ");
            if (nome.isBlank()) {
                view.showMessage("Nome non valido");
                continue;
            }
            if (nome.equals("0"))
                return false;
            if (database.hasComprensorio(nome)) {
                view.showMessage("Comprensorio già esistente");
                continue;
            }

            Comprensorio comprensorio = new Comprensorio(nome);
            addComuniToComprensorio(comprensorio);
            return database.addComprensorio(comprensorio);
        }
    }

    /**
     * Aggiunge comuni a un comprensorio esistente.
     * Continua a richiedere nomi di comuni finché l'utente non termina
     * l'operazione.
     *
     * @param comprensorio il comprensorio a cui aggiungere i comuni
     */
    private void addComuniToComprensorio(Comprensorio comprensorio) {
        boolean finished = false;
        while (!finished) {
            String comune = this.view.getInput("Inserisci il nome del comune. Premi 0 per terminare: ");

            if (comune.isBlank()) {
                this.view.showMessage("Nome non valido");
                continue;
            }

            if (comune.equals("0")) {
                if (comprensorio.getComuni().isEmpty()) {
                    this.view.showMessage("Il comprensorio deve contenere almeno un comune");
                } else {
                    finished = true;
                }
                continue;
            }

            if (!comprensorio.addComune(comune)) {
                this.view.showMessage("Comune già esistente");
            }
        }
    }

    // -----------------------------------------------------------------------------------
    // METODI PER LA GESTIONE DELLE GERARCHIE
    // -----------------------------------------------------------------------------------

    /**
     * Crea una nuova gerarchia nel sistema.
     * Costruisce l'intera struttura gerarchica con categorie e campi.
     *
     * @return la gerarchia creata
     */
    private Gerarchia createGerarchia() {
        List<Gerarchia> categorieFoglia = new ArrayList<>();
        int maxLevel = 0;
        Gerarchia rootGerarchia = new Gerarchia(createCategoria(null, null));
        Stack<Gerarchia> stack = new Stack<>(); // TODO: valutare utilizzo deque
        Categoria categoria = (Categoria) rootGerarchia.root().data();

        for (int i = 0; i < categoria.getCampo().getDominio().size(); i++) {
            stack.push(rootGerarchia);
        }
        while (!stack.isEmpty()) {
            Gerarchia theGoodFather = stack.peek(); // prendo il padre attuale per la visualizzazione
            Categoria categoriaPadre = (Categoria) theGoodFather.data(); // prendo la categoria del padre
            String nomeCategoriaPadre = categoriaPadre.getNome(); // prendo il nome della categoria del padre
            Gerarchia gerarchia = new Gerarchia(createCategoria(nomeCategoriaPadre, rootGerarchia));

            Categoria c = (Categoria) gerarchia.root().data(); // prendo la categoria appena creata
            Campo campo = c.getCampo(); // prendo il campo della categoria appena creata
            Gerarchia padre = stack.pop(); // prendo il padre attuale
            padre.add(gerarchia); // aggiungo la categoria appena creata al padre attuale
            if (campo != null) { // se il campo non è nullo aggiungo i valori del campo nello stack
                for (int i = 0; i < campo.getDominio().size(); i++) { // aggiungo n gerarchie dove n è il numero di
                                                                      // valori del campo
                    stack.push(gerarchia);
                }
            } else {
                if (gerarchia.level() > maxLevel) { // aggiunge la prima categoria foglia di ogni ramo
                    maxLevel = gerarchia.level();
                    categorieFoglia.add(gerarchia);
                }

            }
        }
        // System.out.println("Categorie foglia: " + categorieFoglia);
        return rootGerarchia;
    }

    /**
     * Crea una categoria per una gerarchia.
     * Questo metodo chiama il metodo {@link #createCampo()} per aggiungere un campo
     * caratteristico alla categoria.
     *
     * @param nomePadre il nome della categoria padre, null se è la categoria radice
     * @param root      la gerarchia radice, null se è la categoria radice
     * @return la categoria creata
     */
    private Categoria createCategoria(String nomePadre, Gerarchia root) {
        String nome;
        while (true) {
            // TODO: aggiungere rifermento al campo di appartenenza della categoria che si
            // sta creando
            nome = this.view.getInput("Inserisci il nome della categoria"
                    + (root == null ? " radice" : " figlia di " + (nomePadre)) + ": ");
            if (nome.isBlank()) {
                this.view.showMessage("Nome non valido");
                continue;
            }
            if ((root == null && database.hasGerarchia(nome))
                    || (root != null && root.find(new Categoria(nome)) != null)) {
                view.showMessage(root == null ? "Esiste già una gerarchia con questo nome" : "Categoria già esistente");
                continue;
            }
            break;
        }
        Campo campo = (root == null
                || getUserChoice("Vuoi aggiungere un campo caratteristico a questa categoria? (s/n): ")) ? createCampo()
                        : null;
        return new Categoria(nome, campo);
    }

    /**
     * Crea un campo caratteristico per una categoria.
     * Questo metodo chiama il metodo {@link #createDominio(Campo)} per aggiungere
     * un valori di dominio al campo.
     *
     * @return il campo creato con il suo dominio
     */
    private Campo createCampo() {
        String nome;
        do {
            nome = this.view.getInput("Inserisci il nome del campo: ");
            if (nome.isBlank()) {
                this.view.showMessage("Nome non valido");
            }
        } while (nome.isBlank());
        Campo campo = new Campo(nome);
        do {
            createDominio(campo);
            if (campo.getDominio().isEmpty()) {
                this.view.showMessage("Il campo deve contenere almeno un valore di dominio");
            }
        } while (campo.getDominio().isEmpty());
        return campo;
    }

    /**
     * Crea un dominio di valori per un campo.
     * Aggiunge uno o più valori con relative descrizioni.
     *
     * @param campo il campo a cui aggiungere il dominio
     */
    private void createDominio(Campo campo) {
        boolean finished = false;
        while (!finished) {
            String nomeValore = this.view.getInput("Inserisci il nome del valore di dominio. Premi 0 per terminare: ");
            if (nomeValore.isBlank()) {
                this.view.showMessage("Nome non valido");
                continue;
            }
            if (nomeValore.equals("0"))
                break;
            if (campo.hasValore(nomeValore)) {
                this.view.showMessage("Valore di dominio già esistente");
                continue;
            }
            String descrizione = this.view
                    .getInput("Inserisci una descrizione a questo valore di dominio (facoltativa): ");
            campo.addValore(
                    descrizione.isBlank() ? new ValoreCampo(nomeValore) : new ValoreCampo(nomeValore, descrizione));

            this.view.showMessage("Valore di dominio aggiunto con successo");
            if (!getUserChoice("Vuoi aggiungere un altro valore di dominio? (s/n): "))
                finished = true;
        }
    }

    // -----------------------------------------------------------------------------------
    // METODI PER LA GESTIONE DEI FATTORI DI CONVERSIONE
    // -----------------------------------------------------------------------------------

    /**
     * Visualizza i fattori di conversione tra gerarchie e categorie.
     * Guida l'utente nella selezione di gerarchie e categorie per cui visualizzare
     * le conversioni.
     */
    private void visualizzaFattoriConversione() {
        // Prima selezione: gerarchia origine
        this.view.showMessage(MSG_LISTA_GERARCHIE);
        Gerarchia gerarchiaOrigine = selezionaGerarchia();

        // Selezione della foglia dalla gerarchia origine
        this.view.showMessage(MSG_LISTA_FOGLIE);
        this.view.showMessage(gerarchiaOrigine.toStringSelezioneFoglia());

        TreeNode<Categoria> fogliaScelta = selezionaFoglia(gerarchiaOrigine);

        // Seconda selezione: gerarchia destinazione
        this.view.showMessage(String.format(
                "\nOra seleziona la gerarchia di cui vuoi visualizzare i fattori di conversione relativi alla categoria \"%s\":\n",
                fogliaScelta.data().getNome()));

        Gerarchia gerarchiaDestinazione = selezionaGerarchia();

        // Calcolo del fattore di conversione combinato
        double fattoreIntraGerarchia = gerarchiaOrigine.getFattoreConversione()
                .getFattoreConversione(fogliaScelta.level(),
                        gerarchiaOrigine.getFattoreConversione().getFogliaRiferimento().level());

        double fattoreInterGerarchia = 1.0;
        if (!gerarchiaOrigine.equals(gerarchiaDestinazione)) {
            fattoreInterGerarchia = this.database.getConversioneGerarchie().getFattoreConversione(
                    gerarchiaOrigine.getFattoreConversione().getFogliaRiferimento().data(),
                    gerarchiaDestinazione.getFattoreConversione().getFogliaRiferimento().data());
        }

        // Visualizzazione dei risultati
        this.view.showMessage(String.format("\nEcco i fattori di conversione relativi alla categoria \"%s\":",
                fogliaScelta.data().getNome()));

        this.view.showMessage(
                gerarchiaDestinazione.toStringFattoreConversione(fattoreIntraGerarchia * fattoreInterGerarchia));
    }

    /**
     * Mostra la lista delle gerarchie e chiede all'utente di selezionarne una.
     * 
     * @return la gerarchia selezionata
     */
    private Gerarchia selezionaGerarchia() {
        // Visualizzazione delle gerarchie
        List<Gerarchia> gerarchieList = new ArrayList<>(this.database.getGerarchie());
        for (int i = 0; i < gerarchieList.size(); i++) {
            Gerarchia gerarchia = gerarchieList.get(i);
            Categoria categoria = (Categoria) gerarchia.root().data();
            this.view.showMessage("[" + (i + 1) + "] " + categoria.getNome());
        }
        this.view.showMessage("");

        // Selezione della gerarchia
        int numScelte = this.database.getGerarchie().size();
        int indiceScelta = richiediIndiceDaUtente(MSG_SCELTA_GERARCHIA, numScelte) - 1;

        return this.database.getGerarchia(indiceScelta);
    }

    /**
     * Chiede all'utente di selezionare una foglia dalla gerarchia specificata.
     * 
     * @param gerarchia la gerarchia da cui selezionare la foglia
     * @return la foglia selezionata
     */
    private TreeNode<Categoria> selezionaFoglia(Gerarchia gerarchia) {
        ArrayList<TreeNode<Categoria>> foglie = gerarchia.getFoglie();
        int numScelte = foglie.size();
        int indiceScelta = richiediIndiceDaUtente(MSG_SCELTA_FOGLIA, numScelte) - 1;

        return foglie.get(indiceScelta);
    }

    /**
     * Richiede all'utente di inserire un indice valido.
     * 
     * @param messaggio il messaggio da mostrare all'utente
     * @param numScelte il numero massimo di scelte disponibili
     * @return l'indice selezionato (a partire da 1)
     */
    private int richiediIndiceDaUtente(String messaggio, int numScelte) {
        boolean validInput = false;
        int indiceScelta = 0;

        while (!validInput) {
            String scelta = this.view.getInput(messaggio);
            try {
                indiceScelta = Integer.parseInt(scelta);
                if (indiceScelta > 0 && indiceScelta <= numScelte) {
                    validInput = true;
                } else {
                    this.view.showMessage(String.format(MSG_SCELTA_ERRATA, numScelte));
                }
            } catch (NumberFormatException e) {
                this.view.showMessage(String.format(MSG_SCELTA_ERRATA, numScelte));
            }
        }

        return indiceScelta;
    }

    /**
     * Crea i fattori di conversione tra una nuova gerarchia e tutte le gerarchie
     * esistenti.
     * Per ogni coppia di gerarchie, richiede all'utente di inserire un fattore di
     * conversione.
     *
     * @param gerarchia la nuova gerarchia per cui creare le conversioni
     */
    private void creaConversioneGerarchie(Gerarchia gerarchia) {
        TreeNode<Categoria> fogliaRiferimento = gerarchia.getFattoreConversione().getFogliaRiferimento();
        for (Gerarchia g : database.getGerarchie()) {
            TreeNode<Categoria> secondaFogliaRiferimento = g.getFattoreConversione().getFogliaRiferimento();
            // TODO: inserisci anche il riferimento alle gerarchie in modo carino (un vero
            // tesoro)
            boolean validInput = false;

            while (!validInput) {
                String value = this.view.getInput(String.format(
                        "Inserisci il fattore di conversione tra la categoria \"%s\" e la categoria \"%s\": ",
                        fogliaRiferimento.data().getNome(), secondaFogliaRiferimento.data().getNome()));

                try {
                    double fattore = Double.parseDouble(value);
                    if (fattore >= 0.5 && fattore <= 2) {
                        validInput = true;
                        this.database.getConversioneGerarchie().addFattoreConversione(fogliaRiferimento.data(),
                                secondaFogliaRiferimento.data(), fattore);
                        view.showMessage("Fattore di conversione aggiunto");
                    } else {
                        view.showMessage(
                                "Il fattore di conversione deve essere un valore decimale compreso tra 0.5 e 2.");
                    }

                } catch (NumberFormatException e) {
                    view.showMessage("Il fattore di conversione deve essere un valore decimale compreso tra 0.5 e 2.");
                }
            }
        }
    }

    /**
     * Crea i fattori di conversione tra le categorie foglia all'interno di una
     * gerarchia.
     * Imposta una foglia come riferimento e definisce i fattori di conversione per
     * le altre.
     *
     * @param gerarchia la gerarchia per cui creare i fattori di conversione
     */
    private void creaFattoriConversione(Gerarchia gerarchia) {
        // Ottiene tutte le foglie della gerarchia
        ArrayList<TreeNode<Categoria>> foglie = gerarchia.getFogliePerLivello();

        // Rimuove la prima foglia e la imposta come foglia di riferimento
        TreeNode<Categoria> fogliaRiferimento = foglie.removeFirst();
        gerarchia.getFattoreConversione().setNodoRiferimento(fogliaRiferimento);

        // Itera su tutte le foglie rimanenti
        for (TreeNode<Categoria> foglia : foglie) {
            boolean isSpecializzata = foglia.level() > fogliaRiferimento.level();
            double minFattore = isSpecializzata ? 1.0 : 0.5;
            double maxFattore = isSpecializzata ? 2.0 : 1.0;
            String messaggioErrore = isSpecializzata
                    ? "Il fattore di conversione viene consigliato maggiore di 1 e massimo 2 per attività più specializzate."
                    : "Il fattore di conversione deve essere compreso tra 0.5 e meno di 1 per attività meno specializzate.";

            // Richiede il fattore di conversione
            double fattore = richiediEValidaFattore(
                    fogliaRiferimento.data().getNome(),
                    foglia.data().getNome(),
                    minFattore,
                    maxFattore,
                    messaggioErrore);

            // Arrotonda il fattore a due cifre decimali
            fattore = Math.round(fattore * 100) / 100.0;

            // Aggiunge il fattore di conversione
            gerarchia.getFattoreConversione().addFattoreConversione(foglia.level(), fattore);
            view.showMessage("Fattore di conversione aggiunto");
        }
    }

    /**
     * Richiede e valida un fattore di conversione dall'utente.
     *
     * @param categoriaOrigine      nome della categoria di origine
     * @param categoriaDestinazione nome della categoria di destinazione
     * @param minFattore            valore minimo accettabile
     * @param maxFattore            valore massimo accettabile
     * @param messaggioErrore       messaggio di errore per valori non validi
     * @return fattore di conversione valido
     */
    private double richiediEValidaFattore(String categoriaOrigine, String categoriaDestinazione,
            double minFattore, double maxFattore, String messaggioErrore) {
        while (true) {
            String value = view.getInput(
                    String.format("Inserisci il fattore di conversione tra la categoria \"%s\" e la categoria \"%s\": ",
                            categoriaOrigine, categoriaDestinazione));
            try {
                double fattore = Double.parseDouble(value);
                if (fattore >= minFattore && fattore <= maxFattore) {
                    return fattore;
                } else {
                    view.showMessage(messaggioErrore);
                }
            } catch (NumberFormatException e) {
                view.showMessage("Il fattore di conversione deve essere un valore decimale compreso tra 0.5 e 2.");
            }
        }
    }

    // -----------------------------------------------------------------------------------
    // METODI DI SUPPORTO PER L'INPUT UTENTE
    // -----------------------------------------------------------------------------------

    /**
     * Ottiene un username valido dall'utente.
     * Verifica che lo username rispetti i requisiti di sicurezza.
     *
     * @return lo username valido, null se il processo è annullato
     */
    private String getUsername() {
        while (true) {
            String username = view.getInput(String.format(
                    "Inserisci un nuovo username (non vuoto, senza spazi, lungo massimo %d caratteri, senza caratteri speciali): ",
                    UserUtil.USERNAME_LENGTH));
            if (UserUtil.checkUsername(username))
                return username;
            view.showMessage("Username non valido. Riprova.");
        }
    }

    /**
     * Ottiene una password valida dall'utente.
     * Verifica che la password rispetti i requisiti minimi di sicurezza.
     *
     * @return la password valida, null se il processo è annullato
     */
    private String getPassword() {
        while (true) {
            String password = view.getInput(String.format(
                    "Inserisci una nuova password (lunga almeno %d caratteri, contenere almeno: una lettera maiuscola, una minuscola e un carattere speciale): ",
                    UserUtil.PASSWORD_LENGTH));
            if (UserUtil.checkPassword(password))
                return password;
            view.showMessage("Password non valida. Riprova.");
        }
    }

    /**
     * Ottiene una scelta booleana dall'utente (sì/no).
     * Continua a richiedere l'input finché non viene fornita una risposta valida.
     *
     * @param messaggio il messaggio da mostrare per richiedere la scelta
     * @return true se la risposta è "s", false se è "n"
     */
    private boolean getUserChoice(String messaggio) {
        while (true) {
            String role = view.getInput(messaggio);
            if (role.equalsIgnoreCase("s"))
                return true;
            if (role.equalsIgnoreCase("n"))
                return false;
            view.showMessage("Input non valido. Riprova.");
        }
    }
}