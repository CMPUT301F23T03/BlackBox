package com.example.blackbox;

import android.net.Uri;

/**
 * Represents a user profile containing basic information such as name, bio, email, and profile picture.
 */
public class Profile {
    private String uid;
    private String name;
    private String bio;
    private String email;
    // private Uri profilePic;

    /**
     * Constructs a Profile object with the specified parameters.
     *
     * @param uid   The user ID associated with the profile.
     * @param name  The user's name.
     * @param bio   The user's bio or description.
     * @param email The user's email address.
     *              // @param profilePic The user's profile picture.
     */
    public Profile(String uid, String name, String bio, String email /*,Uri profilePic*/) {
        this.uid = uid;
        this.name = name;
        this.bio = bio;
        this.email = email;
        // this.profilePic = profilePic;
    }

    /**
     * Constructs a Profile object without specifying the user ID.
     * Useful when creating a new profile.
     *
     * @param name  The user's name.
     * @param bio   The user's bio or description.
     * @param email The user's email address.
     *              // @param profilePic The user's profile picture.
     */
    public Profile(String name, String bio, String email /*,Uri profilePic*/) {
        this.uid = uid;
        this.name = name;
        this.bio = bio;
        this.email = email;
        // this.profilePic = profilePic;
    }

    /**
     * Getters and setters
     */
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    //    public Uri getProfilePic() {
//        return profilePic;
//    }
//
//    public void setProfilePic(Uri profilePic) {
//        this.profilePic = profilePic;
//    }
}
