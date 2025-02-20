import model.ValoreCampo;
import model.Categoria;
import model.Gerarchia;
import model.Campo;
import com.scalified.tree.TreeNode;

import java.util.Collection;
import java.util.HashSet;

public class GerarchiaTest {

    public static void main(String[] args) {

        HashSet<ValoreCampo> dominioRoot = new HashSet<>();
        dominioRoot.add(new ValoreCampo("teoria", "desc1"));
        dominioRoot.add(new ValoreCampo("strumento", "desc2"));
        Categoria categoriaroot = new Categoria("Lezioni di musica", new Campo("tipo", dominioRoot));

        HashSet<ValoreCampo> dominioLezTM = new HashSet<>();
        dominioLezTM.add(new ValoreCampo("solfeggio", "desc1"));
        dominioLezTM.add(new ValoreCampo("storia della musica", "desc2"));
        Categoria categoriaLezTM = new Categoria("Lezioni di teoria musicale", new Campo("materia", dominioLezTM));

        Categoria categoriaLezSolf = new Categoria("Lezioni di solfeggio");

        Categoria categoriaLezStMu= new Categoria("Lezioni di storia della musica");

        HashSet<ValoreCampo> dominioLezSM = new HashSet<>();
        dominioLezSM.add(new ValoreCampo("chitarra", "desc1"));
        dominioLezSM.add(new ValoreCampo("pianoforte", "desc2"));
        Categoria categoriaLezSM = new Categoria("Lezioni di uno strumento musicale", new Campo("strumento", dominioLezSM));

        HashSet<ValoreCampo> dominioLezC = new HashSet<>();
        dominioLezC.add(new ValoreCampo("principianti", "desc1"));
        Categoria categoriaLezC = new Categoria("Lezioni di chitarra", new Campo("livello", dominioLezC));

        Categoria categoriaLezCP= new Categoria("Lezioni di chitarra per principianti");

        HashSet<ValoreCampo> dominioLezP = new HashSet<>();
        dominioLezP.add(new ValoreCampo("principianti", "desc1"));
        dominioLezP.add(new ValoreCampo("medio", "desc1"));
        Categoria categoriaLezP = new Categoria("Lezioni di pianoforte", new Campo("livello", dominioLezP));

        Categoria categoriaLezPP= new Categoria("Lezioni di pianoforte per principianti");
        Categoria categoriaLezPM= new Categoria("Lezioni di pianoforte di difficolta media");


        Gerarchia gerarchia = new Gerarchia(categoriaroot);
        Gerarchia figlio1 = new Gerarchia(categoriaLezTM);
        Gerarchia figlio2 = new Gerarchia(categoriaLezSolf);
        Gerarchia figlio3 = new Gerarchia(categoriaLezStMu);
        Gerarchia figlio4 = new Gerarchia(categoriaLezSM);
        Gerarchia figlio5 = new Gerarchia(categoriaLezC);
        Gerarchia figlio6 = new Gerarchia(categoriaLezCP);
        Gerarchia figlio7 = new Gerarchia(categoriaLezP);
        Gerarchia figlio8 = new Gerarchia(categoriaLezPP);
        Gerarchia figlio9 = new Gerarchia(categoriaLezPM);


        gerarchia.add(figlio1);
        figlio1.add(figlio2);
        figlio1.add(figlio3);
        gerarchia.add(figlio4);
        figlio4.add(figlio5);
        figlio5.add(figlio6);
        figlio4.add(figlio7);
        figlio7.add(figlio8);
        figlio7.add(figlio9);

        //System.out.println(figli);
        //System.out.println(figlio5.subtrees().size());
        //System.out.println(gerarchia);
        //System.out.println(figlio9.level());
        //System.out.println(figlio9.root());

        if (figlio9.root().data().equals(categoriaroot)) {
            System.out.println("Test superato");
        } else {
            System.out.println("Test fallito");
        }
        //System.out.println(figlio9.data());

        //
        Categoria padre = (Categoria) figlio9.parent().data();
        Categoria figlio = (Categoria) figlio9.data();
        System.out.println(padre.getNome());
        System.out.println(figlio.getNome());
        //System.out.println(gerarchia);
    }
}