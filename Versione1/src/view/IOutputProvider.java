package view;

/**
 * Interfaccia per la gestione degli output verso l'utente.
 * <p>
 * PRINCIPIO: Interface Segregation Principle (ISP)
 * Questa interfaccia contiene solo i metodi relativi alla visualizzazione di
 * messaggi,
 * permettendo ai client di dipendere solo dalle funzionalità di output che
 * utilizzano.
 * <p>
 * I client che necessitano solo di mostrare messaggi possono dipendere da
 * questa
 * interfaccia invece che dall'intera IView, riducendo l'accoppiamento.
 *
 * @see IInputProvider
 * @see IMenuProvider
 * @see IView
 */
public interface IOutputProvider {

    /**
     * Mostra un messaggio generico all'utente.
     *
     * @param message il messaggio da visualizzare
     */
    void showMessage(String message);

    /**
     * Mostra un messaggio con un ritardo specificato.
     *
     * @param message il messaggio da visualizzare
     * @param delay   il ritardo in millisecondi
     */
    void showMessageWithDelay(String message, int delay);

    /**
     * Mostra un messaggio di errore all'utente.
     *
     * @param message il messaggio di errore
     */
    void showErrorMessage(String message);

    /**
     * Mostra un messaggio di errore senza attendere conferma.
     *
     * @param message il messaggio di errore
     */
    void showErrorMessageNoWait(String message);

    /**
     * Mostra un messaggio di successo all'utente.
     *
     * @param message il messaggio di successo
     */
    void showSuccessMessage(String message);

    /**
     * Mostra un messaggio di successo senza attendere conferma.
     *
     * @param message il messaggio di successo
     */
    void showSuccessMessageNoWait(String message);

    /**
     * Mostra un messaggio informativo all'utente.
     *
     * @param message il messaggio informativo
     */
    void showInfoMessage(String message);

    /**
     * Mostra un messaggio di avviso all'utente.
     *
     * @param message il messaggio di avviso
     */
    void showWarningMessage(String message);
}
