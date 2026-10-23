package controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import view.MockView;
import constants.ControllerConstants;
import controller.*;
import model.*;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test unitari e funzionali per il Controller.
 * Utilizza una MockView per simulare l'input dell'utente e
 * inietta un database pulito prima di ogni test per garantire l'isolamento.
 */
class ControllerTest {

    private Database database;
    private MockView mockView;
    private Controller controller;

    /**
     * Metodo di setup eseguito prima di ogni test (@Test).
     * Inizializza un database pulito, una vista finta (MockView)
     * e il Controller, iniettando le dipendenze (DI).
     * Questo assicura che il controller e il test operino
     * SULLO STESSO database.
     */
    @BeforeEach
    void setUp() {
        // 1. Crea le dipendenze
        database = new Database();
        mockView = new MockView();

        // 2. Inietta le dipendenze nel costruttore del Controller
        // Ora 'controller' e 'database' si riferiscono allo stesso oggetto Database.
        controller = new Controller(mockView, database);
    }

    // ==============================================================================
    // TEST DI LOGIN
    // ==============================================================================

    /**
     * Testa lo scenario di successo del login per un Configuratore.
     * Verifica che il metodo loginUtente() restituisca l'utente corretto
     * quando le credenziali sono valide.
     */
    @Test
    void testLoginUtente_Success_Configuratore() {
        // 1. Arrange (Prepara)
        // Aggiungi un utente al database del test
        database.registraConfiguratore("admin", "Admin123!");

        // Simula l'input dell'utente nella MockView
        mockView.addSimulatedInput("admin"); // Username
        mockView.addSimulatedInput("Admin123!"); // Password

        // 2. Act (Agisci)
        // Chiama il metodo pubblico da testare.
        // Il controller cercherà nel 'database' che abbiamo popolato.
        Utente utente = controller.loginUtente();

        // 3. Assert (Verifica)
        assertNotNull(utente, "L'utente non dovrebbe essere null per un login valido");
        assertEquals("admin", utente.getUsername());
        assertTrue(utente.isConfig(), "L'utente dovrebbe avere i privilegi di configuratore");
    }

    /**
     * Testa lo scenario di fallimento del login (password errata).
     * Verifica che loginUtente() restituisca null.
     */
    @Test
    void testLoginUtente_Failed_WrongPassword() {
        // 1. Arrange
        database.registraConfiguratore("admin", "Admin123!");

        mockView.addSimulatedInput("admin"); // Username corretto
        mockView.addSimulatedInput("wrongpassword"); // Password Sbagliata

        // 2. Act
        Utente utente = controller.loginUtente();

        // 3. Assert
        assertNull(utente, "L'utente dovrebbe essere null per una password errata");
    }

    /**
     * Testa lo scenario di fallimento del login (utente non esistente).
     * Verifica che loginUtente() restituisca null.
     */
    @Test
    void testLoginUtente_Failed_UserNotFound() {
        // 1. Arrange
        // Il database è vuoto

        mockView.addSimulatedInput("nonEsiste"); // Username
        mockView.addSimulatedInput("password"); // Password

        // 2. Act
        Utente utente = controller.loginUtente();

        // 3. Assert
        assertNull(utente, "L'utente dovrebbe essere null se non esiste");
    }

    /**
     * Test del metodo loginUtente() quando non ci sono utenti registrati.
     * Verifica che il metodo restituisca null quando il database è vuoto.
     * 
     * NOTA: Questo test sostituisce il test di start() che era problematico
     * perché start() contiene un loop infinito difficile da testare.
     */
    @Test
    void testLoginUtente_NoUsersInDatabase() {
        // 1. Arrange
        // Il database è vuoto (dal setUp)
        mockView.addSimulatedInput("anyuser");
        mockView.addSimulatedInput("anypassword");

        // 2. Act
        Utente utente = controller.loginUtente();

        // 3. Assert
        assertNull(utente, "Il metodo dovrebbe restituire null quando non ci sono utenti nel database");
    }

    // ==============================================================================
    // TEST DI CREAZIONE COMPRENSORIO
    // ==============================================================================

