# Pattern GoF - Applicazione nel Progetto ActivityNexus

## 📚 Introduzione ai Pattern GoF

I **Pattern GoF** (Gang of Four) sono 23 pattern di progettazione descritti nel libro _"Design Patterns: Elements of Reusable Object-Oriented Software"_ (1994). Sono suddivisi in tre categorie:

| Categoria           | Pattern                                                                                                            | Scopo                          |
| ------------------- | ------------------------------------------------------------------------------------------------------------------ | ------------------------------ |
| **Creazionali**     | Factory Method, Abstract Factory, Singleton, Builder, Prototype                                                    | Creazione di oggetti           |
| **Strutturali**     | Adapter, Bridge, Composite, Decorator, Facade, Flyweight, Proxy                                                    | Composizione di classi/oggetti |
| **Comportamentali** | Chain of Responsibility, Command, Iterator, Mediator, Memento, Observer, State, Strategy, Template Method, Visitor | Interazione tra oggetti        |

### Pattern Applicati in ActivityNexus

| Pattern                        | Categoria       | Applicazione                       |
| ------------------------------ | --------------- | ---------------------------------- |
| **Factory Method + Singleton** | Creazionale     | `UserFactory` per creazione utenti |
| **State**                      | Comportamentale | `PropostaState` per stati proposta |
| **Observer**                   | Comportamentale | `PropostaObserver` per notifiche   |
| **Facade**                     | Strutturale     | `Controller` come punto d'ingresso |

---

## 1️⃣ Factory Method + Singleton: `UserFactory`

### 🎯 Definizione dei Pattern

> **Factory Method**: Definisce un'interfaccia per creare oggetti, ma lascia alle sottoclassi la decisione su quale classe istanziare.

> **Singleton**: Garantisce che una classe abbia una sola istanza e fornisce un punto di accesso globale ad essa.

### Motivazione

Nel progetto ActivityNexus esistono due tipi di utenti (`Configuratore` e `Fruitore`) che condividono una classe base (`Utente`). La creazione di questi oggetti:

1. Richiede **validazione** dei parametri
2. Deve essere **centralizzata** per consistenza
3. Non deve esporre i costruttori delle sottoclassi ai client

### Implementazione

```java
// model/factory/UserFactory.java
package model.factory;

public class UserFactory {

    // ═══════════════════════════════════════════════════════════════
    // SINGLETON PATTERN
    // ═══════════════════════════════════════════════════════════════

    /** Istanza singleton della factory (eager initialization) */
    private static final UserFactory INSTANCE = new UserFactory();

    /**
     * Costruttore privato per impedire istanziazione diretta.
     * Garantisce che esista una sola istanza della factory.
     */
    private UserFactory() {
        // Costruttore vuoto - factory stateless
    }

    /**
     * Restituisce l'istanza singleton della factory.
     * @return l'unica istanza di UserFactory
     */
    public static UserFactory getInstance() {
        return INSTANCE;
    }

    // ═══════════════════════════════════════════════════════════════
    // FACTORY METHOD PATTERN
    // ═══════════════════════════════════════════════════════════════

    /**
     * Enumerazione type-safe per i tipi di utente.
     */
    public enum UserType {
        CONFIGURATORE,
        FRUITORE
    }

    /**
     * Factory Method generico per creare utenti.
     *
     * @param type il tipo di utente da creare
     * @param username username univoco
     * @param password password in chiaro (sarà hashata)
     * @param extraParams parametri extra (email, comprensorio per Fruitore)
     * @return nuova istanza di Utente
     */
    public Utente createUser(UserType type, String username, String password,
                             Object... extraParams) {
        Objects.requireNonNull(type, "Il tipo utente non può essere null");

        return switch (type) {
            case CONFIGURATORE -> createConfiguratore(username, password);
            case FRUITORE -> createFruitoreFromParams(username, password, extraParams);
        };
    }

    /**
     * Factory Method specifico per Configuratore.
     */
    public Configuratore createConfiguratore(String username, String password) {
        validateCredentials(username, password);
        return new Configuratore(username, password);
    }

    /**
     * Factory Method specifico per Fruitore.
     */
    public Fruitore createFruitore(String username, String password,
                                    String email, Comprensorio comprensorio) {
        validateCredentials(username, password);
        Objects.requireNonNull(email, "Email non può essere null");
        return new Fruitore(username, password, email, comprensorio);
    }

    /**
     * Validazione centralizzata delle credenziali.
     */
    private void validateCredentials(String username, String password) {
        Objects.requireNonNull(username, "Username non può essere null");
        Objects.requireNonNull(password, "Password non può essere null");
        if (username.isBlank()) {
            throw new IllegalArgumentException("Username non può essere vuoto");
        }
        if (password.isBlank()) {
            throw new IllegalArgumentException("Password non può essere vuota");
        }
    }
}
```

