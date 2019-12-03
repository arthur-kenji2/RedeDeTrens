package br.unicamp.cotuca.rededetrens;

import java.util.ArrayList;

public class BucketHash
{
    private final int SIZE = 103;
    private ListaSimples<Cidade>[]  data;

    public BucketHash()
    {
        data = new ListaSimples[SIZE];
        for(int i = 0; i < SIZE; i++)
            data[i] = new ListaSimples<>();
    }

    public int Hash(String s)
    {
        long tot = 0;
        char[] charray;
        charray = s.toUpperCase().toCharArray();
        for (int i = 0; i <= s.length() - 1; i++)
            tot += 37 * tot + (int)charray[i];
        tot = tot % data.length - 1;
        if (tot < 0)
            tot += data.length - 1;
        return (int)tot;
    }

    public void Insert(Cidade c)
    {
        int hash_value = Hash(c.getNome());
        if (!data[hash_value].existeDado(c))
            data[hash_value].inserirAposFim(c);
    }

    public boolean Remove(Cidade c)
    {
        int hash_value = Hash(c.getNome());
        if (data[hash_value].existeDado(c))
        {
            data[hash_value].remove(c);
            return true;
        }
        return false;
    }

    public Cidade getCidade(String nome)
    {
        ListaSimples<Cidade> c = getPosicao(Hash(nome));
        try {
            for (int i = 0; i < c.tamanho; i++)
                if (c.get(i).getNome().equals(nome))
                    return c.get(i);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public ListaSimples<Cidade> getPosicao(int hash)
    {
        return data[hash];
    }
}
