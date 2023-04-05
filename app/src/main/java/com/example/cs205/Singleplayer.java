package com.example.cs205;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
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
    Condition computerTurnDone = lock.newCondition();
    Condition userTurnDone = lock.newCondition();
    volatile int index = 1;
    volatile ArrayList<Integer> availableGrids = new ArrayList<>();
    volatile int[] grids = {0, 0, 0, 0, 0, 0, 0, 0, 0};
    AtomicInteger val = new AtomicInteger(10);
    volatile boolean gameOver = false;
    private final Object availableGridsLock = new Object();

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
        text = findViewById(R.id.turnText);

        for (int i = 1; i <= 9; i++) {
            availableGrids.add(i);
        }

        Runnable computerThread = new Runnable() {
            @Override
            public void run() {
                lock.lock();
                try {
                    text.setText("HI");
                    if (index % 2 == 0) {
                        userTurnDone.await();
                    }

                    synchronized (availableGrids) {
                        if (!availableGrids.isEmpty()) {
                            Random random = new Random();
                            int nextIndex = random.nextInt(availableGrids.size());
                            int nextPosition = availableGrids.get(nextIndex);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Button grid = getGrid(nextPosition);
                                    grid.setText("X");

                                    grids[nextPosition - 1] = 1;

                                    synchronized (availableGrids) {
                                        Iterator<Integer> iterator = availableGrids.iterator();
                                        while (iterator.hasNext()) {
                                            int value = iterator.next();
                                            if (value == nextPosition) {
                                                iterator.remove();
                                                break;
                                            }
                                        }
                                    }
                                }
                            });

                            index++;
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    userTurnDone.signal();
                    lock.unlock();
                }
            }
        };

        Runnable userThread = new Runnable() {
            @Override
            public void run() {
                lock.lock();
                try {
                    if (index % 2 == 1) {
                        computerTurnDone.await();
                    }

                    while (val.get() == 10) {
                        continue;
                    }

                    synchronized (availableGrids) {
                        if (!availableGrids.isEmpty()) {
                            index++;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if (val.get() == 10) {
                                        return;
                                    }

                                    if (availableGrids.contains(val.get())) {
                                        Button grid = getGrid(val.get());
                                        grid.setText("O");

                                        grids[val.get() - 1] = 2;

                                        Iterator<Integer> iterator = availableGrids.iterator();
                                        while (iterator.hasNext()) {
                                            int value = iterator.next();
                                            if (value == val.get() - 1) {
                                                iterator.remove();
                                                break;
                                            }
                                        }
                                    }
                                    val.set(10);

                                    text.setText("User" + index);
                                }
                            });
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    computerTurnDone.signal();
                    lock.unlock();
                }
            }
        };


        Thread ComputerThread = new Thread(computerThread);
        Thread UserThread = new Thread(userThread);
        ComputerThread.start();
        UserThread.start();
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

    public void move(View v) {
        switch (v.getId()) {
            case R.id.grid1:
                val.set(1);
                break;
            case R.id.grid2:
                val.set(2);
                break;
            case R.id.grid3:
                val.set(3);
                break;
            case R.id.grid4:
                val.set(4);
                break;
            case R.id.grid5:
                val.set(5);
                break;
            case R.id.grid6:
                val.set(6);
                break;
            case R.id.grid7:
                val.set(7);
                break;
            case R.id.grid8:
                val.set(8);
                break;
            case R.id.grid9:
                val.set(9);
                break;
        }
    }
}