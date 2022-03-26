package com.davidkazad.chantlouange.datas;

import com.davidkazad.chantlouange.R;
import com.davidkazad.chantlouange.models.Book;
import com.davidkazad.chantlouange.models.Page;

import java.util.List;

public class Durban extends Book {


    public Durban() {
        super(5, "Christian Assembly of Durban", "Durban", R.drawable.bground);
    }


    @Override
    public List<Page> getPages() {
        return null;
    }

    private void buildTextItem(String valueOf) {

    }
}
