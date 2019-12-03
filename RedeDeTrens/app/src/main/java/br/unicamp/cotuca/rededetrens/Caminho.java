package br.unicamp.cotuca.rededetrens;

public class Caminho implements Comparable<Caminho>{
    private String origem;
    private String destino;
    private int distancia;
    private int tempo;

    public Caminho()
    {}

    public Caminho(String origem, String destino, int distancia, int tempo) {
        this.origem = origem;
        this.destino = destino;
        this.distancia = distancia;
        this.tempo = tempo;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public int getDistancia() {
        return distancia;
    }

    public void setDistancia(int distancia) {
        this.distancia = distancia;
    }

    public int getTempo() {
        return tempo;
    }

    public void setTempo(int tempo) {
        this.tempo = tempo;
    }

    @Override
    public int compareTo(Caminho o) {
        if(this.getOrigem().compareTo(o.getOrigem()) > 0)
            return 1;
        if(this.getOrigem().compareTo(o.getOrigem()) < 0)
            return -1;
        return 0;
    }
}
