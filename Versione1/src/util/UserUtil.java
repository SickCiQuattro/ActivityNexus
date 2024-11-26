package util;

import view.ConsoleView;

import java.util.Random;

public class UserUtil {
    private static final String USERNAME_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String PASSWORD_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*";
    private static final int USERNAME_LENGTH = 20;
    private static final int PASSWORD_LENGTH = 8;

    public static String generateRandomUsername() {
        Random random = new Random();
        StringBuilder username = new StringBuilder(USERNAME_LENGTH);
        for (int i = 0; i < USERNAME_LENGTH; i++) {
            username.append(USERNAME_CHARS.charAt(random.nextInt(USERNAME_CHARS.length())));
        }
        return username.toString();
    }

    public static String generateRandomPassword() {
        Random random = new Random();
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        password.append(getRandomChar("ABCDEFGHIJKLMNOPQRSTUVWXYZ", random)); // Almeno una lettera maiscuola
        password.append(getRandomChar("abcdefghijklmnopqrstuvwxyz", random)); // Almeno una lettera minuscola
        password.append(getRandomChar("0123456789", random)); // Almeno un numero
        password.append(getRandomChar("!@#$%^&*", random)); // Almeno un carattere speciale

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            password.append(PASSWORD_CHARS.charAt(random.nextInt(PASSWORD_CHARS.length())));
        }
        return password.toString();
    }

    private static char getRandomChar(String chars, Random random) {
        return chars.charAt(random.nextInt(chars.length()));
    }

    /**
     *  Verifica che lo username inserito sia valido. Deve essere non vuoto, senza spazi,
     *  lungo massimo {@value util.UserUtil#USERNAME_LENGTH} caratteri e senza caratteri speciali.
     * @param username lo username da verificare
     * @return true se lo username è valido, false altrimenti
     */
    private boolean checkUsername(String username) {
        return !username.isEmpty() && !username.contains(" ") && username.length() <= USERNAME_LENGTH && !username.matches("[^a-zA-Z0-9]");
    }

    /**
     * Verifica che la password inserita sia valida. Deve essere lunga almeno {@value util.UserUtil#PASSWORD_LENGTH} caratteri,
     * contenere almeno una lettera maiuscola, una lettera minuscola, un numero e un carattere speciale.
     * @param password la password da verificare
     * @return true se la password è valida, false altrimenti
     */
    private boolean checkPassword(String password) {
        return password.length() >= PASSWORD_LENGTH && password.matches(".*[A-Z].*") && password.matches(".*[a-z].*") && password.matches(".*\\d.*") && password.matches(".*[!@#$%^&*].*");
    }


}
