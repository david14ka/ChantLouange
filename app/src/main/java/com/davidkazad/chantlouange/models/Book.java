package com.davidkazad.chantlouange.models;

import com.davidkazad.chantlouange.datas.CC;
import com.davidkazad.chantlouange.datas.CV;
import com.davidkazad.chantlouange.datas.NIMBO_MIPYA;
import com.davidkazad.chantlouange.datas.NIMBO_YA_BUPANDEJI;
import com.davidkazad.chantlouange.datas.NM;
import com.davidkazad.chantlouange.datas.NW;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class Book {

    private final String name;
    private final String abbreviation;
    private final int image;
    private final int id;
    public static List<Book> bookList;

    static {
        bookList = new ArrayList<>();
        bookList.add(new CC());
        bookList.add(new CV());
        bookList.add(new NM());
        bookList.add(new NW());
        bookList.add(new NIMBO_MIPYA());
        bookList.add(new NIMBO_YA_BUPANDEJI());
        //bookList.add(new Durban());
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

    abstract public List<Page> getPages();

    public List<Page> searchPage(String query) {

        if (!query.equals("")) {

            List<Page> pageList = new ArrayList<>();

            for (Page page :
                    this.getPages()) {
                String title = page.getNumber() + page.getTitle();

                String number = page.getNumber();
                String tmp0 = page.getTitle().toLowerCase();
                String tmp1 = number + " " + tmp0;
                String tmp2 = query.toLowerCase();

                if (tmp1.contains(tmp2)) {
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

    public List<Page> sort() {

        List<Page> pageList = this.getPages();

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

        //Collections.reverse(pageList);

        return pageList;
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
}
