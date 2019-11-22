package br.unicamp.cotuca.rededetrens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.*;
import android.widget.*;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.*;
import java.text.NumberFormat;
import java.util.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        try {
            TextView tvResultado = findViewById(R.id.txtViewResultados);
            setContentView(R.layout.activity_main);
            AssetManager ass = getAssets();
            Scanner sc = new Scanner(ass.open("GrafoTrem"));
            String s = "";
            ArrayList<String> lista = new ArrayList<String>();
            for (int i = 0; sc.hasNext(); i++)
            {
                s = sc.next();
                if(!isNumber(s))
                {
                    if(!jaTem(s, lista))
                        lista.add(s);
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, lista);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Spinner spnD = (Spinner) findViewById(R.id.spnDe);
            Spinner spnP = (Spinner) findViewById(R.id.spnPara);
            spnD.setAdapter(adapter);
            spnP.setAdapter(adapter);
            spnD.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                {
                    String st = parent.getItemAtPosition(position).toString();
                    Toast.makeText(parent.getContext(), "Selected: " + st,Toast.LENGTH_LONG).show();
                }
                @Override
                public void onNothingSelected(AdapterView <?> parent)
                {
                }
            });
            spnP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                {
                    String st = parent.getItemAtPosition(position).toString();
                    Toast.makeText(parent.getContext(), "Selected: " + st,Toast.LENGTH_LONG).show();
                }
                @Override
                public void onNothingSelected(AdapterView <?> parent)
                {
                }
            });

            sc.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public boolean isNumber(String n)
    {
        try{
            Integer.parseInt(n);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }


    public boolean jaTem(String n, List lista)
    {
        for(int i = 0; i < lista.size();i++)
            if(lista.get(i).equals(n))
                return true;
            return false;
    }
}
