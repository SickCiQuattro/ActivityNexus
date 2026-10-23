package view;

/**
 * Interfaccia per la gestione dei menu dell'applicazione.
 * <p>
 * PRINCIPIO: Interface Segregation Principle (ISP)
 * Questa interfaccia contiene solo i metodi relativi alla visualizzazione dei
 * menu,
 * permettendo ai client di dipendere solo dalle funzionalità di menu che
 * utilizzano.
 * <p>
 * Le classi di test (MockView) possono evitare di implementare questi metodi
 * se non sono necessari per i test, implementando solo IInputProvider e
 * IOutputProvider.
 *
 * @see IInputProvider
 * @see IOutputProvider
 * @see IView
 */
public interface IMenuProvider {

    /**
     * Mostra il menu principale dell'applicazione.
     */
    void showMenu();

    /**
     * Mostra il menu specifico per gli utenti Configuratore.
     */
    void showMenuConfiguratore();

    /**
     * Mostra il menu specifico per gli utenti Fruitore.
     */
    void showMenuFruitore();
}
