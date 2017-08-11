package com.example.marco.progettolpsmt;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {



    private CircularProgressBar studytimer=null;
    private Button start;
    private Button stop;
    private Button pause;
    private boolean isStarted= false;
    private TextView counter;
    private int sessionCounter=0;
    int lel = 30;
    //text  timer
    TextTimer textTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        studytimer = (CircularProgressBar) findViewById(R.id.study);
        start = (Button) findViewById(R.id.start);
        stop = (Button) findViewById(R.id.stop);
        pause = (Button) findViewById(R.id.pause);
        counter = (TextView) findViewById(R.id.counter);

        pause.setClickable(false);
        stop.setClickable(false);

        /**
         * Below there is the TextView timer
         */
        textTimer = new TextTimer(1500000,1,studytimer);
        /**
         * adding listener to start button, used to start the animation and resume it
         */
        start.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                if(isStarted==false) {
                    startAnimation(studytimer);
                    stop.setClickable(true);
                    pause.setClickable(true);
                }
                else{
                    textTimer.resume();
                    resumeAnimation(studytimer);
                }
            }
        });

        /**
         * adding listener to pause button. It permit the user to pausing the session
         */
        pause.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
               if(isStarted==true) {
                   textTimer.pause();
                   pauseAnimation(studytimer);
                   start.setText("Resume");
               }

            }
        });
        /**
         * adding listener to button stop, it permit to reset the session.
         */
        stop.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                if(isStarted==true){
                    resetAnimation();
                    isStarted = false;
                    start.setText("Start");
                    textTimer.stop();
                }
            }
        });
    }


    /**
     * Method that runs the timer animation
     * @param timer
     */
    public void startAnimation (final CircularProgressBar timer){
            timer.animateProgressTo(0, 100, new CircularProgressBar.ProgressAnimationListener() {

            @Override
            public void onAnimationStart() {
                            isStarted = true;
                            textTimer.start();
            }

            @Override
            public void onAnimationProgress(int progress) {
               // timer.setTitle(progress + "%");
            }
            @Override
            public void onAnimationFinish() {
                //set started to know if there is a new animation
                isStarted=false;
                sessionCounter++;
                counter.setText(""+sessionCounter);

            }
        });

    }

    /**
     * Method that allow users pausing the session
     * @param timer
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void  pauseAnimation(final CircularProgressBar timer){
        timer.pauseAnimation();
    }

    public void stopAnimation(final CircularProgressBar timer){
        timer.stopAnimation();
    }

    /**
     * Mwthod that permite users to resume the session that was paused
     * @param timer
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void resumeAnimation(final CircularProgressBar timer){
        timer.resumeAnimation();
    }

    /**
     * Method that permite to reset sessions.
     */
    public void resetAnimation(){
        studytimer.clearAnimation();
        studytimer.setProgress(0);
    }
}
