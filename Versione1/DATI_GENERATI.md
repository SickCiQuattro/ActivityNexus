# Dati Generati per ActivityNexus

Questo documento descrive in dettaglio tutti i dati serializzati generati per il sistema ActivityNexus.

## 📁 File Serializzati

Tutti i file sono salvati in: `data/`

| File                | Dimensione | Contenuto                                             |
| ------------------- | ---------- | ----------------------------------------------------- |
| `utenti.ser`        | 1.0 KB     | 2 configuratori + 3 fruitori                          |
| `comprensori.ser`   | 440 B      | 3 comprensori (valli lombarde)                        |
| `gerarchie.ser`     | 3.8 KB     | 3 gerarchie multi-livello                             |
| `conversione.ser`   | 554 B      | Fattori di conversione tra foglie di riferimento      |
| `proposte.ser`      | 6.5 KB     | 21 proposte (10 aperte, 2 ritirate, 9 chiuse)         |
| `insiemechiuso.ser` | 6.0 KB     | 3 insiemi chiusi (9 proposte chiuse, 8 scambi totali) |

---

## 👤 UTENTI (utenti.ser)

### Configuratori (2)

1. **admin**

    - Password: `Admin123!`
    - Ruolo: Amministratore di sistema
    - ✅ Rispetta vincoli: min 8 char, maiuscola, minuscola, numero, carattere speciale

2. **config01**
    - Password: `Config01#`
    - Ruolo: Configuratore
    - ✅ Rispetta vincoli: min 8 char, maiuscola, minuscola, numero, carattere speciale

### Fruitori (3)

1. **mariorossi**

    - Password: `Mario123!`
    - Email: `mario.rossi@email.it`
    - Comprensorio: Valcamonica
    - ✅ Username: max 20 char, solo alfanumerici, senza spazi
    - ✅ Password: min 8 char, maiuscola, minuscola, numero, carattere speciale

2. **giuliabianchi**

    - Password: `Giulia123@`
    - Email: `giulia.bianchi@email.it`
    - Comprensorio: Valcamonica
    - ✅ Username: max 20 char, solo alfanumerici, senza spazi
    - ✅ Password: min 8 char, maiuscola, minuscola, numero, carattere speciale

3. **lucaverdi**
    - Password: `Luca123#`
    - Email: `luca.verdi@email.it`
    - Comprensorio: Valcamonica
    - ✅ Username: max 20 char, solo alfanumerici, senza spazi
    - ✅ Password: min 8 char, maiuscola, minuscola, numero, carattere speciale

> **Nota**: Tutti i fruitori sono nello stesso comprensorio (Valcamonica) per permettere la creazione di insiemi chiusi di scambio.

---

## 🗺️ COMPRENSORI (comprensori.ser)

### 1. Valcamonica

**Valli Lombarde - Val Camonica**

Comuni:

-   Breno
-   Darfo Boario Terme
-   Edolo
-   Pisogne
-   Ponte di Legno

**Totale comuni: 5**

### 2. Valtrompia

**Valli Lombarde - Val Trompia**

Comuni:

-   Gardone Val Trompia
-   Sarezzo
-   Lumezzane
-   Bovegno

**Totale comuni: 4**

### 3. Valsabbia

**Valli Lombarde - Val Sabbia**

Comuni:

-   Vestone
-   Vobarno
-   Idro
-   Preseglie

**Totale comuni: 4**

---

## 🌳 GERARCHIE (gerarchie.ser)

### 1. Sport - Struttura Multi-Livello (3 livelli, 6 foglie)

**Struttura completa:**

```
Sport (Livello 0 - Radice)
│
├─ Sport di Squadra (Livello 1)
│  ├─ Allenamento Calcio (Livello 2 - FOGLIA)
│  ├─ Allenamento Basket (Livello 2 - FOGLIA)
│  └─ Allenamento Pallavolo (Livello 2 - FOGLIA)
│
└─ Sport Individuali (Livello 1)
   ├─ Allenamento Tennis (Livello 2 - FOGLIA) [RIFERIMENTO]
   ├─ Allenamento Nuoto (Livello 2 - FOGLIA)
   └─ Allenamento Ciclismo (Livello 2 - FOGLIA)
```

**Campi caratteristici:**

