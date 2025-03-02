package controller;

import com.scalified.tree.TreeNode;

import constants.ControllerConstants;
import model.*;
import util.UserUtil;
import view.ConsoleView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Controller principale dell'applicazione che gestisce la logica di business e
 * coordina
 * le interazioni tra vista (ConsoleView) e modello (Database e classi di
 * dominio).
 * <p>
 * Questa classe implementa il pattern MVC (Model-View-Controller) e si occupa
 * di:
 * <ul>
 * <li>Gestire l'autenticazione e la registrazione degli utenti</li>
 * <li>Coordinare le operazioni di creazione e visualizzazione di
 * comprensori</li>
 * <li>Gestire la creazione e visualizzazione di gerarchie e categorie</li>
 * <li>Gestire i fattori di conversione tra gerarchie e categorie</li>
 * <li>Persistere i dati tramite serializzazione/deserializzazione</li>
 * </ul>
 * <p>
 * Il controller fornisce un'interfaccia guidata per tutte le operazioni
 * dell'applicazione,
 * con validazione degli input e gestione strutturata degli errori.
 * <p>
 * L'applicazione supporta due tipologie di utenti:
 * <ul>
 * <li>Configuratore: può creare e gestire comprensori, gerarchie e fattori di
 * conversione</li>
 * <li>Fruitore: (funzionalità da implementare in future versioni)</li>
 * </ul>
 * <p>
 * Tutte le informazioni create vengono salvate automaticamente su file alla
 * chiusura dell'applicazione
 * o dopo specifiche operazioni.
 *
 * @version 1.0
 * @see model.Database
 * @see model.Utente
 * @see model.Comprensorio
 * @see model.Gerarchia
 * @see model.Categoria
 * @see model.Campo
 * @see view.ConsoleView
 */
public class Controller {

    private Database database;
    private final ConsoleView view;

    // -----------------------------------------------------------------------------------
    // METODI PRINCIPALI
    // -----------------------------------------------------------------------------------

    /**
     * Costruttore della classe Controller.
     * Inizializza una nuova istanza del controller, creando un database vuoto e
     * collegandolo
     * alla vista specificata. Il controller è il componente centrale
     * dell'architettura MVC che
     * coordina l'interazione tra il modello (Database) e la vista (ConsoleView).
     *
     * @param view la vista console utilizzata per l'interazione con l'utente,
     *             che si occuperà di mostrare messaggi e raccogliere input
     * 
     * @see model.Database
     * @see view.ConsoleView
     */
    public Controller(ConsoleView view) {
        // Inizializza un nuovo database vuoto
        this.database = new Database();
        // Mantiene un riferimento alla vista per interagire con l'utente
        this.view = view;
    }

    /**
     * Avvia il ciclo principale del controller.
     * Questo metodo rappresenta il punto di ingresso dell'applicazione e gestisce
     * il flusso
     * di esecuzione principale. Esegue le seguenti operazioni:
     * <ol>
     * <li>Carica i dati persistenti da file all'avvio dell'applicazione</li>
     * <li>Gestisce il ciclo principale mostrandone il menu all'utente</li>
     * <li>Elabora le scelte dell'utente invocando i relativi handler</li>
     * <li>Gestisce l'uscita dall'applicazione salvando i dati</li>
     * <li>Implementa un meccanismo di gestione delle eccezioni per garantire
     * robustezza</li>
     * </ol>
     * 
     * Utilizza un ciclo infinito interrotto solo dall'esplicita richiesta di
     * uscita.
     * 
     * @see #loadData()
     * @see #handleLogin()
     * @see #handleRegistration()
     * @see #salvaTutti()
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
                        if (handleRegistration()) {
                            // Se la registrazione va a buon fine, salva i dati utente
                            salvaUtenti();
                            view.showSuccessMessage(ControllerConstants.SAVING_USERS_SUCCESS);
                        }
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
                e.printStackTrace(); // Stampa lo stack trace per il debug
                view.pressEnterToContinue(); // Attende input utente prima di continuare
            }
        }
    }

    /**
     * Carica tutti i dati dal disco all'avvio dell'applicazione.
     * Questo metodo tenta di caricare tutti i file di dati serializzati necessari
     * per
     * l'esecuzione dell'applicazione, inclusi utenti, comprensori, gerarchie e
     * conversioni.
     * Gestisce automaticamente i casi in cui i file non esistono o sono vuoti,
     * permettendo
     * all'applicazione di iniziare con uno stato pulito.
     * Se si verificano errori durante il caricamento, crea un nuovo database vuoto
     * e continua
     * l'esecuzione, garantendo che l'applicazione possa sempre essere avviata.
     * Sequenza di operazioni:
     * <ol>
     * <li>Verifica l'esistenza di ciascun file dati</li>
     * <li>Carica i dati se presenti, o inizializza strutture vuote</li>
     * <li>Gestisce eventuali errori di deserializzazione</li>
     * <li>Fornisce feedback all'utente sul processo di caricamento</li>
     * </ol>
     * 
     * @see #fileEsistente(String)
     * @see model.Database#deserializeUtenti(String)
     * @see model.Database#deserializeComprensori(String)
     * @see model.Database#deserializeGerarchie(String)
     * @see model.Database#deserializeConversioneGerarchie(String)
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
     * Questo metodo di utilità controlla due condizioni:
     * <ol>
     * <li>Se il file esiste fisicamente nel file system</li>
     * <li>Se il file contiene effettivamente dei dati (dimensione > 0)</li>
     * </ol>
     * 
     * Utilizzato principalmente durante il caricamento dei dati per determinare
     * se ci sono dati serializzati da caricare o se l'applicazione deve iniziare
     * con strutture dati vuote.
     *
     * @param path percorso del file da verificare
     * @return true se il file esiste ed è non vuoto, false altrimenti
     * 
     * @see java.io.File#exists()
     * @see java.io.File#length()
     * @see #loadData()
     */
    private boolean fileEsistente(String path) {
        java.io.File file = new java.io.File(path);
        // Controlla sia l'esistenza che la dimensione del file (deve essere > 0)
        return file.exists() && file.length() > 0;
    }