### Diagramma delle Classi

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        FACTORY METHOD + SINGLETON                            │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐     │
│  │                         «singleton»                                 │     │
│  │                        UserFactory                                  │     │
│  ├────────────────────────────────────────────────────────────────────┤     │
│  │ - INSTANCE: UserFactory = new UserFactory()                        │     │
│  ├────────────────────────────────────────────────────────────────────┤     │
│  │ - UserFactory()                                                    │     │
│  │ + getInstance(): UserFactory                                       │     │
│  │ + createUser(type, username, password, extraParams): Utente       │     │
│  │ + createConfiguratore(username, password): Configuratore          │     │
│  │ + createFruitore(username, password, email, comp): Fruitore      │     │
│  │ - validateCredentials(username, password): void                   │     │
│  └────────────────────────────────────────────────────────────────────┘     │
│                                    │                                         │
│                                    │ «creates»                               │
│                    ┌───────────────┴───────────────┐                        │
│                    ▼                               ▼                        │
│           ┌────────────────┐             ┌────────────────┐                 │
│           │    «enum»      │             │    Utente      │                 │
│           │   UserType     │             │  «abstract»    │                 │
│           ├────────────────┤             ├────────────────┤                 │
│           │ CONFIGURATORE  │             │ - username     │                 │
│           │ FRUITORE       │             │ - passwordHash │                 │
│           └────────────────┘             │ - salt         │                 │
│                                          ├────────────────┤                 │
│                                          │ + isConfig()   │                 │
│                                          │ + verificaPwd()│                 │
│                                          └────────┬───────┘                 │
│                                                   │                         │
│                                    ┌──────────────┴──────────────┐          │
│                                    │                             │          │
│                           ┌────────┴───────┐           ┌─────────┴──────┐   │
│                           │  Configuratore │           │    Fruitore    │   │
│                           ├────────────────┤           ├────────────────┤   │
│                           │                │           │ - email        │   │
│                           │ + isConfig()   │           │ - comprensorio │   │
│                           │   → true       │           │ + isConfig()   │   │
│                           └────────────────┘           │   → false      │   │
│                                                        └────────────────┘   │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Diagramma di Sequenza: Creazione Utente

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                  SEQUENZA: Registrazione Fruitore                            │
├─────────────────────────────────────────────────────────────────────────────┤

    ┌──────────┐        ┌─────────────┐        ┌───────────┐      ┌─────────┐
    │ Database │        │ UserFactory │        │ Fruitore  │      │ Utente  │
    └────┬─────┘        └──────┬──────┘        └─────┬─────┘      └────┬────┘
         │                     │                     │                 │
         │ registraFruitore()  │                     │                 │
         │ ════════════════════│                     │                 │
         │                     │                     │                 │
         │ getInstance()       │                     │                 │
         │────────────────────>│                     │                 │
         │                     │                     │                 │
         │    «singleton»      │                     │                 │
         │<────────────────────│                     │                 │
         │     INSTANCE        │                     │                 │
         │                     │                     │                 │
         │ createFruitore(     │                     │                 │
         │   username,         │                     │                 │
         │   password,         │                     │                 │
         │   email,            │                     │                 │
         │   comprensorio)     │                     │                 │
         │────────────────────>│                     │                 │
         │                     │                     │                 │
         │                     │ validateCredentials │                 │
         │                     │────────┐            │                 │
         │                     │        │            │                 │
         │                     │<───────┘            │                 │
         │                     │                     │                 │
         │                     │ new Fruitore(...)   │                 │
         │                     │────────────────────>│                 │
         │                     │                     │                 │
         │                     │                     │ hashPassword()  │
         │                     │                     │────────────────>│
         │                     │                     │                 │
         │                     │                     │<────────────────│
         │                     │                     │                 │
         │                     │    fruitore         │                 │
         │<────────────────────│<────────────────────│                 │
         │                     │                     │                 │
         │ utenti.add(fruitore)│                     │                 │
         │────────┐            │                     │                 │
         │        │            │                     │                 │
         │<───────┘            │                     │                 │
         │                     │                     │                 │
    └────┴─────┘        └──────┴──────┘        └─────┴─────┘      └────┴────┘

