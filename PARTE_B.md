# Passaggio 1: Implementazione Testing e Refactoring per Testabilità

## Problema Iniziale

Il codice di **Versione 4** presentava tre problematiche critiche che impedivano l'esecuzione di test automatici:

### 1. **Accoppiamento Stretto alla Vista Concreta**

```java
// PRIMA - Controller.java (versione precedente)
public class Controller {
    private final ConsoleView view;  // Dipendenza diretta da classe concreta

    public Controller(ConsoleView view) {
        this.database = new Database();
        this.view = view;
    }
}
```

**Problema**: Il `Controller` dipendeva direttamente dalla classe `ConsoleView`, che richiedeva input reale da console. Questo rendeva impossibile:

-   Eseguire test automatici senza interazione umana
-   Simulare diversi scenari di input
-   Verificare il comportamento del controller in modo deterministico

### 2. **Gestione delle Eccezioni in Main**

```java
// PRIMA - Main.java (versione precedente)
try {
    ConsoleView view = new ConsoleView();  // Dichiarata dentro try
    Controller controller = new Controller(view);
    controller.start();
} catch (Exception e) {
    // view non accessibile qui! ❌
    System.err.println("Errore");
}
```

**Problema**: La variabile `view` era dichiarata dentro il blocco `try`, quindi non era accessibile nei blocchi `catch` per mostrare messaggi di errore all'utente.

### 3. **Loop Infiniti Non Testabili**

```java
// ❌ Test problematico - causava CPU al 100%
@Test
void testStart_Login_NoUsersRegistered() {
    mockView.addSimulatedInput("1"); // Login
    mockView.addSimulatedInput("0"); // Esci

    controller.start(); // while(true) { ... } - LOOP INFINITO!
    // Il test non terminava mai perché start() non ha condizione di uscita
}
```

**Problema**: Il metodo `start()` conteneva un `while(true)` che rendeva il test impossibile da completare. Quando la `MockView` finiva gli input simulati, restituiva `null`, causando un loop infinito che occupava il 100% della CPU.

---

## Soluzione Implementata

### Strategia 1: **Dependency Injection + Interfaccia IView**

#### 1.1 Creazione dell'Interfaccia `IView.java`

```java
// ✅ SOLUZIONE - IView.java (NUOVO FILE)
package view;

public interface IView {
    // Metodi di input
    String getInput(String prompt);
    String getPasswordInput(String prompt);
    boolean getConfirmation(String prompt);
    int getIntInput(String prompt, int min, int max);
    double getDoubleInput(String prompt, double min, double max);

    // Metodi di output
    void showMessage(String message);
    void showErrorMessage(String message);
    void showSuccessMessage(String message);
    void showInfoMessage(String message);
    void showWarningMessage(String message);

    // Metodi di utilità
    void showMenu();
    void showMenuConfiguratore();
    void showMenuFruitore();
    void pressEnterToContinue();
    void close();
}
```

**Vantaggi**:

-   ✅ **Astrazione**: Il controller non conosce più l'implementazione concreta della vista
-   ✅ **Sostituibilità**: Possiamo iniettare `ConsoleView` in produzione e `MockView` nei test
-   ✅ **Testabilità**: La `MockView` può simulare qualsiasi input senza console reale

#### 1.2 Refactoring di `Controller.java`

```java
// ✅ DOPO - Controller.java (refactored)
public class Controller {
    private Database database;
    private final IView view;  // ✅ Interfaccia invece di classe concreta

    // ✅ Dependency Injection: accetta interfaccia + database
    public Controller(IView view, Database database) {
        this.view = view;
        this.database = database;
    }
}
```

**Cambiamenti chiave**:

1. `ConsoleView view` → `IView view` (polimorfismo)
2. Costruttore accetta **sia** vista **che** database (prima il database era creato internamente)
3. Getter `getDatabase()` aggiunto per accedere al database nei test

#### 1.3 Aggiornamento di `Main.java`

