# ActivityNexus

Applicazione Java da console per gestire gerarchie di categorie, comprensori geografici e scambi di prestazioni tra utenti. Progetto del corso di Ingegneria del Software, sviluppato in due parti: funzionalità (Versione 1–4) e rifattorizzazione con pattern di progettazione, principi SOLID e test (Parte B).

![Java](https://img.shields.io/badge/Java-11%2B-orange)
![JUnit](https://img.shields.io/badge/JUnit-5.10-green)
![Architettura](https://img.shields.io/badge/architettura-MVC-blue)

## Funzionalità

- **Due ruoli**: il *configuratore* definisce comprensori, gerarchie di categorie e fattori di conversione; il *fruitore* crea e gestisce proposte di scambio.
- **Gerarchie** ad albero (libreria [Scalified Tree](https://github.com/scalified/tree)) con campi caratteristici e domini di valori.
- **Conversione** delle prestazioni tra categorie, dentro e fra gerarchie diverse.
- **Proposte** con ciclo di vita aperta / chiusa / ritirata e **insiemi chiusi** di scambi.
- **Persistenza** su file `.ser` (serializzazione Java) nella cartella `data/`.

## Avvio rapido

Serve solo un JRE 11+.

```bash
cd Versione1
java -jar ActivityNexus_Parte_B.jar
```

Il JAR include la libreria Tree. La cartella [Versione1/data](Versione1/data) contiene dati d'esempio già caricati; credenziali e scenari di prova sono in [docs/DatiEsempio.md](docs/DatiEsempio.md).

> [!NOTE]
> L'app legge e scrive `data/` relativa alla directory di lavoro: lanciala da `Versione1/`.

## Architettura e pattern (Parte B)

| Area | Applicazione nel codice |
| --- | --- |
| MVC | `Controller` dipende da interfacce `IView`, `IInputProvider`, `IOutputProvider`, non da `ConsoleView` |
| Factory Method + Singleton | `model/factory/UserFactory` |
| State | `model/state/PropostaState*` (aperta, chiusa, ritirata) |
| Observer | `model/observers/PropostaObserver`, `EmailNotifier` |
| GRASP | Controller, Information Expert, Low Coupling / High Cohesion |
| SOLID | SRP, OCP, ISP, DIP |
| Refactoring | Extract Class (es. `service/CredentialService`) |

```
Versione1/src
├── Main.java
├── controller/   Controller (facade verso il modello)
├── model/        dominio, factory/, state/, observers/
├── view/         ConsoleView e interfacce I*
├── service/      CredentialService
├── util/ constants/
└── test/java/    ControllerTest + MockView (test double)
```

## Sviluppo

Requisiti: JDK 11+ e la libreria Tree compilata (`tree/` è una copia vendorizzata di Scalified Tree).

```bash
(cd tree && ./gradlew build -x test)   # produce tree/tree/build/libs
cd Versione1
./build.sh                             # compila, esegue i test JUnit 5, crea ActivityNexus_Parte_B.jar
```

I report dei test finiscono in `Versione1/test-reports/`. Le dipendenze JUnit sono in [Versione1/lib](Versione1/lib).

## Documentazione

Analisi di progetto scritte per la presentazione, in [docs/](docs):

- [Separazione modello-vista](docs/SEPARAZIONE_MODELLO_VISTA.md)
- [Pattern GRASP](docs/PATTERN_GRASP.md) e [pattern GoF](docs/PATTERN_GOF.md)
- [Principi SOLID](docs/PRINCIPI_SOLID.md)
- [Refactoring](docs/REFACTORING.md)
- [Testing](docs/TESTING.md)
- [Note conclusive](docs/NOTE_CONCLUSIVE.md)

## Crediti

Libreria [Scalified Tree](https://github.com/scalified/tree) (Apache 2.0), inclusa in `tree/`.
