package model;

import java.io.Serializable;

/**
 * Classe che rappresenta un utente Configuratore nel sistema.
 * Un Configuratore è un tipo specifico di utente che ha privilegi di
 * amministrazione
 * per configurare il sistema, creare gerarchie e gestire altri aspetti del
 * sistema.
 * 
 * Il Configuratore ha sempre il flag isConfig impostato a true, che gli
 * conferisce
 * i permessi aggiuntivi rispetto a un normale Utente.
 */
public class Configuratore extends Utente implements Serializable {
    private static final long serialVersionUID = 1L;

    // -----------------------------------------------------------------------------------
    // COSTRUTTORI
    // -----------------------------------------------------------------------------------

    /**
     * Costruisce un nuovo Configuratore con lo username e la password specificati.
     * Il flag isConfig viene automaticamente impostato a true per garantire i
     * privilegi
     * di configurazione del sistema.
     *
     * @param username lo username del Configuratore (identificatore univoco)
     * @param password la password del Configuratore per l'autenticazione
     * @throws NullPointerException se username o password sono null
     */
    public Configuratore(String username, String password) {
        super(username, password, true);
    }

    // -----------------------------------------------------------------------------------
    // OVERRIDE DI METODI EREDITATI
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce una rappresentazione testuale del Configuratore.
     * Include l'informazione che si tratta di un utente con privilegi di
     * configuratore.
     *
     * @return stringa che rappresenta il Configuratore
     */
    @Override
    public String toString() {
        return "Configuratore: " + super.toString();
    }
}