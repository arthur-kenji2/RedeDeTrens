package br.unicamp.cotuca.rededetrens;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.ScientificNumberFormatter;
import android.icu.text.UnicodeSetSpanner;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.*;
import android.widget.*;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import org.w3c.dom.Text;

import java.io.*;
import java.text.NumberFormat;
import java.util.*;

public class MainActivity extends AppCompatActivity {

    private Button btnBuscar, btnAdicionarCidade, btnAdicionarCaminho;
    private ArrayList<String> lista;
    private ImageView ivImagem;
    private TextView tvResultado;
    private ListaSimples<Caminho> caminhos;
    private Bitmap mBitmap;
    private OutputStream copiaCidade, copiaGrafo;
    private BucketHash tabelaCidades;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            lista = new ArrayList<String>();
            caminhos = new ListaSimples<>();
            tvResultado = findViewById(R.id.txtViewResultados);
            btnAdicionarCaminho = findViewById(R.id.btnCaminho);
            btnAdicionarCidade = findViewById(R.id.btnCidade);
            btnBuscar = findViewById(R.id.btnBuscar);
            ivImagem = findViewById(R.id.imgView);
            tabelaCidades = new BucketHash();

            AssetManager ass = getAssets();

            Scanner sc = new Scanner(ass.open("GrafoTrem"));
            lerGrafo(sc);
            sc.close();

            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, lista);

            Spinner spnD = (Spinner) findViewById(R.id.spnDe);
            Spinner spnP = (Spinner) findViewById(R.id.spnPara);

            spnD.setAdapter(adapter);
            spnP.setAdapter(adapter);

            ListaSimples<Cidade> cidades = new ListaSimples<>();
            sc = new Scanner(ass.open("Cidades"));
            lerCidades(sc, cidades);
            inserirTabela(cidades);
            sc.close();

            mBitmap = decodeSampledBitmapFromResource(getResources(), R.drawable.mapaespanhaportugal, ivImagem.getMaxWidth(), ivImagem.getMaxHeight());
            ivImagem.setImageBitmap(mBitmap);
            Bitmap mBitnew = mBitmap.copy(mBitmap.getConfig(), true);

            for(int i = 0; i < cidades.tamanho; i++)
                desenharCidade(cidades.get(i), mBitnew);

            mBitmap = mBitnew;

            InputStream assetIs = getAssets().open("Cidades");
            copiaCidade = openFileOutput("Cidades", MODE_APPEND);

            InputStream assetIs2 = getAssets().open("GrafoTrem");
            copiaGrafo = openFileOutput("GrafoTrem", MODE_APPEND);

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
                    final AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                    View mView = getLayoutInflater().inflate(R.layout.activity_adicionar_cidade, null);

                    final TextInputLayout tilNome = mView.findViewById(R.id.text_input_nome);
                    final TextInputLayout tilX = mView.findViewById(R.id.text_input_x);
                    final TextInputLayout tilY = mView.findViewById(R.id.text_input_y);
                    Button btnAdd = mView.findViewById(R.id.btnAdicionar);

                    mBuilder.setView(mView);
                    final AlertDialog dialog = mBuilder.create();
                    dialog.show();

                    btnAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String nome = tilNome.getEditText().getText().toString().trim();
                            String x = tilX.getEditText().getText().toString().trim();
                            String y = tilY.getEditText().getText().toString().trim();

                            if(nome.isEmpty())
                                tilNome.setError("Campo não pode ser nulo");
                            else
                            if(x.isEmpty())
                                tilX.setError("Campo não pode ser nulo");
                            else
                            if(!isFloat(x))
                                tilX.setError("A coordenada não poder ter letras");
                            else
                            if(y.isEmpty())
                                tilY.setError("Campo não pode ser nulo");
                            else
                            if(!isFloat(y))
                                tilX.setError("A coordenada não poder ter letras ou utilize \'.\' ao inves de ,");
                            else {
                                boolean erro = false;
                                for (int i = 0; i < lista.size(); i++) {
                                    try {
                                        if (tabelaCidades.getCidade(nome) != null && tabelaCidades.getCidade(nome).getNome().equals(nome))
                                            erro = true;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                if (erro)
                                    Toast.makeText(getBaseContext(), "Essa cidade já existe", Toast.LENGTH_SHORT).show();
                                else
                                {
                                    try {
                                        Cidade c = new Cidade(lista.size(), nome, Float.parseFloat(x), Float.parseFloat(y));
                                        tabelaCidades.Insert(c);
                                        lista.add(c.getNome());

                                        Bitmap mBitnew = mBitmap.copy(mBitmap.getConfig(), true);
                                        desenharCidade(c, mBitnew);
                                        escreverNome(c, mBitnew);

                                        mBitmap = mBitnew;

                                        String id = String.format("%-2d", c.getId());
                                        String cidadeNome = String.format("%-15s", c.getNome());
                                        String coordX = String.format("&-6d", c.getX());
                                        String coordY = String.format("&5d", c.getY());

                                        String linha = id + cidadeNome + coordX + coordY;

                                        copiaCidade.write(linha.getBytes());

                                        adapter.notifyDataSetChanged();
                                        Toast.makeText(getBaseContext(), "Cidade adicionada com sucesso", Toast.LENGTH_SHORT).show();
                                        dialog.cancel();
                                    }
                                    catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    });
                }
            });

            btnAdicionarCaminho.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                    View mView = getLayoutInflater().inflate(R.layout.activity_adicionar_caminho, null);

                    final TextInputLayout tilOrigem = mView.findViewById(R.id.text_input_origem);
                    final TextInputLayout tilDestino = mView.findViewById(R.id.text_input_destino);
                    final TextInputLayout tilDistancia = mView.findViewById(R.id.text_input_distancia);
                    final TextInputLayout tilTempo = mView.findViewById(R.id.text_input_tempo);
                    Button btnAdd = mView.findViewById(R.id.btnAdicionar);

                    mBuilder.setView(mView);
                    final AlertDialog dialog = mBuilder.create();
                    dialog.show();

                    btnAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String origem = tilOrigem.getEditText().getText().toString().trim();
                            String destino = tilDestino.getEditText().getText().toString().trim();
                            String distancia = tilDistancia.getEditText().getText().toString().trim();
                            String tempo = tilTempo.getEditText().getText().toString().trim();

                            if(origem.isEmpty())
                                tilTempo.setError("Campo não pode ser nulo");
                            else
                            if(destino.isEmpty())
                                tilDestino.setError("Campo não pode ser nulo");
                            else
                            if(distancia.isEmpty())
                                tilDistancia.setError("Campo não pode ser nulo");
                            else
                            if(!isFloat(distancia))
                                tilDistancia.setError("A distancia não poder ter letras");
                            else
                            if(tempo.isEmpty())
                                tilTempo.setError("Campo não pode ser nulo");
                            else
                            if(!isFloat(tempo))
                                tilTempo.setError("O tempo não poder ter letras");
                            else {
                                boolean erro = false;
                                for (int i = 0; i < caminhos.tamanho; i++) {
                                    try {
                                        if (caminhos.get(i).getOrigem().equals(origem) && caminhos.get(i).getDestino().equals(destino))
                                            erro = true;
                                    }
                                    catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                if (erro)
                                    Toast.makeText(getBaseContext(), "Esse caminho já existe", Toast.LENGTH_SHORT).show();
                                else {
                                    Caminho c = new Caminho(origem, destino, Integer.parseInt(distancia), Integer.parseInt(tempo));
                                    caminhos.inserirAposFim(c);

                                    String txtOrigem = String.format("%-15s", c.getOrigem());
                                    String txtDestino= String.format("%-16s", c.getDestino());
                                    String txtDistancia = String.format("&-5d", c.getDistancia());
                                    String txtTempo = String.format("&3d", c.getTempo());

                                    String linha = txtOrigem + txtDestino + txtDistancia + txtTempo;

                                    try {
                                        copiaGrafo.write(linha.getBytes());
                                    }
                                    catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    Toast.makeText(getBaseContext(), "Caminho adicionado com sucesso", Toast.LENGTH_SHORT).show();
                                    dialog.cancel();
                                }
                            }
                        }
                    });
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private void inserirTabela(ListaSimples<Cidade> cidades) {
        for(int i = 0; i < cidades.tamanho; i++) {
            try {
                tabelaCidades.Insert(cidades.get(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void escreverNome(Cidade c, Bitmap mBitnew)
    {
        Canvas canvas = new Canvas(mBitnew);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setTextSize(40);

        Rect bounds = new Rect();
        paint.getTextBounds(c.getNome(), 0, c.getNome().length(), bounds);

        int x = (int)(c.getX() * 1890);
        int y = (int)(c.getY() * 1520);

        canvas.drawText(c.getNome(), x - 80, y - 40, paint);


        ivImagem.setImageBitmap(mBitnew);
    }


    public void desenharCidade(Cidade c, Bitmap mBitnew) {
        Canvas canvas = new Canvas(mBitnew);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        int x = (int)(c.getX() * 1890);
        int y = (int)(c.getY() * 1520);

        canvas.drawCircle(x, y, 15, paint);
        ivImagem.setImageBitmap(mBitnew);
    }

    public void pesquisar() {

    }

    public void fazerTabela()
    {

    }

    public void lerCidades(Scanner sc, ListaSimples<Cidade> cidades)
    {
        String s = "";
        for(int i = 0; sc.hasNextLine(); i++)
        {
            s = sc.nextLine();
            Cidade c = new Cidade();
            c.setId(Integer.parseInt(s.substring(0, 2).trim()));
            c.setNome(s.substring(2, 18));

            String[] x = s.substring(18, 24).trim().split(",");
            c.setX(Float.parseFloat(x[0] + "." + x[1]));

            String[] y = s.substring(24, 29).trim().split(",");
            c.setY(Float.parseFloat(y[0] + "." + y[1]));
            cidades.inserirAposFim(c);
            lista.add(c.getNome());
        }

    }

    public void lerGrafo(Scanner sc)
    {
        String s = "";

        for (int i = 0; sc.hasNextLine(); i++)
        {
            s = sc.nextLine();
            Caminho c = new Caminho();
            c.setOrigem(s.substring(0,15));
            c.setDestino(s.substring(15, 30));
            c.setDistancia(Integer.parseInt(s.substring(30, 35).trim()));
            c.setTempo(Integer.parseInt(s.substring(35, 38).trim()));
            caminhos.inserirAposFim(c);
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

    public boolean isFloat(String n)
    {
        try{
            Float.parseFloat(n);
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

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}
