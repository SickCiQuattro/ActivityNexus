# Analisi Completa dei Pattern e Refactoring per ActivityNexus

## Introduzione

Questa analisi esamina il codice del progetto ActivityNexus per identificare opportunità di applicazione di pattern di design e refactoring. L'obiettivo è migliorare la qualità del codice, la manutenibilità e l'aderenza ai principi di design orientato agli oggetti.

## Pattern GRASP Identificati

### 1. Information Expert (Esperto nell'Informazione)

**Applicazione**: La classe `Database` è già un buon esempio di Information Expert, poiché gestisce tutte le operazioni relative ai dati (utenti, comprensori, gerarchie, proposte).

La classe Database viola il pattern Information Expert perché contiene troppa logica di filtraggio e ricerca che potrebbe essere delegata alle classi che hanno le informazioni.

**Codice Esistente** (da Database.java):

```java
public List<Proposta> getProposteAperteStessoComprensorio(Utente utente, boolean includiUtente) {
    // Cast a Fruitore e recupero del comprensorio
    Fruitore fruitoreRiferimento = (Fruitore) utente;
    Comprensorio comprensorioRiferimento = fruitoreRiferimento.getComprensorio();

    // Lista per le proposte aperte dello stesso comprensorio
    List<Proposta> proposteAperte = new ArrayList<>();

    // Filtra tutte le proposte aperte
    List<Proposta> tutteProposteAperte = getPropostePerStato(StatoProposta.APERTA);

    for (Proposta proposta : tutteProposteAperte) {
        Fruitore proponente = proposta.getPropostitore();

        // Controlla che il proponente abbia lo stesso comprensorio
        if (proponente.getComprensorio().equals(comprensorioRiferimento)) {
            // Se non dobbiamo includere l'utente di riferimento e questo è la proposta
            // dell'utente, la saltiamo
            if (!includiUtente && proponente.equals(fruitoreRiferimento)) {
                continue;
            }

            proposteAperte.add(proposta);
        }
    }

    return proposteAperte;
}
```

**Problema**:

❌ Database deve conoscere la struttura interna di Proposta e Fruitore
❌ Logica di filtraggio sparsa in più classi
❌ Difficile da testare e mantenere

**Codice Refactored**:

```java
// Proposta.java - La classe Proposta è l'esperto delle sue informazioni
public class Proposta {
    // ... campi esistenti ...

    /**
     * Verifica se questa proposta appartiene allo stesso comprensorio del fruitore specificato.
     * PATTERN: Information Expert - Proposta è l'esperto delle sue informazioni
     */
    public boolean appartieneStoComprensorio(Comprensorio comprensorio) {
        return this.propostitore.getComprensorio().equals(comprensorio);
    }

    /**
     * Verifica se questa proposta è stata creata dal fruitore specificato.
     */
    public boolean isDiPropostitore(Fruitore fruitore) {
        return this.propostitore.equals(fruitore);
    }

    /**
     * Verifica se questa proposta è aperta.
     */
    public boolean isAperta() {
        return this.stato == StatoProposta.APERTA;
    }
}

// Database.java - Versione semplificata usando Information Expert
public List<Proposta> getProposteAperteStessoComprensorio(Utente utente, boolean includiUtente) {
    Fruitore fruitore = (Fruitore) utente;
    Comprensorio comprensorio = fruitore.getComprensorio();

    return proposte.stream()
        .filter(Proposta::isAperta)
        .filter(p -> p.appartieneStoComprensorio(comprensorio))
        .filter(p -> includiUtente || !p.isDiPropostitore(fruitore))
        .collect(Collectors.toList());
}
```

**Miglioramento**: Il metodo delega correttamente la logica di filtraggio alla classe `Database`, che ha accesso diretto ai dati e mantiene tutte le proposte centralizzate.

**Benefici**:

-   Riduce l'accoppiamento: il Controller non conosce i dettagli interni di come vengono filtrate le proposte
-   Mantiene la responsabilità dei dati centralizzata nel Database
-   Facilita la manutenzione: se cambia la logica di filtraggio, si modifica un solo punto
-   Migliora la testabilità: è facile mockare il Database nei test

### 2. Low Coupling (Basso Accoppiamento)

Il Controller ha un accoppiamento alto con UserUtil, Database, IView, e molte altre classi.

Metrica attuale del Controller:
Controller:

-   Accoppiamento (CBO): 11-12 classi
-   Dipendenze dirette: Database, IView, UserUtil, Utente, Fruitore,
    Configuratore, Comprensorio, Gerarchia, Categoria, Campo, Proposta, etc.

**Applicazione**: Separare la logica di validazione delle credenziali in `UserUtil` è un passo verso il basso accoppiamento.

**Codice Esistente** (da Controller.java e UserUtil.java):

```java
// In Controller.java
public void login() {
    String username = view.getInput("Username: ");
    String password = view.getInput("Password: ");

    if (!UserUtil.isValidEmail(username) || !UserUtil.isValidPassword(password)) {
        view.showError("Credenziali non valide");
        return;
    }

    Utente utente = database.getUtente(username);
    // ... resto della logica di login
}
```

