package controller;

import com.scalified.tree.TreeNode;
import constants.ControllerConstants;
import util.UserUtil;
import model.*;
import view.ConsoleView;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

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
 * <p>
 * <b>Invarianti:</b>
 * <ul>
 * <li>Il database non è mai null dopo l'inizializzazione del controller</li>
 * <li>La view non è mai null durante l'intero ciclo di vita del controller</li>
 * <li>Le operazioni di modifica dei dati sono sempre seguite da un tentativo di
 * persistenza</li>
 * <li>Le operazioni riservate ai Configuratori non sono mai eseguibili da un
 * Fruitore</li>
 * <li>Gli indici usati nelle selezioni sono sempre validati prima
 * dell'utilizzo</li>
 * </ul>
 *
 * @version 4.0
 * @see model.Database
 * @see view.ConsoleView
 */
public class Controller {
    // TODO: Applicare il Pattern "Separation of Concerns" per dividere le
    // funzionalità
    // da monilitico a moduli più piccoli e coesi

    private Database database;
    private final ConsoleView view;

    // ==============================================================================
    // 1. METODI CORE E INIZIALIZZAZIONE
    // ==============================================================================

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
     * @post Tutte le modifiche ai dati sono salvate prima della terminazione
     *       (tramite salvaTutti())
     * @post Se si verificano eccezioni durante l'esecuzione, vengono catturate e
     *       gestite senza terminare bruscamente
     * @post L'applicazione termina solo quando l'utente seleziona esplicitamente
     *       l'opzione di uscita
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
     * @post I dati degli utenti sono caricati se il file corrispondente esiste e
     *       contiene dati validi
     * @post I comprensori sono caricati se il file corrispondente esiste e contiene
     *       dati validi
     * @post Le gerarchie sono caricate se il file corrispondente esiste e contiene
     *       dati validi
     * @post I fattori di conversione sono caricati se il file corrispondente esiste
     *       e contiene dati validi
     * @post In caso di errore durante il caricamento di un file, il corrispondente
     *       componente del database viene inizializzato vuoto
     * @post L'applicazione può procedere anche se alcuni o tutti i dati non sono
     *       stati caricati
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

            // Carica i dati proposte se il file esiste
            if (fileEsistente(ControllerConstants.PROPOSTE_FILE_PATH)) {
                this.database.deserializeProposte(ControllerConstants.PROPOSTE_FILE_PATH);
                view.showMessage(String.format(ControllerConstants.LOADING_ITEM_SUCCESS, "proposte"));
            } else {
                view.showMessage(String.format(ControllerConstants.LOADING_ITEM_EMPTY, "proposta"));
            }

