package br.unicamp.cotuca.rededetrens;

public class ListaSimples<Dado extends Comparable<Dado>> {

    private NoLista<Dado> primeiro, ultimo, anterior, atual;
    int tamanho;

    public ListaSimples()
    {
        this.primeiro = null;
        this.ultimo = null;
        this.anterior = null;
        this.atual = null;
        this.tamanho = 0;
    }

    public boolean isVazia()
    {
        if(tamanho == 0)
            return true;
        return false;
    }

    public void inserirAntesDoInicio(NoLista<Dado> info)
    {
        if(this.isVazia())
            this.ultimo = info;
        info.proximo = this.primeiro;
        this.primeiro = info;
        tamanho++;
    }

    public void inserirAntesDoInicio(Dado info)
    {
        if(info != null)
        {
            NoLista<Dado> novo = new NoLista<Dado>(info, null);
            this.inserirAntesDoInicio(novo);
        }
    }

    public Dado get(int indice) throws Exception
    {
        Dado sai;
        this.atual = this.primeiro;
        if(indice < 0 || indice >= this.tamanho)
            throw  new Exception("ai droga");
        for(int i = 0; i < indice; i++)
        {
            this.atual = this.atual.proximo;
        }

        sai = atual.info;
        return sai;
    }


    public void inserirAposFim(NoLista<Dado> info)
    {
        if(this.isVazia())
            this.primeiro = info;
        else
            this.ultimo.proximo = info;
        info.proximo = null;
        this.ultimo = info;
        tamanho++;
    }

    public void inserirAposFim(Dado info)
    {
        if(info != null)
        {
            NoLista<Dado> novo = new NoLista<Dado>(info, null);
            this.inserirAposFim(novo);
        }
    }

    public boolean existeDado(Dado procurado)
    {
        this.anterior = null;
        this.atual = this.primeiro;
        if(isVazia())
            return false;
        if(procurado.compareTo(this.primeiro.getInfo()) < 0)
            return false;
        if(procurado.compareTo(this.ultimo.getInfo()) > 0)
        {
            this.anterior = this.ultimo;
            this.atual = null;
            return false;
        }

        boolean achou = false;
        boolean fim = false;

        while(!achou && !fim)
        {
            if(this.atual == null)
                fim = true;
            else
            if(procurado.compareTo(this.atual.getInfo()) == 0)
                achou = true;
            else
            if(this.atual.getInfo().compareTo(procurado) > 0)
                fim = true;
            else {
                this.anterior = this.atual;
                this.atual = this.atual.proximo;
            }
        }

        return achou;
    }

    private void inserirNoMeio (NoLista<Dado> info)
    {
        this.anterior.proximo = info;
        info.proximo = this.atual;
        if(this.anterior == this.ultimo)
            this.ultimo = info;
        tamanho++;
    }

    public  void inserirEmOrdem(Dado info)
    {
        if(!existeDado(info))
        {
            NoLista<Dado> novo = new NoLista<Dado>(info, null);
            if(isVazia())
                inserirAntesDoInicio(novo);
            else
            if(anterior == null && atual != null)
                inserirAntesDoInicio(novo);
            else
                inserirNoMeio(novo);
        }
    }
    protected void removerNo(NoLista<Dado> ant, NoLista<Dado> at)
    {
        if(this.anterior == null && this.atual != null)
        {
            this.primeiro = this.atual.proximo;
            if(this.primeiro == null)
                this.ultimo = null;
        }
        else
        {
            this.anterior.proximo = this.atual.proximo;
            if(this.atual == this.ultimo)
                this.ultimo = this.anterior;
        }
        tamanho--;
    }

    public boolean remove(Dado info)
    {
        if(!existeDado(info))
            return  false;

        removerNo(this.anterior, this.atual);

        return true;
    }

    public NoLista<Dado> getPrimeiro() {
        return primeiro;
    }

    public NoLista<Dado> getUltimo() {
        return ultimo;
    }

    public NoLista<Dado> getAnterior() {
        return anterior;
    }

    public NoLista<Dado> getAtual() {
        return atual;
    }
}