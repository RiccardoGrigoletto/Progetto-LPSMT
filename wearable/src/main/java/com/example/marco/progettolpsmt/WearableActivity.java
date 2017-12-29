package com.example.marco.progettolpsmt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class WearableActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wearable);

        Intent intent = getIntent();
        String courseName = intent.getStringExtra("courseName");
        try {
            ((TextView)findViewById(R.id.courseTextView)).setText(courseName);

        } catch (NullPointerException e) {

        }

    }
}
