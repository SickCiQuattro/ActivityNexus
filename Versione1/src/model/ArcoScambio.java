package model;

/**
 * Rappresenta un arco nel grafo degli scambi.
 * <p>
 * Un arco rappresenta una relazione direzionale "fornisce a" tra due fruitori,
 * dove un fruitore offre una prestazione che soddisfa la richiesta di un altro.
 * Ogni arco mantiene riferimenti a:
 * <ul>
 * <li>Il fruitore che fornisce la prestazione (fornitore)</li>
 * <li>Il fruitore che riceve la prestazione (ricevente)</li>
 * <li>La proposta che contiene i dettagli della prestazione</li>
 * </ul>
 * <p>
 * Gli archi sono utilizzati per costruire il grafo che rappresenta tutte
 * le possibili connessioni di scambio tra i fruitori del sistema.
 */
public class ArcoScambio {
    private final Fruitore fornitore;
    private final Fruitore ricevente;
    private final Proposta proposta;

    /**
     * Costruisce un nuovo arco di scambio tra due fruitori.
     *
     * @param fornitore il fruitore che fornisce la prestazione
     * @param ricevente il fruitore che riceve la prestazione
     * @param proposta  la proposta di scambio che contiene i dettagli della
     *                  prestazione
     *
     * @pre fornitore != null
     * @pre ricevente != null
     * @pre proposta != null
     * @post this.fornitore == fornitore
     * @post this.ricevente == ricevente
     * @post this.proposta == proposta
     */
    public ArcoScambio(Fruitore fornitore, Fruitore ricevente, Proposta proposta) {
        this.fornitore = fornitore;
        this.ricevente = ricevente;
        this.proposta = proposta;
    }

    /**
     * Ottiene il fruitore che fornisce la prestazione.
     *
     * @return il fruitore fornitore
     *
     * @post result == fornitore
     */
    public Fruitore getFornitore() {
        return fornitore;
    }

    /**
     * Ottiene il fruitore che riceve la prestazione.
     *
     * @return il fruitore ricevente
     *
     * @post result == ricevente
     */
    public Fruitore getRicevente() {
        return ricevente;
    }

    /**
     * Ottiene la proposta di scambio associata all'arco.
     *
     * @return la proposta di scambio
     *
     * @post result == proposta
     */
    public Proposta getProposta() {
        return proposta;
    }

    /**
     * Restituisce una rappresentazione testuale dell'arco di scambio.
     * <p>
     * Il formato è: "fornitore → ricevente: dettagli offerta"
     *
     * @return stringa rappresentante l'arco di scambio
     *
     * @post result != null
     */
    @Override
    public String toString() {
        return String.format("%s → %s: %s",
                fornitore.getUsername(),
                ricevente.getUsername(),
                proposta.getOfferta().toString());
    }
}