**Miglioramento**: Utilizzare interfacce per dipendenze esterne e ridurre il numero di dipendenze dirette tra classi.

```java
// Interfaccia per la validazione
public interface CredentialValidator {
    boolean validate(String username, String password);
}

// Implementazione
public class UserCredentialValidator implements CredentialValidator {
    @Override
    public boolean validate(String username, String password) {
        return UserUtil.isValidEmail(username) && UserUtil.isValidPassword(password);
    }
}

// Utilizzo nel Controller
private CredentialValidator validator;

public void login() {
    String username = view.getInput("Username: ");
    String password = view.getInput("Password: ");

    if (!validator.validate(username, password)) {
        view.showError("Credenziali non valide");
        return;
    }
}
```

**Benefici**:

-   Facilita i test: è possibile mockare il validator nei test unitari
-   Riduce le dipendenze dirette: il Controller dipende dall'interfaccia, non dall'implementazione
-   Aumenta la flessibilità: si possono aggiungere nuove strategie di validazione senza modificare il Controller
-   Migliora la manutenibilità del codice

Soluzione: Introdurre Service Layer

```java
// NEW FILE: service/PropostaService.java
/**
 * Service per la gestione delle proposte di scambio.
 * PATTERN: Low Coupling - Riduce l'accoppiamento del Controller
 */
public class PropostaService {
    private final Database database;

    public PropostaService(Database database) {
        this.database = database;
    }

    /**
     * Crea una nuova proposta di scambio.
     */
    public boolean creaProposta(Fruitore fruitore, Prestazione richiesta,
                                Prestazione offerta) {
        // Validazione
        if (fruitore == null || richiesta == null || offerta == null) {
            return false;
        }

        // Logica di business
        Proposta proposta = new Proposta(fruitore, richiesta, offerta, StatoProposta.APERTA);
        database.aggiungiProposta(proposta);
        return true;
    }

    /**
     * Ritira una proposta esistente.
     */
    public boolean ritiraProposta(Proposta proposta, Fruitore fruitore) {
        if (!proposta.isDiPropostitore(fruitore)) {
            return false; // Solo il propostitore può ritirare
        }
        return proposta.ritiraProposta();
    }

    /**
     * Ottiene proposte aperte per un fruitore.
     */
    public List<Proposta> getProposteApertePerComprensorio(Fruitore fruitore) {
        return database.getProposteAperteStessoComprensorio(fruitore, false);
    }
}

// Controller.java - Versione semplificata
public class Controller {
    private Database database;
    private final IView view;
    private PropostaService propostaService; // ✅ Unica dipendenza per proposte

    public Controller(IView view, Database database) {
        this.view = view;
        this.database = database;
        this.propostaService = new PropostaService(database); // ✅ DI
    }

    private boolean propostaScambioOpera(Fruitore fruitore) {
        // ... codice per ottenere richiesta e offerta dalla view ...

        // ✅ Delega al service
        return propostaService.creaProposta(fruitore, richiesta, offerta);
    }
}
```

## Principi SOLID Identificati

### 1. Open/Closed Principle (OCP) con Observer Pattern

**Applicazione**: Implementare Observer per notificare le viste sui cambiamenti del modello senza modificare il codice esistente.

**Problema Attuale**: Il Controller ha dipendenze dirette da IView e Database, rendendo difficile aggiungere nuovi osservatori.

codice attuale:

```java
// ❌ MALE: Modificare Proposta ogni volta che serve una notifica
public class Proposta {
    public void chiudiProposta() {
        if (stato == StatoProposta.APERTA) {
            stato = StatoProposta.CHIUSA;
            dataModifica = LocalDateTime.now();

            // ❌ Codice hardcoded per notifiche
            System.out.println("Email inviata a " + propostitore.getMail());
            System.out.println("SMS inviato...");
            // Ogni nuovo tipo di notifica richiede modifica qui!
        }
    }
}
```

**Codice Proposto**:

