package view;

import java.util.*;
import java.io.IOException;

/**
 * Classe che rappresenta l'interfaccia utente a console del sistema.
 * <p>
 * Fornisce metodi avanzati per visualizzare messaggi, menu interattivi, tabelle
 * e acquisire input dall'utente. Include funzionalità per la formattazione
 * del testo con colori ANSI, animazioni e layout strutturati.
 * <p>
 * Questa implementazione è progettata per migliorare l'esperienza utente
 * su terminali che supportano i codici di escape ANSI.
 *
 * @version 2.0
 */
public class ConsoleView {
    // -----------------------------------------------------------------------------------
    // ATTRIBUTI
    // -----------------------------------------------------------------------------------

    /** Scanner per acquisire input dall'utente */
    private final Scanner scanner = new Scanner(System.in);

    /** Flag che indica se il terminale supporta i codici ANSI */
    private final boolean supportsAnsi = true; // Per Windows moderni e sistemi Unix

    // -----------------------------------------------------------------------------------
    // COSTANTI PER LA FORMATTAZIONE
    // -----------------------------------------------------------------------------------

    // Codici di reset
    /** Codice ANSI per ripristinare tutti gli attributi */
    private static final String RESET = "\033[0m";

    // Stili di testo
    /** Codice ANSI per testo in grassetto */
    private static final String BOLD = "\033[1m";
    /** Codice ANSI per testo sottolineato */
    private static final String UNDERLINE = "\033[4m";
    /** Codice ANSI per testo lampeggiante */
    private static final String BLINK = "\033[5m";
    /** Codice ANSI per testo invertito (sfondo/colore testo scambiati) */
    private static final String REVERSE = "\033[7m";

    // Colori testo
    /** Codice ANSI per il colore nero */
    private static final String BLACK = "\033[30m";
    /** Codice ANSI per il colore rosso */
    private static final String RED = "\033[31m";
    /** Codice ANSI per il colore verde */
    private static final String GREEN = "\033[32m";
    /** Codice ANSI per il colore giallo */
    private static final String YELLOW = "\033[33m";
    /** Codice ANSI per il colore blu */
    private static final String BLUE = "\033[34m";
    /** Codice ANSI per il colore magenta */
    private static final String MAGENTA = "\033[35m";
    /** Codice ANSI per il colore ciano */
    private static final String CYAN = "\033[36m";
    /** Codice ANSI per il colore bianco */
    private static final String WHITE = "\033[37m";

    // Colori sfondo
    /** Codice ANSI per sfondo nero */
    private static final String BG_BLACK = "\033[40m";
    /** Codice ANSI per sfondo rosso */
    private static final String BG_RED = "\033[41m";
    /** Codice ANSI per sfondo verde */
    private static final String BG_GREEN = "\033[42m";
    /** Codice ANSI per sfondo giallo */
    private static final String BG_YELLOW = "\033[43m";
    /** Codice ANSI per sfondo blu */
    private static final String BG_BLUE = "\033[44m";
    /** Codice ANSI per sfondo magenta */
    private static final String BG_MAGENTA = "\033[45m";
    /** Codice ANSI per sfondo ciano */
    private static final String BG_CYAN = "\033[46m";
    /** Codice ANSI per sfondo bianco */
    private static final String BG_WHITE = "\033[47m";

    // -----------------------------------------------------------------------------------
    // COSTANTI PER I MESSAGGI
    // -----------------------------------------------------------------------------------

    /** Messaggio di benvenuto con stile migliorato */
    private static final String WELCOME_MESSAGE = BOLD + YELLOW + "Benvenuto in ActivityNexus!" + RESET;
    /** Titolo del menu configuratore con stile migliorato */
    private static final String CONFIGURATOR_MENU_TITLE = BOLD + YELLOW + "MENU Configuratore" + RESET;
    /** Titolo del menu fruitore con stile migliorato */
    private static final String USER_MENU_TITLE = BOLD + YELLOW + "MENU Fruitore" + RESET;

