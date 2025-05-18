package com.example.lab1;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class StorageActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private TextView txtStorageData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);

        databaseHelper = new DatabaseHelper(this);
        txtStorageData = findViewById(R.id.txtStorageData);
        Button btnDeleteData = findViewById(R.id.btnDeleteData);

        loadData();

        btnDeleteData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseHelper.clearDatabase();
                loadData();
                Toast.makeText(StorageActivity.this, "Всі дані видалено!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadData() {
        String data = databaseHelper.getData();
        if (data.isEmpty()) {
            txtStorageData.setText("База даних порожня.");
        } else {
            txtStorageData.setText(data);
        }
    }
}
