# 7. Refactoring - ActivityNexus

## Panoramica

È stato applicato il refactoring **Extract Class** per separare la logica di validazione delle credenziali dal `Controller`, creando una nuova classe `CredentialService`.

---

## Pattern di Refactoring Applicato: Extract Class

### Definizione

**Extract Class** è un pattern di refactoring che consiste nell'estrarre un sottoinsieme di responsabilità da una classe esistente in una nuova classe dedicata, quando la classe originale ha troppe responsabilità.

```
┌─────────────────────────────────────────────────────────────────────┐
│                      EXTRACT CLASS                                   │
│                                                                      │
│   PRIMA                              DOPO                           │
│   ┌────────────────────┐             ┌────────────────────┐         │
│   │    Controller      │             │    Controller      │         │
│   ├────────────────────┤             ├────────────────────┤         │
│   │ - database         │             │ - database         │         │
│   │ - view             │             │ - view             │         │
│   ├────────────────────┤             │ - credentialService│──┐      │
│   │ + loginUtente()    │             ├────────────────────┤  │      │
│   │ + registraUtente() │             │ + loginUtente()    │  │      │
│   │ + validateEmail()  │ ──────▶     │ + registraUtente() │  │      │
│   │ + validateUsername()│            └────────────────────┘  │      │
│   │ + validatePassword()│                                    │      │
│   │ + generateUsername()│            ┌────────────────────┐  │      │
│   │ + generatePassword()│            │CredentialService   │◀─┘      │
│   └────────────────────┘             ├────────────────────┤         │
│                                      │ + validateEmail()  │         │
│   Controller troppo grande           │ + validateUsername()│        │
│   con responsabilità miste           │ + validatePassword()│        │
│                                      │ + generateUsername()│        │
│                                      │ + generatePassword()│        │
│                                      └────────────────────┘         │
│                                                                      │
│                                      Responsabilità separate        │
└─────────────────────────────────────────────────────────────────────┘
```

---

## Motivazione del Refactoring

### Perché è stato scelto questo refactoring?

1. **Controller troppo grande**: La classe `Controller` conteneva oltre 3000 righe di codice con responsabilità eterogenee

2. **Violazione SRP**: La validazione delle credenziali non è una responsabilità del Controller (che dovrebbe solo coordinare)

3. **Basso riuso**: La logica di validazione era duplicata o difficile da riutilizzare in altri contesti

4. **Difficoltà di testing**: Testare la validazione richiedeva istanziare l'intero Controller

```
┌─────────────────────────────────────────────────────────────────────┐
│                    CODE SMELL IDENTIFICATI                           │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │ 🔴 LARGE CLASS                                               │   │
│  │    Controller.java: 3374 righe                               │   │
│  │    Troppe responsabilità in una sola classe                 │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │ 🔴 FEATURE ENVY                                              │   │
│  │    Controller chiamava continuamente UserUtil               │   │
│  │    per validazione credenziali                              │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │ 🔴 DIVERGENT CHANGE                                          │   │
│  │    Modifiche alla validazione richiedevano                  │   │
│  │    modifiche al Controller                                   │   │
│  └─────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────┘
```

---

## Passi del Refactoring

### Step 1: Identificare le Responsabilità da Estrarre

Analisi delle responsabilità nel `Controller` relative alle credenziali:

```
┌─────────────────────────────────────────────────────────────────────┐
│              RESPONSABILITÀ IDENTIFICATE                             │
│                                                                      │
│  Controller (PRIMA del refactoring)                                 │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  ✓ Gestione flusso applicazione      (CORE - mantiene)      │   │
│  │  ✓ Coordinamento Model-View          (CORE - mantiene)      │   │
│  │  ✓ Persistenza dati                  (CORE - mantiene)      │   │
│  │  ─────────────────────────────────────────────────────────  │   │
│  │  ✗ Validazione username              (ESTRARRE)             │   │
│  │  ✗ Validazione password              (ESTRARRE)             │   │
│  │  ✗ Validazione email                 (ESTRARRE)             │   │
│  │  ✗ Generazione username              (ESTRARRE)             │   │
│  │  ✗ Generazione password              (ESTRARRE)             │   │
│  └─────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────┘
```

---

### Step 2: Creare la Nuova Classe

Creazione della classe `CredentialService` nel package `service`:

```java
package service;

import util.UserUtil;

/**
 * Servizio centralizzato per la validazione e gestione
 * delle credenziali utente.
 *
 * PATTERN: Extract Class + Singleton
 */
public class CredentialService {

    // Singleton instance
    private static final CredentialService INSTANCE = new CredentialService();

    public static CredentialService getInstance() {
        return INSTANCE;
    }

    private CredentialService() {
        // Costruttore privato per Singleton
    }
}
```

---

### Step 3: Spostare i Metodi nella Nuova Classe (Move Method)

