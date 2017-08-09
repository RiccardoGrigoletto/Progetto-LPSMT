package com.example.marco.progettolpsmt.Classes;

import java.util.ArrayList;

/**
 * Created by riccardogrigoletto on 07/08/2017.
 */

public class Argument {
    private String mane;
    private ArrayList<Argument> arguments;
    private int hours;
    private int progress;

    private Argument() {
        this.progress = 0;
        this.arguments = new ArrayList<>();
    }
    public Argument(String mane) {
        this();
        this.mane = mane;
    }
    public Argument(String mane, int hours) {
        this();
        this.mane = mane;
        this.hours = hours;
    }

    public String getMane() {

        return mane;
    }

    public void setMane(String mane) {
        this.mane = mane;
    }

    public ArrayList<Argument> getArguments() {
        return arguments;
    }

    public void setArguments(ArrayList<Argument> arguments) {
        this.arguments = arguments;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
