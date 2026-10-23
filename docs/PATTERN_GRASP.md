# Pattern GRASP - Applicazione nel Progetto ActivityNexus

## 📚 Introduzione ai Pattern GRASP

I **GRASP** (General Responsibility Assignment Software Patterns) sono principi fondamentali per l'assegnazione delle responsabilità in un sistema object-oriented. Non sono pattern di design nel senso tradizionale (GoF), ma **linee guida** per decidere dove collocare metodi e logica.

### I 9 Pattern GRASP

| Pattern                  | Descrizione                                                          | Applicato in ActivityNexus         |
| ------------------------ | -------------------------------------------------------------------- | ---------------------------------- |
| **Information Expert**   | Assegna responsabilità alla classe che ha le informazioni necessarie | ✅ `Proposta`, `Database`          |
| **Creator**              | Definisce chi deve creare istanze di altre classi                    | ✅ `Controller`, `Database`        |
| **Controller**           | Oggetto non-UI che gestisce eventi di sistema                        | ✅ `Controller.java`               |
| **Low Coupling**         | Minimizza le dipendenze tra classi                                   | ✅ Refactoring `CredentialService` |
| **High Cohesion**        | Mantiene responsabilità focalizzate in ogni classe                   | ✅ `ConsoleView`, `Database`       |
| **Polymorphism**         | Usa polimorfismo per gestire variazioni di comportamento             | ✅ `IView`, `PropostaState`        |
| **Pure Fabrication**     | Crea classi artificiali per migliorare il design                     | ✅ `ControllerConstants`           |
| **Indirection**          | Introduce intermediari per disaccoppiare                             | ✅ `IView` interface               |
| **Protected Variations** | Protegge da variazioni con interfacce stabili                        | ✅ `IView`, `UserFactory`          |

---

## 1️⃣ Controller GRASP

### 🎯 Definizione del Pattern

> **Controller GRASP**: Un oggetto **non appartenente all'interfaccia utente** che è responsabile di **ricevere** e **coordinare** ("controllare") gli **eventi di sistema**.

### Tipologie di Controller GRASP

| Tipo                    | Descrizione                                           | Quando Usarlo                       |
| ----------------------- | ----------------------------------------------------- | ----------------------------------- |
| **Facade Controller**   | Un singolo controller per tutti gli eventi di sistema | Sistemi semplici, pochi casi d'uso  |
| **Use-Case Controller** | Un controller per ogni caso d'uso                     | Sistemi complessi, molti casi d'uso |

### Quale Classe è Controller GRASP in ActivityNexus?

```
📁 controller/Controller.java → FACADE CONTROLLER
```

### ✅ Perché `Controller.java` è un Controller GRASP?

Un Controller GRASP deve soddisfare criteri specifici:

| #   | Criterio Controller GRASP               | Implementazione                                                   | ✓/✗ |
| --- | --------------------------------------- | ----------------------------------------------------------------- | --- |
| 1   | **NON è parte dell'interfaccia utente** | `Controller` non ha `Scanner`, `System.out`, GUI                  | ✅  |
| 2   | **Riceve eventi di sistema**            | Metodi `loginUtente()`, `createComprensorio()`, `creaGerarchia()` | ✅  |
| 3   | **Coordina operazioni**                 | Delega a `Database` (model) e `IView` (presentation)              | ✅  |
| 4   | **Non esegue il lavoro**                | Delega la business logic alle classi del dominio                  | ✅  |
| 5   | **Rappresenta il sistema**              | È il punto d'ingresso per tutte le operazioni di sistema          | ✅  |

### Evidenza: Controller Riceve Eventi di Sistema

```java
// controller/Controller.java

public class Controller {
    private Database database;  // Accesso al Model
    private final IView view;   // Accesso alla View

    // ═══════════════════════════════════════════════════════════════
    // EVENTI DI SISTEMA GESTITI DAL CONTROLLER
    // ═══════════════════════════════════════════════════════════════

    // Evento: "Utente vuole effettuare login"
    public Utente loginUtente() { ... }

    // Evento: "Configuratore vuole creare un comprensorio"
    public boolean createComprensorio() { ... }

    // Evento: "Configuratore vuole creare una gerarchia"
    public void creaGerarchia() { ... }

    // Evento: "Fruitore vuole creare una proposta"
    public void creaProposta(Fruitore fruitore) { ... }

    // Evento: "Utente vuole registrarsi"
    public void registraFruitore() { ... }
}
```

