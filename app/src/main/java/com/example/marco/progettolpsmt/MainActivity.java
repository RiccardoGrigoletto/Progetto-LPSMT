package com.example.marco.progettolpsmt;


import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marco.progettolpsmt.backend.Course;
import com.example.marco.progettolpsmt.backend.TimerSettingsSingleton;
import com.example.marco.progettolpsmt.backend.User;
import com.example.marco.progettolpsmt.managers.CalendarUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import devlight.io.library.ntb.NavigationTabBar;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends AppCompatActivity implements Observer {
    final int VIEWS = 3;

    GoogleAccountCredential mCredential;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR};


    public User user;

    ArrayList<Course> courses;
    ArrayList<Event> dailyStudySessions;
    DailyCoursesAdapter<Event> todayAdapter;
    DailyCoursesAdapter<Event> tomorrowAdapter;
    CoursesAdapterMax coursesAdapter;




    protected void onResume() {
        super.onResume();

        //Firebase
        user = User.getInstance();
        user.setName(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        user.updateOnFirestore();
        user.addObserver(this);
        courses = (ArrayList) user.getCourses();

        setContentView(R.layout.activity_main);

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

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

                courses = (ArrayList<Course>) user.getCourses();
                dailyStudySessions = new ArrayList<>();
                switch (position) {
                    case 0: {
                        view = LayoutInflater.from(

                                getBaseContext()).inflate(R.layout.page_0, null, false);

                        ListView lvToday = view.findViewById(R.id.today_events_list_view);

                        todayAdapter = new DailyCoursesAdapter<>(getBaseContext(),
                                R.layout.daily_course, dailyStudySessions);
                        lvToday.setAdapter(todayAdapter);

                        ListView lvTomorrow = view.findViewById(R.id.tomorrow_events_list_view);

                        tomorrowAdapter = new DailyCoursesAdapter<>(getBaseContext(),
                                R.layout.daily_course, dailyStudySessions);
                        lvTomorrow.setAdapter(tomorrowAdapter);

                        getResultsFromApi();
                    }
                    break;
                    case 1: {
                        view = LayoutInflater.from(
                                getBaseContext()).inflate(R.layout.page_1, null, false);

                        ListView courseLV = view.findViewById(R.id.coursesListView);

                        coursesAdapter = new CoursesAdapterMax(getBaseContext(), R.layout.course_card_view_max, courses);
                        courseLV.setAdapter(coursesAdapter);

                    }
                    break;
                    case 2: {
                        view = LayoutInflater.from(
                                getBaseContext()).inflate(R.layout.settings, null, false);
                        //numbers textfields
                        final Spinner numberofsession = view.findViewById(R.id.sessionpicker);
                        TextView studyDuration = view.findViewById(R.id.studyDurationSelector);
                        studyDuration.setText(TimerSettingsSingleton.getInstance().getNumberOfStudyDuration(MainActivity.this) / 60000 + " minutes");
                        TextView breakDuration = view.findViewById(R.id.breakDurationSelector);
                        breakDuration.setText(TimerSettingsSingleton.getInstance().getNumberOfBreakDuration(MainActivity.this) / 60000 + " minutes");

                        //session number picker
                        ArrayAdapter<CharSequence> adapter_1_to10 = ArrayAdapter.createFromResource(getBaseContext(),
                                R.array.one_to_ten_array, android.R.layout.simple_spinner_item);
                        numberofsession.setAdapter(adapter_1_to10);

                        numberofsession.setSelection((int) TimerSettingsSingleton.getInstance().getNumberOfStudySessions(MainActivity.this)-1);
                        numberofsession.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                try {
                                    String temp = (String) numberofsession.getItemAtPosition(i);
                                    TimerSettingsSingleton.getInstance().setNumberOfStudySessions(Integer.parseInt(temp), MainActivity.this);
                                }catch (Exception e){
                                    e.printStackTrace();
                                    Toast.makeText(MainActivity.this, "Il campo non può essere vuoto", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });

                        ((TextView)view.findViewById(R.id.userNameTextView)).setText(user.getName());
                        ((TextView)view.findViewById(R.id.userMailTextView)).setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

                        Button logOut = view.findViewById(R.id.log_out);
                        logOut.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                                intent.putExtra("log-out", LoginActivity.LOG_OUT);
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
                R.drawable.ic_event_note_black_24dp,
                R.color.colorPrimaryDark));
        models.add(ntbModelBuilder(
                R.drawable.ic_list_black_24dp,
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
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), NewCourseActivity.class);
                startActivity(intent);
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_timer: {
                Intent intent = new Intent(getBaseContext(), TimerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                startActivity(intent);
                //finish();
            }
            break;
            case R.id.action_calendar: {
                startActivity(CalendarUtils.getIntent());

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


    public void showNumberPickerDialogForStudyTextView(View v) {

        final Dialog d = new Dialog(MainActivity.this);
        d.setTitle("Study Time");
        d.setContentView(R.layout.number_picker_dialog);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        np.setMaxValue(60);
        np.setMinValue(10);
        np.setWrapSelectorWheel(false);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    TimerSettingsSingleton.getInstance().setDurationOfStudySessions(np.getValue()*60000, MainActivity.this);
                    Toast.makeText(MainActivity.this, "Value Updated", Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Toast.makeText(MainActivity.this, "Impossible Update Values.", Toast.LENGTH_LONG).show();
                }
                ((TextView) findViewById(R.id.studyDurationSelector))
                        .setText(np.getValue() + " minutes");
                d.dismiss();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.show();
    }

    public void showNumberPickerDialogForBreakTextView(View v) {

        final Dialog d = new Dialog(MainActivity.this);
        d.setTitle("Break Time");
        d.setContentView(R.layout.number_picker_dialog);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        np.setMaxValue(60);
        np.setMinValue(10);
        np.setWrapSelectorWheel(false);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    TimerSettingsSingleton.getInstance().setDurationOfBreakSessions(np.getValue()*60000, MainActivity.this);
                    Toast.makeText(MainActivity.this, "Value Updated", Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Toast.makeText(MainActivity.this, "Impossible Update Values.", Toast.LENGTH_LONG).show();
                }
                ((TextView) findViewById(R.id.breakDurationSelector)).setText(np.getValue() + " minutes");
                d.dismiss();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.show();


    }

    @Override
    public void update(Observable o, Object arg) throws ConcurrentModificationException {
        refresh();

    }

    private void refresh() {

        try {
            todayAdapter.notifyDataSetChanged();
            tomorrowAdapter.notifyDataSetChanged();
            coursesAdapter.notifyDataSetChanged();
        } catch (Exception e) {

        }
        try{
            if (todayAdapter.getCount() == 0 ) findViewById(R.id.nothingToday).setVisibility(View.VISIBLE);
            else findViewById(R.id.nothingToday).setVisibility(View.GONE);
            if (tomorrowAdapter.getCount() == 0 ) findViewById(R.id.nothingTomorrow).setVisibility(View.VISIBLE);
            else findViewById(R.id.nothingTomorrow).setVisibility(View.GONE);
            if (coursesAdapter.getCount() == 0 ) findViewById(R.id.noCourses).setVisibility(View.VISIBLE);
            else findViewById(R.id.noCourses).setVisibility(View.GONE);
        }catch(NullPointerException e){
            e.printStackTrace();
        }
    }

    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private void getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential == null || mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
            Toast.makeText(getApplicationContext(),"No network connection available.",Toast.LENGTH_LONG);
        } else {
            new MakeRequestTask(mCredential).execute();
        }
    }
    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, android.Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    android.Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     *
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode  code indicating the result of the incoming
     *                    activity result.
     * @param data        Intent (containing result data) returned by incoming
     *                    activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Toast.makeText(getApplicationContext(),
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.",Toast.LENGTH_LONG);
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     *
     * @param requestCode  The request code passed in
     *                     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }


    /**
     * Checks whether the device currently has a network connection.
     *
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     *
     * @return true if Google Play Services is available and up to
     * date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     *
     * @param connectionStatusCode code describing the presence (or lack of)
     *                             Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                MainActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /**
     * An asynchronous task that handles the Google Calendar API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, List<Event>> {
        private com.google.api.services.calendar.Calendar mService = null;
        private Exception mLastError = null;

        MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Calendar API Android Quickstart")
                    .build();
        }

        /**
         * Fetch a list of the next 10 events from the primary calendar.
         *
         * @return List of Strings describing returned events.
         * @throws IOException
         */
        private List<Event> getDataFromApi(Date targetDay) throws IOException {
            // List the next 10 events from the primary calendar.
            DateTime day = new DateTime(targetDay);
            DateTime maxDay = new DateTime(day.getValue()+1000*60*60*48);
            Events events = mService.events().list("primary")
                    .setMaxResults(50)
                    .setTimeMin(day)
                    .setTimeMax(maxDay)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();

            return events.getItems();
        }

        @Override
        protected List<Event> doInBackground(Void... voids) {
            try {
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DATE);
                c.set(year, month, day, 0, 0, 0);
                return getDataFromApi(c.getTime());
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(List<Event> output) {
            dailyStudySessions = (ArrayList<Event>) output;
            ArrayList<Event> res = new ArrayList<>();
            for (Course c : user.getCourses()) {
                for (Event e: dailyStudySessions) {
                    if (c.getName().equals(e.getSummary())) res.add(e);
                }
            }
            dailyStudySessions = res;
            todayAdapter.setObjects(todayEvents(dailyStudySessions));
            todayAdapter.notifyDataSetChanged();
            tomorrowAdapter.setObjects(tomorrowEvents(dailyStudySessions));
            tomorrowAdapter.notifyDataSetChanged();
            refresh();

        }

        @Override
        protected void onCancelled() {
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            MainActivity.REQUEST_AUTHORIZATION);
                }
            }
        }
    }

    private void filterEvents(ArrayList<Event> dailyStudySessions) {
        for (Course c : user.getCourses()) {
            for (Event e: dailyStudySessions) {
                if (c.getName() != e.getSummary()) dailyStudySessions.remove(e);
            }
        }
    }

    private ArrayList<Event> tomorrowEvents(ArrayList<Event> dailyStudySessions) {
        ArrayList<Event> res = new ArrayList<>();
        Calendar startDay = Calendar.getInstance();
        Calendar endDay = Calendar.getInstance();

        int year = startDay.get(Calendar.YEAR);
        int month = startDay.get(Calendar.MONTH);
        int day = startDay.get(Calendar.DATE);
        startDay.set(year, month, day+1, 0, 0, 0);
        endDay.set(year,month,day+1,23,59,59);

        for (Event e: dailyStudySessions) {
            long eventTime = e.getStart().getDateTime().getValue();
            if (eventTime >= startDay.getTimeInMillis() && eventTime<=endDay.getTimeInMillis()) {
                res.add(e);
            }
        }
        return res;
    }

    private ArrayList<Event> todayEvents(ArrayList<Event> dailyStudySessions) {
        ArrayList<Event> res = new ArrayList<>();
        Calendar startDay = Calendar.getInstance();
        Calendar endDay = Calendar.getInstance();

        int year = startDay.get(Calendar.YEAR);
        int month = startDay.get(Calendar.MONTH);
        int day = startDay.get(Calendar.DATE);
        startDay.set(year, month, day, 0, 0, 0);
        endDay.set(year,month,day,23,59,59);

        for (Event e: dailyStudySessions) {
            long eventTime = e.getStart().getDateTime().getValue();
            if (eventTime >= startDay.getTimeInMillis() && eventTime<=endDay.getTimeInMillis()) {
                res.add(e);
            }
        }
        return res;
    }

}
