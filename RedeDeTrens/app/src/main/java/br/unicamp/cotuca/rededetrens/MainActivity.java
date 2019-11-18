package br.unicamp.cotuca.rededetrens;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        try {
            TextView tvResultado = findViewById(R.id.txtViewResultados);
            setContentView(R.layout.activity_main);
            Scanner sc = new Scanner(new File("C:\\Temp\\GrafoTremEspanhaPortugal.txt").getAbsolutePath());
            String[] array = new String[500];
            for (int i = 0; sc.hasNextLine(); i++) {
                array[i] = sc.nextLine();
            /*
            Spinner spnD = (Spinner) findViewById(R.id.spnDe);
            Spinner spnP = (Spinner) findViewById(R.id.spnPara);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item,sc.nextLine())

             */

            }
            TextView teste = (TextView) findViewById(R.id.txtViewResultados);
            teste.setText(array[1]);
            sc.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
