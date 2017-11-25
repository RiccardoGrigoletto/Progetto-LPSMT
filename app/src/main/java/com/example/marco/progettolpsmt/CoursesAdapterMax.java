package com.example.marco.progettolpsmt;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.marco.progettolpsmt.backend.Argument;
import com.example.marco.progettolpsmt.backend.Course;
import com.example.marco.progettolpsmt.backend.Exam;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by ricca on 24/11/2017.
 */

public class CoursesAdapterMax extends BaseAdapter {

    ArrayList<Course> courses;
    LayoutInflater inflater;
    Context context;
    CoursesAdapterMax(Context context) {
        this.inflater = LayoutInflater.from(context);
        this.courses = new ArrayList<>();
        this.context = context;
    }

    public CoursesAdapterMax(Context context, ArrayList<Course> values) {
        this.inflater = LayoutInflater.from(context);
        this.courses = values;
        this.context = context;
    }

    @Override
    public int getCount() {
        return courses.size();
    }

    @Override
    public Course getItem(int i) {
        return courses.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.course_card_view_max, viewGroup, false);
            final Course course = this.getItem(i);
            ((TextView) view.findViewById(R.id.courseName)).setText(course.getName());
            ((TextView) view.findViewById(R.id.courseCFU1)).setText("23");

            LinearLayout llArgs = (view.findViewById(R.id.argumentsLinearLayout));
            for (Argument arg : course.getArguments()) {
                View view1 = inflater.inflate(R.layout.item_head_1, null, false);
                ((TextView) view1.findViewById(R.id.argumentName)).setText(arg.getName());
                ((ProgressBar) view1.findViewById(R.id.argumentProgressBar)).setProgress(arg.computeStudyTimeSpent());
                llArgs.addView(view1);
            }
            LinearLayout llExams = (view.findViewById(R.id.examsLinearLayout));
            for (Exam exam : course.getExams()) {
                View view1 = inflater.inflate(R.layout.item_head_2, null, false);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(exam.getDate());
                ((TextView) view1.findViewById(R.id.examTextView)).setText(calendar.get(Calendar.DAY_OF_MONTH) + " - " +
                        calendar.get(Calendar.MONTH) + " - " + calendar.get(Calendar.YEAR));
                llExams.addView(view1);
            }
            view.findViewById(R.id.editCourseButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("courseID",0); /*TODO INSERT DIN ID */
                    Intent intent = new Intent(view.getContext(),NewCourseActivity.class);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });

        }
        return view;
    }
}
