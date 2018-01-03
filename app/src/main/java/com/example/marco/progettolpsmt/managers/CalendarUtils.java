package com.example.marco.progettolpsmt.managers;

import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.nfc.FormatException;
import android.provider.CalendarContract;
import android.util.Pair;
import android.widget.TextView;

import com.example.marco.progettolpsmt.R;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;

import java.lang.invoke.ConstantCallSite;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.annotation.Nullable;

/**
 * Created by ricca on 19/11/2017.
 */

public final class CalendarUtils {


    public static Intent getIntent() {
        Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
        builder.appendPath("time");
        ContentUris.appendId(builder, Calendar.getInstance().getTimeInMillis());
        Intent intent = new Intent(Intent.ACTION_VIEW)
                .setData(builder.build());
        return intent;
    }

    private  static Pair<String,String> calendarEventDatesStringBuilder(String strtHour, String endHour,String day ){

        //return values
        Pair <String,String> returnPairDates;
        String strtDateFormatted;
        String endDateFormatted;

        /**
         * Temporary date solution
         */

        strtHour = formateHours(strtHour);
        endHour = formateHours(endHour);


        /**
         * end solution
         */
        //start date building
        StringBuilder dateBuilder = new StringBuilder();
        String currentDate = getNextEventDate(day);
        dateBuilder.append(currentDate);
        dateBuilder.append("T");
        dateBuilder.append(strtHour+":00");

        //start date formatted
        strtDateFormatted = dateBuilder.toString();

        //flushing dateBuilder
        dateBuilder.delete(0,dateBuilder.length());

        //end date builder
        dateBuilder.append(currentDate);
        dateBuilder.append("T");
        dateBuilder.append(endHour+":00");

        //end date formatted
        endDateFormatted = dateBuilder.toString();

        //constructing pair object with date formatted;
        returnPairDates = new Pair<String, String>(strtDateFormatted,endDateFormatted);

        return returnPairDates;

    }

    public static Event examEventBuilder(Date examDate, String courseName){

        Date endExamDate = new Date();
        endExamDate.setTime(examDate.getTime()+24*60*60*1000);
        DateFormat examFormatter = new SimpleDateFormat("yyyy-MM-dd");
        String startDate = "";
        String endDate ="";
        try {
            startDate = examFormatter.format(examDate);
            endDate = examFormatter.format(endExamDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //creating calendar event
        Event event = new Event()
                .setSummary(courseName)
                .setDescription("Happy exam");

        DateTime startDateTime = new DateTime(startDate+"T00:00:00");
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone(Calendar.getInstance().getTimeZone().getID());
        event.setStart(start);
        DateTime endDateTime = new DateTime(endDate+"T00:00:00");
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone(Calendar.getInstance().getTimeZone().getID());
        event.setEnd(end);

        EventReminder[] reminderOverrides = new EventReminder[] {
                new EventReminder().setMethod("popup").setMinutes(10),
        };
        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminderOverrides));
        event.setReminders(reminders);
        return event;
    }

    public static Event eventBuilder(String day, String strtHour, String endHour, String courseName, @Nullable Date examDate){

        //getting dates

        Pair eventDates = calendarEventDatesStringBuilder(strtHour,endHour,day.substring(0,2).toUpperCase());
        //creating calendar event
        Event event = new Event()
                .setSummary(courseName)
                .setDescription("Happy studying");

            DateTime startDateTime = new DateTime(((String) eventDates.first));
            EventDateTime start = new EventDateTime()
                    .setDateTime(startDateTime)
                    .setTimeZone(Calendar.getInstance().getTimeZone().getID());
            event.setStart(start);
        DateTime endDateTime = new DateTime(((String) eventDates.second));
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone(Calendar.getInstance().getTimeZone().getID());
        event.setEnd(end);
        //setting recurrency
        String[] recurrence;
        if(examDate != null) {
            DateFormat examFormatter = new SimpleDateFormat("yyyyMMdd");
            String exam = "";
            try {
                  exam = examFormatter.format(examDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
            recurrence = new String[]{String.format("RRULE:FREQ=WEEKLY;BYDAY=%s;UNTIL=%s",day.substring(0,2),exam)};
        }else{
            recurrence = new String[]{String.format("RRULE:FREQ=WEEKLY;BYDAY=%s",day.substring(0,2).toUpperCase())};
        }
        event.setRecurrence(Arrays.asList(recurrence));

        EventReminder[] reminderOverrides = new EventReminder[] {
                new EventReminder().setMethod("popup").setMinutes(10),
        };
        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminderOverrides));
        event.setReminders(reminders);

        return event;
    }

    public static String getNextEventDate(String dayOfWeek){
        int numberOfDay=0;

        switch (dayOfWeek){
            case "SU":
                numberOfDay = 1;
                break;
            case "MO":
                numberOfDay = 2;
                break;
            case "TU":
                numberOfDay = 3;
                break;
            case "WE":
                numberOfDay = 4;
                break;
            case "TH":
                numberOfDay = 5;
                break;
            case "FR":
                numberOfDay = 6;
                break;
            case "SA":
                numberOfDay = 7;
                break;
        }


        Calendar date = Calendar.getInstance();
        int diff = numberOfDay - date.get(Calendar.DAY_OF_WEEK);
        if (diff <= 0) {
            diff += 7;
        }
        date.add(Calendar.DAY_OF_MONTH, diff);
        DateFormat d = new SimpleDateFormat("yyyy-MM-dd");
        return d.format(date.getTime());
    }



    /**
     * temporary  solution for a wrong hour
     */
    private static String formateHours(String hour){
        int h = Integer.parseInt(hour.substring(0,2));
        --h;
        StringBuilder builder = new StringBuilder();
        if(h <10) {
            builder.append("0");
        }
        builder.append(h);
        builder.append(":00");

        return builder.toString();

    }
}
