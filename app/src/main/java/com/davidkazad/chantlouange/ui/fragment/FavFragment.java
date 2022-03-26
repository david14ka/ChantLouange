package com.davidkazad.chantlouange.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.davidkazad.chantlouange.R;
import com.davidkazad.chantlouange.ui.activities.ItemActivity;
import com.davidkazad.chantlouange.models.Book;
import com.davidkazad.chantlouange.models.Favoris;
import com.davidkazad.chantlouange.models.Page;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    public static final String TAG = FavFragment.class.getName();
    public static final String EXTRA_BOOK_ID = "bookId";

    public static String query;

    @BindView(R.id.grid_song)
    GridView grid_song;

    private Book bookItem;
    private GridAdapter adapter;
    private List<Favoris> favorisList;

    public FavFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_song_list, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(view);

        grid_song = view.findViewById(R.id.grid_song);
        grid_song.setVisibility(View.VISIBLE);
        grid_song.setNumColumns(1);
        grid_song.setOnItemClickListener(this);

        favorisList = Favoris.getList();
        adapter = new GridAdapter();
        grid_song.setAdapter(adapter);

    }

    private class HolderItem {
        public TextView number;
        public TextView title;
        public TextView subtitle;
        public ImageView fav;
        public View menu;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //Toast.makeText(getContext(), pageList.get(position).getTitle(), Toast.LENGTH_SHORT).show();
        final Favoris favoris = favorisList.get(position);
        final Book favItemBook = Book.bookList.get(favorisList.get(position).getBookId()-1);
        final Page mPage = favItemBook.getPage(favorisList.get(position).getSongId()-1);
        if (mPage.getId() < 0) {
            return;
        }
        ItemActivity.currentPage = mPage;
        ItemActivity.currentBook = favItemBook;
        startActivity(new Intent(getContext(), ItemActivity.class));
    }

    private class GridAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return favorisList.size();
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
            HolderItem holder = new HolderItem();

            final Favoris favoris = favorisList.get(position);
            final Book favItemBook = Book.bookList.get(favorisList.get(position).getBookId()-1);
            final Page mPage = favItemBook.getPage(favorisList.get(position).getSongId()-1);

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_fav, parent, false);
                holder.number = convertView.findViewById(R.id.txt_number);
                holder.title = convertView.findViewById(R.id.txt_tittle);
                holder.subtitle = convertView.findViewById(R.id.txt_subtitle);
                holder.fav = convertView.findViewById(R.id.fav);
                holder.menu = convertView.findViewById(R.id.item_menu);

                convertView.setTag(holder);

            } else {

                holder = (HolderItem) convertView.getTag();
            }


            holder.number.setText(String.format(getString(R.string.txt_number), mPage.getNumber().replace(". ", "")));
            holder.title.setText(mPage.getTitle());
            holder.subtitle.setText(favItemBook.getName());
            holder.menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(getContext(), v);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.fav_options_menu);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.menu1:
                                    //handle menu1 click
                                    ItemActivity.currentPage = mPage;
                                    ItemActivity.currentBook = favItemBook;
                                    startActivity(new Intent(getContext(), ItemActivity.class));
                                    break;
                                case R.id.menu2:
                                    //handle menu2 click
                                    favoris.remove();
                                    favorisList = Favoris.getList();
                                    adapter.notifyDataSetChanged();
                                    break;
                            }
                            return false;
                        }
                    });

                    popup.show();
                }
            });
            //holder.fav.setImageDrawable(getResources().getDrawable(R.drawable.star0));

            if (mPage.getId() < 0) {
                holder.title.setGravity(Gravity.CENTER);
                holder.number.setVisibility(View.INVISIBLE);
            } else {
                holder.title.setGravity(Gravity.LEFT);
                holder.number.setVisibility(View.VISIBLE);
            }

            return convertView;
        }
    }
}
