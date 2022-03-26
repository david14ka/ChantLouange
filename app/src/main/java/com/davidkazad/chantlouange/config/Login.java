package com.davidkazad.chantlouange.config;

import com.google.firebase.auth.FirebaseUser;

import java.util.Date;

public class Login {
    private FirebaseUser user;
    private String date;
    private long timestamp;

    public Login(FirebaseUser user) {
        this.user = user;
        this.date = new Date().toString();
        this.timestamp = System.currentTimeMillis();
    }

    public FirebaseUser getUser() {
        return user;
    }

    public void setUser(FirebaseUser user) {
        this.user = user;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
