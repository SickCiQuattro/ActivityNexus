package view;

import java.util.*;

/**
 * Interfaccia utente a console con funzionalità avanzate di visualizzazione.
 * <p>
 * Questa classe implementa il componente View del pattern MVC
 * (Model-View-Controller)
 * per l'applicazione ActivityNexus. Fornisce un'interfaccia testuale completa
 * con:
 * <ul>
 * <li><b>Gestione input/output:</b> metodi per ricevere e mostrare dati in modo
 * strutturato</li>
 * <li><b>Menu interattivi:</b> visualizzazione di menu gerarchici con selezione
 * numerica</li>
 * <li><b>Layout formattati:</b> riquadri, tabelle e separatori visivi</li>
 * <li><b>Supporto colori ANSI:</b> colorazione del testo per migliorare
 * l'usabilità</li>
 * <li><b>Validazione input:</b> verifica dei valori inseriti con richieste
 * ripetute</li>
 * </ul>
 * <p>
 * La classe utilizza i codici di escape ANSI per la formattazione e colorazione
 * del testo,
 * garantendo compatibilità con la maggior parte dei terminali moderni. Per
 * terminali
 * che non supportano ANSI, il testo rimane leggibile anche se non colorato.
 * <p>
 * <b>Invarianti:</b>
 * <ul>
 * <li>Lo scanner per l'input dell'utente è sempre disponibile fino alla
 * chiamata di {@link #close()}</li>
 * <li>Tutti i metodi di visualizzazione garantiscono output leggibili anche
 * senza supporto ANSI</li>
 * </ul>
 * 
 * @see controller.Controller
 * @see java.util.Scanner
 */
public class ConsoleView {
    // -----------------------------------------------------------------------------------
    // ATTRIBUTI
    // -----------------------------------------------------------------------------------

    /** Scanner per input utente */
    private final Scanner scanner = new Scanner(System.in);

    // -----------------------------------------------------------------------------------
    // COSTANTI PER LA FORMATTAZIONE
    // -----------------------------------------------------------------------------------

    // Codici di reset
    private static final String RESET = "\033[0m";

    // Stili di testo
    private static final String BOLD = "\033[1m";

    // Colori testo
    private static final String BLACK = "\033[30m";
    private static final String RED = "\033[31m";
    private static final String GREEN = "\033[32m";
    private static final String YELLOW = "\033[33m";
    private static final String BLUE = "\033[34m";
    private static final String MAGENTA = "\033[35m";
    private static final String CYAN = "\033[36m";
    private static final String WHITE = "\033[37m";

    // Colori sfondo
    private static final String BG_RED = "\033[41m";
    private static final String BG_GREEN = "\033[42m";
    private static final String BG_YELLOW = "\033[43m";
    private static final String BG_BLUE = "\033[44m";

    // -----------------------------------------------------------------------------------
    // COSTANTI PER I SIMBOLI GRAFICI
    // -----------------------------------------------------------------------------------

    /** Carattere per angolo superiore sinistro */
    private static final String TOP_LEFT_CORNER = "╔";

    /** Carattere per angolo superiore destro */
    private static final String TOP_RIGHT_CORNER = "╗";

    /** Carattere per angolo inferiore sinistro */
    private static final String BOTTOM_LEFT_CORNER = "╚";

    /** Carattere per angolo inferiore destro */
    private static final String BOTTOM_RIGHT_CORNER = "╝";

    /** Carattere per bordo orizzontale */
    private static final String HORIZONTAL_BORDER = "═";

    /** Carattere per bordo verticale */
    private static final String VERTICAL_BORDER = "║";

    /** Carattere per connettore sinistro del separatore */
    private static final String LEFT_SEPARATOR = "╠";

    /** Carattere per connettore destro del separatore */
    private static final String RIGHT_SEPARATOR = "╣";

    /** Carattere per simbolo di prompt */
    private static final String PROMPT_SYMBOL = "▶";

    // -----------------------------------------------------------------------------------
    // COSTANTI PER I MESSAGGI
    // -----------------------------------------------------------------------------------

    private static final String WELCOME_MESSAGE = BOLD + YELLOW + "Benvenuto in ActivityNexus!" + RESET;
    private static final String CONFIGURATOR_MENU_TITLE = BOLD + YELLOW + "MENU Configuratore" + RESET;
    private static final String USER_MENU_TITLE = BOLD + YELLOW + "MENU Fruitore" + RESET;

    /** Etichetta per messaggi di errore */
    private static final String ERROR_LABEL = BG_RED + WHITE + " ERRORE " + RESET;

    /** Etichetta per messaggi di successo */
    private static final String SUCCESS_LABEL = BG_GREEN + BLACK + " SUCCESSO " + RESET;

    /** Etichetta per messaggi informativi */
    private static final String INFO_LABEL = BG_BLUE + WHITE + " INFO " + RESET;

    /** Etichetta per messaggi di avviso */
    private static final String WARNING_LABEL = BG_YELLOW + BLACK + " AVVISO " + RESET;

    /** Messaggio per input numerico non valido */
    private static final String INVALID_NUMBER_MESSAGE = "Inserisci un numero valido.";

    /** Messaggio per input fuori range */
    private static final String OUT_OF_RANGE_MESSAGE = "Inserisci un valore compreso tra ";

    /** Messaggio per input di conferma non valido */
    private static final String INVALID_CONFIRMATION_MESSAGE = "Risposta non valida. Utilizza 's' per confermare o 'n' per annullare.";