#### 3.1 Validazione Username

**PRIMA** (in Controller):

```java
// Controller.java - Metodo inline
private boolean validateUsername(String username) {
    return UserUtil.checkUsername(username);
}
```

**DOPO** (in CredentialService):

```java
// CredentialService.java
public boolean validateUsername(String username) {
    return UserUtil.checkUsername(username);
}
```

#### 3.2 Validazione Password

**PRIMA** (in Controller):

```java
// Controller.java - Metodo inline
private boolean validatePassword(String password) {
    return UserUtil.checkPassword(password);
}
```

**DOPO** (in CredentialService):

```java
// CredentialService.java
public boolean validatePassword(String password) {
    return UserUtil.checkPassword(password);
}
```

#### 3.3 Validazione Email

**PRIMA** (in Controller):

```java
// Controller.java - Logica duplicata in più punti
private boolean validaEmail(String email) {
    if (email == null || email.isBlank()) {
        return false;
    }
    int chiocciola = email.indexOf('@');
    if (chiocciola < 1 || chiocciola == email.length() - 1) {
        return false;
    }
    String dominio = email.substring(chiocciola + 1);
    return dominio.contains(".") && !dominio.startsWith(".");
}
```

**DOPO** (in CredentialService):

```java
// CredentialService.java
public boolean validateEmail(String email) {
    if (email == null || email.isBlank()) {
        return false;
    }
    int chiocciola = email.indexOf('@');
    if (chiocciola < 1 || chiocciola == email.length() - 1) {
        return false;
    }
    String dominio = email.substring(chiocciola + 1);
    return dominio.contains(".") && !dominio.startsWith(".");
}
```

#### 3.4 Generazione Credenziali

**PRIMA** (in Controller):

```java
// Controller.java - Chiamate dirette a UserUtil
String username = UserUtil.generateRandomUsername();
String password = UserUtil.generateRandomPassword();
```

**DOPO** (in CredentialService):

```java
// CredentialService.java
public String generateUsername() {
    return UserUtil.generateRandomUsername();
}

public String generatePassword() {
    return UserUtil.generateRandomPassword();
}
```

---

### Step 4: Aggiornare il Controller per Usare il Nuovo Servizio

**PRIMA**:

```java
// Controller.java
private void registraConfiguratore() {
    // Chiamata diretta a UserUtil
    String usernameDefault = UserUtil.generateRandomUsername();
    String passwordDefault = UserUtil.generateRandomPassword();

    // Validazione inline
    if (!UserUtil.checkUsername(username)) {
        view.showError("Username non valido");
    }
    // ...
}
```

**DOPO**:

```java
// Controller.java
private void registraConfiguratore() {
    // PATTERN: Low Coupling - Usa CredentialService
    CredentialService credentialService = CredentialService.getInstance();

    String usernameDefault = credentialService.generateUsername();
    String passwordDefault = credentialService.generatePassword();

    if (!credentialService.validateUsername(username)) {
        view.showError("Username non valido");
    }
    // ...
}
```

---

### Step 5: Introdurre Parameter Object (Refactoring Aggiuntivo)

Per migliorare ulteriormente, è stato creato `ValidationResult`:

```java
package util;

/**
 * REFACTORING: Introduce Parameter Object
 *
 * Incapsula il risultato di una validazione combinando:
 * - Un flag booleano (successo/fallimento)
 * - Un messaggio di errore opzionale
 */
public class ValidationResult {
    private final boolean valid;
    private final String errorMessage;

    private ValidationResult(boolean valid, String errorMessage) {
        this.valid = valid;
        this.errorMessage = errorMessage;
    }

    // Factory methods
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

**Uso nel Controller**:

```java
// Prima: due variabili separate
boolean valido = validateNome(nome);
String errore = getErrorMessage();

// Dopo: oggetto coeso
ValidationResult validazione = validaNomeComprensorioInput(nome);
if (!validazione.isValid()) {
    view.showError(validazione.getErrorMessage());
}
```

---

## Diagramma delle Dipendenze

```
┌─────────────────────────────────────────────────────────────────────┐
│                    ARCHITETTURA DOPO REFACTORING                     │
│                                                                      │
│                    ┌────────────────────┐                           │
│                    │    Controller      │                           │
│                    │    (3374 → ~3200   │                           │
│                    │     righe)         │                           │
│                    └─────────┬──────────┘                           │
│                              │                                       │
│              ┌───────────────┼───────────────┐                      │
│              │               │               │                       │
│              ▼               ▼               ▼                       │
│   ┌──────────────┐  ┌──────────────┐  ┌──────────────┐             │
│   │   Database   │  │    IView     │  │ Credential   │             │
│   │   (Model)    │  │   (View)     │  │  Service     │             │
│   └──────────────┘  └──────────────┘  └──────┬───────┘             │
│                                              │                       │
│                                              ▼                       │
│                                       ┌──────────────┐              │
│                                       │   UserUtil   │              │
│                                       │  (utility)   │              │
│                                       └──────────────┘              │
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐  │
│   │ VANTAGGI:                                                    │  │
│   │ • Controller non dipende più direttamente da UserUtil       │  │
│   │ • CredentialService può essere testato in isolamento        │  │
│   │ • Logica di validazione centralizzata e riusabile           │  │
│   │ • Singola responsabilità per ogni classe                    │  │
│   └─────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────┘
```

---

## Struttura Finale della Classe CredentialService

```java
package service;