```

### Uso nel Database

```java
// model/Database.java - Usa UserFactory
public class Database {

    /**
     * Registra un nuovo Fruitore usando la Factory.
     */
    public boolean registraFruitore(String username, String password,
                                     String mail, Comprensorio comprensorio) {
        if (!hasUtente(username)) {
            // ✅ Usa Factory invece di new Fruitore() diretto
            Fruitore fruitore = UserFactory.getInstance()
                .createFruitore(username, password, mail, comprensorio);
            utenti.add(fruitore);
            return true;
        }
        return false;
    }

    /**
     * Registra un nuovo Configuratore usando la Factory.
     */
    public boolean registraConfiguratore(String username, String password) {
        if (!hasUtente(username)) {
            // ✅ Usa Factory invece di new Configuratore() diretto
            Configuratore config = UserFactory.getInstance()
                .createConfiguratore(username, password);
            utenti.add(config);
            return true;
        }
        return false;
    }
}
```

### Vantaggi dell'Implementazione

| Aspetto           | Senza Factory                 | Con Factory + Singleton          |
| ----------------- | ----------------------------- | -------------------------------- |
| **Validazione**   | Sparsa in più punti           | Centralizzata in un punto        |
| **Consistenza**   | Possibili costruzioni errate  | Tutte le istanze validate        |
| **Testabilità**   | Difficile mockare costruttori | Factory mockabile                |
| **Estensibilità** | Modifica in più punti         | Aggiunta di un case nello switch |
| **Memoria**       | Multiple istanze factory      | Una sola istanza (Singleton)     |

---

## 2️⃣ State Pattern: `PropostaState`

### 🎯 Definizione del Pattern

> **State**: Permette a un oggetto di **modificare il proprio comportamento** quando il suo stato interno cambia. L'oggetto sembrerà cambiare la propria classe.

### Motivazione

Una `Proposta` nel sistema può trovarsi in tre stati:

- **APERTA**: può essere chiusa o ritirata
- **CHIUSA**: stato finale, nessuna transizione possibile
- **RITIRATA**: stato finale, nessuna transizione possibile

Senza State Pattern, la logica sarebbe piena di `if-else`:

```java
// ❌ PRIMA - Senza State Pattern
public void chiudi() {
    if (this.stato == StatoProposta.APERTA) {
        this.stato = StatoProposta.CHIUSA;
    } else if (this.stato == StatoProposta.CHIUSA) {
        // Già chiusa, non fare nulla
    } else if (this.stato == StatoProposta.RITIRATA) {
        // Già ritirata, non fare nulla
    }
}
```

### Implementazione

#### Interfaccia State

```java
// model/state/PropostaState.java
package model.state;

/**
 * Interfaccia per il pattern State delle proposte.
 * Definisce il contratto per tutti gli stati possibili.
 */
public interface PropostaState {

    /**
     * Tenta di chiudere la proposta.
     * @return true se l'operazione è riuscita
     */
    boolean chiudi(Proposta proposta);

    /**
     * Tenta di ritirare la proposta.
     * @return true se l'operazione è riuscita
     */
    boolean ritira(Proposta proposta);

    /** @return true se la proposta è aperta */
    boolean isAperta();

    /** @return true se la proposta è chiusa */
    boolean isChiusa();

    /** @return true se la proposta è ritirata */
    boolean isRitirata();

    /** @return nome descrittivo dello stato */
    String getNomeStato();
}
```

#### Stati Concreti

```java
// model/state/PropostaStateAperta.java
package model.state;

/**
 * Stato APERTA: permette transizioni a CHIUSA o RITIRATA.
 */
public class PropostaStateAperta implements PropostaState {

    @Override
    public boolean chiudi(Proposta proposta) {
        // ✅ Transizione APERTA → CHIUSA permessa
        proposta.setStatoInternal(new PropostaStateChiusa());
        proposta.setDataModifica(LocalDateTime.now());
        return true;
    }

    @Override
    public boolean ritira(Proposta proposta) {
        // ✅ Transizione APERTA → RITIRATA permessa
        proposta.setStatoInternal(new PropostaStateRitirata());
        proposta.setDataModifica(LocalDateTime.now());
        return true;
    }