    /**
     * Salva i dati degli utenti su file.
     * Questo metodo serializza solo la componente utenti del database su un file
     * dedicato.
     * Viene chiamato tipicamente dopo operazioni che modificano gli utenti (come
     * registrazioni
     * o modifiche di profilo) per garantire la persistenza immediata dei dati.
     * In caso di errori durante il salvataggio, mostra un messaggio di errore ma
     * non
     * interrompe l'esecuzione dell'applicazione.
     * 
     * @see model.Database#serializeUtenti(String)
     * @see #handleRegistration()
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
     * Salva tutti i dati su file prima di uscire dall'applicazione.
     * Questo metodo esegue un salvataggio completo di tutte le strutture dati del
     * sistema:
     * utenti, comprensori, gerarchie e fattori di conversione.
     * Procedura di salvataggio:
     * <ol>
     * <li>Serializza i dati utenti</li>
     * <li>Serializza i dati dei comprensori</li>
     * <li>Serializza le gerarchie</li>
     * <li>Serializza i fattori di conversione tra gerarchie</li>
     * </ol>
     * 
     * Visualizza un messaggio di conferma al completamento, o un messaggio di
     * errore
     * se si verifica un problema durante il salvataggio.
     *
     * @see model.Database#serializeUtenti(String)
     * @see model.Database#serializeComprensori(String)
     * @see model.Database#serializeGerarchie(String)
     * @see model.Database#serializeConversioneGerarchie(String)
     * @see #start()
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
     * Gestisce il login dell'utente.
     * Questo metodo coordina il processo di autenticazione, verificando la presenza
     * di utenti nel sistema
     * e reindirizzando l'utente al menu appropriato in base al suo ruolo
     * (configuratore o fruitore).
     * Procedura di login:
     * <ol>
     * <li>Verifica che ci siano utenti registrati nel sistema</li>
     * <li>Richiede le credenziali di login all'utente</li>
     * <li>Verifica le credenziali contro il database</li>
     * <li>Se le credenziali sono valide, indirizza l'utente al menu corrispondente
     * al suo ruolo</li>
     * </ol>
     * 
     * @see #loginUtente()
     * @see #manageConfiguratore()
     * @see model.Database#getUtenti()
     * @see model.Utente#isConfig()
     */
    private void handleLogin() {
        // Verifica preliminare: almeno un utente deve essere registrato nel sistema
        if (database.getUtenti().isEmpty()) {
            view.showMessage(ControllerConstants.NO_USERS_REGISTERED);
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
            view.showMessage("TODO menu Fruitore");
        }
    }

    /**
     * Gestisce il processo di registrazione di un nuovo utente.
     * Coordina la raccolta di dati dell'utente, la validazione e la registrazione
     * nel database.
     * Fornisce un feedback all'utente sull'esito dell'operazione.
     * Il processo include:
     * <ol>
     * <li>Richiesta di credenziali temporanee per l'autenticazione iniziale (misura
     * di sicurezza)</li>
     * <li>Raccolta di un nome utente valido</li>
     * <li>Raccolta di una password valida</li>
     * <li>Selezione del tipo di account (configuratore o fruitore)</li>
     * <li>Registrazione dell'utente nel database</li>
     * </ol>
     *
     * @return true se la registrazione è completata con successo, false altrimenti
     * 
     * @see #registerUtente()
     * @see model.Database#registerUtente(String, String, boolean)
     */
    private boolean handleRegistration() {
        // Tenta la registrazione dell'utente
        boolean checkRegistration = registerUtente();

        // Mostra un messaggio di esito (successo o fallimento)
        view.showMessage(checkRegistration ? ControllerConstants.REGISTRATION_SUCCESS : ControllerConstants.REGISTRATION_FAILED);

        // Ritorna l'esito della registrazione per ulteriori elaborazioni
        return checkRegistration;
    }

