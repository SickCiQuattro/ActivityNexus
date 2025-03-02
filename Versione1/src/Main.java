import controller.Controller;
import view.ConsoleView;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe principale dell'applicazione "ActivityNexus".
 * Inizializza i componenti fondamentali dell'applicazione e avvia il ciclo di
 * esecuzione.
 * <p>
 * L'applicazione segue il pattern MVC (Model-View-Controller), dove:
 * - Model: gestito internamente dal Controller attraverso la classe Database
 * - View: rappresentata da ConsoleView per l'interfaccia utente a console
 * - Controller: coordina le interazioni tra Model e View
 */
public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    /**
     * Punto di ingresso principale dell'applicazione.
     * Inizializza la vista e il controller, poi avvia l'esecuzione del programma.
     * 
     * @param args argomenti della riga di comando (non utilizzati nell'applicazione
     *             attuale)
     */
    public static void main(String[] args) {
        ConsoleView view = null;

        try {
            // Inizializzazione della vista (interfaccia utente a console)
            view = new ConsoleView();

            // Creazione del controller che gestisce la logica applicativa
            Controller controller = new Controller(view);

            // Avvio del ciclo principale dell'applicazione
            controller.start();

        } catch (IllegalStateException e) {
            // Gestione errori di stato dell'applicazione
            registraErrore("Errore di stato dell'applicazione", e);
            mostraErrore(view, "Errore di configurazione: " + e.getMessage());
            System.exit(2);

        } catch (Exception e) {
            // Gestione centralizzata delle eccezioni non gestite
            registraErrore("Errore imprevisto durante l'esecuzione dell'applicazione", e);
            mostraErrore(view, "Si è verificato un errore imprevisto");
            System.exit(1);
        }
    }

    /**
     * Registra un errore nel log di sistema.
     * 
     * @param messaggio descrizione dell'errore
     * @param e         l'eccezione da registrare
     */
    private static void registraErrore(String messaggio, Exception e) {
        LOGGER.log(Level.SEVERE, messaggio, e);
    }

    /**
     * Mostra un messaggio di errore all'utente se la vista è disponibile.
     * 
     * @param view      il componente UI, può essere null
     * @param messaggio il messaggio di errore
     */
    private static void mostraErrore(ConsoleView view, String messaggio) {
        if (view != null) {
            view.showErrorMessage(messaggio);
        } else {
            System.err.println(messaggio);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.severe(messaggio);
            }
        }
    }
}