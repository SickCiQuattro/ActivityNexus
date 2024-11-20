package model;

/**
 * Classe che rappresenta una categoria di un annuncio
 * Una categoria è costituita da un nome e da un campo
 */
public class Categoria {
    private String nome;
    private Campo campo = null;

    public Categoria(String nome){
        this.nome = nome;
    }

    public Categoria(String nome, Campo campo){
        this.nome = nome;
        this.campo = campo;
    }

    public String getNome(){
        return this.nome;
    }

    public Campo getCampo(){
        return this.campo;
    }

    public void setNome(String nome){
        this.nome = nome;
    }

    public void setCampo(Campo campo){
        this.campo = campo;
    }

    @Override
    public String toString(){
        return "Nome: " + this.nome + "\n" + (this.campo!=null ? "Campo: " + this.campo + "\n" : "");
    }
}
