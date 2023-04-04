package com.example.cs205;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startSingleplayer(View view) {
        Intent intent = new Intent(MainActivity.this, Singleplayer.class);
        startActivity(intent);
    }
}