```java
// NEW FILE: model/observers/PropostaObserver.java
/**
 * Observer per eventi sulle proposte.
 * PATTERN: Observer (GoF)
 * PRINCIPIO: Open/Closed - Estendibile senza modifiche
 */
public interface PropostaObserver {
    void onPropostaChiusa(Proposta proposta);
    void onPropostaRitirata(Proposta proposta);
}

// NEW FILE: model/observers/EmailNotifier.java
/**
 * Notificatore via email.
 * PRINCIPIO: OCP - Nuova funzionalità senza modificare codice esistente
 */
public class EmailNotifier implements PropostaObserver {
    @Override
    public void onPropostaChiusa(Proposta proposta) {
        String email = proposta.getPropostitore().getMail();
        System.out.println("📧 Email inviata a " + email +
            ": La tua proposta è stata accettata!");
    }

    @Override
    public void onPropostaRitirata(Proposta proposta) {
        String email = proposta.getPropostitore().getMail();
        System.out.println("📧 Email inviata a " + email +
            ": Hai ritirato la proposta con successo.");
    }
}

// NEW FILE: model/observers/SmsNotifier.java
/**
 * Notificatore via SMS.
 * PRINCIPIO: OCP - Aggiunto SENZA modificare Proposta o EmailNotifier
 */
public class SmsNotifier implements PropostaObserver {
    @Override
    public void onPropostaChiusa(Proposta proposta) {
        System.out.println("📱 SMS inviato: Proposta accettata!");
    }

    @Override
    public void onPropostaRitirata(Proposta proposta) {
        System.out.println("📱 SMS inviato: Proposta ritirata.");
    }
}

// Proposta.java - Modificata per supportare observers
public class Proposta implements Serializable {
    // ... campi esistenti ...

    // ✅ Lista di observers (estendibile)
    private transient List<PropostaObserver> observers = new ArrayList<>();

    /**
     * Registra un observer.
     * PRINCIPIO: OCP - Permette estensione senza modifica
     */
    public void addObserver(PropostaObserver observer) {
        if (observers == null) observers = new ArrayList<>();
        observers.add(observer);
    }

    /**
     * Notifica tutti gli observers della chiusura.
     */
    private void notifyChiusura() {
        if (observers != null) {
            for (PropostaObserver observer : observers) {
                observer.onPropostaChiusa(this);
            }
        }
    }

    public void chiudiProposta() {
        if (stato == StatoProposta.APERTA) {
            stato = StatoProposta.CHIUSA;
            dataModifica = LocalDateTime.now();
            notifyChiusura(); // ✅ Notifica observers
        }
    }

    // Stesso pattern per ritiraProposta()...
}

// Controller.java - Setup degli observers
public class Controller {
    private void setupNotifications() {
        // ✅ Configurazione estendibile
        PropostaObserver emailNotifier = new EmailNotifier();
        PropostaObserver smsNotifier = new SmsNotifier();

        // Futura estensione: new PushNotifier(), new SlackNotifier()
        // SENZA modificare codice esistente!
    }
}
```

**Benefici**:

-   Permette di estendere il comportamento senza modificare il codice esistente della classe Database
-   Aggiungere nuovi osservatori è semplice: basta implementare l'interfaccia ProposalObserver
-   Riduce l'accoppiamento tra Database e i componenti che lo utilizzano
-   Migliora la separazione delle responsabilità

### 2. Interface Segregation Principle (ISP)

**Applicazione**: Suddividere `IView` in interfacce più specifiche per ridurre le dipendenze inutili.

**Problema Attuale**: L'interfaccia `IView` è monolitica e contiene troppi metodi, forzando le implementazioni a supportare funzionalità non necessarie.

codice attuale:

```java
public interface IView {
    // Metodi di input (usati ovunque)
    String getInput(String prompt);
    String getPasswordInput(String prompt);
    int getIntInput(String prompt, int min, int max);
    boolean getConfirmation(String message);

    // Metodi di output (usati ovunque)
    void showMessage(String message);
    void showErrorMessage(String message);
    void showSuccessMessage(String message);
    void showWarningMessage(String message);
    void showInfoMessage(String message);

    // Metodi specifici per menu (NON usati in MockView per test!)
    void showMenu();
    void showMenuConfiguratore();
    void showMenuFruitore();

    // Metodi di formattazione avanzata (raramente usati)
    void showMessageWithDelay(String message, int delayMs);
    void showSuccessMessageNoWait(String message);
    void showErrorMessageNoWait(String message);

    // Metodi utility
    void pressEnterToContinue();
    void clearScreen();
    void close();

    // Easter eggs (VIOLAZIONE ISP!)
    void displayShrek();
    void displayAmongUs();
}
```

**Codice Proposto**:

```java
// view/IInputProvider.java
/**
 * Interfaccia per la gestione degli input utente.
 * PRINCIPIO: ISP - Interfaccia piccola e coesa
 */
public interface IInputProvider {
    String getInput(String prompt);
    String getPasswordInput(String prompt);
    int getIntInput(String prompt, int min, int max);
    boolean getConfirmation(String message);
}

// view/IOutputProvider.java
/**
 * Interfaccia per la gestione degli output.
 * PRINCIPIO: ISP - Interfaccia piccola e coesa
 */
public interface IOutputProvider {
    void showMessage(String message);
    void showErrorMessage(String message);
    void showSuccessMessage(String message);
    void showWarningMessage(String message);
    void showInfoMessage(String message);
}

// view/IMenuProvider.java
/**
 * Interfaccia per la gestione dei menu.
 * PRINCIPIO: ISP - Solo i client che servono menu la implementano
 */
public interface IMenuProvider {
    void showMenu();
    void showMenuConfiguratore();
    void showMenuFruitore();
}

// view/IViewUtilities.java
/**
 * Interfaccia per utilities della view.
 * PRINCIPIO: ISP - Opzionale
 */
public interface IViewUtilities {
    void pressEnterToContinue();
    void clearScreen();
    void close();
}

// view/IView.java - Composta dalle interfacce segregate
/**
 * Interfaccia completa per la view console.
 * PRINCIPIO: ISP - Composizione di interfacce piccole
 */
public interface IView extends IInputProvider, IOutputProvider,
                               IMenuProvider, IViewUtilities {
    // Metodi specifici aggiuntivi se necessari
}

// test/MockView.java - Implementa SOLO ciò che serve!
/**
 * Mock per testing che implementa solo input/output.
 * PRINCIPIO: ISP - Non forza implementazione di metodi inutili
 */
public class MockView implements IInputProvider, IOutputProvider {
    private Queue<String> simulatedInputs = new LinkedList<>();
    private List<String> displayedMessages = new ArrayList<>();

    @Override
    public String getInput(String prompt) {
        return simulatedInputs.poll();
    }

    @Override
    public void showMessage(String message) {
        displayedMessages.add(message);
    }

    // ✅ NON deve implementare showMenu(), displayShrek(), etc.!
}

// Controller.java - Usa solo le interfacce necessarie
public class Controller {
    private final IInputProvider inputProvider;  // ✅ Dipendenza specifica
    private final IOutputProvider outputProvider; // ✅ Dipendenza specifica

    public Controller(IInputProvider input, IOutputProvider output, Database db) {
        this.inputProvider = input;
        this.outputProvider = output;
        this.database = db;
    }

    // ✅ Metodi usano solo interfacce specifiche
    private String getUsername() {
        String username = inputProvider.getInput("Username: ");
        if (!UserUtil.checkUsername(username)) {
            outputProvider.showErrorMessage("Username non valido");
            return null;
        }
        return username;
    }
}
```

