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
 * Un insieme chiuso si crea quando il sistema individua un gruppo di proposte
 * aperte
 * che possono soddisfarsi reciprocamente, formando un ciclo di scambi dove ogni
 * fruitore
 * offre una prestazione e riceve un'altra prestazione di valore equivalente.
 * <p>
 * Le proposte appartenenti all'insieme chiuso vengono automaticamente chiuse e
 * il sistema notifica i configuratori che possono contattare i fruitori
 * coinvolti.
 */
public class InsiemeChiuso implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // Proposte appartenenti all'insieme chiuso
    private final List<Proposta> proposte;

    // Scambi che devono avvenire (chi fornisce cosa a chi)
    private final List<Scambio> scambi;

    // Flag per indicare se l'insieme chiuso è già stato notificato ai configuratori
    private boolean notificato;

    /**
     * Crea un nuovo insieme chiuso per un comprensorio specifico.
     * 
     * @param comprensorio il comprensorio a cui appartengono i fruitori coinvolti
     */
    public InsiemeChiuso(Comprensorio comprensorio) {
        this.proposte = new ArrayList<>();
        this.scambi = new ArrayList<>();
        this.notificato = false;
    }

    /**
     * Aggiunge una proposta all'insieme chiuso e imposta il riferimento
     * bidirezionale.
     * 
     * @param proposta la proposta da aggiungere
     * @return true se la proposta è stata aggiunta, false se era già presente
     */
    public boolean aggiungiProposta(Proposta proposta) {
        if (proposte.contains(proposta)) {
            return false;
        }

        proposte.add(proposta);
        proposta.chiudiProposta();
        return true;
    }

    /**
     * Aggiunge uno scambio all'insieme chiuso.
     * 
     * @param scambio lo scambio da aggiungere
     */
    public void aggiungiScambio(Scambio scambio) {
        scambi.add(scambio);
    }

    /**
     * Restituisce tutti i fruitori coinvolti nell'insieme chiuso.
     * 
     * @return set di fruitori coinvolti nell'insieme chiuso
     */
    public Set<Fruitore> getFruitori() {
        Set<Fruitore> fruitori = new HashSet<>();

        for (Scambio scambio : scambi) {
            fruitori.add(scambio.getFornitore());
            fruitori.add(scambio.getRicevente());
        }

        return Collections.unmodifiableSet(fruitori);
    }

    /**
     * Restituisce la lista di proposte nell'insieme chiuso.
     * 
     * @return le proposte nell'insieme chiuso
     */
    public List<Proposta> getProposte() {
        return Collections.unmodifiableList(proposte);
    }

    /**
     * Restituisce la lista di scambi nell'insieme chiuso.
     * 
     * @return gli scambi nell'insieme chiuso
     */
    public List<Scambio> getScambi() {
        return Collections.unmodifiableList(scambi);
    }

    /**
     * Imposta lo stato di notifica dell'insieme chiuso.
     * 
     * @param notificato se l'insieme è stato notificato
     */
    public void setNotificato(boolean notificato) {
        this.notificato = notificato;
    }

    /**
     * Verifica se l'insieme chiuso è stato notificato.
     * 
     * @return true se è stato notificato, false altrimenti
     */
    public boolean isNotificato() {
        return notificato;
    }

    /**
     * Verifica se l'insieme contiene una specifica proposta.
     * 
     * @param proposta la proposta da verificare
     * @return true se la proposta è contenuta nell'insieme
     */
    public boolean contieneProposta(Proposta proposta) {
        return proposte.contains(proposta);
    }

    /**
     * Restituisce il numero di proposte nell'insieme chiuso.
     * 
     * @return il numero di proposte
     */
    public int getNumeroProposte() {
        return proposte.size();
    }

    /**
     * Restituisce il numero di scambi nell'insieme chiuso.
     * 
     * @return il numero di scambi
     */
    public int getNumeroScambi() {
        return scambi.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════╗\n");
        sb.append("║           INSIEME CHIUSO               ║\n");
        sb.append("╚════════════════════════════════════════╝\n");
        sb.append("Proposte: ").append(proposte.size()).append("\n");
        sb.append("Scambi: ").append(scambi.size()).append("\n");
        sb.append("Stato: ").append(notificato ? "Notificato" : "Non notificato").append("\n");

        return sb.toString();
    }

    /**
     * Restituisce una rappresentazione dettagliata dell'insieme chiuso.
     * 
     * @return la rappresentazione dettagliata
     */
    public String getDettagli() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════╗\n");
        sb.append("║       DETTAGLI INSIEME CHIUSO          ║\n");
        sb.append("╚════════════════════════════════════════╝\n");
        sb.append("Proposte coinvolte: ").append(proposte.size()).append("\n");
        sb.append("Scambi da effettuare: ").append(scambi.size()).append("\n");
        sb.append("Stato: ").append(notificato ? "Notificato" : "In attesa").append("\n\n");

        sb.append("====== SCAMBI DA EFFETTUARE ======\n\n");

        if (scambi.isEmpty()) {
            sb.append("Nessuno scambio da effettuare\n");
        } else {
            for (int i = 0; i < scambi.size(); i++) {
                Scambio scambio = scambi.get(i);
                String fornitore = scambio.getFornitore().getUsername();
                String ricevente = scambio.getRicevente().getUsername();

                sb.append(String.format("%d. %s → %s\n", (i + 1), fornitore, ricevente));
                sb.append("   ").append(scambio.getPrestazione().toString()).append("\n\n");
            }
        }

        sb.append("====== FRUITORI COINVOLTI ======\n\n");

        Set<Fruitore> fruitori = getFruitori();
        if (fruitori.isEmpty()) {
            sb.append("Nessun fruitore coinvolto\n");
        } else {
            int j = 1;
            for (Fruitore fruitore : fruitori) {
                sb.append(String.format("%d. %s\n", j++, fruitore.getUsername()));
                sb.append("   Email: ").append(fruitore.getMail()).append("\n\n");
            }
        }

        return sb.toString();
    }

    /**
     * Genera un report compatto per le notifiche in console.
     * 
     * @return il report compatto
     */
    public String getReportCompatto() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════╗\n");
        sb.append("║        SCAMBI DA EFFETTUARE            ║\n");
        sb.append("╚════════════════════════════════════════╝\n");

        if (scambi.isEmpty()) {
            sb.append("Nessuno scambio da effettuare\n");
        } else {
            for (int i = 0; i < scambi.size(); i++) {
                Scambio scambio = scambi.get(i);
                String fornitore = scambio.getFornitore().getUsername();
                String ricevente = scambio.getRicevente().getUsername();

                sb.append(String.format("%d) %s → %s\n", (i + 1), fornitore, ricevente));
                sb.append("   ").append(scambio.getPrestazione().toString()).append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * Genera un elenco di contatti email dei fruitori coinvolti.
     * 
     * @return l'elenco di contatti email
     */
    public String getContattiEmail() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════╗\n");
        sb.append("║    CONTATTI EMAIL PER NOTIFICHE        ║\n");
        sb.append("╚════════════════════════════════════════╝\n");

        Set<Fruitore> fruitori = getFruitori();
        if (fruitori.isEmpty()) {
            sb.append("Nessun fruitore coinvolto\n");
        } else {
            int i = 1;
            for (Fruitore fruitore : fruitori) {
                sb.append(String.format("%d) %s\n", i++, fruitore.getUsername()));
                sb.append("   <").append(fruitore.getMail()).append(">\n\n");
            }
        }

        return sb.toString();
    }
}