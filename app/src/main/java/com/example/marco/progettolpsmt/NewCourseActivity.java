package com.example.marco.progettolpsmt;

import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marco.progettolpsmt.backend.Argument;
import com.example.marco.progettolpsmt.backend.Course;
import com.example.marco.progettolpsmt.backend.Evaluation;
import com.example.marco.progettolpsmt.backend.Exam;
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

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by ricca on 21/10/2017.
 */

public class NewCourseActivity extends AppCompatActivity {
    GoogleAccountCredential mCredential;
    ProgressDialog mProgress;
    private AlertDialog deleteCourseDialog = null;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR};
    private ArrayList<String> day;
    private ArrayList<String> startHour;
    private ArrayList<String> endHour;
    private ArrayList<Exam> exams;
    private String courseToDeleteName ="";
    private boolean isCourseDeleting = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        day = new ArrayList<>();
        startHour  = new ArrayList<>();
        endHour = new ArrayList<>();
        exams  = new ArrayList<>();

        //the logged user
        final User u = User.getInstance();
        Bundle extras = getIntent().getExtras();

        setContentView(R.layout.activity_course);
        Course courseToEdit = null;

        //fetching the course to edit (if any)
        try {
            if (extras != null) {
                ArrayList<Course> courses = (ArrayList) u.getCourses();
                String courseNameToEdit = extras.getString("courseID");
                for (Course c: courses) {
                    if (c.getName().equals(courseNameToEdit))
                        courseToEdit = c;
                }

            }
        }
        catch (NullPointerException e) {}


        Spinner cfuSpinner = findViewById(R.id.CFUSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getBaseContext(),
                R.array.three_to_fifteen_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cfuSpinner.setAdapter(adapter);
        final LinearLayout linearLayoutArguments = findViewById(R.id.argumentsList);

        //arguments
        FloatingActionButton addArgumentButton = findViewById(R.id.addArgumentButton);

        addArgumentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final View view1 = LayoutInflater.from(getBaseContext())
                        .inflate(R.layout.argument_edit_view,null,false);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getBaseContext(),
                        R.array.difficulty_array, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                ((Spinner)view1.findViewById(R.id.difficulty)).setAdapter(adapter);
                final ImageButton deleteArgumentButton = view1.findViewById(R.id.imageButton);
                deleteArgumentButton.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                linearLayoutArguments.removeView(view1);

                            }
                        });
                linearLayoutArguments.addView(view1);

            }
        });
        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Calling Google Calendar ...");

        //exams
        FloatingActionButton addExamButton = findViewById(R.id.addExamButton);

        final LinearLayout linearLayoutExams = findViewById(R.id.examsList);

        addExamButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final View view1 = LayoutInflater.from(getBaseContext())
                            .inflate(R.layout.exam_edit_view,null,false);
                    final TextView examDate = view1.findViewById(R.id.examDate);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());

                    examDate.setText(calendar.get(Calendar.DAY_OF_MONTH) + " / " +
                            (calendar.get(Calendar.MONTH)+1) + " / " + calendar.get(Calendar.YEAR));
                    examDate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDatePickerDialog(v,examDate);
                        }
                    });
                    final ImageButton deleteButton = view1.findViewById(R.id.imageButton);
                    deleteButton.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    linearLayoutExams.removeView(view1);

                                }
                            });
                    linearLayoutExams.addView(view1);
                }
            });

        Button createCourseButton = findViewById(R.id.addCourseButton);

        final  Course finalCourseToEdit = courseToEdit;
        createCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Course course;
                if (finalCourseToEdit != null) {
                    course= createCourse(finalCourseToEdit);

                }
                else {
                    course = createCourse(null);
                }
                if (course != null) {
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.courseLoaded, Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });

        //study dates
        FloatingActionButton addStudyDateButton = findViewById(R.id.addStudyDateButton);

        final LinearLayout linearLayoutStudyDates = findViewById(R.id.studyDateList);

        addStudyDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final View view1 = LayoutInflater.from(getBaseContext())
                        .inflate(R.layout.study_date_edit_view,null,false);

                Spinner studyDateSpinner = view1.findViewById(R.id.studyDate);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getBaseContext(),
                        R.array.week_days, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                studyDateSpinner.setAdapter(adapter);

                final TextView studyDateFrom = view1.findViewById(R.id.studyDateFrom);
                final TextView studyDateTo = view1.findViewById(R.id.studyDateTo);

                studyDateFrom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showTimePickerDialog(view, studyDateFrom);
                    }
                });
                studyDateTo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showTimePickerDialog(view, studyDateTo);
                    }
                });

                linearLayoutStudyDates.addView(view1);
                final ImageButton deleteArgumentButton = view1.findViewById(R.id.imageButton);
                deleteArgumentButton.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                linearLayoutStudyDates.removeView(view1);

                            }
                        });
            }
        });

        //initialize the interface with the course to edit (if any)
        if (courseToEdit != null) {
            //populate activity
            (findViewById(R.id.courseName)).setFocusable(false);
            (findViewById(R.id.courseName)).setFocusableInTouchMode(false);
            ((TextView)findViewById(R.id.courseName)).setText(courseToEdit.getName());
            ((Spinner)findViewById(R.id.CFUSpinner)).setSelection(courseToEdit.getCredits()-3,true);

            for (Argument argument:courseToEdit.getArguments()) {
                final View view1 = LayoutInflater.from(getBaseContext())
                        .inflate(R.layout.argument_edit_view,null,false);
                ((EditText)view1.findViewById(R.id.argumentName)).setText(argument.getName());
                ArrayAdapter<CharSequence> difficultyAdapter = ArrayAdapter.createFromResource(getBaseContext(),
                        R.array.difficulty_array, android.R.layout.simple_spinner_item);
                difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                ((Spinner)view1.findViewById(R.id.difficulty)).setAdapter(difficultyAdapter);
                ((TextView)view1.findViewById(R.id.journalAddress)).setText(argument.getJournal().toString());
                ((Switch)view1.findViewById(R.id.argumentDone)).setChecked(argument.isDone());
                ((Spinner)view1.findViewById(R.id.difficulty)).setSelection(argument.getDifficulty().getPosition(),true);
                final ImageButton deleteArgumentButton = view1.findViewById(R.id.imageButton);

                linearLayoutArguments.addView(view1);

                deleteArgumentButton.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                linearLayoutArguments.removeView(view1);

                            }
                        });
            }
            findViewById(R.id.examsEditLabel).setVisibility(View.VISIBLE);


            for (Exam exam:courseToEdit.getExams()) {
                final View view1 = LayoutInflater.from(getBaseContext())
                        .inflate(R.layout.exam_edit_view,null,false);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(exam.getDate());
                ((TextView)view1.findViewById(R.id.examDate)).setText(calendar.get(Calendar.DAY_OF_MONTH) + " / " +
                        (calendar.get(Calendar.MONTH)+1) + " / " + calendar.get(Calendar.YEAR));
                final ImageButton deleteExamButton = view1.findViewById(R.id.imageButton);
                deleteExamButton.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                linearLayoutExams.removeView(view1);

                            }
                        });

                linearLayoutExams.addView(view1);
            }

            findViewById(R.id.studySessionsEditLabel).setVisibility(View.VISIBLE);


            Button deleteButton = findViewById(R.id.deleteCourseButton);
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteCourseDialog = new AlertDialog.Builder(NewCourseActivity.this)
                            .setTitle("Deleting course")
                            .setMessage("Do you really want to delete this course?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    try{
                                        courseToDeleteName = finalCourseToEdit.getName();
                                        finalCourseToEdit.removeOnFirestore();
                                        isCourseDeleting = true;
                                        getResultsFromApi();
                                        Toast.makeText(getBaseContext(),"Course and Study Session Deleted",Toast.LENGTH_LONG);
                                        finish();
                                    }catch (Exception e){
                                        Toast.makeText(getBaseContext(),"Impossible delete Course. Try later.",Toast.LENGTH_LONG);
                                    }
                                }})
                            .setNegativeButton(android.R.string.no, null).create();
                    deleteCourseDialog.show();
                }
            });
            ((Button)findViewById(R.id.addCourseButton)).setText("Modify");
        }

    }


    /**
     * create a course or edit the course passed by parameter
     * @param from: the course to edit
     * @return the course loaded
     */
    private Course createCourse(@Nullable Course from) {
        Course course;
        String name = ((TextView) findViewById(R.id.courseName)).getText().toString();
        //if from null the course is new, check the name
        if (from == null) {
            //required
            if (Objects.equals(name, "")) {
                ((TextView) findViewById(R.id.courseName)).setShowSoftInputOnFocus(true);
                Toast toast = Toast.makeText(this, R.string.courseNameVoid, Toast.LENGTH_LONG);
                toast.show();
                return null;
            }
            //unique
            try {
                ArrayList<Course> courses = (ArrayList<Course>) User.getInstance().getCourses();

                for (Course c : courses) {
                    if (c.getName().equals(name)) {
                        ((TextView) findViewById(R.id.courseName)).setShowSoftInputOnFocus(true);
                        Toast toast = Toast.makeText(this, R.string.courseNameNotUnique, Toast.LENGTH_LONG);
                        toast.show();
                        return null;
                    }
                }
            } catch (NullPointerException e) {
            }
        }
        //cfu
        Integer cfu = Integer.parseInt(((Spinner) (findViewById(R.id.CFUSpinner))).getSelectedItem().toString());
        if (cfu == null) {
            ((Spinner) findViewById(R.id.CFUSpinner)).setActivated(true);
            Toast toast = Toast.makeText(this, R.string.courseCFUVoid, Toast.LENGTH_LONG);
            toast.show();
            return null;
        }
        //arguments
        LinearLayout argumentsLinearLayout = findViewById(R.id.argumentsList);
        ArrayList<Argument> arguments = new ArrayList<>();
        for (int i = 0; i < argumentsLinearLayout.getChildCount(); i++) {
            String argumentName = ((TextView) argumentsLinearLayout.getChildAt(i).findViewById(R.id.argumentName)).getText().toString();

            Evaluation difficulty;
            List<Argument> oldArguments = null;

            if (from != null) oldArguments = from.getArguments();


            Spinner evaluationSpinner = argumentsLinearLayout.getChildAt(i).findViewById(R.id.difficulty);
            try {
                switch (evaluationSpinner.getSelectedItemPosition()) {
                    case 0:
                        difficulty = Evaluation.SUPER_EASY;
                        break;
                    case 1:
                        difficulty = Evaluation.EASY;
                        break;
                    case 2:
                        difficulty = Evaluation.REGULAR;
                        break;
                    case 3:
                        difficulty = Evaluation.HARD;
                        break;
                    case 4:
                        difficulty = Evaluation.SUPER_HARD;
                        break;
                    default:
                        difficulty = Evaluation.REGULAR;
                        break;
                }
            }
            catch (NullPointerException e) {
                difficulty = Evaluation.REGULAR;
            }
            Argument newArgument = new Argument();
            newArgument.setDifficulty(difficulty);

            try {
                newArgument.setName(argumentName);
                newArgument.setDone(((Switch)argumentsLinearLayout.getChildAt(i).findViewById(R.id.argumentDone)).isChecked());
                for (Argument oldArg: oldArguments) {
                    if ((oldArg.getJournal().toString()).equals(((TextView)argumentsLinearLayout.getChildAt(i).findViewById(R.id.journalAddress)).getText()))
                        newArgument.setJournal(oldArg.getJournal());
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

            arguments.add(newArgument);



        }
        //exams
        LinearLayout examsLinearLayout = findViewById(R.id.examsList);
        DateFormat df = new SimpleDateFormat("dd / MM / yyyy");
        for (int i = 0; i < examsLinearLayout.getChildCount(); i++) {
            Date examDate = null;
            try {
                examDate = df.parse(((TextView) examsLinearLayout.getChildAt(i).findViewById(R.id.examDate)).getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
                //DATE ERROR RECOVERY
            }
            exams.add(new Exam(examDate));

        }
        //study sessions
        LinearLayout studyDatesLinearLayout = findViewById(R.id.studyDateList);
        for (int i = 0; i < studyDatesLinearLayout.getChildCount(); i++) {
            try {
                day.add(((Spinner) studyDatesLinearLayout.getChildAt(i).findViewById(R.id.studyDate)).getSelectedItem().toString());
                startHour.add(((TextView) studyDatesLinearLayout.getChildAt(i).findViewById(R.id.studyDateFrom)).getText().toString());
                endHour.add(((TextView) studyDatesLinearLayout.getChildAt(i).findViewById(R.id.studyDateTo)).getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
                //DATE ERROR RECOVERY
            }
        }

        getResultsFromApi();

        //create a new course
        if (from == null) {
            course = new Course();
        }
        //inizializzo corso da modificare
        else {
            course = from;
        }

        course.setName(name);
        course.setCredits(cfu);

        if (arguments.size() > 0) course.setArguments(arguments);
        else course.clearArguments();
        if (exams.size() > 0) course.setExams(exams);
        else course.clearExams();

        course.updateCourse();
        course.updateOnFirestore();
        return course;
    }

    /**
     * the date picker dialog for the exams
     */
    public void showDatePickerDialog(View v, TextView target) {
        DialogFragment newFragment = new DatePickerFragment(target);
        newFragment.show(getFragmentManager(), "datePicker");
    }

    /**
     *the time picker dialog for the study dates
     */
    private void showTimePickerDialog(View v, TextView target) {
        DialogFragment newFragment = new TimePickerFragment(target);
        newFragment.show(getFragmentManager(), "timePicker");
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
        } else if (mCredential.getSelectedAccountName() == null) {
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
                NewCourseActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /**
     * An asynchronous task that handles the Google Calendar API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, Event> {
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
         * Background task to call Google Calendar API.
         *
         * @param params no parameters needed for this task.
         */
        @Override
        protected Event doInBackground(Void... params) {
            try {
                if(isCourseDeleting == false){
                    return crateEventFromAPI();
                }else{
                    return deleteEvents();
                }
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch a list of the events from the primary calendar.
         *
         * @return deleted Eevent.
         * @throws IOException
         */
        private  Event deleteEvents() throws IOException {
            // List the next 10 events from the primary calendar.
            DateTime now = new DateTime(System.currentTimeMillis());
            List<String> eventStrings = new ArrayList<String>();
            Events events = mService.events().list("primary")
                    .setTimeMin(now)
                    .execute();

            List<Event> items = events.getItems();
            Event deletedEvent = null;
            for (Event event : items) {
                 event.getSummary();
                if(courseToDeleteName.equals(event.getSummary())){
                    mService.events().delete("primary", event.getId()).execute();
                    deletedEvent = event;
                }
            }
            return deletedEvent;
        }
        /**
         * create a Event for the ...
         *
         * @return List of Strings describing returned events.
         * @throws IOException
         */
        private Event crateEventFromAPI() throws IOException {

            Event studyEvent = null;
            Event examEvent = null;
            String courseName = ((TextView)findViewById(R.id.courseName)).getText().toString();
            try{
                //adding study sessions in google calendar
                for(int i = 0 ; i < day.size();i++ ){
                    if(exams.size() != 0){
                        studyEvent = CalendarUtils.eventBuilder(day.get(i),startHour.get(i),endHour.get(i),courseName ,exams.get(0).getDate());
                    }
                    else{
                        studyEvent = CalendarUtils.eventBuilder(day.get(i),startHour.get(i),endHour.get(i),courseName ,null);
                    }

                    studyEvent = mService.events().insert("primary", studyEvent).execute();
                }
                //adding exams in google calendar
                for(int i = 0 ; i < exams.size(); i++){
                    examEvent = CalendarUtils.examEventBuilder(exams.get(i).getDate(),courseName);
                    examEvent = mService.events().insert("primary", examEvent).execute();
                }
            }catch (Exception  e) {
                e.printStackTrace();
            }
            return studyEvent;
        }

        @Override
        protected void onPreExecute() {
            //mProgress.show();
        }

        @Override
        protected void onPostExecute(Event output) {
            mProgress.hide();
            finish();
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            NewCourseActivity.REQUEST_AUTHORIZATION);
                } else {
                    Toast.makeText(getBaseContext(),"The following error occurred:\n"
                            + mLastError.getMessage(),Toast.LENGTH_LONG);
                }
            } else {
                Toast.makeText(getBaseContext(),"Request cancelled.",Toast.LENGTH_LONG);
            }
        }


    }
}
