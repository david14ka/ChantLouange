package com.davidkazad.chantlouange.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.davidkazad.chantlouange.R;
import com.davidkazad.chantlouange.models.Book;
import com.davidkazad.chantlouange.models.Page;
import com.davidkazad.chantlouange.ui.activities.ItemActivity;
import com.davidkazad.chantlouange.utils.AudioMapper;
import com.pixplicity.easyprefs.library.Prefs;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.davidkazad.chantlouange.config.Common.PREFS_TABLE_MATIERES_ALPHABETIQUE;

public class ListFragment extends BaseFragment {

    public static final String TAG = ListFragment.class.getName();
    public static final String EXTRA_BOOK_ID = "bookId";

    public static String query;

    @BindView(R.id.recycler_song)
    RecyclerView recyclerView;

    @BindView(R.id.result_count)
    TextView resultCountView;

    @BindView(R.id.empty_view)
    View emptyView;

    // Cross-book results views (bound manually — not present in all layout variants)
    private RecyclerView recyclerOtherBooks;
    private TextView txtOtherBooksHeader;
    private CheckBox checkSearchLyrics;
    private Button btnSearchOtherBooks;

    private List<Page> pageList = new ArrayList<>();
    private List<CrossBookResult> otherBookResults = new ArrayList<>();
    private Book bookItem;
    private SongAdapter adapter;
    private CrossBookAdapter crossBookAdapter;
    private String currentQuery = "";

    /** Simple holder pairing a page with the book it came from. */
    private static class CrossBookResult {
        final Book book;
        final Page page;
        CrossBookResult(Book book, Page page) { this.book = book; this.page = page; }
    }

