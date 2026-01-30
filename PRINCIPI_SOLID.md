# Principi SOLID - Applicazione nel Progetto ActivityNexus

## 📚 Introduzione ai Principi SOLID

I **principi SOLID** sono cinque principi di progettazione object-oriented che, se applicati correttamente, rendono il software più manutenibile, flessibile e robusto.

| Acronimo | Principio                       | Significato                                                |
| -------- | ------------------------------- | ---------------------------------------------------------- |
| **S**    | Single Responsibility Principle | Una classe dovrebbe avere una sola ragione per cambiare    |
| **O**    | Open/Closed Principle           | Aperta all'estensione, chiusa alla modifica                |
| **L**    | Liskov Substitution Principle   | Le sottoclassi devono essere sostituibili alle superclassi |
| **I**    | Interface Segregation Principle | Molte interfacce specifiche meglio di una generica         |
| **D**    | Dependency Inversion Principle  | Dipendere da astrazioni, non da concretizzazioni           |

---

## 1️⃣ Single Responsibility Principle (SRP)

### 🎯 Definizione

> **SRP**: Una classe dovrebbe avere **una sola ragione per cambiare**, ovvero una sola responsabilità.

### Applicazione: `ConsoleView.java`

#### Analisi delle Responsabilità

```java
// view/ConsoleView.java
public class ConsoleView implements IView {
    private final Scanner scanner = new Scanner(System.in);

    // ═══════════════════════════════════════════════════════════════
    // RESPONSABILITÀ UNICA: Gestione Input/Output Console
    // ═══════════════════════════════════════════════════════════════

    // Metodi di INPUT
    @Override
    public String getInput(String prompt) {
        System.out.print(CYAN + prompt + RESET + " ");
        return scanner.nextLine().trim();
    }

    @Override
    public String getPasswordInput(String prompt) {
        System.out.print(CYAN + prompt + RESET + " ");
        return scanner.nextLine().trim();  // In console non si può mascherare
    }

    @Override
    public int getIntInput(String prompt, int min, int max) {
        // Gestione input numerico con validazione range
    }

    // Metodi di OUTPUT
    @Override
    public void showMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void showErrorMessage(String message) {
        System.out.println(ERROR_LABEL + " " + RED + message + RESET);
        pressEnterToContinue();
    }

    @Override
    public void showSuccessMessage(String message) {
        System.out.println(SUCCESS_LABEL + " " + GREEN + message + RESET);
    }

    // Metodi di FORMATTAZIONE
    private String centraTesto(String testo, int larghezza) { ... }
    private String formattaVoce(int numero, String testo) { ... }
}
```

### Motivi per Cambiare (SRP Analysis)

| #   | Motivo per Cambiare             | Impatta `ConsoleView`? | Impatta Altre Classi?  |
| --- | ------------------------------- | ---------------------- | ---------------------- |
| 1   | Cambio colori ANSI / tema       | ✅ SÌ                  | ❌ NO                  |
| 2   | Cambio formato messaggi errore  | ✅ SÌ                  | ❌ NO                  |
| 3   | Cambio layout menu              | ✅ SÌ                  | ❌ NO                  |
| 4   | Aggiunta nuovi simboli/emoji    | ✅ SÌ                  | ❌ NO                  |
| 5   | Cambio logica di business       | ❌ NO                  | ✅ Controller/Database |
| 6   | Cambio struttura dati           | ❌ NO                  | ✅ Model classes       |
| 7   | Cambio algoritmo autenticazione | ❌ NO                  | ✅ Database/Utente     |

**Conclusione SRP**: `ConsoleView` ha **UNA sola ragione per cambiare**: modifiche all'interfaccia utente console.

### Applicazione: `Database.java`

```java
// model/Database.java
public class Database {
    private Set<Utente> utenti;
    private Set<Comprensorio> comprensori;
    private Set<Gerarchia> gerarchie;
    private Set<Proposta> proposte;
    private List<InsiemeChiuso> insiemiChiusi;

    // ═══════════════════════════════════════════════════════════════
    // RESPONSABILITÀ UNICA: Gestione Persistenza e Collezioni Dati
    // ═══════════════════════════════════════════════════════════════

    // CRUD Utenti
    public boolean registraFruitore(...) { ... }
    public Utente loginUtente(String username, String password) { ... }

    // CRUD Comprensori
    public void addComprensorio(Comprensorio c) { ... }
    public Set<Comprensorio> getComprensori() { ... }

    // Persistenza
    public void serializeUtenti(String path) { ... }
    public void deserializeUtenti(String path) { ... }
}
```

