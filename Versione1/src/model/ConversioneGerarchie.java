package model;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Classe che rappresenta i fattori di conversione tra categorie di una gerarchia.
 */
public class ConversioneGerarchie implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<CoppiaOrdinata, Double> fattori = new HashMap<>();

    /**
     * Restituisce il fattore di conversione tra due categorie.
     * Se il fattore di conversione non esiste, cerca la coppia invertita e restituisce l'inverso del fattore di conversione.
     *
     * @param prima la prima categoria
     * @param seconda la seconda categoria
     * @return il fattore di conversione, o null se non esiste
     */
    public Double getFattoreConversione(Categoria prima, Categoria seconda) {
        Double fattore = getFattore(prima, seconda);
        if (fattore != null) {
            return fattore;
        }
        fattore = getFattore(seconda, prima);
        if (fattore != null) {
            return 1 / fattore;
        }
        return null;
    }

    /**
     * Aggiunge un fattore di conversione tra due categorie.
     *
     * @param prima la prima categoria
     * @param seconda la seconda categoria
     * @param fattore il fattore di conversione
     */
    public void addFattoreConversione(Categoria prima, Categoria seconda, double fattore) {
        CoppiaOrdinata coppia = new CoppiaOrdinata(prima, seconda);
        fattori.put(coppia, fattore);
    }

    /**
     * Restituisce il fattore di conversione per una coppia ordinata di categorie.
     *
     * @param prima la prima categoria
     * @param seconda la seconda categoria
     * @return il fattore di conversione, o null se non esiste
     */
    private Double getFattore(Categoria prima, Categoria seconda) {
        CoppiaOrdinata coppia = new CoppiaOrdinata(prima, seconda);
        return fattori.get(coppia);
    }

    /**
     * Scrive l'oggetto GerarchiaConversione in un flusso di output.
     *
     * @param out il flusso di output
     * @throws IOException se si verifica un errore di I/O
     */
    @Serial
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    /**
     * Legge l'oggetto GerarchiaConversione da un flusso di input.
     *
     * @param in il flusso di input
     * @throws IOException se si verifica un errore di I/O
     * @throws ClassNotFoundException se la classe non viene trovata
     */
    @Serial
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }

    /**
     * Classe interna che rappresenta una coppia ordinata di categorie.
     */
    private static class CoppiaOrdinata implements Serializable {
        private static final long serialVersionUID = 1L;

        private final Categoria prima;
        private final Categoria seconda;

        public CoppiaOrdinata(Categoria prima, Categoria seconda) {
            this.prima = prima;
            this.seconda = seconda;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            CoppiaOrdinata other = (CoppiaOrdinata) obj;
            return Objects.equals(this.prima, other.prima) && Objects.equals(this.seconda, other.seconda);
        }

        @Override
        public int hashCode() {
            return Objects.hash(prima, seconda);
        }
    }
}
// lavoro qua per capire poi dove mettere il tutto, forse nel Controller vediamo

// due nodi di gerarchie anche diverse
//nodoPartenza;
//nodoArrivo;
//
//if (nodoPartenza.root().data().equals(nodoArrivo.root().data())) {
//    return Database.getGerarchia(nodoPartenza.root()).getFattoreConversione(nodoPartenza.level(), nodoArrivo.level());
//}
//a = Database.getGerarchia(nodoPartenza.root()).getFattoreConversione(nodoPartenza.level(), Database.getGerarchia(nodoPartenza.root()).getNodoRiferimento().level()); // getNodoRiferimento().level() = getLivelloRiferimento()
//b = Database.getGerarchia(nodoArrivo.root()).getFattoreConversione(Database.getGerarchia(nodoArrivo.root()).getNodoRiferimento().level(), nodoArrivo.level());
//c = Database.getConversioneGerarchie(nodoPartenza.root().data(), nodoArrivo.root().data());
//return a * c * b;
