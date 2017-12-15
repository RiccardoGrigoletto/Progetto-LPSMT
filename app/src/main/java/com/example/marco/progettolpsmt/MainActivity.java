package com.example.marco.progettolpsmt;


import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import com.example.marco.progettolpsmt.managers.CalendarManager;
import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Observable;
import java.util.Observer;

import devlight.io.library.ntb.NavigationTabBar;

public class MainActivity extends AppCompatActivity implements Observer {
    final int VIEWS = 3;

    public User user;

    ArrayList<Course> courses;
    CoursesProgressAdapter<Course> progressAdapter;
    DailyCoursesAdapter<Course> dailyAdapter;
    CoursesAdapterMax coursesAdapter;

    protected void onResume() {

        super.onResume();
        refresh();
    }

    protected void onStart() {
        super.onStart();

        //Firebase
        user = User.getInstance();
        user.setName("ugo");
        user.updateOnFirestore();
        user.addObserver(this);
        courses = (ArrayList) user.getCourses();

        setContentView(R.layout.activity_main);

        //Toast Login
        Toast.makeText(this,user.getName(),Toast.LENGTH_LONG).show();

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

                courses = (ArrayList<Course>) user.getCourses();

                switch (position) {
                    case 0: {
                        view = LayoutInflater.from(

                                getBaseContext()).inflate(R.layout.page_0, null, false);

                        ListView lvProgress = view.findViewById(R.id.courses_progress_list_view);

                        ListView lvToday = view.findViewById(R.id.today_events_list_view);


                        progressAdapter = new CoursesProgressAdapter<>(getBaseContext(),
                                R.layout.progress_bar_min, courses);
                        lvProgress.setAdapter(progressAdapter);

                        dailyAdapter = new DailyCoursesAdapter<>(getBaseContext(),
                                R.layout.daily_course, courses);
                        lvToday.setAdapter(dailyAdapter);
                    }
                    break;
                    case 1: {
                        view = LayoutInflater.from(
                                getBaseContext()).inflate(R.layout.page_1, null, false);

                        ListView courseLV = view.findViewById(R.id.coursesListView);

                        coursesAdapter = new CoursesAdapterMax(getBaseContext(),R.layout.course_card_view_max,courses);
                        courseLV.setAdapter(coursesAdapter);

                    }
                    break;
                    case 2: {
                        view = LayoutInflater.from(
                                getBaseContext()).inflate(R.layout.settings, null, false);
                        //numbers textfields
                        final Spinner numberofsession = view.findViewById(R.id.sessionpicker);
                        TextView studyDuration = view.findViewById(R.id.studyDurationSelector);
                        studyDuration.setText(TimerSettingsSingleton.getInstance().getNumberOfStudyDuration(MainActivity.this)/60000+ " minutes");
                        TextView breakDuration = view.findViewById(R.id.breakDurationSelector);
                        breakDuration.setText(TimerSettingsSingleton.getInstance().getNumberOfBreakDuration(MainActivity.this)/60000+ " minutes");

                        //session number picker
                        ArrayAdapter<CharSequence> adapter_1_to10 = ArrayAdapter.createFromResource(getBaseContext(),
                                R.array.one_to_ten_array, android.R.layout.simple_spinner_item);
                        numberofsession.setAdapter(adapter_1_to10);
                        numberofsession.setSelection((int)TimerSettingsSingleton.getInstance().getNumberOfStudySessions(MainActivity.this)%10);
                        numberofsession.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                try {   //Log.d("------------------>","During"+numberofsession.getText().toString());
                                    String temp = (String) numberofsession.getItemAtPosition(i);
                                    TimerSettingsSingleton.getInstance().setNumberOfStudySessions(Integer.parseInt(temp), MainActivity.this);
                                    Log.d("------------------>", "sdksojdajaosdjao" + TimerSettingsSingleton.getInstance().getNumberOfStudySessions(getApplicationContext()));
                                }catch (Exception e){
                                    e.printStackTrace();
                                    Toast.makeText(MainActivity.this, "Il campo non può essere vuoto", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
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
                intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
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


    public void showNumberPickerDialogForStudyTextView(View v)
    {

        final Dialog d = new Dialog(MainActivity.this);
        d.setTitle("Study Time");
        d.setContentView(R.layout.number_picker_dialog);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        np.setMaxValue(60);
        np.setMinValue(10);
        np.setWrapSelectorWheel(false);
        b1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                try {
                    TimerSettingsSingleton.getInstance().setDurationOfStudySessions(np.getValue()*60000, MainActivity.this);
                    Log.d("------------------>", "sdksojdajaosdjao" + TimerSettingsSingleton.getInstance().getNumberOfStudyDuration(getApplicationContext()));
                    Toast.makeText(MainActivity.this, "Value Updated", Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Toast.makeText(MainActivity.this, "Il campo non può essere vuoto", Toast.LENGTH_LONG).show();
                }
                ((TextView)findViewById(R.id.studyDurationSelector))
                        .setText(np.getValue() + " minutes");
                d.dismiss();
            }
        });
        b2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.show();


    }
    public void showNumberPickerDialogForBreakTextView(View v)
    {

        final Dialog d = new Dialog(MainActivity.this);
        d.setTitle("Break Time");
        d.setContentView(R.layout.number_picker_dialog);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        np.setMaxValue(60);
        np.setMinValue(10);
        np.setWrapSelectorWheel(false);
        b1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                try {
                    TimerSettingsSingleton.getInstance().setDurationOfBreakSessions(np.getValue()*60000, MainActivity.this);
                    Log.d("------------------>", "sdksojdajaosdjao" + TimerSettingsSingleton.getInstance().getNumberOfBreakDuration(getApplicationContext()));
                    Toast.makeText(MainActivity.this, "Value Updated", Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Toast.makeText(MainActivity.this, "Il campo non può essere vuoto", Toast.LENGTH_LONG).show();
                }
                ((TextView)findViewById(R.id.breakDurationSelector)).setText(np.getValue() + " minutes");
                d.dismiss();
            }
        });
        b2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.show();


    }

    @Override
    public void update(Observable o, Object arg) throws ConcurrentModificationException {
        // quando sei qui dentro, courses e gia aggiornato
        refresh();

//        ArrayList<Course> newCourses = (ArrayList) user.getCourses();
//        boolean exception = false;
//        try {
//            for (Course c : newCourses) {
//                if (courses.contains(c)) {
//                    newCourses.remove(c);
//                }
//            }
//        }
//        catch (ConcurrentModificationException e) {
//            exception=true;
//        }
//        if (!exception) {
//            courses.addAll(newCourses);
//            progressAdapter.addAll(newCourses);
//            dailyAdapter.addAll(newCourses);
//            coursesAdapter.addAll(newCourses);
//        }
    }

    private void refresh() {

        try {
            progressAdapter.notifyDataSetChanged();
            dailyAdapter.notifyDataSetChanged();
            coursesAdapter.notifyDataSetChanged();
        }
        catch (Exception e) {

        }
    }
}
