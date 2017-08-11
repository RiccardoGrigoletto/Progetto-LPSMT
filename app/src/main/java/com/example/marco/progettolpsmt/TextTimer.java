package com.example.marco.progettolpsmt;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.os.CountDownTimer;

/**
 * Created by Marco on 11/08/2017.
 */

public class TextTimer {
    private CountDownTimer timer;
    private CircularProgressBar ctimer;
    private Animator animator;
    private long reminingtime;
    private long interval;
    public TextTimer(long time , long intervar, CircularProgressBar ctimer){
        timer = initTimer(time,intervar);
        this.ctimer = ctimer;
        setTimer(time);
        this.interval = intervar;
    }

    public CountDownTimer initTimer(long time, long intervar){
        CountDownTimer temp = new CountDownTimer(time , intervar ) {
            @Override
            public void onTick(long l) {
                reminingtime = l;
                setTimer(l);
            }

            @Override
            public void onFinish() {

            }
        };

        return temp;
    }

    public void setTimer(long l){
        long remainedSecs=((l)/1000);
        long seconds = remainedSecs % 60;
        long remainedMinutes = remainedSecs/60;

        if(seconds < 10 && remainedMinutes < 10){
            ctimer.setTitle("0"+remainedMinutes
                    +":0"+(seconds));
        }
        else if(seconds < 10){
            ctimer.setTitle(remainedMinutes
                    +":0"+(seconds));
        }
        else if(remainedMinutes < 10){
            ctimer.setTitle("0"+remainedMinutes
                    +":"+(seconds));
        }
        else{
            ctimer.setTitle(remainedMinutes
                    +":"+(seconds));
        }
    }

    public void start(){
        timer.start();

    }

    public void pause(){
        timer.cancel();
        setTimer(reminingtime);
    }

    public void resume(){
        timer = initTimer(reminingtime,interval);
        timer.start();
    }

    public void stop(){
        timer.cancel();
        ctimer.setTitle("00:00");
    }

}
