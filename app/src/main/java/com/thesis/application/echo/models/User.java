package com.thesis.application.echo.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;

/**
 * Created by Abra Kitlaw on 25-Jun-18.
 */

@IgnoreExtraProperties
public class User {
    public String username;
    public String fullname;
    public String gender;
    public String profilePictUrl;

    public User() {
    }

    public User(String username, String fullname, String gender, String profilePictUrl) {
        this.username = username;
        this.fullname = fullname;
        this.gender = gender;
        this.profilePictUrl = profilePictUrl;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfilePictUrl() {
        return profilePictUrl;
    }

    public void setProfilePictUrl(String profilePictUrl) {
        this.profilePictUrl = profilePictUrl;
    }

}
