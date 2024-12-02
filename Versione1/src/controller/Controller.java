package controller;

import model.*;
import util.UserUtil;
import view.ConsoleView;

import java.util.Stack;

/**
 * Classe Controller che gestisce la logica dell'applicazione.
 * Si occupa di gestire le interazioni tra la vista e il modello.
 */
public class Controller {
    private static final String INVALID_OPTION = "Opzione non valida. Riprova.";
    private static final String NO_USERS_REGISTERED = "Nessun utente registrato. Effettua la registrazione.";
    private static final String LOGIN_SUCCESS = "Login effettuato con successo! Bentornato, ";
    private static final String LOGIN_FAILED = "Credenziali non valide. Riprova.";
    private static final String REGISTRATION_SUCCESS = "Registrazione effettuata con successo!";
    private static final String REGISTRATION_FAILED = "Registrazione fallita. Utente già esistente. Riprova.";
    private static final String EXIT_MESSAGE = "Grazie per aver utilizzato il programma. Arrivederci!";
    private static final String NO_COMPRENSORI = "Nessun comprensorio presente.";
    private static final String COMPRENSORIO_CREATED = "Comprensorio creato con successo!";
    private static final String OPERATION_CANCELLED = "Operazione annullata.";
    private static final String USERS_FILE_PATH = "src/data/utenti.ser";
    private static final String COMPRESORI_FILE_PATH = "src/data/comprensori.ser";

    private Database database;
    private final ConsoleView view;

    /**
     * Costruttore della classe Controller.
     *
     * @param view la vista console utilizzata per l'interazione con l'utente
     */
    public Controller(ConsoleView view) {
        this.database = new Database();
        this.view = view;
    }

    /**
     * Avvia il ciclo principale del controller.
     * Mostra il menu principale e gestisce le scelte dell'utente.
     */
    public void start() {
        this.database.deserializeUtenti(USERS_FILE_PATH);
        this.database.deserializeComprensori(COMPRESORI_FILE_PATH);
        while (true) {
            view.showMenu();
            String choice = view.getInput("Cosa vuoi fare? ");
            switch (choice) {
                case "1": // Login
                    handleLogin();
                    break;
                case "2": // Registrazione
                    boolean checkRegistration = handleRegistration();
                    if (checkRegistration) {
                        this.database.serializeUtenti(USERS_FILE_PATH);
                    }
                    break;
                case "0": // Uscita
                    view.showMessage(EXIT_MESSAGE);
                    return;
                default:
                    view.showMessage(INVALID_OPTION);
            }
        }
    }

    /**
     * Gestisce il login dell'utente.
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
     */
    private boolean handleRegistration() {
        boolean checkRegistration = registerUtente();
        view.showMessage(checkRegistration ? REGISTRATION_SUCCESS : REGISTRATION_FAILED);
        return checkRegistration;
    }

    /**
     * Gestisce il menu del configuratore.
     * Mostra il menu e gestisce le scelte dell'utente configuratore.
     */
    private void manageConfiguratore() {
        while (true) {
            view.showMenuConfiguratore();
            String choice = view.getInput("Cosa vuoi fare? ");
            switch (choice) {
                case "1": // Visualizza comprensori
                    view.showMessage(this.database.getComprensori().isEmpty() ? NO_COMPRENSORI : database.toStringComprensori());
                    break;
                case "2": // Visualizza gerarchie
                    this.view.showMessage(this.database.getGerarchie().isEmpty() ? "Nessuna gerarchia presente." : this.database.toStringGerarchie());

                    break;
                case "3": // Visualizza fattori di conversione
                    // TODO: Visualizza fattori di conversione
                    break;
                case "4": // Crea comprensorio
                    if (createComprensorio()) {
                        this.database.serializeComprensori(COMPRESORI_FILE_PATH);
                        view.showMessage(COMPRENSORIO_CREATED);
                    } else {
                        view.showMessage(OPERATION_CANCELLED);
                    }
                    break;
                case "5": // Crea gerarchia
                    Gerarchia gerarchia = createGerarchia();
                    if(gerarchia != null){
                        this.database.addGerarchia(gerarchia);
                        view.showMessage("Gerarchia creata con successo");
                    }
                    else
                        view.showMessage("Creazione gerarchia fallita");
                    break;
                case "0": // Uscita
                    view.showMessage(EXIT_MESSAGE);
                    return;
                default:
                    view.showMessage(INVALID_OPTION);
            }
        }
    }

    /**
     * Registra un nuovo utente nel sistema.
     *
     * @return true se l'utente è registrato correttamente, false altrimenti
     */
    public boolean registerUtente() {
        if (!defaultUtente()) return false;

        String username = getUsername();
        if (username == null) return false;

        String password = getPassword();
        if (password == null) return false;

        boolean isConfig = getUserChoice("Sei un configuratore? (s/n): ");
        return database.registerUtente(username, password, isConfig);
    }

    /**
     * Ottiene un username valido dall'utente.
     *
     * @return lo username valido, null se non valido
     */
    private String getUsername() {
        while (true) {
            String username = view.getInput(String.format("Inserisci un nuovo username (non vuoto, senza spazi, lungo massimo %d caratteri, senza caratteri speciali): ", UserUtil.USERNAME_LENGTH));
            if (UserUtil.checkUsername(username)) return username;
            view.showMessage("Username non valido. Riprova.");
        }
    }

