package com.example.marco.progettolpsmt.backend;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.marco.progettolpsmt.R;

import java.util.Observable;

/**
 * Setting class to represent settings that applies to university courses.
 * <br>
 * A settings comprehends of "regional settings"
 * (<a href="https://en.wikipedia.org/wiki/European_Credit_Transfer_and_Accumulation_System">country related</a>),
 * time settings (study related) and app features settings.
 */
public class Settings extends Observable {
    static {
        DEFAULT = new Settings();
    }

    // Default settings and fallback
    static public final Settings DEFAULT;

    // Regional settings
    private Integer hoursPerCredit;     // Study time (lectures included, in hours) required for a single credit
    private Integer regularCredits;     // Regular amount of credit for a course TODO not very sensible, but in use

    // Time settings
    private SharedPreferences TimerPreferences ;
    private Integer studyDuration;      // Minutes for a study session
    private Integer pauseDuration;      // Minutes for a pause break between study sessions
    private Integer relaxDuration;      // Minutes for a relax break after $sessionAmount sessions
    private Integer sessionAmount;      // Amount of study session before the relax break

    // Feature settings TODO better settings
    private Boolean fullImmersion;
    private Boolean noDistraction;



    /**
     * Return the amount of hours per credit.
     *
     * @return hours
     */
    public Integer getHoursPerCredit() {
        return hoursPerCredit;
    }

    /**
     * Set the amount of hours per credit.
     *
     * @param hoursPerCredit hours
     * @throws IllegalArgumentException if hoursPerCredit is zero or a negative number
     */
    public void setHoursPerCredit(Integer hoursPerCredit) throws IllegalArgumentException {
        if (hoursPerCredit <= 0)
            throw new IllegalArgumentException("'hoursPerCredit' must be a non-zero positive number");
        this.hoursPerCredit = hoursPerCredit;

        // Notify the subscribed courses (they require to be updated)
        setChanged();
        notifyObservers();
    }

    /**
     * Return the regular amount of credit for an exam.
     *
     * @return credits
     */
    public Integer getRegularCredits() {
        return regularCredits;
    }

    /**
     * Set the regular amount of credit for an exam.
     *
     * @param regularCredits credits
     */
    public void setRegularCredits(Integer regularCredits) {
        this.regularCredits = regularCredits;
    }

    /**
     * Return the amount of minutes for a study session.
     *
     * @return minutes
     */
    public Integer getStudyDuration() {
        return studyDuration;
    }

    /**
     * Set the amount of minutes for a study session.
     *
     * @param studyDuration minutes
     * @throws IllegalArgumentException if "studyDuration" is zero or a negative number
     */
    public void setStudyDuration(Integer studyDuration) throws IllegalArgumentException {
        if (studyDuration <= 0)
            throw new IllegalArgumentException("'studyDuration' must be a non-zero positive number");

    }

    /**
     * Return the amount of minutes for a pause between sessions.
     *
     * @return minutes
     */
    public Integer getPauseDuration() {
        return pauseDuration;
    }

    /**
     * Set the amount of minutes for a pause between sessions.
     *
     * @param pauseDuration minutes
     * @throws IllegalArgumentException if "pauseDuration" is zero or a negative number
     */
    public void setPauseDuration(Integer pauseDuration) throws IllegalArgumentException {
        if (pauseDuration <= 0)
            throw new IllegalArgumentException("'pauseDuration' must be a non-zero positive number");
        this.pauseDuration = pauseDuration;
    }

    /**
     * Return the amount of minutes to relax after $sessionAmount study sessions.
     *
     * @return
     */
    public Integer getRelaxDuration() {
        return relaxDuration;
    }

    /**
     * Set the amount of minutes to relax after $sessionAmount study sessions.
     *
     * @param relaxDuration minutes
     * @throws IllegalArgumentException if "relaxDuration" is zero or a negative number
     */
    public void setRelaxDuration(Integer relaxDuration) throws IllegalArgumentException {
        if (relaxDuration <= 0)
            throw new IllegalArgumentException("'relaxDuration' must be a non-zero positive number");
        this.relaxDuration = relaxDuration;
    }

    /**
     * Return the number of study sessions after which the user should relax.
     *
     * @return number of study sessions
     */
    public Integer getSessionAmount() {
        return sessionAmount;
    }

    /**
     * Set the number of study sessions after which the user should relax.
     *
     * @param sessionAmount number of study sessions
     * @throws IllegalArgumentException if "sessionAmount" is zero or a negative number
     */
    public void setSessionAmount(Integer sessionAmount) throws IllegalArgumentException {
        if (sessionAmount <= 0)
            throw new IllegalArgumentException("'sessionAmount' must be a non-zero positive number");
        this.sessionAmount = sessionAmount;
    }

    /**
     * Return if no distraction feature is enabled.
     *
     * @return if enabled or not
     */
    public Boolean isNoDistraction() {
        return noDistraction;
    }

    /**
     * Set if no distraction feature is enabled.
     *
     * @param noDistraction if enabled or not
     */
    public void setNoDistraction(Boolean noDistraction) {
        this.noDistraction = noDistraction;
    }

    /**
     * Return if full immersion feature is enabled.
     *
     * @return if enabled or not
     */
    public Boolean isFullImmersion() {
        return fullImmersion;
    }

    /**
     * Set if full immersion feature is enabled.
     *
     * @param fullImmersion if enabled or not
     */
    public void setFullImmersion(Boolean fullImmersion) {
        this.fullImmersion = fullImmersion;
    }
}