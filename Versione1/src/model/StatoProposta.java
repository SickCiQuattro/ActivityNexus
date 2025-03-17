package model;

import java.io.Serial;
import java.io.Serializable;

/**
 * Rappresenta i possibili stati di una proposta di scambio nel sistema.
 * <p>
 * Una proposta attraversa diversi stati durante il suo ciclo di vita, con
 * regole
 * specifiche per le transizioni permesse:
 * <ul>
 * <li><b>APERTA</b>: la proposta è disponibile per essere accettata e può
 * essere
 * inclusa in un insieme chiuso</li>
 * <li><b>CHIUSA</b>: la proposta è stata accettata e inserita in un insieme
 * chiuso</li>
 * <li><b>RITIRATA</b>: la proposta è stata ritirata dal propositore e non è più
 * disponibile</li>
 * </ul>
 * <p>
 * <b>Macchina a stati:</b>
 * <ul>
 * <li>Stato iniziale: APERTA</li>
 * <li>Transizioni permesse: APERTA → CHIUSA, APERTA → RITIRATA</li>
 * <li>Stati terminali: CHIUSA, RITIRATA</li>
 * </ul>
 * <p>
 * Questa enumerazione implementa {@link Serializable} per consentire la
 * persistenza
 * degli oggetti che la contengono.
 * 
 * @see Proposta
 * @see InsiemeChiuso
 */
public enum StatoProposta implements Serializable {
    // -----------------------------------------------------------------------------------
    // VALORI DELL'ENUMERAZIONE
    // -----------------------------------------------------------------------------------

    /**
     * Proposta disponibile per l'accettazione.
     * <p>
     * Questo è lo stato iniziale di ogni proposta. Solo le proposte in questo stato
     * possono essere incluse in insiemi chiusi o ritirate dal propositore.
     */
    APERTA("APERTA"),

    /**
     * Proposta accettata e inserita in un insieme chiuso.
     * <p>
     * Una volta chiusa, la proposta non può tornare allo stato APERTA né essere
     * ritirata. Questo è uno stato terminale.
     */
    CHIUSA("ACCETTATA"),

    /**
     * Proposta ritirata dal propositore.
     * <p>
     * Una proposta ritirata non è più disponibile per scambi. Questo è uno stato
     * terminale.
     */
    RITIRATA("RITIRATA");

    // -----------------------------------------------------------------------------------
    // CAMPI E COSTRUTTORI
    // -----------------------------------------------------------------------------------

    /**
     * Identificatore di versione per la serializzazione.
     * Anche se enum sono implicitamente serializzabili, è buona pratica
     * definire esplicitamente la versione.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /** Descrizione testuale dello stato, usata per la visualizzazione */
    private final String descrizione;

    /**
     * Costruisce un nuovo stato con la descrizione specificata.
     * <p>
     * Il costruttore è privato come per tutte le enumerazioni Java.
     *
     * @param descrizione descrizione testuale dello stato per visualizzazione
     *
     * @pre descrizione != null
     * @post getDescrizione().equals(descrizione)
     */
    StatoProposta(String descrizione) {
        // In una enum, è garantito che i parametri siano validi durante la definizione,
        // quindi non sono necessari controlli di validità espliciti
        this.descrizione = descrizione;
    }

    // -----------------------------------------------------------------------------------
    // METODI DI ACCESSO (GETTERS)
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce la descrizione testuale di questo stato.
     * <p>
     * La descrizione è una rappresentazione user-friendly dello stato,
     * utile per l'interfaccia utente.
     *
     * @return la descrizione dello stato (mai null)
     *
     * @post result != null
     */
    public String getDescrizione() {
        return descrizione;
    }

    // -----------------------------------------------------------------------------------
    // METODI DI VALIDAZIONE DELLE TRANSIZIONI
    // -----------------------------------------------------------------------------------

    /**
     * Verifica se da questo stato è possibile passare allo stato specificato.
     * <p>
     * Definisce le transizioni di stato valide secondo le regole di business:
     * <ul>
     * <li>Da APERTA si può passare a CHIUSA o RITIRATA</li>
     * <li>Da CHIUSA non si può passare a nessun altro stato</li>
     * <li>Da RITIRATA non si può passare a nessun altro stato</li>
     * </ul>
     *
     * @param nuovoStato lo stato di destinazione da verificare
     * @return true se la transizione è valida, false altrimenti
     *
     * @pre nuovoStato può essere null
     * @post se this == APERTA && (nuovoStato == CHIUSA || nuovoStato == RITIRATA),
     *       result == true
     * @post se this == CHIUSA || this == RITIRATA, result == false
     * @post se nuovoStato == null, result == false
     * @post se nuovoStato == this, result == false (nessuna transizione verso lo
     *       stesso stato)
     */
    public boolean puoPassareA(StatoProposta nuovoStato) {
        // Una transizione verso null non è mai valida
        if (nuovoStato == null) {
            return false;
        }

        // Una transizione verso lo stesso stato non è considerata una reale transizione
        if (this == nuovoStato) {
            return false;
        }

        // Verifica le transizioni valide in base allo stato corrente
        switch (this) {
            case APERTA:
                // Da APERTA si può passare a CHIUSA o RITIRATA
                return nuovoStato == CHIUSA || nuovoStato == RITIRATA;

            case CHIUSA:
            case RITIRATA:
                // Stati terminali, nessuna transizione possibile
                return false;

            default:
                // Caso di sicurezza (non dovrebbe mai verificarsi)
                return false;
        }
    }

    // -----------------------------------------------------------------------------------
    // OVERRIDE DI OBJECT
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce la descrizione testuale di questo stato.
     * <p>
     * Questa è un'implementazione personalizzata che usa la descrizione
     * invece del nome standard dell'enum.
     *
     * @return la descrizione dello stato
     *
     * @post result == getDescrizione()
     */
    @Override
    public String toString() {
        return descrizione;
    }
}