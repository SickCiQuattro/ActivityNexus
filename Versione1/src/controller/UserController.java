package controller;

import model.Configuratore;
import model.Utente;
import model.Comprensorio;
import view.ConsoleView;
import util.UserGenerator;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe che gestisce le operazioni relative agli utenti.
 * Fornisce metodi per il login, la registrazione e la gestione del menu principale.
 */
public class UserController {
    private final ConsoleView view;
    private final List<Utente> utenti;
    private final List<Comprensorio> comprensori;
    private boolean isConfig = false;

    /**
     * Costruisce un UserController con la vista specificata.
     * Inizializza la lista degli utenti come una ArrayList vuota.
     *
     * @param view la vista console da utilizzare per l'interazione con l'utente
     */
    public UserController(ConsoleView view) {
        this.view = view;
        this.utenti = new ArrayList<>();
        this.comprensori = new ArrayList<>();
    }

    /**
     * Avvia il ciclo principale del controller.
     * Mostra il menu principale e gestisce le scelte dell'utente.
     */
    public void start() {
        while (true) {
            this.manageConfiguratore(); // Skip registrazione e login che ci rompeva le palle
            view.showMenu();
            String choice = view.getInput("Cosa vuoi fare? ");
            switch (choice) {
                case "1":
                    if (this.login())
                        if (this.isConfig) {
                            this.manageConfiguratore();
                            return;
                        }
                        else{
                            this.view.showMessage("TODO menu Fruitore");
                            return;
                        }
                    break;
                case "2":
                    if (this.login(UserGenerator.generateRandomUsername(), UserGenerator.generateRandomPassword()))
                        this.register();
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

    /**
     * Gestisce il processo di login dell'utente.
     * Chiede all'utente di inserire username e password e verifica le credenziali.
     */
    private boolean login() {
        if (this.utenti.isEmpty()) {
            this.view.showMessage("Nessun utente registrato. Effettua la registrazione prima di fare il login.");
            return false;
        }
        String username = this.view.getInput("Inserisci username: ");
        String password = this.view.getInput("Inserisci password: ");

        for (Utente user : this.utenti) {
            if (user.verificaCredenziali(username, password)) {
                this.view.showMessage("Login effettuato con successo! Bentornato, " + username + ".");
                this.isConfig = user.isConfig(); // se l'utente è di tipo Configuratore, isConfig è true
                return true;
            }
        }
        this.view.showMessage("Credenziali non valide. Riprova.");
        return false;
    }

    /**
     * Gestisce il processo di login dell'utente pseudocasuale.
     * Chiede all'utente di inserire username e password di default e verifica le credenziali.
     * Se il login è riuscito, l'utente può procedere con la registrazione.
     * @param usernameDefault lo username di default
     * @param passwordDefault la password di default
     * @return true se il login è riuscito, false altrimenti
     */
    private boolean login(String usernameDefault, String passwordDefault) {
        this.view.showMessage("Effettua il login con le seguenti credenziali:");
        this.view.showMessage("Username: " + usernameDefault);
        this.view.showMessage("Password: " + passwordDefault);
        for (int i=0; i<3; i++) {
            String username = this.view.getInput("Inserisci username: ");
            String password = this.view.getInput("Inserisci password: ");

            if (usernameDefault.equals(username) && passwordDefault.equals(password)) {
                return true;
            }

            this.view.showMessage("Credenziali non valide. Tentativi rimasti: " + (2-i));
        }
        this.view.showMessage("Login non riuscito. Riprova più tardi.");
        return false;
    }

    /**
     * Gestisce il processo di registrazione di un nuovo utente.
     * Chiede all'utente di inserire un nuovo username e una nuova password, e aggiunge l'utente alla lista.
     */
    private void register() {
        String username = this.view.getInput("Inserisci un nuovo username: ");
        if (!checkUsername(username))
            return;
        for (Utente user : this.utenti) {
            if (user.getUsername().equals(username)) {
                this.view.showMessage("Username già in uso. Scegline un altro.");
                return;
            }
        }

        String password = this.view.getInput("Inserisci una nuova password: ");
        if (!checkPassword(password))
            return;

        this.utenti.add(new Configuratore(username, password));
        this.view.showMessage("Registrazione completata con successo! Ora puoi effettuare il login.");
    }

    /**
     *  Verifica che lo username inserito sia valido. Deve essere non vuoto, senza spazi,
     *  lungo massimo 20 caratteri e senza caratteri speciali.
     * @param username lo username da verificare
     * @return true se lo username è valido, false altrimenti
     */
    private boolean checkUsername(String username) {
        if (username.isEmpty()) {
            this.view.showMessage("Lo username non può essere vuoto.");
            return false;
        }
        if (username.contains(" ")) {
            this.view.showMessage("Lo username non può contenere spazi.");
            return false;
        }
        if (username.length() > 20) {
            this.view.showMessage("Lo username deve essere lungo massimo 20 caratteri.");
            return false;
        }
        String specialCharacters = "[^a-zA-Z0-9]";
        if (username.matches(specialCharacters)) {
            this.view.showMessage("Lo username non può contenere caratteri speciali.");
            return false;
        }
        return true;
    }

    /**
     * Verifica che la password inserita sia valida. Deve essere lunga almeno 8 caratteri,
     * contenere almeno una lettera maiuscola, una lettera minuscola, un numero e un carattere speciale.
     * @param password la password da verificare
     * @return true se la password è valida, false altrimenti
     */
    private boolean checkPassword(String password) {
        if (password.length() < 8) {
            this.view.showMessage("La password deve contenere almeno 8 caratteri.");
            return false;
        }
        if (!password.matches(".*[A-Z].*")) {
            this.view.showMessage("La password deve contenere almeno una lettera maiuscola.");
            return false;
        }
        if (!password.matches(".*[a-z].*")) {
            this.view.showMessage("La password deve contenere almeno una lettera minuscola.");
            return false;
        }
        if (!password.matches(".*\\d.*")) {
            this.view.showMessage("La password deve contenere almeno un numero.");
            return false;
        }
        if (!password.matches(".*[!@#$%^&*].*")) {
            this.view.showMessage("La password deve contenere almeno un carattere speciale (es. !@#$%^&*).");
            return false;
        }
        return true;
    }

    private void manageConfiguratore() {
        while (true) {
            view.showMenuConfiguratore();
            String choice = view.getInput("Cosa vuoi fare? ");
            switch (choice) {
                case "1":
                    this.view.showMessage(this.visualizzaComprensori());
                    break;
                case "2":
                    // TODO : Visualizza gerarchie
                    break;
                case "3":
                    // Visualizza fattori di conversione
                    break;
                case "4":
                    // Crea comprensorio
                    break;
                case "5":
                    // Crea gerarchia
                    break;
                case "0":
                    this.view.showMessage("Grazie per aver utilizzato il programma. Arrivederci!");
                    return;
                default:
                    this.view.showMessage("Opzione non valida. Riprova.");
            }
        }
    }

    private String visualizzaComprensori() {
        StringBuilder sb = new StringBuilder();

        Comprensorio c1 = new Comprensorio("Comprensorio1");
        c1.addComune("Comune1");
        c1.addComune("Comune2");
        c1.addComune("Comune3");
        Comprensorio c2 = new Comprensorio("Comprensorio2");
        c2.addComune("Comune4");
        c2.addComune("Comune5");
        c2.addComune("Comune6");
        this.comprensori.add(c1);
        this.comprensori.add(c2);

        for (Comprensorio c : this.comprensori) {
            sb.append(c.toString()).append("\n");
        }
        return sb.toString();
    }

    private void saveUtenti() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("utenti.bin"))) {
            out.writeObject(this.utenti);
            System.out.println("Utenti salvati con successo.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadUtenti() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("utenti.bin"))) {
            this.utenti.clear();
            this.utenti.addAll((List<Utente>) in.readObject());
            System.out.println("Utenti caricati con successo.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}