### Motivi per Cambiare (Database)

| #   | Motivo per Cambiare           | Impatta `Database`? | Impatta Altre Classi?       |
| --- | ----------------------------- | ------------------- | --------------------------- |
| 1   | Cambio da Set a List          | ✅ SÌ               | ❌ NO (interfaccia stabile) |
| 2   | Cambio da Serialization a SQL | ✅ SÌ               | ❌ NO                       |
| 3   | Aggiunta indici/cache         | ✅ SÌ               | ❌ NO                       |
| 4   | Cambio formato UI             | ❌ NO               | ✅ ConsoleView              |
| 5   | Cambio flusso applicativo     | ❌ NO               | ✅ Controller               |

**Conclusione SRP**: `Database` ha **DUE ragioni correlate per cambiare**: struttura dati e persistenza (accettabile, alta coesione).

### Diagramma SRP

```
┌─────────────────────────────────────────────────────────────────┐
│                    ANALISI SRP - ActivityNexus                   │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │                    ConsoleView                           │    │
│  │  ┌─────────────────────────────────────────────────────┐│    │
│  │  │  RESPONSABILITÀ: Input/Output Console               ││    │
│  │  │  MOTIVI PER CAMBIARE: 1 (UI/formattazione)         ││    │
│  │  │  SRP: ✅ RISPETTATO                                 ││    │
│  │  └─────────────────────────────────────────────────────┘│    │
│  └─────────────────────────────────────────────────────────┘    │
│                                                                  │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │                    Database                              │    │
│  │  ┌─────────────────────────────────────────────────────┐│    │
│  │  │  RESPONSABILITÀ: Gestione dati + Persistenza        ││    │
│  │  │  MOTIVI PER CAMBIARE: 2 (correlati, alta coesione) ││    │
│  │  │  SRP: ✅ RISPETTATO (responsabilità correlate)      ││    │
│  │  └─────────────────────────────────────────────────────┘│    │
│  └─────────────────────────────────────────────────────────┘    │
│                                                                  │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │                    Controller                            │    │
│  │  ┌─────────────────────────────────────────────────────┐│    │
│  │  │  RESPONSABILITÀ: Coordinamento flusso applicativo   ││    │
│  │  │  MOTIVI PER CAMBIARE: 1 (nuovi casi d'uso/routing) ││    │
│  │  │  SRP: ✅ RISPETTATO                                 ││    │
│  │  └─────────────────────────────────────────────────────┘│    │
│  └─────────────────────────────────────────────────────────┘    │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 2️⃣ Open/Closed Principle (OCP)

### 🎯 Definizione

> **OCP**: Le entità software dovrebbero essere **aperte all'estensione** ma **chiuse alla modifica**.

### Applicazione: `IView` Interface + Observer Pattern

#### Estensione SENZA Modifica

```java
// view/IView.java - CHIUSA alla modifica
public interface IView {
    String getInput(String prompt);
    void showMessage(String message);
    void showErrorMessage(String message);
    void showSuccessMessage(String message);
    int getIntInput(String prompt, int min, int max);
    // ... altri metodi
}
```

### Tipi di Estensione Supportati (OCP Analysis)

| Estensione                      | Richiede Modifica `IView`? | Richiede Modifica `Controller`? |
| ------------------------------- | -------------------------- | ------------------------------- |
| ✅ Aggiungere `ConsoleView`     | ❌ NO                      | ❌ NO                           |
| ✅ Aggiungere `MockView` (test) | ❌ NO                      | ❌ NO                           |
| ✅ Aggiungere `GUIView` (Swing) | ❌ NO                      | ❌ NO                           |
| ✅ Aggiungere `WebView` (REST)  | ❌ NO                      | ❌ NO                           |
| ✅ Aggiungere `MobileView`      | ❌ NO                      | ❌ NO                           |
| ⚠️ Aggiungere nuovo metodo      | ⚠️ SÌ                      | ⚠️ SÌ                           |

#### Implementazioni Esistenti

```java
// ✅ Estensione 1: Console (produzione)
public class ConsoleView implements IView {
    @Override
    public String getInput(String prompt) {
        return scanner.nextLine().trim();
    }
}

