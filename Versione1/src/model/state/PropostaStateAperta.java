package model.state;

import model.Proposta;

/**
 * Implementazione dello stato APERTA per una proposta.
 * <p>
 * Questo stato rappresenta una proposta disponibile per l'accettazione.
 * È lo stato iniziale di ogni proposta e l'unico stato da cui sono
 * possibili transizioni ad altri stati.
 * <p>
 * <b>Transizioni permesse da questo stato:</b>
 * <ul>
 * <li>APERTA → CHIUSA (tramite {@link #chiudi(Proposta)})</li>
 * <li>APERTA → RITIRATA (tramite {@link #ritira(Proposta)})</li>
 * </ul>
 * <p>
 * Implementa il pattern <b>Singleton</b> per evitare la creazione di
 * istanze multiple dello stesso stato, dato che lo stato è stateless.
 *
 * @see PropostaState
 * @see PropostaStateChiusa
 * @see PropostaStateRitirata
 */
public class PropostaStateAperta implements PropostaState {

    /** Istanza singleton dello stato */
    private static final PropostaStateAperta INSTANCE = new PropostaStateAperta();

    /** Costruttore privato per Singleton */
    private PropostaStateAperta() {
    }

    /**
     * Restituisce l'istanza singleton dello stato APERTA.
     *
     * @return l'unica istanza di PropostaStateAperta
     */
    public static PropostaStateAperta getInstance() {
        return INSTANCE;
    }

    /**
     * Chiude la proposta, transitando allo stato CHIUSA.
     * <p>
     * Questa operazione è permessa solo dallo stato APERTA.
     * Dopo la chiusura, la proposta non può più essere modificata.
     *
     * @param proposta la proposta da chiudere
     * @return true, poiché l'operazione è sempre possibile da questo stato
     */
    @Override
    public boolean chiudi(Proposta proposta) {
        proposta.setStatoInternal(PropostaStateChiusa.getInstance());
        return true;
    }

    /**
     * Ritira la proposta, transitando allo stato RITIRATA.
     * <p>
     * Questa operazione è permessa solo dallo stato APERTA.
     * Dopo il ritiro, la proposta non può più essere modificata.
     *
     * @param proposta la proposta da ritirare
     * @return true, poiché l'operazione è sempre possibile da questo stato
     */
    @Override
    public boolean ritira(Proposta proposta) {
        proposta.setStatoInternal(PropostaStateRitirata.getInstance());
        return true;
    }

    @Override
    public boolean isAperta() {
        return true;
    }

    @Override
    public boolean isChiusa() {
        return false;
    }

    @Override
    public boolean isRitirata() {
        return false;
    }

    @Override
    public String getNomeStato() {
        return "APERTA";
    }

    @Override
    public String toString() {
        return "APERTA";
    }
}
