package model;

/**
 * Classe che rappresenta un utente Configuratore nel sistema.
 * Un Configuratore è un tipo speciale di utente con permessi aggiuntivi.
 */
public class Configuratore extends Utente {

    /**
     * Costruisce un Configuratore con lo username e la password specificati.
     * Il flag isConfig è impostato a true di default.
     *
     * @param username lo username del Configuratore
     * @param password la password del Configuratore
     */
    public Configuratore(String username, String password) {
        super(username, password, true);
    }
}