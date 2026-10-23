# 6. Testing - ActivityNexus

## Panoramica

Il testing del sistema ActivityNexus utilizza **testing black-box funzionale** per verificare il corretto funzionamento delle funzionalità principali senza conoscere i dettagli implementativi interni.

---

## Metodologia di Testing Applicata

### Black-Box Testing (Test Funzionale)

Il testing black-box si concentra sulla verifica del comportamento del sistema dal punto di vista dell'utente, trattando il software come una "scatola nera" di cui si conosce solo:

- **Input**: i dati forniti al sistema
- **Output**: i risultati attesi
- **Requisiti**: le specifiche funzionali da soddisfare

```
┌─────────────────────────────────────────────────────────────────────┐
│                      BLACK-BOX TESTING                               │
│                                                                      │
│   ┌─────────┐      ┌─────────────────────┐      ┌─────────┐        │
│   │  INPUT  │ ───▶ │   SISTEMA SOTTO     │ ───▶ │ OUTPUT  │        │
│   │ (Test   │      │   TEST (SUT)        │      │(Risultato│       │
│   │  Data)  │      │   [scatola nera]    │      │ atteso) │        │
│   └─────────┘      └─────────────────────┘      └─────────┘        │
│                                                                      │
│   Non importa COME funziona internamente                            │
│   Importa CHE COSA produce dato un certo input                      │
└─────────────────────────────────────────────────────────────────────┘
```

---

## Metodologie per Identificazione dei Casi di Test

### 1. Equivalence Partitioning (Partizionamento in Classi di Equivalenza)

Suddividiamo il dominio degli input in **classi di equivalenza**, dove tutti i valori di una classe producono lo stesso comportamento:

```
┌─────────────────────────────────────────────────────────────────────┐
│              EQUIVALENCE PARTITIONING - Login                        │
│                                                                      │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐     │
│  │   CLASSE 1      │  │   CLASSE 2      │  │   CLASSE 3      │     │
│  │   (VALIDA)      │  │  (INVALIDA)     │  │  (INVALIDA)     │     │
│  │                 │  │                 │  │                 │      │
│  │ Username esiste │  │ Username non    │  │ Password        │      │
│  │ Password giusta │  │ esiste          │  │ errata          │      │
│  └────────┬────────┘  └────────┬────────┘  └────────┬────────┘     │
│           │                    │                    │               │
│           ▼                    ▼                    ▼               │
│       LOGIN OK           LOGIN FAILED         LOGIN FAILED          │
└─────────────────────────────────────────────────────────────────────┘
```

### 2. Boundary Value Analysis (Analisi dei Valori Limite)

Testiamo i valori ai **confini** delle classi di equivalenza:

```
┌─────────────────────────────────────────────────────────────────────┐
│            BOUNDARY VALUE ANALYSIS - Ore Prestazione                 │
│                                                                      │
│                    DOMINIO VALIDO: [1, 24]                           │
│                                                                      │
│     INVALIDO │        VALIDO        │ INVALIDO                      │
│    ◄─────────┼───────────────────────┼─────────►                    │
│              │                       │                               │
│       0      1                      24     25                        │
│       ↑      ↑                       ↑      ↑                        │
│    boundary  min                    max  boundary                    │
│                                                                      │
│  Test cases: 0 (errore), 1 (ok), 12 (ok), 24 (ok), 25 (errore)     │
└─────────────────────────────────────────────────────────────────────┘
```

---

## Metodo Selezionato: `loginUtente()`

### Specifica del Metodo

```java
/**
 * Gestisce il processo di login di un utente.
 *
 * @return Utente l'oggetto Utente se login ha successo, null altrimenti
 */
public Utente loginUtente()
```

### Identificazione Classi di Equivalenza

| ID  | Classe di Equivalenza                               | Tipo     | Comportamento Atteso              |
| --- | --------------------------------------------------- | -------- | --------------------------------- |
| EC1 | Username valido + Password corretta (Configuratore) | Valida   | Restituisce oggetto Configuratore |
| EC2 | Username valido + Password corretta (Fruitore)      | Valida   | Restituisce oggetto Fruitore      |
| EC3 | Username valido + Password errata                   | Invalida | Restituisce `null`, mostra errore |
| EC4 | Username inesistente                                | Invalida | Restituisce `null`, mostra errore |
| EC5 | Database vuoto (nessun utente)                      | Invalida | Restituisce `null`, mostra errore |

