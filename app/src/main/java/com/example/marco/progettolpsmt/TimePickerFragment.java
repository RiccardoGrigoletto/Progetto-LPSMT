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
    int resId;
    public final static String resIdKey = "resID";
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle extras = this.getArguments();
        resId = extras.getInt(resIdKey);
        TimePickerDialog tpd = new TimePickerDialog(getActivity(), this, 9, 0, true);
        return tpd;
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        TextView tv = getActivity().findViewById(resId);
        String hourOfDayString = (hourOfDay<10) ? "0" + hourOfDay : String.valueOf(hourOfDay);
        String minuteString = (minute<10) ? "0" + minute : String.valueOf(minute);
        tv.setText(hourOfDayString + ":" + minuteString);
    }
}
