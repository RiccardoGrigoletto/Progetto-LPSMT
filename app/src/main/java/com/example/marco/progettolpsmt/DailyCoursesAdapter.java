package com.example.marco.progettolpsmt;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ricca on 22/09/2017.
 */

public class DailyCoursesAdapter<C> extends ArrayAdapter<Event> {

    public ArrayList<Event> getObjects() {
        return objects;
    }

    public void setObjects(ArrayList<Event> objects) {
        this.objects = objects;
    }

    // declaring our ArrayList of items
    private ArrayList<Event> objects;
    int resource;

    public DailyCoursesAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<Event> objects) {
        super(context, resource, objects);
        this.objects = objects;
        this.resource = resource;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Event getItem(int i) {
        return objects.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
    /*
     * we are overriding the getView method here - this is what defines how each
     * list item will look.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // assign the view we are converting to a local variable
        View v = convertView;

        // first check to see if the view is null. if so, we have to inflate it.
        // to inflate it basically means to render, or show, the view.
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(resource, null);
        }

		/*
		 * Recall that the variable position is sent in as an argument to this method.
		 * The variable simply refers to the position of the current object in the list. (The ArrayAdapter
		 * iterates through the list we sent it)
		 *
		 * Therefore, i refers to the current Item object.
		 */
        final Event i = objects.get(position);

        if (i != null) {

            TextView courseTitle = v.findViewById(R.id.name);
            TextView courseStudyTime = v.findViewById(R.id.hours);
            TextView fromToSession = v.findViewById(R.id.fromTo);

            courseTitle.setText(i.getSummary());
            int duration = (int) ((i.getEnd().getDateTime().getValue()-i.getStart().getDateTime().getValue())/(1000*60*60));
            String start = ((new DateTime(i.getStart().getDateTime().getValue())).toString().substring(11,16)
                    + "/"
                    + (new DateTime(i.getEnd().getDateTime().getValue())).toString().substring(11,16));
            fromToSession.setText(start);
            courseStudyTime.setText(String.format("%dh", duration));
            ImageButton startTimer = v.findViewById(R.id.startTimerActivityImageButton);
            startTimer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), TimerActivity.class);
                    intent.putExtra("courseID",i.getId());
                    view.getContext().startActivity(intent);
                }
            });
        }

        // the view must be returned to our activity
        return v;

    }
}
