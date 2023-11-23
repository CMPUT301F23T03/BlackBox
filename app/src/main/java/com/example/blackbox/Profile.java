package com.example.blackbox;

import android.net.Uri;

public class Profile {
    private String uid;
    private String name;
    private String bio;
    private String email;
    // private Uri profilePic;

    public Profile(String uid, String name, String bio, String email /*,Uri profilePic*/) {
        this.uid = uid;
        this.name = name;
        this.bio = bio;
        this.email = email;
        // this.profilePic = profilePic;
    }

    public Profile(String name, String bio, String email /*,Uri profilePic*/) {
        this.uid = uid;
        this.name = name;
        this.bio = bio;
        this.email = email;
        // this.profilePic = profilePic;
    }

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
