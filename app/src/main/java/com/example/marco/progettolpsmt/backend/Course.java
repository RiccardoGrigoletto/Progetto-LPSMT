package com.example.marco.progettolpsmt.backend;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

/**
 * Course class to represent a university course.
 * <br>
 * A course is made of a name, an amount of
 * <a href="https://en.wikipedia.org/wiki/European_Credit_Transfer_and_Accumulation_System">credits</a>,
 * a set (list) of arguments (handled by the Argument class) and a set (list) of exams (handled by the Exam class).
 * <br>
 * Given the amount of credits, ECTS defines an amount of time expected to complete the course.
 * @see Settings
 * @see Argument
 * @see Exam
 */
public class Course implements Observer {
    private FirebaseFirestore db;
    @Exclude private DocumentReference onFirestore; // non lo esclude...

    public DocumentReference getOnFirestore() {
        return onFirestore;
    }

    public void setOnFirestore(DocumentReference onFirestore) {
        this.onFirestore = onFirestore;
    }

    private String name;
    private int credits;
    private int timeExpected;
    private List<Argument> arguments;
    private List<Exam> exams;
    private Settings settings;

    /**
     * Constructor for a default course.
     */
    public Course() {
        db = FirebaseFirestore.getInstance();

        name = "Untitled Course";
        // Regional settings defaults
        Settings.DEFAULT.setHoursPerCredit(25);
        Settings.DEFAULT.setRegularCredits(6);
        // Time settings defaults (these are the Pomodoro Technique defaults)
        Settings.DEFAULT.setStudyDuration(25);
        Settings.DEFAULT.setPauseDuration(5);
        Settings.DEFAULT.setRelaxDuration(30);
        Settings.DEFAULT.setSessionAmount(4);
        // Features settings defaults
        Settings.DEFAULT.setNoDistraction(false);
        Settings.DEFAULT.setFullImmersion(true);
        credits = Settings.DEFAULT.getRegularCredits();
        arguments = new ArrayList<>();
        exams = new ArrayList<>();
        timeExpected = credits * Settings.DEFAULT.getHoursPerCredit() * 60;
    }

    /**
     * Update the object representation on Firestore.
     */
    public void updateOnFirestore() {
        if (onFirestore == null) {
            onFirestore = db.collection("users").document(FirebaseAuth.getInstance().getUid())
                    .collection("courses").document();
        }
        onFirestore.set(this);
    }

    /**
     * Remove the object representation on Firestore
     */
    public void removeOnFirestore() {
        if (onFirestore != null) {
            onFirestore.delete();
            //onFirestore = null;
        }
    }

