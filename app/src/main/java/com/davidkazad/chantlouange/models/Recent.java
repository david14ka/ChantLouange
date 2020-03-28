package com.davidkazad.chantlouange.models;

import android.util.Log;

import com.davidkz.eazyorm.Model;
import com.davidkz.eazyorm.annotation.Column;
import com.davidkz.eazyorm.annotation.Table;
import com.davidkz.eazyorm.query.Select;

import java.util.List;

@Table(name = "Recent")
public class Recent extends Model {

    @Column(name = "fid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE, length = 5)
    private String rid;
    @Column(name = "songId")
    private int songId;
    @Column(name = "bookId")
    private int bookId;

    private static List<Recent> list;

    public Recent() {

    }

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


    public void add(){
        try {


            List<Recent> recentList = getAll();
            if (recentList.size() > 10) {
                for (int i = 10; i < getAll().size(); i++) {
                    recentList.get(i).delete();
                }
            }
        }catch (Exception ex){
            Log.e("Recent", "add: ", ex);
        }
        try {
            this.save();
        }catch (Exception e){
            Log.e("Recent", "add: ", e);
        }

    }
    public static List<Recent> getList() {

        return getAll();
    }
    private static List<Recent> getAll() {
        return new Select().all().from(Recent.class).orderBy("id DESC").execute();
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
