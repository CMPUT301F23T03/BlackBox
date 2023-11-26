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

/**
 * The ProfileDB class is responsible for interacting with the Firebase Firestore database
 * to perform operations related to user profiles.
 */
public class ProfileDB {
    private CollectionReference profileRef;
    private FirebaseFirestore db;

    /**
     * Default constructor for the ProfileDB class. Initializes the Firebase Firestore instance
     * and sets the collection reference to "profiles".
     */
    public ProfileDB() {
        db = FirebaseFirestore.getInstance();
        profileRef = db.collection("profiles");
    }

    /**
     * Constructor for the ProfileDB class with a custom collection name.
     *
     * @param collectionName The name of the Firestore collection for profiles.
     */
    public ProfileDB(String collectionName) {
        db = FirebaseFirestore.getInstance();
        profileRef = db.collection(collectionName);
    }

    /**
     * Retrieves the Firestore collection reference for profiles.
     *
     * @return The CollectionReference for profiles.
     */
    public CollectionReference getProfileRef() {
        return profileRef;
    }

    /**
     * Adds or updates a user profile in the Firestore database.
     *
     * @param profile The Profile object containing user information to be added or updated.
     */
    public void addEditProfile(Profile profile) {
        Map<String, Object> data = generateProfileHashMap(profile);
        profileRef.document(profile.getUid()).set(data);
    }

    /**
     * Retrieves a user profile from the Firestore database by the user ID.
     *
     * @param uid      The user ID for the profile to retrieve.
     * @param listener An OnProfileRetrievedListener to handle the retrieved profile.
     */
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

    /**
     * Interface to define a callback for Profile retrieval.
     */
    public interface OnProfileRetrievedListener {
        void OnProfileRetrievedListener(Profile profile);
    }

    /**
     * Generates a HashMap from a Profile object, which can be used to update the Firestore database.
     *
     * @param profile The Profile object to convert to a HashMap.
     * @return A HashMap representing the fields of the Profile object.
     */
    private Map<String, Object> generateProfileHashMap(Profile profile){
        Map<String, Object> data = new HashMap<>();
        data.put("name", profile.getName());
        data.put("bio", profile.getBio());
        data.put("email", profile.getEmail());
        return data;
    }
}