    @Override
    public boolean isAperta() { return true; }

    @Override
    public boolean isChiusa() { return false; }

    @Override
    public boolean isRitirata() { return false; }

    @Override
    public String getNomeStato() { return "APERTA"; }
}

// model/state/PropostaStateChiusa.java
package model.state;

/**
 * Stato CHIUSA: stato finale, nessuna transizione permessa.
 */
public class PropostaStateChiusa implements PropostaState {

    @Override
    public boolean chiudi(Proposta proposta) {
        // ❌ Già chiusa, operazione non permessa
        return false;
    }

    @Override
    public boolean ritira(Proposta proposta) {
        // ❌ Non si può ritirare una proposta già chiusa
        return false;
    }

    @Override
    public boolean isAperta() { return false; }

    @Override
    public boolean isChiusa() { return true; }

    @Override
    public boolean isRitirata() { return false; }

    @Override
    public String getNomeStato() { return "CHIUSA"; }
}

// model/state/PropostaStateRitirata.java
package model.state;

/**
 * Stato RITIRATA: stato finale, nessuna transizione permessa.
 */
public class PropostaStateRitirata implements PropostaState {

    @Override
    public boolean chiudi(Proposta proposta) {
        // ❌ Non si può chiudere una proposta già ritirata
        return false;
    }

    @Override
    public boolean ritira(Proposta proposta) {
        // ❌ Già ritirata, operazione non permessa
        return false;
    }

    @Override
    public boolean isAperta() { return false; }

    @Override
    public boolean isChiusa() { return false; }

    @Override
    public boolean isRitirata() { return true; }

    @Override
    public String getNomeStato() { return "RITIRATA"; }
}
```

#### Context (Proposta)

```java
// model/Proposta.java - Usa State Pattern
public class Proposta implements Serializable {

    private PropostaState statoCorrente;  // ✅ State object
    private StatoProposta stato;           // Per retrocompatibilità/serializzazione

    public Proposta(Fruitore propostitore, Prestazione richiesta,
                    Prestazione offerta, StatoProposta stato) {
        this.propostitore = propostitore;
        this.richiesta = richiesta;
        this.offerta = offerta;
        this.stato = stato;
        this.statoCorrente = creaStatoDaEnum(stato);  // ✅ Inizializza State
    }

    /**
     * Chiude la proposta delegando allo stato corrente.
     * @return true se l'operazione è riuscita
     */
    public boolean chiudi() {
        boolean success = statoCorrente.chiudi(this);  // ✅ Delega allo State
        if (success) {
            this.stato = StatoProposta.CHIUSA;
            notifyObservers();
        }
        return success;
    }

    /**
     * Ritira la proposta delegando allo stato corrente.
     * @return true se l'operazione è riuscita
     */
    public boolean ritira() {
        boolean success = statoCorrente.ritira(this);  // ✅ Delega allo State
        if (success) {
            this.stato = StatoProposta.RITIRATA;
            notifyObservers();
        }
        return success;
    }

    /**
     * Metodo interno per permettere agli State di cambiare lo stato.
     */
    public void setStatoInternal(PropostaState nuovoStato) {
        this.statoCorrente = nuovoStato;
    }

    /**
     * Crea l'oggetto State corrispondente all'enum.
     */
    private PropostaState creaStatoDaEnum(StatoProposta stato) {
        return switch (stato) {
            case APERTA -> new PropostaStateAperta();
            case CHIUSA -> new PropostaStateChiusa();
            case RITIRATA -> new PropostaStateRitirata();
        };
    }

