# Note Conclusive - Presentazione Parte B

## Durata: 20-25 minuti

---

## Struttura Consigliata della Presentazione

| Sezione                      | Tempo   | Focus                           |
| ---------------------------- | ------- | ------------------------------- |
| 1. Separazione Modello-Vista | 3-4 min | Package diagram + SSD           |
| 2. Pattern GRASP             | 3-4 min | Controller + Information Expert |
| 3. Principi SOLID            | 4-5 min | ISP + DIP con codice            |
| 4. Pattern GoF               | 4-5 min | Factory + State con diagrammi   |
| 5. Testing                   | 3-4 min | Casi di test + metodologia      |
| 6. Refactoring               | 3-4 min | Extract Class step-by-step      |

---

## Cosa NON Fare ❌

```
┌─────────────────────────────────────────────────────────────────────┐
│  EVITARE:                                                            │
│                                                                      │
│  ❌ "Il pattern Singleton garantisce un'unica istanza..."           │
│  ❌ "Il principio SRP dice che una classe deve avere..."            │
│  ❌ "Il testing black-box è una tecnica che..."                     │
│  ❌ Slide piene di testo teorico                                    │
│  ❌ Definizioni copiate da libri/Wikipedia                          │
└─────────────────────────────────────────────────────────────────────┘
```

---

## Cosa Fare ✅

```
┌─────────────────────────────────────────────────────────────────────┐
│  MOSTRARE:                                                           │
│                                                                      │
│  ✅ "Nel nostro progetto, Database è Singleton perché..."           │
│  ✅ "Abbiamo estratto IView per permettere il testing..."           │
│  ✅ "Questo diagramma mostra come Controller usa Factory..."        │
│  ✅ Diagrammi UML del VOSTRO codice                                 │
│  ✅ Snippet di codice REALE dal progetto                            │
└─────────────────────────────────────────────────────────────────────┘
```

---

## Riepilogo Pattern Applicati in ActivityNexus

### Pattern GRASP

| Pattern            | Dove                | Evidenza                                |
| ------------------ | ------------------- | --------------------------------------- |
| Controller GRASP   | `Controller.java`   | Coordina Model-View, gestisce eventi UI |
| Information Expert | `Database.java`     | Possiede i dati → fornisce i metodi     |
| Low Coupling       | `CredentialService` | Disaccoppia Controller da UserUtil      |
| High Cohesion      | `CredentialService` | Solo metodi per credenziali             |

### Pattern GoF

| Pattern        | Dove                            | Evidenza                             |
| -------------- | ------------------------------- | ------------------------------------ |
| Factory Method | `UserFactory`                   | `createUser(tipo, params)`           |
| Singleton      | `Database`, `CredentialService` | `getInstance()`                      |
| State          | `Proposta` + `StatoProposta`    | Transizioni di stato                 |
| Facade         | `Controller`                    | Interfaccia semplificata verso Model |

### Principi SOLID

| Principio | Dove                | Evidenza                       |
| --------- | ------------------- | ------------------------------ |
| SRP       | `CredentialService` | Una sola responsabilità        |
| OCP       | `UserFactory`       | Estendibile senza modifiche    |
| ISP       | `IView`             | Interfaccia specifica per View |
| DIP       | `Controller(IView)` | Dipende da astrazione          |

---

## Slide Suggerite

### Slide 1: Architettura MVC

```
┌─────────────────────────────────────────────────────────────────────┐
│  MOSTRARE IL PACKAGE DIAGRAM:                                        │
│                                                                      │
│     ┌─────────┐       ┌────────────┐       ┌─────────┐             │
│     │  view   │ ◄──── │ controller │ ────► │  model  │             │
│     └─────────┘       └────────────┘       └─────────┘             │
│                              │                                       │
│                              ▼                                       │
│                       ┌────────────┐                                │
│                       │  service   │                                │
│                       └────────────┘                                │
│                                                                      │
│  "Le dipendenze vanno solo verso il basso, mai da model a view"    │
└─────────────────────────────────────────────────────────────────────┘
```

### Slide 2: Factory Method (codice reale)

```java
// UserFactory.java - CODICE DAL PROGETTO
public static Utente createUser(String tipo, String username,
                                String password, Object... params) {
    return switch (tipo.toLowerCase()) {
        case "configuratore" -> new Configuratore(username, password);
        case "fruitore" -> new Fruitore(username, password,
                                        (String) params[0],
                                        (Comprensorio) params[1]);
        default -> throw new IllegalArgumentException("Tipo non valido");
    };
}
```

### Slide 3: State Pattern (diagramma stati)

