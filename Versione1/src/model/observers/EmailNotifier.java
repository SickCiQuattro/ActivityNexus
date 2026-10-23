package model.observers;

import model.Proposta;

/**
 * Notificatore via email per gli eventi delle proposte.
 * <p>
 * PATTERN: Observer (GoF) + Open/Closed Principle (SOLID)
 * <p>
 * Questa classe implementa {@link PropostaObserver} per inviare notifiche email
 * quando una proposta cambia stato. È un esempio concreto di come il pattern
 * Observer
 * permette di estendere il comportamento del sistema senza modificare il codice
 * esistente.
 * <p>
 * <b>Principio OCP rispettato:</b>
 * <ul>
 * <li>Per aggiungere notifiche SMS, basta creare SmsNotifier implements
 * PropostaObserver</li>
 * <li>Per aggiungere notifiche push, basta creare PushNotifier implements
 * PropostaObserver</li>
 * <li>La classe Proposta NON deve essere modificata per supportare nuovi tipi
 * di notifica</li>
 * </ul>
 *
 * @see PropostaObserver
 * @see Proposta
 */
public class EmailNotifier implements PropostaObserver {

    /**
     * Invia una notifica email quando una proposta viene chiusa.
     * <p>
     * Il destinatario è il propostitore della proposta, che viene informato
     * che la sua proposta è stata accettata in un insieme chiuso di scambio.
     *
     * @param proposta la proposta che è stata chiusa
     */
    @Override
    public void onPropostaChiusa(Proposta proposta) {
        String email = proposta.getPropostitore().getMail();
        String messaggio = String.format(
                "   [SIMULAZIONE EMAIL] Destinatario: %s%n" +
                        "   Oggetto: La tua proposta è stata accettata!%n" +
                        "   Contenuto: La tua proposta di scambio è stata inclusa in un insieme chiuso.%n" +
                        "   Dettagli: Richiesta '%s' in cambio di '%s'",
                email,
                proposta.getRichiesta().getCategoria().data().getNome(),
                proposta.getOfferta().getCategoria().data().getNome());

        // In produzione qui ci sarebbe l'invio reale dell'email
        // Per ora simuliamo stampando a console
        System.out.println(messaggio);
    }

    /**
     * Invia una notifica email quando una proposta viene ritirata.
     * <p>
     * Il destinatario è il propostitore della proposta, che riceve conferma
     * del ritiro della propria proposta di scambio.
     *
     * @param proposta la proposta che è stata ritirata
     */
    @Override
    public void onPropostaRitirata(Proposta proposta) {
        String email = proposta.getPropostitore().getMail();
        String messaggio = String.format(
                "   [SIMULAZIONE EMAIL] Destinatario: %s%n" +
                        "   Oggetto: Conferma ritiro proposta%n" +
                        "   Contenuto: Hai ritirato la tua proposta di scambio con successo.%n" +
                        "   Dettagli: Richiesta '%s' in cambio di '%s'",
                email,
                proposta.getRichiesta().getCategoria().data().getNome(),
                proposta.getOfferta().getCategoria().data().getNome());

        // In produzione qui ci sarebbe l'invio reale dell'email
        System.out.println(messaggio);
    }
}
