package com.example.blackbox;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileDB {
    private CollectionReference profileRef;
    private FirebaseFirestore db;

    public ProfileDB() {
        db = FirebaseFirestore.getInstance();
        profileRef = db.collection("profiles");
    }

    public ProfileDB(String collectionName) {
        db = FirebaseFirestore.getInstance();
        profileRef = db.collection(collectionName);
    }

    public CollectionReference getProfileRef() {
        return profileRef;
    }

    public void addEditProfile(Profile profile) {
        Map<String, Object> data = generateProfileHashMap(profile);
        profileRef.document(profile.getUid()).set(data);
    }

    public void getProfileById(String uid, final OnProfileRetrievedListener listener) {
        profileRef
                .document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Get the specific fields from database
                                String name = document.getString("name");
                                String bio = document.getString("bio");
                                String email = document.getString("email");

                                // Set the profile
                                Profile profile = new Profile(uid, name, bio, email);
                                listener.OnProfileRetrievedListener(profile);

                            } else {
                                Log.d("TAG", "No such document");
                                listener.OnProfileRetrievedListener(null);
                            }
                        } else {
                            Log.w("TAG", "Error getting document", task.getException());
                            listener.OnProfileRetrievedListener(null);
                        }
                    }
                });
    }

    // Interface to define a callback for Profile retrieval
    public interface OnProfileRetrievedListener {
        void OnProfileRetrievedListener(Profile profile);
    }

    private Map<String, Object> generateProfileHashMap(Profile profile){
        Map<String, Object> data = new HashMap<>();
        data.put("name", profile.getName());
        data.put("bio", profile.getBio());
        data.put("email", profile.getEmail());
        return data;
    }
}
