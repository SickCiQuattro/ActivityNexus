package controller;

import com.scalified.tree.TreeNode;

import constants.ControllerConstants;
import model.*;
import util.UserUtil;
import view.ConsoleView;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Controller principale dell'applicazione che gestisce la logica di business e
 * coordina
 * le interazioni tra vista (ConsoleView) e modello (Database).
 * <p>
 * Implementa il pattern MVC (Model-View-Controller) gestendo:
 * <ul>
 * <li>Autenticazione e registrazione degli utenti</li>
 * <li>Operazioni su comprensori geografici</li>
 * <li>Creazione e gestione di gerarchie di categorie</li>
 * <li>Fattori di conversione tra gerarchie</li>
 * <li>Persistenza dei dati tramite serializzazione</li>
 * </ul>
 * <p>
 * Supporta due tipi di utenti: Configuratore (amministratore) e Fruitore
 * (utente standard).
 *
 * @version 2.0
 * @see model.Database
 * @see view.ConsoleView
 */
public class Controller {
    // TODO: Dividere il Controller in classi più piccole per gestire le
    // funzionalità specifiche

    private Database database;
    private final ConsoleView view;

    // -----------------------------------------------------------------------------------
    // METODI PRINCIPALI
    // -----------------------------------------------------------------------------------

    /**
     * Costruisce un nuovo controller collegando il modello (database) alla vista.
     * <p>
     * Inizializza un database vuoto e memorizza il riferimento alla vista fornita
     * per l'interazione con l'utente.
     *
     * @param view la vista console per l'interazione con l'utente
     */
    public Controller(ConsoleView view) {
        // Inizializza un nuovo database vuoto
        this.database = new Database();
        // Mantiene un riferimento alla vista per interagire con l'utente
        this.view = view;
    }

    /**
     * Avvia il ciclo principale dell'applicazione.
     * <p>
     * Gestisce il flusso dell'applicazione attraverso queste fasi:
     * <ol>
     * <li>Caricamento dei dati all'avvio</li>
     * <li>Presentazione del menu principale</li>
     * <li>Elaborazione delle scelte dell'utente</li>
     * <li>Salvataggio dei dati prima dell'uscita</li>
     * </ol>
     * <p>
     * Il ciclo continua finché l'utente non sceglie esplicitamente di terminare
     * l'applicazione.
     */
    public void start() {
        // Carica i dati prima di avviare l'applicazione
        loadData();

        // Ciclo principale dell'applicazione che continua fino a quando l'utente non
        // sceglie di uscire
        while (true) {
            try {
                // Mostra il menu principale e ottieni la scelta dell'utente
                view.showMenu();
                String choice = view.getInput(ControllerConstants.PROMPT_CHOICE);

                switch (choice) {
                    case "1": // Login
                        handleLogin(); // Gestisce l'autenticazione dell'utente
                        break;

                    case "2": // Registrazione
                        handleRegistration();
                        break;

                    case "0": // Uscita
                        // Prima di uscire, salva tutti i dati
                        salvaTutti();
                        view.showMessage(ControllerConstants.EXIT_MESSAGE);
                        view.close(); // Chiude la vista (terminali, scanner, ecc.)
                        return; // Termina il ciclo e il metodo

                    default:
                        // Gestisce input non validi
                        view.showMessage(ControllerConstants.INVALID_OPTION);
                }

            } catch (Exception e) {
                // Gestione errori imprevisti per evitare che l'applicazione si arresti
                view.showErrorMessage(String.format(ControllerConstants.ERROR_GENERIC, e.getMessage()));

                // Logging dell'errore invece di una semplice stack trace
                logErrore(e);

                view.pressEnterToContinue(); // Attende input utente prima di continuare
            }
        }
    }

    /**
     * Carica tutti i dati dal disco all'avvio dell'applicazione.
     * <p>
     * Tenta di caricare utenti, comprensori, gerarchie e conversioni dai rispettivi
     * file.
     * Se i file non esistono o si verificano errori, inizializza strutture vuote
     * garantendo che l'applicazione possa comunque funzionare.
     * <p>
     * Fornisce feedback all'utente durante il processo di caricamento.
     */
    private void loadData() {
        view.showMessage(ControllerConstants.LOADING_DATA);

        try {
            // Carica i dati utenti se il file esiste
            if (fileEsistente(ControllerConstants.USERS_FILE_PATH)) {
                this.database.deserializeUtenti(ControllerConstants.USERS_FILE_PATH);
                view.showMessage(String.format(ControllerConstants.LOADING_ITEM_SUCCESS, "utenti"));
            } else {
                // Altrimenti, indica che si parte da zero per questa sezione
                view.showMessage(String.format(ControllerConstants.LOADING_ITEM_EMPTY, "dato utente"));
            }

            // Carica i dati comprensori se il file esiste
            if (fileEsistente(ControllerConstants.COMPRESORI_FILE_PATH)) {
                this.database.deserializeComprensori(ControllerConstants.COMPRESORI_FILE_PATH);
                view.showMessage(String.format(ControllerConstants.LOADING_ITEM_SUCCESS, "comprensori"));
            } else {
                view.showMessage(String.format(ControllerConstants.LOADING_ITEM_EMPTY, "comprensorio"));
            }

            // Carica i dati gerarchie se il file esiste
            if (fileEsistente(ControllerConstants.GERARCHIE_FILE_PATH)) {
                this.database.deserializeGerarchie(ControllerConstants.GERARCHIE_FILE_PATH);
                view.showMessage(String.format(ControllerConstants.LOADING_ITEM_SUCCESS, "gerarchie"));
            } else {
                view.showMessage(String.format(ControllerConstants.LOADING_ITEM_EMPTY, "gerarchia"));
            }

            // Carica i dati conversione gerarchie se il file esiste
            if (fileEsistente(ControllerConstants.CONVERSIONE_FILE_PATH)) {
                this.database.deserializeConversioneGerarchie(ControllerConstants.CONVERSIONE_FILE_PATH);
                view.showMessage(String.format(ControllerConstants.LOADING_ITEM_SUCCESS, "conversione gerarchie"));
            } else {
                view.showMessage(String.format(ControllerConstants.LOADING_ITEM_EMPTY, "dato di conversione"));
            }

            // Indica all'utente che il processo di caricamento è completato con successo
            view.showSuccessMessage(ControllerConstants.LOADING_COMPLETE);

        } catch (Exception e) {
            // Gestione centralizzata degli errori durante il caricamento
            view.showErrorMessage(String.format(ControllerConstants.LOADING_ERROR, e.getMessage()));
            view.showMessage(ControllerConstants.LOADING_DEFAULT);
            // Re-inizializza il database per garantire uno stato pulito
            this.database = new Database();
            view.pressEnterToContinue();
        }
    }

    /**
     * Verifica se un file esiste ed è non vuoto.
     * <p>
     * Utilizzato durante il caricamento per determinare se ci sono dati da
     * deserializzare.
     *
     * @param path percorso del file da verificare
     * @return true se il file esiste ed ha dimensione maggiore di zero
     */
    private boolean fileEsistente(String path) {
        java.io.File file = new java.io.File(path);
        // Controlla sia l'esistenza che la dimensione del file (deve essere > 0)
        return file.exists() && file.length() > 0;
    }

    /**
     * Salva i dati degli utenti su file.
     * <p>
     * Serializza solo la componente utenti del database, utilizzato dopo operazioni
     * che modificano i dati utente come registrazione o modifiche al profilo.
     */
    private void salvaUtenti() {
        try {
            // Serializza solo i dati utente nel file dedicato
            this.database.serializeUtenti(ControllerConstants.USERS_FILE_PATH);
        } catch (Exception e) {
            // Gestisce eventuali errori durante il salvataggio
            view.showErrorMessage(String.format(ControllerConstants.SAVING_ERROR, e.getMessage()));
        }
    }

    /**
     * Salva tutti i dati dell'applicazione prima dell'uscita.
     * <p>
     * Serializza utenti, comprensori, gerarchie e fattori di conversione nei
     * rispettivi file,
     * garantendo che nessuna informazione venga persa quando l'applicazione
     * termina.
     */
    private void salvaTutti() {
        // Informa l'utente che il processo di salvataggio è iniziato
        view.showMessage(ControllerConstants.SAVING_DATA);

        try {
            // Salva tutti i componenti del database in file separati
            this.database.serializeUtenti(ControllerConstants.USERS_FILE_PATH);
            this.database.serializeComprensori(ControllerConstants.COMPRESORI_FILE_PATH);
            this.database.serializeGerarchie(ControllerConstants.GERARCHIE_FILE_PATH);
            this.database.serializeConversioneGerarchie(ControllerConstants.CONVERSIONE_FILE_PATH);

            // Conferma che il salvataggio è avvenuto con successo
            view.showSuccessMessage(ControllerConstants.SAVING_SUCCESS);
        } catch (Exception e) {
            // Gestisce eventuali errori durante il salvataggio
            view.showErrorMessage(String.format(ControllerConstants.SAVING_ERROR, e.getMessage()));
        }
    }

