package com.example.lab4;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class RadioActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private Spinner radioSpinner;
    private ProgressBar loadingBar;
    private boolean isPaused = false;

    private final String[] radioStations = {
            "Наше радіо",
            "Хіт ФМ",
            "Радіо П'ятниця"
    };

    private final String[] radioUrls = {
            "https://online.nasheradio.ua/NasheRadio_HD",
            "https://online.hitfm.ua/HitFM_HD",
            "https://cast.mediaonline.net.ua/radiopyatnica320"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio);

        radioSpinner = findViewById(R.id.radioSpinner);
        Button playRadioButton = findViewById(R.id.playRadioButton);
        Button pauseRadioButton = findViewById(R.id.pauseRadioButton);
        Button stopRadioButton = findViewById(R.id.stopRadioButton);
        loadingBar = findViewById(R.id.loadingProgressBar);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, radioStations);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        radioSpinner.setAdapter(adapter);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        playRadioButton.setOnClickListener(v -> {
            int selectedIndex = radioSpinner.getSelectedItemPosition();
            String selectedUrl = radioUrls[selectedIndex];

            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.reset();
            }

            loadingBar.setVisibility(View.VISIBLE);

            try {
                mediaPlayer.setDataSource(selectedUrl);
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Помилка підключення", Toast.LENGTH_SHORT).show();
                loadingBar.setVisibility(View.GONE);
                return;
            }

            mediaPlayer.setOnPreparedListener(mp -> {
                loadingBar.setVisibility(View.GONE);
                mp.start();
                isPaused = false;
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                loadingBar.setVisibility(View.GONE);
                Toast.makeText(this, "Сталася помилка при відтворенні", Toast.LENGTH_SHORT).show();
                return true;
            });
        });

        pauseRadioButton.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                isPaused = true;
            } else if (isPaused) {
                mediaPlayer.start();
                isPaused = false;
            }
        });

        stopRadioButton.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying() || isPaused) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                isPaused = false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
}
