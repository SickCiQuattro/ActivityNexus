package util;

import java.util.Random;

public class UserGenerator {
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

}