**Benefici**:

-   Riduce l'accoppiamento: le classi dipendono solo dalle interfacce che effettivamente utilizzano
-   Migliora la flessibilità: è facile creare implementazioni parziali (ad es. solo per messaggi)
-   Facilita i test: è possibile mockare solo l'interfaccia necessaria
-   Aumenta la coesione: ogni interfaccia ha una responsabilità specifica

## Pattern GoF Identificati

### 1. Factory Method

**Applicazione**: Creare una factory per gli utenti in modo centralizzato e flessibile.

**Problema Attuale**: La creazione di utenti è dispersa nel Controller con logica ripetuta.

codice attuale:

```java
// Database.java - Duplicazione di logica
public boolean registraConfiguratore(String username, String password) {
    Objects.requireNonNull(username, "Username non può essere null");
    Objects.requireNonNull(password, "Password non può essere null");
    if (hasUtente(username)) {
        return false;
    }
    Configuratore configuratore = new Configuratore(username, password);
    return this.addUtente(configuratore);
}

public boolean registraFruitore(String username, String password,
                                String email, Comprensorio comprensorio) {
    Objects.requireNonNull(username, "Username non può essere null");
    Objects.requireNonNull(password, "Password non può essere null");
    Objects.requireNonNull(email, "Email non può essere null");
    if (hasUtente(username)) {
        return false;
    }
    Fruitore fruitore = new Fruitore(username, password, email, comprensorio);
    return this.addUtente(fruitore);
}
```

**Codice Proposto**:

```java
// NEW FILE: model/factory/UtenteFactory.java
/**
 * Factory per la creazione di utenti.
 * PATTERN: Factory Method (GoF)
 * Centralizza la creazione e validazione degli utenti
 */
public abstract class UtenteFactory {

    /**
     * Template Method per creare un utente.
     * PATTERN: Template Method (GoF) + Factory Method
     */
    public final Utente creaUtente(String username, String password,
                                    Map<String, Object> parametriAggiuntivi) {
        // 1. Validazione comune
        validaParametriBase(username, password);

        // 2. Validazione specifica (delegata alle sottoclassi)
        validaParametriSpecifici(parametriAggiuntivi);

        // 3. Creazione dell'utente (Factory Method)
        Utente utente = creaUtenteConcreto(username, password, parametriAggiuntivi);

        // 4. Post-processing comune
        inizializzaUtente(utente);

        return utente;
    }

    /**
     * Validazione parametri base.
     */
    private void validaParametriBase(String username, String password) {
        Objects.requireNonNull(username, "Username non può essere null");
        Objects.requireNonNull(password, "Password non può essere null");

        if (!UserUtil.checkUsername(username)) {
            throw new IllegalArgumentException("Username non valido");
        }
        if (!UserUtil.checkPassword(password)) {
            throw new IllegalArgumentException("Password non valida");
        }
    }

    /**
     * Factory Method - Da implementare nelle sottoclassi.
     */
    protected abstract Utente creaUtenteConcreto(
        String username, String password, Map<String, Object> parametri);

    /**
     * Hook Method - Validazione specifica (opzionale).
     */
    protected void validaParametriSpecifici(Map<String, Object> parametri) {
        // Default: nessuna validazione aggiuntiva
    }

    /**
     * Hook Method - Inizializzazione (opzionale).
     */
    protected void inizializzaUtente(Utente utente) {
        // Default: nessuna inizializzazione
    }
}

// NEW FILE: model/factory/ConfiguratoreFactory.java
/**
 * Factory concreta per Configuratori.
 */
public class ConfiguratoreFactory extends UtenteFactory {

    @Override
    protected Utente creaUtenteConcreto(String username, String password,
                                         Map<String, Object> parametri) {
        return new Configuratore(username, password);
    }

    @Override
    protected void inizializzaUtente(Utente utente) {
        // Configuratori hanno privilegi speciali
        System.out.println("✅ Configuratore creato con privilegi amministrativi");
    }
}

// NEW FILE: model/factory/FruitoreFactory.java
/**
 * Factory concreta per Fruitori.
 */
public class FruitoreFactory extends UtenteFactory {

    @Override
    protected Utente creaUtenteConcreto(String username, String password,
                                         Map<String, Object> parametri) {
        String email = (String) parametri.get("email");
        Comprensorio comprensorio = (Comprensorio) parametri.get("comprensorio");
        return new Fruitore(username, password, email, comprensorio);
    }

    @Override
    protected void validaParametriSpecifici(Map<String, Object> parametri) {
        String email = (String) parametri.get("email");
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email obbligatoria per fruitori");
        }
        // Validazione formato email
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Email non valida");
        }
    }
}

// Database.java - Versione semplificata con Factory
public class Database {
    private final UtenteFactory configuratoreFactory = new ConfiguratoreFactory();
    private final UtenteFactory fruitoreFactory = new FruitoreFactory();

    public boolean registraConfiguratore(String username, String password) {
        try {
            Utente configuratore = configuratoreFactory.creaUtente(
                username, password, Collections.emptyMap());
            return addUtente(configuratore);
        } catch (IllegalArgumentException e) {
            System.err.println("Errore: " + e.getMessage());
            return false;
        }
    }

    public boolean registraFruitore(String username, String password,
                                    String email, Comprensorio comprensorio) {
        try {
            Map<String, Object> parametri = new HashMap<>();
            parametri.put("email", email);
            parametri.put("comprensorio", comprensorio);

            Utente fruitore = fruitoreFactory.creaUtente(username, password, parametri);
            return addUtente(fruitore);
        } catch (IllegalArgumentException e) {
            System.err.println("Errore: " + e.getMessage());
            return false;
        }
    }
}
```

