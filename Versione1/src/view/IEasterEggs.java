package view;

/**
 * Interfaccia per le funzionalità "Easter Eggs" della view.
 * <p>
 * PRINCIPIO: Interface Segregation Principle (ISP)
 * Questa interfaccia isola le funzionalità speciali/divertenti che non sono
 * necessarie per il funzionamento dell'applicazione.
 * <p>
 * MockView e altre implementazioni di test NON devono implementare questa
 * interfaccia,
 * evitando di dover fornire implementazioni vuote per metodi non utilizzati.
 *
 * @see IView
 */
public interface IEasterEggs {

    /**
     * Mostra l'ASCII art di Shrek.
     */
    void displayShrek();

    /**
     * Mostra l'ASCII art di Among Us.
     */
    void displayAmongUs();
}