    public ListFragment() {}

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
        View view = inflater.inflate(R.layout.fragment_song_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Main list
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(llm);
        // Séparateur fin entre items (remplace les bordures des anciennes CardView)
        recyclerView.addItemDecoration(
                new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        adapter = new SongAdapter();
        recyclerView.setAdapter(adapter);

        // Cross-book results list (optional — only present in current layout)
        recyclerOtherBooks = view.findViewById(R.id.recycler_other_books);
        txtOtherBooksHeader = view.findViewById(R.id.txt_other_books_header);
        if (recyclerOtherBooks != null) {
            recyclerOtherBooks.setLayoutManager(new LinearLayoutManager(getContext()));
            crossBookAdapter = new CrossBookAdapter();
            recyclerOtherBooks.setAdapter(crossBookAdapter);
        }

        // Button: search in other books (only visible when current book has no results)
        btnSearchOtherBooks = view.findViewById(R.id.btn_search_other_books);
        if (btnSearchOtherBooks != null) {
            btnSearchOtherBooks.setOnClickListener(v -> searchInOtherBooks());
        }

        // Lyrics search checkbox
        checkSearchLyrics = view.findViewById(R.id.check_search_lyrics);
        if (checkSearchLyrics != null) {
            checkSearchLyrics.setOnCheckedChangeListener((btn, checked) -> applySearch(currentQuery));
        }

        if (getArguments() != null) {
            int bookId = getArguments().getInt(EXTRA_BOOK_ID);
            bookItem = Book.bookList.get(bookId);
            loadData();
        }

        if (query != null && !query.isEmpty()) {
            applySearch(query);
        }
    }

    private void loadData() {
        if (bookItem == null) return;
        pageList = Prefs.getBoolean(PREFS_TABLE_MATIERES_ALPHABETIQUE, false)
                ? bookItem.sort()
                : bookItem.getPages();
        adapter.notifyDataSetChanged();
    }

    private void applySearch(String q) {
        currentQuery = (q == null) ? "" : q;
        if (bookItem == null) return;

        // Determine whether to include lyrics in search
        boolean searchInLyrics = checkSearchLyrics != null && checkSearchLyrics.isChecked();
        boolean titleOnly = !searchInLyrics;

        // Show/hide the lyrics checkbox only when a search is active
        if (checkSearchLyrics != null) {
            checkSearchLyrics.setVisibility(!currentQuery.isEmpty() ? View.VISIBLE : View.GONE);
        }

        if (currentQuery.isEmpty()) {
            pageList = Prefs.getBoolean(PREFS_TABLE_MATIERES_ALPHABETIQUE, false)
                    ? bookItem.sort()
                    : bookItem.getPages();
        } else {
            pageList = bookItem.searchPage(currentQuery, titleOnly);
            if (Prefs.getBoolean(PREFS_TABLE_MATIERES_ALPHABETIQUE, false)) {
                pageList = bookItem.sort(pageList);
            }
        }

        boolean hasQuery = !currentQuery.isEmpty();
        boolean hasResults = !pageList.isEmpty();

        // Reset cross-book section — user must click the button again for each new query
        otherBookResults.clear();
        if (recyclerOtherBooks != null && crossBookAdapter != null) {
            crossBookAdapter.notifyDataSetChanged();
            recyclerOtherBooks.setVisibility(View.GONE);
        }
        if (txtOtherBooksHeader != null) {
            txtOtherBooksHeader.setVisibility(View.GONE);
        }

        // Show the "search in other books" button only when no results in the current book
        if (btnSearchOtherBooks != null) {
            btnSearchOtherBooks.setVisibility(hasQuery && !hasResults ? View.VISIBLE : View.GONE);
        }

        if (resultCountView != null) {
            if (hasQuery) {
                resultCountView.setVisibility(View.VISIBLE);
                resultCountView.setText(getString(R.string.result_count, pageList.size()));
            } else {
                resultCountView.setVisibility(View.GONE);
            }
        }

        // Show empty view when no results in current book (button inside will offer the cross-book option)
        if (emptyView != null) {
            emptyView.setVisibility(hasQuery && !hasResults ? View.VISIBLE : View.GONE);
        }
        recyclerView.setVisibility(!hasQuery || hasResults ? View.VISIBLE : View.GONE);

        adapter.notifyDataSetChanged();
    }

    /**
     * Triggered by the "Search in other books" button.
     * Searches all books except the active one and displays results below.
     */
    private void searchInOtherBooks() {
        if (currentQuery.isEmpty()) return;

        boolean titleOnly = checkSearchLyrics == null || !checkSearchLyrics.isChecked();

        otherBookResults.clear();
        for (Book other : Book.bookList) {
            if (other == bookItem) continue;
            List<Page> matches = other.searchPage(currentQuery, titleOnly);
            for (Page p : matches) {
                otherBookResults.add(new CrossBookResult(other, p));
            }
        }

        boolean hasOtherResults = !otherBookResults.isEmpty();

        // Hide the button — user already clicked it
        if (btnSearchOtherBooks != null) {
            btnSearchOtherBooks.setVisibility(View.GONE);
        }

        if (recyclerOtherBooks != null && crossBookAdapter != null) {
            crossBookAdapter.notifyDataSetChanged();
            recyclerOtherBooks.setVisibility(hasOtherResults ? View.VISIBLE : View.GONE);
        }
        if (txtOtherBooksHeader != null) {
            txtOtherBooksHeader.setVisibility(hasOtherResults ? View.VISIBLE : View.GONE);
        }

        // If truly nothing found anywhere, update the empty message
        if (!hasOtherResults && emptyView != null) {
            TextView emptyText = emptyView.findViewById(R.id.empty_text);
            if (emptyText != null) {
                emptyText.setText(getString(R.string.no_results_anywhere));
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String q) {
        applySearch(q);
    }

    public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

        @NonNull
        @Override
        public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            int layoutId = Prefs.getBoolean(PREFS_TABLE_MATIERES_ALPHABETIQUE, false)
                    ? R.layout.item_alphabetique
                    : R.layout.item_numerique;
            View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
            return new SongViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
            holder.bind(pageList.get(position));
        }

        @Override
        public int getItemCount() {
            return pageList.size();
        }

        public class SongViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.txt_number) TextView number;
            @BindView(R.id.txt_tittle) TextView title;
            @BindView(R.id.fav) ImageView fav;
            @BindView(R.id.note) ImageView note;
            @Nullable @BindView(R.id.txt_letter) TextView letter;
            // Optional subtitle view (present in new design)
            TextView txtOriginalTitle;
            View accentBar;

            public SongViewHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                txtOriginalTitle = itemView.findViewById(R.id.txt_original_title);
                accentBar = itemView.findViewById(R.id.accent_bar);
                itemView.setOnClickListener(v -> {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        Page item = pageList.get(pos);
                        if (item.getId() >= 0) {
                            ItemActivity.currentPage = item;
                            ItemActivity.currentBook = bookItem;
                            startActivity(new Intent(getContext(), ItemActivity.class));
                        }
                    }
                });
            }

            public void bind(Page mPage) {
                if (mPage.getId() < 0) {
                    if (letter != null) {
                        letter.setText(mPage.getTitle());
                        letter.setVisibility(View.VISIBLE);
                    }
                    number.setVisibility(View.INVISIBLE);
                    note.setVisibility(View.GONE);
                    title.setVisibility(View.GONE);
                } else {
                    if (letter != null) letter.setVisibility(View.GONE);
                    title.setVisibility(View.VISIBLE);
                    number.setVisibility(View.VISIBLE);
                    note.setVisibility(View.GONE);

                    highlightText(title, mPage.getTitle(), currentQuery);

                    // Format number with leading zero
                    String numStr = mPage.getNumber().replace(". ", "").trim();
                    if (Prefs.getBoolean(PREFS_TABLE_MATIERES_ALPHABETIQUE, false)) {
                        number.setText(String.format(getString(R.string.txt_number), numStr));
                    } else {
                        try {
                            int n = Integer.parseInt(numStr);
                            number.setText(String.format("%02d", n));
                        } catch (NumberFormatException e) {
                            number.setText(numStr);
                        }
                    }

                    // Show original title as subtitle
                    if (txtOriginalTitle != null) {
                        String orig = mPage.getOriginalTitle();
                        if (orig != null && !orig.isEmpty()) {
                            txtOriginalTitle.setText(orig.toUpperCase());
                            txtOriginalTitle.setVisibility(View.VISIBLE);
                        } else {
                            txtOriginalTitle.setVisibility(View.GONE);
                        }
                    }

                    // Audio indicator (red play button)
                    if (AudioMapper.hasAudio(itemView.getContext(), mPage.getBookId(), mPage.getNumber())) {
                        note.setVisibility(View.VISIBLE);
                    } else {
                        note.setVisibility(View.GONE);
                    }
                }
                boolean isFav = mPage.isFavorite();
                fav.setVisibility(isFav ? View.VISIBLE : View.GONE);
                if (accentBar != null) {
                    accentBar.setVisibility(isFav ? View.VISIBLE : View.GONE);
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
                    spannable.setSpan(new BackgroundColorSpan(Color.parseColor("#88FFD700")), start, start + query.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannable.setSpan(new ForegroundColorSpan(Color.BLACK), start, start + query.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv.setText(spannable);
                } else {
                    tv.setText(text);
                }
            }
        }
    }

    // -----------------------------------------------------------------------
    // Cross-book results adapter
    // -----------------------------------------------------------------------

    public class CrossBookAdapter extends RecyclerView.Adapter<CrossBookAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_search_result, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            CrossBookResult result = otherBookResults.get(position);
            Page page = result.page;
            Book book = result.book;

            holder.txtNumber.setText(page.getNumber().replace(". ", ""));
            highlightText(holder.txtTitle, page.getTitle(), currentQuery);
            holder.txtSubtitle.setText(book.getName());
            holder.imgFav.setVisibility(page.isFavorite() ? View.VISIBLE : View.GONE);

            holder.itemView.setOnClickListener(v -> {
                ItemActivity.currentBook = book;
                ItemActivity.currentPage = page;
                startActivity(new Intent(getContext(), ItemActivity.class));
            });
        }

        @Override
        public int getItemCount() { return otherBookResults.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView txtNumber, txtTitle, txtSubtitle;
            ImageView imgFav;
            ViewHolder(View itemView) {
                super(itemView);
                txtNumber   = itemView.findViewById(R.id.txt_number);
                txtTitle    = itemView.findViewById(R.id.txt_title);
                txtSubtitle = itemView.findViewById(R.id.txt_subtitle);
                imgFav      = itemView.findViewById(R.id.img_fav);
            }
        }

        private void highlightText(TextView tv, String text, String query) {
            if (query == null || query.isEmpty()) { tv.setText(text); return; }
            int start = text.toLowerCase().indexOf(query.toLowerCase());
            if (start >= 0) {
                SpannableString s = new SpannableString(text);
                s.setSpan(new BackgroundColorSpan(Color.parseColor("#88FFD700")), start, start + query.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                s.setSpan(new ForegroundColorSpan(Color.BLACK), start, start + query.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv.setText(s);
            } else {
                tv.setText(text);
            }
        }
    }
}
