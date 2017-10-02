package com.example.marco.progettolpsmt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.marco.progettolpsmt.Classes.Course;
import com.example.marco.progettolpsmt.R;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by ricca on 18/09/2017.
 */

public class CoursesProgressAdapter<C> extends ArrayAdapter<Course> {

    // declaring our ArrayList of items
    private ArrayList<Course> objects;

    /* here we must override the constructor for ArrayAdapter
    * the only variable we care about now is ArrayList<Item> objects,
    * because it is the list of objects we want to display.
    */
    public CoursesProgressAdapter(Context context, int textViewResourceId, ArrayList<Course> objects) {
        super(context, textViewResourceId, objects);
        this.objects = objects;
    }

    /*
     * we are overriding the getView method here - this is what defines how each
     * list item will look.
     */
    public View getView(int position, View convertView, ViewGroup parent){

        // assign the view we are converting to a local variable
        View v = convertView;

        // first check to see if the view is null. if so, we have to inflate it.
        // to inflate it basically means to render, or show, the view.
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.progress_bar, null);
        }

		/*
		 * Recall that the variable position is sent in as an argument to this method.
		 * The variable simply refers to the position of the current object in the list. (The ArrayAdapter
		 * iterates through the list we sent it)
		 *
		 * Therefore, i refers to the current Item object.
		 */
        Course i = objects.get(position);

        if (i != null) {

            TextView courseTitle = (TextView) v.findViewById(R.id.name);
            ProgressBar coursePB = (ProgressBar) v.findViewById(R.id.indeterminateBar);

            courseTitle.setText(i.getName());
            /*coursePB.setProgress(i.getProgress());*/
            /*****TMP*****/
            Random randomGenerator = new Random();
            coursePB.setProgress(randomGenerator.nextInt(100));

        }

        // the view must be returned to our activity
        return v;

    }

}