    // -----------------------------------------------------------------------------------
    // COSTANTI PER LE VOCI DEI MENU
    // -----------------------------------------------------------------------------------

    // Menu principale
    /** Voce del menu per il login con stile migliorato */
    private static final String MENU_LOGIN = GREEN + "1. Login" + RESET;
    /** Voce del menu per la registrazione con stile migliorato */
    private static final String MENU_REGISTRATION = CYAN + "2. Registrazione" + RESET;
    /** Voce del menu per l'uscita con stile migliorato */
    private static final String MENU_EXIT = RED + "0. Uscita" + RESET;

    // Menu configuratore
    /** Voce del menu per visualizzare i comprensori con stile migliorato */
    private static final String MENU_VIEW_COMPRENSORI = GREEN + "1. Visualizza Comprensori" + RESET;
    /** Voce del menu per visualizzare le gerarchie con stile migliorato */
    private static final String MENU_VIEW_GERARCHIE = GREEN + "2. Visualizza Gerarchie" + RESET;
    /**
     * Voce del menu per visualizzare i fattori di conversione con stile migliorato
     */
    private static final String MENU_VIEW_CONVERSION_FACTORS = GREEN + "3. Visualizza Fattori di Conversione" + RESET;
    /** Voce del menu per creare un comprensorio con stile migliorato */
    private static final String MENU_CREATE_COMPRENSORIO = CYAN + "4. Crea Comprensorio" + RESET;
    /** Voce del menu per creare una gerarchia con stile migliorato */
    private static final String MENU_CREATE_GERARCHIA = CYAN + "5. Crea Gerarchia" + RESET;
    /** Voce del menu per definire fattori di conversione con stile migliorato */
    private static final String MENU_DEFINE_CONVERSIONS = CYAN + "6. Definisci Fattori di Conversione" + RESET;

    // -----------------------------------------------------------------------------------
    // METODI PER L'INTERAZIONE BASE
    // -----------------------------------------------------------------------------------

    /**
     * Mostra un messaggio sulla console.
     *
     * @param messaggio il messaggio da visualizzare
     */
    public void showMessage(String messaggio) {
        System.out.println(messaggio);
    }

    /**
     * Mostra un messaggio di errore sulla console (in rosso).
     * <p>
     * Il messaggio viene evidenziato con un prefisso di errore
     * e formattazione che lo rende facilmente riconoscibile.
     *
     * @param messaggio il messaggio di errore da visualizzare
     */
    public void showErrorMessage(String messaggio) {
        System.out.println(BG_RED + WHITE + " ERRORE " + RESET + " " + RED + messaggio + RESET);
    }

    /**
     * Mostra un messaggio di successo sulla console (in verde).
     * <p>
     * Il messaggio viene evidenziato con un prefisso di successo
     * e formattazione che lo rende facilmente riconoscibile.
     *
     * @param messaggio il messaggio di successo da visualizzare
     */
    public void showSuccessMessage(String messaggio) {
        System.out.println(BG_GREEN + BLACK + " SUCCESSO " + RESET + " " + GREEN + messaggio + RESET);
    }

    /**
     * Mostra un messaggio informativo sulla console (in blu).
     * <p>
     * Il messaggio viene evidenziato con un prefisso informativo
     * e formattazione che lo rende facilmente riconoscibile.
     *
     * @param messaggio il messaggio informativo da visualizzare
     */
    public void showInfoMessage(String messaggio) {
        System.out.println(BG_BLUE + WHITE + " INFO " + RESET + " " + BLUE + messaggio + RESET);
    }

    /**
     * Mostra un messaggio di avviso sulla console (in giallo).
     * <p>
     * Il messaggio viene evidenziato con un prefisso di avviso
     * e formattazione che lo rende facilmente riconoscibile.
     *
     * @param messaggio il messaggio di avviso da visualizzare
     */
    public void showWarningMessage(String messaggio) {
        System.out.println(BG_YELLOW + BLACK + " AVVISO " + RESET + " " + YELLOW + messaggio + RESET);
    }

