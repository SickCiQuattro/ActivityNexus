package model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Rappresenta un insieme chiuso di proposte che formano un ciclo di scambio
 * valido.
 * <p>
 * Un insieme chiuso si verifica quando il sistema identifica un gruppo di
 * proposte
 * che formano un ciclo completo di scambi, dove:
 * <ul>
 * <li>Ogni fruitore offre una prestazione ad un altro partecipante</li>
 * <li>Ogni fruitore riceve una prestazione di valore equivalente</li>
 * <li>Nessun partecipante resta escluso (ciclo completo)</li>
 * </ul>
 * <p>
 * Al momento della creazione dell'insieme chiuso:
 * <ul>
 * <li>Le proposte coinvolte vengono automaticamente chiuse</li>
 * <li>Gli scambi vengono registrati con i dettagli su chi offre cosa e a
 * chi</li>
 * <li>I configuratori vengono notificati per facilitare il completamento degli
 * scambi</li>
 * </ul>
 * 
 * <p>
 * <b>Invarianti della classe:</b>
 * <ul>
 * <li>Le liste di proposte e scambi non sono mai null</li>
 * <li>Ogni proposta appartiene ad un solo insieme chiuso</li>
 * <li>Tutte le proposte coinvolte hanno stato "chiuso"</li>
 * <li>Per ogni scambio, fornitore e ricevente sono fruitori distinti</li>
 * </ul>
 *
 * @see Proposta
 * @see Scambio
 * @see Fruitore
 * @see Prestazione
 */
public class InsiemeChiuso implements Serializable {
    // -----------------------------------------------------------------------------------
    // FIELDS E COSTANTI
    // -----------------------------------------------------------------------------------

    /** Identificatore di versione per la serializzazione */
    @Serial
    private static final long serialVersionUID = 1L;

    /** Proposte appartenenti all'insieme chiuso */
    private final List<Proposta> proposte;

    /** Scambi che devono avvenire (chi fornisce cosa a chi) */
    private final List<Scambio> scambi;

    /**
     * Flag per indicare se l'insieme chiuso è già stato notificato ai configuratori
     */
    private boolean notificato;

    // -----------------------------------------------------------------------------------
    // COSTRUTTORI
    // -----------------------------------------------------------------------------------

    /**
     * Crea un nuovo insieme chiuso per un comprensorio specifico.
     * <p>
     * Inizializza le liste vuote di proposte e scambi, e imposta lo stato
     * iniziale come "non notificato".
     * 
     * @pre comprensorio può essere null
     * @post getProposte().isEmpty() == true
     * @post getScambi().isEmpty() == true
     * @post isNotificato() == false
     */
    public InsiemeChiuso() {
        // Inizializza le liste vuote per proposte e scambi
        this.proposte = new ArrayList<>();
        this.scambi = new ArrayList<>();

        // Inizialmente l'insieme non è notificato
        this.notificato = false;
    }

    // -----------------------------------------------------------------------------------
    // METODI DI GESTIONE PROPOSTE E SCAMBI
    // -----------------------------------------------------------------------------------

    /**
     * Aggiunge una proposta all'insieme chiuso e la imposta come chiusa.
     * <p>
     * Se la proposta è già presente nell'insieme, l'operazione non ha effetto.
     *
     * @param proposta la proposta da aggiungere all'insieme chiuso
     * 
     * @pre proposta != null
     * @post se !getProposte().contains(proposta) prima della chiamata,
     *       allora getProposte().contains(proposta) dopo la chiamata
     * @post proposta.isChiusa() == true
     */
    public void aggiungiProposta(Proposta proposta) {
        // Verifica se la proposta è già presente nell'insieme (evita duplicati)
        if (proposte.contains(proposta)) {
            return;
        }

        // Aggiunge la proposta all'insieme e la imposta come chiusa
        proposte.add(proposta);
        proposta.chiudiProposta();
    }

    /**
     * Aggiunge uno scambio all'insieme chiuso.
     * <p>
     * Uno scambio rappresenta il trasferimento di una prestazione da un
     * fruitore (fornitore) ad un altro (ricevente).
     * 
     * @param scambio lo scambio da aggiungere
     * 
     * @pre scambio != null
     * @post getScambi().contains(scambio)
     * @post getNumeroScambi() è incrementato di 1
     */
    public void aggiungiScambio(Scambio scambio) {
        // Aggiunge lo scambio alla lista degli scambi dell'insieme chiuso
        scambi.add(scambio);
    }

    // -----------------------------------------------------------------------------------
    // METODI DI ACCESSO (GETTERS)
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce tutti i fruitori coinvolti nell'insieme chiuso.
     * <p>
     * Raccoglie i fruitori che partecipano come fornitori o riceventi negli scambi.
     * 
     * @return set non modificabile di fruitori coinvolti nell'insieme chiuso
     *
     * @post result != null
     * @post result contiene tutti i fornitori e riceventi degli scambi
     * @post result non è modificabile direttamente
     */
    public Set<Fruitore> getFruitori() {
        // Crea un set per evitare duplicati (un fruitore può essere sia
        // fornitore che ricevente in scambi diversi)
        Set<Fruitore> fruitori = new HashSet<>();

        // Raccoglie tutti i fruitori coinvolti come fornitori o riceventi
        for (Scambio scambio : scambi) {
            fruitori.add(scambio.getFornitore());
            fruitori.add(scambio.getRicevente());
        }

        // Restituisce una vista immutabile del set per proteggere l'incapsulamento
        return Collections.unmodifiableSet(fruitori);
    }

