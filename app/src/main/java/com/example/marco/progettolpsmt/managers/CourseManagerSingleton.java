package com.example.marco.progettolpsmt.managers;

import com.example.marco.progettolpsmt.backend.Argument;
import com.example.marco.progettolpsmt.backend.Course;
import com.example.marco.progettolpsmt.backend.Exam;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by rober on 05/12/2017.
 */

public class CourseManagerSingleton {
    private static final CourseManagerSingleton ourInstance = new CourseManagerSingleton();
    private ArrayList<Course> values = new ArrayList<>();
    Course c1 = new Course();

    public static CourseManagerSingleton getInstance() {
        return ourInstance;
    }

    private CourseManagerSingleton() {
        c1.setName("fisica");
        c1.addArgument(new Argument());
        c1.addArgument(new Argument());
        c1.addExam(new Exam(new Date()));
        c1.addExam(new Exam(new Date()));
        values.add(c1);
        values.add(new Course());
    }

    public ArrayList<Course> getAllCourses(){
        return  values;
    }

    public  Course getCourseById(int courseId){
        return c1;
    }
}
