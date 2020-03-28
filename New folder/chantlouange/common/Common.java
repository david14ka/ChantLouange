package com.davidkazad.chantlouange.common;

import com.davidkazad.chantlouange.models.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pixplicity.easyprefs.library.Prefs;

public class Common {

    public static DatabaseReference LogUtil = FirebaseDatabase.getInstance().getReference("LogUtil");
    public static DatabaseReference LogClick = FirebaseDatabase.getInstance().getReference("LogClick");
    public static DatabaseReference SongsError = FirebaseDatabase.getInstance().getReference("SongsError");
    public static DatabaseReference LogPrefs = FirebaseDatabase.getInstance().getReference("UserPref");
    public static DatabaseReference LogUsers = FirebaseDatabase.getInstance().getReference("User");

    public static User currentUser = new User(Prefs.getString("email","14ka135@gmail.com"), Prefs.getString("name", "Developer"));
}
