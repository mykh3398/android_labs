package com.example.lab4;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class DownloadActivity extends AppCompatActivity {
    private EditText urlEditText;
    private RadioButton audioRadioButton;
    private long downloadID = 0;

    private Button downloadButton;
    private ProgressBar progressBar;

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        urlEditText = findViewById(R.id.urlEditText);
        downloadButton = findViewById(R.id.downloadButton);
        audioRadioButton = findViewById(R.id.audioRadioButton);
        progressBar = findViewById(R.id.downloadProgressBar);

        audioRadioButton.setChecked(true);
        progressBar.setVisibility(View.GONE);

        downloadButton.setOnClickListener(v -> {
            String url = urlEditText.getText().toString();
            if (!url.isEmpty()) {
                Log.d("DownloadActivity", "Starting download for URL: " + url);
                downloadButton.setText("Завантаження...");
                downloadButton.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                downloadFile(url);
            } else {
                Toast.makeText(this, "Будь ласка, введіть URL", Toast.LENGTH_SHORT).show();
            }
        });

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(onDownloadComplete, filter, Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(onDownloadComplete, filter);
        }
    }

    private void downloadFile(String url) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url))
                .setTitle("Завантаження медіа")
                .setDescription("Завантаження файлу...")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true);


        String fileName = audioRadioButton.isChecked() ? "downloaded_media.mp3" : "downloaded_media.mp4";
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "downloaded_audio.mp3");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        request.addRequestHeader("User-Agent", "Mozilla/5.0");

        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        downloadID = downloadManager.enqueue(request);
        Log.d("DownloadActivity", "Download started with ID: " + downloadID);
    }

    private final BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @SuppressLint("Range")
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadID == id) {
                Log.d("DownloadActivity", "Download completed for ID: " + id);
                DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadID);

                try (Cursor cursor = downloadManager.query(query)) {
                    downloadButton.setText("Завантажити");
                    downloadButton.setEnabled(true);
                    progressBar.setVisibility(View.GONE);

                    if (cursor.moveToFirst()) {
                        int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                        if (status == DownloadManager.STATUS_SUCCESSFUL) {
                            String uriString = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                            if (uriString == null || uriString.isEmpty()) {
                                String fileName = audioRadioButton.isChecked() ? "downloaded_media.mp3" : "downloaded_media.mp4";
                                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
                                uriString = Uri.fromFile(file).toString();
                            }

                            Uri uri = Uri.parse(uriString);
                            Log.d("DownloadActivity", "Final URI to pass: " + uriString);

                            File file = new File(uri.getPath());
                            if (file.exists()) {
                                Intent newIntent = audioRadioButton.isChecked() ?
                                        new Intent(DownloadActivity.this, AudioPlayerActivity.class) :
                                        new Intent(DownloadActivity.this, VideoPlayerActivity.class);
                                newIntent.putExtra("uri", uri.toString());
                                startActivity(newIntent);
                            } else {
                                Toast.makeText(context, "Файл не знайдено: " + uriString, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            int reason = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON));
                            String reasonString;
                            switch (reason) {
                                case DownloadManager.ERROR_CANNOT_RESUME:
                                    reasonString = "Не вдалося відновити завантаження";
                                    break;
                                case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                                    reasonString = "Пристрій не знайдено";
                                    break;
                                case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                                    reasonString = "Файл вже існує";
                                    break;
                                case DownloadManager.ERROR_FILE_ERROR:
                                    reasonString = "Помилка файлу";
                                    break;
                                case DownloadManager.ERROR_HTTP_DATA_ERROR:
                                    reasonString = "HTTP помилка";
                                    break;
                                case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                                    reasonString = "Недостатньо памʼяті";
                                    break;
                                case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                                    reasonString = "Занадто багато перенаправлень";
                                    break;
                                case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                                    reasonString = "Невідомий HTTP код";
                                    break;
                                case DownloadManager.ERROR_UNKNOWN:
                                default:
                                    reasonString = "Невідома помилка";
                                    break;
                            }
                            Toast.makeText(context, "Завантаження не вдалося: " + reasonString, Toast.LENGTH_LONG).show();

                        }
                    } else {
                        Toast.makeText(context, "Завантаження не вдалося: інформація відсутня", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onDownloadComplete);
    }
}



// https://download.samplelib.com/mp3/sample-3s.mp3
