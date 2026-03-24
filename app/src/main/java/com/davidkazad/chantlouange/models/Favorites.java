package com.davidkazad.chantlouange.models;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;
import java.util.List;

import static com.davidkazad.chantlouange.config.utils.Constants.TAG;


public class Favorites {
    private int songId;
    private int bookId;

    private String fid;

    public Favorites() {

    }

    public Favorites(Page mPage) {
        fid = mPage.getBookId() + "_" + mPage.getId();
        songId = mPage.getId();
        bookId = mPage.getBookId();

    }

    public Favorites(int songId, int bookId) {
        this.fid = bookId + "_" + songId;
        this.songId = songId;
        this.bookId = bookId;
    }

    public static List<Favorites> getList() {
        return FavoriteUtils.loadFavorites();
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
