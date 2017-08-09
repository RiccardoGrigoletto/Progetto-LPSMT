package com.example.marco.progettolpsmt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final CircularProgressBar studytimer = (CircularProgressBar) findViewById(R.id.study);

        studytimer.animateProgressTo(0, 100, new CircularProgressBar.ProgressAnimationListener() {

            @Override
            public void onAnimationStart() {
            }

            @Override
            public void onAnimationProgress(int progress) {
                studytimer.setTitle(progress + "%");
            }

            @Override
            public void onAnimationFinish() {
                studytimer.setSubTitle("done");
            }
        });

        /*Intent intent = new Intent(this, TimerActivity.class);
        startActivity(intent);*/
    }
}
