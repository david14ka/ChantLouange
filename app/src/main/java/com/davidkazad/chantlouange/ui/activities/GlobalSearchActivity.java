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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.davidkazad.chantlouange.R;
import com.davidkazad.chantlouange.models.Book;
import com.davidkazad.chantlouange.models.Page;
import com.davidkazad.chantlouange.utils.SearchUtils;



import java.util.ArrayList;
import java.util.List;

public class GlobalSearchActivity extends BaseActivity {

    private android.widget.EditText searchViewInner;
    private android.widget.ImageView btnSearchSubmit;
    private RecyclerView recyclerResults;
    private View emptyView;
    private android.widget.ProgressBar searchProgress;
    private Thread searchThread;
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

        searchViewInner = findViewById(R.id.search_view_inner);
        btnSearchSubmit = findViewById(R.id.btn_search_submit);
        recyclerResults = findViewById(R.id.recycler_results);
        emptyView = findViewById(R.id.empty_view);
        searchProgress = findViewById(R.id.search_progress);
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

        // Removed btnSearchSubmit listener since search is now automatic


        searchViewInner.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                performSearch(searchViewInner.getText().toString());
                android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) 
                        getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchViewInner.getWindowToken(), 0);
                return true;
            }
            return false;
        });

        searchViewInner.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(android.text.Editable s) {
                String query = s.toString();
                performSearch(query);
            }

        });

        searchViewInner.requestFocus();
    }


    private void setupChips() {
        View.OnClickListener listener = v -> {
            if (v == chipAll) currentFilter = 0;
            else if (v == chipTitles) currentFilter = 1;
            else if (v == chipNumbers) currentFilter = 2;
            else if (v == chipLyrics) currentFilter = 3;

            updateChipUI();
            performSearch(searchViewInner.getText().toString());
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
        final String q = currentQuery.toLowerCase();
        
        // Cancel existing search thread
        if (searchThread != null && searchThread.isAlive()) {
            searchThread.interrupt();
        }

        if (q.length() < 2) {
            allItems.clear();
            adapter.notifyDataSetChanged();
            emptyView.setVisibility(View.GONE);
            recyclerResults.setVisibility(View.GONE);
            searchProgress.setVisibility(View.GONE);
            return;
        }

        searchProgress.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);

        searchThread = new Thread(new Runnable() {
            @Override
            public void run() {
                List<SearchResultScore> allScores = new ArrayList<>();

                for (Book book : Book.bookList) {
                    if (Thread.interrupted()) return;
                    for (Page page : book.getPages()) {
                        if (Thread.interrupted()) return;
                        
                        boolean numericMode = SearchUtils.isNumeric(q);
                        double score = 0.0;

                        if (numericMode) {
                            // FAST TRACK: Numeric only
                            if (page.getNormalizedNumber().contains(q)) {
                                score = page.getNormalizedNumber().equals(q) ? 1.0 : 0.9;
                            }
                        } else {
                            // Phase-based search
                            switch (currentFilter) {
                                case 0: // Tout
                                {
                                    double ts = SearchUtils.getFuzzyScore(q, page.getTitle());
                                    double ns = SearchUtils.getFuzzyScore(q, page.getNumber());
                                    double cs = 0.0;
                                    
                                    // Index content search - prioritization: Title/Number > Content
                                    if (page.getNormalizedContent().contains(q)) {
                                        cs = 0.6; // Lower base score for lyrics in "Tout" mode
                                    }
                                    
                                    // Title * 2.0, Number * 2.0, Content * 0.5 to ensure Titles always win.
                                    score = Math.max(Math.max(ts * 2.0, ns * 2.0), cs * 0.5);

                                    break;
                                }
                                case 1: // Titres
                                    score = SearchUtils.getFuzzyScore(q, page.getTitle());
                                    break;
                                case 2: // Numéros
                                    score = SearchUtils.getFuzzyScore(q, page.getNumber());
                                    break;
                                case 3: // Paroles
                                    if (page.getNormalizedContent().contains(q)) {
                                        score = 0.85;
                                    }
                                    break;
                            }
                        }


                        if (score > 0.4) {
                            allScores.add(new SearchResultScore(book, page, score));
                        }
                    }
                }

                if (Thread.interrupted()) return;

                final List<ListItem> results = new ArrayList<>();
                for (Book book : Book.bookList) {
                    List<SearchResultScore> bookMatches = new ArrayList<>();
                    for (SearchResultScore srs : allScores) {
                        if (srs.book == book) bookMatches.add(srs);
                    }

                    if (!bookMatches.isEmpty()) {
                        java.util.Collections.sort(bookMatches, (r1, r2) -> Double.compare(r2.score, r1.score));
                        results.add(new HeaderItem(book.getName(), bookMatches.size()));
                        for (SearchResultScore r : bookMatches) {
                            results.add(new SearchResult(r.book, r.page));
                        }
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (Thread.interrupted()) return;
                        
                        allItems.clear();
                        allItems.addAll(results);
                        adapter.notifyDataSetChanged();
                        
                        searchProgress.setVisibility(View.GONE);
                        boolean hasResults = !allItems.isEmpty();
                        emptyView.setVisibility(hasResults ? View.GONE : View.VISIBLE);
                        recyclerResults.setVisibility(hasResults ? View.VISIBLE : View.GONE);
                    }
                });
            }
        });
        searchThread.start();
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
                    hvh.txtCount.setText(R.string.search_result_singular);
                } else {
                    hvh.txtCount.setText(String.format(getString(R.string.search_results_plural), header.count));
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

    private static class SearchResultScore {
        final Book book;
        final Page page;
        final double score;

        SearchResultScore(Book book, Page page, double score) {
            this.book = book;
            this.page = page;
            this.score = score;
        }
    }
}