**Benefici**:

-   Incapsula la logica di creazione: la logica di creazione degli utenti è centralizzata
-   Facilita l'estensione: aggiungere nuovi tipi di utenti è semplice
-   Riduce il codice duplicato: la creazione degli utenti non è dispersa nel Controller
-   Migliora la manutenibilità: se cambia la creazione, si modifica un solo punto

### 2. State Pattern

**Applicazione**: Gestire gli stati delle proposte con State Pattern per eliminare complessi if-else.

**Problema Attuale**: La classe Proposta contiene logica if-else complessa per gestire gli stati.

codice attuale:

```java
public class Proposta {
    private StatoProposta stato;

    public void chiudiProposta() {
        if (stato == StatoProposta.APERTA) { // ❌ If-else per stati
            stato = StatoProposta.CHIUSA;
            dataModifica = LocalDateTime.now();
        }
    }

    public boolean ritiraProposta() {
        if (stato == StatoProposta.APERTA) { // ❌ Duplicazione logica
            stato = StatoProposta.RITIRATA;
            dataModifica = LocalDateTime.now();
            return true;
        }
        return false;
    }
}
```

**Codice Proposto**:

```java
// NEW FILE: model/state/StatoPropostaContext.java
/**
 * Interfaccia per gli stati della proposta.
 * PATTERN: State (GoF)
 */
public interface StatoPropostaContext {
    void chiudi();
    void ritira();
    boolean isAperta();
    boolean isChiusa();
    boolean isRitirata();
    String getNome();
}

// NEW FILE: model/state/PropostaAperta.java
/**
 * Stato: Proposta Aperta.
 * PATTERN: State - Stato concreto
 */
public class PropostaAperta implements StatoPropostaContext {
    private final Proposta proposta;

    public PropostaAperta(Proposta proposta) {
        this.proposta = proposta;
    }

    @Override
    public void chiudi() {
        // ✅ Transizione valida: APERTA → CHIUSA
        proposta.cambiaStato(new PropostaChiusa(proposta));
        proposta.aggiornaDataModifica();
    }

    @Override
    public void ritira() {
        // ✅ Transizione valida: APERTA → RITIRATA
        proposta.cambiaStato(new PropostaRitirata(proposta));
        proposta.aggiornaDataModifica();
    }

    @Override
    public boolean isAperta() { return true; }

    @Override
    public boolean isChiusa() { return false; }

    @Override
    public boolean isRitirata() { return false; }

    @Override
    public String getNome() { return "APERTA"; }
}

// NEW FILE: model/state/PropostaChiusa.java
/**
 * Stato: Proposta Chiusa.
 * PATTERN: State - Stato concreto
 */
public class PropostaChiusa implements StatoPropostaContext {
    private final Proposta proposta;

    public PropostaChiusa(Proposta proposta) {
        this.proposta = proposta;
    }

    @Override
    public void chiudi() {
        // ❌ Transizione non valida: già chiusa
        throw new IllegalStateException("La proposta è già chiusa");
    }

    @Override
    public void ritira() {
        // ❌ Transizione non valida: proposte chiuse non si possono ritirare
        throw new IllegalStateException("Impossibile ritirare una proposta chiusa");
    }

    @Override
    public boolean isAperta() { return false; }

    @Override
    public boolean isChiusa() { return true; }

    @Override
    public boolean isRitirata() { return false; }

    @Override
    public String getNome() { return "CHIUSA"; }
}

// NEW FILE: model/state/PropostaRitirata.java
/**
 * Stato: Proposta Ritirata.
 */
public class PropostaRitirata implements StatoPropostaContext {
    private final Proposta proposta;

    public PropostaRitirata(Proposta proposta) {
        this.proposta = proposta;
    }

    @Override
    public void chiudi() {
        throw new IllegalStateException("Impossibile chiudere una proposta ritirata");
    }

    @Override
    public void ritira() {
        throw new IllegalStateException("La proposta è già ritirata");
    }

    @Override
    public boolean isAperta() { return false; }

    @Override
    public boolean isChiusa() { return false; }

    @Override
    public boolean isRitirata() { return true; }

    @Override
    public String getNome() { return "RITIRATA"; }
}

// Proposta.java - Refactored con State Pattern
public class Proposta implements Serializable {
    private final Fruitore propostitore;
    private final Prestazione richiesta;
    private final Prestazione offerta;
    private StatoPropostaContext stato; // ✅ State object invece di enum
    private final LocalDateTime dataCreazione;
    private LocalDateTime dataModifica;

    public Proposta(Fruitore propostitore, Prestazione richiesta,
                    Prestazione offerta, StatoPropostaContext statoIniziale) {
        this.propostitore = Objects.requireNonNull(propostitore);
        this.richiesta = Objects.requireNonNull(richiesta);
        this.offerta = Objects.requireNonNull(offerta);
        this.stato = statoIniziale != null ? statoIniziale : new PropostaAperta(this);
        this.dataCreazione = LocalDateTime.now();
    }

    /**
     * Chiude la proposta delegando allo stato corrente.
     * PATTERN: State - Delega al comportamento dello stato
     */
    public void chiudiProposta() {
        stato.chiudi(); // ✅ Delega allo stato
    }

    /**
     * Ritira la proposta delegando allo stato corrente.
     */
    public boolean ritiraProposta() {
        try {
            stato.ritira(); // ✅ Delega allo stato
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    /**
     * Metodo package-private per cambio stato.
     */
    void cambiaStato(StatoPropostaContext nuovoStato) {
        this.stato = nuovoStato;
    }

    /**
     * Metodo package-private per aggiornare data modifica.
     */
    void aggiornaDataModifica() {
        this.dataModifica = LocalDateTime.now();
    }

    public boolean isAperta() {
        return stato.isAperta();
    }

    public String getStatoNome() {
        return stato.getNome();
    }
}
```

