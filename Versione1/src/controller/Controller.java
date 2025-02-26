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
    private static final String LOGOUT_MESSAGE = "Logout effettuato con successo.";
    private static final String NO_COMPRENSORI = "Nessun comprensorio presente.";
    private static final String COMPRENSORIO_CREATED = "Comprensorio creato con successo!";
    private static final String OPERATION_CANCELLED = "Operazione annullata.";
    private static final String USERS_FILE_PATH = "src/data/utenti.ser";
    private static final String COMPRESORI_FILE_PATH = "src/data/comprensori.ser";
    private static final String GERARCHIE_FILE_PATH = "src/data/gerarchie.ser";
    private static final String CONVERSIONE_FILE_PATH = "src/data/conversione.ser";

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
        //TODO: controllo file vuoti o meno per la serializzazione
        this.database.deserializeUtenti(USERS_FILE_PATH);
        this.database.deserializeComprensori(COMPRESORI_FILE_PATH);
        this.database.deserializeGerarchie(GERARCHIE_FILE_PATH);
        this.database.deserializeConversioneGerarchie(CONVERSIONE_FILE_PATH);
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
                    if (!this.database.getGerarchie().isEmpty()) {
                        visualizzaFattoriConversione();
                    }
                    else{
                        this.view.showMessage("Nessuna gerarchia presente.");
                    }
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
                    view.showMessage("Gerarchia creata con successo");
                    view.showMessage("Generazione dei fattori di conversione...");
                    view.showMessage(gerarchia.toString());
                    creaFattoriConversione(gerarchia);
                    creaConversioneGerarchie(gerarchia);
                    if (this.database.addGerarchia(gerarchia))
                        this.view.showMessage("Gerarchia aggiunta con successo");
                    this.database.serializeGerarchie(GERARCHIE_FILE_PATH);
                    this.database.serializeConversioneGerarchie(CONVERSIONE_FILE_PATH);
                    break;
                case "0": // Uscita
                    view.showMessage(LOGOUT_MESSAGE);
                    return;
                default:
                    view.showMessage(INVALID_OPTION);
            }
        }
    }

    private void visualizzaFattoriConversione() {
        // gerarchie
        List<Gerarchia> gerarchieList = new ArrayList<>(this.database.getGerarchie());
        for (int i = 0; i < gerarchieList.size(); i++) {
            Gerarchia gerarchia = gerarchieList.get(i);
            Categoria categoria = (Categoria)gerarchia.root().data();
            this.view.showMessage("[" + (i + 1) + "] " + categoria.getNome());
        }

        int indiceScelta = 0;
        boolean validInput = false;
        int numScelte = this.database.getGerarchie().size();
        while (!validInput) {
            String scelta = this.view.getInput("Seleziona Gerarchia: ");

            try{
                indiceScelta = Integer.parseInt(scelta);
                if (indiceScelta > 0 && indiceScelta <= numScelte){
                    validInput = true;
                    indiceScelta -= 1;
                }
            } catch (NumberFormatException e) {
                this.view.showMessage(String.format("La scelta deve essere un valore intero compreso tra 1 e %d", numScelte));
            }
        }

        // foglie
        Gerarchia gerarchiaScelta = this.database.getGerarchia(indiceScelta);
        this.view.showMessage(gerarchiaScelta.toStringSelezioneFoglia());

        indiceScelta = 0;
        validInput = false;
        ArrayList<TreeNode<Categoria>> foglie = gerarchiaScelta.getFoglie();
        numScelte = foglie.size();

        while(!validInput){
            String scelta = this.view.getInput("Seleziona Foglia: ");
            try{
                indiceScelta = Integer.parseInt(scelta);
                if (indiceScelta > 0 && indiceScelta <= numScelte){
                    validInput = true;
                    indiceScelta -= 1;
                }
            } catch (NumberFormatException e) {
                this.view.showMessage(String.format("La scelta deve essere un valore intero compreso tra 1 e %d", numScelte));
            }
        }

        TreeNode<Categoria> fogliaScelta = foglie.get(indiceScelta);

        // gerarchie
        gerarchieList = new ArrayList<>(this.database.getGerarchie());
        for (int i = 0; i < gerarchieList.size(); i++) {
            Gerarchia gerarchia = gerarchieList.get(i);
            Categoria categoria = (Categoria)gerarchia.root().data();
            this.view.showMessage("[" + (i + 1) + "] " + categoria.getNome());
        }

        indiceScelta = 0;
        validInput = false;
        numScelte = this.database.getGerarchie().size();
        while (!validInput) {
            String scelta = this.view.getInput("Seleziona Gerarchia: ");

            try{
                indiceScelta = Integer.parseInt(scelta);
                if (indiceScelta > 0 && indiceScelta <= numScelte){
                    validInput = true;
                    indiceScelta -= 1;
                }
            } catch (NumberFormatException e) {
                this.view.showMessage(String.format("La scelta deve essere un valore intero compreso tra 1 e %d", numScelte));
            }
        }

        // foglie
        Gerarchia gerarchiaConversione = this.database.getGerarchia(indiceScelta);

        double a = gerarchiaScelta.getFattoreConversione().getFattoreConversione(fogliaScelta.level(), gerarchiaScelta.getFattoreConversione().getFogliaRiferimento().level());
        double c = 1;
        if (!gerarchiaScelta.equals(gerarchiaConversione))
            c = this.database.getConversioneGerarchie().getFattoreConversione(gerarchiaScelta.getFattoreConversione().getFogliaRiferimento().data(), gerarchiaConversione.getFattoreConversione().getFogliaRiferimento().data());

        this.view.showMessage(gerarchiaConversione.toStringFattoreConversione(a*c));
    }

    private void creaConversioneGerarchie(Gerarchia gerarchia) {
        TreeNode<Categoria> fogliaRiferimento = gerarchia.getFattoreConversione().getFogliaRiferimento();
        for (Gerarchia g : database.getGerarchie()){
            TreeNode<Categoria> secondaFogliaRiferimento = g.getFattoreConversione().getFogliaRiferimento();
            //TODO: inserisci anche il riferimento alle gerarchie in modo carino (un vero tesoro)
            boolean validInput = false;

            while (!validInput){
                String value = this.view.getInput(String.format("Inserisci il fattore di conversione tra la categoria %s e la categoria %s", fogliaRiferimento.data().getNome(), secondaFogliaRiferimento.data().getNome()));

                try{
                    double fattore = Double.parseDouble(value);
                    if (fattore >= 0.5 && fattore <= 2){
                        validInput = true;
                        this.database.getConversioneGerarchie().addFattoreConversione(fogliaRiferimento.data(), secondaFogliaRiferimento.data(), fattore);
                        view.showMessage("Fattore di conversione aggiunto");
                    } else {
                        view.showMessage("Il fattore di conversione deve essere un valore decimale compreso tra 0.5 e 2.");
                    }

                } catch (NumberFormatException e) {
                    view.showMessage("Il fattore di conversione deve essere un valore decimale compreso tra 0.5 e 2.");
                }
            }
        }
    }

    /**
     * Crea i fattori di conversione per una gerarchia.
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
            boolean validInput = false;

            // Continua a chiedere l'input finché non è valido
            while (!validInput) {
                // Richiede all'utente di inserire il fattore di conversione
                String value = view.getInput(String.format("Inserisci il fattore di conversione tra la categoria %s e la categoria %s: ", fogliaRiferimento.data().getNome(), foglia.data().getNome()));
                // TODO: in caso aggiungere foglia.parent().data().getNome() per unicità

                try {
                    // Converte l'input in un valore double
                    double fattore = Double.parseDouble(value);

                    // Controlla se il livello della foglia è maggiore del livello della foglia di riferimento
                    if (foglia.level() > fogliaRiferimento.level()) {
                        // Se il fattore è valido, lo aggiunge e imposta validInput a true
                        if (fattore > 1 && fattore <= 2) {
                            validInput = true;
                            gerarchia.getFattoreConversione().addFattoreConversione(foglia.level(), fattore);
                            view.showMessage("Fattore di conversione aggiunto");
                        } else {
                            // Mostra un messaggio di errore se il fattore non è valido
                            view.showMessage("Il fattore di conversione viene consigliato maggiore di 1 e massimo 2 per attività più specializzate.");
                        }
                    } else {
                        // Se il fattore è valido, lo aggiunge e imposta validInput a true
                        if (fattore >= 0.5 && fattore < 1) {
                            validInput = true;
                            gerarchia.getFattoreConversione().addFattoreConversione(foglia.level(), fattore);
                            view.showMessage("Fattore di conversione aggiunto");
                        } else {
                            // Mostra un messaggio di errore se il fattore non è valido
                            view.showMessage("Il fattore di conversione deve essere compreso tra 0.5 e meno di 1 per attività meno specializzate.");
                        }
                    }
                } catch (NumberFormatException e) {
                    // Mostra un messaggio di errore se l'input non è un numero valido
                    view.showMessage("Il fattore di conversione deve essere un valore decimale compreso tra 0.5 e 2.");
                }
            }
        }
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

    /**
     * Crea una nuova gerarchia nel sistema.
     *
     * @return la gerarchia creata
     */
    private Gerarchia createGerarchia() {
        List<Gerarchia> categorieFoglia = new ArrayList<>();
        int maxLevel = 0;
        Gerarchia rootGerarchia = new Gerarchia(createCategoria( null, null));
        Stack<Gerarchia> stack = new Stack<>(); // TODO: valutare utilizzo deque
        Categoria categoria = (Categoria) rootGerarchia.root().data();

        for (int i=0; i < categoria.getCampo().getDominio().size(); i++) {
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
                for (int i = 0; i < campo.getDominio().size(); i++) { // aggiungo n gerarchie dove n è il numero di valori del campo
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
     * Questo metodo chiama il metodo {@link #createCampo()} per aggiungere un campo caratteristico alla categoria.
     *
     * @param nomePadre il nome della categoria padre
     * @param root la gerarchia radice, null se è la categoria radice
     * @return la categoria creata
     */
    private Categoria createCategoria(String nomePadre, Gerarchia root) {
        String nome;
        while (true) {
            nome = this.view.getInput("Inserisci il nome della categoria" + (root == null ? " radice" : " figlia di " + (nomePadre)) + ": ");
            if (nome.isBlank()) {
                this.view.showMessage("Nome non valido");
                continue;
            }
            if ((root == null && database.hasGerarchia(nome)) || (root != null && root.find(new Categoria(nome)) != null)) {
                view.showMessage(root == null ? "Esiste già una gerarchia con questo nome" : "Categoria già esistente");
                continue;
            }
            break;
        }
        Campo campo = (root == null || getUserChoice("Vuoi aggiungere un campo caratteristico a questa categoria? (s/n): ")) ? createCampo() : null;
        return new Categoria(nome, campo);
    }

    /**
     * Crea un campo caratteristico per una categoria.
     * Questo metodo chiama il metodo {@link #createDominio(Campo)} per aggiungere un valori di dominio al campo.
     *
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
                this.view.showMessage("Il campo deve contenere almeno un valore di dominio");
            }
        } while (campo.getDominio().isEmpty());
        return campo;
    }

    /**
     * Crea un dominio di valori per un campo.
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
            if (nomeValore.equals("0")) break;
            if (campo.hasValore(nomeValore)) {
                this.view.showMessage("Valore di dominio già esistente");
                continue;
            }
            String descrizione = this.view.getInput("Inserisci una descrizione a questo valore di dominio (facoltativa): ");
            campo.addValore(descrizione.isBlank() ? new ValoreCampo(nomeValore) : new ValoreCampo(nomeValore, descrizione));

            this.view.showMessage("Valore di dominio aggiunto con successo");
            if (!getUserChoice("Vuoi aggiungere un altro valore di dominio? (s/n): "))
                finished = true;
        }
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

    /**
     * Restituisce il database.
     *
     * @return il database
     */
    public Database getDatabase() {
        return this.database;
    }
}