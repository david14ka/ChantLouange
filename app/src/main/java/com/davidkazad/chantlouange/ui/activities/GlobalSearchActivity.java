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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.davidkazad.chantlouange.R;
import com.davidkazad.chantlouange.models.Book;
import com.davidkazad.chantlouange.models.Page;

import java.util.ArrayList;
import java.util.List;

public class GlobalSearchActivity extends BaseActivity {

    private SearchView searchView;
    private RecyclerView recyclerResults;
    private View emptyView;
    private ImageView btnBack;

    private TextView chipAll, chipTitles, chipNumbers, chipLyrics;

    private SearchAdapter adapter;
    private List<ListItem> allItems = new ArrayList<>();
    private String currentQuery = "";

    // 0 = All, 1 = Titles, 2 = Numbers, 3 = Lyrics
    private int currentFilter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_search);

        searchView = findViewById(R.id.search_view);
        recyclerResults = findViewById(R.id.recycler_results);
        emptyView = findViewById(R.id.empty_view);
        btnBack = findViewById(R.id.btn_back);

        chipAll = findViewById(R.id.chip_all);
        chipTitles = findViewById(R.id.chip_titles);
        chipNumbers = findViewById(R.id.chip_numbers);
        chipLyrics = findViewById(R.id.chip_lyrics);

        btnBack.setOnClickListener(v -> finish());

        setupChips();

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

        searchView.requestFocus();
    }

    private void setupChips() {
        View.OnClickListener listener = v -> {
            if (v == chipAll) currentFilter = 0;
            else if (v == chipTitles) currentFilter = 1;
            else if (v == chipNumbers) currentFilter = 2;
            else if (v == chipLyrics) currentFilter = 3;

            updateChipUI();
            performSearch(searchView.getQuery().toString());
        };

        chipAll.setOnClickListener(listener);
        chipTitles.setOnClickListener(listener);
        chipNumbers.setOnClickListener(listener);
        chipLyrics.setOnClickListener(listener);
    }

    private void updateChipUI() {
        TextView[] chips = {chipAll, chipTitles, chipNumbers, chipLyrics};
        for (int i = 0; i < chips.length; i++) {
            if (i == currentFilter) {
                chips[i].setBackgroundResource(R.drawable.bg_search_chip_selected);
                chips[i].setTextColor(Color.WHITE);
            } else {
                chips[i].setBackgroundResource(R.drawable.bg_search_chip_unselected);
                chips[i].setTextColor(getResources().getColor(R.color.home_text_body));
            }
        }
    }

    private void performSearch(String query) {
        currentQuery = query == null ? "" : query.trim();
        allItems.clear();

        if (currentQuery.length() >= 2) {
            for (Book book : Book.bookList) {
                List<Page> bookMatches = new ArrayList<>();
                
                // Fetch matches based on current filter
                // (Note: The existing searchPage method only searched titles/content.
                // We'll mimic the logic per the filter required).
                
                for (Page page : book.getPages()) {
                    boolean matches = false;
                    String title = page.getTitle() != null ? page.getTitle().toLowerCase() : "";
                    String content = page.getContent() != null ? page.getContent().toLowerCase() : "";
                    String number = page.getNumber() != null ? page.getNumber().toLowerCase() : "";
                    String q = currentQuery.toLowerCase();

                    switch (currentFilter) {
                        case 0: // Tout
                            matches = title.contains(q) || number.contains(q) || content.contains(q);
                            break;
                        case 1: // Titres
                            matches = title.contains(q);
                            break;
                        case 2: // Numéros
                            matches = number.contains(q);
                            break;
                        case 3: // Paroles
                            matches = content.contains(q);
                            break;
                    }

                    if (matches) {
                        bookMatches.add(page);
                    }
                }

                if (!bookMatches.isEmpty()) {
                    // Add header
                    allItems.add(new HeaderItem(book.getName(), bookMatches.size()));
                    
                    // Add items
                    for (Page p : bookMatches) {
                        allItems.add(new SearchResult(book, p));
                    }
                }
            }
        }

        adapter.notifyDataSetChanged();
        
        boolean hasQuery = currentQuery.length() >= 2;
        boolean hasResults = !allItems.isEmpty();

        emptyView.setVisibility(hasQuery && !hasResults ? View.VISIBLE : View.GONE);
        recyclerResults.setVisibility(hasResults ? View.VISIBLE : View.GONE);
    }

    // --- Adapter ---

    private class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int TYPE_HEADER = 0;
        private static final int TYPE_ITEM = 1;

        @Override
        public int getItemViewType(int position) {
            ListItem item = allItems.get(position);
            if (item instanceof HeaderItem) return TYPE_HEADER;
            return TYPE_ITEM;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == TYPE_HEADER) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_search_header, parent, false);
                return new HeaderViewHolder(view);
            } else {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_search_result, parent, false);
                return new ItemViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ListItem listItem = allItems.get(position);
            
            if (getItemViewType(position) == TYPE_HEADER) {
                HeaderItem header = (HeaderItem) listItem;
                HeaderViewHolder hvh = (HeaderViewHolder) holder;
                hvh.txtTitle.setText(header.bookName);
                if (header.count == 1) {
                    hvh.txtCount.setText("1 RÉSULTAT");
                } else {
                    hvh.txtCount.setText(header.count + " RÉSULTATS");
                }
            } else {
                SearchResult result = (SearchResult) listItem;
                ItemViewHolder ivh = (ItemViewHolder) holder;
                Page page = result.page;
                Book book = result.book;

                String num = page.getNumber().replace(". ", "");
                // Clean up string like "042" if it's "42." or "42" (Optional, depends on dataset)
                ivh.txtNumber.setText(num);
                highlightText(ivh.txtTitle, page.getTitle(), currentQuery);
                ivh.txtSubtitle.setText(book.getName());
                ivh.imgFav.setVisibility(page.isFavorite() ? View.VISIBLE : View.GONE);

                ivh.itemView.setOnClickListener(v -> {
                    ItemActivity.currentBook = book;
                    ItemActivity.currentPage = page;
                    startActivity(new Intent(GlobalSearchActivity.this, ItemActivity.class));
                });
            }
        }

        @Override
        public int getItemCount() {
            return allItems.size();
        }

        class HeaderViewHolder extends RecyclerView.ViewHolder {
            TextView txtTitle;
            TextView txtCount;

            HeaderViewHolder(View itemView) {
                super(itemView);
                txtTitle = itemView.findViewById(R.id.txt_header_title);
                txtCount = itemView.findViewById(R.id.txt_header_count);
            }
        }

        class ItemViewHolder extends RecyclerView.ViewHolder {
            TextView txtNumber;
            TextView txtTitle;
            TextView txtSubtitle;
            ImageView imgFav;

            ItemViewHolder(View itemView) {
                super(itemView);
                txtNumber = itemView.findViewById(R.id.txt_number);
                txtTitle = itemView.findViewById(R.id.txt_title);
                txtSubtitle = itemView.findViewById(R.id.txt_subtitle);
                imgFav = itemView.findViewById(R.id.img_fav);
            }
        }
        
        private void highlightText(TextView tv, String text, String query) {
            // Highlighting titles only works gracefully if the filter is Titles or All, 
            // but we can apply it universally as before.
            if (query == null || query.isEmpty() || text == null) {
                tv.setText(text);
                return;
            }
            int start = text.toLowerCase().indexOf(query.toLowerCase());
            if (start >= 0) {
                SpannableString spannable = new SpannableString(text);
                spannable.setSpan(
                        new BackgroundColorSpan(Color.parseColor("#44FFD700")),
                        start,
                        start + query.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.setSpan(
                        new android.text.style.ForegroundColorSpan(Color.WHITE),
                        start,
                        start + query.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv.setText(spannable);
            } else {
                tv.setText(text);
            }
        }
    }

    // --- POJOs ---

    private interface ListItem {}

    private static class HeaderItem implements ListItem {
        final String bookName;
        final int count;

        HeaderItem(String bookName, int count) {
            this.bookName = bookName;
            this.count = count;
        }
    }

    private static class SearchResult implements ListItem {
        final Book book;
        final Page page;

        SearchResult(Book book, Page page) {
            this.book = book;
            this.page = page;
        }
    }
}
