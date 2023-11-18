package com.example.blackbox;

import android.net.Uri;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class ProfileDB {
    private FirebaseUser user;
    private String uid;
    boolean emailVerified;
    private String name;
    private String email;
    private Uri photoUrl;
    private ArrayList<String> itemIDs;  // store a list of item IDs that are owned by this profile
    private InventoryDB inventoryDB;    // the inventory database object

    public ProfileDB() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            name = user.getDisplayName();
            email = user.getEmail();
            photoUrl = user.getPhotoUrl();
            // Check if user's email is verified
            emailVerified = user.isEmailVerified();
            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            uid = user.getUid();
            inventoryDB = new InventoryDB();
        }
    }

    public void logOut() {
        FirebaseAuth.getInstance().signOut();
    }

    public FirebaseUser getUser() {
        return user;
    }

    public String getUid() {
        return uid;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Uri getPhotoUrl() {
        return photoUrl;
    }

    public void setUser(FirebaseUser user) {
        this.user = user;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhotoUrl(Uri photoUrl) {
        this.photoUrl = photoUrl;
    }

}