```
┌─────────────────────────────────────────────────────────────────────┐
│                     STATI DI UNA PROPOSTA                            │
│                                                                      │
│           ┌─────────┐                                               │
│           │ APERTA  │◄────────────────┐                             │
│           └────┬────┘                 │                              │
│                │                      │                              │
│      accetta() │          ritira()    │ scade()                     │
│                ▼                      │                              │
│           ┌─────────┐           ┌─────────┐                         │
│           │ CHIUSA  │           │RITIRATA │                         │
│           └─────────┘           └─────────┘                         │
│                                                                      │
│  "Lo stato determina quali operazioni sono permesse"                │
└─────────────────────────────────────────────────────────────────────┘
```

### Slide 4: Dependency Injection (codice reale)

```java
// PRIMA - Alto accoppiamento
public class Controller {
    private ConsoleView view = new ConsoleView(); // ❌ Dipendenza concreta
}

// DOPO - Basso accoppiamento (DIP)
public class Controller {
    private final IView view;  // ✅ Dipendenza da astrazione

    public Controller(IView view, Database database) {
        this.view = view;  // Iniettata dall'esterno
    }
}
```

### Slide 5: Testing con MockView

```java
// Test isolato grazie a DIP
@Test
void testLoginUtente_Success() {
    MockView mockView = new MockView();           // Test double
    Database database = new Database();
    Controller controller = new Controller(mockView, database);

    mockView.addSimulatedInput("admin");          // Simula input
    mockView.addSimulatedInput("Admin123!");

    Utente utente = controller.loginUtente();

    assertNotNull(utente);                        // Verifica
}
```

### Slide 6: Extract Class (prima/dopo)

```
PRIMA:                           DOPO:
┌────────────────────┐           ┌────────────────────┐
│    Controller      │           │    Controller      │
├────────────────────┤           ├────────────────────┤
│ validateUsername() │           │                    │───┐
│ validatePassword() │  ──────►  │                    │   │
│ validateEmail()    │           └────────────────────┘   │
│ generateUsername() │                                    │
│ generatePassword() │           ┌────────────────────┐   │
└────────────────────┘           │CredentialService   │◄──┘
                                 ├────────────────────┤
                                 │ validateUsername() │
                                 │ validatePassword() │
                                 │ validateEmail()    │
                                 └────────────────────┘
```

---

## Frasi Chiave per la Presentazione

### Separazione MVC

> "Nel nostro progetto, il Controller non conosce i dettagli della View: riceve un'interfaccia IView che può essere ConsoleView in produzione o MockView nei test."

### GRASP Controller

> "Il Controller è l'unico punto di ingresso per le operazioni di business. Riceve gli input dalla View e coordina il Model."

### Factory Method

> "UserFactory ci permette di creare Configuratori o Fruitori senza modificare il codice client. Se domani aggiungiamo un Admin, basta estendere lo switch."

### State Pattern

> "Una Proposta può essere APERTA, CHIUSA o RITIRATA. Lo stato attuale determina quali operazioni sono valide."

### DIP/ISP

> "Grazie all'interfaccia IView, possiamo testare il Controller senza bisogno della console reale."

### Testing

> "Abbiamo usato Equivalence Partitioning per identificare 5 classi di test per il login: successo, password errata, utente non trovato, ecc."

### Refactoring

> "Il Controller aveva 3000+ righe. Con Extract Class abbiamo spostato la validazione credenziali in CredentialService, rispettando SRP."

---

## Checklist Pre-Presentazione

- [ ] Tutti i diagrammi mostrano il VOSTRO codice
- [ ] Gli snippet sono presi dal progetto reale
- [ ] Nessuna definizione teorica nelle slide
- [ ] Ogni pattern è mostrato con un esempio concreto
- [ ] I test sono eseguibili e passano tutti
- [ ] Il refactoring mostra PRIMA e DOPO

---

## File di Riferimento

| Documento                                                    | Contenuto                      |
| ------------------------------------------------------------ | ------------------------------ |
| [SEPARAZIONE_MODELLO_VISTA.md](SEPARAZIONE_MODELLO_VISTA.md) | MVC, Package Diagram, SSD      |
| [PATTERN_GRASP.md](PATTERN_GRASP.md)                         | Controller, Information Expert |
| [PRINCIPI_SOLID.md](PRINCIPI_SOLID.md)                       | SRP, OCP, ISP, DIP             |
| [PATTERN_GOF.md](PATTERN_GOF.md)                             | Factory, Singleton, State      |
| [TESTING.md](TESTING.md)                                     | Black-box, casi di test        |
| [REFACTORING.md](REFACTORING.md)                             | Extract Class                  |
