package model.observers;

import model.Proposta;

/**
 * Interfaccia Observer per gli eventi sulle proposte di scambio.
 * <p>
 * PATTERN: Observer (GoF) + Open/Closed Principle (SOLID)
 * <p>
 * Questa interfaccia definisce il contratto per gli oggetti che vogliono essere
 * notificati quando una proposta cambia stato. Implementando questo pattern:
 * <ul>
 * <li>Il codice è APERTO all'estensione (nuovi observer) ma CHIUSO alla
 * modifica</li>
 * <li>Nuovi tipi di notifiche possono essere aggiunti senza modificare
 * Proposta</li>
 * <li>Il disaccoppiamento tra Proposta e i suoi observer migliora la
 * testabilità</li>
 * </ul>
 * <p>
 * <b>Esempio di utilizzo:</b>
 * 
 * <pre>
 * PropostaObserver emailNotifier = new EmailNotifier();
 * proposta.addObserver(emailNotifier);
 * proposta.chiudiProposta(); // emailNotifier viene notificato automaticamente
 * </pre>
 *
 * @see Proposta
 * @see EmailNotifier
 */
public interface PropostaObserver {

    /**
     * Chiamato quando una proposta viene chiusa (accettata in un insieme chiuso).
     * <p>
     * Questo metodo viene invocato automaticamente da
     * {@link Proposta#chiudiProposta()}
     * quando la transizione di stato da APERTA a CHIUSA ha successo.
     *
     * @param proposta la proposta che è stata chiusa
     */
    void onPropostaChiusa(Proposta proposta);

    /**
     * Chiamato quando una proposta viene ritirata dal propostitore.
     * <p>
     * Questo metodo viene invocato automaticamente da
     * {@link Proposta#ritiraProposta()}
     * quando la transizione di stato da APERTA a RITIRATA ha successo.
     *
     * @param proposta la proposta che è stata ritirata
     */
    void onPropostaRitirata(Proposta proposta);
}