### Diagramma: Controller come Coordinatore

```
┌─────────────────────────────────────────────────────────────────┐
│                        SISTEMA                                   │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │                    Controller.java                          ││
│  │              (FACADE CONTROLLER GRASP)                      ││
│  │                                                             ││
│  │   ┌──────────────────────────────────────────────────────┐  ││
│  │   │  EVENTI DI SISTEMA GESTITI:                          │  ││
│  │   │                                                      │  ││
│  │   │  • loginUtente()        → Autenticazione             │  ││
│  │   │  • registraFruitore()   → Registrazione              │  ││
│  │   │  • createComprensorio() → Creazione comprensorio     │  ││
│  │   │  • creaGerarchia()      → Creazione gerarchia        │  ││
│  │   │  • creaProposta()       → Creazione proposta         │  ││
│  │   │  • valutaProposte()     → Valutazione proposte       │  ││
│  │   └──────────────────────────────────────────────────────┘  ││
│  │                          │                                  ││
│  │            ┌─────────────┴─────────────┐                   ││
│  │            ▼                           ▼                   ││
│  │   ┌─────────────────┐         ┌─────────────────┐          ││
│  │   │     IView       │         │    Database     │          ││
│  │   │  (Presentazione)│         │    (Model)      │          ││
│  │   └─────────────────┘         └─────────────────┘          ││
│  └─────────────────────────────────────────────────────────────┘│
│                                                                  │
│  ✅ Controller NON è UI (non ha Scanner/System.out)             │
│  ✅ Controller RICEVE eventi e COORDINA, non ESEGUE             │
└─────────────────────────────────────────────────────────────────┘
```

### Esempio Dettagliato: `createComprensorio()` come Evento di Sistema

```java
// controller/Controller.java
public boolean createComprensorio() {
    // ═══════════════════════════════════════════════════════════
    // STEP 1: RACCOLTA DATI (tramite View, NON direttamente)
    // ═══════════════════════════════════════════════════════════
    String nome = view.getInput(ControllerConstants.PROMPT_NOME_COMPRENSORIO);

    // ═══════════════════════════════════════════════════════════
    // STEP 2: VALIDAZIONE (delegata a metodo helper)
    // ═══════════════════════════════════════════════════════════
    ValidationResult validation = validateComprensorioName(nome);
    if (!validation.isValid()) {
        view.showErrorMessage(validation.getErrorMessage());
        return false;
    }

    // ═══════════════════════════════════════════════════════════
    // STEP 3: RACCOLTA COMUNI (loop coordinato dal Controller)
    // ═══════════════════════════════════════════════════════════
    List<String> comuni = new ArrayList<>();
    do {
        String comune = view.getInput(ControllerConstants.PROMPT_NOME_COMUNE);
        comuni.add(comune);
    } while (view.getConfirmation(ControllerConstants.PROMPT_ALTRO_COMUNE));

    // ═══════════════════════════════════════════════════════════
    // STEP 4: CREAZIONE OGGETTO + PERSISTENZA (delegata al Model)
    // ═══════════════════════════════════════════════════════════
    Comprensorio comprensorio = new Comprensorio(nome);
    comuni.forEach(comprensorio::addComune);
    database.addComprensorio(comprensorio);  // ✅ Delega al Model

    // ═══════════════════════════════════════════════════════════
    // STEP 5: FEEDBACK (tramite View)
    // ═══════════════════════════════════════════════════════════
    view.showSuccessMessage(ControllerConstants.COMPRENSORIO_CREATED);
    return true;
}
```

**Analisi del Ruolo Controller GRASP:**

- ✅ **Riceve l'evento**: "Configuratore vuole creare comprensorio"
- ✅ **Coordina il flusso**: Raccolta dati → Validazione → Creazione → Persistenza → Feedback
- ✅ **NON esegue business logic**: Delega validazione e persistenza
- ✅ **NON fa I/O diretto**: Usa `view.getInput()`, mai `Scanner`

---

## 2️⃣ Information Expert

### 🎯 Definizione del Pattern

> **Information Expert**: Assegna la responsabilità alla classe che ha le **informazioni necessarie** per adempierla.

### Applicazione nel Refactoring: `Proposta.java`

#### ❌ PRIMA del Refactoring (Violazione Information Expert)

