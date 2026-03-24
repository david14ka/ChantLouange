package com.davidkazad.chantlouange.models;


import java.util.Date;
import java.util.List;

public class Comment {

    private String date;
    private String cid;
    private String defaultId;
    private String photo;
    private String name;
    private String text;
    private String image;
    private long sentAt;

    public Comment() {
        this.date = new Date().toString();
        this.sentAt = System.currentTimeMillis();
    }

    public Comment(String post, String image) {
        User user = User.getUser();
        this.name = user.getName();
        this.defaultId = user.getDefaultId();
        this.text = post;
        this.image = image;
        this.sentAt = System.currentTimeMillis();
        this.date = new Date().toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void add() {

    }

    public String getDefaultId() {
        return defaultId;
    }

    public void setDefaultId(String defaultId) {
        this.defaultId = defaultId;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public long getSentAt() {
        return sentAt;
    }

    public void setSentAt(long sentAt) {
        this.sentAt = sentAt;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
