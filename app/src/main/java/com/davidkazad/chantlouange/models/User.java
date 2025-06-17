package com.davidkazad.chantlouange.models;

import com.davidkz.eazyorm.Model;
import com.davidkz.eazyorm.annotation.Column;
import com.davidkz.eazyorm.annotation.Table;
import com.davidkz.eazyorm.query.Select;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.UUID;

@Table(name = "User")
public class User extends Model {

    @Column(name = "uid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private String uid = "unique";
    @Column(name = "email")
    private String email;
    @Column(name = "name")
    private String name;
    @Column(name = "photoUrl")
    private String photoUrl;
    @Column(name = "defaultId")
    private String defaultId;

    public User() {

    }

    public User(String email, String name) {
        this.email = email;
        this.name = name;
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {

            this.defaultId = firebaseUser.getUid();

        } else this.defaultId = UUID.randomUUID().toString();
    }

    public static Boolean exists() {

        return new Select().from(User.class).count() > 0;
    }

    public static User getUser() {
        if (exists()) {
            return (User) new Select().from(User.class).execute().get(0);
        } else return new User("@no-user","no-name");
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

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
