import controller.Controller;
import view.ConsoleView;

/**
 * Classe principale dell'applicazione "Decidiamo un nome dai".
 * Inizializza i componenti fondamentali dell'applicazione e avvia il ciclo di
 * esecuzione.
 * L'applicazione segue il pattern MVC (Model-View-Controller), dove:
 * - Model: gestito internamente dal Controller attraverso la classe Database
 * - View: rappresentata da ConsoleView per l'interfaccia utente a console
 * - Controller: coordina le interazioni tra Model e View
 */
public class Main {

    /**
     * Punto di ingresso principale dell'applicazione.
     * Inizializza la vista e il controller, poi avvia l'esecuzione del programma.
     * 
     * @param args argomenti della riga di comando (non utilizzati nell'applicazione
     *             attuale)
     */
    public static void main(String[] args) {
        try {
            // Inizializzazione della vista (interfaccia utente a console)
            ConsoleView view = new ConsoleView();

            // Creazione del controller che gestisce la logica applicativa
            Controller controller = new Controller(view);

            // Avvio del ciclo principale dell'applicazione
            controller.start();

        } catch (Exception e) {
            // Gestione centralizzata delle eccezioni non gestite
            System.err.println("Errore imprevisto durante l'esecuzione dell'applicazione:");
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}