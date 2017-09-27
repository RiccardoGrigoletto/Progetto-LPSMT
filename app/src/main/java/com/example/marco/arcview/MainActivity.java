package com.example.marco.arcview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.Button;

import java.util.ArrayList;

import devlight.io.library.ArcProgressStackView;

import static devlight.io.library.ArcProgressStackView.Model;
//import static devlight.io.sample.MainActivity.MODEL_COUNT;

/**
 *
 *
 * Created by GIGAMOLE on 9/21/16.
 */

public class MainActivity extends Activity {

    private int mCounter = 0;
    int finished = 0;
    public final static int MODEL_COUNT = 3;
    private ArcProgressStackView mArcProgressStackView;
    private Button startbutton;
    private Button pause;
    private Button reset;
    private int numberofripetition =0;
    private long animationstate;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

        startbutton = (Button) findViewById(R.id.startbtn);
        pause =(Button) findViewById(R.id.pausebtn);
        mArcProgressStackView = (ArcProgressStackView) findViewById(R.id.apsv_presentation);
        mArcProgressStackView.setShadowColor(Color.argb(200, 0, 0, 0));
        mArcProgressStackView.setAnimationDuration(25000);
        mArcProgressStackView.setSweepAngle(270);
        /*mArcProgressStackView.setInterpolator(new Interpolator() {
            @Override
            public float getInterpolation(float input) {
                return 0.00000000000000000001F;
            }
        });*/


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


        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(lel);
        final ValueAnimator va2 = ValueAnimator.ofFloat(lel);
        valueAnimator.setDuration(2500);
        // valueAnimator.setStartDelay(200);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.setRepeatCount(MODEL_COUNT - 2);
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(final Animator animation) {
                mCounter = 0;
                numberofripetition++;
                startbutton.setClickable(true);
                finished=1;
                va2.start();
                // mArcProgressStackView.getModels().get(MODEL_COUNT - 1).setProgress(25);
            }
            @Override
            public void onAnimationRepeat(final Animator animation) {
                mCounter++;
            }
        });
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                mArcProgressStackView.getModels().get(Math.min(mCounter, MODEL_COUNT - 2))  //Math.min(mCounter, MODEL_COUNT - 2)
                        .setProgress((Float) animation.getAnimatedValue());
                mArcProgressStackView.postInvalidate();
            }
        });

        //va2.addListener();

        va2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                mArcProgressStackView.getModels().get(MODEL_COUNT - 1)  //Math.min(mCounter, MODEL_COUNT - 2)
                        .setProgress((Float) animation.getAnimatedValue() / 4);mArcProgressStackView.postInvalidate();
            }
        });
        startbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            @TargetApi(Build.VERSION_CODES.KITKAT)
            public void onClick(View v) {
                if(animationstate != 0) {valueAnimator.resume();return;}
                valueAnimator.start();
                startbutton.setClickable(false);
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                valueAnimator.pause();
                startbutton.setClickable(true);
            }
        });

    }
}
