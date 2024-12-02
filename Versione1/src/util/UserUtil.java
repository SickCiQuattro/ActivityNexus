package util;

import java.util.Random;

/**
 * Classe di utilità per la gestione degli utenti.
 * Fornisce metodi per generare username e password casuali e per verificarne la validità.
 */
public class UserUtil {
    private static final String USERNAME_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String PASSWORD_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*";
    public static final int USERNAME_LENGTH = 20;
    public static final int PASSWORD_LENGTH = 8;

    /**
     * Genera uno username casuale.
     * Lo username è composto da caratteri alfanumerici e ha una lunghezza di {@value util.UserUtil#USERNAME_LENGTH} caratteri.
     *
     * @return uno username casuale
     */
    public static String generateRandomUsername() {
        Random random = new Random();
        StringBuilder username = new StringBuilder(USERNAME_LENGTH);
        for (int i = 0; i < 10; i++) {
            username.append(USERNAME_CHARS.charAt(random.nextInt(USERNAME_CHARS.length())));
        }
        return username.toString();
    }

    /**
     * Genera una password casuale.
     * La password è composta da caratteri alfanumerici e caratteri speciali, e ha una lunghezza di {@value util.UserUtil#PASSWORD_LENGTH} caratteri.
     * La password contiene almeno una lettera maiuscola, una lettera minuscola, un numero e un carattere speciale.
     *
     * @return una password casuale
     */
    public static String generateRandomPassword() {
        Random random = new Random();
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        password.append(getRandomChar("ABCDEFGHIJKLMNOPQRSTUVWXYZ", random)); // Almeno una lettera maiuscola
        password.append(getRandomChar("abcdefghijklmnopqrstuvwxyz", random)); // Almeno una lettera minuscola
        password.append(getRandomChar("0123456789", random)); // Almeno un numero
        password.append(getRandomChar("!@#$%^&*", random)); // Almeno un carattere speciale

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            password.append(PASSWORD_CHARS.charAt(random.nextInt(PASSWORD_CHARS.length())));
        }
        return password.toString();
    }

    /**
     * Restituisce un carattere casuale dalla stringa specificata.
     *
     * @param chars la stringa da cui estrarre un carattere
     * @param random l'istanza di Random da utilizzare
     * @return un carattere casuale dalla stringa specificata
     */
    private static char getRandomChar(String chars, Random random) {
        return chars.charAt(random.nextInt(chars.length()));
    }

    /**
     * Verifica che lo username inserito sia valido.
     * Deve essere non vuoto, senza spazi, lungo massimo {@value util.UserUtil#USERNAME_LENGTH} caratteri e senza caratteri speciali.
     *
     * @param username lo username da verificare
     * @return true se lo username è valido, false altrimenti
     */
    public static boolean checkUsername(String username) {
        return !username.isEmpty() && !username.contains(" ") && username.length() <= USERNAME_LENGTH && !username.matches("[^a-zA-Z0-9]");
    }

    /**
     * Verifica che la password inserita sia valida.
     * Deve essere lunga almeno {@value util.UserUtil#PASSWORD_LENGTH} caratteri, contenere almeno una lettera maiuscola, una lettera minuscola, un numero e un carattere speciale.
     *
     * @param password la password da verificare
     * @return true se la password è valida, false altrimenti
     */
    public static boolean checkPassword(String password) {
        return password.length() >= PASSWORD_LENGTH && password.matches(".*[A-Z].*") && password.matches(".*[a-z].*") && password.matches(".*\\d.*") && password.matches(".*[!@#$%^&*].*");
    }
}