    // -----------------------------------------------------------------------------------
    // METODI DI GESTIONE MENU E SESSIONE
    // -----------------------------------------------------------------------------------

    /**
     * Gestisce il processo di autenticazione dell'utente.
     * <p>
     * Verifica le credenziali fornite dall'utente e, se valide,
     * indirizza al menu appropriato in base al tipo di account (configuratore o
     * fruitore).
     */
    private void handleLogin() {
        // Verifica preliminare: almeno un utente deve essere registrato nel sistema
        if (database.getUtenti().isEmpty()) {
            view.showErrorMessage(ControllerConstants.NO_USERS_REGISTERED);
            view.pressEnterToContinue();
            return;
        }

        // Tenta di autenticare l'utente con le credenziali fornite
        Utente utente = loginUtente();

        // Se l'autenticazione fallisce, mostra un messaggio di errore
        if (utente == null) {
            view.showMessage(ControllerConstants.LOGIN_FAILED);
            return;
        }

        // Autenticazione riuscita: mostra un messaggio di benvenuto personalizzato
        view.showMessage(ControllerConstants.LOGIN_SUCCESS + utente.getUsername() + ".");

        // Reindirizzamento al menu appropriato in base al ruolo dell'utente
        if (utente.isConfig()) {
            // Se è un configuratore, mostra il menu corrispondente
            manageConfiguratore();
        } else {
            // Altrimenti è un fruitore (funzionalità future)
            manageFruitore();
        }
    }

    /**
     * Gestisce il processo di registrazione di un nuovo utente.
     * <p>
     * Mostra le opzioni di registrazione e guida l'utente nella scelta
     * del tipo di account (configuratore o fruitore) e nell'inserimento
     * delle informazioni necessarie.
     */
    private void handleRegistration() {
        // Presentazione delle opzioni di registrazione
        view.showMessage(ControllerConstants.TITLE_REGISTRATION);

        // Determinazione del tipo di account (Configuratore o Fruitore)
        boolean isConfiguratore = getUserChoice(ControllerConstants.PROMPT_CONFIG_ROLE);
        boolean registrazioneCompletata;

        try {
            if (isConfiguratore) {
                registrazioneCompletata = registraConfiguratore();
            } else {
                // Verifica preliminare: per i fruitori serve almeno un comprensorio
                if (database.getComprensori().isEmpty()) {
                    view.showErrorMessage(ControllerConstants.ERROR_NO_COMPRENSORI_REGISTRATION);
                    return;
                }
                registrazioneCompletata = registraFruitore();
            }

            // Mostra il messaggio appropriato in base all'esito
            if (registrazioneCompletata) {
                view.showSuccessMessage(ControllerConstants.REGISTRATION_SUCCESS);
                // Salva i dati utente dopo registrazione riuscita
                salvaUtenti();
                view.showSuccessMessage(ControllerConstants.SAVING_USERS_SUCCESS);
            } else {
                view.showErrorMessage(ControllerConstants.REGISTRATION_CANCELLED);
            }
            this.view.pressEnterToContinue();
        } catch (Exception e) {
            view.showErrorMessage(String.format(
                    ControllerConstants.ERROR_REGISTRATION, e.getMessage()));
        }
    }

    /**
     * Gestisce l'interfaccia e le funzionalità dell'utente configuratore.
     * <p>
     * Presenta il menu dedicato al configuratore e coordina l'esecuzione
     * delle operazioni di amministrazione selezionate dall'utente.
     */
    private void manageConfiguratore() {
        // Ciclo principale del menu configuratore
        while (true) {
            try {
                // Mostra il menu configuratore e ottiene la scelta dell'utente
                view.showMenuConfiguratore();
                String choice = view.getInput(ControllerConstants.PROMPT_CHOICE);

                // Elabora la scelta dell'utente
                switch (choice) {
                    case "1": // Visualizza comprensori
                        visualizzaComprensori();
                        break;

                    case "2": // Visualizza gerarchie
                        visualizzaGerarchie();
                        break;

                    case "3": // Visualizza fattori di conversione
                        // Verifica preliminare che esistano gerarchie prima di mostrare i fattori
                        if (verificaPresenzaGerarchie()) {
                            visualizzaFattoriConversione();
                        }
                        break;

                    case "4": // Crea comprensorio
                        creaComprensorio();
                        break;

                    case "5": // Crea gerarchia
                        creaGerarchia();
                        break;

                    case "0": // Logout
                        // Termina il ciclo e ritorna al menu principale
                        view.showMessage(ControllerConstants.LOGOUT_MESSAGE);
                        return;

                    default:
                        // Gestione di input non validi
                        view.showMessage(ControllerConstants.INVALID_OPTION);
                }

                // Pausa per permettere all'utente di leggere i risultati
                view.pressEnterToContinue();

            } catch (Exception e) {
                // Gestione centralizzata delle eccezioni per garantire robustezza
                view.showErrorMessage(String.format(ControllerConstants.ERROR_GENERIC, e.getMessage()));
                view.pressEnterToContinue();
            }
        }
    }

    /**
     * Visualizza i comprensori geografici registrati nel sistema.
     * <p>
     * Mostra un elenco formattato di tutti i comprensori disponibili,
     * con i relativi comuni. Se non ci sono comprensori, visualizza
     * un messaggio informativo.
     */
    private void visualizzaComprensori() {
        // Verifica se ci sono comprensori da visualizzare
        if (this.database.getComprensori().isEmpty()) {
            // Se non ci sono comprensori, mostra un messaggio appropriato
            view.showErrorMessage(ControllerConstants.NO_COMPRENSORI);
        } else {
            // Altrimenti, mostra il titolo e la lista dei comprensori
            view.showMessage(ControllerConstants.TITLE_COMPRENSORI);
            view.showMessage(database.toStringComprensori());
        }
    }

    /**
     * Visualizza le gerarchie di categorie registrate nel sistema.
     * <p>
     * Mostra la struttura completa delle gerarchie di categorie,
     * incluse relazioni padre-figlio, campi caratteristici e valori di dominio.
     * Se non ci sono gerarchie, visualizza un messaggio informativo.
     */
    private void visualizzaGerarchie() {
        // Verifica se ci sono gerarchie da visualizzare
        if (this.database.getGerarchie().isEmpty()) {
            // Se non ci sono gerarchie, mostra un messaggio appropriato
            view.showErrorMessage(ControllerConstants.NO_GERARCHIE);
        } else {
            // Altrimenti, mostra il titolo e la lista delle gerarchie
            view.showMessage(ControllerConstants.TITLE_GERARCHIE);
            view.showMessage(this.database.toStringGerarchie());
        }
    }

    /**
     * Verifica la presenza di gerarchie nel sistema.
     * <p>
     * Controlla se esistono gerarchie configurate e mostra un messaggio
     * appropriato in caso negativo.
     *
     * @return true se esistono gerarchie nel database, false altrimenti
     */
    private boolean verificaPresenzaGerarchie() {
        // Verifica se ci sono gerarchie nel database
        if (this.database.getGerarchie().isEmpty()) {
            // Se non ci sono gerarchie, mostra un messaggio di errore specifico per la
            // funzione di conversione
            this.view.showErrorMessage(ControllerConstants.NO_GERARCHIE_CONV);
            return false;
        }
        // Ci sono gerarchie, l'operazione può procedere
        return true;
    }

    /**
     * Avvia il processo di creazione di un nuovo comprensorio.
     * <p>
     * Guida l'utente attraverso la definizione di nome e comuni appartenenti
     * al comprensorio, salvandolo nel database se la creazione ha successo.
     */
    private void creaComprensorio() {
        // Mostra informazioni introduttive sul processo di creazione
        view.showMessage(ControllerConstants.TITLE_NEW_COMPRENSORIO);
        view.showMessage(ControllerConstants.INFO_COMPRENSORIO_DESC);
        view.showMessage(ControllerConstants.INFO_COMPRENSORIO_NEXT);

        // Avvia il processo di creazione del comprensorio
        if (createComprensorio()) {
            // Se la creazione ha successo, salva i dati su file
            this.database.serializeComprensori(ControllerConstants.COMPRESORI_FILE_PATH);
            view.showSuccessMessage(ControllerConstants.COMPRENSORIO_CREATED);
        } else {
            // In caso di annullamento o errore
            view.showMessage(ControllerConstants.OPERATION_CANCELLED);
        }
    }

