package com.davidkazad.chantlouange.models;

import java.util.Date;
import java.util.List;

public class Post {

    private String pid;
    private String email;
    private String defaultId;
    private String name;
    private String photo;
    private String text;
    private String image;

    private int comments;
    private int likes;
    private String date;

    public Post() {
    }

    public Post(String post, String image) {
        User user = User.getUser();
        this.name = user.getName();
        this.photo = user.getPhotoUrl();
        this.email = user.getEmail();
        this.text = post;
        this.image = image;
        date = new Date().toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getComments() {
        return comments;
    }
    public int getLikes() {
        return likes;
    }

    public void add() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getDefaultId() {
        return defaultId;
    }

    public void setDefaultId(String defaultId) {
        this.defaultId = defaultId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
