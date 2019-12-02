package br.unicamp.cotuca.rededetrens;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
    private ArrayList<Cidade> cidades;

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
            lerCidades(sc);
            sc.close();

            //fazerTabela();

            BitmapDrawable drawable = (BitmapDrawable) ivImagem.getDrawable();
            Bitmap mBitmap = drawable.getBitmap();

            desenharCidades(mBitmap);

            //OutputStream outputStream = new FileOutputStream(new File(String.valueOf(ass.open("Cidades"))), true);
            //OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);

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
                    startActivityForResult(new Intent(MainActivity.this, AdicionarCidade.class), 1);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1) {

        }
    }

    public void desenharCidades(Bitmap mBitmap) {
        Canvas canvas = new Canvas(mBitmap);
        Paint p = new Paint(Color.BLACK);

        for(Cidade c : cidades)
            canvas.drawLine(c.getX(), c.getY(), c.getX(), c.getY(), p);
    }

    public void pesquisar()
    {

    }

    public void fazerTabela()
    {
        BucketHash bh = new BucketHash();
        for(int i = 0; i < cidades.size(); i++)
            bh.Insert(cidades.get(i).getNome());
    }

    public void lerCidades(Scanner sc)
    {
        String s = "";
        for(int i = 0; sc.hasNextLine(); i++)
        {
            s = sc.nextLine();
            Cidade c = new Cidade();
            c.setId(Integer.parseInt(s.substring(0, 2).trim()));
            c.setNome(s.substring(2, 18));
            c.setX(Float.parseFloat(s.substring(18, 24).trim()));
            c.setY(Float.parseFloat(s.substring(24, 29).trim()));
            cidades.add(c);
        }

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