-   **Livello 0 (Sport)**: Campo "Tipo Sport" → {Sport di Squadra, Sport Individuali}
-   **Livello 1 (Sport di Squadra)**: Campo "Disciplina" → {Calcio, Basket, Pallavolo}
-   **Livello 1 (Sport Individuali)**: Campo "Disciplina" → {Tennis, Nuoto, Ciclismo}

**Foglie (6):**

1. Allenamento Calcio
2. Allenamento Basket
3. Allenamento Pallavolo
4. Allenamento Tennis ⭐ (riferimento)
5. Allenamento Nuoto
6. Allenamento Ciclismo

**Fattori di conversione interni:**

-   Nodo riferimento: Allenamento Tennis (livello 2)
-   Livello 0 → livello 2: 1 ora radice = 3.0 ore Tennis
-   Livello 1 → livello 2: 1 ora intermedio = 1.5 ore Tennis

---

### 2. Formazione - Struttura Multi-Livello (4 livelli, 8 foglie)

**Struttura completa:**

```
Formazione (Livello 0 - Radice)
│
├─ Area Scientifica (Livello 1)
│  ├─ Matematica (Livello 2)
│  │  ├─ Ripetizioni Matematica Base (Livello 3 - FOGLIA) [RIFERIMENTO]
│  │  └─ Ripetizioni Matematica Avanzata (Livello 3 - FOGLIA)
│  │
│  └─ Informatica (Livello 2)
│     ├─ Corso Informatica Base (Livello 3 - FOGLIA)
│     └─ Corso Informatica Avanzata (Livello 3 - FOGLIA)
│
└─ Area Umanistica (Livello 1)
   ├─ Lingue (Livello 2)
   │  ├─ Ripetizioni Inglese (Livello 3 - FOGLIA)
   │  └─ Ripetizioni Francese (Livello 3 - FOGLIA)
   │
   └─ Ripetizioni Storia (Livello 2 - FOGLIA)
      [ramificazione diretta, solo 2 livelli per questo ramo]
```

**Campi caratteristici:**

-   **Livello 0 (Formazione)**: Campo "Area" → {Scientifica, Umanistica}
-   **Livello 1 (Area Scientifica)**: Campo "Materia" → {Matematica, Informatica}
-   **Livello 2 (Matematica)**: Campo "Livello" → {Base, Avanzato}
-   **Livello 2 (Informatica)**: Campo "Livello" → {Base, Avanzato}
-   **Livello 1 (Area Umanistica)**: Campo "Materia" → {Lingue, Storia}
-   **Livello 2 (Lingue)**: Campo "Lingua" → {Inglese, Francese}

**Foglie (8):**

1. Ripetizioni Matematica Base ⭐ (riferimento, livello 3)
2. Ripetizioni Matematica Avanzata (livello 3)
3. Corso Informatica Base (livello 3)
4. Corso Informatica Avanzata (livello 3)
5. Ripetizioni Inglese (livello 3)
6. Ripetizioni Francese (livello 3)
7. Ripetizioni Storia (livello 2)

**Fattori di conversione interni:**

-   Nodo riferimento: Ripetizioni Matematica Base (livello 3)
-   Livello 0 → livello 3: 1 ora radice = 4.0 ore Mat Base
-   Livello 1 → livello 3: 1 ora area = 2.5 ore Mat Base
-   Livello 2 → livello 3: 1 ora materia = 1.5 ore Mat Base

---

### 3. Servizi Domestici - Struttura Multi-Livello (3 livelli, 6 foglie)

**Struttura completa:**

```
Servizi Domestici (Livello 0 - Radice)
│
├─ Servizi Esterni (Livello 1)
│  ├─ Giardinaggio (Livello 2 - FOGLIA) [RIFERIMENTO]
│  └─ Manutenzione Esterna (Livello 2 - FOGLIA)
│
├─ Servizi Interni (Livello 1)
│  ├─ Pulizie Domestiche (Livello 2 - FOGLIA)
│  └─ Piccole Riparazioni (Livello 2 - FOGLIA)
│
└─ Cucina (Livello 1)
   ├─ Preparazione Pasti (Livello 2 - FOGLIA)
   └─ Catering Eventi (Livello 2 - FOGLIA)
```

**Campi caratteristici:**

