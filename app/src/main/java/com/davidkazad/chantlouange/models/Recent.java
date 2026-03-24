package com.davidkazad.chantlouange.models;

import java.util.ArrayList;
import java.util.List;

public class Recent  {

    private String rid;
    private int songId;
    private int bookId;

    private static List<Recent> list;

    public Recent(int songId, int bookId) {
        this.rid = bookId+""+songId;
        this.songId = songId;
        this.bookId =bookId;
    }

    public Recent(Page mPage){
        rid = mPage.getBookId()+""+mPage.getId();
        songId = mPage.getId();
        bookId = mPage.getBookId();
    }


    public static List<Recent> getList() {
        return ReadingListUtils.loadRecentList();
    }

    private static List<Recent> getAll() {
        return new ArrayList<>();
    }

    public String getRid() {
        return rid;
    }

    public int getSongId() {
        return songId;
    }

    public int getBookId() {
        return bookId;
    }
}
