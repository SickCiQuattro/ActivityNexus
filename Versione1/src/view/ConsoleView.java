package view;

import java.util.Scanner;
import java.util.List;

/**
 * Classe che rappresenta la vista console del sistema.
 * Fornisce metodi per mostrare messaggi, ottenere input dall'utente e visualizzare un menu.
 */
public class ConsoleView {
    private final Scanner scanner = new Scanner(System.in);

    // Colori ANSI
    private static final String RESET = "\033[0m";
    private static final String RED = "\033[31m";
    private static final String GREEN = "\033[32m";
    private static final String BLUE = "\033[34m";
    private static final String CYAN = "\033[36m";
    private static final String YELLOW = "\033[33m";

    /**
     * Mostra un messaggio sulla console.
     *
     * @param message il messaggio da mostrare
     */
    public void showMessage(String message) {
        System.out.println(message);
    }

    /**
     * Ottiene un input dall'utente dopo aver mostrato un prompt.
     *
     * @param prompt il prompt da mostrare all'utente
     * @return l'input dell'utente
     */
    public String getInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    /**
     * Mostra il menu principale sulla console.
     */
    public void showMenu() {
        List<String> menuItems = List.of(
                GREEN + "1. Login" + RESET,
                CYAN + "2. Registrazione" + RESET,
                RED + "3. Uscita" + RESET
        );
        printMenuBox(YELLOW + "Benvenuto in LogHorizon2!" + RESET, menuItems);
    }

    /**
     * Stampa un riquadro del menu con un titolo e una lista di voci.
     *
     * @param title il titolo del riquadro
     * @param menuItems la lista delle voci del menu
     */
    public void printMenuBox(String title, List<String> menuItems) {
        // Calcola la larghezza del riquadro in base alla voce più lunga
        int maxWidth = Math.max(stripANSI(title).length(), menuItems.stream().mapToInt(this::stripANSILength).max().orElse(0));
        int boxWidth = maxWidth + 4; // Aggiunge spazio per margini laterali

        // Stampa la parte superiore del riquadro
        printLine(boxWidth);

        // Stampa il titolo centrato
        System.out.println(centerText(title, boxWidth));

        // Stampa una linea di separazione
        printLine(boxWidth);

        // Stampa le voci del menu
        for (String item : menuItems) {
            System.out.println(formatMenuItem(item, boxWidth));
        }

        // Stampa la parte inferiore del riquadro
        printLine(boxWidth);
    }

    /**
     * Stampa una linea del riquadro con la larghezza specificata.
     *
     * @param width la larghezza della linea
     */
    private void printLine(int width) {
        System.out.println(BLUE + "+" + "-".repeat(width - 2) + "+" + RESET);
    }

    /**
     * Centra il testo all'interno di una larghezza specificata.
     *
     * @param text il testo da centrare
     * @param width la larghezza totale
     * @return il testo centrato
     */
    private String centerText(String text, int width) {
        String strippedText = stripANSI(text);
        int padding = (width - strippedText.length() - 2) / 2;
        return BLUE + "|" + RESET + " ".repeat(padding) + text + " ".repeat(width - strippedText.length() - padding - 2) + BLUE + "|" + RESET;
    }

    /**
     * Format a una voce del menu all'interno di una larghezza specificata.
     *
     * @param item la voce del menu
     * @param width la larghezza totale
     * @return la voce del menu formattata
     */
    private String formatMenuItem(String item, int width) {
        String strippedItem = stripANSI(item);
        return BLUE + "|" + RESET + " " + item + " ".repeat(width - strippedItem.length() - 3) + BLUE + "|" + RESET;
    }

    /**
     * Rimuove i codici ANSI da una stringa per calcolare correttamente la lunghezza del testo.
     *
     * @param text il testo da cui rimuovere i codici ANSI
     * @return il testo senza codici ANSI
     */
    private String stripANSI(String text) {
        return text.replaceAll("\033\\[[;\\d]*m", "");
    }

    /**
     * Calcola la lunghezza di una stringa senza i codici ANSI.
     *
     * @param text il testo da cui calcolare la lunghezza
     * @return la lunghezza del testo senza codici ANSI
     */
    private int stripANSILength(String text) {
        return stripANSI(text).length();
    }
}