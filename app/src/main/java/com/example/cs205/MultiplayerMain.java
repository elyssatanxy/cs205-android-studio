package com.example.cs205;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MultiplayerMain extends AppCompatActivity {

    Button createButton;
    Button joinButton;
    TextView codeField;

    static boolean isCodeMaker = true;
    static String code = "null";
    static boolean codeFound = false;
    static boolean checkTemp = true;
    static String keyValue = "null";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multiplayer_main);

        createButton = findViewById(R.id.createButton);
        joinButton = findViewById(R.id.joinButton);
        codeField = findViewById(R.id.codeField);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codeFound = false;
                checkTemp = true;
                keyValue = "null";
                code = codeField.getText().toString();

                if(code != "null" && !(code.isEmpty())) {
                    isCodeMaker = true;
                    FirebaseDatabase.getInstance().getReference().child("codes").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean check = isValueAvailable(snapshot, code);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if(check == true) {
                                        ;
                                    } else {
                                        FirebaseDatabase.getInstance().getReference().child("codes").push().setValue(code);
                                        isValueAvailable(snapshot, code);
                                        checkTemp = false;
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                accepted();
                                                // Toast.makeText(onlineGameActivity.this, "Please don't go back", Toast.LENGTH_SHORT).show()
                                            }
                                        }, 300);
                                    }
                                }
                            }, 2000);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter valid code", Toast.LENGTH_SHORT);
                }
            }
        });

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codeFound = false;
                checkTemp = true;
                keyValue = "null";
                code = codeField.getText().toString();
                if(code != "null" && !(code.isEmpty())) {
                    isCodeMaker = false;
                    FirebaseDatabase.getInstance().getReference().child("codes").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean data = isValueAvailable(snapshot, code);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if(data == true) {
                                        codeFound = true;
                                        accepted();
                                    } else {
                                        ;
                                    }
                                }
                            }, 2000);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    })
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter valid code", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void accepted() {
        startActivity(new Intent(MultiplayerMain.this, onlineGameActivity.class));
    }

    public boolean isValueAvailable(DataSnapshot dataSnapshot, String code) {
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            String val = snapshot.getValue().toString();
            if(val == code) {
                keyValue = snapshot.getKey().toString();
                return true;
            }
        }
        return false;
    }
}