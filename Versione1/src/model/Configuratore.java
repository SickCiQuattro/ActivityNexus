package model;

import java.io.Serial;

/**
 * Utente con privilegi amministrativi nel sistema.
 * <p>
 * Estende la classe {@link Utente} e rappresenta un amministratore con diritti
 * speciali per la configurazione e la gestione del sistema.
 * <p>
 * Responsabilità principali:
 * <ul>
 * <li>Gestione comprensori geografici</li>
 * <li>Definizione gerarchie di categorie</li>
 * <li>Configurazione fattori di conversione</li>
 * </ul>
 *
 * <p><b>Invarianti:</b>
 * <ul>
 *   <li>isConfig() restituisce sempre true</li>
 *   <li>Tutte le invarianti ereditate da {@link Utente}</li>
 * </ul>
 *
 * @see Utente
 */
public class Configuratore extends Utente {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Costruisce un nuovo Configuratore con username e password specificati.
     *
     * @param username lo username del Configuratore
     * @param password la password del Configuratore
     * @throws NullPointerException se username o password sono null
     *
     * @pre username != null
     * @pre password != null
     * @post getUsername() restituisce lo username fornito
     * @post password viene impostata come specificato (verificabile con authenticate())
     * @post isConfig() restituisce true
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
     *
     * @post il valore restituito è true
     */
    @Override
    public boolean isConfig() {
        return true; // Sempre true per un Configuratore
    }

    /**
     * Restituisce una rappresentazione testuale del Configuratore.
     *
     * @return stringa che rappresenta il Configuratore
     *
     * @post il valore restituito non è null
     * @post il valore restituito contiene "Configuratore" e la rappresentazione base dell'utente
     */
    @Override
    public String toString() {
        return "Configuratore: " + super.toString();
    }
}