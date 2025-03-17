package model;

import java.io.Serial;

/**
 * Rappresenta un utente amministratore con privilegi avanzati nel sistema.
 * <p>
 * Estende la classe {@link Utente} specializzandola per offrire funzionalità
 * di configurazione e amministrazione del sistema. Un Configuratore ha sempre
 * il flag {@code isConfig} impostato a {@code true}, che garantisce l'accesso
 * a funzionalità privilegiate.
 * <p>
 * <b>Responsabilità principali:</b>
 * <ul>
 * <li>Gestione dei comprensori geografici (creazione, modifica)</li>
 * <li>Definizione delle gerarchie di categorie (struttura, campi
 * caratteristici)</li>
 * <li>Configurazione dei fattori di conversione tra categorie</li>
 * <li>Monitoraggio e notifica degli insiemi chiusi di scambio</li>
 * </ul>
 *
 * <p>
 * <b>Invarianti della classe:</b>
 * <ul>
 * <li>{@code isConfig()} restituisce sempre {@code true}</li>
 * <li>Mantiene tutte le invarianti ereditate da {@link Utente}</li>
 * </ul>
 *
 * @see Utente
 * @see Fruitore
 */
public class Configuratore extends Utente {
    // -----------------------------------------------------------------------------------
    // FIELDS E COSTANTI
    // -----------------------------------------------------------------------------------

    /** Identificatore di versione per la serializzazione */
    @Serial
    private static final long serialVersionUID = 1L;

    // -----------------------------------------------------------------------------------
    // COSTRUTTORI
    // -----------------------------------------------------------------------------------

    /**
     * Costruisce un nuovo Configuratore con le credenziali specificate.
     * <p>
     * Invoca il costruttore della superclasse {@link Utente} con il flag
     * {@code isConfig}
     * impostato a {@code true} per garantire i privilegi di amministrazione.
     *
     * @param username lo username del Configuratore
     * @param password la password del Configuratore
     * @throws NullPointerException se username o password sono null
     *
     * @pre username != null
     * @pre password != null
     * @post getUsername().equals(username)
     * @post authenticate(password) == true
     * @post isConfig() == true
     */
    public Configuratore(String username, String password) {
        // Invoca il costruttore della superclasse con isConfig=true per garantire
        // i privilegi di amministrazione a tutti i Configuratori
        super(username, password, true);
    }

    // -----------------------------------------------------------------------------------
    // OVERRIDE DI METODI EREDITATI
    // -----------------------------------------------------------------------------------

    /**
     * Verifica se l'utente ha privilegi di amministrazione.
     * <p>
     * Questo metodo sovrascrive {@link Utente#isConfig()} per garantire
     * che un Configuratore abbia sempre privilegi amministrativi.
     *
     * @return true (sempre, per definizione di Configuratore)
     * 
     * @post result == true
     */
    @Override
    public boolean isConfig() {
        // Per definizione, un Configuratore ha sempre privilegi di amministrazione
        return true;
    }

    /**
     * Restituisce una rappresentazione testuale del Configuratore.
     * <p>
     * Aggiunge il prefisso "Configuratore: " alla rappresentazione base
     * dell'utente.
     *
     * @return stringa che rappresenta il Configuratore
     *
     * @post result != null
     * @post result.startsWith("Configuratore: ")
     * @post result contiene tutte le informazioni della rappresentazione di Utente
     */
    @Override
    public String toString() {
        // Aggiunge il prefisso "Configuratore: " alla rappresentazione base
        return "Configuratore: " + super.toString();
    }
}