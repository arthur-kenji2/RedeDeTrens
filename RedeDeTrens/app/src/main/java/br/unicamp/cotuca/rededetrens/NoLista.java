package br.unicamp.cotuca.rededetrens;

public class NoLista<Dado> {
    Dado info;
    NoLista<Dado> proximo;
    public NoLista(Dado i, NoLista<Dado> p)
    {
        this.info = i;
        this.proximo = p;
    }

    public Dado getInfo()
    {
        return this.info;
    }

    public  void setInfo(Dado i)
    {
        if(i != null)
            this.info = i;
    }

    public NoLista<Dado> getProximo()
    {
        return this.proximo;
    }

    public void setProximo(NoLista<Dado> prox)
    {
        this.proximo = prox;
    }
}