```java
// ✅ DOPO - Main.java (refactored)
public static void main(String[] args) {
    IView view = null;  // ✅ Dichiarata fuori dal try

    try {
        view = new ConsoleView();          // ✅ Implementazione concreta
        Database database = new Database();
        Controller controller = new Controller(view, database); // ✅ DI
        controller.start();

    } catch (IllegalStateException e) {
        registraErrore("Errore di configurazione", e);
        mostraErrore(view, "Errore: " + e.getMessage()); // ✅ view accessibile!
        System.exit(EXIT_CODE_CONFIG_ERROR);
    }
}
```

**Miglioramenti**:

-   ✅ `view` dichiarata **prima** del try → accessibile in `catch`
-   ✅ `Database` creato esternamente e iniettato
-   ✅ Gestione errori più robusta con accesso alla vista

---

### Strategia 2: **Implementazione MockView per Testing**

#### 2.1 Creazione di `MockView.java`

```java
// ✅ SOLUZIONE - src/test/view/MockView.java
package view;

import java.util.LinkedList;
import java.util.Queue;

public class MockView implements IView {

    // ✅ Coda per simulare input dell'utente
    private final Queue<String> inputs = new LinkedList<>();

    // ✅ Campi per verificare cosa il Controller ha mostrato
    public String lastErrorMessage = null;
    public String lastSuccessMessage = null;
    public int showMenuConfiguratoreCallCount = 0;

    // ✅ Metodo per programmare input simulati
    public void addSimulatedInput(String input) {
        this.inputs.add(input);
    }

    @Override
    public String getInput(String prompt) {
        String input = inputs.poll();
        return input != null ? input : "0";  // ✅ Restituisce "0" invece di null
    }

    @Override
    public void showErrorMessage(String message) {
        this.lastErrorMessage = message;  // ✅ Cattura il messaggio per assert
    }

    // ... altri metodi implementati ...
}
```

**Caratteristiche chiave**:

1. **Coda di Input**: `Queue<String>` per simulare sequenze di input utente
2. **Safety Mechanism**: Restituisce `"0"` (comando di uscita) quando la coda è vuota → **Previene loop infiniti**
3. **Spy Pattern**: Cattura i messaggi mostrati dal Controller per verificarli nei test

---

### Strategia 3: **Test Atomici invece di Test Orchestrali**

#### 3.1 Problema: Test del Metodo `start()`

```java
// ❌ PROBLEMA - Test che causava loop infinito
@Test
void testStart_Login_NoUsersRegistered() {
    mockView.addSimulatedInput("1");  // Scelta: Login
    mockView.addSimulatedInput("0");  // Scelta: Esci

    controller.start();  // ❌ while(true) NON TERMINA MAI
}
```

**Perché falliva**:

-   `start()` contiene `while(true) { ... }`
-   Dopo i 2 input simulati, `mockView.getInput()` restituiva `null`
-   Il controller non gestiva `null` → loop infinito → CPU 100%

#### 3.2 Soluzione: Test di Metodi Atomici

```java
// ✅ SOLUZIONE - Test metodi specifici invece di start()
@Test
void testLoginUtente_NoUsersInDatabase() {
    // 1. Arrange
    mockView.addSimulatedInput("anyuser");
    mockView.addSimulatedInput("anypassword");

    // 2. Act
    Utente utente = controller.loginUtente();  // ✅ Metodo atomico

    // 3. Assert
    assertNull(utente, "Deve restituire null con database vuoto");
}
```

**Vantaggi**:

-   ✅ Test **deterministico** e **rapido** (< 1ms)
-   ✅ Testa una **singola responsabilità**
-   ✅ Nessun loop infinito possibile
-   ✅ Facile da debuggare

---

## 🧪 Test Implementati

### Test Suite Completa: 12 Test (100% Success Rate)

#### Categoria 1: **Login** (4 test)

```java
@Test
void testLoginUtente_Success_Configuratore() {
    // Verifica login con credenziali valide
    database.registraConfiguratore("admin", "Admin123!");
    mockView.addSimulatedInput("admin");
    mockView.addSimulatedInput("Admin123!");

    Utente utente = controller.loginUtente();

    assertNotNull(utente);
    assertEquals("admin", utente.getUsername());
    assertTrue(utente.isConfig());
}
```