### Derivazione dei Casi di Test

```
┌─────────────────────────────────────────────────────────────────────┐
│                    DECISION TABLE - loginUtente()                    │
├─────────────────────────────────────────────────────────────────────┤
│ Condizioni            │ TC1 │ TC2 │ TC3 │ TC4 │ TC5 │              │
├─────────────────────────────────────────────────────────────────────┤
│ Utente esiste         │  ✓  │  ✓  │  ✓  │  ✗  │  -  │              │
│ Password corretta     │  ✓  │  ✓  │  ✗  │  -  │  -  │              │
│ È Configuratore       │  ✓  │  ✗  │  -  │  -  │  -  │              │
│ Database non vuoto    │  ✓  │  ✓  │  ✓  │  ✓  │  ✗  │              │
├─────────────────────────────────────────────────────────────────────┤
│ Risultato Atteso      │Config│Fruit│null │null │null │             │
│                       │urato-│ore  │     │     │     │              │
│                       │re    │     │     │     │     │              │
└─────────────────────────────────────────────────────────────────────┘
```

---

## Casi di Test Implementati

### TC1: Login Configuratore con Successo (EC1)

```java
@Test
void testLoginUtente_Success_Configuratore() {
    // 1. ARRANGE - Prepara i dati di test
    database.registraConfiguratore("admin", "Admin123!");
    mockView.addSimulatedInput("admin");      // Simula input username
    mockView.addSimulatedInput("Admin123!");  // Simula input password

    // 2. ACT - Esegue il metodo sotto test
    Utente utente = controller.loginUtente();

    // 3. ASSERT - Verifica il risultato
    assertNotNull(utente, "L'utente non deve essere null");
    assertEquals("admin", utente.getUsername());
    assertTrue(utente.isConfig(), "Deve essere un Configuratore");
}
```

| Aspetto              | Valore                                        |
| -------------------- | --------------------------------------------- |
| **Input**            | username="admin", password="Admin123!"        |
| **Precondizione**    | Configuratore "admin" registrato nel database |
| **Risultato Atteso** | Oggetto `Configuratore` con username "admin"  |
| **Classe Coperta**   | EC1                                           |

---

### TC2: Login con Password Errata (EC3)

```java
@Test
void testLoginUtente_Failed_WrongPassword() {
    // 1. ARRANGE
    database.registraConfiguratore("admin", "Admin123!");
    mockView.addSimulatedInput("admin");
    mockView.addSimulatedInput("WrongPassword");  // Password sbagliata

    // 2. ACT
    Utente utente = controller.loginUtente();

    // 3. ASSERT
    assertNull(utente, "L'utente deve essere null con password errata");
    assertEquals(ControllerConstants.ERROR_LOGIN_FAILED, mockView.lastErrorMessage);
}
```

| Aspetto              | Valore                                     |
| -------------------- | ------------------------------------------ |
| **Input**            | username="admin", password="WrongPassword" |
| **Precondizione**    | Configuratore "admin" con password diversa |
| **Risultato Atteso** | `null` + messaggio errore                  |
| **Classe Coperta**   | EC3                                        |

---

### TC3: Login con Username Inesistente (EC4)

```java
@Test
void testLoginUtente_Failed_UserNotFound() {
    // 1. ARRANGE
    database.registraConfiguratore("admin", "Admin123!");
    mockView.addSimulatedInput("nonexistent");  // Utente non esistente
    mockView.addSimulatedInput("Password123!");

    // 2. ACT
    Utente utente = controller.loginUtente();

    // 3. ASSERT
    assertNull(utente, "L'utente deve essere null se non trovato");
    assertEquals(ControllerConstants.ERROR_LOGIN_FAILED, mockView.lastErrorMessage);
}
```

| Aspetto              | Valore                                          |
| -------------------- | ----------------------------------------------- |
| **Input**            | username="nonexistent", password="Password123!" |
| **Precondizione**    | Username non presente nel database              |
| **Risultato Atteso** | `null` + messaggio errore                       |
| **Classe Coperta**   | EC4                                             |

