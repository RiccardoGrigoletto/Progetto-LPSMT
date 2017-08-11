package com.example.marco.progettolpsmt.Classes;

/**
 * Created by riccardogrigoletto on 09/08/2017.
 *
 * Use this for global variables, methods etc.
 */

public final class Global {
    static final int HOURS_CREDIT = 25;
    static final int MINUTES_CREDIT = 25*60;
    static final float LECTURE_HOURS = 1/3;

    /**
     *
     * @param credits the CFU of the course
     * @return (int) CFU*25-CFU*25*1/3
     */
    static int calculateHours(int credits) {
        int hours = credits* HOURS_CREDIT;
        return (int) (hours-hours* LECTURE_HOURS);
    }

    /**
     *
     * @param credits the CFU of the course
     * @param hoursCredit the amount of hours to spend for study for each CFU
     * @return (int) CFU*HOURS_CREDIT-CFU*HOURS_CREDIT*1/3
     */
    static int calculateHours(int credits, int hoursCredit) {
        int hours = credits*hoursCredit;
        return (int) (hours-hours* LECTURE_HOURS);
    }

    /**
     *
     * @param credits the CFU of the course
     * @param hoursCredit the amount of hours to spend for study for each CFU
     * @param totalLectureHours the hours of lecture
     * @return CFU*HOURS_CREDIT-totalLectureHours or 0
     */
    static int calculateHours(int credits, int hoursCredit,int totalLectureHours) {
        int hours = credits * hoursCredit;
        return (hours - totalLectureHours) > 0 ? hours - totalLectureHours : 0;
    }

    /**
     *
     * @param credits  the CFU of the course
     * @param attended if attended the course
     * @return if attended then calculateHours(credits) else credits*25
     */
    static int calculateHours(int credits, boolean attended) {
        return (attended) ? calculateHours(credits): credits* HOURS_CREDIT;
    }
}
