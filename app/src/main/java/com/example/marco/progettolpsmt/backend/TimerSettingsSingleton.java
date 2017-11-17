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
        ///timerPreferences = // c.getSharedPreferences((R.string.studysessiondefault),Context.MODE_PRIVATE);
        ////SharedPreferences.Editor editor = timerPreferences.edit();
    }

    public void setNumberOfStudySessions(int studySessions, Context c){
        timerPreferences = PreferenceManager
                .getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = timerPreferences.edit();
        editor.putString("studysessions", String.valueOf(studySessions));
        editor.commit();
        /* timerPreferences = c.getSharedPreferences(getString(R.string.studysession),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = timerPreferences.edit();
        editor.putInt(getString(R.string.studysession),studySessions);
        editor.commit();*/
    }

    public void setDurationOfStudySessions(int studyduration, Context c){
         timerPreferences = PreferenceManager
                .getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = timerPreferences.edit();
        editor.putString("studyduration", String.valueOf(studyduration));
        editor.apply();

        /*timerPreferences = c.getSharedPreferences(getString(R.string.studyduration),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = timerPreferences.edit();
        editor.putInt(getString(R.string.studyduration),studyduration);
        editor.commit();*/
    }

    public void setDurationOfBreakSessions(int breakduration, Context c){
        /*timerPreferences = c.getSharedPreferences(getString(R.string.breakduration),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = timerPreferences.edit();
        editor.putInt(getString(R.string.breakduration),breakduration);
        editor.commit();*/
    }

    public long getNumberOfStudySessions(Context c ){
        return Long.parseLong(c.getResources().getString(R.string.studysession));
    }

    public long getNumberOfBreakDuration(Context c ){
        return Long.parseLong(c.getResources().getString(R.string.breakduration));
    }

    public long getNumberOfStudyDuration(Context c ){
        return Long.parseLong(c.getResources().getString(R.string.studyduration));
    }

}
