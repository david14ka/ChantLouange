package com.davidkazad.chantlouange.songs;


import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static com.davidkazad.chantlouange.fragment.SongListFragment.TAG;

/**
 * Created by davidkazad on 21/04/2018.
 */

public abstract class SongsBook {

    private final String songsBookName;
    private final int songsBookImage;
    private final int songsBookId;

    public SongsBook(int songsBookId, int songsBookImage, String songsBookName) {
        this.songsBookName = songsBookName;
        this.songsBookId = songsBookId;
        this.songsBookImage = songsBookImage;
    }

    public String getSongsBookName() {
        return songsBookName;
    }

    public abstract String[] getTitle();

    public abstract String[] getContent();

    public static String[] getSongTitles(int book) {
        switch (book) {
            case 1:
                return new CC().getTitle();
            case 2:
                return new CV().getTitle();
            case 3:
                return new NM().getTitle();
            case 4:
                return new NW().getTitle();
        }

        return new String[]{};
    }

    public static SongsBook getSongBook(int book) {

        switch (book) {
            case 0:
                return new CC();
            case 1:
                return new CV();
            case 2:
                return new NM();
            case 3:
                return new NW();
        }

        return new CC();
    }



    /**
     * Get SongsItem Contents static method
     *
     * @param bookId - The book id
     *               - {1: collection de cantiques ,
     *               2: chant de victoire, 3: Nyimbo za mungu, 4: Nyimbo za wokovu}
     * @return - The Table of contents
     */
    private static String[] getSongContents(int bookId) {
        switch (bookId) {
            case 1:
                return new CC().getContent();
            case 2:
                return new CV().getContent();
            case 3:
                return new NM().getContent();
            case 4:
                return new NW().getContent();
        }

        return new String[]{};

    }

    /**
     * get song method
     *
     * @param bookId     - the book id {1: collection de cantiques ,
     *                   2: chant de victoire, 3: Nyimbo za mungu, 4: Nyimbo za wokovu}
     * @param songNumber - the number of the song in the book
     *                   first index in table equal to 0;
     * @return - The song by bookId and songNumber or null if it  doesn't exist
     */
    public static SongsItem getSong(int bookId, int songNumber) {
        if (getSongTitles(bookId).length<songNumber){
            return null;
        }else if (bookId<1)
            return null;

        return new SongsItem(getSongBook(bookId-1).getNumber(songNumber), getSongTitles(bookId)[songNumber], getSongContents(bookId)[songNumber]);

    }


    public static List<SongsBook> getBookList(){

        List<SongsBook> songsBooks = new ArrayList<>();

        songsBooks.add(new CC());
        songsBooks.add(new CV());
        songsBooks.add(new NM());
        songsBooks.add(new NW());

        return songsBooks;
    }

    public int getSongsBookImage() {
        return songsBookImage;
    }

    public int getSongsBookId() {
        return songsBookId;
    }

    public abstract int getCount();

    public List<SongsItem> search(String query){

        if (!query.equals("")){

            List<SongsItem> songsItems = new ArrayList<>();

            for (int i = 0; i < this.getCount(); i++) {

                String number = getNumber(i);
                String tmp0 = this.getTitle()[i].toLowerCase();
                String tmp1 = number+" "+tmp0;
                String tmp2 = query.toLowerCase();

                /*Log.d(TAG, "search: "+tmp2);
                Log.d(TAG, "in : "+tmp1);*/

                if (tmp1.contains(tmp2)) {
                    songsItems.add(new SongsItem(i, number, tmp0.toUpperCase(),""));
                    //Log.d(TAG, "found : "+tmp0);
                }
            }

            return songsItems;
        }

        return this.getItems();
    }

    public abstract String getNumber(int number);

    public List<SongsItem> getItems(){

        List<SongsItem> songsItems = new ArrayList<>();
        //SongsBook songsBook = getSongBook(songsBookId);

        for (int i = 0; i < this.getCount(); i++) {

            songsItems.add(new SongsItem(i, getNumber(i).toUpperCase(),this.getTitle()[i].toUpperCase(),this.getContent()[i]));

        }

        return songsItems;
    }

    public static int cvException(CharSequence number) {

        SongsBook book = SongsBook.getSongBook(1); // CV

        for (int i = 0; i < book.getTitle().length; i++) {
            String numberEx0 = number + ". ";
            String numberEx1 = number + "a. ";
            String numberEx2 = number + "b. ";

            if (book.getNumber(i).equals(numberEx0)) {
                //System.out.println("cvException: " + numberEx0);
                return i;
            } else if (book.getNumber(i).equals(numberEx1)) {
                //System.out.println("cvException: " + numberEx1);
                return i;
            } else if (book.getNumber(i).equals(numberEx2)) {
                //System.out.println("cvException: " + numberEx2);
                return i;
            }
        }

        return Integer.valueOf(String.valueOf(number));

    }
}