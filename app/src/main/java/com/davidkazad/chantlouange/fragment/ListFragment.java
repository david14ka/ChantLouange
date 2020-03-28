package com.davidkazad.chantlouange.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.davidkazad.chantlouange.R;
import com.davidkazad.chantlouange.activities.ItemActivity;
import com.davidkazad.chantlouange.models.Book;
import com.davidkazad.chantlouange.models.Favoris;
import com.davidkazad.chantlouange.models.Page;
import com.davidkazad.chantlouange.utils.LogUtil;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

import static com.davidkazad.chantlouange.common.Common.PREFS_TABLE_MATIERES_ALPHABETIQUE;

public class ListFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    public static final String TAG = ListFragment.class.getName();
    public static final String EXTRA_BOOK_ID = "bookId";

    public static String query;

    @BindView(R.id.grid_song)
    GridView grid_song;

    private List<Page> pageList;
    private Book bookItem;
    private GridAdapter adapter;

    public ListFragment() {

    }

    public static ListFragment getInstance(int bookId) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putInt(EXTRA_BOOK_ID, bookId);
        fragment.setArguments(args);

        return fragment;
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

        LogUtil.d();

        grid_song = view.findViewById(R.id.grid_song);
        grid_song.setVisibility(View.VISIBLE);
        grid_song.setOnItemClickListener(this);

        if (Prefs.getBoolean(PREFS_TABLE_MATIERES_ALPHABETIQUE, false)) {
            grid_song.setNumColumns(1);
        }

        if (getArguments() != null) {

            int bookId = getArguments().getInt(EXTRA_BOOK_ID);

            bookItem = Book.bookList.get(bookId);

            if (Prefs.getBoolean(PREFS_TABLE_MATIERES_ALPHABETIQUE, false)) {

                pageList = bookItem.sort();

            } else

                pageList = bookItem.getPages();

            adapter = new GridAdapter();

            //if there is data in searchView
            if (query != null && !query.equals("")) {

                pageList = bookItem.searchPage(query);

                if (Prefs.getBoolean(PREFS_TABLE_MATIERES_ALPHABETIQUE, false)) {

                    pageList = bookItem.sort(pageList);

                }

            }

            grid_song.setAdapter(adapter);
        }
    }

    private class HolderItem {
        public TextView number;
        public TextView title;
        public ImageView fav;
        public ImageView note;
        public TextView letter;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(String query) {

        pageList = bookItem.searchPage(query);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //Toast.makeText(getContext(), pageList.get(position).getTitle(), Toast.LENGTH_SHORT).show();
        final Page item = pageList.get(position);
        if (item.getId()<0){
            return;
        }
        ItemActivity.currentPage = item;
        ItemActivity.currentBook = bookItem;
        startActivity(new Intent(getContext(), ItemActivity.class));
    }

    private class GridAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return pageList.size();
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

            Page mPage = pageList.get(position);

            if (convertView == null) {
                if (Prefs.getBoolean(PREFS_TABLE_MATIERES_ALPHABETIQUE, false)) {

                    convertView = inflater.inflate(R.layout.item_alphabetique, parent, false);
                } else {

                    convertView = inflater.inflate(R.layout.item_numerique, parent, false);
                }

                holder.number = convertView.findViewById(R.id.txt_number);
                holder.title = convertView.findViewById(R.id.txt_tittle);
                holder.fav = convertView.findViewById(R.id.fav);
                holder.note = convertView.findViewById(R.id.note);
                try {
                    holder.letter = convertView.findViewById(R.id.txt_letter);
                }catch (Exception r){

                }

                convertView.setTag(holder);

            } else {

                holder = (HolderItem) convertView.getTag();
            }


            holder.title.setText(mPage.getTitle());

            if (mPage.getId()<0){

                holder.letter.setText(mPage.getTitle());
                holder.letter.setVisibility(View.VISIBLE);

                holder.number.setVisibility(View.INVISIBLE);
                holder.note.setVisibility(View.GONE);
                holder.title.setVisibility(View.GONE);

            }else {

                holder.letter.setVisibility(View.GONE);
                holder.title.setVisibility(View.VISIBLE);
                holder.title.setGravity(Gravity.LEFT);
                holder.number.setVisibility(View.VISIBLE);
                holder.note.setVisibility(View.VISIBLE);
            }

            if (Favoris.exists(mPage)){
                holder.fav.setVisibility(View.VISIBLE);
            }else {
                holder.fav.setVisibility(View.GONE);
            }
            if (Prefs.getBoolean(PREFS_TABLE_MATIERES_ALPHABETIQUE, false)) {
                holder.number.setText(String.format(getString(R.string.txt_number), mPage.getNumber().replace(". ", "")));

            } else {
                holder.note.setVisibility(View.GONE);
                holder.number.setText(mPage.getNumber().replace(". ", ""));
            }

            //holder.number.setText(mPage.getNumber().replace(". ", ""));


            return convertView;
        }
    }
}
