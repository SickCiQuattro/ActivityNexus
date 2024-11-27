package controller;

import model.Configuratore;
import model.Utente;
import model.Comprensorio;
import view.ConsoleView;
import util.UserUtil;
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
}