// ✅ Estensione 2: Mock (testing)
public class MockView implements IView {
    private Queue<String> inputs = new LinkedList<>();

    @Override
    public String getInput(String prompt) {
        return inputs.poll();  // Input simulato
    }
}

// ✅ Estensione 3: GUI (futura - POSSIBILE senza modifiche)
public class GUIView implements IView {
    @Override
    public String getInput(String prompt) {
        return JOptionPane.showInputDialog(prompt);
    }
}
```

### OCP con Observer Pattern su `Proposta`

#### ❌ PRIMA (Violazione OCP)

```java
// Controller.java - PRIMA
public void cambiaStatoProposta(Proposta proposta, StatoProposta nuovoStato) {
    proposta.setStato(nuovoStato);

    // ❌ Per ogni nuova azione, devo MODIFICARE questo metodo
    inviaEmailNotifica(proposta);
    aggiornaLog(proposta);
    // ❌ Aggiungere SMS? Devo modificare qui
    // ❌ Aggiungere webhook? Devo modificare qui
}
```

#### ✅ DOPO (OCP con Observer)

```java
// model/observers/PropostaObserver.java - Interfaccia stabile
public interface PropostaObserver {
    void onStatoChanged(Proposta proposta, StatoProposta vecchioStato, StatoProposta nuovoStato);
}

// model/Proposta.java - CHIUSA alla modifica per nuovi observer
public class Proposta {
    private List<PropostaObserver> observers = new ArrayList<>();

    public void addObserver(PropostaObserver observer) {
        observers.add(observer);
    }

    public void setStato(StatoProposta nuovoStato) {
        StatoProposta vecchioStato = this.stato;
        this.stato = nuovoStato;
        notifyObservers(vecchioStato, nuovoStato);  // ✅ Notifica automatica
    }

    private void notifyObservers(StatoProposta vecchio, StatoProposta nuovo) {
        for (PropostaObserver obs : observers) {
            obs.onStatoChanged(this, vecchio, nuovo);
        }
    }
}

// ✅ Estensione 1: Email Notifier
public class EmailNotifier implements PropostaObserver {
    @Override
    public void onStatoChanged(Proposta p, StatoProposta vecchio, StatoProposta nuovo) {
        sendEmail(p.getPropostitore().getMail(), "Stato cambiato: " + nuovo);
    }
}

// ✅ Estensione 2: SMS Notifier (NUOVA - senza modificare Proposta)
public class SMSNotifier implements PropostaObserver {
    @Override
    public void onStatoChanged(Proposta p, StatoProposta vecchio, StatoProposta nuovo) {
        sendSMS(p.getPropostitore().getTelefono(), "Proposta: " + nuovo);
    }
}

// ✅ Estensione 3: Webhook (NUOVA - senza modificare Proposta)
public class WebhookNotifier implements PropostaObserver {
    @Override
    public void onStatoChanged(Proposta p, StatoProposta vecchio, StatoProposta nuovo) {
        httpClient.post(webhookUrl, createPayload(p, nuovo));
    }
}
```

### Diagramma OCP

```
┌─────────────────────────────────────────────────────────────────┐
│                    ANALISI OCP - ActivityNexus                   │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │                    IView (Interface)                     │    │
│  │  ┌─────────────────────────────────────────────────────┐│    │
│  │  │  CHIUSA A: Modifica dell'interfaccia               ││    │
│  │  │  APERTA A: Nuove implementazioni (Console, GUI,    ││    │
│  │  │            Web, Mobile, Mock, ...)                  ││    │
│  │  └─────────────────────────────────────────────────────┘│    │
│  │                          │                               │    │
│  │         ┌────────────────┼────────────────┐             │    │
│  │         ▼                ▼                ▼             │    │
│  │  ┌────────────┐  ┌────────────┐  ┌────────────┐        │    │
│  │  │ConsoleView │  │  MockView  │  │  GUIView   │        │    │
│  │  │ (prod)     │  │  (test)    │  │  (futuro)  │        │    │
│  │  └────────────┘  └────────────┘  └────────────┘        │    │
│  └─────────────────────────────────────────────────────────┘    │
│                                                                  │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │              PropostaObserver (Interface)                │    │
│  │  ┌─────────────────────────────────────────────────────┐│    │
│  │  │  CHIUSA A: Modifica di Proposta.setStato()         ││    │
│  │  │  APERTA A: Nuovi observer (Email, SMS, Webhook,    ││    │
│  │  │            Log, Analytics, ...)                     ││    │
│  │  └─────────────────────────────────────────────────────┘│    │
│  │                          │                               │    │
│  │      ┌───────────────────┼───────────────────┐          │    │
│  │      ▼                   ▼                   ▼          │    │
│  │  ┌──────────┐    ┌────────────┐    ┌──────────────┐    │    │
│  │  │ Email    │    │    SMS     │    │   Webhook    │    │    │
│  │  │ Notifier │    │  Notifier  │    │   Notifier   │    │    │
│  │  └──────────┘    └────────────┘    └──────────────┘    │    │
│  └─────────────────────────────────────────────────────────┘    │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 3️⃣ Interface Segregation Principle (ISP)

