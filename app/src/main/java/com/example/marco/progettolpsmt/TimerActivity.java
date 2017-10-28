package com.example.marco.progettolpsmt;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.ArrayList;

import devlight.io.library.ArcProgressStackView;

import static devlight.io.library.ArcProgressStackView.Model;


public class TimerActivity extends Activity {

    private int mCounter = 0;
    public final static int MODEL_COUNT = 3;
    private ArcProgressStackView mArcProgressStackView;
    private Button startbutton;
    private Button pause;
    private Button reset;
    private int session = 0;
    private long current_playtime =0;
    private long animationstate;
    private int n_session = 4;
    private long studytimetimer;
    private long breaktimetimer;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        setContentView(R.layout.activity_timer);
        super.onCreate(savedInstanceState);
        //final LayoutInflater inflater = MainActivity.this.getLayoutInflater();//(LayoutInflater) getApplicationContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        //Dialog used in order to take data from user, that we need in order to initializate timer
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.timerinitializationpopup);
        Button yourButton = dialog.findViewById(R.id.button);
        //textbox of the dialog
        final EditText sessions = dialog.findViewById(R.id.editText2);
        final EditText studytime = dialog.findViewById(R.id.editText3);
        final EditText breaktime = dialog.findViewById(R.id.editText4);

        //adding listener to buttons
        yourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( !((sessions.getText()).toString().equals("")) && !((studytime.getText()).toString().equals("")) && !((breaktime.getText()).toString().equals(""))) {
                    //go on here and dismiss dialog
                    n_session = Integer.parseInt(sessions.getText().toString());
                    studytimetimer = Long.parseLong(studytime.getText().toString());
                    breaktimetimer = Long.parseLong(breaktime.getText().toString());
                    dialog.dismiss();
                }
                else{
                    Toast.makeText(view.getContext(),"Invalid data", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();

        //ArcProgressView initialization
        startbutton = (Button) findViewById(R.id.startbtn);
        pause =(Button) findViewById(R.id.pausebtn);
        mArcProgressStackView = (ArcProgressStackView) findViewById(R.id.apsv_presentation);
        //mArcProgressStackView.setShadowColor(Color.argb(200, 0, 0, 0));
        mArcProgressStackView.setAnimationDuration(25000);
        mArcProgressStackView.setSweepAngle(270);
        //circle creation
        final ArrayList<ArcProgressStackView.Model> models = new ArrayList<>();
        models.add(new ArcProgressStackView.Model("Study Time", 0, R.color.white,R.color.colorPrimary));
        models.add(new ArcProgressStackView.Model("Break Time", 0,R.color.white , R.color.colorPrimary));
        models.add(new ArcProgressStackView.Model("Session Progress", 0, R.color.white, R.color.colorPrimary));
        mArcProgressStackView.setModels(models);

        float[] lel = new float[200];
        for(int i = 0 ; i < 150 ; i++){
            lel[i] =(float)i;
        }
        final ValueAnimator firstarc = ValueAnimator.ofFloat(lel);
        final ValueAnimator secondarc = ValueAnimator.ofFloat(lel);
        final ValueAnimator thirdarc = ValueAnimator.ofFloat(lel);
        firstarc.setDuration(2500);
        secondarc.setDuration(333);
        /**
         * On end listeners. This listeners are used in order to allow graphic sync between circles.
         * When a circle animation end, the onEndListener update
         */
        firstarc.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(final Animator animation) {
                mCounter = 0;
                animationstate = 0;
                session += (100)/n_session;
                secondarc.start();
            }
            @Override
            public void onAnimationRepeat(final Animator animation) {
                mCounter++;
            }
        });

        secondarc.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(final Animator animation) {
                Log.d("sono","entrato");
                thirdarc.start();
            }
            @Override
            public void onAnimationRepeat(final Animator animation) {

            }
        });

        thirdarc.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(final Animator animation) {
                startbutton.setClickable(true);
                animationstate = 0;
                if(n_session == 0){
                    n_session = 4;
                    thirdarc.setCurrentPlayTime(-1);
                }
            }
            @Override
            public void onAnimationRepeat(final Animator animation) {

            }
        });

        /**
         * Animator update listener. This methods are used to update graphics animation of
         * the ArchModel. Every circle own a method that update graphics valued differently as the other
         * due to different values setted by user before the animation starts.
         */

        firstarc.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                mArcProgressStackView.getModels().get(MODEL_COUNT-3)  //Math.min(mCounter, MODEL_COUNT - 2)
                        .setProgress((Float) animation.getAnimatedValue());
                animationstate = animation.getCurrentPlayTime();
                mArcProgressStackView.postInvalidate();
            }
        });

        secondarc.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                mArcProgressStackView.getModels().get(MODEL_COUNT-2)  //Math.min(mCounter, MODEL_COUNT - 2)
                        .setProgress((Float) animation.getAnimatedValue());
                animationstate = animation.getCurrentPlayTime();
                mArcProgressStackView.postInvalidate();
            }
        });

        thirdarc.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                Log.d("Animator", String.valueOf(animation.getAnimatedValue() ));
                Log.d("Session", String.valueOf(session));
                Log.d("Number of session", String.valueOf(n_session));
                Log.d("Current playtime", String.valueOf(current_playtime));
                mArcProgressStackView.getModels().get(MODEL_COUNT - 1).setProgress(session);
            }
        });


        /**
         * Button listeners
         */
        startbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            @TargetApi(Build.VERSION_CODES.KITKAT)
            public void onClick(View v) {
                if(animationstate != 0) {firstarc.resume(); startbutton.setClickable(false); return;}
                firstarc.start();
                //cambiare il colore del bottone
                startbutton.setClickable(false);
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                firstarc.pause();
                startbutton.setClickable(true);
            }
        });

    }
}