    /**
     * Gestisce il menu e le funzionalità dell'utente configuratore.
     * Presenta un'interfaccia che permette al configuratore di visualizzare e
     * creare
     * comprensori, gerarchie e fattori di conversione.
     * Implementa un ciclo che continua fino a quando l'utente non sceglie di
     * uscire.
     * All'interno del ciclo, gestisce:
     * <ol>
     * <li>Visualizzazione del menu configuratore</li>
     * <li>Raccolta della scelta dell'utente</li>
     * <li>Esecuzione dell'operazione corrispondente</li>
     * <li>Gestione degli errori durante l'esecuzione delle operazioni</li>
     * <li>Logout quando richiesto</li>
     * </ol>
     * 
     * @see #visualizzaComprensori()
     * @see #visualizzaGerarchie()
     * @see #visualizzaFattoriConversione()
     * @see #creaComprensorio()
     * @see #creaGerarchia()
     * @see view.ConsoleView#showMenuConfiguratore()
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
     * Visualizza l'elenco completo dei comprensori presenti nel sistema.
     * Se non ci sono comprensori, mostra un messaggio appropriato.
     * Il metodo:
     * <ol>
     * <li>Controlla se esistono comprensori nel database</li>
     * <li>In caso negativo, mostra un messaggio di errore</li>
     * <li>In caso affermativo, visualizza la lista dei comprensori formattata</li>
     * </ol>
     * 
     * @see model.Database#getComprensori()
     * @see model.Database#toStringComprensori()
     * @see view.ConsoleView#showErrorMessage(String)
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
     * Visualizza l'elenco completo delle gerarchie presenti nel sistema.
     * Se non ci sono gerarchie, mostra un messaggio appropriato.
     * Il metodo:
     * <ol>
     * <li>Controlla se esistono gerarchie nel database</li>
     * <li>In caso negativo, mostra un messaggio di errore</li>
     * <li>In caso affermativo, visualizza la lista delle gerarchie formattata</li>
     * </ol>
     * 
     * @see model.Database#getGerarchie()
     * @see model.Database#toStringGerarchie()
     * @see view.ConsoleView#showErrorMessage(String)
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
     * Utilizzato come controllo preventivo prima di eseguire operazioni che
     * richiedono
     * l'esistenza di almeno una gerarchia.
     * Il metodo:
     * <ol>
     * <li>Controlla se esistono gerarchie nel database</li>
     * <li>In caso negativo, mostra un messaggio di errore e ritorna false</li>
     * <li>In caso affermativo, ritorna true senza visualizzare messaggi</li>
     * </ol>
     * 
     * @return true se ci sono gerarchie nel sistema, false altrimenti
     * 
     * @see model.Database#getGerarchie()
     * @see view.ConsoleView#showErrorMessage(String)
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
     * Gestisce il processo completo di creazione di un nuovo comprensorio.
     * Guida l'utente attraverso i passaggi di definizione e configurazione del
     * comprensorio,
     * salvando i dati su file in caso di successo.
     * Procedura di creazione:
     * <ol>
     * <li>Mostra informazioni introduttive sul concetto di comprensorio</li>
     * <li>Avvia il processo di creazione raccogliendo nome e comuni</li>
     * <li>Se la creazione ha successo, salva i dati su file</li>
     * <li>Mostra un messaggio di conferma appropriato</li>
     * </ol>
     * 
     * @see #createComprensorio()
     * @see model.Database#serializeComprensori(String)
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
     * Gestisce il processo completo di creazione di una nuova gerarchia.
     * Guida l'utente nella creazione dell'intera struttura gerarchica, inclusi
     * i fattori di conversione interni ed esterni alla gerarchia.
     * Procedura di creazione:
     * <ol>
     * <li>Mostra informazioni introduttive sulla creazione della gerarchia</li>
     * <li>Crea la struttura principale della gerarchia con categorie e campi</li>
     * <li>Visualizza un riepilogo della gerarchia creata</li>
     * <li>Configura i fattori di conversione interni tra le categorie</li>
     * <li>Se esistono altre gerarchie, configura i fattori di conversione tra
     * gerarchie</li>
     * <li>Aggiunge la nuova gerarchia al database e salva i dati</li>
     * </ol>
     * 
     * @see #createGerarchia()
     * @see #creaFattoriConversione(Gerarchia)
     * @see #creaConversioneGerarchie(Gerarchia)
     * @see model.Database#addGerarchia(Gerarchia)
     * @see model.Database#serializeGerarchie(String)
     * @see model.Database#serializeConversioneGerarchie(String)
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

    // -----------------------------------------------------------------------------------
    // METODI DI AUTENTICAZIONE E REGISTRAZIONE
    // -----------------------------------------------------------------------------------

    /**
     * Effettua il login di un utente nel sistema.
     * Questo metodo raccoglie le credenziali dell'utente (username e password) e
     * verifica
     * la loro validità attraverso il database. Supporta sia utenti di tipo
     * Configuratore
     * che Fruitore.
     * <p>
     * Il processo di login:
     * <ol>
     * <li>Richiede l'inserimento di username all'utente</li>
     * <li>Richiede l'inserimento della password all'utente</li>
     * <li>Verifica le credenziali attraverso il database</li>
     * </ol>
     * <p>
     * Non viene effettuato alcun controllo preliminare sulla validità delle
     * credenziali
     * inserite dall'utente in questa fase; tale verifica è delegata al database.
     *
     * @return l'istanza dell'utente autenticato se le credenziali sono corrette,
     *         null altrimenti
     * 
     * @see model.Database#loginUtente(String, String)
     * @see model.Utente
     * @see #handleLogin()
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
     * Registra un nuovo utente nel sistema.
     * Questo metodo guida l'utente attraverso il processo completo di
     * registrazione,
     * che include un'autenticazione preliminare di sicurezza, la raccolta di
     * informazioni
     * per il nuovo account e la registrazione effettiva nel database.
     * <p>
     * Il processo di registrazione:
     * <ol>
     * <li>Esegue un'autenticazione preliminare con credenziali temporanee (misura
     * di sicurezza)</li>
     * <li>Raccoglie uno username valido secondo i criteri di sicurezza</li>
     * <li>Raccoglie una password valida secondo i criteri di sicurezza</li>
     * <li>Determina se l'utente sarà di tipo Configuratore o Fruitore</li>
     * <li>Registra le informazioni nel database</li>
     * </ol>
     * <p>
     * Il processo può essere interrotto in diversi punti se l'utente fornisce input
     * non validi
     * o se si verificano problemi nella registrazione.
     *
     * @return true se l'utente è stato registrato con successo, false in caso di
     *         fallimento o annullamento
     * 
     * @see #defaultUtente()
     * @see #getUsername()
     * @see #getPassword()
     * @see #getUserChoice(String)
     * @see model.Database#registerUtente(String, String, boolean)
     */
    public boolean registerUtente() {
        // Step 1: Autenticazione di sicurezza con credenziali temporanee
        // Questo passaggio previene registrazioni automatizzate o non autorizzate
        if (!defaultUtente())
            return false; // Se l'autenticazione fallisce, interrompi la registrazione

        // Step 2: Raccolta di uno username valido
        String username = getUsername();
        if (username == null)
            return false; // Se l'utente non fornisce uno username valido, interrompi

        // Step 3: Raccolta di una password valida
        String password = getPassword();
        if (password == null)
            return false; // Se l'utente non fornisce una password valida, interrompi

        // Step 4: Determinazione del tipo di account (Configuratore o Fruitore)
        boolean isConfig = getUserChoice(ControllerConstants.PROMPT_CONFIG_ROLE);

        // Step 5: Registrazione effettiva dell'utente nel database
        // Il metodo ritorna true se la registrazione ha successo, false altrimenti
        return database.registerUtente(username, password, isConfig);
    }

