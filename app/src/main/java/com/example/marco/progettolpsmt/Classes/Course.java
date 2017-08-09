package com.example.marco.progettolpsmt.Classes;

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
    private int studyHours;
    private int progress; /*from 0 to 100 */
    private ArrayList<Argument> arguments;
    private Date exam;
    private ArrayList<WeeklySessions> studySessions;

    /*****CONSTRUCTORS*******/
    public Course(String name, int CFU, ArrayList<Argument> arguments, Date exam, ArrayList<WeeklySessions> studySessions, int studyHours) throws Exception {
        this(name, CFU, arguments, exam, studySessions);
        this.studyHours = studyHours;
    }
    public Course(String name, int CFU, ArrayList<Argument> arguments, Date exam, ArrayList<WeeklySessions> studySessions) throws Exception {
        this(name, CFU, arguments, exam);
        this.studySessions = studySessions;
    }

    public Course(String name, int CFU, ArrayList<Argument> arguments, Date exam) throws Exception {
        this(name, CFU, exam);
        this.arguments = arguments;
    }

    public Course(String name, int CFU, Date exam) throws Exception {
        this(name,CFU);
        this.exam = exam;

    }

    public Course(String name, int CFU) throws Exception {
        this();
        setCFU(CFU);
        this.name = name;

    }

    private Course() {
        this.arguments = new ArrayList<>();
        if (studyHours==0) {
           this.studyHours = Global.calculateHours(this.getCFU());
        }
        arguments.add(new Argument("TODO", studyHours));
        this.studySessions = new ArrayList<>();
        this.progress = 0;
    }


    public int getStudyHours() {
        return studyHours;
    }

    public void setStudyHours(int studyHours) {
        this.studyHours = studyHours;
    }

    public void setProgress(int progress) {
        this.progress = progress;
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