    /**
     * Testa lo scenario di successo della creazione di un comprensorio
     * con un singolo comune.
     */
    @Test
    void testCreateComprensorio_Success() {
        // 1. Arrange
        // Simula l'input dell'utente per creare un comprensorio
        mockView.addSimulatedInput("Lombardia"); // Nome Comprensorio
        mockView.addSimulatedInput("Milano"); // Primo comune
        mockView.addSimulatedInput("false"); // "Vuoi aggiungere un altro comune?" -> no (false)

        // 2. Act
        boolean risultato = controller.createComprensorio();

        // 3. Assert
        assertTrue(risultato, "Il metodo createComprensorio dovrebbe restituire true");

        // Controlla lo stato del database DOPO l'azione
        assertEquals(1, database.getComprensori().size(), "Il database dovrebbe contenere 1 comprensorio");

        Comprensorio c = database.getComprensori().iterator().next();
        assertEquals("Lombardia", c.getNome());
        assertTrue(c.hasComune("Milano"), "Il comprensorio dovrebbe contenere 'Milano'");
    }

    /**
     * Testa l'annullamento della creazione del comprensorio
     * da parte dell'utente (inserendo "0").
     */
    @Test
    void testCreateComprensorio_Cancel() {
        // 1. Arrange
        // L'utente digita "0" per annullare alla prima richiesta (nome)
        mockView.addSimulatedInput("0");

        // 2. Act
        boolean risultato = controller.createComprensorio();

        // 3. Assert
        assertFalse(risultato, "Il metodo dovrebbe restituire false se l'utente annulla");
        assertEquals(0, database.getComprensori().size(), "Il database dovrebbe rimanere vuoto");
    }

    /**
     * Testa il fallimento della creazione a causa di un nome duplicato.
     * L'utente prova a inserire un nome duplicato, riceve un errore,
     * e poi annulla.
     */
    @Test
    void testCreateComprensorio_NomeDuplicato() {
        // 1. Arrange
        // Aggiungi un comprensorio esistente
        database.addComprensorio(new Comprensorio("Lombardia"));

        // Simula l'utente che prova a inserire un nome duplicato,
        // e poi annulla alla successiva richiesta
        mockView.addSimulatedInput("Lombardia"); // Nome duplicato
        mockView.addSimulatedInput("0"); // Annulla

        // 2. Act
        boolean risultato = controller.createComprensorio();

        // 3. Assert
        assertFalse(risultato, "Il metodo dovrebbe restituire false");
        assertEquals(1, database.getComprensori().size(), "Il database deve avere ancora 1 solo elemento");

        // Verifica che sia stato mostrato il messaggio di errore corretto
        assertEquals(ControllerConstants.ERROR_NOME_DUPLICATO, mockView.lastErrorMessage);
    }

    // ==============================================================================
    // TEST DI REGISTRAZIONE
    // ==============================================================================

    /**
     * Testa la registrazione di un nuovo Fruitore con tutti i dati validi.
     * Questo test verifica direttamente il database invece del controller
     * perché la registrazione via controller richiede interazione complessa.
     */
    @Test
    void testRegistraFruitore_Success() {
        // 1. Arrange
        // Crea un comprensorio da associare al fruitore
        Comprensorio comprensorio = new Comprensorio("Lombardia");
        comprensorio.addComune("Milano");
        database.addComprensorio(comprensorio);

        // 2. Act
        boolean risultato = database.registraFruitore(
                "mario",
                "Mario123!",
                "mario@test.it",
                comprensorio);
        // 3. Assert
        assertTrue(risultato, "La registrazione dovrebbe avere successo");
        assertTrue(database.hasUtente("mario"), "L'utente dovrebbe essere nel database");

        // Verifica che sia effettivamente un fruitore
        Utente utente = database.loginUtente("mario", "Mario123!");
        assertNotNull(utente, "Dovrebbe poter fare login");
        assertFalse(utente.isConfig(), "Dovrebbe essere un fruitore, non un configuratore");
        assertTrue(utente instanceof Fruitore, "Dovrebbe essere un'istanza di Fruitore");
    }

