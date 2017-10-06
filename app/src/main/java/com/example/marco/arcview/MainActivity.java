package com.example.marco.arcview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.app.Dialog;
import java.util.ArrayList;
import android.widget.Toast;
import devlight.io.library.ArcProgressStackView;
import static devlight.io.library.ArcProgressStackView.Model;
/**
 *
 *
 * Created by GIGAMOLE on 9/21/16.
 */

public class MainActivity extends Activity {

    private int mCounter = 0;
    public final static int MODEL_COUNT = 3;
    //private final float max_value = 105.0F, min_value = 0.0F;
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
        setContentView(R.layout.activity_main);
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
                Log.d("Entratoo","lel");
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
        mArcProgressStackView.setShadowColor(Color.argb(200, 0, 0, 0));
        //mArcProgressStackView.setAnimationDuration(25000);
        mArcProgressStackView.setSweepAngle(270);
        final String[] stringColors = getResources().getStringArray(R.array.devlight);
        final String[] stringBgColors = getResources().getStringArray(R.array.bg);

        final int[] colors = new int[MODEL_COUNT];
        final int[] bgColors = new int[MODEL_COUNT];
        for (int i = 0; i < MODEL_COUNT; i++) {
            colors[i] = Color.parseColor(stringColors[i]);
            bgColors[i] = Color.parseColor(stringBgColors[i]);
        }

        //circle creation
        final ArrayList<ArcProgressStackView.Model> models = new ArrayList<>();
        models.add(new Model("Study Time", 0, bgColors[0], colors[0]));
        models.add(new Model("Break Time", 0, bgColors[1], colors[1]));
        models.add(new Model("Session Progress", 0, bgColors[2], colors[2]));
        mArcProgressStackView.setModels(models);

        //105.0F
        float[] lel = new float[200];
        for(int i = 0 ; i < 150 ; i++){
            lel[i] =(float)i;
        }
        final ValueAnimator firstarc = ValueAnimator.ofFloat(lel);
        final ValueAnimator va2 = ValueAnimator.ofFloat(lel);

        firstarc.setDuration(2500);
        // valueAnimator.setStartDelay(200);
        firstarc.setRepeatMode(ValueAnimator.RESTART);
        firstarc.setRepeatCount(MODEL_COUNT - 2);
        firstarc.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(final Animator animation) {
                mCounter = 0;
                animationstate = 0;
                session += (100)/n_session;
                va2.start();
            }
            @Override
            public void onAnimationRepeat(final Animator animation) {
                mCounter++;
            }
        });

        va2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(final Animator animation) {

                startbutton.setClickable(true);
                animationstate = 0;
                if(n_session == 0){
                    n_session = 4;
                    va2.setCurrentPlayTime(-1);

                }
            }
            @Override
            public void onAnimationRepeat(final Animator animation) {

            }
        });

        firstarc.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                mArcProgressStackView.getModels().get(Math.min(mCounter, MODEL_COUNT - 2))  //Math.min(mCounter, MODEL_COUNT - 2)
                        .setProgress((Float) animation.getAnimatedValue());
                animationstate = animation.getCurrentPlayTime();
                mArcProgressStackView.postInvalidate();
            }
        });
        va2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                Log.d("Animator",String.valueOf(animation.getAnimatedValue() ));
                Log.d("Session",String.valueOf(session));
                Log.d("Number of session",String.valueOf(n_session));
                Log.d("Current playtime",String.valueOf(current_playtime));
                mArcProgressStackView.getModels().get(MODEL_COUNT - 1).setProgress(session);
            }
        });

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
