package com.davidkazad.chantlouange.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class Book {

    private final String name;
    private final int image;
    private final int id;

    public Book(int id, String name, int image) {
        this.name = name;
        this.image = image;
        this.id = id;
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

    public static Book getInstance(int bookId) {

        switch (bookId){
            case 1 : return new CC1();
            case 2 : return new CV1();
            case 3 : return new CC1();
            case 4 : return new CC1();
        }

        return new CC1();
    }

    public List<Page> searchPage(String query) {

        if (!query.equals("")) {

            List<Page> pageList = new ArrayList<>();

            for (Page page :
                    this.getPages()) {
                String title = page.getNumber() + page.getTitle();
                if (title.contains(query)) {
                    pageList.add(page);
                }
            }

            return pageList;
        }

        return getPages();
    }

    public List<Page> sort() {

        List<Page> pageList = this.getPages();

        Collections.sort(pageList, new Comparator<Page>() {
            @Override
            public int compare(Page tc1, Page tc2) {
                return tc1.getTitle().compareTo(tc2.getTitle());
            }
        });

        Collections.reverse(pageList);

        return pageList;
    }

    public Page getPage(int pageId){
        return getPages().get(pageId);
    }


}
