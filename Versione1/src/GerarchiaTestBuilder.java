import model.*;
import com.scalified.tree.TreeNode;
import java.util.*;

/**
 * Classe di test che crea una gerarchia di servizi formativi con struttura
 * complessa
 * per testare le funzionalità del sistema di gestione gerarchie.
 * <p>
 * La gerarchia creata contiene più di 5 foglie distribuite su almeno 3 livelli,
 * con campi caratteristici e valori di dominio appropriati per ogni livello.
 */
public class GerarchiaTestBuilder {

    /**
     * Costruisce una gerarchia completa per testing.
     * 
     * @return una gerarchia strutturata con almeno 5 foglie su 3 livelli
     */
    public static Gerarchia createTestHierarchy() {
        // LIVELLO 0: Radice - Servizi Formativi
        Campo campoAreaFormativa = createCampoWithValues("Area Formativa",
                List.of("Tecnologica", "Linguistica", "Artistica"));
        Categoria radice = new Categoria("Servizi Formativi", campoAreaFormativa);
        Gerarchia gerarchia = new Gerarchia(radice);

        // LIVELLO 1: Prima generazione
        // 1. Ramo Tecnologico
        Campo campoTecnologico = createCampoWithValues("Ambito Tecnologico",
                List.of("Programmazione", "Reti", "Sicurezza"));
        Categoria tecnologia = new Categoria("Formazione Tecnologica", campoTecnologico);
        Gerarchia ramoTecnologico = new Gerarchia(tecnologia);

        // 2. Ramo Linguistico
        Campo campoLinguistico = createCampoWithValues("Tipo Lingua",
                List.of("Europee", "Asiatiche"));
        Categoria linguistica = new Categoria("Formazione Linguistica", campoLinguistico);
        Gerarchia ramoLinguistico = new Gerarchia(linguistica);

        // 3. Ramo Artistico (sarà una foglia al livello 1)
        Categoria artistica = new Categoria("Formazione Artistica", null);
        Gerarchia ramoArtistico = new Gerarchia(artistica);

        // Aggiungiamo i rami principali alla radice
        gerarchia.add(ramoTecnologico);
        gerarchia.add(ramoLinguistico);
        gerarchia.add(ramoArtistico);

        // LIVELLO 2: Seconda generazione
        // 1.1 Ramo Programmazione
        Campo campoProgrammazione = createCampoWithValues("Linguaggio",
                List.of("Java", "Python", "JavaScript"));
        Categoria programmazione = new Categoria("Corso Programmazione", campoProgrammazione);
        Gerarchia ramoProgrammazione = new Gerarchia(programmazione);
        ramoTecnologico.add(ramoProgrammazione);

        // 1.2 Ramo Reti (sarà una foglia al livello 2)
        Categoria reti = new Categoria("Corso Reti", null);
        ramoTecnologico.add(new Gerarchia(reti));

        // 1.3 Ramo Sicurezza (sarà una foglia al livello 2)
        Categoria sicurezza = new Categoria("Corso Sicurezza", null);
        ramoTecnologico.add(new Gerarchia(sicurezza));

        // 2.1 Ramo Lingue Europee
        Campo campoEuropee = createCampoWithValues("Lingua Europea",
                List.of("Inglese", "Francese", "Spagnolo"));
        Categoria lingueEuropee = new Categoria("Corso Lingue Europee", campoEuropee);
        Gerarchia ramoEuropee = new Gerarchia(lingueEuropee);
        ramoLinguistico.add(ramoEuropee);

        // 2.2 Ramo Lingue Asiatiche
        Campo campoAsiatiche = createCampoWithValues("Lingua Asiatica",
                List.of("Giapponese", "Cinese"));
        Categoria lingueAsiatiche = new Categoria("Corso Lingue Asiatiche", campoAsiatiche);
        Gerarchia ramoAsiatiche = new Gerarchia(lingueAsiatiche);
        ramoLinguistico.add(ramoAsiatiche);

        // LIVELLO 3: Foglie
        // Corsi di Programmazione
        ramoProgrammazione.add(new Gerarchia(new Categoria("Corso Java", null)));
        ramoProgrammazione.add(new Gerarchia(new Categoria("Corso Python", null)));
        ramoProgrammazione.add(new Gerarchia(new Categoria("Corso JavaScript", null)));

        // Corsi di Lingue Europee
        ramoEuropee.add(new Gerarchia(new Categoria("Corso Inglese", null)));
        ramoEuropee.add(new Gerarchia(new Categoria("Corso Francese", null)));
        ramoEuropee.add(new Gerarchia(new Categoria("Corso Spagnolo", null)));

        // Corsi di Lingue Asiatiche
        ramoAsiatiche.add(new Gerarchia(new Categoria("Corso Giapponese", null)));
        ramoAsiatiche.add(new Gerarchia(new Categoria("Corso Cinese", null)));

        // Configura i fattori di conversione
        configureFattoriConversione(gerarchia);

        return gerarchia;
    }

