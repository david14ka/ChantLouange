package com.davidkazad.chantlouange.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.davidkazad.chantlouange.R;
import com.davidkazad.chantlouange.activities.SlideDetails;
import com.davidkazad.chantlouange.models.Favoris;
import com.davidkazad.chantlouange.songs.SongsBook;
import com.davidkazad.chantlouange.songs.SongsItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public class SongListFragment extends BaseFragment {

    public static final String TAG = SongListFragment.class.getName();
    public static String query;
    private SongsBook songsBook;
    private int bookId;

    @BindView(R.id.recycler_song)
    RecyclerView recycler_song;
    RecyclerView.LayoutManager layoutManager;
    private List<SongsItem> songsBookItems;
    private SongAdapter songAdapter;

    public SongListFragment() {


    }

    public static SongListFragment getInstance(int bookId){
        SongListFragment fragment = new SongListFragment();
        Bundle args = new Bundle();
        args.putInt("bookId",bookId);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_song_list,container,false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(view);

        recycler_song = view.findViewById(R.id.recycler_song);
        layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL, false);
        recycler_song.setLayoutManager(layoutManager);
        recycler_song.setItemAnimator(new DefaultItemAnimator());
        recycler_song.setHasFixedSize(true);


        if (getArguments() != null) {

            bookId = getArguments().getInt("bookId");
            songsBook = SongsBook.getSongBook(bookId);

            songsBookItems = songsBook.getItems();

            songAdapter = new SongAdapter();

            //if there is data in searchView
            if (query!=null && !query.equals("")){

                songsBookItems = songsBook.search(query);

            }

            recycler_song.setAdapter(songAdapter);
        }




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

    public void onEvent(String query){

        Log.d(TAG, "OnEvent: "+query);
        songsBookItems = songsBook.search(query);

        songAdapter.notifyDataSetChanged();

    }

    private class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongHolder> {

        @NonNull
        @Override
        public SongHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = inflater.inflate(R.layout.item_song,parent,false);
            return new SongHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SongHolder holder, final int position) {
            final SongsItem songsItem = songsBookItems.get(position);
            holder.txtNumber.setText(songsItem.getNumber());
            holder.txtTitle.setText(songsItem.getTitle());

            if (Favoris.isAdded(songsBook.getSongsBookId(),songsItem.getId())) {
                holder.iFav.setImageDrawable(getResources().getDrawable(R.drawable.star0));
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // getNumber - 1 because the number begins by 1 and the the data table index start by 0
                    SlideDetails.songId = songsItem.getId();
                    SlideDetails.bookId = songsBook.getSongsBookId();
                    startActivity(new Intent(getContext(), SlideDetails.class));
                }
            });
        }

        @Override
        public int getItemCount() {
            return songsBookItems.size();
        }

        class SongHolder extends RecyclerView.ViewHolder {

            public TextView txtNumber;
            public TextView txtTitle;
            public ImageView iFav;

            SongHolder(View itemView) {
                super(itemView);
                txtNumber = itemView.findViewById(R.id.txt_number);
                txtTitle = itemView.findViewById(R.id.txt_tittle);
                iFav = itemView.findViewById(R.id.item_menu);
            }
        }
    }

}
