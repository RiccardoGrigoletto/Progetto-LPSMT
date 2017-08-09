package com.example.marco.progettolpsmt.Classes;

/**
 * Created by riccardogrigoletto on 09/08/2017.
 *
 * Use this for global variables, methods etc.
 */

public final class Global {
    static final int hoursxCFU = 25;
    static final float lectureHours = 1/3;

    /**
     *
     * @param CFU the CFU of the course
     * @return (int) CFU*25-CFU*25*1/3
     */
    static int calculateHours(int CFU) {
        int hours = CFU*hoursxCFU;
        return (int) (hours-hours*lectureHours);
    }

    /**
     *
     * @param CFU the CFU of the course
     * @param hoursxCFU the amount of hours to spend for study for each CFU
     * @return (int) CFU*hoursxCFU-CFU*hoursxCFU*1/3
     */
    static int calculateHours(int CFU, int hoursxCFU) {
        int hours = CFU*hoursxCFU;
        return (int) (hours-hours*lectureHours);
    }

    /**
     *
     * @param CFU the CFU of the course
     * @param hoursxCFU the amount of hours to spend for study for each CFU
     * @param totalLectureHours the hours of lecture
     * @return CFU*hoursxCFU-totalLectureHours or 0
     */
    static int calculateHours(int CFU, int hoursxCFU,int totalLectureHours) {
        int hours = CFU * hoursxCFU;
        return (hours - totalLectureHours) > 0 ? hours - totalLectureHours : 0;
    }
}
