package com.davidkazad.chantlouange.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.davidkazad.chantlouange.R;
import com.davidkazad.chantlouange.models.Book;
import com.davidkazad.chantlouange.models.Page;

import java.util.ArrayList;
import java.util.List;

public class GlobalSearchActivity extends BaseActivity {

    private SearchView searchView;
    private CheckBox checkSearchLyrics;
    private RecyclerView recyclerResults;
    private View emptyView;
    private TextView resultCountView;

    private SearchAdapter adapter;
    private List<SearchResult> allResults = new ArrayList<>();
    private String currentQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_search);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        searchView = findViewById(R.id.search_view);
        checkSearchLyrics = findViewById(R.id.check_search_lyrics);
        recyclerResults = findViewById(R.id.recycler_results);
        emptyView = findViewById(R.id.empty_view);
        resultCountView = findViewById(R.id.result_count);

        recyclerResults.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchAdapter();
        recyclerResults.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText);
                return true;
            }
        });

        checkSearchLyrics.setOnCheckedChangeListener((buttonView, isChecked) -> {
            performSearch(searchView.getQuery().toString());
        });

        // Auto-focus search view
        searchView.requestFocus();
    }

    private void performSearch(String query) {
        currentQuery = query == null ? "" : query.trim();
        allResults.clear();

        if (currentQuery.length() >= 2) {
            boolean searchLyrics = checkSearchLyrics.isChecked();
            
            for (Book book : Book.bookList) {
                List<Page> matches = book.searchPage(currentQuery, !searchLyrics);
                for (Page p : matches) {
                    allResults.add(new SearchResult(book, p));
                }
            }
        }

        adapter.notifyDataSetChanged();
        
        boolean hasQuery = currentQuery.length() >= 2;
        boolean hasResults = !allResults.isEmpty();

        if (hasQuery) {
            resultCountView.setVisibility(View.VISIBLE);
            resultCountView.setText(getString(R.string.result_count, allResults.size()));
        } else {
            resultCountView.setVisibility(View.GONE);
        }

        emptyView.setVisibility(hasQuery && !hasResults ? View.VISIBLE : View.GONE);
        recyclerResults.setVisibility(hasResults ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // --- Adapter ---

    private class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_search_result, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            SearchResult result = allResults.get(position);
            Page page = result.page;
            Book book = result.book;

            holder.txtNumber.setText(page.getNumber().replace(". ", ""));
            highlightText(holder.txtTitle, page.getTitle(), currentQuery);
            holder.txtSubtitle.setText(book.getName());
            holder.imgFav.setVisibility(page.isFavorite() ? View.VISIBLE : View.GONE);

            holder.itemView.setOnClickListener(v -> {
                ItemActivity.currentBook = book;
                ItemActivity.currentPage = page;
                startActivity(new Intent(GlobalSearchActivity.this, ItemActivity.class));
            });
        }

        @Override
        public int getItemCount() {
            return allResults.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView txtNumber;
            TextView txtTitle;
            TextView txtSubtitle;
            ImageView imgFav;

            ViewHolder(View itemView) {
                super(itemView);
                txtNumber = itemView.findViewById(R.id.txt_number);
                txtTitle = itemView.findViewById(R.id.txt_title);
                txtSubtitle = itemView.findViewById(R.id.txt_subtitle);
                imgFav = itemView.findViewById(R.id.img_fav);
            }
        }
        
        private void highlightText(TextView tv, String text, String query) {
            if (query == null || query.isEmpty()) {
                tv.setText(text);
                return;
            }
            int start = text.toLowerCase().indexOf(query.toLowerCase());
            if (start >= 0) {
                SpannableString spannable = new SpannableString(text);
                spannable.setSpan(
                        new BackgroundColorSpan(Color.parseColor("#55FFD700")),
                        start,
                        start + query.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv.setText(spannable);
            } else {
                tv.setText(text);
            }
        }
    }

    // --- POJO ---

    private static class SearchResult {
        final Book book;
        final Page page;

        SearchResult(Book book, Page page) {
            this.book = book;
            this.page = page;
        }
    }
}
