package com.example.marco.progettolpsmt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cn.iwgang.countdownview.CountdownView;

public class WearableActivity extends Activity implements Runnable {

    CountdownView cdv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_wearable);

        Intent intent = getIntent();
        String courseName = intent.getStringExtra("courseName");
        String argumentName = intent.getStringExtra("argumentName");
        final String status = intent.getStringExtra("status");
        long remainingTime = intent.getLongExtra("remainingTime", 0);

        ((TextView) findViewById(R.id.courseTextView)).setText(courseName);
        ((TextView) findViewById(R.id.argumentTextView)).setText(argumentName);
        CountdownView cdv = findViewById(R.id.countdownview);
        cdv.updateShow(remainingTime);


        intent = new Intent(this, NotificationUpdateService.class);
        MyResultReceiver resultReceiver = new MyResultReceiver(null);
        intent.putExtra("receiver", resultReceiver);
        startService(intent);

        try {
            if (status.equals("start")) cdv.start(remainingTime);
            if (status.equals("puase")) cdv.pause();
        } catch (NullPointerException e) {
        }
    }
    @Override
    public void run() {
        cdv.pause();
    }

    class MyResultReceiver extends ResultReceiver
    {
        public MyResultReceiver(Handler handler) {
            super(handler);
        }
    }
}
