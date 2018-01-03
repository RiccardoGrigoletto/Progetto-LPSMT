package com.example.marco.progettolpsmt;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import java.util.List;

/**
 * Created by ricca on 24/11/2017.
 */

public class CoursesAdapterMax extends ArrayAdapter<Course> {

    ArrayList<Course> courses;
    LayoutInflater inflater;
    Context context;

    public CoursesAdapterMax(@NonNull Context context, int resource, @NonNull List<Course> objects) {
        super(context, resource, objects);
        this.context = context;
        this.courses = (ArrayList<Course>) objects;
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
            inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.course_card_view_max, null);
        }
        final Course course = this.getItem(i);
        ((TextView) view.findViewById(R.id.courseName)).setText(course.getName());
        ((TextView) view.findViewById(R.id.courseCFU1)).setText(Integer.toString(course.getCredits()));
        ((ProgressBar) view.findViewById(R.id.progressBar4)).setProgress((int)(course.computeProgress()*100));
        ((TextView) view.findViewById(R.id.progressTextView)).setText((course.computeStudyTimeSpent()/60)+"h/"+(course.getTimeExpected()/60)+"h");

        LinearLayout llArgs = (view.findViewById(R.id.argumentsLinearLayout));
        llArgs.removeAllViews();
        for (Argument arg : course.getArguments()) {
            android.util.Log.d("arguments", "name: " + arg.getName() + " progress: " + arg.computeProgress());
            View view1 = inflater.inflate(R.layout.item_head_1, null, false);
            ((TextView) view1.findViewById(R.id.argumentName)).setText(arg.getName());
            ((ProgressBar) view1.findViewById(R.id.argumentProgressBar)).setProgress((int)(arg.computeProgress()*100));
            ((TextView) view1.findViewById(R.id.argumentProgressTextView)).setText((arg.computeStudyTimeSpent()/60)+"h/"+(arg.getExpectedTime()/60)+"h");
            llArgs.addView(view1);
        }
        LinearLayout llExams = (view.findViewById(R.id.examsLinearLayout));
        llExams.removeAllViews();
        for (Exam exam : course.getExams()) {
            View view1 = inflater.inflate(R.layout.item_head_2, null, false);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(exam.getDate());
            ((TextView) view1.findViewById(R.id.examTextView)).setText(calendar.get(Calendar.DAY_OF_MONTH) + " / " +
                    (calendar.get(Calendar.MONTH)+1) + " / " + calendar.get(Calendar.YEAR));
            llExams.addView(view1);
        }
        view.findViewById(R.id.editCourseButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("courseID", course.getName());
                Intent intent = new Intent(view.getContext(), NewCourseActivity.class);
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        return view;
    }


}