            // Carica i dati insieme chiuso se il file esiste
            if (fileEsistente(ControllerConstants.INSIEMECHIUSO_FILE_PATH)) {
                this.database.deserializeInsiemiChiusi(ControllerConstants.INSIEMECHIUSO_FILE_PATH);
                view.showMessage(String.format(ControllerConstants.LOADING_ITEM_SUCCESS, "insieme chiuso"));
            } else {
                // Altrimenti, indica che si parte da zero per questa sezione
                view.showMessage(String.format(ControllerConstants.LOADING_ITEM_EMPTY, "dato insieme chiuso"));
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
     * @post restituisce true se e solo se il file esiste fisicamente e ha
     *       dimensione > 0
     * @post restituisce false se il file non esiste, non è accessibile o ha
     *       dimensione zero
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
     * @post In caso di errore durante il salvataggio, viene mostrato un messaggio
     *       appropriato all'utente
     * @post La struttura dati degli utenti nel database rimane invariata
     *       indipendentemente dal risultato dell'operazione
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
     * @post L'utente riceve un messaggio di conferma al completamento del
     *       salvataggio
     * @post In caso di errore durante il salvataggio di qualsiasi componente, viene
     *       mostrato un messaggio appropriato
     * @post Il sistema tenta di salvare tutti i componenti anche se si verificano
     *       errori con componenti precedenti
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
            this.database.serializeProposte(ControllerConstants.PROPOSTE_FILE_PATH);
            this.database.serializeInsiemiChiusi(ControllerConstants.INSIEMECHIUSO_FILE_PATH);

            // Conferma che il salvataggio è avvenuto con successo
            view.showSuccessMessage(ControllerConstants.SAVING_SUCCESS);
        } catch (Exception e) {
            // Gestisce eventuali errori durante il salvataggio
            view.showErrorMessage(String.format(ControllerConstants.SAVING_ERROR, e.getMessage()));
        }
    }

    // ==============================================================================
    // 2. GESTIONE MENU E SESSIONE
    // ==============================================================================

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
     * @post Se non ci sono utenti registrati, viene mostrato un messaggio
     *       informativo
     * @post Se l'autenticazione fallisce, viene mostrato un messaggio di errore
     * @post Se l'autenticazione ha successo per un Configuratore, viene mostrato il
     *       menu Configuratore
     * @post Se l'autenticazione ha successo per un Fruitore, viene mostrato il menu
     *       Fruitore
     * @post L'applicazione rimane nello stato autenticato finché l'utente non
     *       effettua il logout
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
            Fruitore fruitore = (Fruitore) utente;
            // Altrimenti è un fruitore (funzionalità future)
            manageFruitore(fruitore);
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
     * @pre this.database != null
     * @pre this.view != null
     * @post Se la registrazione ha successo, l'utente viene aggiunto al database
     * @post Se la registrazione ha successo, i dati utente vengono salvati su file
     *       (tramite salvaUtenti())
     * @post Viene mostrato un messaggio appropriato all'utente in base all'esito
     *       della registrazione
     */
    private void handleRegistration() {
        // Presentazione delle opzioni di registrazione
        view.showMessage(ControllerConstants.TITLE_REGISTRATION);

        // Determinazione del tipo di account (Configuratore o Fruitore)
        boolean isConfiguratore = this.view.getConfirmation(ControllerConstants.PROMPT_CONFIG_ROLE);
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
     * Implementa un sottomenu dedicato alle operazioni amministrative, permettendo
     * di:
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
     * @post Ogni operazione completata è seguita da una pausa per consentire la
     *       lettura dei risultati
     * @post Il controllo ritorna al menu principale solo quando l'utente seleziona
     *       l'opzione di logout
     * @post Eventuali eccezioni durante le operazioni sono gestite senza
     *       interrompere il flusso di esecuzione
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

                    case "6": // Visualizza proposte di scambio
                        visualizzaProposteTotali();
                        break;

                    case "7": // Visualizza insiemi chiusi
                        visualizzaInsiemiChiusi();
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
     * Gestisce l'interfaccia e le funzionalità dell'utente fruitore.
     * <p>
     * Implementa un sottomenu dedicato alle operazioni del fruitore, permettendo
     * di:
     * <ul>
     * <li>Visualizzare le gerarchie di categorie disponibili</li>
     * <li>Creare proposte di scambio d'opera</li>
     * <li>Visualizzare le proprie proposte attive</li>
     * <li>Modificare lo stato delle proprie proposte (ritirare)</li>
     * <li>Tornare al menu principale (logout)</li>
     * </ul>
     * <p>
     * Il ciclo continua finché l'utente non sceglie di effettuare il logout.
     *
     * @param fruitore l'utente fruitore che sta utilizzando il sistema
     * 
     * @pre fruitore != null
     * @pre this.database != null
     * @pre this.view != null
     * @post L'utente può eseguire solo operazioni consentite al ruolo fruitore
     * @post Ogni operazione completata è seguita da una pausa per consentire la
     *       lettura dei risultati
     * @post Il controllo ritorna al menu principale solo quando l'utente seleziona
     *       l'opzione di logout
     * @post Eventuali proposte di scambio create vengono salvate nel database
     * @post Eventuali modifiche allo stato delle proposte sono persistenti
     * @post Eventuali eccezioni durante le operazioni sono gestite senza
     *       interrompere il flusso di esecuzione
     */
    private void manageFruitore(Fruitore fruitore) {
        // Ciclo principale del menu fruitore
        while (true) {
            try {
                // Mostra il menu fruitore e ottiene la scelta dell'utente
                view.showMenuFruitore();
                String choice = view.getInput(ControllerConstants.PROMPT_CHOICE);

                // Elabora la scelta dell'utente
                switch (choice) {
                    case "1": // Visualizza gerarchie
                        Gerarchia gerarchiaScelta = selezionaGerarchia();
                        // visualizzare
                        this.view.showMessage(gerarchiaScelta.toString());
                        break;

                    case "2": // Proposta scambio d'opera
                        if (propostaScambioOpera(fruitore))
                            valutaProposteScambio(fruitore);

                        // Salvataggio delle proposte
                        this.database.serializeProposte(ControllerConstants.PROPOSTE_FILE_PATH);
                        break;

                    case "3": // Visualizza proposte di scambio
                        visualizzaProposteFruitore(fruitore);
                        break;

                    case "4":
                        modificaStatoProposta(fruitore);
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

    // ==============================================================================
    // 3. AUTENTICAZIONE E REGISTRAZIONE UTENTI
    // ==============================================================================

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
     * @post Se le credenziali sono valide, restituisce l'oggetto Utente
     *       corrispondente
     * @post Se le credenziali sono invalide o l'utente non esiste, restituisce null
     * @post Lo stato dell'applicazione non viene modificato (operazione di sola
     *       lettura)
     * @post Il risultato può essere utilizzato per determinare se concedere
     *       l'accesso alle funzionalità protette
     */
    public Utente loginUtente() {
        // Richiesta dello username all'utente
        String username = view.getInput(ControllerConstants.PROMPT_USERNAME);

        // Richiesta della password all'utente
        String password = view.getPasswordInput(ControllerConstants.PROMPT_PASSWORD);

        // Verifica delle credenziali attraverso il database
        // Ritorna l'utente se valido, null altrimenti
        return database.loginUtente(username, password);
    }

    /**
     * Registra un nuovo utente configuratore nel sistema.
     * <p>
     * Il processo include:
     * <ol>
     * <li>Verifica di sicurezza preliminare attraverso credenziali temporanee</li>
     * <li>Acquisizione e validazione di uno username univoco</li>
     * <li>Definizione di una password sicura</li>
     * <li>Creazione dell'account configuratore nel database</li>
     * </ol>
     * <p>
     * I configuratori hanno accesso a funzionalità amministrative avanzate come la
     * gestione
     * di comprensori, gerarchie e monitoraggio degli scambi.
     *
     * @return true se la registrazione è completata con successo, false altrimenti
     *
     * @pre this.database != null
     * @pre this.view != null
     * @post Se restituisce true, un nuovo utente configuratore è stato aggiunto al
     *       database
     * @post Se restituisce false, nessun utente è stato aggiunto e lo stato del
     *       sistema rimane invariato
     * @post Se la registrazione ha successo, l'utente configuratore ha privilegi
     *       amministrativi
     * @post Se la verifica di sicurezza fallisce, la registrazione viene interrotta
     * @post Se lo username scelto è già in uso, la registrazione fallisce
     * @post L'utente riceve feedback appropriato in caso di errori durante il
     *       processo
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
     * Guida l'utente attraverso il processo completo di registrazione che include:
     * <ol>
     * <li>Selezione del comprensorio geografico di appartenenza</li>
     * <li>Acquisizione e validazione di uno username univoco</li>
     * <li>Definizione di una password sicura</li>
     * <li>Inserimento e validazione di un indirizzo email</li>
     * <li>Creazione dell'account fruitore nel database</li>
     * </ol>
     * <p>
     * Il processo include verifiche di unicità, validità dei format e controlli
     * di sicurezza per garantire l'integrità dei dati utente.
     *
     * @return true se la registrazione è completata con successo, false altrimenti
     *
     * @pre this.database != null
     * @pre this.view != null
     * @pre database.getComprensori() non è vuoto (verificato prima della chiamata)
     * @post Se restituisce true, un nuovo fruitore è stato aggiunto al database
     * @post Se restituisce false, nessun fruitore è stato aggiunto e lo stato del
     *       sistema rimane invariato
     * @post Se la registrazione ha successo, il fruitore è associato al
     *       comprensorio selezionato
     * @post Se la registrazione ha successo, le credenziali e i dati dell'utente
     *       sono validati e persistenti
     * @post L'utente riceve feedback appropriato in caso di errori durante il
     *       processo
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
     * Crea un utente predefinito nel sistema quando non esistono utenti registrati.
     * <p>
     * Utilizzato per garantire l'accesso iniziale al sistema con credenziali
     * standard.
     * Questo metodo viene tipicamente invocato durante il primo avvio
     * dell'applicazione
     * o quando il database utenti è vuoto.
     *
     * @return true se la creazione dell'utente predefinito ha successo, false
     *         altrimenti
     *
     * @pre this.database != null
     * @pre this.view != null
     * @post Se la creazione ha successo, un nuovo utente Configuratore con
     *       credenziali predefinite è aggiunto al database
     * @post Se la creazione ha successo, i dati utente sono salvati su file
     *       (tramite salvaUtenti())
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
            String password = view.getPasswordInput(ControllerConstants.PROMPT_PASSWORD);
            this.view.showMessage("\n");
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

    // ==============================================================================
    // 4. GESTIONE COMPRENSORI
    // ==============================================================================

    /**
     * Visualizza i comprensori geografici registrati nel sistema.
     * <p>
     * Mostra un elenco formattato di tutti i comprensori disponibili con le
     * seguenti informazioni:
     * <ul>
     * <li>Nome del comprensorio</li>
     * <li>Elenco dei comuni appartenenti</li>
     * </ul>
     *
     * @pre this.database != null
     * @pre this.view != null
     * @post Se non esistono comprensori, viene mostrato un messaggio informativo
     * @post Se esistono comprensori, viene visualizzato l'elenco completo in
     *       formato leggibile
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
     * @post Se la creazione ha successo, un nuovo comprensorio viene aggiunto al
     *       database
     * @post Se la creazione ha successo, i dati vengono salvati su file (tramite
     *       serializeComprensori())
     * @post Se la creazione ha successo, viene mostrato un messaggio di conferma
     * @post Se la creazione è annullata o fallisce, viene mostrato un messaggio
     *       appropriato
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
     * @return true se il comprensorio è stato creato e aggiunto con successo, false
     *         altrimenti
     *
     * @pre this.database != null
     * @pre this.view != null
     * @post Se restituisce true, un nuovo comprensorio è stato aggiunto al database
     * @post Se l'utente annulla l'operazione (inserendo "0"), restituisce false
     * @post Se il nome scelto è già in uso, l'utente viene guidato a scegliere un
     *       altro nome
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
     * Aggiunge comuni a un comprensorio esistente attraverso un'interfaccia
     * interattiva.
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
     * @post Ogni comune aggiunto rispetta i criteri di validità (non vuoto,
     *       lunghezza minima)
     * @post L'utente può terminare l'inserimento in qualsiasi momento inserendo "0"
     * @post Se un comune esiste già nel comprensorio, viene mostrato un messaggio
     *       di errore senza duplicazione
     * @post Il processo continua fino a quando l'utente non sceglie di terminare
     *       l'inserimento
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
                if (!this.view.getConfirmation(ControllerConstants.PROMPT_ADD_ALTRO_COMUNE)) {
                    finished = true; // Termina il ciclo se l'utente sceglie di non continuare
                }
            }
        }
    }

    /**
     * Permette all'utente di selezionare un comprensorio geografico.
     * <p>
     * Presenta all'utente un elenco numerato dei comprensori disponibili nel
     * sistema
     * e acquisisce la scelta tramite input numerico. Il processo include:
     * <ol>
     * <li>Verifica della presenza di comprensori nel database</li>
     * <li>Visualizzazione dell'elenco numerato di comprensori con numero di
     * comuni</li>
     * <li>Acquisizione della scelta dell'utente (con possibilità di
     * annullamento)</li>
     * <li>Validazione della scelta e selezione del comprensorio corrispondente</li>
     * </ol>
     * <p>
     * Questo metodo è utilizzato durante la registrazione dei fruitori e in altre
     * operazioni che richiedono l'associazione a un comprensorio specifico.
     *
     * @return il comprensorio selezionato dall'utente, o null se l'operazione è
     *         annullata o non ci sono comprensori
     *
     * @pre this.database != null
     * @pre this.view != null
     * @post Se il database non contiene comprensori, viene mostrato un messaggio di
     *       errore e restituito null
     * @post Se l'utente seleziona 0, l'operazione viene annullata e viene
     *       restituito null
     * @post Se l'utente seleziona un indice valido i (dove 1 ≤ i ≤ numero di
     *       comprensori),
     *       viene restituito il comprensorio corrispondente
     * @post Se restituisce un valore non-null, questo rappresenta un comprensorio
     *       valido selezionato dall'utente
     * @post All'utente viene mostrata conferma della selezione effettuata o
     *       dell'annullamento
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

    // ==============================================================================
    // 5. GESTIONE GERARCHIE
    // ==============================================================================

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
     * @post Se esistono gerarchie, viene visualizzata la struttura completa in
     *       formato leggibile
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
     * appropriato in caso negativo.
     *
     * @return true se esistono gerarchie nel database, false altrimenti
     *
     * @pre this.database != null
     * @pre this.view != null
     * @post Se non esistono gerarchie, viene mostrato un messaggio specifico di
     *       errore
     * @post Il valore restituito è true se e solo se esiste almeno una gerarchia
     *       nel database
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
     * Avvia il processo di creazione di una nuova gerarchia di categorie.
     * <p>
     * Orchestrazione completa del flusso di creazione che include:
     * <ol>
     * <li>Strutturazione della gerarchia con categorie e relazioni
     * padre-figlio</li>
     * <li>Definizione di campi caratteristici e domini di valori</li>
     * <li>Configurazione dei fattori di conversione interni alla gerarchia</li>
     * <li>Configurazione dei fattori di conversione con altre gerarchie
     * esistenti</li>
     * <li>Salvataggio della gerarchia completa nel database</li>
     * <li>Persistenza dei dati su file</li>
     * </ol>
     *
     * @pre this.database != null
     * @pre this.view != null
     * @post Una nuova gerarchia completa viene aggiunta al database
     * @post I dati delle gerarchie vengono salvati su file (tramite
     *       serializeGerarchie())
     * @post I fattori di conversione vengono salvati su file (tramite
     *       serializeConversioneGerarchie())
     * @post Se esistono altre gerarchie, vengono configurati fattori di conversione
     *       tra la nuova e le esistenti
     * @post Viene mostrato un messaggio di conferma al completamento
     *       dell'operazione
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

    /**
     * Crea una nuova gerarchia di categorie completa.
     * <p>
     * Implementa un processo interattivo di costruzione di una gerarchia che
     * include:
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
     * @post Le relazioni gerarchiche tra le categorie formano una struttura ad
     *       albero coerente
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
     * Il metodo crea e configura completamente la categoria prima di restituirla.
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
     * @param nomePadre nome della categoria padre sotto cui posizionare la nuova
     *                  categoria
     * @param root      gerarchia a cui aggiungere la categoria
     * @return il nodo dell'albero contenente la categoria creata
     *
     * @pre nomePadre != null
     * @pre root != null
     * @pre this.view != null
     * @post Il nodo restituito contiene una categoria valida e completamente
     *       configurata
     * @post La categoria creata è correttamente posizionata nella struttura
     *       gerarchica
     * @post La categoria ha un nome univoco all'interno del suo livello nella
     *       gerarchia
     * @post La categoria ha almeno un campo caratteristico con valori di dominio
     *       definiti
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
            aggiungiCampo = this.view.getConfirmation(ControllerConstants.PROMPT_CAMPO_CARATTERISTICO);
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
     * Implementa un'interfaccia interattiva per la definizione completa di un
     * campo, includendo:
     * <ol>
     * <li>Acquisizione e validazione del nome del campo</li>
     * <li>Definizione dei valori di dominio ammissibili</li>
     * <li>Validazione di ciascun valore inserito</li>
     * <li>Verifica che il dominio contenga almeno un valore</li>
     * </ol>
     * <p>
     * Ogni campo creato include un insieme non vuoto di valori possibili che
     * rappresentano
     * il dominio del campo stesso.
     *
     * @return il campo caratteristico creato e configurato, o null se l'utente
     *         annulla l'operazione
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
     * Guida l'utente attraverso un processo interattivo per definire l'insieme di
     * valori
     * validi (dominio) che un campo può assumere. Il processo include:
     * <ol>
     * <li>Acquisizione e validazione di ogni valore inserito</li>
     * <li>Verifica dell'unicità all'interno del dominio</li>
     * <li>Possibilità di aggiungere descrizioni opzionali ai valori</li>
     * <li>Garanzia che il campo abbia almeno un valore nel dominio</li>
     * </ol>
     * <p>
     * Il metodo utilizza un approccio iterativo, permettendo all'utente di
     * aggiungere
     * valori finché non decide di terminare l'inserimento.
     *
     * @param campo il campo caratteristico per cui definire il dominio di valori
     *
     * @pre campo != null
     * @pre this.view != null
     * @post Il campo ha almeno un valore definito nel suo dominio
     * @post Ogni valore nel dominio è valido (non vuoto, rispetta la lunghezza
     *       minima)
     * @post Non ci sono valori duplicati nel dominio (ogni valore ha un nome
     *       univoco)
     * @post L'utente riceve feedback appropriati durante il processo di inserimento
     * @post Se l'utente tenta di terminare senza inserire valori, riceve un avviso
     */
    private void createDominio(Campo campo) {
        boolean finished = false;
        int valoreCount = 0;

        // Messaggio introduttivo
        view.showMessage(String.format(ControllerConstants.PROMPT_VALORI_CAMPO, campo.getNome()));

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
                if (!this.view.getConfirmation(ControllerConstants.PROMPT_ALTRO_VALORE)) {
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

    // ==============================================================================
    // 6. GESTIONE FATTORI DI CONVERSIONE
    // ==============================================================================

    /**
     * Visualizza i fattori di conversione tra categorie di gerarchie diverse.
     * <p>
     * Questo metodo guida l'utente attraverso un processo interattivo che include:
     * <ol>
     * <li>Selezione della gerarchia di origine</li>
     * <li>Selezione di una categoria foglia specifica all'interno della gerarchia
     * di origine</li>
     * <li>Selezione della gerarchia di destinazione per cui visualizzare i
     * fattori</li>
     * <li>Calcolo e visualizzazione del fattore di conversione per ogni foglia
     * nella gerarchia di destinazione</li>
     * </ol>
     * <p>
     * Il fattore di conversione viene calcolato considerando tre componenti:
     * <ul>
     * <li>Fattore interno alla gerarchia di origine (dalla foglia selezionata al
     * nodo di riferimento)</li>
     * <li>Fattore tra gerarchie (tra i nodi di riferimento delle due
     * gerarchie)</li>
     * <li>Fattore interno alla gerarchia di destinazione (dal nodo di riferimento a
     * ogni foglia)</li>
     * </ul>
     * <p>
     * Questa funzionalità permette ai configuratori di comprendere le relazioni
     * quantitative tra
     * diverse categorie nel sistema, facilitando la valutazione dell'equità negli
     * scambi.
     *
     * @pre this.database != null
     * @pre this.view != null
     * @pre database.getGerarchie() non è vuoto (verificato prima della chiamata
     *      tramite verificaPresenzaGerarchie())
     * @pre Per ogni gerarchia, esiste almeno un fattoreConversione definito
     * @post L'utente visualizza i fattori di conversione dalla categoria
     *       selezionata a tutte le categorie
     *       della gerarchia di destinazione
     * @post I fattori mostrati riflettono accuratamente le relazioni definite tra
     *       le gerarchie
     * @post Lo stato del sistema rimane invariato (operazione di sola lettura)
     * @post L'utente comprende come convertire ore tra categorie diverse
     *       appartenenti a gerarchie diverse
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
                String.format(ControllerConstants.INFO_SELEZIONA_DESTINAZIONE_FATTORI, fogliaScelta.data().getNome()));
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
     * Permette all'utente di selezionare una gerarchia tra quelle disponibili nel
     * sistema.
     * <p>
     * Visualizza un elenco numerato delle gerarchie esistenti e acquisisce la
     * scelta
     * dell'utente tramite input numerico. La numerazione presentata all'utente è
     * 1-based
     * per una migliore comprensione da parte dell'utente finale.
     *
     * @return la gerarchia selezionata dall'utente
     *
     * @pre this.database != null
     * @pre this.view != null
     * @pre this.database.getGerarchie() non è vuoto
     * @post La gerarchia restituita è una delle gerarchie esistenti nel database
     * @post L'utente ha potuto visualizzare l'elenco completo delle gerarchie
     *       disponibili
     * @post L'indice selezionato dall'utente è stato validato prima di accedere
     *       alla gerarchia corrispondente
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
        int indiceScelta = view.getIntInput(ControllerConstants.MSG_SCELTA_GERARCHIA, 1, numScelte) - 1;

        // Restituisce la gerarchia selezionata
        return this.database.getGerarchia(indiceScelta);
    }

    /**
     * Permette all'utente di selezionare una categoria foglia da una gerarchia
     * specificata.
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
     * @post Il nodo restituito appartiene all'insieme delle foglie della gerarchia
     *       specificata
     * @post L'indice selezionato dall'utente è stato validato prima di accedere
     *       alla foglia corrispondente
     * @post L'utente ha potuto visualizzare la struttura gerarchica con le foglie
     *       evidenziate
     */
    private TreeNode<Categoria> selezionaFoglia(Gerarchia gerarchia) {
        // Ottiene tutte le foglie della gerarchia (le categorie che rappresentano
        // attività concrete)
        ArrayList<TreeNode<Categoria>> foglie = gerarchia.getFoglie();

        // Richiede all'utente di inserire l'indice della foglia desiderata
        int numScelte = foglie.size();
        // Gli indici visualizzati partono da 1, quindi sottraiamo 1 per ottenere
        // l'indice effettivo (0-based)
        int indiceScelta = view.getIntInput(ControllerConstants.MSG_SCELTA_FOGLIA, 1, numScelte) - 1;

        // Restituisce la foglia selezionata
        return foglie.get(indiceScelta);
    }

    /**
     * Crea i fattori di conversione tra una nuova gerarchia e tutte quelle
     * esistenti.
     * <p>
     * Guida l'utente attraverso il processo di definizione delle relazioni
     * quantitative
     * tra la nuova gerarchia e ciascuna di quelle già presenti nel sistema. Questi
     * fattori permettono la conversione di valori tra domini concettuali diversi ma
     * correlati.
     * <p>
     * Il processo richiede l'inserimento di un valore numerico positivo che
     * definisce
     * il rapporto di conversione tra i nodi di riferimento delle due gerarchie.
     *
     * @param gerarchia la nuova gerarchia da collegare alle gerarchie esistenti
     *
     * @pre gerarchia != null
     * @pre this.database != null
     * @pre this.view != null
     * @pre Il database contiene almeno una gerarchia oltre a quella specificata
     * @post Per ogni gerarchia esistente, viene definito un fattore di conversione
     *       con la nuova gerarchia
     * @post Ogni fattore di conversione è un numero positivo maggiore di zero
     * @post La consistenza matematica delle conversioni inter-gerarchiche è
     *       garantita
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
     * Visualizza il percorso completo dalla radice a un nodo specifico nella
     * gerarchia.
     * <p>
     * Costruisce e mostra la catena gerarchica di categorie che collega la
     * categoria radice
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
     * @post L'utente visualizza il percorso completo dalla radice al nodo
     *       specificato
     * @post La visualizzazione include tutte le categorie nel percorso,
     *       correttamente ordinate
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
     * Implementa un processo interattivo per definire relazioni quantitative tra le
     * varie
     * attività (categorie foglia) di una stessa gerarchia, permettendo:
     * <ol>
     * <li>Selezione automatica di una foglia di riferimento (base di
     * conversione)</li>
     * <li>Definizione guidata dei fattori relativi per tutte le altre foglie</li>
     * <li>Costruzione di un sistema coerente di conversioni intra-gerarchiche</li>
     * </ol>
     * <p>
     * I fattori definiti rappresentano la relazione quantitativa tra attività dello
     * stesso
     * dominio concettuale ma con diversi livelli di specializzazione.
     *
     * @param gerarchia la gerarchia su cui definire i fattori di conversione
     *                  interni
     *
     * @pre gerarchia != null
     * @pre gerarchia.getFogliePerLivello() non è vuoto
     * @pre this.view != null
     * @post La gerarchia contiene un nodo di riferimento definito come base di
     *       conversione
     * @post Ogni foglia della gerarchia ha un fattore di conversione definito
     *       rispetto al riferimento
     * @post I fattori di conversione sono maggiori di zero
     * @post L'utente riceve feedback appropriato durante il processo di definizione
     * @post Il sistema di conversioni interno alla gerarchia è matematicamente
     *       coerente
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
     * @param categoriaOrigine      nome della categoria di origine per la
     *                              conversione
     * @param categoriaDestinazione nome della categoria di destinazione per la
     *                              conversione
     * @param minFattore            valore minimo accettabile per il fattore
     *                              (incluso)
     * @param maxFattore            valore massimo accettabile per il fattore
     *                              (incluso)
     * @param messaggioErrore       messaggio da mostrare in caso di input non
     *                              valido
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
     * @post Il metodo ha gestito correttamente eventuali errori di formato
     *       nell'input
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

    /**
     * Calcola il fattore di conversione tra due categorie foglia, potenzialmente in
     * gerarchie diverse.
     * <p>
     * Questo metodo implementa l'algoritmo di conversione completo che permette di
     * stabilire una relazione
     * quantitativa tra categorie diverse, considerando tre componenti:
     * <ol>
     * <li>Fattore interno alla gerarchia di origine (dalla foglia richiesta al nodo
     * di riferimento)</li>
     * <li>Fattore tra le gerarchie (tra i rispettivi nodi di riferimento)</li>
     * <li>Fattore interno alla gerarchia di destinazione (dal nodo di riferimento
     * alla foglia offerta)</li>
     * </ol>
     * <p>
     * Il risultato finale è il prodotto di questi tre fattori, che permette di
     * determinare
     * il rapporto di conversione tra ore di categorie diverse, consentendo scambi
     * equi
     * tra prestazioni di natura differente.
     *
     * @param gerarchiaOrigine      gerarchia della categoria richiesta
     * @param fogliaRichiesta       nodo foglia della categoria richiesta
     * @param gerarchiaDestinazione gerarchia della categoria offerta
     * @param fogliaOfferta         nodo foglia della categoria offerta
     * @return il fattore di conversione complessivo
     *
     * @pre gerarchiaOrigine != null
     * @pre fogliaRichiesta != null
     * @pre gerarchiaDestinazione != null
     * @pre fogliaOfferta != null
     * @pre gerarchiaOrigine.getFattoreConversione() != null
     * @pre gerarchiaDestinazione.getFattoreConversione() != null
     * @pre gerarchiaOrigine.getFattoreConversione().getFogliaRiferimento() != null
     * @pre gerarchiaDestinazione.getFattoreConversione().getFogliaRiferimento() !=
     *      null
     * @pre fogliaRichiesta appartiene a gerarchiaOrigine
     * @pre fogliaOfferta appartiene a gerarchiaDestinazione
     * @post Il valore restituito è un numero decimale positivo > 0
     * @post Se le gerarchie sono identiche, il fattore inter-gerarchia è 1.0
     * @post Il fattore risultante è il prodotto di: fattoreIntraOrigine *
     *       fattoreInterGerarchia * fattoreIntraDestinazione
     * @post Il fattore può essere usato per convertire quantitativamente ore tra le
     *       categorie specificate
     */
    private double calcolaFattoreConversione(
            Gerarchia gerarchiaOrigine, TreeNode<Categoria> fogliaRichiesta,
            Gerarchia gerarchiaDestinazione, TreeNode<Categoria> fogliaOfferta) {

        // 1. Fattore interno alla gerarchia di origine (dalla foglia al nodo di
        // riferimento)
        double fattoreIntraGerarchia = gerarchiaOrigine.getFattoreConversione().getFattoreConversione(
                fogliaRichiesta.level(),
                gerarchiaOrigine.getFattoreConversione().getFogliaRiferimento().level());

        // 2. Fattore tra gerarchie diverse (tra i nodi di riferimento)
        double fattoreInterGerarchia = 1.0; // Default: stessa gerarchia, fattore 1
        if (!gerarchiaOrigine.equals(gerarchiaDestinazione)) {
            // Se le gerarchie sono diverse, ottiene il fattore tra i loro nodi di
            // riferimento
            fattoreInterGerarchia = database.getConversioneGerarchie().getFattoreConversione(
                    gerarchiaOrigine.getFattoreConversione().getFogliaRiferimento().data(),
                    gerarchiaDestinazione.getFattoreConversione().getFogliaRiferimento().data());
        }

        // 3. Fattore interno alla gerarchia di destinazione (dal nodo di riferimento
        // alla foglia)
        double fattoreIntraGerarchiaDest = gerarchiaDestinazione.getFattoreConversione().getFattoreConversione(
                gerarchiaDestinazione.getFattoreConversione().getFogliaRiferimento().level(),
                fogliaOfferta.level());

        // Calcolo del fattore combinato
        return Math.clamp(fattoreIntraGerarchia * fattoreInterGerarchia * fattoreIntraGerarchiaDest, 0.5, 2);
    }

    // ==============================================================================
    // 7. GESTIONE SCAMBI D'OPERA
    // ==============================================================================

    /**
     * Gestisce il processo di creazione di una proposta di scambio d'opera.
     * <p>
     * Guida l'utente attraverso un flusso interattivo che include:
     * <ol>
     * <li>Selezione della categoria richiesta (ciò che il fruitore desidera
     * ricevere)</li>
     * <li>Indicazione del numero di ore richieste</li>
     * <li>Selezione della categoria offerta (ciò che il fruitore è disposto a
     * offrire)</li>
     * <li>Calcolo automatico del fattore di conversione tra le categorie
     * selezionate</li>
     * <li>Determinazione delle ore da offrire in base al fattore di
     * conversione</li>
     * <li>Creazione e registrazione della proposta nel sistema</li>
     * </ol>
     * <p>
     * Il processo applica automaticamente i fattori di conversione tra gerarchie
     * diverse
     * per garantire l'equità dello scambio, considerando la specificità delle
     * categorie
     * coinvolte.
     *
     * @param fruitore il fruitore che sta creando la proposta di scambio
     * @return true se la proposta è stata creata e registrata con successo, false
     *         se l'utente ha annullato o la creazione è fallita
     *
     * @pre fruitore != null
     * @pre this.view != null
     * @pre this.database != null
     * @pre database.getGerarchie() non è vuoto (deve esistere almeno una gerarchia
     *      nel sistema)
     * @post Se restituisce true, una nuova proposta è stata creata e registrata nel
     *       database
     * @post Se restituisce true, la proposta ha stato APERTA e viene salvata
     *       tramite salvaUtenti()
     * @post Se restituisce false, nessuna proposta attiva è stata aggiunta al
     *       sistema (o è stata aggiunta con stato RITIRATA)
     * @post L'utente riceve feedback appropriati durante l'intero processo
     * @post Il calcolo delle ore offerte è basato sul fattore di conversione tra le
     *       categorie selezionate
     */
    private Boolean propostaScambioOpera(Fruitore fruitore) {
        // Intestazione e spiegazione del processo
        view.showMessage(ControllerConstants.TITLE_PROPOSTA_SCAMBIO);
        view.showMessage(ControllerConstants.INFO_PROPOSTA_DESCRIZIONE);
        view.showWarningMessage(ControllerConstants.INFO_PROPOSTA_AVVERTENZA);

        // FASE 1: Selezione della prestazione richiesta
        view.showMessage("\n" + ControllerConstants.STEP_SELEZIONE_RICHIESTA);

        // Selezione della gerarchia di origine
        view.showMessage(ControllerConstants.MSG_LISTA_GERARCHIE);
        Gerarchia gerarchiaOrigine = selezionaGerarchia();
        if (gerarchiaOrigine == null) {
            view.showMessage(ControllerConstants.INFO_OPERAZIONE_ANNULLATA);
            return false;
        }

        // Selezione della foglia (attività concreta) dalla gerarchia origine
        view.showMessage(ControllerConstants.MSG_LISTA_FOGLIE);
        view.showMessage(gerarchiaOrigine.toStringSelezioneFoglia());
        TreeNode<Categoria> fogliaRichiesta = selezionaFoglia(gerarchiaOrigine);
        if (fogliaRichiesta == null) {
            view.showMessage(ControllerConstants.INFO_OPERAZIONE_ANNULLATA);
            return false;
        }

        // FASE 2: Acquisizione delle ore richieste
        view.showMessage("\n" + ControllerConstants.STEP_ORE_RICHIESTE);
        int oreRichieste = view.getIntInput(
                String.format(ControllerConstants.PROMPT_ORE_RICHIESTE,
                        fogliaRichiesta.data().getNome()),
                1, 100); // Limitiamo a 100 ore per praticità

        // FASE 3: Selezione della prestazione offerta
        view.showMessage("\n" + ControllerConstants.STEP_SELEZIONE_OFFERTA);

        // Selezione della gerarchia di destinazione
        view.showMessage(String.format(ControllerConstants.INFO_SELEZIONA_DESTINAZIONE_PRESTAZIONI,
                fogliaRichiesta.data().getNome()));
        Gerarchia gerarchiaDestinazione = selezionaGerarchia();
        if (gerarchiaDestinazione == null) {
            view.showMessage(ControllerConstants.INFO_OPERAZIONE_ANNULLATA);
            return false;
        }

        // Selezione della categoria foglia per l'offerta
        view.showMessage(ControllerConstants.MSG_LISTA_FOGLIE);
        view.showMessage(gerarchiaDestinazione.toStringSelezioneFoglia());
        TreeNode<Categoria> fogliaOfferta = selezionaFoglia(gerarchiaDestinazione);
        if (fogliaOfferta == null) {
            view.showMessage(ControllerConstants.INFO_OPERAZIONE_ANNULLATA);
            return false;
        }

        // Verifica che non si stia offrendo la stessa categoria richiesta
        if (fogliaRichiesta.data().equals(fogliaOfferta.data())) {
            view.showErrorMessage(ControllerConstants.ERROR_STESSA_CATEGORIA);
            view.showMessage(ControllerConstants.INFO_OPERAZIONE_ANNULLATA);
            return false;
        }

        // FASE 4: Calcolo delle ore offerte in base ai fattori di conversione
        view.showMessage("\n" + ControllerConstants.STEP_CALCOLO_CONVERSIONE);

        // Calcolo del fattore di conversione complessivo
        double fattoreConversione = calcolaFattoreConversione(
                gerarchiaOrigine, fogliaRichiesta,
                gerarchiaDestinazione, fogliaOfferta);

        view.showMessage(String.format(ControllerConstants.STEP_FATTORE_CONV, fattoreConversione));

        // Calcolo delle ore offerte basate sul fattore di conversione
        int oreOfferte = getOreOfferte(oreRichieste, fattoreConversione);
        // FASE 5: Presentazione del risultato e conferma
        view.showMessage("\n" + ControllerConstants.STEP_RIEPILOGO_PROPOSTA);
        view.showMessage(String.format(ControllerConstants.INFO_RIEPILOGO_PROPOSTA,
                oreRichieste, fogliaRichiesta.data().getNome(),
                oreOfferte, fogliaOfferta.data().getNome()));

        // Chiede la conferma dell'utente
        boolean accetta = view.getConfirmation(ControllerConstants.PROMPT_CONFERMA_PROPOSTA);

        // FASE 6: Creazione e registrazione della proposta
        Prestazione prestazioneRichiesta = new Prestazione(fogliaRichiesta, oreRichieste);
        Prestazione prestazioneOfferta = new Prestazione(fogliaOfferta, oreOfferte);

        StatoProposta stato = accetta ? StatoProposta.APERTA : StatoProposta.RITIRATA;
        Proposta proposta = new Proposta(fruitore, prestazioneRichiesta, prestazioneOfferta, stato);

        // Aggiunge la proposta al fruitore e mostra il messaggio appropriato
        this.database.aggiungiProposta(proposta);

        if (accetta) {
            view.showSuccessMessage(ControllerConstants.SUCCESS_PROPOSTA_CREATA);
            view.showMessage("\n" + proposta);
            return true;
        } else {
            view.showMessage(ControllerConstants.INFO_PROPOSTA_RITIRATA);
        }
        return false;
    }

    private static int getOreOfferte(int oreRichieste, double fattoreConversione) {
        double oreOfferteOriginali = oreRichieste * fattoreConversione;
        int oreOfferte;
        // Se il fattore è < 1, tende ad arrotondare per difetto
        // Se il fattore è > 1, tende ad arrotondare per eccesso
        if (fattoreConversione < 1) {
            // Per fattori < 1, arrotonda per difetto se la parte decimale è <= 0.5
            oreOfferte = (int) Math.max(1, Math.floor(oreOfferteOriginali +
                    (oreOfferteOriginali % 1 > 0.5 ? 1 : 0)));
        } else {
            // Per fattori > 1, arrotonda per difetto anche se la parte decimale è > 0.5
            // ma < 0.7 per compensare l'asimmetria
            oreOfferte = (int) Math.max(1, Math.floor(oreOfferteOriginali +
                    (oreOfferteOriginali % 1 > 0.7 ? 1 : 0)));
        }
        return oreOfferte;
    }

    /**
     * Valuta se è possibile creare un insieme chiuso di scambi con l'ultima
     * proposta del fruitore.
     * <p>
     * Analizza tutte le proposte attive degli altri fruitori appartenenti allo
     * stesso comprensorio
     * del fruitore specificato, cercando cicli di scambio che formino insiemi
     * chiusi. Il processo include:
     * <ol>
     * <li>Verifica della presenza di proposte per il fruitore</li>
     * <li>Creazione del grafo degli scambi potenziali tra fruitori dello stesso
     * comprensorio</li>
     * <li>Ricerca di cicli nel grafo che rappresentano scambi reciprocamente
     * vantaggiosi</li>
     * <li>Chiusura delle proposte coinvolte e creazione dell'insieme chiuso</li>
     * <li>Notifica ai configuratori dell'insieme chiuso trovato</li>
     * </ol>
     * <p>
     * Un insieme chiuso è valido quando tutti i fruitori coinvolti ricevono una
     * prestazione che
     * soddisfa la loro richiesta e forniscono una prestazione che soddisfa la
     * richiesta di un altro.
     *
     * @param fruitore il fruitore che ha appena creato una proposta
     * 
     * @pre fruitore != null
     * @pre this.view != null
     * @pre this.database != null
     * @post Se viene trovato un ciclo valido, le proposte coinvolte vengono chiuse
     *       (StatoProposta.CHIUSA)
     * @post Se viene trovato un ciclo valido, viene creato un InsiemeChiuso nel
     *       sistema
     * @post Se viene trovato un ciclo valido, l'InsiemeChiuso viene salvato nel
     *       database
     * @post Se non viene trovato alcun ciclo valido, lo stato delle proposte rimane
     *       invariato
     * @post Gli utenti ricevono notifica appropriata sull'esito dell'operazione
     */
    private void valutaProposteScambio(Fruitore fruitore) {
        if (fruitore == null) {
            return;
        }

        List<Proposta> proposteFruitore = this.database.getPropostePerFruitore(fruitore);

        // Verifica se il fruitore ha proposte di scambio
        if (proposteFruitore.isEmpty()) {
            return;
        }

        // Ottiene l'ultima proposta del fruitore (quella appena creata)
        Proposta propostaCorrente = proposteFruitore.getLast();

        // Verifica che la proposta sia aperta
        if (propostaCorrente.getStato() != StatoProposta.APERTA) {
            return;
        }

        // Ottiene le proposte aperte nel comprensorio (escluso il fruitore corrente)
        List<Proposta> proposte = this.database.getProposteAperteStessoComprensorio(fruitore, false);

        if (proposte.isEmpty()) {
            return;
        }

        // Costruisce il grafo degli scambi
        Map<Fruitore, List<ArcoScambio>> grafo = costruisciGrafoScambi(fruitore, propostaCorrente, proposte);

        // Cerca cicli nel grafo
        InsiemeChiuso insiemeChiuso = trovaCicloScambio(fruitore, grafo);

        // Se viene trovato un ciclo valido
        if (insiemeChiuso != null) {
            // Informa l'utente del ciclo trovato
            view.showSuccessMessage(ControllerConstants.TITLE_INSIEME_CHIUSO_TROVATO);
            view.showMessage(ControllerConstants.INFO_PROPOSTE_INSIEME_CHIUSO);

            // Chiudi tutte le proposte nell'insieme e mostra dettagli
            for (Proposta proposta : insiemeChiuso.getProposte()) {
                proposta.chiudiProposta();
                view.showMessage(String.format(ControllerConstants.FORMATO_LISTA_SCAMBI, proposta));
            }

            // Mostra gli scambi che formano il ciclo
            view.showMessage(ControllerConstants.INFO_DETTAGLI_SCAMBI);
            for (Scambio scambio : insiemeChiuso.getScambi()) {
                view.showMessage(String.format(ControllerConstants.FORMATO_LISTA_SCAMBI, scambio.toString()));
            }
            this.database.aggiungiInsiemeChiuso(insiemeChiuso);
            this.database.serializeInsiemiChiusi(ControllerConstants.INSIEMECHIUSO_FILE_PATH);
        }
    }

    /**
     * Classe interna che rappresenta un arco nel grafo degli scambi.
     * <p>
     * Un arco rappresenta una relazione direzionale "fornisce a" tra due fruitori,
     * dove un fruitore offre una prestazione che soddisfa la richiesta di un altro.
     * Ogni arco mantiene riferimenti a:
     * <ul>
     * <li>Il fruitore che fornisce la prestazione (fornitore)</li>
     * <li>Il fruitore che riceve la prestazione (ricevente)</li>
     * <li>La proposta che contiene i dettagli della prestazione</li>
     * </ul>
     * <p>
     * Gli archi sono utilizzati per costruire il grafo che rappresenta tutte
     * le possibili connessioni di scambio tra i fruitori del sistema.
     */
    private static class ArcoScambio {
        private final Fruitore fornitore;
        private final Fruitore ricevente;
        private final Proposta proposta;

        public ArcoScambio(Fruitore fornitore, Fruitore ricevente, Proposta proposta) {
            this.fornitore = fornitore;
            this.ricevente = ricevente;
            this.proposta = proposta;
        }

        public Fruitore getFornitore() {
            return fornitore;
        }

        public Fruitore getRicevente() {
            return ricevente;
        }

        public Proposta getProposta() {
            return proposta;
        }

        @Override
        public String toString() {
            return String.format("%s → %s: %s",
                    fornitore.getUsername(),
                    ricevente.getUsername(),
                    proposta.getOfferta().toString());
        }
    }

    /**
     * Costruisce un grafo orientato che rappresenta le possibili connessioni
     * tra fruitori basate sulle loro proposte di scambio.
     * <p>
     * Nel grafo, ogni nodo è un fruitore e ogni arco rappresenta una possibile
     * relazione di scambio dove un fruitore (origine) può fornire ciò che
     * un altro fruitore (destinazione) richiede. Il grafo viene costruito
     * considerando:
     * <ul>
     * <li>La corrispondenza tra categorie offerte e richieste</li>
     * <li>La compatibilità tra ore offerte e ore richieste</li>
     * <li>Le relazioni dirette e indirette tra tutte le proposte nel
     * comprensorio</li>
     * </ul>
     *
     * @param fruitoreCorrente il fruitore che ha appena creato una proposta
     * @param propostaCorrente la proposta appena creata
     * @param proposteAltri    le altre proposte aperte nel comprensorio
     * @return una mappa che rappresenta il grafo degli scambi potenziali
     *
     * @pre fruitoreCorrente != null
     * @pre propostaCorrente != null
     * @pre proposteAltri != null (può essere vuota)
     * @pre propostaCorrente.getStato() == StatoProposta.APERTA
     * @post Il grafo restituito non è null
     * @post Ogni arco nel grafo rappresenta uno scambio potenziale valido tra due
     *       fruitori
     * @post Ogni arco contiene riferimenti validi a fruitore fornitore, fruitore
     *       ricevente e proposta
     * @post Il grafo include tutte le possibili connessioni compatibili tra le
     *       proposte
     */
    private Map<Fruitore, List<ArcoScambio>> costruisciGrafoScambi(
            Fruitore fruitoreCorrente,
            Proposta propostaCorrente,
            List<Proposta> proposteAltri) {

        Map<Fruitore, List<ArcoScambio>> grafo = new HashMap<>();

        // Esamina la proposta corrente e verifica se può soddisfare altre proposte
        for (Proposta altraProposta : proposteAltri) {
            // Trova il fruitore associato all'altra proposta
            Fruitore altroFruitore = trovaFruitore(altraProposta);
            if (altroFruitore == null)
                continue;

            // Se ciò che il fruitore corrente offre corrisponde a ciò che l'altro fruitore
            // richiede
            if (matchCategorie(propostaCorrente.getOfferta(), altraProposta.getRichiesta())) {
                ArcoScambio arco = new ArcoScambio(fruitoreCorrente, altroFruitore, propostaCorrente);
                grafo.computeIfAbsent(fruitoreCorrente, k -> new ArrayList<>()).add(arco);
            }

            // Se ciò che l'altro fruitore offre corrisponde a ciò che il fruitore corrente
            // richiede
            if (matchCategorie(altraProposta.getOfferta(), propostaCorrente.getRichiesta())) {
                ArcoScambio arco = new ArcoScambio(altroFruitore, fruitoreCorrente, altraProposta);
                grafo.computeIfAbsent(altroFruitore, k -> new ArrayList<>()).add(arco);
            }
        }

        // Esamina tutte le relazioni tra le altre proposte per completare il grafo
        for (int i = 0; i < proposteAltri.size(); i++) {
            Proposta proposta1 = proposteAltri.get(i);
            Fruitore fruitore1 = trovaFruitore(proposta1);
            if (fruitore1 == null)
                continue;

            for (int j = 0; j < proposteAltri.size(); j++) {
                if (i == j)
                    continue;

                Proposta proposta2 = proposteAltri.get(j);
                Fruitore fruitore2 = trovaFruitore(proposta2);
                if (fruitore2 == null)
                    continue;

                // Se ciò che il fruitore1 offre corrisponde a ciò che il fruitore2 richiede
                if (matchCategorie(proposta1.getOfferta(), proposta2.getRichiesta())) {
                    ArcoScambio arco = new ArcoScambio(fruitore1, fruitore2, proposta1);
                    grafo.computeIfAbsent(fruitore1, k -> new ArrayList<>()).add(arco);
                }
            }
        }

        return grafo;
    }

    /**
     * Verifica se una prestazione offerta corrisponde a una prestazione richiesta,
     * con una tolleranza di ±1 ora per gestire arrotondamenti nelle conversioni.
     * <p>
     * Due prestazioni sono considerate corrispondenti quando:
     * <ul>
     * <li>Appartengono esattamente alla stessa categoria (no sottocategorie)</li>
     * <li>Le ore offerte sono sufficienti a coprire le ore richieste (con
     * tolleranza)</li>
     * </ul>
     * <p>
     * La tolleranza permette di gestire piccole discrepanze dovute agli
     * arrotondamenti
     * nei calcoli dei fattori di conversione, facilitando la formazione di insiemi
     * chiusi.
     *
     * @param offerta   la prestazione offerta da un fruitore
     * @param richiesta la prestazione richiesta da un altro fruitore
     * @return true se le prestazioni corrispondono (con tolleranza), false
     *         altrimenti
     *
     * @pre offerta != null
     * @pre richiesta != null
     * @pre offerta.getCategoria() != null
     * @pre richiesta.getCategoria() != null
     * @post Il risultato è true se le categorie sono uguali e le ore offerte ≥ ore
     *       richieste - 1
     * @post Lo stato delle prestazioni rimane invariato
     */
    private boolean matchCategorie(Prestazione offerta, Prestazione richiesta) {
        // Confronta le categorie delle due prestazioni
        Categoria categoriaOfferta = offerta.getCategoria().data();
        Categoria categoriaRichiesta = richiesta.getCategoria().data();

        // Verifica che siano della stessa categoria e che le ore siano compatibili
        // aggiungendo una tolleranza di ±1 ora per compensare gli arrotondamenti
        return categoriaOfferta.equals(categoriaRichiesta) &&
                offerta.getOre() >= (richiesta.getOre() - 1);
    }

    /**
     * Trova il fruitore associato a una proposta.
     * <p>
     * Questa funzione di utilità recupera il fruitore che ha creato una specifica
     * proposta,
     * utilizzando il metodo diretto della proposta.
     *
     * @param proposta la proposta di cui trovare il fruitore proprietario
     * @return il fruitore associato alla proposta, o null se non trovato
     *
     * @pre proposta != null
     * @post Il risultato è il fruitore che ha creato la proposta o null se non
     *       trovato
     * @post Lo stato del sistema rimane invariato (operazione di sola lettura)
     */
    private Fruitore trovaFruitore(Proposta proposta) {
        return proposta.getPropostitore();
    }

    /**
     * Cerca un ciclo di scambio nel grafo partendo dal fruitore specificato.
     * <p>
     * Utilizza un algoritmo DFS (Depth-First Search) modificato per identificare
     * cicli nel grafo che rappresentano insiemi chiusi di scambi. Un ciclo valido
     * si verifica quando una serie di scambi forma un percorso che:
     * <ul>
     * <li>Parte da un fruitore e ritorna allo stesso fruitore</li>
     * <li>Ogni fruitore nel ciclo riceve ciò che richiede e fornisce ciò che
     * offre</li>
     * <li>Tutte le categorie e ore sono compatibili negli scambi</li>
     * </ul>
     * <p>
     * Quando viene trovato un ciclo valido, viene creato un insieme chiuso con
     * tutti
     * gli scambi e le proposte coinvolte.
     * 
     * @param fruitoreIniziale il fruitore da cui iniziare la ricerca
     * @param grafo            il grafo degli scambi potenziali
     * @return un InsiemeChiuso se viene trovato un ciclo valido, null altrimenti
     *
     * @pre fruitoreIniziale != null
     * @pre grafo != null
     * @pre fruitoreIniziale.getComprensorio() != null
     * @post Se viene trovato un ciclo valido, restituisce un InsiemeChiuso non null
     * @post Se restituito, l'InsiemeChiuso contiene tutte le proposte e gli scambi
     *       che formano il ciclo
     * @post Se restituito, l'InsiemeChiuso ha lo stesso comprensorio del fruitore
     *       iniziale
     * @post Se non viene trovato alcun ciclo valido, restituisce null
     * @post L'algoritmo considera tutti i possibili cicli partendo dal fruitore
     *       iniziale
     */
    private InsiemeChiuso trovaCicloScambio(Fruitore fruitoreIniziale, Map<Fruitore, List<ArcoScambio>> grafo) {
        // Strutture dati per il tracciamento del percorso
        Set<Fruitore> visitati = new HashSet<>();
        List<ArcoScambio> percorsoCorrente = new ArrayList<>();
        Map<Fruitore, ArcoScambio> archiEntranti = new HashMap<>();
        Set<Fruitore> inPercorso = new HashSet<>();

        // Esegui la ricerca DFS
        boolean trovato = dfs(fruitoreIniziale, fruitoreIniziale, grafo, visitati, percorsoCorrente, archiEntranti,
                inPercorso);

        // Se non è stato trovato un ciclo
        if (!trovato) {
            return null;
        }

        // Crea un insieme chiuso con gli archi del ciclo trovato
        InsiemeChiuso insiemeChiuso = new InsiemeChiuso(fruitoreIniziale.getComprensorio());

        // Ricostruisci il ciclo dagli archi entranti
        Fruitore nodoCorrente = fruitoreIniziale;
        do {
            ArcoScambio arco = archiEntranti.get(nodoCorrente);
            if (arco == null)
                break;

            // Aggiungi proposta e scambio all'insieme chiuso
            Proposta proposta = arco.getProposta();
            insiemeChiuso.aggiungiProposta(proposta);

            Scambio scambio = new Scambio(
                    arco.getFornitore(),
                    arco.getRicevente(),
                    proposta.getOfferta(),
                    proposta);
            insiemeChiuso.aggiungiScambio(scambio);

            // Passa al nodo precedente
            nodoCorrente = arco.getFornitore();

        } while (!nodoCorrente.equals(fruitoreIniziale));

        return insiemeChiuso;
    }

    /**
     * Esegue una ricerca in profondità (DFS) nel grafo per trovare cicli.
     * <p>
     * Implementa un algoritmo DFS modificato che tiene traccia del percorso
     * corrente
     * e rileva cicli che ritornano al nodo iniziale. Durante la ricerca:
     * <ul>
     * <li>Ogni nodo visitato viene marcato per evitare cicli spuri</li>
     * <li>Il percorso corrente viene costruito aggiungendo archi man mano che si
     * esplora</li>
     * <li>Quando si trova un arco che torna al nodo iniziale, viene identificato un
     * ciclo valido</li>
     * <li>Gli archi entranti vengono registrati per poter ricostruire il ciclo
     * completo</li>
     * </ul>
     *
     * @param nodoIniziale     il nodo di partenza da cui deve chiudersi il ciclo
     * @param nodoCorrente     il nodo attualmente in esame nella ricorsione
     * @param grafo            il grafo degli scambi potenziali
     * @param visitati         insieme dei fruitori già visitati in qualsiasi ramo
     *                         della ricerca
     * @param percorsoCorrente lista ordinata degli archi nel percorso corrente
     * @param archiEntranti    mappa che associa a ogni fruitore l'arco con cui è
     *                         stato raggiunto
     * @param inPercorso       insieme dei fruitori nel percorso corrente (per
     *                         rilevare cicli)
     * @return true se viene trovato un ciclo valido, false altrimenti
     *
     * @pre nodoIniziale != null
     * @pre nodoCorrente != null
     * @pre grafo != null
     * @pre visitati != null
     * @pre percorsoCorrente != null
     * @pre archiEntranti != null
     * @pre inPercorso != null
     * @post Se restituisce true, è stato trovato un ciclo valido che parte e
     *       termina in nodoIniziale
     * @post Se restituisce true, archiEntranti contiene tutti gli archi che formano
     *       il ciclo
     * @post Se restituisce false, non esiste alcun ciclo valido che parte da
     *       nodoIniziale
     * @post Lo stato del grafo rimane invariato durante l'esecuzione dell'algoritmo
     */
    private boolean dfs(Fruitore nodoIniziale, Fruitore nodoCorrente,
            Map<Fruitore, List<ArcoScambio>> grafo,
            Set<Fruitore> visitati, List<ArcoScambio> percorsoCorrente,
            Map<Fruitore, ArcoScambio> archiEntranti, Set<Fruitore> inPercorso) {

        visitati.add(nodoCorrente);
        inPercorso.add(nodoCorrente);

        // Esplora tutti gli archi uscenti dal nodo corrente
        List<ArcoScambio> archiUscenti = grafo.getOrDefault(nodoCorrente, Collections.emptyList());
        for (ArcoScambio arco : archiUscenti) {
            Fruitore prossimo = arco.getRicevente();

            // Ciclo trovato? Verifica che sia un ciclo che torna al nodo iniziale
            if (prossimo.equals(nodoIniziale) && !percorsoCorrente.isEmpty()) {
                // Registra l'ultimo arco che chiude il ciclo
                archiEntranti.put(prossimo, arco);
                return true;
            }

            // Se non abbiamo già visitato questo nodo nel percorso corrente
            if (!inPercorso.contains(prossimo)) {
                // Aggiungi l'arco al percorso corrente
                percorsoCorrente.add(arco);
                // Registra l'arco entrante per questo nodo
                archiEntranti.put(prossimo, arco);

                // Prosegui la ricerca DFS
                if (dfs(nodoIniziale, prossimo, grafo, visitati, percorsoCorrente, archiEntranti, inPercorso)) {
                    return true; // Ciclo trovato
                }

                // Backtracking: rimuovi l'arco dal percorso corrente
                percorsoCorrente.removeLast();
                archiEntranti.remove(prossimo);
            }
        }

        // Backtracking: rimuovi il nodo dal percorso corrente
        inPercorso.remove(nodoCorrente);
        return false; // Nessun ciclo trovato
    }

    /**
     * Visualizza gli insiemi chiusi per il configuratore.
     * <p>
     * Presenta un elenco di tutti gli insiemi chiusi presenti nel sistema,
     * permettendo al configuratore di:
     * <ul>
     * <li>Visualizzare i dettagli di ciascun insieme (fruitori, proposte e scambi
     * coinvolti)</li>
     * <li>Contrassegnare un insieme come notificato dopo aver contattato i
     * fruitori</li>
     * <li>Monitorare lo stato complessivo degli scambi nel sistema</li>
     * </ul>
     * <p>
     * Gli insiemi chiusi vengono visualizzati in ordine cronologico di creazione,
     * con indicazione dello stato di notifica.
     *
     * @pre this.database != null
     * @pre this.view != null
     * @post Se esistono insiemi chiusi, vengono visualizzati con i relativi
     *       dettagli
     * @post Se l'utente contrassegna un insieme come notificato, il suo stato viene
     *       aggiornato
     * @post Se non esistono insiemi chiusi, viene mostrato un messaggio informativo
     * @post Gli aggiornamenti allo stato di notifica vengono salvati in modo
     *       persistente
     */
    private void visualizzaInsiemiChiusi() {
        List<InsiemeChiuso> insiemi = database.getInsiemiChiusi();

        if (insiemi.isEmpty()) {
            view.showMessage(ControllerConstants.MSG_NESSUN_INSIEME_CHIUSO);
            return;
        }

        view.showMessage(ControllerConstants.TITLE_INSIEMI_CHIUSI);
        view.showWarningMessage(ControllerConstants.INFO_NOTIFICA_SIMBOLO);
        view.showWarningMessage(ControllerConstants.INFO_NON_NOTIFICA_SIMBOLO);
        for (int i = 0; i < insiemi.size(); i++) {
            InsiemeChiuso insieme = insiemi.get(i);
            view.showMessage(String.format(ControllerConstants.FORMATO_INSIEME_CHIUSO,
                    (i + 1),
                    insieme.isNotificato() ? ControllerConstants.SIMBOLO_NOTIFICATO
                            : ControllerConstants.SIMBOLO_NON_NOTIFICATO,
                    insieme.getNumeroScambi()));
        }

        int scelta = view.getIntInput(ControllerConstants.PROMPT_SELEZIONA_INSIEME_CHIUSO, 0, insiemi.size());

        if (scelta == 0)
            return;

        InsiemeChiuso selezionato = insiemi.get(scelta - 1);
        view.showMessage("\n" + selezionato.getDettagli());

        if (!selezionato.isNotificato()) {
            boolean conferma = view.getConfirmation(ControllerConstants.PROMPT_NOTIFICA_INSIEME);
            if (conferma) {
                selezionato.setNotificato(true);
                salvaTutti();
                view.showSuccessMessage(ControllerConstants.SUCCESS_INSIEME_NOTIFICATO);
            }
        }
    }

    // ==============================================================================
    // 8. GESTIONE PROPOSTE
    // ==============================================================================

    public void visualizzaProposteFruitore(Fruitore fruitore) {
        List<Proposta> proposte = this.database.getPropostePerFruitore(fruitore);

        if (proposte.isEmpty()) {
            this.view.showMessage(ControllerConstants.MSG_NESSUNA_PROPOSTA);
            return;
        }

        this.view.showMessage(ControllerConstants.TITLE_PROPOSTE_FRUITORE);
        for (Proposta proposta : proposte) {
            this.view.showMessage(proposta.toString());
        }
    }

    /**
     * Permette all'utente di selezionare una proposta aperta e modificarne lo
     * stato.
     * <p>
     * Mostra un elenco numerato di tutte le proposte aperte del fruitore
     * e acquisisce la scelta dell'utente. Consente anche di annullare l'operazione.
     *
     * @param fruitore il fruitore le cui proposte devono essere gestite
     * @pre fruitore != null
     * @post Se l'utente seleziona una proposta valida e conferma la modifica,
     *       lo stato della proposta viene aggiornato a RITIRATA
     */
    private void modificaStatoProposta(Fruitore fruitore) {
        // Recupera la lista delle proposte aperte disponibili
        List<Proposta> proposteAperte = this.database.getPropostePerFruitore(fruitore, StatoProposta.APERTA);

        // Verifica se ci sono proposte aperte disponibili
        if (proposteAperte.isEmpty()) {
            this.view.showMessage(ControllerConstants.MSG_NESSUNA_PROPOSTA_APERTA);
            return;
        }

        // Mostra il titolo della selezione
        this.view.showMessage(ControllerConstants.TITLE_MODIFICA_PROPOSTA);
        this.view.showMessage(ControllerConstants.INFO_SELEZIONE_PROPOSTA);

        // Visualizza l'elenco delle proposte in formato numerato
        for (int i = 0; i < proposteAperte.size(); i++) {
            this.view.showMessage("[" + (i + 1) + "]\n" + proposteAperte.get(i).toString());
        }

        // Richiedi la selezione dell'utente (0 per annullare, 1-N per selezionare)
        int index = this.view.getIntInput(ControllerConstants.PROMPT_SELEZIONA_PROPOSTA, 0, proposteAperte.size());

        // Validazione dell'input
        if (index < 0 || index > proposteAperte.size()) {
            this.view.showErrorMessage(ControllerConstants.ERROR_INDICE_NON_VALIDO);
            return;
        }

        // Gestione dell'annullamento dell'operazione
        if (index == 0) {
            this.view.showMessage(ControllerConstants.MSG_OPERAZIONE_ANNULLATA);
            return;
        }

        // Recupera la proposta selezionata
        Proposta propostaSelezionata = proposteAperte.get(index - 1);

        // Chiede conferma per ritirare la proposta
        boolean conferma = this.view.getConfirmation(ControllerConstants.PROMPT_CONFERMA_RITIRO);

        if (conferma) {
            // Modifica lo stato della proposta
            boolean risultato = propostaSelezionata.ritiraProposta();

            if (risultato) {
                this.view.showSuccessMessage(ControllerConstants.SUCCESS_PROPOSTA_RITIRATA);
                // Salvataggio delle proposte
                this.database.serializeProposte(ControllerConstants.PROPOSTE_FILE_PATH);
            } else {
                this.view.showErrorMessage(ControllerConstants.ERROR_MODIFICA_PROPOSTA);
            }
        } else {
            this.view.showMessage(ControllerConstants.MSG_OPERAZIONE_ANNULLATA);
        }
    }

    /**
     * Visualizza tutte le proposte di scambio relative a una specifica categoria
     * foglia.
     * <p>
     * Il metodo permette all'utente di selezionare una gerarchia e una categoria
     * foglia,
     * per poi visualizzare tutte le proposte di scambio (sia in richiesta che in
     * offerta)
     * che coinvolgono quella categoria.
     * <p>
     * Il processo include:
     * <ol>
     * <li>Selezione della gerarchia di interesse</li>
     * <li>Selezione della categoria foglia da monitorare</li>
     * <li>Visualizzazione delle proposte filtrate</li>
     * </ol>
     *
     * @pre database != null
     * @pre view != null
     * @post Se ci sono gerarchie e proposte, vengono visualizzate tutte quelle che
     *       coinvolgono
     *       la categoria selezionata
     * @post Lo stato delle proposte non viene modificato (operazione di sola
     *       lettura)
     */
    private void visualizzaProposteTotali() {
        // Mostra il titolo dell'operazione
        this.view.showMessage(ControllerConstants.TITLE_PROPOSTE_CATEGORIA);

        // Verifica che esistano gerarchie nel sistema
        if (!verificaPresenzaGerarchie()) {
            return;
        }

        // FASE 1: Selezione della gerarchia
        this.view.showMessage(ControllerConstants.MSG_LISTA_GERARCHIE);
        Gerarchia gerarchia = selezionaGerarchia();

        // Gestione dell'annullamento della selezione gerarchia
        if (gerarchia == null) {
            this.view.showMessage(ControllerConstants.MSG_OPERAZIONE_ANNULLATA);
            return;
        }

        // FASE 2: Selezione della categoria foglia
        this.view.showMessage(ControllerConstants.MSG_LISTA_FOGLIE);
        this.view.showMessage(gerarchia.toStringSelezioneFoglia());
        TreeNode<Categoria> foglia = selezionaFoglia(gerarchia);

        // Gestione dell'annullamento della selezione foglia
        if (foglia == null) {
            this.view.showMessage(ControllerConstants.MSG_OPERAZIONE_ANNULLATA);
            return;
        }

        // FASE 3: Ricerca e visualizzazione delle proposte corrispondenti
        this.view.showMessage(String.format(ControllerConstants.INFO_PROPOSTE_PER_CATEGORIA,
                foglia.data().getNome()));

        // Ottiene tutte le proposte dal database
        List<Proposta> tutteProposte = this.database.getPropostePerStato(null);
        boolean proposteFound = false;

        // Filtra e visualizza le proposte che coinvolgono la categoria selezionata
        for (Proposta proposta : tutteProposte) {
            // Controlla se la proposta richiede o offre la categoria selezionata
            if (proposta.getRichiesta().getCategoria().data().equals(foglia.data()) ||
                    proposta.getOfferta().getCategoria().data().equals(foglia.data())) {

                proposteFound = true;

                this.view.showMessage(proposta.toString());
            }
        }

        // Se non sono state trovate proposte, mostra un messaggio appropriato
        if (!proposteFound) {
            this.view.showMessage(
                    String.format(ControllerConstants.MSG_NESSUNA_PROPOSTA_CATEGORIA, foglia.data().getNome()));
        }
    }

    // ==============================================================================
    // 9. SUPPORTO INPUT UTENTE E LOGGING
    // ==============================================================================

    /**
     * Richiede e valida uno username per la registrazione.
     * <p>
     * Guida l'utente attraverso il processo di selezione di uno username valido,
     * verificando che rispetti i requisiti del sistema (lunghezza minima, caratteri
     * ammessi)
     * e che non sia già in uso da un altro utente registrato.
     *
     * @return lo username validato, o null se l'utente annulla l'operazione
     *
     * @pre this.database != null
     * @pre this.view != null
     * @post Se restituisce un valore non null, questo rappresenta uno username
     *       valido e disponibile
     * @post Se restituisce null, l'utente ha scelto di annullare l'operazione
     * @post Lo username restituito non è già associato a un utente esistente
     * @post Lo username restituito soddisfa tutti i requisiti di formattazione
     *       definiti nel sistema
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
     * Guida l'utente nella creazione di una password sicura, verificando che
     * soddisfi
     * i requisiti minimi di sicurezza definiti dal sistema (lunghezza, complessità,
     * ecc.).
     * Fornisce feedback appropriati in caso di password non conforme.
     *
     * @return la password validata, o null se l'utente annulla l'operazione
     *
     * @pre this.view != null
     * @post Se restituisce un valore non null, questo rappresenta una password che
     *       soddisfa i requisiti di sicurezza
     * @post Se restituisce null, l'utente ha scelto di annullare l'operazione
     * @post La password restituita soddisfa tutti i requisiti di sicurezza definiti
     *       nel sistema
     * @post La password non viene memorizzata in chiaro ma processata attraverso
     *       funzioni di hash appropriate
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
        this.view.showErrorMessage(logMessage.toString());

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
            this.view.showErrorMessage("Impossibile scrivere nel file di log: " + loggingError.getMessage());
        }
    }
}