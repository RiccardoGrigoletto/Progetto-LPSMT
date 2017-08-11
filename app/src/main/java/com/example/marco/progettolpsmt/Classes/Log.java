package com.example.marco.progettolpsmt.Classes;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by riccardogrigoletto on 10/08/2017.
 */

class Log extends ArrayList<Log> {
    private Date start;
    private Date end;
    private String notes;

    public Log(Date start, Date end) {
        this.start = start;
        this.end = end;
    }

    public Log(Date start, Date end, String notes) {
        this(start,end);
        this.notes = notes;
    }
    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
