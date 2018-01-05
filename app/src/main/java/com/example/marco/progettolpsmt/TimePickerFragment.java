package com.example.marco.progettolpsmt;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;


/**
 * Created by ricca on 06/12/2017.
 */

public class TimePickerFragment  extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {
    private final TextView target;
    int resId;
    public final static String resIdKey = "resID";

    public TimePickerFragment(TextView target) {
        this.target = target;
    }

    public TimePickerFragment() {
        this.target = null;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        TimePickerDialog tpd = new TimePickerDialog(getActivity(), this, 9, 0, true);
        return tpd;
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        String hourOfDayString = (hourOfDay<10) ? "0" + hourOfDay : String.valueOf(hourOfDay);
        String minuteString = (minute<10) ? "0" + minute : String.valueOf(minute);
        target.setText(hourOfDayString + ":" + minuteString);
    }
}
