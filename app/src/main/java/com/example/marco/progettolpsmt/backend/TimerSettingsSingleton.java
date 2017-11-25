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
    }

    public void setDurationOfStudySessions(int studyduration, Context c){
        timerPreferences = PreferenceManager
                .getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = timerPreferences.edit();
        editor.putInt("studyduration", studyduration);
        editor.commit();
    }

    public void setDurationOfBreakSessions(int breakduration, Context c){
        timerPreferences = PreferenceManager
                .getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = timerPreferences.edit();
        editor.putInt("breakduration", breakduration);
        editor.commit();
    }

    public long getNumberOfStudySessions(Context c ){
        timerPreferences = PreferenceManager
                .getDefaultSharedPreferences(c);

        return (timerPreferences.getInt("studysession",99999));
    }

    public long getNumberOfBreakDuration(Context c ){
        timerPreferences = PreferenceManager
                .getDefaultSharedPreferences(c);
        return (timerPreferences.getInt("breakduration",99999));
    }

    public long getNumberOfStudyDuration(Context c ){
        timerPreferences = PreferenceManager
                .getDefaultSharedPreferences(c);
        return (timerPreferences.getInt("studyduration",99999));
    }

}
