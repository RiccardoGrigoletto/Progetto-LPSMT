package com.example.marco.progettolpsmt;


import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.calendar.CalendarScopes;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.marco.progettolpsmt.backend.Argument;
import com.example.marco.progettolpsmt.backend.Course;
import com.example.marco.progettolpsmt.backend.Exam;
import com.example.marco.progettolpsmt.backend.TimerSettingsSingleton;
import com.example.marco.progettolpsmt.managers.CalendarManager;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import devlight.io.library.ntb.NavigationTabBar;
import pub.devrel.easypermissions.EasyPermissions;

import static com.example.marco.progettolpsmt.ProvaCalendar.REQUEST_ACCOUNT_PICKER;
import static com.example.marco.progettolpsmt.ProvaCalendar.REQUEST_AUTHORIZATION;
import static com.example.marco.progettolpsmt.ProvaCalendar.REQUEST_PERMISSION_GET_ACCOUNTS;

public class MainActivity extends AppCompatActivity {
    final int VIEWS = 3;

    //calendar scopes
    // Projection array. Creating indices for this array instead of doing
// dynamic lookups improves performance.
    public static final String[] EVENT_PROJECTION = new String[]{
            CalendarContract.Calendars._ID,                           // 0
            CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
            CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
    };
    final Event event = new Event()
            .setSummary("Google I/O 2015")
            .setLocation("800 Howard St., San Francisco, CA 94103")
            .setDescription("A chance to hear more about Google's developer products.");

    com.google.api.services.calendar.Calendar mService = null;
    HttpTransport transport = AndroidHttp.newCompatibleTransport();
    JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
    GoogleAccountCredential login=null;
    final String calendarId = "primary";
    // The indices for the projection array above.
    private final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;


    public FirebaseUser user;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Firebase
        user = FirebaseAuth.getInstance().getCurrentUser();
        setContentView(R.layout.activity_main);

        //Toast Login
        Toast.makeText(this, user.getDisplayName(), Toast.LENGTH_LONG).show();

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("courses");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //create calendar TODO

