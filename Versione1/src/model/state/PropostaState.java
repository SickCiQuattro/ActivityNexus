package model.state;

import model.Proposta;

/**
 * Interfaccia che definisce il contratto per gli stati di una proposta.
 * <p>
 * Questa interfaccia implementa il pattern <b>State</b> (GoF), permettendo
 * a un oggetto {@link Proposta} di modificare il proprio comportamento
 * quando il suo stato interno cambia.
 * <p>
 * <b>Vantaggi del pattern State:</b>
 * <ul>
 * <li><b>Eliminazione if-else:</b> la logica condizionale basata sullo stato
 * viene sostituita dal polimorfismo</li>
 * <li><b>Single Responsibility:</b> ogni stato è responsabile solo del proprio
 * comportamento</li>
 * <li><b>Open/Closed:</b> nuovi stati possono essere aggiunti senza modificare
 * il codice esistente</li>
 * <li><b>Leggibilità:</b> il comportamento di ogni stato è chiaramente isolato
 * nella sua classe</li>
 * </ul>
 *
 * @see Proposta
 * @see PropostaStateAperta
 * @see PropostaStateChiusa
 * @see PropostaStateRitirata
 */
public interface PropostaState {

    /**
     * Tenta di chiudere la proposta.
     * <p>
     * L'operazione di chiusura è possibile solo quando la proposta è nello
     * stato APERTA. In altri stati, l'operazione non ha effetto.
     *
     * @param proposta la proposta su cui eseguire l'operazione
     * @return true se l'operazione è riuscita, false altrimenti
     */
    boolean chiudi(Proposta proposta);

    /**
     * Tenta di ritirare la proposta.
     * <p>
     * L'operazione di ritiro è possibile solo quando la proposta è nello
     * stato APERTA. In altri stati, l'operazione non ha effetto.
     *
     * @param proposta la proposta su cui eseguire l'operazione
     * @return true se l'operazione è riuscita, false altrimenti
     */
    boolean ritira(Proposta proposta);

    /**
     * Verifica se la proposta in questo stato è aperta.
     *
     * @return true se la proposta è aperta, false altrimenti
     */
    boolean isAperta();

    /**
     * Verifica se la proposta in questo stato è chiusa.
     *
     * @return true se la proposta è chiusa, false altrimenti
     */
    boolean isChiusa();

    /**
     * Verifica se la proposta in questo stato è ritirata.
     *
     * @return true se la proposta è ritirata, false altrimenti
     */
    boolean isRitirata();

    /**
     * Restituisce il nome dello stato corrente.
     *
     * @return il nome descrittivo dello stato
     */
    String getNomeStato();
}
