package com.example.marco.progettolpsmt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class WearableActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String message = intent.getStringExtra("courseName");
        try {
            Log.d("DIOAND", message);
        } catch (NullPointerException e) {

        }
        setContentView(R.layout.activity_wearable);

    }
}
