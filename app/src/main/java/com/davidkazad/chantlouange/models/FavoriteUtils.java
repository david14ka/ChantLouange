package com.davidkazad.chantlouange.models;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class FavoriteUtils {

    private static final String FAVORITES_KEY = "favorites_list";

    public String getFavId() {
        return favId;
    }

    public void setFavId() {
        this.favId = this.bookId+"_"+this.songId;
    }

    private String favId;
    private int bookId;
    private int songId;
    private Page page;

    public FavoriteUtils(Page page) {

        this.page = page;
        this.songId = page.getId();
        this.bookId = page.getBookId();
        this.setFavId();
    }
    public FavoriteUtils(int bookId, int songId) {
        this.bookId = bookId;
        this.songId = songId;
        this.page = Book.bookList.get(bookId).getPage(songId);
        this.setFavId();
    }

    public void toggleFavorite() {

        JSONArray favorites = getFavorites();

        int index2 = findIndex(favorites, null);
        favorites.remove(index2);
        // Check if already favorited
        int index = findIndex(favorites, this.favId);

        if (index != -1) {
            // Remove favorite
            favorites.remove(index);
        } else {
            // Add new favorite
            favorites.put(this.favId);
        }

        Prefs.putString(FAVORITES_KEY, favorites.toString());
        Log.d(TAG, "toggleFavorite: "+favorites);
    }

    public boolean isFavorite() {
        JSONArray favorites = getFavorites();
        //String key = bookId + "_" + songId;
        return findIndex(favorites, favId) != -1;
    }

    public static JSONArray getFavorites() {
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

    public static List<Favorites> loadFavorites() {
        ArrayList<Favorites> favoritesList = new ArrayList<>();
        JSONArray favs = FavoriteUtils.getFavorites();

        Log.d(TAG, "loadFavorites: "+favs);
        for (int i = 0; i < favs.length(); i++) {
            String key = favs.optString(i);
            String[] parts = key.split("_");
            if (parts.length == 2) {
                String bookId = parts[0];
                String songId = parts[1];

                String bookTitle = "Book " + bookId;
                String songTitle = "Song " + songId;

                Log.d(TAG, "loadFavorites: "+bookTitle);
                Log.d(TAG, "loadFavorites: "+songTitle);

                favoritesList.add(new Favorites(Integer.parseInt(songId),Integer.parseInt(bookId)));
            }
        }
        return favoritesList;
    }
}