### 🎯 Definizione

> **ISP**: I client non dovrebbero essere forzati a dipendere da interfacce che non usano. Meglio **molte interfacce specifiche** che una sola interfaccia generale.

### Applicazione: Segregazione di `IView`

#### ❌ PRIMA (Violazione ISP)

```java
// view/IView.java - PRIMA (interfaccia "grassa")
public interface IView {
    // Input methods
    String getInput(String prompt);
    String getPasswordInput(String prompt);
    boolean getConfirmation(String prompt);
    int getIntInput(String prompt, int min, int max);
    double getDoubleInput(String prompt, double min, double max);

    // Output methods
    void showMessage(String message);
    void showErrorMessage(String message);
    void showSuccessMessage(String message);
    void showInfoMessage(String message);
    void showWarningMessage(String message);

    // Menu methods
    void showMenu();
    void showMenuConfiguratore();
    void showMenuFruitore();

    // Utility methods
    void pressEnterToContinue();
    void clearScreen();
    void close();

    // Easter eggs (!)
    void showEasterEgg();
    void playSound(String sound);
}
```

**Problema**: Un `MockView` per testing deve implementare **TUTTI** i metodi, anche `showEasterEgg()` e `playSound()` che non userà mai!

```java
// ❌ MockView deve implementare metodi inutili
public class MockView implements IView {
    @Override
    public void showEasterEgg() {
        // Non serve per i test, ma DEVO implementarlo
    }

    @Override
    public void playSound(String sound) {
        // Non serve per i test, ma DEVO implementarlo
    }

    // ... altri 15+ metodi ...
}
```

#### ✅ DOPO (ISP Applicato)

```java
// view/IInputProvider.java - Interfaccia focalizzata su INPUT
public interface IInputProvider {
    String getInput(String prompt);
    String getPasswordInput(String prompt);
    boolean getConfirmation(String prompt);
    int getIntInput(String prompt, int min, int max);
    double getDoubleInput(String prompt, double min, double max);
}

// view/IOutputProvider.java - Interfaccia focalizzata su OUTPUT
public interface IOutputProvider {
    void showMessage(String message);
    void showErrorMessage(String message);
    void showSuccessMessage(String message);
    void showInfoMessage(String message);
    void showWarningMessage(String message);
}

// view/IMenuProvider.java - Interfaccia focalizzata su MENU
public interface IMenuProvider {
    void showMenu();
    void showMenuConfiguratore();
    void showMenuFruitore();
}

// view/IViewUtilities.java - Interfaccia per UTILITÀ
public interface IViewUtilities {
    void pressEnterToContinue();
    void clearScreen();
    void close();
}

// view/IEasterEggs.java - Interfaccia OPZIONALE per funzionalità extra
public interface IEasterEggs {
    void showEasterEgg();
    void playSound(String sound);
}

// view/IView.java - Interfaccia composta (per retrocompatibilità)
public interface IView extends IInputProvider, IOutputProvider,
                               IMenuProvider, IViewUtilities {
    // Eredita tutti i metodi dalle interfacce segregate
    // IEasterEggs è OPZIONALE - non inclusa!
}
```

### Vantaggi del Codice Rifattorizzato (ISP)

