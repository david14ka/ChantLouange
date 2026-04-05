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
            
            // Basic data
            sh.txtNum.setText(item.page.getNumber().replace(". ", ""));
            sh.txtTitle.setText(item.page.getTitle());
            sh.txtBook.setText(item.bookAbbr);
            
            // Click handling (on itemView for reliability)
            sh.itemView.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onItemClick(item);
                }
            });

            // Audio indicator (Red play button)
            boolean hasAudio = AudioMapper.hasAudio(sh.itemView.getContext(), item.page.getBookId(), item.page.getNumber());
            sh.note.setVisibility(hasAudio ? View.VISIBLE : View.GONE);

            // Partition indicator (Document icon)
            boolean hasPdf = com.davidkazad.chantlouange.utils.PdfMapper.hasPdf(sh.itemView.getContext(), item.page.getBookId(), item.page.getNumber());
            sh.icPartition.setVisibility(hasPdf ? View.VISIBLE : View.GONE);

            // Favorite indicator (Bookmark)
            sh.imgFav.setVisibility(item.page.isFavorite() ? View.VISIBLE : View.GONE);
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
        ImageView note;
        ImageView icPartition;
        ImageView imgFav;

        SongViewHolder(View view) {
            super(view);
            txtNum = view.findViewById(R.id.txt_song_num);
            txtTitle = view.findViewById(R.id.txt_song_title);
            txtBook = view.findViewById(R.id.txt_song_book);
            note = view.findViewById(R.id.note);
            icPartition = view.findViewById(R.id.ic_partition);
            imgFav = view.findViewById(R.id.img_fav);
        }
    }

    static class FeaturedViewHolder extends RecyclerView.ViewHolder {
        FeaturedViewHolder(View view) {
            super(view);
        }
    }
}
