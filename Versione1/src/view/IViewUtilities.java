package view;

/**
 * Interfaccia per le funzionalità utility della view.
 * <p>
 * PRINCIPIO: Interface Segregation Principle (ISP)
 * Questa interfaccia contiene metodi utility opzionali che non sono essenziali
 * per le operazioni di base di input/output.
 * <p>
 * Le classi di test possono ignorare questa interfaccia se non necessaria,
 * riducendo il codice boilerplate nei mock.
 *
 * @see IInputProvider
 * @see IOutputProvider
 * @see IView
 */
public interface IViewUtilities {

    /**
     * Attende che l'utente prema INVIO per continuare.
     */
    void pressEnterToContinue();

    /**
     * Chiude le risorse della view (es. Scanner).
     */
    void close();
}
