import controller.UserController;
import view.ConsoleView;

/**
 * Classe principale del programma.
 * Avvia l'applicazione creando una vista console e un controller utente.
 */
public class Main {
    /**
     * Metodo principale che avvia l'applicazione.
     *
     * @param args argomenti della riga di comando (non utilizzati)
     */
    public static void main(String[] args) {
        ConsoleView view = new ConsoleView();
        UserController controller = new UserController(view);

        controller.start();
    }
}