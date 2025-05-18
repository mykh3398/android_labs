package com.example.lab1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentInput extends Fragment {

    private RadioGroup radioGroupFaculties, radioGroupCourses;
    private Button btnOk, btnCancel;
    private TextView txtSelectionResult;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_input, container, false);

        radioGroupFaculties = view.findViewById(R.id.radioGroupFaculties);
        radioGroupCourses = view.findViewById(R.id.radioGroupCourses);
        btnOk = view.findViewById(R.id.btnOk);
        btnCancel = view.findViewById(R.id.btnCancel);
        txtSelectionResult = view.findViewById(R.id.txtSelectionResult);

        btnOk.setOnClickListener(v -> {
            int facultyId = radioGroupFaculties.getCheckedRadioButtonId();
            int courseId = radioGroupCourses.getCheckedRadioButtonId();

            if (facultyId != -1 && courseId != -1) {
                RadioButton selectedFaculty = view.findViewById(facultyId);
                RadioButton selectedCourse = view.findViewById(courseId);

                String result = "Факультет: " + selectedFaculty.getText().toString() +
                        "\nКурс: " + selectedCourse.getText().toString();
                txtSelectionResult.setText(result);
            } else {
                Toast.makeText(getContext(), "Будь ласка, оберіть факультет і курс", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> {
            radioGroupFaculties.clearCheck();
            radioGroupCourses.clearCheck();
            txtSelectionResult.setText("");
        });

        return view;
    }
}
