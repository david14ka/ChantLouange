package com.davidkazad.chantlouange.common;

import com.davidkazad.chantlouange.models.Post;
import com.davidkazad.chantlouange.models.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;
import java.util.List;

public class Common {

    public static DatabaseReference LogUtil = FirebaseDatabase.getInstance().getReference("LogUtil");
    public static DatabaseReference NOTIFICATION = FirebaseDatabase.getInstance().getReference("Notification");
    public static DatabaseReference SongsError = FirebaseDatabase.getInstance().getReference("SongsError");
    public static DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Posts");
    public static DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference("Comments");
    public static DatabaseReference TestCommentRef = FirebaseDatabase.getInstance().getReference("testComments");
    public static DatabaseReference USER_REFERENCE = FirebaseDatabase.getInstance().getReference("Users");

    public static final String PREFS_TABLE_MATIERES_ALPHABETIQUE = "numerotation";

    public static User currentUser = User.getUser();
    public static List<Post> posts = new ArrayList<>();

    public static int IterCounter = 0;
}
