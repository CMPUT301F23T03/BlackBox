package com.example.blackbox;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

/**
 * The `GoogleAuthDB` class represents a user profile in a Firebase authentication system.
 * It encapsulates user information such as display name, email, photo URL, and authentication status.
 * This class provides methods to interact with the user profile, such as retrieving and setting user information,
 * checking email verification status, and logging out the user.
 */
public class GoogleAuthDB {
    private FirebaseUser user;
    private String uid;
    private String name;
    private String email;
    private Uri photoUrl;
    private Profile currentProfile;
    private ProfileDB profileDB;

    /**
     * Constructs a new `GoogleAuthDB` instance by retrieving user information from the current Firebase authentication session.
     * If no user is authenticated, the instance will be initialized with default values.
     */
    public GoogleAuthDB() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            name = user.getDisplayName();
            email = user.getEmail();
            photoUrl = user.getPhotoUrl();
            uid = user.getUid();
            profileDB = new ProfileDB();
        }
    }

    /**
     * Logs out the currently authenticated user by signing them out of Firebase.
     */
    public void logOut() {
        FirebaseAuth.getInstance().signOut();
    }

    public void createProfile() {
        ProfileExists(uid, new OnDocumentCheckListener() {
            @Override
            public void onDocumentChecked(boolean exists) {
                if (!exists) {
                    System.out.println("Document with name " + uid + " does not exist in the 'profiles' collection.");
                    String bio = "Write a bio by editing your profile!";
                    Profile profile = new Profile(uid, name, bio, email);
                    profileDB.addEditProfile(profile);
                    System.out.println("Document with name " + uid + " does exist in the 'profiles' collection.");
                } else {
                    System.out.println("Document with name " + uid + " does exist in the 'profiles' collection.");
                }
            }
        });
    }

    public void displayGoogleProfilePicture(ImageButton profilePicture, int height, int width, Fragment fragment) {
        Uri imageUri = photoUrl;
        int desiredWidth = height; // width
        int desiredHeight = width; // height
        Glide.with(fragment)
                .load(imageUri)
                .apply(new RequestOptions()
                        .override(desiredWidth, desiredHeight) // resize the image to the desired dimensions
                        .circleCrop()) // make the image round
                .into(profilePicture);
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

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhotoUrl(Uri photoUrl) {
        this.photoUrl = photoUrl;
    }

    private void ProfileExists(String uid, OnDocumentCheckListener listener) {
        // Use a query to check if a document with the specified uid exists
        DocumentReference docRef = profileDB.getProfileRef().document(uid);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                listener.onDocumentChecked(document.exists());
            } else {
                listener.onDocumentChecked(false);
            }
        });
    }

    interface OnDocumentCheckListener {
        void onDocumentChecked(boolean exists);
    }
}
