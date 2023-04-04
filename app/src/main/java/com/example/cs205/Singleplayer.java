package com.example.cs205;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Singleplayer extends AppCompatActivity {
    Button grid1;
    Button grid2;
    Button grid3;
    Button grid4;
    Button grid5;
    Button grid6;
    Button grid7;
    Button grid8;
    Button grid9;
    TextView text;
    private ReentrantLock lock = new ReentrantLock();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singleplayer);
        grid1 = findViewById(R.id.grid1);
        grid2 = findViewById(R.id.grid2);
        grid3 = findViewById(R.id.grid3);
        grid4 = findViewById(R.id.grid4);
        grid5 = findViewById(R.id.grid5);
        grid6 = findViewById(R.id.grid6);
        grid7 = findViewById(R.id.grid7);
        grid8 = findViewById(R.id.grid8);
        grid9 = findViewById(R.id.grid9);
        text = findViewById(R.id.computer_value);

        Runnable ComputerThread = new Runnable() {
            @Override
            public void run() {
                Random random = new Random();
                int nextPosition = random.nextInt(9) + 1;

                for (int i = 1; i <= 9; i++) {
                    Button grid = getGrid(i);
                    if (i == nextPosition) {
                        grid.setText("X");
                    }
                }
                text.setText("" + nextPosition);
                grid9.postDelayed(this, 1000);
            }
        };

        grid9.postDelayed(ComputerThread, 1000);
    }

    public Button getGrid(int position) {
        switch (position) {
            case 1:
                return grid1;
            case 2:
                return grid2;
            case 3:
                return grid3;
            case 4:
                return grid4;
            case 5:
                return grid5;
            case 6:
                return grid6;
            case 7:
                return grid7;
            case 8:
                return grid8;
            case 9:
                return grid9;
            default:
                throw new IllegalArgumentException("Invalid grid position: " + position);
        }
    }
}