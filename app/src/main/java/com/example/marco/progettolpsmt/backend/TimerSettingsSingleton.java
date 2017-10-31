package com.example.marco.progettolpsmt.backend;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.marco.progettolpsmt.R;

/**
 * Created by Marco on 31/10/2017.
 */

public class TimerSettingsSingleton extends Application {
    private static final TimerSettingsSingleton timersettings = new TimerSettingsSingleton();
    private SharedPreferences timerPreferences;
    public static TimerSettingsSingleton getInstance() {
        return timersettings;
    }

    private TimerSettingsSingleton() {

    }

    public void setNumberOfStudySessions(int studySessions){
        SharedPreferences.Editor editor = timerPreferences.edit();
        editor.putInt(getString(R.string.studysessiondefault),studySessions);
        editor.commit();
    }

    public void setDurationOfStudySessions(int studyduration){
        SharedPreferences.Editor editor = timerPreferences.edit();
        editor.putInt(getString(R.string.studyduration),studyduration);
        editor.commit();
    }

    public void setDurationOfBreakSessions(int breakduration){
        SharedPreferences.Editor editor = timerPreferences.edit();
        editor.putInt(getString(R.string.breakduration),breakduration);
        editor.commit();
    }

    public int getNumberOfStudySessions(){
        return Integer.parseInt(getResources().getString(R.string.studysessiondefault));
    }

    public int getNumberOfBreakDuration(){
        return Integer.parseInt(getResources().getString(R.string.breakduration));
    }

    public int getNumberOfStudyDuration(){
        return Integer.parseInt(getResources().getString(R.string.studyduration));
    }


}
