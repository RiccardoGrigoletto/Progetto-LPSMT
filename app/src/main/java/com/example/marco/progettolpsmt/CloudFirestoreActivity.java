package com.example.marco.progettolpsmt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.marco.progettolpsmt.backend.Course;
import com.example.marco.progettolpsmt.backend.FirestoreInitializer;
import com.example.marco.progettolpsmt.backend.User;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class CloudFirestoreActivity extends AppCompatActivity implements Observer {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_firestore);

        FirestoreInitializer.init();
    }

    private List<Course> courses;

    @Override
    protected void onStart() {
        super.onStart();

        User u = User.getInstance();
        u.addObserver(this); // to enable update() to run every time something change
        u.setName("Utente 1");
        u.updateOnFirestore();

        Course c = new Course();
        c.setName("Corso 1");
        c.setCredits(12);
        c.updateOnFirestore();
        c.removeOnFirestore();

        courses = u.getCourses();
    }

    @Override
    public void update(Observable observable, Object o) {
        TextView t = (TextView) findViewById(R.id.hey);
        t.setText("Name: "); t.append(User.getInstance().getName());
        for (Course c: courses) {
            t.append("\n" + c.getName() + " (" + c.getOnFirestore().getId() + ")");
        }
    }
}