    /**
     * Ottiene una password valida dall'utente.
     *
     * @return la password valida, null se non valida
     */
    private String getPassword() {
        while (true) {
            String password = view.getInput(String.format("Inserisci una nuova password (lunga almeno %d caratteri, contenere almeno: una lettera maiuscola, una minuscola e un carattere speciale): ", UserUtil.PASSWORD_LENGTH));
            if (UserUtil.checkPassword(password)) return password;
            view.showMessage("Password non valida. Riprova.");
        }
    }

    /**
     * Ottiene la scelta dell'utente.
     *
     * @return true se l'utente preme s, false se preme n
     */
    private boolean getUserChoice(String messaggio) {
        while (true) {
            String role = view.getInput(messaggio);
            if (role.equalsIgnoreCase("s")) return true;
            if (role.equalsIgnoreCase("n")) return false;
            view.showMessage("Input non valido. Riprova.");
        }
    }

    public Database getDatabase() {
        return this.database;
    }

    /**
     * Effettua il login di un utente nel sistema.
     * L'utente può essere di tipo Utente (in futuro Fruitore) o Configuratore.
     * Viene richiesto all'utente di inserire username e password.
     *
     * @return l'utente loggato, null se le credenziali non sono valide
     */
    public Utente loginUtente() {
        String username = view.getInput("Inserisci username: ");
        String password = view.getInput("Inserisci password: ");
        return database.loginUtente(username, password);
    }

    /**
     * Crea un nuovo comprensorio nel sistema.
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
            if (nome.equals("0")) return false;
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
     * Aggiunge comuni a un comprensorio.
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
                finished = true;
                continue;
            }
            if (!comprensorio.addComune(comune)) {
                this.view.showMessage("Comune già esistente");
            }
        }
    }


    private Gerarchia createGerarchia() {
        Gerarchia g = new Gerarchia(createCategoria(true, ""));

        Stack<Gerarchia> stack = new Stack<>();
        Categoria categoria = (Categoria) g.root().data();

        for (int i=0; i < categoria.getCampo().getDominio().size(); i++) {
            stack.push(g);
        }

        while (!stack.isEmpty()) {
            Gerarchia theGoodFather = stack.peek();
            Categoria categoriaPadre = (Categoria) theGoodFather.data();
            String nomeCategoriaPadre = categoriaPadre.getNome();
            Gerarchia gerarchia = new Gerarchia(createCategoria(false, nomeCategoriaPadre));
            Categoria c = (Categoria) gerarchia.root().data();
            Campo campo = c.getCampo();
            Gerarchia padre = stack.pop();
            padre.add(gerarchia);
            if (campo != null){
                for (int i=0; i < campo.getDominio().size(); i++) {
                    stack.push(gerarchia);
                }
            }
        }


        return g;
    }

    /**
     * Crea una categoria di attività per un comprensorio.
     * Questo metodo chiama il metodo {@link #createCampo()} per aggiungere un campo di attività alla categoria.
     * @return la categoria creata
     */
    private Categoria createCategoria(boolean isRoot, String nomePadre) {
        String nome;
        while (true) {
            nome = this.view.getInput("Inserisci il nome della categoria " + (isRoot ? "radice: " : "figlia di " + (nomePadre)+ ": "));
            if (nome.isBlank()) {
                this.view.showMessage("Nome non valido");
                continue;
            }
            if (isRoot && this.database.hasGerarchia(nome)) {
                this.view.showMessage("Categoria già esistente");
                continue;
            }
            break;
        }

        Campo campo;

        boolean choice = this.getUserChoice("Vuoi aggiungere un campo di attività a questa categoria? (s/n): ");

        if (isRoot || choice) {
            campo = this.createCampo();
            return new Categoria(nome, campo);
        }

        return new Categoria(nome);
    }

    /**
     * Crea un campo di attività per un comprensorio.
     * Questo metodo chiama il metodo {@link #createDominio(Campo)} per aggiungere attività al campo.
     * @return il campo creato
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
                this.view.showMessage("Il campo deve contenere almeno un'attività");
                // TODO: dare la possibiltò di annullare completamente l'operazione di creazione del campo? allora sei un utente coglione
            }
        } while (campo.getDominio().isEmpty());

        return campo;
    }

    /**
     * Crea un dominio di attività per un campo.
     * // TODO: deve essere davvero void? si
     * @param campo il campo a cui aggiungere il dominio
     */
    private void createDominio(Campo campo) {
        boolean finished = false;
        while (!finished) {
            String nomeAttivita = this.view.getInput("Inserisci il nome dell'attività. Premi 0 per terminare: ");
            if (nomeAttivita.isBlank()) {
                this.view.showMessage("Nome non valido");
                continue;
            }
            if (nomeAttivita.equals("0")) break;
            if (campo.hasAttivita(nomeAttivita)) {
                this.view.showMessage("Attività già esistente");
                continue;
            }

            String descrizione = this.view.getInput("Inserisci una descrizione a questa attività (facoltativa): ");
            campo.addAttivita(descrizione.isBlank() ? new Attivita(nomeAttivita) : new Attivita(nomeAttivita, descrizione));

            this.view.showMessage("Attività aggiunta con successo");

            if (!getUserChoice("Vuoi aggiungere un'altra attività? (s/n): "))
                finished = true;
        }
    }

    /**
    * Effettua il login di un utente nel sistema con credenziali predefinite.
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
            if (usernameDefault.equals(username) && passwordDefault.equals(password)) return true;
            view.showMessage("Credenziali non valide. Tentativi rimasti: " + i);
        }
        view.showMessage("Login non riuscito. Riprova più tardi.");
        return false;
    }
}