    /**
     * Testa che non si possano registrare due utenti con lo stesso username.
     */
    @Test
    void testRegistraFruitore_UsernameDuplicato() {
        // 1. Arrange
        Comprensorio comprensorio = new Comprensorio("Lombardia");
        database.addComprensorio(comprensorio);

        // Registra il primo utente
        database.registraFruitore("mario", "Mario123!", "mario@test.it", comprensorio);

        // 2. Act - Prova a registrare un secondo utente con lo stesso username
        boolean risultato = database.registraFruitore(
                "mario", // Stesso username
                "DifferentPass123!",
                "altro@test.it",
                comprensorio);
        // 3. Assert
        assertFalse(risultato, "La registrazione con username duplicato dovrebbe fallire");
        assertEquals(1, database.getUtenti().size(), "Dovrebbe esserci solo 1 utente");
    }

    // ==============================================================================
    // TEST DI PROPOSTA DI SCAMBIO
    // ==============================================================================

    /**
     * Test BLACK-BOX per la creazione di una proposta di scambio.
     *
     * Scenario: Un fruitore crea una proposta per offrire ore di lavoro
     * in una determinata categoria e richiedere ore in un'altra categoria.
     *
     * Questo test rappresenta uno dei casi d'uso principali dell'applicazione
     * ed è ideale per la presentazione del punto 6 (Testing).
     */
    @Test
    void testPropostaScambio_CreazioneSemplice() {
        // 1. Arrange - Prepara il contesto completo

        // 1a. Crea un comprensorio
        Comprensorio comprensorio = new Comprensorio("Lombardia");
        comprensorio.addComune("Milano");
        database.addComprensorio(comprensorio);

        // 1b. Registra un fruitore
        database.registraFruitore("mario", "Mario123!", "mario@test.it", comprensorio);
        Utente utenteGenerico = database.loginUtente("mario", "Mario123!");
        assertTrue(utenteGenerico instanceof Fruitore, "Dovrebbe essere un Fruitore");
        Fruitore fruitore = (Fruitore) utenteGenerico;

        // 1c. Crea una gerarchia con due categorie foglia
        Campo campo = new Campo("Tipo");
        campo.addValore(new ValoreCampo("A"));
        campo.addValore(new ValoreCampo("B"));

        Categoria categoriaRadice = new Categoria("Servizi", campo);
        Categoria categoriaGiardinaggio = new Categoria("Giardinaggio", null); // Foglia
        Categoria categoriaIdraulica = new Categoria("Idraulica", null); // Foglia

        Gerarchia gerarchia = new Gerarchia(categoriaRadice);
        Gerarchia fogliaGiardinaggio = new Gerarchia(categoriaGiardinaggio);
        Gerarchia fogliaIdraulica = new Gerarchia(categoriaIdraulica);
        gerarchia.add(fogliaGiardinaggio);
        gerarchia.add(fogliaIdraulica);

        // Ottieni i nodi foglia
        com.scalified.tree.TreeNode<Categoria> nodoGiardinaggio = gerarchia.getFoglie().get(0);
        com.scalified.tree.TreeNode<Categoria> nodoIdraulica = gerarchia.getFoglie().get(1);

        // Aggiungi fattore di conversione (necessario per la gerarchia)
        gerarchia.getFattoreConversione().setNodoRiferimento(nodoGiardinaggio);

        database.addGerarchia(gerarchia);

        // 2. Act - Crea la proposta
        // Il fruitore OFFRE 10 ore di Giardinaggio
        // Il fruitore RICHIEDE 10 ore di Idraulica
        Prestazione offerta = new Prestazione(nodoGiardinaggio, 10);
        Prestazione richiesta = new Prestazione(nodoIdraulica, 10);

        Proposta proposta = new Proposta(
                fruitore,
                richiesta,
                offerta,
                StatoProposta.APERTA);
        database.aggiungiProposta(proposta);

        // 3. Assert - Verifica che la proposta sia stata creata correttamente
        List<Proposta> proposte = database.getPropostePerStato(StatoProposta.APERTA);
        assertEquals(1, proposte.size(), "Dovrebbe esserci 1 proposta");

        // Verifica i dettagli della proposta
        Proposta propostaRecuperata = proposte.get(0);
        assertNotNull(propostaRecuperata, "La proposta dovrebbe esistere");
        assertEquals(fruitore, propostaRecuperata.getPropostitore(), "Il fruitore deve corrispondere");
        assertEquals(StatoProposta.APERTA, propostaRecuperata.getStato(), "Lo stato deve essere APERTA");

        // Verifica le prestazioni
        assertEquals(10, propostaRecuperata.getOfferta().getOre(), "Ore offerte devono essere 10");
        assertEquals(10, propostaRecuperata.getRichiesta().getOre(), "Ore richieste devono essere 10");
        assertEquals("Giardinaggio", propostaRecuperata.getOfferta().getCategoria().data().getNome());
        assertEquals("Idraulica", propostaRecuperata.getRichiesta().getCategoria().data().getNome());
    }

