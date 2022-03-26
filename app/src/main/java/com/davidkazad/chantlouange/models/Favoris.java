package com.davidkazad.chantlouange.models;

import android.util.Log;

import com.davidkz.eazyorm.Model;
import com.davidkz.eazyorm.annotation.Column;
import com.davidkz.eazyorm.annotation.Table;
import com.davidkz.eazyorm.query.Select;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;
import java.util.List;

import static com.davidkazad.chantlouange.config.utils.Constants.TAG;


@Table(name = "Favoris")
public class Favoris extends Model {

    @Column(name = "fid", unique = true, onUniqueConflict = Column.ConflictAction.IGNORE)
    private String fid;
    @Column(name = "songId")
    private int songId;
    @Column(name = "bookId")
    private int bookId;

    public Favoris() {

    }

    public Favoris(Page mPage) {
        fid = mPage.getBookId() + "" + mPage.getId();
        songId = mPage.getId();
        bookId = mPage.getBookId();

    }

    public Favoris(int songId, int bookId) {
        this.fid = bookId + "" + songId;
        this.songId = songId;
        this.bookId = bookId;

        Log.d(TAG, "Favoris: ");
    }

    public static List<Favoris> getList() {
        List<Favoris> favorises = new Select().from(Favoris.class).execute();

        Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.STATIC, Modifier.FINAL, Modifier.ABSTRACT).create();
        Log.d("TAG", "getList: " + gson.toJson(favorises));

        return favorises;
    }

    public static String getListGon() {
        List<Favoris> favorises = new Select().from(Favoris.class).execute();

        Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.STATIC, Modifier.FINAL, Modifier.ABSTRACT).create();
        Log.d(TAG, "getList: " + gson.toJson(favorises));

        return gson.toJson(favorises);
    }

    public void add() {
        try {
            this.save();
        }catch (Exception ex){
            Log.e(TAG, "add: ", ex);
        }

        Log.d(TAG, "addToFavoris: " + this);
    }

    public void remove() {
        this.delete();
        Log.d(TAG, "removeFromFavoris: " + this);
    }

    private Favoris get() {
        Favoris favoris = (Favoris) new Select().from(Favoris.class).where("fid = ?", this.fid).execute();
        Log.d(TAG, "getFavoris: " + favoris);
        return favoris;
    }

    public static boolean exists(int bookId, int songId) {
        boolean r = new Select().from(Favoris.class).where("songId = ? AND bookId=?", songId, bookId).count() == 1;
        return r;
    }
    public static boolean exists(Page mPage) {
        return new Select().from(Favoris.class).where("bookId=? AND songId=?", mPage.getBookId(), mPage.getId()).count() == 1;
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.STATIC, Modifier.FINAL, Modifier.ABSTRACT).create();
        return gson.toJson(this); //"[{fid:"+fid+", bookId:"+bookId+", songId:"+songId+"}]";
    }

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }
}
