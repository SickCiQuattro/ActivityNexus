package model;

import java.io.Serializable;

/**
 * Rappresenta un utente con privilegi amministrativi nel sistema.
 * <p>
 * Un Configuratore può:
 * <ul>
 * <li>Gestire comprensori geografici</li>
 * <li>Definire gerarchie di categorie</li>
 * <li>Configurare fattori di conversione</li>
 * <li>Impostare parametri di sistema</li>
 * </ul>
 * <p>
 * Ha sempre il flag {@code isConfig=true}, garantendo accesso alle funzionalità
 * amministrative non disponibili ai fruitori comuni.
 * 
 * @see Utente
 * @see Fruitore
 */
public class Configuratore extends Utente implements Serializable {
    // Identificatore di versione per la serializzazione
    // Importante mantenere questa compatibilità quando si modificano le classi
    // serializzabili
    private static final long serialVersionUID = 1L;

    // -----------------------------------------------------------------------------------
    // COSTRUTTORI
    // -----------------------------------------------------------------------------------

    /**
     * Costruisce un nuovo Configuratore con le credenziali specificate.
     * <p>
     * Inizializza l'utente con privilegi amministrativi, impostando
     * automaticamente il flag {@code isConfig} a {@code true}.
     *
     * @param username lo username del Configuratore
     * @param password la password del Configuratore
     * @throws NullPointerException se username o password sono null
     * 
     * @see Utente#Utente(String, String, boolean)
     */
    public Configuratore(String username, String password) {
        // Invoca il costruttore della superclasse Utente con il flag isConfig=true
        // Questo garantisce che ogni Configuratore abbia sempre i privilegi di
        // amministrazione
        super(username, password, true);
    }

    // -----------------------------------------------------------------------------------
    // OVERRIDE DI METODI EREDITATI
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce una rappresentazione testuale del Configuratore.
     * <p>
     * Aggiunge il prefisso "Configuratore: " alla rappresentazione base dell'utente
     * per facilitare l'identificazione del tipo nei report e nelle liste.
     *
     * @return stringa che rappresenta il Configuratore
     * @see Utente#toString()
     */
    @Override
    public String toString() {
        // Aggiunge il prefisso "Configuratore: " al risultato del toString() del padre
        // per identificare chiaramente il tipo di utente
        return "Configuratore: " + super.toString();
    }

    // Nota: questa classe non sovrascrive equals() e hashCode() poiché si affida
    // all'implementazione della classe padre Utente, che confronta gli utenti
    // in base al loro username, indipendentemente dal tipo specifico
}