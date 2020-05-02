package com.davidkazad.chantlouange.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.davidkazad.chantlouange.R;
import com.davidkazad.chantlouange.activities.ListActivity;
import com.davidkazad.chantlouange.models.Book;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookFragment extends Fragment implements AdapterView.OnItemClickListener {

    @BindView(R.id.grid_book)
    GridView grid_book;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_books, container, false);
        ButterKnife.bind(view);
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        grid_book = view.findViewById(R.id.grid_book);

        grid_book.setOnItemClickListener(this);
        grid_book.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return Book.bookList.size();
            }

            @Override
            public Object getItem(int position) {
                return Book.bookList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = LayoutInflater.from(getContext());

                BookHolder holder = new BookHolder();

                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.item_book, parent, false);
                    holder.bookName = convertView.findViewById(R.id.book_name);
                    holder.bookImage = convertView.findViewById(R.id.book_image);

                    convertView.setTag(holder);

                } else {

                    holder = (BookHolder) convertView.getTag();
                }


                holder.bookName.setText(Book.bookList.get(position).getName());
                holder.bookImage.setImageDrawable(getResources().getDrawable(Book.bookList.get(position).getImage()));


                return convertView;
            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        ListActivity.bookItem = Book.bookList.get(position);
        startActivity(new Intent(getContext(), ListActivity.class));

    }


    private class BookHolder {
        public ImageView bookImage;
        public TextView bookName;
    }

}
