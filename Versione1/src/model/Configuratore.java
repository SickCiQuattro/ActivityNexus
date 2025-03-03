package model;

import java.io.Serial;

/**
 * Utente con privilegi amministrativi nel sistema.
 * <p>
 * Responsabilità principali:
 * <ul>
 * <li>Gestione comprensori geografici</li>
 * <li>Definizione gerarchie di categorie</li>
 * <li>Configurazione fattori di conversione</li>
 * </ul>
 * 
 * @see Utente
 * @see Fruitore
 */
public class Configuratore extends Utente {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Costruisce un nuovo Configuratore con username e password specificati.
     *
     * @param username lo username del Configuratore (non null)
     * @param password la password del Configuratore (non null)
     * @throws NullPointerException se username o password sono null
     */
    public Configuratore(String username, String password) {
        // Tutti i Configuratori hanno sempre isConfig=true
        super(username, password, true);
    }

    /**
     * Verifica se l'utente ha permessi di amministrazione.
     * <p>
     * Per un Configuratore, questo metodo restituisce sempre true.
     *
     * @return true (sempre, per definizione di Configuratore)
     * @see Utente#isConfig()
     */
    @Override
    public boolean isConfig() {
        return true; // Sempre true per un Configuratore
    }

    /**
     * Restituisce una rappresentazione testuale del Configuratore.
     *
     * @return stringa che rappresenta il Configuratore
     */
    @Override
    public String toString() {
        return "Configuratore: " + super.toString();
    }

    /**
     * Verifica se questo Configuratore può gestire la risorsa specificata.
     * <p>
     * I Configuratori hanno accesso a tutte le risorse amministrative.
     *
     * @param risorsa identificatore della risorsa da verificare
     * @return true se il Configuratore ha accesso alla risorsa
     */
    public boolean puoGestireRisorsa(String risorsa) {
        // I configuratori hanno accesso a tutte le risorse
        return true;
    }
}