import util.UserUtil;

public class CredentialService {

    // ══════════════════════════════════════════════════════════════════
    // SINGLETON
    // ══════════════════════════════════════════════════════════════════

    private static final CredentialService INSTANCE = new CredentialService();

    public static CredentialService getInstance() {
        return INSTANCE;
    }

    private CredentialService() { }

    // ══════════════════════════════════════════════════════════════════
    // COSTANTI
    // ══════════════════════════════════════════════════════════════════

    public static final int MAX_USERNAME_LENGTH = UserUtil.USERNAME_LENGTH;
    public static final int MIN_PASSWORD_LENGTH = UserUtil.PASSWORD_LENGTH;

    // ══════════════════════════════════════════════════════════════════
    // METODI DI VALIDAZIONE
    // ══════════════════════════════════════════════════════════════════

    public boolean validateUsername(String username) {
        return UserUtil.checkUsername(username);
    }

    public boolean validatePassword(String password) {
        return UserUtil.checkPassword(password);
    }

    public boolean validateEmail(String email) {
        if (email == null || email.isBlank()) return false;
        int chiocciola = email.indexOf('@');
        if (chiocciola < 1 || chiocciola == email.length() - 1) return false;
        String dominio = email.substring(chiocciola + 1);
        return dominio.contains(".") && !dominio.startsWith(".");
    }

    public CredentialValidationResult validateCredentials(
            String username, String password) {
        return new CredentialValidationResult(
            validateUsername(username),
            validatePassword(password)
        );
    }

    // ══════════════════════════════════════════════════════════════════
    // METODI DI GENERAZIONE
    // ══════════════════════════════════════════════════════════════════

    public String generateUsername() {
        return UserUtil.generateRandomUsername();
    }

    public String generatePassword() {
        return UserUtil.generateRandomPassword();
    }
}
```

---

## Riepilogo dei Pattern Applicati

| Pattern                        | Descrizione                                  | Applicazione                                     |
| ------------------------------ | -------------------------------------------- | ------------------------------------------------ |
| **Extract Class**              | Crea nuova classe da responsabilità estratte | `CredentialService` estratto da `Controller`     |
| **Move Method**                | Sposta metodi nella classe appropriata       | 5 metodi spostati in `CredentialService`         |
| **Introduce Parameter Object** | Raggruppa parametri correlati                | `ValidationResult`, `CredentialValidationResult` |
| **Singleton**                  | Istanza unica del servizio                   | `CredentialService.getInstance()`                |
| **Facade**                     | Interfaccia semplificata                     | `CredentialService` nasconde `UserUtil`          |

---

## Benefici Ottenuti

```
┌─────────────────────────────────────────────────────────────────────┐
│                    BENEFICI DEL REFACTORING                          │
│                                                                      │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐     │
│  │   PRIMA         │  │     DOPO        │  │   MIGLIORAMENTO │     │
│  ├─────────────────┤  ├─────────────────┤  ├─────────────────┤     │
│  │ Controller:     │  │ Controller:     │  │                 │     │
│  │ 5 responsabilità│  │ 3 responsabilità│  │ -40% responsa-  │     │
│  │ credenziali     │  │ (core only)     │  │ bilità extra    │     │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘     │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │ ✅ Single Responsibility Principle rispettato               │   │
│  │ ✅ Low Coupling tra Controller e UserUtil                   │   │
│  │ ✅ High Cohesion in CredentialService                       │   │
│  │ ✅ Testabilità migliorata (test isolati)                    │   │
│  │ ✅ Riusabilità della validazione in altri contesti          │   │
│  └─────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────┘
```

---

## Conclusioni

Il refactoring **Extract Class** ha permesso di:

1. **Separare le responsabilità** - La validazione delle credenziali è ora una responsabilità esclusiva di `CredentialService`

2. **Ridurre l'accoppiamento** - Il Controller non dipende più direttamente da `UserUtil`

3. **Aumentare la coesione** - `CredentialService` contiene solo metodi correlati alla gestione delle credenziali

4. **Migliorare la testabilità** - Il servizio può essere testato e mockato indipendentemente

5. **Facilitare l'estensione** - Nuove validazioni possono essere aggiunte senza modificare il Controller