    /**
     * Avvia il processo di creazione di una nuova gerarchia di categorie.
     * <p>
     * Guida l'utente attraverso la definizione dell'intera struttura gerarchica,
     * includendo categorie, campi caratteristici e fattori di conversione,
     * salvando il risultato nel database.
     */
    private void creaGerarchia() {
        // Mostra informazioni introduttive sulla creazione della gerarchia
        view.showMessage(ControllerConstants.TITLE_NEW_GERARCHIA);

        // Creazione della struttura principale della gerarchia
        Gerarchia gerarchia = createGerarchia();
        view.showSuccessMessage(ControllerConstants.GERARCHIA_CREATED);

        // Visualizzazione della struttura gerarchica creata
        view.showMessage("\n" + gerarchia);

        // Configurazione dei fattori di conversione interni alla gerarchia
        view.showMessage("\n" + ControllerConstants.CONVERSIONE_TITLE);
        creaFattoriConversione(gerarchia);

        // Configurazione delle conversioni tra la nuova gerarchia e quelle esistenti
        if (!database.getGerarchie().isEmpty()) {
            view.showMessage(ControllerConstants.TITLE_CONVERSIONI_TRA_GERARCHIE);
            creaConversioneGerarchie(gerarchia);
        }

        // Aggiunta della gerarchia al database
        if (this.database.addGerarchia(gerarchia)) {
            view.showSuccessMessage(ControllerConstants.GERARCHIA_ADDED);
        }

        // Salvataggio dei dati su file
        this.database.serializeGerarchie(ControllerConstants.GERARCHIE_FILE_PATH);
        this.database.serializeConversioneGerarchie(ControllerConstants.CONVERSIONE_FILE_PATH);
    }

    private void manageFruitore() {
        // Ciclo principale del menu fruitore
        while (true) {
            try {
                // Mostra il menu fruitore e ottiene la scelta dell'utente
                view.showMenuFruitore();
                String choice = view.getInput(ControllerConstants.PROMPT_CHOICE);

                // Elabora la scelta dell'utente
                switch (choice) {
                    case "1": // Visualizza gerarchie
                        // TODO: visualizzazione categorie radice, per poi scegliere la gerarchia da
                        // visualizzare
                        visualizzaGerarchie();
                        break;

                    case "0": // Logout
                        // Termina il ciclo e ritorna al menu principale
                        view.showMessage(ControllerConstants.LOGOUT_MESSAGE);
                        return;

                    default:
                        // Gestione di input non validi
                        view.showMessage(ControllerConstants.INVALID_OPTION);
                }

                // Pausa per permettere all'utente di leggere i risultati
                view.pressEnterToContinue();

            } catch (Exception e) {
                // Gestione centralizzata delle eccezioni per garantire robustezza
                view.showErrorMessage(String.format(ControllerConstants.ERROR_GENERIC, e.getMessage()));
                view.pressEnterToContinue();
            }
        }
    }

    // -----------------------------------------------------------------------------------
    // METODI DI AUTENTICAZIONE E REGISTRAZIONE
    // -----------------------------------------------------------------------------------

    /**
     * Autentica un utente nel sistema.
     * <p>
     * Raccoglie username e password dall'utente
     * e verifica le credenziali contro il database.
     *
     * @return l'utente autenticato se le credenziali sono valide, null altrimenti
     */
    public Utente loginUtente() {
        // Richiesta dello username all'utente
        String username = view.getInput(ControllerConstants.PROMPT_USERNAME);

        // Richiesta della password all'utente
        String password = view.getInput(ControllerConstants.PROMPT_PASSWORD);

        // Verifica delle credenziali attraverso il database
        // Ritorna l'utente se valido, null altrimenti
        return database.loginUtente(username, password);
    }

    /**
     * Registra un nuovo utente configuratore nel sistema.
     * <p>
     * Il processo include:
     * <ul>
     * <li>Verifica di sicurezza preliminare</li>
     * <li>Acquisizione di username valido e univoco</li>
     * <li>Acquisizione di password sicura</li>
     * <li>Creazione dell'account nel database</li>
     * </ul>
     *
     * @return true se la registrazione è completata con successo, false altrimenti
     */
    private boolean registraConfiguratore() {
        // Mostra intestazione per la registrazione configuratore
        view.showMessage(ControllerConstants.TITLE_REGISTRA_CONFIGURATORE);

        // Verifica di sicurezza preliminare per prevenire registrazioni automatizzate
        if (!defaultUtente()) {
            view.showErrorMessage(ControllerConstants.SECURITY_CHECK_FAILED);
            return false;
        }

        // Acquisizione username valido e verifica unicità
        String username = getUsername();
        if (username == null) {
            view.showErrorMessage(ControllerConstants.USERNAME_ACQUISITION_FAILED);
            return false;
        }

        // Verifica che lo username non sia già utilizzato
        if (!database.esisteUtente(username)) {
            view.showErrorMessage(ControllerConstants.ERROR_USERNAME_ALREADY_EXISTS);
            return false;
        }

        // Acquisizione password valida
        String password = getPassword();
        if (password == null) {
            view.showErrorMessage(ControllerConstants.PASSWORD_ACQUISITION_FAILED);
            return false;
        }

        // Registrazione effettiva nel database
        return database.registraConfiguratore(username, password);
    }

    /**
     * Registra un nuovo utente fruitore nel sistema.
     * <p>
     * Il processo include:
     * <ul>
     * <li>Selezione del comprensorio di appartenenza</li>
     * <li>Acquisizione di username valido e univoco</li>
     * <li>Acquisizione di password sicura</li>
     * <li>Acquisizione e validazione dell'indirizzo email</li>
     * <li>Creazione dell'account nel database</li>
     * </ul>
     *
     * @return true se la registrazione è completata con successo, false altrimenti
     */
    private boolean registraFruitore() {
        // Mostra intestazione per la registrazione fruitore
        view.showMessage(ControllerConstants.TITLE_REGISTRA_FRUITORE);

        // Selezione del comprensorio di appartenenza
        Comprensorio comprensorio = selezionaComprensorio();
        if (comprensorio == null) {
            return false;
        }

        // Acquisizione username valido e verifica unicità
        String username = getUsername();
        if (username == null) {
            view.showErrorMessage(ControllerConstants.USERNAME_ACQUISITION_FAILED);
            return false;
        }

        // Verifica che lo username non sia già utilizzato
        if (!database.esisteUtente(username)) {
            view.showErrorMessage(ControllerConstants.ERROR_USERNAME_ALREADY_EXISTS);
            return false;
        }

        // Acquisizione password valida
        String password = getPassword();
        if (password == null) {
            view.showErrorMessage(ControllerConstants.PASSWORD_ACQUISITION_FAILED);
            return false;
        }

        // Acquisizione e validazione email
        String email = getEmailValida();
        if (email == null) {
            view.showErrorMessage(ControllerConstants.EMAIL_ACQUISITION_FAILED);
            return false;
        }

        // Registrazione effettiva nel database
        return database.registraFruitore(username, password, email, comprensorio);
    }

    /**
     * Richiede e valida un indirizzo email dall'utente.
     *
     * @return l'indirizzo email validato, o null se il processo è annullato
     */
    private String getEmailValida() {
        final int MAX_TENTATIVI = 3;

        for (int tentativo = 0; tentativo < MAX_TENTATIVI; tentativo++) {
            String email = view.getInput(ControllerConstants.PROMPT_MAIL);

            // Annullamento inserimento
            if (email.equals("0")) {
                return null;
            }

            // Validazione di base: controllo formato email
            if (validaFormatoEmail(email)) {
                return email;
            }

            // Messaggio di errore con tentativi rimanenti
            int tentativi_rimanenti = MAX_TENTATIVI - tentativo - 1;
            view.showErrorMessage(String.format(
                    ControllerConstants.ERROR_INVALID_EMAIL,
                    tentativi_rimanenti > 0 ? tentativi_rimanenti : "nessun"));
        }

        return null;
    }