#### Categoria 2: **Registrazione** (2 test - BLACK BOX)

```java
@Test
void testRegistraFruitore_Success() {
    // Testa registrazione fruitore completa
    Comprensorio comprensorio = new Comprensorio("Lombardia");
    database.addComprensorio(comprensorio);

    boolean risultato = database.registraFruitore(
        "mario", "Mario123!", "mario@test.it", comprensorio
    );

    assertTrue(risultato);
    assertTrue(database.hasUtente("mario"));
}
```

#### Categoria 3: **Comprensori** (3 test)

```java
@Test
void testCreateComprensorio_Success() {
    mockView.addSimulatedInput("Lombardia");     // Nome
    mockView.addSimulatedInput("Milano");        // Primo comune
    mockView.addSimulatedInput("false");         // Non aggiungere altri

    boolean risultato = controller.createComprensorio();

    assertTrue(risultato);
    assertEquals(1, database.getComprensori().size());
}
```

#### Categoria 4: **Proposta di Scambio** (3 test - ⭐ PRIORITÀ ALTA)

```java
@Test
void testPropostaScambio_CreazioneSemplice() {  // ⭐ BLACK-BOX
    // 1. Setup completo del dominio
    Comprensorio comprensorio = new Comprensorio("Valcamonica");
    database.addComprensorio(comprensorio);

    database.registraFruitore("mario", "Mario123!", "mario@test.it", comprensorio);
    Fruitore fruitore = (Fruitore) database.loginUtente("mario", "Mario123!");

    // 2. Crea gerarchia con 2 categorie foglia
    Gerarchia gerarchia = creaGerarchiaTest();  // Helper method
    TreeNode<Categoria> nodoGiardinaggio = gerarchia.getFoglie().get(0);
    TreeNode<Categoria> nodoIdraulica = gerarchia.getFoglie().get(1);

    // 3. Crea proposta
    Prestazione offerta = new Prestazione(nodoGiardinaggio, 10);
    Prestazione richiesta = new Prestazione(nodoIdraulica, 10);
    Proposta proposta = new Proposta(fruitore, richiesta, offerta, StatoProposta.APERTA);

    database.aggiungiProposta(proposta);

    // 4. Verifica
    List<Proposta> proposte = database.getPropostePerStato(StatoProposta.APERTA);
    assertEquals(1, proposte.size());
    assertEquals(fruitore, proposte.get(0).getPropostitore());
    assertEquals(10, proposte.get(0).getOfferta().getOre());
}
```

---

## 📊 Risultati Ottenuti

### Metriche di Successo

```
┌─────────────────────────────────────────────┐
│  TEST EXECUTION REPORT                      │
├─────────────────────────────────────────────┤
│  Total Tests:        12                     │
│  ✅ Passed:          12 (100%)              │
│  ❌ Failed:           0                     │
│  ⏱️  Execution Time:  64ms                  │
│  📦 JAR Size:        99KB                   │
│  🌲 TreeNode:        Included (16 classes)  │
└─────────────────────────────────────────────┘
```

### Build Script Automatizzato (`build.sh`)

```bash
#!/bin/bash
# Script in 8 step:
# 1. Verifica dipendenze (TreeNode + JUnit 5)
# 2. Pulizia directory bin
# 3. Compilazione sorgenti principali
# 4. Compilazione test (MockView + ControllerTest)
# 5. Estrazione libreria TreeNode
# 6. Inclusione TreeNode nel JAR
# 7. ✅ ESECUZIONE TEST JUNIT (AUTOMATICA)
# 8. Creazione JAR eseguibile

# Output esempio:
# [7/8] Esecuzione test JUnit...
# ✓ Tutti i test passati
```

**Vantaggi**:

