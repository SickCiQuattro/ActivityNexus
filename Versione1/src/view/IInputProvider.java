package view;

/**
 * Interfaccia per la gestione degli input utente.
 * <p>
 * PRINCIPIO: Interface Segregation Principle (ISP)
 * Questa interfaccia contiene solo i metodi relativi all'acquisizione di input,
 * permettendo ai client di dipendere solo dalle funzionalità che effettivamente
 * utilizzano.
 * <p>
 * I client che necessitano solo di ricevere input dall'utente possono dipendere
 * da questa interfaccia invece che dall'intera IView, riducendo
 * l'accoppiamento.
 *
 * @see IOutputProvider
 * @see IMenuProvider
 * @see IView
 */
public interface IInputProvider {

    /**
     * Richiede un input testuale all'utente.
     *
     * @param prompt il messaggio da mostrare come richiesta
     * @return la stringa inserita dall'utente
     */
    String getInput(String prompt);

    /**
     * Richiede una password all'utente (input mascherato).
     *
     * @param prompt il messaggio da mostrare come richiesta
     * @return la password inserita dall'utente
     */
    String getPasswordInput(String prompt);

    /**
     * Richiede una conferma (sì/no) all'utente.
     *
     * @param prompt il messaggio di conferma
     * @return true se l'utente conferma, false altrimenti
     */
    boolean getConfirmation(String prompt);

    /**
     * Richiede un numero intero all'utente entro un range specificato.
     *
     * @param prompt il messaggio da mostrare come richiesta
     * @param min    il valore minimo accettabile
     * @param max    il valore massimo accettabile
     * @return il numero intero inserito dall'utente
     */
    int getIntInput(String prompt, int min, int max);

    /**
     * Richiede un numero decimale all'utente entro un range specificato.
     *
     * @param prompt il messaggio da mostrare come richiesta
     * @param min    il valore minimo accettabile
     * @param max    il valore massimo accettabile
     * @return il numero decimale inserito dall'utente
     */
    double getDoubleInput(String prompt, double min, double max);
}
