package com.davidkazad.chantlouange.models;

import com.davidkz.eazyorm.Model;
import com.davidkz.eazyorm.annotation.Column;
import com.davidkz.eazyorm.annotation.Table;
import com.davidkz.eazyorm.query.Select;

import java.util.Date;
import java.util.List;

@Table(name = "Comment")
public class Comment extends Model{

    private String date;
    @Column(name = "cid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private String cid;
    @Column(name = "defaultId")
    private String defaultId;
    @Column(name = "photo")
    private String photo;
    @Column(name = "name")
    private String name;
    @Column(name = "text")
    private String text;
    @Column(name = "image")
    private String image;
    @Column(name = "sentAt")
    private long sentAt;

    public Comment() {
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

    public static List<Comment> getList(){
        return new Select().from(Comment.class).orderBy("id DESC").execute();
    }

    public void add() {
        try {

            this.save();

        }catch (Exception ex){

        }
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
