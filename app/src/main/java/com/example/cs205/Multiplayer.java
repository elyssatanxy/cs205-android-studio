package com.example.cs205;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Multiplayer extends AppCompatActivity {

    private DatabaseReference mDatabase;

    private String board; // The game board String
    private EditText codeField;
    private String code;
    private char playerLetter; // 'X' or 'O'
    private TextView turnText;

    private String zeroBoard = "O000000000";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        codeField = findViewById(R.id.codeField);

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
    }

    public void createGame(View view) {
        board = zeroBoard;
        code = codeField.getText().toString();
        mDatabase.child(code).setValue(zeroBoard);

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
        code = codeField.getText().toString();
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
                    turnText.setText("Opponent's Turn");
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

        mDatabase.child(code).setValue(board);
        updateBoard(view);
        disableGrid(view);
        turnText.setText("Opponent's Turn");
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
        board = zeroBoard;
        mDatabase.child(code).setValue(zeroBoard);
        if(board.charAt(0) != playerLetter) {
            enableGrid(view);
        } else {
            disableGrid(view);
        }
    }

}