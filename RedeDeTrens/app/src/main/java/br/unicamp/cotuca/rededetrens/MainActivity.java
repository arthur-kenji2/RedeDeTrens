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
import java.nio.Buffer;
import java.text.NumberFormat;
import java.util.*;

public class MainActivity extends AppCompatActivity {

    private Button btnBuscar, btnAdicionarCidade, btnAdicionarCaminho;
    private ArrayList<String> lista;
    private ImageView ivImagem;
    private TextView tvResultado;
    private Grafo grafoTempo, grafoDistancia;
    private ListaSimples<Caminho> caminhos;
    private Bitmap mBitmap;
    private OutputStream copiaCidade, copiaGrafo;
    private BucketHash tabelaCidades;
    private Spinner spnDe, spnPara;
    private RadioButton rbEscolha;
    private RadioGroup rbGroup;
    private ListaSimples<Cidade> cidades;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            lista = new ArrayList<String>();
            caminhos = new ListaSimples<>();
            cidades = new ListaSimples<>();

            tvResultado = findViewById(R.id.txtViewResultados);
            btnAdicionarCaminho = findViewById(R.id.btnCaminho);
            btnAdicionarCidade = findViewById(R.id.btnCidade);
            btnBuscar = findViewById(R.id.btnBuscar);
            ivImagem = findViewById(R.id.imgView);
            rbGroup = findViewById(R.id.rbGroup);
            spnDe = findViewById(R.id.spnDe);
            spnPara = findViewById(R.id.spnPara);

            tabelaCidades = new BucketHash();
            grafoDistancia = new Grafo();
            grafoTempo = new Grafo();

            AssetManager ass = getAssets();
            FileInputStream fis = null;

            try{
                fis = openFileInput("Cidades");

                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr);

