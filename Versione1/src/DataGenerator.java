import model.*;
import com.scalified.tree.TreeNode;
import com.scalified.tree.multinode.LinkedMultiTreeNode;
import java.io.File;

/**
 * Generatore di dati per il sistema ActivityNexus.
 * Crea e serializza tutti i dati richiesti per il progetto.
 */
public class DataGenerator {
    
    private static final String DATA_DIR = "data/";
    
    public static void main(String[] args) {
        // Crea la directory data se non esiste
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
            System.out.println("✓ Directory 'data' creata");
        }
        
        Database db = new Database();
        
        // Genera dati
        System.out.println("\n=== GENERAZIONE DATI ===\n");
        
        generaComprensori(db);
        generaUtenti(db);
        generaGerarchie(db);
        generaProposte(db);
        generaInsiemiChiusi(db);
        
        // Salva i dati
        System.out.println("\n=== SERIALIZZAZIONE ===\n");
        serializzaDati(db);
        
        System.out.println("\n✓ Generazione completata con successo!");
        System.out.println("\n=== RIEPILOGO DATI GENERATI ===");
        System.out.println("• 2 Configuratori");
        System.out.println("• 3 Fruitori (con email)");
        System.out.println("• 3 Comprensori (valli lombarde)");
        System.out.println("• 3 Gerarchie multi-livello (20 categorie foglia totali)");
        System.out.println("  - Sport: 6 foglie (3 livelli)");
        System.out.println("  - Formazione: 8 foglie (4 livelli)");
        System.out.println("  - Servizi Domestici: 6 foglie (3 livelli)");
        System.out.println("• Fattori di conversione tra gerarchie");
        System.out.println("• 10 Proposte APERTE");
        System.out.println("• 2 Proposte RITIRATE");
        System.out.println("• 3 Insiemi chiusi (9 proposte chiuse, 8 scambi totali)");
        System.out.println("  - Insieme 1: ciclo a 3 (Mario→Giulia→Luca→Mario)");
        System.out.println("  - Insieme 2: ciclo a 2 (Mario↔Giulia, notificato)");
        System.out.println("  - Insieme 3: ciclo complesso a 3");
        System.out.println("\nFile generati in: " + DATA_DIR);
    }
    
    private static void generaComprensori(Database db) {
        System.out.println("Generazione comprensori (valli lombarde)...");
        
        // Comprensorio 1: Valcamonica
        Comprensorio valcamonica = new Comprensorio("Valcamonica");
        valcamonica.addComune("Breno");
        valcamonica.addComune("Darfo Boario Terme");
        valcamonica.addComune("Edolo");
        valcamonica.addComune("Pisogne");
        valcamonica.addComune("Ponte di Legno");
        db.addComprensorio(valcamonica);
        System.out.println("  ✓ Comprensorio 'Valcamonica' con 5 comuni");
        
        // Comprensorio 2: Valtrompia
        Comprensorio valtrompia = new Comprensorio("Valtrompia");
        valtrompia.addComune("Gardone Val Trompia");
        valtrompia.addComune("Sarezzo");
        valtrompia.addComune("Lumezzane");
        valtrompia.addComune("Bovegno");
        db.addComprensorio(valtrompia);
        System.out.println("  ✓ Comprensorio 'Valtrompia' con 4 comuni");
        
        // Comprensorio 3: Valsabbia
        Comprensorio valsabbia = new Comprensorio("Valsabbia");
        valsabbia.addComune("Vestone");
        valsabbia.addComune("Vobarno");
        valsabbia.addComune("Idro");
        valsabbia.addComune("Preseglie");
        db.addComprensorio(valsabbia);
        System.out.println("  ✓ Comprensorio 'Valsabbia' con 4 comuni");
    }
    
    private static void generaUtenti(Database db) {
        System.out.println("\nGenerazione utenti (con vincoli di sicurezza)...");
        
        // 2 Configuratori con password sicure
        // Password deve avere: min 8 char, 1 maiuscola, 1 minuscola, 1 numero, 1 carattere speciale
        db.registraConfiguratore("admin", "Admin123!");
        System.out.println("  ✓ Configuratore: admin");
        
        db.registraConfiguratore("config01", "Config01#");
        System.out.println("  ✓ Configuratore: config01");
        
        // 3 Fruitori con email e comprensorio (tutti nello stesso per permettere scambi)
        Comprensorio valcamonica = null;
        
        for (Comprensorio c : db.getComprensori()) {
            if (c.getNome().equals("Valcamonica")) {
                valcamonica = c;
                break;
            }
        }
        
        // Username max 20 caratteri, solo alfanumerici
        db.registraFruitore("mariorossi", "Mario123!", "mario.rossi@email.it", valcamonica);
        System.out.println("  ✓ Fruitore: mariorossi (mario.rossi@email.it) - Valcamonica");
        
        db.registraFruitore("giuliabianchi", "Giulia123@", "giulia.bianchi@email.it", valcamonica);
        System.out.println("  ✓ Fruitore: giuliabianchi (giulia.bianchi@email.it) - Valcamonica");
        
        db.registraFruitore("lucaverdi", "Luca123#", "luca.verdi@email.it", valcamonica);
        System.out.println("  ✓ Fruitore: lucaverdi (luca.verdi@email.it) - Valcamonica");
    }
    
    private static void generaGerarchie(Database db) {
        System.out.println("\nGenerazione gerarchie (multi-livello)...");
        
        // GERARCHIA 1: Sport (struttura a 3 livelli, 6 foglie)
        Gerarchia sport = creaGerarchaSport();
        db.addGerarchia(sport);
        System.out.println("  ✓ Gerarchia 'Sport' con 6 categorie foglia (3 livelli)");
        
        // GERARCHIA 2: Formazione (struttura a 4 livelli, 8 foglie)
        Gerarchia formazione = creaGerarchaFormazione();
        db.addGerarchia(formazione);
        System.out.println("  ✓ Gerarchia 'Formazione' con 8 categorie foglia (4 livelli)");
        
        // GERARCHIA 3: Servizi Domestici (struttura a 3 livelli, 6 foglie)
        Gerarchia servizi = creaGerarchaServizi();
        db.addGerarchia(servizi);
        System.out.println("  ✓ Gerarchia 'Servizi Domestici' con 6 categorie foglia (3 livelli)");
        
        // Fattori di conversione tra gerarchie
        generaFattoriConversione(db);
        System.out.println("  ✓ Fattori di conversione configurati");
    }
    
    private static Gerarchia creaGerarchaSport() {
        // LIVELLO 0: Radice Sport
        Categoria sport = new Categoria("Sport");
        Gerarchia gerarchiaSport = new Gerarchia(sport);
        
        Campo tipoSport = new Campo("Tipo Sport");
        tipoSport.addValore(new ValoreCampo("Sport di Squadra"));
        tipoSport.addValore(new ValoreCampo("Sport Individuali"));
        sport.setCampo(tipoSport);
        
        // LIVELLO 1: Sport di Squadra
        Categoria sportSquadra = new Categoria("Sport di Squadra");
        Campo campoSquadra = new Campo("Disciplina");
        campoSquadra.addValore(new ValoreCampo("Calcio"));
        campoSquadra.addValore(new ValoreCampo("Basket"));
        campoSquadra.addValore(new ValoreCampo("Pallavolo"));
        sportSquadra.setCampo(campoSquadra);
        TreeNode<Categoria> nodoSquadra = new LinkedMultiTreeNode<>(sportSquadra);
        gerarchiaSport.add(nodoSquadra);
        
        // LIVELLO 2: Foglie Sport di Squadra
        Categoria calcio = new Categoria("Allenamento Calcio");
        Categoria basket = new Categoria("Allenamento Basket");
        Categoria pallavolo = new Categoria("Allenamento Pallavolo");
        nodoSquadra.add(new LinkedMultiTreeNode<>(calcio));
        nodoSquadra.add(new LinkedMultiTreeNode<>(basket));
        nodoSquadra.add(new LinkedMultiTreeNode<>(pallavolo));
        
        // LIVELLO 1: Sport Individuali
        Categoria sportIndividuali = new Categoria("Sport Individuali");
        Campo campoIndividuali = new Campo("Disciplina");
        campoIndividuali.addValore(new ValoreCampo("Tennis"));
        campoIndividuali.addValore(new ValoreCampo("Nuoto"));
        campoIndividuali.addValore(new ValoreCampo("Ciclismo"));
        sportIndividuali.setCampo(campoIndividuali);
        TreeNode<Categoria> nodoIndividuali = new LinkedMultiTreeNode<>(sportIndividuali);
        gerarchiaSport.add(nodoIndividuali);
        
        // LIVELLO 2: Foglie Sport Individuali
        Categoria tennis = new Categoria("Allenamento Tennis");
        Categoria nuoto = new Categoria("Allenamento Nuoto");
        Categoria ciclismo = new Categoria("Allenamento Ciclismo");
        TreeNode<Categoria> nodoTennis = new LinkedMultiTreeNode<>(tennis);
        nodoIndividuali.add(nodoTennis);
        nodoIndividuali.add(new LinkedMultiTreeNode<>(nuoto));
        nodoIndividuali.add(new LinkedMultiTreeNode<>(ciclismo));
        
        // Fattori di conversione
        gerarchiaSport.getFattoreConversione().setNodoRiferimento(nodoTennis);
        gerarchiaSport.getFattoreConversione().addFattoreConversione(0, 3.0); // Radice
        gerarchiaSport.getFattoreConversione().addFattoreConversione(1, 1.5); // Livello intermedio
        
        return gerarchiaSport;
    }
    
    private static Gerarchia creaGerarchaFormazione() {
        // LIVELLO 0: Radice Formazione
        Categoria formazione = new Categoria("Formazione");
        Gerarchia gerarchiaFormazione = new Gerarchia(formazione);
        
        Campo areaFormazione = new Campo("Area");
        areaFormazione.addValore(new ValoreCampo("Scientifica"));
        areaFormazione.addValore(new ValoreCampo("Umanistica"));
        formazione.setCampo(areaFormazione);
        
        // LIVELLO 1: Area Scientifica
        Categoria scientifica = new Categoria("Area Scientifica");
        Campo campoScientifica = new Campo("Materia");
        campoScientifica.addValore(new ValoreCampo("Matematica"));
        campoScientifica.addValore(new ValoreCampo("Informatica"));
        scientifica.setCampo(campoScientifica);
        TreeNode<Categoria> nodoScientifica = new LinkedMultiTreeNode<>(scientifica);
        gerarchiaFormazione.add(nodoScientifica);
        
        // LIVELLO 2: Matematica
        Categoria matematica = new Categoria("Matematica");
        Campo campoMatematica = new Campo("Livello");
        campoMatematica.addValore(new ValoreCampo("Base"));
        campoMatematica.addValore(new ValoreCampo("Avanzato"));
        matematica.setCampo(campoMatematica);
        TreeNode<Categoria> nodoMatematica = new LinkedMultiTreeNode<>(matematica);
        nodoScientifica.add(nodoMatematica);
        
        // LIVELLO 3: Foglie Matematica
        Categoria matBase = new Categoria("Ripetizioni Matematica Base");
        Categoria matAvanzata = new Categoria("Ripetizioni Matematica Avanzata");
        TreeNode<Categoria> nodoMatBase = new LinkedMultiTreeNode<>(matBase);
        nodoMatematica.add(nodoMatBase);
        nodoMatematica.add(new LinkedMultiTreeNode<>(matAvanzata));
        
        // LIVELLO 2: Informatica
        Categoria informatica = new Categoria("Informatica");
        Campo campoInformatica = new Campo("Livello");
        campoInformatica.addValore(new ValoreCampo("Base"));
        campoInformatica.addValore(new ValoreCampo("Avanzato"));
        informatica.setCampo(campoInformatica);
        TreeNode<Categoria> nodoInformatica = new LinkedMultiTreeNode<>(informatica);
        nodoScientifica.add(nodoInformatica);
        
        // LIVELLO 3: Foglie Informatica
        Categoria infBase = new Categoria("Corso Informatica Base");
        Categoria infAvanzata = new Categoria("Corso Informatica Avanzata");
        nodoInformatica.add(new LinkedMultiTreeNode<>(infBase));
        nodoInformatica.add(new LinkedMultiTreeNode<>(infAvanzata));
        
        // LIVELLO 1: Area Umanistica
        Categoria umanistica = new Categoria("Area Umanistica");
        Campo campoUmanistica = new Campo("Materia");
        campoUmanistica.addValore(new ValoreCampo("Lingue"));
        campoUmanistica.addValore(new ValoreCampo("Storia"));
        umanistica.setCampo(campoUmanistica);
        TreeNode<Categoria> nodoUmanistica = new LinkedMultiTreeNode<>(umanistica);
        gerarchiaFormazione.add(nodoUmanistica);
        
        // LIVELLO 2: Lingue (foglie dirette)
        Categoria lingue = new Categoria("Lingue");
        Campo campoLingue = new Campo("Lingua");
        campoLingue.addValore(new ValoreCampo("Inglese"));
        campoLingue.addValore(new ValoreCampo("Francese"));
        lingue.setCampo(campoLingue);
        TreeNode<Categoria> nodoLingue = new LinkedMultiTreeNode<>(lingue);
        nodoUmanistica.add(nodoLingue);
        
        // LIVELLO 3: Foglie Lingue
        Categoria inglese = new Categoria("Ripetizioni Inglese");
        Categoria francese = new Categoria("Ripetizioni Francese");
        nodoLingue.add(new LinkedMultiTreeNode<>(inglese));
        nodoLingue.add(new LinkedMultiTreeNode<>(francese));
        
        // LIVELLO 2: Storia (foglia diretta)
        Categoria storia = new Categoria("Ripetizioni Storia");
        nodoUmanistica.add(new LinkedMultiTreeNode<>(storia));
        
        // Fattori di conversione
        gerarchiaFormazione.getFattoreConversione().setNodoRiferimento(nodoMatBase);
        gerarchiaFormazione.getFattoreConversione().addFattoreConversione(0, 4.0); // Radice
        gerarchiaFormazione.getFattoreConversione().addFattoreConversione(1, 2.5); // Area
        gerarchiaFormazione.getFattoreConversione().addFattoreConversione(2, 1.5); // Materia
        
        return gerarchiaFormazione;
    }
    
    private static Gerarchia creaGerarchaServizi() {
        // LIVELLO 0: Radice Servizi Domestici
        Categoria servizi = new Categoria("Servizi Domestici");
        Gerarchia gerarchiaServizi = new Gerarchia(servizi);
        
        Campo tipoServizio = new Campo("Tipo");
        tipoServizio.addValore(new ValoreCampo("Esterni"));
        tipoServizio.addValore(new ValoreCampo("Interni"));
        servizi.setCampo(tipoServizio);
        
        // LIVELLO 1: Servizi Esterni
        Categoria esterni = new Categoria("Servizi Esterni");
        Campo campoEsterni = new Campo("Categoria");
        campoEsterni.addValore(new ValoreCampo("Giardino"));
        campoEsterni.addValore(new ValoreCampo("Manutenzione"));
        esterni.setCampo(campoEsterni);
        TreeNode<Categoria> nodoEsterni = new LinkedMultiTreeNode<>(esterni);
        gerarchiaServizi.add(nodoEsterni);
        
        // LIVELLO 2: Foglie Esterni
        Categoria giardino = new Categoria("Giardinaggio");
        Categoria manutenzioneEsterna = new Categoria("Manutenzione Esterna");
        TreeNode<Categoria> nodoGiardino = new LinkedMultiTreeNode<>(giardino);
        nodoEsterni.add(nodoGiardino);
        nodoEsterni.add(new LinkedMultiTreeNode<>(manutenzioneEsterna));
        
        // LIVELLO 1: Servizi Interni
        Categoria interni = new Categoria("Servizi Interni");
        Campo campoInterni = new Campo("Categoria");
        campoInterni.addValore(new ValoreCampo("Pulizia"));
        campoInterni.addValore(new ValoreCampo("Riparazioni"));
        interni.setCampo(campoInterni);
        TreeNode<Categoria> nodoInterni = new LinkedMultiTreeNode<>(interni);
        gerarchiaServizi.add(nodoInterni);
        
        // LIVELLO 2: Foglie Interni
        Categoria pulizia = new Categoria("Pulizie Domestiche");
        Categoria riparazioni = new Categoria("Piccole Riparazioni");
        nodoInterni.add(new LinkedMultiTreeNode<>(pulizia));
        nodoInterni.add(new LinkedMultiTreeNode<>(riparazioni));
        
        // LIVELLO 1: Servizio aggiuntivo diretto (2 foglie)
        Categoria cucina = new Categoria("Cucina");
        Campo campoCucina = new Campo("Tipo");
        campoCucina.addValore(new ValoreCampo("Preparazione Pasti"));
        campoCucina.addValore(new ValoreCampo("Catering"));
        cucina.setCampo(campoCucina);
        TreeNode<Categoria> nodoCucina = new LinkedMultiTreeNode<>(cucina);
        gerarchiaServizi.add(nodoCucina);
        
        Categoria preparazionePasti = new Categoria("Preparazione Pasti");
        Categoria catering = new Categoria("Catering Eventi");
        nodoCucina.add(new LinkedMultiTreeNode<>(preparazionePasti));
        nodoCucina.add(new LinkedMultiTreeNode<>(catering));
        
        // Fattori di conversione
        gerarchiaServizi.getFattoreConversione().setNodoRiferimento(nodoGiardino);
        gerarchiaServizi.getFattoreConversione().addFattoreConversione(0, 3.0); // Radice
        gerarchiaServizi.getFattoreConversione().addFattoreConversione(1, 1.8); // Tipo servizio
        
        return gerarchiaServizi;
    }
    
    private static void generaFattoriConversione(Database db) {
        ConversioneGerarchie conversione = db.getConversioneGerarchie();
        
        // Ottieni le gerarchie cercandole per nome della radice
        // (non possiamo usare gli indici perché Set non mantiene l'ordine)
        Gerarchia sportGerarchia = null;
        Gerarchia formazioneGerarchia = null;
        Gerarchia serviziGerarchia = null;
        
        for (Gerarchia g : db.getGerarchie()) {
            String nomeRadice = g.root().data().getNome();
            if (nomeRadice.equals("Sport")) {
                sportGerarchia = g;
            } else if (nomeRadice.equals("Formazione")) {
                formazioneGerarchia = g;
            } else if (nomeRadice.equals("Servizi Domestici")) {
                serviziGerarchia = g;
            }
        }
        
        // Ottieni le categorie foglia di riferimento (usate per i fattori di conversione)
        Categoria tennis = sportGerarchia.getFattoreConversione().getFogliaRiferimento().data();
        Categoria matBase = formazioneGerarchia.getFattoreConversione().getFogliaRiferimento().data();
        Categoria giardinaggio = serviziGerarchia.getFattoreConversione().getFogliaRiferimento().data();
        
        // Fattori di conversione tra le foglie di riferimento delle gerarchie
        // Sport (Tennis) <-> Formazione (Mat Base)
        conversione.addFattoreConversione(tennis, matBase, 1.3);
        // Sport (Tennis) <-> Servizi Domestici (Giardinaggio)
        conversione.addFattoreConversione(tennis, giardinaggio, 0.7);
        // Formazione (Mat Base) <-> Servizi Domestici (Giardinaggio)
        conversione.addFattoreConversione(matBase, giardinaggio, 1.6);
    }
    
    private static void generaProposte(Database db) {
        System.out.println("\nGenerazione proposte...");
        
        // Recupera fruitori
        Fruitore mario = null;
        Fruitore giulia = null;
        Fruitore luca = null;
        
        for (Utente u : db.getUtenti()) {
            if (u instanceof Fruitore) {
                Fruitore f = (Fruitore) u;
                if (f.getUsername().equals("mariorossi")) {
                    mario = f;
                } else if (f.getUsername().equals("giuliabianchi")) {
                    giulia = f;
                } else if (f.getUsername().equals("lucaverdi")) {
                    luca = f;
                }
            }
        }
        
        // Recupera gerarchie e foglie (con nuove gerarchie multi-livello)
        Gerarchia sport = db.getGerarchia(0);
        Gerarchia formazione = db.getGerarchia(1);
        Gerarchia servizi = db.getGerarchia(2);
        
        // Sport: 6 foglie (Calcio, Basket, Pallavolo, Tennis, Nuoto, Ciclismo)
        TreeNode<Categoria> calcio = sport.getFoglie().get(0);
        TreeNode<Categoria> basket = sport.getFoglie().get(1);
        TreeNode<Categoria> tennis = sport.getFoglie().get(3);
        TreeNode<Categoria> nuoto = sport.getFoglie().get(4);
        
        // Formazione: 8 foglie (Mat Base, Mat Avanzata, Inf Base, Inf Avanzata, Inglese, Francese, Storia)
        TreeNode<Categoria> matBase = formazione.getFoglie().get(0);
        TreeNode<Categoria> matAvanzata = formazione.getFoglie().get(1);
        TreeNode<Categoria> infBase = formazione.getFoglie().get(2);
        TreeNode<Categoria> inglese = formazione.getFoglie().get(4);
        
        // Servizi: 6 foglie (Giardinaggio, Manutenzione Esterna, Pulizie, Riparazioni, Preparazione Pasti, Catering)
        TreeNode<Categoria> giardinaggio = servizi.getFoglie().get(0);
        TreeNode<Categoria> pulizie = servizi.getFoglie().get(2);
        TreeNode<Categoria> riparazioni = servizi.getFoglie().get(3);
        TreeNode<Categoria> preparazionePasti = servizi.getFoglie().get(4);
        
        // 10 PROPOSTE APERTE
        db.aggiungiProposta(new Proposta(mario, 
            new Prestazione(calcio, 2), 
            new Prestazione(matBase, 3), 
            StatoProposta.APERTA));
        
        db.aggiungiProposta(new Proposta(mario, 
            new Prestazione(inglese, 2), 
            new Prestazione(tennis, 2), 
            StatoProposta.APERTA));
        
        db.aggiungiProposta(new Proposta(giulia, 
            new Prestazione(matAvanzata, 3), 
            new Prestazione(giardinaggio, 4), 
            StatoProposta.APERTA));
        
        db.aggiungiProposta(new Proposta(giulia, 
            new Prestazione(nuoto, 1), 
            new Prestazione(infBase, 2), 
            StatoProposta.APERTA));
        
        db.aggiungiProposta(new Proposta(luca, 
            new Prestazione(giardinaggio, 3), 
            new Prestazione(basket, 2), 
            StatoProposta.APERTA));
        
        db.aggiungiProposta(new Proposta(luca, 
            new Prestazione(riparazioni, 2), 
            new Prestazione(inglese, 3), 
            StatoProposta.APERTA));
        
        db.aggiungiProposta(new Proposta(mario, 
            new Prestazione(tennis, 3), 
            new Prestazione(pulizie, 2), 
            StatoProposta.APERTA));
        
        db.aggiungiProposta(new Proposta(giulia, 
            new Prestazione(infBase, 2), 
            new Prestazione(nuoto, 2), 
            StatoProposta.APERTA));
        
        db.aggiungiProposta(new Proposta(luca, 
            new Prestazione(calcio, 1), 
            new Prestazione(matBase, 2), 
            StatoProposta.APERTA));
        
        db.aggiungiProposta(new Proposta(mario, 
            new Prestazione(preparazionePasti, 2), 
            new Prestazione(tennis, 2), 
            StatoProposta.APERTA));
        
        System.out.println("  ✓ 10 proposte APERTE");
        
        // 2 PROPOSTE RITIRATE
        Proposta p4 = new Proposta(mario, 
            new Prestazione(riparazioni, 4), 
            new Prestazione(calcio, 3), 
            StatoProposta.APERTA);
        p4.ritiraProposta();
        db.aggiungiProposta(p4);
        
        Proposta p5 = new Proposta(giulia, 
            new Prestazione(nuoto, 2), 
            new Prestazione(inglese, 2), 
            StatoProposta.APERTA);
        p5.ritiraProposta();
        db.aggiungiProposta(p5);
        
        System.out.println("  ✓ 2 proposte RITIRATE");
    }
    
    private static void generaInsiemiChiusi(Database db) {
        System.out.println("\nGenerazione insiemi chiusi...");
        
        // Recupera fruitori
        Fruitore mario = null;
        Fruitore giulia = null;
        Fruitore luca = null;
        
        for (Utente u : db.getUtenti()) {
            if (u instanceof Fruitore) {
                Fruitore f = (Fruitore) u;
                if (f.getUsername().equals("mariorossi")) {
                    mario = f;
                } else if (f.getUsername().equals("giuliabianchi")) {
                    giulia = f;
                } else if (f.getUsername().equals("lucaverdi")) {
                    luca = f;
                }
            }
        }
        
        // Recupera gerarchie e categorie
        Gerarchia sport = null;
        Gerarchia formazione = null;
        Gerarchia servizi = null;
        
        for (Gerarchia g : db.getGerarchie()) {
            String nomeRadice = g.root().data().getNome();
            if (nomeRadice.equals("Sport")) {
                sport = g;
            } else if (nomeRadice.equals("Formazione")) {
                formazione = g;
            } else if (nomeRadice.equals("Servizi Domestici")) {
                servizi = g;
            }
        }
        
        // Foglie per Sport
        TreeNode<Categoria> calcio = sport.getFoglie().get(0);
        TreeNode<Categoria> basket = sport.getFoglie().get(1);
        TreeNode<Categoria> pallavolo = sport.getFoglie().get(2);
        TreeNode<Categoria> tennis = sport.getFoglie().get(3);
        
        // Foglie per Formazione
        TreeNode<Categoria> matBase = formazione.getFoglie().get(0);
        TreeNode<Categoria> infBase = formazione.getFoglie().get(2);
        TreeNode<Categoria> inglese = formazione.getFoglie().get(4);
        
        // Foglie per Servizi
        TreeNode<Categoria> giardinaggio = servizi.getFoglie().get(0);
        TreeNode<Categoria> pulizie = servizi.getFoglie().get(2);
        TreeNode<Categoria> riparazioni = servizi.getFoglie().get(3);
        
        // INSIEME CHIUSO 1: Ciclo a 3 con Mario→Giulia→Luca→Mario (3 scambi)
        InsiemeChiuso insieme1 = new InsiemeChiuso();
        
        // Proposte per insieme 1
        Proposta prop1A = new Proposta(mario, 
            new Prestazione(tennis, 2), 
            new Prestazione(matBase, 2), 
            StatoProposta.APERTA);
        
        Proposta prop1B = new Proposta(giulia, 
            new Prestazione(matBase, 2), 
            new Prestazione(giardinaggio, 2), 
            StatoProposta.APERTA);
        
        Proposta prop1C = new Proposta(luca, 
            new Prestazione(giardinaggio, 2), 
            new Prestazione(tennis, 2), 
            StatoProposta.APERTA);
        
        // Aggiungi proposte all'insieme (vengono automaticamente chiuse)
        insieme1.aggiungiProposta(prop1A);
        insieme1.aggiungiProposta(prop1B);
        insieme1.aggiungiProposta(prop1C);
        
        // Crea scambi (chi offre cosa a chi)
        // Mario offre Tennis a Luca (chiude il ciclo)
        insieme1.aggiungiScambio(new Scambio(mario, luca, new Prestazione(tennis, 2)));
        // Giulia offre MatBase a Mario
        insieme1.aggiungiScambio(new Scambio(giulia, mario, new Prestazione(matBase, 2)));
        // Luca offre Giardinaggio a Giulia
        insieme1.aggiungiScambio(new Scambio(luca, giulia, new Prestazione(giardinaggio, 2)));
        
        db.aggiungiInsiemeChiuso(insieme1);
        db.aggiungiProposta(prop1A);
        db.aggiungiProposta(prop1B);
        db.aggiungiProposta(prop1C);
        
        System.out.println("  ✓ Insieme chiuso 1: ciclo Mario→Giulia→Luca→Mario (3 scambi)");
        
        // INSIEME CHIUSO 2: Ciclo a 2 con Mario↔Giulia (2 scambi)
        InsiemeChiuso insieme2 = new InsiemeChiuso();
        
        Proposta prop2A = new Proposta(mario, 
            new Prestazione(calcio, 3), 
            new Prestazione(inglese, 3), 
            StatoProposta.APERTA);
        
        Proposta prop2B = new Proposta(giulia, 
            new Prestazione(inglese, 3), 
            new Prestazione(calcio, 3), 
            StatoProposta.APERTA);
        
        insieme2.aggiungiProposta(prop2A);
        insieme2.aggiungiProposta(prop2B);
        
        // Mario offre Calcio a Giulia
        insieme2.aggiungiScambio(new Scambio(mario, giulia, new Prestazione(calcio, 3)));
        // Giulia offre Inglese a Mario
        insieme2.aggiungiScambio(new Scambio(giulia, mario, new Prestazione(inglese, 3)));
        
        insieme2.setNotificato(true); // Questo è già stato notificato
        
        db.aggiungiInsiemeChiuso(insieme2);
        db.aggiungiProposta(prop2A);
        db.aggiungiProposta(prop2B);
        
        System.out.println("  ✓ Insieme chiuso 2: ciclo Mario↔Giulia (2 scambi, notificato)");
        
        // INSIEME CHIUSO 3: Ciclo complesso a 3 (tutti e 3 i fruitori)
        InsiemeChiuso insieme3 = new InsiemeChiuso();
        
        Proposta prop3A = new Proposta(mario, 
            new Prestazione(pulizie, 2), 
            new Prestazione(basket, 2), 
            StatoProposta.APERTA);
        
        Proposta prop3B = new Proposta(giulia, 
            new Prestazione(basket, 2), 
            new Prestazione(riparazioni, 2), 
            StatoProposta.APERTA);
        
        Proposta prop3C = new Proposta(luca, 
            new Prestazione(riparazioni, 2), 
            new Prestazione(pulizie, 2), 
            StatoProposta.APERTA);
        
        insieme3.aggiungiProposta(prop3A);
        insieme3.aggiungiProposta(prop3B);
        insieme3.aggiungiProposta(prop3C);
        
        // Scambi per insieme 3
        insieme3.aggiungiScambio(new Scambio(mario, luca, new Prestazione(pulizie, 2)));
        insieme3.aggiungiScambio(new Scambio(giulia, mario, new Prestazione(basket, 2)));
        insieme3.aggiungiScambio(new Scambio(luca, giulia, new Prestazione(riparazioni, 2)));
        
        db.aggiungiInsiemeChiuso(insieme3);
        db.aggiungiProposta(prop3A);
        db.aggiungiProposta(prop3B);
        db.aggiungiProposta(prop3C);
        
        System.out.println("  ✓ Insieme chiuso 3: ciclo complesso con Pulizie→Basket→Riparazioni (3 scambi)");
    }
    
    private static void serializzaDati(Database db) {
        db.serializeUtenti(DATA_DIR + "utenti.ser");
        System.out.println("  ✓ utenti.ser (2 configuratori + 3 fruitori)");
        
        db.serializeComprensori(DATA_DIR + "comprensori.ser");
        System.out.println("  ✓ comprensori.ser (2 comprensori)");
        
        db.serializeGerarchie(DATA_DIR + "gerarchie.ser");
        System.out.println("  ✓ gerarchie.ser (3 gerarchie)");
        
        db.serializeConversioneGerarchie(DATA_DIR + "conversione.ser");
        System.out.println("  ✓ conversione.ser (fattori di conversione)");
        
        db.serializeProposte(DATA_DIR + "proposte.ser");
        System.out.println("  ✓ proposte.ser (10 aperte + 2 ritirate + 9 chiuse in insiemi)");
        
        db.serializeInsiemiChiusi(DATA_DIR + "insiemechiuso.ser");
        System.out.println("  ✓ insiemechiuso.ser (3 insiemi chiusi)");
    }
}
