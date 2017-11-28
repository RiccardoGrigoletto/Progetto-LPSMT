package com.example.marco.progettolpsmt.backend;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.*;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Marco on 28/11/2017.
 */

public class CalendarManagerSingleton extends AppCompatActivity {
    private static final CalendarManagerSingleton ourInstance = new CalendarManagerSingleton();

    public static CalendarManagerSingleton getInstance() {
        return ourInstance;
    }

    private CalendarManagerSingleton() {
    }

    GoogleAccountCredential mCredential;


    public void createEvent() {
        startActivityForResult(
                mCredential.newChooseAccountIntent(),
                1000);
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        com.google.api.services.calendar.Calendar service = new com.google.api.services.calendar.Calendar.Builder(transport, jsonFactory, mCredential)
                .setApplicationName("R_D_Location Callendar")
                .build();


        Event event = new Event()
                .setSummary("Event- November 2017")
                .setLocation("Dhaka")
                .setDescription("New Event 1");

        DateTime startDateTime = new DateTime("2017-11-17T18:10:00+06:00");
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("Asia/Dhaka");
        event.setStart(start);

        DateTime endDateTime = new DateTime("2017-11-17T18:10:00+06:00");
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("Asia/Dhaka");
        event.setEnd(end);

        String[] recurrence = new String[]{"RRULE:FREQ=DAILY;COUNT=2"};
        event.setRecurrence(Arrays.asList(recurrence));

        EventAttendee[] attendees = new EventAttendee[]{
                new EventAttendee().setEmail("abir@aksdj.com"),
                new EventAttendee().setEmail("asdasd@andlk.com"),
        };
        event.setAttendees(Arrays.asList(attendees));

        EventReminder[] reminderOverrides = new EventReminder[]{
                new EventReminder().setMethod("email").setMinutes(24 * 60),
                new EventReminder().setMethod("popup").setMinutes(10),
        };
        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminderOverrides));
        event.setReminders(reminders);

        String calendarId = "primary";
        try {
            event = service.events().insert(calendarId, event).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.printf("Event created: %s\n", event.getHtmlLink());
    }


    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null &&
                data.getExtras() != null) {
            String accountName =
                    data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            if (accountName != null) {
                SharedPreferences settings =
                        getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("PREF_ACCOUNT_NAME", accountName);
                editor.apply();
                mCredential.setSelectedAccountName(accountName);
            }

        }
    }
}
