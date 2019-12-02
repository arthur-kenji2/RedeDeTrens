package br.unicamp.cotuca.rededetrens;

public class Cidade {
    private int id;
    private String nome;
    private double x;
    private double y;

    public Cidade(){
    }

    public Cidade(int id, String nome, double x, double y) {
        this.id = id;
        this.nome = nome;
        this.x = x;
        this.y = y;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
