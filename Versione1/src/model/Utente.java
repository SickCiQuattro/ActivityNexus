package model;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class Utente implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username;
    private String hashedPassword;
    private byte[] salt;
    private boolean isConfig = false;

    public Utente(String username, String password) {
        this.username = username;
        this.salt = generateSalt();
        this.hashedPassword = hashPassword(password, this.salt);
    }

    public Utente(String username, String password, boolean isConfig) {
        this.username = username;
        this.salt = generateSalt();
        this.hashedPassword = hashPassword(password, this.salt);
        this.isConfig = isConfig;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isConfig() {
        return isConfig;
    }

    public boolean verificaCredenziali(String username, String password) {
        return this.username.equals(username) && this.hashedPassword.equals(hashPassword(password, this.salt));
    }

    private String hashPassword(String password, byte[] salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hash = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    @Serial
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    @Serial
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }

    @Override
    public String toString() {
        return "Username: " + username + "\n" + "Permessi: " + (isConfig ? "Configuratore" : "Fruitore");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Utente utente = (Utente) obj;
        return this.username.equals(utente.getUsername());
    }

    @Override
    public int hashCode() {
        return this.username.hashCode();
    }
}