    /**
     * Ottiene un input testuale dall'utente dopo aver mostrato un prompt.
     * <p>
     * Il prompt viene evidenziato per distinguerlo dal resto dell'output.
     *
     * @param prompt il messaggio da mostrare all'utente per richiedere l'input
     * @return l'input dell'utente con spazi iniziali e finali rimossi
     */
    public String getInput(String prompt) {
        System.out.print(CYAN + prompt + RESET + " ");
        return scanner.nextLine().trim();
    }

    /**
     * Ottiene una password dall'utente nascondendo i caratteri digitati.
     * <p>
     * Nota: questa implementazione base non nasconde realmente i caratteri
     * poiché Java console standard non supporta questa funzionalità.
     * In un'implementazione reale si potrebbe usare System.console().readPassword()
     *
     * @param prompt il messaggio da mostrare all'utente per richiedere la password
     * @return la password inserita dall'utente
     */
    public String getPasswordInput(String prompt) {
        System.out.print(MAGENTA + prompt + RESET + " ");
        // In una console standard non possiamo mascherare l'input
        return scanner.nextLine();
    }

    /**
     * Ottiene un input numerico dall'utente con validazione del range.
     * <p>
     * Richiede ripetutamente l'input finché non viene inserito un numero valido
     * all'interno del range specificato.
     *
     * @param prompt il messaggio da mostrare all'utente
     * @param min    il valore minimo accettabile
     * @param max    il valore massimo accettabile
     * @return l'input numerico dell'utente entro i limiti specificati
     */
    public int getIntInput(String prompt, int min, int max) {
        while (true) {
            try {
                String input = getInput(prompt + " [" + min + "-" + max + "]:");
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                } else {
                    showErrorMessage("Inserire un valore compreso tra " + min + " e " + max);
                }
            } catch (NumberFormatException e) {
                showErrorMessage("Inserire un numero valido");
            }
        }
    }

    /**
     * Richiede una conferma all'utente (si/no).
     * <p>
     * Accetta varie forme di risposta affermativa (s, si, y, yes)
     * o negativa (n, no) indipendentemente dal case.
     *
     * @param prompt il messaggio da mostrare per richiedere la conferma
     * @return true se l'utente conferma, false altrimenti
     */
    public boolean getConfirmation(String prompt) {
        while (true) {
            String input = getInput(prompt + " (s/n):").toLowerCase();
            if (input.equals("s") || input.equals("si") || input.equals("y") || input.equals("yes")) {
                return true;
            } else if (input.equals("n") || input.equals("no")) {
                return false;
            } else {
                showErrorMessage("Risposta non valida. Utilizzare 's' per confermare o 'n' per annullare.");
            }
        }
    }

    /**
     * Chiede all'utente di premere un tasto per continuare.
     * <p>
     * Mostra un messaggio formattato e attende che l'utente prema INVIO.
     */
    public void pressEnterToContinue() {
        System.out.print(YELLOW + "▶ Premere INVIO per continuare..." + RESET);
        scanner.nextLine();
    }

    /**
     * Pulisce lo schermo del terminale.
     * <p>
     * Utilizza il codice ANSI per pulire lo schermo e riposizionare il cursore,
     * oppure stampa righe vuote come fallback.
     */
    public void clearScreen() {
        if (supportsAnsi) {
            System.out.print("\033[H\033[2J"); // Codice ANSI per pulire lo schermo
            System.out.flush();
        } else {
            // Fallback per terminali che non supportano ANSI
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }

    // -----------------------------------------------------------------------------------
    // METODI PER LA VISUALIZZAZIONE DEI MENU
    // -----------------------------------------------------------------------------------

    /**
     * Mostra il menu principale sulla console con logo ASCII art.
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
     * Mostra il menu del configuratore sulla console.
     */
    public void showMenuConfiguratore() {
        clearScreen();

        List<String> menuItems = List.of(
                MENU_VIEW_COMPRENSORI,
                MENU_VIEW_GERARCHIE,
                MENU_VIEW_CONVERSION_FACTORS,
                MENU_CREATE_COMPRENSORIO,
                MENU_CREATE_GERARCHIA,
                MENU_DEFINE_CONVERSIONS,
                MENU_EXIT);

        printMenuBox(CONFIGURATOR_MENU_TITLE, menuItems);
    }

    /**
     * Mostra il menu del fruitore sulla console.
     */
    public void showMenuFruitore() {
        clearScreen();

        List<String> menuItems = List.of(
                GREEN + "1. Visualizza Attività" + RESET,
                GREEN + "2. Registra Nuova Attività" + RESET,
                CYAN + "3. Visualizza Statistiche" + RESET,
                YELLOW + "9. Logout" + RESET,
                RED + "0. Uscita" + RESET);

        printMenuBox(USER_MENU_TITLE, menuItems);
    }

    /**
     * Visualizza il logo ASCII del programma.
     */
    private void displayAsciiLogo() {
        String[] logoLines = {
                BLUE + "╔═════════════════════════════════════════════════════════════════╗" + RESET,
                BLUE + "║" + CYAN + "     █████     ██████  ████████ ██ ██    ██ ██ ████████ ██    ██ " + BLUE + "║"
                        + RESET,
                BLUE + "║" + CYAN + "    ██   ██    ██         ██    ██ ██    ██ ██    ██     ██  ██  " + BLUE + "║"
                        + RESET,
                BLUE + "║" + CYAN + "   █████████   ██         ██    ██ ██    ██ ██    ██      ████   " + BLUE + "║"
                        + RESET,
                BLUE + "║" + CYAN + "  ██       ██  ██         ██    ██  ██  ██  ██    ██       ██    " + BLUE + "║"
                        + RESET,
                BLUE + "║" + CYAN + "  ██       ██  ██████     ██    ██   ████   ██    ██       ██    " + BLUE + "║"
                        + RESET,
                BLUE + "║" + MAGENTA + "                                                                 " + BLUE + "║"
                        + RESET,
                BLUE + "║" + MAGENTA + "           ███    ██ ███████ ██   ██ ██    ██ ███████            " + BLUE + "║"
                        + RESET,
                BLUE + "║" + MAGENTA + "           ████   ██ ██       ██ ██  ██    ██ ██                 " + BLUE + "║"
                        + RESET,
                BLUE + "║" + MAGENTA + "           ██ ██  ██ █████     ███   ██    ██ ███████            " + BLUE + "║"
                        + RESET,
                BLUE + "║" + MAGENTA + "           ██  ██ ██ ██       ██ ██  ██    ██      ██            " + BLUE + "║"
                        + RESET,
                BLUE + "║" + MAGENTA + "           ██   ████ ███████ ██   ██  ██████  ███████            " + BLUE + "║" + RESET,
                BLUE + "╚═════════════════════════════════════════════════════════════════╝" + RESET
        };

        for (String line : logoLines) {
            System.out.println(line);
        }
        System.out.println();
    }

    // -----------------------------------------------------------------------------------
    // METODI PER LA VISUALIZZAZIONE TABELLARE
    // -----------------------------------------------------------------------------------

    /**
     * Visualizza dati in formato tabellare.
     * <p>
     * Crea una tabella formattata con intestazioni di colonna e righe di dati,
     * con bordi e allineamento appropriato.
     *
     * @param headers le intestazioni delle colonne
     * @param data    i dati da visualizzare (lista di righe, ogni riga è una lista
     *                di celle)
     */
    public void displayTable(List<String> headers, List<List<String>> data) {
        if (headers == null || data == null)
            return;

        // Calcola le larghezze massime per ogni colonna
        List<Integer> columnWidths = new ArrayList<>(headers.size());
        for (int i = 0; i < headers.size(); i++) {
            int maxWidth = headers.get(i).length();
            for (List<String> row : data) {
                if (i < row.size()) {
                    maxWidth = Math.max(maxWidth, row.get(i).length());
                }
            }
            columnWidths.add(maxWidth + 2); // +2 per il padding
        }

        // Stampa l'intestazione
        printTableSeparator(columnWidths);
        printTableRow(headers, columnWidths, true);
        printTableSeparator(columnWidths);

        // Stampa i dati
        for (List<String> row : data) {
            printTableRow(row, columnWidths, false);
        }

        // Stampa il separatore finale
        printTableSeparator(columnWidths);
    }

    /**
     * Stampa un separatore orizzontale per la tabella.
     *
     * @param columnWidths le larghezze delle colonne
     */
    private void printTableSeparator(List<Integer> columnWidths) {
        System.out.print(BLUE + "+" + RESET);
        for (Integer width : columnWidths) {
            System.out.print(BLUE + "-".repeat(width) + "+" + RESET);
        }
        System.out.println();
    }

    /**
     * Stampa una riga della tabella.
     *
     * @param cells        i contenuti delle celle
     * @param columnWidths le larghezze delle colonne
     * @param isHeader     true se è una riga di intestazione
     */
    private void printTableRow(List<String> cells, List<Integer> columnWidths, boolean isHeader) {
        System.out.print(BLUE + "|" + RESET);
        for (int i = 0; i < columnWidths.size(); i++) {
            String cell = i < cells.size() ? cells.get(i) : "";
            String formattedCell = formatTableCell(cell, columnWidths.get(i), isHeader);
            System.out.print(formattedCell + BLUE + "|" + RESET);
        }
        System.out.println();
    }

    /**
     * Formatta una cella della tabella con padding e colorazione.
     *
     * @param content  il contenuto della cella
     * @param width    la larghezza della colonna
     * @param isHeader true se è una cella di intestazione
     * @return la cella formattata
     */
    private String formatTableCell(String content, int width, boolean isHeader) {
        // Rimuovi eventuali codici ANSI per calcolare la lunghezza effettiva
        String plainContent = rimuoviANSI(content);
        int paddingSize = width - plainContent.length();
        int leftPadding = paddingSize / 2;
        int rightPadding = paddingSize - leftPadding;

        String padding = " ".repeat(leftPadding);
        String rightPad = " ".repeat(rightPadding);

        if (isHeader) {
            return CYAN + BOLD + padding + content + rightPad + RESET;
        } else {
            return padding + content + rightPad;
        }
    }

    /**
     * Visualizza un indicatore di progresso per operazioni lunghe.
     * <p>
     * Mostra una barra di progresso animata mentre si attende il completamento
     * di un'operazione.
     *
     * @param message    il messaggio da mostrare
     * @param iterations il numero di iterazioni totali
     * @param sleepMs    i millisecondi di attesa tra le iterazioni (simulazione)
     */
    public void showProgressBar(String message, int iterations, int sleepMs) {
        System.out.print(message + " ");
        int barWidth = 20;

        for (int i = 0; i <= iterations; i++) {
            int progress = (int) ((double) i / iterations * barWidth);

            System.out.print("\r" + message + " [");
            for (int j = 0; j < barWidth; j++) {
                if (j < progress) {
                    System.out.print(GREEN + "█" + RESET);
                } else {
                    System.out.print(" ");
                }
            }
            System.out.print("] " + (i * 100 / iterations) + "%");

            try {
                Thread.sleep(sleepMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("\n" + GREEN + "Completato!" + RESET);
    }

    // -----------------------------------------------------------------------------------
    // METODI DI SUPPORTO PER LA FORMATTAZIONE
    // -----------------------------------------------------------------------------------

    /**
     * Stampa un riquadro contenente un menu con titolo e voci.
     * <p>
     * Crea un riquadro colorato e formattato con bordi, titolo centrato,
     * voci di menu allineate e supporta separatori tra gruppi di voci.
     * <p>
     * Il metodo gestisce automaticamente l'adattamento della larghezza del riquadro
     * in base alla lunghezza del titolo e delle voci del menu, garantendo un layout
     * consistente e professionale.
     *
     * @param titolo   il titolo del riquadro (può contenere codici di formattazione
     *                 ANSI)
     * @param vociMenu la lista delle voci del menu (può essere vuota)
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
        String bordoSuperiore = BLUE + "╔" + "═".repeat(larghezzaRiquadro - 2) + "╗" + RESET;
        System.out.println(bordoSuperiore);

        // Stampa il titolo se presente, altrimenti salta
        if (!titolo.isEmpty()) {
            System.out.println(centraTesto(titolo, larghezzaRiquadro));

            // Separatore dopo il titolo con design speciale
            System.out.println(BLUE + "╠" + "═".repeat(larghezzaRiquadro - 2) + "╣" + RESET);
        }

        // Stampa le voci del menu
        if (!vociMenu.isEmpty()) {
            // Aggiungi spazio vuoto prima delle voci del menu per migliorare la leggibilità
            System.out.println(BLUE + "║" + RESET + " ".repeat(larghezzaRiquadro - 2) + BLUE + "║" + RESET);

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
            System.out.println(BLUE + "║" + RESET + " ".repeat(larghezzaRiquadro - 2) + BLUE + "║" + RESET);
        }

        // Stampa il bordo inferiore decorato
        String bordoInferiore = BLUE + "╚" + "═".repeat(larghezzaRiquadro - 2) + "╝" + RESET;
        System.out.println(bordoInferiore);

        // Prompt per l'input (opzionale, ma è un buon posto per aggiungerlo)
        System.out.print("\n" + YELLOW + "▶ " + RESET + "Inserisci la tua scelta: ");
    }

    /**
     * Calcola la larghezza necessaria per il riquadro del menu.
     *
     * @param titolo   il titolo del menu
     * @param vociMenu le voci del menu
     * @return la larghezza calcolata per il riquadro
     */
    private int calcolaLarghezzaRiquadro(String titolo, List<String> vociMenu) {
        int larghezzaMassima = Math.max(
                rimuoviANSI(titolo).length(),
                vociMenu.stream()
                        .mapToInt(voce -> rimuoviANSI(voce).length())
                        .max()
                        .orElse(0));

        // Aggiungi margine extra per migliorare la leggibilità
        return larghezzaMassima + 8;
    }

    /**
     * Stampa una linea del bordo del riquadro.
     *
     * @param larghezza la larghezza della linea
     */
    private void stampaLinea(int larghezza) {
        System.out.println(BLUE + "+" + "-".repeat(larghezza - 2) + "+" + RESET);
    }

    /**
     * Centra il testo all'interno della larghezza specificata.
     *
     * @param testo     il testo da centrare
     * @param larghezza la larghezza totale disponibile
     * @return il testo centrato con bordi
     */
    private String centraTesto(String testo, int larghezza) {
        String testoSenzaANSI = rimuoviANSI(testo);
        int spazi = larghezza - testoSenzaANSI.length() - 2;
        int spaziSinistra = spazi / 2;
        int spaziDestra = spazi - spaziSinistra;

        return BLUE + "|" + RESET + " ".repeat(spaziSinistra) +
                testo + " ".repeat(spaziDestra) + BLUE + "|" + RESET;
    }

    /**
     * Formatta una voce del menu all'interno del riquadro.
     *
     * @param voce      la voce del menu da formattare
     * @param larghezza la larghezza totale disponibile
     * @return la voce formattata con bordi
     */
    private String formattaVoceMenu(String voce, int larghezza) {
        String voceSenzaANSI = rimuoviANSI(voce);
        int spaziDestra = larghezza - voceSenzaANSI.length() - 4;

        return BLUE + "|  " + RESET + voce + " ".repeat(spaziDestra) + BLUE + "|" + RESET;
    }

    /**
     * Rimuove i codici ANSI da una stringa per calcolare correttamente la
     * lunghezza.
     *
     * @param testo il testo contenente codici ANSI
     * @return il testo senza codici ANSI
     */
    private String rimuoviANSI(String testo) {
        return testo.replaceAll("\033\\[[;\\d]*m", "");
    }

    /**
     * Chiude lo scanner quando non è più necessario.
     * Dovrebbe essere chiamato quando l'applicazione termina.
     */
    public void close() {
        scanner.close();
    }
}