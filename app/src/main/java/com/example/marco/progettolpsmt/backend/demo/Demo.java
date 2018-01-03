package com.example.marco.progettolpsmt.backend.demo;

import com.example.marco.progettolpsmt.backend.Argument;
import com.example.marco.progettolpsmt.backend.Course;
import com.example.marco.progettolpsmt.backend.Distraction;
import com.example.marco.progettolpsmt.backend.DistractionLog;
import com.example.marco.progettolpsmt.backend.Evaluation;
import com.example.marco.progettolpsmt.backend.Exam;
import com.example.marco.progettolpsmt.backend.Log;
import com.example.marco.progettolpsmt.backend.Settings;
import com.example.marco.progettolpsmt.backend.StudyLog;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Demo class to illustrate the usage of the backend.
 */
public class Demo {
    public static void main(String[] args) throws Exception {
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

        // Settings creation. Those that are not set falls-back to defaults
        Settings s = new Settings();
        s.setStudyDuration(50);
        s.setRelaxDuration(90);
        s.setNoDistraction(true);

        Argument a;

        // Course creation
        Course c = new Course();
        c.setName("Algorithms and Data Structures");
        c.setCredits(12);
        c.setSettings(s); // If not set, falls-back to defaults
        // Add exams to the course
        Exam e = new Exam(Utilities.stringToDate("2017/08/31-14:00"));
        c.addExam(e);
        e = new Exam(Utilities.stringToDate("2018/08/31-15:00"));
        e.setDetails("It's Friday!");
        c.addExam(e);
        e = new Exam(Utilities.stringToDate("2016/08/31-16:00"));
        c.addExam(e);

        // Argument creation
        a = new Argument();
        a.setName("Trees");
        a.setDifficulty(Evaluation.EASY);
        a.setDone(true);
        // Add the argument to the course
        c.addArgument(a);
        // Add logs to the argument
        a.addLog(new StudyLog(new Date(0), new Date(1_000*60*60*30))); // 30h
        a.addLog(new DistractionLog(new Date(0), new Date(1_000*60*75), Distraction.OTHER_ACTIVITY)); // 75'

        // Already shown
        a = new Argument();
        c.addArgument(a);
        a.setName("Divide et Impera");
        a.setDifficulty(Evaluation.HARD);
        a.addLog(new StudyLog(new Date(0), new Date(1_000*60*60*50))); // 50h
        a.addLog(new StudyLog(new Date(0), new Date(1_000*60*50))); // 50'
        a = new Argument();
        c.addArgument(a);
        a.setName("Graphs");
        a.setDifficulty(Evaluation.REGULAR);
        a.addLog(new StudyLog(new Date(0), new Date(1_000*60*60*2))); // 2h
        a.addLog(new DistractionLog(new Date(0), new Date(1_000*60*40), Distraction.CALL)); // 40'
        a = new Argument();
        c.addArgument(a);
        a.setName("Dynamic Programming");
        a.setDifficulty(Evaluation.SUPER_HARD);

        // Output example
        System.out.println("Course:\t\t" + c.getName());
        System.out.println("Credits:\t" + c.getCredits());

        System.out.println("Settings:");
        {
            System.out.print("\t\t\tHours per credit: ");
            if (c.getSettings() == null || c.getSettings().getHoursPerCredit() == null) {
                System.out.print(Settings.DEFAULT.getHoursPerCredit() + " (DEFAULT)");
            } else {
                System.out.print(c.getSettings().getHoursPerCredit());
            }
            System.out.println();

            System.out.print("\t\t\tStudy duration: ");
            if (c.getSettings() == null || c.getSettings().getStudyDuration() == null) {
                System.out.print(Utilities.hoursAndMinutes(Settings.DEFAULT.getStudyDuration()) + " (DEFAULT)");
            } else {
                System.out.print(Utilities.hoursAndMinutes(c.getSettings().getStudyDuration()));
            }
            System.out.println();

            System.out.print("\t\t\tPause duration: ");
            if (c.getSettings() == null || c.getSettings().getPauseDuration() == null) {
                System.out.print(Utilities.hoursAndMinutes(Settings.DEFAULT.getPauseDuration()) + " (DEFAULT)");
            } else {
                System.out.print(Utilities.hoursAndMinutes(c.getSettings().getPauseDuration()));
            }
            System.out.println();

            System.out.print("\t\t\tRelax duration: ");
            if (c.getSettings() == null || c.getSettings().getRelaxDuration() == null) {
                System.out.print(Utilities.hoursAndMinutes(Settings.DEFAULT.getRelaxDuration()) + " (DEFAULT)");
            } else {
                System.out.print(Utilities.hoursAndMinutes(c.getSettings().getRelaxDuration()));
            }
            System.out.println();


            System.out.print("\t\t\tSessions amount before relax: ");
            if (c.getSettings() == null || c.getSettings().getSessionAmount() == null) {
                System.out.print(Settings.DEFAULT.getSessionAmount() + " (DEFAULT)");
            } else {
                System.out.print(c.getSettings().getSessionAmount());
            }
            System.out.println();


            System.out.print("\t\t\tNo distraction Mode: ");
            if (c.getSettings() == null || c.getSettings().isNoDistraction() == null) {
                System.out.print((Settings.DEFAULT.isNoDistraction() ? "on" : "off") + " (DEFAULT)");
            } else {
                System.out.print(c.getSettings().isNoDistraction() ? "on" : "off");
            }
            System.out.println();


            System.out.print("\t\t\tFull immersion Mode: ");
            if (c.getSettings() == null || c.getSettings().isFullImmersion() == null) {
                System.out.print((Settings.DEFAULT.isFullImmersion() ? "on" : "off") + " (DEFAULT)");
            } else {
                System.out.print(c.getSettings().isFullImmersion() ? "on" : "off");
            }
            System.out.println();
        }

        System.out.println("Progress:\t" + Math.round(c.computeProgress()*100) + "%");
        System.out.println("Arguments:");

        for (Argument argument: c.getArguments()) {
            System.out.print("\t\t\t");
            if (argument.isDone()) {
                System.out.print("[DONE]\t");
            } else {
                System.out.print("[" + Math.round(argument.computeProgress()*100)+ "%]\t");
            }
            int timeSpent = argument.computeStudyTimeSpent();
            int timeExpected = argument.getExpectedTime();
            System.out.print(argument.getName() + " (" + argument.getDifficulty().toString() + "): ");
            System.out.print(Utilities.hoursAndMinutes(timeSpent) + "/" + Utilities.hoursAndMinutes(timeExpected));
            System.out.print(" (Focus ratio: " + Math.round(argument.computeOnFocusRatio()*100) + "%)");
            System.out.println();
            if (argument.getJournal().size() > 0) {
                for (Log log: argument.getJournal()) {
                    System.out.print("\t\t\t\t\t`--");
                    if (log instanceof StudyLog) {
                        StudyLog t = (StudyLog) log;
                        System.out.print("[STUDY " + Utilities.hoursAndMinutes((int) (t.getEnd().getTime() - t.getStart().getTime()) /1_000/60) + "] from: " + t.getStart() + " to: " + t.getEnd());
                    }
                    if (log instanceof DistractionLog) {
                        DistractionLog d = (DistractionLog) log;
                        System.out.print("[" + d.getEvent() + " " + Utilities.hoursAndMinutes((int) (d.getEnd().getTime() - d.getStart().getTime()) /1_000/60) + "] from: " + d.getStart() + " to: " + d.getEnd());
                    }
                    System.out.println();
                }
            }
        }

        System.out.println("Exams:");
        for (Exam exam: c.getExams()) {
            System.out.print("\t\t\t");

            /*int daysLeft = exam.getDaysLeft();
            if (daysLeft < 0) {
                System.out.print("["+ -daysLeft + " days ago]\t");
            } else if (daysLeft == 0) {
                System.out.print("[today]\t\t\t");
            } else {
                System.out.print("["+ daysLeft + " days left]\t");
            }*/

            System.out.print(exam.getDate().toString());

            if (exam.getDetails() != null) {
                System.out.print("\t(" + exam.getDetails() + ")");
            }
            System.out.println();
        }
    }

    /**
     * Utilities class handling time-related things.
     */
    public static class Utilities {
        /**
         * Return a Date object for the date's string if the format "yyyy/MM/dd-hh:mm"
         * @param yyyyMMddhhmm date's string
         * @return Date object for the date's string
         * @throws Exception thrown by SimpleDateFormat
         */
        static public Date stringToDate(String yyyyMMddhhmm) throws Exception {
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd-hh:mm");
            return format.parse(yyyyMMddhhmm);
        }

        /**
         * Return a pretty string for the minutes amount
         * @param minutes minutes
         * @return String in format "${hours}h${minutes}'"
         */
        static public String hoursAndMinutes(int minutes) {
            int hours = minutes/60;
            minutes = minutes%60;
            return (hours == 0 ? "" : String.valueOf(hours) + "h") + (minutes < 10 ? "0" : "") + String.valueOf(minutes%60) + "'";
        }
    }
}
