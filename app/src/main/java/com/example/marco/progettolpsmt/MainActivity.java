package com.example.marco.progettolpsmt;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.marco.progettolpsmt.backend.Course;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import devlight.io.library.ntb.NavigationTabBar;

public class MainActivity extends AppCompatActivity {
    final int VIEWS = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                switch (position) {
                    case 0: {
                        view = LayoutInflater.from(
                                getBaseContext()).inflate(R.layout.page_0, null, false);

                        ListView lvProgress = view.findViewById(R.id.courses_progress_list_view);

                        ListView lvDay = view.findViewById(R.id.daily_events_list_view);

                        ArrayList<Course> values = new ArrayList<>();
                        try {
                            values.add(new Course());
                            values.add(new Course());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        CoursesProgressAdapter<Course> progressAdapter = new CoursesProgressAdapter<>(getBaseContext(),
                                R.layout.progress_bar_min,values);
                        lvProgress.setAdapter(progressAdapter);

                        DailyCoursesAdapter<Course> adapter = new DailyCoursesAdapter<>(getBaseContext(),
                                R.layout.daily_course,values);
                        lvDay.setAdapter(adapter);
                    } break;
                    case 1: {
                        view = LayoutInflater.from(
                                getBaseContext()).inflate(R.layout.page_1, null, false);
                        RecyclerView coursesRV = view.findViewById(R.id.coursesRecyclerView);
                        ArrayList<Course> courses = new ArrayList<>();
                        courses.add(new Course());
                        courses.add(new Course());
                        RecyclerView.Adapter myAdapter = new CoursesAdapter (courses);

                        // use a linear layout manager
                        RecyclerView.LayoutManager  mLayoutManager = new LinearLayoutManager(getBaseContext());
                        coursesRV.setLayoutManager(mLayoutManager);

                        coursesRV.setAdapter(myAdapter);

                        /*final View argument = LayoutInflater.from(getBaseContext())
                                .inflate(R.layout.argument_view,null,false);
                        coursesRV.addView(argument);*/
                    } break;
                    case 2: {
                        view = LayoutInflater.from(
                                getBaseContext()).inflate(R.layout.settings, null, false);

                    } break;
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
                    R.drawable.ic_settings_black_24px,
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
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), NewCourseActivity.class);
                startActivity(intent);
            }
        });
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            /*sample
            case R.id.action_new:
                Intent intent = new Intent(this, NewCourseActivity.class);
                startActivity(intent);
                return true;
                */
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