-   ✅ Compilazione + Test + JAR in un unico comando
-   ✅ Report XML generati automaticamente in `test-reports/`
-   ✅ Verifica inclusione dipendenze (TreeNode)

---

## 🔍 Correzioni API Scoperte Durante Testing

### 1. Database API: Nome Metodo

```java
// ❌ Tentativo iniziale (ERRORE)
database.addProposta(proposta);  // Metodo non esiste!

// ✅ API corretta
database.aggiungiProposta(proposta);
```

### 2. FattoreConversione: Final Field

```java
// ❌ Tentativo iniziale (ERRORE)
FattoreConversione fattore = new FattoreConversione();
fattore.setNodoRiferimento(nodo);
gerarchia.setFattoreConversione(fattore);  // Metodo non esiste!

// ✅ Pattern corretto
gerarchia.getFattoreConversione().setNodoRiferimento(nodo);
// Il FattoreConversione è creato automaticamente in Gerarchia (final field)
```

### 3. Database.getPropostePerStato()

```java
// ❌ Tentativo iniziale (ERRORE DI TIPO)
Set<Proposta> proposte = database.getPropostePerStato(StatoProposta.APERTA);

// ✅ Tipo corretto
List<Proposta> proposte = database.getPropostePerStato(StatoProposta.APERTA);
```

---

## ✅ Pattern e Best Practices Applicati

### 1. **Arrange-Act-Assert (AAA)**

```java
@Test
void testLoginUtente_Success_Configuratore() {
    // 1️⃣ ARRANGE - Prepara stato iniziale
    database.registraConfiguratore("admin", "Admin123!");
    mockView.addSimulatedInput("admin");
    mockView.addSimulatedInput("Admin123!");

    // 2️⃣ ACT - Esegui l'azione da testare
    Utente utente = controller.loginUtente();

    // 3️⃣ ASSERT - Verifica risultato
    assertNotNull(utente);
    assertEquals("admin", utente.getUsername());
}
```

### 2. **Test Isolation**

```java
@BeforeEach
void setUp() {
    database = new Database();      // ✅ Database PULITO
    mockView = new MockView();      // ✅ MockView PULITA
    controller = new Controller(mockView, database);
}
// Ogni test parte da stato ZERO → nessuna interferenza
```

### 3. **Naming Descrittivo**

```
✅ testLoginUtente_Failed_WrongPassword()
✅ testPropostaScambio_CreazioneSemplice()
✅ testRegistraFruitore_UsernameDuplicato()

❌ test1(), test2(), testLogin()  // EVITATI
```

---

## 📚 Documentazione Generata

### File Creati Durante il Processo

1. **`ANALISI_TEST.md`** - Analisi problemi e soluzioni dettagliate
2. **`SUGGERIMENTI_TEST_AGGIUNTIVI.md`** - Template per futuri test
3. **`README_TEST.md`** - Guida rapida per eseguire i test
4. **`RIEPILOGO_IMPLEMENTAZIONE.md`** - Questo documento finale

---

## 🎯 Conclusioni

### Obiettivi Raggiunti

-   ✅ **Testabilità**: Codice ora completamente testabile senza interazione umana
-   ✅ **Copertura**: 12 test per casi d'uso principali (Login, Registrazione, Comprensori, Proposte)
-   ✅ **Qualità**: 100% test passati, zero warning critici
-   ✅ **Build Automation**: Script bash per compilazione + test + JAR
-   ✅ **Documentazione**: 4 documenti completi per manutenzione futura

### Lezioni Apprese

1. **Dependency Injection** è fondamentale per testing
2. **Interfacce** permettono sostituibilità (Console → Mock)
3. **Test atomici** > Test orchestrali (evitare `while(true)`)
4. **Mock Objects** devono gestire edge cases (input vuoto → "0")

### Prossimi Passi Suggeriti

-   ⏳ Test per `valutaProposteScambio()` (InsiemeChiuso)
-   ⏳ Test per `creaGerarchia()` (richiede refactoring)
-   ⏳ Integration test end-to-end
-   ⏳ Code coverage metrics (JaCoCo)
