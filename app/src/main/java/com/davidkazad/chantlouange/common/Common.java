package com.davidkazad.chantlouange.common;

import com.davidkazad.chantlouange.models.Post;
import com.davidkazad.chantlouange.models.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;
import java.util.List;

public class Common {

    public static DatabaseReference LogUtil = FirebaseDatabase.getInstance().getReference("LogUtil");
    public static DatabaseReference LogClick = FirebaseDatabase.getInstance().getReference("LogClick");
    public static DatabaseReference SongsError = FirebaseDatabase.getInstance().getReference("SongsError");
    public static DatabaseReference LogPrefs = FirebaseDatabase.getInstance().getReference("UserPref");
    public static DatabaseReference LogUsers = FirebaseDatabase.getInstance().getReference("User");
    public static DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Posts");
    public static DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference("Comments");
    public static DatabaseReference loginRef = FirebaseDatabase.getInstance().getReference("login");
    //public static DatabaseReference logUtil = FirebaseDatabase.getInstance().getReference("logUtil");

    public static final String PREFS_TABLE_MATIERES_ALPHABETIQUE = "numerotation";

    public static User currentUser = User.getUser();
    public static List<Post> posts = new ArrayList<>();


    public static int IterCounter = 0;
}
