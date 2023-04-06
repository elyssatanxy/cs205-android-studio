package com.example.cs205;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
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
    volatile String board = "000000000";
    AtomicInteger val = new AtomicInteger(10);
    volatile boolean gameOver = false;
    private final Object availableGridsLock = new Object();
    MediaPlayer bg_mp;

    MediaPlayer effects_mp;
    Vibrator v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singleplayer);
        bg_mp = MediaPlayer.create(this, R.raw.game_music);
        effects_mp = MediaPlayer.create(this, R.raw.button_press);
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        bg_mp.setLooping(true);
        bg_mp.start();

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
                    if (index % 2 == 0) {
                        userTurnDone.await();
                    }

                    com_Move();
                    if (checkWin()){
                        play_winner_sound();
                    }
                    System.out.println(board);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    computerTurnDone.signal();
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

                    user_move();
                    if (checkWin()){
                        play_winner_sound();
                    }
                    System.out.println(board);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    userTurnDone.signal();
                    lock.unlock();
                }
            }
        };

        Runnable schedulerThread = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 9; i++) {
                    if (i % 2 == 0) {
                        Thread ComputerThread = new Thread(computerThread);
                        ComputerThread.start();
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Thread UserThread = new Thread(userThread);
                        UserThread.start();
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }

        };

        Thread SchedulerThread = new Thread(schedulerThread);
        SchedulerThread.start();
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

    public void com_Move(){
        synchronized (availableGridsLock) {
            Log.i("COMPUTER THREAD", "ARRAY CURR" + availableGrids);
            if (!availableGrids.isEmpty()) {
                Random random = new Random();
                int nextIndex = random.nextInt(availableGrids.size());
                int nextPosition = availableGrids.get(nextIndex);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        text.setText("Your Turn");
                        Button grid = getGrid(nextPosition);
                        grid.setText("X");
                        StringBuilder temp = new StringBuilder(board);
                        temp.setCharAt(nextPosition - 1, 'X');
                        board = String.valueOf(temp);
                    }

                });

                synchronized (availableGridsLock) {
                    Iterator<Integer> iterator = availableGrids.iterator();
                    while (iterator.hasNext()) {
                        int value = iterator.next();
                        if (value == nextPosition) {
                            iterator.remove();
                            break;
                        }
                    }
                }


                index++;
            }
        }
    }

    public void user_move(){
        effects_mp.start();
        synchronized (availableGridsLock) {
            Log.i("USER", "this" + availableGrids);
            if (!availableGrids.isEmpty()) {
                index++;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        text.setText("Computer's Turn");
                        if (val.get() == 10) {
                            return;
                        }
                        Button grid = getGrid(val.get());
                        grid.setText("O");
                        StringBuilder temp = new StringBuilder(board);
                        temp.setCharAt(val.get() - 1, 'O');
                        board = String.valueOf(temp);
                        val.set(10);
                    }
                });

                synchronized (availableGridsLock) {
                    Iterator<Integer> iterator = availableGrids.iterator();
                    while (iterator.hasNext()) {
                        int value = iterator.next();
                        if (value == val.get()) {
                            iterator.remove();
                            break;
                        }
                    }
                }
            }
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

    public boolean checkWin() {
        String[] winningSequences = {
                "XXX......",
                "...XXX...",
                "......XXX",
                "X..X..X..",
                ".X..X..X.",
                "..X..X..X",
                "X...X...X",
                "..X.X.X..",
                "OOO......",
                "...OOO...",
                "......OOO",
                "O..O..O..",
                ".O..O..O.",
                "..O..O..O",
                "O...O...O",
                "..O.O.O.."
        };

        String tempBoard = board;

        for(int i = 0; i < winningSequences.length; i++) {
            if(tempBoard.matches(winningSequences[i])) {
//                 StringBuilder tBoard = new StringBuilder(board);
//                 tBoard.setCharAt(0, playerLetter);
//                 board = String.valueOf(tBoard);
//                 mDatabase.child(code).setValue(board);
                System.out.println("winnerrrr");
                return true;
            }
        }
        return false;
    }

    public void play_winner_sound(){
        bg_mp.stop();
        effects_mp.stop();
        bg_mp.reset();
        bg_mp = MediaPlayer.create(Singleplayer.this, R.raw.you_win);
        bg_mp.start();
        v.vibrate(500);
    }
}