    /**
     * Crea un campo con valori predefiniti.
     * 
     * @param nome   nome del campo
     * @param valori lista dei valori da aggiungere al campo
     * @return il campo con i valori specificati
     */
    private static Campo createCampoWithValues(String nome, List<String> valori) {
        Campo campo = new Campo(nome);
        for (String valore : valori) {
            campo.addValore(new ValoreCampo(valore));
        }
        return campo;
    }

    /**
     * Configura i fattori di conversione per tutta la gerarchia.
     * 
     * @param gerarchia la gerarchia su cui configurare i fattori
     */
    private static void configureFattoriConversione(Gerarchia gerarchia) {
        ArrayList<TreeNode<Categoria>> foglie = gerarchia.getFogliePerLivello();

        if (!foglie.isEmpty()) {
            // Imposta la prima foglia come riferimento
            TreeNode<Categoria> fogliaRiferimento = foglie.getFirst();
            gerarchia.getFattoreConversione().setNodoRiferimento(fogliaRiferimento);

            // Configura i fattori per le altre foglie
            for (TreeNode<Categoria> foglia : foglie) {
                if (foglia == fogliaRiferimento)
                    continue;

                double fattore;
                // Fattore basato sulla profondità relativa nella gerarchia
                if (foglia.level() > fogliaRiferimento.level()) {
                    // Foglia più specializzata (livello più profondo)
                    fattore = 1.0 + ((foglia.level() - fogliaRiferimento.level()) * 0.2);
                } else if (foglia.level() < fogliaRiferimento.level()) {
                    // Foglia meno specializzata (livello più superficiale)
                    fattore = 1.0 - ((fogliaRiferimento.level() - foglia.level()) * 0.2);
                } else {
                    // Stesso livello
                    fattore = 1.0;
                }

                // Limita il fattore al range 0.5-2.0
                fattore = Math.max(0.5, Math.min(2.0, fattore));

                gerarchia.getFattoreConversione().addFattoreConversione(foglia.level(), fattore);
            }
        }
    }

    /**
     * Metodo main per testare la creazione della gerarchia.
     * 
     * @param args argomenti da linea di comando (non utilizzati)
     */
    public static void main(String[] args) {
        Gerarchia gerarchia = createTestHierarchy();
        System.out.println("=== GERARCHIA DI TEST CREATA ===");
        System.out.println(gerarchia);

        ArrayList<TreeNode<Categoria>> foglie = gerarchia.getFoglie();
        System.out.println("\nFoglie della gerarchia:");
        for (int i = 0; i < foglie.size(); i++) {
            TreeNode<Categoria> foglia = foglie.get(i);
            System.out.println((i + 1) + ". " + foglia.data().getNome() +
                    " (livello " + foglia.level() + ")");
        }

        System.out.println("\nStatistiche:");
        System.out.println("- Totale foglie: " + foglie.size());
        System.out.println("- Livelli di profondità: 3");

        // Mostra i fattori di conversione
        System.out.println("\nFattori di conversione:");
        TreeNode<Categoria> riferimento = gerarchia.getFattoreConversione().getFogliaRiferimento();
        System.out.println("Foglia di riferimento: " + riferimento.data().getNome());
    }
}