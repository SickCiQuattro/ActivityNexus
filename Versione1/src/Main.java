import controller.Controller;
import view.ConsoleView;
import view.IView;
import model.Database;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe principale dell'applicazione "ActivityNexus".
 * <p>
 * Responsabile dell'inizializzazione dei componenti fondamentali
 * dell'applicazione,
 * dell'avvio del ciclo di esecuzione e della gestione centralizzata degli
 * errori.
 * <p>
 * L'applicazione segue il pattern architetturale MVC (Model-View-Controller):
 * <ul>
 * <li><b>Model:</b> gestito internamente dal Controller attraverso la classe
 * Database</li>
 * <li><b>View:</b> rappresentata da ConsoleView per l'interfaccia utente
 * testuale</li>
 * <li><b>Controller:</b> coordina le interazioni tra Model e View</li>
 * </ul>
 * <p>
 * Il flusso di esecuzione principale consiste in:
 * <ol>
 * <li>Inizializzazione della view</li>
 * <li>Creazione del controller</li>
 * <li>Avvio del ciclo principale dell'applicazione tramite controller</li>
 * </ol>
 * <p>
 * La classe implementa anche un sistema di gestione centralizzata delle
 * eccezioni
 * con logging e notifiche appropriate all'utente.
 *
 * @see Controller
 * @see ConsoleView
 */
public class Main {
    // -----------------------------------------------------------------------------------
    // COSTANTI E CAMPI STATICI
    // -----------------------------------------------------------------------------------

    /** Logger per la registrazione degli errori e degli eventi dell'applicazione */
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    /** Codice di uscita per errori imprevisti */
    private static final int EXIT_CODE_UNEXPECTED_ERROR = 1;

    /** Codice di uscita per errori di configurazione */
    private static final int EXIT_CODE_CONFIG_ERROR = 2;

    // -----------------------------------------------------------------------------------
    // PUNTO DI INGRESSO PRINCIPALE
    // -----------------------------------------------------------------------------------

    /**
     * Punto di ingresso principale dell'applicazione.
     * <p>
     * Inizializza la vista e il controller, avvia l'esecuzione del programma
     * e gestisce eventuali eccezioni non catturate altrove.
     * 
     * @param args argomenti della riga di comando (non utilizzati nell'applicazione
     *             attuale)
     *
     * @pre true (nessuna precondizione particolare)
     * @post L'applicazione è stata eseguita correttamente o è stata terminata con
     *       un codice
     *       di errore appropriato dopo aver registrato l'eccezione
     */
    public static void main(String[] args) {
        IView view = null; // Dichiara view fuori dal try per usarla nei catch

        try {
            // Fase 1: Inizializzazione della vista
            view = new ConsoleView();

            // Fase 2: Inizializzazione del modello
            Database database = new Database();

            // Fase 3: Creazione del controller e INIEZIONE delle dipendenze
            Controller controller = new Controller(view, database);

            // Fase 4: Avvio
            controller.start();

        } catch (IllegalStateException e) {
            // Gestione specifica per errori di configurazione o stato non valido
            // Questi errori indicano problemi nella configurazione dell'applicazione
            registraErrore("Errore di stato dell'applicazione", e);
            mostraErrore(view, "Errore di configurazione: " + e.getMessage());
            System.exit(EXIT_CODE_CONFIG_ERROR);

        } catch (Exception e) {
            // Gestione centralizzata per tutte le altre eccezioni non gestite
            // Questo è l'ultimo livello di difesa per evitare che l'applicazione termini
            // senza tracce
            registraErrore("Errore imprevisto durante l'esecuzione dell'applicazione", e);
            mostraErrore(view, "Si è verificato un errore imprevisto");
            System.exit(EXIT_CODE_UNEXPECTED_ERROR);
        }
    }

    // -----------------------------------------------------------------------------------
    // METODI DI SUPPORTO PER LA GESTIONE DEGLI ERRORI
    // -----------------------------------------------------------------------------------

    /**
     * Registra un errore nel log di sistema.
     * <p>
     * Utilizza il logger configurato per salvare i dettagli dell'errore,
     * incluso lo stack trace completo.
     * 
     * @param messaggio descrizione dell'errore che specifica il contesto
     * @param e         l'eccezione da registrare con i dettagli tecnici
     *
     * @pre messaggio != null
     * @pre e != null
     * @post L'errore è stato registrato nel log con livello SEVERE
     */
    private static void registraErrore(String messaggio, Exception e) {
        // Registra l'errore con livello SEVERE includendo il messaggio di contesto
        // e lo stack trace completo dell'eccezione per facilitare il debug
        LOGGER.log(Level.SEVERE, messaggio, e);
    }

    /**
     * Mostra un messaggio di errore all'utente usando il canale più appropriato.
     * <p>
     * Se la vista è disponibile, usa il suo meccanismo di visualizzazione degli
     * errori.
     * Altrimenti, usa System.err e registra anche nel log di sistema.
     * 
     * @param view      il componente UI, può essere null se non ancora
     *                  inizializzato
     * @param messaggio il messaggio di errore da mostrare all'utente
     *
     * @pre messaggio != null
     * @post Il messaggio è stato mostrato all'utente attraverso almeno un canale
     */
    private static void mostraErrore(IView view, String messaggio) {
        if (view != null) {
            // Se la vista è stata inizializzata, usa il suo meccanismo per mostrare errori
            // Questo garantisce coerenza con lo stile dell'interfaccia utente
            view.showErrorMessage(messaggio);
        } else {
            // Fallback nel caso la vista non sia stata inizializzata
            // Usa lo stream di errore standard e registra anche nel log
            System.err.println(messaggio);

            // Verifica se il logger è configurato per il livello SEVERE
            // per evitare operazioni di logging inutili
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.severe(messaggio);
            }
        }
    }
}