    // ✅ Metodi delegati allo State
    public boolean isAperta() { return statoCorrente.isAperta(); }
    public boolean isChiusa() { return statoCorrente.isChiusa(); }
    public boolean isRitirata() { return statoCorrente.isRitirata(); }
}
```

### Diagramma delle Classi

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           STATE PATTERN                                      │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐     │
│  │                           Proposta                                  │     │
│  │                          «context»                                  │     │
│  ├────────────────────────────────────────────────────────────────────┤     │
│  │ - statoCorrente: PropostaState                                     │     │
│  │ - propostitore: Fruitore                                           │     │
│  │ - richiesta: Prestazione                                           │     │
│  │ - offerta: Prestazione                                             │     │
│  ├────────────────────────────────────────────────────────────────────┤     │
│  │ + chiudi(): boolean        // delega a statoCorrente.chiudi(this) │     │
│  │ + ritira(): boolean        // delega a statoCorrente.ritira(this) │     │
│  │ + isAperta(): boolean      // delega a statoCorrente              │     │
│  │ + setStatoInternal(state): void                                   │     │
│  └──────────────────────────────────┬─────────────────────────────────┘     │
│                                     │                                        │
│                                     │ has-a                                  │
│                                     ▼                                        │
│  ┌────────────────────────────────────────────────────────────────────┐     │
│  │                      «interface»                                    │     │
│  │                     PropostaState                                   │     │
│  ├────────────────────────────────────────────────────────────────────┤     │
│  │ + chiudi(proposta: Proposta): boolean                              │     │
│  │ + ritira(proposta: Proposta): boolean                              │     │
│  │ + isAperta(): boolean                                              │     │
│  │ + isChiusa(): boolean                                              │     │
│  │ + isRitirata(): boolean                                            │     │
│  │ + getNomeStato(): String                                           │     │
│  └────────────────────────────────────┬───────────────────────────────┘     │
│                                       │                                      │
│           ┌───────────────────────────┼───────────────────────────┐         │
│           │                           │                           │         │
│           ▼                           ▼                           ▼         │
│  ┌─────────────────┐      ┌─────────────────┐      ┌─────────────────┐     │
│  │PropostaState    │      │PropostaState    │      │PropostaState    │     │
│  │    Aperta       │      │    Chiusa       │      │   Ritirata      │     │
│  ├─────────────────┤      ├─────────────────┤      ├─────────────────┤     │
│  │chiudi(): true   │      │chiudi(): false  │      │chiudi(): false  │     │
│  │ → CHIUSA        │      │ (già chiusa)    │      │ (già ritirata)  │     │
│  │                 │      │                 │      │                 │     │
│  │ritira(): true   │      │ritira(): false  │      │ritira(): false  │     │
│  │ → RITIRATA      │      │ (non permesso)  │      │ (già ritirata)  │     │
│  │                 │      │                 │      │                 │     │
│  │isAperta(): true │      │isAperta(): false│      │isAperta(): false│     │
│  │isChiusa(): false│      │isChiusa(): true │      │isChiusa(): false│     │
│  │isRitirata():fals│      │isRitirata():fals│      │isRitirata():true│     │
│  └─────────────────┘      └─────────────────┘      └─────────────────┘     │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Diagramma di Sequenza: Transizione di Stato

```
┌─────────────────────────────────────────────────────────────────────────────┐
│               SEQUENZA: Chiusura Proposta (da APERTA a CHIUSA)               │
├─────────────────────────────────────────────────────────────────────────────┤

    ┌──────────┐     ┌──────────┐     ┌──────────────┐     ┌──────────────┐
    │Controller│     │ Proposta │     │ StateAperta  │     │ StateChiusa  │
    └────┬─────┘     └────┬─────┘     └──────┬───────┘     └──────┬───────┘
         │                │                   │                    │
         │ proposta.chiudi()                  │                    │
         │───────────────>│                   │                    │
         │                │                   │                    │
         │                │ chiudi(this)      │                    │
         │                │──────────────────>│                    │
         │                │                   │                    │
         │                │                   │ setStatoInternal(  │
         │                │                   │   new StateChiusa())
         │                │                   │───────────────────>│
         │                │                   │                    │
         │                │                   │ setDataModifica(now)
         │                │                   │────────┐           │
         │                │                   │        │           │
         │                │                   │<───────┘           │
         │                │                   │                    │
         │                │      true         │                    │
         │                │<──────────────────│                    │
         │                │                   │                    │
         │                │ stato = CHIUSA    │                    │
         │                │────────┐          │                    │
         │                │        │          │                    │
         │                │<───────┘          │                    │
         │                │                   │                    │
         │                │ notifyObservers() │                    │
         │                │────────┐          │                    │
         │                │        │          │                    │
         │                │<───────┘          │                    │
         │                │                   │                    │
         │      true      │                   │                    │
         │<───────────────│                   │                    │
         │                │                   │                    │
    └────┴─────┘     └────┴─────┘     └──────┴───────┘     └──────┴───────┘

