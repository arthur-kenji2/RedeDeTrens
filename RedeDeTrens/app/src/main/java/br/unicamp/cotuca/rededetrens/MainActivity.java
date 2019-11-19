package br.unicamp.cotuca.rededetrens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.*;
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
            Scanner sc = new Scanner(ass.open("GrafoTrem.txt"));
            String s = "";
            List<String> lista = new ArrayList<String>();
            for (int i = 0; sc.hasNext(); i++) {
                s = sc.next();
                lista.add(s);
            /*
            Spinner spnD = (Spinner) findViewById(R.id.spnDe);
            Spinner spnP = (Spinner) findViewById(R.id.spnPara);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item,sc.nextLine())

             */

            }
            TextView teste = (TextView) findViewById(R.id.txtViewResultados);
                teste.setText(lista.get(0).toString());
            sc.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
