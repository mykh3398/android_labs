package com.example.lab1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private RadioGroup radioGroupFaculties, radioGroupCourses;
    private Button btnOk, btnCancel, btnViewData;
    private TextView txtSelectionResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_input);

        radioGroupFaculties = findViewById(R.id.radioGroupFaculties);
        radioGroupCourses = findViewById(R.id.radioGroupCourses);
        btnOk = findViewById(R.id.btnOk);
        btnCancel = findViewById(R.id.btnCancel);
        txtSelectionResult = findViewById(R.id.txtSelectionResult);
        btnViewData = findViewById(R.id.btnViewData);

        DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int facultyId = radioGroupFaculties.getCheckedRadioButtonId();
                int courseId = radioGroupCourses.getCheckedRadioButtonId();

                if (facultyId != -1 && courseId != -1) {
                    RadioButton selectedFaculty = findViewById(facultyId);
                    RadioButton selectedCourse = findViewById(courseId);

                    String faculty = selectedFaculty.getText().toString();
                    String course = selectedCourse.getText().toString();

                    String result = "Факультет: " + faculty + "\nКурс: " + course;
                    txtSelectionResult.setText(result);

                    // Зберегти у базу даних
                    dbHelper.insertData(faculty, course);
                } else {
                    Toast.makeText(MainActivity.this, "Будь ласка, оберіть факультет і курс", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnViewData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StorageActivity.class);
                startActivity(intent);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioGroupFaculties.clearCheck();
                radioGroupCourses.clearCheck();
                txtSelectionResult.setText("");
            }
        });
    }
}
