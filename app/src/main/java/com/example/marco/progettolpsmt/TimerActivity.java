package com.example.marco.progettolpsmt;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

//import com.example.marco.progettolpsmt.backend.Log;
import com.example.marco.progettolpsmt.backend.Argument;
import com.example.marco.progettolpsmt.backend.Course;
import com.example.marco.progettolpsmt.backend.StudyLog;
import com.example.marco.progettolpsmt.backend.TimerSettingsSingleton;
import com.example.marco.progettolpsmt.backend.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.iwgang.countdownview.CountdownView;
import devlight.io.library.ArcProgressStackView;
import static devlight.io.library.ArcProgressStackView.Model;


public class
TimerActivity extends AppCompatActivity {

    private int mCounter = 0;
    public final static int MODEL_COUNT = 3;
    private ArcProgressStackView mArcProgressStackView;
    private Button startButton;
    private Button pause;
    private Button settings;
    private CountdownView countdownView;
    private long animationStateThirdArch =0, animationStateSecondArch =0, thirdArchAnimationState =0;
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
    final ValueAnimator firstArch = ValueAnimator.ofFloat(100);
    final ValueAnimator secondArch = ValueAnimator.ofFloat(100);
    final ValueAnimator thirdArch = ValueAnimator.ofFloat(100);
    //on finish animation declaration
    final ValueAnimator reverseFirstArch = ValueAnimator.ofFloat(100);
    final ValueAnimator reverseSecondArch = ValueAnimator.ofFloat(100);
    final ValueAnimator reverseThirdArch = ValueAnimator.ofFloat(100);
    //backends classes
    private Course course;
    //alert dialog
    private AlertDialog confirmChangeCourseArgumentDialog = null;
    private AlertDialog stopAlerDialog = null;
    private AlertDialog backButtonAlertDialog = null;
    //button pause binary flag
    private int pauseBtnBinaryFlag = 1 ;
    //timestamps
    private Date initialTimeStamp;
    private Date timeStampFromInterruption;
    private com.example.marco.progettolpsmt.backend.Log studyLog;
    //Notification
    TimerNotification timerNotification;
    //courses of the user
    private List<Course> userCourses;
    private List<Argument> userArguments;
    private Argument studyingArgument;
    //boundle elements
    private String boundleArgument = null;
    @Override
    protected void onCreate(final Bundle extras) {
        super.onCreate(extras);
        setContentView(R.layout.activity_timer);

        //Dialog used in order to take data from user, that we need in order to initializate timer
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.timerinitializationpopup);

        //
        confirmChangeCourseArgumentDialog = new AlertDialog.Builder(this)
                .setTitle("Change Course or Argument")
                .setMessage("Are you sure that you want to change argument or course?\n You will lose current progress..")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }})
                .setNegativeButton(android.R.string.no, null).create();;


        Button confirmTimerTemporaryChanges = dialog.findViewById(R.id.button);
        Button cancelTimerTemporaryChanges  = dialog.findViewById(R.id.cancelbutton);
        //studyLog
        studyLog = new StudyLog();
        //textbox of the dialog
        final EditText sessions = dialog.findViewById(R.id.editText2);
        final EditText studyTime = dialog.findViewById(R.id.editText3);
        final EditText breakTime = dialog.findViewById(R.id.editText4);
        //spinners
        courseSpinner = findViewById(R.id.courseSpinner);
        argumentSpinner = findViewById(R.id.argumentSpinner);
        //buttons
        startButton = (Button) findViewById(R.id.startbtn);
        pause =(Button) findViewById(R.id.pausebtn);
        pause.setEnabled(false);
        settings = (Button) findViewById(R.id.settings);
        //testual timer
        countdownView = findViewById(R.id.countdownview);
        //arch model
        mArcProgressStackView = (ArcProgressStackView) findViewById(R.id.apsv_presentation);
        //model array
        models = new ArrayList<>();
        /**
         * setting courses with extras
         */

        try {
            /**
             * SETTING UP course and argument spinner here
             *
             */
            initiCourseSpinner();
            if (extras != null) {
                preSelectItem(extras.getString("courseID"));
                //CourseManagerSingleton.getInstance().getCourseById(extras.getInt("courseId")).getName()
            }
        }
        catch (NullPointerException e) {
                Toast.makeText(TimerActivity.this, "Impossible Getting Courses/Arguments", Toast.LENGTH_LONG).show();
        }

        //adding listener to buttons
        confirmTimerTemporaryChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( !((sessions.getText()).toString().equals("")) && !((studyTime.getText()).toString().equals("")) && !((breakTime.getText()).toString().equals(""))) {
                    //go on here and dismiss dialog
                    nSession = Integer.parseInt(sessions.getText().toString());
                    studyTimeTimer = Long.parseLong(studyTime.getText().toString())*60000;
                    breakTimeTimer = Long.parseLong(breakTime.getText().toString())*60000;
                    isDialogSetted = true;
                    countdownView.updateShow(studyTimeTimer);
                    initializeTimerView(mArcProgressStackView);
                    initializeArcModel(nSession, studyTimeTimer, breakTimeTimer);
                    dialog.dismiss();
                }
            }
        });

        cancelTimerTemporaryChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    dialog.dismiss();
            }
        });

        /**
         * after course being selected, argument are setted
         */

        courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try{
                    String selectedCourse = courseSpinner.getSelectedItem().toString();
                    List<Argument> argList = getArgumentsFromCourse(selectedCourse);
                    initiArgumentSpinner(argList);
                    if(boundleArgument != null){
                        preSelectItem(boundleArgument);
                    }
                    /**
                     * after set boundleArgument as preSelectedItem, a null value is assigned to this wariable, in order to
                     * avoid problems if user change courses on timer(there was a problem when user changed selected course, i due to non-null
                     * value of this variable, the selected courses didn't change
                     */
                    boundleArgument = null;
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(TimerActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        argumentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
              //  try {
                    String selectedArgument = argumentSpinner.getSelectedItem().toString();
                    setStudyArgument(selectedArgument);
               // }catch(Exception e){
              //      Toast.makeText(TimerActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
              //  }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



         /*
           if user doesn't set values, the system will use default setted values
         */
         if(isDialogSetted == false) {
            readFromSharedPreferences();
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

        this.initializeArcModel(nSession, studyTimeTimer, breakTimeTimer);
        firstArch.setInterpolator(new LinearInterpolator());
        secondArch.setInterpolator(new LinearInterpolator());
        thirdArch.setInterpolator(new LinearInterpolator());

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
        firstArch.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(final Animator animation) {
                mCounter = 0;
                animationStateThirdArch = 0;
                countdownView.start(breakTimeTimer);
                secondArch.start();
            }
            @Override
            public void onAnimationRepeat(final Animator animation) {
                mCounter++;
            }
        });

        secondArch.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(final Animator animation) {
                animationStateSecondArch = 0;
                thirdArch.pause();
               reverseFirstArch.reverse();
               reverseSecondArch.reverse();
            }
            @Override
            public void onAnimationRepeat(final Animator animation) {

            }
        });

        thirdArch.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(final Animator animation) {
                reverseThirdArch.reverse();
            }
            @Override
            public void onAnimationRepeat(final Animator animation) {

            }
        });

        final NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Sets an ID for the notification, so it can be updated
        final int notifyID = 1;
        timerNotification = new TimerNotification();


        /**
         * Animator update listener. This methods are used to update graphics animation of
         * the ArchModel. Every circle own a method that update graphics valued differently as the other
         * due to different values setted by user before the animation starts.
         */


        firstArch.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                mArcProgressStackView.getModels().get(MODEL_COUNT-3)  //Math.min(mCounter, MODEL_COUNT - 2)
                        .setProgress((Float) animation.getAnimatedValue());
                animationStateThirdArch = animation.getCurrentPlayTime();
                mArcProgressStackView.postInvalidate();

            }
        });

        secondArch.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                mArcProgressStackView.getModels().get(MODEL_COUNT-2)  //Math.min(mCounter, MODEL_COUNT - 2)
                        .setProgress((Float) animation.getAnimatedValue());
                animationStateSecondArch = animation.getCurrentPlayTime();
                mArcProgressStackView.postInvalidate();
            }
        });

        thirdArch.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
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
                mArcProgressStackView.postInvalidate();
            }
        });
        reverseSecondArch.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                //mArcProgressStackView.getModels().get(MODEL_COUNT - 1).setProgress(session);
                mArcProgressStackView.getModels().get(MODEL_COUNT-2)  //Math.min(mCounter, MODEL_COUNT - 2)
                        .setProgress((Float) animation.getAnimatedValue());
                mArcProgressStackView.postInvalidate();
            }
        });
        reverseThirdArch.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                //mArcProgressStackView.getModels().get(MODEL_COUNT - 1).setProgress(session);
                mArcProgressStackView.getModels().get(MODEL_COUNT-1)  //Math.min(mCounter, MODEL_COUNT - 2)
                        .setProgress((Float) animation.getAnimatedValue());
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
                /**
                 * in order to avoid sync pause/stop button,
                 * here this button will be forced to Pause status
                 */
                pauseBtnBinaryFlag = 1;
                pause.setText(R.string.timerPauseButton);
                timerNotification.notify(getBaseContext(),"Studying",1);
                final NotificationCompat.Builder mNotifyBuilder = timerNotification.getBuilder();
                if(animationStateThirdArch != 0) {
                    firstArch.resume();
                    thirdArch.resume();
                    countdownView.restart();
                    startButton.setEnabled(false);
                    return;
                }
                if(animationStateSecondArch != 0){
                    secondArch.resume();
                    thirdArch.resume();
                    countdownView.restart();
                    startButton.setEnabled(false);
                    return;
                }
                if(thirdArchAnimationState != 0 ){
                    firstArch.start();
                    thirdArch.resume();
                    countdownView.restart();
                    startButton.setEnabled(false);
                    return;
                }
                countdownView.start(studyTimeTimer);
                firstArch.start();
                thirdArch.start();
                startButton.setEnabled(false);
                settings.setEnabled(false);
                studyLog.setStart(new Date());
                courseSpinner.setEnabled(false);
                argumentSpinner.setEnabled(false);
                pause.setEnabled(true);
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                if(pauseBtnBinaryFlag != 0) {
                    pause.setText(R.string.timerStopButton);
                    timerNotification.notify(getBaseContext(), "Timer Paused", 1);
                    final NotificationCompat.Builder mNotifyBuilder = timerNotification.getBuilder();
                    firstArch.pause();
                    secondArch.pause();
                    thirdArch.pause();
                    countdownView.pause();
                    startButton.setEnabled(true);

                    pauseBtnBinaryFlag = 0;
                }
                else if(pauseBtnBinaryFlag ==0){
                    pause.setText(R.string.timerPauseButton);
                    initializeTimerView(mArcProgressStackView);
                    courseSpinner.setEnabled(true);
                    argumentSpinner.setEnabled(true);
                    startButton.setEnabled(true);
                    pause.setEnabled(false);
                    pauseBtnBinaryFlag = 1;
                }

                try {
                    studyLog.setEnd(new Date());
                    studyingArgument.addLog(studyLog);
                }catch(Exception e){
                    Toast.makeText(TimerActivity.this, "Impossible adding Log", Toast.LENGTH_LONG).show();
                }

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
        breaktime = breakTimeTimer;
        firstArch.setDuration(studytime);
        secondArch.setDuration(breaktime);
        thirdArch.setDuration((studytime+breaktime)*numberofsessions);
    }

    /**
     * Method useed in order to init all the timer values
     * @param stackView
     * @return void
     */
    private void initializeTimerView(ArcProgressStackView stackView){
        readFromSharedPreferences();
        firstArch.cancel();
        secondArch.cancel();
        thirdArch.cancel();
        animationStateSecondArch = 0;
        thirdArchAnimationState = 0;
        animationStateThirdArch = 0;
        stackView.getModels().get(MODEL_COUNT-2)  //Math.min(mCounter, MODEL_COUNT - 2)
                .setProgress(0);
        stackView.getModels().get(MODEL_COUNT-1)  //Math.min(mCounter, MODEL_COUNT - 2)
                .setProgress(0);
        stackView.getModels().get(MODEL_COUNT-3)  //Math.min(mCounter, MODEL_COUNT - 2)
                .setProgress(0);
        startButton.setClickable(true);
        countdownView.stop();
        countdownView.updateShow(studyTimeTimer);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerNotification.cancel(this);
        try {
            if(studyLog.getStart() != null) {
                studyLog.setEnd(new Date());
                studyingArgument.addLog(studyLog);
            }
        }catch(Exception e){
            Toast.makeText(TimerActivity.this, "Impossible adding Log", Toast.LENGTH_LONG).show();
        }
    }

    protected void onStop() {
        super.onStop();
        try {
            if(studyLog.getStart() != null) {
                studyLog.setEnd(new Date());
                studyingArgument.addLog(studyLog);
            }
        }catch(Exception e){
            Toast.makeText(TimerActivity.this, "Impossible adding Log", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * catching button back pressed event in order to save study progress logs and in order to offer to users a
     * way for recovering a possible mistake.
     * @param
     * @retun is void
     */

    public void onBackPressed() {
        backButtonAlertDialog = new AlertDialog.Builder(this)
                .setTitle("Exiting Timer")
                .setMessage("Do you really want to leave this session? Don't worry, all the progress will be saved!")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        TimerActivity.super.onBackPressed();
                        try {
                            if(studyLog.getStart() != null){
                                studyLog.setEnd(new Date());
                                studyingArgument.addLog(studyLog);
                            }
                        }catch(Exception e){
                            Toast.makeText(TimerActivity.this, "Impossible adding Log", Toast.LENGTH_LONG).show();
                        }
                    }})
                .setNegativeButton(android.R.string.no, null).create();;
        backButtonAlertDialog.show();

    }

    /**
     * method used in order to get defaults timer params from shared preferences.
     * @param
     * @return void
     */
    private void readFromSharedPreferences(){
        nSession = TimerSettingsSingleton.getInstance().getNumberOfStudySessions(this);
        studyTimeTimer = TimerSettingsSingleton.getInstance().getNumberOfStudyDuration(this);
        breakTimeTimer = TimerSettingsSingleton.getInstance().getNumberOfBreakDuration(this);
        countdownView.updateShow(studyTimeTimer);
    }


    /**
     * method that initialize course spinner
     */
    private void initiCourseSpinner(){
        //saving users courses
        userCourses = new ArrayList<Course>();
        userCourses = User.getInstance().getCourses();

        //setting course spinner
        ArrayList<String> coursesNames = new ArrayList<String>();
        for(int i = 0; i < User.getInstance().getCourses().size(); i++ ){
            coursesNames.add(User.getInstance().getCourses().get(i).getName());
        }
        ArrayAdapter<String> courseAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,coursesNames);
        courseSpinner.setAdapter(courseAdapter);
    }

    /**
     * method that initialize argument spinner
     */
    private void initiArgumentSpinner(List<Argument> arguments){
        //setting course spinner
        ArrayList<String> argumentsName = new ArrayList<String>();
        if(arguments.size() != 0 ){
            userArguments = arguments;
            for(int i = 0; i < arguments.size(); i++ ){
                argumentsName.add(arguments.get(i).getName());
            }
            ArrayAdapter<String> courseAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,argumentsName);
            argumentSpinner.setAdapter(courseAdapter);
        }
        else{
            argumentsName.add("");
            ArrayAdapter<String> courseAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,argumentsName);
            argumentSpinner.setAdapter(courseAdapter);
        }

    }
    /**
     * method that return all the arguments from a given course name
     * @param courseName
     * @return
     */
    private List<Argument> getArgumentsFromCourse(String courseName){
            for(int i = 0 ; i < userCourses.size(); i++){
                if(userCourses.get(i).getName().equals(courseName)){
                    return userCourses.get(i).getArguments();
                }
            }
            //if no arguments, return null
            return null;
    }

    /**
     *getting elem position and set pre selected item
     */

    public void preSelectItem(String name) {
        boolean doWeStopTheLoop = false;
        for (int i = 0; i < this.courseSpinner.getAdapter().getCount() && !doWeStopTheLoop; i++) {
            String tmp = (String) courseSpinner.getItemAtPosition(i);
            if (tmp.equals(name)) {
                courseSpinner.setSelection(i);
                doWeStopTheLoop = true; //you can use break; too
            }
        }
    }

    /**
     * method used to get studying argument
     */

    private void setStudyArgument(String studyingArgument){
        this.studyingArgument = User.getInstance().getArgumentByName(studyingArgument);
    }


}