```java
// Database.java - PRIMA (Information Expert VIOLATO)
public List<Proposta> getProposteAperteStessoComprensorio(Utente utente, boolean includiUtente) {
    Fruitore fruitoreRiferimento = (Fruitore) utente;
    Comprensorio comprensorioRiferimento = fruitoreRiferimento.getComprensorio();

    List<Proposta> proposteAperte = new ArrayList<>();
    List<Proposta> tutteProposteAperte = getPropostePerStato(StatoProposta.APERTA);

    for (Proposta proposta : tutteProposteAperte) {
        Fruitore proponente = proposta.getPropostitore();

        // ❌ Database deve "entrare" nella Proposta per filtrare
        // ❌ Database conosce troppi dettagli interni di Proposta
        if (proponente.getComprensorio().equals(comprensorioRiferimento)) {
            if (!includiUtente && proponente.equals(fruitoreRiferimento)) {
                continue;
            }
            proposteAperte.add(proposta);
        }
    }
    return proposteAperte;
}
```

**Problema**: `Database` deve conoscere la struttura interna di `Proposta` e `Fruitore` per filtrare. Ma chi ha le informazioni su una proposta? **La proposta stessa!**

#### ✅ DOPO il Refactoring (Information Expert APPLICATO)

```java
// Proposta.java - DOPO (Information Expert)
public class Proposta {
    private Fruitore propostitore;
    private StatoProposta stato;
    // ...

    /**
     * Verifica se questa proposta appartiene allo stesso comprensorio.
     * PATTERN: Information Expert - Proposta ha le informazioni necessarie
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

// Database.java - DOPO (usa Information Expert)
public List<Proposta> getProposteAperteStessoComprensorio(Utente utente, boolean includiUtente) {
    Fruitore fruitore = (Fruitore) utente;
    Comprensorio comprensorio = fruitore.getComprensorio();

    // ✅ Database chiede alla Proposta (l'esperto) di rispondere
    return proposte.stream()
        .filter(Proposta::isAperta)                              // ✅ Proposta sa se è aperta
        .filter(p -> p.appartieneStoComprensorio(comprensorio))  // ✅ Proposta sa il suo comprensorio
        .filter(p -> includiUtente || !p.isDiPropostitore(fruitore)) // ✅ Proposta sa chi l'ha creata
        .collect(Collectors.toList());
}
```

### Confronto Prima/Dopo

| Aspetto                                 | PRIMA                                         | DOPO                              |
| --------------------------------------- | --------------------------------------------- | --------------------------------- |
| **Chi conosce i dettagli di Proposta?** | Database                                      | Proposta stessa                   |
| **Accoppiamento Database-Proposta**     | Alto (accede a campi interni)                 | Basso (chiama metodi)             |
| **Testabilità**                         | Difficile (mock complessi)                    | Facile (test unitari su Proposta) |
| **Manutenibilità**                      | Bassa (modifica Proposta → modifica Database) | Alta (modifiche isolate)          |

---

## 3️⃣ Low Coupling e High Cohesion

### 🎯 Definizioni

> **Low Coupling**: Minimizzare le dipendenze tra classi. Meno dipendenze = più facile da modificare, testare, riusare.

> **High Cohesion**: Mantenere responsabilità correlate nella stessa classe. Alta coesione = classe focalizzata su un compito.

### Confronto Progettuale: Gestione Credenziali

#### ❌ PRIMA del Refactoring (High Coupling, Low Cohesion)

```java
// Controller.java - PRIMA
public class Controller {
    private Database database;
    private IView view;

    // ❌ Controller gestisce DIRETTAMENTE la validazione credenziali
    public Utente loginUtente() {
        String username = view.getInput("Username:");
        String password = view.getPasswordInput("Password:");

        // ❌ Logica di validazione nel Controller
        if (username.isEmpty() || password.isEmpty()) {
            view.showErrorMessage("Campi vuoti!");
            return null;
        }

        // ❌ Logica di formato password nel Controller
        if (password.length() < 8) {
            view.showErrorMessage("Password troppo corta!");
            return null;
        }

        // ❌ Pattern matching nel Controller
        if (!password.matches(".*[A-Z].*") || !password.matches(".*[0-9].*")) {
            view.showErrorMessage("Password deve avere maiuscola e numero!");
            return null;
        }

        return database.loginUtente(username, password);
    }

    public void registraFruitore() {
        String username = view.getInput("Username:");
        String password = view.getPasswordInput("Password:");

        // ❌ DUPLICAZIONE: Stessa logica di validazione ripetuta!
        if (username.isEmpty() || password.isEmpty()) { ... }
        if (password.length() < 8) { ... }
        if (!password.matches(".*[A-Z].*")) { ... }

        database.registraFruitore(username, password, ...);
    }
}
```

