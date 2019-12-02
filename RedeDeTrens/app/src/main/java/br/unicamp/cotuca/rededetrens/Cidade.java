package br.unicamp.cotuca.rededetrens;

public class Cidade {
    private String id;
    private String nome;
    private float x;
    private float y;

    public Cidade(){
    }

    public Cidade(String id, String nome, float x, float y) {
        this.id = id;
        this.nome = nome;
        this.x = x;
        this.y = y;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
