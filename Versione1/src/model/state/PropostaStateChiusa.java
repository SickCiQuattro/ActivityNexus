package model.state;

import model.Proposta;

/**
 * Implementazione dello stato CHIUSA per una proposta.
 * <p>
 * Questo stato rappresenta una proposta che è stata accettata e inserita
 * in un insieme chiuso di scambio. È uno stato terminale: una volta che
 * una proposta raggiunge questo stato, non può più cambiare.
 * <p>
 * <b>Transizioni permesse da questo stato:</b>
 * <ul>
 * <li>Nessuna - stato terminale</li>
 * </ul>
 * <p>
 * Implementa il pattern <b>Singleton</b> per evitare la creazione di
 * istanze multiple dello stesso stato, dato che lo stato è stateless.
 *
 * @see PropostaState
 * @see PropostaStateAperta
 * @see PropostaStateRitirata
 */
public class PropostaStateChiusa implements PropostaState {

    /** Istanza singleton dello stato */
    private static final PropostaStateChiusa INSTANCE = new PropostaStateChiusa();

    /** Costruttore privato per Singleton */
    private PropostaStateChiusa() {
    }

    /**
     * Restituisce l'istanza singleton dello stato CHIUSA.
     *
     * @return l'unica istanza di PropostaStateChiusa
     */
    public static PropostaStateChiusa getInstance() {
        return INSTANCE;
    }

    /**
     * Tenta di chiudere la proposta.
     * <p>
     * Operazione non permessa: la proposta è già chiusa.
     *
     * @param proposta la proposta (ignorata)
     * @return false, poiché una proposta già chiusa non può essere richiusa
     */
    @Override
    public boolean chiudi(Proposta proposta) {
        // Proposta già chiusa, operazione non permessa
        return false;
    }

    /**
     * Tenta di ritirare la proposta.
     * <p>
     * Operazione non permessa: una proposta chiusa non può essere ritirata.
     *
     * @param proposta la proposta (ignorata)
     * @return false, poiché una proposta chiusa non può essere ritirata
     */
    @Override
    public boolean ritira(Proposta proposta) {
        // Proposta chiusa, non può essere ritirata
        return false;
    }

    @Override
    public boolean isAperta() {
        return false;
    }

    @Override
    public boolean isChiusa() {
        return true;
    }

    @Override
    public boolean isRitirata() {
        return false;
    }

    @Override
    public String getNomeStato() {
        return "CHIUSA";
    }

    @Override
    public String toString() {
        return "ACCETTATA";
    }
}
