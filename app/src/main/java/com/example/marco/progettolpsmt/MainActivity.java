package com.example.marco.progettolpsmt;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marco.progettolpsmt.backend.Argument;
import com.example.marco.progettolpsmt.backend.Course;
import com.example.marco.progettolpsmt.backend.Exam;
import com.example.marco.progettolpsmt.backend.TimerSettingsSingleton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

import devlight.io.library.ntb.NavigationTabBar;

public class MainActivity extends AppCompatActivity {
    final int VIEWS = 3;


    public FirebaseUser user;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = FirebaseAuth.getInstance().getCurrentUser();
        setContentView(R.layout.activity_main);

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

        /*page creation*/
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

                        ListView lvDay = view.findViewById(R.id.daily_events_list_view);


                        CoursesProgressAdapter<Course> progressAdapter = new CoursesProgressAdapter<>(getBaseContext(),
                                R.layout.progress_bar_min, values);
                        lvProgress.setAdapter(progressAdapter);

                        DailyCoursesAdapter<Course> adapter = new DailyCoursesAdapter<>(getBaseContext(),
                                R.layout.daily_course, values);
                        lvDay.setAdapter(adapter);
                    }
                    break;
                    case 1: {
                        view = LayoutInflater.from(
                                getBaseContext()).inflate(R.layout.page_1, null, false);
                       /* RecyclerView coursesRV = view.findViewById(R.id.coursesRecyclerView);
                        RecyclerView.Adapter myAdapter = new CoursesAdapter(values);

                        // use a linear layout manager
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getBaseContext());
                        coursesRV.setLayoutManager(mLayoutManager);

                        coursesRV.setAdapter(myAdapter);*/

                        /*final View argument = LayoutInflater.from(getBaseContext())
                                .inflate(R.layout.argument_view,null,false);
                        coursesRV.addView(argument);*/

                        ExpandableListView extendedLisView = view.findViewById(R.id.coursesExtendableListView);
                        extendedLisView.setAdapter(new CourseExpandableListAdapter(getBaseContext(),values));
                    }
                    break;
                    case 2: {
                        view = LayoutInflater.from(
                                getBaseContext()).inflate(R.layout.settings, null, false);
                        LinearLayout settingsLL = view.findViewById(R.id.settingsLinearLayout);
                        //numbers textfields
                        final EditText numberofsession = view.findViewById(R.id.nsession);
                        final EditText studyduration = view.findViewById(R.id.studyduration);
                        final EditText breakduration = view.findViewById(R.id.breakduration);
                        //onchange listeners
                        numberofsession.setText(String.valueOf(TimerSettingsSingleton.getInstance().getNumberOfStudySessions(MainActivity.this)));
                        studyduration.setText(String.valueOf(TimerSettingsSingleton.getInstance().getNumberOfStudyDuration(MainActivity.this)));
                        breakduration.setText(String.valueOf(TimerSettingsSingleton.getInstance().getNumberOfBreakDuration(MainActivity.this)));
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
                                Log.d("------------------>","During"+numberofsession.getText().toString());
                                TimerSettingsSingleton.getInstance().setNumberOfStudySessions(Integer.parseInt(numberofsession.getText().toString()),MainActivity.this);

                                Toast.makeText(MainActivity.this, "Impossible Add preference",Toast.LENGTH_LONG).show();

                            }
                        });

                        Log.d("------------------>","During"+TimerSettingsSingleton.getInstance().getNumberOfStudySessions(getApplicationContext()));
                        ConstraintLayout settingStudyTime = (ConstraintLayout) getLayoutInflater().inflate(R.layout.setting_constraint,null);
                        ((TextView)settingStudyTime.findViewById(R.id.name)).setText("ore di studio");
                        settingsLL.addView(settingStudyTime);

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
        if (menuItem.getItemId() == R.id.action_timer) {
            Intent intent = new Intent(getBaseContext(),TimerActivity.class);
            startActivity(intent);
            //finish();
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
