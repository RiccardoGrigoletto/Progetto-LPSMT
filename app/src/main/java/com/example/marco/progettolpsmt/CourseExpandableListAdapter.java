package com.example.marco.progettolpsmt;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.HeterogeneousExpandableList;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.marco.progettolpsmt.backend.Course;

import java.text.DateFormat;
import java.util.ArrayList;

/**
 * Created by ricca on 02/11/2017.
 */

public class CourseExpandableListAdapter extends BaseExpandableListAdapter implements HeterogeneousExpandableList {

    // 4 Child types
    private static final int CHILD_TYPE_1 = 0;
    private static final int CHILD_TYPE_2 = 1;
    private static final int CHILD_TYPE_UNDEFINED = 3;

    // 1 Group type
    private static final int GROUP_TYPE_1 = 0;


    private Context context;
    private ArrayList<Course> items;


    public CourseExpandableListAdapter(Context context, ArrayList<Course> items) {
        this.context = context;
        this.items = items;

    }

    @Override
    public int getGroupCount() {
        return items.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return items.get(i).getArguments().size()+items.get(i).getExams().size();
    }

    @Override
    public Object getGroup(int i) {
        return items.get(i);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        int argumentsNumber = items.get(groupPosition).getArguments().size();
        int examsNumber = items.get(groupPosition).getExams().size();
        if (argumentsNumber > childPosition)
            return items.get(groupPosition).getArguments().get(childPosition);
        else if (examsNumber > childPosition - argumentsNumber)
            return items.get(groupPosition).getExams().get(childPosition);
        else return null;
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        Course c = ((Course)getGroup(i));
        if (view==null) {
            LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_group,null);

        }
        ((TextView)view.findViewById(R.id.name)).setText(c.getName());
        ((TextView)view.findViewById(R.id.CFU)).setText(((Integer)c.getCredits()).toString());


        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        switch (getChildType(i,i1)) {
            case CHILD_TYPE_1: {
                if (view==null) {
                    view = inflater.inflate(R.layout.item_head_1,null);

                }
                ((TextView)view.findViewById(R.id.argumentName) ).setText(items.get(i).getArguments().get(i1).getName());
                ((ProgressBar) view.findViewById(R.id.progressBar4)).setProgress(items.get(i).getArguments().get(i1).getExpectedTime());
            }
            break;
            case CHILD_TYPE_2: {
                if (view==null) {
                    view = inflater.inflate(R.layout.item_head_2,null);

                }
                String date = DateFormat.getDateInstance().format(items.get(i).getExams().get(i1-argumentsSize(i)).getDate());
                ((TextView)view.findViewById(R.id.examTextView) ).setText(date);
            }
            break;
            case CHILD_TYPE_UNDEFINED: {
            }
            break;
            default: {
            }
        }

        return view;
    }

    private int argumentsSize(int i) {
        return items.get(i).getArguments().size();
    }

    private int examsSize(int i) {
        return items.get(i).getExams().size();
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
    @Override
    public int getChildTypeCount() {
        return 3;
    }

    @Override
    public int getGroupTypeCount() {
        return 1;
    }

    @Override
    public int getGroupType(int groupPosition) {
                return GROUP_TYPE_1;
    }

    @Override
    public int getChildType(int groupPosition, int childPosition) {
        int argumentsNumber = argumentsSize(groupPosition);
        int examsNumber = examsSize(groupPosition);
        if (argumentsNumber > childPosition) {
            return CHILD_TYPE_1;
        }
        else if (examsNumber >  childPosition - argumentsNumber) {
            return CHILD_TYPE_2;
        }

        return CHILD_TYPE_UNDEFINED;
    }




}