    /**
     * Effettua un'autenticazione preliminare con credenziali generate casualmente.
     * Questo metodo implementa un meccanismo di sicurezza per prevenire
     * registrazioni automatizzate,
     * mostrando all'utente credenziali generate casualmente che deve inserire
     * correttamente.
     * <p>
     * Il processo:
     * <ol>
     * <li>Genera uno username casuale temporaneo</li>
     * <li>Genera una password casuale temporanea</li>
     * <li>Mostra le credenziali all'utente</li>
     * <li>Richiede all'utente di inserire le credenziali mostrate</li>
     * <li>Concede fino a tre tentativi per inserire correttamente le
     * credenziali</li>
     * </ol>
     * <p>
     * Questo metodo è utilizzato come primo step nel processo di registrazione
     * per verificare che l'utente stia prestando attenzione e sia un utente reale.
     *
     * @return true se l'utente inserisce correttamente le credenziali temporanee,
     *         false altrimenti
     * 
     * @see util.UserUtil#generateRandomUsername()
     * @see util.UserUtil#generateRandomPassword()
     * @see #registerUtente()
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
     * Crea un nuovo comprensorio nel sistema.
     * Questo metodo implementa un processo guidato per la creazione di un nuovo
     * comprensorio,
     * raccogliendo un nome valido e aggiungendo i comuni che lo compongono.
     * <p>
     * Il processo di creazione è suddiviso in due fasi principali:
     * <ol>
     * <li><strong>Definizione del nome</strong>: Raccoglie e valida il nome del
     * comprensorio</li>
     * <li><strong>Aggiunta dei comuni</strong>: Guida l'utente nell'aggiunta di
     * almeno un comune</li>
     * </ol>
     * <p>
     * Durante la fase di definizione del nome, vengono eseguiti diversi controlli
     * di validazione:
     * <ul>
     * <li>Verifica che il nome non sia vuoto</li>
     * <li>Verifica che il nome abbia una lunghezza minima (3 caratteri)</li>
     * <li>Verifica che non esista già un comprensorio con lo stesso nome</li>
     * </ul>
     * <p>
     * L'utente può annullare l'operazione in qualsiasi momento inserendo '0'.
     *
     * @return {@code true} se il comprensorio è stato creato con successo e
     *         aggiunto al database,
     *         {@code false} se l'operazione è stata annullata o è fallita
     * 
     * @see #addComuniToComprensorio(Comprensorio)
     * @see model.Database#addComprensorio(Comprensorio)
     * @see model.Database#hasComprensorio(String)
     * @see model.Comprensorio
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
     * Aggiunge uno o più comuni a un comprensorio esistente.
     * Questo metodo implementa un ciclo interattivo che consente all'utente di
     * aggiungere
     * successivamente più comuni a un comprensorio, con controlli di validazione
     * per ciascuno.
     * <p>
     * Per ogni comune che l'utente tenta di aggiungere, vengono eseguiti i seguenti
     * controlli:
     * <ul>
     * <li>Verifica che il nome del comune non sia vuoto</li>
     * <li>Verifica che il nome abbia una lunghezza minima (2 caratteri)</li>
     * <li>Verifica che il comune non sia già presente nel comprensorio</li>
     * </ul>
     * <p>
     * Caratteristiche del processo:
     * <ul>
     * <li>L'utente può terminare l'inserimento digitando '0', ma solo dopo aver
     * aggiunto almeno un comune</li>
     * <li>Dopo l'aggiunta di ciascun comune, l'utente può scegliere se continuare o
     * terminare</li>
     * <li>Il metodo visualizza un contatore progressivo dei comuni aggiunti</li>
     * </ul>
     * <p>
     * Il metodo modifica direttamente l'oggetto comprensorio passato come
     * parametro,
     * aggiungendo a esso i comuni validi inseriti dall'utente.
     *
     * @param comprensorio il comprensorio a cui aggiungere i comuni
     * 
     * @see model.Comprensorio#addComune(String)
     * @see model.Comprensorio#hasComune(String)
     * @see #getUserChoice(String)
     * @see #createComprensorio()
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
            this.view.showSuccessMessage(String.format(ControllerConstants.SUCCESS_COMUNE_AGGIUNTO, comune, comprensorio.getNome()));

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

    // -----------------------------------------------------------------------------------
    // METODI PER LA GESTIONE DELLE GERARCHIE
    // -----------------------------------------------------------------------------------

    /**
     * Crea una nuova gerarchia nel sistema.
     * Questo metodo implementa un processo guidato per la definizione completa di
     * una nuova gerarchia,
     * costruendo l'intera struttura ad albero con categorie e campi caratteristici.
     * <p>
     * Il processo di creazione è articolato in fasi:
     * <ol>
     * <li>Creazione della categoria radice (con campo caratteristico
     * obbligatorio)</li>
     * <li>Costruzione ricorsiva della struttura gerarchica basata sui valori di
     * dominio</li>
     * <li>Definizione delle categorie foglia (attività concrete)</li>
     * <li>Riepilogo finale della struttura creata</li>
     * </ol>
     * <p>
     * La gerarchia viene costruita utilizzando un algoritmo iterativo con uno
     * stack, che permette
     * di visitare in profondità l'albero senza utilizzare ricorsione esplicita. Per
     * ogni nodo
     * sono tracciate informazioni sul progresso della creazione attraverso mappe di
     * contatori.
     *
     * @return la gerarchia completa creata
     * 
     * @see #createCategoria(String, Gerarchia)
     * @see model.Gerarchia
     * @see model.Categoria
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
     * Crea una nuova categoria per una gerarchia.
     * Guida l'utente nella definizione di una categoria, verificando la validità
     * del nome
     * e permettendo l'aggiunta opzionale di un campo caratteristico (obbligatorio
     * per la radice).
     * <p>
     * Il metodo effettua diversi controlli di validazione:
     * <ul>
     * <li>Verifica che il nome non sia vuoto</li>
     * <li>Verifica l'unicità del nome all'interno della gerarchia</li>
     * <li>Verifica che la categoria radice abbia un campo caratteristico</li>
     * </ul>
     * <p>
     * Le categorie possono essere di due tipi:
     * <ul>
     * <li><b>Con campo caratteristico:</b> categorie intermedie che possono avere
     * sottocategorie</li>
     * <li><b>Senza campo caratteristico:</b> categorie foglia che rappresentano
     * attività concrete</li>
     * </ul>
     *
     * @param nomePadre il nome della categoria padre, null se è la categoria radice
     * @param root      la gerarchia radice, null se è la categoria radice stessa
     * @return la categoria creata con il nome e l'eventuale campo caratteristico
     * 
     * @see #createCampo()
     * @see model.Categoria
     * @see model.Campo
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
                view.showErrorMessage(root == null ? ControllerConstants.ERROR_NOME_GERARCHIA_DUPLICATO : ControllerConstants.ERROR_NOME_CATEGORIA_DUPLICATO);
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
            view.showSuccessMessage(String.format(ControllerConstants.SUCCESS_CATEGORIA_CON_CAMPO, nome, campo.getNome()));
        } else {
            view.showSuccessMessage(String.format(ControllerConstants.SUCCESS_CATEGORIA_FOGLIA, nome));
        }

        // Crea e restituisce la nuova categoria
        return new Categoria(nome, campo);
    }

    /**
     * Crea un campo caratteristico per una categoria.
     * Un campo caratteristico definisce le proprietà di una categoria e determina
     * le possibili sottocategorie che possono essere create.
     * <p>
     * Il processo di creazione consiste in:
     * <ol>
     * <li>Definizione del nome del campo (es. "Disciplina", "Area geografica")</li>
     * <li>Creazione del dominio di valori possibili per il campo</li>
     * </ol>
     * <p>
     * Il metodo garantisce che ogni campo abbia almeno un valore nel dominio,
     * ripetendo la richiesta in caso di dominio vuoto.
     *
     * @return il campo creato con il suo dominio di valori
     * 
     * @see #createDominio(Campo)
     * @see model.Campo
     * @see model.ValoreCampo
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
     * Crea un dominio di valori per un campo caratteristico.
     * Il dominio definisce l'insieme dei possibili valori che il campo può
     * assumere,
     * e determina le sottocategorie che verranno generate.
     * <p>
     * Ogni valore nel dominio può avere:
     * <ul>
     * <li>Un nome obbligatorio (es. "Calcio", "Nuoto")</li>
     * <li>Una descrizione opzionale</li>
     * </ul>
     * <p>
     * Il processo è iterativo, permettendo all'utente di aggiungere più valori in
     * sequenza.
     * Vengono effettuati controlli di validazione per ogni valore inserito:
     * <ul>
     * <li>Il nome non può essere vuoto</li>
     * <li>Il nome deve essere univoco nel dominio</li>
     * <li>È richiesto almeno un valore di dominio</li>
     * </ul>
     *
     * @param campo il campo a cui aggiungere il dominio di valori
     * 
     * @see model.Campo#addValore(ValoreCampo)
     * @see model.ValoreCampo
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
            String descrizione = this.view.getInput(String.format(ControllerConstants.PROMPT_DESCRIZIONE_VALORE, nomeValore));

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
                String desc = v.getDescrizione().isEmpty() ? ControllerConstants.NESSUNA_DESCRIZIONE : v.getDescrizione();
                view.showMessage(String.format(ControllerConstants.FORMATO_VALORE_DOMINIO, i + 1, v.getNome(), desc));
            }
        }
    }

    // -----------------------------------------------------------------------------------
    // METODI PER LA GESTIONE DEI FATTORI DI CONVERSIONE
    // -----------------------------------------------------------------------------------

    /**
     * Visualizza i fattori di conversione tra gerarchie e categorie.
     * Questo metodo guida l'utente attraverso un processo interattivo per
     * visualizzare
     * come le categorie di diverse gerarchie si relazionano tra loro in termini di
     * "valore"
     * del tempo dedicato alle attività.
     * <p>
     * Il processo di visualizzazione avviene in tre fasi:
     * <ol>
     * <li>Selezione della gerarchia di origine</li>
     * <li>Selezione della categoria foglia (attività concreta) dalla gerarchia di
     * origine</li>
     * <li>Selezione della gerarchia di destinazione per cui visualizzare i fattori
     * di conversione</li>
     * </ol>
     * <p>
     * Il metodo calcola e visualizza come il tempo dedicato all'attività
     * selezionata
     * viene convertito in tempo equivalente per tutte le attività della gerarchia
     * di destinazione,
     * considerando sia i fattori interni alla gerarchia (specializzazione) sia
     * quelli
     * tra gerarchie diverse.
     * 
     * @see #selezionaGerarchia()
     * @see #selezionaFoglia(Gerarchia)
     * @see model.FattoreConversione
     * @see model.ConversioneGerarchie
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
        this.view.showMessage(String.format(ControllerConstants.INFO_SELEZIONA_DESTINAZIONE, fogliaScelta.data().getNome()));
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
        this.view.showMessage(String.format(ControllerConstants.INFO_VISUALIZZA_FATTORI, fogliaScelta.data().getNome()));
        this.view.showMessage(
                gerarchiaDestinazione.toStringFattoreConversione(fattoreCombinato));
    }

    /**
     * Permette all'utente di selezionare una gerarchia tra quelle disponibili nel
     * sistema.
     * Il metodo mostra la lista di tutte le gerarchie disponibili, identificate dal
     * nome della
     * loro categoria radice, e chiede all'utente di selezionarne una tramite un
     * indice numerico.
     * <p>
     * Procedura di selezione:
     * <ol>
     * <li>Visualizza la lista numerata delle gerarchie disponibili</li>
     * <li>Richiede all'utente l'indice della gerarchia desiderata</li>
     * <li>Valida l'input e restituisce la gerarchia selezionata</li>
     * </ol>
     *
     * @return la gerarchia selezionata dall'utente
     * 
     * @see #richiediIndiceDaUtente(String, int)
     * @see model.Database#getGerarchia(int)
     * @see model.Gerarchia
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
     * Permette all'utente di selezionare una categoria foglia da una gerarchia
     * specificata.
     * Le foglie rappresentano le attività concrete che possono essere comparate
     * attraverso
     * i fattori di conversione.
     * <p>
     * Il metodo ottiene tutte le foglie della gerarchia e chiede all'utente di
     * selezionarne
     * una tramite un indice numerico. La visualizzazione delle foglie è già stata
     * effettuata
     * precedentemente tramite il metodo {@code toStringSelezioneFoglia()} della
     * gerarchia.
     *
     * @param gerarchia la gerarchia da cui selezionare la categoria foglia
     * @return il nodo foglia selezionato dall'utente
     * 
     * @see #richiediIndiceDaUtente(String, int)
     * @see model.Gerarchia#getFoglie()
     * @see model.Gerarchia#toStringSelezioneFoglia()
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
     * Richiede all'utente di inserire un indice numerico valido entro un intervallo
     * specificato.
     * Questo metodo si occupa di validare l'input garantendo che sia un numero
     * intero
     * compreso tra 1 e il numero massimo di scelte disponibili.
     * <p>
     * Il metodo è di supporto per tutte le funzionalità che richiedono una
     * selezione
     * numerica da parte dell'utente, come la selezione di gerarchie o categorie.
     * <p>
     * Il ciclo di validazione continua fino a quando non viene fornito un input
     * valido.
     *
     * @param messaggio il messaggio da mostrare all'utente per richiedere l'input
     * @param numScelte il numero massimo di scelte disponibili (limite superiore
     *                  dell'indice)
     * @return l'indice valido selezionato dall'utente (a partire da 1)
     * 
     * @see #selezionaGerarchia()
     * @see #selezionaFoglia(Gerarchia)
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
     * Crea i fattori di conversione tra una nuova gerarchia e tutte le gerarchie
     * esistenti.
     * Questo processo è essenziale per rendere confrontabili attività appartenenti
     * a gerarchie diverse.
     * <p>
     * Il metodo implementa un processo guidato in cui:
     * <ol>
     * <li>Vengono fornite spiegazioni all'utente sul concetto di conversione tra
     * gerarchie</li>
     * <li>Per ogni gerarchia esistente nel sistema, viene mostrato il percorso
     * dalla radice
     * alla foglia di riferimento per entrambe le gerarchie</li>
     * <li>L'utente definisce un fattore di conversione tra le foglie di riferimento
     * delle gerarchie</li>
     * <li>Il fattore viene salvato nel sistema di conversione globale</li>
     * </ol>
     * <p>
     * Per ogni coppia di gerarchie, viene creato un "ponte" tra le loro categorie
     * di riferimento,
     * che permette poi di convertire i tempi tra qualsiasi attività delle due
     * gerarchie.
     *
     * @param gerarchia la nuova gerarchia per cui creare i fattori di conversione
     *                  con le altre
     * 
     * @see #mostraPercorsoFino(TreeNode)
     * @see #richiediEValidaFattore(String, String, double, double, String)
     * @see model.ConversioneGerarchie#addFattoreConversione(Categoria, Categoria,
     *      double)
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
            view.showMessage(String.format(ControllerConstants.INFO_CONVERSIONE_CON, categoriaRadiceEsistente.getNome()));
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
     * Visualizza il percorso completo dalla radice fino al nodo specificato in una
     * gerarchia.
     * <p>
     * Questo metodo è utile per mostrare all'utente la posizione di una categoria
     * all'interno
     * della sua gerarchia, evidenziando tutti i livelli gerarchici attraversati per
     * raggiungerla.
     * <p>
     * La visualizzazione utilizza un'indentazione progressiva e simboli grafici per
     * rendere
     * più chiara la struttura gerarchica:
     * <ul>
     * <li>Il primo elemento (radice) è preceduto da un punto</li>
     * <li>Gli elementi successivi sono preceduti da una linea verticale e un
     * trattino</li>
     * <li>L'ultimo elemento (foglia) è contrassegnato con un asterisco</li>
     * </ul>
     *
     * @param nodo il nodo di cui visualizzare il percorso dalla radice
     * 
     * @see model.Categoria
     * @see com.scalified.tree.TreeNode
     * @see #creaConversioneGerarchie(Gerarchia)
     */
    private void mostraPercorsoFino(TreeNode<Categoria> nodo) {
        // Crea una lista per memorizzare il percorso dalla radice alla foglia
        List<Categoria> percorso = new ArrayList<>();
        TreeNode<Categoria> corrente = nodo;

        // Risale l'albero dal nodo specificato fino alla radice, raccogliendo tutte le
        // categorie
        while (corrente != null) {
            // addFirst per avere l'ordine corretto (dalla radice alla foglia)
            percorso.add(0, corrente.data());
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
     * Crea i fattori di conversione tra le categorie foglia all'interno di una
     * gerarchia.
     * Questo processo è essenziale per confrontare diverse attività concrete
     * all'interno
     * della stessa gerarchia, considerando il loro diverso livello di
     * specializzazione.
     * <p>
     * Il metodo implementa un approccio basato sul concetto di "foglia di
     * riferimento":
     * <ol>
     * <li>La prima foglia della gerarchia viene designata come "foglia di
     * riferimento"</li>
     * <li>Per ogni altra foglia, l'utente definisce un fattore di conversione
     * rispetto alla foglia di riferimento</li>
     * <li>I fattori dipendono dal livello di specializzazione relativa:
     * <ul>
     * <li>Foglie più specializzate (livello più profondo): fattore tra 1.0 e
     * 2.0</li>
     * <li>Foglie meno specializzate (livello meno profondo): fattore tra 0.5 e
     * 1.0</li>
     * </ul>
     * </li>
     * </ol>
     * <p>
     * Questi fattori di conversione interni permettono poi di calcolare rapporti
     * indiretti
     * tra qualsiasi coppia di foglie nella gerarchia.
     *
     * @param gerarchia la gerarchia per cui creare i fattori di conversione interni
     * 
     * @see #richiediEValidaFattore(String, String, double, double, String)
     * @see model.FattoreConversione#setNodoRiferimento(TreeNode)
     * @see model.FattoreConversione#addFattoreConversione(int, double)
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
            String messaggioErrore = isSpecializzata ? ControllerConstants.ERROR_FATTORE_SPECIALIZZATO : ControllerConstants.ERROR_FATTORE_NON_SPECIALIZZATO;

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
     * Richiede e valida un fattore di conversione dall'utente.
     * <p>
     * Questo metodo implementa un ciclo di validazione che continua finché l'utente
     * non fornisce un valore numerico valido all'interno dell'intervallo
     * specificato.
     * <p>
     * I fattori di conversione rappresentano la relazione tra due categorie in
     * termini
     * di "valore del tempo". Ad esempio, un fattore di 1.5 significa che un'ora
     * della
     * prima categoria equivale a 1.5 ore della seconda.
     *
     * @param categoriaOrigine      nome della categoria di origine per la
     *                              conversione
     * @param categoriaDestinazione nome della categoria di destinazione per la
     *                              conversione
     * @param minFattore            valore minimo accettabile per il fattore
     * @param maxFattore            valore massimo accettabile per il fattore
     * @param messaggioErrore       messaggio da mostrare in caso di valore fuori
     *                              range
     * @return il fattore di conversione validato
     * 
     * @see #creaFattoriConversione(Gerarchia)
     * @see #creaConversioneGerarchie(Gerarchia)
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
     * Questo metodo richiede ripetutamente l'inserimento di uno username finché
     * l'utente non inserisce uno username valido secondo i criteri di sicurezza:
     * <ul>
     * <li>Non vuoto</li>
     * <li>Senza spazi</li>
     * <li>Lunghezza massima limitata</li>
     * <li>Senza caratteri speciali non consentiti</li>
     * </ul>
     * <p>
     * La validazione effettiva viene delegata alla classe {@link util.UserUtil}.
     *
     * @return uno username valido, o null se il processo viene annullato
     * 
     * @see util.UserUtil#checkUsername(String)
     * @see util.UserUtil#USERNAME_LENGTH
     * @see #registerUtente()
     */
    private String getUsername() {
        while (true) {
            // Richiede uno username all'utente, specificando i requisiti di lunghezza
            String username = view.getInput(String.format(ControllerConstants.PROMPT_NEW_USERNAME, UserUtil.USERNAME_LENGTH));

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
     * Questo metodo richiede ripetutamente l'inserimento di una password finché
     * l'utente non inserisce una password valida secondo i criteri di sicurezza:
     * <ul>
     * <li>Lunghezza minima</li>
     * <li>Presenza di almeno una lettera maiuscola</li>
     * <li>Presenza di almeno una lettera minuscola</li>
     * <li>Presenza di almeno un carattere speciale</li>
     * </ul>
     * <p>
     * La validazione effettiva viene delegata alla classe {@link util.UserUtil}.
     *
     * @return una password valida, o null se il processo viene annullato
     * 
     * @see util.UserUtil#checkPassword(String)
     * @see util.UserUtil#PASSWORD_LENGTH
     * @see #registerUtente()
     */
    private String getPassword() {
        while (true) {
            // Richiede una password all'utente, specificando i requisiti minimi di
            // lunghezza
            // e complessità (maiuscole, minuscole, caratteri speciali)
            String password = view.getPasswordInput(String.format(ControllerConstants.PROMPT_NEW_PASSWORD, UserUtil.PASSWORD_LENGTH));

            // Verifica se la password soddisfa i criteri di sicurezza
            if (UserUtil.checkPassword(password))
                return password; // Password valida

            // Se la password non è valida, mostra un messaggio di errore
            // e richiede un nuovo tentativo
            view.showMessage(ControllerConstants.ERROR_INVALID_PASSWORD);
        }
    }

    /**
     * Ottiene una scelta booleana dall'utente (sì/no).
     * <p>
     * Questo metodo richiede ripetutamente l'inserimento di una risposta
     * finché l'utente non inserisce una risposta valida ('s' per sì o 'n' per no).
     * L'input è case-insensitive, quindi 'S' e 's' sono considerati equivalenti.
     * <p>
     * Utilizzato in vari contesti dell'applicazione per ottenere conferme
     * o scelte binarie dall'utente.
     *
     * @param messaggio il messaggio da mostrare all'utente per richiedere la scelta
     * @return true se l'utente risponde affermativo ('s'), false se risponde
     *         negativo ('n')
     * 
     * @see #registerUtente()
     * @see #addComuniToComprensorio(model.Comprensorio)
     * @see #createCategoria(String, Gerarchia)
     */
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
            view.showMessage(ControllerConstants.ERROR_INVALID_INPUT);
        }
    }
}