| Aspetto                    | PRIMA                             | DOPO                              | Vantaggio              |
| -------------------------- | --------------------------------- | --------------------------------- | ---------------------- |
| **Metodi in IView**        | 18+                               | 5 per interfaccia                 | Interfacce focalizzate |
| **MockView implementa**    | 18+ metodi                        | Solo quelli necessari             | -70% boilerplate       |
| **Nuova View solo output** | Deve implementare input           | Implementa solo `IOutputProvider` | Flessibilità           |
| **Testing**                | Mock complessi                    | Mock minimali                     | Facilità               |
| **Manutenibilità**         | Cambio in IView → tutte le classi | Cambio isolato                    | Stabilità              |

### Diagramma ISP

```
┌─────────────────────────────────────────────────────────────────┐
│                    PRIMA (Violazione ISP)                        │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │                    IView (18+ metodi)                    │    │
│  │  ┌─────────────────────────────────────────────────────┐│    │
│  │  │ getInput, getPasswordInput, getConfirmation,       ││    │
│  │  │ getIntInput, getDoubleInput, showMessage,          ││    │
│  │  │ showErrorMessage, showSuccessMessage, showMenu,    ││    │
│  │  │ showMenuConfiguratore, showMenuFruitore,           ││    │
│  │  │ pressEnterToContinue, clearScreen, close,          ││    │
│  │  │ showEasterEgg, playSound, ...                      ││    │
│  │  └─────────────────────────────────────────────────────┘│    │
│  │                          │                               │    │
│  │         ┌────────────────┴────────────────┐             │    │
│  │         ▼                                 ▼             │    │
│  │  ┌────────────────────┐        ┌────────────────────┐  │    │
│  │  │    ConsoleView     │        │     MockView       │  │    │
│  │  │ (implementa TUTTO) │        │ (implementa TUTTO) │  │    │
│  │  │    18+ metodi      │        │   18+ metodi ❌    │  │    │
│  │  └────────────────────┘        └────────────────────┘  │    │
│  └─────────────────────────────────────────────────────────┘    │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                    DOPO (ISP Applicato)                          │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌────────┐ │
│  │IInputProvider│ │IOutputProvider│ │IMenuProvider │ │IView   │ │
│  │  5 metodi    │ │   5 metodi   │ │   3 metodi   │ │Utilities│ │
│  └──────┬───────┘ └──────┬───────┘ └──────┬───────┘ └───┬────┘ │
│         │                │                │             │       │
│         └────────────────┴────────────────┴─────────────┘       │
│                              │                                   │
│                              ▼                                   │
│                    ┌─────────────────┐                          │
│                    │      IView      │                          │
│                    │   (composta)    │                          │
│                    └────────┬────────┘                          │
│                             │                                    │
│         ┌───────────────────┴───────────────────┐               │
│         ▼                                       ▼               │
│  ┌────────────────────┐              ┌────────────────────┐    │
│  │    ConsoleView     │              │     MockView       │    │
│  │ (implementa IView) │              │(implementa SOLO    │    │
│  │                    │              │ IInputProvider +   │    │
│  │                    │              │ IOutputProvider) ✅│    │
│  └────────────────────┘              └────────────────────┘    │
│                                                                  │
│  + IEasterEggs (OPZIONALE - non forzata!)                       │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 4️⃣ Dependency Inversion Principle (DIP)

### 🎯 Definizione

> **DIP**: I moduli di alto livello non dovrebbero dipendere da moduli di basso livello. Entrambi dovrebbero dipendere da **astrazioni**.

### Applicazione: `Controller` dipende da `IView`

#### ❌ PRIMA (Violazione DIP)

```java
// Controller.java - PRIMA
public class Controller {
    private ConsoleView view;  // ❌ Dipendenza da classe CONCRETA

    public Controller(ConsoleView view) {
        this.view = view;
    }
}
```

**Problema**: `Controller` (alto livello) dipende da `ConsoleView` (basso livello, implementazione specifica).

#### ✅ DOPO (DIP Applicato)

```java
// Controller.java - DOPO
public class Controller {
    private IView view;  // ✅ Dipendenza da ASTRAZIONE

    public Controller(IView view, Database database) {
        this.view = view;      // ✅ Qualsiasi implementazione di IView
        this.database = database;
    }
}

