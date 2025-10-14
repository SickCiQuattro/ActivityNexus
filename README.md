# ActivityNexus - Sistema di Gestione Gerarchie v4.0 Final

[![Java](https://img.shields.io/badge/Java-11%2B-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-Custom-blue.svg)](LICENSE)
[![Version](https://img.shields.io/badge/Version-4.0%20Final-green.svg)](https://github.com/SickCiQuattro/ProjectIngSW)
[![Status](https://img.shields.io/badge/Status-Stable-success.svg)](https://github.com/SickCiQuattro/ProjectIngSW)

## 📋 Descrizione

**ActivityNexus** è un'applicazione Java avanzata per la gestione di gerarchie di categorie, comprensori geografici e sistemi di proposte e scambi. Il progetto implementa un sistema completo di conversione tra gerarchie diverse, gestione di proposte configurabili e insiemi chiusi di scambi.

### Caratteristiche Principali

✅ **Gestione Gerarchie**: Sistema completo per creare e gestire gerarchie di categorie con campi caratteristici e domini di valori  
✅ **Conversione Multi-Gerarchia**: Fattori di conversione intra e inter-gerarchia con calcoli automatici  
✅ **Sistema di Proposte**: Creazione, approvazione e gestione di proposte configurabili  
✅ **Insiemi Chiusi**: Gestione avanzata di insiemi chiusi di scambi con validazione automatica  
✅ **Multi-Utente**: Supporto per Configuratori (amministratori) e Fruitori (utenti standard)  
✅ **Persistenza Dati**: Serializzazione automatica di tutti i dati applicativi  
✅ **JAR Standalone**: Distribuzione come singolo JAR eseguibile con tutte le dipendenze incluse

## 🚀 Quick Start

### Prerequisiti

-   **Java JRE 11 o superiore** ([Download](https://adoptium.net/))

### Esecuzione Rapida

```bash
# Scarica la release
cd Versione1/dist

# Esegui l'applicazione
java -jar ActivityNexus-v4.0-Final.jar

# Oppure usa lo script (macOS/Linux)
./run.sh

# Oppure usa lo script (Windows)
run.bat
```

## 📦 Download

### Release Corrente: v4.0 Final

**Download diretto:**

-   [ActivityNexusV4.jar](Versione1/dist/ActivityNexusV4.jar) (96 KB)

## 🏗️ Struttura del Progetto

```
ProjectIngSW/
├── README.md                          # Questo file
├── Versione1/                         # Versione finale dell'applicazione
│   ├── src/                          # Codice sorgente
│   │   ├── Main.java                # Entry point
│   │   ├── controller/              # Logica di controllo MVC
│   │   │   └── Controller.java      # Controller principale (~2100 linee)
│   │   ├── model/                   # Modello dati
│   │   │   ├── Database.java        # Gestione database
│   │   │   ├── Gerarchia.java       # Struttura gerarchica
│   │   │   ├── Categoria.java       # Categorie
│   │   │   ├── Campo.java           # Campi caratteristici
│   │   │   ├── Utente.java          # Utenti (base)
│   │   │   ├── Configuratore.java   # Utente amministratore
│   │   │   ├── Fruitore.java        # Utente standard
│   │   │   ├── Comprensorio.java    # Territori geografici
│   │   │   ├── FattoreConversione.java
│   │   │   └── ConversioneGerarchie.java
│   │   ├── view/                    # Interfaccia utente
│   │   │   └── ConsoleView.java     # Vista console
│   │   ├── util/                    # Utility
│   │   │   └── UserUtil.java        # Utilità utente
│   │   └── constants/               # Costanti
│   │       └── ControllerConstants.java
│   ├── ActivityNexusV4.jar
│   └── data/                        # Dati persistenti (creata runtime)
│       ├── utenti.ser
│       ├── comprensori.ser
│       ├── gerarchie.ser
│       ├── conversione.ser
│       ├── proposte.ser
│       └── insiemechiuso.ser
└── tree/                            # Libreria Scalified Tree (dipendenza)
    └── tree/build/libs/tree-0.2.5.jar
```

## 🎯 Funzionalità Complete

### 1. Sistema Utenti

-   **Configuratore** (Amministratore)

    -   Creazione comprensori geografici
    -   Creazione gerarchie di categorie
    -   Definizione fattori di conversione
    -   Approvazione/Rifiuto proposte
    -   Gestione insiemi chiusi

-   **Fruitore** (Utente Standard)
    -   Visualizzazione gerarchie
    -   Creazione proposte
    -   Partecipazione a insiemi chiusi
    -   Tracking delle proprie configurazioni

### 2. Gestione Comprensori

```
Comprensorio
├── Nome
└── Comuni[]
    ├── Comune 1
    ├── Comune 2
    └── ...
```

### 3. Gestione Gerarchie

```
Gerarchia
├── Categoria Radice
│   ├── Campo Caratteristico
│   │   └── Dominio Valori[]
│   └── Sottocategorie[]
│       ├── Categoria Figlia 1
│       │   ├── Campo Caratteristico
│       │   └── Sottocategorie[]
│       └── Categoria Figlia 2 (Foglia)
└── Fattori Conversione
    ├── Intra-gerarchia
    └── Inter-gerarchia
```

### 4. Sistema di Conversione

-   **Conversione Intra-Gerarchia**: Tra categorie della stessa gerarchia
-   **Conversione Inter-Gerarchia**: Tra categorie di gerarchie diverse
-   **Calcolo Automatico**: Fattori combinati calcolati automaticamente
-   **Visualizzazione**: Percorsi gerarchici e fattori applicati

### 5. Sistema Proposte (v4.0)

-   Creazione proposte da Fruitori
-   Stati: In Attesa, Approvata, Rifiutata
-   Approvazione da Configuratori
-   Tracking completo

### 6. Insiemi Chiusi (v4.0)

-   Gestione set di scambi chiusi
-   Validazione automatica
-   Calcoli di equilibrio
-   Ottimizzazione proposte

## 💻 Sviluppo

### Compilazione da Sorgente

```bash
# Clona il repository
git clone https://github.com/SickCiQuattro/ProjectIngSW.git
cd ProjectIngSW

# Compila la libreria tree (se necessario)
cd tree
./gradlew build -x test
cd ..

# Compila il progetto
cd Versione1
javac -cp ../tree/tree/build/libs/tree-0.2.5.jar \
      -d out/production \
      -sourcepath src \
      $(find src -name "*.java" -not -name "GerarchiaTest.java")

# Crea il JAR
mkdir -p temp_build
cd temp_build
jar xf ../../tree/tree/build/libs/tree-0.2.5.jar
cp -r ../out/production/* .

# Crea manifest
cat > META-INF/MANIFEST.MF << EOF
Manifest-Version: 1.0
Main-Class: Main
Implementation-Version: 4.0
EOF

# Genera JAR finale
jar cfm ../ActivityNexus-v4.0-Final.jar META-INF/MANIFEST.MF .
cd ..
rm -rf temp_build
```

### Architettura

-   **Pattern**: MVC (Model-View-Controller)
-   **Persistenza**: Serializzazione Java
-   **Struttura Dati**: Alberi n-ari (Scalified Tree Library)
-   **Interfaccia**: Console interattiva con menu strutturati

### Tecnologie Utilizzate

| Componente      | Tecnologia         | Versione |
| --------------- | ------------------ | -------- |
| Linguaggio      | Java               | 11+      |
| Libreria Alberi | Scalified Tree     | 0.2.5    |
| Build           | Gradle             | 8.x      |
| Persistenza     | Java Serialization | Built-in |

## 📊 Statistiche Progetto

-   **Linee di Codice**: ~5,000+
-   **Classi**: 17
-   **Metodi**: 100+
-   **Branch**: main, fix-v1.0, fix-v2.0, fix-v3.0
-   **Versioni Release**: 4 (v1.0, v2.0, v3.0, v4.0 Final)

## 🔧 Configurazione

### Primo Avvio

Al primo avvio, l'applicazione:

1. Crea automaticamente la directory `data/`
2. Inizializza il database vuoto
3. Richiede la registrazione del primo utente

### File di Configurazione

I dati sono salvati in formato serializzato:

-   `data/utenti.ser` - Utenti e credenziali
-   `data/comprensori.ser` - Comprensori geografici
-   `data/gerarchie.ser` - Gerarchie complete
-   `data/conversione.ser` - Fattori di conversione
-   `data/proposte.ser` - Proposte configurazione
-   `data/insiemechiuso.ser` - Insiemi chiusi

### Backup e Ripristino

```bash
# Backup
cp -r Versione1/data Versione1/data_backup_$(date +%Y%m%d)

# Ripristino
rm -rf Versione1/data
cp -r Versione1/data_backup_YYYYMMDD Versione1/data
```

## 🐛 Risoluzione Problemi

### Errore: "no main manifest attribute"

```bash
# Soluzione: Usa sempre -jar
java -jar ActivityNexus-v4.0-Final.jar
# NON usare: java -cp ActivityNexus-v4.0-Final.jar Main
```

### Errore: Java version

```bash
# Verifica versione Java
java -version

# Deve mostrare versione 11 o superiore
# Se inferiore, aggiorna Java da https://adoptium.net/
```

### Dati corrotti

```bash
# Reset completo (attenzione: cancella tutti i dati)
rm -rf Versione1/data
# L'applicazione ricreerà la struttura al prossimo avvio
```

## 📖 Documentazione

-   [README Distribuzione](Versione1/dist/README.txt) - Manuale utente completo
-   [Javadoc](Versione1/src/) - Documentazione inline nel codice
-   [Changelog](#changelog) - Storia delle versioni

## 🔄 Changelog

### v4.0 Final (14 Ottobre 2025) - CURRENT

-   ✅ Sistema completo di proposte e configurazioni
-   ✅ Gestione avanzata insiemi chiusi di scambi
-   ✅ Miglioramenti alla persistenza dei dati
-   ✅ Ottimizzazioni delle performance
-   ✅ Interfaccia utente migliorata
-   ✅ Gestione errori più robusta
-   ✅ Documentazione completa

### v3.0 (13 Ottobre 2025)

-   ➕ Implementazione base proposte
-   ➕ Struttura insiemi chiusi
-   🔧 Refactoring Controller
-   🐛 Fix bug conversioni

### v2.0 (13 Ottobre 2025)

-   ➕ Sistema di conversione inter-gerarchia
-   ➕ Visualizzazione fattori di conversione
-   🔧 Miglioramenti loadData
-   📝 Documentazione estesa

### v1.0 (12 Ottobre 2025)

-   🎉 Release iniziale
-   ➕ Gestione utenti (Configuratore/Fruitore)
-   ➕ Gestione comprensori
-   ➕ Gestione gerarchie
-   ➕ Conversione intra-gerarchia

## 👥 Team

-   **Repository**: [SickCiQuattro/ProjectIngSW](https://github.com/SickCiQuattro/ProjectIngSW)
-   **Progetto**: ActivityNexus - Sistema di Gestione Gerarchie
-   **Anno**: 2025

## 📄 Licenza

Copyright © 2025 ProjectIngSW Team. Tutti i diritti riservati.

### Librerie di Terze Parti

-   **Scalified Tree Library** (v0.2.5)
    -   Licenza: Apache License 2.0
    -   Repository: [scalified/tree](https://github.com/scalified/tree)

## 🤝 Contributi

Questo è un progetto didattico. Per bug report o suggerimenti:

1. Apri una Issue su GitHub
2. Descrivi il problema o la feature richiesta
3. Attendi feedback dal team

## 📞 Contatti e Supporto

-   **Issues**: [GitHub Issues](https://github.com/SickCiQuattro/ProjectIngSW/issues)
-   **Repository**: [ProjectIngSW](https://github.com/SickCiQuattro/ProjectIngSW)

## ⭐ Riconoscimenti

-   Grazie a [Scalified](https://github.com/scalified) per la libreria Tree
-   Ispirato dai principi di progettazione MVC
-   Sviluppato come progetto di Ingegneria del Software

---

**Versione**: 4.0 Final  
**Data**: 14 Ottobre 2025  
**Status**: ✅ Stable Release  
**Build**: Final