    /**
     * Valida il formato di un indirizzo email.
     * 
     * @param email l'indirizzo email da validare
     * @return true se il formato è valido, false altrimenti
     */
    private boolean validaFormatoEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }

        // Validazione di base: deve contenere @ e almeno un punto dopo @
        int chiocciola = email.indexOf('@');
        if (chiocciola < 1 || chiocciola == email.length() - 1) {
            return false;
        }

        String dominio = email.substring(chiocciola + 1);
        return dominio.contains(".") && !dominio.startsWith(".");
    }

    /**
     * Esegue una verifica preliminare durante la registrazione.
     * <p>
     * Implementa un meccanismo di sicurezza basato su credenziali temporanee
     * per prevenire registrazioni automatizzate.
     *
     * @return true se la verifica è superata, false altrimenti
     */
    private boolean defaultUtente() {
        // Genera credenziali casuali temporanee
        String usernameDefault = UserUtil.generateRandomUsername();
        String passwordDefault = UserUtil.generateRandomPassword();

        // Mostra le credenziali all'utente
        view.showMessage(ControllerConstants.DEFAULT_LOGIN_INTRO);
        view.showMessage(String.format(ControllerConstants.DEFAULT_USERNAME_DISPLAY, usernameDefault));
        view.showMessage(String.format(ControllerConstants.DEFAULT_PASSWORD_DISPLAY, passwordDefault));

        // Concede all'utente tre tentativi per inserire le credenziali correttamente
        for (int i = 2; i >= 0; i--) {
            // Richiede l'inserimento delle credenziali
            String username = view.getInput(ControllerConstants.PROMPT_USERNAME);
            String password = view.getInput(ControllerConstants.PROMPT_PASSWORD);

            // Verifica se le credenziali inserite corrispondono a quelle generate
            if (usernameDefault.equals(username) && passwordDefault.equals(password))
                return true; // Autenticazione preliminare riuscita

            // Se le credenziali non corrispondono, mostra un messaggio di errore
            // con il numero di tentativi rimasti
            view.showMessage(String.format(ControllerConstants.DEFAULT_LOGIN_INVALID, i));
        }

        // Se sono stati esauriti tutti i tentativi, mostra un messaggio di errore
        // e interrompi il processo di registrazione
        view.showMessage(ControllerConstants.DEFAULT_LOGIN_FAILED);
        return false;
    }

    // -----------------------------------------------------------------------------------
    // METODI PER LA GESTIONE DEI COMPRENSORI
    // -----------------------------------------------------------------------------------

    /**
     * Crea un nuovo comprensorio con nome e comuni definiti dall'utente.
     * <p>
     * Acquisisce il nome del comprensorio, verificandone la validità,
     * e raccoglie i comuni appartenenti attraverso un processo interattivo.
     *
     * @return true se il comprensorio è stato creato e aggiunto con successo, false
     *         altrimenti
     */
    public boolean createComprensorio() {
        // Mostra il titolo della prima fase: definizione del nome del comprensorio
        view.showMessage(ControllerConstants.STEP_NOME_COMPRENSORIO);

        String nome = null;
        boolean nomeValido = false;

        // Ciclo di validazione del nome del comprensorio
        while (!nomeValido) {
            // Richiede il nome all'utente
            nome = view.getInput(ControllerConstants.PROMPT_NOME_COMPRENSORIO);

            // Verifica se l'utente vuole annullare l'operazione
            if (nome.equals("0")) {
                return false; // Operazione annullata dall'utente
            }
            // Verifica che il nome non sia vuoto
            else if (nome.isBlank()) {
                view.showErrorMessage(ControllerConstants.ERROR_NOME_VUOTO);
            }
            // Verifica che il nome abbia almeno 3 caratteri
            else if (nome.length() < 3) {
                view.showErrorMessage(ControllerConstants.ERROR_NOME_CORTO);
            }
            // Verifica che non esista già un comprensorio con questo nome
            else if (database.hasComprensorio(nome)) {
                view.showErrorMessage(ControllerConstants.ERROR_NOME_DUPLICATO);
            } else {
                // Tutte le verifiche sono passate, il nome è valido
                nomeValido = true;
            }
        }

        // Inizia la seconda fase: aggiunta dei comuni al comprensorio
        view.showMessage(ControllerConstants.STEP_COMUNI_COMPRENSORIO);
        view.showMessage(String.format(ControllerConstants.INFO_AGGIUNGI_COMUNI, nome));
        view.showMessage(ControllerConstants.INFO_MINIMO_UN_COMUNE);

        // Crea il nuovo oggetto Comprensorio con il nome validato
        Comprensorio comprensorio = new Comprensorio(nome);

        // Chiama il metodo per aggiungere comuni al comprensorio appena creato
        addComuniToComprensorio(comprensorio);

        // Verifica che il comprensorio abbia almeno un comune
        if (comprensorio.getComuni().isEmpty()) {
            view.showErrorMessage(ControllerConstants.ERROR_CREAZIONE_ANNULLATA);
            return false; // Operazione fallita: nessun comune aggiunto
        }

        // Aggiunge il comprensorio completo al database
        boolean risultato = database.addComprensorio(comprensorio);

        // Mostra un messaggio di successo se l'operazione è andata a buon fine
        if (risultato) {
            view.showSuccessMessage(String.format(ControllerConstants.SUCCESS_COMPRENSORIO_AGGIUNTO, nome));
        }

        return risultato;
    }

    /**
     * Aggiunge comuni a un comprensorio esistente.
     * <p>
     * Permette all'utente di inserire più comuni in sequenza,
     * verificando che siano unici all'interno del comprensorio.
     *
     * @param comprensorio il comprensorio a cui aggiungere i comuni
     */
    private void addComuniToComprensorio(Comprensorio comprensorio) {
        boolean finished = false;
        int comuniAggiunti = 0;

        // Ciclo principale per l'aggiunta dei comuni
        while (!finished) {
            // Richiede il nome del comune all'utente
            String comune = this.view.getInput(ControllerConstants.PROMPT_NOME_COMUNE);

            // Gestisce il caso di uscita (digitando '0')
            if (comune.equals("0")) {
                // Se non è stato aggiunto ancora nessun comune, non permette l'uscita
                if (comuniAggiunti == 0) {
                    this.view.showErrorMessage(ControllerConstants.ERROR_NESSUN_COMUNE);
                } else {
                    // Termina il ciclo se è stato aggiunto almeno un comune
                    finished = true;
                }
                continue;
            }

            // Verifica che il nome del comune non sia vuoto
            if (comune.isBlank()) {
                this.view.showErrorMessage(ControllerConstants.ERROR_COMUNE_VUOTO);
                continue;
            }

            // Verifica che il nome del comune abbia almeno 2 caratteri
            if (comune.length() < 2) {
                this.view.showErrorMessage(ControllerConstants.ERROR_COMUNE_CORTO);
                continue;
            }

            // Verifica che il comune non sia già presente nel comprensorio
            if (comprensorio.hasComune(comune)) {
                this.view.showErrorMessage(String.format(ControllerConstants.ERROR_COMUNE_DUPLICATO, comune));
                continue;
            }

            // Se tutti i controlli sono passati, aggiunge il comune al comprensorio
            comprensorio.addComune(comune);
            comuniAggiunti++;

            // Mostra conferma dell'aggiunta
            this.view.showSuccessMessage(
                    String.format(ControllerConstants.SUCCESS_COMUNE_AGGIUNTO, comune, comprensorio.getNome()));

            // Dopo aver aggiunto almeno un comune, offre la possibilità di terminare
            if (comuniAggiunti > 0) {
                // Mostra il numero di comuni aggiunti finora
                this.view.showMessage(String.format(ControllerConstants.INFO_COMUNI_AGGIUNTI, comuniAggiunti));

                // Chiede all'utente se vuole aggiungere un altro comune
                if (!getUserChoice(ControllerConstants.PROMPT_ADD_ALTRO_COMUNE)) {
                    finished = true; // Termina il ciclo se l'utente sceglie di non continuare
                }
            }
        }
    }

    /**
     * Permette all'utente di selezionare un comprensorio geografico.
     * <p>
     * Mostra un elenco numerato di tutti i comprensori disponibili nel sistema
     * e acquisisce la scelta dell'utente. Consente anche di annullare l'operazione.
     *
     * @return il comprensorio selezionato, o null se l'operazione è annullata o non
     *         ci sono comprensori
     */
    public Comprensorio selezionaComprensorio() {
        // Recupera la lista dei comprensori disponibili
        List<Comprensorio> comprensori = new ArrayList<>(this.database.getComprensori());

        // Verifica se ci sono comprensori disponibili
        if (comprensori.isEmpty()) {
            this.view.showErrorMessage(ControllerConstants.ERROR_NO_COMPRENSORI);
            return null;
        }

        // Mostra il titolo della selezione
        this.view.showMessage(ControllerConstants.TITLE_SELEZIONA_COMPRENSORIO);
        this.view.showMessage(ControllerConstants.INFO_SELEZIONE_COMPRENSORIO);

        // Visualizza l'elenco dei comprensori in formato numerato
        for (int i = 0; i < comprensori.size(); i++) {
            Comprensorio comprensorio = comprensori.get(i);

            // Formato: [1] Nome Comprensorio (3 comuni)
            this.view.showMessage(String.format(ControllerConstants.FORMATO_COMPRENSORIO_LISTA,
                    i + 1,
                    comprensorio.getNome(),
                    comprensorio.getComuni().size()));
        }

        // Mostra le istruzioni per annullare
        this.view.showMessage(ControllerConstants.INFO_ANNULLA_SELEZIONE);

        // Richiedi la selezione dell'utente (0 per annullare, 1-N per selezionare)
        int index = this.view.getIntInput(
                ControllerConstants.PROMPT_COMPRENSORIO_INDEX,
                0,
                comprensori.size());

        // Gestione dell'annullamento dell'operazione
        if (index == 0) {
            return null;
        }

        // Recupera e mostra il comprensorio selezionato
        Comprensorio selezionato = comprensori.get(index - 1);
        this.view.showSuccessMessage(String.format(
                ControllerConstants.SUCCESS_COMPRENSORIO_SELEZIONATO,
                selezionato.getNome()));

        return selezionato;
    }

    // -----------------------------------------------------------------------------------
    // METODI PER LA GESTIONE DELLE GERARCHIE
    // -----------------------------------------------------------------------------------

    /**
     * Crea una nuova gerarchia di categorie completa.
     * <p>
     * Implementa un algoritmo di costruzione della gerarchia basato sui valori
     * dei campi caratteristici, generando automaticamente la struttura ad albero.
     *
     * @return la gerarchia completa creata
     */
    private Gerarchia createGerarchia() {
        // Intestazione e spiegazione iniziale per l'utente
        view.showMessage(ControllerConstants.TITLE_CREATE_GERARCHIA);
        view.showMessage(ControllerConstants.INFO_GERARCHIA_DESC);
        view.showMessage(ControllerConstants.INFO_GERARCHIA_PROCESS);

        // Lista per tenere traccia delle categorie foglia e il livello massimo
        // raggiunto
        List<Gerarchia> categorieFoglia = new ArrayList<>();
        int maxLevel = 0;

        // FASE 1: Creazione della categoria radice
        view.showMessage(ControllerConstants.STEP_RADICE_GERARCHIA);
        view.showMessage(ControllerConstants.INFO_RADICE_DESC);
        view.showMessage(ControllerConstants.INFO_RADICE_CAMPO);

        // Crea la categoria radice (il primo parametro null indica che è la radice)
        Gerarchia rootGerarchia = new Gerarchia(createCategoria(null, null));
        Stack<Gerarchia> stack = new Stack<>();
        Categoria categoria = rootGerarchia.root().data();

        // Verifica obbligatoria: la categoria radice deve avere un campo caratteristico
        if (categoria.getCampo() == null) {
            view.showErrorMessage(ControllerConstants.ERROR_RADICE_SENZA_CAMPO);
            return createGerarchia(); // Riavvia la procedura se la radice non ha campo
        }

        // FASE 2: Creazione della struttura gerarchica basata sui valori di dominio
        view.showMessage(ControllerConstants.STEP_STRUTTURA_GERARCHIA);
        view.showMessage(ControllerConstants.INFO_STRUTTURA_DESC);
        view.showMessage(ControllerConstants.INFO_STRUTTURA_VALORI);

        // Inizializza lo stack con la radice, una volta per ogni valore del suo dominio
        for (int i = 0; i < categoria.getCampo().getDominio().size(); i++) {
            stack.push(rootGerarchia);
        }

        // Mappe per tracciare il progresso della creazione per ogni categoria padre
        Map<Gerarchia, Integer> contatorePerPadre = new HashMap<>();
        Map<Gerarchia, Integer> totalePerPadre = new HashMap<>();

        // Inizializza i contatori per la radice
        contatorePerPadre.put(rootGerarchia, 0);
        totalePerPadre.put(rootGerarchia, categoria.getCampo().getDominio().size());

        // Ciclo principale di costruzione gerarchica
        while (!stack.isEmpty()) {
            // Estrae il prossimo nodo padre da elaborare
            Gerarchia theGoodFather = stack.peek();
            Categoria categoriaPadre = theGoodFather.data();
            String nomeCategoriaPadre = categoriaPadre.getNome();

            // Aggiorna il contatore per il padre corrente
            int contatoreFiglio = contatorePerPadre.get(theGoodFather) + 1;
            contatorePerPadre.put(theGoodFather, contatoreFiglio);
            int totaleFigli = totalePerPadre.get(theGoodFather);

            // Mostra il progresso all'utente per il padre corrente
            view.showMessage(String.format(ControllerConstants.INFO_CREAZIONE_SOTTOCATEGORIA,
                    contatoreFiglio, totaleFigli, nomeCategoriaPadre));

            // Crea una nuova categoria come figlio del padre corrente
            Gerarchia gerarchia = new Gerarchia(createCategoria(nomeCategoriaPadre, rootGerarchia));
            Categoria c = gerarchia.root().data();
            Campo campo = c.getCampo();
            Gerarchia padre = stack.pop(); // Rimuove il padre dallo stack dopo aver creato un figlio
            padre.add(gerarchia); // Aggiunge il figlio al padre nella struttura gerarchica

            if (campo != null) {
                // Se la categoria ha un campo caratteristico, genera ulteriori sottocategorie
                int nuoviNodi = campo.getDominio().size();

                // Inizializza i contatori per questa nuova categoria
                contatorePerPadre.put(gerarchia, 0);
                totalePerPadre.put(gerarchia, nuoviNodi);

                // Aggiunge la nuova categoria allo stack tante volte quanti sono i valori nel
                // dominio
                for (int i = 0; i < nuoviNodi; i++) {
                    stack.push(gerarchia);
                }
            } else {
                // Se la categoria non ha campo, è una foglia (attività concreta)
                if (gerarchia.level() > maxLevel) {
                    maxLevel = gerarchia.level();
                    categorieFoglia.add(gerarchia);
                }
            }
        }

        // FASE 3: Riepilogo finale
        view.showMessage(ControllerConstants.STEP_COMPLETAMENTO_GERARCHIA);
        view.showMessage(ControllerConstants.SUCCESS_GERARCHIA_COMPLETATA);
        view.showMessage(String.format(ControllerConstants.RIEPILOGO_PROFONDITA, maxLevel));
        view.showMessage(String.format(ControllerConstants.RIEPILOGO_FOGLIE, rootGerarchia.getFoglie().size()));

        return rootGerarchia;
    }

    /**
     * Crea una categoria per una gerarchia.
     * <p>
     * Definisce nome e campo caratteristico della categoria,
     * verificando l'unicità del nome all'interno della gerarchia.
     *
     * @param nomePadre nome della categoria padre (null se è la radice)
     * @param root      gerarchia radice (null se è la categoria radice)
     * @return la categoria creata
     */
    private Categoria createCategoria(String nomePadre, Gerarchia root) {
        String nome;

        // Ciclo di validazione del nome della categoria
        while (true) {
            // Messaggio contestualizzato in base al tipo di categoria (radice o
            // sottocategoria)
            String prompt = (root == null) ? ControllerConstants.PROMPT_NOME_CATEGORIA_RADICE
                    : String.format(ControllerConstants.PROMPT_NOME_SOTTOCATEGORIA, nomePadre);

            nome = this.view.getInput(prompt);

            // Verifica che il nome non sia vuoto
            if (nome.isBlank()) {
                this.view.showErrorMessage(ControllerConstants.ERROR_NOME_CATEGORIA_VUOTO);
                continue;
            }

            // Verifica l'unicità del nome: controlla se esiste già una gerarchia con questo
            // nome
            // (per la radice) o una categoria con questo nome (per le sottocategorie)
            if ((root == null && database.hasGerarchia(nome)) ||
                    (root != null && root.find(new Categoria(nome)) != null)) {
                view.showErrorMessage(root == null ? ControllerConstants.ERROR_NOME_GERARCHIA_DUPLICATO
                        : ControllerConstants.ERROR_NOME_CATEGORIA_DUPLICATO);
                continue;
            }

            // Nome valido, uscita dal ciclo
            break;
        }

        // Gestione del campo caratteristico
        boolean aggiungiCampo;

        if (root == null) {
            // Per la categoria radice il campo è obbligatorio
            aggiungiCampo = true;
            view.showMessage(ControllerConstants.INFO_RADICE_CAMPO);
        } else {
            // Per le altre categorie è opzionale, in base alla scelta dell'utente
            view.showMessage("\n" + ControllerConstants.INFO_CAMPO_PROPRIETA);
            view.showMessage(ControllerConstants.INFO_CAMPO_FOGLIE);
            aggiungiCampo = getUserChoice(ControllerConstants.PROMPT_CAMPO_CARATTERISTICO);
        }

        // Creazione del campo se necessario
        Campo campo = aggiungiCampo ? createCampo() : null;

        // Feedback all'utente sul tipo di categoria creata
        if (campo != null) {
            view.showSuccessMessage(
                    String.format(ControllerConstants.SUCCESS_CATEGORIA_CON_CAMPO, nome, campo.getNome()));
        } else {
            view.showSuccessMessage(String.format(ControllerConstants.SUCCESS_CATEGORIA_FOGLIA, nome));
        }

        // Crea e restituisce la nuova categoria
        return new Categoria(nome, campo);
    }

    /**
     * Crea un campo caratteristico per una categoria.
     * <p>
     * Definisce il nome del campo e il suo dominio di valori possibili,
     * che determineranno le possibili sottocategorie.
     *
     * @return il campo caratteristico creato
     */
    private Campo createCampo() {
        view.showMessage(ControllerConstants.TITLE_CAMPO_CARATTERISTICO);
        view.showMessage(ControllerConstants.INFO_CAMPO_DESC);

        // Richiede e valida il nome del campo
        String nome;
        do {
            nome = this.view.getInput(ControllerConstants.PROMPT_NOME_CAMPO);
            if (nome.isBlank()) {
                this.view.showErrorMessage(ControllerConstants.ERROR_CAMPO_VUOTO);
            }
        } while (nome.isBlank());

        // Crea l'oggetto Campo con il nome validato
        Campo campo = new Campo(nome);

        // Spiegazione del concetto di dominio all'utente
        view.showMessage(ControllerConstants.TITLE_VALORI_DOMINIO);
        view.showMessage(ControllerConstants.INFO_DOMINIO_DESC);
        view.showMessage(ControllerConstants.INFO_DOMINIO_SOTTOCATEGORIE);

        // Ciclo di creazione del dominio (si ripete finché il dominio non è vuoto)
        do {
            createDominio(campo);
            if (campo.getDominio().isEmpty()) {
                this.view.showErrorMessage(ControllerConstants.ERROR_DOMINIO_VUOTO);
            }
        } while (campo.getDominio().isEmpty());

        // Feedback di successo con riepilogo
        view.showSuccessMessage(
                String.format(ControllerConstants.SUCCESS_CAMPO_CREATO, nome, campo.getDominio().size()));
        return campo;
    }

    /**
     * Crea il dominio di valori per un campo caratteristico.
     * <p>
     * Raccoglie una serie di valori (con eventuali descrizioni)
     * che definiscono l'insieme delle possibili sottocategorie.
     *
     * @param campo il campo a cui assegnare il dominio
     */
    private void createDominio(Campo campo) {
        boolean finished = false;
        int valoreCount = 0;

        // Messaggio introduttivo
        view.showMessage("Inserisci i valori possibili per il campo '" + campo.getNome() + "':");

        // Ciclo di inserimento valori
        while (!finished) {
            // Richiede il nome del valore
            String nomeValore = this.view.getInput(
                    String.format(ControllerConstants.PROMPT_VALORE_DOMINIO, valoreCount + 1));

            // Verifica che il nome non sia vuoto
            if (nomeValore.isBlank()) {
                this.view.showErrorMessage(ControllerConstants.ERROR_VALORE_VUOTO);
                continue;
            }

            // Verifica se l'utente vuole terminare l'inserimento
            if (nomeValore.equals("0")) {
                // Se non è stato aggiunto ancora nessun valore, non permette l'uscita
                if (valoreCount == 0) {
                    this.view.showMessage(ControllerConstants.ERROR_MINIMO_VALORE);
                } else {
                    finished = true; // Termina il ciclo se c'è almeno un valore
                }
                continue;
            }

            // Verifica che il valore non sia duplicato
            if (campo.hasValore(nomeValore)) {
                this.view.showErrorMessage(String.format(ControllerConstants.ERROR_VALORE_DUPLICATO, nomeValore));
                continue;
            }

            // Richiede una descrizione opzionale per il valore
            String descrizione = this.view
                    .getInput(String.format(ControllerConstants.PROMPT_DESCRIZIONE_VALORE, nomeValore));

            // Crea il valore con o senza descrizione
            ValoreCampo valore = descrizione.isBlank() ? new ValoreCampo(nomeValore)
                    : new ValoreCampo(nomeValore, descrizione);

            // Aggiunge il valore al dominio del campo
            campo.addValore(valore);
            valoreCount++;

            // Conferma l'aggiunta all'utente
            this.view.showSuccessMessage(String.format(ControllerConstants.SUCCESS_VALORE_AGGIUNTO, nomeValore));

            // Dopo il primo valore, chiede se l'utente vuole aggiungerne altri
            if (valoreCount >= 1) {
                if (!getUserChoice(ControllerConstants.PROMPT_ALTRO_VALORE)) {
                    finished = true; // Termina se l'utente non vuole aggiungere altri valori
                }
            }
        }

        // Mostra il riepilogo dei valori aggiunti
        if (valoreCount > 0) {
            view.showMessage(String.format(ControllerConstants.RIEPILOGO_DOMINIO, campo.getNome()));
            List<ValoreCampo> valori = new ArrayList<>(campo.getDominio());
            for (int i = 0; i < valori.size(); i++) {
                ValoreCampo v = valori.get(i);
                String desc = v.getDescrizione().isEmpty() ? ControllerConstants.NESSUNA_DESCRIZIONE
                        : v.getDescrizione();
                view.showMessage(String.format(ControllerConstants.FORMATO_VALORE_DOMINIO, i + 1, v.getNome(), desc));
            }
        }
    }

    // -----------------------------------------------------------------------------------
    // METODI PER LA GESTIONE DEI FATTORI DI CONVERSIONE
    // -----------------------------------------------------------------------------------

    /**
     * Visualizza i fattori di conversione tra categorie di gerarchie diverse.
     * <p>
     * Permette all'utente di selezionare una categoria foglia di origine
     * e una gerarchia di destinazione, mostrando i fattori di conversione
     * applicati.
     */
    private void visualizzaFattoriConversione() {
        // Mostra il titolo della funzionalità
        view.showMessage(ControllerConstants.TITLE_FATTORI_CONVERSIONE);

        // FASE 1: Selezione della gerarchia di origine
        this.view.showMessage(ControllerConstants.MSG_LISTA_GERARCHIE);
        Gerarchia gerarchiaOrigine = selezionaGerarchia();

        // FASE 2: Selezione della foglia (attività concreta) dalla gerarchia origine
        this.view.showMessage(ControllerConstants.MSG_LISTA_FOGLIE);
        // Mostra la gerarchia con le foglie numerate per facilitare la selezione
        this.view.showMessage(gerarchiaOrigine.toStringSelezioneFoglia());

        // Ottiene la foglia selezionata dall'utente
        TreeNode<Categoria> fogliaScelta = selezionaFoglia(gerarchiaOrigine);

        // FASE 3: Selezione della gerarchia di destinazione per cui visualizzare i
        // fattori
        this.view.showMessage(
                String.format(ControllerConstants.INFO_SELEZIONA_DESTINAZIONE, fogliaScelta.data().getNome()));
        Gerarchia gerarchiaDestinazione = selezionaGerarchia();

        // CALCOLO CONVERSIONE: Il processo coinvolge due fattori:
        // 1. Fattore di conversione interno alla gerarchia di origine (dalla foglia al
        // nodo di riferimento)
        double fattoreIntraGerarchia = gerarchiaOrigine.getFattoreConversione().getFattoreConversione(
                fogliaScelta.level(),
                gerarchiaOrigine.getFattoreConversione().getFogliaRiferimento().level());

        // 2. Fattore di conversione tra gerarchie (se diverse)
        double fattoreInterGerarchia = 1.0; // Default: stessa gerarchia, fattore 1
        if (!gerarchiaOrigine.equals(gerarchiaDestinazione)) {
            // Se le gerarchie sono diverse, ottiene il fattore di conversione tra i loro
            // nodi di riferimento
            fattoreInterGerarchia = this.database.getConversioneGerarchie().getFattoreConversione(
                    gerarchiaOrigine.getFattoreConversione().getFogliaRiferimento().data(),
                    gerarchiaDestinazione.getFattoreConversione().getFogliaRiferimento().data());
        }

        // Calcolo del fattore combinato
        double fattoreCombinato = fattoreIntraGerarchia * fattoreInterGerarchia;

        // VISUALIZZAZIONE RISULTATI: Mostra i fattori di conversione per tutte le
        // foglie
        // della gerarchia di destinazione, in relazione alla foglia selezionata
        this.view
                .showMessage(String.format(ControllerConstants.INFO_VISUALIZZA_FATTORI, fogliaScelta.data().getNome()));
        this.view.showMessage(
                gerarchiaDestinazione.toStringFattoreConversione(fattoreCombinato));
    }

    /**
     * Permette all'utente di selezionare una gerarchia tra quelle disponibili.
     * <p>
     * Mostra un elenco numerato delle gerarchie nel sistema
     * e acquisisce la scelta dell'utente.
     *
     * @return la gerarchia selezionata dall'utente
     */
    private Gerarchia selezionaGerarchia() {
        // Estrae la lista delle gerarchie dal database per numerazione e
        // visualizzazione
        List<Gerarchia> gerarchieList = new ArrayList<>(this.database.getGerarchie());

        // Mostra ciascuna gerarchia con un indice progressivo
        for (int i = 0; i < gerarchieList.size(); i++) {
            Gerarchia gerarchia = gerarchieList.get(i);
            Categoria categoria = gerarchia.root().data();
            // Formato: [1] NomeGerarchia - rappresenta l'indice e il nome della radice
            this.view.showMessage("[" + (i + 1) + "] " + categoria.getNome());
        }
        this.view.showMessage(""); // Linea vuota per migliore leggibilità

        // Richiede all'utente di selezionare una gerarchia tramite il suo indice
        int numScelte = this.database.getGerarchie().size();
        // Gli indici visualizzati partono da 1, quindi sottraiamo 1 per ottenere
        // l'indice effettivo (0-based)
        int indiceScelta = richiediIndiceDaUtente(ControllerConstants.MSG_SCELTA_GERARCHIA, numScelte) - 1;

        // Restituisce la gerarchia selezionata
        return this.database.getGerarchia(indiceScelta);
    }

    /**
     * Permette all'utente di selezionare una categoria foglia da una gerarchia.
     * <p>
     * Mostra le foglie disponibili nella gerarchia e acquisisce la scelta
     * dell'utente.
     *
     * @param gerarchia la gerarchia da cui selezionare una foglia
     * @return la categoria foglia selezionata
     */
    private TreeNode<Categoria> selezionaFoglia(Gerarchia gerarchia) {
        // Ottiene tutte le foglie della gerarchia (le categorie che rappresentano
        // attività concrete)
        ArrayList<TreeNode<Categoria>> foglie = gerarchia.getFoglie();

        // Richiede all'utente di inserire l'indice della foglia desiderata
        int numScelte = foglie.size();
        // Gli indici visualizzati partono da 1, quindi sottraiamo 1 per ottenere
        // l'indice effettivo (0-based)
        int indiceScelta = richiediIndiceDaUtente(ControllerConstants.MSG_SCELTA_FOGLIA, numScelte) - 1;

        // Restituisce la foglia selezionata
        return foglie.get(indiceScelta);
    }

    /**
     * Richiede all'utente di selezionare un'opzione numerica da un intervallo.
     * <p>
     * Valida l'input assicurandosi che sia un numero intero
     * compreso nell'intervallo specificato.
     *
     * @param messaggio il messaggio da mostrare per la richiesta
     * @param numScelte il numero massimo di scelte disponibili
     * @return l'indice numerico selezionato dall'utente (1-based)
     */
    private int richiediIndiceDaUtente(String messaggio, int numScelte) {
        boolean validInput = false;
        int indiceScelta = 0;

        // Ciclo di validazione: continua finché non viene fornito un input valido
        while (!validInput) {
            // Richiede l'input all'utente
            String scelta = this.view.getInput(messaggio);

            try {
                // Tenta di convertire l'input in un numero intero
                indiceScelta = Integer.parseInt(scelta);

                // Verifica che l'indice sia nel range valido (da 1 a numScelte)
                if (indiceScelta > 0 && indiceScelta <= numScelte) {
                    validInput = true; // Input valido, uscita dal ciclo
                } else {
                    // Indice fuori dal range valido
                    this.view.showMessage(String.format(ControllerConstants.MSG_SCELTA_ERRATA, numScelte));
                }
            } catch (NumberFormatException e) {
                // Input non convertibile in numero
                this.view.showMessage(String.format(ControllerConstants.MSG_SCELTA_ERRATA, numScelte));
            }
        }

        // Restituisce l'indice validato
        return indiceScelta;
    }

    /**
     * Crea i fattori di conversione tra una nuova gerarchia e quelle esistenti.
     * <p>
     * Stabilisce relazioni quantitative tra categorie di riferimento di gerarchie
     * diverse,
     * permettendo la conversione di valori tra domini eterogenei.
     *
     * @param gerarchia la nuova gerarchia da collegare alle esistenti
     */
    private void creaConversioneGerarchie(Gerarchia gerarchia) {
        // Spiegazione introduttiva sul funzionamento dei fattori di conversione tra
        // gerarchie
        view.showMessage(ControllerConstants.TITLE_CONVERSIONE_GERARCHIE);
        view.showMessage(ControllerConstants.INFO_CONVERSIONE_INTRO);
        view.showMessage(ControllerConstants.INFO_CONVERSIONE_PONTE);
        view.showMessage(ControllerConstants.INFO_CONVERSIONE_GT1);
        view.showMessage(ControllerConstants.INFO_CONVERSIONE_LT1);

        // Ottiene la foglia di riferimento della nuova gerarchia
        // Questa foglia servirà come "ponte" per tutte le conversioni con altre
        // gerarchie
        TreeNode<Categoria> fogliaRiferimento = gerarchia.getFattoreConversione().getFogliaRiferimento();
        Categoria categoriaRadice = gerarchia.root().data();

        // Mostra informazioni sulla gerarchia corrente e la sua categoria di
        // riferimento
        view.showMessage(String.format(ControllerConstants.INFO_GERARCHIA_NUOVA, categoriaRadice.getNome()));
        view.showMessage(String.format(ControllerConstants.INFO_CATEGORIA_RIFERIMENTO,
                fogliaRiferimento.data().getNome(), fogliaRiferimento.level()));

        // Elenco delle gerarchie esistenti con cui creare conversioni
        List<Gerarchia> gerarchieEsistenti = new ArrayList<>(database.getGerarchie());
        if (gerarchieEsistenti.isEmpty()) {
            // Se non ci sono altre gerarchie nel sistema, non serve creare conversioni
            view.showMessage(ControllerConstants.INFO_NESSUNA_GERARCHIA);
            return;
        }

        // Contatore per il riepilogo finale
        int fattoriCreati = 0;

        // Per ogni gerarchia esistente, crea un fattore di conversione con la nuova
        // gerarchia
        for (Gerarchia gerarchiaEsistente : gerarchieEsistenti) {
            // Salta la gerarchia stessa (non serve conversione con sé stessa)
            if (gerarchiaEsistente.equals(gerarchia)) {
                continue;
            }

            // Ottiene informazioni sulla gerarchia esistente
            Categoria categoriaRadiceEsistente = gerarchiaEsistente.root().data();
            TreeNode<Categoria> fogliaRiferimentoEsistente = gerarchiaEsistente.getFattoreConversione()
                    .getFogliaRiferimento();

            // Visualizza separatore e informazioni sulla gerarchia con cui si sta creando
            // la conversione
            view.showMessage(ControllerConstants.SEPARATORE);
            view.showMessage(
                    String.format(ControllerConstants.INFO_CONVERSIONE_CON, categoriaRadiceEsistente.getNome()));
            view.showMessage(String.format(ControllerConstants.INFO_CATEGORIA_RIFERIMENTO,
                    fogliaRiferimentoEsistente.data().getNome(), fogliaRiferimentoEsistente.level()));

            // Visualizza il percorso dalla radice alle foglie di riferimento per entrambe
            // le gerarchie
            // Questo aiuta l'utente a comprendere meglio la relazione tra le categorie
            view.showMessage(ControllerConstants.INFO_PERCORSO_NUOVO);
            mostraPercorsoFino(fogliaRiferimento);

            view.showMessage(ControllerConstants.INFO_PERCORSO_ESISTENTE);
            mostraPercorsoFino(fogliaRiferimentoEsistente);

            // Richiede all'utente il fattore di conversione con validazione
            double fattore = richiediEValidaFattore(
                    fogliaRiferimento.data().getNome() + " (" + categoriaRadice.getNome() + ")",
                    fogliaRiferimentoEsistente.data().getNome() + " (" + categoriaRadiceEsistente.getNome() + ")",
                    0.5, 2.0, // Limiti min e max del fattore
                    ControllerConstants.ERROR_FATTORE_RANGE); // Messaggio di errore in caso di input non valido

            // Aggiunge il fattore di conversione al sistema globale di conversione
            this.database.getConversioneGerarchie().addFattoreConversione(
                    fogliaRiferimento.data(),
                    fogliaRiferimentoEsistente.data(),
                    fattore);

            // Conferma l'aggiunta del fattore e incrementa il contatore
            view.showSuccessMessage(String.format(ControllerConstants.SUCCESS_FATTORE_AGGIUNTO, fattore));
            fattoriCreati++;
        }

        // Riepilogo finale dell'operazione
        if (fattoriCreati > 0) {
            view.showSuccessMessage(String.format(ControllerConstants.SUCCESS_FATTORI_CREATI, fattoriCreati));
        } else {
            view.showMessage(ControllerConstants.INFO_NESSUN_FATTORE);
        }
    }

    /**
     * Visualizza il percorso completo dalla radice a un nodo specifico.
     * <p>
     * Mostra la catena gerarchica che collega la categoria radice
     * alla categoria specificata, con formattazione grafica.
     *
     * @param nodo il nodo di cui visualizzare il percorso
     */
    private void mostraPercorsoFino(TreeNode<Categoria> nodo) {
        // Crea una lista per memorizzare il percorso dalla radice alla foglia
        List<Categoria> percorso = new ArrayList<>();
        TreeNode<Categoria> corrente = nodo;

        // Risale l'albero dal nodo specificato fino alla radice, raccogliendo tutte le
        // categorie
        while (corrente != null) {
            // addFirst per avere l'ordine corretto (dalla radice alla foglia)
            percorso.addFirst(corrente.data());
            corrente = corrente.parent(); // Risale al nodo genitore
        }

        // Visualizza il percorso con indentazione progressiva
        for (int i = 0; i < percorso.size(); i++) {
            // Costruisce il prefisso con indentazione crescente e simboli grafici
            // appropriati
            String prefix = "   " + "  ".repeat(i) + (i == 0 ? "" : "|---");

            // Aggiunge un asterisco per identificare l'ultimo elemento (foglia o nodo
            // specificato)
            view.showMessage(prefix + percorso.get(i).getNome() +
                    (i == percorso.size() - 1 ? " *" : ""));
        }
    }

    /**
     * Crea i fattori di conversione tra categorie foglia di una stessa gerarchia.
     * <p>
     * Definisce relazioni quantitative tra attività della stessa gerarchia,
     * basate sul loro livello di specializzazione relativa.
     *
     * @param gerarchia la gerarchia su cui definire i fattori di conversione
     */
    private void creaFattoriConversione(Gerarchia gerarchia) {
        // Ottiene tutte le foglie della gerarchia, ordinate per livello
        ArrayList<TreeNode<Categoria>> foglie = gerarchia.getFogliePerLivello();

        // Rimuove la prima foglia e la imposta come foglia di riferimento
        // Questa foglia servirà come punto di riferimento per tutte le altre
        // conversioni
        TreeNode<Categoria> fogliaRiferimento = foglie.removeFirst();
        gerarchia.getFattoreConversione().setNodoRiferimento(fogliaRiferimento);

        // Itera su tutte le foglie rimanenti per definire i loro fattori di conversione
        for (TreeNode<Categoria> foglia : foglie) {
            // Determina se la foglia corrente è più specializzata della foglia di
            // riferimento
            // basandosi sul livello nella gerarchia (più profondo = più specializzato)
            boolean isSpecializzata = foglia.level() > fogliaRiferimento.level();

            // Imposta i limiti del fattore di conversione in base alla specializzazione
            double minFattore = isSpecializzata ? 1.0 : 0.5; // Minimo: 1.0 per specializzate, 0.5 per altre
            double maxFattore = isSpecializzata ? 2.0 : 1.0; // Massimo: 2.0 per specializzate, 1.0 per altre

            // Seleziona il messaggio di errore appropriato
            String messaggioErrore = isSpecializzata ? ControllerConstants.ERROR_FATTORE_SPECIALIZZATO
                    : ControllerConstants.ERROR_FATTORE_NON_SPECIALIZZATO;

            // Richiede all'utente di definire il fattore di conversione
            double fattore = richiediEValidaFattore(
                    fogliaRiferimento.data().getNome(), // Categoria di riferimento
                    foglia.data().getNome(), // Categoria da confrontare
                    minFattore, // Valore minimo accettabile
                    maxFattore, // Valore massimo accettabile
                    messaggioErrore); // Messaggio di errore

            // Aggiunge il fattore di conversione al sistema
            gerarchia.getFattoreConversione().addFattoreConversione(foglia.level(), fattore);
            view.showMessage(ControllerConstants.SUCCESS_FATTORE_SEMPLICE);
        }
    }

    /**
     * Richiede e valida un fattore di conversione numerico dall'utente.
     * <p>
     * Acquisisce e verifica che il valore sia un numero decimale
     * compreso nell'intervallo specificato.
     *
     * @param categoriaOrigine      nome della categoria di origine
     * @param categoriaDestinazione nome della categoria di destinazione
     * @param minFattore            valore minimo accettabile
     * @param maxFattore            valore massimo accettabile
     * @param messaggioErrore       messaggio da mostrare in caso di errore
     * @return il fattore di conversione validato
     */
    private double richiediEValidaFattore(String categoriaOrigine, String categoriaDestinazione,
            double minFattore, double maxFattore, String messaggioErrore) {
        // Ciclo di validazione: continua finché non viene fornito un valore valido
        while (true) {
            // Richiede all'utente di inserire il fattore di conversione
            String value = view.getInput(
                    String.format(ControllerConstants.PROMPT_FATTORE, categoriaOrigine, categoriaDestinazione));

            try {
                // Converte l'input in un valore numerico
                double fattore = Double.parseDouble(value);

                // Verifica che il valore sia nel range consentito
                if (fattore >= minFattore && fattore <= maxFattore) {
                    return fattore; // Valore valido, esce dal ciclo
                } else {
                    // Valore fuori dal range consentito
                    view.showMessage(messaggioErrore);
                }
            } catch (NumberFormatException e) {
                // Input non convertibile in numero decimale
                view.showMessage(ControllerConstants.ERROR_FATTORE_FORMATO);
            }
        }
    }

    // -----------------------------------------------------------------------------------
    // METODI DI SUPPORTO PER L'INPUT UTENTE
    // -----------------------------------------------------------------------------------

    /**
     * Richiede e valida uno username per la registrazione.
     * <p>
     * Acquisisce uno username dall'utente e verifica che rispetti
     * i requisiti di sicurezza e formattazione.
     *
     * @return lo username validato, o null se il processo è annullato
     */
    private String getUsername() {
        while (true) {
            // Richiede uno username all'utente, specificando i requisiti di lunghezza
            String username = view
                    .getInput(String.format(ControllerConstants.PROMPT_NEW_USERNAME, UserUtil.USERNAME_LENGTH));

            // Verifica se lo username soddisfa i criteri di sicurezza
            if (UserUtil.checkUsername(username))
                return username; // Username valido

            // Se lo username non è valido, mostra un messaggio di errore
            // e richiede un nuovo tentativo
            view.showMessage(ControllerConstants.ERROR_INVALID_USERNAME);
        }
    }

    /**
     * Richiede e valida una password per la registrazione.
     * <p>
     * Acquisisce una password dall'utente e verifica che rispetti
     * i requisiti minimi di sicurezza (lunghezza, complessità).
     *
     * @return la password validata, o null se il processo è annullato
     */
    private String getPassword() {
        while (true) {
            // Richiede una password all'utente, specificando i requisiti minimi di
            // lunghezza
            // e complessità (maiuscole, minuscole, caratteri speciali)
            String password = view
                    .getPasswordInput(String.format(ControllerConstants.PROMPT_NEW_PASSWORD, UserUtil.PASSWORD_LENGTH));

            // Verifica se la password soddisfa i criteri di sicurezza
            if (UserUtil.checkPassword(password))
                return password; // Password valida

            // Se la password non è valida, mostra un messaggio di errore
            // e richiede un nuovo tentativo
            view.showMessage(ControllerConstants.ERROR_INVALID_PASSWORD);
        }
    }

    /**
     * TODO ma perchè esiste questo metodo
     * 
     * @return
     */
    private String getMail() {
        return view.getInput(ControllerConstants.PROMPT_MAIL);
    }

    /**
     * Acquisisce una scelta binaria (sì/no) dall'utente.
     * <p>
     * Richiede all'utente di inserire 's' per sì o 'n' per no,
     * validando l'input fino a ottenere una risposta valida.
     *
     * @param messaggio il messaggio da mostrare per la richiesta
     * @return true se l'utente risponde affermativamente, false altrimenti
     */
    // TODO: sostituire con view.getConfirmation
    private boolean getUserChoice(String messaggio) {
        while (true) {
            // Richiede una risposta all'utente, mostrando il messaggio specificato
            String role = view.getInput(messaggio);

            // Verifica se la risposta è affermativa
            if (role.equalsIgnoreCase("s"))
                return true;

            // Verifica se la risposta è negativa
            if (role.equalsIgnoreCase("n"))
                return false;

            // Se l'input non è valido, mostra un messaggio di errore
            // e richiede un nuovo tentativo
            view.showErrorMessage(ControllerConstants.ERROR_INVALID_INPUT);
        }
    }

    /**
     * Registra un errore nel sistema di logging dell'applicazione.
     * <p>
     * Gestisce un'eccezione in modo centralizzato, registrando informazioni
     * dettagliate per il debug e notificando appropriatamente l'utente.
     *
     * @param e l'eccezione da registrare
     */
    private void logErrore(Exception e) {
        // Inizializza un builder per il messaggio di log
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("\n=== ERRORE APPLICAZIONE ===\n");
        logMessage.append("Contesto: ").append("Errore imprevisto nel menu principale").append("\n");
        logMessage.append("Timestamp: ").append(new java.util.Date()).append("\n");
        logMessage.append("Tipo errore: ").append(e.getClass().getName()).append("\n");
        logMessage.append("Messaggio: ").append(e.getMessage()).append("\n");

        // Aggiungi informazioni sulla causa originale dell'errore, se presente
        Throwable causa = e.getCause();
        if (causa != null) {
            logMessage.append("Causa: ").append(causa.getMessage()).append("\n");
        }

        // Aggiungi stack trace formattata
        logMessage.append("Stack trace:\n");
        for (StackTraceElement element : e.getStackTrace()) {
            // Includi solo elementi relativi al nostro package per ridurre il rumore
            if (element.getClassName().startsWith("model") ||
                    element.getClassName().startsWith("controller") ||
                    element.getClassName().startsWith("view")) {
                logMessage.append("  at ").append(element).append("\n");
            }
        }

        // Registra l'errore nel file di log (in modalità sviluppo stampa su console)
        System.err.println(logMessage);

        try {
            // Scrivi l'errore su un file di log per riferimento futuro
            String logFilePath = "error_log.txt";
            BufferedWriter writer = new BufferedWriter(
                    new FileWriter(logFilePath, true)); // true = append mode
            writer.write(logMessage.toString());
            writer.newLine();
            writer.close();
        } catch (IOException loggingError) {
            // In caso di errore durante il logging, stampa almeno un avviso
            System.err.println("Impossibile scrivere nel file di log: " + loggingError.getMessage());
        }
    }
}