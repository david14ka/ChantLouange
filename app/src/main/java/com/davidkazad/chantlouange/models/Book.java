package com.davidkazad.chantlouange.models;

import com.davidkazad.chantlouange.datas.BB;
import com.davidkazad.chantlouange.datas.CC;
import com.davidkazad.chantlouange.datas.CS;
import com.davidkazad.chantlouange.datas.CV;
import com.davidkazad.chantlouange.datas.NP;
import com.davidkazad.chantlouange.datas.NB;
import com.davidkazad.chantlouange.datas.NM;
import com.davidkazad.chantlouange.datas.NW;
import com.davidkazad.chantlouange.datas.OB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class Book {

    private final String name;
    private final String abbreviation;
    private final int image;
    private final int id;

    // Lazy cache — built once on first access, never again.
    private List<Page> cachedPages = null;

    protected boolean bookComingSoon = false;
    public static List<Book> bookList;

    static {
        bookList = new ArrayList<>();
        bookList.add(new CC());
        bookList.add(new CV());
        bookList.add(new NM());
        bookList.add(new NW());
        bookList.add(new NP());
        bookList.add(new NB());
        bookList.add(new OB());
        bookList.add(new CS());
        bookList.add(new BB());
        //bookList.add(new Durban());
    }

    public Book(){
        this.name = "name";
        this.abbreviation = "abbreviation";
        this.image = 0;
        this.id = 0;
    }

    public Book(int id, String name, String abbreviation, int image) {
        this.name = name;
        this.abbreviation = abbreviation;
        this.image = image;
        this.id = id;
    }

    public static List<Book> getAll() {

        return bookList;
    }

    public String getName() {
        return name;
    }

    public int getImage() {
        return image;
    }

    public int getId() {
        return id;
    }

    /** Subclasses implement this to supply their raw page list. Called at most once. */
    protected abstract List<Page> buildPages();

    /**
     * Returns the cached page list, building it on the first call.
     * This avoids rebuilding thousands of Page objects every time the list is needed.
     */
    public final List<Page> getPages() {
        if (cachedPages == null) {
            cachedPages = buildPages();
        }
        return cachedPages;
    }

    public List<Page> searchPage(String query) {
        return searchPage(query, false);
    }

    public List<Page> searchPage(String query, boolean titleOnly) {

        if (!query.equals("")) {

            List<Page> pageList = new ArrayList<>();
            String tmp2 = query.toLowerCase();

            for (Page page : this.getPages()) {
                String number    = page.getNumber();
                String titleLow  = page.getTitle().toLowerCase();
                String searchStr = number + " " + titleLow;

                if (!titleOnly && page.getContent() != null) {
                    searchStr += " " + page.getContent().toLowerCase();
                }

                if (searchStr.contains(tmp2)) {
                    pageList.add(page);
                }
            }

            return pageList;
        }

        return getPages();
    }

    public List<Page> find(String query) {

        if (!query.equals("")) {

            List<Page> pageList = new ArrayList<>();

            for (Page page :
                    this.getPages()) {

                if (page.getNumber().equalsIgnoreCase(query + ". ")) {
                    pageList.add(page);
                } else if (page.getNumber().equalsIgnoreCase(query + "b. ")) {
                    pageList.add(page);
                } else if (page.getNumber().equalsIgnoreCase(query + "a. ")) {
                    pageList.add(page);
                }
            }

            return pageList;
        }

        return getPages();
    }

    // Cache for alphabet sorted pages
    private List<Page> cachedSortedPages = null;

    public List<Page> sort() {
        if (cachedSortedPages != null) {
            return cachedSortedPages;
        }

        // Must work on a copy to avoid modifying the original cachedPages!
        List<Page> pageList = new ArrayList<>(this.getPages());

        String[] letters = new String[]{
                "A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"
        };

        for (int j = 0; j < letters.length; j++) {
            pageList.add(new Page(-1*(j+1), "",letters[j],"",getId()));
        }

        Collections.sort(pageList, new Comparator<Page>() {
            @Override
            public int compare(Page tc1, Page tc2) {
                return tc1.getTitle().compareTo(tc2.getTitle());
            }
        });

        cachedSortedPages = pageList;
        return cachedSortedPages;
    }

    public List<Page> sort(List<Page> pageList) {

        Collections.sort(pageList, new Comparator<Page>() {
            @Override
            public int compare(Page tc1, Page tc2) {
                return tc1.getTitle().compareTo(tc2.getTitle());
            }
        });

        //Collections.reverse(pageList);

       /* String[] letters = new String[]{
                "A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"
        };
        boolean[] isletters = new boolean[]{
                false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false
        };


        for (Page page : pageList) {

            for (int j = 0; j < letters.length; j++) {

                if (page.getTitle().startsWith(letters[j])){

                }

            }

        }*/

        return pageList;
    }

    public Page getPage(int pageId) {
        if (count() > pageId)
            return getPages().get(pageId);
        return null;
    }


    public int count() {
        return getPages().size();
    }

    public String[] numbers() {
        String[] data = new String[count()];
        int i = 0;
        for (Page pa :
                getPages()) {
            data[i++] = pa.getNumber().replace(". ","");
        }

        return data;
    }

    public String getAbbreviation() {
        return abbreviation;
    }
    public boolean isBookComingSoon(){
        return bookComingSoon;
    }

}