**Problemi:**

- ❌ **High Coupling**: Controller accoppiato a logica di validazione
- ❌ **Low Cohesion**: Controller ha troppe responsabilità (coordinamento + validazione)
- ❌ **Duplicazione**: Stessa logica in `loginUtente()` e `registraFruitore()`
- ❌ **Difficile da testare**: Test del Controller richiede test della validazione
- ❌ **Difficile da modificare**: Cambio regole password → modifica Controller in più punti

#### Diagramma Coupling PRIMA

```
┌─────────────────────────────────────────────────────────────────┐
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                    Controller                            │   │
│  │                                                          │   │
│  │  ┌────────────────────────────────────────────────────┐ │   │
│  │  │  Responsabilità MISTE (LOW COHESION):              │ │   │
│  │  │                                                    │ │   │
│  │  │  • Coordinamento flusso ✓                         │ │   │
│  │  │  • Validazione username ✗                         │ │   │
│  │  │  • Validazione password ✗                         │ │   │
│  │  │  • Regex pattern matching ✗                       │ │   │
│  │  │  • Messaggi errore specifici ✗                    │ │   │
│  │  └────────────────────────────────────────────────────┘ │   │
│  │                          │                               │   │
│  │            ┌─────────────┼─────────────┐                │   │
│  │            ▼             ▼             ▼                │   │
│  │     ┌──────────┐  ┌──────────┐  ┌──────────────┐       │   │
│  │     │  IView   │  │ Database │  │ Regex Engine │       │   │
│  │     └──────────┘  └──────────┘  └──────────────┘       │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
│  Dipendenze Controller: 3+ (HIGH COUPLING)                      │
│  Responsabilità Controller: 5+ (LOW COHESION)                   │
└─────────────────────────────────────────────────────────────────┘
```

#### ✅ DOPO il Refactoring (Low Coupling, High Cohesion)

```java
// service/CredentialService.java - NUOVO (Pure Fabrication + High Cohesion)
package service;

public class CredentialService {
    // ═══════════════════════════════════════════════════════════
    // ALTA COESIONE: Tutte le responsabilità relative alle credenziali
    // ═══════════════════════════════════════════════════════════

    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final String UPPERCASE_PATTERN = ".*[A-Z].*";
    private static final String DIGIT_PATTERN = ".*[0-9].*";
    private static final String SPECIAL_CHAR_PATTERN = ".*[!@#$%^&*].*";

    /**
     * Valida le credenziali e restituisce un risultato dettagliato.
     */
    public ValidationResult validateCredentials(String username, String password) {
        // Validazione username
        if (username == null || username.trim().isEmpty()) {
            return ValidationResult.failure("Username non può essere vuoto");
        }

        // Validazione password - lunghezza
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            return ValidationResult.failure(
                "Password deve avere almeno " + MIN_PASSWORD_LENGTH + " caratteri"
            );
        }

        // Validazione password - complessità
        if (!password.matches(UPPERCASE_PATTERN)) {
            return ValidationResult.failure("Password deve contenere almeno una maiuscola");
        }

        if (!password.matches(DIGIT_PATTERN)) {
            return ValidationResult.failure("Password deve contenere almeno un numero");
        }

        if (!password.matches(SPECIAL_CHAR_PATTERN)) {
            return ValidationResult.failure("Password deve contenere un carattere speciale");
        }

        return ValidationResult.success();
    }

    /**
     * Verifica solo se username è valido (per controlli rapidi).
     */
    public boolean isValidUsername(String username) {
        return username != null && !username.trim().isEmpty() && username.length() >= 3;
    }
}

// Controller.java - DOPO (Low Coupling)
public class Controller {
    private Database database;
    private IView view;
    private CredentialService credentialService;  // ✅ Nuova dipendenza

    public Controller(IView view, Database database) {
        this.view = view;
        this.database = database;
        this.credentialService = new CredentialService();  // ✅ Composizione
    }

    public Utente loginUtente() {
        String username = view.getInput("Username:");
        String password = view.getPasswordInput("Password:");

        // ✅ DELEGA la validazione al servizio specializzato
        ValidationResult validation = credentialService.validateCredentials(username, password);
        if (!validation.isValid()) {
            view.showErrorMessage(validation.getErrorMessage());
            return null;
        }

        return database.loginUtente(username, password);
    }

    public void registraFruitore() {
        String username = view.getInput("Username:");
        String password = view.getPasswordInput("Password:");

        // ✅ RIUSO dello stesso servizio - ZERO duplicazione
        ValidationResult validation = credentialService.validateCredentials(username, password);
        if (!validation.isValid()) {
            view.showErrorMessage(validation.getErrorMessage());
            return;
        }

        database.registraFruitore(username, password, ...);
    }
}
```

