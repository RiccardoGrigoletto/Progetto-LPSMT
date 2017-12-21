package com.example.marco.progettolpsmt.backend;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.marco.progettolpsmt.R;

import java.io.Console;

/**
 * Created by Marco on 31/10/2017.
 */

public class TimerSettingsSingleton extends Application {
    private static  TimerSettingsSingleton timersettings = null;
    private SharedPreferences timerPreferences;

    public static TimerSettingsSingleton getInstance() {
        if(timersettings == null)
            timersettings = new TimerSettingsSingleton();
        return  timersettings;
    }

    private TimerSettingsSingleton() {
    }

    public void setNumberOfStudySessions(int studySessions, Context c){
        timerPreferences = PreferenceManager
                .getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = timerPreferences.edit();
        editor.putInt("studysession", studySessions);
        editor.commit();
        User.getInstance().getSettings().setSessionAmount(studySessions);
        User.getInstance().updateOnFirestore();

    }

    public void setDurationOfStudySessions(int studyduration, Context c){
        timerPreferences = PreferenceManager
                .getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = timerPreferences.edit();
        editor.putInt("studyduration", studyduration);
        editor.commit();
        User.getInstance().getSettings().setStudyDuration(studyduration);
        User.getInstance().updateOnFirestore();
    }

    public void setDurationOfBreakSessions(int breakduration, Context c){
        timerPreferences = PreferenceManager
                .getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = timerPreferences.edit();
        editor.putInt("breakduration", breakduration);
        editor.commit();
        User.getInstance().getSettings().setPauseDuration(breakduration);
        User.getInstance().updateOnFirestore();
    }

    public long getNumberOfStudySessions(Context c ){
      long returnStudySessionAmout;
        if(User.getInstance().getSettings().getSessionAmount() != null){
            returnStudySessionAmout = User.getInstance().getSettings().getSessionAmount();
        }
        else {
            timerPreferences = PreferenceManager
                    .getDefaultSharedPreferences(c);
            returnStudySessionAmout = timerPreferences.getInt("studysession",99999);
            //if not sette on fireetore, it will be setted here
            setNumberOfStudySessions((int)returnStudySessionAmout,c);
        }
        return returnStudySessionAmout;
    }

    public long getNumberOfBreakDuration(Context c ){
        long breakDurationReturnValue;
        if(User.getInstance().getSettings().getRelaxDuration() != null){
             breakDurationReturnValue = User.getInstance().getSettings().getPauseDuration();
        }else{
            timerPreferences = PreferenceManager
                    .getDefaultSharedPreferences(c);
            breakDurationReturnValue = timerPreferences.getInt("breakduration",99999);
            setDurationOfBreakSessions((int)breakDurationReturnValue,c);
        }

        return breakDurationReturnValue;
    }

    public long getNumberOfStudyDuration(Context c ){
        long studyDurationRetunrValue;
        if(User.getInstance().getSettings().getStudyDuration() != null){
            studyDurationRetunrValue = User.getInstance().getSettings().getStudyDuration();
        }else{
            timerPreferences = PreferenceManager
                    .getDefaultSharedPreferences(c);
            studyDurationRetunrValue = timerPreferences.getInt("studyduration",99999);
            setDurationOfStudySessions((int)studyDurationRetunrValue,c);
        }
        return studyDurationRetunrValue;
    }

}
