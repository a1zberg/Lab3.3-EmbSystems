package com.example.lab3_3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private EditText editText1;
    private EditText editText2;
    private EditText editText3;
    private EditText editText4;
    private TextView textResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText1 = findViewById(R.id.editTextx1);
        editText2 = findViewById(R.id.editTextx2);
        editText3 = findViewById(R.id.editTextx3);
        editText4 = findViewById(R.id.editTexty);
        textResult = findViewById(R.id.outputText);
    }

    public void onClickInput(View view) {
        editText1.setText("1");
        editText2.setText("1");
        editText3.setText("2");
        editText4.setText("15");
    }

    public void onClickStart(View view) {
        if (editText1.getText().length() == 0 || editText2.getText().length() == 0 || editText3.getText().length() == 0 || editText4.getText().length() == 0) {
            textResult.setText("Введіть дані!");
        } else {
            int[] result = GeneticAlg();
            if(result == null) textResult.setText("Вибачте, алгоритм не знайшов відповіді.");
            else {
                textResult = findViewById(R.id.outputText);
                textResult.setText("Результат: (" + result[0] + ", " + result[1] + ", " + result[2] + ")");
            }
        }
    }

    private int[] GeneticAlg() {
        int x1 = Integer.parseInt(String.valueOf(editText1.getText()));
        int x2 = Integer.parseInt(String.valueOf(editText2.getText()));
        int x3 = Integer.parseInt(String.valueOf(editText3.getText()));
        int y = Integer.parseInt(String.valueOf(editText4.getText()));
        double invDeltaSum = 0;
        ArrayList<Chromosome> arrChromo = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            arrChromo.add(new Chromosome());
        }
        for (int tryP = 0; tryP < 10; tryP++) {
            for (int i = 0; i < 4; i++) {
                arrChromo.get(i).randomiseGens(y);
            }
            for (int step = 0; step < 100000; step++) {
                for (int i = 0; i < 4; i++) {
                    arrChromo.get(i).findDelta(y, x1, x2, x3);
                    if (arrChromo.get(i).delta == 0) {
                        int[] res = new int[3];
                        res[0] = arrChromo.get(i).a;
                        res[1] = arrChromo.get(i).b;
                        res[2] = arrChromo.get(i).c;
                        return res;
                    }
                    invDeltaSum += ((double) 1 / arrChromo.get(i).delta);
                }
                for (int i = 0; i < 4; i++) {
                    arrChromo.get(i).findChance(invDeltaSum);
                }
//find fittest to Selection
                Chromosome helper = new Chromosome();
                for (int i = 3; i > 0; i--) {
                    for (int j = 0; j < i; j++) {
                        if (arrChromo.get(j).chanceToLive < arrChromo.get(j + 1).chanceToLive) {
                            helper = arrChromo.get(j);
                            arrChromo.set(j, arrChromo.get(j + 1));
                            arrChromo.set(j + 1, helper);
                        }
                    }
                }
//Crossover 1|23  3|21
                int swap;
                swap = arrChromo.get(0).a;
                arrChromo.get(0).a = arrChromo.get(0 + 1).a;
                arrChromo.get(0 + 1).a = swap;
                swap = arrChromo.get(2).c;
                arrChromo.get(2).c = arrChromo.get(2 + 1).c;
                arrChromo.get(2 + 1).c = swap;

//Mutation
                Random rand = new Random();
                if (rand.nextInt(10 + 1) < 5) {
                    swap = rand.nextInt(3 + 1);
                    arrChromo.get(swap).b = arrChromo.get(swap).b ^ (arrChromo.get(3).b >> 1);
                }
                for (int i = 0; i < 4; i++) {
                    arrChromo.get(i).progeny();
                }
            }
        }
        return null;
    }
}

class Chromosome{
    int a;
    int b;
    int c;
    int delta;
    double chanceToLive;
    void randomiseGens(int y){
        Random rand = new Random();
        a = rand.nextInt(y/2+1) +1;;
        b = rand.nextInt(y/2+1) +1;
        c = rand.nextInt(y/2+1) +1;
    }
    void findDelta(int y, int x1, int x2, int x3){
        for (int i = 0; i < 4; i++) {
            delta = Math.abs(y - (x1*a + x2*b + x3*c));
        }
    }
    void findChance(double invDeltaSum){
        chanceToLive = ((double)1/delta)/invDeltaSum;
    }

    void outputGenes(){
        System.out.print("(" + a + ", ");
        System.out.print(b + ", ");
        System.out.println(c + ")");
    }

    void progeny(){
        delta = 0;
        chanceToLive = 0;
    }
}