**Benefici**:

-   Elimina i lunghi if-else: la logica di stato è distribuita tra le classi state
-   Facilita l'aggiunta di nuovi stati: basta creare una nuova classe che implementi ProposalState
-   Migliora la leggibilità: il codice è più facile da comprendere e manutenere
-   Rispetta il Single Responsibility Principle: ogni stato ha una responsabilità specifica

        ┌─────────┐
        │ APERTA  │
        └────┬────┘
             │
        ┌────┴────────────┐
        │                 │
        ▼                 ▼

    ┌─────────┐ ┌───────────┐
    │ CHIUSA │ │ RITIRATA │
    └─────────┘ └───────────┘

## Refactoring Identificato

### 1. Extract Method

**Applicazione**: Estrarre metodi dal metodo `createComprensorio()` in `Controller.java`.

**Codice Esistente** (da Controller.java):

```java
public boolean createComprensorio() {
    // Mostra il titolo della prima fase: definizione del nome del comprensorio
    view.showInfoMessage(ControllerConstants.STEP_NOME_COMPRENSORIO);

    String nome = null;
    boolean nomeValido = false;

    // Ciclo di validazione del nome del comprensorio
    while (!nomeValido) {
        // Richiede il nome all'utente
        nome = view.getInput(ControllerConstants.PROMPT_NOME_COMPRENSORIO);

        // Verifica se l'utente vuole annullare l'operazione
        if (nome.equals("0")) {
            return false; // Operazione annullata dall'utente
        }
        // Verifica che il nome non sia vuoto
        else if (nome.isBlank()) {
            view.showErrorMessageNoWait(ControllerConstants.ERROR_NOME_VUOTO);
        }
        // Verifica che il nome abbia almeno 3 caratteri
        else if (nome.length() < 3) {
            view.showErrorMessageNoWait(ControllerConstants.ERROR_NOME_CORTO);
        }
        // Verifica che non esista già un comprensorio con questo nome
        else if (database.hasComprensorio(nome)) {
            view.showErrorMessageNoWait(ControllerConstants.ERROR_NOME_DUPLICATO);
        } else {
            // Tutte le verifiche sono passate, il nome è valido
            nomeValido = true;
        }
    }

    // Inizia la seconda fase: aggiunta dei comuni al comprensorio
    view.showInfoMessage(ControllerConstants.STEP_COMUNI_COMPRENSORIO);
    view.showMessage(String.format(ControllerConstants.INFO_AGGIUNGI_COMUNI, nome));
    view.showMessage(ControllerConstants.INFO_MINIMO_UN_COMUNE);

    // Crea il nuovo oggetto Comprensorio con il nome validato
    Comprensorio comprensorio = new Comprensorio(nome);

    // Chiama il metodo per aggiungere comuni al comprensorio appena creato
    addComuniToComprensorio(comprensorio);

    // Verifica che il comprensorio abbia almeno un comune
    if (comprensorio.getComuni().isEmpty()) {
        view.showErrorMessage(ControllerConstants.ERROR_CREAZIONE_ANNULLATA);
        return false; // Operazione fallita: nessun comune aggiunto
    }

    // Aggiunge il comprensorio completo al database
    boolean risultato = database.addComprensorio(comprensorio);

    // Mostra un messaggio di successo se l'operazione è andata a buon fine
    if (risultato) {
        view.showSuccessMessageNoWait(String.format(ControllerConstants.SUCCESS_COMPRENSORIO_AGGIUNTO, nome));
    }

    return risultato;
}
```

