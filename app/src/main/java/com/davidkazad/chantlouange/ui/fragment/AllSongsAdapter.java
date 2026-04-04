package com.davidkazad.chantlouange.ui.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.davidkazad.chantlouange.R;
import com.davidkazad.chantlouange.utils.AudioMapper;

import java.util.List;

public class AllSongsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_SONG = 1;
    public static final int TYPE_FEATURED = 2;

    private List<AllSongsFragment.ListItem> mItems;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(AllSongsFragment.ListItem item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public AllSongsAdapter(List<AllSongsFragment.ListItem> items) {
        this.mItems = items;
    }

    public void updateData(List<AllSongsFragment.ListItem> newItems) {
        this.mItems = newItems;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song_letter_header, parent, false);
            return new HeaderViewHolder(view);
        } else if (viewType == TYPE_FEATURED) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song_featured, parent, false);
            return new FeaturedViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song_editorial, parent, false);
            return new SongViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AllSongsFragment.ListItem item = mItems.get(position);
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).txtLetter.setText(item.headerText);
        } else if (holder instanceof SongViewHolder) {
            SongViewHolder sh = (SongViewHolder) holder;
            sh.txtNum.setText(item.page.getNumber().replace(". ", ""));
            sh.txtTitle.setText(item.page.getTitle());
            sh.txtBook.setText(item.bookAbbr);
            sh.itemView.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onItemClick(item);
                }
            });

            if (AudioMapper.hasAudio(sh.itemView.getContext(), item.page.getBookId(), item.page.getNumber())) {
                sh.imgAudio.setVisibility(View.VISIBLE);
            } else {
                sh.imgAudio.setVisibility(View.GONE);
            }
        }
        // FeaturedViewHolder requires no dynamic binding for now
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView txtLetter;
        HeaderViewHolder(View view) {
            super(view);
            txtLetter = view.findViewById(R.id.txt_letter);
        }
    }

    static class SongViewHolder extends RecyclerView.ViewHolder {
        TextView txtNum;
        TextView txtTitle;
        TextView txtBook;
        ImageView imgAudio;
        SongViewHolder(View view) {
            super(view);
            txtNum = view.findViewById(R.id.txt_song_num);
            txtTitle = view.findViewById(R.id.txt_song_title);
            txtBook = view.findViewById(R.id.txt_song_book);
            imgAudio = view.findViewById(R.id.img_audio_icon);
        }
    }

    static class FeaturedViewHolder extends RecyclerView.ViewHolder {
        FeaturedViewHolder(View view) {
            super(view);
        }
    }
}
