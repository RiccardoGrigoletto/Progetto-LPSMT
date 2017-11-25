package com.example.marco.progettolpsmt;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.marco.progettolpsmt.backend.Course;

import java.util.ArrayList;

/**
 * Created by ricca on 25/10/2017.
 */

class CoursesAdapterMin extends RecyclerView.Adapter<CoursesAdapterMin.ViewHolder> {

    // declaring our ArrayList of items
    private ArrayList<Course> objects;

    public CoursesAdapterMin(ArrayList<Course> objects) {
        super();
        this.objects =  objects;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        // each data item is just a string in this case
        public CardView mCardView;
        public ViewHolder(CardView v) {
            super(v);
            mCardView = v;
        }

    }
    // Create new views (invoked by the layout manager)
    @Override
    public CoursesAdapterMin.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        // create a new view

        CardView v = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.course_card_view_min, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        ((TextView) holder.mCardView.findViewById(R.id.course_name)).setText(objects.get(position).getName());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return objects.size();
    }

}