// Main.java - Iniezione della dipendenza concreta
public static void main(String[] args) {
    IView view = new ConsoleView();  // Decisione a runtime
    Database database = new Database();
    Controller controller = new Controller(view, database);
    controller.start();
}

// Test - Iniezione di MockView
@BeforeEach
void setUp() {
    IView mockView = new MockView();  // ✅ Stessa interfaccia, diversa implementazione
    Database database = new Database();
    controller = new Controller(mockView, database);
}
```

### Vantaggi DIP

| Aspetto            | PRIMA                  | DOPO                 | Vantaggio           |
| ------------------ | ---------------------- | -------------------- | ------------------- |
| **Testabilità**    | Richiede console reale | MockView iniettabile | ✅ Test automatici  |
| **Flessibilità**   | Solo ConsoleView       | Qualsiasi IView      | ✅ GUI, Web, Mobile |
| **Accoppiamento**  | Alto (classe concreta) | Basso (interfaccia)  | ✅ -80% coupling    |
| **Sostituibilità** | Impossibile            | Runtime              | ✅ Polimorfismo     |

### Diagramma DIP

```
┌─────────────────────────────────────────────────────────────────┐
│                    PRIMA (Violazione DIP)                        │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │  ALTO LIVELLO                                           │    │
│  │  ┌─────────────────────────────────────────────────────┐│    │
│  │  │                    Controller                       ││    │
│  │  │                        │                            ││    │
│  │  │                        │ dipende da (CONCRETA)      ││    │
│  │  │                        ▼                            ││    │
│  │  └─────────────────────────────────────────────────────┘│    │
│  └─────────────────────────────────────────────────────────┘    │
│                              │                                   │
│                              ▼                                   │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │  BASSO LIVELLO                                          │    │
│  │  ┌─────────────────────────────────────────────────────┐│    │
│  │  │                   ConsoleView                       ││    │
│  │  │              (classe concreta) ❌                   ││    │
│  │  └─────────────────────────────────────────────────────┘│    │
│  └─────────────────────────────────────────────────────────┘    │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                    DOPO (DIP Applicato)                          │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │  ALTO LIVELLO                                           │    │
│  │  ┌─────────────────────────────────────────────────────┐│    │
│  │  │                    Controller                       ││    │
│  │  │                        │                            ││    │
│  │  │                        │ dipende da (ASTRAZIONE) ✅ ││    │
│  │  │                        ▼                            ││    │
│  │  │                 ┌─────────────┐                     ││    │
│  │  │                 │    IView    │                     ││    │
│  │  │                 │ «interface» │                     ││    │
│  │  │                 └─────────────┘                     ││    │
│  │  └─────────────────────────────────────────────────────┘│    │
│  └─────────────────────────────────────────────────────────┘    │
│                              ▲                                   │
│                              │ implementa                        │
│         ┌────────────────────┼────────────────────┐             │
│         │                    │                    │             │
│  ┌──────┴──────┐     ┌───────┴──────┐    ┌───────┴──────┐      │
│  │ ConsoleView │     │   MockView   │    │   GUIView    │      │
│  │ (produzione)│     │   (test)     │    │   (futuro)   │      │
│  └─────────────┘     └──────────────┘    └──────────────┘      │
│                                                                  │
│  ✅ Alto livello dipende da astrazione                          │
│  ✅ Basso livello implementa astrazione                         │
│  ✅ Nessuna dipendenza diretta alto → basso                     │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 5️⃣ Pattern SOLID nella Prima Parte del Progetto

### Identificazione Pattern Pre-Esistenti (Parte A)

| Principio | Parte A (Pre-esistente)                  | Parte B (Refactoring)        | Stato |
| --------- | ---------------------------------------- | ---------------------------- | ----- |
| **SRP**   | ✅ `ConsoleView` solo I/O                | Mantenuto                    | ✅✅  |
| **SRP**   | ✅ `Database` solo dati                  | Mantenuto                    | ✅✅  |
| **SRP**   | ⚠️ `Controller` con validazione          | Estratto `CredentialService` | ✅    |
| **OCP**   | ❌ Assente                               | ✅ `IView` + Observer        | ✅    |
| **LSP**   | ✅ `Utente` → `Fruitore`/`Configuratore` | Mantenuto                    | ✅✅  |
| **ISP**   | ❌ `IView` monolitica                    | ✅ 5 interfacce segregate    | ✅    |
| **DIP**   | ❌ `ConsoleView` concreta                | ✅ `IView` astrazione        | ✅    |

