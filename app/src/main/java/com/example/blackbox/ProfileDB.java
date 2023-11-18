package com.example.blackbox;

import android.net.Uri;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

/**
 * The `ProfileDB` class represents a user profile in a Firebase authentication system.
 * It encapsulates user information such as display name, email, photo URL, and authentication status.
 * This class provides methods to interact with the user profile, such as retrieving and setting user information,
 * checking email verification status, and logging out the user.
 */
public class ProfileDB {
    private FirebaseUser user;
    private String uid;
    boolean emailVerified;
    private String name;
    private String email;
    private Uri photoUrl;

    /**
     * Constructs a new `ProfileDB` instance by retrieving user information from the current Firebase authentication session.
     * If no user is authenticated, the instance will be initialized with default values.
     */
    public ProfileDB() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            name = user.getDisplayName();
            email = user.getEmail();
            photoUrl = user.getPhotoUrl();
            emailVerified = user.isEmailVerified();
            uid = user.getUid();
        }
    }

    /**
     * Logs out the currently authenticated user by signing them out of Firebase.
     */
    public void logOut() {
        FirebaseAuth.getInstance().signOut();
    }

    /**
     * Getters and setters
     */
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
