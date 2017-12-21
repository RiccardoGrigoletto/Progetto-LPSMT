package com.example.marco.progettolpsmt.backend;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import static android.content.ContentValues.TAG;

/**
 * User class to represent a user profile.
 * <br>
 * A user has a name, some default settings and a set (list) of courses.
 * @see Settings
 * @see Course
 */
public class User extends Observable {
    // Firestore related things
    private FirebaseFirestore db;
    private DocumentReference onFirestore; // reference to user document on Firestore

    // Actual data stored in Firestore as is
    private String name;
    private Settings settings;

    // Actual data stored in Firestore as Collection
    private List<Course> courses;

    private User() {
        db = FirebaseFirestore.getInstance();
        onFirestore = db.collection("users").document(FirebaseAuth.getInstance().getUid());

        settings = new Settings();
        courses = new ArrayList<>();

        // Automatically update the instance with Firebase updates
        onFirestore.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                    if (e != null) {
                        android.util.Log.w(TAG, "Listen error", e);
                        return;
                    }

//                    Map<String, Object> change = documentSnapshot.getData();
//                    name = (String) change.get("name");
                    // FIX java.lang.ClassCastException: java.util.HashMap cannot be cast to com.example.marco.progettolpsmt.backend.Settings
                    //settings = (Settings) change.get("settings");

                    // Notify a change
                    setChanged();
                    notifyObservers("user");
                }
            });

        // Automatically update the instance's courses with Firebase updates
        onFirestore.collection("courses")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            android.util.Log.w(TAG, "Listen error", e);
                            return;
                        }

                        Course changedCourse = null;
                        for (DocumentChange change : querySnapshot.getDocumentChanges()) {
                            DocumentSnapshot ds = change.getDocument();
                            changedCourse = ds.toObject(Course.class);


                            changedCourse.setOnFirestore(change.getDocument().getReference());

                            if (change.getType() == DocumentChange.Type.ADDED) {
                                courses.add(changedCourse);
                            }

                            else if (change.getType() == DocumentChange.Type.REMOVED) {
                                for (int i = 0; i < courses.size(); i++) {

                                    // Find and remove
                                    if (courses.get(i).getOnFirestore().getId().equals(changedCourse.getOnFirestore().getId())) {
                                        courses.remove(i);
                                        break;
                                    }
                                }
                            }
                            else if (change.getType() == DocumentChange.Type.MODIFIED) {
                                for (int i = 0; i < courses.size(); i++) {

                                    // Find and replace
                                    if (courses.get(i).getOnFirestore().getId().equals(changedCourse.getOnFirestore().getId())) {
                                        courses.remove(i);
                                        courses.add(changedCourse);
                                        break;
                                    }
                                }
                            }
                        }

//                        android.util.Log.w(TAG, "New course list");
//                        for (Course c : courses) {
//                            android.util.Log.w(TAG, c.getName() + " (" + c.getOnFirestore().getId() + ")");
//                        }

                        // Notify a change
                        setChanged();
                        notifyObservers("courses");
                    }
                });
    }

    private static User instance = null;

    public static User getInstance() {
        if (instance == null) {
            instance = new User();
        }

        return instance;
    }

    @Exclude public List<Course> getCourses() {
        return courses;
    }

    @Exclude public Argument getArgumentByName(String name) {
        for(int i = 0; i < courses.size(); i++ ){
            for(int j = 0; j < courses.get(i).getArguments().size(); j++ ) {
                if (courses.get(i).getArguments().get(j).getName().equals(name)) {
                    return courses.get(i).getArguments().get(j);
                }
            }
        }
        return null;
    }

    public Course getCoursebyName(String name){
        for(int i = 0; i < courses.size(); i++ ){
                if (courses.get(i).getName().equals(name)) {
                    return courses.get(i);
                }
            }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    /**
     * Update the instance's Firestore representation with the instance content.
     */
    public void updateOnFirestore() {
        onFirestore.set(this);
    }
}
