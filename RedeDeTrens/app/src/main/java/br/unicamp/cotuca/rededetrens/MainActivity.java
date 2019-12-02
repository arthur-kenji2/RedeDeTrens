package br.unicamp.cotuca.rededetrens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.ScientificNumberFormatter;
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

    private Button btnBuscar, btnAdicionarCidade, btnAdicionarCaminho;
    private ArrayList<String> lista;
    private ImageView ivImagem;
    private TextView tvResultado;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            lista = new ArrayList<String>();
            tvResultado = findViewById(R.id.txtViewResultados);
            btnAdicionarCaminho = findViewById(R.id.btnCaminho);
            btnAdicionarCidade = findViewById(R.id.btnCidade);
            btnBuscar = findViewById(R.id.btnBuscar);
            ivImagem = findViewById(R.id.imgView);

            AssetManager ass = getAssets();
            Scanner sc = new Scanner(ass.open("GrafoTrem"));
            lerArquivo(sc);
            sc.close();

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, lista);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            Spinner spnD = (Spinner) findViewById(R.id.spnDe);
            Spinner spnP = (Spinner) findViewById(R.id.spnPara);

            spnD.setAdapter(adapter);
            spnP.setAdapter(adapter);

            sc = new Scanner(ass.open("Cidades"));

            BitmapDrawable drawable = (BitmapDrawable) ivImagem.getDrawable();
            Bitmap mBitmap = drawable.getBitmap();



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


            btnBuscar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pesquisar();
                }
            });

            btnAdicionarCidade.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            btnAdicionarCaminho.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void pesquisar()
    {

    }

    public void fazerTabela()
    {
        BucketHash balde = new BucketHash();
        for(int i = 0; i)
            balde.Insert();
    }

    public void lerArquivo(Scanner sc)
    {
        String s = "";

        for (int i = 0; sc.hasNextLine(); i++)
        {
            s = sc.nextLine();
            s = s.substring(0,15);
            if(!isNumber(s))
            {
                if(!jaTem(s, lista))
                    lista.add(s);
            }
        }
        sc.close();
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
