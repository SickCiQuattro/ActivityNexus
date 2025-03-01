package model;

import java.io.Serializable;

/**
 * Classe che rappresenta un utente Configuratore nel sistema.
 * <p>
 * Un Configuratore è un tipo specializzato di utente con privilegi di
 * amministrazione
 * che gli permettono di modificare e configurare gli elementi strutturali del
 * sistema.
 * La classe eredita da {@link Utente} e rappresenta il ruolo con i massimi
 * privilegi
 * nell'applicazione.
 * <p>
 * Responsabilità specifiche del Configuratore:
 * <ul>
 * <li>Creazione e gestione di comprensori geografici</li>
 * <li>Definizione delle gerarchie di categorizzazione delle attività</li>
 * <li>Configurazione dei fattori di conversione tra categorie</li>
 * <li>Impostazione dei parametri di sistema</li>
 * </ul>
 * <p>
 * Il Configuratore ha sempre il flag {@code isConfig} impostato a {@code true},
 * che gli
 * conferisce accesso alle funzionalità di amministrazione non disponibili ai
 * fruitori
 * comuni del sistema. Questa distinzione è fondamentale per la sicurezza e il
 * controllo
 * degli accessi all'interno dell'applicazione.
 * <p>
 * La classe implementa {@link Serializable} per consentire la persistenza degli
 * oggetti
 * Configuratore insieme al resto del modello dati.
 * 
 * @see Utente
 * @see Fruitore
 * @see java.io.Serializable
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
     * Costruisce un nuovo Configuratore con lo username e la password specificati.
     * <p>
     * Questo costruttore crea un'istanza di Configuratore invocando il costruttore
     * della
     * superclasse {@link Utente} e impostando automaticamente il flag
     * {@code isConfig} a
     * {@code true}. Questo garantisce che l'utente creato abbia sempre i privilegi
     * di
     * configurazione, indipendentemente da come viene istanziato.
     * <p>
     * La validazione dei parametri (controllo di null) viene delegata al
     * costruttore della classe
     * padre, che genererà eccezioni appropriate in caso di parametri non validi.
     * <p>
     * Esempio di utilizzo:
     * 
     * <pre>
     * Configuratore admin = new Configuratore("admin", "password123");
     * </pre>
     *
     * @param username lo username del Configuratore (identificatore univoco)
     * @param password la password del Configuratore per l'autenticazione
     * @throws NullPointerException se username o password sono null (lanciata dal
     *                              costruttore padre)
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
     * Questo metodo estende il comportamento della superclasse
     * {@link Utente#toString()},
     * aggiungendo un prefisso che identifica esplicitamente questo utente come un
     * Configuratore.
     * Il formato della stringa risultante sarà:
     * 
     * <pre>
     * "Configuratore: [rappresentazione dell'utente]"
     * </pre>
     * <p>
     * Questa distinzione è utile quando si visualizzano liste di utenti di diverso
     * tipo,
     * permettendo di identificare immediatamente i Configuratori.
     *
     * @return stringa che rappresenta il Configuratore, includendo il suo tipo e i
     *         dettagli base
     * 
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