-   **Livello 0 (Servizi Domestici)**: Campo "Tipo" → {Esterni, Interni}
-   **Livello 1 (Servizi Esterni)**: Campo "Categoria" → {Giardino, Manutenzione}
-   **Livello 1 (Servizi Interni)**: Campo "Categoria" → {Pulizia, Riparazioni}
-   **Livello 1 (Cucina)**: Campo "Tipo" → {Preparazione Pasti, Catering}

**Foglie (6):**

1. Giardinaggio ⭐ (riferimento)
2. Manutenzione Esterna
3. Pulizie Domestiche
4. Piccole Riparazioni
5. Preparazione Pasti
6. Catering Eventi

**Fattori di conversione interni:**

-   Nodo riferimento: Giardinaggio (livello 2)
-   Livello 0 → livello 2: 1 ora radice = 3.0 ore Giardinaggio
-   Livello 1 → livello 2: 1 ora tipo = 1.8 ore Giardinaggio

---

### Totale Categorie Foglia: 20

**Sport (6 foglie):**

1. Allenamento Calcio
2. Allenamento Basket
3. Allenamento Pallavolo
4. Allenamento Tennis
5. Allenamento Nuoto
6. Allenamento Ciclismo

**Formazione (8 foglie):** 7. Ripetizioni Matematica Base 8. Ripetizioni Matematica Avanzata 9. Corso Informatica Base 10. Corso Informatica Avanzata 11. Ripetizioni Inglese 12. Ripetizioni Francese 13. Ripetizioni Storia

**Servizi Domestici (6 foglie):** 14. Giardinaggio 15. Manutenzione Esterna 16. Pulizie Domestiche 17. Piccole Riparazioni 18. Preparazione Pasti 19. Catering Eventi

---

## 🔄 CONVERSIONI TRA GERARCHIE (conversione.ser)

Fattori di conversione tra le **foglie di riferimento** delle gerarchie:

| Da Gerarchia      | Foglia Riferimento          | A Gerarchia       | Foglia Riferimento          | Fattore           |
| ----------------- | --------------------------- | ----------------- | --------------------------- | ----------------- |
| Sport             | Allenamento Tennis          | Formazione        | Ripetizioni Matematica Base | 1.3               |
| Formazione        | Ripetizioni Matematica Base | Sport             | Allenamento Tennis          | 0.769 (reciproco) |
| Sport             | Allenamento Tennis          | Servizi Domestici | Giardinaggio                | 0.7               |
| Servizi Domestici | Giardinaggio                | Sport             | Allenamento Tennis          | 1.429 (reciproco) |
| Formazione        | Ripetizioni Matematica Base | Servizi Domestici | Giardinaggio                | 1.6               |
| Servizi Domestici | Giardinaggio                | Formazione        | Ripetizioni Matematica Base | 0.625 (reciproco) |

**Come funzionano i fattori di conversione:**

I fattori sono definiti tra le **foglie di riferimento** di ciascuna gerarchia:

-   **Sport**: Allenamento Tennis (livello 2)
-   **Formazione**: Ripetizioni Matematica Base (livello 3)
-   **Servizi Domestici**: Giardinaggio (livello 2)

Per convertire tra categorie di gerarchie diverse:

1. Si converte dalla categoria foglia al nodo di riferimento della sua gerarchia (usando i fattori interni)
2. Si applica il fattore di conversione tra le foglie di riferimento delle gerarchie
3. Si converte dal nodo di riferimento alla categoria desiderata nella gerarchia di destinazione

**Esempi pratici:**

-   1 ora di Allenamento Tennis = 1.3 ore di Ripetizioni Matematica Base
-   1 ora di Ripetizioni Matematica Base = 1.6 ore di Giardinaggio
-   1 ora di Allenamento Tennis = 0.7 ore di Giardinaggio

---

## 📋 PROPOSTE (proposte.ser)

### Proposte APERTE (10)

#### 1. mariorossi

-   **Richiede:** Allenamento Calcio, 2 ore
-   **Offre:** Ripetizioni Matematica Base, 3 ore
-   **Stato:** APERTA

#### 2. mariorossi

-   **Richiede:** Ripetizioni Inglese, 2 ore
-   **Offre:** Allenamento Tennis, 2 ore
-   **Stato:** APERTA

#### 3. giuliabianchi

-   **Richiede:** Ripetizioni Matematica Avanzata, 3 ore
-   **Offre:** Giardinaggio, 4 ore
-   **Stato:** APERTA

