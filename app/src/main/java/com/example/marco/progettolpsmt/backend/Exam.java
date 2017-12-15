package com.example.marco.progettolpsmt.backend;

import com.google.firebase.firestore.Exclude;

import java.util.Date;

/**
 * Exam class to represent a date for an exam and some details.
 */
public class Exam {
    private Date date;
    private String details;

    public Exam() {}

    public Exam(Date date) {
        this.date = date;
    }

    /**
     * Return the exam's date
     * @return date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Set the exam's date
     * @param date date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Return the exam's details
     * @return
     */
    public String getDetails() {
        return details;
    }

    /**
     * Set the exam's details
     * @param details details
     */
    public void setDetails(String details) {
        this.details = details;
    }

    /**
     * Return how many days are left (positive) / passed (negative).
     * @return days left/passed
     */
    @Exclude public int getDaysLeft() {
        // TODO fix the by 1 day problem (now+24h - now = 1 day, now+24h-1ms - now = 0 days)
        int days = (int) ((date.getTime() - new Date().getTime()) / (1_000 * 60 * 60 * 24));

        return days;
    }
}