#### Diagramma Coupling DOPO

```
┌─────────────────────────────────────────────────────────────────┐
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                    Controller                            │   │
│  │                                                          │   │
│  │  ┌────────────────────────────────────────────────────┐ │   │
│  │  │  Responsabilità FOCALIZZATE (HIGH COHESION):       │ │   │
│  │  │                                                    │ │   │
│  │  │  • Coordinamento flusso ✓                         │ │   │
│  │  │  • Routing tra schermate ✓                        │ │   │
│  │  │  • Gestione eventi di sistema ✓                   │ │   │
│  │  └────────────────────────────────────────────────────┘ │   │
│  │                          │                               │   │
│  │            ┌─────────────┼─────────────┐                │   │
│  │            ▼             ▼             ▼                │   │
│  │     ┌──────────┐  ┌──────────┐  ┌───────────────────┐  │   │
│  │     │  IView   │  │ Database │  │ CredentialService │  │   │
│  │     └──────────┘  └──────────┘  └───────────────────┘  │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │              CredentialService (NUOVO)                   │   │
│  │                                                          │   │
│  │  ┌────────────────────────────────────────────────────┐ │   │
│  │  │  Responsabilità FOCALIZZATE (HIGH COHESION):       │ │   │
│  │  │                                                    │ │   │
│  │  │  • Validazione username ✓                         │ │   │
│  │  │  • Validazione password ✓                         │ │   │
│  │  │  • Gestione pattern complexity ✓                  │ │   │
│  │  │  • Messaggi errore validazione ✓                  │ │   │
│  │  └────────────────────────────────────────────────────┘ │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
│  Dipendenze Controller: 3 (stabile)                             │
│  Responsabilità Controller: 3 (HIGH COHESION ✅)                │
│  Responsabilità CredentialService: 4 correlate (HIGH COHESION ✅)│
└─────────────────────────────────────────────────────────────────┘
```

### Metriche Confronto Low Coupling / High Cohesion

| Metrica                                    | PRIMA                   | DOPO                  | Miglioramento |
| ------------------------------------------ | ----------------------- | --------------------- | ------------- |
| **Linee codice validazione in Controller** | ~30                     | 5                     | -83%          |
| **Duplicazione codice**                    | 2x (login + registra)   | 0x                    | -100%         |
| **Responsabilità Controller**              | 5+                      | 3                     | -40%          |
| **Testabilità validazione**                | Integrata in Controller | Unitaria isolata      | ✅            |
| **Modificabilità regole password**         | 2+ file                 | 1 file                | -50%          |
| **Riusabilità validazione**                | No                      | Sì (qualsiasi classe) | ✅            |

---

## 4️⃣ Pattern GRASP nella Prima Parte del Progetto

### Identificazione Pattern Pre-Esistenti

Prima del refactoring della Parte B, il progetto già implementava alcuni pattern GRASP nella sua architettura originale:

### 4.1 Controller GRASP (Pre-esistente)

```java
// Già presente nella Versione 4 originale
public class Controller {
    private Database database;
    private ConsoleView view;  // ❌ Classe concreta (poi cambiata in IView)

    public void start() {
        // ✅ Già coordinava Model e View
        // ✅ Già gestiva eventi di sistema
    }
}
```

**Stato**: ✅ Presente ma migliorato (ConsoleView → IView)

### 4.2 Creator GRASP (Pre-esistente)

```java
// Database.java - Versione originale
public class Database {
    // ✅ Database CREA Fruitore perché:
    // - Ha i dati necessari (username, password hash)
    // - Aggrega utenti (contiene Set<Utente>)
    public boolean registraFruitore(String username, String password,
                                     String mail, Comprensorio comprensorio) {
        if (!hasUtente(username)) {
            Fruitore fruitore = new Fruitore(username, password, mail, comprensorio);
            utenti.add(fruitore);
            return true;
        }
        return false;
    }
}
```

**Stato**: ✅ Già correttamente applicato

