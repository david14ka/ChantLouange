package com.davidkazad.chantlouange.models;

import com.davidkz.eazyorm.Model;
import com.davidkz.eazyorm.annotation.Column;
import com.davidkz.eazyorm.annotation.Table;
import com.davidkz.eazyorm.query.Select;

import java.util.Date;
import java.util.List;

@Table(name = "Post")
public class Post extends Model{

    @Column(name = "pid", unique = true, onUniqueConflict = Column.ConflictAction.ROLLBACK)
    private String pid;
    @Column(name = "email")
    private String email;
    @Column(name = "defaultId")
    private String defaultId;
    @Column(name = "name")
    private String name;
    @Column(name = "photo")
    private String photo;
    @Column(name = "text")
    private String text;
    @Column(name = "image")
    private String image;

    @Column(name = "comments")
    private int comments;
    @Column(name = "likes")
    private int likes;
    @Column(name = "date")
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


    public static List<Post> getList(){
        return new Select().from(Post.class).orderBy("id DESC").execute();
    }

    public void add() {
        try {

            this.save();

        }catch (Exception ex){

        }
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
