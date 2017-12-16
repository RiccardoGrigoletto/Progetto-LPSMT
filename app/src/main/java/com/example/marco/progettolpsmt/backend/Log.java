package com.example.marco.progettolpsmt.backend;

import java.util.Date;

/**
 * Log class to represent an event.
 * <br>
 * An event is represented by when it started, and when it ended.
 */
public abstract class Log {
    private Date start;
    private Date end;
    // TODO add location, for place-aware analysis

    /**
     * Constructor of an event that started and ended in certain moments.
     * @param start when started
     * @param end when ended
     */
    public Log(Date start, Date end) {
        this.start = start;
        this.end = end;
    }

    public Log() {
    }

    /**
     * Return when the event started.
     * @return when started
     */
    public Date getStart() {
        return start;
    }

    /**
     * Return when the event ended.
     * @return when ended
     */
    public Date getEnd() {
        return end;
    }


    public void setStart(Date start) {
        this.start = start;
    }

    public void setEnd(Date end) {
        this.end = end;
    }
}
