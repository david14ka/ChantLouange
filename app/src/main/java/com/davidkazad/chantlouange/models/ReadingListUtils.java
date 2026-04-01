package com.davidkazad.chantlouange.models;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ReadingListUtils {

    private static final String FAVORITES_KEY = "reading_list";

    public String getId() {
        return id;
    }

    public void setId() {
        this.id = this.bookId+"_"+this.songId;
    }

    private String id;
    private int bookId;
    private int songId;
    private Page page;

    public ReadingListUtils(Page page) {

        this.page = page;
        this.songId = page.getId();
        this.bookId = page.getBookId();
        this.setId();
    }
    public ReadingListUtils(int bookId, int songId) {
        this.bookId = bookId;
        this.songId = songId;
        this.page = Book.bookList.get(bookId).getPage(songId);
        this.setId();
    }

    public void toggleReadingList() {

        JSONArray readingList = getReadingList();

        int index2 = findIndex(readingList, null);
        readingList.remove(index2);

        int index = findIndex(readingList, this.id);

        if (index != -1) {
            // Remove favorite
            readingList.remove(index);
        } else {
            // Add new favorite
            readingList.put(this.id);
        }

        Prefs.putString(FAVORITES_KEY, readingList.toString());
        Log.d(TAG, "toggleReadingList: "+readingList);
    }
    
    public void addToRecent() {
        JSONArray readingList = getReadingList();
        
        int index = findIndex(readingList, this.id);
        if (index != -1) {
            readingList.remove(index); // Remove it so we can push it to the top
        }
        
        JSONArray newList = new JSONArray();
        newList.put(this.id);
        
        for (int i = 0; i < readingList.length(); i++) {
            newList.put(readingList.optString(i));
        }
        
        // Keep only top 50 recent
        while (newList.length() > 50) {
            newList.remove(newList.length() - 1);
        }
        
        Prefs.putString(FAVORITES_KEY, newList.toString());
    }

    public boolean wasOpenedRecently() {
        JSONArray favorites = getReadingList();
        //String key = bookId + "_" + songId;
        return findIndex(favorites, id) != -1;
    }

    public static JSONArray getReadingList() {
        String saved = Prefs.getString(FAVORITES_KEY, "[]");
        try {
            return new JSONArray(saved);
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    private static int findIndex(JSONArray array, String value) {
        for (int i = 0; i < array.length(); i++) {
            if (array.optString(i).equals(value)) {
                return i;
            }
        }
        return -1;
    }

    public static List<Recent> loadRecentList() {
        ArrayList<Recent> recentArrayList = new ArrayList<>();
        JSONArray list = ReadingListUtils.getReadingList();

        Log.d(TAG, "loadReadingList: "+list);
        for (int i = 0; i < list.length(); i++) {
            String key = list.optString(i);
            String[] parts = key.split("_");
            if (parts.length == 2) {
                String bookId = parts[0];
                String songId = parts[1];

                String bookTitle = "Book " + bookId;
                String songTitle = "Song " + songId;

                Log.d(TAG, "loadReadingList: "+bookTitle);
                Log.d(TAG, "loadReadingList: "+songTitle);

                recentArrayList.add(new Recent(Integer.parseInt(songId),Integer.parseInt(bookId)));
            }
        }
        return recentArrayList;
    }
}