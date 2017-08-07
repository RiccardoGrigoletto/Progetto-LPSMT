package com.example.riccardogrigoletto.progetto_lpsmt.Classes;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by riccardogrigoletto on 07/08/2017.
 *
 * Manage courses
 */

public class Course {
    private final String ProgressException = "Course Class: Progress need to be between 0 and 100";
    private final String CFUException = "Course Class: CFU need to be between 1 and 15";
    private String name;
    private int CFU; /*from 1 to 15*/
    private int progress; /*from 0 to 100 */
    private ArrayList<Argument> arguments;
    private Date exam;
    private ArrayList<WeeklySessions> studySessions;

    private Course() {
        this.arguments = new ArrayList<>();
        this.studySessions = new ArrayList<>();
        this.progress = 0;
    }
    public Course(String name, int CFU, ArrayList<Argument> arguments, Date exam, ArrayList<WeeklySessions> studySessions) {
        this();
        this.name = name;
        this.CFU = CFU;
        this.arguments = arguments;
        this.exam = exam;
        this.studySessions = studySessions;
    }

    public Course(String name, int CFU, ArrayList<Argument> arguments, Date exam) {
        this();
        this.name = name;
        this.CFU = CFU;
        this.arguments = arguments;
        this.exam = exam;
    }

    public Course(String name, int CFU, Date exam) {
        this();
        this.name = name;
        this.CFU = CFU;
        this.exam = exam;
    }

    public Course(String name, int CFU) {
        this();
        this.name = name;
        this.CFU = CFU;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCFU() {
        return CFU;
    }

    public void setCFU(int CFU) throws Exception {
        if (CFU>=1 && CFU<=15) {
            this.CFU = CFU;
        }
        else {
            throw new Exception(CFUException);
        }
    }

    public int getProgress() {
        return progress;
    }

    public ArrayList<Argument> getArguments() {
        return arguments;
    }

    public void setArguments(ArrayList<Argument> arguments) {
        this.arguments = arguments;
    }

    public Date getExam() {
        return exam;
    }

    public void setExam(Date exam) {
        this.exam = exam;
    }

    public ArrayList<WeeklySessions> getStudySessions() {
        return studySessions;
    }

    public void setStudySessions(ArrayList<WeeklySessions> studySessions) {
        this.studySessions = studySessions;
    }

}


