package model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Rappresenta uno scambio di prestazione tra due fruitori.
 * <p>
 * Contiene informazioni sul fornitore, sul ricevente e sulla prestazione
 * scambiata. Nella Versione 4, gli scambi fanno parte di insiemi chiusi
 * che formano cicli di prestazioni tra fruitori dello stesso comprensorio.
 * <p>
 * <b>Invarianti:</b>
 * <ul>
 * <li>fornitore != null</li>
 * <li>ricevente != null</li>
 * <li>Prestazione != null</li>
 * <li>Fornitore e ricevente appartengono allo stesso comprensorio</li>
 * </ul>
 */
public class Scambio implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /** Il fruitore che fornisce la prestazione */
    private final Fruitore fornitore;

    /** Il fruitore che riceve la prestazione */
    private final Fruitore ricevente;

    /** La prestazione scambiata */
    private final Prestazione prestazione;

    /** La proposta da cui deriva questo scambio */
    private final Proposta propostaOrigine;

    /**
     * Crea un nuovo scambio collegandolo alla proposta di origine.
     * 
     * @param fornitore       il fruitore che fornisce la prestazione
     * @param ricevente       il fruitore che riceve la prestazione
     * @param prestazione     la prestazione scambiata
     * @param propostaOrigine la proposta da cui deriva lo scambio
     * @throws IllegalArgumentException se i fruitori appartengono a comprensori
     *                                  diversi
     * @throws NullPointerException     se uno dei parametri essenziali è null
     */
    public Scambio(Fruitore fornitore, Fruitore ricevente, Prestazione prestazione, Proposta propostaOrigine) {
        this.fornitore = Objects.requireNonNull(fornitore, "Il fornitore non può essere null");
        this.ricevente = Objects.requireNonNull(ricevente, "Il ricevente non può essere null");
        this.prestazione = Objects.requireNonNull(prestazione, "La prestazione non può essere null");
        this.propostaOrigine = propostaOrigine;

        // Verifica che i fruitori appartengano allo stesso comprensorio
        verificaStessoComprensorio();
    }

    /**
     * Crea un nuovo scambio senza collegamento alla proposta di origine.
     * 
     * @param fornitore   il fruitore che fornisce la prestazione
     * @param ricevente   il fruitore che riceve la prestazione
     * @param prestazione la prestazione scambiata
     * @throws IllegalArgumentException se i fruitori appartengono a comprensori
     *                                  diversi
     */
    public Scambio(Fruitore fornitore, Fruitore ricevente, Prestazione prestazione) {
        this(fornitore, ricevente, prestazione, null);
    }

    /**
     * Verifica che fornitore e ricevente appartengano allo stesso comprensorio.
     * 
     * @throws IllegalArgumentException se i fruitori appartengono a comprensori
     *                                  diversi
     */
    private void verificaStessoComprensorio() {
        if (!fornitore.getComprensorio().equals(ricevente.getComprensorio())) {
            throw new IllegalArgumentException(
                    "Fornitore e ricevente devono appartenere allo stesso comprensorio. " +
                            "Fornitore: " + fornitore.getUsername() + " (" + fornitore.getComprensorio().getNome()
                            + "), " +
                            "Ricevente: " + ricevente.getUsername() + " (" + ricevente.getComprensorio().getNome()
                            + ")");
        }
    }

    /**
     * Restituisce il fruitore che fornisce la prestazione.
     * 
     * @return il fornitore
     */
    public Fruitore getFornitore() {
        return fornitore;
    }

    /**
     * Restituisce il fruitore che riceve la prestazione.
     * 
     * @return il ricevente
     */
    public Fruitore getRicevente() {
        return ricevente;
    }

    /**
     * Restituisce la prestazione scambiata.
     * 
     * @return la prestazione
     */
    public Prestazione getPrestazione() {
        return prestazione;
    }

    /**
     * Restituisce la proposta da cui deriva questo scambio.
     * 
     * @return la proposta di origine, o null se non specificata
     */
    public Proposta getPropostaOrigine() {
        return propostaOrigine;
    }

    /**
     * Restituisce il comprensorio a cui appartengono i fruitori coinvolti.
     * 
     * @return il comprensorio comune dei fruitori
     */
    public Comprensorio getComprensorio() {
        return fornitore.getComprensorio();
    }

    /**
     * Verifica se lo scambio coinvolge un dato fruitore.
     * 
     * @param fruitore il fruitore da verificare
     * @return true se il fruitore è coinvolto come fornitore o ricevente
     */
    public boolean coinvolge(Fruitore fruitore) {
        return fornitore.equals(fruitore) || ricevente.equals(fruitore);
    }

    @Override
    public String toString() {
        return String.format("%s fornisce %s ore di %s a %s",
                fornitore.getUsername(),
                prestazione.getOre(),
                prestazione.getCategoria().data().getNome(),
                ricevente.getUsername());
    }

    /**
     * Restituisce una rappresentazione dettagliata dello scambio.
     * 
     * @return una stringa dettagliata con informazioni complete sullo scambio
     */
    public String getDettagli() {
        StringBuilder sb = new StringBuilder();
        sb.append("┌───────────────────────────────────────────┐\n");
        sb.append("│               SCAMBIO                     │\n");
        sb.append("├───────────────────────────────────────────┤\n");
        sb.append(String.format("│ Fornitore: %-30s │\n", fornitore.getUsername()));
        sb.append(String.format("│ Email: %-34s │\n", fornitore.getMail()));
        sb.append("├───────────────────────────────────────────┤\n");
        sb.append(String.format("│ Ricevente: %-30s │\n", ricevente.getUsername()));
        sb.append(String.format("│ Email: %-34s │\n", ricevente.getMail()));
        sb.append("├───────────────────────────────────────────┤\n");
        sb.append("│ PRESTAZIONE                               │\n");
        sb.append(String.format("│ %s │\n", prestazione.toString()));
        sb.append("│                                           │\n");
        sb.append(String.format("│ Categoria: %-30s │\n", prestazione.getCategoria().data().getNome()));
        sb.append(String.format("│ Ore: %-36d │\n", prestazione.getOre()));
        sb.append("└───────────────────────────────────────────┘");
        return sb.toString();
    }

    /**
     * Restituisce una rappresentazione compatta dello scambio.
     * 
     * @return una stringa compatta con informazioni essenziali
     */
    public String toStringCompatto() {
        return String.format("%s → %s: %d ore di %s",
                fornitore.getUsername(),
                ricevente.getUsername(),
                prestazione.getOre(),
                prestazione.getCategoria().data().getNome());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        Scambio other = (Scambio) obj;
        return Objects.equals(fornitore, other.fornitore) &&
                Objects.equals(ricevente, other.ricevente) &&
                Objects.equals(prestazione, other.prestazione);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fornitore, ricevente, prestazione);
    }
}