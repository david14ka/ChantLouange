package com.davidkazad.chantlouange.songs;

import com.davidkazad.chantlouange.models.Book;
import com.davidkazad.chantlouange.models.CV1;
import com.davidkazad.chantlouange.models.Page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Test{

    public static void main(String[] args) {


        trie();

    }

    public static List<SongsItem> getItems(){

        List<SongsItem> songsItems = new ArrayList<>();

        //SongsBook songsBook = getSongBook(songsBookId);
        CV nm = new CV();

        for (int i = 0; i < nm.getContent().length; i++) {

            //songsItems.add(new SongsItem(i, getNumber(i).toUpperCase(),this.getTitle()[i].toUpperCase(),this.getContent()[i]));
            String tt = nm.getContent()[i].replace("\n","#n");
            songsItems.add(new SongsItem());
            System.out.println("songsItems.add(new Page("+
                    (i+1)+", \""+
                    nm.getNumber(i).toUpperCase()+"\",\""+
                    nm.getTitle()[i].toUpperCase()+"\",\""+
                    tt+"\","+
                    nm.getSongsBookId()+"));");
        }

        return songsItems;
    }

    public static void trie(){

        Book cv1 = new CV1();

        List<Page> pageList = cv1.getPages();

        Collections.sort(pageList, new Comparator<Page>() {
            @Override
            public int compare(Page tc1, Page tc2) {
                return tc1.getTitle().compareTo(tc2.getTitle());
            }
        });

        Collections.reverse(pageList);

        for (Page p :
                pageList) {

            System.out.println(p.getId()+" "+p.getTitle());
        }
    }
}
