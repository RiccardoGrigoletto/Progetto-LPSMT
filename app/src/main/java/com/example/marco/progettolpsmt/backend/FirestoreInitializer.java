package com.example.marco.progettolpsmt.backend;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

/**
 * FirestoreInitializer static class that just configure Firestore to work.
 */
public final class FirestoreInitializer {
    public static void init() {
            // Setting up Cloud Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .build();
            db.setFirestoreSettings(settings); // Enable offline data
    }
}