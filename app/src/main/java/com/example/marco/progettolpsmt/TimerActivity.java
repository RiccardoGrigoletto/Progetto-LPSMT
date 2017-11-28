package com.example.marco.progettolpsmt;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

//import com.example.marco.progettolpsmt.backend.Log;
import com.example.marco.progettolpsmt.backend.Course;
import com.example.marco.progettolpsmt.backend.TimerSettingsSingleton;
import java.util.ArrayList;
import cn.iwgang.countdownview.CountdownView;
import devlight.io.library.ArcProgressStackView;
import static devlight.io.library.ArcProgressStackView.Model;


public class TimerActivity extends AppCompatActivity {

    private int mCounter = 0;
    public final static int MODEL_COUNT = 3;
    private ArcProgressStackView mArcProgressStackView;
    private Button startButton;
    private Button pause;
    private Button settings;
    private CountdownView  countdownview;
    private long animationstate=0, animationStateSecondArch =0, thirdArchAnimationState =0;
    private long nSession = 4;
    private long studyTimeTimer;
    private Spinner courseSpinner;
    private Spinner argumentSpinner;
    private long breakTimeTimer;
    private boolean isDialogSetted = false;
    private Course courses;
    //circle creation
    private ArrayList<Model> models;
    //animator declaration and initialization
    final ValueAnimator firstarc = ValueAnimator.ofFloat(100);
    final ValueAnimator secondarc = ValueAnimator.ofFloat(100);
    final ValueAnimator thirdarc = ValueAnimator.ofFloat(100);
    //on finish animation declaration
    final ValueAnimator reverseFirstArch = ValueAnimator.ofFloat(100);
    final ValueAnimator reverseSecondArch = ValueAnimator.ofFloat(100);
    final ValueAnimator reverseThirdArch = ValueAnimator.ofFloat(100);
    //backends classes
    private Course course;
    //settings dialog


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        setContentView(R.layout.activity_timer);
        super.onCreate(savedInstanceState);
        //backend example
        course = new Course();
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.timerinitializationpopup);
        Button confirmTemporarySettingsButton = dialog.findViewById(R.id.button);
        //textbox of the dialog
        final EditText sessions = dialog.findViewById(R.id.editText2);
        final EditText studyTimeDialogTextbox = dialog.findViewById(R.id.editText3);
        final EditText breakTimeDialogTextbox = dialog.findViewById(R.id.editText4);
        //Dialog used in order to take data from user, that we need in order to initializate timer
        final AlertDialog confirmchangecourseargumentdialog = new AlertDialog.Builder(this)
                .setTitle("Change Course or Argument")
                .setMessage("Are you sure that you want to change argument or course?\n You will lose current progress..")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }})
                .setNegativeButton(android.R.string.no, null).create();;



        //spinners
        courseSpinner = findViewById(R.id.coursespinner);
        argumentSpinner = findViewById(R.id.argumentspinner);

       // coursespinner.set(course.getName());
        ;
        //buttons
        startButton = (Button) findViewById(R.id.startbtn);
        pause =(Button) findViewById(R.id.pausebtn);
        settings = (Button) findViewById(R.id.settings);
        //testual timer
        countdownview = findViewById(R.id.countdownview);
        //arch model
        mArcProgressStackView = (ArcProgressStackView) findViewById(R.id.apsv_presentation);
        //model array
        models = new ArrayList<>();

        //adding listener to buttons
        confirmTemporarySettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( !((sessions.getText()).toString().equals("")) && !((studyTimeDialogTextbox.getText()).toString().equals("")) && !((breakTimeDialogTextbox.getText()).toString().equals(""))) {
                    //go on here and dismiss dialog
                    nSession = Integer.parseInt(sessions.getText().toString());
                    studyTimeTimer = Long.parseLong(studyTimeDialogTextbox.getText().toString())*60000;
                    breakTimeTimer = Long.parseLong(breakTimeDialogTextbox.getText().toString())*60000;
                    isDialogSetted = true;
                    countdownview.updateShow(studyTimeTimer);
                    initializeArcModel(nSession, studyTimeTimer, breakTimeTimer);
                    initializeTimerView(thirdarc,secondarc,firstarc,mArcProgressStackView);
                    dialog.dismiss();
                }
            }
        });
         /*
           if user doesn't set values, the system will use default setted values
         */
         if(isDialogSetted == false) {
             nSession = TimerSettingsSingleton.getInstance().getNumberOfStudySessions(this);
             studyTimeTimer = TimerSettingsSingleton.getInstance().getNumberOfStudyDuration(this);
             breakTimeTimer = TimerSettingsSingleton.getInstance().getNumberOfBreakDuration(this);
             countdownview.updateShow(studyTimeTimer);
         }

        //ArcProgressView initialization
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            models.add(new Model("Study Time", 0,getColor(R.color.colorPrimary),getColor(R.color.colorAccent)));
            models.add(new Model("Break Time", 0,getColor(R.color.colorPrimary) , getColor(R.color.colorAccent)));
            models.add(new Model("Session Progress", 0, getColor(R.color.colorPrimary), getColor(R.color.colorAccent)));

        }
        else{
            models.add(new Model("Study Time", 0,Color.parseColor("#00bcd4"),Color.parseColor("#ff5722")));
            models.add(new Model("Break Time", 0,Color.parseColor("#00bcd4") , Color.parseColor("#ff5722")));
            models.add(new Model("Session Progress", 0,Color.parseColor("#00bcd4"), Color.parseColor("#ff5722")));
        }
        mArcProgressStackView.setModels(models);
        mArcProgressStackView.setSweepAngle(270);
        /**
         * firstArch == external arch
         * secondArch = middle arch
         * thirdArch = internal arch
         * Setting up Animators
         */
        this.initializeArcModel(nSession, studyTimeTimer, breakTimeTimer);
        firstarc.setInterpolator(new LinearInterpolator());
        secondarc.setInterpolator(new LinearInterpolator());
        thirdarc.setInterpolator(new LinearInterpolator());

        /**
         * setting up on finish animation
         */
        reverseFirstArch.setDuration(2000);
        reverseSecondArch.setDuration(2000);
        reverseThirdArch.setDuration(2000);
        /**
         * On end listeners. This listeners are used in order to allow graphic sync between circles.
         * When a circle animation end, the onEndListener update
         */
        firstarc.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(final Animator animation) {
                mCounter = 0;
                animationstate = 0;
                countdownview.start(breakTimeTimer);
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
                animationStateSecondArch = 0;
                thirdarc.pause();
               /* mArcProgressStackView.getModels().get(MODEL_COUNT-2).setProgress(0);
                mArcProgressStackView.getModels().get(MODEL_COUNT-3).setProgress(0);*/
               reverseFirstArch.reverse();
               reverseSecondArch.reverse();
            }
            @Override
            public void onAnimationRepeat(final Animator animation) {

            }
        });

        thirdarc.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(final Animator animation) {
                reverseThirdArch.reverse();
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
                animationStateSecondArch = animation.getCurrentPlayTime();
                mArcProgressStackView.postInvalidate();
            }
        });

        thirdarc.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                //mArcProgressStackView.getModels().get(MODEL_COUNT - 1).setProgress(session);
                mArcProgressStackView.getModels().get(MODEL_COUNT-1)  //Math.min(mCounter, MODEL_COUNT - 2)
                        .setProgress((Float) animation.getAnimatedValue());
                thirdArchAnimationState = animation.getCurrentPlayTime();
                mArcProgressStackView.postInvalidate();
            }
        });
        /**
         * reverse animation on finish
         */
        reverseFirstArch.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                //mArcProgressStackView.getModels().get(MODEL_COUNT - 1).setProgress(session);
                mArcProgressStackView.getModels().get(MODEL_COUNT-3)
                        .setProgress((Float) animation.getAnimatedValue());
                thirdArchAnimationState = animation.getCurrentPlayTime();
                mArcProgressStackView.postInvalidate();
            }
        });
        reverseSecondArch.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                //mArcProgressStackView.getModels().get(MODEL_COUNT - 1).setProgress(session);
                mArcProgressStackView.getModels().get(MODEL_COUNT-2)  //Math.min(mCounter, MODEL_COUNT - 2)
                        .setProgress((Float) animation.getAnimatedValue());
                thirdArchAnimationState = animation.getCurrentPlayTime();
                mArcProgressStackView.postInvalidate();
            }
        });
        reverseThirdArch.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                //mArcProgressStackView.getModels().get(MODEL_COUNT - 1).setProgress(session);
                mArcProgressStackView.getModels().get(MODEL_COUNT-2)  //Math.min(mCounter, MODEL_COUNT - 2)
                        .setProgress((Float) animation.getAnimatedValue());
                thirdArchAnimationState = animation.getCurrentPlayTime();
                mArcProgressStackView.postInvalidate();
            }
        });

        //revers animation onEnd listener

        reverseSecondArch.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(final Animator animation) {
                startButton.setClickable(true);
            }
            @Override
            public void onAnimationRepeat(final Animator animation) {

            }
        });

        /**
         * Button listeners
         */
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            @TargetApi(Build.VERSION_CODES.KITKAT)
            public void onClick(View v) {
                if(animationstate != 0) {
                    firstarc.resume();
                    thirdarc.resume();
                    countdownview.restart();
                    startButton.setClickable(false);
                    return;
                }
                if(animationStateSecondArch != 0){
                    secondarc.resume();
                    thirdarc.resume();
                    countdownview.restart();
                    startButton.setClickable(false);
                    return;
                }
                if(thirdArchAnimationState != 0 ){
                    firstarc.start();
                    thirdarc.resume();
                    startButton.setClickable(false);
                    return;
                }
                firstarc.start();
                thirdarc.start();
                countdownview.start(studyTimeTimer);
                //cambiare il colore del bottone
                startButton.setClickable(false);
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                firstarc.pause();
                secondarc.pause();
                thirdarc.pause();
                countdownview.pause();
                startButton.setClickable(true);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        //spinners onchange listeners


    }

    private void initializeArcModel(long numberofsessions , long studytime, long breaktime){
        firstarc.setDuration(studytime);
        secondarc.setDuration(breaktime);
        thirdarc.setDuration((studytime+breaktime)*numberofsessions);
    }

    private void initializeTimerView(ValueAnimator session, ValueAnimator study, ValueAnimator breakt, ArcProgressStackView stackView){
        firstarc.cancel();
        secondarc.cancel();
        thirdarc.cancel();
        animationStateSecondArch = 0;
        thirdArchAnimationState = 0;
        animationstate = 0;
        stackView.getModels().get(MODEL_COUNT-2)  //Math.min(mCounter, MODEL_COUNT - 2)
                .setProgress(0);
        stackView.getModels().get(MODEL_COUNT-1)  //Math.min(mCounter, MODEL_COUNT - 2)
                .setProgress(0);
        stackView.getModels().get(MODEL_COUNT-3)  //Math.min(mCounter, MODEL_COUNT - 2)
                .setProgress(0);
        startButton.setClickable(true);
        countdownview.stop();
        countdownview.updateShow(studyTimeTimer);
    }


}