Pattern di Refactoring:

Extract Method: Estrarre logica di validazione nome
Replace Temp with Query: Eliminare variabile nomeValido
Introduce Explaining Variable: Migliorare leggibilità
Decompose Conditional: Semplificare if complessi

**Codice Refactored**:

```java
// DOPO: Metodo principale semplificato
/**
 * Crea un nuovo comprensorio attraverso un processo guidato.
 *
 * REFACTORING APPLICATI:
 * - Extract Method: validaNomeComprensorio(), creaComprensorioConComuni()
 * - Replace Temp with Query: eliminata variabile nomeValido
 * - Decompose Conditional: separati metodi di validazione
 */
public boolean createComprensorio() {
    view.showInfoMessage(ControllerConstants.STEP_NOME_COMPRENSORIO);

    // ✅ STEP 1: Ottieni nome valido
    String nome = validaNomeComprensorio();
    if (nome == null) {
        return false; // Utente ha annullato
    }

    // ✅ STEP 2: Crea comprensorio con comuni
    Comprensorio comprensorio = creaComprensorioConComuni(nome);
    if (comprensorio == null) {
        return false; // Nessun comune aggiunto
    }

    // ✅ STEP 3: Salva nel database
    return salvaComprensorio(comprensorio);
}

/**
 * Valida e ottiene un nome valido per il comprensorio.
 * REFACTORING: Extract Method
 *
 * @return il nome validato, o null se l'utente annulla
 */
private String validaNomeComprensorio() {
    while (true) {
        String nome = view.getInput(ControllerConstants.PROMPT_NOME_COMPRENSORIO);

        // ✅ Early return per annullamento
        if (nome.equals("0")) {
            return null;
        }

        // ✅ Validazione delegata a metodi specifici
        ValidationResult validazione = validaNome(nome);

        if (validazione.isValid()) {
            return nome; // ✅ Nome valido trovato
        }

        // Mostra errore e riprova
        view.showErrorMessageNoWait(validazione.getErrorMessage());
    }
}

/**
 * Valida un nome di comprensorio.
 * REFACTORING: Extract Method + Introduce Parameter Object
 */
private ValidationResult validaNome(String nome) {
    // ✅ Validation chain chiara
    if (nome.isBlank()) {
        return ValidationResult.error(ControllerConstants.ERROR_NOME_VUOTO);
    }

    if (nome.length() < 3) {
        return ValidationResult.error(ControllerConstants.ERROR_NOME_CORTO);
    }

    if (database.hasComprensorio(nome)) {
        return ValidationResult.error(ControllerConstants.ERROR_NOME_DUPLICATO);
    }

    return ValidationResult.success();
}

/**
 * Crea un comprensorio e aggiunge i comuni.
 * REFACTORING: Extract Method
 *
 * @return il comprensorio creato, o null se nessun comune è stato aggiunto
 */
private Comprensorio creaComprensorioConComuni(String nome) {
    view.showInfoMessage(ControllerConstants.STEP_COMUNI_COMPRENSORIO);
    view.showMessage(String.format(ControllerConstants.INFO_AGGIUNGI_COMUNI, nome));

    Comprensorio comprensorio = new Comprensorio(nome);
    addComuniToComprensorio(comprensorio);

    if (comprensorio.getComuni().isEmpty()) {
        view.showErrorMessage(ControllerConstants.ERROR_CREAZIONE_ANNULLATA);
        return null;
    }

    return comprensorio;
}

/**
 * Salva il comprensorio nel database.
 * REFACTORING: Extract Method
 */
private boolean salvaComprensorio(Comprensorio comprensorio) {
    boolean risultato = database.addComprensorio(comprensorio);

    if (risultato) {
        view.showSuccessMessageNoWait(
            String.format(ControllerConstants.SUCCESS_COMPRENSORIO_AGGIUNTO,
                         comprensorio.getNome()));
    }

    return risultato;
}

// NEW FILE: util/ValidationResult.java
/**
 * Risultato di una validazione.
 * REFACTORING: Introduce Parameter Object
 */
public class ValidationResult {
    private final boolean valid;
    private final String errorMessage;

    private ValidationResult(boolean valid, String errorMessage) {
        this.valid = valid;
        this.errorMessage = errorMessage;
    }

    public static ValidationResult success() {
        return new ValidationResult(true, null);
    }

    public static ValidationResult error(String message) {
        return new ValidationResult(false, message);
    }

    public boolean isValid() {
        return valid;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
```

