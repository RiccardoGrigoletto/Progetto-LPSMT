package com.example.marco.progettolpsmt;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marco.progettolpsmt.backend.Course;

import java.util.ArrayList;
import java.util.Random;
import java.util.zip.Inflater;

/**
 * Created by ricca on 22/09/2017.
 */

public class DailyCoursesAdapter<C> extends ArrayAdapter<Course> {

    // declaring our ArrayList of items
    private ArrayList<Course> objects;

    public DailyCoursesAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<Course> objects) {
        super(context, resource, objects);
        this.objects = objects;
    }

    /*
     * we are overriding the getView method here - this is what defines how each
     * list item will look.
     */
    public View getView(int position, View convertView, ViewGroup parent) {

        // assign the view we are converting to a local variable
        View v = convertView;

        // first check to see if the view is null. if so, we have to inflate it.
        // to inflate it basically means to render, or show, the view.
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.daily_course, null);
        }

		/*
		 * Recall that the variable position is sent in as an argument to this method.
		 * The variable simply refers to the position of the current object in the list. (The ArrayAdapter
		 * iterates through the list we sent it)
		 *
		 * Therefore, i refers to the current Item object.
		 */
        final Course i = objects.get(position);

        if (i != null) {

            TextView courseTitle = v.findViewById(R.id.name);
            TextView courseStudyTime = v.findViewById(R.id.hours);

            courseTitle.setText(i.getName());
            courseStudyTime.setText("10h");
            ImageButton startTimer = v.findViewById(R.id.startTimerActivityImageButton);
            startTimer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), TimerActivity.class);
                    intent.putExtra("courseID",i.getName());
                    view.getContext().startActivity(intent);
                }
            });
        }

        // the view must be returned to our activity
        return v;

    }
}
