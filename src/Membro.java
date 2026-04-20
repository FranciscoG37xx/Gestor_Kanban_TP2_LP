/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author franc
 */
public class Membro {
    private String nome;
    private String papel; // ex: Developer, Designer, Tester

    public Membro(String nome, String papel) {
        this.nome = nome;
        this.papel = papel;
    }

    public String getNome()  { return nome; }
    public String getPapel() { return papel; }

    public void setNome(String nome)   { this.nome = nome; }
    public void setPapel(String papel) { this.papel = papel; }

    @Override
    public String toString() { return nome + " (" + papel + ")"; }
}
