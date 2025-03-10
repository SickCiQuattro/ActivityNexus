package view;

import java.util.*;

/**
 * Interfaccia utente a console con funzionalità avanzate di visualizzazione.
 * <p>
 * Fornisce metodi per gestire input/output, menu interattivi e layout
 * formattati con supporto ai colori ANSI.
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
    private static final String UNDERLINE = "\033[4m";
    private static final String BLINK = "\033[5m";
    private static final String REVERSE = "\033[7m";

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
    private static final String BG_BLACK = "\033[40m";
    private static final String BG_RED = "\033[41m";
    private static final String BG_GREEN = "\033[42m";
    private static final String BG_YELLOW = "\033[43m";
    private static final String BG_BLUE = "\033[44m";
    private static final String BG_MAGENTA = "\033[45m";
    private static final String BG_CYAN = "\033[46m";
    private static final String BG_WHITE = "\033[47m";

    // -----------------------------------------------------------------------------------
    // COSTANTI PER I MESSAGGI
    // -----------------------------------------------------------------------------------

    private static final String WELCOME_MESSAGE = BOLD + YELLOW + "Benvenuto in ActivityNexus!" + RESET;
    private static final String CONFIGURATOR_MENU_TITLE = BOLD + YELLOW + "MENU Configuratore" + RESET;
    private static final String USER_MENU_TITLE = BOLD + YELLOW + "MENU Fruitore" + RESET;

    // -----------------------------------------------------------------------------------
    // COSTANTI PER LE VOCI DEI MENU
    // -----------------------------------------------------------------------------------

    // Menu principale
    private static final String MENU_LOGIN = GREEN + "1. Login" + RESET;
    private static final String MENU_REGISTRATION = CYAN + "2. Registrazione" + RESET;
    private static final String MENU_EXIT = RED + "0. Uscita" + RESET;

    // Menu configuratore
    private static final String MENU_VIEW_COMPRENSORI = GREEN + "1. Visualizza Comprensori" + RESET;
    private static final String MENU_VIEW_GERARCHIE = GREEN + "2. Visualizza Gerarchie" + RESET;
    private static final String MENU_VIEW_CONVERSION_FACTORS = GREEN + "3. Visualizza Fattori di Conversione" + RESET;
    private static final String MENU_CREATE_COMPRENSORIO = CYAN + "4. Crea Comprensorio" + RESET;
    private static final String MENU_CREATE_GERARCHIA = CYAN + "5. Crea Gerarchia" + RESET;
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
     * Mostra un messaggio di errore con formattazione rossa.
     *
     * @param messaggio il messaggio di errore
     */
    public void showErrorMessage(String messaggio) {
        System.out.println(BG_RED + WHITE + " ERRORE " + RESET + " " + RED + messaggio + RESET);
    }

    /**
     * Mostra un messaggio di successo con formattazione verde.
     *
     * @param messaggio il messaggio di successo
     */
    public void showSuccessMessage(String messaggio) {
        System.out.println(BG_GREEN + BLACK + " SUCCESSO " + RESET + " " + GREEN + messaggio + RESET);
    }

    /**
     * Mostra un messaggio informativo con formattazione blu.
     *
     * @param messaggio il messaggio informativo
     */
    public void showInfoMessage(String messaggio) {
        System.out.println(BG_BLUE + WHITE + " INFO " + RESET + " " + BLUE + messaggio + RESET);
    }

    /**
     * Mostra un messaggio di avviso con formattazione gialla.
     *
     * @param messaggio il messaggio di avviso
     */
    public void showWarningMessage(String messaggio) {
        System.out.println(BG_YELLOW + BLACK + " AVVISO " + RESET + " " + YELLOW + messaggio + RESET);
    }

    /**
     * Ottiene input testuale dopo aver mostrato un prompt.
     *
     * @param prompt il messaggio di richiesta
     * @return l'input dell'utente con trim applicato
     */
    public String getInput(String prompt) {
        System.out.print(CYAN + prompt + RESET + " ");
        return scanner.nextLine().trim();
    }

    /**
     * Ottiene una password dall'utente (senza mascheramento).
     *
     * @param prompt il messaggio di richiesta
     * @return la password inserita
     */
    public String getPasswordInput(String prompt) {
        System.out.print(MAGENTA + prompt + RESET + " ");
        // In una console standard non possiamo mascherare l'input
        return scanner.nextLine();
    }

    /**
     * Ottiene un input numerico con validazione del range.
     *
     * @param prompt il messaggio di richiesta
     * @param min    valore minimo accettabile
     * @param max    valore massimo accettabile
     * @return il numero inserito entro il range specificato
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
     * Ottiene un input numerico con validazione del range.
     *
     * @param prompt il messaggio di richiesta
     * @param min    valore minimo accettabile
     * @param max    valore massimo accettabile
     * @return il numero inserito entro il range specificato
     */
    public float getFloatInput(String prompt, float min, float max) {
        while (true) {
            try {
                String input = getInput(prompt + " [" + min + "-" + max + "]:");
                float value = Float.parseFloat(input);
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
     * Richiede conferma (sì/no) all'utente.
     *
     * @param prompt il messaggio di richiesta
     * @return true per conferma, false per rifiuto
     */
    public boolean getConfirmation(String prompt) {
        while (true) {
            String input = getInput(prompt + " (s/n): ").toLowerCase();
            if (input.equals("s") || input.equals("si") || input.equals("y") || input.equals("yes")) {
                return true;
            } else if (input.equals("n") || input.equals("no")) {
                return false;
            } else {
                showErrorMessage("Risposta non valida. Utilizzare 's' per confermare o 'n' per annullare");
            }
        }
    }

    /**
     * Attende che l'utente prema INVIO per continuare.
     */
    public void pressEnterToContinue() {
        System.out.print(YELLOW + "▶ Premere INVIO per continuare..." + RESET);
        scanner.nextLine();
    }

    /**
     * Pulisce lo schermo del terminale.
     */
    public void clearScreen() {
        // Flag per supporto codici ANSI
        boolean supportsAnsi = true;
        if (supportsAnsi) {
            System.out.print("\033[H\033[2J");
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
     * Mostra il menu principale con logo ASCII art.
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
     */
    public void showMenuConfiguratore() {
        clearScreen();

        List<String> menuItems = List.of(
                MENU_VIEW_COMPRENSORI,
                MENU_VIEW_GERARCHIE,
                MENU_VIEW_CONVERSION_FACTORS,
                MENU_CREATE_COMPRENSORIO,
                MENU_CREATE_GERARCHIA,
                GREEN + "6. Visualizza proposte" + RESET,
                MENU_EXIT);

        printMenuBox(CONFIGURATOR_MENU_TITLE, menuItems);
    }

    /**
     * Mostra il menu per utenti fruitori.
     */
    public void showMenuFruitore() {
        clearScreen();

        List<String> menuItems = List.of(
                GREEN + "1. Visualizza categorie" + RESET,
                GREEN + "2. Formula proposta di scambio d'opera" + RESET,
                GREEN + "3. Visualizza proposte di scambio d'opera" + RESET,
                GREEN + "4. Ritira una proposta aperta" + RESET,
                MENU_EXIT);

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
                BLUE + "║" + MAGENTA + "           ██   ████ ███████ ██   ██  ██████  ███████            " + BLUE + "║"
                        + RESET,
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
     * Visualizza dati in formato tabella con intestazioni e bordi.
     *
     * @param headers intestazioni delle colonne
     * @param data    righe di dati (lista di liste, ogni lista è una riga)
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
     * Visualizza una barra di progresso animata.
     *
     * @param message    messaggio da mostrare
     * @param iterations numero totale di iterazioni
     * @param sleepMs    pausa tra iterazioni in millisecondi
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
     * Crea un riquadro con titolo e voci di menu formattate.
     *
     * @param titolo   titolo del menu
     * @param vociMenu lista delle voci del menu
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
     */
    private void stampaLinea(int larghezza) {
        System.out.println(BLUE + "+" + "-".repeat(larghezza - 2) + "+" + RESET);
    }

    /**
     * Centra il testo all'interno della larghezza specificata.
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
     */
    private String formattaVoceMenu(String voce, int larghezza) {
        String voceSenzaANSI = rimuoviANSI(voce);
        int spaziDestra = larghezza - voceSenzaANSI.length() - 4;

        return BLUE + "|  " + RESET + voce + " ".repeat(spaziDestra) + BLUE + "|" + RESET;
    }

    /**
     * Rimuove i codici ANSI da una stringa.
     */
    private String rimuoviANSI(String testo) {
        return testo.replaceAll("\033\\[[;\\d]*m", "");
    }

    /**
     * Chiude lo scanner quando l'applicazione termina.
     */
    public void close() {
        scanner.close();
    }
}