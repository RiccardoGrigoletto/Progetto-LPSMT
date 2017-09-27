package com.example.marco.progettolpsmt.backend;

import java.util.Date;

/**
 * StudyLog class to represent a study event.
 */
public class StudyLog extends Log {
    /**
     * Constructor of a study event that started and ended in certain moments.
     * @param start when started
     * @param end when ended
     */
    public StudyLog(Date start, Date end) {
        super(start, end);
    }
}
