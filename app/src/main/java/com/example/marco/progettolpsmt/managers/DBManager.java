package com.example.marco.progettolpsmt.managers;

import com.example.marco.progettolpsmt.backend.Argument;
import com.example.marco.progettolpsmt.backend.Course;
import com.example.marco.progettolpsmt.backend.Exam;

import java.util.Date;

/**
 * Created by ricca on 23/11/2017.
 */

public class DBManager {


    public static void uploadCourse(Course course) {
        //todo
    }

    public static Course getCourse(int courseID) {
        //todo
        Course c1 = new Course();
        c1.setName("fisica");
        c1.addArgument(new Argument());
        c1.addArgument(new Argument());
        c1.addExam(new Exam(new Date()));
        c1.addExam(new Exam(new Date()));
        return c1;
    }
}
