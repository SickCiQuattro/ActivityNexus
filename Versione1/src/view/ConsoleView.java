package view;

import java.util.Scanner;
import java.util.List;

/**
 * Classe che rappresenta l'interfaccia utente a console del sistema.
 * Fornisce metodi per visualizzare messaggi, menu interattivi e acquisire input
 * dall'utente.
 * Include funzionalità per la formattazione del testo con colori ANSI.
 */
public class ConsoleView {
    // -----------------------------------------------------------------------------------
    // ATTRIBUTI
    // -----------------------------------------------------------------------------------

    /** Scanner per acquisire input dall'utente */
    private final Scanner scanner = new Scanner(System.in);

    // -----------------------------------------------------------------------------------
    // COSTANTI PER LA FORMATTAZIONE
    // -----------------------------------------------------------------------------------

    /** Codice ANSI per ripristinare il colore di default */
    private static final String RESET = "\033[0m";
    /** Codice ANSI per il colore rosso */
    private static final String RED = "\033[31m";
    /** Codice ANSI per il colore verde */
    private static final String GREEN = "\033[32m";
    /** Codice ANSI per il colore blu */
    private static final String BLUE = "\033[34m";
    /** Codice ANSI per il colore ciano */
    private static final String CYAN = "\033[36m";
    /** Codice ANSI per il colore giallo */
    private static final String YELLOW = "\033[33m";

    // -----------------------------------------------------------------------------------
    // COSTANTI PER I MESSAGGI
    // -----------------------------------------------------------------------------------

    /** Messaggio di benvenuto */
    private static final String WELCOME_MESSAGE = YELLOW + "Benvenuto in LogHorizon2!" + RESET;
    /** Titolo del menu configuratore */
    private static final String CONFIGURATOR_MENU_TITLE = YELLOW + "MENU Configuratore" + RESET;

    // -----------------------------------------------------------------------------------
    // COSTANTI PER LE VOCI DEI MENU
    // -----------------------------------------------------------------------------------

    // Menu principale
    /** Voce del menu per il login */
    private static final String MENU_LOGIN = GREEN + "1. Login" + RESET;
    /** Voce del menu per la registrazione */
    private static final String MENU_REGISTRATION = CYAN + "2. Registrazione" + RESET;
    /** Voce del menu per l'uscita */
    private static final String MENU_EXIT = RED + "0. Uscita" + RESET;

    // Menu configuratore
    /** Voce del menu per visualizzare i comprensori */
    private static final String MENU_VIEW_COMPRENSORI = GREEN + "1. Visualizza Comprensori" + RESET;
    /** Voce del menu per visualizzare le gerarchie */
    private static final String MENU_VIEW_GERARCHIE = GREEN + "2. Visualizza Gerarchie" + RESET;
    /** Voce del menu per visualizzare i fattori di conversione */
    private static final String MENU_VIEW_CONVERSION_FACTORS = GREEN + "3. Visualizza Fattori di Conversione" + RESET;
    /** Voce del menu per creare un comprensorio */
    private static final String MENU_CREATE_COMPRENSORIO = CYAN + "4. Crea Comprensorio" + RESET;
    /** Voce del menu per creare una gerarchia */
    private static final String MENU_CREATE_GERARCHIA = CYAN + "5. Crea Gerarchia" + RESET;

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
     *
     * @param messaggio il messaggio di errore da visualizzare
     */
    public void showErrorMessage(String messaggio) {
        System.out.println(RED + messaggio + RESET);
    }

    /**
     * Mostra un messaggio di successo sulla console (in verde).
     *
     * @param messaggio il messaggio di successo da visualizzare
     */
    public void showSuccessMessage(String messaggio) {
        System.out.println(GREEN + messaggio + RESET);
    }

    /**
     * Ottiene un input testuale dall'utente dopo aver mostrato un prompt.
     *
     * @param prompt il messaggio da mostrare all'utente per richiedere l'input
     * @return l'input dell'utente con spazi iniziali e finali rimossi
     */
    public String getInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /**
     * Ottiene un input numerico dall'utente.
     *
     * @param prompt il messaggio da mostrare all'utente per richiedere l'input
     * @param min    il valore minimo accettabile
     * @param max    il valore massimo accettabile
     * @return l'input numerico dell'utente entro i limiti specificati
     */
    public int getIntInput(String prompt, int min, int max) {
        while (true) {
            try {
                String input = getInput(prompt);
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
     * Chiede all'utente di premere un tasto per continuare.
     */
    public void pressEnterToContinue() {
        System.out.print(YELLOW + "Premere INVIO per continuare..." + RESET);
        scanner.nextLine();
    }

    // -----------------------------------------------------------------------------------
    // METODI PER LA VISUALIZZAZIONE DEI MENU
    // -----------------------------------------------------------------------------------

    /**
     * Mostra il menu principale sulla console.
     */
    public void showMenu() {
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
        List<String> menuItems = List.of(
                MENU_VIEW_COMPRENSORI,
                MENU_VIEW_GERARCHIE,
                MENU_VIEW_CONVERSION_FACTORS,
                MENU_CREATE_COMPRENSORIO,
                MENU_CREATE_GERARCHIA,
                MENU_EXIT);
        printMenuBox(CONFIGURATOR_MENU_TITLE, menuItems);
    }

    // -----------------------------------------------------------------------------------
    // METODI DI SUPPORTO PER LA FORMATTAZIONE
    // -----------------------------------------------------------------------------------

    /**
     * Stampa un riquadro contenente un menu con titolo e voci.
     *
     * @param titolo   il titolo del riquadro
     * @param vociMenu la lista delle voci del menu
     */
    private void printMenuBox(String titolo, List<String> vociMenu) {
        int larghezzaRiquadro = calcolaLarghezzaRiquadro(titolo, vociMenu);
        stampaLinea(larghezzaRiquadro);
        System.out.println(centraTesto(titolo, larghezzaRiquadro));
        stampaLinea(larghezzaRiquadro);
        vociMenu.forEach(voce -> System.out.println(formattaVoceMenu(voce, larghezzaRiquadro)));
        stampaLinea(larghezzaRiquadro);
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
        return larghezzaMassima + 6; // Margine aggiuntivo
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
        int spaziDestra = larghezza - voceSenzaANSI.length() - 3;

        return BLUE + "|" + RESET + " " + voce + " ".repeat(spaziDestra) + BLUE + "|" + RESET;
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