    /**
     * Test per verificare che una proposta non possa essere creata senza fruitore.
     */
    @Test
    void testPropostaScambio_FallimentoSenzaFruitore() {
        // 1. Arrange
        Comprensorio comprensorio = new Comprensorio("Lombardia");
        database.addComprensorio(comprensorio);

        // Crea una gerarchia semplice
        Campo campo = new Campo("Tipo");
        campo.addValore(new ValoreCampo("A"));
        Categoria categoriaRadice = new Categoria("Servizi", campo);
        Categoria categoriaFoglia = new Categoria("Giardinaggio", null);

        Gerarchia gerarchia = new Gerarchia(categoriaRadice);
        gerarchia.add(new Gerarchia(categoriaFoglia));
        com.scalified.tree.TreeNode<Categoria> nodo = gerarchia.getFoglie().get(0);

        gerarchia.getFattoreConversione().setNodoRiferimento(nodo);
        database.addGerarchia(gerarchia);

        Prestazione prestazione = new Prestazione(nodo, 10);

        // 2. Act & Assert
        // Tentare di creare una proposta con fruitore null dovrebbe lanciare
        // un'eccezione
        assertThrows(NullPointerException.class, () -> {
            new Proposta(null, prestazione, prestazione, StatoProposta.APERTA);
        }, "Dovrebbe lanciare NullPointerException per fruitore null");
    }

    /**
     * Test per verificare lo scenario in cui un fruitore ritira una proposta.
     */
    @Test
    void testPropostaScambio_RitiroProposta() {
        // 1. Arrange
        Comprensorio comprensorio = new Comprensorio("Lombardia");
        database.addComprensorio(comprensorio);

        database.registraFruitore("mario", "Mario123!", "mario@test.it", comprensorio);
        Fruitore fruitore = (Fruitore) database.loginUtente("mario", "Mario123!");

        // Crea gerarchia e proposta
        Campo campo = new Campo("Tipo");
        campo.addValore(new ValoreCampo("A"));
        Categoria categoriaRadice = new Categoria("Servizi", campo);
        Categoria categoriaFoglia = new Categoria("Giardinaggio", null);

        Gerarchia gerarchia = new Gerarchia(categoriaRadice);
        gerarchia.add(new Gerarchia(categoriaFoglia));
        com.scalified.tree.TreeNode<Categoria> nodo = gerarchia.getFoglie().get(0);

        gerarchia.getFattoreConversione().setNodoRiferimento(nodo);
        database.addGerarchia(gerarchia);

        Prestazione prestazione = new Prestazione(nodo, 10);
        Proposta proposta = new Proposta(fruitore, prestazione, prestazione, StatoProposta.APERTA);
        database.aggiungiProposta(proposta);

        // 2. Act - Ritira la proposta
        proposta.ritiraProposta();

        // 3. Assert
        assertEquals(StatoProposta.RITIRATA, proposta.getStato(),
                "Lo stato della proposta dovrebbe essere RITIRATA");
    }
}