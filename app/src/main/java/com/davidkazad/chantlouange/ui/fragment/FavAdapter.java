package com.davidkazad.chantlouange.ui.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.davidkazad.chantlouange.R;
import com.davidkazad.chantlouange.models.Book;
import com.davidkazad.chantlouange.models.Page;

import java.util.List;

public class FavAdapter extends RecyclerView.Adapter<FavAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Page page, Book book);
        void onBookmarkClick(Page page, Book book);
    }

    public static class FavItem {
        public Page page;
        public Book book;
        public boolean isRecent;

        public FavItem(Page page, Book book, boolean isRecent) {
            this.page = page;
            this.book = book;
            this.isRecent = isRecent;
        }
    }

    private List<FavItem> items;
    private OnItemClickListener listener;

    public FavAdapter(List<FavItem> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public void updateData(List<FavItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fav_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FavItem item = items.get(position);
        
        holder.txtBookName.setText(item.book.getName());
        holder.txtSongTitle.setText(item.page.getTitle());
        holder.txtKeyInfo.setText("N° " + item.page.getNumber().replace(". ", ""));
        holder.txtWatermark.setText(item.page.getNumber().replaceAll("[^0-9]", ""));
        
        // Extract lyrics preview if available
        if (item.page.getContent() != null && item.page.getContent().length() > 0) {
            holder.txtLyricsPreview.setVisibility(View.VISIBLE);
            String snippet = item.page.getContent().replace("\n", " ").trim();
            if (snippet.length() > 80) {
                snippet = snippet.substring(0, 80) + "...";
            }
            holder.txtLyricsPreview.setText(snippet);
        } else {
            holder.txtLyricsPreview.setVisibility(View.GONE);
        }
        
        if (item.isRecent) {
             holder.icBookmark.setVisibility(View.GONE);
        } else {
             holder.icBookmark.setVisibility(View.VISIBLE);
             holder.icBookmark.setOnClickListener(v -> {
                 if (listener != null) listener.onBookmarkClick(item.page, item.book);
             });
        }
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item.page, item.book);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtBookName, txtSongTitle, txtLyricsPreview, txtKeyInfo, txtWatermark;
        ImageView icBookmark;

        public ViewHolder(View itemView) {
            super(itemView);
            txtBookName = itemView.findViewById(R.id.txt_book_name);
            txtSongTitle = itemView.findViewById(R.id.txt_song_title);
            txtLyricsPreview = itemView.findViewById(R.id.txt_lyrics_preview);
            txtKeyInfo = itemView.findViewById(R.id.txt_key_info);
            txtWatermark = itemView.findViewById(R.id.txt_watermark_number);
            icBookmark = itemView.findViewById(R.id.ic_bookmark);
        }
    }
}