                lerCidades(br, cidades);
                inserirTabela(cidades);
            }
            catch (FileNotFoundException e){
                try {
                    Scanner sc = new Scanner(ass.open("Cidades"));
                    lerCidades(sc, cidades);
                    inserirTabela(cidades);
                    sc.close();
                }
                catch (Exception err)
                {
                    err.printStackTrace();
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
            finally {
                if(fis != null)
                    fis.close();
            }

            try{
                fis = openFileInput("GrafoTrem");
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr);
                lerGrafo(br);
            }
            catch (FileNotFoundException e){
                try {
                    Scanner sc = new Scanner(ass.open("GrafoTrem"));
                    lerGrafo(sc);
                    sc.close();

                }
                catch (Exception err)
                {
                    err.printStackTrace();
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
            finally {
                if(fis != null)
                    fis.close();
            }

            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, lista);

            spnDe.setAdapter(adapter);
            spnPara.setAdapter(adapter);

            mBitmap = decodeSampledBitmapFromResource(getResources(), R.drawable.mapaespanhaportugal, ivImagem.getMaxWidth(), ivImagem.getMaxHeight());
            ivImagem.setImageBitmap(mBitmap);
            Bitmap mBitnew = mBitmap.copy(mBitmap.getConfig(), true);

            for(int i = 0; i < cidades.tamanho; i++)
                desenharCidade(cidades.get(i), mBitnew);

            mBitmap = mBitnew;

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

                                        grafoTempo.novoVertice(c.getNome());
                                        grafoDistancia.novoVertice(c.getNome());

                                        Bitmap mBitnew = mBitmap.copy(mBitmap.getConfig(), true);
                                        desenharCidade(c, mBitnew);
                                        escreverNome(c, mBitnew);

                                        mBitmap = mBitnew;

                                        cidades.inserirAposFim(c);

                                        FileOutputStream fos = openFileOutput("Cidades", MODE_PRIVATE);
                                        for(int i = 0; i < cidades.tamanho; i++) {
                                            Cidade ci = tabelaCidades.getCidade(cidades.get(i).getNome());

                                            String id = String.format("%-2d", ci.getId());
                                            String cidadeNome = String.format("%-16s", ci.getNome());
                                            String coordX = String.format("%.4f", ci.getX());
                                            String coordY = String.format("%.3f", ci.getY());

                                            String linha = id + cidadeNome + coordX + coordY + "\n";

                                            fos.write(linha.getBytes());
                                        }

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
                                        if (caminhos.get(i).getOrigem().equals(origem) && caminhos.get(i).getDestino().equals(destino)) {
                                            erro = true;
                                            break;
                                        }
                                    }
                                    catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                if (erro)
                                    Toast.makeText(getBaseContext(), "Esse caminho já existe", Toast.LENGTH_SHORT).show();
                                else {
                                    boolean erro1 = true, erro2 = true;
                                    for (int i = 0; i < lista.size(); i++) {
                                        try {
                                            if (lista.get(i).equals(origem)) {
                                                erro1 = false;
                                                break;
                                            }
                                        }
                                        catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    for (int i = 0; i < lista.size(); i++) {
                                        try {
                                            if (lista.get(i).equals(destino)) {
                                                erro2 = false;
                                                break;
                                            }
                                        }
                                        catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    if (erro1 || erro2)
                                        Toast.makeText(getBaseContext(), "Cidades inexistentes", Toast.LENGTH_SHORT).show();
                                    else {
                                        try {
                                            Caminho c = new Caminho(origem, destino, Integer.parseInt(distancia), Integer.parseInt(tempo));
                                            caminhos.inserirAposFim(c);

                                            FileOutputStream fos = openFileOutput("GrafoTrem", MODE_PRIVATE);
                                            for(int i = 0; i < cidades.tamanho; i++) {
                                                Caminho ca = caminhos.get(i);

                                                String txtOrigem = String.format("%-16s", ca.getOrigem());
                                                String txtDestino = String.format("%-16s", ca.getDestino());
                                                String txtDistancia = String.format("%-5d", ca.getDistancia());
                                                String txtTempo = String.format("%-3d", ca.getTempo());

                                                String linha = txtOrigem + txtDestino + txtDistancia + txtTempo + "\n";
                                                fos.write(linha.getBytes());
                                            }

                                            int idOrigem = tabelaCidades.getCidade(origem).getId();
                                            int idDestino = tabelaCidades.getCidade(destino).getId();

                                            grafoDistancia.novaAresta(idOrigem, idDestino, c.getDistancia());
                                            grafoTempo.novaAresta(idOrigem, idDestino, c.getTempo());

                                            Toast.makeText(getBaseContext(), "Caminho adicionado com sucesso", Toast.LENGTH_SHORT).show();
                                            dialog.cancel();
                                        }
                                        catch (Exception e)
                                        {
                                            e.printStackTrace();
                                        }
                                    }
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
        String origem  = (String) spnDe.getSelectedItem();
        String destino = (String) spnPara.getSelectedItem();

        int radioId = rbGroup.getCheckedRadioButtonId();

        rbEscolha = findViewById(radioId);

        if(destino != null && !destino.equals("") && origem != null || !origem.equals(""))
        {
            if(!destino.equals(origem)) {
                int idOrigem = tabelaCidades.getCidade(origem).getId();
                int idDestino = tabelaCidades.getCidade(destino).getId();

                if (rbEscolha.getText().equals("Distancia")) {
                    try {
                        String[] percurso = grafoDistancia.caminho(idOrigem, idDestino);
                        Bitmap mBitnew = mBitmap.copy(mBitmap.getConfig(), true);
                        if (percurso != null) {
                            tvResultado.setText(percurso[0]);
                            for(int i = 1; i < percurso.length; i++) {
                                if(percurso[i] == null)
                                    break;
                                if (isNumber(percurso[i])) {
                                    tvResultado.setText(tvResultado.getText() + " distancia total = " + percurso[i] + "km");
                                    break;
                                }
                                tvResultado.setText(tvResultado.getText() + " ---> " + percurso[i]);
                                desenharPercurso(percurso[i-1],percurso[i], mBitnew);
                            }
                        }
                        else
                            Toast.makeText(getBaseContext(), "Não existe existe caminhos entre essas cidades", Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                else {
                    try {
                        String[] percurso = grafoTempo.caminho(idOrigem, idDestino);

                        if (percurso != null) {
                            tvResultado.setText(percurso[0]);
                            for(int i = 1; i < percurso.length; i++) {
                                if(percurso[i] == null)
                                    break;
                                if (isNumber(percurso[i])) {
                                    tvResultado.setText(tvResultado.getText() + " distancia total = " + percurso[i] + "km");
                                    break;
                                }
                                tvResultado.setText(tvResultado.getText() + " ---> " + percurso[i]);
                            }
                        }
                        else
                            Toast.makeText(getBaseContext(), "Não existe existe caminhos entre essas cidades", Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            else
                Toast.makeText(getBaseContext(), "Origem e Destino são iguais", Toast.LENGTH_SHORT).show();
        }
    }

    private void desenharPercurso(String or, String des, Bitmap mBitnew) {
        Canvas canvas = new Canvas(mBitnew);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        Float espessura = 4.0f;
        paint.setStrokeWidth(espessura);

        Cidade origem = tabelaCidades.getCidade(or);
        Cidade destino = tabelaCidades.getCidade(des);

        int startX = (int) (origem.getX() * 1890);
        int startY = (int) (origem.getY() * 1520);

        int finalX = (int) (destino.getX() * 1890);
        int finalY = (int) (destino.getY() * 1520);

        canvas.drawLine(startX, startY, finalX, finalY, paint);
        ivImagem.setImageBitmap(mBitnew);
    }

    public void lerCidades(Scanner sc, ListaSimples<Cidade> cidades)
    {
        String s = "";
        for(int i = 0; sc.hasNextLine(); i++)
        {
            s = sc.nextLine();
            Cidade c = new Cidade();
            c.setId(Integer.parseInt(s.substring(0, 2).trim()));
            c.setNome(s.substring(2, 18).trim());

            String[] x = s.substring(18, 24).trim().split(",");
            c.setX(Float.parseFloat(x[0] + "." + x[1]));

            String[] y = s.substring(24, 29).trim().split(",");
            c.setY(Float.parseFloat(y[0] + "." + y[1]));
            cidades.inserirAposFim(c);

            grafoDistancia.novoVertice(c.getNome());
            grafoTempo.novoVertice(c.getNome());
            lista.add(c.getNome());
        }

    }

    public void lerCidades(BufferedReader br, ListaSimples<Cidade> cidades)
    {
        String s = "";
        try {
            for (int i = 0; (s = br.readLine()) != null; i++) {
                Cidade c = new Cidade();
                c.setId(Integer.parseInt(s.substring(0, 2).trim()));
                c.setNome(s.substring(2, 18).trim());

                if(isFloat(s.substring(18, 24).trim()))
                    c.setX(Float.parseFloat(s.substring(18, 24).trim()));
                else {
                    String[] x = s.substring(18, 24).trim().split(",");
                    c.setX(Float.parseFloat(x[0] + "." + x[1]));
                }

                if(isFloat(s.substring(24, 29).trim()))
                    c.setY(Float.parseFloat(s.substring(24, 29).trim()));
                else {
                        String[] y = s.substring(24, 29).trim().split(",");
                        c.setY(Float.parseFloat(y[0] + "." + y[1]));
                }
                cidades.inserirAposFim(c);

                grafoDistancia.novoVertice(c.getNome());
                grafoTempo.novoVertice(c.getNome());
                lista.add(c.getNome());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void lerGrafo(Scanner sc)
    {
        String s = "";

        for (int i = 0; sc.hasNextLine(); i++)
        {
            s = sc.nextLine();
            Caminho c = new Caminho();
            c.setOrigem(s.substring(0,15).trim());
            c.setDestino(s.substring(15, 30).trim());
            c.setDistancia(Integer.parseInt(s.substring(30, 35).trim()));
            c.setTempo(Integer.parseInt(s.substring(35, 39).trim()));

            int idOrigem  = tabelaCidades.getCidade(c.getOrigem().trim()).getId();
            int idDestino = tabelaCidades.getCidade(c.getDestino().trim()).getId();

            grafoDistancia.novaAresta(idOrigem, idDestino, c.getDistancia());
            grafoDistancia.novaAresta(idOrigem, idDestino, c.getDistancia());

            caminhos.inserirAposFim(c);
        }
        sc.close();
    }

    public void lerGrafo(BufferedReader br)
    {
        String s = "";
        try {
            for (int i = 0; (s = br.readLine()) != null; i++) {
                Caminho c = new Caminho();
                c.setOrigem(s.substring(0, 15).trim());
                c.setDestino(s.substring(15, 30).trim());
                c.setDistancia(Integer.parseInt(s.substring(30, 35).trim()));
                c.setTempo(Integer.parseInt(s.substring(35, 40).trim()));

                int idOrigem = tabelaCidades.getCidade(c.getOrigem().trim()).getId();
                int idDestino = tabelaCidades.getCidade(c.getDestino().trim()).getId();

                grafoDistancia.novaAresta(idOrigem, idDestino, c.getDistancia());
                grafoDistancia.novaAresta(idOrigem, idDestino, c.getDistancia());

                caminhos.inserirAposFim(c);
            }
            br.close();
        }
        catch (Exception e) {
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
