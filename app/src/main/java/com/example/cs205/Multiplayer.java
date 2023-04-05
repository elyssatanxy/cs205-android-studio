package com.example.cs205;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

    private TextView textTest1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        codeField = findViewById(R.id.codeField);

        textTest1 = findViewById(R.id.textTest1);


//        ValueEventListener postListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String obj = snapshot.child("ttt").getValue().toString();
//                textTest1.setText(obj);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                textTest1.setText("rip");
//            }
//        };
//
//        mDatabase.addValueEventListener(postListener);

    }

    public void createGame(View view) {
//        mDatabase.child("ttt").setValue(codeField.getText().toString());
        String zeroBoard = "000000000";
        code = codeField.getText().toString();
        mDatabase.child(code).setValue(zeroBoard);

//        createListener(view);
    }

    public void joinGame(View view) {
        code = codeField.getText().toString();
        createListener(view);
    }

    public void createListener(View view) {
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String obj = snapshot.child(code).getValue().toString();
                textTest1.setText(obj);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
//                textTest1.setText("rip");
                ;
            }
        };

        mDatabase.addValueEventListener(postListener);
    }

}