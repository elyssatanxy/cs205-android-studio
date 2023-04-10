package com.example.cs205;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Multiplayer extends AppCompatActivity {

    private DatabaseReference mDatabase;

    private String board; // The game board String
    private EditText codeField;
    private Button createButton;
    private Button joinButton;
    private String code;
    private char playerLetter; // 'X' or 'O'
    private TextView turnText;

    private String zeroBoard = "000000000";

    Button grid1;
    Button grid2;
    Button grid3;
    Button grid4;
    Button grid5;
    Button grid6;
    Button grid7;
    Button grid8;
    Button grid9;

    Button[] gridArray;
    MediaPlayer bgMp;

    MediaPlayer effectsMp;
    Vibrator v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        bgMp = MediaPlayer.create(this, R.raw.game_music);
        effectsMp = MediaPlayer.create(this, R.raw.button_press);
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        bgMp.setLooping(true);
        bgMp.start();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        codeField = findViewById(R.id.codeField);
        createButton = findViewById(R.id.createButton);
        joinButton = findViewById(R.id.joinButton);

        turnText = findViewById(R.id.turnText);

        grid1 = findViewById(R.id.grid1);
        grid2 = findViewById(R.id.grid2);
        grid3 = findViewById(R.id.grid3);
        grid4 = findViewById(R.id.grid4);
        grid5 = findViewById(R.id.grid5);
        grid6 = findViewById(R.id.grid6);
        grid7 = findViewById(R.id.grid7);
        grid8 = findViewById(R.id.grid8);
        grid9 = findViewById(R.id.grid9);

        gridArray = new Button[9];
        gridArray[0] = grid1;
        gridArray[1] = grid2;
        gridArray[2] = grid3;
        gridArray[3] = grid4;
        gridArray[4] = grid5;
        gridArray[5] = grid6;
        gridArray[6] = grid7;
        gridArray[7] = grid8;
        gridArray[8] = grid9;

        disableGrid(findViewById(android.R.id.content).getRootView());
    }

    public void createGame(View view) {
        if(codeField.getText().toString().isEmpty()) {
            Toast.makeText(this, "Code cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        codeField.setEnabled(false);
        createButton.setEnabled(false);
        joinButton.setEnabled(false);

        Random random = new Random();
        int x = random.nextInt(3);
        String startingPlayer = "O";
        if(x == 1) {
            startingPlayer = "X";
        }
        board = startingPlayer.concat(zeroBoard);
        code = codeField.getText().toString();
        mDatabase.child(code).setValue(board);

        playerLetter = 'X';
        createListener(view);

        if(board.charAt(0) != playerLetter) {
            turnText.setText("Your Turn");
            enableGrid(view);
        } else {
            turnText.setText("Opponent's Turn");
            disableGrid(view);
        }
    }

    public void joinGame(View view) {
        if(codeField.getText().toString().isEmpty()) {
            Toast.makeText(this, "Code cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        code = codeField.getText().toString();

        codeField.setEnabled(false);
        createButton.setEnabled(false);
        joinButton.setEnabled(false);

        playerLetter = 'O';
        createListener(view);
    }

    public void createListener(View view) {
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                board = snapshot.child(code).getValue().toString();
                updateBoard(view);

                if(board.charAt(0) != playerLetter) {
                    enableGrid(view);
                    turnText.setText("Your Turn");
                } else {
                    disableGrid(view);
                    turnText.setText("Opponent's Turn");
                }

                if(checkWin(view) == 1) {
                    bgMp.stop();
                    effectsMp.stop();
                    bgMp.reset();
                    v.vibrate(500);

                    if(board.charAt(0) == playerLetter) {
                        bgMp = MediaPlayer.create(Multiplayer.this, R.raw.you_win);
                        bgMp.start();
                        turnText.setText("You Win!");

                        Context context = getApplicationContext();
                        CharSequence text = "Game Over! You Win!";
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    } else {
                        bgMp = MediaPlayer.create(Multiplayer.this, R.raw.oh_no);
                        bgMp.start();
                        turnText.setText("Opponent Wins!");

                        Context context = getApplicationContext();
                        CharSequence text = "Game Over! You Lose!";
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                    disableGrid(view);
                } else if (checkWin(view) == 2) {
                    bgMp.stop();
                    effectsMp.stop();
                    bgMp.reset();
                    bgMp = MediaPlayer.create(Multiplayer.this, R.raw.you_win);
                    bgMp.start();
                    v.vibrate(500);
                    turnText.setText("Tie!");
                    disableGrid(view);

                    Context context = getApplicationContext();
                    CharSequence text = "Game Over! Tie!";
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
//                textTest1.setText("rip");
                ;
            }
        };

        mDatabase.addValueEventListener(postListener);
    }

    public void updateBoard(View view) {
        board = board.replaceAll("0", " ");

        for(int i = 0; i < 9; i++) {
            gridArray[i].setText(String.valueOf(board.charAt(i+1)));
        }
    }

    public void move (View view) {
        StringBuilder tempBoard = new StringBuilder(board);
        effectsMp.start();
        switch (view.getId()) {
            case R.id.grid1:
                tempBoard.setCharAt(1, playerLetter);
                board = String.valueOf(tempBoard);
                break;
            case R.id.grid2:
                tempBoard.setCharAt(2, playerLetter);
                board = String.valueOf(tempBoard);
                break;
            case R.id.grid3:
                tempBoard.setCharAt(3, playerLetter);
                board = String.valueOf(tempBoard);
                break;
            case R.id.grid4:
                tempBoard.setCharAt(4, playerLetter);
                board = String.valueOf(tempBoard);
                break;
            case R.id.grid5:
                tempBoard.setCharAt(5, playerLetter);
                board = String.valueOf(tempBoard);
                break;
            case R.id.grid6:
                tempBoard.setCharAt(6, playerLetter);
                board = String.valueOf(tempBoard);
                break;
            case R.id.grid7:
                tempBoard.setCharAt(7, playerLetter);
                board = String.valueOf(tempBoard);
                break;
            case R.id.grid8:
                tempBoard.setCharAt(8, playerLetter);
                board = String.valueOf(tempBoard);
                break;
            case R.id.grid9:
                tempBoard.setCharAt(9, playerLetter);
                board = String.valueOf(tempBoard);
                break;
        }

        tempBoard.setCharAt(0, playerLetter);
        board = String.valueOf(tempBoard);

        if(checkWin(view) == 1) {
            StringBuilder tBoard = new StringBuilder(board);
            tBoard.setCharAt(0, playerLetter);
            board = String.valueOf(tBoard);
            mDatabase.child(code).setValue(board);
        }

        mDatabase.child(code).setValue(board);
        updateBoard(view);
        turnText.setText("Opponent's Turn");

        disableGrid(view);
    }

    // Enables the playable buttons
    public void enableGrid(View view) {
        for(int i = 0; i < 9; i++) {
            if(board.charAt(i + 1) == ' ') {
                gridArray[i].setEnabled(true);
            }
        }
    }

    // Disables the grid
    public void disableGrid(View view) {
        for(int i = 0; i < 9; i++) {
            gridArray[i].setEnabled(false);
        }
    }

    public void reset(View view) {
        if(!bgMp.isPlaying()){
            bgMp.reset();
            bgMp = MediaPlayer.create(this, R.raw.game_music);
            bgMp.setLooping(true);
            bgMp.start();
        }
        Random random = new Random();
        int x = random.nextInt(3);
        String startingPlayer = "O";
        if(x == 1) {
            startingPlayer = "X";
        }
        board = startingPlayer + zeroBoard;
        code = codeField.getText().toString();
        mDatabase.child(code).setValue(board);
        if(board.charAt(0) != playerLetter) {
            enableGrid(view);
        } else {
            disableGrid(view);
        }
    }

    public int checkWin(View view) {
        // If draw
        if( !board.contains(" ") && !board.contains("0") ) {
            return 2;
        }

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

         String tempBoard = board.substring(1);

         for(int i = 0; i < winningSequences.length; i++) {
             if(tempBoard.matches(winningSequences[i])) {
                 return 1;
             }
         }
         return 0;
    }

    public void back(View view) {
        bgMp.stop();
        finish();
    }
    @Override
    public void finish() {
        super.finish();
        bgMp.stop();
    }

}