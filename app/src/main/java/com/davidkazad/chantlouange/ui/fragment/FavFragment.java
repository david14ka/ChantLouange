package com.davidkazad.chantlouange.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.davidkazad.chantlouange.R;
import com.davidkazad.chantlouange.models.Book;
import com.davidkazad.chantlouange.models.FavoriteUtils;
import com.davidkazad.chantlouange.models.Favorites;
import com.davidkazad.chantlouange.models.Page;
import com.davidkazad.chantlouange.models.Recent;
import com.davidkazad.chantlouange.ui.activities.ItemActivity;

import java.util.ArrayList;
import java.util.List;

public class FavFragment extends Fragment {

    private RecyclerView recyclerView;
    private FavAdapter adapter;
    private TextView txtFavCount, txtFavSubtitle;
    private TextView tabFavorites, tabRecent;
    
    private boolean showingFavorites = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fav, container, false);
        
        recyclerView = view.findViewById(R.id.rv_favorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        txtFavCount = view.findViewById(R.id.txt_fav_count);
        txtFavSubtitle = view.findViewById(R.id.txt_fav_subtitle);
        tabFavorites = view.findViewById(R.id.tab_favorites);
        tabRecent = view.findViewById(R.id.tab_recent);
        
        adapter = new FavAdapter(new ArrayList<>(), new FavAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Page page, Book book) {
                ItemActivity.currentBook = book;
                ItemActivity.currentPage = page;
                startActivity(new Intent(getActivity(), ItemActivity.class));
            }

            @Override
            public void onBookmarkClick(Page page, Book book) {
                page.toggleFavorite();
                Toast.makeText(getContext(), page.isFavorite() ? R.string.fav_added : R.string.fav_removed, Toast.LENGTH_SHORT).show();
                if (showingFavorites) {
                    loadFavorites(); // Refresh list to remove it smoothly
                }
            }
        });
        recyclerView.setAdapter(adapter);
        
        View.OnClickListener tabListener = v -> {
            boolean isFav = v.getId() == R.id.tab_favorites;
            if (showingFavorites == isFav) return;
            
            showingFavorites = isFav;
            
            tabFavorites.setBackgroundResource(isFav ? R.drawable.pill_active_bg : R.drawable.pill_inactive_bg);
            tabRecent.setBackgroundResource(isFav ? R.drawable.pill_inactive_bg : R.drawable.pill_active_bg);
            
            if (isFav) {
                txtFavSubtitle.setText(R.string.fav_curated_subtitle);
                loadFavorites();
            } else {
                txtFavSubtitle.setText(R.string.recent_subtitle);
                loadRecents();
            }
        };
        
        tabFavorites.setOnClickListener(tabListener);
        tabRecent.setOnClickListener(tabListener);

        // Load initially
        loadFavorites();

        return view;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        if (showingFavorites) {
            loadFavorites();
        } else {
            loadRecents();
        }
    }

    private void loadFavorites() {
        List<Favorites> rawFavs = FavoriteUtils.loadFavorites();
        List<FavAdapter.FavItem> items = new ArrayList<>();
        
        for (Favorites fav : rawFavs) {
            try {
                // IDs are 1-indexed in old app logic
                Book b = Book.bookList.get(fav.getBookId() - 1);
                Page p = b.getPage(fav.getSongId() - 1);
                if (p.getId() >= 0) {
                    items.add(new FavAdapter.FavItem(p, b, false));
                }
            } catch (Exception e) {
                // Safe lookup
            }
        }
        
        txtFavCount.setText(String.format(getString(R.string.saved_count_label), items.size()));
        adapter.updateData(items);
    }
    
    private void loadRecents() {
        List<Recent> rawRecents = Recent.getList();
        List<FavAdapter.FavItem> items = new ArrayList<>();
        
        for (Recent rec : rawRecents) {
            try {
                Book b = Book.bookList.get(rec.getBookId() - 1);
                Page p = b.getPage(rec.getSongId() - 1);
                if (p.getId() >= 0) {
                    items.add(new FavAdapter.FavItem(p, b, true));
                }
            } catch (Exception e) {
                // Safe lookup
            }
        }
        
        txtFavCount.setText(String.format(getString(R.string.viewed_count_label), items.size()));
        adapter.updateData(items);
    }
}
