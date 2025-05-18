package com.example.lab4;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private final ActivityResultLauncher<String> pickAudioLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    Intent intent = new Intent(MainActivity.this, AudioPlayerActivity.class);
                    startActivity(intent);
                }
            }
    );

    private final ActivityResultLauncher<String> pickVideoLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    Intent intent = new Intent(MainActivity.this, VideoPlayerActivity.class);
                    intent.putExtra("uri", uri.toString());
                    startActivity(intent);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button openRadioButton = findViewById(R.id.openRadioButton);
        openRadioButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RadioActivity.class);
            startActivity(intent);
        });

        Button playLocalAudioButton = findViewById(R.id.playLocalAudioButton);
        playLocalAudioButton.setOnClickListener(v -> pickAudioLauncher.launch("audio/*"));

        Button playLocalVideoButton = findViewById(R.id.playLocalVideoButton);
        playLocalVideoButton.setOnClickListener(v -> pickVideoLauncher.launch("video/*"));

        Button downloadMediaButton = findViewById(R.id.downloadMediaButton);
        downloadMediaButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DownloadActivity.class);
            startActivity(intent);
        });
    }
}