Pattern Applicati:

✅ Extract Method: validaNomeComprensorio(), creaComprensorioConComuni(), salvaComprensorio()
✅ Replace Temp with Query: Eliminata variabile nomeValido
✅ Introduce Parameter Object: ValidationResult
✅ Decompose Conditional: Separati controlli di validazione
Benefici:

✅ Ogni metodo ha una singola responsabilità
✅ Testing facilitato (test ogni metodo separatamente)
✅ Codice autodocumentante (nomi metodi descrivono cosa fanno)
✅ Riusabilità (validazione riutilizzabile per altre entità)

**Benefici**: Migliora la leggibilità, facilita i test e riduce la complessità ciclomatica.

## Priorità di Implementazione

### Alta Priorità (Implementare Subito)

1. **Extract Method su `createComprensorio()`** - Benefici immediati in leggibilità e testabilità
2. **Interface Segregation per `IView`** - Riduce accoppiamento senza grandi cambiamenti

### Media Priorità (Prossima Iterazione)

3. **Factory Method per creazione utenti** - Migliora estensibilità
4. **Observer Pattern per notifiche** - Aggiunge reattività al sistema

### Bassa Priorità (Futuro)

5. **State Pattern per proposte** - Rifattorizzazione architetturale complessa

## Stima dello Sforzo

### Extract Method

-   **Sforzo**: 2-4 ore
-   **Rischi**: Basso - refactoring sicuro
-   **Test**: Facili da aggiungere per metodi estratti

### Interface Segregation

-   **Sforzo**: 4-6 ore
-   **Rischi**: Medio - richiede aggiornamento implementazioni
-   **Test**: Necessari per nuove interfacce

### Factory Method

-   **Sforzo**: 6-8 ore
-   **Rischi**: Medio - cambia logica di creazione
-   **Test**: Importanti per validare creazione corretta

### Observer Pattern

-   **Sforzo**: 8-12 ore
-   **Rischi**: Alto - introduce complessità asincrona
-   **Test**: Critici per notifiche

### State Pattern

-   **Sforzo**: 12-16 ore
-   **Rischi**: Alto - cambia gestione stati esistente
-   **Test**: Estensivi per tutti stati

## Impatto sui Test

### Test Esistenti

-   **ControllerTest.java**: Richiederà aggiornamenti per metodi estratti
-   **Database**: Test di integrazione necessari per Observer

### Nuovi Test Richiesti

-   Unit test per metodi privati estratti
-   Test per nuove interfacce IView
-   Test di integrazione per Factory Method
-   Test di stato per State Pattern

## Metriche di Qualità

### Prima del Refactoring

-   Complessità ciclomatica `createComprensorio()`: ~15
-   Numero metodi per classe Controller: 58
-   Linee di codice Database.java: 3252

### Dopo il Refactoring (Stima)

-   Complessità ciclomatica `createComprensorio()`: ~3
-   Numero metodi per classe Controller: ~65 (con metodi estratti)
-   Linee di codice Database.java: ~3200 (con Observer aggiunto)

## Considerazioni Architetturali

### Vantaggi Generali

-   **Manutenibilità**: Codice più modulare e comprensibile
-   **Estensibilità**: Facilita aggiunta di nuove funzionalità
-   **Testabilità**: Metodi più piccoli e isolati
-   **Riutilizzo**: Componenti più indipendenti

### Svantaggi Potenziali

-   **Complessità Iniziale**: Aumento temporaneo di classi/interfacce
-   **Curva di Apprendimento**: Team deve familiarizzare con pattern
-   **Over-engineering**: Rischio di applicare pattern non necessari

## Piano di Migrazione

### Fase 1: Preparazione (1 settimana)

-   Analisi impatto sui test esistenti
-   Creazione branch di sviluppo
-   Documentazione cambiamenti pianificati

### Fase 2: Implementazione Graduale (2-3 settimane)

-   Applicare Extract Method
-   Implementare Interface Segregation
-   Aggiungere Factory Method

### Fase 3: Pattern Avanzati (3-4 settimane)

-   Implementare Observer Pattern
-   Rifattorizzare con State Pattern

### Fase 4: Testing e Validazione (1 settimana)

-   Eseguire tutti test
-   Code review
-   Validazione manuale

## Rischi e Mitigazioni

### Rischio: Introduzione di Bug

-   **Mitigazione**: Test automatici completi, code review

### Rischio: Performance Degradation

-   **Mitigazione**: Profilazione prima/dopo, ottimizzazioni se necessarie

### Rischio: Team Resistance

-   **Mitigazione**: Formazione sui pattern, dimostrazione benefici

## Conclusioni Estese

L'implementazione di questi pattern e refactoring rappresenterà un significativo miglioramento architetturale per ActivityNexus. Mentre l'investimento iniziale in tempo e sforzo è consistente, i benefici a lungo termine in termini di manutenibilità, estensibilità e qualità del codice saranno sostanziali.

Si raccomanda di procedere con un approccio incrementale, iniziando dai refactoring a basso rischio e alto impatto, per costruire momentum e dimostrare valore al team di sviluppo.