    /** Messaggio per attendere pressione del tasto INVIO */
    private static final String PRESS_ENTER_MESSAGE = YELLOW + PROMPT_SYMBOL + " Premi INVIO per continuare..."
            + RESET;

    /** Formato per il prompt di input con range */
    private static final String RANGE_PROMPT_FORMAT = " [%s-%s]";

    /** Suffisso per prompt di conferma */
    private static final String CONFIRMATION_PROMPT_SUFFIX = " (s/n): ";

    /** Prompt per l'inserimento della scelta nel menu */
    private static final String MENU_CHOICE_PROMPT = YELLOW + PROMPT_SYMBOL + " " + RESET + "Inserisci la tua scelta: ";

    // -----------------------------------------------------------------------------------
    // COSTANTI PER LE VOCI DEI MENU
    // -----------------------------------------------------------------------------------

    // Menu principale
    private static final String MENU_LOGIN = GREEN + "1. Accesso" + RESET;
    private static final String MENU_REGISTRATION = CYAN + "2. Registrazione" + RESET;
    private static final String MENU_EXIT = RED + "0. Uscita" + RESET;

    // Menu configuratore
    private static final String MENU_VIEW_COMPRENSORI = GREEN + "1. Visualizza Comprensori" + RESET;
    private static final String MENU_VIEW_GERARCHIE = GREEN + "2. Visualizza Gerarchie" + RESET;
    private static final String MENU_VIEW_CONVERSION_FACTORS = GREEN + "3. Visualizza Fattori di Conversione" + RESET;
    private static final String MENU_CREATE_COMPRENSORIO = CYAN + "4. Crea Comprensorio" + RESET;
    private static final String MENU_CREATE_GERARCHIA = CYAN + "5. Crea Gerarchia" + RESET;
    private static final String MENU_VIEW_PROPOSTE = GREEN + "6. Visualizza proposte" + RESET;
    private static final String MENU_VIEW_INSIEMI_CHIUSI = GREEN + "7. Visualizza insiemi chiusi" + RESET;

    // Menu fruitore
    private static final String MENU_VIEW_CATEGORIE = GREEN + "1. Visualizza categorie" + RESET;
    private static final String MENU_CREA_PROPOSTA = BLUE + "2. Formula proposta di scambio d'opera" + RESET;
    private static final String MENU_VIEW_PROPOSTE_FRUITORE = GREEN + "3. Visualizza proposte di scambio d'opera"
            + RESET;
    private static final String MENU_RITIRA_PROPOSTA = BLUE + "4. Ritira una proposta aperta" + RESET;

    // -----------------------------------------------------------------------------------
    // METODI PER L'INTERAZIONE BASE
    // -----------------------------------------------------------------------------------

    /**
     * Mostra un messaggio sulla console.
     * <p>
     * Visualizza il messaggio specificato seguito da un'interruzione di riga.
     * Non applica formattazione speciale al testo.
     *
     * @param messaggio il messaggio da visualizzare
     * 
     * @pre true (messaggio può essere null)
     * @post Se messaggio == null, viene visualizzato "null"
     * @post Se messaggio != null, il messaggio viene visualizzato sulla console
     */
    public void showMessage(String messaggio) {
        System.out.println(messaggio);
    }

    /**
     * Mostra un messaggio sulla console e attende INVIO.
     * <p>
     * Visualizza il messaggio specificato seguito da un'interruzione di riga.
     * Attende che l'utente prema INVIO per continuare. Utile per mostrare
     * informazioni che l'utente deve poter leggere prima che lo schermo si pulisca.
     *
     * @param messaggio il messaggio da visualizzare
     * 
     * @pre true (messaggio può essere null)
     * @post Se messaggio == null, viene visualizzato "null"
     * @post Se messaggio != null, il messaggio viene visualizzato sulla console
     * @post L'esecuzione si blocca fino alla pressione di INVIO
     */
    public void showMessageAndWait(String messaggio) {
        System.out.println(messaggio);
        pressEnterToContinue();
    }

    /**
     * Mostra un messaggio di errore con formattazione rossa.
     * <p>
     * Visualizza il messaggio con sfondo rosso per l'etichetta "ERRORE" e testo
     * rosso
     * per il contenuto, per evidenziare situazioni problematiche o errori.
     *
     * @param messaggio il messaggio di errore
     * 
     * @pre true (messaggio può essere null)
     * @post Il messaggio di errore viene visualizzato sulla console con
     *       formattazione rossa
     */
    public void showErrorMessage(String messaggio) {
        System.out.println(ERROR_LABEL + " " + RED + messaggio + RESET);
        pressEnterToContinue();
    }

    /**
     * Mostra un messaggio di successo con formattazione verde.
     * <p>
     * Visualizza il messaggio con sfondo verde per l'etichetta "SUCCESSO" e testo
     * verde
     * per il contenuto, per confermare operazioni completate con successo.
     * Attende che l'utente prema INVIO per permettergli di leggere il messaggio.
     *
     * @param messaggio il messaggio di successo
     * 
     * @pre true (messaggio può essere null)
     * @post Il messaggio di successo viene visualizzato sulla console con
     *       formattazione verde
     * @post L'esecuzione si blocca fino alla pressione di INVIO
     */
    public void showSuccessMessage(String messaggio) {
        System.out.println(SUCCESS_LABEL + " " + GREEN + messaggio + RESET);
        pressEnterToContinue();
    }

