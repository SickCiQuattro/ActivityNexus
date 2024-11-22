package controller;

import model.Utente;
import view.ConsoleView;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe che gestisce le operazioni relative agli utenti.
 * Fornisce metodi per il login, la registrazione e la gestione del menu principale.
 */
public class UserController {
    private final ConsoleView view;
    private final List<Utente> utenti;

    /**
     * Costruisce un UserController con la vista specificata.
     * Inizializza la lista degli utenti come una ArrayList vuota.
     *
     * @param view la vista console da utilizzare per l'interazione con l'utente
     */
    public UserController(ConsoleView view) {
        this.view = view;
        this.utenti = new ArrayList<>();
    }

    /**
     * Avvia il ciclo principale del controller.
     * Mostra il menu principale e gestisce le scelte dell'utente.
     */
    public void start() {
        while (true) {
            view.showMenu();
            String choice = view.getInput("");
            switch (choice) {
                case "1":
                    login();
                    break;
                case "2":
                    register();
                    break;
                case "3":
                    view.showMessage("Grazie per aver utilizzato il programma. Arrivederci!");
                    return;
                default:
                    view.showMessage("Opzione non valida. Riprova.");
            }
        }
    }

    /**
     * Gestisce il processo di login dell'utente.
     * Chiede all'utente di inserire username e password e verifica le credenziali.
     */
    private void login() {
        String username = view.getInput("Inserisci username: ");
        String password = view.getInput("Inserisci password: ");

        for (Utente user : utenti) {
            if (user.verificaCredenziali(username, password)) {
                view.showMessage("Login effettuato con successo! Bentornato, " + username + ".");
                return;
            }
        }
        view.showMessage("Credenziali non valide. Riprova.");
    }

    /**
     * Gestisce il processo di registrazione di un nuovo utente.
     * Chiede all'utente di inserire un nuovo username e una nuova password, e aggiunge l'utente alla lista.
     */
    private void register() {
        String username = view.getInput("Inserisci un nuovo username: ");
        for (Utente user : utenti) {
            if (user.getUsername().equals(username)) {
                view.showMessage("Username già in uso. Scegline un altro.");
                return;
            }
        }

        String password = view.getInput("Inserisci una nuova password: ");

        utenti.add(new Utente(username, password));
        view.showMessage("Registrazione completata con successo! Ora puoi effettuare il login.");
    }
}