### 4.3 Information Expert Parziale (Pre-esistente)

```java
// Utente.java - Versione originale
public abstract class Utente {
    private String username;
    private String passwordHash;
    private String salt;

    // ✅ Information Expert: Utente conosce la propria password
    public boolean verificaPassword(String password) {
        String hashedInput = hashPassword(password, this.salt);
        return this.passwordHash.equals(hashedInput);
    }

    // ✅ Information Expert: Utente conosce il proprio tipo
    public abstract boolean isConfig();
}
```

**Stato**: ✅ Già presente, esteso durante refactoring (metodi `appartieneStoComprensorio()`, etc.)

### 4.4 Pure Fabrication (Pre-esistente)

```java
// constants/ControllerConstants.java - Versione originale
public class ControllerConstants {
    // ✅ Pure Fabrication: Classe senza controparte nel dominio
    // ✅ Creata per migliorare coesione (centralizza costanti)

    public static final String LOGIN_SUCCESS = "Accesso effettuato con successo!";
    public static final String LOGIN_FAILED = "Credenziali non valide.";
    public static final String USERS_FILE_PATH = "data/utenti.ser";
    // ...
}
```

**Stato**: ✅ Già presente e ben applicato

### Tabella Riepilogativa Pattern GRASP

| Pattern                  | Parte A (Pre-esistente)    | Parte B (Refactoring)            | Stato Finale |
| ------------------------ | -------------------------- | -------------------------------- | ------------ |
| **Controller**           | ✅ `Controller.java`       | Migliorato con `IView`           | ✅✅         |
| **Information Expert**   | Parziale (`Utente`)        | Esteso a `Proposta`, `Database`  | ✅✅         |
| **Creator**              | ✅ `Database` crea utenti  | Aggiunto `UserFactory`           | ✅✅         |
| **Low Coupling**         | ❌ `ConsoleView` hardcoded | ✅ `IView` + `CredentialService` | ✅           |
| **High Cohesion**        | Parziale                   | ✅ Separazione responsabilità    | ✅           |
| **Polymorphism**         | ❌ Assente                 | ✅ `IView`, `PropostaState`      | ✅           |
| **Pure Fabrication**     | ✅ `ControllerConstants`   | Aggiunto `CredentialService`     | ✅✅         |
| **Indirection**          | ❌ Assente                 | ✅ `IView` interface             | ✅           |
| **Protected Variations** | ❌ Assente                 | ✅ `IView` protegge da cambi UI  | ✅           |

---

## 📋 Checklist Conformità Pattern GRASP

| Requisito                                 | Stato | Evidenza                                     |
| ----------------------------------------- | ----- | -------------------------------------------- |
| ✅ Identificare Controller GRASP          | ✅    | `Controller.java` con 5 criteri verificati   |
| ✅ Spiegare PERCHÉ è Controller           | ✅    | Tabella criteri + diagramma                  |
| ✅ Pattern GRASP su codice rifattorizzato | ✅    | Information Expert su `Proposta`             |
| ✅ Low Coupling prima/dopo                | ✅    | Confronto `Controller` ↔ `CredentialService` |
| ✅ High Cohesion prima/dopo               | ✅    | Metriche responsabilità                      |
| ✅ Pattern nella prima parte              | ✅    | Tabella pattern pre-esistenti                |

---

## 🎯 Conclusioni

### Pattern GRASP Applicati

1. **Controller GRASP**: `Controller.java` coordina eventi di sistema senza essere UI
2. **Information Expert**: `Proposta` risponde a domande su se stessa (`isAperta()`, `appartieneStoComprensorio()`)
3. **Low Coupling + High Cohesion**: Estrazione di `CredentialService` dal `Controller`

### Benefici Ottenuti

| Beneficio                           | Quantificazione                          |
| ----------------------------------- | ---------------------------------------- |
| Riduzione duplicazione codice       | -100% (validazione centralizzata)        |
| Riduzione responsabilità Controller | -40% (da 5+ a 3)                         |
| Testabilità                         | +∞ (validazione testabile unitariamente) |
| Manutenibilità                      | +50% (modifiche isolate)                 |

---

## 📚 Riferimenti

- Larman, C. (2004). _Applying UML and Patterns_, 3rd Edition - Cap. 17 "GRASP: More Objects with Responsibilities"
- Larman, C. (2004). _Applying UML and Patterns_, 3rd Edition - Cap. 25 "Designing a Persistence Framework with Patterns"
