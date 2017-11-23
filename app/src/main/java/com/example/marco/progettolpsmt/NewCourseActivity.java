package com.example.marco.progettolpsmt;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marco.progettolpsmt.backend.Argument;
import com.example.marco.progettolpsmt.backend.Course;
import com.example.marco.progettolpsmt.backend.Exam;
import com.example.marco.progettolpsmt.managers.DBManager;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

/**
 * Created by ricca on 21/10/2017.
 */

public class NewCourseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        Spinner spinner = findViewById(R.id.CFUSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getBaseContext(),
                R.array.CFU_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        final LinearLayout linearLayoutArguments = findViewById(R.id.argumentsList);

        FloatingActionButton addArgumentButton = findViewById(R.id.addArgumentButton);

        addArgumentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final View view1 = LayoutInflater.from(getBaseContext())
                        .inflate(R.layout.argument_edit_view,null,false);
                linearLayoutArguments.addView(view1);
                final ImageButton deleteArgumentButton = view1.findViewById(R.id.imageButton);
                deleteArgumentButton.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                linearLayoutArguments.removeView(view1);

                            }
                        });
            }
        });
        FloatingActionButton addExamButton = findViewById(R.id.addExamButton);

        addExamButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDatePickerDialog(view);
                }
            });


        Button createCourseButton = findViewById(R.id.addCourseButton);

        createCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Course course = createCourse();
               if (course != null) {
                   DBManager.uploadCourse(course);
                   Toast toast = Toast.makeText(getApplicationContext(), R.string.courseLoaded, Toast.LENGTH_LONG);
                   toast.show();
                   finish();
               }
            }
        });

    }

    private Course createCourse() {
        //name
        String name = ((TextView)findViewById(R.id.courseName)).getText().toString();
        if (Objects.equals(name, "")) {
            ((TextView)findViewById(R.id.courseName)).setShowSoftInputOnFocus(true);
            Toast toast = Toast.makeText(this, R.string.courseNameVoid, Toast.LENGTH_LONG);
            toast.show();
            return null;
        }
        Integer cfu = Integer.parseInt(((Spinner)(findViewById(R.id.CFUSpinner))).getSelectedItem().toString());
        if (cfu == null) {
            ((Spinner)findViewById(R.id.CFUSpinner)).setActivated(true);
            Toast toast = Toast.makeText(this, R.string.courseCFUVoid, Toast.LENGTH_LONG);
            toast.show();
            return null;
        }
        LinearLayout argumentsLinearLayout = findViewById(R.id.argumentsList);
        int childs = argumentsLinearLayout.getChildCount();
        ArrayList<Argument> arguments = new ArrayList<>();
        for (int i = 1; i < argumentsLinearLayout.getChildCount(); i++) {
            String argumentName = ((TextView)argumentsLinearLayout.getChildAt(i).findViewById(R.id.argumentName)).getText().toString();
            Integer expectedHours = Integer.parseInt(((TextView)argumentsLinearLayout.getChildAt(i).findViewById(R.id.argumentName)).getText().toString());
            arguments.add(new Argument(argumentName,expectedHours));
        }
        LinearLayout examsLinearLayout = findViewById(R.id.argumentsList);
        ArrayList<Exam> exams = new ArrayList<>();
        DateFormat df = new SimpleDateFormat("dd-MM-YYYY");
        for (int i = 0; i < examsLinearLayout.getChildCount(); i++) {
            Date examDate = null;
            try {
                examDate = df.parse(((TextView)argumentsLinearLayout.getChildAt(i).findViewById(R.id.argumentName)).getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
                //DATE ERROR RECOVERY
            }
            exams.add(new Exam(examDate));
        }
        Course course = new Course();
        course.setName(name);
        course.setCredits(cfu);
        if (arguments.size() > 0) course.addArguments(arguments);
        if (exams.size() > 0) course.addExams(exams);

        return course;
    }

    public void showDatePickerDialog (View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }




}
