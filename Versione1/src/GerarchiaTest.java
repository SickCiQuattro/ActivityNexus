
import model.Attivita;
import model.Categoria;
import model.Gerarchia;
import model.Campo;
import com.scalified.tree.TreeNode;

import java.util.HashSet;

public class GerarchiaTest {

    public static void main(String[] args) {
        Attivita attivita1 = new Attivita("nomeattivita1", "descrizione1");
        Attivita attivita2 = new Attivita("nomeattivita2");

        HashSet<Attivita> dominio = new HashSet<>();
        dominio.add(attivita1);
        dominio.add(attivita2);

        Campo campo = new Campo("nomecampo", dominio);



        HashSet<Attivita> dominioRoot = new HashSet<>();
        dominioRoot.add(new Attivita("teoria", "desc1"));
        dominioRoot.add(new Attivita("pratica", "desc2"));
        Categoria categoriaroot = new Categoria("Lezioni di musica", new Campo("tipo", dominioRoot));

        HashSet<Attivita> dominioLezTM = new HashSet<>();
        dominioLezTM.add(new Attivita("solfeggio", "desc1"));
        dominioLezTM.add(new Attivita("storia della musica", "desc2"));
        Categoria categoriaLezTM = new Categoria("Lezioni di teoria musicale", new Campo("materia", dominioLezTM));

        Categoria categoriaLezSolf = new Categoria("Lezioni di solfeggio");

        Categoria categoriaLezStMu= new Categoria("Lezioni di storia della musica");

        HashSet<Attivita> dominioLezSM = new HashSet<>();
        dominioLezSM.add(new Attivita("chitarra", "desc1"));
        dominioLezSM.add(new Attivita("pianoforte", "desc2"));
        Categoria categoriaLezSM = new Categoria("Lezioni di uno strumento musicale", new Campo("strumento", dominioLezSM));

        HashSet<Attivita> dominioLezC = new HashSet<>();
        dominioLezC.add(new Attivita("principianti", "desc1"));
        Categoria categoriaLezC = new Categoria("Lezioni di chitarra", new Campo("lezione", dominioLezC));

        Categoria categoriaLezCP= new Categoria("Lezioni di chitarra per principianti");

        HashSet<Attivita> dominioLezP = new HashSet<>();
        dominioLezP.add(new Attivita("principianti", "desc1"));
        dominioLezP.add(new Attivita("medio", "desc1"));
        Categoria categoriaLezP = new Categoria("Lezioni di pianoforte", new Campo("lezione", dominioLezP));

        Categoria categoriaLezPP= new Categoria("Lezioni di pianoforte per principianti");
        Categoria categoriaLezPM= new Categoria("Lezioni di pianoforte di difficolta media");


        TreeNode<Categoria> gerarchia = new Gerarchia(categoriaroot);
        TreeNode<Categoria> figlio1 = new Gerarchia(categoriaLezTM);
        TreeNode<Categoria> figlio2 = new Gerarchia(categoriaLezSolf);
        TreeNode<Categoria> figlio3 = new Gerarchia(categoriaLezStMu);
        TreeNode<Categoria> figlio4 = new Gerarchia(categoriaLezSM);
        TreeNode<Categoria> figlio5 = new Gerarchia(categoriaLezC);
        TreeNode<Categoria> figlio6 = new Gerarchia(categoriaLezCP);
        TreeNode<Categoria> figlio7 = new Gerarchia(categoriaLezP);
        TreeNode<Categoria> figlio8 = new Gerarchia(categoriaLezPP);
        TreeNode<Categoria> figlio9 = new Gerarchia(categoriaLezPM);


        gerarchia.add(figlio1);
        figlio1.add(figlio2);
        figlio1.add(figlio3);
        gerarchia.add(figlio4);
        figlio4.add(figlio5);
        figlio5.add(figlio6);
        figlio4.add(figlio7);
        figlio7.add(figlio8);
        figlio7.add(figlio9);

        System.out.println(gerarchia);
    }
}