#### 4. giuliabianchi

-   **Richiede:** Allenamento Nuoto, 1 ora
-   **Offre:** Corso Informatica Base, 2 ore
-   **Stato:** APERTA

#### 5. lucaverdi

-   **Richiede:** Giardinaggio, 3 ore
-   **Offre:** Allenamento Basket, 2 ore
-   **Stato:** APERTA

#### 6. lucaverdi

-   **Richiede:** Piccole Riparazioni, 2 ore
-   **Offre:** Ripetizioni Inglese, 3 ore
-   **Stato:** APERTA

#### 7. mariorossi

-   **Richiede:** Allenamento Tennis, 3 ore
-   **Offre:** Pulizie Domestiche, 2 ore
-   **Stato:** APERTA

#### 8. giuliabianchi

-   **Richiede:** Corso Informatica Base, 2 ore
-   **Offre:** Allenamento Nuoto, 2 ore
-   **Stato:** APERTA

#### 9. lucaverdi

-   **Richiede:** Allenamento Calcio, 1 ora
-   **Offre:** Ripetizioni Matematica Base, 2 ore
-   **Stato:** APERTA

#### 10. mariorossi

-   **Richiede:** Preparazione Pasti, 2 ore
-   **Offre:** Allenamento Tennis, 2 ore
-   **Stato:** APERTA

---

### Proposte CHIUSE in Insiemi (9)

Le seguenti proposte sono state chiuse perché fanno parte di insiemi chiusi di scambio:

#### Insieme Chiuso 1: Ciclo a 3 (Mario→Giulia→Luca→Mario)

**Proposta 11. mariorossi**
-   **Offre:** Allenamento Tennis, 2 ore
-   **Richiede:** Ripetizioni Matematica Base, 2 ore
-   **Stato:** CHIUSA (in insieme chiuso 1)

**Proposta 12. giuliabianchi**
-   **Offre:** Ripetizioni Matematica Base, 2 ore
-   **Richiede:** Giardinaggio, 2 ore
-   **Stato:** CHIUSA (in insieme chiuso 1)

**Proposta 13. lucaverdi**
-   **Offre:** Giardinaggio, 2 ore
-   **Richiede:** Allenamento Tennis, 2 ore
-   **Stato:** CHIUSA (in insieme chiuso 1)

#### Insieme Chiuso 2: Ciclo a 2 (Mario↔Giulia) - NOTIFICATO

**Proposta 14. mariorossi**
-   **Offre:** Allenamento Calcio, 3 ore
-   **Richiede:** Ripetizioni Inglese, 3 ore
-   **Stato:** CHIUSA (in insieme chiuso 2)

**Proposta 15. giuliabianchi**
-   **Offre:** Ripetizioni Inglese, 3 ore
-   **Richiede:** Allenamento Calcio, 3 ore
-   **Stato:** CHIUSA (in insieme chiuso 2)

#### Insieme Chiuso 3: Ciclo complesso a 3

**Proposta 16. mariorossi**
-   **Offre:** Pulizie Domestiche, 2 ore
-   **Richiede:** Allenamento Basket, 2 ore
-   **Stato:** CHIUSA (in insieme chiuso 3)

**Proposta 17. giuliabianchi**
-   **Offre:** Allenamento Basket, 2 ore
-   **Richiede:** Piccole Riparazioni, 2 ore
-   **Stato:** CHIUSA (in insieme chiuso 3)

**Proposta 18. lucaverdi**
-   **Offre:** Piccole Riparazioni, 2 ore
-   **Richiede:** Pulizie Domestiche, 2 ore
-   **Stato:** CHIUSA (in insieme chiuso 3)

---

### Proposte RITIRATE (2)

#### 19. mariorossi

-   **Richiede:** Piccole Riparazioni, 4 ore
-   **Offre:** Allenamento Calcio, 3 ore
-   **Stato:** RITIRATA

#### 20. giuliabianchi

-   **Richiede:** Allenamento Nuoto, 2 ore
-   **Offre:** Ripetizioni Inglese, 2 ore
-   **Stato:** RITIRATA

---

## 🔄 INSIEMI CHIUSI (insiemechiuso.ser)

### Insieme Chiuso 1: Ciclo a 3 partecipanti

**Tipo:** Ciclo Mario→Giulia→Luca→Mario  
**Stato:** Non notificato  
**Numero di scambi:** 3