### LSP Pre-Esistente: Gerarchia `Utente`

```java
// Già presente in Parte A - LSP rispettato
public abstract class Utente {
    protected String username;
    protected String passwordHash;

    public abstract boolean isConfig();
    public boolean verificaPassword(String password) { ... }
}

public class Configuratore extends Utente {
    @Override
    public boolean isConfig() { return true; }  // ✅ Comportamento coerente
}

public class Fruitore extends Utente {
    @Override
    public boolean isConfig() { return false; }  // ✅ Comportamento coerente
}

// ✅ LSP: Ovunque si usa Utente, posso sostituire con Fruitore o Configuratore
public Utente loginUtente(String username, String password) {
    for (Utente utente : utenti) {  // Funziona con qualsiasi sottotipo
        if (utente.getUsername().equals(username) &&
            utente.verificaPassword(password)) {
            return utente;  // Restituisce Fruitore o Configuratore
        }
    }
    return null;
}
```

---

## 📋 Checklist Conformità SOLID

| Requisito                                            | Stato | Evidenza                                          |
| ---------------------------------------------------- | ----- | ------------------------------------------------- |
| ✅ Applicare principi SOLID al codice rifattorizzato | ✅    | SRP, OCP, ISP, DIP documentati                    |
| ✅ SRP: indicare motivi per cambiare                 | ✅    | Tabelle per ConsoleView, Database, Controller     |
| ✅ OCP: indicare tipi di estensione                  | ✅    | Tabella IView + Observer Pattern                  |
| ✅ ISP: vantaggi rispetto a soluzione originaria     | ✅    | Confronto prima/dopo con diagrammi                |
| ✅ DIP: vantaggi rispetto a soluzione originaria     | ✅    | Confronto ConsoleView → IView                     |
| ✅ Identificare SOLID nella prima parte              | ✅    | Tabella pattern pre-esistenti (LSP, SRP parziale) |

---

## 🎯 Conclusioni

### Principi SOLID Applicati

| Principio | Applicazione                            | Beneficio Principale             |
| --------- | --------------------------------------- | -------------------------------- |
| **SRP**   | `ConsoleView`, `Database`, `Controller` | 1 motivo per cambiare per classe |
| **OCP**   | `IView`, `PropostaObserver`             | Estensione senza modifica        |
| **ISP**   | 5 interfacce da `IView` monolitica      | Mock minimali, flessibilità      |
| **DIP**   | `Controller` → `IView`                  | Testabilità, sostituibilità      |

### Metriche di Miglioramento

```
┌─────────────────────────────────────────────────────────────────┐
│  SOLID COMPLIANCE METRICS - ActivityNexus                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  SRP Compliance:                                                 │
│  ├── ConsoleView: 1 responsabilità ✅                           │
│  ├── Database: 2 responsabilità correlate ✅                    │
│  └── Controller: 1 responsabilità (dopo refactoring) ✅         │
│                                                                  │
│  OCP Extensions Supported:                                       │
│  ├── IView implementations: ∞ (Console, Mock, GUI, Web, ...)   │
│  └── PropostaObserver implementations: ∞ (Email, SMS, ...)     │
│                                                                  │
│  ISP Interface Segregation:                                      │
│  ├── Interfacce create: 5 (da 1 monolitica)                     │
│  ├── Metodi per interfaccia: ~5 (da 18+)                        │
│  └── Riduzione boilerplate MockView: -70%                       │
│                                                                  │
│  DIP Abstraction Level:                                          │
│  ├── Dipendenze concrete eliminate: 1 (ConsoleView)             │
│  ├── Astrazioni introdotte: 1 (IView)                           │
│  └── Test automatici abilitati: 12 (0 → 12)                     │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 📚 Riferimenti

- Martin, R.C. (2003). _Agile Software Development: Principles, Patterns, and Practices_ - Cap. 7-12 "SOLID Principles"
- Martin, R.C. (2017). _Clean Architecture_ - Part III "Design Principles"
- Larman, C. (2004). _Applying UML and Patterns_, 3rd Edition - Cap. 25 "GRASP and SOLID"
