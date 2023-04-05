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
    private TextView textTest1;
    private EditText editText1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        textTest1 = findViewById(R.id.textTest1);
        editText1 = findViewById(R.id.editText1);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String obj = snapshot.child("ttt").getValue().toString();
                textTest1.setText(obj);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                textTest1.setText("rip");
            }
        };

        mDatabase.addValueEventListener(postListener);

    }

    public void writeToDatabase(View view) {
        mDatabase.child("ttt").setValue(editText1.getText().toString());
    }

    public void readFromDatabase(View view) {

    }
}