**Fruitori coinvolti:**
- mariorossi (mario.rossi@email.it)
- giuliabianchi (giulia.bianchi@email.it)  
- lucaverdi (luca.verdi@email.it)

**Proposte incluse:** 3 (proposte 11, 12, 13)

**Scambi da effettuare:**

1. **mariorossi → lucaverdi**
   - Prestazione: Allenamento Tennis, 2 ore
   
2. **giuliabianchi → mariorossi**
   - Prestazione: Ripetizioni Matematica Base, 2 ore
   
3. **lucaverdi → giuliabianchi**
   - Prestazione: Giardinaggio, 2 ore

**Descrizione del ciclo:**
- Mario offre Tennis a Luca
- Luca offre Giardinaggio a Giulia
- Giulia offre Matematica Base a Mario
- Tutti ricevono ciò che hanno richiesto

---

### Insieme Chiuso 2: Ciclo a 2 partecipanti (scambio diretto)

**Tipo:** Ciclo Mario↔Giulia  
**Stato:** ✅ NOTIFICATO  
**Numero di scambi:** 2

**Fruitori coinvolti:**
- mariorossi (mario.rossi@email.it)
- giuliabianchi (giulia.bianchi@email.it)

**Proposte incluse:** 2 (proposte 14, 15)

**Scambi da effettuare:**

1. **mariorossi → giuliabianchi**
   - Prestazione: Allenamento Calcio, 3 ore
   
2. **giuliabianchi → mariorossi**
   - Prestazione: Ripetizioni Inglese, 3 ore

**Descrizione del ciclo:**
- Mario offre Calcio a Giulia
- Giulia offre Inglese a Mario
- Scambio diretto perfettamente bilanciato (3 ore ciascuno)

---

### Insieme Chiuso 3: Ciclo complesso a 3

**Tipo:** Ciclo complesso (Pulizie→Basket→Riparazioni)  
**Stato:** Non notificato  
**Numero di scambi:** 3

**Fruitori coinvolti:**
- mariorossi (mario.rossi@email.it)
- giuliabianchi (giulia.bianchi@email.it)
- lucaverdi (luca.verdi@email.it)

**Proposte incluse:** 3 (proposte 16, 17, 18)

**Scambi da effettuare:**

1. **mariorossi → lucaverdi**
   - Prestazione: Pulizie Domestiche, 2 ore
   
2. **giuliabianchi → mariorossi**
   - Prestazione: Allenamento Basket, 2 ore
   
3. **lucaverdi → giuliabianchi**
   - Prestazione: Piccole Riparazioni, 2 ore

**Descrizione del ciclo:**
- Mario offre Pulizie a Luca
- Luca offre Riparazioni a Giulia
- Giulia offre Basket a Mario
- Ciclo di 3 gerarchie diverse (Sport, Formazione, Servizi)

---

### Statistiche Insiemi Chiusi

| Insieme | Partecipanti | Scambi | Proposte | Notificato | Ore Totali Scambiate |
|---------|--------------|--------|----------|------------|----------------------|
| 1       | 3            | 3      | 3        | ❌ No      | 6                    |
| 2       | 2            | 2      | 2        | ✅ Sì      | 6                    |
| 3       | 3            | 3      | 3        | ❌ No      | 6                    |
| **TOT** | **3**        | **8**  | **9**    | **1/3**    | **18**               |

**Vantaggi degli insiemi chiusi:**
- ✅ Ogni fruitore riceve esattamente ciò che ha richiesto
- ✅ Tutti partecipano attivamente offrendo prestazioni
- ✅ Nessuno resta escluso dal ciclo di scambio
- ✅ Ore bilanciate (tutti scambiano la stessa quantità di ore)
- ✅ I configuratori possono facilitare gli scambi contattando i fruitori

---

## 📊 Statistiche

### Per Fruitore

| Fruitore      | Proposte Totali | Aperte | Chiuse (insiemi) | Ritirate |
| ------------- | --------------- | ------ | ---------------- | -------- |
| mariorossi    | 8               | 4      | 3                | 1        |
| giuliabianchi | 8               | 4      | 3                | 1        |
| lucaverdi     | 5               | 2      | 3                | 0        |
| **TOTALE**    | **21**          | **10** | **9**            | **2**    |

### Per Categoria (nelle proposte)

