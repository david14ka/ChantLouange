package com.davidkazad.chantlouange.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.davidkazad.chantlouange.R;
import com.davidkazad.chantlouange.activities.SongListActivity;
import com.davidkazad.chantlouange.songs.SongsBook;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public class SongsBookFragment extends Fragment implements AdapterView.OnItemClickListener {

    @BindView(R.id.grid_book)
    GridView grid_book;

    private List<SongsBook> bookList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_books, container, false);
        ButterKnife.bind(view);
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bookList = SongsBook.getBookList();
        grid_book = view.findViewById(R.id.grid_book);

        grid_book.setOnItemClickListener(this);
        grid_book.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return bookList.size();
            }

            @Override
            public Object getItem(int position) {
                return bookList.get(position);
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

                holder.bookName.setText(bookList.get(position).getSongsBookName());
                holder.bookImage.setImageDrawable(getResources().getDrawable(bookList.get(position).getSongsBookImage()));

                return convertView;
            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        SongListActivity.songBookId = position;
        startActivity(new Intent(getContext(), SongListActivity.class));

        /*SongListFragment songListFragment = new SongListFragment();

        FragmentManager fm = getChildFragmentManager();
        fm.beginTransaction()
                .setCustomAnimations(R.anim.slide_left,
                        R.anim.slide_right,
                        R.anim.slide_left,
                        R.anim.slide_right)
                .add(R.id.container, songListFragment, "list")
                .show(songListFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss();*/
    }


    private class BookHolder {
        public ImageView bookImage;
        public TextView bookName;
    }


}
