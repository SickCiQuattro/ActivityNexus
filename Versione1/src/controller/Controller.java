package controller;

import model.Database;
import model.Utente;
import model.Comprensorio;
import model.Gerarchia;
import util.UserUtil;
import view.ConsoleView;

public class Controller {
     private Database database;
     private ConsoleView view;

     public Controller(ConsoleView view) {
         this.database = new Database();
         this.view = view;
     }

    /**
     * Avvia il ciclo principale del controller.
     * Mostra il menu principale e gestisce le scelte dell'utente.
     */
    public void start() {
        while (true) {
            view.showMenu();
            String choice = view.getInput("Cosa vuoi fare? ");
            switch (choice) {
                case "1":
                    if (!this.database.getUtenti().isEmpty()){
                        Utente utente = this.loginUtente();

                        if(utente == null){
                            this.view.showMessage("Credenziali non valide. Riprova.");
                            break;
                        }

                        if (utente.isConfig()){
                            this.view.showMessage("Login effettuato con successo! Bentornato, " + utente.getUsername() + ".");
                            this.manageConfiguratore();
                        } else {
                            this.view.showMessage("TODO menu Fruitore");
                        }
                    }
                    else{
                        this.view.showMessage("Nessun utente registrato. Effettua la registrazione.");
                    }

                    break;
                case "2":
                    if (this.registerUtente()) {
                        this.view.showMessage("Registrazione effettuata con successo!");
                    } else {
                        this.view.showMessage("Registrazione fallita. Utente già esistente. Riprova.");
                    }
                    // TODO : forse qua va un salvataggio su file
                    break;
                case "0":
                    this.view.showMessage("Grazie per aver utilizzato il programma. Arrivederci!");
                    return;
                default:
                    this.view.showMessage("Opzione non valida. Riprova.");
            }
        }
        // TODO forse qua va un salvataggio su file
    }

    private void manageConfiguratore() {
        while (true) {
            this.view.showMenuConfiguratore();
            String choice = view.getInput("Cosa vuoi fare? ");
            switch (choice) {
                case "1":
                    if (!this.database.getComprensori().isEmpty())
                        this.view.showMessage(this.database.getComprensori().toString());
                    else
                        this.view.showMessage("Nessun comprensorio presente.");
                    break;
                case "2":
                    // TODO: Visualizza gerarchie
                    break;
                case "3":
                    // TODO: Visualizza fattori di conversione
                    break;
                case "4":
                    Comprensorio comprensorio = this.createComprensorio();
                    if (this.addComprensorio(comprensorio))
                        this.view.showMessage("successo");
                    else
                        this.view.showMessage("fallito");
                    break;
                case "5":
                    // TODO: Crea gerarchia
                    break;
                case "0":
                    this.view.showMessage("Grazie per aver utilizzato il programma. Arrivederci!");
                    return;
                default:
                    this.view.showMessage("Opzione non valida. Riprova.");
            }
        }
    }

     public boolean addUtente(Utente utente) {
         return this.database.addUtente(utente);
     }

     public boolean addComprensorio(Comprensorio comprensorio) {
         return this.database.addComprensorio(comprensorio);
     }

     public boolean addGerarchia(Gerarchia gerarchia) {
         return this.database.addGerarchia(gerarchia);
     }

    /**
     *
     * @return true se l'utente è registrato correttamente, false altrimenti
     */
    public boolean registerUtente(){
//        if (!defaultUtente())
//            return false;

        String username = "";
        String password = "";
        boolean isConfig = false;

        while (true) {
            String role = this.view.getInput("Sei un configuratore? (s/n): ");
            if (role.equalsIgnoreCase("s")) {
                isConfig = true;
                break;
            } else if (role.equalsIgnoreCase("n")) {
                break;
            } else {
                this.view.showMessage("Input non valido. Riprova.");
            }
        }

        while (true) {
            username = this.view.getInput("Inserisci un nuovo username: ");
            if (!UserUtil.checkUsername(username)) {
                this.view.showMessage("Username non valido. Riprova.");
                this.view.showMessage(String.format("Lo username non può essere vuoto, deve essere senza spazi, lungo massimo %d caratteri e senza caratteri speciali.", UserUtil.USERNAME_LENGTH));
                continue;
            }

            while (true) {
                password = this.view.getInput("Inserisci una nuova password: ");
                if (!UserUtil.checkPassword(password)) {
                    this.view.showMessage("Password non valida. Riprova.");
                    this.view.showMessage(String.format("La password deve essere senza spazi, lunga minimo %d caratteri, contenere almeno: \n una lettera maiuscola e una minuscola, un numero e un carattere speciale.", UserUtil.PASSWORD_LENGTH));
                    continue;
                }
                break;
            }
            break;
        }
        return this.database.registerUtente(username, password, isConfig);
    }

    /**
     *
     * @return true se il login va a buon fine, false altrimenti
     */
    private boolean defaultUtente(){
        String usernameDefault = UserUtil.generateRandomUsername();
        String passwordDefault = UserUtil.generateRandomPassword();

        this.view.showMessage("Effettua il login con le seguenti credenziali:");
        this.view.showMessage("Username: " + usernameDefault);
        this.view.showMessage("Password: " + passwordDefault);
        for (int i=2; i>=0; i--) {
            String username = this.view.getInput("Inserisci username: ");
            String password = this.view.getInput("Inserisci password: ");

            if (usernameDefault.equals(username) && passwordDefault.equals(password)) {
                return true;
            }

            this.view.showMessage("Credenziali non valide. Tentativi rimasti: " + (i));
        }
        this.view.showMessage("Login non riuscito. Riprova più tardi.");
        return false;
    }
    /**
     * Effettua il login di un utente nel sistema. L'utente può essere di tipo Utente (in futuro Fruitore) o Configuratore.
     * Viene richiesto all'utente di inserire username e password.
     *
     * @return l'utente loggato, null se le credenziali non sono valide
     */
    public Utente loginUtente(){
        String username = this.view.getInput("Inserisci username: ");
        String password = this.view.getInput("Inserisci password: ");

        return this.database.loginUtente(username, password);
    }

    public Comprensorio createComprensorio(){

        while (true) {
            String nome = this.view.getInput("Inserisci il nome del comprensorio: ");

            if (nome.isBlank()) {
                this.view.showMessage("nome non valido");
                continue;
            }
            if (this.database.hasComprensorio(nome)){
                this.view.showMessage("Comprensorio già esistente");
                continue;
            }
            Comprensorio comprensorio = new Comprensorio(nome);
            String comune = "";
            while(true){
                 comune = this.view.getInput("Inserisci il nome del comprensorio: ");
                if (comune.isBlank()) {
                    this.view.showMessage("nome non valido");
                    continue;
                }
                if (comprensorio.addComune(comune)){
                    this.view.showMessage("Comune già esistente");
                    continue;
                }



            }

            // crea comprensorio
        }
        return comprensorio;
    }
}
