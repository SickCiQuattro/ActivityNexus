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
 * coordina le interazioni tra vista (ConsoleView) e modello (Database).
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
 * <p><b>Invarianti:</b>
 * <ul>
 *   <li>Il database non è mai null dopo l'inizializzazione del controller</li>
 *   <li>La view non è mai null durante l'intero ciclo di vita del controller</li>
 *   <li>Le operazioni di modifica dei dati sono sempre seguite da un tentativo di persistenza</li>
 *   <li>Le operazioni riservate ai Configuratori non sono mai eseguibili da un Fruitore</li>
 *   <li>Gli indici usati nelle selezioni sono sempre validati prima dell'utilizzo</li>
 * </ul>
 *
 * @version 1.0
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
     * @throws NullPointerException se view è null
     *
     * @pre view != null
     * @post this.database != null (inizializzato con un nuovo database vuoto)
     * @post this.view == view
     * @post il controller è pronto per gestire le interazioni utente
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
     *
     * @pre this.database != null
     * @pre this.view != null
     * @post Tutti i dati persistenti sono caricati all'avvio (tramite loadData())
     * @post Tutte le modifiche ai dati sono salvate prima della terminazione (tramite salvaTutti())
     * @post Se si verificano eccezioni durante l'esecuzione, vengono catturate e gestite senza terminare bruscamente
     * @post L'applicazione termina solo quando l'utente seleziona esplicitamente l'opzione di uscita
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
     * <p>
     * Tenta di caricare in sequenza:
     * <ol>
     * <li>Utenti registrati e loro credenziali</li>
     * <li>Comprensori geografici e loro comuni</li>
     * <li>Gerarchie di categorie con struttura completa</li>
     * <li>Fattori di conversione tra gerarchie diverse</li>
     * </ol>
     * <p>
     * Se i file non esistono o si verificano errori durante la lettura,
     * inizializza strutture vuote corrispondenti, garantendo che l'applicazione
     * possa comunque funzionare senza interruzioni.
     * <p>
     * Fornisce feedback all'utente durante l'intero processo di caricamento.
     *
     * @pre this.database != null
     * @pre this.view != null
     * @post I dati degli utenti sono caricati se il file corrispondente esiste e contiene dati validi
     * @post I comprensori sono caricati se il file corrispondente esiste e contiene dati validi
     * @post Le gerarchie sono caricate se il file corrispondente esiste e contiene dati validi
     * @post I fattori di conversione sono caricati se il file corrispondente esiste e contiene dati validi
     * @post In caso di errore durante il caricamento di un file, il corrispondente componente del database viene inizializzato vuoto
     * @post L'applicazione può procedere anche se alcuni o tutti i dati non sono stati caricati
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
     *
     * @pre path != null
     * @post restituisce true se e solo se il file esiste fisicamente e ha dimensione > 0
     * @post restituisce false se il file non esiste, non è accessibile o ha dimensione zero
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
     *
     * @pre this.database != null
     * @pre this.view != null
     * @post I dati utenti sono serializzati nel file specificato da USERS_FILE_PATH
     * @post In caso di errore durante il salvataggio, viene mostrato un messaggio appropriato all'utente
     * @post La struttura dati degli utenti nel database rimane invariata indipendentemente dal risultato dell'operazione
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
     * rispettivi file, garantendo che nessuna informazione venga persa quando
     * l'applicazione termina.
     *
     * @pre this.database != null
     * @pre this.view != null
     * @post Tutti i dati dell'applicazione sono serializzati nei rispettivi file:
     *       - Utenti in USERS_FILE_PATH
     *       - Comprensori in COMPRESORI_FILE_PATH
     *       - Gerarchie in GERARCHIE_FILE_PATH
     *       - Fattori di conversione in CONVERSIONE_FILE_PATH
     * @post L'utente riceve un messaggio di conferma al completamento del salvataggio
     * @post In caso di errore durante il salvataggio di qualsiasi componente, viene mostrato un messaggio appropriato
     * @post Il sistema tenta di salvare tutti i componenti anche se si verificano errori con componenti precedenti
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
     * Esegue la sequenza completa di autenticazione:
     * <ol>
     * <li>Verifica che esistano utenti registrati nel sistema</li>
     * <li>Richiede le credenziali di accesso (username e password)</li>
     * <li>Valida le credenziali contro il database</li>
     * <li>Reindirizza l'utente al menu appropriato in base al suo ruolo</li>
     * </ol>
     *
     * @pre this.database != null
     * @pre this.view != null
     * @post Se non ci sono utenti registrati, viene mostrato un messaggio informativo
     * @post Se l'autenticazione fallisce, viene mostrato un messaggio di errore
     * @post Se l'autenticazione ha successo per un Configuratore, viene mostrato il menu Configuratore
     * @post Se l'autenticazione ha successo per un Fruitore, viene mostrato il menu Fruitore
     * @post L'applicazione rimane nello stato autenticato finché l'utente non effettua il logout
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
     * <p>
     * Coordina l'intero flusso di registrazione che include:
     * <ol>
     * <li>Validazione preliminare tramite l'autenticazione temporanea</li>
     * <li>Raccolta e convalida delle credenziali (username e password)</li>
     * <li>Selezione del tipo di account (Configuratore o Fruitore)</li>
     * <li>Creazione dell'account nel database</li>
     * </ol>
     *
     * @return true se la registrazione è completata con successo, false altrimenti
     *
     * @pre this.database != null
     * @pre this.view != null
     * @post Se la registrazione ha successo, l'utente viene aggiunto al database
     * @post Se la registrazione ha successo, i dati utente vengono salvati su file (tramite salvaUtenti())
     * @post Viene mostrato un messaggio appropriato all'utente in base all'esito della registrazione
     */
    private boolean handleRegistration() {
        // Tenta la registrazione dell'utente
        boolean checkRegistration = registerUtente();

        // Mostra un messaggio di esito (successo o fallimento)
        view.showMessage(
                checkRegistration ? ControllerConstants.REGISTRATION_SUCCESS : ControllerConstants.REGISTRATION_FAILED);

        // Ritorna l'esito della registrazione per ulteriori elaborazioni
        return checkRegistration;
    }

    /**
     * Gestisce l'interfaccia e le funzionalità dell'utente configuratore.
     * <p>
     * Implementa un sottomenu dedicato alle operazioni amministrative, permettendo di:
     * <ul>
     * <li>Visualizzare e gestire i comprensori geografici</li>
     * <li>Creare e modificare gerarchie di categorie</li>
     * <li>Configurare fattori di conversione tra categorie</li>
     * <li>Tornare al menu principale (logout)</li>
     * </ul>
     * <p>
     * Il ciclo continua finché l'utente non sceglie di effettuare il logout.
     *
     * @pre this.database != null
     * @pre this.view != null
     * @pre È stato effettuato l'accesso come utente Configuratore
     * @post L'utente può eseguire solo operazioni di amministrazione
     * @post Ogni operazione completata è seguita da una pausa per consentire la lettura dei risultati
     * @post Il controllo ritorna al menu principale solo quando l'utente seleziona l'opzione di logout
     * @post Eventuali eccezioni durante le operazioni sono gestite senza interrompere il flusso di esecuzione
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
     * Mostra un elenco formattato di tutti i comprensori disponibili con le seguenti informazioni:
     * <ul>
     * <li>Nome del comprensorio</li>
     * <li>Elenco dei comuni appartenenti</li>
     * </ul>
     *
     * @pre this.database != null
     * @pre this.view != null
     * @post Se non esistono comprensori, viene mostrato un messaggio informativo
     * @post Se esistono comprensori, viene visualizzato l'elenco completo in formato leggibile
     * @post Lo stato del sistema rimane invariato (operazione di sola lettura)
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
     * Presenta una rappresentazione strutturata delle gerarchie, mostrando:
     * <ul>
     * <li>Relazioni gerarchiche tra categorie (struttura ad albero)</li>
     * <li>Campi caratteristici di ciascuna categoria</li>
     * <li>Valori di dominio per i campi caratteristici</li>
     * </ul>
     *
     * @pre this.database != null
     * @pre this.view != null
     * @post Se non esistono gerarchie, viene mostrato un messaggio informativo
     * @post Se esistono gerarchie, viene visualizzata la struttura completa in formato leggibile
     * @post Lo stato del sistema rimane invariato (operazione di sola lettura)
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
     * appropriato in caso negativo. Utilizzato come precondizione per operazioni
     * che necessitano di almeno una gerarchia esistente.
     *
     * @return true se esistono gerarchie nel database, false altrimenti
     *
     * @pre this.database != null
     * @pre this.view != null
     * @post Se non esistono gerarchie, viene mostrato un messaggio specifico di errore
     * @post Il valore restituito è true se e solo se esiste almeno una gerarchia nel database
     * @post Lo stato del sistema rimane invariato (operazione di sola lettura)
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
     * Guida l'utente attraverso un flusso interattivo che include:
     * <ol>
     * <li>Presentazione delle informazioni introduttive sul processo</li>
     * <li>Raccolta del nome e dei comuni appartenenti al comprensorio</li>
     * <li>Validazione dei dati inseriti</li>
     * <li>Salvataggio del nuovo comprensorio nel database</li>
     * <li>Persistenza dei dati su file</li>
     * </ol>
     *
     * @pre this.database != null
     * @pre this.view != null
     * @post Se la creazione ha successo, un nuovo comprensorio viene aggiunto al database
     * @post Se la creazione ha successo, i dati vengono salvati su file (tramite serializeComprensori())
     * @post Se la creazione ha successo, viene mostrato un messaggio di conferma
     * @post Se la creazione è annullata o fallisce, viene mostrato un messaggio appropriato
     * @post L'operazione non modifica i comprensori esistenti
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
     * Orchestrazione completa del flusso di creazione che include:
     * <ol>
     * <li>Strutturazione della gerarchia con categorie e relazioni padre-figlio</li>
     * <li>Definizione di campi caratteristici e domini di valori</li>
     * <li>Configurazione dei fattori di conversione interni alla gerarchia</li>
     * <li>Configurazione dei fattori di conversione con altre gerarchie esistenti</li>
     * <li>Salvataggio della gerarchia completa nel database</li>
     * <li>Persistenza dei dati su file</li>
     * </ol>
     *
     * @pre this.database != null
     * @pre this.view != null
     * @post Una nuova gerarchia completa viene aggiunta al database
     * @post I dati delle gerarchie vengono salvati su file (tramite serializeGerarchie())
     * @post I fattori di conversione vengono salvati su file (tramite serializeConversioneGerarchie())
     * @post Se esistono altre gerarchie, vengono configurati fattori di conversione tra la nuova e le esistenti
     * @post Viene mostrato un messaggio di conferma al completamento dell'operazione
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
     * Autentica un utente nel sistema.
     * <p>
     * Raccoglie le credenziali (username e password) dall'utente e le verifica
     * confrontandole con i dati memorizzati nel database. Gestisce l'intera
     * procedura di autenticazione, dall'acquisizione dei dati alla verifica.
     *
     * @return l'oggetto Utente se l'autenticazione ha successo, null altrimenti
     *
     * @pre this.database != null
     * @pre this.view != null
     * @post Se le credenziali sono valide, restituisce l'oggetto Utente corrispondente
     * @post Se le credenziali sono invalide o l'utente non esiste, restituisce null
     * @post Lo stato dell'applicazione non viene modificato (operazione di sola lettura)
     * @post Il risultato può essere utilizzato per determinare se concedere l'accesso alle funzionalità protette
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
     * <p>
     * Coordina l'intero processo di registrazione utente che include:
     * <ul>
     * <li>Raccolta e validazione dello username</li>
     * <li>Raccolta e validazione della password</li>
     * <li>Determinazione del tipo di account (Configuratore o Fruitore)</li>
     * <li>Creazione dell'utente nel database</li>
     * <li>Persistenza dei dati utente su file</li>
     * </ul>
     *
     * @return true se la registrazione è completata con successo, false altrimenti
     *
     * @pre this.database != null
     * @pre this.view != null
     * @post Se la registrazione ha successo, un nuovo utente è aggiunto al database
     * @post Se la registrazione ha successo, i dati utente sono salvati su file (tramite salvaUtenti())
     * @post Se la registrazione fallisce, il database rimane invariato
     * @post Se lo username scelto è già in uso, la registrazione fallisce
     * @post Se l'utente annulla il processo, la registrazione fallisce senza modificare il database
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
     * Crea un utente predefinito nel sistema quando non esistono utenti registrati.
     * <p>
     * Utilizzato per garantire l'accesso iniziale al sistema con credenziali standard.
     * Questo metodo viene tipicamente invocato durante il primo avvio dell'applicazione
     * o quando il database utenti è vuoto.
     *
     * @return true se la creazione dell'utente predefinito ha successo, false altrimenti
     *
     * @pre this.database != null
     * @pre this.view != null
     * @post Se la creazione ha successo, un nuovo utente Configuratore con credenziali predefinite è aggiunto al database
     * @post Se la creazione ha successo, i dati utente sono salvati su file (tramite salvaUtenti())
     * @post Se la creazione fallisce, il database rimane vuoto
     * @post Il metodo mostrerà all'utente le credenziali predefinite create
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
     * Gestisce l'intero processo di creazione interattiva che include:
     * <ol>
     * <li>Acquisizione e validazione del nome del comprensorio</li>
     * <li>Verifica dell'unicità del nome tra i comprensori esistenti</li>
     * <li>Raccolta dei comuni appartenenti tramite processo guidato</li>
     * <li>Aggiunta del comprensorio completo al database</li>
     * </ol>
     *
     * @return true se il comprensorio è stato creato e aggiunto con successo, false altrimenti
     *
     * @pre this.database != null
     * @pre this.view != null
     * @post Se restituisce true, un nuovo comprensorio è stato aggiunto al database
     * @post Se l'utente annulla l'operazione (inserendo "0"), restituisce false
     * @post Se il nome scelto è già in uso, l'utente viene guidato a scegliere un altro nome
     * @post Se il comprensorio non contiene almeno un comune, la creazione fallisce
     * @post I comprensori esistenti non vengono modificati durante il processo
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
     * Aggiunge comuni a un comprensorio esistente attraverso un'interfaccia interattiva.
     * <p>
     * Guida l'utente nel processo di aggiunta comuni che include:
     * <ol>
     * <li>Richiesta iterativa del nome di ciascun comune</li>
     * <li>Validazione del nome del comune (non vuoto, lunghezza minima)</li>
     * <li>Verifica dell'unicità del comune all'interno del comprensorio</li>
     * <li>Possibilità di terminare l'inserimento in qualsiasi momento</li>
     * </ol>
     *
     * @param comprensorio il comprensorio a cui aggiungere i comuni
     *
     * @pre comprensorio != null
     * @pre this.view != null
     * @post Il comprensorio contiene tutti i comuni validi inseriti dall'utente
     * @post Ogni comune aggiunto rispetta i criteri di validità (non vuoto, lunghezza minima)
     * @post L'utente può terminare l'inserimento in qualsiasi momento inserendo "0"
     * @post Se un comune esiste già nel comprensorio, viene mostrato un messaggio di errore senza duplicazione
     * @post Il processo continua fino a quando l'utente non sceglie di terminare l'inserimento
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

    // -----------------------------------------------------------------------------------
    // METODI PER LA GESTIONE DELLE GERARCHIE
    // -----------------------------------------------------------------------------------

    /**
     * Crea una nuova gerarchia di categorie completa.
     * <p>
     * Implementa un processo interattivo di costruzione di una gerarchia che include:
     * <ol>
     * <li>Definizione del nome e descrizione della gerarchia</li>
     * <li>Creazione delle categorie con le relative relazioni gerarchiche</li>
     * <li>Configurazione dei campi caratteristici per ogni categoria</li>
     * <li>Generazione dei valori di dominio per ciascun campo</li>
     * </ol>
     * <p>
     * Utilizza un approccio guidato per supportare l'utente nella creazione
     * della struttura completa della gerarchia.
     *
     * @return la gerarchia completa creata
     *
     * @pre this.database != null
     * @pre this.view != null
     * @post La gerarchia creata ha un nome valido e univoco
     * @post La gerarchia contiene almeno una categoria
     * @post Ogni categoria ha almeno un campo caratteristico
     * @post Ogni campo caratteristico ha almeno un valore di dominio valido
     * @post Le relazioni gerarchiche tra le categorie formano una struttura ad albero coerente
     * @post La gerarchia è pronta per essere aggiunta al database dell'applicazione
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
     * Crea una nuova categoria e la aggiunge alla gerarchia specificata.
     * <p>
     * Guida l'utente nel processo di creazione di una categoria che include:
     * <ol>
     * <li>Acquisizione e validazione del nome della categoria</li>
     * <li>Definizione di una descrizione opzionale</li>
     * <li>Posizionamento corretto nella gerarchia (sotto il padre specificato)</li>
     * <li>Aggiunta di campi caratteristici e relativi valori di dominio</li>
     * </ol>
     * <p>
     * Il metodo crea e configura completamente la categoria prima di restituirla.
     *
     * @param nomePadre nome della categoria padre sotto cui posizionare la nuova categoria
     * @param root gerarchia a cui aggiungere la categoria
     * @return il nodo dell'albero contenente la categoria creata
     *
     * @pre nomePadre != null
     * @pre root != null
     * @pre this.view != null
     * @post Il nodo restituito contiene una categoria valida e completamente configurata
     * @post La categoria creata è correttamente posizionata nella struttura gerarchica
     * @post La categoria ha un nome univoco all'interno del suo livello nella gerarchia
     * @post La categoria ha almeno un campo caratteristico con valori di dominio definiti
     * @post Se l'utente annulla l'operazione, restituisce null
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
     * Crea un nuovo campo caratteristico per una categoria.
     * <p>
     * Implementa un'interfaccia interattiva per la definizione completa di un campo, includendo:
     * <ol>
     * <li>Acquisizione e validazione del nome del campo</li>
     * <li>Definizione dei valori di dominio ammissibili</li>
     * <li>Validazione di ciascun valore inserito</li>
     * <li>Verifica che il dominio contenga almeno un valore</li>
     * </ol>
     * <p>
     * Ogni campo creato include un insieme non vuoto di valori possibili che rappresentano
     * il dominio del campo stesso.
     *
     * @return il campo caratteristico creato e configurato, o null se l'utente annulla l'operazione
     *
     * @pre this.view != null
     * @post Il campo creato ha un nome valido e non vuoto
     * @post Il campo ha almeno un valore di dominio definito
     * @post Ogni valore nel dominio ha un nome valido (non vuoto, lunghezza minima)
     * @post Se l'utente annulla l'operazione, viene restituito null
     * @post I valori nel dominio sono univoci (non ci sono duplicati)
     * @post L'utente riceve feedback appropriati durante il processo di creazione
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
     * Guida l'utente attraverso un processo interattivo per definire l'insieme di valori
     * validi (dominio) che un campo può assumere. Il processo include:
     * <ol>
     * <li>Acquisizione e validazione di ogni valore inserito</li>
     * <li>Verifica dell'unicità all'interno del dominio</li>
     * <li>Possibilità di aggiungere descrizioni opzionali ai valori</li>
     * <li>Garanzia che il campo abbia almeno un valore nel dominio</li>
     * </ol>
     * <p>
     * Il metodo utilizza un approccio iterativo, permettendo all'utente di aggiungere
     * valori finché non decide di terminare l'inserimento.
     *
     * @param campo il campo caratteristico per cui definire il dominio di valori
     *
     * @pre campo != null
     * @pre this.view != null
     * @post Il campo ha almeno un valore definito nel suo dominio
     * @post Ogni valore nel dominio è valido (non vuoto, rispetta la lunghezza minima)
     * @post Non ci sono valori duplicati nel dominio (ogni valore ha un nome univoco)
     * @post L'utente riceve feedback appropriati durante il processo di inserimento
     * @post Se l'utente tenta di terminare senza inserire valori, riceve un avviso
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
     * Permette all'utente di selezionare una gerarchia tra quelle disponibili nel sistema.
     * <p>
     * Visualizza un elenco numerato delle gerarchie esistenti e acquisisce la scelta
     * dell'utente tramite input numerico. La numerazione presentata all'utente è 1-based
     * per una migliore comprensione da parte dell'utente finale.
     *
     * @return la gerarchia selezionata dall'utente
     *
     * @pre this.database != null
     * @pre this.view != null
     * @pre this.database.getGerarchie() non è vuoto
     * @post La gerarchia restituita è una delle gerarchie esistenti nel database
     * @post L'utente ha potuto visualizzare l'elenco completo delle gerarchie disponibili
     * @post L'indice selezionato dall'utente è stato validato prima di accedere alla gerarchia corrispondente
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
     * Permette all'utente di selezionare una categoria foglia da una gerarchia specificata.
     * <p>
     * Utilizza la visualizzazione numerata delle foglie generata dalla gerarchia e
     * acquisisce la scelta dell'utente. Le foglie rappresentano categorie terminali
     * che non hanno ulteriori sottocategorie.
     *
     * @param gerarchia la gerarchia da cui selezionare una foglia
     * @return il nodo dell'albero contenente la categoria foglia selezionata
     *
     * @pre gerarchia != null
     * @pre gerarchia.getFoglie() non è vuoto
     * @pre this.view != null
     * @post Il nodo restituito appartiene all'insieme delle foglie della gerarchia specificata
     * @post L'indice selezionato dall'utente è stato validato prima di accedere alla foglia corrispondente
     * @post L'utente ha potuto visualizzare la struttura gerarchica con le foglie evidenziate
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
     * Richiede all'utente di selezionare un'opzione numerica da un intervallo definito.
     * <p>
     * Implementa un ciclo di validazione che garantisce che l'input fornito sia:
     * <ul>
     * <li>Un numero intero valido</li>
     * <li>Compreso tra 1 e il numero massimo di opzioni disponibili</li>
     * </ul>
     * <p>
     * Il metodo mostra un messaggio personalizzato e continua a richiedere input
     * finché non viene fornito un valore valido.
     *
     * @param messaggio il messaggio da mostrare all'utente durante la richiesta
     * @param numScelte il numero massimo di scelte disponibili (limite superiore dell'intervallo)
     * @return l'indice numerico selezionato dall'utente (1-based)
     *
     * @pre messaggio != null
     * @pre numScelte > 0
     * @pre this.view != null
     * @post Il valore restituito è un intero compreso tra 1 e numScelte (inclusi)
     * @post L'utente ha ricevuto feedback appropriati in caso di input non valido
     * @post Il metodo ha gestito correttamente eventuali errori di formato nell'input
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
     * Crea i fattori di conversione tra una nuova gerarchia e tutte quelle esistenti.
     * <p>
     * Guida l'utente attraverso il processo di definizione delle relazioni quantitative
     * tra la nuova gerarchia e ciascuna di quelle già presenti nel sistema. Questi
     * fattori permettono la conversione di valori tra domini concettuali diversi ma
     * correlati.
     * <p>
     * Il processo richiede l'inserimento di un valore numerico positivo che definisce
     * il rapporto di conversione tra i nodi di riferimento delle due gerarchie.
     *
     * @param gerarchia la nuova gerarchia da collegare alle gerarchie esistenti
     *
     * @pre gerarchia != null
     * @pre this.database != null
     * @pre this.view != null
     * @pre Il database contiene almeno una gerarchia oltre a quella specificata
     * @post Per ogni gerarchia esistente, viene definito un fattore di conversione con la nuova gerarchia
     * @post Ogni fattore di conversione è un numero positivo maggiore di zero
     * @post La consistenza matematica delle conversioni inter-gerarchiche è garantita
     * @post Le relazioni di conversione sono bidirezionali (da A a B e da B ad A)
     * @post L'utente riceve feedback appropriati durante il processo di inserimento
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
     * Visualizza il percorso completo dalla radice a un nodo specifico nella gerarchia.
     * <p>
     * Costruisce e mostra la catena gerarchica di categorie che collega la categoria radice
     * alla categoria specificata, con formattazione grafica che include:
     * <ul>
     * <li>Indentazione progressiva che rappresenta la profondità del nodo</li>
     * <li>Indicatori visuali della relazione genitore-figlio</li>
     * <li>Evidenziazione dei dettagli di ogni categoria nel percorso</li>
     * </ul>
     * <p>
     * Il percorso viene costruito risalendo l'albero dalla foglia alla radice e poi
     * visualizzato in ordine dall'alto verso il basso.
     *
     * @param nodo il nodo di cui visualizzare il percorso completo nella gerarchia
     *
     * @pre nodo != null
     * @pre this.view != null
     * @post L'utente visualizza il percorso completo dalla radice al nodo specificato
     * @post La visualizzazione include tutte le categorie nel percorso, correttamente ordinate
     * @post La formattazione grafica riflette accuratamente la struttura gerarchica
     * @post Lo stato del sistema rimane invariato (operazione di sola lettura)
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
     * Crea i fattori di conversione tra categorie foglia di una stessa gerarchia.
     * <p>
     * Implementa un processo interattivo per definire relazioni quantitative tra le varie
     * attività (categorie foglia) di una stessa gerarchia, permettendo:
     * <ol>
     * <li>Selezione automatica di una foglia di riferimento (base di conversione)</li>
     * <li>Definizione guidata dei fattori relativi per tutte le altre foglie</li>
     * <li>Costruzione di un sistema coerente di conversioni intra-gerarchiche</li>
     * </ol>
     * <p>
     * I fattori definiti rappresentano la relazione quantitativa tra attività dello stesso
     * dominio concettuale ma con diversi livelli di specializzazione.
     *
     * @param gerarchia la gerarchia su cui definire i fattori di conversione interni
     *
     * @pre gerarchia != null
     * @pre gerarchia.getFogliePerLivello() non è vuoto
     * @pre this.view != null
     * @post La gerarchia contiene un nodo di riferimento definito come base di conversione
     * @post Ogni foglia della gerarchia ha un fattore di conversione definito rispetto al riferimento
     * @post I fattori di conversione sono maggiori di zero
     * @post L'utente riceve feedback appropriato durante il processo di definizione
     * @post Il sistema di conversioni interno alla gerarchia è matematicamente coerente
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
     * Implementa un ciclo di validazione che garantisce che il valore inserito:
     * <ul>
     * <li>Sia un numero decimale valido</li>
     * <li>Rientri nell'intervallo specificato dai parametri</li>
     * <li>Rappresenti correttamente la relazione tra le categorie</li>
     * </ul>
     * <p>
     * Il metodo guida l'utente fornendo contesto sulle categorie coinvolte e
     * continua a richiedere input finché non viene fornito un valore accettabile.
     *
     * @param categoriaOrigine      nome della categoria di origine per la conversione
     * @param categoriaDestinazione nome della categoria di destinazione per la conversione
     * @param minFattore            valore minimo accettabile per il fattore (incluso)
     * @param maxFattore            valore massimo accettabile per il fattore (incluso)
     * @param messaggioErrore       messaggio da mostrare in caso di input non valido
     * @return il fattore di conversione validato come numero decimale
     *
     * @pre categoriaOrigine != null
     * @pre categoriaDestinazione != null
     * @pre minFattore > 0
     * @pre maxFattore >= minFattore
     * @pre messaggioErrore != null
     * @pre this.view != null
     * @post Il valore restituito è un numero decimale > 0
     * @post Il valore restituito è compreso tra minFattore e maxFattore (inclusi)
     * @post L'utente ha ricevuto feedback appropriato in caso di input non valido
     * @post Il metodo ha gestito correttamente eventuali errori di formato nell'input
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
     * Guida l'utente attraverso il processo di selezione di uno username valido,
     * verificando che rispetti i requisiti del sistema (lunghezza minima, caratteri ammessi)
     * e che non sia già in uso da un altro utente registrato.
     *
     * @return lo username validato, o null se l'utente annulla l'operazione
     *
     * @pre this.database != null
     * @pre this.view != null
     * @post Se restituisce un valore non null, questo rappresenta uno username valido e disponibile
     * @post Se restituisce null, l'utente ha scelto di annullare l'operazione
     * @post Lo username restituito non è già associato a un utente esistente
     * @post Lo username restituito soddisfa tutti i requisiti di formattazione definiti nel sistema
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
     * Guida l'utente nella creazione di una password sicura, verificando che soddisfi
     * i requisiti minimi di sicurezza definiti dal sistema (lunghezza, complessità, ecc.).
     * Fornisce feedback appropriati in caso di password non conforme.
     *
     * @return la password validata, o null se l'utente annulla l'operazione
     *
     * @pre this.view != null
     * @post Se restituisce un valore non null, questo rappresenta una password che soddisfa i requisiti di sicurezza
     * @post Se restituisce null, l'utente ha scelto di annullare l'operazione
     * @post La password restituita soddisfa tutti i requisiti di sicurezza definiti nel sistema
     * @post La password non viene memorizzata in chiaro ma processata attraverso funzioni di hash appropriate
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
     * Acquisisce una scelta binaria (sì/no) dall'utente.
     * <p>
     * Richiede all'utente di inserire 's' per sì o 'n' per no,
     * validando l'input fino a ottenere una risposta valida.
     * Utilizzato per decisioni semplici durante i processi interattivi.
     *
     * @param messaggio il messaggio da mostrare all'utente per la richiesta
     * @return true se l'utente risponde affermativamente ('s'), false altrimenti ('n')
     *
     * @pre this.view != null
     * @pre messaggio != null
     * @post Il valore restituito corrisponde esattamente alla scelta dell'utente: true per 's', false per 'n'
     * @post Il metodo termina solo quando l'utente ha fornito una risposta valida ('s' o 'n')
     * @post Durante il processo vengono mostrati messaggi appropriati per guidare l'utente verso un input valido
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
            view.showMessage(ControllerConstants.ERROR_INVALID_INPUT);
        }
    }
}