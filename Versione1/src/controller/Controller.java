package controller;

import model.Database;
import model.Utente;
import model.Comprensorio;
import model.Gerarchia;

public class Controller {
     private Database database;

     public Controller() {
         this.database = new Database();
     }

     public Database getDatabase() {
         return this.database;
     }

     public boolean addUtente(Utente utente) {
         return this.database.addUtente(utente);
     }

     public boolean addComprensorio(Comprensorio comprensorio) {
         return this.database.addComprensorio(comprensorio);
     }

     public boolean addGerarchia(Gerarchia gerarchia) {
         return this.database.addGerarchia(gerarchia);
     }

    public boolean registerUtente(String username, String password, boolean isConfig){
         return this.database.registerUtente(username, password, isConfig);
    }

    public boolean loginUtente(String username, String password){
        return this.database.loginUtente(username, password);
    }
}
