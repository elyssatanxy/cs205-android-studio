package com.example.cs205;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MediaPlayer bgMp = MediaPlayer.create(this, R.raw.game_start);
        bgMp.start();

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void startSingleplayer(View view) {
        Intent intent = new Intent(MainActivity.this, Singleplayer.class);
        startActivity(intent);
    }

    public void startMultiplayer(View view) {
        Intent intent = new Intent(MainActivity.this, Multiplayer.class);
        startActivity(intent);
    }
}