```

### Macchina a Stati

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    STATE MACHINE: Proposta                                   │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│                           ┌────────────────┐                                 │
│                           │                │                                 │
│        ┌──────────────────│    APERTA      │──────────────────┐             │
│        │                  │                │                  │             │
│        │                  └────────────────┘                  │             │
│        │                          │                           │             │
│        │                          │                           │             │
│    chiudi()                       │                       ritira()          │
│    [return true]                  │                     [return true]       │
│        │                          │                           │             │
│        ▼                          │                           ▼             │
│  ┌────────────────┐               │               ┌────────────────┐        │
│  │                │               │               │                │        │
│  │    CHIUSA      │               │               │   RITIRATA     │        │
│  │                │               │               │                │        │
│  │  «final»       │               │               │   «final»      │        │
│  └────────────────┘               │               └────────────────┘        │
│        │                          │                       │                 │
│        │ chiudi() → false         │         ritira() → false                │
│        │ ritira() → false         │         chiudi() → false                │
│        └──────────────────────────┴───────────────────────┘                 │
│                                                                              │
│  Legenda:                                                                    │
│  ─────── Transizione permessa                                               │
│  «final» Stato terminale (nessuna transizione in uscita)                    │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Vantaggi del Pattern State

| Aspetto                   | Senza State              | Con State Pattern                |
| ------------------------- | ------------------------ | -------------------------------- |
| **Logica condizionale**   | if-else in ogni metodo   | Polimorfismo                     |
| **Single Responsibility** | Proposta gestisce tutto  | Ogni State ha una responsabilità |
| **Open/Closed**           | Modifica per nuovo stato | Aggiunta di nuova classe State   |
| **Testabilità**           | Test complessi           | Test isolati per ogni State      |
| **Leggibilità**           | Logica sparsa            | Comportamento localizzato        |

---

## 3️⃣ Facade Pattern: `Controller`

### 🎯 Definizione del Pattern

> **Facade**: Fornisce un'**interfaccia unificata** a un insieme di interfacce in un sottosistema. Definisce un'interfaccia di alto livello che rende il sottosistema più facile da usare.

### Applicazione in ActivityNexus

`Controller` funge da **Facade** per l'intero sistema, nascondendo la complessità di:

- `Database` (persistenza e gestione dati)
- `IView` (interfaccia utente)
- Classi del dominio (`Proposta`, `Gerarchia`, `Comprensorio`, etc.)
- Servizi (`CredentialService`)

### Diagramma Facade

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           FACADE PATTERN                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│                              ┌─────────────┐                                 │
│                              │    Main     │                                 │
│                              │   (Client)  │                                 │
│                              └──────┬──────┘                                 │
│                                     │                                        │
│                                     │ usa                                    │
│                                     ▼                                        │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │                         «facade»                                      │   │
│  │                        Controller                                     │   │
│  ├──────────────────────────────────────────────────────────────────────┤   │
│  │  Interfaccia semplificata per il sottosistema:                       │   │
│  │                                                                       │   │
│  │  + start()                    // Avvia l'applicazione                │   │
│  │  + loginUtente()              // Gestisce autenticazione             │   │
│  │  + registraFruitore()         // Gestisce registrazione              │   │
│  │  + createComprensorio()       // Crea comprensorio                   │   │
│  │  + creaGerarchia()            // Crea gerarchia                      │   │
│  │  + creaProposta()             // Crea proposta                       │   │
│  │  + valutaProposte()           // Valuta proposte                     │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                     │                                        │
│                   ┌─────────────────┼─────────────────┐                     │
│                   │                 │                 │                     │
│                   ▼                 ▼                 ▼                     │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │                      SOTTOSISTEMA NASCOSTO                            │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  │   │
│  │  │   IView     │  │  Database   │  │  Proposta   │  │ Credential  │  │   │
│  │  │             │  │             │  │             │  │  Service    │  │   │
│  │  │ getInput()  │  │ login()     │  │ chiudi()    │  │ validate()  │  │   │
│  │  │ showMsg()   │  │ save()      │  │ ritira()    │  │             │  │   │
│  │  │ showError() │  │ find()      │  │ getStato()  │  │             │  │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘  │   │
│  │                                                                       │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  │   │
│  │  │ Gerarchia   │  │Comprensorio │  │ UserFactory │  │  Utente     │  │   │
│  │  │             │  │             │  │             │  │             │  │   │
│  │  │ getFoglie() │  │ getComuni() │  │ create()    │  │ isConfig()  │  │   │
│  │  │ addCateg()  │  │ addComune() │  │ getInstance │  │ verificaPwd │  │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘  │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
│  ✅ Il Client (Main) interagisce SOLO con Controller                        │
│  ✅ Controller nasconde la complessità del sottosistema                     │
│  ✅ Modifiche al sottosistema non impattano Main                            │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Esempio: `loginUtente()` come Facade Method

```java
// Controller.java - Facade method
public Utente loginUtente() {
    // ═══════════════════════════════════════════════════════════════
    // Il Facade nasconde la complessità di:
    // 1. Validazione input (CredentialService)
    // 2. Ricerca utente (Database)
    // 3. Verifica password (Utente)
    // 4. Feedback utente (IView)
    // ═══════════════════════════════════════════════════════════════

    // 1. Interazione con IView
    String username = view.getInput(ControllerConstants.PROMPT_USERNAME);
    String password = view.getPasswordInput(ControllerConstants.PROMPT_PASSWORD);

    // 2. Validazione con CredentialService
    ValidationResult validation = credentialService.validateCredentials(username, password);
    if (!validation.isValid()) {
        view.showErrorMessage(validation.getErrorMessage());
        return null;
    }

    // 3. Autenticazione con Database
    Utente utente = database.loginUtente(username, password);

    // 4. Feedback con IView
    if (utente != null) {
        view.showSuccessMessage("Benvenuto " + utente.getUsername());
    } else {
        view.showErrorMessage("Credenziali non valide");
    }

    return utente;
}
```

---

## 📋 Checklist Conformità Pattern GoF

| Requisito                      | Pattern        | Stato | Evidenza                                |
| ------------------------------ | -------------- | ----- | --------------------------------------- |
| ✅ Factory per istanziazione   | Factory Method | ✅    | `UserFactory.createUser()`              |
| ✅ Singleton per Factory       | Singleton      | ✅    | `UserFactory.getInstance()`             |
| ✅ Facade per punto d'ingresso | Facade         | ✅    | `Controller` come interfaccia unificata |
| ✅ Diagrammi delle classi      | -              | ✅    | 3 diagrammi ASCII completi              |
| ✅ Diagrammi di sequenza       | -              | ✅    | 2 diagrammi sequenza dettagliati        |

---

## 🎯 Conclusioni

### Pattern GoF Applicati

| Pattern            | Classe          | Categoria       | Beneficio Principale                |
| ------------------ | --------------- | --------------- | ----------------------------------- |
| **Factory Method** | `UserFactory`   | Creazionale     | Centralizzazione creazione oggetti  |
| **Singleton**      | `UserFactory`   | Creazionale     | Una sola istanza, accesso globale   |
| **State**          | `PropostaState` | Comportamentale | Eliminazione if-else, polimorfismo  |
| **Facade**         | `Controller`    | Strutturale     | Interfaccia semplificata al sistema |

### Metriche di Miglioramento

```
┌─────────────────────────────────────────────────────────────────────────────┐
│  PATTERN GoF METRICS - ActivityNexus                                         │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  Factory Method + Singleton:                                                 │
│  ├── Validazione centralizzata: 100%                                        │
│  ├── Costruttori esposti: 0 (tutti via Factory)                             │
│  └── Istanze UserFactory: 1 (Singleton)                                     │
│                                                                              │
│  State Pattern:                                                              │
│  ├── if-else eliminati: 6 (in chiudi() e ritira())                         │
│  ├── Classi State create: 3 (Aperta, Chiusa, Ritirata)                     │
│  └── Transizioni gestite: 4 (APERTA→CHIUSA, APERTA→RITIRATA, 2 noop)       │
│                                                                              │
│  Facade Pattern:                                                             │
│  ├── Entry point unico: Controller                                          │
│  ├── Sottosistemi nascosti: 6 (Database, IView, Proposta, etc.)            │
│  └── Complessità per client: Bassa (solo metodi Controller)                │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 📚 Riferimenti

- Gamma, E., Helm, R., Johnson, R., Vlissides, J. (1994). _Design Patterns: Elements of Reusable Object-Oriented Software_
- Freeman, E., Robson, E. (2004). _Head First Design Patterns_
- Larman, C. (2004). _Applying UML and Patterns_, 3rd Edition - Cap. 26 "GoF Design Patterns"
