package model;

import java.io.Serializable;

/**
 * Rappresenta i possibili stati di una proposta di scambio.
 * <p>
 * Una proposta attraversa diversi stati durante il suo ciclo di vita:
 * <ul>
 * <li>APERTA: la proposta è disponibile per essere accettata</li>
 * <li>CHIUSA: la proposta è stata accettata e lo scambio è in corso</li>
 * <li>RITIRATA: la proposta è stata ritirata dal propositore</li>
 * </ul>
 */
public enum StatoProposta implements Serializable {

    /**
     * Proposta disponibile per l'accettazione.
     */
    APERTA("Proposta aperta"),

    /**
     * Proposta accettata e in fase di esecuzione.
     */
    CHIUSA("Proposta accettata"),

    /**
     * Proposta ritirata dal propositore.
     */
    RITIRATA("Proposta ritirata");

    private final String descrizione;

    /**
     * Costruisce un nuovo stato con la descrizione specificata.
     *
     * @param descrizione descrizione testuale dello stato
     */
    StatoProposta(String descrizione) {
        this.descrizione = descrizione;
    }

    /**
     * Restituisce la descrizione testuale di questo stato.
     *
     * @return la descrizione dello stato
     */
    public String getDescrizione() {
        return descrizione;
    }

    /**
     * Verifica se da questo stato è possibile passare allo stato specificato.
     * <p>
     * Definisce le transizioni di stato valide secondo le regole di business.
     *
     * @param nuovoStato lo stato di destinazione
     * @return true se la transizione è valida, false altrimenti
     */
    public boolean puòPassareA(StatoProposta nuovoStato) {
        switch (this) {
            case APERTA:
                // Da APERTA si può passare a CHIUSA o RITIRATA
                return nuovoStato == CHIUSA ||
                        nuovoStato == RITIRATA;

            case CHIUSA:
            case RITIRATA:
                // Stati terminali, nessuna transizione possibile
                return false;

            default:
                return false;
        }
    }

    /**
     * Restituisce la descrizione testuale di questo stato.
     *
     * @return la descrizione dello stato
     */
    @Override
    public String toString() {
        return descrizione;
    }
}