    public void showSuccessMessageNoWait(String messaggio) {
        System.out.println(SUCCESS_LABEL + " " + GREEN + messaggio + RESET);
    }

    /**
     * Mostra un messaggio informativo con formattazione blu.
     * <p>
     * Visualizza il messaggio con sfondo blu per l'etichetta "INFO" e testo blu
     * per il contenuto, per comunicare informazioni neutrali all'utente.
     * Attende che l'utente prema INVIO per permettergli di leggere il messaggio.
     *
     * @param messaggio il messaggio informativo
     * 
     * @pre true (messaggio può essere null)
     * @post Il messaggio informativo viene visualizzato sulla console con
     *       formattazione blu
     * @post L'esecuzione si blocca fino alla pressione di INVIO
     */
    public void showInfoMessage(String messaggio) {
        System.out.println(INFO_LABEL + " " + BLUE + messaggio + RESET);
        pressEnterToContinue();
    }

    /**
     * Mostra un messaggio di avviso con formattazione gialla.
     * <p>
     * Visualizza il messaggio con sfondo giallo per l'etichetta "AVVISO" e testo
     * giallo
     * per il contenuto, per segnalare situazioni che richiedono attenzione ma non
     * sono errori.
     * Attende che l'utente prema INVIO per permettergli di leggere il messaggio.
     *
     * @param messaggio il messaggio di avviso
     * 
     * @pre true (messaggio può essere null)
     * @post Il messaggio di avviso viene visualizzato sulla console con
     *       formattazione gialla
     * @post L'esecuzione si blocca fino alla pressione di INVIO
     */
    public void showWarningMessage(String messaggio) {
        System.out.println(WARNING_LABEL + " " + YELLOW + messaggio + RESET);
    }

    /**
     * Ottiene input testuale dopo aver mostrato un prompt.
     * <p>
     * Visualizza il prompt specificato e attende che l'utente inserisca
     * una stringa, rimuovendo gli spazi iniziali e finali (trim).
     *
     * @param prompt il messaggio di richiesta
     * @return l'input dell'utente con trim applicato
     * 
     * @pre scanner != null e disponibile per l'input
     * @post result != null
     * @post result non contiene spazi iniziali o finali
     */
    public String getInput(String prompt) {
        System.out.print(CYAN + prompt + RESET + " ");
        return scanner.nextLine().trim();
    }

    /**
     * Ottiene una password dall'utente (senza mascheramento).
     * <p>
     * Visualizza il prompt specificato e attende che l'utente inserisca
     * una password. A differenza di {@link #getInput}, non applica trim
     * per preservare eventuali spazi nella password.
     * <p>
     * <b>Nota:</b> In un'interfaccia console standard, la password viene mostrata
     * in chiaro senza mascheramento.
     *
     * @param prompt il messaggio di richiesta
     * @return la password inserita
     * 
     * @pre scanner != null e disponibile per l'input
     * @post result != null
     * @post result è esattamente la stringa inserita dall'utente senza modifiche
     */
    public String getPasswordInput(String prompt) {
        System.out.print(MAGENTA + prompt + RESET + " ");
        // In una console standard non possiamo mascherare l'input
        return scanner.nextLine();
    }

