package com.davidkazad.chantlouange.songs;

import com.davidkazad.chantlouange.models.Book;
import com.davidkazad.chantlouange.models.Page;

import java.util.List;

public class Test {

    public static void main(String[] args){

        //System.out.println(Book.bookList.size());

        Book bookItem = Book.bookList.get(1);
        List<Page> pageList = bookItem.find("31");
        //System.out.println(bookItem.count());

        System.out.println(pageList.size());

        for (Page page:pageList){
            System.out.println(page.getNumber());
        }
        //System.out.println(pageList.size());

    }
}
