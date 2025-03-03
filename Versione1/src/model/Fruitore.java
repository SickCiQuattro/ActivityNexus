package model;

public class Fruitore extends Utente{
    private String mail;
    private Comprensorio comprensorio;

    public Fruitore(String nome, String cognome, String mail, Comprensorio comprensorio) {
        super(nome, cognome);
        this.mail = mail;
        this.comprensorio = comprensorio;
    }

    public String getMail() {
        return mail;
    }

    public Comprensorio getComprensorio() {
        return comprensorio;
    }
}