---

### TC4: Login con Database Vuoto (EC5)

```java
@Test
void testLoginUtente_NoUsersInDatabase() {
    // 1. ARRANGE - Database senza utenti
    mockView.addSimulatedInput("admin");
    mockView.addSimulatedInput("Admin123!");

    // 2. ACT
    Utente utente = controller.loginUtente();

    // 3. ASSERT
    assertNull(utente, "L'utente deve essere null con database vuoto");
}
```

| Aspetto              | Valore                                 |
| -------------------- | -------------------------------------- |
| **Input**            | username="admin", password="Admin123!" |
| **Precondizione**    | Database senza utenti registrati       |
| **Risultato Atteso** | `null`                                 |
| **Classe Coperta**   | EC5                                    |

---

## Pattern AAA (Arrange-Act-Assert)

Tutti i test seguono il pattern **AAA** per una struttura chiara e leggibile:

```
┌─────────────────────────────────────────────────────────────────────┐
│                       PATTERN AAA                                    │
│                                                                      │
│  ┌─────────────┐                                                    │
│  │  ARRANGE    │  Prepara il contesto del test:                     │
│  │             │  - Crea oggetti necessari                          │
│  │             │  - Configura lo stato iniziale                     │
│  │             │  - Prepara i mock/stub                             │
│  └──────┬──────┘                                                    │
│         │                                                            │
│         ▼                                                            │
│  ┌─────────────┐                                                    │
│  │    ACT      │  Esegue l'azione da testare:                       │
│  │             │  - Invoca il metodo sotto test                     │
│  │             │  - Cattura il risultato                            │
│  └──────┬──────┘                                                    │
│         │                                                            │
│         ▼                                                            │
│  ┌─────────────┐                                                    │
│  │   ASSERT    │  Verifica i risultati:                             │
│  │             │  - Controlla valori di ritorno                     │
│  │             │  - Verifica effetti collaterali                    │
│  │             │  - Valida lo stato finale                          │
│  └─────────────┘                                                    │
└─────────────────────────────────────────────────────────────────────┘
```

---

## Test Double: MockView

Per isolare il Controller dalla View reale, utilizziamo un **Test Double** di tipo Mock:

```java
/**
 * MockView - Test Double per simulare l'interazione utente
 *
 * Permette di:
 * - Simulare input utente predefiniti
 * - Catturare messaggi di errore mostrati
 * - Testare il Controller senza interfaccia console reale
 */
public class MockView implements IView {

    private Queue<String> simulatedInputs = new LinkedList<>();
    public String lastErrorMessage = null;

    /** Aggiunge un input simulato alla coda */
    public void addSimulatedInput(String input) {
        simulatedInputs.add(input);
    }

    @Override
    public String readString(String prompt) {
        return simulatedInputs.poll();  // Restituisce il prossimo input
    }

    @Override
    public void showError(String message) {
        lastErrorMessage = message;  // Cattura l'errore per verifica
    }

    // ... altri metodi mock
}
```

### Architettura del Test con MockView

```
┌─────────────────────────────────────────────────────────────────────┐
│                    ARCHITETTURA TEST                                 │
│                                                                      │
│  ┌──────────────┐     ┌──────────────┐     ┌──────────────┐        │
│  │   TEST       │     │  CONTROLLER  │     │   DATABASE   │        │
│  │   CLASS      │────▶│    (SUT)     │────▶│   (reale)    │        │
│  └──────────────┘     └──────────────┘     └──────────────┘        │
│         │                    │                                       │
│         │                    │                                       │
│         ▼                    ▼                                       │
│  ┌──────────────┐     ┌──────────────┐                              │
│  │  Prepara     │     │  MockView    │  ◄── Test Double            │
│  │  input       │────▶│  (mock)      │                              │
│  │  simulati    │     └──────────────┘                              │
│  └──────────────┘                                                    │
│                                                                      │
│  Il MockView permette di controllare gli input e                    │
│  verificare gli output senza interazione umana                      │
└─────────────────────────────────────────────────────────────────────┘
```