    /**
     * Return the name of the course.
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name for the course (if the new name has a positive length, otherwise leave it unchanged).
     * @param name name
     * @throws NullPointerException if "name" is a null reference
     */
    public void setName(String name) throws NullPointerException {
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
     * Return the amount of credits for the course.
     * @return amount of credits
     */
    public int getCredits() {
        return credits;
    }

    /**
     * Set the amount of credits for the course.
     * @param credits amount of credits
     * @throws IllegalArgumentException if "credits" is 0 or a negative number
     */
    public void setCredits(int credits) throws IllegalArgumentException {
        if (credits <= 0) {
            throw new IllegalArgumentException("'Credits' must be a non-zero positive number");
        }
        this.credits = credits;

        // Things are changed, recompute expected time (for the arguments too)
        updateCourse();
    }

    /**
     * Returns the amount of time expected to complete the course.
     * @return minutes to complete
     */
    public int getTimeExpected() {
        return timeExpected;
    }

    /**
     * Add the argument to the course.
     * @param argument argument to add
     */
    public void addArgument(Argument argument) {
        argument.addObserver(this);
        arguments.add(argument);

        // Things are changed, recompute expected time for the arguments
        updateCourse();
    }

    /**
     * Remove the argument from the course.
     * @param argument argument to remove
     */
    public void removeArgument(Argument argument) {
        argument.deleteObserver(this);
        arguments.remove(argument);

        // Things are changed, recompute expected time for the arguments
        updateCourse();
    }

    /**
     * Return the arguments's list for the course.
     * @return list of arguments
     */
    public List<Argument> getArguments() {
        // TODO return an hard copy
        return arguments;
    }

    /**
     * Add the exam to the course.
     * @param exam exam to add
     */
    public void addExam(Exam exam) {
        exams.add(exam);

        // The list could now be unsorted
        // TODO fix: exams.sort(Comparator.comparing(Exam::getDate));
    }

    /**
     * Remove the exam from the course.
     * @param exam exam to remove
     */
    public void removeExam(Date exam) {
        exams.remove(exam);
    }

    /**
     * Return the exams's list for the course.
     * @return list of exams
     */
    public List<Exam> getExams() {
        // TODO return an hard copy
        return exams;
    }

    /**
     * Return the course's settings.
     * @return setting of the course
     */
    public Settings getSettings() {
        return settings;
    }

    /**
     * Set the settings for the course.
     * @param settings settings
     * @throws NullPointerException
     */
    public void setSettings(Settings settings) throws NullPointerException {
//        if (settings == null)
////            throw new NullPointerException();
//
//        // Remove the course for the previous settings (if any)
//        if (this.settings != null) {
//            this.settings.deleteObserver(this);
//        }
//
//        this.settings = settings;
//        settings.addObserver(this);
    }

    /**
     * Compute and return the amount of time spent to study for the course.
     * @return minutes spent to study
     */
    public int computeStudyTimeSpent() {
        int time = 0;
        for (Argument argument: arguments) {
            time += argument.computeStudyTimeSpent();
        }

        return time;
    }

    /**
     * Compute and return the amount of expected time to complete the study for the course.
     * @return minutes left to study
     */
    public int computeExpectedTimeLeft() {
        return timeExpected - computeStudyTimeSpent();
    }

    /**
     * Compute and return the progress of the course.
     * @return progress
     */
    public double computeProgress() {
        // No arguments
        if (arguments.size() == 0) {
            return 0.0;
        }

        double progress = 0.0;
        for (Argument argument: arguments) {
            progress += argument.computeProgress();
        }

        return progress / arguments.size();
    }

    /**
     * Update the expected time for every argument.
     * <br>
     * This is called when there's a change in:
     * <ul>
     *     <li>the arguments's list (Course class thing);</li>
     *     <li>the amount of credits (Course class thing);</li>
     *     <li>the argument's difficulty (Argument class thing); (*)</li>
     *     <li>the course's settings (Setting class thing). (*)</li>
     * </ul>
     * (*) <i>called via Observable.notifyObservers()</i>
     */
    private void updateCourse() {
        // No arguments
        if (arguments.size() == 0) {
            return;
        }

        // Settings in use
        int hoursPerCredit;
        if (getSettings() == null || getSettings().getHoursPerCredit() == null) {
            hoursPerCredit = Settings.DEFAULT.getHoursPerCredit();
        } else {
            hoursPerCredit = getSettings().getHoursPerCredit();
        }

        timeExpected = credits * hoursPerCredit * 60;

        // Amount of minutes for every argument, if they have the same difficulty
        int sameDifficultyExpectedTime = timeExpected / arguments.size();

        double totalDifficulty = 0.0;
        for (Argument argument: arguments) {
            totalDifficulty += argument.getDifficulty().getValue();
        }

        // To adjust the product
        double k = arguments.size() / totalDifficulty;

        for (Argument argument: arguments) {
            argument.setExpectedTime((int) Math.round( sameDifficultyExpectedTime * argument.getDifficulty().getValue()
                    * k ) );
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        updateCourse();
    }

    public void setArguments(ArrayList<Argument> arguments) {
        this.arguments = arguments;
    }
    public void clearArguments() {
        this.arguments.clear();
    }
    public void setExams(ArrayList<Exam> exams) {
        this.exams = exams;
    }
    public void clearExams() {
        this.exams.clear();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != Course.class) return false;
        return this.name == ((Course)obj).getName();
    }
}
