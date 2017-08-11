package com.example.marco.progettolpsmt.Classes;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by riccardogrigoletto on 07/08/2017.
 */

public class Argument {
    private String name;
    private boolean done;
    private int expectedTime;
    private ArrayList<Log> logs;

    private Argument() {
        this.logs = new ArrayList<>();
    }
    public Argument(String name) {
        this();
        this.name = name;

    }
    public Argument(String name, int expectedTime) {
        this();
        this.name = name;
        this.expectedTime = expectedTime;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getExpectedTime() {
        return expectedTime;
    }

    public void setExpectedTime(int expectedTime) {
        this.expectedTime = expectedTime;
    }
    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
    public ArrayList<Log> getLog() {
        return logs;
    }

    public void setLog(ArrayList<Log> logs) {
        this.logs = logs;
    }

    public void addLog(Log log) {
        this.logs.add(log);
    }

    /**
     * this function looks on the logs variable and calculate the amount of time spent studying this
     * argument
     * @return time spent for the argument (minutes)
     */
    public int getSpentTime() {
        long total = 0;
        for (Log l: logs) {
            long end = l.getEnd().getTime()/1000;
            long start = l.getStart().getTime()/1000;
            total += end-start;
        }
        return (int) (total/60);
    }




    public static void main(String[] args) throws Exception {

        System.out.println("test getSpentTime");

        Argument a = new Argument("culo",25);
        System.out.println(a.getSpentTime());
        Log log1 = new Log(new Date(0), new Date(1*1000*60*60*10));
        a.addLog(log1);

        System.out.println(a.getSpentTime());


        System.out.println("test Course");

        Course c = new Course("LPSMT",6);

        c.addArgument(a);



        System.out.println(c.getProgress());



    }
}
