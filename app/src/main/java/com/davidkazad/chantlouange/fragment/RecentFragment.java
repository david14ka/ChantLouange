package com.davidkazad.chantlouange.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.davidkazad.chantlouange.R;
import com.davidkazad.chantlouange.activities.ItemActivity;
import com.davidkazad.chantlouange.models.Book;
import com.davidkazad.chantlouange.models.Favoris;
import com.davidkazad.chantlouange.models.Page;
import com.davidkazad.chantlouange.models.Recent;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecentFragment extends BaseFragment {

    public static final String TAG = RecentFragment.class.getName();
    public static final String EXTRA_BOOK_ID = "bookId";

    public static String query;

    @BindView(R.id.recycler_song)
    RecyclerView recycler_song;

    private Book bookItem;
    private List<Recent> recentList;
    private RecentAdapter adapter;

    public RecentFragment() {

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

        recycler_song = view.findViewById(R.id.recycler_song);
        recycler_song.setVisibility(View.VISIBLE);
        recycler_song.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,true));

        recentList = Recent.getList();
        adapter = new RecentAdapter();
        recycler_song.setAdapter(adapter);

    }

    private class RecentItem extends RecyclerView.ViewHolder {
        public TextView number;
        public TextView book;
        public ImageView fav;

        public RecentItem(View itemView) {
            super(itemView);
            number = itemView.findViewById(R.id.txt_number);
            fav = itemView.findViewById(R.id.fav);
            book = itemView.findViewById(R.id.txt_book);

        }
    }

    private class RecentAdapter extends RecyclerView.Adapter<RecentItem> {

        @NonNull
        @Override
        public RecentItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_recent, parent, false);

            return new RecentItem(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull RecentItem holder, int position) {

            Recent recent = recentList.get(position);
            final Book book = Book.bookList.get(recent.getBookId()-1);
            final Page mPage = book.getPage(recent.getSongId()-1);
            holder.number.setText(mPage.getNumber().replace(". ",""));
            holder.book.setText(book.getAbbreviation());

            if (Favoris.exists(mPage)){
                holder.fav.setVisibility(View.VISIBLE);
            }else {
                holder.fav.setVisibility(View.INVISIBLE);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ItemActivity.currentPage = mPage;
                    ItemActivity.currentBook = book;
                    startActivity(new Intent(getContext(), ItemActivity.class));
                }
            });

        }

        @Override
        public int getItemCount() {
            return recentList.size();
        }
    }
}
