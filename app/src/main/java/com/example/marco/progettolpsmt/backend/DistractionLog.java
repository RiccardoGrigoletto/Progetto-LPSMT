package com.example.marco.progettolpsmt.backend;

import java.util.Date;

/**
 * DistractionLog class to represent a distracting event while studying.
 */
public class DistractionLog extends Log {
    private Distraction event;
    /**
     * Constructor of a distracting event that started and ended in certain moments.
     * @param start when started
     * @param end when ended
     * @param event type of distraction
     */
    public DistractionLog(Date start, Date end, Distraction event) {
        super(start, end);
        this.event = event;
    }

    /**
     * Return the type of distracting event.
     * @return type of distraction.
     */
    public Distraction getEvent() {
        return event;
    }
}
