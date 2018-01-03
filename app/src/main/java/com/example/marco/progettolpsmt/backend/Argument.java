package com.example.marco.progettolpsmt.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * Argument class to represent an argument for a university course.
 * <br>
 * An argument is made of a name, an evaluation of difficulty, a journal for tracking the user activity, and a boolean
 * to denote if the argument is complete or not.
 * <br>
 * Given an evaluation of difficulty, the argument gets the amount of expected time to complete it (which is
 * fraction of the expected time for the course containing the argument). This amount does not mean that once it is
 * reached or surpassed, the argument is done, so there's a boolean value to indicate that.
 * <br>
 * Nota bene: the amount of expected time is computed by the Course object, since it requires the knowledge of all the
 * arguments involved in it.
 * @see Course
 * @see Evaluation
 * @see Log
 */
public class Argument {
    private String name;
    private Evaluation difficulty;
    private int expectedTime;
    private List<Log> journal;
    private boolean done;

    /**
     * Constructor for a default argument.
     */
    public Argument() {
        name = "Untitled Argument";
        difficulty = Evaluation.REGULAR;
        journal = new ArrayList<>();
        done = false;
    }

    /**
     * Return the name of the argument.
     * @return argument's name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name for the argument (if the new name has a positive length, otherwise leave it unchanged).
     * @param name name
     * @throws Exception if "name" is a null reference
     */
    public void setName(String name) throws Exception {
        if (name == null) {
            throw new NullPointerException();
        }

        // Leave the name unchanged
        if (name.equals("")) {
            return;
        }

        this.name = name;
    }

    /**
     * Return the difficulty of the argument.
     * @return difficulty
     */
    public Evaluation getDifficulty() {
        return difficulty;
    }

    /**
     * Set the difficulty of the argument.
     * @param difficulty difficulty
     */
    public void setDifficulty(Evaluation difficulty) throws NullPointerException {
        if (difficulty == null) {
            throw new NullPointerException();
        }
        this.difficulty = difficulty;

    }

    /**
     * Return the expected time to complete the argument.
     * @return minutes to complete the argument
     */
    public int getExpectedTime() {
        return expectedTime;
    }

    /**
     * Set the expected time (in minutes) to complete the argument.
     * @param expectedTime minutes to complete the argument
     */
    void setExpectedTime(int expectedTime) {
        this.expectedTime = expectedTime;
    }

    /**
     * Add the log to the journal of the argument.
     * @param log log to add
     */
    public void addLog(Log log) {
        journal.add(log);

    }

    /**
     * Remove the log from the journal of the argument.
     * @param log log to remove
     */
    public void removeLog(Log log) {
        journal.remove(log);
    }

    /**
     * Return the journal for the argument.
     * @return journal
     */
    public List<Log> getJournal() {
        return journal;
    }

    /**
     * Return if the study for the argument is done.
     * @return if is done or not
     */
    public boolean isDone() {
        return done;
    }

    /**
     * Set if the study for the argument is done.
     * @param done if is done or not
     */
    public void setDone(boolean done) {
        this.done = done;
    }

    public void setJournal(List<Log> journal) {
        this.journal = journal;
    }

    /**
     * Compute and return the amount of time spent (in minutes) to study for the argument.
     * @return minutes spent to study
     */
    public int computeStudyTimeSpent() {
        int time = 0;
        for (Log log: journal) {
            // Date.getTime() returns milliseconds
            // It subtract time using seconds (/1_000) and not minutes (/60_000) because the last one can introduce
            // 1-minute error, so we divide by 60 later to get minutes
            long end = log.getEnd().getTime() / 1_000;
            long start = log.getStart().getTime() / 1_000;

            time += (end - start);
        }

        // There's a loss of seconds (1'59" -> 1'), but who cares (minutes do the difference, not seconds)
        return time / 60;
    }

    /**
     * Compute and return the progress of the argument.
     * <br>
     * Return value meanings:
     * <ul>
     *     <li>[0.00, 0.90]: the study for the argument is not complete;</li>
     *     <li>1.00: the study for the argument is complete.</li>
     * </ul>
     * @return progress
     */
    public double computeProgress() {
        if (done) {
            return 1.00;
        }

        double estimation = (double) computeStudyTimeSpent() / expectedTime;

        if (estimation > 0.90) {
            return 0.90;
        }

        return estimation;
    }

    /**
     * Compute and return the ratio between time spent for studying and time tracked.
     * @return focus ratio
     */
    public double computeOnFocusRatio() {
        int totalTime = 0;
        for (Log log: journal) {
            long end = log.getEnd().getTime() / 1_000;
            long start = log.getStart().getTime() / 1_000;
            totalTime += (end - start);
        }

        return (double) computeStudyTimeSpent() / (totalTime / 60);
    }
}