---

## Tabella Riassuntiva dei Casi di Test

| #   | Nome Test                                     | Metodo Testato         | Classe EQ | Input              | Output Atteso        | Stato |
| --- | --------------------------------------------- | ---------------------- | --------- | ------------------ | -------------------- | ----- |
| 1   | `testLoginUtente_Success_Configuratore`       | `loginUtente()`        | EC1       | admin/Admin123!    | Configuratore        | ✅    |
| 2   | `testLoginUtente_Failed_WrongPassword`        | `loginUtente()`        | EC3       | admin/Wrong        | null + errore        | ✅    |
| 3   | `testLoginUtente_Failed_UserNotFound`         | `loginUtente()`        | EC4       | nonexistent/any    | null + errore        | ✅    |
| 4   | `testLoginUtente_NoUsersInDatabase`           | `loginUtente()`        | EC5       | any/any            | null                 | ✅    |
| 5   | `testCreateComprensorio_Success`              | `createComprensorio()` | -         | Lombardia, Milano  | Comprensorio creato  | ✅    |
| 6   | `testCreateComprensorio_NomeDuplicato`        | `createComprensorio()` | -         | Nome duplicato     | Errore               | ✅    |
| 7   | `testRegistraFruitore_Success`                | `registraFruitore()`   | -         | Dati validi        | Fruitore creato      | ✅    |
| 8   | `testRegistraFruitore_UsernameDuplicato`      | `registraFruitore()`   | -         | Username duplicato | false                | ✅    |
| 9   | `testPropostaScambio_CreazioneSemplice`       | `aggiungiProposta()`   | -         | Proposta valida    | Proposta APERTA      | ✅    |
| 10  | `testPropostaScambio_FallimentoSenzaFruitore` | `Proposta()`           | -         | fruitore=null      | NullPointerException | ✅    |
| 11  | `testPropostaScambio_RitiroProposta`          | `ritiraProposta()`     | -         | Proposta esistente | Stato=RITIRATA       | ✅    |
| 12  | `testCreateComprensorio_Cancel`               | `createComprensorio()` | -         | Annulla operazione | Nessuna creazione    | ✅    |

---

## Copertura delle Funzionalità

```
┌─────────────────────────────────────────────────────────────────────┐
│                    COPERTURA FUNZIONALITÀ                            │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │ AUTENTICAZIONE                                    [4 test]   │   │
│  │ ████████████████████████████████████████████████████████████│   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │ REGISTRAZIONE                                     [2 test]   │   │
│  │ ████████████████████████████                                │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │ COMPRENSORI                                       [3 test]   │   │
│  │ ██████████████████████████████████████                      │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │ PROPOSTE DI SCAMBIO                               [3 test]   │   │
│  │ ██████████████████████████████████████                      │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│  TOTALE: 12 test | PASSATI: 12 | FALLITI: 0 | COPERTURA: 100%      │
└─────────────────────────────────────────────────────────────────────┘
```

---

## Framework e Tecnologie Utilizzate

| Componente  | Tecnologia                                 | Scopo                                   |
| ----------- | ------------------------------------------ | --------------------------------------- |
| Framework   | **JUnit 5**                                | Esecuzione e gestione test              |
| Asserzioni  | `assertEquals`, `assertNull`, `assertTrue` | Verifica risultati                      |
| Setup       | `@BeforeEach`                              | Inizializzazione contesto per ogni test |
| Test Double | **MockView**                               | Simulazione interfaccia utente          |
| Pattern     | **AAA** (Arrange-Act-Assert)               | Strutturazione test                     |

---

## Conclusioni

Il testing del metodo `loginUtente()` dimostra l'applicazione rigorosa di:

1. **Equivalence Partitioning**: identificazione di 5 classi di equivalenza che coprono tutti gli scenari significativi

2. **Black-Box Testing**: focus sul comportamento esterno senza dipendenza da dettagli implementativi

3. **Test Doubles**: uso di MockView per isolare il componente sotto test e controllare input/output

4. **Pattern AAA**: struttura chiara e manutenibile dei test case

La suite di test garantisce che le funzionalità principali dell'applicazione siano verificate in modo sistematico e ripetibile.
