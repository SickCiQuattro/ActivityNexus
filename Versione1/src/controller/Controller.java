package controller;

import model.Database;
import model.Utente;
import model.Comprensorio;
import util.UserUtil;
import view.ConsoleView;

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
                case "1":
                    handleLogin();
                    break;
                case "2":
                    boolean checkRegistration = handleRegistration();
                    if (checkRegistration) {
                        this.database.serializeUtenti(USERS_FILE_PATH);
                    }
                    break;
                case "0":
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
                case "1":
                    view.showMessage(database.getComprensori().isEmpty() ? NO_COMPRENSORI : database.toStringComprensori());
                    break;
                case "2":
                    // TODO: Visualizza gerarchie
                    break;
                case "3":
                    // TODO: Visualizza fattori di conversione
                    break;
                case "4":
                    if (createComprensorio()) {
                        this.database.serializeComprensori(COMPRESORI_FILE_PATH);
                        view.showMessage(COMPRENSORIO_CREATED);
                    } else {
                        view.showMessage(OPERATION_CANCELLED);
                    }
                    break;
                case "5":
                    // TODO: Crea gerarchia
                    break;
                case "0":
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

        boolean isConfig = getUserRole();
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
     * Ottiene il ruolo dell'utente.
     *
     * @return true se l'utente è un configuratore, false altrimenti
     */
    private boolean getUserRole() {
        while (true) {
            String role = view.getInput("Sei un configuratore? (s/n): ");
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
        while (true) {
            String comune = view.getInput("Inserisci il nome del comune. Premi 0 per terminare: ");
            if (comune.isBlank()) {
                view.showMessage("Nome non valido");
                continue;
            }
            if (comune.equals("0")) break;
            if (!comprensorio.addComune(comune)) {
                view.showMessage("Comune già esistente");
            }
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