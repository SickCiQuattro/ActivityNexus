package controller;

import model.Comprensorio;

import java.util.List;

public class ComprensorioController { // sono tanto stanco non ce la faccio più. ci penseremo domani se farlo o meno
    private final List<Comprensorio> comprensori;

    public ComprensorioController(List<Comprensorio> comprensori) {
        this.comprensori = comprensori;
    }

    private String visualizzaComprensori() {
        StringBuilder sb = new StringBuilder();

        Comprensorio c1 = new Comprensorio("Comprensorio1");
        c1.addComune("Comune1");
        c1.addComune("Comune2");
        c1.addComune("Comune3");
        Comprensorio c2 = new Comprensorio("Comprensorio2");
        c2.addComune("Comune4");
        c2.addComune("Comune5");
        c2.addComune("Comune6");
        this.comprensori.add(c1);
        this.comprensori.add(c2);

        for (Comprensorio c : this.comprensori) {
            sb.append(c.toString()).append("\n");
        }
        return sb.toString();
    }
}
