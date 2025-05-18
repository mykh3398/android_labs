package com.example.lab4;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class AudioPlayerActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private TextView timerText;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean isPlaying = false;

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);

        Button playButton = findViewById(R.id.playButton);
        Button pauseButton = findViewById(R.id.pauseButton);
        Button stopButton = findViewById(R.id.stopButton);
        seekBar = findViewById(R.id.seekBar);
        timerText = findViewById(R.id.timerText);

        String uriString = getIntent().getStringExtra("uri");
        if (uriString == null) {
            Log.e("AudioPlayerActivity", "Received null URI from intent, using fallback local file");
            File testFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "downloaded_media.mp3");
            if (testFile.exists()) {
                uriString = Uri.fromFile(testFile).toString();
                Log.d("AudioPlayerActivity", "Fallback URL: " + uriString);
            } else {
                Log.e("AudioPlayerActivity", "File not found: " + testFile.getAbsolutePath());
                Toast.makeText(this, "Помилка: URI не передано і тестовий файл відсутній", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        }

        Uri uri = Uri.parse(uriString);
        Log.d("AudioPlayerActivity", "Received URI: " + uriString);

        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(this, uri);
            mediaPlayer.prepare();
            setupSeekBar();
        } catch (Exception e) {
            Log.e("AudioPlayerActivity", "Error preparing MediaPlayer: " + e.getMessage());
            Toast.makeText(this, "Помилка відтворення: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }

        playButton.setOnClickListener(v -> {
            if (!isPlaying) {
                mediaPlayer.start();
                isPlaying = true;
                updateSeekBar();
                Log.d("AudioPlayerActivity", "Playback started");
            }
        });

        pauseButton.setOnClickListener(v -> {
            if (isPlaying) {
                mediaPlayer.pause();
                isPlaying = false;
                handler.removeCallbacksAndMessages(null);
                Log.d("AudioPlayerActivity", "Playback paused");
            }
        });

        stopButton.setOnClickListener(v -> {
            if (isPlaying || mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                isPlaying = false;
                handler.removeCallbacksAndMessages(null);
                mediaPlayer.reset();
                try {
                    mediaPlayer.setDataSource(this, uri);
                    mediaPlayer.prepare();
                } catch (Exception e) {
                    Log.e("AudioPlayerActivity", "Error resetting MediaPlayer: " + e.getMessage());
                }
                seekBar.setProgress(0);
                timerText.setText("00:00");
                Log.d("AudioPlayerActivity", "Playback stopped");
            }
        });
    }

    private void setupSeekBar() {
        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
                updateTimer(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void updateSeekBar() {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());
        if (isPlaying) {
            handler.postDelayed(this::updateSeekBar, 1000);
        }
    }

    @SuppressLint("DefaultLocale")
    private void updateTimer(int progress) {
        int minutes = (progress / 1000) / 60;
        int seconds = (progress / 1000) % 60;
        timerText.setText(String.format("%02d:%02d", minutes, seconds));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        handler.removeCallbacksAndMessages(null);
    }
}