package com.example.marco.progettolpsmt;


import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import com.example.marco.progettolpsmt.backend.Argument;
import com.example.marco.progettolpsmt.backend.Course;
import com.example.marco.progettolpsmt.backend.Exam;
import com.example.marco.progettolpsmt.backend.TimerSettingsSingleton;
import com.example.marco.progettolpsmt.managers.CalendarManager;
import com.google.android.gms.tasks.Task;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import devlight.io.library.ntb.NavigationTabBar;
import com.google.api.services.calendar.Calendar;

public class MainActivity extends AppCompatActivity {
    final int VIEWS = 3;
    private String[] studypickervalues = {"5","10","15","20","25","30","35","40","45","50","55","60"};
    private String[] breakpickervalues = {"5","10","15","20","25","30"};
    private String[] sessionpickervalues = {"1","2","3","4","5","6","7","8","9","10"};

    public FirebaseUser user;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Firebase
        user = FirebaseAuth.getInstance().getCurrentUser();
        setContentView(R.layout.activity_main);

        //Toast Login
        Toast.makeText(this,user.getDisplayName(),Toast.LENGTH_LONG).show();

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
                        CoursesAdapterMax coursesAdapter = new CoursesAdapterMax(getBaseContext(),values);
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
}
