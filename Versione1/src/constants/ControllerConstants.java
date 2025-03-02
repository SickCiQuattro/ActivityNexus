package constants;

public class ControllerConstants {

    // 1. AppConstants.java
    // Costanti per messaggi di interfaccia generale
    public static final String INVALID_OPTION = "Opzione non valida. Riprova.";
    public static final String EXIT_MESSAGE = "Grazie per aver utilizzato il programma. Arrivederci!";
    public static final String LOGOUT_MESSAGE = "Logout effettuato con successo.";
    public static final String OPERATION_CANCELLED = "Operazione annullata.";
    public static final String ERROR_GENERIC = "Si è verificato un errore: %s";
    public static final String PROMPT_CHOICE = "Cosa vuoi fare? ";
    public static final String ERROR_INVALID_INPUT = "Input non valido. Riprova.";
    // Costanti per percorsi file
    public static final String USERS_FILE_PATH = "src/data/utenti.ser";
    public static final String COMPRESORI_FILE_PATH = "src/data/comprensori.ser";
    public static final String GERARCHIE_FILE_PATH = "src/data/gerarchie.ser";
    public static final String CONVERSIONE_FILE_PATH = "src/data/conversione.ser";
    // Costanti per caricamento e salvataggio dati
    public static final String LOADING_DATA = "Caricamento dati in corso...";
    public static final String LOADING_ITEM_SUCCESS = "✓ Dati %s caricati";
    public static final String LOADING_ITEM_EMPTY = "✓ Nessun %s trovato, si parte da zero";
    public static final String LOADING_COMPLETE = "Caricamento dati completato con successo";
    public static final String LOADING_ERROR = "Errore durante il caricamento dei dati: %s";
    public static final String LOADING_DEFAULT = "L'applicazione continuerà con un database vuoto";
    public static final String SAVING_DATA = "Salvataggio dati in corso...";
    public static final String SAVING_SUCCESS = "Dati salvati con successo";
    public static final String SAVING_ERROR = "Errore durante il salvataggio dei dati: %s";
    // 2. UserConstants.java
    public static final String NO_USERS_REGISTERED = "Nessun utente registrato. Effettua la registrazione.";
    public static final String LOGIN_SUCCESS = "Login effettuato con successo! Bentornato, ";
    public static final String LOGIN_FAILED = "Credenziali non valide. Riprova.";
    public static final String REGISTRATION_SUCCESS = "Registrazione effettuata con successo!";
    public static final String REGISTRATION_FAILED = "Registrazione fallita. Utente già esistente. Riprova.";
    public static final String SAVING_USERS_SUCCESS = "Dati utente salvati con successo";
    // Costanti per autenticazione e registrazione
    public static final String PROMPT_USERNAME = "Inserisci username: ";
    public static final String PROMPT_PASSWORD = "Inserisci password: ";
    public static final String PROMPT_NEW_USERNAME = "Inserisci un nuovo username (non vuoto, senza spazi, lungo massimo %d caratteri, senza caratteri speciali): ";
    public static final String PROMPT_NEW_PASSWORD = "Inserisci una nuova password (lunga almeno %d caratteri, contenere almeno: una lettera maiuscola, una minuscola e un carattere speciale): ";
    public static final String PROMPT_CONFIG_ROLE = "Sei un configuratore? (s/n): ";
    public static final String DEFAULT_LOGIN_INTRO = "Effettua il login con le seguenti credenziali:";
    public static final String DEFAULT_USERNAME_DISPLAY = "Username: %s";
    public static final String DEFAULT_PASSWORD_DISPLAY = "Password: %s";
    public static final String DEFAULT_LOGIN_INVALID = "Credenziali non valide. Tentativi rimasti: %d";
    public static final String DEFAULT_LOGIN_FAILED = "Login non riuscito. Riprova più tardi.";
    public static final String ERROR_INVALID_USERNAME = "Username non valido. Riprova.";
    public static final String ERROR_INVALID_PASSWORD = "Password non valido. Riprova.";
    // 3. ComprensoriConstants.java
    // Costanti per titoli e sezioni
    public static final String TITLE_COMPRENSORI = "\n=== COMPRENSORI DISPONIBILI ===";
    public static final String TITLE_NEW_COMPRENSORIO = "\n=== CREAZIONE NUOVO COMPRENSORIO ===";
    // Costanti per messaggi informativi
    public static final String INFO_COMPRENSORIO_DESC = "Un comprensorio è un'area geografica che contiene più comuni.";
    public static final String INFO_COMPRENSORIO_NEXT = "Dopo aver definito il nome del comprensorio, potrai aggiungere i comuni appartenenti ad esso.";
    public static final String NO_COMPRENSORI = "Nessun comprensorio presente.";
    public static final String COMPRENSORIO_CREATED = "Comprensorio creato con successo!";
    // Costanti per comprensori - passi
    public static final String STEP_NOME_COMPRENSORIO = "\n➤ PASSO 1: DEFINIZIONE DEL NOME DEL COMPRENSORIO";
    public static final String STEP_COMUNI_COMPRENSORIO = "\n➤ PASSO 2: AGGIUNTA DEI COMUNI";
    // Costanti per comprensori - prompts
    public static final String PROMPT_NOME_COMPRENSORIO = "Inserisci il nome del comprensorio (premi 0 per annullare): ";
    public static final String PROMPT_NOME_COMUNE = "Inserisci il nome del comune (premi 0 per terminare): ";
    public static final String PROMPT_ADD_ALTRO_COMUNE = "Vuoi aggiungere un altro comune? (s/n): ";
    // Costanti per comprensori - messaggi informativi
    public static final String INFO_AGGIUNGI_COMUNI = "Ora puoi aggiungere i comuni che fanno parte del comprensorio '%s'.";
    public static final String INFO_MINIMO_UN_COMUNE = "Ogni comprensorio deve contenere almeno un comune.";
    public static final String INFO_COMUNI_AGGIUNTI = "Comuni aggiunti finora: %d";
    // Costanti per comprensori - messaggi di errore
    public static final String ERROR_NOME_VUOTO = "✗ Il nome del comprensorio non può essere vuoto.";
    public static final String ERROR_NOME_CORTO = "✗ Il nome del comprensorio deve contenere almeno 3 caratteri.";
    public static final String ERROR_NOME_DUPLICATO = "✗ Esiste già un comprensorio con questo nome.";
    public static final String ERROR_COMUNE_VUOTO = "✗ Il nome del comune non può essere vuoto.";
    public static final String ERROR_COMUNE_CORTO = "✗ Il nome del comune deve contenere almeno 2 caratteri.";
    public static final String ERROR_COMUNE_DUPLICATO = "✗ Il comune '%s' è già presente in questo comprensorio.";
    public static final String ERROR_NESSUN_COMUNE = "✗ Il comprensorio deve contenere almeno un comune.";
    public static final String ERROR_CREAZIONE_ANNULLATA = "✗ Creazione comprensorio annullata: nessun comune aggiunto.";
    // Costanti per comprensori - messaggi di successo
    public static final String SUCCESS_COMPRENSORIO_AGGIUNTO = "✓ Comprensorio '%s' aggiunto con successo!";
    public static final String SUCCESS_COMUNE_AGGIUNTO = "✓ Comune '%s' aggiunto al comprensorio '%s'";
    // 4. GerarchieConstants.java
    // Costanti per titoli e sezioni
    public static final String TITLE_GERARCHIE = "\n=== GERARCHIE DISPONIBILI ===";
    public static final String TITLE_NEW_GERARCHIA = "\n=== CREAZIONE NUOVA GERARCHIA ===";
    public static final String TITLE_CREATE_GERARCHIA = "\n=== CREAZIONE GERARCHIA ===";
    public static final String STEP_RADICE_GERARCHIA = "\n➤ PASSO 1: CREAZIONE CATEGORIA RADICE";
    public static final String STEP_STRUTTURA_GERARCHIA = "\n➤ PASSO 2: STRUTTURAZIONE DELLA GERARCHIA";
    public static final String STEP_COMPLETAMENTO_GERARCHIA = "\n➤ PASSO 3: COMPLETAMENTO";
    public static final String TITLE_CAMPO_CARATTERISTICO = "\n== CREAZIONE CAMPO CARATTERISTICO ==";
    public static final String TITLE_VALORI_DOMINIO = "\n== CREAZIONE VALORI DI DOMINIO ==";
    // Costanti per messaggi informativi
    public static final String NO_GERARCHIE = "Nessuna gerarchia presente nel sistema.";
    public static final String NO_GERARCHIE_CONV = "Nessuna gerarchia presente nel sistema. Impossibile visualizzare fattori di conversione.";
    public static final String INFO_GERARCHIA_DESC = "Una gerarchia è una struttura ad albero di categorie che rappresentano attività.";
    public static final String INFO_GERARCHIA_PROCESS = "Il processo di creazione sarà guidato partendo dalla categoria radice.";
    public static final String INFO_RADICE_DESC = "La categoria radice rappresenta il livello più alto della gerarchia.";
    public static final String INFO_RADICE_CAMPO = "Deve avere un campo caratteristico che definisce le sottocategorie.";
    public static final String INFO_STRUTTURA_DESC = "Ora procederemo a creare la struttura gerarchica completa.";
    public static final String INFO_STRUTTURA_VALORI = "Per ogni valore del campo verranno create sottocategorie.";
    public static final String INFO_CAMPO_DESC = "Un campo caratteristico descrive una proprietà della categoria.";
    public static final String INFO_DOMINIO_DESC = "Il dominio è l'insieme dei possibili valori che il campo può assumere.";
    public static final String INFO_DOMINIO_SOTTOCATEGORIE = "Ogni valore del dominio genererà una sottocategoria.";
    public static final String INFO_CAMPO_PROPRIETA = "Un campo caratteristico definisce le proprietà della categoria";
    public static final String INFO_CAMPO_FOGLIE = "e permette di creare sottocategorie. Le categorie senza campo sono foglie (attività concrete).";
    public static final String INFO_CREAZIONE_SOTTOCATEGORIA = "\n-- Creazione sottocategoria %d/%d per '%s' --";
    public static final String GERARCHIA_CREATED = "Gerarchia creata con successo!";
    public static final String GERARCHIA_ADDED = "Gerarchia aggiunta al database con successo!";
    public static final String CONVERSIONE_TITLE = "Generazione dei fattori di conversione...";
    // Costanti per gerarchie - prompt
    public static final String MSG_SCELTA_ERRATA = "La scelta deve essere un valore intero compreso tra 1 e %d.";
    public static final String MSG_LISTA_GERARCHIE = "\nEcco una lista delle categorie radice delle gerarchie presenti. Le radici sono contrassegnate da un numero:";
    public static final String MSG_SCELTA_GERARCHIA = "Inserisci il numero corrispondente alla gerarchia scelta: ";
    public static final String MSG_LISTA_FOGLIE = "\nEcco la gerarchia scelta. Le foglie sono contrassegnate da un numero:";
    public static final String MSG_SCELTA_FOGLIA = "Inserisci il numero corrispondente alla categoria foglia scelta: ";
    public static final String PROMPT_NOME_CATEGORIA_RADICE = "Inserisci il nome della categoria radice: ";
    public static final String PROMPT_NOME_SOTTOCATEGORIA = "Inserisci il nome della sottocategoria di '%s': ";
    public static final String PROMPT_CAMPO_CARATTERISTICO = "Vuoi aggiungere un campo caratteristico a questa categoria? (s/n): ";
    public static final String PROMPT_NOME_CAMPO = "Inserisci il nome del campo: ";
    public static final String PROMPT_VALORE_DOMINIO = "\nInserisci il nome del valore #%d. Premi 0 per terminare: ";
    public static final String PROMPT_DESCRIZIONE_VALORE = "Inserisci una descrizione per '%s' (facoltativa, premi INVIO per saltare): ";
    public static final String PROMPT_ALTRO_VALORE = "Vuoi aggiungere un altro valore di dominio? (s/n): ";
    // Costanti per gerarchie - messaggi di errore
    public static final String ERROR_NOME_CATEGORIA_VUOTO = "✗ Nome non valido. Il nome non può essere vuoto.";
    public static final String ERROR_NOME_GERARCHIA_DUPLICATO = "✗ Esiste già una gerarchia con questo nome";
    public static final String ERROR_NOME_CATEGORIA_DUPLICATO = "✗ Esiste già una categoria con questo nome in questa gerarchia";
    public static final String ERROR_CAMPO_VUOTO = "✗ Il nome del campo non può essere vuoto.";
    public static final String ERROR_DOMINIO_VUOTO = "✗ Il campo deve contenere almeno un valore di dominio.";
    public static final String ERROR_VALORE_VUOTO = "✗ Il nome del valore non può essere vuoto.";
    public static final String ERROR_VALORE_DUPLICATO = "✗ Valore '%s' già esistente nel dominio.";
    public static final String ERROR_RADICE_SENZA_CAMPO = "ERRORE: La categoria radice deve avere un campo caratteristico!";
    public static final String ERROR_MINIMO_VALORE = "Devi inserire almeno un valore di dominio.";
    // Costanti per gerarchie - messaggi di successo
    public static final String SUCCESS_GERARCHIA_COMPLETATA = "Gerarchia completata con successo!";
    public static final String SUCCESS_CATEGORIA_CON_CAMPO = "✓ Categoria '%s' creata con campo caratteristico '%s'";
    public static final String SUCCESS_CATEGORIA_FOGLIA = "✓ Categoria foglia '%s' creata";
    public static final String SUCCESS_CAMPO_CREATO = "✓ Campo '%s' creato con %d valori di dominio";
    public static final String SUCCESS_VALORE_AGGIUNTO = "✓ Valore '%s' aggiunto al dominio";
    // Costanti per gerarchie - messaggi di riepilogo
    public static final String RIEPILOGO_PROFONDITA = "La gerarchia ha una profondità di %d livelli.";
    public static final String RIEPILOGO_FOGLIE = "Contiene %d categorie foglia.";
    public static final String RIEPILOGO_DOMINIO = "\nRiepilogo valori del dominio per il campo '%s':";
    public static final String FORMATO_VALORE_DOMINIO = " %d. %s - %s";
    public static final String NESSUNA_DESCRIZIONE = "(nessuna descrizione)";
    // 5. ConversioneConstants.java
    // Costanti per fattori di conversione - titoli e passi
    public static final String TITLE_FATTORI_CONVERSIONE = "\n=== FATTORI DI CONVERSIONE ===";
    public static final String TITLE_CONVERSIONI_TRA_GERARCHIE = "\n=== CONVERSIONI TRA GERARCHIE ===";
    public static final String TITLE_CONVERSIONE_GERARCHIE = "\n=== CREAZIONE FATTORI DI CONVERSIONE TRA GERARCHIE ===";
    // Costanti per fattori di conversione - descrizioni informative
    public static final String INFO_CONVERSIONE_INTRO = "Per rendere confrontabili attività di gerarchie diverse, è necessario definire fattori di conversione.";
    public static final String INFO_CONVERSIONE_PONTE = "Il processo utilizza categorie di riferimento (una per ogni gerarchia) come 'ponte' tra le gerarchie.";
    public static final String INFO_CONVERSIONE_GT1 = "Un fattore > 1 significa che un'ora dell'attività di destinazione vale più di un'ora dell'attività di origine.";
    public static final String INFO_CONVERSIONE_LT1 = "Un fattore < 1 significa che un'ora dell'attività di destinazione vale meno di un'ora dell'attività di origine.";
    public static final String INFO_GERARCHIA_NUOVA = "➤ GERARCHIA NUOVA: %s";
    public static final String INFO_CATEGORIA_RIFERIMENTO = "   Categoria di riferimento: %s (livello %d)";
    public static final String INFO_CONVERSIONE_CON = "➤ CONVERSIONE CON: %s";
    public static final String INFO_PERCORSO_NUOVO = "\n   Percorso nella gerarchia nuova:";
    public static final String INFO_PERCORSO_ESISTENTE = "\n   Percorso nella gerarchia esistente:";
    public static final String INFO_NESSUNA_GERARCHIA = "\nNessun'altra gerarchia presente nel sistema. Non è necessario creare conversioni.";
    public static final String INFO_VISUALIZZA_FATTORI = "\nEcco i fattori di conversione relativi alla categoria \"%s\":";
    public static final String INFO_SELEZIONA_DESTINAZIONE = "\nOra seleziona la gerarchia di cui vuoi visualizzare i fattori di conversione relativi alla categoria \"%s\":\n";
    // Costanti per fattori di conversione - prompts
    public static final String PROMPT_FATTORE = "Inserisci il fattore di conversione tra la categoria \"%s\" e la categoria \"%s\": ";
    // Costanti per fattori di conversione - messaggi di errore
    public static final String ERROR_FATTORE_SPECIALIZZATO = "Il fattore di conversione viene consigliato maggiore di 1 e massimo 2 per attività più specializzate.";
    public static final String ERROR_FATTORE_NON_SPECIALIZZATO = "Il fattore di conversione deve essere compreso tra 0.5 e meno di 1 per attività meno specializzate.";
    public static final String ERROR_FATTORE_FORMATO = "Il fattore di conversione deve essere un valore decimale compreso tra 0.5 e 2.";
    public static final String ERROR_FATTORE_RANGE = "Il fattore di conversione tra gerarchie deve essere un valore decimale compreso tra 0.5 e 2.";
    // Costanti per fattori di conversione - messaggi di successo
    public static final String SUCCESS_FATTORE_AGGIUNTO = "✓ Fattore di conversione aggiunto: %s";
    public static final String SUCCESS_FATTORI_CREATI = "\nSono stati creati %d fattori di conversione tra gerarchie.";
    public static final String SUCCESS_FATTORE_SEMPLICE = "Fattore di conversione aggiunto";
    public static final String INFO_NESSUN_FATTORE = "\nNessun fattore di conversione creato.";
    // Costanti per separatori visivi
    public static final String SEPARATORE = "\n----------------------------------------------------------";
}
