package com.example.marco.progettolpsmt;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.text.format.DateFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import org.w3c.dom.Text;

import java.time.DayOfWeek;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ricca on 09/10/2017.
 */

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {
    private final TextView view;

    public DatePickerFragment(TextView view) {
        this.view = view;
    }
    public DatePickerFragment(){
        this.view=null;
    }

    @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int dayOfmonth = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of TimePickerDialog and return it
            DatePickerDialog dpd = new DatePickerDialog(getActivity(), this, year, month, dayOfmonth);
            dpd.getDatePicker().setMinDate(new Date().getTime());
            return dpd;
    }

    @Override
    public void onDateSet(DatePicker datePicker, final int year, final int month, final int dayOfMonth) {
        view.setText(dayOfMonth + " / " + (month+1) + " / " + year);
    }
}