        //page creation
        final ViewPager viewPager = findViewById(R.id.vp_ntb);
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return VIEWS;
            }

            @Override
            public boolean isViewFromObject(final View view, final Object object) {
                return view.equals(object);
            }

            @Override
            public void destroyItem(final View container, final int position, final Object object) {
                ((ViewPager) container).removeView((View) object);
            }

            @Override
            public Object instantiateItem(final ViewGroup container, final int position) {
                View view = null;

                ArrayList<Course> values = new ArrayList<>();
                Course c1 = new Course();
                c1.setName("fisica");
                c1.addArgument(new Argument());
                c1.addArgument(new Argument());
                c1.addExam(new Exam(new Date()));
                c1.addExam(new Exam(new Date()));
                values.add(c1);
                values.add(new Course());

                switch (position) {
                    case 0: {
                        view = LayoutInflater.from(
                                getBaseContext()).inflate(R.layout.page_0, null, false);

                        ListView lvProgress = view.findViewById(R.id.courses_progress_list_view);

                        ListView lvToday = view.findViewById(R.id.today_events_list_view);


                        CoursesProgressAdapter<Course> progressAdapter = new CoursesProgressAdapter<>(getBaseContext(),
                                R.layout.progress_bar_min, values);
                        lvProgress.setAdapter(progressAdapter);

                        DailyCoursesAdapter<Course> adapter = new DailyCoursesAdapter<>(getBaseContext(),
                                R.layout.daily_course, values);
                        lvToday.setAdapter(adapter);
                    }
                    break;
                    case 1: {
                        view = LayoutInflater.from(
                                getBaseContext()).inflate(R.layout.page_1, null, false);

                        ListView courseLV = view.findViewById(R.id.coursesListView);
                        CoursesAdapterMax coursesAdapter = new CoursesAdapterMax(getBaseContext(), values);
                        courseLV.setAdapter(coursesAdapter);

                    }
                    break;
                    case 2: {
                        view = LayoutInflater.from(
                                getBaseContext()).inflate(R.layout.settings, null, false);
                        //numbers textfields
                        final NumberPicker numberofsession = view.findViewById(R.id.sessionpicker);
                        final NumberPicker studyduration = view.findViewById(R.id.studypicker);
                        final NumberPicker breakduration = view.findViewById(R.id.breakpicker);
                        final Button CalendarButton = view.findViewById(R.id.calendar);

                        CalendarButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                List<String> googleAccounts = new ArrayList<String>();

                                final String[] SCOPES = { CalendarScopes.CALENDAR_READONLY };
                                Account[] accounts = AccountManager.get(MainActivity.this).getAccounts();
                                for (Account account : accounts) {
                                    if (account.type.equals("com.google") &&  user.getEmail().equals(account.name)) {
                                        login = GoogleAccountCredential.usingOAuth2(
                                                getApplicationContext(),Arrays.asList(SCOPES))
                                                .setBackOff(new ExponentialBackOff())
                                                .setSelectedAccountName(account.name);
                                    }
                                }

                                /*
                                * querying the calendar
                                * */
                                mService = new com.google.api.services.calendar.Calendar.Builder(
                                        transport, jsonFactory, login)
                                        .setApplicationName("Progetto-LPSMT")
                                        .build();
                               /* Cursor cur = null;
                                ContentResolver cr = getContentResolver();
                                Uri uri = CalendarContract.Calendars.CONTENT_URI;
                                String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                                        + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?) AND ("
                                        + CalendarContract.Calendars.OWNER_ACCOUNT + " = ?))";
                                String[] selectionArgs = new String[]{"robertimarco16@gmail.com","com.google","robertimarco16@gmail.com"};
// Submit the query and get a Cursor object back.

                                if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return;
                                }
                                cur = cr.query(uri, EVENT_PROJECTION, selection,  selectionArgs, null);
                                long calID = 0;
                                //getting user calendars
                                while (cur.moveToNext()) {


                                    String displayName = null;
                                    String accountName = null;
                                    String ownerName = null;



                                    // Get the field values
                                    calID = cur.getLong(PROJECTION_ID_INDEX);
                                    displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
                                    accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
                                    ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);
                                    Log.d("entrato------>",""+calID);
                                    Log.d("accountname---->",""+accountName);
                                    Log.d("displayname---->",""+displayName);
                                    // Do something with the values...
                                    long startMillis = 0;
                                    long endMillis = 0;
                                    Calendar beginTime = Calendar.getInstance();
                                    beginTime.set(2017, 12, 25, 7, 30);
                                    startMillis = beginTime.getTimeInMillis();
                                    Calendar endTime = Calendar.getInstance();
                                    endTime.set(2017, 12, 25, 8, 45);
                                    endMillis = endTime.getTimeInMillis();


                                    ContentResolver contentResolver = getContentResolver();
                                    ContentValues values = new ContentValues();
                                    values.put(CalendarContract.Events.DTSTART, startMillis);
                                    values.put(CalendarContract.Events.DTEND, endMillis);
                                    values.put(CalendarContract.Events.TITLE, "Jazzercise");
                                    values.put(CalendarContract.Events.DESCRIPTION, "Group workout");
                                    values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                    values.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/Rome");
                                    if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                                        // TODO: Consider calling
                                        //    ActivityCompat#requestPermissions
                                        // here to request the missing permissions, and then overriding
                                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                        //                                          int[] grantResults)
                                        // to handle the case where the user grants the permission. See the documentation
                                        // for ActivityCompat#requestPermissions for more details.
                                        return;
                                    }
                                    Uri lel = cr.insert(CalendarContract.Events.CONTENT_URI, values);*/


                                DateTime startDateTime = new DateTime("2017-12-02T09:00:00-07:00");
                                EventDateTime start = new EventDateTime()
                                        .setDateTime(startDateTime)
                                        .setTimeZone(TimeZone.getDefault().toString());
                                event.setStart(start);

                                DateTime endDateTime = new DateTime("2017-12-02T17:00:00-07:00");
                                EventDateTime end = new EventDateTime()
                                        .setDateTime(endDateTime)
                                        .setTimeZone(TimeZone.getDefault().toString());
                                event.setEnd(end);

                                String[] recurrence = new String[] {"RRULE:FREQ=DAILY;COUNT=2"};
                                event.setRecurrence(Arrays.asList(recurrence));

                                EventAttendee[] attendees = new EventAttendee[] {
                                        new EventAttendee().setEmail("robertimarco16@gmail.com"),
                                };
                                event.setAttendees(Arrays.asList(attendees));

                                EventReminder[] reminderOverrides = new EventReminder[] {
                                        new EventReminder().setMethod("email").setMinutes(24 * 60),
                                        new EventReminder().setMethod("popup").setMinutes(10),
                                };
                                Event.Reminders reminders = new Event.Reminders()
                                        .setUseDefault(false)
                                        .setOverrides(Arrays.asList(reminderOverrides));
                                event.setReminders(reminders);


                                @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
                                    @Override
                                    protected String doInBackground(Void... params) {
                                        try {
                                            mService.events().insert(calendarId,event).execute();
                                        } catch (Exception e) {
                                           // Toast.makeText(MainActivity.this, "Sboret", Toast.LENGTH_LONG).show();
                                            /*startActivityForResult(
                                                    ((UserRecoverableAuthIOException) e).getIntent(),
                                                    REQUEST_AUTHORIZATION);*/
                                            e.printStackTrace();
                                        }
                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(String token) {
                                       // Toast.makeText(MainActivity.this, "Onesto", Toast.LENGTH_LONG).show();
                                    }

                                };
                                task.execute();






/*
                                ProvaCalendar p = new ProvaCalendar();
                                p.insertEvent();*/

                               /* java.util.Calendar beginTime = java.util.Calendar.getInstance();
                                beginTime.set(2012, 0, 19, 7, 30);
                                java.util.Calendar endTime = java.util.Calendar.getInstance();
                                endTime.set(2012, 0, 19, 8, 30);
                                Intent intent = new Intent(Intent.ACTION_INSERT)
                                        .setData(CalendarContract.Events.CONTENT_URI)
                                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                                        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                                        .putExtra(CalendarContract.Events.TITLE, "Yoga")
                                        .putExtra(CalendarContract.Events.DESCRIPTION, "Group class")
                                        .putExtra(CalendarContract.Events.EVENT_LOCATION, "The gym")
                                        .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
                                        .putExtra(Intent.EXTRA_EMAIL, "rowan@example.com,trevor@example.com");
                                startActivity(intent);*/
                            }
                        });
                        //onchange listeners
                        //numberofsession.setValue((int)TimerSettingsSingleton.getInstance().getNumberOfStudySessions(MainActivity.this));
                        studyduration.setValue((int)TimerSettingsSingleton.getInstance().getNumberOfStudyDuration(MainActivity.this)/60000);

                        //session number picker
                        numberofsession.setMinValue(1);
                        numberofsession.setMaxValue(10);
                        numberofsession.setWrapSelectorWheel(true);
                        numberofsession.setValue((int)TimerSettingsSingleton.getInstance().getNumberOfStudySessions(MainActivity.this));
                        numberofsession.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                            @Override
                            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                                try {   //Log.d("------------------>","During"+numberofsession.getText().toString());
                                    TimerSettingsSingleton.getInstance().setNumberOfStudySessions(numberofsession.getValue(), MainActivity.this);
                                    Log.d("------------------>", "sdksojdajaosdjao" + TimerSettingsSingleton.getInstance().getNumberOfStudySessions(getApplicationContext()));
                                    Toast.makeText(MainActivity.this, "Value Updated", Toast.LENGTH_LONG).show();
                                }catch (Exception e){
                                    Toast.makeText(MainActivity.this, "Il campo non può essere vuoto", Toast.LENGTH_LONG).show();
                                }
                            }
                        });


                        //study duration number picker

                        studyduration.setMinValue(10);
                        studyduration.setMaxValue(60);
                        studyduration.setWrapSelectorWheel(true);
                        studyduration.setValue((int)TimerSettingsSingleton.getInstance().getNumberOfStudyDuration(MainActivity.this)/60000);
                        studyduration.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                            @Override
                            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                                try {   //Log.d("------------------>","During"+numberofsession.getText().toString());
                                    TimerSettingsSingleton.getInstance().setDurationOfStudySessions(studyduration.getValue()*60000, MainActivity.this);
                                    Toast.makeText(MainActivity.this, "Value Updated", Toast.LENGTH_LONG).show();
                                }catch (Exception e){
                                    Toast.makeText(MainActivity.this, "Il campo non può essere vuoto", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                        //break duration number picker
                        breakduration.setMaxValue(10);
                        breakduration.setMinValue(1);
                        breakduration.setWrapSelectorWheel(true);
                        //breakduration.setValue((int)TimerSettingsSingleton.getInstance().getNumberOfBreakDuration(MainActivity.this)/60000);
                        breakduration.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                            @Override
                            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                                try {   //Log.d("------------------>","During"+numberofsession.getText().toString());
                                    TimerSettingsSingleton.getInstance().setDurationOfBreakSessions(breakduration.getValue()*60000, MainActivity.this);
                                    Toast.makeText(MainActivity.this, "Value Updated", Toast.LENGTH_LONG).show();
                                }catch (Exception e){
                                    Toast.makeText(MainActivity.this, "Il campo non può essere vuoto", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                       /* numberofsession.
                        numberofsession.setText(String.valueOf(TimerSettingsSingleton.getInstance().getNumberOfStudySessions(MainActivity.this)));
                        studyduration.setText(String.valueOf(TimerSettingsSingleton.getInstance().getNumberOfStudyDuration(MainActivity.this)/60000));
                        breakduration.setText(String.valueOf(TimerSettingsSingleton.getInstance().getNumberOfBreakDuration(MainActivity.this)/60000));
                        //number of session text area
                        numberofsession.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                Log.d("------------------>","before");
                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                                 try {   //Log.d("------------------>","During"+numberofsession.getText().toString());
                                         TimerSettingsSingleton.getInstance().setNumberOfStudySessions(Integer.parseInt(numberofsession.getText().toString()), MainActivity.this);
                                         Log.d("------------------>", "sdksojdajaosdjao" + TimerSettingsSingleton.getInstance().getNumberOfStudySessions(getApplicationContext()));
                                 }catch (Exception e){
                                     Toast.makeText(MainActivity.this, "Il campo non può essere vuoto", Toast.LENGTH_LONG).show();
                                 }
                             }
                        });
                        //study duration text area
                        studyduration.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                Log.d("------------------>","before");
                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                                try {   //Log.d("------------------>","During"+numberofsession.getText().toString());
                                    TimerSettingsSingleton.getInstance().setDurationOfStudySessions(Integer.parseInt(studyduration.getText().toString())*60000, MainActivity.this);
                                }catch (Exception e){
                                    Toast.makeText(MainActivity.this, "Il campo non può essere vuoto", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                        //break duration text area
                        breakduration.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                Log.d("------------------>","before");
                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                                try {   //Log.d("------------------>","During"+numberofsession.getText().toString());
                                    TimerSettingsSingleton.getInstance().setDurationOfBreakSessions(Integer.parseInt(breakduration.getText().toString()), MainActivity.this);
                                }catch (Exception e){
                                    Toast.makeText(MainActivity.this, "Il campo non può essere vuoto", Toast.LENGTH_LONG).show();
                                }
                            }
                        });*/


                        Button logOut = view.findViewById(R.id.log_out);
                        logOut.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                    break;
                }
                /*final TextView txtPage = (TextView) view.findViewById(R.id.txt_vp_item_page);
                txtPage.setText(String.format("Hello everyone! this is the page #%d", position));*/

                container.addView(view);
                return view;
            }
        });

        final NavigationTabBar navigationTabBar = findViewById(R.id.ntb);
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();

        models.add(ntbModelBuilder(
                R.drawable.ic_home_black_24dp,
                R.color.colorPrimaryDark));
        models.add(ntbModelBuilder(
                R.drawable.ic_account_circle_black_24dp,
                R.color.colorPrimaryDark));
        models.add(ntbModelBuilder(
                R.drawable.ic_settings_black_24dp,
                R.color.colorPrimaryDark));
        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(viewPager, 0);
        navigationTabBar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                navigationTabBar.getModels().get(position).hideBadge();
            }

            @Override
            public void onPageScrollStateChanged(final int state) {

            }
        });

        navigationTabBar.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < navigationTabBar.getModels().size(); i++) {
                    final NavigationTabBar.Model model = navigationTabBar.getModels().get(i);
                    navigationTabBar.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            /*model.showBadge();*/
                        }
                    }, i * 100);
                }
            }
        }, 500);



    /*floating button listener*/
    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.newCourse);
        fab.setOnClickListener(new View.OnClickListener()

    {
        @Override
        public void onClick (View v){
        Intent intent = new Intent(getBaseContext(), NewCourseActivity.class);
        startActivity(intent);
    }
    });
}
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_timer: {
                Intent intent = new Intent(getBaseContext(), TimerActivity.class);
                startActivity(intent);
                //finish();
            }
            break;
            case R.id.action_calendar: {
                startActivity(CalendarManager.getIntent());

            }
            break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private NavigationTabBar.Model ntbModelBuilder(int icon, int activeColor) {
        NavigationTabBar.Model.Builder ntb = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ntb = new NavigationTabBar.Model.Builder(getResources().getDrawable(icon, null),
                    getResources().getColor(activeColor, null));
            /*res.badgeTitle("");*/
        } else {
            ntb = new NavigationTabBar.Model.Builder(
                    getResources().getDrawable(icon),
                    getResources().getColor(activeColor));
        }
        return ntb.build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.items, menu);
        return true;
    }

    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_ACCOUNT_PICKER:
                String accountName =
                        data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                login.setSelectedAccountName(accountName);
                @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        try {
                            mService.events().insert(calendarId,event).execute();
                        } catch (IOException e) {
                            // Toast.makeText(MainActivity.this, "Sboret", Toast.LENGTH_LONG).show();
                            startActivityForResult(
                                    login.newChooseAccountIntent(),
                                    REQUEST_ACCOUNT_PICKER);
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(String token) {
                        // Toast.makeText(MainActivity.this, "Onesto", Toast.LENGTH_LONG).show();
                    }

                };
                task.execute();
                break;
            case REQUEST_AUTHORIZATION:
                login.setSelectedAccountName("robertimarco16@gmail.com");
                if (resultCode == RESULT_OK) {
                    @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, String> task2 = new AsyncTask<Void, Void, String>() {
                        @Override
                        protected String doInBackground(Void... params) {
                            try {
                                mService.events().insert(calendarId,event).execute();
                            } catch (IOException e) {
                                // Toast.makeText(MainActivity.this, "Sboret", Toast.LENGTH_LONG).show();
                                startActivityForResult(
                                        login.newChooseAccountIntent(),
                                        REQUEST_ACCOUNT_PICKER);
                                e.printStackTrace();
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(String token) {
                            // Toast.makeText(MainActivity.this, "Onesto", Toast.LENGTH_LONG).show();
                        }

                    };
                    task2.execute();
                }
                break;
        }
    }
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, MainActivity.this);
    }

}


