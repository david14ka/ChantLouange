package com.davidkazad.chantlouange.models;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.UUID;

public class User {

    private String email;
    private String name;
    private String defaultId;

    public User() {
    }

    public User(String email, String name) {
        this.email = email;
        this.name = name;

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {

            this.defaultId = firebaseUser.getUid();

        }else this.defaultId = UUID.randomUUID().toString();

        Prefs.putString("email",email);
        Prefs.putString("name",name);
        Prefs.putString("id",defaultId);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefaultId() {
        return defaultId;
    }

    public void setDefaultId(String defaultId) {
        this.defaultId = defaultId;
    }
}
