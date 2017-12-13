package com.example.marco.progettolpsmt;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marco.progettolpsmt.backend.Argument;
import com.example.marco.progettolpsmt.backend.Course;
import com.example.marco.progettolpsmt.backend.Evaluation;
import com.example.marco.progettolpsmt.backend.Exam;
import com.example.marco.progettolpsmt.backend.Settings;
import com.example.marco.progettolpsmt.backend.User;
import com.example.marco.progettolpsmt.managers.DBManager;
import com.google.api.client.util.DateTime;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * Created by ricca on 21/10/2017.
 */

public class NewCourseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        User u = User.getInstance();
        Bundle extras = getIntent().getExtras();
        setContentView(R.layout.activity_course);
        Course courseToEdit = null;
        try {
            if (extras != null) {
                ArrayList<Course> courses = (ArrayList) u.getCourses();
                String courseNameToEdit = extras.getString("courseID");
                for (Course c: courses) {
                    if (c.getName().equals(courseNameToEdit))
                        courseToEdit = c;
                }
            }
        }
        catch (NullPointerException e) {}

        Spinner cfuSpinner = findViewById(R.id.CFUSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getBaseContext(),
                R.array.three_to_fifteen_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cfuSpinner.setAdapter(adapter);
        final LinearLayout linearLayoutArguments = findViewById(R.id.argumentsList);

        FloatingActionButton addArgumentButton = findViewById(R.id.addArgumentButton);

        addArgumentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final View view1 = LayoutInflater.from(getBaseContext())
                        .inflate(R.layout.argument_edit_view,null,false);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getBaseContext(),
                        R.array.difficulty_array, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                ((Spinner)view1.findViewById(R.id.difficulty)).setAdapter(adapter);
                final ImageButton deleteArgumentButton = view1.findViewById(R.id.imageButton);
                deleteArgumentButton.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                linearLayoutArguments.removeView(view1);

                            }
                        });
                linearLayoutArguments.addView(view1);

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

        final Course finalCourseToEdit = courseToEdit;
        createCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Course course;
                if (finalCourseToEdit != null) {
                    //todo edit existing course
                    course= createCourse(finalCourseToEdit);

                }
                else {
                    course = createCourse(null);
                }
                if (course != null) {
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.courseLoaded, Toast.LENGTH_LONG);
                    toast.show();
                    finish();
                }
            }
        });
        FloatingActionButton addStudyDateButton = findViewById(R.id.addStudyDateButton);

        final LinearLayout linearLayoutStudyDates = findViewById(R.id.studyDateList);

        addStudyDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final View view1 = LayoutInflater.from(getBaseContext())
                        .inflate(R.layout.study_date_edit_view,null,false);

                Spinner studyDateSpinner = view1.findViewById(R.id.studyDate);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getBaseContext(),
                        R.array.week_days, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                studyDateSpinner.setAdapter(adapter);

                final TextView studyDateFrom = view1.findViewById(R.id.studyDateFrom);
                final TextView studyDateTo = view1.findViewById(R.id.studyDateTo);

                studyDateFrom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showTimePickerDialog(view, R.id.studyDateFrom);
                    }
                });
                studyDateTo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showTimePickerDialog(view, R.id.studyDateTo);
                    }
                });

                linearLayoutStudyDates.addView(view1);
                final ImageButton deleteArgumentButton = view1.findViewById(R.id.imageButton);
                deleteArgumentButton.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                linearLayoutStudyDates.removeView(view1);

                            }
                        });
            }
        });
        /**
         Edit course
         */
        if (courseToEdit != null) {
            //populate activity
            ((TextView)findViewById(R.id.courseName)).setText(courseToEdit.getName());
            ((Spinner)findViewById(R.id.CFUSpinner)).setSelection(courseToEdit.getCredits()-3,true);
            for (Argument argument:courseToEdit.getArguments()) {
                final View view1 = LayoutInflater.from(getBaseContext())
                        .inflate(R.layout.argument_edit_view,null,false);
                ((EditText)view1.findViewById(R.id.argumentName)).setText(argument.getName());
                ArrayAdapter<CharSequence> difficultyAdapter = ArrayAdapter.createFromResource(getBaseContext(),
                        R.array.difficulty_array, android.R.layout.simple_spinner_item);
                difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                ((Spinner)view1.findViewById(R.id.difficulty)).setAdapter(difficultyAdapter);
                int position = argument.getDifficulty().getPosition();
                ((Spinner)view1.findViewById(R.id.difficulty)).setSelection(argument.getDifficulty().getPosition(),true);
                final ImageButton deleteArgumentButton = view1.findViewById(R.id.imageButton);

                linearLayoutArguments.addView(view1);

                deleteArgumentButton.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                linearLayoutArguments.removeView(view1);

                            }
                        });
            }
            for (Exam exam:courseToEdit.getExams()) {
                final View view1 = LayoutInflater.from(getBaseContext())
                        .inflate(R.layout.exam_edit_view,null,false);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(exam.getDate());
                ((TextView)view1.findViewById(R.id.examDate)).setText(calendar.get(Calendar.DAY_OF_MONTH) + " - " +
                        calendar.get(Calendar.MONTH) + " - " + calendar.get(Calendar.YEAR));
                final LinearLayout ll = findViewById(R.id.examsList);
                final ImageButton deleteArgumentButton = view1.findViewById(R.id.imageButton);
                deleteArgumentButton.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ll.removeView(view1);

                            }
                        });
                ll.addView(view1);
            }

        }

    }



    private Course createCourse(@Nullable Course from) {
        Course course;
        User u = User.getInstance();
        //name
        String name = ((TextView) findViewById(R.id.courseName)).getText().toString();
        if (Objects.equals(name, "")) {
            ((TextView) findViewById(R.id.courseName)).setShowSoftInputOnFocus(true);
            Toast toast = Toast.makeText(this, R.string.courseNameVoid, Toast.LENGTH_LONG);
            toast.show();
            return null;
        }
        //cfu
        Integer cfu = Integer.parseInt(((Spinner) (findViewById(R.id.CFUSpinner))).getSelectedItem().toString());
        if (cfu == null) {
            ((Spinner) findViewById(R.id.CFUSpinner)).setActivated(true);
            Toast toast = Toast.makeText(this, R.string.courseCFUVoid, Toast.LENGTH_LONG);
            toast.show();
            return null;
        }
        //arguments
        LinearLayout argumentsLinearLayout = findViewById(R.id.argumentsList);
        ArrayList<Argument> arguments = new ArrayList<>();
        for (int i = 0; i < argumentsLinearLayout.getChildCount(); i++) {
            String argumentName = ((TextView) argumentsLinearLayout.getChildAt(i).findViewById(R.id.argumentName)).getText().toString();
            /*String expectedHoursString = ((TextView) argumentsLinearLayout.getChildAt(i).findViewById(R.id.argumentExpectedHours)).getText().toString();
            Integer expectedHours = 25;
            try {
                 expectedHours = Integer.parseInt(expectedHoursString);
            }
            catch (NumberFormatException e) {}
            arguments.add(new Argument(argumentName, expectedHours));*/
            Evaluation difficulty;
            Spinner evaluationSpinner = (findViewById(R.id.difficulty));
            try {
                switch (evaluationSpinner.getSelectedItem().toString()) {
                    case "super easy":
                        difficulty = Evaluation.SUPER_EASY;
                        break;
                    case "easy":
                        difficulty = Evaluation.EASY;
                        break;
                    case "regular":
                        difficulty = Evaluation.REGULAR;
                        break;
                    case "hard":
                        difficulty = Evaluation.HARD;
                        break;
                    case "super hard":
                        difficulty = Evaluation.SUPER_HARD;
                        break;
                    default:
                        difficulty = Evaluation.REGULAR;
                        break;
                }
            }
            catch (NullPointerException e) {
                difficulty = Evaluation.REGULAR;
            }
            Argument newArgument = new Argument();
            newArgument.setDifficulty(difficulty);
            try {
                newArgument.setName(argumentName);
            } catch (Exception e) {
                e.printStackTrace();
            }
            arguments.add(newArgument);



        }
        //exams
        LinearLayout examsLinearLayout = findViewById(R.id.examsList);
        ArrayList<Exam> exams = new ArrayList<>();
        DateFormat df = new SimpleDateFormat("dd-MM-YYYY");
        for (int i = 0; i < examsLinearLayout.getChildCount(); i++) {
            Date examDate = null;
            try {
                examDate = df.parse(((TextView) argumentsLinearLayout.getChildAt(i).findViewById(R.id.examDate)).getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
                //DATE ERROR RECOVERY
            }
            exams.add(new Exam(examDate));
        }
        //study sessions
        LinearLayout studyDatesLinearLayout = findViewById(R.id.studyDateList);
        //todo
        for (int i = 0; i < examsLinearLayout.getChildCount(); i++) {
            Date examDate = null;
            try {
                examDate = df.parse(((TextView) argumentsLinearLayout.getChildAt(i).findViewById(R.id.examDate)).getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
                //DATE ERROR RECOVERY
            }
            exams.add(new Exam(examDate));
        }
        if (from == null) {
            course = new Course();
        }
        else {
            u.getCourses().remove(from);
            course = from;
        }
        course.setName(name);
        course.setCredits(cfu);
        if (arguments.size() > 0) course.addArguments(arguments);
        if (exams.size() > 0) course.addExams(exams);

        course.updateOnFirestore();
        return course;
    }

    public void showDatePickerDialog (View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }
    private void showTimePickerDialog(View view, int id) {
        Bundle extras = new Bundle();
        extras.putInt(TimePickerFragment.resIdKey,id);
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.setArguments(extras);
        newFragment.show(getFragmentManager(), "timePicker");
    }

}
