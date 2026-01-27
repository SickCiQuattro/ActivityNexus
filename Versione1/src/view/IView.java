package view;

/**
 * Interfaccia completa per la View dell'applicazione.
 * <p>
 * PRINCIPIO: Interface Segregation Principle (ISP)
 * Questa interfaccia è composta da interfacce più piccole e coese:
 * <ul>
 * <li>{@link IInputProvider} - Metodi per acquisire input dall'utente</li>
 * <li>{@link IOutputProvider} - Metodi per mostrare messaggi all'utente</li>
 * <li>{@link IMenuProvider} - Metodi per visualizzare i menu</li>
 * <li>{@link IViewUtilities} - Metodi utility opzionali</li>
 * <li>{@link IEasterEggs} - Funzionalità speciali/divertenti</li>
 * </ul>
 * <p>
 * <b>Vantaggi dell'ISP applicato:</b>
 * <ul>
 * <li>Le classi client possono dipendere solo dalle interfacce che usano</li>
 * <li>MockView per i test può implementare solo IInputProvider e IOutputProvider</li>
 * <li>Riduzione dell'accoppiamento e del codice boilerplate</li>
 * <li>Maggiore flessibilità nell'aggiungere nuove implementazioni</li>
 * </ul>
 *
 * @see IInputProvider
 * @see IOutputProvider
 * @see IMenuProvider
 * @see IViewUtilities
 * @see IEasterEggs
 * @see ConsoleView
 */
public interface IView extends IInputProvider, IOutputProvider, IMenuProvider, IViewUtilities, IEasterEggs {
    // L'interfaccia IView eredita tutti i metodi dalle interfacce segregate.
    // Non sono necessari metodi aggiuntivi qui.
    //
    // PRIMA (violazione ISP):
    // - 19 metodi in un'unica interfaccia monolitica
    // - MockView doveva implementare TUTTI i metodi (anche displayShrek!)
    //
    // DOPO (ISP applicato):
    // - 5 interfacce piccole e coese
    // - MockView può implementare solo IInputProvider + IOutputProvider
    // - Ogni interfaccia ha una singola responsabilità
}