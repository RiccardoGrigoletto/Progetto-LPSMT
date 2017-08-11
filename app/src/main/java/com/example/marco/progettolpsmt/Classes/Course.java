package com.example.marco.progettolpsmt.Classes;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by riccardogrigoletto on 07/08/2017.
 *
 * Manage courses
 */

public class Course {
    private final String CreditsException = "Course Class: credits need to be between 3 and 15";
    private String name;
    private Integer credits; /*from 3 to 15*/
    private ArrayList<Argument> arguments;
    private ArrayList<Date> exams;
    private boolean attended;

    /*****CONSTRUCTORS*******/

    public Course(String name, int Credits, ArrayList<Argument> arguments, ArrayList<Date> exams) throws Exception {
        this(name, Credits, exams);
        this.arguments = arguments;
    }

    public Course(String name, int Credits, ArrayList<Date> exams) throws Exception {
        this(name, Credits);
        this.exams = exams;

    }

    public Course(String name, int Credits) throws Exception {
        this();
        setCredits(Credits);
        this.name = name;

    }

    private Course() {
        this.arguments = new ArrayList<>();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) throws Exception {
        if (credits >=3 && credits <=15) {
            this.credits = credits;
        }
        else {
            throw new Exception(CreditsException);
        }
    }

    public ArrayList<Argument> getArguments() {
        return arguments;
    }

    public void setArguments(ArrayList<Argument> arguments) {
        this.arguments = arguments;
    }

    public ArrayList<Date> getExam() {
        return exams;
    }

    public void setExam(ArrayList<Date> exams) {
        this.exams = exams;
    }

    public boolean isAttended() {
        return attended;
    }

    public void setAttended(boolean attended) {
        this.attended = attended;
    }

    /**
     * This function calculate the amount of time spent for each argument of the course and returns
     * the ratio with the expected time required.
     * Note: the "previously done" arguments contain fake log with the required amount of time
     * @return the progress
     */
    public int getProgress() {
        int studied = 0;
        for (Argument arg: arguments) {
            studied+=arg.getSpentTime();
        }
        int expected = Global.calculateHours(credits,attended);
        return studied/expected;
    }


    public void addArgument(Argument arg) {
        arguments.add(arg);
    }
}