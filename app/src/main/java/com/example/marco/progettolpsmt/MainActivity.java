package com.example.marco.progettolpsmt;

import android.app.DialogFragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
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

        final ViewPager viewPager = (ViewPager) findViewById(R.id.vp_ntb);
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
                                R.layout.progress_bar,values);
                        lvProgress.setAdapter(progressAdapter);

                        DailyCoursesAdapter<Course> adapter = new DailyCoursesAdapter<>(getBaseContext(),
                                R.layout.daily_course,values);
                        lvDay.setAdapter(adapter);
                    } break;
                    case 1: {
                        view = LayoutInflater.from(
                                getBaseContext()).inflate(R.layout.page_1, null, false);

                    } break;
                    case 2: {

                        view = LayoutInflater.from(
                                getBaseContext()).inflate(R.layout.page_2, null, false);
                        Spinner spinner = view.findViewById(R.id.CFUSpinner);
                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getBaseContext(),
                                R.array.CFU_array, android.R.layout.simple_spinner_item);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(adapter);

                        final LinearLayout linearLayoutArguments = view.findViewById(R.id.argoumentsList);
                        View view1 = LayoutInflater.from(getBaseContext())
                                .inflate(R.layout.argument_view, null, false);
                        linearLayoutArguments.addView(view1);

                        FloatingActionButton addArgumentButton = view.findViewById(R.id.addArgumentButton);

                        addArgumentButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                View view1 = LayoutInflater.from(getBaseContext())
                                        .inflate(R.layout.argument_view,null,false);
                                linearLayoutArguments.addView(view1);
                            }
                        });
                        FloatingActionButton deleteArgumentButton = view.findViewById(R.id.deleteArgumentButton);

                        deleteArgumentButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                View view1 = LayoutInflater.from(getBaseContext())
                                        .inflate(R.layout.argument_view,null,false);
                                linearLayoutArguments.addView(view1);
                            }
                        });

                        Button addExamButton = view.findViewById(R.id.addExamButton);

                        addExamButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showDatePickerDialog(view);
                            }

                        });



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
                    R.drawable.ic_add_black_24dp,
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

        //attach timer
       /* myToolbar.findViewById(R.id.action_timer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/
        /*myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // new TimerActivity();

            }
        });*/


    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.action_timer) {
            Intent intent = new Intent(getBaseContext(),TimerActivity.class);
            startActivity(intent);
            finish();
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
    public void showDatePickerDialog (View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }
}
