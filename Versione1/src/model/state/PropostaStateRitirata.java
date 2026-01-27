package model.state;

import model.Proposta;

/**
 * Implementazione dello stato RITIRATA per una proposta.
 * <p>
 * Questo stato rappresenta una proposta che è stata ritirata dal propostitore.
 * È uno stato terminale: una volta che una proposta raggiunge questo stato,
 * non può più cambiare.
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
 * @see PropostaStateChiusa
 */
public class PropostaStateRitirata implements PropostaState {

    /** Istanza singleton dello stato */
    private static final PropostaStateRitirata INSTANCE = new PropostaStateRitirata();

    /** Costruttore privato per Singleton */
    private PropostaStateRitirata() {
    }

    /**
     * Restituisce l'istanza singleton dello stato RITIRATA.
     *
     * @return l'unica istanza di PropostaStateRitirata
     */
    public static PropostaStateRitirata getInstance() {
        return INSTANCE;
    }

    /**
     * Tenta di chiudere la proposta.
     * <p>
     * Operazione non permessa: una proposta ritirata non può essere chiusa.
     *
     * @param proposta la proposta (ignorata)
     * @return false, poiché una proposta ritirata non può essere chiusa
     */
    @Override
    public boolean chiudi(Proposta proposta) {
        // Proposta ritirata, non può essere chiusa
        return false;
    }

    /**
     * Tenta di ritirare la proposta.
     * <p>
     * Operazione non permessa: la proposta è già ritirata.
     *
     * @param proposta la proposta (ignorata)
     * @return false, poiché una proposta già ritirata non può essere ri-ritirata
     */
    @Override
    public boolean ritira(Proposta proposta) {
        // Proposta già ritirata, operazione non permessa
        return false;
    }

    @Override
    public boolean isAperta() {
        return false;
    }

    @Override
    public boolean isChiusa() {
        return false;
    }

    @Override
    public boolean isRitirata() {
        return true;
    }

    @Override
    public String getNomeStato() {
        return "RITIRATA";
    }

    @Override
    public String toString() {
        return "RITIRATA";
    }
}
