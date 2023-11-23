package com.example.blackbox;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileDB {
    private CollectionReference profileRef;
    private FirebaseFirestore db;

    public ProfileDB() {
        db = FirebaseFirestore.getInstance();
        profileRef = db.collection("profiles");
    }

    public CollectionReference getProfileRef() {
        return profileRef;
    }

    public void addProfile(Profile profile) {
        Map<String, Object> data = generateProfileHashMap(profile);
        profileRef.document(profile.getUid()).set(data);
    }

    public void edit(Profile oldProfile, Profile newProfile) {

    }

    private Map<String, Object> generateProfileHashMap(Profile profile){
        Map<String, Object> data = new HashMap<>();
        data.put("name", profile.getName());
        data.put("bio", profile.getBio());
        data.put("email", profile.getEmail());
        return data;
    }
}
