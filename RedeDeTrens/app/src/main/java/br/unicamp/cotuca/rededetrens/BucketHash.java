package br.unicamp.cotuca.rededetrens;

import java.util.ArrayList;

public class BucketHash
{
    private int SIZE = 100;
    ArrayList[] data;


    public BucketHash()
    {
        data = new ArrayList[SIZE];
        for(int i = 0; i < SIZE; i++)
            data[i] = new ArrayList(1);
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

    public void Insert(String item)
    {
        int hash_value = Hash(item);
        if (!data[hash_value].contains(item))
            data[hash_value].add(item);
    }

    public boolean Remove(String item)
    {
        int hash_value = Hash(item);
        if (data[hash_value].contains(item))
        {
            data[hash_value].remove(item);
            return true;
        }
        return false;
    }




}