**Categorie più richieste:**

1. Allenamento Calcio - 2 richieste
2. Allenamento Tennis - 2 richieste
3. Allenamento Nuoto - 2 richieste
4. Ripetizioni Inglese - 2 richieste
5. Giardinaggio - 1 richiesta
6. Corso Informatica Base - 2 richieste

**Categorie più offerte:**

1. Ripetizioni Matematica Base - 3 offerte
2. Allenamento Tennis - 2 offerte
3. Ripetizioni Inglese - 2 offerte
4. Allenamento Nuoto - 2 offerte

---

## 🔐 Credenziali di Accesso

### Per Testing del Sistema

| Username      | Password   | Tipo          | Comprensorio | Note                      |
| ------------- | ---------- | ------------- | ------------ | ------------------------- |
| admin         | Admin123!  | Configuratore | -            | Amministratore principale |
| config01      | Config01#  | Configuratore | -            | Configuratore secondario  |
| mariorossi    | Mario123!  | Fruitore      | Valcamonica  | 8 proposte (3 in insiemi) |
| giuliabianchi | Giulia123@ | Fruitore      | Valcamonica  | 8 proposte (3 in insiemi) |
| lucaverdi     | Luca123#   | Fruitore      | Valcamonica  | 5 proposte (3 in insiemi) |

---

## 🛠️ Rigenerazione dei Dati

Per rigenerare i file `.ser`, eseguire:

```bash
cd Versione1
javac -cp ".:../tree/tree/build/libs/tree-0.2.5.jar" -d out/production/Versione1 src/model/*.java src/util/*.java src/DataGenerator.java
java -cp "out/production/Versione1:../tree/tree/build/libs/tree-0.2.5.jar" DataGenerator
```

I file saranno ricreati in `data/`

---

## ✅ Requisiti Soddisfatti

### Vincoli Credenziali

-   ✅ **Username**: max 20 caratteri, solo alfanumerici, senza spazi
-   ✅ **Password**: min 8 caratteri, almeno 1 maiuscola, 1 minuscola, 1 numero, 1 carattere speciale

### Dati Richiesti

-   ✅ Credenziali di almeno **2 configuratori** (abbiamo 2)
-   ✅ Credenziali e indirizzi email di almeno **3 fruitori** (abbiamo 3)
-   ✅ Composizioni di almeno **2 comprensori** (abbiamo 3 valli lombarde)
-   ✅ Descrizioni di almeno **3 gerarchie** con globalmente almeno **6 categorie foglia** (abbiamo 3 gerarchie con 20 foglie totali!)
    -   Sport: 6 foglie, 3 livelli di profondità
    -   Formazione: 8 foglie, 4 livelli di profondità
    -   Servizi Domestici: 6 foglie, 3 livelli di profondità
-   ✅ Fattori di conversione tra le gerarchie (sia interni che tra gerarchie diverse)
-   ✅ Informazioni su almeno **10 proposte aperte** (abbiamo 10)
-   ✅ Informazioni su almeno **3 proposte chiuse** (abbiamo 9 in insiemi chiusi)
-   ✅ Informazioni su almeno **2 proposte ritirate** (abbiamo 2)
-   ✅ Informazioni su **insiemi chiusi** con cicli di scambio validi (abbiamo 3 insiemi con 8 scambi totali)

---

## 📝 Note Finali

### Insiemi Chiusi Generati

Il sistema include **3 insiemi chiusi** che dimostrano il funzionamento del matching delle proposte:

1. **Insieme 1** - Ciclo a 3 fruitori (Mario→Giulia→Luca→Mario)
   - 3 scambi bilanciati
   - Coinvolge tutte e 3 le gerarchie
   - Non ancora notificato

2. **Insieme 2** - Scambio diretto (Mario↔Giulia)
   - 2 scambi perfettamente bilanciati (3 ore ciascuno)
   - Già notificato ai configuratori
   - Esempio di match semplice

3. **Insieme 3** - Ciclo complesso a 3
   - 3 scambi tra gerarchie diverse
   - Dimostra la versatilità del sistema
   - In attesa di notifica

Tutti i fruitori sono stati assegnati allo stesso comprensorio (Valcamonica) per permettere la creazione di insiemi chiusi di scambio, rispettando il vincolo che gli scambi possano avvenire solo tra fruitori dello stesso comprensorio geografico.

````