    /**
     * Restituisce la lista di proposte nell'insieme chiuso.
     * 
     * @return lista non modificabile delle proposte nell'insieme chiuso
     *
     * @post result != null
     * @post result contiene tutte le proposte aggiunte
     * @post result non è modificabile direttamente
     */
    public List<Proposta> getProposte() {
        // Restituisce una vista immutabile della lista per proteggere l'incapsulamento
        return Collections.unmodifiableList(proposte);
    }

    /**
     * Restituisce la lista di scambi nell'insieme chiuso.
     * 
     * @return lista non modificabile degli scambi nell'insieme chiuso
     *
     * @post result != null
     * @post result contiene tutti gli scambi aggiunti
     * @post result non è modificabile direttamente
     */
    public List<Scambio> getScambi() {
        // Restituisce una vista immutabile della lista per proteggere l'incapsulamento
        return Collections.unmodifiableList(scambi);
    }

    /**
     * Restituisce il numero di scambi nell'insieme chiuso.
     * 
     * @return il numero di scambi
     *
     * @post result >= 0
     * @post result == getScambi().size()
     */
    public int getNumeroScambi() {
        return scambi.size();
    }

    // -----------------------------------------------------------------------------------
    // METODI DI STATO
    // -----------------------------------------------------------------------------------

    /**
     * Imposta lo stato di notifica dell'insieme chiuso.
     * <p>
     * Quando un insieme chiuso viene notificato, i configuratori possono
     * contattare i fruitori coinvolti per facilitare gli scambi.
     * 
     * @param notificato true se l'insieme è stato notificato, false altrimenti
     *
     * @post isNotificato() == notificato
     */
    public void setNotificato(boolean notificato) {
        this.notificato = notificato;
    }

    /**
     * Verifica se l'insieme chiuso è stato notificato ai configuratori.
     * 
     * @return true se è stato notificato, false altrimenti
     *
     * @post result == this.notificato
     */
    public boolean isNotificato() {
        return notificato;
    }

    // -----------------------------------------------------------------------------------
    // METODI DI VISUALIZZAZIONE
    // -----------------------------------------------------------------------------------

    /**
     * Restituisce una rappresentazione sintetica dell'insieme chiuso.
     * <p>
     * Mostra il numero di proposte, di scambi e lo stato di notifica.
     *
     * @return rappresentazione testuale dell'insieme chiuso
     *
     * @post result != null
     * @post result contiene informazioni su numero di proposte, scambi e stato di
     *       notifica
     */
    @Override
    public String toString() {
        return "╔════════════════════════════════════════╗\n" +
                "║           INSIEME CHIUSO               ║\n" +
                "╚════════════════════════════════════════╝\n" +
                "Proposte: " + proposte.size() + "\n" +
                "Scambi: " + scambi.size() + "\n" +
                "Stato: " + (notificato ? "Notificato" : "Non notificato") + "\n";
    }

    /**
     * Restituisce una rappresentazione dettagliata dell'insieme chiuso.
     * <p>
     * Include informazioni estese su:
     * <ul>
     * <li>Numero di proposte e scambi</li>
     * <li>Stato di notifica</li>
     * <li>Elenco degli scambi con dettagli su fornitori e riceventi</li>
     * <li>Elenco dei fruitori coinvolti con i loro contatti</li>
     * </ul>
     * 
     * @return rappresentazione testuale dettagliata dell'insieme chiuso
     *
     * @post result != null
     * @post result contiene informazioni dettagliate su scambi e fruitori coinvolti
     */
    public String getDettagli() {
        StringBuilder sb = new StringBuilder();
        // Intestazione con informazioni generali
        sb.append("╔════════════════════════════════════════╗\n");
        sb.append("║       DETTAGLI INSIEME CHIUSO          ║\n");
        sb.append("╚════════════════════════════════════════╝\n");
        sb.append("Proposte coinvolte: ").append(proposte.size()).append("\n");
        sb.append("Scambi da effettuare: ").append(scambi.size()).append("\n");
        sb.append("Stato: ").append(notificato ? "Notificato" : "In attesa").append("\n\n");

        // Sezione degli scambi da effettuare
        sb.append("====== SCAMBI DA EFFETTUARE ======\n\n");

        if (scambi.isEmpty()) {
            sb.append("Nessuno scambio da effettuare\n");
        } else {
            // Elenca tutti gli scambi con i dettagli
            for (int i = 0; i < scambi.size(); i++) {
                Scambio scambio = scambi.get(i);
                String fornitore = scambio.getFornitore().getUsername();
                String ricevente = scambio.getRicevente().getUsername();

                // Mostra chi offre la prestazione a chi
                sb.append(String.format("%d. %s → %s\n", (i + 1), fornitore, ricevente));
                // Dettagli della prestazione offerta
                sb.append("   ").append(scambio.getPrestazione().toString()).append("\n\n");
            }
        }

        // Sezione dei fruitori coinvolti
        sb.append("====== FRUITORI COINVOLTI ======\n\n");

        Set<Fruitore> fruitori = getFruitori();
        if (fruitori.isEmpty()) {
            sb.append("Nessun fruitore coinvolto\n");
        } else {
            // Elenca tutti i fruitori con i loro contatti
            int j = 1;
            for (Fruitore fruitore : fruitori) {
                sb.append(String.format("%d. %s\n", j++, fruitore.getUsername()));
                sb.append("   Email: ").append(fruitore.getMail()).append("\n\n");
            }
        }

        return sb.toString();
    }
}