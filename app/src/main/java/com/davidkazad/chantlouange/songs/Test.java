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

        /***
         * 22A
         * 71A
         * 73A
         * 80A
         * 83A
         * 84A
         * 90A
         * 97A
         * 130A
         * 132A
         * 136A
         * 156A
         * 157 NOT EXIST
         * 160A
         * 162B
         * 191A
         * 229A
         * 256A
         * 258B
         * 296A
         * 329A
         * 337B
         * 339A
         * 342A
         *
          ***/

    }
}