    /**
     * Ottiene un input numerico intero con validazione del range.
     * <p>
     * Continua a richiedere l'input finché l'utente non fornisce un numero valido
     * nell'intervallo specificato. Gestisce automaticamente gli errori di formato
     * e i valori fuori dal range consentito.
     *
     * @param prompt il messaggio di richiesta
     * @param min    valore minimo accettabile
     * @param max    valore massimo accettabile
     * @return il numero intero inserito entro il range specificato
     * 
     * @pre scanner != null e disponibile per l'input
     * @pre min <= max
     * @post min <= result <= max
     * @post Il metodo ha richiesto input finché non ha ricevuto un valore valido
     */
    public int getIntInput(String prompt, int min, int max) {
        while (true) {
            try {
                // Rimuovi eventuali due punti e spazi finali dal prompt per evitare duplicazioni
                String cleanPrompt = prompt.trim();
                if (cleanPrompt.endsWith(":")) {
                    cleanPrompt = cleanPrompt.substring(0, cleanPrompt.length() - 1).trim();
                }
                
                String input = getInput(cleanPrompt + String.format(RANGE_PROMPT_FORMAT, min, max) + ": ");
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                } else {
                    showErrorMessage(OUT_OF_RANGE_MESSAGE + min + " e " + max);
                }
            } catch (NumberFormatException e) {
                showErrorMessage(INVALID_NUMBER_MESSAGE);
            }
        }
    }

    /**
     * Ottiene un input numerico decimale con validazione del range.
     * <p>
     * Continua a richiedere l'input finché l'utente non fornisce un numero valido
     * nell'intervallo specificato. Gestisce automaticamente gli errori di formato
     * e i valori fuori dal range consentito.
     *
     * @param prompt il messaggio di richiesta
     * @param min    valore minimo accettabile
     * @param max    valore massimo accettabile
     * @return il numero decimale inserito entro il range specificato
     * 
     * @pre scanner != null e disponibile per l'input
     * @pre min <= max
     * @post min <= result <= max
     * @post Il metodo ha richiesto input finché non ha ricevuto un valore valido
     */
    public double getDoubleInput(String prompt, double min, double max) {
        while (true) {
            try {
                // Rimuovi eventuali due punti e spazi finali dal prompt per evitare duplicazioni
                String cleanPrompt = prompt.trim();
                if (cleanPrompt.endsWith(":")) {
                    cleanPrompt = cleanPrompt.substring(0, cleanPrompt.length() - 1).trim();
                }
                
                String input = getInput(cleanPrompt + String.format(RANGE_PROMPT_FORMAT, min, max) + ": ");
                double value = Double.parseDouble(input);
                if (value >= min && value <= max) {
                    return value;
                } else {
                    showErrorMessage(OUT_OF_RANGE_MESSAGE + min + " e " + max);
                }
            } catch (NumberFormatException e) {
                showErrorMessage(INVALID_NUMBER_MESSAGE);
            }
        }
    }

    /**
     * Richiede conferma (sì/no) all'utente.
     * <p>
     * Continua a richiedere l'input finché l'utente non fornisce una risposta
     * valida tra quelle riconosciute come affermative (s, si, y, yes) o
     * negative (n, no).
     *
     * @param prompt il messaggio di richiesta
     * @return true per conferma, false per rifiuto
     * 
     * @pre scanner != null e disponibile per l'input
     * @post result è true solo se l'utente ha inserito una risposta affermativa
     * @post Il metodo ha richiesto input finché non ha ricevuto una risposta valida
     */
    public boolean getConfirmation(String prompt) {
        while (true) {
            String input = getInput(prompt + CONFIRMATION_PROMPT_SUFFIX).toLowerCase();
            if (input.equals("s") || input.equals("si") || input.equals("y") || input.equals("yes")) {
                return true;
            } else if (input.equals("n") || input.equals("no")) {
                return false;
            } else {
                showMessage(INVALID_CONFIRMATION_MESSAGE);
            }
        }
    }

    /**
     * Attende che l'utente prema INVIO per continuare.
     * <p>
     * Visualizza un messaggio standard e blocca l'esecuzione fino a quando
     * l'utente non preme il tasto INVIO. Utile per permettere all'utente di
     * leggere informazioni prima di procedere.
     * 
     * @pre scanner != null e disponibile per l'input
     * @post L'esecuzione è stata bloccata fino alla pressione di INVIO
     */
    public void pressEnterToContinue() {
        System.out.print(PRESS_ENTER_MESSAGE);
        scanner.nextLine();
    }

    /**
     * Mostra un messaggio e attende automaticamente per un tempo specificato.
     * <p>
     * Visualizza il messaggio e mette in pausa l'esecuzione per permettere
     * all'utente di leggere, senza richiedere l'interazione con INVIO.
     * Utile per messaggi di benvenuto, logout o altri messaggi informativi
     * che non richiedono conferma esplicita.
     *
     * @param messaggio    il messaggio da visualizzare
     * @param millisecondi il tempo di attesa in millisecondi (es. 2000 = 2 secondi)
     * 
     * @pre true (messaggio può essere null)
     * @pre millisecondi >= 0
     * @post Il messaggio è stato visualizzato
     * @post L'esecuzione è stata bloccata per il tempo specificato
     */
    public void showMessageWithDelay(String messaggio, int millisecondi) {
        System.out.println(messaggio);
        try {
            Thread.sleep(millisecondi);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Pulisce lo schermo del terminale.
     * <p>
     * Utilizza i codici di escape ANSI per cancellare il contenuto del terminale
     * e posizionare il cursore in alto a sinistra. Funziona sulla maggior parte
     * dei terminali moderni ma potrebbe non funzionare su tutti i sistemi.
     *
     * @post Sono stati inviati i codici ANSI per pulire lo schermo
     */
    public void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    // -----------------------------------------------------------------------------------
    // METODI PER LA VISUALIZZAZIONE DEI MENU
    // -----------------------------------------------------------------------------------

    /**
     * Mostra il menu principale con logo ASCII art.
     * <p>
     * Pulisce lo schermo, visualizza il logo dell'applicazione e mostra le opzioni
     * del menu principale (login, registrazione, uscita). Attende l'input
     * dell'utente
     * alla fine della visualizzazione.
     * 
     * @pre true (nessuna precondizione particolare)
     * @post Lo schermo è stato pulito
     * @post Il logo ASCII è stato visualizzato
     * @post Il menu principale è stato visualizzato con tutte le opzioni
     */
    public void showMenu() {
        clearScreen();
        displayAsciiLogo();

        List<String> menuItems = List.of(
                MENU_LOGIN,
                MENU_REGISTRATION,
                MENU_EXIT);

        printMenuBox(WELCOME_MESSAGE, menuItems);
    }

    /**
     * Mostra il menu per utenti configuratori.
     * <p>
     * Pulisce lo schermo e visualizza le opzioni disponibili per gli utenti
     * con ruolo di configuratore. Include funzionalità amministrative come
     * la gestione di comprensori, gerarchie e visualizzazione di dati di sistema.
     * 
     * @pre true (nessuna precondizione particolare)
     * @post Lo schermo è stato pulito
     * @post Il menu configuratore è stato visualizzato con tutte le opzioni
     */
    public void showMenuConfiguratore() {
        clearScreen();

        List<String> menuItems = List.of(
                MENU_VIEW_COMPRENSORI,
                MENU_VIEW_GERARCHIE,
                MENU_VIEW_CONVERSION_FACTORS,
                MENU_CREATE_COMPRENSORIO,
                MENU_CREATE_GERARCHIA,
                MENU_VIEW_PROPOSTE,
                MENU_VIEW_INSIEMI_CHIUSI,
                MENU_EXIT);

        printMenuBox(CONFIGURATOR_MENU_TITLE, menuItems);
    }

    /**
     * Mostra il menu per utenti fruitori.
     * <p>
     * Pulisce lo schermo e visualizza le opzioni disponibili per gli utenti
     * standard (fruitori). Include funzionalità come visualizzazione categorie,
     * gestione proposte di scambio e altre operazioni per l'utente comune.
     * 
     * @pre true (nessuna precondizione particolare)
     * @post Lo schermo è stato pulito
     * @post Il menu fruitore è stato visualizzato con tutte le opzioni
     */
    public void showMenuFruitore() {
        clearScreen();

        List<String> menuItems = List.of(
                MENU_VIEW_CATEGORIE,
                MENU_CREA_PROPOSTA,
                MENU_VIEW_PROPOSTE_FRUITORE,
                MENU_RITIRA_PROPOSTA,
                MENU_EXIT);

        printMenuBox(USER_MENU_TITLE, menuItems);
    }

    /**
     * Visualizza il logo ASCII del programma.
     * <p>
     * Mostra un logo artistico ASCII multicolore per l'applicazione ActivityNexus.
     * Il logo utilizza codici ANSI per la colorazione e appare come un banner
     * decorativo all'inizio dell'applicazione.
     *
     * @post Il logo ASCII è stato visualizzato sulla console
     */
    private void displayAsciiLogo() {
        String[] logoLines = {
                BLUE + TOP_LEFT_CORNER + HORIZONTAL_BORDER.repeat(65) + TOP_RIGHT_CORNER + RESET,
                BLUE + VERTICAL_BORDER + CYAN + "     █████     ██████  ████████ ██ ██    ██ ██ ████████ ██    ██ "
                        + BLUE + VERTICAL_BORDER + RESET,
                BLUE + VERTICAL_BORDER + CYAN + "    ██   ██    ██         ██    ██ ██    ██ ██    ██     ██  ██  "
                        + BLUE + VERTICAL_BORDER + RESET,
                BLUE + VERTICAL_BORDER + CYAN + "   █████████   ██         ██    ██ ██    ██ ██    ██      ████   "
                        + BLUE + VERTICAL_BORDER + RESET,
                BLUE + VERTICAL_BORDER + CYAN + "  ██       ██  ██         ██    ██  ██  ██  ██    ██       ██    "
                        + BLUE + VERTICAL_BORDER + RESET,
                BLUE + VERTICAL_BORDER + CYAN + "  ██       ██  ██████     ██    ██   ████   ██    ██       ██    "
                        + BLUE + VERTICAL_BORDER + RESET,
                BLUE + VERTICAL_BORDER + MAGENTA + "                                                                 "
                        + BLUE + VERTICAL_BORDER + RESET,
                BLUE + VERTICAL_BORDER + MAGENTA + "           ███    ██ ███████ ██   ██ ██    ██ ███████            "
                        + BLUE + VERTICAL_BORDER + RESET,
                BLUE + VERTICAL_BORDER + MAGENTA + "           ████   ██ ██       ██ ██  ██    ██ ██                 "
                        + BLUE + VERTICAL_BORDER + RESET,
                BLUE + VERTICAL_BORDER + MAGENTA + "           ██ ██  ██ █████     ███   ██    ██ ███████            "
                        + BLUE + VERTICAL_BORDER + RESET,
                BLUE + VERTICAL_BORDER + MAGENTA + "           ██  ██ ██ ██       ██ ██  ██    ██      ██            "
                        + BLUE + VERTICAL_BORDER + RESET,
                BLUE + VERTICAL_BORDER + MAGENTA + "           ██   ████ ███████ ██   ██  ██████  ███████            "
                        + BLUE + VERTICAL_BORDER + RESET,
                BLUE + BOTTOM_LEFT_CORNER + HORIZONTAL_BORDER.repeat(65) + BOTTOM_RIGHT_CORNER + RESET
        };

        for (String line : logoLines) {
            System.out.println(line);
        }
        System.out.println();
    }

    // -----------------------------------------------------------------------------------
    // METODI DI SUPPORTO PER LA FORMATTAZIONE
    // -----------------------------------------------------------------------------------

    /**
     * Crea un riquadro con titolo e voci di menu formattate.
     * <p>
     * Genera un riquadro decorativo contenente un titolo e una serie di voci di
     * menu.
     * Il riquadro si adatta automaticamente alla larghezza del contenuto più largo
     * e utilizza caratteri speciali e colori per creare un'interfaccia visivamente
     * attraente.
     * <p>
     * Supporta separatori speciali attraverso l'uso del valore speciale
     * "--separator--"
     * che viene visualizzato come una linea orizzontale all'interno del menu.
     *
     * @param titolo   titolo del menu
     * @param vociMenu lista delle voci del menu
     * 
     * @pre true (titolo e vociMenu possono essere null)
     * @post Viene visualizzato un riquadro contenente titolo e voci di menu
     * @post Se titolo == null, viene utilizzata una stringa vuota
     * @post Se vociMenu == null, viene utilizzata una lista vuota
     * @post Il prompt per l'inserimento della scelta è visualizzato dopo il
     *       riquadro
     */
    private void printMenuBox(String titolo, List<String> vociMenu) {
        // Gestione di input null o vuoto
        if (titolo == null)
            titolo = "";
        if (vociMenu == null)
            vociMenu = new ArrayList<>();

        // Calcola la larghezza necessaria e aggiungi margine
        int larghezzaRiquadro = calcolaLarghezzaRiquadro(titolo, vociMenu);
        larghezzaRiquadro += 6; // Margine extra per migliorare l'estetica

        // Stampa il bordo superiore decorato
        String bordoSuperiore = BLUE + TOP_LEFT_CORNER + HORIZONTAL_BORDER.repeat(larghezzaRiquadro - 2)
                + TOP_RIGHT_CORNER + RESET;
        System.out.println(bordoSuperiore);

        // Stampa il titolo se presente, altrimenti salta
        if (!titolo.isEmpty()) {
            System.out.println(centraTesto(titolo, larghezzaRiquadro));

            // Separatore dopo il titolo con design speciale
            System.out.println(
                    BLUE + LEFT_SEPARATOR + HORIZONTAL_BORDER.repeat(larghezzaRiquadro - 2) + RIGHT_SEPARATOR + RESET);
        }

        // Stampa le voci del menu
        if (!vociMenu.isEmpty()) {
            // Aggiungi spazio vuoto prima delle voci del menu per migliorare la leggibilità
            System.out.println(BLUE + VERTICAL_BORDER + RESET + " ".repeat(larghezzaRiquadro - 2) + BLUE
                    + VERTICAL_BORDER + RESET);

            // Stampa ogni voce del menu
            for (String voce : vociMenu) {
                // Gestisce i separatori speciali (elementi con valore "--separator--")
                if ("--separator--".equals(rimuoviANSI(voce))) {
                    System.out.println(BLUE + "╟" + "─".repeat(larghezzaRiquadro - 2) + "╢" + RESET);
                } else {
                    System.out.println(formattaVoceMenu(voce, larghezzaRiquadro));
                }
            }

            // Aggiungi spazio vuoto dopo le voci del menu per migliorare la leggibilità
            System.out.println(BLUE + VERTICAL_BORDER + RESET + " ".repeat(larghezzaRiquadro - 2) + BLUE
                    + VERTICAL_BORDER + RESET);
        }

        // Stampa il bordo inferiore decorato
        String bordoInferiore = BLUE + BOTTOM_LEFT_CORNER + HORIZONTAL_BORDER.repeat(larghezzaRiquadro - 2)
                + BOTTOM_RIGHT_CORNER + RESET;
        System.out.println(bordoInferiore);

        // Prompt per l'input
        System.out.print("\n" + MENU_CHOICE_PROMPT);
    }

    /**
     * Calcola la larghezza necessaria per il riquadro del menu.
     * <p>
     * Determina la larghezza ottimale del riquadro in base alla lunghezza
     * del titolo e delle voci di menu, considerando solo i caratteri visibili
     * (escludendo i codici ANSI di formattazione).
     *
     * @param titolo   il titolo del menu
     * @param vociMenu la lista delle voci del menu
     * @return la larghezza ottimale per il riquadro
     * 
     * @pre true (titolo e vociMenu possono essere null)
     * @post result >= 0
     * @post result è almeno pari alla lunghezza visibile del testo più lungo tra
     *       titolo e voci
     */
    private int calcolaLarghezzaRiquadro(String titolo, List<String> vociMenu) {
        // Inizializza con lunghezza 0
        int larghezzaMassima = 0;

        // Considera la lunghezza del titolo, se non è null
        if (titolo != null) {
            larghezzaMassima = rimuoviANSI(titolo).length();
        }

        // Trova la voce di menu più lunga, solo se la lista non è null
        if (vociMenu != null && !vociMenu.isEmpty()) {
            int larghezzaMassimaVoci = vociMenu.stream()
                    .filter(Objects::nonNull) // Filtra eventuali voci null
                    .mapToInt(voce -> rimuoviANSI(voce).length())
                    .max()
                    .orElse(0);

            // Aggiorna la larghezza massima se necessario
            larghezzaMassima = Math.max(larghezzaMassima, larghezzaMassimaVoci);
        }

        // Aggiungi margine extra per migliorare la leggibilità
        return larghezzaMassima + 8;
    }

    /**
     * Centra il testo all'interno della larghezza specificata.
     * <p>
     * Aggiunge spazi prima e dopo il testo per centrarlo all'interno
     * di una riga di lunghezza specificata, considerando solo i caratteri
     * visibili (escludendo i codici ANSI).
     *
     * @param testo     il testo da centrare
     * @param larghezza la larghezza totale della riga
     * @return il testo centrato con bordi laterali
     * 
     * @pre testo != null
     * @pre larghezza >= rimuoviANSI(testo).length() + 2 (per i bordi)
     * @post result != null
     * @post result inizia con il carattere di bordo sinistro e termina con il bordo
     *       destro
     * @post result contiene il testo originale centrato
     * @post la lunghezza visibile di result (senza codici ANSI) è esattamente
     *       larghezza
     */
    private String centraTesto(String testo, int larghezza) {
        // Calcola la lunghezza del testo senza i codici di formattazione ANSI
        String testoSenzaANSI = rimuoviANSI(testo);

        // Calcola il numero totale di spazi da aggiungere
        int spazi = larghezza - testoSenzaANSI.length() - 2; // -2 per i bordi

        // Distribuisci gli spazi a sinistra e a destra
        int spaziSinistra = spazi / 2;
        int spaziDestra = spazi - spaziSinistra;

        // Costruisci la riga formattata con il testo centrato
        return BLUE + VERTICAL_BORDER + RESET + " ".repeat(spaziSinistra) +
                testo + " ".repeat(spaziDestra) + BLUE + VERTICAL_BORDER + RESET;
    }

    /**
     * Formatta una voce del menu all'interno del riquadro.
     * <p>
     * Aggiunge caratteri decorativi prima della voce e riempie con spazi
     * fino alla larghezza specificata, per creare un effetto visivo uniforme.
     * Il testo viene allineato a sinistra con un piccolo margine.
     *
     * @param voce      la voce del menu da formattare
     * @param larghezza la larghezza totale della riga
     * @return la voce del menu formattata
     * 
     * @pre voce != null
     * @pre larghezza >= rimuoviANSI(voce).length() + 4 (per margini e bordi)
     * @post result != null
     * @post result inizia con il bordo sinistro seguito da spazi e termina con il
     *       bordo destro
     * @post la lunghezza visibile di result (senza codici ANSI) è esattamente
     *       larghezza
     */
    private String formattaVoceMenu(String voce, int larghezza) {
        // Calcola la lunghezza visibile della voce (senza codici ANSI)
        String voceSenzaANSI = rimuoviANSI(voce);

        // Calcola quanti spazi aggiungere a destra per riempire la riga
        int spaziDestra = larghezza - voceSenzaANSI.length() - 4; // -4 per bordo e margine (2 spazi)

        // Formatta la voce del menu con margine a sinistra e allineamento a destra
        return BLUE + VERTICAL_BORDER + "  " + RESET + voce + " ".repeat(spaziDestra) + BLUE + VERTICAL_BORDER + RESET;
    }

    /**
     * Rimuove i codici ANSI da una stringa.
     * <p>
     * Utilizza espressioni regolari per rimuovere tutti i codici di controllo ANSI
     * da una stringa, restituendo solo il testo visibile. Utile per calcolare
     * correttamente la lunghezza effettiva di un testo formattato con colori
     * o altri stili.
     *
     * @param testo la stringa che può contenere codici ANSI
     * @return la stringa con tutti i codici ANSI rimossi
     * 
     * @pre true (testo può essere null)
     * @post se testo == null, result è "null"
     * @post se testo != null, result contiene testo senza codici ANSI
     * @post result.length() <= testo.length() se testo contiene codici ANSI
     * @post result.length() == testo.length() se testo non contiene codici ANSI
     */
    private String rimuoviANSI(String testo) {
        // Gestione del caso null
        if (testo == null) {
            return "null";
        }

        // Espressione regolare che identifica i codici di controllo ANSI
        // Questa regex corrisponde a sequenze che iniziano con ESC [ e terminano con m
        return testo.replaceAll("\033\\[[;\\d]*m", "");
    }

    // -----------------------------------------------------------------------------------
    // METODI PER LA GESTIONE DEL CICLO DI VITA
    // -----------------------------------------------------------------------------------

    /**
     * Chiude lo scanner quando l'applicazione termina.
     * <p>
     * Rilascia le risorse associate allo Scanner utilizzato per l'input
     * dell'utente.
     * Questo metodo dovrebbe essere chiamato quando l'applicazione termina per
     * evitare
     * memory leak, specialmente se si utilizza System.in.
     * <p>
     * <b>Importante:</b> Dopo aver chiamato questo metodo, tutti i tentativi di
     * utilizzo
     * dei metodi di input di questa classe (getInput, getIntInput, ecc.)
     * genereranno
     * eccezioni perché lo scanner sarà chiuso.
     *
     * @post Lo scanner è chiuso e non può più essere utilizzato
     * @post System.in potrebbe essere chiuso se questo è l'unico scanner che lo
     *       utilizzava
     */
    public void close() {
        scanner.close();
    }

    public void displayShrek() {
        // Definizione dell'ASCII art di Shrek
        String[] shrekArt = {
                "⢀⡴⠑⡄⠀⠀⠀⠀⠀⠀⠀⣀⣀⣤⣤⣤⣀⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
                "⠸⡇⠀⠿⡀⠀⠀⠀⣀⡴⢿⣿⣿⣿⣿⣿⣿⣿⣷⣦⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
                "⠀⠀⠀⠀⠑⢄⣠⠾⠁⣀⣄⡈⠙⣿⣿⣿⣿⣿⣿⣿⣿⣆⠀⠀⠀⠀⠀⠀⠀⠀",
                "⠀⠀⠀⠀⢀⡀⠁⠀⠀⠈⠙⠛⠂⠈⣿⣿⣿⣿⣿⠿⡿⢿⣆⠀⠀⠀⠀⠀⠀⠀",
                "⠀⠀⠀⢀⡾⣁⣀⠀⠴⠂⠙⣗⡀⠀⢻⣿⣿⠭⢤⣴⣦⣤⣹⠀⠀⠀⢀⢴⣶⣆",
                "⠀⠀⢀⣾⣿⣿⣿⣷⣮⣽⣾⣿⣥⣴⣿⣿⡿⢂⠔⢚⡿⢿⣿⣦⣴⣾⠁⠸⣼⡿",
                "⠀⢀⡞⠁⠙⠻⠿⠟⠉⠀⠛⢹⣿⣿⣿⣿⣿⣌⢤⣼⣿⣾⣿⡟⠉⠀⠀⠀⠀⠀",
                "⠀⣾⣷⣶⠇⠀⠀⣤⣄⣀⡀⠈⠻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡇⠀⠀⠀⠀⠀⠀",
                "⠀⠉⠈⠉⠀⠀⢦⡈⢻⣿⣿⣿⣶⣶⣶⣶⣤⣽⡹⣿⣿⣿⣿⡇⠀⠀⠀⠀⠀⠀",
                "⠀⠀⠀⠀⠀⠀⠀⠉⠲⣽⡻⢿⣿⣿⣿⣿⣿⣿⣷⣜⣿⣿⣿⡇⠀⠀⠀⠀⠀⠀",
                "⠀⠀⠀⠀⠀⠀⠀⠀⢸⣿⣿⣷⣶⣮⣭⣽⣿⣿⣿⣿⣿⣿⣿⠀⠀⠀⠀⠀⠀⠀",
                "⠀⠀⠀⠀⠀⠀⣀⣀⣈⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⠇⠀⠀⠀⠀⠀⠀⠀",
                "⠀⠀⠀⠀⠀⠀⢿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⠃⠀⠀⠀⠀⠀⠀⠀⠀",
                "⠀⠀⠀⠀⠀⠀⠀⠹⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡿⠟⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀",
                "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠛⠻⠿⠿⠿⠿⠛⠉"
        };

        // Opzionale: aggiungi colore verde caratteristico di Shrek
        for (String line : shrekArt) {
            System.out.println(GREEN + line + RESET);
        }
    }

    public void displayAmongUs() {
        // Definizione dell'ASCII art di Among Us
        String[] amongUsArt = {
                "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣤⣤⣤⣤⣤⣶⣦⣤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀",
                "⠀⠀⠀⠀⠀⠀⠀⠀⢀⣴⣿⡿⠛⠉⠙⠛⠛⠛⠛⠻⢿⣿⣷⣤⡀⠀⠀⠀⠀⠀",
                "⠀⠀⠀⠀⠀⠀⠀⠀⣼⣿⠋⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⠈⢻⣿⣿⡄⠀⠀⠀⠀",
                "⠀⠀⠀⠀⠀⠀⠀⣸⣿⡏⠀⠀⠀⣠⣶⣾⣿⣿⣿⠿⠿⠿⢿⣿⣿⣿⣄⠀⠀⠀",
                "⠀⠀⠀⠀⠀⠀⠀⣿⣿⠁⠀⠀⢰⣿⣿⣯⠁⠀⠀⠀⠀⠀⠀⠀⠈⠙⢿⣷⡄⠀",
                "⠀⠀⣀⣤⣴⣶⣶⣿⡟⠀⠀⠀⢸⣿⣿⣿⣆⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣿⣷⠀",
                "⠀⢰⣿⡟⠋⠉⣹⣿⡇⠀⠀⠀⠘⣿⣿⣿⣿⣷⣦⣤⣤⣤⣶⣶⣶⣶⣿⣿⣿⠀",
                "⠀⢸⣿⡇⠀⠀⣿⣿⡇⠀⠀⠀⠀⠹⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡿⠃⠀",
                "⠀⣸⣿⡇⠀⠀⣿⣿⡇⠀⠀⠀⠀⠀⠉⠻⠿⣿⣿⣿⣿⡿⠿⠿⠛⢻⣿⡇⠀⠀",
                "⠀⣿⣿⠁⠀⠀⣿⣿⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⣿⣧⠀⠀",
                "⠀⣿⣿⠀⠀⠀⣿⣿⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⣿⣿⠀⠀",
                "⠀⣿⣿⠀⠀⠀⣿⣿⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⣿⣿⠀⠀",
                "⠀⢿⣿⡆⠀⠀⣿⣿⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⣿⡇⠀⠀",
                "⠀⠸⣿⣧⡀⠀⣿⣿⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣿⣿⠃⠀⠀",
                "⠀⠀⠛⢿⣿⣿⣿⣿⣇⠀⠀⠀⠀⠀⣰⣿⣿⣷⣶⣶⣶⣶⠶⠀⢠⣿⣿⠀⠀⠀",
                "⠀⠀⠀⠀⠀⠀⠀⣿⣿⠀⠀⠀⠀⠀⣿⣿⡇⠀⣽⣿⡏⠁⠀⠀⢸⣿⡇⠀⠀⠀",
                "⠀⠀⠀⠀⠀⠀⠀⣿⣿⠀⠀⠀⠀⠀⣿⣿⡇⠀⢹⣿⡆⠀⠀⠀⣸⣿⠇⠀⠀⠀",
                "⠀⠀⠀⠀⠀⠀⠀⢿⣿⣦⣄⣀⣠⣴⣿⣿⠁⠀⠈⠻⣿⣿⣿⣿⡿⠏⠀⠀⠀⠀",
                "⠀⠀⠀⠀⠀⠀⠀⠈⠛⠻⠿⠿⠿⠿⠋⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀"
        };

        // Colora l'Among Us in rosso, il colore più iconico del gioco
        for (String line : amongUsArt) {
            System.out.println(RED + line + RESET);
        }

        // Aggiungi una riga vuota dopo l'ASCII art per migliorare la leggibilità
        System.out.println();
    }
}